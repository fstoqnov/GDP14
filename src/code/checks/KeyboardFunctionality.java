package code.checks;

import java.util.List;

import org.openqa.selenium.WebElement;

import code.Marker;
import code.interfaces.SeleniumInterface;
import tests.Test;

public class KeyboardFunctionality extends Check {
	public KeyboardFunctionality() {
		super("Criterion 2.1.1 Keyboard");
	}
	
	private static String ERR_POS_TAB() { return "Must not use tabindex greater than 0";}
	private static String SUCC_TAB() { return "Tabindex used not greater than 0";}
	private static String ERR_SERVER_MAP() {return "Must not use server side image maps ('ismap' attribute)";}

	private static enum ResultType implements Result {
		ERROR,
		SUCCESS
	}
	@Override
	public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) {
		//Initially, we check that all elements with tabindex attributes have values <=0
		WebElement[] tabIndexEle = inter.getElementsWithAttributeAnyValue("tabindex");
		for (int i = 0; i < tabIndexEle.length; i ++) {
			String tabIndexVal = tabIndexEle[i].getAttribute("tabindex");
			if (Integer.valueOf(tabIndexVal) > 0) {
				addFlagToElement(markers, Marker.MARKER_ERROR, tabIndexEle[i], ERR_POS_TAB(), ResultType.ERROR);
			}
			else {
				addFlagToElement(markers, Marker.MARKER_SUCCESS, tabIndexEle[i], SUCC_TAB(), ResultType.SUCCESS);

			}
		}
		
		//We ensure that server-side image maps are not used.
		WebElement[] isMapEle = inter.getElementsWithAttributeAnyValue("ismap");
		for (int i = 0; i < isMapEle.length; i++) {
			addFlagToElement(markers, Marker.MARKER_ERROR, isMapEle[i], ERR_SERVER_MAP(), ResultType.ERROR);
		}
		
	}

	@Override
	public void initialise() {
		//nothing to do
	}
	
	public void setupTests() {
		this.tests.add(new Test("<input type=\"button\" tabindex=\"0\" aria-label=\"Close\">", 
				new Result[] {ResultType.SUCCESS}));
		
		this.tests.add(new Test("<input type=\"button\" tabindex=\"-1\" aria-label=\"Skip Link\">", 
				new Result[] {ResultType.SUCCESS}));
		
		this.tests.add(new Test("<input type=\"button\" tabindex=\"1\" aria-label=\"Open\">", 
				new Result[] {ResultType.ERROR}));
		
		this.tests.add(new Test("<input type=\"button\" tabindex=\"2\" aria-label=\"Close\">", 
				new Result[] {ResultType.ERROR}));
		
		this.tests.add(new Test("<img ismap src=\"/images/logo.png\" alt=\"fancyImage\"/>", 
				new Result[] {ResultType.ERROR}));
		
		this.tests.add(new Test("<img src=\"/images/logo.png\" alt=\"fancyImage\" ismap/>", 
				new Result[] {ResultType.ERROR}));
	}
	
	
	
	
}
