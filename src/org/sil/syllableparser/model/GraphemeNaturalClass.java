// Copyright (c) 2016-2025 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlList;

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
public class GraphemeNaturalClass extends SylParserObject {
	private final StringProperty ncName;
	private final SimpleListProperty<Object> graphemesOrNaturalClasses;
	private final StringProperty description;
	private final StringProperty gncRepresentation;
	ObservableList<SylParserObject> gnc = FXCollections.observableArrayList();

	public GraphemeNaturalClass() {
		super();
		this.ncName = new SimpleStringProperty("");
		this.graphemesOrNaturalClasses = new SimpleListProperty<Object>();
		this.description = new SimpleStringProperty("");
		this.gncRepresentation = new SimpleStringProperty("");
		createUUID();
	}

	public GraphemeNaturalClass(String className, SimpleListProperty<Object> graphemesOrNaturalClasses,
			String description, String sncRepresentation) {
		super();
		this.ncName = new SimpleStringProperty(className);
		this.graphemesOrNaturalClasses = new SimpleListProperty<Object>(graphemesOrNaturalClasses);
		this.description = new SimpleStringProperty(description);
		this.gncRepresentation = new SimpleStringProperty(sncRepresentation);
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

	@XmlAttribute(name="gnc")
	@XmlIDREF
	@XmlList
	public ObservableList<SylParserObject> getGraphemesOrNaturalClasses() {
		return gnc;
	}

	public void setGraphemesOrNaturalClasses(ObservableList<SylParserObject> gnc) {
		this.gnc = gnc;
	}

	public SimpleListProperty<Object> graphemesOrNaturalClassesProperty() {
		return graphemesOrNaturalClasses;
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

	public String getGNCRepresentation() {
		return gncRepresentation.get();
	}

	public StringProperty gncRepresentationProperty() {
		return gncRepresentation;
	}

	public void setGNCRepresentation(String gncRepresentation) {
		this.gncRepresentation.set(gncRepresentation);
	}

	/**
	 * @return
	 */
	public StringProperty naturalClassProperty() {
		return this.ncName;
	}
	@Override
	public int hashCode() {
		String sCombo = ncName.getValueSafe() + gncRepresentation.getValueSafe();
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
		GraphemeNaturalClass natClass = (GraphemeNaturalClass) obj;
		if (!getNCName().equals(natClass.getNCName())) {
			result = false;
		} else {
			if (!getGNCRepresentation().equals(natClass.getGNCRepresentation())) {
				result = false;
			}
		}
		return result;
	}

	@Override
	public String getSortingValue() {
		return getGNCRepresentation();
	}

}
