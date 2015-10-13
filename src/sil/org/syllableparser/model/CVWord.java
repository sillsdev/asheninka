/**
 * 
 */
package sil.org.syllableparser.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Andy Black
 *
 */
public class CVWord extends SylParserObject {
	private final StringProperty cvWord;
	private final StringProperty predictedHyphenation;
	private final StringProperty correctHyphenation;

	// TODO: decide if we need some kind of a comment field to say what kind of
	// case this word represents

	// TODO: add an indication of whether or not the predicted and correct
	// values are the same

	public CVWord() {
		super();
		this.cvWord = new SimpleStringProperty("");
		this.predictedHyphenation = new SimpleStringProperty("");
		this.correctHyphenation = new SimpleStringProperty("");
		createUUID();
	}

	public CVWord(String word, String predictedHyphenation,
			String correctHyphenation) {
		super();
		this.cvWord = new SimpleStringProperty(word);
		this.predictedHyphenation = new SimpleStringProperty(
				predictedHyphenation);
		this.correctHyphenation = new SimpleStringProperty(correctHyphenation);
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

	public String getPredictedHyphenation() {
		return predictedHyphenation.get();
	}

	public StringProperty predictedHyphenationProperty() {
		return predictedHyphenation;
	}

	public void setPredictedHyphenation(String predictedHyphenation) {
		this.predictedHyphenation.set(predictedHyphenation);
	}

	public String getCorrectHyphenation() {
		return correctHyphenation.get();
	}

	public StringProperty correctHyphenationProperty() {
		return correctHyphenation;
	}

	public void setCorrectHyphenation(String correctHyphenation) {
		this.correctHyphenation.set(correctHyphenation);
	}
}
