// Copyright (c) 2019-2021 SIL International
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
import org.sil.syllableparser.service.parsing.MoraicSyllabifier;
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
	boolean useMoraicApproach = true;
	boolean useNPApproach = false;
	boolean useOTApproach = false;

	public enum ApproachesToCompare {
		CV_SH, CV_ONC, CV_MORAIC, SH_ONC, SH_MORAIC, ONC_MORAIC,
		CV_SH_ONC, CV_SH_MORAIC, CV_ONC_MORAIC, SH_ONC_MORAIC,
		CV_SH_ONC_MORAIC,
	}

	ApproachesToCompare approachesToCompare = ApproachesToCompare.CV_SH_ONC_MORAIC;

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

	public boolean isUseMoraicApproach() {
		return useMoraicApproach;
	}

	public void setUseMoraicApproach(boolean useMoraicApproach) {
		this.useMoraicApproach = useMoraicApproach;
	}

	public boolean isUseNPApproach() {
		return useNPApproach;
	}

	public void setUseNPApproach(boolean useNPApproach) {
		this.useNPApproach = useNPApproach;
	}

	public boolean isUseOTApproach() {
		return useOTApproach;
	}

	public void setUseOTApproach(boolean useOTApproach) {
		this.useOTApproach = useOTApproach;
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
		if (useMoraicApproach) {
			syllabifyWordsMoraic(words);
		}
		calculateApproachesToCompare();
		List<Word> diffs = words.stream().filter(word -> !syllabificationsAreTheSame(word))
				.collect(Collectors.toList());
		for (Word word : diffs) {
			syllabificationsWhichDiffer.add(word);
		}
	}

	public void calculateApproachesToCompare() {
		if (useCVApproach && useSHApproach && useONCApproach && useMoraicApproach) {
			approachesToCompare = ApproachesToCompare.CV_SH_ONC_MORAIC;
		} else if (useCVApproach && useSHApproach && useONCApproach) {
			approachesToCompare = ApproachesToCompare.CV_SH_ONC;
		} else if (useCVApproach && useSHApproach && useMoraicApproach) {
			approachesToCompare = ApproachesToCompare.CV_SH_MORAIC;
		} else if (useCVApproach && useONCApproach && useMoraicApproach) {
			approachesToCompare = ApproachesToCompare.CV_ONC_MORAIC;
		} else if (useSHApproach && useONCApproach && useMoraicApproach) {
			approachesToCompare = ApproachesToCompare.SH_ONC_MORAIC;
		} else if (useCVApproach && useSHApproach) {
			approachesToCompare = ApproachesToCompare.CV_SH;
		} else if (useCVApproach && useONCApproach) {
			approachesToCompare = ApproachesToCompare.CV_ONC;
		} else if (useCVApproach && useMoraicApproach) {
			approachesToCompare = ApproachesToCompare.CV_MORAIC;
		} else if (useSHApproach && useONCApproach) {
			approachesToCompare = ApproachesToCompare.SH_ONC;
		} else if (useSHApproach && useMoraicApproach) {
			approachesToCompare = ApproachesToCompare.SH_MORAIC;
		} else if (useONCApproach && useMoraicApproach) {
			approachesToCompare = ApproachesToCompare.ONC_MORAIC;
		} else {
			// if only one is true or none are true, do them all
			approachesToCompare = ApproachesToCompare.CV_SH_ONC_MORAIC;
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
		case SH_ONC:
			result = word.getSHPredictedSyllabification().equals(
					word.getONCPredictedSyllabification());
			break;
		case CV_MORAIC:
			result = word.getCVPredictedSyllabification().equals(
					word.getMoraicPredictedSyllabification());
			break;
		case SH_MORAIC:
			result = word.getSHPredictedSyllabification().equals(
					word.getMoraicPredictedSyllabification());
			break;
		case ONC_MORAIC:
			result = word.getONCPredictedSyllabification().equals(
					word.getMoraicPredictedSyllabification());
			break;
		case CV_SH_ONC:
			result = word.getCVPredictedSyllabification().equals(
					word.getSHPredictedSyllabification())
					&& word.getCVPredictedSyllabification().equals(
							word.getONCPredictedSyllabification());
			break;
		case CV_SH_MORAIC:
			result = word.getCVPredictedSyllabification().equals(
					word.getSHPredictedSyllabification())
					&& word.getCVPredictedSyllabification().equals(
							word.getMoraicPredictedSyllabification());
			break;
		case CV_ONC_MORAIC:
			result = word.getCVPredictedSyllabification().equals(
					word.getONCPredictedSyllabification())
					&& word.getCVPredictedSyllabification().equals(
							word.getMoraicPredictedSyllabification());
			break;
		case SH_ONC_MORAIC:
			result = word.getSHPredictedSyllabification().equals(
					word.getONCPredictedSyllabification())
					&& word.getSHPredictedSyllabification().equals(
							word.getMoraicPredictedSyllabification());
			break;
		case CV_SH_ONC_MORAIC:
			result = word.getCVPredictedSyllabification().equals(
					word.getSHPredictedSyllabification())
					&& word.getCVPredictedSyllabification().equals(
							word.getONCPredictedSyllabification())
					&& word.getCVPredictedSyllabification().equals(
							word.getMoraicPredictedSyllabification());
			break;
		default:
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

	protected void syllabifyWordsMoraic(List<Word> words) {
		MoraicSyllabifier moraicSyllabifier = new MoraicSyllabifier(langProject.getMoraicApproach());
		for (Word word : words) {
			boolean fSuccess = moraicSyllabifier.convertStringToSyllables(word.getWord());
			if (fSuccess) {
				word.setMoraicPredictedSyllabification(moraicSyllabifier
						.getSyllabificationOfCurrentWord());
			}
		}
	}

	public int numberOfApproachesBeingCompared() {
		calculateApproachesToCompare();
		int i = 0;
		switch (approachesToCompare) {
		case CV_SH:
		case CV_ONC:
		case CV_MORAIC:
		case SH_ONC:
		case SH_MORAIC:
		case ONC_MORAIC:
			i = 2;
			break;
		case CV_SH_ONC:
		case CV_SH_MORAIC:
		case CV_ONC_MORAIC:
		case SH_ONC_MORAIC:
			i = 3;
			break;
		case CV_SH_ONC_MORAIC:
			i = 4;
			break;
		default:
			break;
		}
		return i;
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