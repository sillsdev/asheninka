// Copyright (c) 2016-2017 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model.cvapproach;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import org.sil.syllableparser.model.Approach;
import org.sil.syllableparser.model.GraphemeNaturalClass;
import org.sil.syllableparser.model.HyphenationParameters;
import org.sil.syllableparser.model.HyphenationParametersListWord;
import org.sil.syllableparser.model.HyphenationParametersParaTExt;
import org.sil.syllableparser.model.HyphenationParametersXLingPaper;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.utility.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
// @XmlAccessorType(XmlAccessType.FIELD)
public class CVApproach extends Approach {

	private LanguageProject languageProject;
	private ObservableList<CVNaturalClass> cvNaturalClasses = FXCollections.observableArrayList();
	private ObservableList<CVSyllablePattern> cvSyllablePatterns = FXCollections
			.observableArrayList();

	public void afterUnmarshal(Unmarshaller u, Object parent) {
		this.languageProject = (LanguageProject) parent;
	}

	/**
	 * @return the cvNaturalClassesData
	 */
	@XmlElementWrapper(name = "cvNaturalClasses")
	@XmlElement(name = "cvNaturalClass")
	public ObservableList<CVNaturalClass> getCVNaturalClasses() {
		return cvNaturalClasses;
	}

	/**
	 * @param cvSegmentInventoryData
	 *            the cvSegmentInventoryData to set
	 */
	public void setCVNaturalClasses(ObservableList<CVNaturalClass> cvNaturalClassesData) {
		this.cvNaturalClasses = cvNaturalClassesData;
	}

	public List<CVNaturalClass> getActiveCVNaturalClasses() {
		return cvNaturalClasses.stream().filter(naturalClass -> naturalClass.isActive())
				.collect(Collectors.toList());
	}

	@XmlElementWrapper(name = "cvSyllablePatterns")
	@XmlElement(name = "cvSyllablePattern")
	public ObservableList<CVSyllablePattern> getCVSyllablePatterns() {
		return cvSyllablePatterns;
	}

	public void setCVSyllablePatterns(ObservableList<CVSyllablePattern> cvSyllablePatterns) {
		this.cvSyllablePatterns = cvSyllablePatterns;
	}

	public List<CVSyllablePattern> getActiveCVSyllablePatterns() {
		return cvSyllablePatterns.stream()
				.filter(pattern -> pattern.isActive() && pattern.ncs.size() != 0)
				.collect(Collectors.toList());
	}

	@XmlTransient
	public LanguageProject getLanguageProject() {
		return languageProject;
	}

	public void setLanguageProject(LanguageProject languageProject) {
		this.languageProject = languageProject;
	}

	public ObservableList<Word> getWords() {
		return languageProject.getWords();
	}

	/**
	 * Clear out all data in this CV approach
	 */
	public void clear() {
		cvNaturalClasses.clear();
		cvSyllablePatterns.clear();
	}

	/**
	 * @param cvApproach
	 */
	public void load(CVApproach cvApproachLoaded) {
		ObservableList<CVNaturalClass> cvNaturalClassesLoadedData = cvApproachLoaded
				.getCVNaturalClasses();
		for (CVNaturalClass cvNaturalClass : cvNaturalClassesLoadedData) {
			cvNaturalClasses.add(cvNaturalClass);
		}
		ObservableList<CVSyllablePattern> cvSyllablePatternsLoadedData = cvApproachLoaded
				.getCVSyllablePatterns();
		for (CVSyllablePattern cvSyllablePattern : cvSyllablePatternsLoadedData) {
			cvSyllablePatterns.add(cvSyllablePattern);
		}
	}

	public ArrayList<String> getHyphenatedWordsListWord(ObservableList<Word> words) {
		int totalNumberOfWords = words.size();
		ArrayList<String> hyphenatedWords = new ArrayList<String>(totalNumberOfWords);
		for (Word word : words) {
			String sSyllabifiedWord = word.getCorrectSyllabification();
			if (StringUtilities.isNullOrEmpty(sSyllabifiedWord)) {
				// no overt correct syllabified word present; try predicted
				sSyllabifiedWord = word.getCVPredictedSyllabification();
				if (StringUtilities.isNullOrEmpty(sSyllabifiedWord)) {
					// skip this one
					continue;
				}
			}
			HyphenationParametersListWord hyphenationParameters = languageProject
					.getHyphenationParametersListWord();
			String sHyphenatedWord = getHyphenatedWord(hyphenationParameters, sSyllabifiedWord);
			// int positionFromStart =
			// hyphenationParameters.getStartAfterCharactersFromBeginning();
			// int positionFromEnd =
			// hyphenationParameters.getStopBeforeCharactersFromEnd();
			// String sHyphenatedWord =
			// StringUtilities.removeFromStart(sSyllabifiedWord, ".",
			// positionFromStart);
			// sHyphenatedWord = StringUtilities.removeFromEnd(sHyphenatedWord,
			// ".", positionFromEnd);
			String sDiscretionaryHyphen = hyphenationParameters.getDiscretionaryHyphen();
			hyphenatedWords.add(sHyphenatedWord.replaceAll("\\.", sDiscretionaryHyphen));
		}

		return hyphenatedWords;
	}

	protected String getHyphenatedWord(HyphenationParameters hyphenationParameters,
			String sSyllabifiedWord) {
		int positionFromStart = hyphenationParameters.getStartAfterCharactersFromBeginning();
		int positionFromEnd = hyphenationParameters.getStopBeforeCharactersFromEnd();
		String sHyphenatedWord = StringUtilities.removeFromStart(sSyllabifiedWord, ".",
				positionFromStart);
		sHyphenatedWord = StringUtilities.removeFromEnd(sHyphenatedWord, ".", positionFromEnd);
		return sHyphenatedWord;
	}

	public ArrayList<String> getHyphenatedWordsParaTExt(ObservableList<Word> words) {
		final String kAsterisk = "*";
		int totalNumberOfWords = words.size();
		ArrayList<String> hyphenatedWords = new ArrayList<String>(totalNumberOfWords);
		for (Word word : words) {
			String sSyllabifiedWord = word.getCorrectSyllabification();
			if (StringUtilities.isNullOrEmpty(sSyllabifiedWord)) {
				// no overt correct syllabified word present; try predicted
				sSyllabifiedWord = word.getCVPredictedSyllabification();
				if (StringUtilities.isNullOrEmpty(sSyllabifiedWord)) {
					// skip this one
					continue;
				}
			} else {
				// is correct so mark it with an initial asterisk
				sSyllabifiedWord = kAsterisk + sSyllabifiedWord;
			}
			HyphenationParametersParaTExt hyphenationParameters = languageProject
					.getHyphenationParametersParaTExt();
			// do not count initial asterisk for removing hyphens from beginning
			String sWordToCheck = sSyllabifiedWord;
			if (sWordToCheck.startsWith(kAsterisk)) {
				sWordToCheck = sSyllabifiedWord.substring(1);
			}
			String sHyphenatedWord = getHyphenatedWord(hyphenationParameters, sWordToCheck);
			if (sSyllabifiedWord.startsWith(kAsterisk)) {
				sHyphenatedWord = kAsterisk + sHyphenatedWord;
			}
			// int positionFromStart =
			// hyphenationParameters.getStartAfterCharactersFromBeginning();
			// int positionFromEnd =
			// hyphenationParameters.getStopBeforeCharactersFromEnd();
			// String sHyphenatedWord =
			// StringUtilities.removeFromStart(sSyllabifiedWord, ".",
			// positionFromStart);
			// sHyphenatedWord = StringUtilities.removeFromEnd(sHyphenatedWord,
			// ".", positionFromEnd);
			String sDiscretionaryHyphen = hyphenationParameters.getDiscretionaryHyphen();
			hyphenatedWords.add(sHyphenatedWord.replaceAll("\\.", sDiscretionaryHyphen));
			// hyphenatedWords.add(sSyllabifiedWord.replaceAll("\\.",
			// sDiscretionaryHyphen));
		}

		return hyphenatedWords;
	}

	public ArrayList<String> getHyphenatedWordsXLingPaper(ObservableList<Word> words) {
		int totalNumberOfWords = words.size();
		ArrayList<String> hyphenatedWords = new ArrayList<String>(totalNumberOfWords);
		for (Word word : words) {
			String sSyllabifiedWord = word.getCorrectSyllabification();
			if (StringUtilities.isNullOrEmpty(sSyllabifiedWord)) {
				// no overt correct syllabified word present; try predicted
				sSyllabifiedWord = word.getCVPredictedSyllabification();
				if (StringUtilities.isNullOrEmpty(sSyllabifiedWord)) {
					// skip this one
					continue;
				}
			}
			HyphenationParametersXLingPaper hyphenationParameters = languageProject
					.getHyphenationParametersXLingPaper();
			String sHyphenatedWord = getHyphenatedWord(hyphenationParameters, sSyllabifiedWord);
			// int positionFromStart =
			// hyphenationParameters.getStartAfterCharactersFromBeginning();
			// int positionFromEnd =
			// hyphenationParameters.getStopBeforeCharactersFromEnd();
			// String sHyphenatedWord =
			// StringUtilities.removeFromStart(sSyllabifiedWord, ".",
			// positionFromStart);
			// sHyphenatedWord = StringUtilities.removeFromEnd(sHyphenatedWord,
			// ".", positionFromEnd);
			String sDiscretionaryHyphen = hyphenationParameters.getDiscretionaryHyphen();
			hyphenatedWords.add(sHyphenatedWord.replaceAll("\\.", sDiscretionaryHyphen));

			// hyphenatedWords.add(sSyllabifiedWord.replaceAll("\\.",
			// sDiscretionaryHyphen));
		}

		return hyphenatedWords;
	}

}
