/**
 * 
 */
package sil.org.syllableparser.model.cvapproach;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import sil.org.syllableparser.model.Word;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public class CVApproach {

	private ObservableList<CVSegment> cvSegmentInventory = FXCollections.observableArrayList();
	private ObservableList<CVNaturalClass> cvNaturalClasses = FXCollections.observableArrayList();
	private ObservableList<CVSyllablePattern> cvSyllablePatterns = FXCollections
			.observableArrayList();
	private ObservableList<Word> words = FXCollections.observableArrayList();

	/**
	 * @return the cvSegmentInventoryData
	 */
	@XmlElementWrapper(name = "cvSegments")
	@XmlElement(name = "cvSegment")
	public ObservableList<CVSegment> getCVSegmentInventory() {
		return cvSegmentInventory;
	}

	/**
	 * @param cvSegmentInventoryData
	 *            the cvSegmentInventoryData to set
	 */
	public void setCVSegmentInventory(ObservableList<CVSegment> cvSegmentInventoryData) {
		this.cvSegmentInventory = cvSegmentInventoryData;
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

	@XmlElementWrapper(name = "cvSyllablePatterns")
	@XmlElement(name = "cvSyllablePattern")
	public ObservableList<CVSyllablePattern> getCVSyllablePatterns() {
		return cvSyllablePatterns;
	}

	public void setCVSyllablePatterns(ObservableList<CVSyllablePattern> cvSyllablePatterns) {
		this.cvSyllablePatterns = cvSyllablePatterns;
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

	/**
	 * Clear out all data in this CV approach
	 */
	public void clear() {
		cvSegmentInventory.clear();
		cvNaturalClasses.clear();
		cvSyllablePatterns.clear();
		words.clear();
	}

	/**
	 * @param cvApproach
	 */
	public void load(CVApproach cvApproachLoaded) {
		ObservableList<CVSegment> cvSegmentInventoryLoadedData = cvApproachLoaded
				.getCVSegmentInventory();
		for (CVSegment cvSegment : cvSegmentInventoryLoadedData) {
			cvSegmentInventory.add(cvSegment);
		}
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
		ObservableList<Word> wordsLoadedData = cvApproachLoaded.getWords();
		for (Word word : wordsLoadedData) {
			words.add(word);
		}
	}

	public ArrayList<String> getHyphenatedWords() {
		int totalNumberOfWords = words.size();
		ArrayList<String> hyphenatedWords = new ArrayList<String>(totalNumberOfWords);
		for (Word word : words) {
			String sSyllabifiedWord = word.getCorrectSyllabification();
			if (sSyllabifiedWord == null || sSyllabifiedWord.isEmpty()) {
				// no overt correct syllabified word present; try predicted
				sSyllabifiedWord = word.getCVPredictedSyllabification();
				if (sSyllabifiedWord != null) {
					// predicted has something
					if (sSyllabifiedWord.isEmpty()) {
						// skip this one
						continue;
					}
				}
			}
			hyphenatedWords.add(sSyllabifiedWord.replaceAll("\\.", "="));
		}

		return hyphenatedWords;
	}

}
