package code.selenium_interface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Interface {
	public WebDriver driver;
	
	public Interface() {
		System.setProperty("webdriver.chrome.silentOutput", "true");
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		Logger.getLogger("org.openqa.selenium.remote").setLevel(Level.OFF);
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		driver = new ChromeDriver(options);
	}

	//Navigates to url and returns it's rendered form
	public String getRenderedHtml(String url) {
		driver.get(url);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		while (!js.executeScript("return document.readyState").toString().equals("complete")) {
			try { Thread.sleep(50); } catch (Exception e) {  }
		}
		return this.getElementsByTagName("html")[0].getAttribute("outerHTML");
	}

	//Returns the attributes of an element
	public Map<String, String> getElementAttributes(WebElement ele) {
		Map<String, String> attr = new HashMap<String, String>();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String ret = js.executeScript("var items = {}; for (index = 0; index < arguments[0].attributes.length; ++index) { items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;", ele).toString();
		ret = ret.substring(1, ret.length() - 1);
		for (int i = 0; i < ret.split(",").length; i ++) {
			attr.put(ret.split(",")[i].split("=")[0].trim(), ret.split(",")[i].split("=")[1].trim());
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
	
	public WebElement[] getSubElementsByTagName(WebElement parent, String tag) {
		List<WebElement> elements = parent.findElements(By.tagName(tag));
		WebElement[] e = new WebElement[elements.size()];
		for (int i = 0; i < elements.size(); i ++) {
			e[i] = elements.get(i);
		}
		return e;
	}

	//Returns list of all elements present on page.
	public List<WebElement> getAllElements() {
		List<WebElement> el = driver.findElements(By.cssSelector("*"));

		return el;
	}

	public void close() {
		driver.close();
		driver.quit();
		driver = null;
	}

	public Object executeJavascript(String script) {
		JavascriptExecutor js = (JavascriptExecutor) driver;  
		return js.executeScript(script);
	}
}