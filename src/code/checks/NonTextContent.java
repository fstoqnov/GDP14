package code.checks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.openqa.selenium.WebElement;

import code.Marker;
import code.interfaces.SeleniumInterface;
import tests.Test;

public class NonTextContent extends Check {

	private static final String WARNING_CHECK_DECORATIVE() {return "Non-Text element is marked up as if it is purely decorative - there is no accessible label. Check that this is accurate"; }
	private static final String WARNING_HAS_LABEL(String label) { return "Non-Text element has been marked up with a label. Check that this label is sufficiently detailed. Label found: " + label; }
	private static final String ERR_NO_LABEL() { return "Non-Text element has no accessible label, but is not hidden to screen-readers."; }
	private static final String WARNING_SRS_LABEL_LENGTH(String label) { return "Label length is longer than 100 - try to keep accessible labels short. Label found: " + label; }
	private static final String WARNING_DESCRIPTION(String description) { return "Non-Text element references a long description. Ensure that this is suitable for describing the image - Description found: " + description; }
	
	private static enum Result implements ResultSet {
		ERROR,
		SUCCESS,
		WARNING_CHECK_DECORATIVE,
		WARNING_HAS_LABEL,
		WARNING_SRS_LABEL_LENGTH,
		WARNING_DESCRIPTION
	}
	public NonTextContent() {
		super("Criterion 1.1.1 Non-Text Content");
	}
	
	private class NonTextLabel {
		String specificLabel;
		ArrayList<String> ariaLabelledbyLabels;
		String titleLabel;
		ArrayList<String> descriptions;
		WebElement ele;
		
		NonTextLabel(WebElement ele) {
			this.ele = ele;
			this.ariaLabelledbyLabels = new ArrayList<String>();
			this.descriptions = new ArrayList<String>();
		}
		
		void setAlt(String s) {
			this.specificLabel = s;
		}
		void addDescription(String s) {
			this.descriptions.add(s);
		}
		void setAriaLabel(String s) {
			this.specificLabel = s;
		}
		void setTitleLabel(String s) {
			this.titleLabel = s;
		}
		void addAriaLabelledByLabel(String s) {
			this.ariaLabelledbyLabels.add(s);
		}
		
		boolean hasSpecificLabel() {
			return this.specificLabel != null;
		}
		String getSpecificLabel() {
			return this.specificLabel;
		}
		
		boolean hasAriaLabelledby() {
			return this.ariaLabelledbyLabels.size() > 0;
		}
		ArrayList<String> getAriaLabelledbyLabels() {
			return this.ariaLabelledbyLabels;
		}
		
		boolean hasTitlelabel() {
			return this.titleLabel != null;
		}
		String getTitleLabel() {
			return this.titleLabel;
		}
		
		boolean hasDescriptions() {
			return this.descriptions.size() > 0;
		}
		ArrayList<String> getDescriptions() {
			return this.descriptions;
		}
		
	}

	@Override
	public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) {
		this.checkVisualAlts(markers, inter);
	}

	private void checkVisualAlts(List<Marker> markers, SeleniumInterface inter) {
		
		WebElement[] elementsWithAlt = inter.getElementsWithAttributeAnyValue("alt");
		HashSet<WebElement> altEles = new HashSet<WebElement>();
		for (int i=0; i < elementsWithAlt.length; i++) {
			altEles.add(elementsWithAlt[i]);
		}
		
		//<input type="image" elemenets will be handled by 2.4.6[HeadingsAndLabels] and 3.2.2[LabelsOrInstructions]
		WebElement[] imgEles = inter.getElementsByTagName("img");
		//images can be labelled by "alt" attribute, or aria-labelledby
		
		WebElement[] areaEles = inter.getElementsByTagName("area");
		//<area> elements form part of <map> elements - client side image maps.
		//<area> elements can be labelled by the "alt" attribute, or "aria-labelledby"
		
		WebElement[] imageRoleEles = inter.getElementsWithAttribute("role", "img");
		//elements with attribute {role="image"} can be labelled by "aria-labelledby" or "aria-label" or "title".
		
		WebElement[] objectEles = inter.getElementsByTagName("object");
		//elements with <object> tag can be described by standard labelling techniques.
		
		System.out.println("Found: img: " + imgEles.length + ", area: " + areaEles.length + ", imageRole: " + imageRoleEles.length + ", object: " + objectEles.length);
		for (int i = 0; i < imgEles.length; i++) {
			NonTextLabel ntl = new NonTextLabel(imgEles[i]);
			this.findImgLabel(markers, imgEles[i], ntl, altEles, inter);
		}
		for (int i=0; i < areaEles.length; i++) {
			NonTextLabel ntl = new NonTextLabel(areaEles[i]);
			this.findImgLabel(markers, areaEles[i], ntl, altEles, inter);
		}
		for (int i=0; i < imageRoleEles.length; i++) {
			NonTextLabel ntl = new NonTextLabel(imageRoleEles[i]);
			this.findRoleImgLabel(markers, imageRoleEles[i], ntl, inter);
		}
		for (int i=0; i < objectEles.length; i++) {
			NonTextLabel ntl = new NonTextLabel(objectEles[i]);
			this.findElementLabel(markers, objectEles[i], ntl, inter);
		}
		
	}
	
	private void findImgLabel(List<Marker> markers, WebElement ele, NonTextLabel ntl, HashSet<WebElement> altEles, SeleniumInterface inter) {
		//check the alt tag and the 'longdesc' tag, which are unique to these elements, then pass it along
		
		if (altEles.contains(ele)) {
			String altTag = ele.getAttribute("alt");
			if (altTag.equals("")) {
				addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, ele, WARNING_CHECK_DECORATIVE(), Result.WARNING_CHECK_DECORATIVE);
				return;
			}
			ntl.setAlt(ele.getAttribute(altTag));
		}
		/*This doesn't work due to a strange quirk either of Selenium, or ChromeDriver
		 * All img elements don't show up as having an 'alt' attribute when searching for it, but
		 * if you call getAttribute("alt") on any element, it will add a blank alt="" attribute - which destroys this method.
		 * This doesn't happen for other attributes (eg 'aria-label') - only for 'alt'.
		 * 
		 * if (altTag != null) {
			if (altTag.equals("")) {
				System.out.println("DECORATIVE alt");
				//this is a sign for a decoration image - accessible tools will ignore this element.
				addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, ele, WARNING_CHECK_DECORATIVE);
				return;
			}
			else {
				ntl.setAlt(altTag);
			}
		}*/
		String longDesc = ele.getAttribute("longdesc");
		if (longDesc != null) {
			if (!longDesc.equals("")) {
				ntl.addDescription(longDesc);
			}
		}
		this.findElementLabel(markers, ele, ntl, inter);
	}
	
	private void findRoleImgLabel(List<Marker> markers, WebElement ele, NonTextLabel ntl, SeleniumInterface inter) {
		//look for aria-label attribute.
		String ariaLabel = ele.getAttribute("aria-label");
		if (ariaLabel != null) {
			if (!ariaLabel.equals("")) {
				ntl.setAriaLabel(ariaLabel);
			}
		}
		this.findElementLabel(markers, ele, ntl, inter);
	}
	
	private void findElementLabel(List<Marker> markers, WebElement ele, NonTextLabel ntl, SeleniumInterface inter) {
		System.out.println("Called findElementLabel");
		//check if element is decorative:
		String eleRole = ele.getAttribute("role");
		if (eleRole != null) {
			if (eleRole.equals("presentation")) {
				System.out.println("DECORATIVE role");
				//this is a sign for a decoration image - accessible tools will ignore this element.
				addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, ele, WARNING_CHECK_DECORATIVE(), Result.WARNING_CHECK_DECORATIVE);
				return;
			}
		}
		
		
		//Find elements that constitute a textual description for the non-texual element:
		
		
		//look for aria-labelledby attribute
		String ariaLabelledby = ele.getAttribute("aria-labelledby");
		if (ariaLabelledby != null) {
			if (!ariaLabelledby.equals("")) {
				String[] labelledIDs = ariaLabelledby.split(" ");
				for (String id: labelledIDs) {
					WebElement referencedEle = inter.getElementById(id);
					String textContent = referencedEle.getAttribute("textContent");
					if (!textContent.equals("")) {
						ntl.addAriaLabelledByLabel(textContent);
					}
				}
			}
		}
		
		String titleLabel = ele.getAttribute("title");
		if (titleLabel != null) {
			if (!titleLabel.equals("")) {
				ntl.setTitleLabel(titleLabel);
			}
		}
		
		String ariaDescribedby = ele.getAttribute("aria-describedby");
		if (ariaDescribedby != null) {
			if (!ariaDescribedby.equals("")) {
				String[] descriptions = ariaDescribedby.split(" ");
				for (int i=0; i < descriptions.length; i++) {
					ntl.addDescription(descriptions[i]);
				}
			}
		}
		this.checkElementLabel(markers,  ele, ntl, inter);

	}
	
	//handle markers for complete labels.
	private void checkElementLabel(List<Marker> markers, WebElement ele, NonTextLabel ntl, SeleniumInterface inter) {
		if (ntl.hasSpecificLabel()) {
			System.out.println("specific label!");
			String specLabel = ntl.getSpecificLabel();
			this.checkLabel(markers, ele, specLabel, inter);
		}
		else if (ntl.hasAriaLabelledby()) {
			System.out.println("arialabelledby!");
			ArrayList<String> labels = ntl.getAriaLabelledbyLabels();
			for (int i=0; i < labels.size(); i++) {
				this.checkLabel(markers, ele, labels.get(i), inter);
			}
		}
		else if (ntl.hasTitlelabel()) {
			System.out.println("title!");
			String titleLabel = ntl.getTitleLabel();
			this.checkLabel(markers, ele, titleLabel, inter);
		}
		else if (ntl.hasDescriptions()) {
			System.out.println("description!");
			ArrayList<String> descriptions = ntl.getDescriptions();
			for (int i=0; i < descriptions.size(); i++) {
				addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, ele, WARNING_DESCRIPTION(descriptions.get(i)), Result.WARNING_DESCRIPTION);
			}
		}
		else {
			System.out.println("no labels!");
			addFlagToElement(markers, Marker.MARKER_ERROR, ele, ERR_NO_LABEL(), Result.ERROR);
		}
	}
	
	private void checkLabel(List<Marker> markers, WebElement ele, String label, SeleniumInterface inter) {
		if (label.length() > 100) {
			addFlagToElement(markers, Marker.MARKER_AMBIGUOUS_SERIOUS, ele, WARNING_SRS_LABEL_LENGTH(label), Result.WARNING_SRS_LABEL_LENGTH);
		}
		else {
			addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, ele, WARNING_HAS_LABEL(label), Result.WARNING_HAS_LABEL);
		}
	}
	
	
	
	@Override
	public void initialise() {
		
	}

	
	public void setupTests() {
		
		//using alt
		this.tests.add(new Test("<img src=\"smiley.gif\" alt=\"Smiley face\">", 
				new ResultSet[] {Result.SUCCESS}));
		
		//using title
		this.tests.add(new Test("<img src=\"smiley.gif\" title=\"Smiley face\">", 
				new ResultSet[] {Result.SUCCESS}));
		
		//using aria-labelledby
		this.tests.add(new Test("<p id=\"mydesc\">DescriptionRightHere</p>\n<p id=\"betterdesc\">There are many facets to a good accessible description</p>\n"
				+ "<img src=\"smiley.gif\" aria-labelledby=\"mydesc betterdesc\">", 
				new ResultSet[] {Result.SUCCESS}));
		
		//using aria-describedby
		this.tests.add(new Test("<img src=\"smiley.gif\" aria-describedby=\"Somethingexternal.com/my_image_label\">", 
				new ResultSet[] {Result.SUCCESS}));
		
		//using <area> and aria-label
		this.tests.add(new Test("<img src=\"planets.gif\" width=\"145\" height=\"126\" alt=\"Planets\"\r\n" + 
				"usemap=\"#planetmap\">\r\n" + 
				"\r\n" + 
				"<map name=\"planetmap\">\r\n" + 
				"  <area shape=\"rect\" coords=\"0,0,82,126\" href=\"sun.htm\" alt=\"Sun\">\r\n" + 
				"</map>", 
				new ResultSet[] {Result.SUCCESS}));
		
		//using longdesc
		this.tests.add(new Test("<img src=\"sculpture.png\" longdesc=\"https://en.wikipedia.org/wiki/Desiderio_da_Settignano\">", 
				new ResultSet[] {Result.SUCCESS}));
		
		//a decorative smiley gif
		this.tests.add(new Test("<img src=\"smiley.gif\" alt=\"\">" , 
				new ResultSet[] {Result.SUCCESS}));

		//a decorative <object> smiley gif
		this.tests.add(new Test("<object src=\"smiley.gif\" role=\"presentation\">", 
				new ResultSet[] {Result.SUCCESS}));

		//using role="img" with a suitable label with 'aria-label'
		this.tests.add(new Test("<div class=\"sprite card_icons visa\" role=\"img\" aria-label=\"Visa\"></div>", 
				new ResultSet[] {Result.SUCCESS}));

		
		
		//no description given
		this.tests.add(new Test("<img src=\"smiley.gif\" height=\"42\" width=\"42\">", 
				new ResultSet[] {Result.ERROR}));

		//<area> with no description given (despite img above having description)
		this.tests.add(new Test("<img src=\"planets.gif\" width=\"145\" height=\"126\" alt=\"Planets\"\r\n" + 
				"usemap=\"#planetmap\">\r\n" + 
				"\r\n" + 
				"<map name=\"planetmap\">\r\n" + 
				"  <area shape=\"rect\" coords=\"0,0,82,126\" href=\"sun.htm\">\r\n" + 
				"</map>", 
				new ResultSet[] {Result.ERROR}));

		 //an overly long alt text. doesn't currently fail as its only an ambiguous_serious.
		this.tests.add(new Test("<img src=\"smiley.gif\" alt=\"This alt text is way too long. This alt text is way too long. "
				+ "This alt text is way too long. This alt text is way too long. This alt text is way too long. This alt text is way too long. "
				+ "This alt text is way too long.This alt text is way too long.This alt text is way too long.This alt text is way too long."
				+ "This alt text is way too long.This alt text is way too long. This alt text is way too long. "
				+ "This alt text is way too long. This alt text is way too long\">",
				new ResultSet[] {Result.WARNING_SRS_LABEL_LENGTH}));
	}

}
