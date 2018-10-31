package code.checks;

import java.util.ArrayList;
import java.util.List;

import code.interfaces.DatabaseInterface;
import code.interfaces.SeleniumInterface;

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
		checks.add(new Parsing());
		checks.add(new StatusMessages());
		checks.add(new LabelsOrInstructions());
		checks.add(new LanguageOfPage());
	}

	public boolean runChecksAtURLs(String[] urls) {
		boolean passed = true;
		int totalPassed = 0;
		int totalFailed = 0;
		boolean curPassed;
		SeleniumInterface inter = new SeleniumInterface();
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
			DatabaseInterface.insertIntoDatabase(markers, inter.driver.getCurrentUrl(), inter.driver.getPageSource());
		}
		System.out.println("Total passed: " + totalPassed + "/" + (totalPassed + totalFailed));
		System.out.println();
		inter.close();
		return passed;
	}

	public List<Check> getChecks() { return checks; }
}