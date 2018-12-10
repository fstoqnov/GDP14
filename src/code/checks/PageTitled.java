package code.checks;

import java.util.List;

import org.openqa.selenium.WebElement;

import code.Marker;
import code.interfaces.SeleniumInterface;
import tests.Test;

public class PageTitled extends Check {

	private static String ERR_NO_TITLE() { return "Document does not have a title"; }
	private static String WARNING_TITLE_FOUND(String title) { return "Ensure that this title is a good title for the document. Title found: " + title; }
	
	private static enum ResultType implements Result {
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
			addFlagToElement(markers, Marker.MARKER_ERROR, doc[0], ERR_NO_TITLE(), ResultType.ERROR); //title not found on the page
		} else {
			addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, titlehead, WARNING_TITLE_FOUND(titlehead.getText()), ResultType.WARNING_TITLE_FOUND); //title found at titlehead location, might not be described, can't tell
		}							  
	}
	
	public void setupTests() {
		tests.add(new Test("<html>No Title present in document</html>", new Result[] {ResultType.ERROR}));
		tests.add(new Test("<html> <head> <title>Hello everyone</title> </head> <body> wow lots going on today </body> </html>", new Result[] {ResultType.WARNING_TITLE_FOUND}));

	}

	@Override
	public void initialise() {
		
	}

}
