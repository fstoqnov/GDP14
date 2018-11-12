package tests.checks;

import code.checks.Check;
import code.interfaces.SeleniumInterface;
import tests.RunTests;
import tests.interfaces.TestsServer;

public class CheckList {

	code.CheckList cl;

	private CheckList() {
		cl = new code.CheckList();
	}

	public static boolean runTests() {
		CheckList testCheckList = new CheckList();
		boolean correct = true;
		for (Check c : testCheckList.cl.getChecks()) {
			correct = testCheckList.testCheck(c) && correct;
		}
		return correct;
	}

	private boolean testCheck(Check c) {
		SeleniumInterface inter = new SeleniumInterface();
		String[] pass = c.getHTMLPass();
		String[] fail = c.getHTMLFail();
		int passTestCount = 0;
		int failTestCount = 0;
		boolean correct = true;
		if (pass != null) {
			for (String passHTML : pass) {
				try {
					TestsServer ts = new TestsServer(passHTML);
					ts.createServer(RunTests.TEST_PORT);
					String content = inter.getRenderedHtml("http://localhost:" + RunTests.TEST_PORT + "/");
					if(c.getName().equals("Criterion 4.1.1 Parsing")) {
						content = passHTML;
					}
					correct = RunTests.test(c.getName() + "-pass (" + (passTestCount + 1) + "/" + pass.length + ")", true, c.executeCheck(content, inter)) && correct;
					passTestCount ++;
				} catch (Exception e) { RunTests.countFailure++; correct = false; e.printStackTrace(); }
			}
		} else {
			correct = false;
			System.err.println(c.getName() + " has no pass tests!");
			RunTests.countFailure++;
		}
		c.initialise();
		if (pass != null) {
			for (String failHTML : fail) {
				try {
					TestsServer ts = new TestsServer(failHTML);
					ts.createServer(RunTests.TEST_PORT);
					String content = inter.getRenderedHtml("http://localhost:" + RunTests.TEST_PORT + "/");
					if(c.getName().equals("Criterion 4.1.1 Parsing")) {
						content = failHTML;
					}
					correct = RunTests.test(c.getName() + "-fail (" + (failTestCount + 1) + "/" + fail.length + ")", false, c.executeCheck(content, inter)) && correct;
					failTestCount ++;
				} catch (Exception e) { RunTests.countFailure++; correct = false; e.printStackTrace(); }
			}
			inter.close();
		} else {
			correct = false;
			System.err.println(c.getName() + " has no fail tests!");
			RunTests.countFailure++;
		}
		return correct;
	}
}