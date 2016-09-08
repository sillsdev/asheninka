// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.syllableparser.model.cvapproach.CVNaturalClassInSyllable;
import sil.org.syllableparser.model.cvapproach.CVSegmentInSyllable;
import sil.org.syllableparser.model.cvapproach.CVSyllable;
import sil.org.syllableparser.model.cvapproach.CVSyllablePattern;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 * a Service
 *         Takes a sequence of natural classes and parses them into a sequence
 *         of syllables
 */
public class CVSyllabifier {

	private LanguageProject languageProject;
	private CVApproach cva;
	private List<Segment> activeSegmentInventory;
	private List<CVNaturalClass> activeNaturalClasses;
	private CVSegmenter segmenter;
	private CVNaturalClasser naturalClasser;
	
	private final List<CVSyllablePattern> activeCVPatterns;
	private List<CVNaturalClassInSyllable> naturalClassesInCurrentWord;

	LinkedList<CVSyllable> syllablesInCurrentWord = new LinkedList<CVSyllable>(
			Arrays.asList(new CVSyllable(null)));
	String sSyllabifiedWord;

	public CVSyllabifier(List<CVSyllablePattern> activeCVPatterns,
			List<CVNaturalClassInSyllable> naturalClassesInCurrentWord) {
		super();
		this.activeCVPatterns = activeCVPatterns;
		this.naturalClassesInCurrentWord = naturalClassesInCurrentWord;
		sSyllabifiedWord = "";
	}

	public CVSyllabifier(CVApproach cva) {
		super();
		this.cva = cva;
		languageProject = cva.getLanguageProject();
		activeSegmentInventory = languageProject.getActiveSegmentsInInventory();
		activeNaturalClasses = cva.getActiveCVNaturalClasses();
		activeCVPatterns = cva.getActiveCVSyllablePatterns();
		segmenter = new CVSegmenter(activeSegmentInventory);
		naturalClasser = new CVNaturalClasser(activeNaturalClasses);
		sSyllabifiedWord = "";
	}
	
	public List<CVSyllable> getSyllablesInCurrentWord() {
		return syllablesInCurrentWord;
	}

	public void setSyllablesInCurrentWord(LinkedList<CVSyllable> syllablesInCurrentWord) {
		this.syllablesInCurrentWord = syllablesInCurrentWord;
	}

	public List<CVSyllablePattern> getActiveCVPatterns() {
		return activeCVPatterns;
	}

	public boolean convertNaturalClassesToSyllables() {
		syllablesInCurrentWord.clear();

		// recursively parse into syllables
		boolean result = parseIntoSyllables(naturalClassesInCurrentWord, activeCVPatterns, true);

		if (result) {
			// the list of syllables found is in reverse order; flip them
			Collections.reverse(syllablesInCurrentWord);
		}
		return result;
	}

	public boolean convertStringToSyllables(String word) {
		syllablesInCurrentWord.clear();	
		boolean fSuccess = false;
		fSuccess = segmenter.segmentWord(word);
		if (fSuccess) {
			List<CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
			fSuccess = naturalClasser.convertSegmentsToNaturalClasses(segmentsInWord);
			if (fSuccess) {
				naturalClassesInCurrentWord = naturalClasser
						.getNaturalClassesInCurrentWord();
				fSuccess = parseIntoSyllables(naturalClassesInCurrentWord, activeCVPatterns, true);

				if (fSuccess) {
					// the list of syllables found is in reverse order; flip them
					Collections.reverse(syllablesInCurrentWord);
				}
			}
		}
		return fSuccess;
	}

	private boolean parseIntoSyllables(List<CVNaturalClassInSyllable> naturalClassesInWord,
			List<CVSyllablePattern> activePatterns, Boolean isWordInitial) {
		if (naturalClassesInWord.size() == 0) {
			return true;
		}
		for (CVSyllablePattern pattern : activePatterns) {
			if (pattern.isWordInitial() && !isWordInitial) {
				continue;
			}
			if (naturalClassesMatchSyllablePattern(naturalClassesInWord, pattern)) {
				List<CVNaturalClassInSyllable> remainingNaturalClassesInWord = naturalClassesInWord
						.subList(pattern.getNCs().size(), naturalClassesInWord.size());
				if (parseIntoSyllables(remainingNaturalClassesInWord, activePatterns, false)) {
					List<CVNaturalClassInSyllable> naturalClassesInSyllable = naturalClassesInWord
							.subList(0, pattern.getNCs().size());
					CVSyllable syl = new CVSyllable(naturalClassesInSyllable);
					syllablesInCurrentWord.add(syl);
					return true;
				}
			}
		}
		return false;
	}

	private boolean naturalClassesMatchSyllablePattern(
			List<CVNaturalClassInSyllable> naturalClassesInWord, CVSyllablePattern currentCVPattern) {
		// TODO: is there another, better way to compare the contents of two
		// lists?
		ObservableList<CVNaturalClass> naturalClassesInCVPattern = currentCVPattern.getNCs();
		if (naturalClassesInCVPattern.size() > naturalClassesInWord.size()) {
			return false;
		}
		Iterator<CVNaturalClassInSyllable> currentNaturalClass = naturalClassesInWord.iterator();
		CVNaturalClassInSyllable ncInSyllable = currentNaturalClass.next();
		for (CVNaturalClass ncInPattern : naturalClassesInCVPattern) {
			if (!ncInSyllable.getNaturalClass().equals(ncInPattern)) {
				return false;
			}
			if (currentNaturalClass.hasNext()) {
				ncInSyllable = currentNaturalClass.next();
			} else {
				break;
			}
		}
		if (currentCVPattern.isWordFinal() && currentNaturalClass.hasNext()) {
			return false;
		}
		return true;
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
