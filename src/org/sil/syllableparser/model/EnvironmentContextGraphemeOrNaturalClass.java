// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
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
 * an Object Value used in environment contexts
 */
public class EnvironmentContextGraphemeOrNaturalClass {
	private String graphemeString;
	private GraphemeNaturalClass graphemeNaturalClass;
	private boolean isGrapheme = true;
	private boolean isOptional = false;

	public EnvironmentContextGraphemeOrNaturalClass() {
		this(null, true);
	}

	public EnvironmentContextGraphemeOrNaturalClass(String graphemeString, boolean isGrapheme) {
		this.graphemeString = graphemeString;
		this.isGrapheme = isGrapheme;
	}
	
	/**
	 * Properties
	 */
	@XmlElement(name="graphemeString")
	public String getGraphemeString() {
		return graphemeString;
	}

	public void setGraphemeString(String graphemeString) {
		this.graphemeString = graphemeString;
	}

	/**
	 * @return the graphemeNaturalClass
	 */
	@XmlElement(name = "graphemeNaturalClass")
	public GraphemeNaturalClass getGraphemeNaturalClass() {
		return graphemeNaturalClass;
	}

	/**
	 * @param graphemeNaturalClass the graphemeNaturalClass to set
	 */
	public void setGraphemeNaturalClass(GraphemeNaturalClass graphemeNaturalClass) {
		this.graphemeNaturalClass = graphemeNaturalClass;
	}

	@XmlElement(name="isGrapheme")
	public boolean isGrapheme() {
		return isGrapheme;
	}

	public void setGrapheme(boolean isGrapheme) {
		this.isGrapheme = isGrapheme;
	}

	@XmlElement(name="isOptional")
	public boolean isOptional() {
		return isOptional;
	}

	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

}
