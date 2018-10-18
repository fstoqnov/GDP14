package code;

import code.checks.CheckList;

public class RunChecks {
	public static void main(String[] args) {
		for (String url : args) {
			CheckList cl = new CheckList();
			cl.runChecksAtURL(url);
		}
	}
}