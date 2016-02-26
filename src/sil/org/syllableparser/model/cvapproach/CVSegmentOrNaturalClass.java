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
	private boolean isActive = true;

	public CVSegmentOrNaturalClass() {
		this(null, null, true, null, true);
	}

	public CVSegmentOrNaturalClass(String segmentOrNaturalClass, String description, boolean isSegment, String uuid, boolean isActive) {
		this.segmentOrNaturalClass = new SimpleStringProperty(segmentOrNaturalClass);
		this.description = new SimpleStringProperty(description);
		this.checked = new SimpleBooleanProperty(false);
		this.isSegment = isSegment;
		this.isActive = isActive;
		this.uuid = uuid;
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

	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
}
