package code.checks;

import java.util.List;

import code.selenium_interface.Interface;

public class InfoAndRelationships extends Check {
	
	protected InfoAndRelationships() {
		super("Criterion 1.3.1 Info and Relationships");
	}

	@Override
	public void runCheck(String urlContent, List<Marker> markers, Interface inter) {
		// TODO Auto-generated method stub
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

	@Override
	public void initialise() {
		// TODO Auto-generated method stub
	}
}