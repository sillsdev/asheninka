// Copyright (c) 2019 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.sil.syllableparser.model.Environment;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.GraphemeNaturalClass;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.utility.StringUtilities;

/**
 * @author Andy Black
 *
 */
public class SyllabificationsComparer extends ApproachLanguageComparer {

	LanguageProject langProject;

	String dataSet1Info;

	SortedSet<Word> syllabificationsWhichDiffer = new TreeSet<>(
			Comparator.comparing(Word::getSortingValue));

	public SyllabificationsComparer(LanguageProject langProj) {
		super(langProj, langProj);
		langProject = langProj;
	}

	public LanguageProject getLanguageProject() {
		return langProject;
	}

	public SortedSet<Word> getSyllabificationsWhichDiffer() {
		return syllabificationsWhichDiffer;
	}

	public void compareSyllabifications() {
		// make sure all approaches have been syllabified
		List<Word> words = langProject.getWords();
		syllabifyWordsCV(words);
		syllabifyWordsSH(words);
		
		List<Word> diffs = words.stream().filter(w -> !w.getCVPredictedSyllabification().equals(w.getSHPredictedSyllabification())).collect(Collectors.toList());
		for (Word word: diffs) {
			syllabificationsWhichDiffer.add(word);
		}
	}

	protected void syllabifyWordsCV(List<Word> words) {
		CVSyllabifier stringSyllabifier = new CVSyllabifier(langProject.getCVApproach());
		for (Word word : words) {
			boolean fSuccess = stringSyllabifier.convertStringToSyllables(word.getWord());
			if (fSuccess) {
				word.setCVPredictedSyllabification(stringSyllabifier.getSyllabificationOfCurrentWord());
			}
		}
	}
	
	protected void syllabifyWordsSH(List<Word> words) {
		SHSyllabifier shSyllabifier = new SHSyllabifier(langProject.getSHApproach());
		for (Word word : words) {
			boolean fSuccess = shSyllabifier.convertStringToSyllables(word.getWord());
			if (fSuccess) {
				word.setSHPredictedSyllabification(shSyllabifier.getSyllabificationOfCurrentWord());
			}
		}
	}

	@Override
	public void compare() {
		// not used in this comparer
		
	}

	@Override
	protected void syllabifyWords(List<Word> words1, List<Word> words2) {
		// not used in this comparer
		
	}
}
