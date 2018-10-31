package code.checks;

import java.util.Objects;

import org.openqa.selenium.WebElement;

public class Marker {
	private WebElement element;
	private String attribute;
	private long position;
	private int type;
	private Check check;

	public static final int MARKER_ERROR = 1;
	public static final int MARKER_AMBIGUOUS = 2;
	public static final int MARKER_AMBIGUOUS_SERIOUS = 3;
	public static final int MARKER_SUCCESS = 4;

	public Marker(int type, Check check, WebElement element) {
		this.element = element;
		this.position = -1;
		this.attribute = null;
		this.type = type;
		this.check = check;
	}

	public Marker(int type, Check check, WebElement element, String attribute) {
		this.element = element;
		this.attribute = attribute;
		this.position = -1;
		this.type = type;
		this.check = check;
	}

	public Marker(int type, Check check, WebElement element, long position) {
		this.element = element;
		this.position = position;
		this.attribute = null;
		this.type = type;
		this.check = check;
	}

	public Marker(int type, Check check, long position) {
		this.element = null;
		this.position = position;
		this.attribute = null;
		this.type = type;
		this.check = check;
	}

	public WebElement getElement() { return element; }
	public String getAttribute() { return attribute; }
	public long getPosition() { return position; }
	public int getType() { return type; }
	public Check getCheck() { return check; }

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