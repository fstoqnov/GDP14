package code.structures;

import java.util.List;
import java.util.TreeMap;

import org.openqa.selenium.WebElement;

import code.interfaces.SeleniumInterface;

public class Headings {
	//get an Ordered Map (by element ID on page) of all Heading elements (all h1, h2, h3...)
	//this can be browsed as if it were a tree.
	public static TreeMap<Integer, TreeNode<WebElement>> getHeadingTree(SeleniumInterface inter) {
		List<WebElement> allElements = inter.getAllElements();
		
		TreeMap<Integer, TreeNode<WebElement>> headingsTree = new TreeMap<Integer, TreeNode<WebElement>>();
		//TreeNode<WebElement> root = new TreeNode<WebElement>(null, null);
		for (int level = 1; level <= 6; level++) { //h1 to h6
			String headingLevel = "h" + String.valueOf(level); //eg level1 --> "h1"
			WebElement[] headings = inter.getElementsByTagName(headingLevel);
			//System.out.println("Found " + String.valueOf(headings.length) + " headings at level " + headingLevel);
			for (int i = 0; i < headings.length; i++) {
				int eleIndex = allElements.indexOf(headings[i]);
				//TreeNode headingNode = new TreeNode(eleIndex, headings[i]);
				TreeNode headingNode = new TreeNode(level, headings[i]);
				headingsTree.put(eleIndex, headingNode);
			}
		}
		return headingsTree;
		
	}
}
