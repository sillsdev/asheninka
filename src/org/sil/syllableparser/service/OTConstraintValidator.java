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
		if (element1 == null || element1.length() > 0) {
			isValid = validateStructuralOptions(constraint.getStructuralOptions1());
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
				isValid = validateStructuralOptions(constraint.getStructuralOptions2());
				if (!isValid) {
					errorMessageProperty = "otconstraint.message.element2.invalidoptions";
				} 
			} else if (constraint.getStructuralOptions2() > 0) {
				isValid = false;
				errorMessageProperty = "otconstraint.message.element2.optionsignored";
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
