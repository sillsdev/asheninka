// Copyright (c) 2016-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model.cvapproach;

import java.util.ArrayList;
import java.util.List;

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
public class CVNaturalClass extends SylParserObject {
	private final StringProperty ncName;
	private final SimpleListProperty<Object> segmentsOrNaturalClasses;
	private final StringProperty description;
	private final StringProperty sncRepresentation;
	ObservableList<SylParserObject> snc = FXCollections.observableArrayList();

	public CVNaturalClass() {
		super();
		this.ncName = new SimpleStringProperty("");
		this.segmentsOrNaturalClasses = new SimpleListProperty<Object>();
		this.description = new SimpleStringProperty("");
		this.sncRepresentation = new SimpleStringProperty("");
		createUUID();
	}

	public CVNaturalClass(String className, SimpleListProperty<Object> segmentsOrNaturalClasses,
			String description, String sncRepresentation) {
		super();
		this.ncName = new SimpleStringProperty(className);
		this.segmentsOrNaturalClasses = new SimpleListProperty<Object>(segmentsOrNaturalClasses);
		this.description = new SimpleStringProperty(description);
		this.sncRepresentation = new SimpleStringProperty(sncRepresentation);
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

	@XmlAttribute(name="snc")
	@XmlIDREF
	@XmlList
	public ObservableList<SylParserObject> getSegmentsOrNaturalClasses() {
		return snc;
	}

	public void setSegmentsOrNaturalClasses(ObservableList<SylParserObject> snc) {
		this.snc = snc;
	}

	public SimpleListProperty<Object> segmentsOrNaturalClassesProperty() {
		return segmentsOrNaturalClasses;
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

	public String getSNCRepresentation() {
		return sncRepresentation.get();
	}

	public StringProperty sncRepresentationProperty() {
		return sncRepresentation;
	}

	public void setSNCRepresentation(String sncRepresentation) {
		this.sncRepresentation.set(sncRepresentation);
	}

	/**
	 * @return
	 */
	public StringProperty naturalClassProperty() {
		return this.ncName;
	}

	/**
	 * Get a list of all segments in this natural class, including any in any
	 * embedded natural classes.
	 * @return a list of all segments
	 */
	public List<Segment> getAllSegments() {
		List<Segment> segments = new ArrayList<Segment>();
		if (isActive()) {
			for (SylParserObject spo : getSegmentsOrNaturalClasses()) {
				if (spo instanceof Segment) {
					segments.add((Segment) spo);
				} else if (spo instanceof CVNaturalClass) {
					segments.addAll(((CVNaturalClass)spo).getAllSegments());
				}
			}
		}
		return segments;
	}

	@Override
	public int hashCode() {
		String sCombo = ncName.getValueSafe() + sncRepresentation.getValueSafe();
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
		CVNaturalClass natClass = (CVNaturalClass) obj;
		if (!getNCName().equals(natClass.getNCName())) {
			result = false;
		} else {
			if (!getSNCRepresentation().equals(natClass.getSNCRepresentation())) {
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
