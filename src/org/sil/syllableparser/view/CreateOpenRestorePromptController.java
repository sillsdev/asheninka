// Copyright (c) 2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import org.sil.syllableparser.MainApp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class CreateOpenRestorePromptController implements Initializable {

	public CreateOpenRestorePromptController() {
		super();
	}

	@FXML
	protected Button cancelButton;
	@FXML
	protected Button createButton;
	@FXML
	protected Button openButton;
	@FXML
	protected Button restoreButton;

	public enum Result {
		CANCEL,
		CREATE,
		OPEN,
		RESTORE,
	}
	Stage dialogStage;
	protected MainApp mainApp;
	protected ResourceBundle bundle;
	protected Locale locale;
	protected Result result = Result.CANCEL; 

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		
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
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * Called when the user clicks Close.
	 */
	@FXML
	private void handleClose() {
		dialogStage.close();
	}
	@FXML
	private void handleCancel() {
		result = Result.CANCEL;
		dialogStage.close();
	}
	@FXML
	private void handleCreate() {
		result = Result.CREATE;
		dialogStage.close();
	}
	@FXML
	private void handleOpen() {
		result = Result.OPEN;
		dialogStage.close();
	}
	@FXML
	private void handleRestore() {
		result = Result.RESTORE;
		dialogStage.close();
	}

	public Result getResult() {
		return result;
	}
}
