package code.checks;
import java.util.List;

import org.openqa.selenium.WebElement;

import code.interfaces.SeleniumInterface;

public class LabelsOrInstructions extends Check {
	//aria-describe-by still needs to be implemented
	
	//return error if no input ID as labels are linked via it ?
	
	//more user input types - make a list ?
	
	protected LabelsOrInstructions() {
		super("Criterion 3.3.2 Labels or Instructions");
	}
	
	//would be easier if function had return type boolean ?
	@Override
	public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) {
		WebElement[] forms = inter.getElementsByTagName("form");
		for (int i = 0; i < forms.length; i ++) {
			WebElement[] labels = inter.getElementsByTagName("label");
			WebElement[] inputs = inter.getSubElementsByTagName(forms[i], "input");
			for (int j = 0; j < inputs.length; j ++) {
				String inputId = inputs[j].getAttribute("inputId");
				if (inputId != null) {
					for(int v = 0; v < labels.length; v ++) {
						if (labels[v].getAttribute("labelId").equals(inputId)) {
							if(labels[v].getAttribute("description") != null) {
								addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, inputs[j]); 
							} else {
								addFlagToElement(markers, Marker.MARKER_ERROR, inputs[j]);
							}
						} 
					}
					addFlagToElement(markers, Marker.MARKER_ERROR, inputs[j]);
				} else {
					addFlagToElement(markers, Marker.MARKER_ERROR, inputs[j]);
				}
			}
		}
	}

	@Override
	public String[] getHTMLPass() {
		//aria-describedby / more options ?
		return new String[] {
				"<form><label for=\"labelId\">\"description\"</label><input id=\"inputId\"></form>"
		};
	}

	@Override
	public String[] getHTMLFail() {
		return new String[] {
				"<form><input></form>",
				"<form><label for=\"labelId\">\"missing-description\"</label><input id=\"inputId\"></form>"
		};
	}

	@Override
	public void initialise() {}

}