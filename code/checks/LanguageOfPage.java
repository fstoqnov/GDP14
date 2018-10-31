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
			if(doc[i].getAttribute("language") == null) {
				addFlagToElement(markers, Marker.MARKER_ERROR, doc[i]);
			} else if(!lang.contains(doc[i].getAttribute("language"))){
				addFlagToElement(markers, Marker.MARKER_ERROR, doc[i]);;
			}
			addFlagToElement(markers, Marker.MARKER_SUCCESS, doc[i]);
		}
		
	}

	@Override
	public String[] getHTMLPass() {
		return new String[] {
				"<html lang=\"language\"></html>"
		};
	}

	@Override
	public String[] getHTMLFail() {
		return new String[] {
				"<html lang=\"not-included\"></html>"
		};
	}

	@Override
	public void initialise() {
		lang = new ArrayList<String>();
		lang.addAll(Arrays.asList(Locale.getISOLanguages()));
		
	}

}