package code.checks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.openqa.selenium.WebElement;

import code.Marker;
import code.interfaces.SeleniumInterface;

public class HeadingsAndLabels extends Check {

	public HeadingsAndLabels() {
		super("Criterion 2.4.6 Headings and Labels");
	}

	@Override
	public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) {
		List<WebElement> allElements = inter.getAllElements();

		checkSiblingHeadingsUnique(inter);
		//Sibling headings must be unique
		//int startLevel = 1;
		//WebElement parent = null;
		//boolean uniqueSiblingHeaders = checkSiblingHeaderUniqueness(startLevel, parent, inter);
		

	}
	
	private boolean checkSiblingHeadingsUnique(int level, List<WebElement> allElements, SeleniumInterface inter) {
		//check that headings are unique,
		String headerLevel = "h" + String.valueOf(level); //eg level 1 = "h1"
		String nextHeaderLevel = "h" + String.valueOf(level+1);

		//can use getAllElements to find all elements - then find all h1,h2,h3,h4 etc by tag name.
		//then can use allElements.indexOf(myElement) to work out how all those elements were ordered...
		//surely there's a better way though... 
		//maybe not...
		
		WebElement[] headers = inter.getElementsByTagName(headerLevel);
		WebElement[] nextHeaders = inter.getElementsByTagName(nextHeaderLevel);
		
		System.out.println("Number of " + headerLevel + " elements found is " + String.valueOf(headers.length));
		
		
		
		
		
		ArrayList<String> headerLabels = new ArrayList<String>();
		
		
		
		for (int i = 0; i < headers.length; i++) {
			//check uniqueness of sub-headers of child:
			if (!checkSiblingHeaderUniqueness(level+1, headers[i], inter)) {
				return false;
			}
			//processing for uniqueness at this level
			System.out.println("Header: text: " + headers[i].getText());
			headerLabels.add(headers[i].getText());
		}
		HashSet<String> headerSet = new HashSet<String>(headerLabels);
		if (headerSet.size() == headerLabels.size()) {
			return true;
		}
		else {
			//need to modify this to place the warnings in the exact locations.
			return false;
		}
		
	}

	@Override
	public void initialise() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getHTMLPass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getHTMLFail() {
		// TODO Auto-generated method stub
		return null;
	}

}
