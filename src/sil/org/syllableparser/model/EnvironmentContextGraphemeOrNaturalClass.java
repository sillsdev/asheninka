// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;

import sil.org.syllableparser.model.Grapheme;
import sil.org.syllableparser.model.SylParserObject;
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
	private String graphemeOrNaturalClass;
	private boolean isGrapheme = true;
	private boolean isOptional = false;

	public EnvironmentContextGraphemeOrNaturalClass() {
		this(null, true);
	}

	public EnvironmentContextGraphemeOrNaturalClass(String graphemeOrNaturalClass, boolean isGrapheme) {
		this.graphemeOrNaturalClass = graphemeOrNaturalClass;
		this.isGrapheme = isGrapheme;
	}
	
	/**
	 * Properties
	 */
	public String getGraphemeOrNaturalClass() {
		return graphemeOrNaturalClass;
	}

	public void setGraphemeOrNaturalClass(String graphemeOrNaturalClass) {
		this.graphemeOrNaturalClass = graphemeOrNaturalClass;
	}

	public boolean isGrapheme() {
		return isGrapheme;
	}

	public void setGrapheme(boolean isGrapheme) {
		this.isGrapheme = isGrapheme;
	}

	public boolean isOptional() {
		return isOptional;
	}

	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

}
