/**
 * 
 */
package sil.org.syllableparser.model;

import java.util.UUID;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public class CVSyllablePattern extends SylParserObject {
	private final StringProperty spName;
	private final SimpleListProperty<CVNaturalClass> naturalClasses;
	private StringProperty description;
	private StringProperty ncsRepresentation;
	ObservableList<CVNaturalClass> ncs = FXCollections.observableArrayList();
	
	public CVSyllablePattern() {
		super();
		this.spName = new SimpleStringProperty("");
		this.naturalClasses = new SimpleListProperty<CVNaturalClass>();
		this.description = new SimpleStringProperty("");
		this.ncsRepresentation = new SimpleStringProperty("");
		this.uuid = UUID.randomUUID();
	}

	public CVSyllablePattern(String patternName, SimpleListProperty<CVNaturalClass> naturalClasses, 
			String description, String sncRepresentation) {
		super();
		this.spName = new SimpleStringProperty(patternName);
		this.naturalClasses = new SimpleListProperty<CVNaturalClass>(naturalClasses);
		this.description = new SimpleStringProperty(description);
		this.ncsRepresentation = new SimpleStringProperty(sncRepresentation);
		uuid = UUID.randomUUID();
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

	public String getDescription() {
		return description.get();
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	/**
	 * @return the ncsRepresentation
	 */
	public StringProperty getNCsRepresentation() {
		return ncsRepresentation;
	}
	public StringProperty sncRepresentationProperty() {
		return ncsRepresentation;
	}
	public void setNCsRepresentation(StringProperty ncsRepresentation) {
		this.ncsRepresentation = ncsRepresentation;
	}
	public ObservableList<CVNaturalClass> getNaturalClasses() {
		return naturalClasses;
	}

	public void setNaturalClasses(ObservableList<CVNaturalClass> naturalClasses) {
		this.naturalClasses.set(naturalClasses);
	}

	public SimpleListProperty<CVNaturalClass> naturalClassesProperty() {
		return naturalClasses;
	}
	public ObservableList<CVNaturalClass> getNCs() {
		return ncs;
	}

	public void setNCs(ObservableList<CVNaturalClass> ncs) {
		this.ncs = ncs;
	}


}
