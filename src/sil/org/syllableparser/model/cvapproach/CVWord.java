/**
 * 
 */
package sil.org.syllableparser.model.cvapproach;

import sil.org.syllableparser.model.Word;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Andy Black
 *
 */
public class CVWord extends Word {
	private final StringProperty predictedSyllabification;

	// TODO: decide if we need some kind of a comment field to say what kind of
	// case this word represents

	// TODO: add an indication of whether or not the predicted and correct
	// values are the same

	public CVWord() {
		super();
		this.predictedSyllabification = new SimpleStringProperty("");
	}

	public CVWord(String word, String predictedSyllabification,
			String correctSyllabification, String parserResult) {
		super(word, correctSyllabification, parserResult);
		this.predictedSyllabification = new SimpleStringProperty(
				predictedSyllabification);
	}

	public String getCVWord() {
		return word.get();
	}

	public StringProperty cvWordProperty() {
		return word;
	}

	public void setCVWord(String cvWord) {
		this.word.set(cvWord);
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

	public String getCVParserResult() {
		return parserResult.get();
	}

	public StringProperty cvParserResultProperty() {
		return parserResult;
	}

	public void setCVParserResult(String cvParserResult) {
		this.parserResult.set(cvParserResult);
	}

}
