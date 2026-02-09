/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.syllableparser.model.hyphenapproach;

import org.sil.syllableparser.Constants;

/**
 * 
 */
public class HyphenClassInChooser {

	HyphenClass hyphenClass;
	boolean doNotMatchClassAgain = false;
	/**
	 * @param hyphenClass
	 * @param doNotMatchClassAgain
	 */
	public HyphenClassInChooser(HyphenClass hyphenClass, boolean doNotMatchClassAgain) {
		this.hyphenClass = hyphenClass;
		this.doNotMatchClassAgain = doNotMatchClassAgain;
	}
	/**
	 * 
	 */
	public HyphenClassInChooser() {
		// TODO Auto-generated constructor stub
	}
	public HyphenClass getHyphenClass() {
		return hyphenClass;
	}
	public void setHyphenClass(HyphenClass hyphenClass) {
		this.hyphenClass = hyphenClass;
	}
	public boolean isDoNotMatchClassAgain() {
		return doNotMatchClassAgain;
	}
	public void setDoNotMatchClassAgain(boolean doNotMatchClassAgain) {
		this.doNotMatchClassAgain = doNotMatchClassAgain;
	}

	public String getSegmentsRepresentation() {
		return hyphenClass.getSegmentsRepresentation();
	}
	
	public String getClassName() {
		StringBuilder sb = new StringBuilder();
		if (doNotMatchClassAgain) {
			sb.append(Constants.HYPHEN_DO_NOT_MATCH_AGAIN_SYMBOL);
			sb.append(hyphenClass.getClassName().toLowerCase());
		} else {
			sb.append(hyphenClass.getClassName());
		}
		return sb.toString();
	}
	
	public String getDescription() {
		return hyphenClass.getDescription();
	}
	
	public void setDescription(String value) {
		hyphenClass.setDescription(value);
	}
	
	public boolean isActive() {
		return hyphenClass.isActive();
	}
	
	public String getID() {
		return hyphenClass.getID();
	}
	
	public String getClassTextForComboBox() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClassName());
		sb.append(" - ");
		sb.append(getDescription());
		return sb.toString();
	}
}
