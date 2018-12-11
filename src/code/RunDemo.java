package code;

import java.util.ArrayList;

import code.interfaces.DatabaseInterface;
import code.interfaces.SeleniumInterface;

public class RunDemo {
	public static void main (String[] args) {
		new SeleniumInterface(true);
		/*CheckList cl = new CheckList();
		DatabaseInterface db;
		try {
			//String testURL = "https://google.co.uk";
			//String testURL = "https://ssl.bbc.co.uk/faqs/forms";
			String testURL = "https://www.wufoo.com/html5/tabindex-attribute/";
			cl.runChecksAtURLs(new String[] {testURL}, true, db = new DatabaseInterface(tests.interfaces.DatabaseInterface.connString), false);
			new ConformanceReport().generateReportFromPage(db, testURL, new SeleniumInterface());
			//cl.runChecksAtURLs(new String[] { "https://www.guru99.com/execute-javascript-selenium-webdriver.html" }, true, db = new DatabaseInterface(tests.interfaces.DatabaseInterface.connString), true);
			//new ConformanceReport().generateReportFromPage(db, "guru99.com");
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
}