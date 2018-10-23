package code.checks;

import java.util.List;

import code.selenium_interface.Interface;

public class MeaningfulSequence extends Check {

	protected MeaningfulSequence() {
		super("Criterion 1.3.2 Meaningful Sequence");
	}

	public void runCheck(String urlContent, List<Marker> markers, Interface inter) {
		// TODO Auto-generated method stub
	}

	@Override
	public String[] getHTMLPass() {
		return null;
	}

	@Override
	public String[] getHTMLFail() {
		return null;
	}

	@Override
	public void initialise() {
		
	}

}
