/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service;

import org.sil.syllableparser.model.npapproach.NPRule;
import org.sil.syllableparser.model.npapproach.NPRuleAction;
import org.sil.syllableparser.model.npapproach.NPRuleLevel;

/**
 * @author Andy Black
 *
 *         Singleton pattern for validation of NP rules
 */
public class NPRuleValidator {

	private NPRule rule;
	private boolean isValid = false;
	private String errorMessageProperty = "";
	
	private static NPRuleValidator instance;

	public static NPRuleValidator getInstance() {
		if (instance == null) {
			instance = new NPRuleValidator();
		}
		return instance;
	}

	public NPRule getRule() {
		return rule;
	}

	public void setRule(NPRule rule) {
		this.rule = rule;
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
		isValid = validateBuildAction();
		if (isValid) {
			isValid = validateAttachAction();
		}
		if (isValid) {
			isValid = validateAugmentAction();
		}
		if (isValid) {
			isValid = validateAdjoinAction();
		}
	}

	protected boolean validateBuildAction() {
		// action Build must be level All with an affected item but not a context item
		boolean passes = true;
		if (rule.getRuleAction() == NPRuleAction.BUILD) {
			passes = ((rule.getRuleLevel() == NPRuleLevel.ALL)
					&& (rule.getAffectedSegOrNC() != null)
					&& (rule.getContextSegOrNC() == null));
			if (!passes) {
				errorMessageProperty = "nprule.message.buildall";
			} else {
				errorMessageProperty = "";
			}
		}
		return passes;
	}

	protected boolean validateAttachAction() {
		// action Attach cannot be level All and must have both an affected item and a context item
		boolean passes = true;
		if (rule.getRuleAction() == NPRuleAction.ATTACH) {
			passes = ((rule.getRuleLevel() != NPRuleLevel.ALL)
					&& (rule.getAffectedSegOrNC() != null)
					&& (rule.getContextSegOrNC() != null));
			if (!passes) {
				errorMessageProperty = "nprule.message.attach";
			} else {
				errorMessageProperty = "";
			}
		}
		return passes;
	}

	protected boolean validateAugmentAction() {
		// action Attach cannot be level All and must have both an affected item and a context item
		boolean passes = true;
		if (rule.getRuleAction() == NPRuleAction.AUGMENT) {
			passes = ((rule.getRuleLevel() != NPRuleLevel.ALL)
					&& (rule.getAffectedSegOrNC() != null)
					&& (rule.getContextSegOrNC() != null));
			if (!passes) {
				errorMessageProperty = "nprule.message.augment";
			} else {
				errorMessageProperty = "";
			}
		}
		return passes;
	}

	protected boolean validateAdjoinAction() {
		// action Left/Right adjoin cannot be level All and must have both an affected item and a context item
		boolean passes = true;
		if ((rule.getRuleAction() == NPRuleAction.LEFT_ADJOIN)
				|| (rule.getRuleAction() == NPRuleAction.RIGHT_ADJOIN)) {
			passes = ((rule.getRuleLevel() != NPRuleLevel.ALL)
					&& (rule.getAffectedSegOrNC() != null)
					&& (rule.getContextSegOrNC() != null));
			if (!passes) {
				errorMessageProperty = "nprule.message.adjoin";
			} else {
				errorMessageProperty = "";
			}
		}
		return passes;
	}

}
