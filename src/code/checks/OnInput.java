package code.checks;

import java.util.List;

import org.openqa.selenium.WebElement;

import code.Marker;
import code.interfaces.SeleniumInterface;
import tests.Test;

public class OnInput extends Check {

	private static final String WARNING_SRS_NO_SUBMIT() { return "Ensure that <form> elements do not initiate a 'change-of-context without the user's knowledge - this form has no submit button. This may also make it unclear for the user how to submit the values entered in the form fields."; }
	private static final String WARNING_SUBMIT_PRESENT() { return "Ensure that <form> elements other than explicit submit buttons do not initiate a 'change-of-context' for the user"; }
	
	private static enum ResultType implements Result {
		ERROR,
		SUCCESS,
		WARNING_SRS_NO_SUBMIT,
		WARNING_SUBMIT_PRESENT
	}
	
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
				addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, form, WARNING_SUBMIT_PRESENT(), ResultType.WARNING_SUBMIT_PRESENT);
			}
			else {
				addFlagToElement(markers, Marker.MARKER_AMBIGUOUS_SERIOUS, form, WARNING_SRS_NO_SUBMIT(), ResultType.WARNING_SRS_NO_SUBMIT);

			}
			

		}
		
		
		
	}

	@Override
	public void initialise() {
		
	}
	
	public void setupTests() {
		//form with input and button
		this.tests.add(new Test("<form><input type=\"text\"><button type=\"button\">CLICK HERE TO SUBMIT</button></form>", new Result[] {ResultType.WARNING_SUBMIT_PRESENT}));
		//form with no button
		this.tests.add(new Test("<form><input type=\"text\"></form>", new Result[] {ResultType.WARNING_SRS_NO_SUBMIT}));
		
		//both
		this.tests.add(new Test("<form><input type=\"text\"></form>\n<form><input type=\"text\"><button type=\"button\">CLICK HERE TO SUBMIT</button></form>", 
				new Result[] {ResultType.WARNING_SUBMIT_PRESENT, ResultType.WARNING_SRS_NO_SUBMIT}));

		
		

	}
	

}
