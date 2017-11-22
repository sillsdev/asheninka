// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.model;

import javax.xml.bind.annotation.XmlElement;

import sil.org.syllableparser.model.SylParserObject;
import javafx.beans.binding.Bindings;
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
	protected StringProperty cvParserResult;
	protected StringProperty cvPredictedSyllabification;

	// TODO: decide if we need some kind of a comment field to say what kind of
	// case this word represents

	// TODO: add an indication of whether or not the predicted and correct
	// values are the same

	public Word() {
		super();
		this.word = new SimpleStringProperty("");
		this.correctSyllabification = new SimpleStringProperty("");
		this.cvParserResult = new SimpleStringProperty("");
		this.cvPredictedSyllabification = new SimpleStringProperty("");
		createUUID();
	}

	public Word(String word, String correctHyphenation, String parserResult) {
		super();
		this.word = new SimpleStringProperty(word);
		this.correctSyllabification = new SimpleStringProperty(correctHyphenation);
		this.cvParserResult = new SimpleStringProperty(parserResult);
		this.cvPredictedSyllabification = new SimpleStringProperty("");
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

	public String getCVParserResult() {
		return cvParserResult.get();
	}

	public StringProperty cvParserResultProperty() {
		return cvParserResult;
	}

	public void setCVParserResult(String parserResult) {
		this.cvParserResult.set(parserResult);
	}

	public String getCVPredictedSyllabification() {
		return cvPredictedSyllabification.get();
	}

	public StringProperty cvPredictedSyllabificationProperty() {
		return cvPredictedSyllabification;
	}

	@XmlElement(name = "cvPredictedSyllabification")
	public void setCVPredictedSyllabification(String predictedSyllabification) {
		this.cvPredictedSyllabification.set(predictedSyllabification);
	}

	public StringProperty cvPredictedVsCorrectSyllabificationProperty() {
		SimpleStringProperty s = new SimpleStringProperty();
		s.bind(Bindings.concat(cvPredictedSyllabificationProperty(), "\n",
				correctSyllabificationProperty()));
		return s;
	}

	public static int findIndexInWordListByUuid(ObservableList<Word> list, String uuid) {
		// TODO: is there a way to do this with lambda expressions?
		// Is there a way to use SylParserObject somehow?
		int index = -1;
		for (SylParserObject sylParserObject : list) {
			index++;
			if (sylParserObject.getID() == uuid) {
				return index;
			}
		}
		return -1;
	}

	@Override
	public int hashCode() {
		String sCombo = word.getValueSafe() + cvPredictedSyllabification.getValueSafe()
				+ correctSyllabification.getValueSafe();
		return sCombo.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		boolean result = true;
		Word otherWord = (Word) obj;
		if (!getWord().equals(otherWord.getWord())) {
			result = false;
		} else {
			if (!getCVPredictedSyllabification().equals(otherWord.getCVPredictedSyllabification())) {
				result = false;
			}
		}
		return result;
	}
}
