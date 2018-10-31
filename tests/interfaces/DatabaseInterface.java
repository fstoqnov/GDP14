package tests.interfaces;

import java.util.List;
import java.util.Map.Entry;

import tests.RunTests;

public class DatabaseInterface {


	private code.interfaces.DatabaseInterface inter;

	private static final String testURL = "https://www.google.com/index.php?arg1=val1&arg2=val2&arg3=val3";
	
	public static boolean runTests() {
		DatabaseInterface testInter = new DatabaseInterface();
		boolean correct = true;
		correct = testInter.testPartialiseFullURL() && correct;
		correct = testInter.testGetURLArgs() && correct;
		return correct;
	}

	private DatabaseInterface() {
		inter = new code.interfaces.DatabaseInterface();
	}

	private boolean testPartialiseFullURL() {
		return
				RunTests.test("DatabaseInterface(PartialiseFullURL - test site)", "google.com", code.interfaces.DatabaseInterface.partialiseFullURL(testURL).getKey()) &&
				RunTests.test("DatabaseInterface(PartialiseFullURL - test page)", "index.php", code.interfaces.DatabaseInterface.partialiseFullURL(testURL).getValue());
	}

	private boolean testGetURLArgs() {
		List<Entry<String, String>> map = code.interfaces.DatabaseInterface.getURLArgs(testURL);
		String correctMap = "arg1=val1 arg2=val2 arg3=val3 ";
		String actualMap = "";
		for (Entry<String, String> cur : map) {
			actualMap += cur.getKey() + "=" + cur.getValue();
			actualMap += " ";
		}
		return RunTests.test("DatabaseInterface(GetURLArgs)", correctMap, actualMap);
	}
}