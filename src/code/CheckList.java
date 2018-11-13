package code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import code.checks.Check;
import code.checks.IdentifyInputPurpose;
import code.checks.LabelsOrInstructions;
import code.checks.LanguageOfPage;
import code.checks.NameRoleVal;
import code.checks.PageTitled;
import code.checks.Parsing;
import code.interfaces.DatabaseInterface;
import code.interfaces.SeleniumInterface;

public class CheckList {

	private List<Check> checks;

	public CheckList() {
		checks = new ArrayList<Check>();
		addChecks();
	}

	public static void addChecks(List<Check> checks) {
		checks.add(new IdentifyInputPurpose());
		checks.add(new Parsing());
		checks.add(new LabelsOrInstructions());
		checks.add(new LanguageOfPage());
		checks.add(new PageTitled());
		checks.add(new NameRoleVal());
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

	public boolean runChecksAtURLs(String[] urls) throws Exception {
		return runChecksAtURLs(urls, false, null);
	}

	public boolean runChecksAtURLs(String[] urls, boolean store, DatabaseInterface db) throws Exception {
		boolean passed = true;
		int totalPassed = 0;
		int totalFailed = 0;
		boolean curPassed;
		SeleniumInterface inter = new SeleniumInterface();
		long curTime = System.currentTimeMillis();

		ConformanceReport cr = new ConformanceReport();

		for (String url : urls) {
			List<Marker> markers = new ArrayList<Marker>();
			System.out.println("Running checks for url: '" + url + "'");
			String content = inter.getRenderedHtml(url);

			for (Check c : checks) {
				curPassed = c.executeCheck(content, markers, inter);
				if (curPassed) {
					c.outputPassed();
					totalPassed ++;
				}
				else {
					c.outputFailed();
					totalFailed ++;
				}
				passed = passed && curPassed;
			}
			if (store) {
				db.insertIntoDatabase(markers, inter.driver.getCurrentUrl(), inter.driver.getPageSource(), curTime, inter);
			}
		}

		if(store) {
			cr.generateReportFromPage(db, db.partialiseFullURL(urls[0]).getKey());
		}


		System.out.println("Total passed: " + totalPassed + "/" + (totalPassed + totalFailed));
		System.out.println();
		inter.close();
		return passed;
	}

	public List<Check> getChecks() { return checks; }
}