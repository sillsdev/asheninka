// Copyright (c) 2016-2019 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVNaturalClassInSyllable;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.cvapproach.CVSyllable;
import org.sil.syllableparser.model.cvapproach.CVSyllablePattern;
import org.sil.syllableparser.model.cvapproach.CVTraceSyllabifierInfo;

import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 *         a Service Takes a sequence of natural classes and parses them into a
 *         sequence of syllables
 */
public class CVSyllabifier {

	private LanguageProject languageProject;
	private CVApproach cva;
	private List<Segment> activeSegmentInventory;
	private List<CVNaturalClass> activeNaturalClasses;
	private CVSegmenter segmenter;
	private CVNaturalClasser naturalClasser;
	private boolean fDoTrace = false;
	private List<CVTraceSyllabifierInfo> syllabifierTraceInfo = new ArrayList<CVTraceSyllabifierInfo>();

	private final List<CVSyllablePattern> activeCVPatterns;
	private List<List<CVNaturalClassInSyllable>> naturalClassListsInCurrentWord;

	LinkedList<CVSyllable> syllablesInCurrentWord = new LinkedList<CVSyllable>(
			Arrays.asList(new CVSyllable(null)));
	String sSyllabifiedWord;

	public CVSyllabifier(List<CVSyllablePattern> activeCVPatterns,
			List<List<CVNaturalClassInSyllable>> naturalClassesInWord) {
		super();
		this.activeCVPatterns = activeCVPatterns;
		this.naturalClassListsInCurrentWord = naturalClassesInWord;
		sSyllabifiedWord = "";
	}

	public CVSyllabifier(CVApproach cva) {
		super();
		this.cva = cva;
		languageProject = cva.getLanguageProject();
		activeSegmentInventory = languageProject.getActiveSegmentsInInventory();
		activeNaturalClasses = cva.getActiveCVNaturalClasses();
		activeCVPatterns = cva.getActiveCVSyllablePatterns();
		segmenter = new CVSegmenter(languageProject.getActiveGraphemes(),
				languageProject.getActiveGraphemeNaturalClasses());
		naturalClasser = new CVNaturalClasser(activeNaturalClasses);
		sSyllabifiedWord = "";
	}

	public List<CVSyllable> getSyllablesInCurrentWord() {
		return syllablesInCurrentWord;
	}

	public void setSyllablesInCurrentWord(LinkedList<CVSyllable> syllablesInCurrentWord) {
		this.syllablesInCurrentWord = syllablesInCurrentWord;
	}

	public List<CVTraceSyllabifierInfo> getSyllabifierTraceInfo() {
		return syllabifierTraceInfo;
	}

	public List<CVSyllablePattern> getActiveCVPatterns() {
		return activeCVPatterns;
	}

	public boolean isDoTrace() {
		return fDoTrace;
	}

	public void setDoTrace(boolean fDoTrace) {
		this.fDoTrace = fDoTrace;
	}

	public boolean convertNaturalClassesToSyllables() {
		syllablesInCurrentWord.clear();
		syllabifierTraceInfo.clear();

		// recursively parse into syllables
		boolean result = parseIntoSyllables(naturalClassListsInCurrentWord, activeCVPatterns,
				syllabifierTraceInfo, true);

		if (result) {
			// the list of syllables found is in reverse order; flip them
			Collections.reverse(syllablesInCurrentWord);
		}
		return result;
	}

	public boolean convertStringToSyllables(String word) {
		syllablesInCurrentWord.clear();
		syllabifierTraceInfo.clear();
		boolean fSuccess = false;
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		fSuccess = segResult.success;
		if (fSuccess) {
			List<CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
			CVNaturalClasserResult ncResult = naturalClasser
					.convertSegmentsToNaturalClasses(segmentsInWord);
			fSuccess = ncResult.success;
			if (fSuccess) {
				naturalClassListsInCurrentWord = naturalClasser.getNaturalClassListsInCurrentWord();
				fSuccess = parseIntoSyllables(naturalClassListsInCurrentWord, activeCVPatterns,
						syllabifierTraceInfo, true);

				if (fSuccess) {
					// the list of syllables found is in reverse order; flip
					// them
					Collections.reverse(syllablesInCurrentWord);
				}
			}
		}
		return fSuccess;
	}

	private boolean parseIntoSyllables(List<List<CVNaturalClassInSyllable>> naturalClassesInWord,
			List<CVSyllablePattern> activePatterns, List<CVTraceSyllabifierInfo> sylTraceInfo,
			Boolean isWordInitial) {
		if (naturalClassesInWord.size() == 0) {
			return true;
		}
		CVTraceSyllabifierInfo sylInfo = new CVTraceSyllabifierInfo("");
		for (CVSyllablePattern pattern : activePatterns) {
			if (fDoTrace) {
				sylInfo = new CVTraceSyllabifierInfo(pattern.getNCSRepresentation());
				sylTraceInfo.add(sylInfo);
			}
			if (pattern.isWordInitial() && !isWordInitial) {
				continue;
			}
			CVSyllabifierResult result = naturalClassesMatchSyllablePattern(naturalClassesInWord,
					pattern);
			if (result.success) {
				List<List<CVNaturalClassInSyllable>> remainingNaturalClassesInWord = naturalClassesInWord
						.subList(pattern.getNCs().size(), naturalClassesInWord.size());
				if (fDoTrace) {
					sylInfo.syllablePatternMatched = true;
				}
				if (parseIntoSyllables(remainingNaturalClassesInWord, activePatterns,
						sylInfo.daughterInfo, false)) {
					CVSyllable syl = new CVSyllable(result.getNaturalClassesInSyllable());
					syllablesInCurrentWord.add(syl);
					if (fDoTrace) {
						sylInfo.parseWasSuccessful = true;
					}
					return true;
				}
			}
		}
		return false;
	}

	private CVSyllabifierResult naturalClassesMatchSyllablePattern(
			List<List<CVNaturalClassInSyllable>> naturalClassesInWord,
			CVSyllablePattern currentCVPattern) {
		CVSyllabifierResult result = new CVSyllabifierResult();
		result.success = false;
		// TODO: is there another, better way to compare the contents of two
		// lists?
		ObservableList<CVNaturalClass> naturalClassesInCVPattern = currentCVPattern.getNCs();
		if (naturalClassesInCVPattern.size() > naturalClassesInWord.size()) {
			return result;
		}
		Iterator<List<CVNaturalClassInSyllable>> currentNaturalClass = naturalClassesInWord
				.iterator();
		List<CVNaturalClassInSyllable> ncInSyllable = currentNaturalClass.next();
		for (CVNaturalClass ncInPattern : naturalClassesInCVPattern) {
			boolean fClassInPatternMatchesClassInList = false;
			for (CVNaturalClassInSyllable ncPossibility : ncInSyllable) {
				if (ncPossibility.getNaturalClass().equals(ncInPattern)) {
					fClassInPatternMatchesClassInList = true;
					result.getNaturalClassesInSyllable().add(ncPossibility);
					break;
				}
			}
			if (!fClassInPatternMatchesClassInList) {
				return result;
			}
			if (currentNaturalClass.hasNext()) {
				ncInSyllable = currentNaturalClass.next();
			} else {
				break;
			}
		}
		if (currentCVPattern.isWordFinal()
				&& result.naturalClassesInSyllable.size() < naturalClassesInWord.size()) {
			// not word final; return false result
			return result;
		}
		result.success = true;
		return result;
	}

	public String getSyllabificationOfCurrentWord() {
		// TODO: figure out a lambda way to do this
		StringBuilder sb = new StringBuilder();
		int iSize = syllablesInCurrentWord.size();
		int i = 1;
		for (CVSyllable syl : syllablesInCurrentWord) {
			for (CVNaturalClassInSyllable nc : syl.getNaturalClassesInSyllable()) {
				sb.append(nc.getSegmentInSyllable().getGrapheme());
			}
			if (i++ < iSize) {
				sb.append(".");
			}
		}
		return sb.toString();
	}

}
