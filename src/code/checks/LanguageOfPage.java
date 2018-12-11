package code.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.WebElement;

import code.Marker;
import code.interfaces.SeleniumInterface;
import tests.Test;

public class LanguageOfPage extends Check {
	
	private ArrayList<String> lang;
	
	private static String ERR_NO_LANG() {return "No language declared for page";}
	private static String ERR_INVALID_LANG(String givenLang) {return "The language found is not in a valid format. Found lang: " + givenLang;}
	private static String SUCC_LANG() { return "The language is declared and valid"; }
	
	private static enum ResultType implements ResultT {
		ERROR,
		SUCCESS
	}
	
	public LanguageOfPage() {
		super("Criterion 3.1.1 Language of Page");
	}

	@Override
	public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) {
		WebElement[] doc = inter.getElementsByTagName("html");
		for (int i = 0; i < doc.length; i ++) {
			String docLang = doc[i].getAttribute("lang");
			if(docLang == null) {
				addFlagToElement(markers, Marker.MARKER_ERROR, doc[i], ERR_NO_LANG(), ResultType.ERROR);
			} else if(!lang.contains(docLang)){
				addFlagToElement(markers, Marker.MARKER_ERROR, doc[i], ERR_INVALID_LANG(docLang), ResultType.ERROR);
			} else {
				addFlagToElement(markers, Marker.MARKER_SUCCESS, doc[i], SUCC_LANG(), ResultType.SUCCESS);

			}
		}
		
	}
	

	public void setupTests() {
		String pass1 = "<html lang=\"en-GB\"></html>";
		ResultT[] expectedPass1 = {ResultType.SUCCESS};
		this.tests.add(new Test(pass1, expectedPass1));
		
		String fail1 = "<html lang=\"not-included\"></html>";
		ResultT[] expectedFail1 = {ResultType.ERROR};
		this.tests.add(new Test(fail1, expectedFail1));
		
		String fail2 = "<html>I have no lang tag specified</html>";
		ResultT[] expectedFail2 = {ResultType.ERROR};
		this.tests.add(new Test(fail2, expectedFail2));

	}

	@Override
	public void initialise() {
		lang = new ArrayList<String>();
		lang.addAll(Arrays.asList(Locale.getISOLanguages()));
		lang.add("ar-DZ");
		lang.add("ar-BH");
		lang.add("ar-EG");
		lang.add("ar-IQ");
		lang.add("ar-JO");
		lang.add("ar-KW");
		lang.add("ar-LB");
		lang.add("ar-LY");
		lang.add("ar-MA");
		lang.add("ar-OM");
		lang.add("ar-QA");
		lang.add("ar-SA");
		lang.add("ar-SY");
		lang.add("ar-TN");
		lang.add("ar-AE");
		lang.add("ar-YE");
		lang.add("az-AZ");
		lang.add("zh-CN");
		lang.add("zh-HK");
		lang.add("zh-MO");
		lang.add("zh-SG");
		lang.add("zh-TW");
		lang.add("nl-BE");
		lang.add("nl-NL");
		lang.add("en-AU");
		lang.add("en-BZ");
		lang.add("en-CA");
		lang.add("en-CB");
		lang.add("en-GB");
		lang.add("en-IN");
		lang.add("en-IE");
		lang.add("en-JIM");
		lang.add("en-NZ");
		lang.add("en-PH");
		lang.add("en-ZA");
		lang.add("en-TT");
		lang.add("en-US");
		lang.add("fr-BE");
		lang.add("fr-CA");
		lang.add("fr-FR");
		lang.add("fr-LU");
		lang.add("fr-CH");
		lang.add("gd-IE");
		lang.add("de-AT");
		lang.add("de-DE");
		lang.add("de-LI");
		lang.add("de-LU");
		lang.add("de-CH");
		lang.add("it-IT");
		lang.add("it-CH");
		lang.add("ms-BN");
		lang.add("de-MY");
		lang.add("no-NO");
		lang.add("pt-BR");
		lang.add("pt-PT");
		lang.add("ro-MO");
		lang.add("ru-MO");
		lang.add("sr-SP");
		lang.add("es-AR");
		lang.add("es-BO");
		lang.add("es-CL");
		lang.add("es-CO");
		lang.add("es-CR");
		lang.add("es-DO");
		lang.add("es-EC");
		lang.add("es-SV");
		lang.add("es-GT");
		lang.add("es-HN");
		lang.add("es-MX");
		lang.add("es-NI");
		lang.add("es-PA");
		lang.add("es-PY");
		lang.add("es-PE");
		lang.add("es-PR");
		lang.add("es-ES");
		lang.add("es-UY");
		lang.add("es-VE");
		lang.add("sv-FI");
		lang.add("sv-SE");
		lang.add("uz-UZ");
	
	}
}