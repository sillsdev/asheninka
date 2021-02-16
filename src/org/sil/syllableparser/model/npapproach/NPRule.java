// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model.npapproach;

import javax.xml.bind.annotation.XmlElement;

import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.cvapproach.CVSegmentOrNaturalClass;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 * an Entity
 */
public class NPRule extends SylParserObject {
	private final StringProperty ruleName;
	private final StringProperty description;
	private final StringProperty affectedSegmentOrNaturalClass;
	private final StringProperty contextSegmentOrNaturalClass;
	private final StringProperty action;
	private final StringProperty level;
	private final BooleanProperty obeysSSP;
	private final StringProperty ruleRepresentation;

	private NPRuleAction ruleAction = NPRuleAction.ATTACH;
	private NPRuleLevel ruleLevel = NPRuleLevel.N_DOUBLE_BAR;
	private CVSegmentOrNaturalClass affectedSegOrNC;
	private CVSegmentOrNaturalClass contextSegOrNC;

	public NPRule() {
		super();
		this.ruleName = new SimpleStringProperty("");
		this.description = new SimpleStringProperty("");
		this.affectedSegmentOrNaturalClass = new SimpleStringProperty("");
		this.contextSegmentOrNaturalClass = new SimpleStringProperty("");
		this.action = new SimpleStringProperty("");
		this.level = new SimpleStringProperty("");
		this.obeysSSP = new SimpleBooleanProperty(true);
		this.ruleRepresentation = new SimpleStringProperty("");
		createUUID();
	}

	public NPRule(String className, String description, String affectedSegmentOrNaturalClass,
			String context, String action, String level, boolean obeysSSP, String ruleRepresentation) {
		super();
		this.ruleName = new SimpleStringProperty(className);
		this.description = new SimpleStringProperty(description);
		this.affectedSegmentOrNaturalClass = new SimpleStringProperty(affectedSegmentOrNaturalClass);
		this.contextSegmentOrNaturalClass = new SimpleStringProperty(context);
		this.action = new SimpleStringProperty(action);
		this.level = new SimpleStringProperty(level);
		this.obeysSSP = new SimpleBooleanProperty(true);
		this.ruleRepresentation = new SimpleStringProperty(ruleRepresentation);
		createUUID();
	}

	public String getRuleName() {
		return ruleName.get();
	}

	public StringProperty ruleNameProperty() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName.set(ruleName);
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

	public String getAffectedSegmentOrNaturalClass() {
		return affectedSegmentOrNaturalClass.get();
	}

	public StringProperty affectedSegmentOrNaturalClassProperty() {
		return affectedSegmentOrNaturalClass;
	}

	public void setAffectedSegmentOrNaturalClass(String value) {
		this.affectedSegmentOrNaturalClass.set(value);
	}

	@XmlElement(name="affectedSegOrNC")
	public CVSegmentOrNaturalClass getAffectedSegOrNC() {
		return affectedSegOrNC;
	}

	public void setAffectedSegOrNC(CVSegmentOrNaturalClass affectedSegOrNC) {
		this.affectedSegOrNC = affectedSegOrNC;
	}

	public String getContextSegmentOrNaturalClass() {
		return contextSegmentOrNaturalClass.get();
	}

	public StringProperty contextSegmentOrNaturalClassProperty() {
		return contextSegmentOrNaturalClass;
	}

	public void setContextSegmentOrNaturalClass(String value) {
		this.contextSegmentOrNaturalClass.set(value);
	}

	@XmlElement(name="contextSegOrNC")
	public CVSegmentOrNaturalClass getContextSegOrNC() {
		return contextSegOrNC;
	}

	public void setContextSegOrNC(CVSegmentOrNaturalClass levelSegOrNC) {
		this.contextSegOrNC = levelSegOrNC;
	}

	public String getAction() {
		return action.get();
	}

	public StringProperty ActionProperty() {
		return action;
	}

	public void setAction(String value) {
		this.action.set(value);
	}

	public NPRuleAction getRuleAction() {
		return ruleAction;
	}

	public void setRuleAction(NPRuleAction value) {
		this.ruleAction = value;
	}

	public String getLevel() {
		return level.get();
	}

	public StringProperty LevelProperty() {
		return level;
	}

	public void setLevel(String value) {
		this.level.set(value);
	}

	public NPRuleLevel getRuleLevel() {
		return ruleLevel;
	}

	public void setRuleLevel(NPRuleLevel value) {
		this.ruleLevel = value;
	}

	public boolean isObeysSSP() {
		return obeysSSP.get();
	}

	public BooleanProperty obeysSSPProperty() {
		return obeysSSP;
	}

	public void setObeysSSP(boolean value) {
		this.obeysSSP.set(value);
	}

	public String getRuleRepresentation() {
		return ruleRepresentation.get();
	}

	public StringProperty ruleRepresentationProperty() {
		return ruleRepresentation;
	}

	public void setRuleRepresentation(String ruleRepresentation) {
		this.ruleRepresentation.set(ruleRepresentation);
	}

	/**
	 * @return
	 */
	public StringProperty ruleProperty() {
		return this.ruleName;
	}

	@Override
	public int hashCode() {
		String sCombo = ruleName.getValueSafe() + description.getValueSafe()
				+ affectedSegmentOrNaturalClass.getValueSafe() + contextSegmentOrNaturalClass.getValueSafe()
				+ action.getValueSafe() + level.getValueSafe() + obeysSSP.getValue()
				+ ruleRepresentation.getValueSafe();
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
		NPRule rule = (NPRule) obj;
		if (!getRuleName().equals(rule.getRuleName())) {
			result = false;
		} else if (!getDescription().equals(rule.getDescription())) {
			result = false;
		} else if (!getAffectedSegmentOrNaturalClass().equals(
				rule.getAffectedSegmentOrNaturalClass())) {
			result = false;
		} else if (!getContextSegmentOrNaturalClass().equals(rule.getContextSegmentOrNaturalClass())) {
			result = false;
		} else if (!getAction().equals(rule.getAction())) {
			result = false;
		} else if (!getLevel().equals(rule.getLevel())) {
			result = false;
		} else if (isObeysSSP() != rule.isObeysSSP()) {
			result = false;
		} else if (!getRuleRepresentation().equals(rule.getRuleRepresentation())) {
			result = false;
		}
		return result;
	}

	@Override
	public String getSortingValue() {
		return getRuleName();
	}

}
