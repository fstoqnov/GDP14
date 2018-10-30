package tests.selenium_interface;

import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.WebElement;

import tests.RunTests;

public class Interface {
	private code.selenium_interface.Interface inter;

	public static boolean runTests() {
		Interface testInter = new Interface();
		boolean correct = true;
		correct = testInter.testGetRenderedHtml() && correct;
		correct = testInter.testGetElementAttributes() && correct;
		correct = testInter.testGetElementContent() && correct;
		correct = testInter.testGetElementById() && correct;
		correct = testInter.testGetElementsByTagName() && correct;
		correct = testInter.testGetSubElementsByTagName() && correct;
		correct = testInter.testGetParentWithAttributeAndTag() && correct;
		correct = testInter.testExecuteJavascript() && correct;
		correct = testInter.testClose() && correct;
		return correct;
	}

	private Interface() {
		inter = new code.selenium_interface.Interface();
	}

	private boolean testGetRenderedHtml() {
		try {
			TestsServer ts = new TestsServer();
			ts.createServer(RunTests.TEST_PORT);
			return
				RunTests.test("Interface(GetRenderedHtml)", TestsServer.renderedHTML, inter.getRenderedHtml("http://localhost:" + RunTests.TEST_PORT + "/")) &&
				RunTests.test("Interface(GetRenderedHtml)", true, !ts.hadErrors());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean testGetElementAttributes() {
		Map<String, String> attr = inter.getElementAttributes(inter.getElementById("val"));
		String attrString = "";
		String expectedAttrString = "id=\"val\" class=\"test\"";
		int loop = 0;
		for (Entry<String, String> entry : attr.entrySet()) {
			if (loop != 0) { attrString += " "; }
			loop ++;
			attrString += entry.getKey() + "=\"" + entry.getValue() + "\"";
		}
		return RunTests.test("Interface(GetElementAttributes)", expectedAttrString, attrString);
	}

	private boolean testGetElementContent() {
		String content = inter.getElementContent(inter.getElementById("val"));
		String expectedContent = "hello world!";
		return RunTests.test("Interface(GetElementContent)", expectedContent, content);
	}

	private boolean testGetElementById() {
		String expectedElementStr = "div val";
		String elementStr = inter.getElementById("val").getTagName() + " " + inter.getElementById("val").getAttribute("id");
		return RunTests.test("Interface(GetElementById)", expectedElementStr, elementStr);
	}

	private boolean testGetElementsByTagName() {
		String expectedElementStr = "div 0 div 1 div 2 div 3";
		WebElement[] elements = inter.getElementsByTagName("div");
		String elementStr = "";
		int loop = 0;
		for (WebElement element : elements) {
			if (loop != 0) { elementStr += " "; }
			elementStr += element.getTagName() + " " + loop;
			loop ++;
		}
		return RunTests.test("Interface(GetElementsByTagName)", expectedElementStr, elementStr);
	}
	
	private boolean testGetSubElementsByTagName() {
		String expectedElementStr = "div 0 div 1";
		WebElement[] elements = inter.getSubElementsByTagName(inter.getElementById("one"), "div");
		String elementStr = "";
		int loop = 0;
		for (WebElement element : elements) {
			if (loop != 0) { elementStr += " "; }
			elementStr += element.getTagName() + " " + loop;
			loop ++;
		}
		return RunTests.test("Interface(GetSubElementsByTagName)", expectedElementStr, elementStr);
	}
	
	private boolean testGetParentWithAttributeAndTag() {
		String expectedElementStr = "div one div one";
		String elementStr = "";
		WebElement element = inter.getParentWithAttributeAndTag(inter.getElementById("three"), "find_attr", "find", "div");
		elementStr += element.getTagName() + " " + inter.getElementId(element);
		element = inter.getParentWithAttributeAndTag(inter.getElementById("three"), "find_attr", "*", "div");
		elementStr += " " + element.getTagName() + " " + inter.getElementId(element);
		return RunTests.test("Interface(GetParentWithAttributeAndTag)", expectedElementStr, elementStr);
	}

	private boolean testExecuteJavascript() {
		inter.executeJavascript("document.getElementById('val').innerHTML = \"goodbye world!\"");
		return RunTests.test("Interface(ExecuteJavascript)", "goodbye world!", inter.getElementById("val").getText());
	}
	
	private boolean testClose() {
		inter.close();
		return RunTests.test("Interface(Close)", true, inter.driver == null);
	}
}