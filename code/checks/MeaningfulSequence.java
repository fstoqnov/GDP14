package code.checks;

import code.selenium_interface.Interface;

public class MeaningfulSequence extends Check {

	protected MeaningfulSequence() {
		super("Criterion 1.3.2 Meaningful Sequence");
	}

	@Override
	public boolean runCheck(String urlContent, Interface inter) {
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
