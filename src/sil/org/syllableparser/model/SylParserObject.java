// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.model;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlTransient;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;

/**
 * @author Andy Black
 *
 */
public abstract class SylParserObject {

	protected String id;
	protected boolean active;
	protected BooleanProperty activeCheckBox;
	
	public SylParserObject() {
		active = true;
		activeCheckBox = new SimpleBooleanProperty(true);
	}
	
	@XmlAttribute(name="active")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
		this.activeCheckBox.set(active);
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


	@XmlAttribute(name="id")
	@XmlID
	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	protected void createUUID() {
		UUID uuid = UUID.randomUUID();
		setID(uuid.toString());
	}

	public static int findIndexInSylParserObjectListByUuid(ObservableList<SylParserObject> list,
			String uuid) {
		// TODO: is there a way to do this with lambda expressions?
		// Is there a way to use SylParserObject somehow?
		int index = -1;
		for (SylParserObject sylParserObject : list) {
			index++;
			if (sylParserObject.getID().equals(uuid)) {
				return index;
			}
		}
		return -1;
	}
}
