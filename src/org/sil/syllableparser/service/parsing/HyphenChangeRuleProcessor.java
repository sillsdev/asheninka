// Copyright (c) 2025-2026 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.parsing;

import java.util.ArrayList;
import java.util.List;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.hyphenapproach.HyphenApproach;
import org.sil.syllableparser.model.hyphenapproach.HyphenChangeRule;
import org.sil.syllableparser.model.hyphenapproach.HyphenClass;
import org.sil.syllableparser.model.hyphenapproach.HyphenClassInWord;
import org.sil.syllableparser.model.hyphenapproach.HyphenTraceInfo;

import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 *         a Service Takes a sequence of natural classes and parses them into a
 *         sequence of syllables
 */
public class HyphenChangeRuleProcessor {

	private LanguageProject languageProject;
	private HyphenApproach hyphenApproach;
	private CVSegmenter segmenter;
	HyphenClasser hyphenClasser;
	private boolean fDoTrace = false;
	HyphenTraceInfo traceInfo = new HyphenTraceInfo("");
	String sSyllabifiedWord;
	private List<HyphenChangeRuleState> stateHistory = new ArrayList<HyphenChangeRuleState>();

	public HyphenChangeRuleProcessor(HyphenApproach hyphenApproach) {
		super();
		this.hyphenApproach = hyphenApproach;
		languageProject = hyphenApproach.getLanguageProject();
		segmenter = new CVSegmenter(languageProject.getActiveGraphemes(),
				languageProject.getActiveGraphemeNaturalClasses());
		hyphenClasser = new HyphenClasser(hyphenApproach);
		sSyllabifiedWord = "";
	}

	public String getSyllabifiedWord() {
		return sSyllabifiedWord;
	}

	public void setSyllabifiedWord(String sSyllabifiedWord) {
		this.sSyllabifiedWord = sSyllabifiedWord;
	}


	public boolean isDoTrace() {
		return fDoTrace;
	}

	public void setDoTrace(boolean fDoTrace) {
		this.fDoTrace = fDoTrace;
	}

	public HyphenTraceInfo getTraceInfo() {
		return traceInfo;
	}

	public void setTraceInfo(HyphenTraceInfo traceInfo) {
		this.traceInfo = traceInfo;
	}

	public List<HyphenChangeRuleState> getStateHistory() {
		return stateHistory;
	}

	public void setStateHistory(List<HyphenChangeRuleState> stateHistory) {
		this.stateHistory = stateHistory;
	}

	public HyphenChangeRuleResult applyChangeRules(List<HyphenClassInWord> classesInWord) {
		HyphenChangeRuleResult crResult = new HyphenChangeRuleResult();
		if (fDoTrace) {
			stateHistory.clear();
		}
		for (HyphenChangeRule rule : hyphenApproach.getActiveHyphenChangeRules()) {
			HyphenChangeRuleState hcState = new HyphenChangeRuleState(rule, classesInWord, -1);
			if (!tryRule(hcState)) {
				crResult.success = false;
				crResult.sHyphenation = getSyllabificationOfCurrentWord(classesInWord);
				return crResult;
			}
		}
		sSyllabifiedWord = getSyllabificationOfCurrentWord(classesInWord);
		crResult.sHyphenation = sSyllabifiedWord;
		return crResult;
	}

	boolean tryRule(HyphenChangeRuleState hcState) {
		hcState.setClassIndex(-1);
		while (ruleMatches(hcState)) {
			applyRule(hcState);
			if (fDoTrace) {
				traceInfo.getStates().add(hcState);
			}
		}
		return true;
	}

	public boolean ruleMatches(HyphenChangeRuleState hcState) {
		boolean isWordInitial = hcState.getRule().isWordInitial();
		boolean isWordFinal  = hcState.getRule().isWordFinal();
		ObservableList<HyphenClass> classesToMatch = hcState.rule.getMatchHyphenClasses();
		int ciwLast = hcState.getClassIndex();
		if (classesToMatch.size() == 1 && ciwLast != -1) {
			ciwLast++;
		}
		List<HyphenClassInWord> classesInWord = hcState.classesInWord;
		// Check for word initial or both word initial and final
		if (isWordInitial && ciwLast != -1
				|| (isWordInitial && isWordFinal && (classesInWord.size() - 2) != classesToMatch.size())) {
			hcState.setClassIndex(-1);
			return false;
		}
		int ciwStart = Math.max(0, ciwLast);
		if (hcState.getClassIndex() >= classesInWord.size()) {
			return false;
		}
		HyphenClass hcInMatch = classesToMatch.get(0);
		for (int iWord = ciwStart; iWord < classesInWord.size(); iWord++) {
			HyphenClassInWord hciw = classesInWord.get(iWord);
			if (isWordInitial) {
				if (iWord > 0 && iWord <= classesToMatch.size()) {
					if (!hciw.getClassID().equals(hcInMatch.getID())) {
						return false;
					}
				}
			}
			if (!hciw.getClassID().equals(hcInMatch.getID())) {
				continue;
			}
			if (isWordFinal) {
				if (iWord < classesInWord.size() - classesToMatch.size() - 1) {
					continue;
				}
			}
			// matches first hc in match
			int iMatch = classesToMatch.indexOf(hcInMatch);
			int iClassInWord = classesInWord.indexOf(hciw);
			boolean matchedAll = true;
			for (int i = iMatch + 1; i < classesToMatch.size(); i++) {
				if (++iClassInWord >= classesInWord.size()
						|| !classesToMatch.get(i).getID().equals(classesInWord.get(iClassInWord).getClassID())) {
					matchedAll = false;
					break;
				}
			}
			if (matchedAll) {
				hcState.setClassIndex(iClassInWord);
				return true;
			}
		}
		hcState.setClassIndex(-1);
		return false;
	}

	public void applyRule(HyphenChangeRuleState hcState) {
		HyphenChangeRule rule = hcState.getRule();
		List<HyphenClassInWord> classesInWord = hcState.getClassesInWord();
		List<HyphenClass> classesInChange = rule.getChangeHyphenClasses();
		int iStartInWord = hcState.getClassIndex() - rule.getMatchHyphenClasses().size() + 1;
		for (int iInWord = iStartInWord, jInChange = 0; iInWord > -1 && iInWord < classesInWord.size()
				&& jInChange < rule.getChangeHyphenClasses().size(); iInWord++, jInChange++) {
			HyphenClass hcChange = classesInChange.get(jInChange);
			if (hcChange.getID().equals(hyphenApproach.getInsertHereHC().getID())) {
				HyphenClassInWord insertHere = new HyphenClassInWord(hyphenApproach.getInsertHereHC(), null);
				classesInWord.add(iInWord, insertHere);
			} else {
				HyphenClassInWord ciwMatch = classesInWord.get(iInWord);
				HyphenClassInWord ciwChanged = new HyphenClassInWord(hcChange, ciwMatch.getSegInWord());
				classesInWord.set(iInWord, ciwChanged);
			}
		}
		if (fDoTrace) {
			HyphenChangeRuleState thisState = new HyphenChangeRuleState();
			List<HyphenClassInWord> thisStatesClasses = new ArrayList<HyphenClassInWord>(hcState.getClassesInWord());
			thisState.setClassesInWord(thisStatesClasses);
			thisState.setClassIndex(hcState.getClassIndex());
			thisState.setRule(hcState.getRule());
			stateHistory.add(thisState);
		}
	}

	public boolean convertStringToHyphenatedForm(String word) {
		sSyllabifiedWord = "";
		boolean fSuccess = false;
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		fSuccess = segResult.success;
		if (fSuccess) {
			List<? extends CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
			HyphenClasserResult hcResult = hyphenClasser.parseIntoHyphenClasses(segmentsInWord);
			fSuccess = hcResult.success;
			if (fSuccess) {
				List<HyphenClassInWord> classesInWord = hyphenClasser.getClassesInWord();
				HyphenChangeRuleResult crResult = applyChangeRules(classesInWord);
				fSuccess = crResult.success;
			}
		}
		return fSuccess;
	}

	public String getSyllabificationOfCurrentWord(List<HyphenClassInWord> classesInWord) {
		// TODO: figure out a lambda way to do this
		StringBuilder sb = new StringBuilder();
		for (HyphenClassInWord ciw : classesInWord) {
			String classId = ciw.getHyphenClass().getID();
			if (classId.equals(hyphenApproach.getWordBoundaryHC().getID())) {
				continue;
			}
			if (classId.equals(hyphenApproach.getInsertHereHC().getID())) {
				sb.append(Constants.SYLLABLE_BREAK_INDICATOR);
				continue;
			}
			CVSegmentInSyllable seg = ciw.getSegInWord();
			sb.append(seg.getGrapheme());
		}
		return sb.toString();
	}

}
