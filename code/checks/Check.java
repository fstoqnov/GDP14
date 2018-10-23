package code.checks;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;

import code.selenium_interface.Interface;

public abstract class Check {
	private String name;

	protected Check(String name) {
		this.name = name;
		initialise();
	}

	//runs the check on the url content and the selenium interface to the page. Adds markers to the checklist marker page
	public boolean runCheck(String urlContent, CheckList cl, Interface inter) {
		List<Marker> markers = new ArrayList<Marker>();
		runCheck(urlContent, markers, inter);
		boolean passed = true;
		for (int i = 0; i < markers.size(); i ++) {
			if (markers.get(i).getType() != Marker.MARKER_SUCCESS) {
				passed = false;
			}
			cl.addMarker(markers.get(i));
		}
		return passed;
	}

	public abstract void runCheck(String urlContent, List<Marker> markers, Interface inter);

	public String getName() { return name; }
	
	public void outputPassed() {
		System.out.println("Passed test '" + getName() + "'");
	}
	
	public void outputFailed() {
		System.out.println("Failed test '" + getName() + "'");
	}

	//Called to set up any variables that might be used for multiple same site urls
	public abstract void initialise();
	
	public abstract String[] getHTMLPass();

	public abstract String[] getHTMLFail();
	
	public static void addFlagToElement(List<Marker> markers, int type, Check check, WebElement ele) {
		markers.add(new Marker(type, check, ele));
	}

	public static void addFlagToElementAttribute(List<Marker> markers, int type, Check check, WebElement ele, String attr) {
		markers.add(new Marker(type, check, ele, attr));
	}

	public static void addFlagToElementInnerPosition(List<Marker> markers, int type, Check check, WebElement ele, int position) {
		markers.add(new Marker(type, check, ele, position));
	}
}