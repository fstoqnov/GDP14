package code.checks;

import code.selenium_interface.Interface;

public class Orientation extends Check {
	public Orientation() {
		super("Orientation");
	}

	@Override
	public boolean runTest(String content, Interface inter) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getHTMLPass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getHTMLFail() {
		// TODO Auto-generated method stub
		return null;
	}
}