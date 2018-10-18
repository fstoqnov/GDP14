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

	public void runChecksAtURL(String url) {
		System.out.println("Running checks for url: '" + url + "'");
		Interface inter = new Interface();
		String content = inter.getRenderedHtml(url);
		boolean passed = true;
		int totalPassed = 0;
		int totalFailed = 0;
		boolean curPassed;
		for (Check c : checks) {
			curPassed = c.runTest(content, inter);
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
		System.out.println("Total passed: " + totalPassed + "/" + (totalPassed + totalFailed));
		System.out.println();
		inter.close();
	}
	
	public List<Check> getChecks() { return checks; }
}