// Copyright (c) 2016-2019 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model;

import javax.xml.bind.annotation.XmlElement;

import org.sil.syllableparser.model.SylParserObject;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Andy Black
 *
 */
public class Word extends SylParserObject {
	protected final StringProperty word;
	protected final StringProperty correctSyllabification;
	protected StringProperty cvParserResult;
	protected StringProperty cvPredictedSyllabification;
	protected StringProperty shParserResult;
	protected StringProperty shPredictedSyllabification;
	protected StringProperty oncParserResult;
	protected StringProperty oncPredictedSyllabification;
	protected StringProperty oncLingTreeDescription;

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
		this.shParserResult = new SimpleStringProperty("");
		this.shPredictedSyllabification = new SimpleStringProperty("");
		this.oncParserResult = new SimpleStringProperty("");
		this.oncPredictedSyllabification = new SimpleStringProperty("");
		this.oncLingTreeDescription = new SimpleStringProperty("");
		createUUID();
	}

	public Word(String word, String correctHyphenation, String parserResult) {
		super();
		this.word = new SimpleStringProperty(word);
		this.correctSyllabification = new SimpleStringProperty(correctHyphenation);
		this.cvParserResult = new SimpleStringProperty(parserResult);
		this.cvPredictedSyllabification = new SimpleStringProperty("");
		this.shParserResult = new SimpleStringProperty(parserResult);
		this.shPredictedSyllabification = new SimpleStringProperty("");
		this.oncParserResult = new SimpleStringProperty(parserResult);
		this.oncPredictedSyllabification = new SimpleStringProperty("");
		this.oncLingTreeDescription = new SimpleStringProperty("");
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

	public String getSHParserResult() {
		return shParserResult.get();
	}

	public StringProperty shParserResultProperty() {
		return shParserResult;
	}

	public void setSHParserResult(String parserResult) {
		this.shParserResult.set(parserResult);
	}

	public String getSHPredictedSyllabification() {
		return shPredictedSyllabification.get();
	}

	public StringProperty shPredictedSyllabificationProperty() {
		return shPredictedSyllabification;
	}

	@XmlElement(name = "shPredictedSyllabification")
	public void setSHPredictedSyllabification(String predictedSyllabification) {
		this.shPredictedSyllabification.set(predictedSyllabification);
	}

	public StringProperty shPredictedVsCorrectSyllabificationProperty() {
		SimpleStringProperty s = new SimpleStringProperty();
		s.bind(Bindings.concat(shPredictedSyllabificationProperty(), "\n",
				correctSyllabificationProperty()));
		return s;
	}

	public String getONCParserResult() {
		return oncParserResult.get();
	}

	public StringProperty oncParserResultProperty() {
		return oncParserResult;
	}

	public void setONCParserResult(String parserResult) {
		this.oncParserResult.set(parserResult);
	}

	public String getONCPredictedSyllabification() {
		return oncPredictedSyllabification.get();
	}

	public StringProperty oncPredictedSyllabificationProperty() {
		return oncPredictedSyllabification;
	}

	@XmlElement(name = "oncPredictedSyllabification")
	public void setONCPredictedSyllabification(String predictedSyllabification) {
		this.oncPredictedSyllabification.set(predictedSyllabification);
	}

	public String getONCLingTreeDescription() {
		return oncLingTreeDescription.get();
	}

	public StringProperty oncLingTreeDescriptionProperty() {
		return oncLingTreeDescription;
	}

	public void setONCLingTreeDescription(String ltDescription) {
		this.oncLingTreeDescription.set(ltDescription);
	}

	public StringProperty oncPredictedVsCorrectSyllabificationProperty() {
		SimpleStringProperty s = new SimpleStringProperty();
		s.bind(Bindings.concat(oncPredictedSyllabificationProperty(), "\n",
				correctSyllabificationProperty()));
		return s;
	}

	@Override
	public int hashCode() {
		String sCombo = word.getValueSafe() + cvPredictedSyllabification.getValueSafe()
				+ shPredictedSyllabification.getValueSafe()
				+ oncPredictedSyllabification.getValueSafe()
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
			} else {
				if (!getSHPredictedSyllabification().equals(
						otherWord.getSHPredictedSyllabification())) {
					result = false;
				} else {
					if (!getONCPredictedSyllabification().equals(
							otherWord.getONCPredictedSyllabification())) {
						result = false;
					}
				}
			}
		}
		return result;
	}

	public String getSortingValue() {
		return getWord();
	}
}
