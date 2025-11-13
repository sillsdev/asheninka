// Copyright (c) 2016-2025 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model;

import java.util.ArrayList;

import jakarta.xml.bind.annotation.XmlTransient;

import org.sil.utility.StringUtilities;

import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public abstract class Approach {

	protected LanguageProject languageProject;

	protected abstract String getPredictedSyllabificationOfWord(Word word);

	@XmlTransient
	public LanguageProject getLanguageProject() {
		return languageProject;
	}

	public ArrayList<String> getHyphenatedWordsListWord(ObservableList<Word> words) {
		int totalNumberOfWords = words.size();
		ArrayList<String> hyphenatedWords = new ArrayList<String>(totalNumberOfWords);
		for (Word word : words) {
			String sSyllabifiedWord = word.getCorrectSyllabification();
			if (StringUtilities.isNullOrEmpty(sSyllabifiedWord)) {
				// no overt correct syllabified word present; try predicted
				sSyllabifiedWord = getPredictedSyllabificationOfWord(word);
				if (StringUtilities.isNullOrEmpty(sSyllabifiedWord)) {
					// skip this one
					continue;
				}
			}
			HyphenationParametersListWord hyphenationParameters = languageProject
					.getHyphenationParametersListWord();
			String sHyphenatedWord = getHyphenatedWord(hyphenationParameters, sSyllabifiedWord);
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
				sSyllabifiedWord = getPredictedSyllabificationOfWord(word);
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
			String sDiscretionaryHyphen = hyphenationParameters.getDiscretionaryHyphen();
			hyphenatedWords.add(sHyphenatedWord.replaceAll("\\.", sDiscretionaryHyphen));
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
				sSyllabifiedWord = getPredictedSyllabificationOfWord(word);
				if (StringUtilities.isNullOrEmpty(sSyllabifiedWord)) {
					// skip this one
					continue;
				}
			}
			HyphenationParametersXLingPaper hyphenationParameters = languageProject
					.getHyphenationParametersXLingPaper();
			String sHyphenatedWord = getHyphenatedWord(hyphenationParameters, sSyllabifiedWord);
			String sDiscretionaryHyphen = hyphenationParameters.getDiscretionaryHyphen();
			hyphenatedWords.add(sHyphenatedWord.replaceAll("\\.", sDiscretionaryHyphen));
		}
		return hyphenatedWords;
	}

	public void setLanguageProject(LanguageProject languageProject) {
		this.languageProject = languageProject;
	}

	public ObservableList<Word> getWords() {
		return languageProject.getWords();
	}

}
