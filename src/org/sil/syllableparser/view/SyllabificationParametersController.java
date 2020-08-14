// Copyright (c) 2019-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.SyllabificationParameters;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

/**
 * @author Andy Black
 *
 */

public class SyllabificationParametersController extends SylParserBaseController implements
		Initializable {

	@FXML
	private CheckBox codasAllowedCheckBox;
	@FXML
	private CheckBox onsetMaximizationCheckBox;
	@FXML
	protected ToggleGroup group;
	@FXML
	private RadioButton allButFirstOPRadioButton;
	@FXML
	private RadioButton everySyllableOPRadioButton;
	@FXML
	private RadioButton onsetsNotRequiredOPRadioButton;
	@FXML
	private Label lblOnsetPrinciple;

	private SyllabificationParameters syllabificationParameters;
	private LanguageProject languageProject;

	public SyllabificationParametersController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		this.bundle = resources;

		codasAllowedCheckBox.setOnAction((event) -> {
			if (syllabificationParameters != null) {
				syllabificationParameters.setCodasAllowed(codasAllowedCheckBox.isSelected());
			}
		});
		onsetMaximizationCheckBox.setOnAction((event) -> {
			if (syllabificationParameters != null) {
				syllabificationParameters.setOnsetMaximization(onsetMaximizationCheckBox.isSelected());
			}
		});

		allButFirstOPRadioButton.setText(bundle.getString("radio.allbutfirst"));
		everySyllableOPRadioButton.setText(bundle.getString("radio.everysyllable"));
		onsetsNotRequiredOPRadioButton.setText(bundle.getString("radio.onsetsnotrequired"));

		codasAllowedCheckBox.requestFocus();
	}

	public RadioButton getAllButFirstOPRadioButton() {
		return allButFirstOPRadioButton;
	}

	public void setAllButFirstOPRadioButton(RadioButton allButFirstOPRadioButton) {
		this.allButFirstOPRadioButton = allButFirstOPRadioButton;
	}


	public RadioButton getEverySyllableOPRadioButton() {
		return everySyllableOPRadioButton;
	}

	public void setEverySyllableOPRadioButton(RadioButton everySyllableOPRadioButton) {
		this.everySyllableOPRadioButton = everySyllableOPRadioButton;
	}

	public RadioButton getOnsetsNotRequiredOPRadioButton() {
		return onsetsNotRequiredOPRadioButton;
	}

	public void setOnsetsNotRequiredOPRadioButton(RadioButton onsetsNotRequiredOPRadioButton) {
		this.onsetsNotRequiredOPRadioButton = onsetsNotRequiredOPRadioButton;
	}
	
	@FXML
	public void handleAllButFirstOPRadioButton() {
		languageProject.getSyllabificationParameters().setOnsetPrinciple(OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET.toString());
		allButFirstOPRadioButton.setSelected(true); // needed by test for some
													// reason...
	}

	@FXML
	public void handleEverySyllableOPRadioButton() {
		languageProject.getSyllabificationParameters().setOnsetPrinciple(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET.toString());
		everySyllableOPRadioButton.setSelected(true); // needed by test for some
													// reason...
	}
	
	@FXML
	public void handleOnsetsNotRequiredOPRadioButton() {
		languageProject.getSyllabificationParameters().setOnsetPrinciple(OnsetPrincipleType.ONSETS_NOT_REQUIRED.toString());
		onsetsNotRequiredOPRadioButton.setSelected(true); // needed by test for some
													// reason...
	}

	public void setData(LanguageProject langProj) {
		languageProject = langProj;
		syllabificationParameters = langProj.getSyllabificationParameters();
		if (codasAllowedCheckBox != null) {
			codasAllowedCheckBox.setSelected(syllabificationParameters.isCodasAllowed());
		}
		if (onsetMaximizationCheckBox != null) {
			onsetMaximizationCheckBox.setSelected(syllabificationParameters.isOnsetMaximization());
		}
		if (allButFirstOPRadioButton != null && everySyllableOPRadioButton != null && onsetsNotRequiredOPRadioButton != null) {
			String onsetPrinciple = syllabificationParameters.getOnsetPrinciple();
			if (onsetPrinciple.equals(OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET.toString())) {
				allButFirstOPRadioButton.setSelected(true);
			} else if (onsetPrinciple.equals(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET.toString())) {
				everySyllableOPRadioButton.setSelected(true);
			} else {
				onsetsNotRequiredOPRadioButton.setSelected(true);
			}
		}
	}

	@Override
	void handleInsertNewItem() {
		// nothing to do
	}

	@Override
	void handleRemoveItem() {
		// nothing to do
	}

	@Override
	TextField[] createTextFields() {
		// nothing to do
		return null;
	}

	@Override
	void handlePreviousItem() {
		// nothing to do
	}

	@Override
	void handleNextItem() {
		// nothing to do
	}
}
