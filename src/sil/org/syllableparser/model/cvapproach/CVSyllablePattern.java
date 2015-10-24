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
	
	public CVSyllablePattern() {
		super();
		this.spName = new SimpleStringProperty("");
		this.naturalClasses = new SimpleListProperty<CVNaturalClass>();
		this.description = new SimpleStringProperty("");
		this.ncsRepresentation = new SimpleStringProperty("");
		createUUID();
	}

	public CVSyllablePattern(String patternName, SimpleListProperty<CVNaturalClass> naturalClasses, 
			String description, String ncsRepresentation) {
		super();
		this.spName = new SimpleStringProperty(patternName);
		this.naturalClasses = new SimpleListProperty<CVNaturalClass>(naturalClasses);
		this.description = new SimpleStringProperty(description);
		this.ncsRepresentation = new SimpleStringProperty(ncsRepresentation);
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


}
