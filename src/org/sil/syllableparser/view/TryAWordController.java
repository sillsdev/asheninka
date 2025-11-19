// Copyright (c) 2016-2025 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.Language;
import org.sil.utility.StringUtilities;
import org.sil.utility.service.keyboards.KeyboardChanger;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public abstract class TryAWordController implements Initializable {

	@FXML
	protected TextField wordToTry;
	@FXML
	protected Button tryItButton = new Button();
	@FXML
	protected WebView browser;
	@FXML
	protected WebEngine webEngine;

	Stage dialogStage;
	protected MainApp mainApp;
	protected ApplicationPreferences preferences;
	protected String sWordToTry;
	protected ResourceBundle bundle;
	protected Locale locale;
	protected String lastTryAWord;
	protected KeyboardChanger keyboardChanger;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		tryItButton.setText(bundle.getString("label.tryit"));
		setTryItButtonDisable();
		// browser = new WebView();
		webEngine = browser.getEngine();

		keyboardChanger = KeyboardChanger.getInstance();
		wordToTry.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue) {
				setTryItButtonDisable();
			}
		});
		wordToTry.focusedProperty().addListener((observable, wasFocused, isNowFocused) -> {
			if (isNowFocused) {
				keyboardChanger.tryToChangeKeyboardTo(mainApp.getLanguageProject().getVernacularLanguage().getKeyboard(), MainApp.class);
			}
		});

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				wordToTry.requestFocus();
			}
		});
	}

	// Following getters and setters are for testing

	public TextField getWordToTry() {
		return wordToTry;
	}

	public void setWordToTry(TextField wordToTry) {
		this.wordToTry = wordToTry;
	}

	public Button getTryItButton() {
		return tryItButton;
	}

	public void setTryItButton(Button tryItButton) {
		this.tryItButton = tryItButton;
	}

	public void setTryItButtonDisable() {
		sWordToTry = wordToTry.getText();
		if (StringUtilities.isNullOrEmpty(sWordToTry)) {
			tryItButton.setDisable(true);
		} else {
			tryItButton.setDisable(false);
		}
	}

	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;

		this.dialogStage.setOnCloseRequest(event -> {
			handleClose();
		});
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		preferences = mainApp.getApplicationPreferences();
		String lastWordTried = getLastTryAWordUsed();
		Language vernacular = mainApp.getLanguageProject().getVernacularLanguage();
		NodeOrientation vernacularOrientation = vernacular.getOrientation();
		wordToTry.setNodeOrientation(vernacularOrientation);
		wordToTry.setFont(vernacular.getFont());
		String sVernacular = mainApp.getStyleFromColor(vernacular.getColor());
		wordToTry.setStyle(sVernacular);
		wordToTry.setText(lastWordTried);
		lastTryAWord = getLastTryAWord();
		dialogStage = preferences.getLastWindowParameters(lastTryAWord, dialogStage, 533., 637.);
	}

	protected abstract String getLastTryAWord();
	protected abstract String getLastTryAWordUsed();
	protected String setWordAsString() {
		return wordToTry.getText().trim();
	}
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * Called when the user clicks Close.
	 */
	@FXML
	private void handleClose() {
		String lastWordTried = wordToTry.getText();
		setLastTryAWordUsed(lastWordTried);
		preferences.setLastWindowParameters(lastTryAWord, dialogStage);
		dialogStage.close();
	}

	protected abstract void setLastTryAWordUsed(String lastWordTried);
	
	/**
	 * Called when the user clicks help.
	 */
	@FXML
	private void handleHelp() {
		mainApp.showNotImplementedYet();
	}

	protected void createAndShowPleaseWaitMessage() {
		String sPleaseWaitMessage = Constants.PLEASE_WAIT_HTML_BEGINNING
				+ bundle.getString("label.pleasewait") + Constants.PLEASE_WAIT_HTML_MIDDLE
				+ bundle.getString("label.pleasewaittaw") + Constants.PLEASE_WAIT_HTML_ENDING;
		webEngine.loadContent(sPleaseWaitMessage);
	}
}
