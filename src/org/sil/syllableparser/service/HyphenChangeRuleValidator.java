/**
 * Copyright (c) 2025-2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service;

import java.util.ResourceBundle;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.hyphenapproach.HyphenChangeRule;
import org.sil.syllableparser.model.hyphenapproach.HyphenClass;

/**
 * @author Andy Black
 *
 *         Singleton pattern for validation of NP rules
 */
public class HyphenChangeRuleValidator {

	private HyphenChangeRule changeRule;
	private boolean isValid = false;
	private String errorMessage = "";
	private ResourceBundle bundle;
	private static HyphenChangeRuleValidator instance;

	public static HyphenChangeRuleValidator getInstance() {
		if (instance == null) {
			instance = new HyphenChangeRuleValidator();
		}
		return instance;
	}

	public HyphenChangeRule getChangeRule() {
		return changeRule;
	}

	public void setChangeRule(HyphenChangeRule rule) {
		this.changeRule = rule;
	};

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessageProperty) {
		this.errorMessage = errorMessageProperty;
	}

	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public boolean validate() {
		isValid = true;
		StringBuilder sb = new StringBuilder();
		if (!sizesCheck()) {
			sb.append(bundle.getString("hyphenerror.samenumberofclasses"));
			sb.append("\n");
			isValid = false;
		}
		// name and order the same between match and change
		if (!sameClassesInMatchAndChange()) {
			sb.append(bundle.getString("hyphenerror.samenamesandorder"));
			sb.append("\n");
			isValid = false;
		}
		// no hyphen in change, but more than one class in change
		if (!onlyOneHyphenInChange()) {
			sb.append(bundle.getString("hyphenerror.onlyoneinserthyphenperchange"));
			sb.append("\n");
			isValid = false;
		}
		// only one hyphen in change
		if (!oneHyphenInChange()) {
			sb.append(bundle.getString("hyphenerror.nohypheninchange"));
			sb.append("\n");
			isValid = false;
		}
		errorMessage = sb.toString();
		return isValid;
	}

	private boolean sizesCheck() {
		int iSizeMatches = changeRule.getMatchHyphenClasses().size();
		int iSizeChanges = changeRule.getChangeHyphenClasses().size();
		if (iSizeChanges < iSizeMatches || iSizeChanges > iSizeMatches + 1) {
			return false;
		}
		return true;
	}
	
	private boolean sameClassesInMatchAndChange() {
		// this test assumes there's one insert hyphen in the change
		if (changeRule.getChangeHyphenClasses().filtered(c -> c.getSegmentsRepresentation().equals(Constants.SPECIAL_INSERT_CODE)).size() != 1) {
			return true;
		}
		int iSizeMatches = changeRule.getMatchHyphenClasses().size();
		int iSizeChanges = changeRule.getChangeHyphenClasses().size();
		if (iSizeMatches != iSizeChanges - 1) {
			return false;
		}
		if (changeRule.getChangeHyphenClasses()
				.filtered(c -> c.getSegmentsRepresentation().equals(Constants.SPECIAL_INSERT_CODE)).size() == 1) {
			for (int i = 0, j = 0; i < iSizeChanges; i++) {
				HyphenClass changeClass = changeRule.getChangeHyphenClasses().get(i);
				if (changeClass.getSegmentsRepresentation().equals(Constants.SPECIAL_INSERT_CODE)) {
					continue;
				}
				if (j == changeRule.getMatchHyphenClasses().size()) {
					return false;
				}
				HyphenClass matchClass = changeRule.getMatchHyphenClasses().get(j);
				if (!changeClass.getClassName().equals(matchClass.getClassName())) {
					return false;
				}
				j++;
			}
		} else {
			if (iSizeChanges > 1 || iSizeMatches > 1) {
				return false;
			}
		}
		return true;
	}

	private boolean onlyOneHyphenInChange() {
		if (changeRule.getChangeHyphenClasses().filtered(c -> c.getSegmentsRepresentation().equals(Constants.SPECIAL_INSERT_CODE)).size() > 1) {
			return false;
		}
		return true;
	}

	private boolean oneHyphenInChange() {
		if (changeRule.getChangeHyphenClasses().filtered(c -> c.getSegmentsRepresentation().equals(Constants.SPECIAL_INSERT_CODE)).size() == 0
				&& changeRule.getChangeHyphenClasses().size() > 1) {
			return false;
		}
		return true;
	}

}
