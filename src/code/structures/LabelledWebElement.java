package code.structures;

import java.util.ArrayList;
import java.util.Collections;

import org.openqa.selenium.WebElement;

public class LabelledWebElement {

	WebElement webEle;
	ArrayList<String> labels;
	
	public LabelledWebElement(WebElement we) {
		this.webEle = we;
	}
	
	public void addLabel(String s) {
		this.labels.add(s);
		Collections.sort(this.labels);
	}
	
	public WebElement getEle() {
		return this.webEle;
	}
	
	public int getLabelsSize() {
		return this.labels.size();
	}
	
	public boolean checkEqualityWith(LabelledWebElement eleLabel2) {
		if (labels.size() == eleLabel2.labels.size()) {
			for (int i = 0; i < labels.size(); i++) {
				if (!labels.get(i).equals(eleLabel2.labels.get(i))) {
					return false;
				}
			}
			return true; //if all elements came up with the same labels.
		}
		return false;
	}
	
	public String toString() {
		String labelsString = "";
		for (String s: labels) {
			labelsString.concat(s + ", ");
		}
		return labelsString;
	}
	
}
