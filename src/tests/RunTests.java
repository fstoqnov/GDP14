package tests;

import java.util.ArrayList;
import java.util.HashSet;

import code.checks.Result;
import tests.checks.CheckList;
import tests.interfaces.DatabaseInterface;
import tests.interfaces.SeleniumInterface;

public class RunTests {

	public static int countPass = 0;
	public static int countFailure = 0;
	public static final int TEST_PORT = 28739;

	public static void main(String[] args) {
		boolean correct = true;
		try {
			correct = test_selenium_interface() && correct;
			System.out.println();
			correct = test_database_interface() && correct;
			System.out.println();
			correct = test_checks() && correct;

			System.out.println("Tests complete. Passed " + countPass + "/" + (countPass + countFailure) + " tests");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean test_selenium_interface() {
		boolean correct = true;
		correct = SeleniumInterface.runTests() && correct;
		return correct;
	}

	private static boolean test_database_interface() throws Exception {
		boolean correct = true;
		correct = DatabaseInterface.runTests() && correct;
		return correct;
	}

	private static boolean test_checks() {
		boolean correct = true;
		correct = CheckList.runTests() && correct;
		return correct;
	}

	public static boolean test(String testName, long expectedValue, long testValue) {
		return test(testName, expectedValue + "", testValue + "");
	}
	
	public static boolean test(String testName, int expectedValue, int testValue) {
		return test(testName, expectedValue + "", testValue + "");
	}
	
	public static boolean test(String testName, Result[] expectedResults, ArrayList<Result> receivedResults) {
		boolean result = true;
		
		HashSet<Result> setRecResults = new HashSet<Result>(receivedResults);
		HashSet<Result> setExpResults = new HashSet<Result>();
		for (int i=0; i < expectedResults.length; i++) {
			setExpResults.add(expectedResults[i]);
		}
		if (!setRecResults.equals(setExpResults)) {
			result = false;
			countFailure++;
			System.err.println("Test : " + testName + " failed. Expected: set{" + setExpResults + "}, Received: set{" + setRecResults + "}");
		}
		else {
			countPass++;
			System.out.println("Test: " + testName + " passed ");
		}

		return result;
	}
	
	public static boolean test(String testName, String expectedValue, String testValue) {
		boolean result = true;
		if (expectedValue == null || testValue == null) {
			result = expectedValue==null && testValue == null;
		} else {
			result = expectedValue.equals(testValue);
		}
		
		if (result) {
			countPass++;
			System.out.println("Test: " + testName + " passed ");
		} else {
			countFailure++;
			System.err.println("Test: " + testName + " failed. Expected: '" + expectedValue + "', Received: '" + testValue + "'");
		}
		return result;
	}

	public static boolean test(String testName, boolean expectedValue, boolean testValue) {
		return test(testName, String.valueOf(expectedValue), String.valueOf(testValue));
	}
}