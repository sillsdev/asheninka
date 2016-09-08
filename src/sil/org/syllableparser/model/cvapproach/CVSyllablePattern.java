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
public class CVSyllablePattern extends SylParserObject {
	private final StringProperty spName;
	private final SimpleListProperty<CVNaturalClass> naturalClasses;
	private final StringProperty description;
	private final StringProperty ncsRepresentation;
	ObservableList<CVNaturalClass> ncs = FXCollections.observableArrayList();
	private Boolean fWordInitial;
	private Boolean fWordFinal;
	
	public CVSyllablePattern() {
		super();
		this.spName = new SimpleStringProperty("");
		this.naturalClasses = new SimpleListProperty<CVNaturalClass>();
		this.description = new SimpleStringProperty("");
		this.ncsRepresentation = new SimpleStringProperty("");
		this.fWordInitial = false;
		this.fWordFinal = false;
		createUUID();
	}

	public CVSyllablePattern(String patternName, SimpleListProperty<CVNaturalClass> naturalClasses, 
			String description, String ncsRepresentation) {
		super();
		this.spName = new SimpleStringProperty(patternName);
		this.naturalClasses = new SimpleListProperty<CVNaturalClass>(naturalClasses);
		this.description = new SimpleStringProperty(description);
		this.ncsRepresentation = new SimpleStringProperty(ncsRepresentation);
		this.fWordInitial = false;
		this.fWordFinal = false;
		createUUID();
	}

	public String getSPName() {
		return spName.get();
	}

	public StringProperty spNameProperty() {
		return spName;
	}

	public void setSPName(String spName) {
		this.spName.set(spName);
	}

	public ObservableList<CVNaturalClass> getNaturalClasses() {
		return naturalClasses;
	}

	@XmlAttribute(name="ncs")
	@XmlIDREF
	@XmlList
	public ObservableList<CVNaturalClass> getNCs() {
		return ncs;
	}

	public void setNCs(ObservableList<CVNaturalClass> ncs) {
		this.ncs = ncs;
	}

	public SimpleListProperty<CVNaturalClass> naturalClassesProperty() {
		return naturalClasses;
	}

	public void setNaturalClasses(ObservableList<CVNaturalClass> naturalClasses) {
		this.naturalClasses.set(naturalClasses);
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

	public String getNCSRepresentation() {
		return ncsRepresentation.get();
	}
	public StringProperty ncsRepresentationProperty() {
		return ncsRepresentation;
	}
	public void setNCSRepresentation(String ncsRepresentation) {
		this.ncsRepresentation.set(ncsRepresentation);
	}
	
	public Boolean isWordInitial() {
		return fWordInitial;
	}

	public void setWordInitial(Boolean fWordInitial) {
		this.fWordInitial = fWordInitial;
	}

	public Boolean isWordFinal() {
		return fWordFinal;
	}

	public void setWordFinal(Boolean fWordFinal) {
		this.fWordFinal = fWordFinal;
	}

	public static int findIndexInSyllablePatternListByUuid(ObservableList<CVSyllablePattern> list, String uuid) {
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
	public StringProperty syllablePatternProperty() {
		return this.spName;
	}

	@Override
	public int hashCode() {
		String sCombo = spName.getValueSafe() + ncsRepresentation.getValueSafe();
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
		CVSyllablePattern sylPattern = (CVSyllablePattern) obj;
		if (!getSPName().equals(sylPattern.getSPName())) {
			result = false;
		} else {
			if (!getNCSRepresentation().equals(sylPattern.getNCSRepresentation())) {
				result = false;
			}
		}
		return result;
	}

}
