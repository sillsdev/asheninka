/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service;

import org.sil.syllableparser.model.otapproach.OTConstraint;
import org.sil.syllableparser.model.otapproach.OTStructuralOptions;

/**
 * @author Andy Black
 *
 *         Singleton pattern for validation of NP rules
 */
public class OTConstraintValidator {

	private OTConstraint constraint;
	private boolean isValid = false;
	private String errorMessageProperty = "";

	private static OTConstraintValidator instance;

	public static OTConstraintValidator getInstance() {
		if (instance == null) {
			instance = new OTConstraintValidator();
		}
		return instance;
	}

	public OTConstraint getConstraint() {
		return constraint;
	}

	public void setConstraint(OTConstraint rule) {
		this.constraint = rule;
	};

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public String getErrorMessageProperty() {
		return errorMessageProperty;
	}

	public void setErrorMessageProperty(String errorMessageProperty) {
		this.errorMessageProperty = errorMessageProperty;
	}

	public void validate() {
		isValid = true;
		errorMessageProperty = "";
		String element1 = constraint.getAffectedElement1();
		String element2 = constraint.getAffectedElement2();
		int structuralOptions1 = constraint.getStructuralOptions1();
		int structuralOptions2 = constraint.getStructuralOptions2();
		if ((structuralOptions2 & OTStructuralOptions.WORD_INITIAL) > 0) {
			isValid = false;
			errorMessageProperty = "otconstraint.message.element2.nowordinitial";
			return;
		}
		if (element1 == null || element1.length() > 0) {
			isValid = validateStructuralOptions(structuralOptions1);
			if (!isValid) {
				isValid = false;
				errorMessageProperty = "otconstraint.message.element1.invalidoptions";
			}
		} else {
			isValid = false;
			errorMessageProperty = "otconstraint.message.element1.cannotbeempty";
		}
		if (isValid) {
			if (element2 == null || element2.length() > 0) {
				isValid = validateStructuralOptions(structuralOptions2);
				if (!isValid) {
					errorMessageProperty = "otconstraint.message.element2.invalidoptions";
				} else if ((structuralOptions1 & OTStructuralOptions.WORD_FINAL) > 0) {
					isValid = false;
					errorMessageProperty = "otconstraint.message.element1.nowordfinal";
				}
			} else if (structuralOptions2 > 0) {
				isValid = false;
				if (structuralOptions2 == OTStructuralOptions.WORD_FINAL) {
					errorMessageProperty = "otconstraint.message.element2.wordfinalignored";
				} else {
					errorMessageProperty = "otconstraint.message.element2.optionsignored";
				}
			}
		}
	}

	protected boolean validateStructuralOptions(int structuralOptions) {
		int result;
		int o = structuralOptions & OTStructuralOptions.ONSET;
		int n = structuralOptions & OTStructuralOptions.NUCLEUS;
		int c = structuralOptions & OTStructuralOptions.CODA;
		int u = structuralOptions & OTStructuralOptions.UNPARSED;
		result = o + n + c + u;
		return (result > 0);
	}

}
