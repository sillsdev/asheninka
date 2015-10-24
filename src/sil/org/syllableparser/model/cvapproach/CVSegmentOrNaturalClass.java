/**
 * Used in a chooser
 */
package sil.org.syllableparser.model.cvapproach;

import sil.org.syllableparser.model.SylParserObject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Andy Black
 *
 */
public class CVSegmentOrNaturalClass {

	private StringProperty segmentOrNaturalClass;
	private StringProperty description;
	private BooleanProperty checked;
	private String uuid;
	private boolean isSegment = true;

	public CVSegmentOrNaturalClass() {
		this(null, null, true, null);
	}

	public CVSegmentOrNaturalClass(String segmentOrNaturalClass, String description, boolean isSegment, String uuid) {
		this.segmentOrNaturalClass = new SimpleStringProperty(segmentOrNaturalClass);
		this.description = new SimpleStringProperty(description);
		this.checked = new SimpleBooleanProperty(false);
		this.isSegment = isSegment;
		this.uuid = uuid;
	}
	public CVSegmentOrNaturalClass(SylParserObject segment) {
		
	}
	
	/**
	 * Properties
	 */
	public String getSegmentOrNaturalClass() {
		return segmentOrNaturalClass.get();
	}
	public String getDescription() {
		return description.get();
	}
	public boolean isChecked() {
		return checked.get();
	}

	public void setSegmentOrNaturalClass(String segmentOrNaturalClass) {
		this.segmentOrNaturalClass.set(segmentOrNaturalClass);
	}
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public boolean isSegment() {
		return isSegment;
	}

	public void setSegment(boolean isSegment) {
		this.isSegment = isSegment;
	}

	public void setDescription(String description) {
		this.description.set(description);
	}
	public void setChecked(boolean value) {
		this.checked.set(value);	
	}

	public StringProperty segmentOrNaturalClassProperty() {
		return segmentOrNaturalClass;
	}
	public StringProperty descriptionProperty() {
		return description;
	}
	public BooleanProperty checkedProperty() {
		return checked;
	}
}
