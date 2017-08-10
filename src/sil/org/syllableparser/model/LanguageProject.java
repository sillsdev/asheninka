// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.model;

import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.utility.StringUtilities;

/**
 * @author Andy Black
 *
 */
@XmlRootElement(name = "languageProject")
public class LanguageProject {

	private CVApproach cvApproach;
	private ObservableList<Word> words = FXCollections.observableArrayList();
	private String sParaTExtHyphenatedWordsPreamble;
	private ObservableList<Segment> segmentInventory = FXCollections.observableArrayList();
	private Language vernacularLanguage;
	private Language analysisLanguage;
	private HyphenationParametersListWord hyphenationParametersListWord;
	private HyphenationParametersParaTExt hyphenationParametersParaTExt;
	private HyphenationParametersXLingPaper hyphenationParametersXLingPaper;
	private int databaseVersion;
	private ObservableList<Grapheme> graphemes = FXCollections.observableArrayList();
	private ObservableList<Environment> environments = FXCollections.observableArrayList();
	
	public LanguageProject() {
		super();
		cvApproach = new CVApproach();
		cvApproach.setLanguageProject(this);
		vernacularLanguage = new Language();
		analysisLanguage = new Language();
		hyphenationParametersListWord = new HyphenationParametersListWord("=", 0, 0);
		hyphenationParametersParaTExt = new HyphenationParametersParaTExt("=", 2, 2);
		hyphenationParametersXLingPaper = new HyphenationParametersXLingPaper("-", 2, 2);
	}

	/**
	 * Clear out all data in this language project
	 */
	public void clear() {
		cvApproach.clear();
		segmentInventory.clear();
		words.clear();
		graphemes.clear();
		environments.clear();
	}

	public int getDatabaseVersion() {
		return databaseVersion;
	}

	@XmlAttribute(name="databaseVersion")
	public void setDatabaseVersion(int databaseVersion) {
		this.databaseVersion = databaseVersion;
	}

	public CVApproach getCVApproach() {
		return cvApproach;
	}

	@XmlElement(name = "cvApproach")
	public void setCVApproach(CVApproach cvApproach) {
		this.cvApproach = cvApproach;
	}

	/**
	 * @return the cvSegmentInventoryData
	 */
	@XmlElementWrapper(name = "segments")
	@XmlElement(name = "segment")
	public ObservableList<Segment> getSegmentInventory() {
		return segmentInventory;
	}
	
	public List<Segment> getActiveSegmentsInInventory() {
		return segmentInventory.stream().filter(segment -> segment.isActive()).collect(Collectors.toList());
	}

	/**
	 * @param cvSegmentInventoryData
	 *            the cvSegmentInventoryData to set
	 */
	public void setSegmentInventory(ObservableList<Segment> cvSegmentInventoryData) {
		this.segmentInventory = cvSegmentInventoryData;
	}

	/**
	 * @return the word Data
	 */
	@XmlElementWrapper(name = "words")
	@XmlElement(name = "word")
	public ObservableList<Word> getWords() {
		return words;
	}

	/**
	 * @param word
	 *            Data to set
	 */
	public void setWords(ObservableList<Word> words) {
		this.words = words;
	}

	@XmlElementWrapper(name = "graphemes")
	@XmlElement(name = "grapheme")
	public ObservableList<Grapheme> getGraphemes() {
		return graphemes;
	}

	public void setGraphemes(ObservableList<Grapheme> graphemes) {
		this.graphemes = graphemes;
	}

	@XmlElementWrapper(name = "environments")
	@XmlElement(name = "environment")
	public ObservableList<Environment> getEnvironments() {
		return environments;
	}

	public void setEnvironments(ObservableList<Environment> environments) {
		this.environments = environments;
	}
	
	public String getParaTExtHyphenatedWordsPreamble() {
		return sParaTExtHyphenatedWordsPreamble;
	}

	public void setParaTExtHyphenatedWordsPreamble(String sParaTExtHyphenatedWordsPreamble) {
		this.sParaTExtHyphenatedWordsPreamble = sParaTExtHyphenatedWordsPreamble;
	}

	/**
	 * @param languageProjectLoaded
	 */
	public void load(LanguageProject languageProjectLoaded) {
		databaseVersion = languageProjectLoaded.getDatabaseVersion();
		cvApproach.load(languageProjectLoaded.getCVApproach());
		cvApproach.setLanguageProject(this);
		ObservableList<Segment> segmentInventoryLoadedData = languageProjectLoaded
				.getSegmentInventory();
		for (Segment segment : segmentInventoryLoadedData) {
			segmentInventory.add(segment);
		}
		ObservableList<Word> wordsLoadedData = languageProjectLoaded.getWords();
		for (Word word : wordsLoadedData) {
			words.add(word);
		}
		ObservableList<Grapheme> graphemesLoadedData = languageProjectLoaded.getGraphemes();
		for (Grapheme grapheme : graphemesLoadedData) {
			graphemes.add(grapheme);
		}
		ObservableList<Environment> environmentsLoadedData = languageProjectLoaded.getEnvironments();
		for (Environment environment : environmentsLoadedData) {
			environments.add(environment);
		}
		analysisLanguage = languageProjectLoaded.getAnalysisLanguage();
		vernacularLanguage = languageProjectLoaded.getVernacularLanguage();
	}

	public void createNewWord(String word, String sUntested) {
		String wordContentOnly = word.trim();
		int indexOfHashMark = word.indexOf('#');
		if (indexOfHashMark > 0) {
			wordContentOnly = word.substring(0, indexOfHashMark - 1);
		} else if (indexOfHashMark == 0) {
			wordContentOnly = "";
		}
		if (!wordContentOnly.isEmpty()) {
			final String wordToCheck = wordContentOnly;
			Word newWord = new Word(wordToCheck, "", sUntested);
			addWordIfDistinct(wordToCheck, newWord);
		}
	}

	public void createNewWordFromFLExExportedWordformsAsTabbedList(String line, String sUntested) {
		int iTabIndex = line.indexOf('\t');
		if (iTabIndex > 0) {
			String wordContentOnly = line.substring(0, iTabIndex);
			String[] wordsInLine = wordContentOnly.split(" ");
			for (String word : wordsInLine) {
				if (!word.isEmpty()) {
					Word newWord = new Word(word, "", sUntested);
					addWordIfDistinct(word, newWord);
				}
			}
		}
	}

	public void createNewWordFromParaTExt(String word, String sUntested) {
		String wordContentOnly = word.trim();
		String wordWithCorrectSyllabification = "";
		int indexOfHashMark = word.indexOf('*');
		if (indexOfHashMark == 0) {
			wordContentOnly = word.substring(1);
			wordWithCorrectSyllabification = wordContentOnly.replace("=", ".");
		}
		if (!wordContentOnly.isEmpty()) {
			final String wordToCheck = wordContentOnly.replace("=", "");
			Word newWord = new Word(wordToCheck, wordWithCorrectSyllabification, sUntested);
			addWordIfDistinct(wordToCheck, newWord);
		}
	}

	public void addWordIfDistinct(final String wordToCheck, Word newWord) {
		ObservableList<Word> matchingWords = words.filtered(extantWord -> extantWord.getWord()
				.equals(wordToCheck));
		if (matchingWords.size() == 0) {
			words.add(newWord);
		}
	}

	/**
	 * @param word
	 * @param sUntested
	 *            TODO
	 */
	public void createNewWordFromParaTExt(Word word, String sUntested) {
		word.setCVParserResult(sUntested);
		addWordIfDistinct(word.getWord(), word);
	}

	/**
	 * @return the vernacular language
	 */
	public Language getVernacularLanguage() {
		return vernacularLanguage;
	}

	/**
	 * @param language the vernacular language to set
	 */
	public void setVernacularLanguage(Language language) {
		this.vernacularLanguage = language;
	}

	/**
	 * @return the analysis language
	 */
	public Language getAnalysisLanguage() {
		return analysisLanguage;
	}

	/**
	 * @param language the analysis language to set
	 */
	public void setAnalysisLanguage(Language language) {
		this.analysisLanguage = language;
	}

	public HyphenationParametersListWord getHyphenationParametersListWord() {
		return hyphenationParametersListWord;
	}

	public void setHyphenationParametersListWord(
			HyphenationParametersListWord hyphenationParametersListWord) {
		this.hyphenationParametersListWord = hyphenationParametersListWord;
	}

	public HyphenationParametersParaTExt getHyphenationParametersParaTExt() {
		return hyphenationParametersParaTExt;
	}

	public void setHyphenationParametersParaTExt(
			HyphenationParametersParaTExt hyphenationParametersParaTExt) {
		this.hyphenationParametersParaTExt = hyphenationParametersParaTExt;
	}

	public HyphenationParametersXLingPaper getHyphenationParametersXLingPaper() {
		return hyphenationParametersXLingPaper;
	}

	public void setHyphenationParametersXLingPaper(
			HyphenationParametersXLingPaper hyphenationParametersXLingPaper) {
		this.hyphenationParametersXLingPaper = hyphenationParametersXLingPaper;
	}

}
