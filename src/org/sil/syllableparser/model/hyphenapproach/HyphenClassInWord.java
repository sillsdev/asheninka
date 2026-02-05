/**
 * Copyright (c) 2025-2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.syllableparser.model.hyphenapproach;

import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;

/**
 * 
 */
public class HyphenClassInWord {

	private HyphenClass hyphenClass;
	private CVSegmentInSyllable segInWord;
	private boolean doNotMatchClassAgain;
	 
	public HyphenClassInWord() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param hyphenClass
	 * @param segInWord
	 */
	public HyphenClassInWord(HyphenClass hyphenClass, CVSegmentInSyllable segInWord) {
		super();
		this.hyphenClass = hyphenClass;
		this.segInWord = segInWord;
	}

	public HyphenClass getHyphenClass() {
		return hyphenClass;
	}

	public void setHyphenClass(HyphenClass hyphenClass) {
		this.hyphenClass = hyphenClass;
	}

	public CVSegmentInSyllable getSegInWord() {
		return segInWord;
	}

	public void setSegInWord(CVSegmentInSyllable segInWord) {
		this.segInWord = segInWord;
	}

	public String getClassID() {
		return hyphenClass.getID();
	}

	public String getClassName() {
		return hyphenClass.getClassName();
	}

	public boolean isDoNotMatchClassAgain() {
		return doNotMatchClassAgain;
	}

	public void setDoNotMatchClassAgain(boolean doNotMatchClassAgain) {
		this.doNotMatchClassAgain = doNotMatchClassAgain;
	}
}
