/**
 * Copyright (c) 2025-2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.syllableparser.service.parsing;

import java.util.List;

import org.sil.syllableparser.model.hyphenapproach.HyphenChangeRule;
import org.sil.syllableparser.model.hyphenapproach.HyphenClassInWord;

/**
 * 
 */
public class HyphenChangeRuleState {
	HyphenChangeRule rule;
	List<HyphenClassInWord> classesInWord;
	int classIndex;
	/**
	 * @param rule
	 * @param classesInWord
	 * @param classIndex
	 */
	public HyphenChangeRuleState(HyphenChangeRule rule, List<HyphenClassInWord> classesInWord, int classIndex) {
		super();
		this.rule = rule;
		this.classesInWord = classesInWord;
		this.classIndex = classIndex;
	}
	/**
	 * 
	 */
	public HyphenChangeRuleState() {
		// TODO Auto-generated constructor stub
	}
	public HyphenChangeRule getRule() {
		return rule;
	}
	public void setRule(HyphenChangeRule rule) {
		this.rule = rule;
	}
	public List<HyphenClassInWord> getClassesInWord() {
		return classesInWord;
	}
	public void setClassesInWord(List<HyphenClassInWord> classesInWord) {
		this.classesInWord = classesInWord;
	}
	public int getClassIndex() {
		return classIndex;
	}
	public void setClassIndex(int classIndex) {
		this.classIndex = classIndex;
	}

}
