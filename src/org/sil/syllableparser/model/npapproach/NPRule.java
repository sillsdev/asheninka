// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model.npapproach;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.cvapproach.CVSegmentOrNaturalClass;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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
	private final BooleanProperty isValid;
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
		this.isValid = new SimpleBooleanProperty(true);
		this.ruleRepresentation = new SimpleStringProperty("");
		createUUID();
	}

	public NPRule(String ruleName, String description, String affectedSegmentOrNaturalClass,
			String context, String action, String level, boolean obeysSSP, boolean isValid, String ruleRepresentation) {
		super();
		this.ruleName = new SimpleStringProperty(ruleName);
		this.description = new SimpleStringProperty(description);
		this.affectedSegmentOrNaturalClass = new SimpleStringProperty(affectedSegmentOrNaturalClass);
		this.contextSegmentOrNaturalClass = new SimpleStringProperty(context);
		this.action = new SimpleStringProperty(action);
		this.level = new SimpleStringProperty(level);
		this.obeysSSP = new SimpleBooleanProperty(obeysSSP);
		this.isValid = new SimpleBooleanProperty(isValid);
		this.ruleRepresentation = new SimpleStringProperty(ruleRepresentation);
		createUUID();
	}

	public NPRule(String ruleName, String description, CVSegmentOrNaturalClass affectedSegOrNC,
			CVSegmentOrNaturalClass contextSegOrNC, NPRuleAction action, NPRuleLevel level,
			boolean obeysSSP, boolean isValid, String ruleRepresentation) {
		super();
		this.ruleName = new SimpleStringProperty(ruleName);
		this.description = new SimpleStringProperty(description);
		this.affectedSegmentOrNaturalClass = new SimpleStringProperty("");
		this.contextSegmentOrNaturalClass = new SimpleStringProperty("");
		this.action = new SimpleStringProperty("");
		this.level = new SimpleStringProperty("");
		this.affectedSegOrNC = affectedSegOrNC;
		this.contextSegOrNC = contextSegOrNC;
		this.ruleAction = action;
		this.ruleLevel = level;
		this.obeysSSP = new SimpleBooleanProperty(obeysSSP);
		this.isValid = new SimpleBooleanProperty(isValid);
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
		if (affectedSegOrNC == null) {
			this.affectedSegOrNC = null;
		} else {
			this.affectedSegOrNC = new CVSegmentOrNaturalClass(getAffectedSegmentOrNaturalClass(),
					affectedSegOrNC.getDescription(), affectedSegOrNC.isSegment(),
					affectedSegOrNC.getUuid(), affectedSegOrNC.isActive());
		}
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

	public void setContextSegOrNC(CVSegmentOrNaturalClass contextSegOrNC) {
		if (contextSegOrNC == null) {
			this.contextSegOrNC = null;
		} else {
			this.contextSegOrNC = new CVSegmentOrNaturalClass(getContextSegmentOrNaturalClass(),
					contextSegOrNC.getDescription(), contextSegOrNC.isSegment(),
					contextSegOrNC.getUuid(), contextSegOrNC.isActive());
		}
	}

	@XmlTransient
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

	@XmlTransient
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

	@XmlAttribute(name="valid")
	public boolean isValid() {
		return isValid.get();
	}

	public BooleanProperty isValidProperty() {
		return isValid;
	}

	public void setIsValid(boolean value) {
		this.isValid.set(value);
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

	public String createLingTreeDescription() {
		if (!isValid()) {
			setRuleRepresentation("");
			return "";
		}
		StringBuilder sb = new StringBuilder();
		switch (ruleAction) {
		case ATTACH:
			switch (ruleLevel) {
			case ALL:
				break;
			case N:
				// diphthong
				sb.append("(N(\\L ");
				sb.append(contextSegOrNC.getSegmentOrNaturalClass());
				sb.append(")(\\L ");
				sb.append(getAffectedValueForLingTree());
				sb.append("))");
				break;
			case N_BAR:
				// coda
				sb.append("(N'(N(\\L ");
				sb.append(contextSegOrNC.getSegmentOrNaturalClass());
				sb.append("))(\\L ");
				sb.append(getAffectedValueForLingTree());
				sb.append("))");
				break;
			case N_DOUBLE_BAR:
				// onset
				sb.append("(N''(\\L ");
				sb.append(getAffectedValueForLingTree());
				sb.append(")(N'(N(\\L ");
				sb.append(contextSegOrNC.getSegmentOrNaturalClass());
				sb.append("))))");
				break;
			default:
				break;
			}
			break;
		case AUGMENT:
			switch (ruleLevel) {
			case ALL:
				break;
			case N:
				// diphthong
				sb.append("(N(\\L ");
				sb.append(contextSegOrNC.getSegmentOrNaturalClass());
				sb.append(")(\\L ");
				sb.append(getAffectedValueForLingTree());
				sb.append("))");
				break;
			case N_BAR:
				// coda
				sb.append("(N'(\\L ");
				sb.append(contextSegOrNC.getSegmentOrNaturalClass());
				sb.append(")(\\L ");
				sb.append(getAffectedValueForLingTree());
				sb.append("))");
				break;
			case N_DOUBLE_BAR:
				// onset
				sb.append("(N''(\\L ");
				sb.append(getAffectedValueForLingTree());
				sb.append(")(\\L ");
				sb.append(contextSegOrNC.getSegmentOrNaturalClass());
				sb.append("))");
				break;
			default:
				break;
			}
			break;
		case BUILD:
			sb.append("(N''(N'(N(\\L ");
			sb.append(getAffectedValueForLingTree());
			sb.append("))))");
			break;
		case LEFT_ADJOIN:
			switch (ruleLevel) {
			case ALL:
				break;
			case N:
				sb.append("(N(N(\\L ");
				sb.append(getAffectedValueForLingTree());
				sb.append("))(N(\\L ");
				sb.append(contextSegOrNC.getSegmentOrNaturalClass());
				sb.append(")))");
				break;
			case N_BAR:
				sb.append("(N'(N'(\\L ");
				sb.append(getAffectedValueForLingTree());
				sb.append("))(N'(\\L ");
				sb.append(contextSegOrNC.getSegmentOrNaturalClass());
				sb.append(")))");
				break;
			case N_DOUBLE_BAR:
				sb.append("(N''(N''(\\L ");
				sb.append(getAffectedValueForLingTree());
				sb.append("))(N''(\\L ");
				sb.append(contextSegOrNC.getSegmentOrNaturalClass());
				sb.append(")))");
				break;
			default:
				break;
			}			break;
		case RIGHT_ADJOIN:
			switch (ruleLevel) {
			case ALL:
				break;
			case N:
				sb.append("(N(N(\\L ");
				sb.append(contextSegOrNC.getSegmentOrNaturalClass());
				sb.append("))(N(\\L ");
				sb.append(getAffectedValueForLingTree());
				sb.append(")))");
				break;
			case N_BAR:
				sb.append("(N'(N'(\\L ");
				sb.append(contextSegOrNC.getSegmentOrNaturalClass());
				sb.append("))(N'(\\L ");
				sb.append(getAffectedValueForLingTree());
				sb.append(")))");
				break;
			case N_DOUBLE_BAR:
				sb.append("(N''(N''(\\L ");
				sb.append(contextSegOrNC.getSegmentOrNaturalClass());
				sb.append("))(N''(\\L ");
				sb.append(getAffectedValueForLingTree());
				sb.append(")))");
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		setRuleRepresentation(sb.toString());
		return sb.toString();
	}

	protected String getAffectedValueForLingTree() {
		StringBuilder sb = new StringBuilder();
		if (!isObeysSSP()) {
			sb.append("*");
		}
		sb.append(affectedSegOrNC.getSegmentOrNaturalClass());
		return sb.toString();
	}

	public String adjustForAffectedSVG(String svg) {
		switch (ruleAction) {
		case ATTACH:
			// same as augment
		case AUGMENT:
			switch (ruleLevel) {
			case ALL:
				break;
			case N:
				// diphthong;  change last line (which should have x1 < x2) to use dashed line
				// same as coda so fall through
			case N_BAR:
				// coda; change last line (which should have x1 < x2) to use dashed line
				svg = adjustLastLine(svg);
				break;
			case N_DOUBLE_BAR:
				// onset; change first line (which should have x1 > x2) to use dashed line
				svg = adjustFirstLine(svg);
				break;
			default:
				break;
			}
			break;
		case BUILD:
			break;
		case LEFT_ADJOIN:
			switch (ruleLevel) {
			case ALL:
				break;
			case N:
				// fall through
			case N_BAR:
				// fall through
			case N_DOUBLE_BAR:
				// change first, second, and third lines to use dashed line
				// first line (which should have x1 > x2)
				// second line (which should have x1 == x2)
				// third line (which should have x1 < x2)
				svg = adjustFirstLine(svg);
				svg = adjustSecondLine(svg);
				svg = adjustThirdLine(svg);
				break;
			default:
				break;
			}
			break;
		case RIGHT_ADJOIN:
			switch (ruleLevel) {
			case ALL:
				break;
			case N:
				// fall through
			case N_BAR:
				// fall through
			case N_DOUBLE_BAR:
				// change first, third, and last (fourth) lines to use dashed line
				// first line (which should have x1 > x2)
				// third line (which should have x1 < x2)
				// last/fourth line (which should have x1 == x2)
				svg = adjustFirstLine(svg);
				svg = adjustThirdLine(svg);
				svg = adjustLastLine(svg);
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		return svg;
	}

	protected String adjustFirstLine(String svg) {
		int i = svg.indexOf("<line ");
		int j = svg.substring(i).indexOf("/>");
		svg = svg.substring(0, i + j) + Constants.SVG_DASHED_LINE + svg.substring(i + j);
		return svg;
	}

	protected String adjustSecondLine(String svg) {
		int i1 = svg.indexOf("<line ");
		int i2 = svg.substring(i1 + 1).indexOf("<line ");
		int i = i1 + i2;
		int j = svg.substring(i).indexOf("/>");
		svg = svg.substring(0, i + j) + Constants.SVG_DASHED_LINE + svg.substring(i + j);
		return svg;
	}

	protected String adjustThirdLine(String svg) {
		int i1 = svg.indexOf("<line ");
		int i2 = svg.substring(i1 + 1).indexOf("<line ");
		int i3 = svg.substring(i1 + i2 + 2).indexOf("<line ");
		int i = i1 + i2 + i3;
		int j = svg.substring(i).indexOf("/>");
		svg = svg.substring(0, i + j) + Constants.SVG_DASHED_LINE + svg.substring(i + j);
		return svg;
	}

	protected String adjustLastLine(String svg) {
		int i = svg.lastIndexOf("<line ");
		int j = svg.substring(i).indexOf("/>");
		svg = svg.substring(0, i + j) + Constants.SVG_DASHED_LINE + svg.substring(i + j);
		return svg;
	}

	@Override
	public int hashCode() {
		String sCombo = ruleName.getValueSafe() + description.getValueSafe()
				+ affectedSegmentOrNaturalClass.getValueSafe() + contextSegmentOrNaturalClass.getValueSafe()
				+ action.getValueSafe() + level.getValueSafe() + obeysSSP.getValue() + isValid.getValue()
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
		} else if (isValid() != rule.isValid()) {
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
