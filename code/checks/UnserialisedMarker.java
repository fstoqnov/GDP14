package code.checks;

import java.util.Objects;

public class UnserialisedMarker {
	public int type;
	public String tag;
	public int tagPos;
	public String attribute;
	public long position;
	public Check check;
	
	public UnserialisedMarker(int type, String tag, int tagPos, String attribute, long position, Check check) {
		this.type = type;
		this.tag = tag;
		this.tagPos = tagPos;
		this.attribute = attribute;
		this.position = position;
		this.check = check;
	}
	
	public boolean equals(Object o) {

		if (o == this) { return true; }
		if (!(o instanceof Marker)) {
			return false;
		}

		UnserialisedMarker marker = (UnserialisedMarker) o;

		return marker.position == position &&
				marker.type == type &&
				marker.check.equals(check) &&
				(marker.attribute == null ? attribute == null : (attribute == null ? false : marker.attribute.equals(attribute))) &&
				(marker.tag == null ? tag == null : (tag == null ? false : marker.tag.equals(tag))) &&
				marker.tagPos == tagPos;
	}

	public int hashCode() {
		return Objects.hash(type, tag, tagPos, attribute, position, check);
	}
}
