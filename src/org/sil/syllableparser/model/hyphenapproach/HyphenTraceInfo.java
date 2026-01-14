/**
 * Copyright (c) 2025-2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.syllableparser.model.hyphenapproach;

import java.util.ArrayList;
import java.util.List;

import org.sil.syllableparser.model.TraceInfo;
import org.sil.syllableparser.service.parsing.CVSegmenter;
import org.sil.syllableparser.service.parsing.HyphenChangeRuleProcessor;
import org.sil.syllableparser.service.parsing.HyphenChangeRuleResult;
import org.sil.syllableparser.service.parsing.HyphenChangeRuleState;
import org.sil.syllableparser.service.parsing.HyphenClasser;
import org.sil.syllableparser.service.parsing.HyphenClasserResult;

/**
 * 
 */
public class HyphenTraceInfo extends TraceInfo {

	HyphenClasser hyphenClasser;
	HyphenChangeRuleProcessor hyphenProcessor;
	HyphenClasserResult hcResult;
	HyphenChangeRuleResult crResult;
	List<HyphenChangeRuleState> states = new ArrayList<HyphenChangeRuleState>();
	
	/**
	 * @param sWord
	 */
	public HyphenTraceInfo(String sWord) {
		super(sWord);
		// TODO Auto-generated constructor stub
	}

	public HyphenTraceInfo(String sWord, CVSegmenter segmenter, HyphenClasser hyphenClasser, HyphenChangeRuleProcessor hyphenProcessor) {
		super(sWord, segmenter);
		this.hyphenClasser = hyphenClasser;
		this.hyphenProcessor = hyphenProcessor;
	}

	public HyphenClasser getHyphenClasser() {
		return hyphenClasser;
	}

	public void setHyphenClasser(HyphenClasser hyphenClasser) {
		this.hyphenClasser = hyphenClasser;
	}

	public HyphenChangeRuleProcessor getHyphenProcessor() {
		return hyphenProcessor;
	}

	public void setHyphenProcessor(HyphenChangeRuleProcessor hyphenProcessor) {
		this.hyphenProcessor = hyphenProcessor;
	}

	public HyphenClasserResult getHyphenClasserResult() {
		return hcResult;
	}

	public void setHyphenClasserResult(HyphenClasserResult hcResult) {
		this.hcResult = hcResult;
	}

	public HyphenChangeRuleResult getHyphenChangeRuleResult() {
		return crResult;
	}

	public void setHyphenChangeRuleResult(HyphenChangeRuleResult crResult) {
		this.crResult = crResult;
	}

	public List<HyphenChangeRuleState> getStates() {
		return states;
	}

	public void setStates(List<HyphenChangeRuleState> states) {
		this.states = states;
	}
}
