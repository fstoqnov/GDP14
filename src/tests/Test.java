package tests;

import code.checks.Result;

public class Test {

	public String html;
	public Result[] expectedResults;
	
	public Test(String html, Result[] expectedR) {
		this.html = html;
		this.expectedResults = expectedR;
	}
	
	public String getHTMLString() {
		return this.html;
	}
	
	public Result[] getExpectedResults() {
		return this.expectedResults;
	}
}
