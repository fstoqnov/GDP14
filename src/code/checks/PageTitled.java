package code.checks;

import java.util.List;

import org.openqa.selenium.WebElement;

import code.Marker;
import code.interfaces.SeleniumInterface;

public class PageTitled extends Check {

	private static String ERR_NO_TITLE() { return "Document does not have a title"; }
	private static String WARNING_TITLE_FOUND(String title) { return "Ensure that this title is a good title for the document. Title found: " + title; }
	
	private static enum Result implements ResultSet {
		ERROR,
		SUCCESS,
		WARNING_TITLE_FOUND
	}
	public PageTitled() {
		super("Criterion 2.4.2 Page Titled");
	}

	public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) {
		WebElement[] title = inter.getElementsByTagName("title");
		WebElement[] doc = inter.getElementsByTagName("html");
		boolean titleExists = false;
		WebElement titlehead = null;
		
		for (int i = 0; i < title.length; i ++) {
			if(title[i] != null) {
				titleExists = true;
				titlehead = title[i];
			} 
		}
		
		if(titleExists == false) {
			addFlagToElement(markers, Marker.MARKER_ERROR, doc[0], ERR_NO_TITLE(), Result.ERROR); //title not found on the page
		} else {
			addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, titlehead, WARNING_TITLE_FOUND(titlehead.getText()), Result.WARNING_TITLE_FOUND); //title found at titlehead location, might not be described, can't tell
		}							  
	}
	
	@Override
	public String[] getHTMLPass() {
		//empty string passed due to no success case for guideline in current implementation
		return new String[] {};
	}

	@Override
	public String[] getHTMLFail() {
		return new String[] {
				"<html>No Title present in document</html>"
		};
	}

	@Override
	public void initialise() {
		
	}

}
