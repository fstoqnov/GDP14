package code.checks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.lang3.mutable.MutableInt;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import code.Marker;
import code.interfaces.SeleniumInterface;
import code.structures.Headings;
import code.structures.LabelledWebElement;
import code.structures.TreeNode;
import nu.validator.messages.Result;
import tests.Test;

public class HeadingsAndLabels extends Check {

	public HeadingsAndLabels() {
		super("Criterion 2.4.6 Headings and Labels");
	}
	
	private static String WARNING_SRS_TITLE_ONLY() {return "Primary label for this element is a 'title' attribute, which is not always accessible to all users";}
	private static String WARNING_LABEL_GENERAL() {return "Manual Check: does this label properly describes the form control element";}
	private static String ERR_LABEL_DUPL() {return "Element label is not unique";}
	private static String SUCC_LABEL_DUPL() {return "Element label is not duplicated on this page";}
	
	private static enum Result implements ResultSet {
		ERROR,
		SUCCESS,
		WARNING_LABEL_GENERAL,
		WARNING_SRS_TITLE_ONLY
	}
	
	//There is also a requirement within 2.4.6 that Headings and Labels are fit-for-purpose, 
	//obviously we can't check this. So SUCCESS is probably not really possible to raise
	//we can raise a warning on all these elements --> Need to be checked manually.	

	@Override
	public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) {
		
		checkSiblingHeadingsUnique(markers, inter);
		checkLabelsUnique(markers, inter);
		

	}
	
	private void checkLabelsUnique(List<Marker> markers, SeleniumInterface inter) {
		//we can only find "<input>", "<select>", "<button>", and "<textarea>" form control elements.
		WebElement[] inputEles = inter.getElementsByTagName("input");
		WebElement[] selectEles = inter.getElementsByTagName("select");
		WebElement[] buttonEles = inter.getElementsByTagName("button");
		WebElement[] textareaEles = inter.getElementsByTagName("textarea");
		
		WebElement[] labelEles = inter.getElementsByTagName("label");
		WebElement[] fieldsetEles = inter.getElementsByTagName("fieldset");

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
			formControlLabels.add(this.getInputLabel(markers, inputEles[i], eleLabel, labelEles, fieldsetEles, inter));
		}
		for (int i = 0; i < selectEles.length; i++) {
			LabelledWebElement eleLabel = new LabelledWebElement(selectEles[i]);
			formControlLabels.add(this.getSelectLabel(markers, selectEles[i], eleLabel, labelEles, fieldsetEles, inter));
		}
		for (int i = 0; i < buttonEles.length; i++) {
			LabelledWebElement eleLabel = new LabelledWebElement(buttonEles[i]);
			formControlLabels.add(this.getButtonLabel(markers, buttonEles[i], eleLabel, labelEles, fieldsetEles, inter));
		}
		for (int i = 0; i < textareaEles.length; i++) {
			LabelledWebElement eleLabel = new LabelledWebElement(textareaEles[i]);
			formControlLabels.add(this.getElementLabel(markers, textareaEles[i], eleLabel, labelEles, fieldsetEles, inter));
		}
		
		/*
		Display all elements with the labels that were found for them
		 
		for (int i = 0; i < formControlLabels.size(); i++) {
			System.out.println(LabelledWebElement.getWebElementString(formControlLabels.get(i).getEle()));
			System.out.println(formControlLabels.get(i));
		}*/
		
		
		//firstly: We know that we have to check that each label is a good description manually.
		Iterator<LabelledWebElement> formControlIt = formControlLabels.iterator();
		while(formControlIt.hasNext()) {
			LabelledWebElement formControlLabel = formControlIt.next();
			WebElement formControlEle = formControlLabel.getEle();
				
			addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, formControlEle,
					WARNING_LABEL_GENERAL(), Result.WARNING_LABEL_GENERAL);
			if (formControlLabel.getLabelsSize() ==0) {
				formControlIt.remove(); //this should be handled elsewhere. Don't want to highlight duplicate lacking labels.
			}
		}
		
		
		ArrayList<LabelledWebElement> duplicateLabels = new ArrayList<LabelledWebElement>();
		int formCtrlCnt = formControlLabels.size();
		//compare each pair (once) of formControlLabels - if equal, mark as failures.
		for (int i = 0 ; i != formCtrlCnt; i++) {
			for (int j = i+1; j != formCtrlCnt; j++) {
				if (formControlLabels.get(i).checkEqualityWith(formControlLabels.get(j))) {
					//found two form control elements with duplicate labels
					duplicateLabels.add(formControlLabels.get(i));
					duplicateLabels.add(formControlLabels.get(j));
				}
			}
		}
		
		for (int i = 0; i < duplicateLabels.size(); i++) {
			addFlagToElement(markers, Marker.MARKER_ERROR, duplicateLabels.get(i).getEle(),
					ERR_LABEL_DUPL(), Result.ERROR);
		}
		
		formControlLabels.removeAll(duplicateLabels);
		for (int i = 0; i < formControlLabels.size(); i++) {
			addFlagToElement(markers, Marker.MARKER_SUCCESS, formControlLabels.get(i).getEle(),
					SUCC_LABEL_DUPL(), Result.SUCCESS);
			
		}
		
	}

	
	private LabelledWebElement getElementLabel(
			List<Marker> markers, WebElement ele, 
			LabelledWebElement eleLabel, WebElement[] labelEles, 
			WebElement[] fieldsetEles, SeleniumInterface inter) {
		//No special cases will be handled here.
		//We look for use of 'aria-labelledby',
		//or use of 'aria-label',
		//or use of a <label> tag - either referenced by id, or encapsulating this element
		//or use of 'title' in place of a label
		
		//then we call getSecondaryLabels to look for other valid additions to the labelling

		//primary label comes from 'aria-labelledby', or 'aria-label', or <label>, or 'title'
		String labelledby;
		String ariaLabelText;
		if ((labelledby = ele.getAttribute("aria-labelledby")) != null) {
			String[] labelIDs = labelledby.split(" ");
			for (String labelID: labelIDs) {
				WebElement labelElement = inter.getElementById(labelID);
				if (labelElement != null) {
					eleLabel.addLabel(labelElement.getAttribute("textContent"));

				}
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
				//this is the parent  <html> tag - no labels outside this.
				break;
			}
		}

		
		//there are other ways the element could be labelled too:
		String describedby;
		if ((describedby = ele.getAttribute("aria-describedby")) != null) {
			String[] descIDs = describedby.split(" ");
			for (String descID: descIDs) {
				WebElement descElement = inter.getElementById(descID);
				if (descElement != null) {
					eleLabel.addLabel(descElement.getAttribute("textContent"));

				}
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
				//addFlagToElement(markers, Marker.MARKER_AMBIGUOUS_SERIOUS, ele, WARNING_SRS_TITLE_ONLY(), Result.WARNING_SRS_TITLE_ONLY);
			}

		}
		
		WebElement checkForParentFieldset = ele;
		while ((checkForParentFieldset = checkForParentFieldset.findElement(By.xpath(".."))) != null) {

			if (checkForParentFieldset.getTagName().equals("fieldset")) {
				WebElement legend;
				if ((legend = checkForParentFieldset.findElement(By.xpath("legend"))) != null) {
					eleLabel.addLabel(legend.getAttribute("textContent"));
				}
				break;
			}
			else if (checkForParentFieldset.getTagName().equals("html")) {
				//this is the parent  <html> tag - no labels outside this.
				break;
			}
		}

		
		//you can nest groups in other groups.
		//in order to find the label for a group, we recursively call this function.
		WebElement checkForParentGroup = ele;
		while ((checkForParentGroup = checkForParentGroup.findElement(By.xpath(".."))) != null) {
			WebElement parent = checkForParentGroup;
			if (parent.getAttribute("role") != null) {
				if (parent.getAttribute("role").equals("group")) {
					getElementLabel(markers, parent, eleLabel, labelEles, fieldsetEles, inter);
					break;
				}
			}
			if (parent.getTagName().equals("html")) {
				//this is the parent  <html> tag - no labels outside this.
				break;
			}
		}


		
		
		return eleLabel;
		
	}
	
	//a label element can have labels from textContent, or from images inside the label element.
	private void getLabelEleLabels(List<Marker> markers, WebElement labelEle, LabelledWebElement eleLabel) {
		eleLabel.addLabel(labelEle.getAttribute("textContent"));
		this.addContainedImageLabels(labelEle, eleLabel);
	}
	
	private LabelledWebElement getInputLabel(
			List<Marker> markers, WebElement inputEle, 
			LabelledWebElement eleLabel, WebElement[] labelEles, 
			WebElement[] fieldsetEles, SeleniumInterface inter) {

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
		return this.getElementLabel(markers, inputEle, eleLabel, labelEles, fieldsetEles, inter);
	}
	
	private LabelledWebElement getSelectLabel(
			List<Marker> markers, WebElement selectEle, 
			LabelledWebElement eleLabel, WebElement[] labelEles, 
			WebElement[] fieldsetEles, SeleniumInterface inter) {
		//Firstly: Check for internal uniquenesss of <options> within this <select> block:
		//Check that there aren't duplicate optgroup names, or
		//duplicate option names (which aren't otherwise separated by their optgroups).
		
		
		List<WebElement> optgroups = selectEle.findElements(By.xpath("optgroup"));
		List<WebElement> baseOptions = selectEle.findElements(By.xpath("option"));
		//check that optgroup labels are unique:
		this.checkUniqueAttr(markers, optgroups, "label",
				"<optgroup> elements within the same <select> field must have different labels",
				"", "<optgroup> has distinct label within <select> field");
		for (WebElement optgroup: optgroups) {
			List<WebElement> options = optgroup.findElements(By.xpath("option"));
			String optgroupLabel = optgroup.getAttribute("label");
			boolean nullOptgroupLabel = false;
			if (optgroupLabel != null) {
				if (optgroupLabel.equals("")) {
					nullOptgroupLabel = true;
				}
				this.checkUniqueText(markers, options, "<option> elements in same <optgroup> have duplicate labels", 
					"", "<option> elements in this <optgroup> have distinct labels");
			}
			else {
				nullOptgroupLabel = true;
			}
			if (nullOptgroupLabel) {
				baseOptions.addAll(options);
			}
		}
		//check the base options (those without <optgroup> labels) for uniqueness.
		this.checkUniqueText(markers, baseOptions, 
				"<option> elements within <select> field must have distinct labels", "", 
				"<option> element within <select> field has distinct label");
		
		
		return this.getElementLabel(markers, selectEle, eleLabel, labelEles, fieldsetEles, inter);
	}
	
	
	private LabelledWebElement getButtonLabel(
			List<Marker> markers, WebElement buttonEle, 
			LabelledWebElement eleLabel, WebElement[] labelEles, 
			WebElement[] fieldsetEles, SeleniumInterface inter) {
		eleLabel.addLabel(buttonEle.getAttribute("textContent"));
		//if a button contains an <img> element, an alt tag for that image becomes part of the effective label.

		//
		this.addContainedImageLabels(buttonEle, eleLabel);

		
		
		return this.getElementLabel(markers, buttonEle, eleLabel, labelEles, fieldsetEles, inter);
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

	//Ensure that text in sibling headings is not duplicated.
	private void checkSiblingHeadingsUnique(List<Marker> markers, SeleniumInterface inter) {
		//check that sibling headings are unique,
		TreeMap<Integer, TreeNode<WebElement>> headingTree = Headings.getHeadingTree(inter);
		ArrayList<TreeNode<WebElement>> headingList = new ArrayList<TreeNode<WebElement>>(headingTree.values());
		//Iterator<Entry<Integer, TreeNode<WebElement>>> it = (Iterator<Entry<Integer, TreeNode<WebElement>>>) headingTree.entrySet().iterator();
		
		this.checkLevelSiblingHeadingsUnique(markers, 0, new MutableInt(0), headingList, inter);
	}
	
	//Recursive function.
	//ensure that text in sibling headings at the current level (specified) are unique.
	//if the next heading found is at a deeper level - re-call this function at that new level
	//if the next heading found is at a shallower level:
	//ensure that sibling headings found are unique, then return.
	//all instances of this function share the same list, indexed by the mutable sharedIndex
	private void checkLevelSiblingHeadingsUnique(
			List<Marker> markers,
			int level, 
			MutableInt sharedIndex,
			ArrayList<TreeNode<WebElement>> headingList,
			SeleniumInterface inter
	) {
		ArrayList<WebElement> siblingHeadings = new ArrayList<WebElement>();
		
		while (sharedIndex.getValue() < headingList.size()) {
	
			TreeNode<WebElement> node = headingList.get(sharedIndex.getValue());
			int headingLevel = node.getLevel();
			WebElement headingEle = node.getElement();
			String heading = headingEle.getAttribute("textContent");
			
			if (headingLevel == level) {
				//this is part of the current group of siblings
				siblingHeadings.add(headingEle);
				//move the index on:
				sharedIndex.increment();
			}
			else if (headingLevel > level) {
				//then this is the start of a nested set of headings:
				//sharedIndex will be the same, so it will be added to the list at this index.
				checkLevelSiblingHeadingsUnique(markers, headingLevel, sharedIndex, headingList, inter);
				//recursively call this function at the new higher heading level.
			}
			else {
				//the headingLevel is lower than the current level: End of this sibling block.
				//check for duplicate Strings.
				break; //will drop this function, and resume in the previous recursive layer.
				
			}
		}

		//check for duplicate Strings.
		String failMsg = "Sibling heading elements must be unique";
		String passMsg = "Not a duplicate of other sibling headings";
		String warningMsg = "";
		this.checkUniqueText(markers, siblingHeadings, failMsg, warningMsg, passMsg);
		
	}
	
	//check that a list of WebElements, each with an attribute: attr, have unique values for that attr.
	private boolean checkUniqueAttr(List<Marker> markers, List<WebElement> eleList, String attr,
			String errorMsg, String warningMsg, String successMsg) {
		if (eleList == null) {
			return true;
		}
		HashSet<WebElement> duplicates = new HashSet<WebElement>();
		int duplicatesFound = 0;
		HashMap<String, WebElement> uniqueAttrs = new HashMap<String, WebElement>();
		for (int i = 0; i < uniqueAttrs.size(); i++) {
			WebElement ele_i = eleList.get(i);
			String attrValue = ele_i.getAttribute(attr);
			if (attrValue == null) {
				continue;
			}
			if (uniqueAttrs.get(attrValue) == null) {
				uniqueAttrs.put(attrValue, ele_i);
			}
			else {
				//this is a duplicate.
				duplicatesFound++;
				//we must mark both elements as duplicates.
				duplicates.add(ele_i);
				duplicates.add(uniqueAttrs.get(attrValue));
			}
		}

		if (duplicatesFound > 0) {
			Iterator<WebElement> failIt = duplicates.iterator();
			while (failIt.hasNext()) {
				WebElement dupl = failIt.next();
				addFlagToElement(markers, Marker.MARKER_ERROR, dupl, errorMsg, Result.ERROR);
			}
			eleList.removeAll(duplicates);
			Iterator<WebElement> succIt = eleList.iterator();
			while (succIt.hasNext()) {
				WebElement passEle = succIt.next();
				addFlagToElement(markers, Marker.MARKER_SUCCESS, passEle, successMsg, Result.SUCCESS);
			}
			return false;
		}
		return true;
	}
	
	//check that the list of WebElements all have unique 'textContent' values.
	//markup errors on elements if they are not unique.
	private boolean checkUniqueText(List<Marker> markers, List<WebElement> eleList,
			String errorMsg, String warningMsg, String successMsg) {
		if (eleList == null) {
			return true;
		}
		HashSet<WebElement> duplicateTexts = new HashSet<WebElement>();
		int duplicatesFound = 0;
		HashMap<String, WebElement> uniqueStrings = new HashMap<String, WebElement>();
		for (int i = 0; i < eleList.size(); i++) {
			WebElement ele_i = eleList.get(i);
			String i_textContent = ele_i.getAttribute("textContent");
			
			if (uniqueStrings.get(i_textContent) == null) {
				uniqueStrings.put(ele_i.getAttribute("textContent"), ele_i);
			}
			else {
				//element i's textContent is a duplicate.
				duplicatesFound++;
				//must mark both headings as duplicates
				duplicateTexts.add(ele_i);
				duplicateTexts.add(uniqueStrings.get(i_textContent));
			}
		}

		//mark all duplicates
		if (duplicatesFound > 0) {
			//mark all the elements as duplicates.
			Iterator<WebElement> failIt = duplicateTexts.iterator();
			while (failIt.hasNext()) {
				WebElement dupl = failIt.next();
				addFlagToElement(markers, Marker.MARKER_ERROR, dupl, errorMsg, Result.ERROR);

			}
			eleList.removeAll(duplicateTexts);

			Iterator<WebElement> succIt = eleList.iterator();
			while (succIt.hasNext()) {
				WebElement passEle = succIt.next();
				addFlagToElement(markers, Marker.MARKER_SUCCESS, passEle, successMsg, Result.SUCCESS);

			}
			return false;
		}
		//else:
		Iterator<WebElement> successIt = eleList.iterator();
		while (successIt.hasNext()) {
			WebElement passEle = successIt.next();
			addFlagToElement(markers, Marker.MARKER_SUCCESS, passEle, successMsg, Result.SUCCESS);
		}
		return true;
	}
	

	@Override
	public void initialise() {
		
	}

	
	public void setupTests() {
		this.tests.add(new Test("<h1>My Pass Heading</h1>", 
				new ResultSet[] {Result.SUCCESS}));
		this.tests.add(new Test("<h1>My Pass Heading</h1>\n<h2>My Pass Heading</h2>", 
				new ResultSet[] {Result.SUCCESS}));
		
		this.tests.add(new Test("<h1>My Pass Heading</h1>\n<h2>My Pass Heading</h2>\n"
				+ "<h1>My-Diff Pass Heading</h1>\n<h2>My Pass Heading</h2>", 
				new ResultSet[] {Result.SUCCESS}));
		
		//using one label:
		this.tests.add(new Test("<label for=\"request_subject\">Subject</label>\n"
				+ "<input type=\"text\"  id=\"request_subject\">", 
				new ResultSet[] {Result.SUCCESS, Result.WARNING_LABEL_GENERAL}));
		
		//matching labels, but one is inside a fieldset with a <legend>
		this.tests.add(new Test("<label for=\"size\">Red</label>\n"
				+ "<input type=\"text\"  id=\"size\">\n"
				+ "<fieldset>\n <legend>Choose colour of car</legend>\n"
				+ "<label for=\"b\">Blue</label>\n"
				+ "<label for=\"g\">Green</label>\n"
				+ "<label for=\"r\">Red</label>\n"
				+ "<input id =\"b\" type=\"checkbox\"Blue>\n"
				+ "<input id=\"g\" type=\"checkbox\"Green>\n"
				+ "<input id=\"r\" type=\"checkbox\"Red>\n"
				+ "</fieldset>", 
				new ResultSet[] {Result.SUCCESS, Result.WARNING_LABEL_GENERAL}));

		//select with no duplicates
		this.tests.add(new Test("<select>\n"
				+ "<option>A</option>\n"
				+ "<option>B</option<\n>"
				+ "<option>C</option>\n"
				+ "</select>",
				new ResultSet[] {Result.SUCCESS, Result.WARNING_LABEL_GENERAL}));
		
		//select with duplicates, but optgroups make them unique.
		this.tests.add(new Test("<select>\n"
				+ "<optgroup label=\"German\">"
				+ "<option>A</option>\n"
				+ "<option>B</option>\n"
				+ "</optgroup>"
				+ "<optgroup label=\"Spanish\">"
				+ "<option>A</option>\n"
				+ "<option>B</option>\n"
				+ "</optgroup>\n"
				+ "</select>", 
				new ResultSet[] {Result.SUCCESS, Result.WARNING_LABEL_GENERAL}));

		this.tests.add(new Test("<h1>My Fail Heading</h1>\n<h1>My Fail Heading</h1>", 
				new ResultSet[] {Result.ERROR}));
		
		this.tests.add(new Test("<h1>My Fail Heading</h1>\n<h1>My Fail Heading</h1>\n<h1>My Fail Heading</h1>\n<h1>My Fail Heading</h1>", 
				new ResultSet[] {Result.ERROR}));
		
		this.tests.add(new Test("<h1>My Fail Heading</h1>\n<h2>My Pass Heading</h2>\n<h1>My Fail Heading</h1>", 
				new ResultSet[] {Result.ERROR, Result.SUCCESS}));
		
		this.tests.add(new Test("<h1>My Pass Heading</h1>\n<h2>My Fail Heading</h2>\n<h2>My Fail Heading</h2>", 
				new ResultSet[] {Result.ERROR, Result.SUCCESS}));
		
		this.tests.add(new Test("<h1>Cool</h1>\n<h2>NotVeryCool</h2>\n<h3>Bewildering</h3>\n<h3>Bewildering</h3>", 
				new ResultSet[] {Result.ERROR, Result.SUCCESS}));
		this.tests.add(new Test("<h1>Cool</h1>\n<h1>Not cool</h1>\n<h1>Cool</h1>", 
				new ResultSet[] {Result.ERROR, Result.SUCCESS}));
		
		this.tests.add(new Test("<h1>A</h1>\n<h2>B</h2>\n<h3>C</h3>\n<h1>A</h1>", 
				new ResultSet[] {Result.ERROR, Result.SUCCESS}));
		
		//duplicate labels:
		this.tests.add(new Test(" <label for=\"request_subject\">Duplicate Label</label>\r\n"
				+ "<input type=\"text\" id=\"request_subject\">\n"
				+ "<label for=\"request_duplicate\">Duplicate Label</label>\r\n"
				+ "<input type=\"text\" id=\"request_duplicate\">", 
				new ResultSet[] {Result.ERROR, Result.WARNING_LABEL_GENERAL}));
		
		//duplicate labels from different origins
		this.tests.add(new Test(" <label for=\"request_subject\">Duplicate Label</label>\r\n"
				+ "<input type=\"text\" id=\"request_subject\">\n"
				+ "<input type=\"text\" id=\"other\" aria-label=\"Duplicate Label\">", 
				new ResultSet[] {Result.ERROR, Result.WARNING_LABEL_GENERAL}));
		
		//duplicate two labels
		this.tests.add(new Test("<p id =\"long_desc1\">\"There are many options to input into this form\"</p>\n"
				+ "<p id=\"long_desc2\">\"There are many options to input into this form\"</p>\n"
				+ "<input id=\"a\" type=\"button\" aria-label=\"Default\" aria-describedby=\"long_desc1\">\n"
				+ "<input id=\"b\" type=\"button\" aria-label=\"Default\" aria-labelledby=\"long_desc2\">", 
				new ResultSet[] {Result.ERROR, Result.WARNING_LABEL_GENERAL}));
		
		//select with duplicate options (optgroup with no labels doesn't help)
		this.tests.add(new Test("<select>\n"
				+ "<option>A</option>"
				+ "<option>B</option>"
				+ "<optgroup>"
				+ "<option>A</option>"
				+ "</select>", 
				new ResultSet[] {Result.ERROR, Result.WARNING_LABEL_GENERAL, Result.SUCCESS}));


	}
	


}
