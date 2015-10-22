/**
 * 
 */
package sil.org.syllableparser.model.entity;

import sil.org.syllableparser.model.SylParserObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Andy Black
 *
 */
public class CVWord extends SylParserObject {
	private final StringProperty cvWord;
	private final StringProperty predictedSyllabification;
	private final StringProperty correctSyllabification;

	// TODO: decide if we need some kind of a comment field to say what kind of
	// case this word represents

	// TODO: add an indication of whether or not the predicted and correct
	// values are the same

	public CVWord() {
		super();
		this.cvWord = new SimpleStringProperty("");
		this.predictedSyllabification = new SimpleStringProperty("");
		this.correctSyllabification = new SimpleStringProperty("");
		createUUID();
	}

	public CVWord(String word, String predictedHyphenation,
			String correctHyphenation) {
		super();
		this.cvWord = new SimpleStringProperty(word);
		this.predictedSyllabification = new SimpleStringProperty(
				predictedHyphenation);
		this.correctSyllabification = new SimpleStringProperty(correctHyphenation);
		createUUID();
	}

	public String getCVWord() {
		return cvWord.get();
	}

	public StringProperty cvWordProperty() {
		return cvWord;
	}

	public void setCVWord(String cvWord) {
		this.cvWord.set(cvWord);
	}

	public String getPredictedSyllabification() {
		return predictedSyllabification.get();
	}

	public StringProperty predictedSyllabificationProperty() {
		return predictedSyllabification;
	}

	public void setPredictedSyllabification(String predictedSyllabification) {
		this.predictedSyllabification.set(predictedSyllabification);
	}

	public String getCorrectSyllabification() {
		return correctSyllabification.get();
	}

	public StringProperty correctSyllabificationProperty() {
		return correctSyllabification;
	}

	public void setCorrectSyllabification(String correctSyllabification) {
		this.correctSyllabification.set(correctSyllabification);
	}
}
