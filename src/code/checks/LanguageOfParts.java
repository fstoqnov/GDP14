package code.checks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.detectlanguage.DetectLanguage;
import com.detectlanguage.errors.APIError;
import com.detectlanguage.Result;

import org.openqa.selenium.WebElement;

import code.Marker;
import code.interfaces.SeleniumInterface;
import tests.Test;

public class LanguageOfParts extends Check {
	
	public LanguageOfParts() {
		super("Criterion 3.1.2 Language of Parts");
	}
	
	private static String ERR_MISSING_LANG() {return "Some/All languages on page are not properly declared";}
	private static String WARNING_LANG_UNCONFIRMED_PLACE() {return "Languages are declared, please ensure they are in their correct positions";}
	private static String SUCC_LANG_DECLARED() { return "Languages on page are properly declared"; }
	
	private static enum ResultType implements ResultT {
		ERROR,
		SUCCESS,
		WARNING_LANG_UNCONFIRMED_PLACE
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
		
		System.out.println("Given text content: "+ htmlHead[0].getText());
		for(Result asd : results) {
			System.out.println("Found language " + asd.language);
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
				addFlagToElement(markers, Marker.MARKER_SUCCESS, htmlHead[0], SUCC_LANG_DECLARED(), ResultType.SUCCESS); //markers are in the correct place provided not more than once instance of a language is present
			} else {
				addFlagToElement(markers, Marker.MARKER_ERROR, htmlHead[0], ERR_MISSING_LANG(), ResultType.ERROR); //markers are not in the correct place when only one instance is present
			}
		} else{
			for(String languageInText : languagesFound) {
				if(langTags.contains(languageInText)) {
					languagesMatched = languagesMatched + 1;
				}
			}
			if(languagesMatched == languagesFound.size()) {
				addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, htmlHead[0], WARNING_LANG_UNCONFIRMED_PLACE(), ResultType.WARNING_LANG_UNCONFIRMED_PLACE); //markers are the same, but no way to tell if they are declared in all situations
			} else {
				addFlagToElement(markers, Marker.MARKER_ERROR, htmlHead[0], ERR_MISSING_LANG(), ResultType.ERROR); //one or more language declared in text missing from the lang declarations
			}
		}
	}
	
	public void setupTests() {
		this.tests.add(new Test("<html>" 
				+ "<span title=\"Spanish\"><a lang=\"es\">Español. Buenos días, Esteban. ¿Cómo estás? Como siempre.</a></span>"
				+ "<blockquote lang=\"de\">"
				+ "<p>"
				+ "Da dachte der Herr daran, ihn aus dem Futter zu schaffen,\r\n" + 
				"    aber der Esel merkte, daÃŸ kein guter Wind wehte, lief fort\r\n" + 
				"    und machte sich auf den Weg nach Bremen: dort, meinte er,\r\n" + 
				"    kÃ¶nnte er ja Stadtmusikant werden.</a></span>"+
				"</p>" +
				"</html>",
				new ResultT[] {ResultType.SUCCESS}));
		
		this.tests.add(new Test("<html>" 
				+ "<span title=\"Spanish\"><a>Español. Buenos días, Esteban. ¿Cómo estás? Como siempre.</a></span>"
				+ "<blockquote lang=\"de\">"
				+ "<p>"
				+ "Da dachte der Herr daran, ihn aus dem Futter zu schaffen,\r\n" + 
				"    aber der Esel merkte, daÃŸ kein guter Wind wehte, lief fort\r\n" + 
				"    und machte sich auf den Weg nach Bremen: dort, meinte er,\r\n" + 
				"    kÃ¶nnte er ja Stadtmusikant werden.</a></span>"+
				  "</p>" +
				"</html>",
				new ResultT[] {ResultType.ERROR}));
	}


	@Override
	public void initialise() {}
}