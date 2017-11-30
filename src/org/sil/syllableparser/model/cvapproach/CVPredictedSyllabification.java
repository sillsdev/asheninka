/**
// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
 * Used in a chooser
 */
package org.sil.syllableparser.model.cvapproach;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Andy Black
 *
 */
public class CVPredictedSyllabification {

	private StringProperty predictedSyllabification;
	private BooleanProperty checked;
	private String uuid;

	public CVPredictedSyllabification() {
		this(null, null);
	}

	public CVPredictedSyllabification(String predictedSyllabification, String uuid) {
		this.predictedSyllabification = new SimpleStringProperty(predictedSyllabification);
		this.checked = new SimpleBooleanProperty(false);
		this.uuid = uuid;
	}
	
	/**
	 * Properties
	 */
	public String getPredictedSyllabification() {
		return predictedSyllabification.get();
	}
	public boolean isChecked() {
		return checked.get();
	}

	public void setPredictedSyllabification(String predictedSyllabification) {
		this.predictedSyllabification.set(predictedSyllabification);
	}
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setChecked(boolean value) {
		this.checked.set(value);	
	}

	public StringProperty predictedSyllabificationProperty() {
		return predictedSyllabification;
	}
	public BooleanProperty checkedProperty() {
		return checked;
	}
}
