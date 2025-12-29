/**
 * Copyright (c) 2025 SIL International
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

	public String getClassName() {
		return hyphenClass.getClassName();
	}
}
