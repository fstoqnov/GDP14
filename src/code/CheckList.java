package code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import org.jnativehook.keyboard.NativeKeyEvent;

import com.google.common.collect.Lists;

import code.checks.Check;
import code.checks.ContrastMinimum;
import code.checks.HeadingsAndLabels;
import code.checks.IdentifyInputPurpose;
import code.checks.InfoAndRelationships;
import code.checks.KeyboardFunctionality;
import code.checks.LabelsOrInstructions;
import code.checks.LanguageOfPage;
import code.checks.LanguageOfParts;
import code.checks.NameRoleVal;
import code.checks.NonTextContent;
import code.checks.OnInput;
import code.checks.PageTitled;
import code.checks.Parsing;
import code.interfaces.DatabaseInterface;
import code.interfaces.SeleniumInterface;
import database_records.DBSimplePage;

public class CheckList {

	private List<Check> implementedChecks;

	public CheckList() {
		implementedChecks = new ArrayList<Check>();
		addImplementedChecks();
	}

	private boolean hasFrame = false;
	private ViewFrame frame;
	public void setGUI(ViewFrame frame) {
		this.frame = frame;
		this.hasFrame = true;
	}

	private GlobalKeyListener keyListener;

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

	private void addImplementedChecks() {
		addImplementedChecks(implementedChecks);
	}

	public boolean runChecksAtURLs(RuntimeConfig config) throws Exception {
		return runChecksAtURLs(config, false, null);
	}

	private static final int KEY_F2 = NativeKeyEvent.VC_F2; //TODO
	private static final int KEY_F4 = NativeKeyEvent.VC_F4; //TODO

	public boolean runChecksAtURLs(RuntimeConfig config, DatabaseInterface db) throws Exception {
		return runChecksAtURLs(config, true, db);
	}

	private boolean runChecksAtURLs(RuntimeConfig config, boolean store, DatabaseInterface db) throws Exception {
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

		String url;

		keyListener = GlobalKeyListener.startup();

		if (this.hasFrame) {
			this.frame.setKeyListener(keyListener);
		}

		for (CheckURL curUrl : config.urls) {
			log("Checking URL " + curUrl.checkURL);
			if (this.hasFrame) {
				this.frame.curLabel.setText(this.frame.CUR_START + curUrl.checkURL);
			}
			url = curUrl.checkURL;

			inter.getRenderedHtml(url);
			if (curUrl.loginRequired) {
				if (this.hasFrame) {
					frame.next.setEnabled(true);
					frame.next.setText("Login Complete (F2)");
				}
				log("Login required, waiting for F2 keypress...");
				waitForInput(KEY_F2);
				frame.next.setText("Next URL (F2)");
				frame.next.setEnabled(false);
				log("Key press detected");
			}
			domReps.add(inter.getDomRep());
			rootPage = runCheckAtPermutedPage(inter, url, null, store, db, checkResults, curTime, null);
			log("Checks complete");
			if (store) {
				log("Adding screenshots");
				cr.addCheckImages(db, url, inter);
				log("Screenshots complete");
			}
			System.out.println();

			if (curUrl.dynamic) {
				log("Dynamic input mode. Press F2 to stop dynamic content checking, press F4 to store results");
				int event_no = 0;
				if (hasFrame) {
					frame.next.setEnabled(true);
					frame.nextDynamic.setEnabled(true);
				}
				while (waitForInput(new int[] { KEY_F2, KEY_F4 }) != KEY_F2) {
					log("Key press detected");
					if (hasFrame) {
						frame.next.setEnabled(false);
						frame.nextDynamic.setEnabled(false);
					}
					runCheckAtPermutedPage(inter, inter.driver.getCurrentUrl(), "event_num:" + event_no, store, db, checkResults, curTime, rootPage); //TODO can we name the event in some way
					log("Checks completed");
					log("Press F2 to stop dynamic content checking, press F4 to store results");
					event_no++;
					if (hasFrame) {
						frame.next.setEnabled(true);
						frame.nextDynamic.setEnabled(true);
					}
				}
				if (hasFrame) {
					frame.next.setEnabled(false);
					frame.nextDynamic.setEnabled(false);
				}
			}
		}

		log("Total passed: " + checkResults.totalPassed + "/" + (checkResults.totalPassed + checkResults.totalFailed));
		log("");
		inter.close();

		if(store) {
			cr.generateReportFromPage(db, config.urls.get(0).checkURL);
		}

		GlobalKeyListener.stop();

		if (this.hasFrame) {
			this.frame.displayFinish();
		}

		return checkResults.overallPass;
	}

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
		log("Running checks for url: '" + url + "'" + (event != null ? " with event '" + event + "'" : ""));
		String content = inter.getHTML();
		boolean curPassed;
		for (Check c : implementedChecks) {
			try {
				curPassed = c.noFailExecuteCheck(content, markers, inter);
				checkResults.insertResult(curPassed);
				if (RunChecks.couldRunLanguage == false) {
					log("ERROR: COULD NOT RUN LANGUAGE OF PARTS CHECK!");
					RunChecks.couldRunLanguage = true;
				} else {
					if (curPassed) {
						c.outputPassed(this);
					}
					else {
						c.outputFailed(this);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (this.hasFrame) {
					this.frame.log("ERROR: COULD NOT RUN CHECK " + c.getName());
				}
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

	public List<Check> getChecks() { return implementedChecks; }

	public void log(String line) {
		if (this.hasFrame) {
			this.frame.log(line);
		}
		System.out.println(line);
	}
}