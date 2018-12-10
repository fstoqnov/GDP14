package code.checks;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.lang3.mutable.MutableInt;
import org.openqa.selenium.WebElement;

import code.Marker;
import code.interfaces.SeleniumInterface;
import code.structures.Headings;
import code.structures.TreeNode;
import tests.Test;

public class InfoAndRelationships extends Check {

	public InfoAndRelationships() {
		super("Criterion 1.3.1 Info and Relationships");
	}
	
	private static String SUCC_HEADING_NEST() {return "Correctly nested heading";}
	private static String ERR_HEADING_NEST(int initialLevel, int newLevel) {return "Incorrectly nested heading - jumped from " + initialLevel + " to " + newLevel;}

	private static enum ResultType implements Result{
		ERROR,
		SUCCESS,
	}
	public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) {
		checkHeadingNesting(markers, inter);
	}
	
	private void checkHeadingNesting(List<Marker> markers, SeleniumInterface inter) {
		//check that headings are nested correctly
		TreeMap<Integer, TreeNode<WebElement>> headingTree = Headings.getHeadingTree(inter);
		ArrayList<TreeNode<WebElement>> headingList = new ArrayList<TreeNode<WebElement>>(headingTree.values());
		
		checkHeadingNestingAtLevel(0, headingList, new MutableInt(0), markers, inter);
	
	}
	
	//recursive function
	private void checkHeadingNestingAtLevel(int level, 
			ArrayList<TreeNode<WebElement>> headingList, MutableInt sharedIndex,
			List<Marker> markers, SeleniumInterface inter) {
		while (sharedIndex.getValue() < headingList.size()) {
			TreeNode<WebElement> node = headingList.get(sharedIndex.getValue());
			int headingLevel = node.getLevel();
			WebElement headingEle = node.getElement();
			
			if (headingLevel == level) {
				//this heading element is correctly nested
				addFlagToElement(markers, Marker.MARKER_SUCCESS, headingEle, SUCC_HEADING_NEST(), ResultType.SUCCESS);
				sharedIndex.increment();
			}
			else if (headingLevel > level) {
				if (headingLevel-level ==1) {
					//correctly nested new heading level
					addFlagToElement(markers, Marker.MARKER_SUCCESS, headingEle, SUCC_HEADING_NEST(), ResultType.SUCCESS);
					sharedIndex.increment();
					checkHeadingNestingAtLevel(headingLevel, headingList, sharedIndex, markers, inter);
				}
				else {
					//a jump of more than one in heading level is invalid.
					addFlagToElement(markers, Marker.MARKER_ERROR, headingEle, ERR_HEADING_NEST(level, headingLevel), ResultType.ERROR);
					sharedIndex.increment();
					checkHeadingNestingAtLevel(headingLevel, headingList, sharedIndex, markers, inter);
				}
			}
			else {
				//the heading has dropped back to a previous level.
				//will drop back to the previous loop without increasing the sharedIndex
				//so we can assess whether the heading level is correct from the previous heading level.
				//do nothing here and end the loop
				break;
			}
		}
	}

	@Override
	public void initialise() {
		
	}

	public void setupTests() {
		this.tests.add(new Test("<h1>My Pass Heading</h1>\n<h2>My Pass Heading</h2>\n<h3>My Pass Heading</h3>", new Result[] {ResultType.SUCCESS}));
		this.tests.add(new Test("<h1>My Pass Heading</h1>\n<h2>My Pass Heading</h2>\n<h3>My Pass Heading</h3>\n<h1>Another Pass Heading</h1>", new Result[] {ResultType.SUCCESS}));


		
		this.tests.add(new Test("<h1>My Pass Heading</h1>\n<h3>My Fail Heading</h3>", new Result[] {ResultType.ERROR, ResultType.SUCCESS}));
		this.tests.add(new Test("<h2>My Fail Heading</h2>", new Result[] {ResultType.ERROR}));

	}
}
