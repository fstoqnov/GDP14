package code;

import java.util.Objects;

import code.checks.Check;
import code.interfaces.DatabaseInterface;

public class UnserialisedMarker {
	public long id;
	public int type;
	public String tag;
	public int tagPos;
	public String attribute;
	public long position;
	public Check check;
	public String desc;
	public boolean hidden;

	public UnserialisedMarker(long id, int type, String tag, int tagPos, String attribute, long position, Check check, String desc, boolean hidden) {
		this.id = id;
		this.type = type;
		this.tag = tag;
		this.tagPos = tagPos;
		this.attribute = attribute;
		this.position = position;
		this.check = check;
		this.desc = desc;
		this.hidden = hidden;
	}
	
	public void setHidden(DatabaseInterface db) throws Exception {
		this.hidden = true;
		db.updateHiddenStatus(this);
	}
	public void setVisible(DatabaseInterface db) throws Exception {
		this.hidden = false;
		db.updateHiddenStatus(this);
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
