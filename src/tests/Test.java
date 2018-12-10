package tests;

import code.checks.ResultSet;

public class Test {

	public String html;
	public ResultSet[] expectedResults;
	
	public Test(String html, ResultSet[] expectedR) {
		this.html = html;
		this.expectedResults = expectedR;
	}
	
	public String getHTMLString() {
		return this.html;
	}
	
	public ResultSet[] getExpectedResults() {
		return this.expectedResults;
	}
}
