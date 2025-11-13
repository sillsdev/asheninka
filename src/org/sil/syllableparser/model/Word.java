// Copyright (c) 2016-2025 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model;

import jakarta.xml.bind.annotation.XmlElement;

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
	protected StringProperty comment;
	protected StringProperty cvParserResult;
	protected StringProperty cvPredictedSyllabification;
	protected StringProperty shParserResult;
	protected StringProperty shPredictedSyllabification;
	protected StringProperty oncParserResult;
	protected StringProperty oncPredictedSyllabification;
	protected StringProperty moraicParserResult;
	protected StringProperty moraicPredictedSyllabification;
	protected StringProperty npParserResult;
	protected StringProperty npPredictedSyllabification;
	protected StringProperty otParserResult;
	protected StringProperty otPredictedSyllabification;
	protected StringProperty cvLingTreeDescription;
	protected StringProperty shLingTreeDescription;
	protected StringProperty oncLingTreeDescription;
	protected StringProperty muLingTreeDescription;
	protected StringProperty npLingTreeDescription;
	protected StringProperty otLingTreeDescription;

	public Word() {
		super();
		this.word = new SimpleStringProperty("");
		this.correctSyllabification = new SimpleStringProperty("");
		this.comment = new SimpleStringProperty("");
		this.cvParserResult = new SimpleStringProperty("");
		this.cvPredictedSyllabification = new SimpleStringProperty("");
		this.shParserResult = new SimpleStringProperty("");
		this.shPredictedSyllabification = new SimpleStringProperty("");
		this.oncParserResult = new SimpleStringProperty("");
		this.oncPredictedSyllabification = new SimpleStringProperty("");
		this.moraicParserResult = new SimpleStringProperty("");
		this.moraicPredictedSyllabification = new SimpleStringProperty("");
		this.npParserResult = new SimpleStringProperty("");
		this.npPredictedSyllabification = new SimpleStringProperty("");
		this.otParserResult = new SimpleStringProperty("");
		this.otPredictedSyllabification = new SimpleStringProperty("");
		this.cvLingTreeDescription = new SimpleStringProperty("");
		this.shLingTreeDescription = new SimpleStringProperty("");
		this.oncLingTreeDescription = new SimpleStringProperty("");
		this.muLingTreeDescription = new SimpleStringProperty("");
		this.npLingTreeDescription = new SimpleStringProperty("");
		this.otLingTreeDescription = new SimpleStringProperty("");
		createUUID();
	}

	public Word(String word, String correctHyphenation, String parserResult) {
		super();
		this.word = new SimpleStringProperty(word);
		this.correctSyllabification = new SimpleStringProperty(correctHyphenation);
		this.comment = new SimpleStringProperty("");
		this.cvParserResult = new SimpleStringProperty(parserResult);
		this.cvPredictedSyllabification = new SimpleStringProperty("");
		this.shParserResult = new SimpleStringProperty(parserResult);
		this.shPredictedSyllabification = new SimpleStringProperty("");
		this.oncParserResult = new SimpleStringProperty(parserResult);
		this.oncPredictedSyllabification = new SimpleStringProperty("");
		this.moraicParserResult = new SimpleStringProperty(parserResult);
		this.moraicPredictedSyllabification = new SimpleStringProperty("");
		this.npParserResult = new SimpleStringProperty(parserResult);
		this.npPredictedSyllabification = new SimpleStringProperty("");
		this.otParserResult = new SimpleStringProperty(parserResult);
		this.otPredictedSyllabification = new SimpleStringProperty("");
		this.cvLingTreeDescription = new SimpleStringProperty("");
		this.shLingTreeDescription = new SimpleStringProperty("");
		this.oncLingTreeDescription = new SimpleStringProperty("");
		this.muLingTreeDescription = new SimpleStringProperty("");
		this.npLingTreeDescription = new SimpleStringProperty("");
		this.otLingTreeDescription = new SimpleStringProperty("");
		createUUID();
	}

	public String getComment() {
		return comment.get();
	}

	public StringProperty commentProperty() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment.set(comment);
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

	public String getWord() {
		return word.get();
	}

	public StringProperty wordProperty() {
		return word;
	}

	public void setWord(String word) {
		this.word.set(word);
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

	public StringProperty oncPredictedVsCorrectSyllabificationProperty() {
		SimpleStringProperty s = new SimpleStringProperty();
		s.bind(Bindings.concat(oncPredictedSyllabificationProperty(), "\n",
				correctSyllabificationProperty()));
		return s;
	}

	public StringProperty moraicParserResultProperty() {
		return moraicParserResult;
	}

	public String getMoraicParserResult() {
		return moraicParserResult.get();
	}

	public void setMoraicParserResult(String parserResult) {
		this.moraicParserResult.set(parserResult);
	}

	public String getMoraicPredictedSyllabification() {
		return moraicPredictedSyllabification.get();
	}

	public StringProperty moraicPredictedSyllabificationProperty() {
		return moraicPredictedSyllabification;
	}

	public void setMoraicPredictedSyllabification(String string) {
		this.moraicPredictedSyllabification.set(string);
	}

	public StringProperty moraicPredictedVsCorrectSyllabificationProperty() {
		SimpleStringProperty s = new SimpleStringProperty();
		s.bind(Bindings.concat(moraicPredictedSyllabificationProperty(), "\n",
				correctSyllabificationProperty()));
		return s;
	}

	public String getNPParserResult() {
		return npParserResult.get();
	}

	public StringProperty npParserResultProperty() {
		return npParserResult;
	}

	public void setNPParserResult(String parserResult) {
		this.npParserResult.set(parserResult);
	}

	public String getNPPredictedSyllabification() {
		return npPredictedSyllabification.get();
	}

	public StringProperty npPredictedSyllabificationProperty() {
		return npPredictedSyllabification;
	}

	@XmlElement(name = "npPredictedSyllabification")
	public void setNPPredictedSyllabification(String predictedSyllabification) {
		this.npPredictedSyllabification.set(predictedSyllabification);
	}

	public StringProperty npPredictedVsCorrectSyllabificationProperty() {
		SimpleStringProperty s = new SimpleStringProperty();
		s.bind(Bindings.concat(npPredictedSyllabificationProperty(), "\n",
				correctSyllabificationProperty()));
		return s;
	}

	public String getOTParserResult() {
		return otParserResult.get();
	}

	public StringProperty otParserResultProperty() {
		return otParserResult;
	}

	public void setOTParserResult(String parserResult) {
		this.otParserResult.set(parserResult);
	}

	public String getOTPredictedSyllabification() {
		return otPredictedSyllabification.get();
	}

	public StringProperty otPredictedSyllabificationProperty() {
		return otPredictedSyllabification;
	}

	@XmlElement(name = "otPredictedSyllabification")
	public void setOTPredictedSyllabification(String predictedSyllabification) {
		this.otPredictedSyllabification.set(predictedSyllabification);
	}

	public StringProperty otPredictedVsCorrectSyllabificationProperty() {
		SimpleStringProperty s = new SimpleStringProperty();
		s.bind(Bindings.concat(otPredictedSyllabificationProperty(), "\n",
				correctSyllabificationProperty()));
		return s;
	}

	public String getCVLingTreeDescription() {
		return cvLingTreeDescription.get();
	}

	public StringProperty cvLingTreeDescriptionProperty() {
		return cvLingTreeDescription;
	}

	public void setCVLingTreeDescription(String ltDescription) {
		this.cvLingTreeDescription.set(ltDescription);
	}

	public String getSHLingTreeDescription() {
		return shLingTreeDescription.get();
	}

	public StringProperty shLingTreeDescriptionProperty() {
		return shLingTreeDescription;
	}

	public void setSHLingTreeDescription(String ltDescription) {
		this.shLingTreeDescription.set(ltDescription);
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

	public String getMoraicLingTreeDescription() {
		return muLingTreeDescription.get();
	}

	public StringProperty moraicLingTreeDescriptionProperty() {
		return muLingTreeDescription;
	}

	@XmlElement(name = "moraicLingTreeDescription")
	public void setMoraicLingTreeDescription(String ltDescription) {
		this.muLingTreeDescription.set(ltDescription);
	}

	public String getNPLingTreeDescription() {
		return npLingTreeDescription.get();
	}

	public StringProperty npLingTreeDescriptionProperty() {
		return npLingTreeDescription;
	}

	public void setNPLingTreeDescription(String ltDescription) {
		this.npLingTreeDescription.set(ltDescription);
	}

	public String getOTLingTreeDescription() {
		return otLingTreeDescription.get();
	}

	public StringProperty otLingTreeDescriptionProperty() {
		return otLingTreeDescription;
	}

	public void setOTLingTreeDescription(String ltDescription) {
		this.otLingTreeDescription.set(ltDescription);
	}

	@Override
	public int hashCode() {
		String sCombo = word.getValueSafe() + comment.getValueSafe()
				+ cvPredictedSyllabification.getValueSafe()
				+ shPredictedSyllabification.getValueSafe()
				+ oncPredictedSyllabification.getValueSafe()
				+ moraicPredictedSyllabification.getValueSafe()
				+ npPredictedSyllabification.getValueSafe()
				+ otPredictedSyllabification.getValueSafe()
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
					} else {
						if (!getMoraicPredictedSyllabification().equals(
								otherWord.getMoraicPredictedSyllabification())) {
							result = false;
						} else {
							if (!getNPPredictedSyllabification().equals(otherWord.getNPPredictedSyllabification())) {
								result = false;
							} else {
								if (!getOTPredictedSyllabification().equals(otherWord.getOTPredictedSyllabification())) {
									result = false;
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public String getSortingValue() {
		return getWord();
	}
}
