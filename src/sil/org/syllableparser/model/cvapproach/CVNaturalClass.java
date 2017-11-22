// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.model.cvapproach;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;

import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.SylParserObject;
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
//	ListChangeListener<SylParserObject> lcl;
//	WeakListChangeListener<SylParserObject> weakListener = new WeakListChangeListener<>(lcl);
	
	public CVNaturalClass() {
		super();
		this.ncName = new SimpleStringProperty("");
		this.segmentsOrNaturalClasses = new SimpleListProperty<Object>();
		this.description = new SimpleStringProperty("");
		this.sncRepresentation = new SimpleStringProperty("");
		createUUID();
//		snc.addListener(weakListener);
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

	public static int findIndexInNaturaClassListByUuid(ObservableList<CVNaturalClass> list, String uuid) {
		// TODO: is there a way to do this with lambda expressions?
		// Is there a way to use SylParserObject somehow?
				int index = -1;
				for (SylParserObject sylParserObject : list) {
					index++;
					if (sylParserObject.getID() == uuid) {
						return index;
					}
				}		
				return -1;
			}

	/**
	 * @return
	 */
	public StringProperty naturalClassProperty() {
		return this.ncName;
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

}
