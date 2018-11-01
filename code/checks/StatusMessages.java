package code.checks;

import java.util.List;

import code.Marker;
import code.interfaces.SeleniumInterface;

public class StatusMessages extends Check {

    public StatusMessages() { super("Criterion 4.1.3 Status Messages"); }

	public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) {
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