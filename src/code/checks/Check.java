package code.checks;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;

import code.Marker;
import code.interfaces.SeleniumInterface;

public abstract class Check implements Comparable<Check> {
	private String name;

	protected Check(String name) {
		this.name = name;
		initialise();
	}

	//runs the check on the url content and the selenium interface to the page. Adds markers to the checklist marker page
	public boolean executeCheck(String urlContent, List<Marker> m, SeleniumInterface inter) {
		List<Marker> markers = new ArrayList<Marker>();
		runCheck(urlContent, markers, inter);
		boolean passed = true;
		for (int i = 0; i < markers.size(); i ++) {
			if (markers.get(i).getType() == Marker.MARKER_ERROR) {
				passed = false;
			}
			m.add(markers.get(i));
		}
		if(markers.size() == 0 && passed) {
			m.add(new Marker(Marker.MARKER_SUCCESS, this));
		}
		return passed;
	}

	//runs the check on the url content and the selenium interface to the page. Adds markers to the checklist marker page
	public boolean executeCheck(String urlContent, SeleniumInterface inter) {
		return executeCheck(urlContent, new ArrayList<Marker>(), inter);
	}

	public abstract void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter);

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

	public void addFlagToElement(List<Marker> markers, int type, WebElement ele, String desc) {
		markers.add(new Marker(desc, type, this, ele));
	}
	
	public void addFlagToElement(List<Marker> markers, int type, WebElement ele) {
		markers.add(new Marker(type, this, ele));
	}

	public void addFlagToElementAttribute(List<Marker> markers, int type, WebElement ele, String attr, String desc) {
		markers.add(new Marker(desc, type, this, ele, attr));
	}
	
	public void addFlagToElementAttribute(List<Marker> markers, int type, WebElement ele, String attr) {
		markers.add(new Marker(type, this, ele, attr));
	}

	public void addFlagToElementInnerPosition(List<Marker> markers, int type, WebElement ele, int position, String desc) {
		markers.add(new Marker(desc, type, this, ele, position));
	}	
	
	public void addFlagToElementInnerPosition(List<Marker> markers, int type, WebElement ele, int position) {
		markers.add(new Marker(type, this, ele, position));
	}

	public int compareTo(Check c) {
		return getName().compareTo(c.getName());
	}
}