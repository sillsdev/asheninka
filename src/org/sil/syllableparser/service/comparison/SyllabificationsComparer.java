// Copyright (c) 2019-2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.comparison;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.service.parsing.CVSyllabifier;
import org.sil.syllableparser.service.parsing.MoraicSyllabifier;
import org.sil.syllableparser.service.parsing.NPSyllabifier;
import org.sil.syllableparser.service.parsing.ONCSyllabifier;
import org.sil.syllableparser.service.parsing.OTSyllabifier;
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
	boolean useNPApproach = true;
	boolean useOTApproach = true;

	final int usesCVApproach = 1;
	final int usesSHApproach = 2;
	final int usesONCApproach = 4;
	final int usesMoraicApproach = 8;
	final int usesNPApproach = 16;
	final int usesOTApproach = 32;

	int approachesToUse = 0;

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

	public int getApproachesToUse() {
		return approachesToUse;
	}

	public void setApproachesToUse(int approachesToUse) {
		this.approachesToUse = approachesToUse;
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
		if (useNPApproach) {
			syllabifyWordsNP(words);
		}
		if (useOTApproach) {
			syllabifyWordsOT(words);
		}
		calculateApproachesToUse();
		List<Word> diffs = words.stream().filter(word -> !syllabificationsAreTheSame(word))
				.collect(Collectors.toList());
		for (Word word : diffs) {
			syllabificationsWhichDiffer.add(word);
		}
	}

	public void calculateApproachesToUse() {
		approachesToUse = 0;
		if (useCVApproach)
			approachesToUse += usesCVApproach;
		if (useSHApproach)
			approachesToUse += usesSHApproach;
		if (useONCApproach)
			approachesToUse += usesONCApproach;
		if (useMoraicApproach)
			approachesToUse += usesMoraicApproach;
		if (useNPApproach)
			approachesToUse += usesNPApproach;
		if (useOTApproach)
			approachesToUse += usesOTApproach;
	}

	protected boolean syllabificationsAreTheSame(Word word) {
		List<String> syllabifications = new ArrayList<String>(6);
		int iUsed = createSyllabificationsToCheck(word, syllabifications);
		for (int i = 1; i < iUsed; i++) {
			if (!syllabifications.get(i-1).equals(syllabifications.get(i))) {
				return false;
			}
		}
		return true;
	}

	protected int createSyllabificationsToCheck(Word word, List<String> syllabifications) {
		int iUsed = 0;
		if ((approachesToUse & usesCVApproach) > 0) {
			syllabifications.add(word.getCVPredictedSyllabification());
			iUsed++;
		}
		if ((approachesToUse & usesSHApproach) > 0) {
			syllabifications.add(word.getSHPredictedSyllabification());
			iUsed++;
		}
		if ((approachesToUse & usesONCApproach) > 0) {
			syllabifications.add(word.getONCPredictedSyllabification());
			iUsed++;
		}
		if ((approachesToUse & usesMoraicApproach) > 0) {
			syllabifications.add(word.getMoraicPredictedSyllabification());
			iUsed++;
		}
		if ((approachesToUse & usesNPApproach) > 0) {
			syllabifications.add(word.getNPPredictedSyllabification());
			iUsed++;
		}
		if ((approachesToUse & usesOTApproach) > 0) {
			syllabifications.add(word.getOTPredictedSyllabification());
			iUsed++;
		}
		return iUsed;
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

	protected void syllabifyWordsNP(List<Word> words) {
		NPSyllabifier npSyllabifier = new NPSyllabifier(langProject.getNPApproach());
		for (Word word : words) {
			boolean fSuccess = npSyllabifier.convertStringToSyllables(word.getWord());
			if (fSuccess) {
				word.setNPPredictedSyllabification(npSyllabifier.getSyllabificationOfCurrentWord());
			}
		}
	}

	protected void syllabifyWordsOT(List<Word> words) {
		OTSyllabifier otSyllabifier = new OTSyllabifier(langProject.getOTApproach());
		Locale locale = new Locale("en");
		otSyllabifier.setBundle(ResourceBundle.getBundle(Constants.RESOURCE_LOCATION, locale));
		for (Word word : words) {
			boolean fSuccess = otSyllabifier.convertStringToSyllables(word.getWord());
			if (fSuccess) {
				word.setOTPredictedSyllabification(otSyllabifier.getSyllabificationOfCurrentWord());
			}
		}
	}

	public int numberOfApproachesBeingCompared() {
		calculateApproachesToUse();
		int i = 0;
		if ((approachesToUse & usesCVApproach) > 0)
			i++;
		if ((approachesToUse & usesSHApproach) > 0)
			i++;
		if ((approachesToUse & usesONCApproach) > 0)
			i++;
		if ((approachesToUse & usesMoraicApproach) > 0)
			i++;
		if ((approachesToUse & usesNPApproach) > 0)
			i++;
		if ((approachesToUse & usesOTApproach) > 0)
			i++;
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