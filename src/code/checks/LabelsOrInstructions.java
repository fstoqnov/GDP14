package code.checks;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import code.Marker;
import code.interfaces.SeleniumInterface;
import code.structures.LabelledWebElement;
import tests.Test;

public class LabelsOrInstructions extends Check {
	private static String ERR_ARIA_DESCRIBED_BY_MISSING() { return "The element specified in aria-describedby is missing";}
	private static String ERR_ARIA_DESCRIBED_BY_EMPTY() { return "No elements were specified in aria-describedby";}
	
	private static String ERR_ARIA_LABELLED_BY_MISSING() { return "An element specified in aria-labelledby is missing";}
	private static String ERR_ARIA_LABELLED_BY_EMPTY() { return "No elements specified in aria-labelledby";}
	
	private static String ERR_LABEL_MISSING() { return "No accessible label provided for this form control element";}
	
	private static String ERR_OPTION_NO_TEXT() { return "This <option> element does not have accessible text to differentiate it from other options";}
	
	private static String WARNING_SRS_TITLE_ONLY() { return "Primary label for this element is a 'title' attribute, which is not always accessible to all users";}
	
	private static String WARNING_RECAPTCHA_TEXTAREA() { return "Recaptcha is not WCAG2.1 compliant - this <textarea> element has no accessible label";}
	
	private static String WARNING_HAS_LABEL() { return "Ensure that this label is sufficient to identify the associated form control field: "; }
	
	private static enum ResultType implements Result {
		ERROR,
		SUCCESS,
		WARNING_RECAPTCHA_TEXTAREA,
		WARNING_SRS_TITLE_ONLY,
		WARNING_HAS_LABEL
	}
	public LabelsOrInstructions() {
		super("Criterion 3.3.2 Labels or Instructions");
	}

	@Override
	public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) {
		//we can only find "<input>", "<select>", "<button>", and "<textarea>" form control elements.
		WebElement[] inputEles = inter.getElementsByTagName("input");
		WebElement[] selectEles = inter.getElementsByTagName("select");
		WebElement[] buttonEles = inter.getElementsByTagName("button");
		WebElement[] textareaEles = inter.getElementsByTagName("textarea");
		
		WebElement[] labelEles = inter.getElementsByTagName("label");

		ArrayList<LabelledWebElement> formControlLabels = new ArrayList<LabelledWebElement>();
		for (int i = 0; i < inputEles.length; i++) {
			//if input type="hidden", then we ignore this.
			String inputType;
			if ((inputType = inputEles[i].getAttribute("type")) != null) {
				if (inputType.equals("hidden")) {
					continue;
				}
			}
			LabelledWebElement eleLabel = new LabelledWebElement(inputEles[i]);
			this.checkInputLabel(markers, inputEles[i], eleLabel, labelEles, inter);
		}
		for (int i = 0; i < selectEles.length; i++) {
			LabelledWebElement eleLabel = new LabelledWebElement(selectEles[i]);
			this.checkSelectLabel(markers, selectEles[i], eleLabel, labelEles, inter);
		}
		for (int i = 0; i < buttonEles.length; i++) {
			LabelledWebElement eleLabel = new LabelledWebElement(buttonEles[i]);
			this.checkButtonLabel(markers, buttonEles[i], eleLabel, labelEles, inter);
		}
		for (int i = 0; i < textareaEles.length; i++) {
			String elementID = textareaEles[i].getAttribute("id");
			if (elementID != null) {
				if (elementID.equals("g-recaptcha-response")) {
					addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, textareaEles[i], WARNING_RECAPTCHA_TEXTAREA(), ResultType.WARNING_RECAPTCHA_TEXTAREA);
					continue;
				}
			}
			LabelledWebElement eleLabel = new LabelledWebElement(textareaEles[i]);
			this.checkElementLabel(markers, textareaEles[i], eleLabel, labelEles, inter);
		}
				
	}
	
	//a label element can have labels from getText() or from images inside the label element.
	private void getLabelEleLabels(List<Marker> markers, WebElement labelEle, LabelledWebElement eleLabel) {
		eleLabel.addLabel(labelEle.getAttribute("textContent"));
		this.addContainedImageLabels(labelEle, eleLabel);
	}
	
	

	private void checkInputLabel(
			List<Marker> markers, WebElement inputEle, 
			LabelledWebElement eleLabel, WebElement[] labelEles, SeleniumInterface inter) {

		//there are a some special cases to handle for 'input' elements.
		String type = inputEle.getAttribute("type");
		if (type != null) {
			if (type.equals("button") || type.equals("submit") || type.equals("reset")) {
				String value = inputEle.getAttribute("value");
				if (value != null) {
					eleLabel.addLabel(value);
				}
			}
			else if (type.equals("image")) {
				String alt = inputEle.getAttribute("alt");
				if (alt != null) {
					eleLabel.addLabel(alt);
				}
				
			}
		}
		this.checkElementLabel(markers, inputEle, eleLabel, labelEles, inter);
	}
	
	private void checkSelectLabel(
			List<Marker> markers, WebElement selectEle, 
			LabelledWebElement eleLabel, WebElement[] labelEles, 
			SeleniumInterface inter) {
		//ensure that all contained <option> elements have accessible text to differentiate them.
		
		List<WebElement> optgroups = selectEle.findElements(By.xpath("optgroup"));
		List<WebElement> optionEles = selectEle.findElements(By.xpath("option"));
		//add all options that are part of <optgroup>s:
		for (WebElement optgroup: optgroups) {
			optionEles.addAll(optgroup.findElements(By.xpath("option")));
		}
		
		for (int i = 0;i < optionEles.size(); i++) {
			String optionText = optionEles.get(i).getAttribute("textContent");
			if (optionText.equals("")) {
				addFlagToElement(markers, Marker.MARKER_ERROR, optionEles.get(i), ERR_OPTION_NO_TEXT(), ResultType.ERROR);
			}
		}
		
		this.checkElementLabel(markers, selectEle, eleLabel, labelEles, inter);
	}
	
	
	private void checkButtonLabel(
			List<Marker> markers, WebElement buttonEle, 
			LabelledWebElement eleLabel, WebElement[] labelEles, 
			SeleniumInterface inter) {
		//default location for a label on a button is the readable text of the button
		eleLabel.addLabel(buttonEle.getAttribute("textContent"));
		
		//if a button contains an <img> element, an alt tag for that image becomes part of the effective label.
		this.addContainedImageLabels(buttonEle, eleLabel);
		
		
		this.checkElementLabel(markers, buttonEle, eleLabel, labelEles, inter);
	}
	
	
	//<button> or <label> objects can contain <img> tags, and the 'alt' attributes of these become ...
	//part of the effective label.
	private void addContainedImageLabels(WebElement ele, LabelledWebElement eleLabel) {
		List<WebElement> childrenElements = ele.findElements(By.xpath(".//*"));
		
		for (WebElement child: childrenElements) {
			if (child.getTagName().equals("img")) {
				//add the 'alt' tag of any images that are children of this button 
				String alt = child.getAttribute("alt");
				if (alt != null) {
					eleLabel.addLabel(alt);
				}
			}
		}
	}
	
	
	private void checkElementLabel(
			List<Marker> markers, WebElement ele, 
			LabelledWebElement eleLabel, WebElement[] labelEles, 
			SeleniumInterface inter) {
		//No special cases will be handled here in this function
		//We look for use of 'aria-labelledby',
		//or use of 'aria-label',
		//or use of a <label> tag - either referenced by id, or encapsulating this element
		//or use of 'title' in place of a label
		
		//if there is no accessible primary label, we raise an error.	

		String labelledby;
		String ariaLabelText;
		if ((labelledby = ele.getAttribute("aria-labelledby")) != null) {
			String[] labelIDs = labelledby.split(" ");
			for (String labelID: labelIDs) {
				WebElement labelElement = inter.getElementById(labelID);
				if (labelElement != null) {
					eleLabel.addLabel(labelElement.getAttribute("textContent"));
					//can't use getText() as that Fails with 'hidden' element in css, in cases where the text should still be accessible.
				}
				else {
					addFlagToElement(markers, Marker.MARKER_ERROR, ele, ERR_ARIA_LABELLED_BY_MISSING(), ResultType.ERROR);
			
				}
			}
			if (labelIDs.length == 0) {
				addFlagToElement(markers, Marker.MARKER_ERROR, ele, ERR_ARIA_LABELLED_BY_EMPTY(), ResultType.ERROR);
			}
		}
			
		if ((ariaLabelText = ele.getAttribute("aria-label")) != null) {
			eleLabel.addLabel(ariaLabelText);
		}
		

		String eleID = ele.getAttribute("id");
		if (eleID != null) { //might be referenced by labels
			for (int i = 0; i < labelEles.length; i++) {
				String labelForID;
				if ((labelForID = labelEles[i].getAttribute("for")) != null) {
					if (labelForID.equals(eleID)) {
						this.getLabelEleLabels(markers, labelEles[i], eleLabel);
					}
				}
			}
		}


		//check for parent elements that are labels.
		WebElement checkForParentLabels = ele;
		while ((checkForParentLabels = checkForParentLabels.findElement(By.xpath(".."))) != null) {
			if (checkForParentLabels.getTagName().equals("label")) {
				this.getLabelEleLabels(markers, checkForParentLabels, eleLabel);
				break;
			}
			else if (checkForParentLabels.getTagName().equals("html")) {
				//this is the parent <html> tag - no labels outside this.
				break;
			}
		}

		//check for labels using "aria-describedby"
		String describedby;
		if ((describedby = ele.getAttribute("aria-describedby")) != null) {
			String[] descIDs = describedby.split(" ");
			for (String descID: descIDs) {
				WebElement descElement = inter.getElementById(descID);
				if (descElement != null) {
					//can't use 'getText()' as that fails when the element is css hidden - even if that text is still accessible for accesibility tools
					eleLabel.addLabel(descElement.getAttribute("textContent")); 
				}
				else {
					addFlagToElement(markers, Marker.MARKER_ERROR, ele, ERR_ARIA_DESCRIBED_BY_MISSING(), ResultType.ERROR);
				}
			}
			if (descIDs.length == 0) {
				addFlagToElement(markers, Marker.MARKER_ERROR, ele, ERR_ARIA_DESCRIBED_BY_EMPTY(), ResultType.ERROR);
			}
		}
		


		String elementTitle;
		if ((elementTitle = ele.getAttribute("title"))!= null ) {
			int prevLabelsSize = eleLabel.getLabelsSize();
			eleLabel.addLabel(elementTitle);
			int newLabelsSize = eleLabel.getLabelsSize();
			if (newLabelsSize == 1 && prevLabelsSize == 0) {
				//if the 'label' is blank ("") then no label is added.
				//this tests for that case, and if not: this is the primary label so raises the error.
				addFlagToElement(markers, Marker.MARKER_AMBIGUOUS_SERIOUS, ele, WARNING_SRS_TITLE_ONLY(), ResultType.WARNING_SRS_TITLE_ONLY);

			}

		}

		
		if (eleLabel.getLabelsSize() == 0) {
			addFlagToElement(markers, Marker.MARKER_ERROR, ele, ERR_LABEL_MISSING(), ResultType.ERROR);
		}
		else {
			addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, ele, WARNING_HAS_LABEL(), ResultType.WARNING_HAS_LABEL);
		}
	}

	public void setupTests() {
		//using label 'for='
		this.tests.add(new Test("<label for=\"inputId\">description</label><input id=\"inputId\">", 
				new Result[] {ResultType.WARNING_HAS_LABEL}));
		
		//aria described by
		this.tests.add(new Test("<input id=\"inputId\" aria-describedby=\"description\"><div id=\"description\">A quick description</div>", 
				new Result[] {ResultType.WARNING_HAS_LABEL}));
		
		//aria labelled by
		this.tests.add(new Test("<input id=\"inputId\" aria-labelledby=\"description other\">\n"
				+ "<div id=\"description\">A quick description</div><span id=\"other\">extended</span>", 
				new Result[] {ResultType.WARNING_HAS_LABEL}));
		
		//aria labelled by hidden - should not make a difference
		this.tests.add(new Test("<input id=\"inputId\" aria-labelledby=\"description\"><div style=\"display:none\" id=\"description\">A quick description</div>", 
				new Result[] {ResultType.WARNING_HAS_LABEL}));
		
		//using 'aria-label' and an encapsulating <label>., 
		this.tests.add(new Test("<fieldset><legend>Car Details</legend>\n"
				+ "<input type=\"checkbox\" aria-label=\"Audi\"AUDI>\n"
				+ "<input type=\"checkbox\" aria-label=\"Ford\"FORD></fieldset>\n"
				+ "<label>Click when happy with selection\n"
				+ "<input type=\"submit\">", 
				new Result[] {ResultType.WARNING_HAS_LABEL}));
		
		//one good, one missing
		this.tests.add(new Test("<form> <input> <label for=\"inputId\">description</label><input id=\"inputId\"> </form>", 
				new Result[] {ResultType.ERROR, ResultType.WARNING_HAS_LABEL}));
		this.tests.add(new Test("<input>", 
				new Result[] {ResultType.ERROR}));
		
		//aria described by missing
		this.tests.add(new Test("<input id=\"inputId\" aria-describedby=\"description\">", 
				new Result[] {ResultType.ERROR}));
		
		//aria labelled by missing
		this.tests.add(new Test("<input id=\"inputId\" aria-labelledby=\"description other\"><div id=\"description\">A quick description</div>", 
				new Result[] {ResultType.ERROR, ResultType.WARNING_HAS_LABEL}));
		
		//aria labelled by empty
		this.tests.add(new Test("<input id=\"inputId\" aria-labelledby=\"\">", 
				new Result[] {ResultType.ERROR}));
		
		//using checkboxes with text, but no accessible label.
		this.tests.add(new Test("<fieldset><legend>Car Brands</legend>\n"
				+ "<input type=\"checkbox\"AUDI>\n"
				+ "<input type=\"checkbox\"FORD></fieldset>", 
				new Result[] {ResultType.ERROR}));
		
		//only label is a 'title' attribute
		this.tests.add(new Test("<input id=\"inputId\" type=\"text\" title=\"Enter your registration no. here\">", 
				new Result[] {ResultType.WARNING_SRS_TITLE_ONLY, ResultType.WARNING_HAS_LABEL}));
	}
	

	@Override
	public void initialise() {}

}