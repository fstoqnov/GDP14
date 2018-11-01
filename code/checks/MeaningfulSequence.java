package code.checks;

import java.util.List;

import code.Marker;
import code.interfaces.SeleniumInterface;

public class MeaningfulSequence extends Check {

	public MeaningfulSequence() {
		super("Criterion 1.3.2 Meaningful Sequence");
	}

	public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) {
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
