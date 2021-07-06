/**
 * Copyright (c) 2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Andy Black
 *
 */
public class SyllabificationParameters {
	private BooleanProperty codasAllowed;
	private BooleanProperty onsetMaximization;
	private int maxMorasPerSyllable = 2;
	private BooleanProperty useWeightByPosition;
	// Using the string property to store and read from storage
	// as I do not know of a way to do that for an enum.
	private StringProperty onsetPrincipleAsString;
	private OnsetPrincipleType onsetPrincipleEnum;

	public SyllabificationParameters() {
		codasAllowed = new SimpleBooleanProperty(true);
		onsetMaximization = new SimpleBooleanProperty(false);
		useWeightByPosition = new SimpleBooleanProperty(false);
		onsetPrincipleAsString = new SimpleStringProperty("");
		setOnsetPrincipleEnum(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
	}

	public SyllabificationParameters(boolean codasAllowed, boolean onsetMaximization, String sOnsetPrinciple, boolean useWeightByPosition) {
		this.codasAllowed = new SimpleBooleanProperty(codasAllowed);
		this.onsetMaximization = new SimpleBooleanProperty(onsetMaximization);
		onsetPrincipleAsString = new SimpleStringProperty(sOnsetPrinciple);
		this.useWeightByPosition = new SimpleBooleanProperty(useWeightByPosition);
		convertPrincipleFromStringToEnum(sOnsetPrinciple);
	}
	
	public BooleanProperty onsetMaximizationProperty() {
		return onsetMaximization;
	}

	public boolean isOnsetMaximization() {
		return onsetMaximization.get();
	}

	public void setOnsetMaximization(boolean value) {
		this.onsetMaximization.set(value);
	}

	public int getMaxMorasPerSyllable() {
		return maxMorasPerSyllable;
	}

	public void setMaxMorasPerSyllable(int maxMorasPerSyllable) {
		this.maxMorasPerSyllable = maxMorasPerSyllable;
	}

	public BooleanProperty useWeightByPositionProperty() {
		return useWeightByPosition;
	}

	public boolean isUseWeightByPosition() {
		return useWeightByPosition.get();
	}

	public void setUseWeightByPosition(boolean value) {
		this.useWeightByPosition.setValue(value);
	}

	public BooleanProperty codasAllowedProperty() {
		return codasAllowed;
	}

	public void setCodasAllowed(boolean value) {
		this.codasAllowed.set(value);
	}

	public boolean isCodasAllowed() {
		return codasAllowed.get();
	}

	public String getOnsetPrinciple() {
		return onsetPrincipleAsString.get().trim();
	}

	public StringProperty onsetPrincipleProperty() {
		return onsetPrincipleAsString;
	}

	public void setOnsetPrinciple(String value) {
		convertPrincipleFromStringToEnum(value);
		this.onsetPrincipleAsString.set(value);
	}

	/**
	 * @return the onsetPrincipleEnum
	 */
	public OnsetPrincipleType getOnsetPrincipleEnum() {
		return onsetPrincipleEnum;
	}

	/**
	 * @param onsetPrincipleEnum the onsetPrincipleEnum to set
	 */
	public void setOnsetPrincipleEnum(OnsetPrincipleType onsetPrincipleEnum) {
		this.onsetPrincipleEnum = onsetPrincipleEnum;
	}

	private void convertPrincipleFromStringToEnum(String value) {
		if (value.equals(OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET.toString())) {
			setOnsetPrincipleEnum(OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET);
		} else if (value.equals(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET.toString())) {
			setOnsetPrincipleEnum(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET);
		} else {
			setOnsetPrincipleEnum(OnsetPrincipleType.ONSETS_NOT_REQUIRED);
		}
	}
}
