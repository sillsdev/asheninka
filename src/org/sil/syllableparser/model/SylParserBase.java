/**
 * Copyright (c) 2025 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model;

import javafx.beans.property.BooleanProperty;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 * @author Andy Black
 *
 * Base class for both entities and value objects.
 */
public class SylParserBase {
	
	protected boolean active;
	protected BooleanProperty activeCheckBox;

	/**
	 * currently empty 
	 */
	public SylParserBase() {
		
	}

	@XmlAttribute(name = "active")
	public boolean isActive() {
		return active;
	}

	public BooleanProperty activeCheckBoxProperty() {
		return activeCheckBox;
	}

	@XmlTransient
	public boolean isActiveCheckBox() {
		return activeCheckBox.get();
	}

	public void setActiveCheckBox(boolean value) {
		this.activeCheckBox.set(value);
	}

	public void setActive(boolean active) {
		this.active = active;
		this.activeCheckBox.set(active);
	}
}
