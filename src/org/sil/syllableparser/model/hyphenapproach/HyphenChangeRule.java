// Copyright (c) 2025 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model.hyphenapproach;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlList;

import org.sil.syllableparser.model.SylParserObject;

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
public class HyphenChangeRule extends SylParserObject {
	private final StringProperty hypRuleName;
//	private final SimpleListProperty<HyphenClass> hyphenClasses;
	private final StringProperty description;
	private final StringProperty matchRepresentation;
	private final StringProperty changeRepresentation;
	ObservableList<HyphenClass> matchClasses = FXCollections.observableArrayList();
	ObservableList<HyphenClass> changeClasses = FXCollections.observableArrayList();
	private Boolean fWordInitial;
	private Boolean fWordFinal;

	public HyphenChangeRule() {
		super();
		this.hypRuleName = new SimpleStringProperty("");
//		this.hyphenClasses = new SimpleListProperty<HyphenClass>();
		this.description = new SimpleStringProperty("");
		this.matchRepresentation = new SimpleStringProperty("");
		this.changeRepresentation = new SimpleStringProperty("");
		this.fWordInitial = false;
		this.fWordFinal = false;
		createUUID();
	}

	public HyphenChangeRule(String ruleName, SimpleListProperty<HyphenClass> hyphenClasses,
			String description, String matchStringRepresentation, String changeStringRepresentation) {
		super();
		this.hypRuleName = new SimpleStringProperty(ruleName);
//		this.hyphenClasses = new SimpleListProperty<HyphenClass>(hyphenClasses);
		this.description = new SimpleStringProperty(description);
		this.matchRepresentation = new SimpleStringProperty(matchStringRepresentation);
		this.changeRepresentation = new SimpleStringProperty(changeStringRepresentation);;
		this.fWordInitial = false;
		this.fWordFinal = false;
		createUUID();
	}

	public String getRuleName() {
		return hypRuleName.get();
	}

	public StringProperty ruleNameProperty() {
		return hypRuleName;
	}

	public void setRuleName(String value) {
		this.hypRuleName.set(value);
	}

//	public ObservableList<HyphenClass> getHypenClasses() {
//		return hyphenClasses;
//	}

	@XmlAttribute(name="matchClasses")
	@XmlIDREF
	@XmlList
	public ObservableList<HyphenClass> getMatchHyphenClasses() {
		return matchClasses;
	}

	public void setMatchClasses(ObservableList<HyphenClass> value) {
		this.matchClasses = value;
	}

	@XmlAttribute(name="changeClasses")
	@XmlIDREF
	@XmlList
	public ObservableList<HyphenClass> getChangeHyphenClasses() {
		return changeClasses;
	}

	public void setChangeClasses(ObservableList<HyphenClass> value) {
		this.changeClasses = value;
	}

//	public SimpleListProperty<HyphenClass> naturalClassesProperty() {
//		return hyphenClasses;
//	}
//
//	public void setNaturalClasses(ObservableList<HyphenClass> naturalClasses) {
//		this.hyphenClasses.set(naturalClasses);
//	}

	public String getDescription() {
		return description.get();
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	public String getMatchRepresentation() {
		return matchRepresentation.get();
	}
	public StringProperty matchRepresentationProperty() {
		return matchRepresentation;
	}
	public void setMatchRepresentation(String value) {
		this.matchRepresentation.set(value);
	}

	public String getChangeRepresentation() {
		return changeRepresentation.get();
	}
	public StringProperty changeRepresentationProperty() {
		return changeRepresentation;
	}
	public void setChangeRepresentation(String value) {
		this.changeRepresentation.set(value);
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

	/**
	 * @return
	 */
	public StringProperty hyphenChangeProperty() {
		return this.hypRuleName;
	}

	@Override
	public int hashCode() {
		String sCombo = hypRuleName.getValueSafe() + matchRepresentation.getValueSafe() + changeRepresentation.getValueSafe();
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
		HyphenChangeRule hypRule = (HyphenChangeRule) obj;
		if (!getRuleName().equals(hypRule.getRuleName())) {
			result = false;
		} else {
			if (!getMatchRepresentation().equals(hypRule.getMatchRepresentation())) {
				result = false;
			} else {
				if (!getChangeRepresentation().equals(hypRule.getChangeRepresentation())) {
					result = false;
				}
			}
		}
		return result;
	}

	@Override
	public String getSortingValue() {
		return getRuleName();
	}

}
