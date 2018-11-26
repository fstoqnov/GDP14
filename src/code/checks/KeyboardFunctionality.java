package code.checks;

import java.util.List;

import org.openqa.selenium.WebElement;

import code.Marker;
import code.interfaces.SeleniumInterface;

public class KeyboardFunctionality extends Check {
	public KeyboardFunctionality() {
		super("Criterion 2.1.1 Keyboard");
	}

	@Override
	public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) {
		//Initially just want to check that all elements do not have tabIndex greater than 0.
		System.out.println("Starting Keyboard Functionality Test");
		WebElement[] tabIndexEle = inter.getElementsWithAttribute("tabindex");
		System.out.println("Number of 'tabindex' found is " + String.valueOf(tabIndexEle.length));
		for (int i = 0; i < tabIndexEle.length; i ++) {
			String tabIndexVal = tabIndexEle[i].getAttribute("tabindex");
			if (Integer.valueOf(tabIndexVal) > 0) {
				addFlagToElement(markers, Marker.MARKER_ERROR, tabIndexEle[i], "Must not use tabindex greater than 0");
			}
			else {
				addFlagToElement(markers, Marker.MARKER_SUCCESS, tabIndexEle[i], "Tabindex used not greater than 0");

			}
		}
	}

	@Override
	public void initialise() {
		//nothing to do
	}

	@Override
	public String[] getHTMLPass() {
		return new String[] {
				"<input type=\"button\" tabindex=\"0\" aria-label=\"Close\">",
				"<input type=\"button\" tabindex=\"-1\" aria-label=\"Skip Link\">"
		};
	}

	@Override
	public String[] getHTMLFail() {
		return new String[] {
				"<input type=\"button\" tabindex=\"1\" aria-label=\"Open\">",
				"<input type=\"button\" tabindex=\"2\" aria-label=\"Close\">"
		};
	}
	
	
	
	
	
	
	
}
