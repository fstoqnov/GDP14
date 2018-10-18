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
			correct = correct && testCheckList.testCheck(c);
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
		for (String passHTML : pass) {
			try {
				TestsServer ts = new TestsServer(passHTML);
				ts.createServer(RunTests.TEST_PORT);
				String content = inter.getRenderedHtml("http://localhost:" + RunTests.TEST_PORT + "/");
				correct = correct && RunTests.test(c.getName() + "-pass (" + passTestCount + "/" + pass.length + ")", true, c.runCheck(content, inter));
				passTestCount ++;
			} catch (Exception e) {  }
		}
		c.initialise();
		for (String failHTML : fail) {
			try {
				TestsServer ts = new TestsServer(failHTML);
				ts.createServer(RunTests.TEST_PORT);
				String content = inter.getRenderedHtml("http://localhost:" + RunTests.TEST_PORT + "/");
				correct = correct && RunTests.test(c.getName() + "-fail (" + failTestCount + "/" + pass.length + ")", false, c.runCheck(content, inter));
				failTestCount ++;
			} catch (Exception e) {  }
		}
		inter.close();
		return correct;
	}
}
