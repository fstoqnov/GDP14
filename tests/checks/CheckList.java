package tests.checks;

import code.checks.Check;
import code.selenium_interface.Interface;
import tests.RunTests;
import tests.selenium_interface.TestsServer;

public class CheckList {

	code.checks.CheckList cl;

	private CheckList() {
		cl = new code.checks.CheckList();
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
		Interface inter = new Interface();
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
					correct = RunTests.test(c.getName() + "-pass (" + (passTestCount + 1) + "/" + pass.length + ")", true, c.runCheck(content, cl, inter)) && correct;
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
					correct = RunTests.test(c.getName() + "-fail (" + (failTestCount + 1) + "/" + pass.length + ")", false, c.runCheck(content, cl, inter)) && correct;
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