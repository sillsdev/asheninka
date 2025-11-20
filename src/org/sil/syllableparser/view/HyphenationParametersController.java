// Copyright (c) 2016-2025 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.HyphenationParameters;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class HyphenationParametersController implements Initializable {

	@FXML
	private Label hyphenationParametersFor;
	@FXML
	private TextField discretionaryHyphen;
	@FXML
	private TextField startIndex;
	@FXML
	private TextField endIndex;

	Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;
	private HyphenationParameters hyphenationParameters;
	private UnaryOperator<TextFormatter.Change> filter;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		filter = new UnaryOperator<TextFormatter.Change>() {
			@Override
			public TextFormatter.Change apply(TextFormatter.Change change) {
				String text = change.getText();
				for (int i = 0; i < text.length(); i++) {
					if (!Character.isDigit(text.charAt(i)))
						return null;
				}
				return change;
			}
		};

		startIndex.setTextFormatter(new TextFormatter<String>(filter));
		endIndex.setTextFormatter(new TextFormatter<String>(filter));
	}

	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param words
	 *            TODO
	 * @param cvApproachController
	 */
	public void setData(HyphenationParameters parameters) {
		hyphenationParameters = parameters;
		discretionaryHyphen.setText(parameters.getDiscretionaryHyphen());
		startIndex.setText(String.valueOf(parameters.getStartAfterCharactersFromBeginning()));
		endIndex.setText(String.valueOf(parameters.getStopBeforeCharactersFromEnd()));
	}

	/**
	 * Returns true if the user clicked OK, false otherwise.
	 * 
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	public void setHyphenationParametersFor(String hyphenationParametersFor) {
		this.hyphenationParametersFor.setText(hyphenationParametersFor);
	}

	/**
	 * Called when the user clicks OK.
	 */
	@FXML
	private void handleOk() {
		if (discretionaryHyphen.getText().length() > 0) {
			hyphenationParameters.setDiscretionaryHyphen(discretionaryHyphen.getText());
		}
		if (startIndex.getText().length() > 0) {
			int startValue = Integer.valueOf(startIndex.getText());
			hyphenationParameters.setStartAfterCharactersFromBeginning(startValue);
		}
		if (endIndex.getText().length() > 0) {
			int endValue = Integer.valueOf(endIndex.getText());
			hyphenationParameters.setStopBeforeCharactersFromEnd(endValue);
		}
		okClicked = true;
		dialogStage.close();
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * Called when the user clicks help.
	 */
	@FXML
	private void handleHelp() {
		mainApp.showNotImplementedYet();
	}

}
