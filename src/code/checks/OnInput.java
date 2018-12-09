package code.checks;

import java.util.List;

import org.openqa.selenium.WebElement;

import code.Marker;
import code.interfaces.SeleniumInterface;

public class OnInput extends Check {

	private static final String WARNING_NO_SUBMIT = "Ensure that <form> elements do not initiate a 'change-of-context without the user's knowledge - this form has no submit button. This may also make it unclear for the user how to submit the values entered in the form fields.";
	private static final String WARNING_SUBMIT_PRESENT = "Ensure that <form> elements other than explicit submit buttons do not initiate a 'change-of-context' for the user";
	public OnInput() {
		super("Criterion 3.2.2 On Input");
	}

	public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) {
		this.checkFormSubmitButtons(markers, inter);
	}
	
	private void checkFormSubmitButtons(List<Marker> markers, SeleniumInterface inter) {
		WebElement[] formElements = inter.getElementsByTagName("form");
		for (int i = 0; i < formElements.length; i++) {
			WebElement form = formElements[i];
			int formButtonCount = inter.getChildrenWithTag(form, "button").length;
			int formInputSubmitCount = inter.getChildrenWithAttributeAndTag(form, "type", "submit", "input").length;
			int formInputButtonCount = inter.getChildrenWithAttributeAndTag(form, "type", "button", "input").length;
			int formInputImageCount = inter.getChildrenWithAttributeAndTag(form, "type", "image", "input").length;
			int formRoleButtonCount = inter.getChildrenWithAttribute(form, "role", "button").length;
			if (formButtonCount+formInputSubmitCount+formInputButtonCount+formInputImageCount+formRoleButtonCount > 0) {
				//there is an element that could be a submit button in this element.
				addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, form, WARNING_SUBMIT_PRESENT);
			}
			else {
				addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, form, WARNING_NO_SUBMIT);

			}
			

		}
		
		
		
	}

	@Override
	public void initialise() {
		
	}

	@Override
	public String[] getHTMLPass() {
		//invalid because it only throws a warning - never passes.
		return null;
	}

	@Override
	public String[] getHTMLFail() {
		//invalid because it only throws a warning - never fails.
		return null;
	}
	
	

}
