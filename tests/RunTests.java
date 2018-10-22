package tests;

import tests.checks.CheckList;
import tests.selenium_interface.Interface;

public class RunTests {

	public static int countPass = 0;
	public static int countFailure = 0;
	public static final int TEST_PORT = 28739;

	public static void main(String[] args) {
		boolean correct = true;
		correct = test_selenium_interface() && correct;
		correct = test_checks() && correct;

		System.out.println("Tests complete. Passed " + countPass + "/" + (countPass + countFailure) + " tests");
	}

	private static boolean test_selenium_interface() {
		boolean correct = true;
		correct = Interface.runTests() && correct;
		return correct;
	}

	private static boolean test_checks() {
		boolean correct = true;
		correct = CheckList.runTests() && correct;
		return correct;
	}

	public static boolean test(String testName, String expectedValue, String testValue) {
		boolean result = expectedValue.equals(testValue);
		if (result) {
			countPass++;
			System.out.println("Test: " + testName + " passed");
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