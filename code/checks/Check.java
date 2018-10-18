package code.checks;

import code.selenium_interface.Interface;

public abstract class Check {
	private String name;

	protected Check(String name) {
		this.name = name;
	}

	//runs the check on the url content and the selenium interface to the page
	public abstract boolean runCheck(String urlContent, Interface inter);

	public String getName() { return name; }
	
	public void outputPassed() {
		System.out.println("Passed test '" + getName() + "'");
	}
	
	public void outputFailed() {
		System.out.println("Failed test '" + getName() + "'");
	}

	public abstract String[] getHTMLPass();

	public abstract String[] getHTMLFail();
}