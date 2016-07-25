/**
 * 
 */
package sil.org.syllableparser.model.cvapproach;

import java.util.ArrayList;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import com.cedarsoftware.util.StringUtilities;

import sil.org.syllableparser.model.Approach;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.Word;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
//@XmlAccessorType(XmlAccessType.FIELD)
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

	@XmlElementWrapper(name = "cvSyllablePatterns")
	@XmlElement(name = "cvSyllablePattern")
	public ObservableList<CVSyllablePattern> getCVSyllablePatterns() {
		return cvSyllablePatterns;
	}

	public void setCVSyllablePatterns(ObservableList<CVSyllablePattern> cvSyllablePatterns) {
		this.cvSyllablePatterns = cvSyllablePatterns;
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

	public ArrayList<String> getHyphenatedWords(ObservableList<Word> words) {
		int totalNumberOfWords = words.size();
		ArrayList<String> hyphenatedWords = new ArrayList<String>(totalNumberOfWords);
		for (Word word : words) {
			String sSyllabifiedWord = word.getCorrectSyllabification();
			if (StringUtilities.isEmpty(sSyllabifiedWord)) {
				// no overt correct syllabified word present; try predicted
				sSyllabifiedWord = word.getCVPredictedSyllabification();
				if (StringUtilities.isEmpty(sSyllabifiedWord)) {
					// skip this one
					continue;
				}
			}
			hyphenatedWords.add(sSyllabifiedWord.replaceAll("\\.", "="));
		}

		return hyphenatedWords;
	}

	public ArrayList<String> getParaTExtHyphenatedWords(ObservableList<Word> words) {
		int totalNumberOfWords = words.size();
		ArrayList<String> hyphenatedWords = new ArrayList<String>(totalNumberOfWords);
		for (Word word : words) {
			String sSyllabifiedWord = word.getCorrectSyllabification();
			if (StringUtilities.isEmpty(sSyllabifiedWord)) {
				// no overt correct syllabified word present; try predicted
				sSyllabifiedWord = word.getCVPredictedSyllabification();
				if (StringUtilities.isEmpty(sSyllabifiedWord)) {
					// skip this one
					continue;
				}
			} else {
				// is correct so mark it with an initial asterisk
				sSyllabifiedWord = "*" + sSyllabifiedWord;
			}
			hyphenatedWords.add(sSyllabifiedWord.replaceAll("\\.", "="));
		}

		return hyphenatedWords;
	}
	
	public ArrayList<String> getXLingPaperHyphenatedWords(ObservableList<Word> words) {
		int totalNumberOfWords = words.size();
		ArrayList<String> hyphenatedWords = new ArrayList<String>(totalNumberOfWords);
		for (Word word : words) {
			String sSyllabifiedWord = word.getCorrectSyllabification();
			if (StringUtilities.isEmpty(sSyllabifiedWord)) {
				// no overt correct syllabified word present; try predicted
				sSyllabifiedWord = word.getCVPredictedSyllabification();
				if (StringUtilities.isEmpty(sSyllabifiedWord)) {
					// skip this one
					continue;
				}
			}
			hyphenatedWords.add(sSyllabifiedWord.replaceAll("\\.", "-"));
		}

		return hyphenatedWords;
	}

}
