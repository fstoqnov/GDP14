package code.checks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.detectlanguage.DetectLanguage;
import com.detectlanguage.Result;
import com.detectlanguage.errors.APIError;

import org.openqa.selenium.WebElement;

import code.Marker;
import code.interfaces.SeleniumInterface;

public class LanguageOfParts extends Check {
	
	public LanguageOfParts() {
		super("Criterion 3.1.2 Language of Parts");
	}
	
	@Override
	public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) {
		List<WebElement> eles = inter.getAllElements();
		WebElement[] htmlHead = inter.getElementsByTagName("html");
		HashSet<String> langTags = new HashSet<String>();
		DetectLanguage.apiKey = "a2d999f4d1ed25be3ea6606a5c3c463c";
		List<Result> results = null;
		List<String> languagesFound = new ArrayList<String>();
		int languagesMatched = 0;
		
		try {
			results = DetectLanguage.detect(htmlHead[0].getText());
		} catch (APIError e) {
			System.out.println("API ERROR CHECK IF KEY IS VALID");
			e.printStackTrace();
		}
		
		for(Result asd : results) {
			languagesFound.add(asd.language);
		}
		
		for (WebElement element : eles) {
			if(element.getAttribute("lang") != null) {
				langTags.add(element.getAttribute("lang"));
			}
		}
		
		ArrayList<String> languageTagsFound = new ArrayList<String>(langTags);
		languageTagsFound.remove(0);
		
				
		if(languageTagsFound.size() == languagesFound.size()) {
			if(languageTagsFound.equals(languagesFound)) {
				addFlagToElement(markers, Marker.MARKER_SUCCESS, htmlHead[0], "Languages on page are properly declared"); //markers are in the correct place provided not more than once instance of a language is present
			} else {
				addFlagToElement(markers, Marker.MARKER_ERROR, htmlHead[0], "Some/All languages on page are not properly declared"); //markers are not in the correct place when only one instance is present
			}
		} else{
			for(String languageInText : languagesFound) {
				if(langTags.contains(languageInText)) {
					languagesMatched = languagesMatched + 1;
				}
			}
			if(languagesMatched == languagesFound.size()) {
				addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, htmlHead[0], "Languages are declared, please ensure they are in their correct positions"); //markers are the same, but no way to tell if they are declared in all situations
			} else {
				addFlagToElement(markers, Marker.MARKER_ERROR, htmlHead[0], "Languages are not properly declared, language tags are missing in some positions"); //one or more language declared in text missing from the lang declarations
			}
		}
	}
	
	@Override
	public String[] getHTMLPass() {
		return new String[] {
				"<html>" 
				+ "<span title=\"Spanish\"><a lang=\"es\">Español. Buenos días, Esteban. ¿Cómo estás? Como siempre.</a></span>"
				+ "<blockquote lang=\"de\">"
				+ "<p>"
				+ "Da dachte der Herr daran, ihn aus dem Futter zu schaffen,\r\n" + 
				"    aber der Esel merkte, daß kein guter Wind wehte, lief fort\r\n" + 
				"    und machte sich auf den Weg nach Bremen: dort, meinte er,\r\n" + 
				"    könnte er ja Stadtmusikant werden.</a></span>"+
				"</p>" +
				"</html>"
		};
	}

	@Override
	public String[] getHTMLFail() {
		return new String[] {
				"<html>" 
				+ "<span title=\"Spanish\"><a>Español. Buenos días, Esteban. ¿Cómo estás? Como siempre.</a></span>"
				+ "<blockquote lang=\"de\">"
				+ "<p>"
				+ "Da dachte der Herr daran, ihn aus dem Futter zu schaffen,\r\n" + 
				"    aber der Esel merkte, daß kein guter Wind wehte, lief fort\r\n" + 
				"    und machte sich auf den Weg nach Bremen: dort, meinte er,\r\n" + 
				"    könnte er ja Stadtmusikant werden.</a></span>"+
				  "</p>" +
				"</html>"
		};
	}

	@Override
	public void initialise() {}
}