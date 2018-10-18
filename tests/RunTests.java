package tests;

import tests.selenium_interface.Interface;

public class RunTests {

	public static int countPass = 0;
	public static int countFailure = 0;

	public static void main(String[] args) {
		boolean correct = true;
		correct = correct && test_checks();
		correct = correct && test_selenium_interface();

		System.out.println("Tests complete. Passed " + countPass + "/" + (countPass + countFailure) + " tests");
	}

	private static boolean test_checks() {
		boolean correct = true;
		return correct;
	}

	private static boolean test_selenium_interface() {
		boolean correct = true;
		correct = correct && Interface.runTests();
		return correct;
	}

	public static boolean test(String testName, String expectedValue, String testValue) {
		boolean result = expectedValue.equals(testValue);
		if (result) {
			System.out.println("Test: " + testName + " passed");
		} else {
			System.err.println("Test: " + testName + " failed. Expected: '" + expectedValue + "', Received: '" + testValue + "'");
		}
		return result;
	}

	public static boolean test(String testName, boolean expectedValue, boolean testValue) {
		return test(testName, String.valueOf(expectedValue), String.valueOf(testValue));
	}
}