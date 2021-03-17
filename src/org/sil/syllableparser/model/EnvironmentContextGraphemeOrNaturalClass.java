// Copyright (c) 2016-2021 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model;

import javax.xml.bind.annotation.XmlElement;

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
