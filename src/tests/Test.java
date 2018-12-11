package tests;

import code.checks.ResultT;

public class Test {

	public String html;
	public ResultT[] expectedResults;
	
	public Test(String html, ResultT[] expectedR) {
		this.html = html;
		this.expectedResults = expectedR;
	}
	
	public String getHTMLString() {
		return this.html;
	}
	
	public ResultT[] getExpectedResults() {
		return this.expectedResults;
	}
}
