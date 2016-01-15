/**
 * 
 */
package sil.org.syllableparser.model;

import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import sil.org.syllableparser.model.cvapproach.CVApproach;

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

	public LanguageProject() {
		super();
		cvApproach = new CVApproach();
		cvApproach.setLanguageProject(this);
	}

	/**
	 * Clear out all data in this language project
	 */
	public void clear() {
		cvApproach.clear();
		segmentInventory.clear();
		words.clear();
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

}
