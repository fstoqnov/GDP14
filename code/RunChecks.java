package code;

import code.checks.CheckList;

public class RunChecks {
	public static void main(String[] args) {
		CheckList cl = new CheckList();
		try {
			cl.runChecksAtURLs(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}