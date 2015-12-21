/**
 * 
 */
package sil.org.syllableparser.model;

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

	public LanguageProject() {
		super();
		cvApproach = new CVApproach();
	}

	/**
	 * Clear out all data in this language project
	 */
	public void clear() {
		cvApproach.clear();
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
	 * @param languageProjectLoaded
	 */
	public void load(LanguageProject languageProjectLoaded) {
		cvApproach.load(languageProjectLoaded.getCVApproach());
		cvApproach.setLanguageProject(this);
		ObservableList<Word> wordsLoadedData = languageProjectLoaded.getWords();
		for (Word word : wordsLoadedData) {
			words.add(word);
		}

	}

}
