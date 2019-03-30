// Copyright (c) 2019 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.comparison;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.service.parsing.CVSyllabifier;
import org.sil.syllableparser.service.parsing.ONCSyllabifier;
import org.sil.syllableparser.service.parsing.SHSyllabifier;

/**
 * @author Andy Black
 *
 */
// We extend ApproachLanguageComparer to avoid duplicating code in the
// HTML formatting class
public class SyllabificationsComparer extends ApproachLanguageComparer {

	LanguageProject langProject;
	String dataSet1Info;
	boolean useCVApproach = true;
	boolean useSHApproach = true;
	boolean useONCApproach = true;

	public enum ApproachesToCompare {
		CV_SH, CV_ONC, CV_SH_ONC, SH_ONC,
	}

	ApproachesToCompare approachesToCompare = ApproachesToCompare.CV_SH_ONC;

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

	public boolean isUseCVApproach() {
		return useCVApproach;
	}

	public void setUseCVApproach(boolean useCVApproach) {
		this.useCVApproach = useCVApproach;
	}

	public boolean isUseSHApproach() {
		return useSHApproach;
	}

	public void setUseSHApproach(boolean useSHApproach) {
		this.useSHApproach = useSHApproach;
	}

	public boolean isUseONCApproach() {
		return useONCApproach;
	}

	public void setUseONCApproach(boolean useONCApproach) {
		this.useONCApproach = useONCApproach;
	}

	public ApproachesToCompare getApproachesToCompare() {
		return approachesToCompare;
	}

	public void compareSyllabifications() {
		// make sure all approaches to compare have been syllabified
		List<Word> words = langProject.getWords();
		if (useCVApproach) {
			syllabifyWordsCV(words);
		}
		if (useSHApproach) {
			syllabifyWordsSH(words);
		}
		if (useONCApproach) {
			syllabifyWordsONC(words);
		}
		calculateApproachesToCompare();
		List<Word> diffs = words.stream().filter(word -> !syllabificationsAreTheSame(word))
				.collect(Collectors.toList());
		for (Word word : diffs) {
			syllabificationsWhichDiffer.add(word);
		}
	}

	public void calculateApproachesToCompare() {
		if (useCVApproach && useSHApproach && useONCApproach) {
			approachesToCompare = ApproachesToCompare.CV_SH_ONC;
		} else if (useCVApproach && useONCApproach) {
			approachesToCompare = ApproachesToCompare.CV_ONC;
		} else if (useCVApproach && useSHApproach) {
			approachesToCompare = ApproachesToCompare.CV_SH;
		} else  if (useSHApproach && useONCApproach) {
			approachesToCompare = ApproachesToCompare.SH_ONC;
		} else {
			// if only one is true or none are true, do them all
			approachesToCompare = ApproachesToCompare.CV_SH_ONC;
		}
	}

	protected boolean syllabificationsAreTheSame(Word word) {
		boolean result = true;
		switch (approachesToCompare) {
		case CV_ONC:
			result = word.getCVPredictedSyllabification().equals(
					word.getONCPredictedSyllabification());
			break;
		case CV_SH:
			result = word.getCVPredictedSyllabification().equals(
					word.getSHPredictedSyllabification());
			break;
		case CV_SH_ONC:
			result = word.getCVPredictedSyllabification().equals(
					word.getSHPredictedSyllabification())
					&& word.getCVPredictedSyllabification().equals(
							word.getONCPredictedSyllabification());
			break;
		case SH_ONC:
			result = word.getSHPredictedSyllabification().equals(
					word.getONCPredictedSyllabification());
			break;
		}
		return result;
	}

	protected void syllabifyWordsCV(List<Word> words) {
		CVSyllabifier stringSyllabifier = new CVSyllabifier(langProject.getCVApproach());
		for (Word word : words) {
			boolean fSuccess = stringSyllabifier.convertStringToSyllables(word.getWord());
			if (fSuccess) {
				word.setCVPredictedSyllabification(stringSyllabifier
						.getSyllabificationOfCurrentWord());
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

	protected void syllabifyWordsONC(List<Word> words) {
		ONCSyllabifier oncSyllabifier = new ONCSyllabifier(langProject.getONCApproach());
		for (Word word : words) {
			boolean fSuccess = oncSyllabifier.convertStringToSyllables(word.getWord());
			if (fSuccess) {
				word.setONCPredictedSyllabification(oncSyllabifier
						.getSyllabificationOfCurrentWord());
			}
		}
	}

	@Override
	public void compare() {
		// not used for this comparer
	}

	@Override
	protected void syllabifyWords(List<Word> words1, List<Word> words2) {
		// not used for this comparer
	}

	@Override
	protected boolean predictedSyllabificationAreSame(DifferentWord diffWord, Word word) {
		// not used for this comparer
		return true;
	}
}