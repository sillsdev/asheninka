// Copyright (c) 2018-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model.sonorityhierarchyapproach;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;

import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SylParserObject;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 * an Entity
 */
public class SHNaturalClass extends SylParserObject {
	private final StringProperty ncName;
	private final SimpleListProperty<Segment> segments;
	private final StringProperty description;
	private final StringProperty segmentsRepresentation;
	ObservableList<Segment> segs = FXCollections.observableArrayList();

	public SHNaturalClass() {
		super();
		this.ncName = new SimpleStringProperty("");
		this.segments = new SimpleListProperty<Segment>();
		this.description = new SimpleStringProperty("");
		this.segmentsRepresentation = new SimpleStringProperty("");
		createUUID();
	}

	public SHNaturalClass(String className, SimpleListProperty<Segment> segments,
			String description, String sncRepresentation) {
		super();
		this.ncName = new SimpleStringProperty(className);
		this.segments = new SimpleListProperty<Segment>(segments);
		this.description = new SimpleStringProperty(description);
		this.segmentsRepresentation = new SimpleStringProperty(sncRepresentation);
		createUUID();
	}

	public String getNCName() {
		return ncName.get();
	}

	public StringProperty ncNameProperty() {
		return ncName;
	}

	public void setNCName(String ncName) {
		this.ncName.set(ncName);
	}

	@XmlAttribute(name="segs")
	@XmlIDREF
	@XmlList
	public ObservableList<Segment> getSegments() {
		return segs;
	}

	public void setSegments(ObservableList<Segment> snc) {
		this.segs = snc;
	}

	public SimpleListProperty<Segment> segmentsProperty() {
		return segments;
	}

	public String getDescription() {
		return description.get();
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	public String getSegmentsRepresentation() {
		return segmentsRepresentation.get();
	}

	public StringProperty segmentsRepresentationProperty() {
		return segmentsRepresentation;
	}

	public void setSegmentsRepresentation(String segmentsRepresentation) {
		this.segmentsRepresentation.set(segmentsRepresentation);
	}

	/**
	 * @return
	 */
	public StringProperty naturalClassProperty() {
		return this.ncName;
	}
	@Override
	public int hashCode() {
		String sCombo = ncName.getValueSafe() + segmentsRepresentation.getValueSafe();
		return sCombo.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		boolean result = true;
		SHNaturalClass natClass = (SHNaturalClass) obj;
		if (!getNCName().equals(natClass.getNCName())) {
			result = false;
		} else {
			if (!getSegmentsRepresentation().equals(natClass.getSegmentsRepresentation())) {
				result = false;
			}
		}
		return result;
	}

	@Override
	public String getSortingValue() {
		return getNCName();
	}

}
