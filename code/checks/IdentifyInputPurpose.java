package code.checks;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;

import code.selenium_interface.Interface;

public class IdentifyInputPurpose extends Check {

	private List<String> list;
	
	protected IdentifyInputPurpose() {
		super("Criterion 1.3.5 Identify Input Purpose");
	}

	@Override
	public boolean runCheck(String urlContent, Interface inter) {
		WebElement[] forms = inter.getElementsByTagName("form");
		for (int i = 0; i < forms.length; i ++) {
			WebElement[] inputs = inter.getSubElementsByTagName(forms[i], "input");
			for (int j = 0; j < inputs.length; j ++) {
				if (inputs[j].getAttribute("autocomplete") != null) {
					if (!list.contains(inputs[j].getAttribute("autocomplete"))) {
						return false;
					}
				} else {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String[] getHTMLPass() {
		return new String[] {
				"<form><input autocomplete=\"honorific-prefix\"></form>",
				"<input type=\"text\"><form><input autocomplete=\"language\"></form>"
		};
	}

	@Override
	public String[] getHTMLFail() {
		return new String[] {
				"<form><input autocomplete=\"not-included\"></form>",
				"<form><input type=\"text\"></form>"
		};
	}

	@Override
	public void initialise() {
		list = new ArrayList<String>();
		list.add("name");
		list.add("honorific-prefix");
		list.add("given-name");
		list.add("additional-name");
		list.add("family-name");
		list.add("honorific-suffix");
		list.add("nickname");
		list.add("organization-title");
		list.add("username");
		list.add("new-password");
		list.add("current-password");
		list.add("organization");
		list.add("street-address");
		list.add("address-line1");
		list.add("address-line2");
		list.add("address-line3");
		list.add("address-level4");
		list.add("address-level3");
		list.add("address-level2");
		list.add("address-level1");
		list.add("country");
		list.add("country-name");
		list.add("postal-code");
		list.add("cc-name");
		list.add("cc-given-name");
		list.add("cc-additional-name");
		list.add("cc-family-name");
		list.add("cc-number");
		list.add("cc-exp");
		list.add("cc-exp-month");
		list.add("cc-exp-year");
		list.add("cc-csc");
		list.add("cc-type");
		list.add("transaction-currency");
		list.add("transaction-amount");
		list.add("language");
		list.add("bday");
		list.add("bday-day");
		list.add("bday-month");
		list.add("bday-year");
		list.add("sex");
		list.add("url");
		list.add("photo");
		list.add("tel");
		list.add("tel-country-code");
		list.add("tel-national");
		list.add("tel-area-code");
		list.add("tel-local");
		list.add("tel-local-prefix");
		list.add("tel-local-suffix");
		list.add("tel-extension");
		list.add("email");
		list.add("impp");
	}
}