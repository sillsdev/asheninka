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

import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

/**
 * @author Andy Black
 *
 *         an Entity
 */
public class Grapheme extends SylParserObject {
	private final StringProperty form;
	private final StringProperty description;
	private final SimpleListProperty<Environment> environments;
	ObservableList<Environment> envs = FXCollections.observableArrayList();
	private final StringProperty envsRepresentation;

	public Grapheme() {
		super();
		this.form = new SimpleStringProperty("");
		this.description = new SimpleStringProperty("");
		this.environments = new SimpleListProperty<Environment>();
		this.envsRepresentation = new SimpleStringProperty("");
		createUUID();
	}

	public Grapheme(String form, String graphemes, String description,
			SimpleListProperty<Environment> environments, String envsRepresentation) {
		super();
		this.form = new SimpleStringProperty(form);
		this.description = new SimpleStringProperty(description);
		this.environments = new SimpleListProperty<Environment>(environments);
		this.envsRepresentation = new SimpleStringProperty(envsRepresentation);
		createUUID();
	}

	public String getForm() {
		return form.get().trim();
	}

	public StringProperty formProperty() {
		return form;
	}

	public void setForm(String form) {
		this.form.set(form);
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

	public ObservableList<Environment> getEnvironments() {
		return environments;
	}

	@XmlAttribute(name="envs")
	@XmlIDREF
	@XmlList
	public ObservableList<Environment> getEnvs() {
		return envs;
	}

	public void setEnvs(ObservableList<Environment> envs) {
		this.envs = envs;
	}

	public SimpleListProperty<Environment> environmentsProperty() {
		return environments;
	}

	public void setNaturalClasses(ObservableList<Environment> environments) {
		this.environments.set(environments);
	}

	public String getEnvsRepresentation() {
		return envsRepresentation.get();
	}
	public StringProperty envsRepresentationProperty() {
		return envsRepresentation;
	}
	public void setEnvsRepresentation(String envsRepresentation) {
		this.envsRepresentation.set(envsRepresentation);
	}
	
	public static int findIndexInGraphemesListByUuid(ObservableList<Grapheme> list, String uuid) {
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

	@Override
	public int hashCode() {
		String sCombo = form.getValueSafe();
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
		Grapheme seg = (Grapheme) obj;
		if (!getForm().equals(seg.getForm())) {
			result = false;
		}
		return result;
	}
}
