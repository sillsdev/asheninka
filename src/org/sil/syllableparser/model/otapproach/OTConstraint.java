// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model.otapproach;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

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
 *         an Entity
 */
public class OTConstraint extends SylParserObject {
	private final StringProperty constraintName;
	private final StringProperty description;
	private final StringProperty affectedElement1;
	private final StringProperty affectedElement2;
	private BooleanProperty isWordInitial1;
	private BooleanProperty isOnset1;
	private BooleanProperty isNucleus1;
	private BooleanProperty isCoda1;
	private BooleanProperty isUnparsed1;
	private BooleanProperty isWordFinal1;
	private StringProperty constraintRepresentation1;
	private BooleanProperty isWordInitial2;
	private BooleanProperty isOnset2;
	private BooleanProperty isNucleus2;
	private BooleanProperty isCoda2;
	private BooleanProperty isUnparsed2;
	private BooleanProperty isWordFinal2;
	private StringProperty constraintRepresentation2;
	private final BooleanProperty isValid;

	private CVSegmentOrNaturalClass affectedElementSegOrNC1;
	private CVSegmentOrNaturalClass affectedElementSegOrNC2;
	private int structuralOptions1;
	private int structuralOptions2;
	private String lingTreeDescription;

	public OTConstraint() {
		super();
		this.constraintName = new SimpleStringProperty("");
		this.description = new SimpleStringProperty("");
		this.affectedElement1 = new SimpleStringProperty("");
		this.affectedElement2 = new SimpleStringProperty("");
		structuralOptions1 = OTStructuralOptions.INITIALIZED;
		structuralOptions2 = OTStructuralOptions.INITIALIZED;
		initializeStructuralOptionsBooleans();
		setStructuralOptions1Booleans(structuralOptions1);
		setStructuralOptions2Booleans(structuralOptions1);
		this.constraintRepresentation1 = new SimpleStringProperty("");
		this.constraintRepresentation2 = new SimpleStringProperty("");
		this.isValid = new SimpleBooleanProperty(true);
		createUUID();
	}

	public OTConstraint(String ruleName, String description, String affectedElement,
			String secondAffectedElement, int structuralOptions1, int structuralOptions2,
			String constraintRepresentation, String secondConstraintRepresentation, boolean isValid) {
		super();
		this.constraintName = new SimpleStringProperty(ruleName);
		this.description = new SimpleStringProperty(description);
		this.affectedElement1 = new SimpleStringProperty(affectedElement);
		this.affectedElement2 = new SimpleStringProperty(secondAffectedElement);
		this.structuralOptions1 = structuralOptions1;
		this.structuralOptions2 = structuralOptions2;
		initializeStructuralOptionsBooleans();
		setStructuralOptions1Booleans(structuralOptions1);
		setStructuralOptions2Booleans(structuralOptions2);
		this.constraintRepresentation1 = new SimpleStringProperty(constraintRepresentation);
		this.constraintRepresentation2 = new SimpleStringProperty(
				secondConstraintRepresentation);
		this.isValid = new SimpleBooleanProperty(isValid);
		createUUID();
	}

	public OTConstraint(String ruleName, String description,
			CVSegmentOrNaturalClass affectedElementSegOrNC,
			CVSegmentOrNaturalClass secondAffectedElementSegOrNC, int structuralOptions1,
			int structuralOptions2, String constraintRepresentation,
			String secondConstraintRepresentation, boolean isValid) {
		super();
		this.constraintName = new SimpleStringProperty(ruleName);
		this.description = new SimpleStringProperty(description);
		this.affectedElement1 = new SimpleStringProperty("");
		this.affectedElement2 = new SimpleStringProperty("");
		this.affectedElementSegOrNC1 = affectedElementSegOrNC;
		this.affectedElementSegOrNC2 = secondAffectedElementSegOrNC;
		this.structuralOptions1 = structuralOptions1;
		this.structuralOptions2 = structuralOptions2;
		initializeStructuralOptionsBooleans();
		setStructuralOptions1Booleans(structuralOptions1);
		setStructuralOptions2Booleans(structuralOptions2);
		this.constraintRepresentation1 = new SimpleStringProperty(constraintRepresentation);
		this.constraintRepresentation2 = new SimpleStringProperty(
				secondConstraintRepresentation);
		this.isValid = new SimpleBooleanProperty(isValid);
		createUUID();
	}

	protected void initializeStructuralOptionsBooleans() {
		isWordInitial1 = new SimpleBooleanProperty(false);
		isOnset1 = new SimpleBooleanProperty(false);
		isNucleus1 = new SimpleBooleanProperty(false);
		isCoda1 = new SimpleBooleanProperty(false);
		isUnparsed1 = new SimpleBooleanProperty(false);
		isWordFinal1 = new SimpleBooleanProperty(false);
		isWordInitial2 = new SimpleBooleanProperty(false);
		isOnset2 = new SimpleBooleanProperty(false);
		isNucleus2 = new SimpleBooleanProperty(false);
		isCoda2 = new SimpleBooleanProperty(false);
		isUnparsed2 = new SimpleBooleanProperty(false);
		isWordFinal2 = new SimpleBooleanProperty(false);
	}

	protected void setStructuralOptions1Booleans(int structuralOptions) {
		if ((structuralOptions & OTStructuralOptions.WORD_INITIAL) > 0)
			isWordInitial1.set(true);
		else
			isWordInitial1.set(false);
		if ((structuralOptions & OTStructuralOptions.ONSET) > 0)
			isOnset1.set(true);
		else
			isOnset1.set(false);
		if ((structuralOptions & OTStructuralOptions.NUCLEUS) > 0)
			isNucleus1.set(true);
		else
			isNucleus1.set(false);
		if ((structuralOptions & OTStructuralOptions.CODA) > 0)
			isCoda1.set(true);
		else
			isCoda1.set(false);
		if ((structuralOptions & OTStructuralOptions.UNPARSED) > 0)
			isUnparsed1.set(true);
		else
			isUnparsed1.set(false);
		if ((structuralOptions & OTStructuralOptions.WORD_FINAL) > 0)
			isWordFinal1.set(true);
		else
			isWordFinal1.set(false);
	}

	protected void setStructuralOptions2Booleans(int structuralOptions) {
		if ((structuralOptions & OTStructuralOptions.WORD_INITIAL) > 0)
			isWordInitial2.set(true);
		else
			isWordInitial2.set(false);
		if ((structuralOptions & OTStructuralOptions.ONSET) > 0)
			isOnset2.set(true);
		else
			isOnset2.set(false);
		if ((structuralOptions & OTStructuralOptions.NUCLEUS) > 0)
			isNucleus2.set(true);
		else
			isNucleus2.set(false);
		if ((structuralOptions & OTStructuralOptions.CODA) > 0)
			isCoda2.set(true);
		else
			isCoda2.set(false);
		if ((structuralOptions & OTStructuralOptions.UNPARSED) > 0)
			isUnparsed2.set(true);
		else
			isUnparsed2.set(false);
		if ((structuralOptions & OTStructuralOptions.WORD_FINAL) > 0)
			isWordFinal2.set(true);
		else
			isWordFinal2.set(false);
	}

	public String getConstraintName() {
		return constraintName.get();
	}

	public StringProperty constraintNameProperty() {
		return constraintName;
	}

	public void setConstraintName(String ruleName) {
		this.constraintName.set(ruleName);
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

	public String getAffectedElement1() {
		return affectedElement1.get();
	}

	public StringProperty affectedElement1Property() {
		return affectedElement1;
	}

	public void setAffectedElement1(String value) {
		this.affectedElement1.set(value);
	}

	@XmlElement(name = "affectedSegOrNC1")
	public CVSegmentOrNaturalClass getAffectedSegOrNC1() {
		return affectedElementSegOrNC1;
	}

	public void setAffectedSegOrNC1(CVSegmentOrNaturalClass affectedSegOrNC) {
		if (affectedSegOrNC == null) {
			this.affectedElementSegOrNC1 = null;
		} else {
			this.affectedElementSegOrNC1 = new CVSegmentOrNaturalClass(getAffectedElement1(),
					affectedSegOrNC.getDescription(), affectedSegOrNC.isSegment(),
					affectedSegOrNC.getUuid(), affectedSegOrNC.isActive());
		}
	}

	public String getAffectedElement2() {
		return affectedElement2.get();
	}

	public StringProperty affectedElement2Property() {
		return affectedElement2;
	}

	public void setAffectedElement2(String value) {
		this.affectedElement2.set(value);
	}

	@XmlElement(name = "affectedSegOrNC2")
	public CVSegmentOrNaturalClass getAffectedSegOrNC2() {
		return affectedElementSegOrNC2;
	}

	public void setAffectedSegOrNC2(CVSegmentOrNaturalClass affectedSegOrNC) {
		if (affectedSegOrNC == null) {
			this.affectedElementSegOrNC2 = null;
		} else {
			this.affectedElementSegOrNC2 = new CVSegmentOrNaturalClass(getAffectedElement2(),
					affectedSegOrNC.getDescription(), affectedSegOrNC.isSegment(),
					affectedSegOrNC.getUuid(), affectedSegOrNC.isActive());
		}
	}

	@XmlAttribute(name = "structuralOptions1")
	public int getStructuralOptions1() {
		return structuralOptions1;
	}

	public void setStructuralOptions1(int structuralOptions) {
		this.structuralOptions1 = structuralOptions;
	}

	public int getStructuralOptions2() {
		return structuralOptions2;
	}

	public void setStructuralOptions2(int structuralOptions) {
		this.structuralOptions2 = structuralOptions;
	}

	public boolean isWordInitial1() {
		return isWordInitial1.get();
	}

	public BooleanProperty isWordInitial1Property() {
		return isWordInitial1;
	}

	public void setIsWordInitial1(boolean value) {
		this.isWordInitial1.set(value);
	}

	public boolean isOnset1() {
		return isOnset1.get();
	}

	public BooleanProperty isOnset1Property() {
		return isOnset1;
	}

	public void setIsOnset1(boolean value) {
		this.isOnset1.set(value);
	}

	public boolean isNucleus1() {
		return isNucleus1.get();
	}

	public BooleanProperty isNucleus1Property() {
		return isNucleus1;
	}

	public void setIsNucleus1(boolean value) {
		this.isNucleus1.set(value);
	}

	public boolean isCoda1() {
		return isCoda1.get();
	}

	public BooleanProperty isCoda1Property() {
		return isCoda1;
	}

	public void setIsCoda1(boolean value) {
		this.isCoda1.set(value);
	}

	public boolean isUnparsed1() {
		return isUnparsed1.get();
	}

	public BooleanProperty isUnparsed1Property() {
		return isUnparsed1;
	}

	public void setIsUnparsed1(boolean value) {
		this.isUnparsed1.set(value);
	}

	public boolean isWordFinal1() {
		return isWordFinal1.get();
	}

	public BooleanProperty isWordFinal1Property() {
		return isWordFinal1;
	}

	public void setIsWordFinal1(boolean value) {
		this.isWordFinal1.set(value);
	}

	public boolean isWordInitial2() {
		return isWordInitial2.get();
	}

	public BooleanProperty isWordInitial2Property() {
		return isWordInitial2;
	}

	public void setIsWordInitial2(boolean value) {
		this.isWordInitial2.set(value);
	}

	public boolean isOnset2() {
		return isOnset2.get();
	}

	public BooleanProperty isOnset2Property() {
		return isOnset2;
	}

	public void setIsOnset2(boolean value) {
		this.isOnset2.set(value);
	}

	public boolean isNucleus2() {
		return isNucleus2.get();
	}

	public BooleanProperty isNucleus2Property() {
		return isNucleus2;
	}

	public void setIsNucleus2(boolean value) {
		this.isNucleus2.set(value);
	}

	public boolean isCoda2() {
		return isCoda2.get();
	}

	public BooleanProperty isCoda2Property() {
		return isCoda2;
	}

	public void setIsCoda2(boolean value) {
		this.isCoda2.set(value);
	}

	public boolean isUnparsed2() {
		return isUnparsed2.get();
	}

	public BooleanProperty isUnparsed2Property() {
		return isUnparsed2;
	}

	public void setIsUnparsed2(boolean value) {
		this.isUnparsed2.set(value);
	}

	public boolean isWordFinal2() {
		return isWordFinal2.get();
	}

	public BooleanProperty isWordFinal2Property() {
		return isWordFinal2;
	}

	public void setIsWordFinal2(boolean value) {
		this.isWordFinal2.set(value);
	}

	public String getConstraintRepresentation1() {
		return constraintRepresentation1.get();
	}

	public StringProperty constraintRepresentationProperty() {
		return constraintRepresentation1;
	}

	public void setConstraintRepresentation(String representation) {
		this.constraintRepresentation1.set(representation);
	}

	public String getConstraintRepresentation2() {
		return constraintRepresentation2.get();
	}

	public StringProperty getConstraintRepresentation2Property() {
		return constraintRepresentation2;
	}

	/**
	 * @return the lingTreeDescription
	 */
	public String getLingTreeDescription() {
		return lingTreeDescription;
	}

	/**
	 * @param lingTreeDescription the lingTreeDescription to set
	 */
	public void setLingTreeDescription(String lingTreeDescription) {
		this.lingTreeDescription = lingTreeDescription;
	}

	public void setConstraintRepresentation2(StringProperty representation) {
		this.constraintRepresentation2 = representation;
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


	public String createLingTreeDescription() {
		if (!isValid()) {
			setConstraintRepresentation("");
			return "";
		}
		 StringBuilder sb = new StringBuilder();
		// switch (ruleAction) {
		// case ATTACH:
		// switch (ruleLevel) {
		// case ALL:
		// break;
		// case N:
		// // diphthong
		// sb.append("(N(\\L ");
		// sb.append(secondAffectedElementSegOrNC.getSegmentOrNaturalClass());
		// sb.append(")(\\L ");
		// sb.append(getAffectedValueForLingTree());
		// sb.append("))");
		// break;
		// case N_BAR:
		// // coda
		// sb.append("(N'(N(\\L ");
		// sb.append(secondAffectedElementSegOrNC.getSegmentOrNaturalClass());
		// sb.append("))(\\L ");
		// sb.append(getAffectedValueForLingTree());
		// sb.append("))");
		// break;
		// case N_DOUBLE_BAR:
		// // onset
		// sb.append("(N''(\\L ");
		// sb.append(getAffectedValueForLingTree());
		// sb.append(")(N'(N(\\L ");
		// sb.append(secondAffectedElementSegOrNC.getSegmentOrNaturalClass());
		// sb.append("))))");
		// break;
		// default:
		// break;
		// }
		// break;
		// case AUGMENT:
		// switch (ruleLevel) {
		// case ALL:
		// break;
		// case N:
		// // diphthong
		// sb.append("(N(\\L ");
		// sb.append(secondAffectedElementSegOrNC.getSegmentOrNaturalClass());
		// sb.append(")(\\L ");
		// sb.append(getAffectedValueForLingTree());
		// sb.append("))");
		// break;
		// case N_BAR:
		// // coda
		// sb.append("(N'(\\L ");
		// sb.append(secondAffectedElementSegOrNC.getSegmentOrNaturalClass());
		// sb.append(")(\\L ");
		// sb.append(getAffectedValueForLingTree());
		// sb.append("))");
		// break;
		// case N_DOUBLE_BAR:
		// // onset
		// sb.append("(N''(\\L ");
		// sb.append(getAffectedValueForLingTree());
		// sb.append(")(\\L ");
		// sb.append(secondAffectedElementSegOrNC.getSegmentOrNaturalClass());
		// sb.append("))");
		// break;
		// default:
		// break;
		// }
		// break;
		// case BUILD:
		// sb.append("(N''(N'(N(\\L ");
		// sb.append(getAffectedValueForLingTree());
		// sb.append("))))");
		// break;
		// case LEFT_ADJOIN:
		// switch (ruleLevel) {
		// case ALL:
		// break;
		// case N:
		// sb.append("(N(N(\\L ");
		// sb.append(getAffectedValueForLingTree());
		// sb.append("))(N(\\L ");
		// sb.append(secondAffectedElementSegOrNC.getSegmentOrNaturalClass());
		// sb.append(")))");
		// break;
		// case N_BAR:
		// sb.append("(N'(N'(\\L ");
		// sb.append(getAffectedValueForLingTree());
		// sb.append("))(N'(\\L ");
		// sb.append(secondAffectedElementSegOrNC.getSegmentOrNaturalClass());
		// sb.append(")))");
		// break;
		// case N_DOUBLE_BAR:
		// sb.append("(N''(N''(\\L ");
		// sb.append(getAffectedValueForLingTree());
		// sb.append("))(N''(\\L ");
		// sb.append(secondAffectedElementSegOrNC.getSegmentOrNaturalClass());
		// sb.append(")))");
		// break;
		// default:
		// break;
		// } break;
		// case RIGHT_ADJOIN:
		// switch (ruleLevel) {
		// case ALL:
		// break;
		// case N:
		// sb.append("(N(N(\\L ");
		// sb.append(secondAffectedElementSegOrNC.getSegmentOrNaturalClass());
		// sb.append("))(N(\\L ");
		// sb.append(getAffectedValueForLingTree());
		// sb.append(")))");
		// break;
		// case N_BAR:
		// sb.append("(N'(N'(\\L ");
		// sb.append(secondAffectedElementSegOrNC.getSegmentOrNaturalClass());
		// sb.append("))(N'(\\L ");
		// sb.append(getAffectedValueForLingTree());
		// sb.append(")))");
		// break;
		// case N_DOUBLE_BAR:
		// sb.append("(N''(N''(\\L ");
		// sb.append(secondAffectedElementSegOrNC.getSegmentOrNaturalClass());
		// sb.append("))(N''(\\L ");
		// sb.append(getAffectedValueForLingTree());
		// sb.append(")))");
		// break;
		// default:
		// break;
		// }
		// break;
		// default:
		// break;
		// }
		 setConstraintRepresentation(sb.toString());
		 return sb.toString();
	}

	protected String getAffectedValueForLingTree() {
		StringBuilder sb = new StringBuilder();
		sb.append(affectedElementSegOrNC1.getSegmentOrNaturalClass());
		return sb.toString();
	}

	public String adjustForAffectedSVG(String svg) {
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
		String affectedValue1 = "";
		if (affectedElementSegOrNC1 != null)
			affectedValue1 = affectedElementSegOrNC1.getUuid();
		String affectedValue2 = "";
		if (affectedElementSegOrNC2 != null)
			affectedValue2 = affectedElementSegOrNC2.getUuid();
		String sCombo = id + constraintName.getValueSafe() + description.getValueSafe()
				+ affectedValue1 + affectedValue2 + structuralOptions1 + structuralOptions2 + isValid() + isActive();
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
		OTConstraint constraint = (OTConstraint) obj;
		if (!id.equals(constraint.getID())) {
			result = false;
		} else if (!getConstraintName().equals(constraint.getConstraintName())) {
			result = false;
		} else if (!getDescription().equals(constraint.getDescription())) {
			result = false;
		} else if (!CVSegmentOrNaturalClass.equalsCVSegOrNC(affectedElementSegOrNC1,
				constraint.getAffectedSegOrNC1())) {
			result = false;
		} else if (!CVSegmentOrNaturalClass.equalsCVSegOrNC(affectedElementSegOrNC2,
				constraint.getAffectedSegOrNC2())) {
			result = false;
		} else if (structuralOptions1 != constraint.structuralOptions1) {
			result = false;
		} else if (structuralOptions2 != constraint.structuralOptions2) {
			result = false;
		} else if (isActive() != constraint.isActive()) {
			result = false;
		} else if (isValid() != constraint.isValid()) {
			result = false;
		}
		return result;
	}

	@Override
	public String getSortingValue() {
		return getConstraintName();
	}
}
