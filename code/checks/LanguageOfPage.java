package code.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.WebElement;

import code.interfaces.SeleniumInterface;

public class LanguageOfPage extends Check {
	
	private ArrayList<String> lang;
	
	protected LanguageOfPage() {
		super("Criterion 3.1.1 Language of Page");
	}

	@Override
	public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) {
		WebElement[] doc = inter.getElementsByTagName("html");
		for (int i = 0; i < doc.length; i ++) {
			if(doc[i].getAttribute("lang") == null) {
				addFlagToElement(markers, Marker.MARKER_ERROR, doc[i]);
			} else if(!lang.contains(doc[i].getAttribute("lang"))){
				addFlagToElement(markers, Marker.MARKER_ERROR, doc[i]);
			} else {
				addFlagToElement(markers, Marker.MARKER_SUCCESS, doc[i]);
			}
		}
		
	}

	@Override
	public String[] getHTMLPass() {
		return new String[] {
				"<html lang=\"en\"></html>"
		};
	}

	@Override
	public String[] getHTMLFail() {
		return new String[] {
				"<html lang=\"not-included\"></html>",
				"<html>I have no lang tag specified</html>"
		};
	}

	@Override
	public void initialise() {
		lang = new ArrayList<String>();
		lang.addAll(Arrays.asList(Locale.getISOLanguages()));
		
	}

}