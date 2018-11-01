package code.checks;
import java.util.List;

import org.openqa.selenium.WebElement;

import code.interfaces.SeleniumInterface;

public class LabelsOrInstructions extends Check {
	private static final String ERR_ARIA_DESCRIBED_BY_MISSING = "The element specified in aria-describedby is missing";
	private static final String ERR_ARIA_DESCRIBED_BY_HIDDEN = "The element specified in aria-describedby is hidden";
	
	private static final String ERR_ARIA_LABELLED_BY_MISSING = "An element specified in aria-labelledby is missing";
	private static final String ERR_ARIA_LABELLED_BY_HIDDEN = "An element specified in aria-labelledby is hidden";
	private static final String ERR_ARIA_LABELLED_BY_EMPTY = "No elements specified in aria-labelledby";
	
	protected LabelsOrInstructions() {
		super("Criterion 3.3.2 Labels or Instructions");
	}

	@Override
	public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) {
		WebElement[] forms = inter.getElementsByTagName("form");
		WebElement[] labels = inter.getElementsByTagName("label");

		for (int i = 0; i < forms.length; i ++) {
			WebElement[] inputs = inter.getSubElementsByTagName(forms[i], "input");

			for (int j = 0; j < inputs.length; j ++) {
				String inputId = inputs[j].getAttribute("id");
				String ariaDescribedBy;
				String ariaLabelledBy;
				
				if ((ariaDescribedBy = inputs[j].getAttribute("aria-describedby")) != null) {
					WebElement ariaDescription;
					if ((ariaDescription = inter.getElementById(ariaDescribedBy)) != null) {
						if (ariaDescription.isDisplayed()) {
							addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[j]);
						} else {
							addFlagToElement(markers, Marker.MARKER_ERROR, ariaDescription, ERR_ARIA_DESCRIBED_BY_HIDDEN); //oh dear how silly! You went to all that trouble but the element isn't visible
						}
					} else {
						addFlagToElementAttribute(markers, Marker.MARKER_ERROR, inputs[j], "aria-describedby", ERR_ARIA_DESCRIBED_BY_MISSING); //You can't find your aria described by. What a shame!
					}
				} else if ((ariaLabelledBy = inputs[j].getAttribute("aria-labelledby")) != null) {
					String[] ariaLabels = ariaLabelledBy.split(" ");
					WebElement ariaLabel;
					boolean allExists = true;
					for (int k = 0; k < ariaLabels.length; k ++) {
						if ((ariaLabel = inter.getElementById(ariaLabels[k])) == null) {
							allExists = false;
							addFlagToElementAttribute(markers, Marker.MARKER_ERROR, inputs[j], "aria-labelledby", ERR_ARIA_LABELLED_BY_MISSING);
						} else if (!ariaLabel.isDisplayed()) {
							addFlagToElement(markers, Marker.MARKER_ERROR, ariaLabel, ERR_ARIA_LABELLED_BY_HIDDEN); //oh dear how silly! You went to all that trouble but the element isn't visible
						}
					}
					if (ariaLabels.length == 0) {
						addFlagToElementAttribute(markers, Marker.MARKER_ERROR, inputs[j], "aria-labelledby", ERR_ARIA_LABELLED_BY_EMPTY);
					}
					if (allExists) {
						addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[j]);
					}
				} else if (inputId != null) {
					boolean found_for = false;

					for(int v = 0; v < labels.length; v ++) {
						if (labels[v].getAttribute("for").equals(inputId)) {
							addFlagToElement(markers, Marker.MARKER_SUCCESS, inputs[j]); //yay, a label has been found for this input. It has been labelled, this is one way of succeeding
							found_for = true; //mark found_for so as to not add an ambiguous flag
						}
					}

					if (!found_for) {
						addFlagToElement(markers, Marker.MARKER_AMBIGUOUS, inputs[j]); //label was not found. This _might_ not be described but we have no way of telling
					}

				} else {
					addFlagToElement(markers, Marker.MARKER_AMBIGUOUS_SERIOUS, inputs[j]);
				}
			}
		}
	}

	@Override
	public String[] getHTMLPass() {
		return new String[] {
				"<form><label for=\"inputId\">description</label><input id=\"inputId\"></form>",
				"<input type=\"text\" id=\"no_label_needed\">",
				"<form><input id=\"inputId\" aria-describedby=\"description\"></form><div id=\"description\">A quick description</div>", //aria described by
				"<form><input id=\"inputId\" aria-labelledby=\"description other\"></form><div id=\"description\">A quick description</div><span id=\"other\">extended</span>" //aria labelled by
		};
	}

	@Override
	public String[] getHTMLFail() {
		return new String[] {
				"<form><input></form>",
				"<form><input id=\"inputId\" aria-describedby=\"description\"></form>", //aria described by missing
				"<form><input id=\"inputId\" aria-describedby=\"description\"></form><div style=\"display:none\" id=\"description\">A quick description</div>", //aria described by hidden
				"<form><input id=\"inputId\" aria-labelledby=\"description other\"></form><div id=\"description\">A quick description</div>", //aria labelled by missing
				"<form><input id=\"inputId\" aria-labelledby=\"description\"></form><div style=\"display:none\" id=\"description\">A quick description</div>", //aria labelled by hidden
				"<form><input id=\"inputId\" aria-labelledby=\"\"></form>" //aria labelled by empty
		};
	}

	@Override
	public void initialise() {}

}