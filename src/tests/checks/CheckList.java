package tests.checks;

import java.util.ArrayList;

import code.checks.Check;
import code.interfaces.SeleniumInterface;
import tests.RunTests;
import tests.Test;
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
		ArrayList<Test> tests = c.getTests();

		int testCount = 0;
		boolean correct = true;
		c.initialise();
		for (Test t : tests) {
			try {				
				TestsServer ts = new TestsServer(t.getHTMLString());
				ts.createServer(RunTests.TEST_PORT);
				String content = inter.getRenderedHtml("http://localhost:" + RunTests.TEST_PORT + "/");
				if(c.getName().equals("Criterion 4.1.1 Parsing")) {
					content = t.getHTMLString();
				}
				testCount++;
				System.out.println("Next test: " + t.getHTMLString());
				String testDetails = c.getName() + "-test (" + (testCount) + "/" + tests.size()+ ")";
				correct = RunTests.test(testDetails, t.getExpectedResults(), c.executeCheck(content, inter)) && correct;
				
			} catch (Exception e) { 
				RunTests.countFailure++; 
				correct = false; 
				System.err.println("Test on " + c.getName() + " failed. Exception thrown:");
	
				e.printStackTrace(); 
			}
	}
		inter.close();
	
			

		return correct;
	}
}