package code.checks;

import code.selenium_interface.Interface;

public abstract class Check {
	private String name;

	protected Check(String name) {
		this.name = name;
	}

	public abstract boolean runTest(String urlContent, Interface inter);

	public String getName() { return name; }
	
	public void outputPassed() {
		System.out.println("Passed test '" + getName() + "'");
	}
	
	public void outputFailed() {
		System.out.println("Failed test '" + getName() + "'");
	}
}