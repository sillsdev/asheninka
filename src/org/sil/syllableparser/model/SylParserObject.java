// Copyright (c) 2016-2025 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model;

import java.util.UUID;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public abstract class SylParserObject extends SylParserBase {

	protected String id;
	public SylParserObject() {
		active = true;
		activeCheckBox = new SimpleBooleanProperty(true);
	}

	@XmlAttribute(name="id")
	@XmlID
	public String getID() {
		return id;
	}
	
	public String getSortingValue() {
		return "";
	}

	public void setID(String id) {
		this.id = id;
	}

	protected void createUUID() {
		UUID uuid = UUID.randomUUID();
		setID(uuid.toString());
	}

	public static int findIndexInListByUuid(ObservableList<? extends SylParserObject> list,
			String uuid) {
		// TODO: is there a way to do this with lambda expressions?
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
