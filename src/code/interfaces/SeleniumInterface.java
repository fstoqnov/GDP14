package code.interfaces;

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

public class SeleniumInterface {
	public WebDriver driver;

	public SeleniumInterface() {
		Logger.getLogger("org.openqa.selenium.remote").setLevel(Level.OFF);
		startChrome();
	}

	private void startChrome() {
		System.setProperty("webdriver.chrome.silentOutput", "true");
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");

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
		return getHTML();
	}

	public String getHTML() {
		return this.getElementsByTagName("html")[0].getAttribute("outerHTML");
	}

	public String getTextContentStrict(WebElement ele) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		try {
			return js.executeScript("[].reduce.call(arguments[0].childNodes, function(a, b) { return a + (b.nodeType === 3 ? b.textContent : ''); }, '')", ele).toString();
		} catch (Exception e) {
			return null;
		}
	}

	public String getComputedStyleElement(WebElement ele, String property) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		try {
			return js.executeScript("return window.getComputedStyle(arguments[0])." + property, ele).toString();
		} catch (Exception e) {
			return null;
		}
	}

	public String getParentalComputedStyle(WebElement ele, String property, String nullValue) {
		WebElement parent = ele;
		String attr = nullValue;
		try {
			do {
				attr = getComputedStyleElement(parent, property);
			} while ((parent = parent.findElement(By.xpath(".."))) != null && attr.equals(nullValue));
		} catch (Exception e) {  }
		return attr;
	}

	public String getDomRep() {
		return getDomRep(this.getElementsByTagName("html")[0]);
	}

	public String getDomRep(WebElement parent) {
		String flattened = "<" + parent.getTagName() + ">";
		List<WebElement> children = getSubElements(parent);
		for (int i = 0; i < children.size(); i ++) {
			flattened += getDomRep(children.get(i));
		}
		flattened += "</" + parent.getTagName() + ">";
		return flattened;
	}

	public List<WebElement> getSubElements(WebElement parent) {
		return parent.findElements(By.xpath("./*"));
	}

	public boolean containsText(WebElement element) {
		List<WebElement> children = getSubElements(element);
		for (int i = 0; i < children.size(); i ++) {
			if (children.get(i).getText().equals(element.getText())) {
				return false;
			}
		}
		return true;
	}

	public int getTagPosition(WebElement ele) {
		WebElement[] elements = this.getElementsByTagName(ele.getTagName());
		for (int i = 0; i < elements.length; i ++) {
			if (elements[i].equals(ele)) { return i; }
		}
		return -1;
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

	public String getElementId(WebElement ele) {
		return getElementAttributes(ele).get("id");
	}


	//Gets the first parent with a specified <* attrName=attrVal></*>
	public WebElement getParentWithAttribute(WebElement ele, String attrName, String attrVal) {
		WebElement parent;
		WebElement first = ele;
		Map<String, String> attrs;
		while ((parent = first.findElement(By.xpath(".."))) != null) {
			attrs = getElementAttributes(parent);
			if (attrVal.equals("*") ? attrs.containsKey(attrName) : (attrs.containsKey(attrName) && attrs.get(attrName).equals(attrVal) )) {
				return parent;
			}
			first = parent;
		}
		return null; //never reaches here as an exception will be thrown if the element cannot be found
	}

	//Gets the first parent with a specified <tagName attrName=attrVal></tagName>
	public WebElement getParentWithAttributeAndTag(WebElement ele, String attrName, String attrVal, String tag) {
		WebElement parent;
		WebElement first = ele;
		Map<String, String> attrs;
		while ((parent = first.findElement(By.xpath(".."))) != null) {
			attrs = getElementAttributes(parent);
			if (parent.getTagName().equals(tag) && attrVal.equals("*") ? attrs.containsKey(attrName) : (attrs.containsKey(attrName) && attrs.get(attrName).equals(attrVal) )) {
				return parent;
			}
			first = parent;
		}
		return null; //never reaches here as an exception will be thrown if the element cannot be found
	}
	
	public WebElement[] getElementsWithAttribute(String attrName, String attrVal) {
		List<WebElement> elements = driver.findElements(By.xpath("//*[@" + attrName + "='" + attrVal + "']"));
		
		WebElement[] e = new WebElement[elements.size()];
		for (int i = 0; i < elements.size(); i++) {
			e[i] = elements.get(i);
		}
		return e;
	}
	public WebElement[] getElementsWithAttributeAnyValue(String attrName) {
		List<WebElement> elements = driver.findElements(By.xpath("//*[@" + attrName + "]"));

		WebElement[] e = new WebElement[elements.size()];
		for (int i = 0; i < elements.size(); i ++) {
			e[i] = elements.get(i);
		}
		return e;
	}

	//Gets the first parent with a specified <tagName></tagName>
	public WebElement getParentWithTag(WebElement ele, String tag) {
		WebElement parent;
		WebElement first = ele;
		while ((parent = first.findElement(By.xpath(".."))) != null) {
			if (parent.getTagName().equals(tag)) {
				return parent;
			}
			first = parent;
		}
		return null; //never reaches here as an exception will be thrown if the element cannot be found
	}

	//selects all children with a specified <* attrName=attrVal></*>
	public WebElement[] getChildrenWithAttribute(WebElement ele, String attrName, String attrVal) {
		List<WebElement> elements = ele.findElements(By.xpath("//*[@" + attrName + "='" + attrVal + "']"));
		WebElement[] e = new WebElement[elements.size()];
		for (int i = 0; i < elements.size(); i ++) {
			e[i] = elements.get(i);
		}
		return e;
	}
	
	//selects all children with a specified <tagName attrName=attrVal></tagName>
	public WebElement[] getChildrenWithAttributeAndTag(WebElement ele, String attrName, String attrVal, String tagName) {
		List<WebElement> elements = ele.findElements(By.xpath("//" + tagName + "[@" + attrName + "='" + attrVal + "']"));
		WebElement[] e = new WebElement[elements.size()];
		for (int i = 0; i < elements.size(); i ++) {
			e[i] = elements.get(i);
		}
		return e;
	}

	//selects all children with a specified <tagName></tagName>
	public WebElement[] getChildrenWithTag(WebElement ele, String tag) {
		List<WebElement> elements = ele.findElements(By.tagName(tag));
		WebElement[] e = new WebElement[elements.size()];
		for (int i = 0; i < elements.size(); i ++) {
			e[i] = elements.get(i);
		}
		return e;
	}

	//Returns the content of the element
	public String getElementContent(WebElement ele) {
		return ele.getText();
	}

	//Returns the element with specified ID
	public WebElement getElementById(String id) {
		try {
			return driver.findElement(By.id(id));
		} catch (Exception e) { return null; }
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

	public String getCurrentURL() { return driver.getCurrentUrl(); }

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