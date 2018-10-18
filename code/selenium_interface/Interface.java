package code.selenium_interface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Interface {
	public WebDriver driver;
	
	public Interface() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		driver = new ChromeDriver(options);
	}

	//Navigates to url and returns it's rendered form
	public String getRenderedHtml(String url) {
		driver.get(url);
		return driver.getPageSource();
	}

	//Returns the attributes of an element
	public Map<String, String> getElementAttributes(WebElement ele) {
		Map<String, String> attr = new HashMap<String, String>();
		String outer = ele.getAttribute("outerHTML");
		Pattern pattern = Pattern.compile("([a-z]+-?[a-z]+_?)=('?\"?)");
		Matcher m = pattern.matcher(outer);
		while (m.find()) {
			attr.put(m.group(1), m.group(2));
		}
		return attr;
	}

	//Returns the content of the element
	public String getElementContent(WebElement ele) {
		return ele.getText();
	}

	//Returns the element with specified ID
	public WebElement getElementById(String id) {
		return driver.findElement(By.id(id));
	}

	//Returns the elements with specified tag name
	public WebElement[] getElementsByTagName(String tag) {
		List<WebElement> elements = driver.findElements(By.tagName(tag));
		WebElement[] e = new WebElement[elements.size()];
		for (int i = 0; i < elements.size(); i ++) {
			e[i] = elements.get(i);
		}
		return e;
	}

	public void close() {
		driver.close();
		driver.quit();
		driver = null;
	}
}