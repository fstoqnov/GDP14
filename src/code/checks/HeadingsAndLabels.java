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

import org.openqa.selenium.WebElement;

import code.Marker;
import code.interfaces.SeleniumInterface;
import code.structures.Headings;
import code.structures.TreeNode;

public class HeadingsAndLabels extends Check {

	public HeadingsAndLabels() {
		super("Criterion 2.4.6 Headings and Labels");
	}

	@Override
	public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) {

		checkSiblingHeadingsUnique(markers, inter);
		checkLabelsUnique(markers, inter);
		checkSubmitResetUnique(markers, inter);
		

	}
	
	private void checkLabelsUnique(List<Marker> markers, SeleniumInterface inter) {
		WebElement[] inputEles = inter.getElementsByTagName("input");
		WebElement[] labelEles = inter.getElementsByTagName("label");
		
		
		ArrayList<String> labelTexts = new ArrayList<String>();
		for (int i = 0; i < labelEles.length; i++) {
			labelTexts.add(labelEles[i].getText());
		}
		
		System.out.println("All labels: ");
		for (int i = 0; i < labelTexts.size(); i++) {
			System.out.println(labelTexts.get(i));
		}
		/*
		ArrayList<String> labelledElementIDs = new ArrayList<String>();
		for (int i = 0; i < labelEles.length; i++) {
			labelledElementIDs.add(labelEles[i].getAttribute("for"));
		}
		System.out.println("All labelled elements: ");
		for (int i = 0; i < labelledElementIDs.size(); i++) {
			System.out.println(labelledElementIDs.get(i));
		}
		ArrayList<String> inputIDs = new ArrayList<String>();
		for (int i = 0; i < inputEles.length; i++) {
			inputIDs.add(inputEles[i].getAttribute("id"));
		}
		System.out.println("All ID'd inputs: ");
		for (int i = 0; i < inputIDs.size(); i++) {
			System.out.println(inputIDs.get(i));
		}*/
		System.err.println("TODO: Finish implementing checkLabelsUnique");

	}
	
	//if there is more than one form on a page, ensure the labels for the submit/reset 
	//buttons of those forms are unique 
	//[so, it is at least possible that each submit/reset has a good description]
	private void checkSubmitResetUnique(List<Marker> markers, SeleniumInterface inter) {
		System.err.println("TODO: Implement checkSubmitResetUnique");
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
		this.checkUniqueText(markers, siblingHeadings);
		
	}
	
	
	//check that the list of WebElements all have unique 'getText' values.
	//markup errors on elements if they are not unique.
	private boolean checkUniqueText(List<Marker> markers, ArrayList<WebElement> eleList) {
		if (eleList == null) {
			return true;
		}
		ArrayList<WebElement> duplicateHeadings = new ArrayList<WebElement>();
		int duplicatesFound = 0;
		HashMap<String, WebElement> uniqueStrings = new HashMap<String, WebElement>();
		for (int i = 0; i < eleList.size(); i++) {
			WebElement ele_i = eleList.get(i);
			uniqueStrings.put(ele_i.getText(), ele_i);
			if (uniqueStrings.keySet().size() != (i+1)-duplicatesFound) {
				//the previously added string was a duplicate.
				duplicatesFound++;
				
				//must mark both headings as duplicates.
				duplicateHeadings.add(ele_i);
				duplicateHeadings.add(uniqueStrings.get(ele_i.getText()));
			}
		}
		if (duplicatesFound > 0) {
			//mark all the elements as duplicates.
			Iterator<WebElement> failIt = duplicateHeadings.iterator();
			while (failIt.hasNext()) {
				WebElement dupl = failIt.next();
				addFlagToElement(markers, Marker.MARKER_ERROR, dupl, "Sibling headings must not be duplicates");
				System.out.println("Added failure marker to element: " + dupl.getText());
				//System.out.println(x);
			}
			eleList.removeAll(duplicateHeadings);
			Iterator<WebElement> succIt = eleList.iterator();
			while (succIt.hasNext()) {
				WebElement passEle = succIt.next();
				addFlagToElement(markers, Marker.MARKER_SUCCESS, passEle, "Not a duplicate with sibling headings");
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
