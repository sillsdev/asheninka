// Copyright (c) 2016-2017 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;

import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.service.CVSegmenterGraphemeEnvironmentResult;
import org.sil.syllableparser.service.CVSegmenterGraphemeResult;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
	private BooleanProperty checked;
	private Segment owningSegment;

	public Grapheme() {
		super();
		this.form = new SimpleStringProperty("");
		this.description = new SimpleStringProperty("");
		this.environments = new SimpleListProperty<Environment>();
		this.envsRepresentation = new SimpleStringProperty("");
		this.checked = new SimpleBooleanProperty(true);
		active = true;
		createUUID();
	}

	public Grapheme(String form, String description, SimpleListProperty<Environment> environments,
			String envsRepresentation, boolean isActive) {
		super();
		this.form = new SimpleStringProperty(form);
		this.description = new SimpleStringProperty(description);
		this.environments = new SimpleListProperty<Environment>(environments);
		this.envsRepresentation = new SimpleStringProperty(envsRepresentation);
		this.checked = new SimpleBooleanProperty(isActive);
		this.active = isActive;
		createUUID();
	}

	public boolean isChecked() {
		return this.checked.getValue();
	}

	public void setChecked(boolean value) {
		checked.set(value);
		this.setActive(value);
	}

	public BooleanProperty checkedProperty() {
		return checked;
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

	@XmlAttribute(name = "envs")
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

	public String getEnvsRepresentation() {
		return envsRepresentation.get();
	}

	public StringProperty envsRepresentationProperty() {
		return envsRepresentation;
	}

	public void setEnvsRepresentation(String envsRepresentation) {
		this.envsRepresentation.set(envsRepresentation);
	}

	public void recalulateEnvsRepresentation() {
		String sEnvs = envs.stream().filter(env -> env.isActive())
				.map(Environment::getEnvironmentRepresentation).collect(Collectors.joining("; "));
		setEnvsRepresentation(sEnvs);
		envsRepresentationProperty().setValue(sEnvs);
	}

	public Segment getOwningSegment() {
		return owningSegment;
	}

	@XmlAttribute(name = "segment")
	@XmlIDREF
	public void setOwningSegment(Segment owningSegment) {
		this.owningSegment = owningSegment;
	}

	public boolean matchesAnEnvironment(String sBefore, String sAfter,
			List<GraphemeNaturalClass> classes, CVSegmenterGraphemeResult graphemeResult) {
		boolean fMatches = false;
		if (envs.size() == 0) {
			fMatches = true;
		} else {
			for (Environment env : envs) {
				CVSegmenterGraphemeEnvironmentResult envResult = new CVSegmenterGraphemeEnvironmentResult();
				envResult.sEnvironmentRepresentation = env.getEnvironmentRepresentation();
				graphemeResult.environmentsTried.add(envResult);
				if (env.matches(sBefore, sAfter, classes)) {
					fMatches = true;
					envResult.fEnvironmentPassed = true;
					break;
				}
			}
		}
		return fMatches;
	}

	@Override
	public int hashCode() {
		String sCombo = form.getValueSafe() + envsRepresentation.getValueSafe();
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
		Grapheme grapheme = (Grapheme) obj;
		if (!getForm().equals(grapheme.getForm())) {
			result = false;
		} else if (!getEnvsRepresentation().equals(grapheme.getEnvsRepresentation())) {
			result = false;
		}
		return result;
	}
}
