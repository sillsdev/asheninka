/**
// Copyright (c) 2016-2025 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
 * Used in a chooser
 */
package org.sil.syllableparser.model.cvapproach;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.SylParserBase;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Andy Black
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class CVSegmentOrNaturalClass extends SylParserBase  {

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

	@XmlAttribute(name="segOrNC")
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@XmlAttribute(name="isSegment")
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

	public String getSegmentOrNaturalClassForShow() {
		String s = getSegmentOrNaturalClass();
		if (!isSegment) {
			s = Constants.NATURAL_CLASS_PREFIX + s + Constants.NATURAL_CLASS_SUFFIX;
		}
		return s;
	}

	public static boolean equalsCVSegOrNC(CVSegmentOrNaturalClass thisOne, CVSegmentOrNaturalClass thatOne) {
		boolean result = true;
		if (thisOne != null) {
			if (thatOne != null) {
				if (!thisOne.getUuid().equals(thatOne.getUuid())) {
					result = false;
				}
			} else {
				result = false;
			}
		} else if (thatOne != null) {
			result = false;
		}
		return result;
	}

	public static void createTextFromCVSegOrNC(CVSegmentOrNaturalClass cvsegc, StringBuilder sb) {
		if (cvsegc != null) {
			sb.append(cvsegc.getUuid());
		} else {
			sb.append(Constants.NULL_AS_STRING);
		}
	}

}
