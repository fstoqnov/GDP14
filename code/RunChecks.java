package code;

import code.checks.CheckList;

public class RunChecks {
	public static void main(String[] args) {
		CheckList cl = new CheckList();
		cl.runChecksAtURLs(args);
	}
}