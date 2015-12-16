/**
 * 
 */
package sil.org.syllableparser.model;

import sil.org.syllableparser.model.SylParserObject;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public class Word extends SylParserObject {
	protected final StringProperty word;
	protected final StringProperty correctSyllabification;
	protected StringProperty parserResult;

	// TODO: decide if we need some kind of a comment field to say what kind of
	// case this word represents

	// TODO: add an indication of whether or not the predicted and correct
	// values are the same

	public Word() {
		super();
		this.word = new SimpleStringProperty("");
		this.correctSyllabification = new SimpleStringProperty("");
		this.parserResult = new SimpleStringProperty("");
		createUUID();
	}

	public Word(String word, String correctHyphenation, String parserResult) {
		super();
		this.word = new SimpleStringProperty(word);
		this.correctSyllabification = new SimpleStringProperty(correctHyphenation);
		this.parserResult = new SimpleStringProperty(parserResult);
		createUUID();
	}

	public String getWord() {
		return word.get();
	}

	public StringProperty wordProperty() {
		return word;
	}

	public void setWord(String word) {
		this.word.set(word);
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

	public String getParserResult() {
		return parserResult.get();
	}

	public StringProperty parserResultProperty() {
		return parserResult;
	}

	public void setParserResult(String parserResult) {
		this.parserResult.set(parserResult);
	}
}
