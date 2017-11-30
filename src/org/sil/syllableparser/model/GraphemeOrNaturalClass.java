// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;

import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.SylParserObject;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 *         an Object Value
 */
public class GraphemeOrNaturalClass {
	private StringProperty graphemeOrNaturalClass;
	private StringProperty description;
	private BooleanProperty checked;
	private String uuid;
	private boolean isGrapheme = true;
	private boolean isActive = true;

	public GraphemeOrNaturalClass() {
		this(null, null, true, null, true);
	}

	public GraphemeOrNaturalClass(String graphemeOrNaturalClass, String description,
			boolean isGrapheme, String uuid, boolean isActive) {
		this.graphemeOrNaturalClass = new SimpleStringProperty(graphemeOrNaturalClass);
		this.description = new SimpleStringProperty(description);
		this.checked = new SimpleBooleanProperty(false);
		this.isGrapheme = isGrapheme;
		this.isActive = isActive;
		this.uuid = uuid;
	}

	/**
	 * Properties
	 */
	public String getGraphemeOrNaturalClass() {
		return graphemeOrNaturalClass.get();
	}

	public String getDescription() {
		return description.get();
	}

	public boolean isChecked() {
		return checked.get();
	}

	public void setGraphemeOrNaturalClass(String graphemeOrNaturalClass) {
		this.graphemeOrNaturalClass.set(graphemeOrNaturalClass);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public boolean isGrapheme() {
		return isGrapheme;
	}

	public void setGrapheme(boolean isGrapheme) {
		this.isGrapheme = isGrapheme;
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	public void setChecked(boolean value) {
		this.checked.set(value);
	}

	public StringProperty graphemeOrNaturalClassProperty() {
		return graphemeOrNaturalClass;
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
	 * @param isActive
	 *            the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
