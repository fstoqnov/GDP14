package code.checks;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import code.Marker;
import code.interfaces.SeleniumInterface;
import tests.Test;

public class ContrastMinimum extends Check {

	public ContrastMinimum() {
		super("Criterion 1.4.3 Contrast(Minimum)");
	}
	
	private static String SUCCESS_CONTRAST() { return "Text contrast ratio adequate for font size and weight";}
	private static String ERR_INADEQUATE_CONTRAST(String contrastString, double requiredRatio) { return "Contrast ratio inadequate for text - Contrast found was (" + contrastString + "). Should be " + requiredRatio; }

	private static enum Result implements ResultSet {
		ERROR,
		SUCCESS,
	}
	
	@Override
	public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) {
		List<WebElement> eles = inter.getAllElements();
		String content;
		boolean containsText;
		for (int i = 0; i < eles.size(); i ++) {
			if (!eles.get(i).isDisplayed()) { continue; }
			content = eles.get(i).getText();
			containsText = inter.containsText(eles.get(i));
			if (containsText && content != null && content.trim().length() != 0) {
				double contrast = calculateContrastRatio(eles.get(i), inter);
				String contrastString = getFormattedContrastRatio(contrast);
				double size = Double.parseDouble(inter.getComputedStyleElement(eles.get(i), "fontSize").split("px")[0]);
				boolean bold = Double.parseDouble(inter.getComputedStyleElement(eles.get(i), "fontWeight")) >= 700 ? true : false;
				double requiredRatio;
				if ((bold && size >= 14) || (size >= 18)) {
					requiredRatio = 3;
				} else {
					requiredRatio = 4.5D;
				}
				if (contrast >= requiredRatio) {
					addFlagToElement(markers, Marker.MARKER_SUCCESS, eles.get(i), SUCCESS_CONTRAST(), Result.SUCCESS);
				} else {
					addFlagToElement(markers, Marker.MARKER_ERROR, eles.get(i), ERR_INADEQUATE_CONTRAST(contrastString, requiredRatio), Result.ERROR);
				}
			}
		}
	}

	private String getFormattedContrastRatio(double contrast) {
		NumberFormat formatter = new DecimalFormat("#0.00");     
		return formatter.format(contrast);
	}

	@Override
	public void initialise() {
		// TODO Auto-generated method stub

	}
	
	public void setupTests() {
		this.tests.add(new Test("<div style=\"background:black; color:white\">Test text</div>", new ResultSet[] {Result.SUCCESS}));
		this.tests.add(new Test("<div style=\"background:black\"><div style=\"color:white\">Test text</div></div>", new ResultSet[] {Result.SUCCESS}));
		
		//should ignore elements that aren't displayed
		this.tests.add(new Test("<div style=\"background:black\"><div style=\"color:black; display:none\">Test text</div></div>", new ResultSet[] {}));
		
		//If there is no text, no contrast failure.
		this.tests.add(new Test("<div style=\"background:black\"><div style=\"color:black\"></div></div>", new ResultSet[] {}));
		
		this.tests.add(new Test("<div>Test text</div>", new ResultSet[] {Result.SUCCESS}));

		
		this.tests.add(new Test("<div style=\"background:red; color:orange\">Test text</div>", new ResultSet[] {Result.ERROR}));
		this.tests.add(new Test("<div style=\"background:red\"><div style=\"color:orange\">Test text</div></div>", new ResultSet[] {Result.ERROR}));

	}

	public double calculateContrastRatio(WebElement element, SeleniumInterface inter) {
		String foreground = inter.getComputedStyleElement(element, "color");
		String background = inter.getParentalComputedStyle(element, "backgroundColor", "rgba(0, 0, 0, 0)");

		if (background.equals("rgba(0, 0, 0, 0)")) {
			return 21;
		}
		
		String startF = foreground.startsWith("rgba") ? "rgba" : "rgb";
		String startB = background.startsWith("rgba") ? "rgba" : "rgb";
		if (foreground == null || background == null) {
			return 21;
		}
		double fRed = Double.parseDouble(foreground.toLowerCase().split(startF + "\\(")[1].split("\\)")[0].split("\\,")[0].trim()) / 255D;
		double fGreen = Double.parseDouble(foreground.toLowerCase().split(startF + "\\(")[1].split("\\)")[0].split("\\,")[1].trim()) / 255D;
		double fBlue = Double.parseDouble(foreground.toLowerCase().split(startF + "\\(")[1].split("\\)")[0].split("\\,")[2].trim()) / 255D;

		double bRed = Double.parseDouble(background.toLowerCase().split(startB + "\\(")[1].split("\\)")[0].split("\\,")[0].trim()) / 255D;
		double bGreen = Double.parseDouble(background.toLowerCase().split(startB + "\\(")[1].split("\\)")[0].split("\\,")[1].trim()) / 255D;
		double bBlue = Double.parseDouble(background.toLowerCase().split(startB + "\\(")[1].split("\\)")[0].split("\\,")[2].trim()) / 255D;

		double fLum = calculateLuminosity(fRed, fGreen, fBlue);
		double bLum = calculateLuminosity(bRed, bGreen, bBlue);

		double lumMax = Math.max(fLum, bLum);
		double lumMin = Math.min(fLum, bLum);

		return (lumMax + 0.05D) / (lumMin + 0.05D);
	}

	public double calculateLuminosity(double R, double G, double B) {
		if (R <= 0.03928) {
			R = R / 12.92D;
		} else {
			R = Math.pow(((R+0.055)/1.055), 2.4);
		}
		if (G <= 0.03928) {
			G = G / 12.92D;
		} else {
			G = Math.pow(((G+0.055)/1.055), 2.4);
		}
		if (B <= 0.03928) {
			B = B / 12.92D;
		} else {
			B = Math.pow(((B+0.055)/1.055), 2.4);
		}
		return 0.2126 * R + 0.7152 * G + 0.0722 * B;
	}

}