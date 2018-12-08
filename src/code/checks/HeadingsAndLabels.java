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

public class HeadingsAndLabels extends Check {

	public HeadingsAndLabels() {
		super("Criterion 2.4.6 Headings and Labels");
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
		
		System.out.println("Found the following form control elements, with their labels");
		for (int i = 0; i < formControlLabels.size(); i++) {
			System.out.println(this.getWebElementString(formControlLabels.get(i).getEle()));
			System.out.println(formControlLabels.get(i));
		}
		
		
		//firstly: We know that we have to check that each label is a good description manually.
		for (int i = 0; i < formControlLabels.size(); i++) {
			addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, formControlLabels.get(i).getEle(),
					"Manual Check: does this label properly describes the form control element");
			
		}
		
		
		ArrayList<LabelledWebElement> duplicateLabels = new ArrayList<LabelledWebElement>();
		int formCtrlCnt = formControlLabels.size();
		//compare each pair (once) of formControlLabels - if equal, mark as failures.
		for (int i = 0 ; i != formCtrlCnt; i++) {
			for (int j = i; j != formCtrlCnt; j++) {
				if (formControlLabels.get(i).checkEqualityWith(formControlLabels.get(j))) {
					System.out.println("Found two matching label elements: " + "\n" + formControlLabels.get(i).toString() + "\n" + formControlLabels.get(j).toString());
					duplicateLabels.add(formControlLabels.get(i));
					duplicateLabels.add(formControlLabels.get(j));
				}
			}
		}
		
		for (int i = 0; i < duplicateLabels.size(); i++) {
			addFlagToElement(markers, Marker.MARKER_ERROR, duplicateLabels.get(i).getEle(),
					"Element label is not unique");
		}
		
		formControlLabels.removeAll(duplicateLabels);
		for (int i = 0; i < formControlLabels.size(); i++) {
			addFlagToElement(markers, Marker.MARKER_SUCCESS, formControlLabels.get(i).getEle(),
					"Element label is not duplicated on this page");
			
		}
		
	}
	
	private String getWebElementString(WebElement ele) {
		return "tag: " + ele.getTagName() + "text: " + ele.getText();
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
					eleLabel.addLabel(labelElement.getText());
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
		while (checkForParentLabels.findElement(By.xpath("..")) != null) {
			if (checkForParentLabels.getTagName() == "label") {
				this.getLabelEleLabels(markers, checkForParentLabels, eleLabel);
				break;
			}
		}
		
		//there are other ways the element could be labelled too:
		//'title', 'aria-describedby', Parent: <fieldset> with a <legend>; parent: role="group" and then aria-labelledby=...
		String describedby;
		if ((describedby = ele.getAttribute("aria-describedby")) != null) {
			String[] descIDs = describedby.split(" ");
			for (String descID: descIDs) {
				WebElement descElement = inter.getElementById(descID);
				if (descElement != null) {
					eleLabel.addLabel(descElement.getText());
				}
			}
		}

		String elementTitle;
		if ((elementTitle = ele.getAttribute("title"))!= null ) {
			if (eleLabel.getLabelsSize() == 0) {
				//raise an issue, as the primary label is a 'title' which is not always accessible.
				addFlagToElement(markers, Marker.MARKER_ERROR, ele, "Primary label for this element is a 'title' attribute, which is not always accessible to all users");
			}
			eleLabel.addLabel(elementTitle);
		}
		
		if (eleLabel.getLabelsSize() == 0) {
			System.out.println("Should have found an error on LabelsOrInstructions -- NO LABEL for this element: " + ele.getTagName() + "; ID: " + ele.getAttribute("id"));
		}
		
		WebElement checkForParentFieldset = ele;
		while (checkForParentFieldset.findElement(By.xpath("..")) != null) {
			if (checkForParentFieldset.getTagName() == "fieldset") {
				WebElement legend;
				if ((legend = checkForParentFieldset.findElement(By.xpath("legend"))) != null) {
					eleLabel.addLabel(legend.getText());
				}
				break;
			}
		}
		
		//you can nest groups in other groups.
		//in order to find the label for a group, we recursively call this function.
		WebElement checkForParentGroup = ele;
		while (checkForParentGroup.findElement(By.xpath("..")) != null) {
			WebElement parent = checkForParentGroup;
			if (parent.getAttribute("role") != null) {
				if (parent.getAttribute("role").equals("group")) {
					getElementLabel(markers, parent, eleLabel, labelEles, fieldsetEles, inter);
					break;
				}
			}
		}

		
		
		return eleLabel;
		
	}
	
	//a label element can have labels from getText() or from images inside the label element.
	private void getLabelEleLabels(List<Marker> markers, WebElement labelEle, LabelledWebElement eleLabel) {
		eleLabel.addLabel(labelEle.getText());
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
			if (optgroupLabel != null) {
				this.checkUniqueText(markers, options, "<option> elements in same <optgroup> have duplicate labels", 
					"", "<option> elements in this <optgroup> have distinct labels");
			}
			else {
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
		eleLabel.addLabel(buttonEle.getText());
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
			if (child.getTagName() == "img") {
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
		//System.out.println("Entering with level: " + String.valueOf(level));
		ArrayList<WebElement> siblingHeadings = new ArrayList<WebElement>();
		
		while (sharedIndex.getValue() < headingList.size()) {
	
			TreeNode<WebElement> node = headingList.get(sharedIndex.getValue());
			int headingLevel = node.getLevel();
			WebElement headingEle = node.getElement();
			String heading = headingEle.getText();
			
			/*System.out.println("Current level: " + String.valueOf(level)
				+ ", Found heading with level: " + String.valueOf(headingLevel)
				+ ", with text: " + heading 
				+ "-----String of length " + String.valueOf(heading.length()));*/
			
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
		ArrayList<WebElement> duplicates = new ArrayList<WebElement>();
		int duplicatesFound = 0;
		HashMap<String, WebElement> uniqueAttrs = new HashMap<String, WebElement>();
		for (int i = 0; i < uniqueAttrs.size(); i++) {
			WebElement ele_i = eleList.get(i);
			String attrValue = ele_i.getAttribute(attr);
			if (attrValue == null) {
				continue;
			}
			uniqueAttrs.put(attrValue, ele_i);
			if (uniqueAttrs.keySet().size() != (i+1)-duplicatesFound) {
				//the previously added string was a duplicate.
				duplicatesFound++;
				//so we must add both elements as duplicates.
				duplicates.add(ele_i);
				duplicates.add(uniqueAttrs.get(attrValue));
			}
		}
		
		//mark all duplicates
		if (duplicatesFound > 0) {
			Iterator<WebElement> failIt = duplicates.iterator();
			while (failIt.hasNext()) {
				WebElement dupl = failIt.next();
				addFlagToElement(markers, Marker.MARKER_ERROR, dupl, errorMsg);
				System.out.println("Added failure marker to element: " + dupl.getTagName() + ": " + dupl.getAttribute(attr));
			}
			eleList.removeAll(duplicates);
			Iterator<WebElement> succIt = eleList.iterator();
			while (succIt.hasNext()) {
				WebElement passEle = succIt.next();
				addFlagToElement(markers, Marker.MARKER_SUCCESS, passEle, successMsg);
			}
			return false;
		}
		return true;
	}
	
	//check that the list of WebElements all have unique 'getText' values.
	//markup errors on elements if they are not unique.
	private boolean checkUniqueText(List<Marker> markers, List<WebElement> eleList,
			String errorMsg, String warningMsg, String successMsg) {
		if (eleList == null) {
			return true;
		}
		ArrayList<WebElement> duplicateTexts = new ArrayList<WebElement>();
		int duplicatesFound = 0;
		HashMap<String, WebElement> uniqueStrings = new HashMap<String, WebElement>();
		for (int i = 0; i < eleList.size(); i++) {
			WebElement ele_i = eleList.get(i);
			uniqueStrings.put(ele_i.getText(), ele_i);
			if (uniqueStrings.keySet().size() != (i+1)-duplicatesFound) {
				//the previously added string was a duplicate.
				duplicatesFound++;
				
				//must mark both headings as duplicates.
				duplicateTexts.add(ele_i);
				duplicateTexts.add(uniqueStrings.get(ele_i.getText()));
			}
		}
		if (duplicatesFound > 0) {
			//mark all the elements as duplicates.
			Iterator<WebElement> failIt = duplicateTexts.iterator();
			while (failIt.hasNext()) {
				WebElement dupl = failIt.next();
				addFlagToElement(markers, Marker.MARKER_ERROR, dupl, errorMsg);
				System.out.println("Added failure marker to element: " + dupl.getText());
				//System.out.println(x);
			}
			eleList.removeAll(duplicateTexts);
			Iterator<WebElement> succIt = eleList.iterator();
			while (succIt.hasNext()) {
				WebElement passEle = succIt.next();
				addFlagToElement(markers, Marker.MARKER_SUCCESS, passEle, successMsg);
			}
			return false;
		}
		//else:
		return true;
	}
	

	@Override
	public void initialise() {
		
	}

	@Override
	public String[] getHTMLPass() {
		return new String[] {
			"<h1>My Pass Heading</h1>",
			"<h1>My Pass Heading</h1>\n<h2>My Pass Heading</h2>",
			"<h1>My Pass Heading</h1>\n<h2>My Pass Heading</h2>\n"
			+ "<h1>My-Diff Pass Heading</h1>\n<h2>My Pass Heading</h2>"
			
			
		};
	}

	@Override
	public String[] getHTMLFail() {
		return new String[] {
			"<h1>My Fail Heading</h1>\n<h1>My Fail Heading</h1>",
			"<h1>My Fail Heading</h1>\n<h2>My Pass Heading</h2>\n<h1>My Fail Heading</h1>",
			"<h1>My Pass Heading</h1>\n<h2>My Fail Heading</h2>\n<h2>My Fail Heading</h2>",
			"<h1>Cool</h1>\n<h2>NotVeryCool</h2>\n<h3>Bewildering</h3>\n<h3>Bewildering</h3>",
			"<h1>Cool</h1>\n<h1>Not cool</h1>\n<h1>Cool</h1>",
			"<h1>A</h1>\n<h2>B</h2>\n<h3>C</h3>\n<h1>A</h1>"
		};
	}

}
