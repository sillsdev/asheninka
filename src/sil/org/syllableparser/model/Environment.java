package sil.org.syllableparser.model;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlTransient;

import sil.org.syllableparser.model.cvapproach.CVNaturalClass;

public class Environment extends SylParserObject {
	// a. String representation
	// b. Name
	// c. Description
	// d. Left context
	// e. Right context

	private final StringProperty envName;
	private final StringProperty description;
	private final StringProperty environmentRepresentation;
	private EnvironmentContext leftContext;
	private EnvironmentContext rightContext;

	public Environment() {
		super();
		this.envName = new SimpleStringProperty("");
		this.description = new SimpleStringProperty("");
		this.environmentRepresentation = new SimpleStringProperty("");
		this.leftContext = new EnvironmentContext();
		this.rightContext = new EnvironmentContext();
		createUUID();
	}

	public Environment(String envName, String description, String envRepresentation,
			EnvironmentContext left, EnvironmentContext right) {
		super();
		this.envName = new SimpleStringProperty(envName);
		this.description = new SimpleStringProperty(description);
		this.environmentRepresentation = new SimpleStringProperty(envRepresentation);
		this.leftContext = left;
		this.rightContext = right;
		createUUID();
	}

	public String getEnvironmentName() {
		return envName.get();
	}

	public StringProperty environmentNameProperty() {
		return envName;
	}

	public void setEnvironmentName(String envName) {
		this.envName.set(envName);
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

	public String getEnvironmentRepresentation() {
		return environmentRepresentation.get();
	}

	public StringProperty environmentRepresentationProperty() {
		return environmentRepresentation;
	}

	public void setEnvironmentRepresentation(String environmentRepresentation) {
		this.environmentRepresentation.set(environmentRepresentation);
	}

	public EnvironmentContext getLeftContext() {
		return leftContext;
	}

	@XmlTransient
	public void setLeftContext(EnvironmentContext leftContext) {
		this.leftContext = leftContext;
	}

	public EnvironmentContext getRightContext() {
		return rightContext;
	}

	@XmlTransient
	public void setRightContext(EnvironmentContext rightContext) {
		this.rightContext = rightContext;
	}

	public static int findIndexInEnvironmentListByUuid(ObservableList<Environment> list,
			String uuid) {
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
	public StringProperty environmentProperty() {
		return this.envName;
	}

	@Override
	public int hashCode() {
		String sCombo = envName.getValueSafe() + environmentRepresentation.getValueSafe();
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
		Environment env = (Environment) obj;
		if (!getEnvironmentName().equals(env.getEnvironmentName())) {
			result = false;
		} else {
			if (!getEnvironmentRepresentation().equals(env.getEnvironmentRepresentation())) {
				result = false;
			}
		}
		return result;
	}

}
