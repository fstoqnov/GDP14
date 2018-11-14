package code;

import code.interfaces.DatabaseInterface;

public class RunDemo {
	public static void main (String[] args) {
		CheckList cl = new CheckList();
		DatabaseInterface db;
		try {
			cl.runChecksAtURLs(new String[] { "https://www.google.com/" }, true, db = new DatabaseInterface(tests.interfaces.DatabaseInterface.connString), false);
			new ConformanceReport().generateReportFromPage(db, "google.com");
			//cl.runChecksAtURLs(new String[] { "https://www.guru99.com/execute-javascript-selenium-webdriver.html" }, true, db = new DatabaseInterface(tests.interfaces.DatabaseInterface.connString), true);
			//new ConformanceReport().generateReportFromPage(db, "guru99.com");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}