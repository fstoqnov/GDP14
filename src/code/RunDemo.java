package code;

import code.interfaces.DatabaseInterface;
import code.interfaces.SeleniumInterface;

public class RunDemo {
	public static void main (String[] args) {
		CheckList cl = new CheckList();
		DatabaseInterface db;
		try {
			//cl.runChecksAtURLs(new String[] { "https://ssl.bbc.co.uk/faqs/forms" }, true, db = new DatabaseInterface(tests.interfaces.DatabaseInterface.connString), false);
			//cl.runChecksAtURLs(new String[] { "https://google.co.uk" }, true, db = new DatabaseInterface(tests.interfaces.DatabaseInterface.connString), false);
			cl.runChecksAtURLs(new String[] { "https://www.wufoo.com/html5/tabindex-attribute/" }, true, db = new DatabaseInterface(tests.interfaces.DatabaseInterface.connString), false);
			new ConformanceReport().generateReportFromPage(db, "google.com", new SeleniumInterface());
			//cl.runChecksAtURLs(new String[] { "https://www.guru99.com/execute-javascript-selenium-webdriver.html" }, true, db = new DatabaseInterface(tests.interfaces.DatabaseInterface.connString), true);
			//new ConformanceReport().generateReportFromPage(db, "guru99.com");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}