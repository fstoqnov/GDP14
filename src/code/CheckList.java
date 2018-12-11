package code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.*;

import com.google.common.collect.Lists;

import code.checks.*;
import code.interfaces.DatabaseInterface;
import code.interfaces.SeleniumInterface;
import database_records.DBSimplePage;

public class CheckList {

	private List<Check> checks;
	private List<Check> implementedChecks;

	public CheckList() {
		checks = new ArrayList<Check>();
		implementedChecks = new ArrayList<Check>();
		addChecks();
		addImplementedChecks();
	}

	public static void addChecks(List<Check> checks) {

		checks.add(new ContrastMinimum());
		checks.add(new HeadingsAndLabels());
		checks.add(new InfoAndRelationships());
		checks.add(new KeyboardFunctionality());
		checks.add(new LabelsOrInstructions());
		checks.add(new LanguageOfPage());
		checks.add(new NonTextContent());
		checks.add(new OnInput());
		checks.add(new PageTitled());
		checks.add(new Parsing());
		checks.add(new IdentifyInputPurpose());
		checks.add(new LanguageOfParts());
		checks.add(new NameRoleVal());

		Collections.sort(checks);
	}

	public static void addImplementedChecks(List<Check> implementedChecks) {

		implementedChecks.add(new ContrastMinimum());
		implementedChecks.add(new HeadingsAndLabels());
		implementedChecks.add(new InfoAndRelationships());
		implementedChecks.add(new KeyboardFunctionality());
		implementedChecks.add(new LabelsOrInstructions());
		implementedChecks.add(new LanguageOfPage());
		implementedChecks.add(new NonTextContent());
		implementedChecks.add(new OnInput());
		implementedChecks.add(new PageTitled());
		implementedChecks.add(new Parsing());
		implementedChecks.add(new IdentifyInputPurpose());
		implementedChecks.add(new LanguageOfParts());
		implementedChecks.add(new NameRoleVal());
		Collections.sort(implementedChecks);
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

	private void addImplementedChecks() {
		addImplementedChecks(implementedChecks);
	}

	public boolean runChecksAtURLs(String[] urls) throws Exception {
		return runChecksAtURLs(urls, false, null, false);
	}

	public boolean runChecksAtURLs(String[] urls, boolean store, DatabaseInterface db, boolean dynamic) throws Exception {
		//Boolean passed = true;
		//Integer totalPassed = 0;
		//Integer totalFailed = 0;
		/*Integers are not mutable, so this previous method won't work
		need to use CheckResults here.*/
		CheckResults checkResults = new CheckResults();
		SeleniumInterface inter = new SeleniumInterface();
		long curTime = System.currentTimeMillis();

		ConformanceReport cr = new ConformanceReport();

		DBSimplePage rootPage;
		ArrayList<String> domReps = new ArrayList<String>();

		String rep;
		String baseURL;

		for (String url : urls) {
			inter.getRenderedHtml(url);
			baseURL = inter.driver.getCurrentUrl();
			domReps.add(inter.getDomRep());
			rootPage = runCheckAtPermutedPage(inter, url, null, store, db, checkResults, curTime, null);
            cr.addCheckImages(db, url, inter);
			if (dynamic) {
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
			}
		}

		System.out.println("Total passed: " + checkResults.totalPassed + "/" + (checkResults.totalPassed + checkResults.totalFailed));
		System.out.println();
		inter.close();

		if(store) {
			cr.generateReportFromPage(db, urls[0]);

		}

		return checkResults.overallPass;
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
			curPassed = c.noFailExecuteCheck(content, markers, inter);
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