// Copyright (c) 2025-2026 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.parsing;

import java.util.ArrayList;
import java.util.List;

import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.hyphenapproach.HyphenApproach;
import org.sil.syllableparser.model.hyphenapproach.HyphenChangeRule;
import org.sil.syllableparser.model.hyphenapproach.HyphenClass;
import org.sil.syllableparser.model.hyphenapproach.HyphenClassInWord;
import org.sil.syllableparser.model.hyphenapproach.HyphenTracingStep;

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
	private List<HyphenTracingStep> syllabifierTraceInfoList = new ArrayList<HyphenTracingStep>();
	String sSyllabifiedWord;

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


	public List<HyphenTracingStep> getSyllabifierTraceInfo() {
		return syllabifierTraceInfoList;
	}

	public boolean isDoTrace() {
		return fDoTrace;
	}

	public void setDoTrace(boolean fDoTrace) {
		this.fDoTrace = fDoTrace;
	}

	public HyphenChangeRuleResult applyChangeRules(List<HyphenClassInWord> classesInWord) {
		HyphenChangeRuleResult crResult = new HyphenChangeRuleResult();
		for (HyphenChangeRule rule : hyphenApproach.getHyphenChangeRules()) {
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
		hcState.setClassIndex(0);
		while (ruleMatches(hcState)) {
			applyRule(hcState);
		}

		return true;
	}

	public boolean ruleMatches(HyphenChangeRuleState hcState) {
		ObservableList<HyphenClass> classesToMatch = hcState.rule.getMatchHyphenClasses();
		int ciwLast = hcState.getClassIndex();
		if (classesToMatch.size() == 1 && ciwLast != -1) {
			ciwLast++;
		}
		int ciwStart = Math.max(0, ciwLast);
		List<HyphenClassInWord> classesInWord = hcState.classesInWord;
		if (hcState.getClassIndex() >= classesInWord.size()) {
			return false;
		}
		HyphenClass hcInMatch = classesToMatch.get(0);
		for (int iWord = ciwStart; iWord < classesInWord.size(); iWord++) {
			HyphenClassInWord hciw = classesInWord.get(iWord);
			if (!hciw.getClassID().equals(hcInMatch.getID())) {
				continue;
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
		hcState.setClassesInWord(classesInWord);
	}

	public boolean convertStringToHyphenatedForm(String word) {
		sSyllabifiedWord = "";
		syllabifierTraceInfoList.clear();
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

//	public boolean syllabify(List<? extends CVSegmentInSyllable> segmentsInWord) {
//		syllablesInCurrentWord.clear();
//		syllabifierTraceInfoList.clear();
//		SHTracingStep traceInfo = null;
//		boolean fLastStartedSyllable = true;
//		int segmentCount = segmentsInWord.size();
//		if (segmentCount == 0) {
//			return false;
//		}
//		SHSyllable syl = new SHSyllable(new ArrayList<CVSegmentInSyllable>());
//		syl.add(segmentsInWord.get(0));
//		Segment seg1 = segmentsInWord.get(0).getSegment();
//		HyphenClass natClass = hyphenApproach.getNaturalClassContainingSegment(seg1);
//		if (natClass == null) {
//			if (fDoTrace) {
//				traceInfo = new SHTracingStep(seg1, null, null, null, SHComparisonResult.MISSING1);
//				syllabifierTraceInfoList.add(traceInfo);
//			}
//			return false;
//		}
//		int i = 1;
//		while (i < segmentCount) {
//			seg1 = segmentsInWord.get(i - 1).getSegment();
//			Segment seg2 = segmentsInWord.get(i).getSegment();
//			SHComparisonResult result = sonorityComparer.compare(seg1, seg2);
//			if (fDoTrace) {
//				traceInfo = new SHTracingStep(seg1,
//						hyphenApproach.getNaturalClassContainingSegment(seg1), seg2,
//						hyphenApproach.getNaturalClassContainingSegment(seg2), result);
//				if (fLastStartedSyllable) {
//					traceInfo.startsSyllable = true;
//					fLastStartedSyllable = false;
//				}
//				syllabifierTraceInfoList.add(traceInfo);
//			}
//			if (result == SHComparisonResult.MORE) {
//				int j = i + 1;
//				if (j < segmentCount) {
//					Segment seg3 = segmentsInWord.get(j).getSegment();
//					result = sonorityComparer.compare(seg2, seg3);
//					if (result == SHComparisonResult.EQUAL || result == SHComparisonResult.MORE) {
//						syl.add(segmentsInWord.get(i));
//						i++;
//						if (fDoTrace) {
//							traceInfo = new SHTracingStep(seg2,
//									hyphenApproach.getNaturalClassContainingSegment(seg2), seg3,
//									hyphenApproach.getNaturalClassContainingSegment(seg3), result);
//							syllabifierTraceInfoList.add(traceInfo);
//						}
//					}
//					syl = endThisSyllableStartNew(segmentsInWord, syl, i);
//					fLastStartedSyllable = true;
//				} else {
//					syl.add(segmentsInWord.get(i));
//				}
//			} else if (result == SHComparisonResult.LESS) {
//				syl.add(segmentsInWord.get(i));
//			} else if (result == SHComparisonResult.EQUAL) {
//				syl = endThisSyllableStartNew(segmentsInWord, syl, i);
//				fLastStartedSyllable = true;
//			} else {
//				return false;
//			}
//			i++;
//		}
//		if (syl.getSegmentsInSyllable().size() > 0) {
//			syllablesInCurrentWord.add(syl);
//			if (fDoTrace && fLastStartedSyllable) {
//				Segment seg = segmentsInWord.get(segmentCount -1).getSegment();
//				traceInfo = new SHTracingStep(seg,
//						hyphenApproach.getNaturalClassContainingSegment(seg), null,
//						null, null);
//				traceInfo.startsSyllable = true;
//				syllabifierTraceInfoList.add(traceInfo);
//			}
//		}
//		return true;
//	}

	public String getSyllabificationOfCurrentWord(List<HyphenClassInWord> classesInWord) {
		// TODO: figure out a lambda way to do this
		StringBuilder sb = new StringBuilder();
		for (HyphenClassInWord ciw : classesInWord) {
			String classId = ciw.getHyphenClass().getID();
			if (classId.equals(hyphenApproach.getWordBoundaryHC().getID())) {
				continue;
			}
			if (classId.equals(hyphenApproach.getInsertHereHC().getID())) {
				sb.append(".");
				continue;
			}
			CVSegmentInSyllable seg = ciw.getSegInWord();
			sb.append(seg.getGrapheme());
		}
		return sb.toString();
	}

//	public String getNaturalClassesInCurrentWord() {
//		StringBuilder sb = new StringBuilder();
//		int iSize = syllabifierTraceInfoList.size();
//		for (int i = 0; i < iSize; i++) {
//			SHTracingStep info = syllabifierTraceInfoList.get(i);
//			if (i > 0) {
//				sb.append(", ");
//			}
////			sb.append(getNCName(info.naturalClass1));
//			if (i == iSize - 1) {
//				sb.append(", ");
////				sb.append(getNCName(info.naturalClass2));
//			}
//		}
//		return sb.toString();
//	}

//	private String getNCName(HyphenClass natClass) {
//		if (natClass == null) {
//			return "null";
//		} else {
//			return natClass.getClassName();
//		}
//	}

}
