package code.checks;

import java.util.ArrayList;
import java.util.List;

import code.selenium_interface.Interface;

public class CheckList {

	private List<Check> checks;

	public CheckList() {
		checks = new ArrayList<Check>();
		addChecks();
	}

	private void addChecks() {
		checks.add(new InfoAndRelationships());
		checks.add(new MeaningfulSequence());
		checks.add(new IdentifyInputPurpose());
	}

	public boolean runChecksAtURLs(String[] urls) {
		boolean passed = true;
		int totalPassed = 0;
		int totalFailed = 0;
		boolean curPassed;
		Interface inter = new Interface();
		for (String url : urls) {
			System.out.println("Running checks for url: '" + url + "'");
			String content = inter.getRenderedHtml(url);
			for (Check c : checks) {
				curPassed = c.runCheck(content, inter);
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
		}
		System.out.println("Total passed: " + totalPassed + "/" + (totalPassed + totalFailed));
		System.out.println();
		inter.close();
		return passed;
	}

	public List<Check> getChecks() { return checks; }
}