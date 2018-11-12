package code;

import code.interfaces.DatabaseInterface;

public class RunDemo {
	public static void main (String[] args) {
		CheckList cl = new CheckList();
		DatabaseInterface db;
		try {
			cl.runChecksAtURLs(new String[] { "https://www.google.com/" }, true, db = new DatabaseInterface(tests.interfaces.DatabaseInterface.connString));
			new ConformanceReport().generateReportFromPage(db, "google.com");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}