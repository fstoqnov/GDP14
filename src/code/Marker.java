package code;

import java.util.Objects;

import org.openqa.selenium.WebElement;

import code.checks.Check;

public class Marker {
	private WebElement element;
	private String attribute;
	private long position;
	private int type;
	private Check check;
	private String desc;
	private boolean hidden;
	private String id;
	private String outerHTML;

	public static final int MARKER_ERROR = 1;
	public static final int MARKER_AMBIGUOUS = 2;
	public static final int MARKER_AMBIGUOUS_SERIOUS = 3;
	public static final int MARKER_SUCCESS = 4;

	//The constructors are coded lazily, I was tired. Feel free to clean them up
	public Marker(int type, Check check, WebElement element) {
		this.element = element;
		this.position = -1;
		this.attribute = null;
		this.type = type;
		this.check = check;
		id = element.getAttribute("id");
		this.outerHTML = element.getAttribute("outerHTML");
	}
	
	public Marker(int type, Check check) {
		this.element = null;
		this.position = -1;
		this.attribute = null;
		this.type = type;
		this.check = check;
	}
	
	public Marker(String desc, int type, Check check, WebElement element) {
		this.element = element;
		this.position = -1;
		this.attribute = null;
		this.type = type;
		this.check = check;
		this.desc = desc;
		id = element.getAttribute("id");
		this.outerHTML = element.getAttribute("outerHTML");
	}

	public Marker(String desc, int type, Check check, WebElement element, String attribute) {
		this.element = element;
		this.attribute = attribute;
		this.position = -1;
		this.type = type;
		this.check = check;
		this.desc = desc;
		id = element.getAttribute("id");
		this.outerHTML = element.getAttribute("outerHTML");
	}
	
	public Marker(int type, Check check, WebElement element, String attribute) {
		this.element = element;
		this.attribute = attribute;
		this.position = -1;
		this.type = type;
		this.check = check;
		id = element.getAttribute("id");
		this.outerHTML = element.getAttribute("outerHTML");
	}
	
	public Marker(String desc, int type, Check check, WebElement element, long position) {
		this.element = element;
		this.position = position;
		this.attribute = null;
		this.type = type;
		this.check = check;
		this.desc = desc;
		id = element.getAttribute("id");
		this.outerHTML = element.getAttribute("outerHTML");
	}

	public Marker(int type, Check check, WebElement element, long position) {
		this.element = element;
		this.position = position;
		this.attribute = null;
		this.type = type;
		this.check = check;
		id = element.getAttribute("id");
		this.outerHTML = element.getAttribute("outerHTML");
	}

	public Marker(int type, Check check, long position) {
		this.element = null;
		this.position = position;
		this.attribute = null;
		this.type = type;
		this.check = check;
	}
	
	public Marker(String desc, int type, Check check, long position) {
		this.element = null;
		this.position = position;
		this.attribute = null;
		this.type = type;
		this.check = check;
		this.desc = desc;
	}

	public Marker(String desc, int type, Check check, String outerHTML) {
		this.element = null;
		this.position = -1;
		this.attribute = null;
		this.type = type;
		this.check = check;
		this.desc = desc;
		this.outerHTML = outerHTML;
	}

	public WebElement getElement() { return element; }
	public String getAttribute() { return attribute; }
	public long getPosition() { return position; }
	public int getType() { return type; }
	public Check getCheck() { return check; }
	public String getDesc() { return desc; }
	public boolean getHidden() { return hidden; }
	public String getEleID() { return id; }
	public String getOuterHTML() { return outerHTML; }

	public boolean equals(Object o) {

		if (o == this) { return true; }
		if (!(o instanceof Marker)) {
			return false;
		}

		Marker marker = (Marker) o;

		return marker.attribute.equals(attribute) &&
				marker.position == position &&
				marker.type == type &&
				marker.check.equals(check) &&
				(marker.attribute == null ? attribute == null : (attribute == null ? false : marker.attribute.equals(attribute))) &&
				(marker.element == null ? element == null : (element == null ? false : marker.element.equals(element)));
	}

	public int hashCode() {
		return Objects.hash(type, check, element, attribute, position);
	}
}