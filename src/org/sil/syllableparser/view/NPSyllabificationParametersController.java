// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.CheckBox;

/**
 * @author Andy Black
 *
 */

public class NPSyllabificationParametersController extends SyllabificationParametersController {

	public NPSyllabificationParametersController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		codasAllowedCheckBox = new CheckBox();
		onsetMaximizationCheckBox = new CheckBox();
		super.initialize(location, resources);
	}
}
