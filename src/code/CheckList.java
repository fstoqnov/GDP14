package code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import com.google.common.collect.Lists;

import code.checks.*;
import code.interfaces.DatabaseInterface;
import code.interfaces.SeleniumInterface;
import database_records.DBSimplePage;

public class CheckList {

	private List<Check> checks;
	private GlobalKeyListener keyListener;

	public CheckList() {
		checks = new ArrayList<Check>();
		addChecks();
	}

	public static void addChecks(List<Check> checks) {
		checks.add(new IdentifyInputPurpose());
		//checks.add(new Parsing());
		checks.add(new LabelsOrInstructions());
		checks.add(new LanguageOfPage());
		checks.add(new PageTitled());
		checks.add(new NameRoleVal());
		checks.add(new ContrastMinimum());
		checks.add(new LanguageOfParts());
		checks.add(new KeyboardFunctionality());
		Collections.sort(checks);
	}

	public static Check getCheckFromCriterionNumber(List<Check> checks, String num) {
		for (Check c : checks) {
			if (c.getName().split("Criterion ")[1].split(" ")[0].equals(num)) {
				return c;
			}
		}
		return null;
	}

	private Check getCheckFromCriterionNumber(String num) {
		return getCheckFromCriterionNumber(checks, num);
	}

	private void addChecks() {
		addChecks(checks);
	}

	public boolean runChecksAtURLs(RuntimeConfig config) throws Exception {
		return runChecksAtURLs(config, false, null, false);
	}

	public boolean runChecksAtURLs(RuntimeConfig config, boolean store, DatabaseInterface db, boolean dynamic) throws Exception {
		//Boolean passed = true;
		//Integer totalPassed = 0;
		//Integer totalFailed = 0;
		/*Integers are not mutable, so this previous method won't work
		need to use CheckResults here.*/
		CheckResults checkResults = new CheckResults();
		SeleniumInterface inter = new SeleniumInterface(config.headedRequired());
		long curTime = System.currentTimeMillis();

		ConformanceReport cr = new ConformanceReport();

		DBSimplePage rootPage;
		ArrayList<String> domReps = new ArrayList<String>();

		String rep;
		String baseURL;
		String url;
		
		keyListener = GlobalKeyListener.startup();

		for (CheckURL curUrl : config.urls) {
			System.out.println("Checking URL " + curUrl.checkURL);
			url = curUrl.checkURL;
			inter.getRenderedHtml(url);
			if (curUrl.loginRequired) {
				System.out.println("Login required, waiting for F2 keypress...");
				waitForInput(KEY_F2);
			}
			System.out.println("Key press detected. Running checks");
			baseURL = inter.driver.getCurrentUrl();
			domReps.add(inter.getDomRep());
			rootPage = runCheckAtPermutedPage(inter, inter.driver.getCurrentUrl(), null, store, db, checkResults, curTime, null);
			System.out.println("Checks completed");
			System.out.println();
			//System.out.println("'passed' is " + passed.toString());
			//System.out.println("totalPassed, totalFailed : " + totalPassed.toString() + ", " + totalFailed.toString());
			
			if (curUrl.dynamic) {
				System.out.println("Dynamic input mode. Press F2 to stop dynamic content checking, press F4 to store results");
				int event_no = 0;
				while (waitForInput(new int[] { KEY_F2, KEY_F4 }) != KEY_F2) {
					System.out.println("Key press detected. Running checks");
					runCheckAtPermutedPage(inter, inter.driver.getCurrentUrl(), "event_num:" + event_no, store, db, checkResults, curTime, rootPage); //TODO can we name the event in some way
					System.out.println("Checks completed");
					System.out.println();
					event_no++;
				}
			}
			System.out.println();
			
			/*if (dynamic) {
				List<WebElement> elements = inter.getAllElements();
				JavascriptExecutor js = (JavascriptExecutor) inter.driver;
				for (int i = 0; i < elements.size(); i ++) {
					for (int j = 0; j < events.size(); j ++) {
						if (events.get(j).equals("onclick") && elements.get(i).getTagName().toLowerCase().equals("a") && elements.get(i).getAttribute("href") != null) {
							if (!elements.get(i).getAttribute("href").startsWith("#")) {
								continue;
							}
						}
						try {
							js.executeScript("arguments[0]." + events.get(j) + "()", elements.get(i));
							if (inter.driver.getCurrentUrl().equals(baseURL)) {
								if (!domReps.contains(rep = inter.getDomRep())) {
									domReps.add(rep);
									runCheckAtPermutedPage(inter, url, events.get(j), store, db, checkResults, curTime, rootPage);
								}
							} else {
								inter.getRenderedHtml(url);
							}
						} catch (Exception e) {  }
					}
				}
			}*/
		}

		System.out.println("Total passed: " + checkResults.totalPassed + "/" + (checkResults.totalPassed + checkResults.totalFailed));
		System.out.println();
		inter.close();

		if(store) {
			cr.generateReportFromPage(db, config.urls.get(0).checkURL, new SeleniumInterface(false));
		}

		return checkResults.overallPass;
	}
	
	private static final int KEY_F2 = NativeKeyEvent.VC_F2; //TODO
	private static final int KEY_F4 = NativeKeyEvent.VC_F4; //TODO
	
	public int waitForInput(int permitted) {
		return waitForInput(new int[] { permitted });
	}
	
	public int waitForInput(int[] permitted) {
		keyListener.lastKey = 0;
		while (true) {
			for (int i = 0; i < permitted.length; i ++) {
				if (keyListener.lastKey == permitted[i]) {
					return keyListener.lastKey;
				}
			}
			try { Thread.sleep(50); } catch (Exception e) {  }
		}
	}

	private static final List<String> events = Lists.newArrayList(
			"onchange", "onclick", "onmouseover",
			"onmouseout", "onkeyup", "onkeydown");

	public DBSimplePage runCheckAtPermutedPage(SeleniumInterface inter, String url, String event, boolean store, DatabaseInterface db, CheckResults checkResults, long curTime, DBSimplePage parent) throws Exception {
		List<Marker> markers = new ArrayList<Marker>();
		System.out.println("Running checks for url: '" + url + "'" + (event != null ? " with event '" + event + "'" : ""));
		String content = inter.getHTML();
		boolean curPassed;
		for (Check c : checks) {
			curPassed = c.executeCheck(content, markers, inter);
			checkResults.insertResult(curPassed);
			if (curPassed) {
				c.outputPassed();
			}
			else {
				c.outputFailed();
			}
		}
		if (store) {
			if (parent != null) {
				return db.insertIntoDatabase(markers, inter.driver.getCurrentUrl(), inter.driver.getPageSource(), event, curTime, parent, inter);
			} else {
				return db.insertIntoDatabase(markers, inter.driver.getCurrentUrl(), inter.driver.getPageSource(), event, curTime, inter);
			}
		}
		return null;
	}

	public List<Check> getChecks() { return checks; }
}