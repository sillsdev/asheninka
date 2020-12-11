// Copyright (c) 2016-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 * 
 */
package org.sil.syllableparser.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.BackupFile;
import org.sil.utility.StringUtilities;
import org.sil.utility.view.ControllerUtilities;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public abstract class ComparisonController implements Initializable {

	@FXML
	protected TextField directory1Field;
	@FXML
	protected TextField directory2Field;
	@FXML
	protected RadioButton currentImplementation;
	@FXML
	protected RadioButton chosenImplementation;
	@FXML
	protected Button firstButton = new Button();
	@FXML
	protected Button compareButton = new Button();
	@FXML
	protected ToggleGroup group;
	@FXML
	protected WebView browser;
	@FXML
	protected WebEngine webEngine;

	protected Stage dialogStage;
	protected boolean okClicked = false;
	protected MainApp mainApp;
	protected ApplicationPreferences preferences;

	protected String backupDirectory;
	protected BackupFile backupFile1;
	protected BackupFile backupFile2;
	protected String backupDirectory1;
	protected String backupDirectory2;
	protected ResourceBundle bundle;
	protected Locale locale;
	protected String sDataSet1Info;
	protected String sDataSet2Info;
	protected final String sCurrentImplementationResourceLabel = "radio.current";
	protected String sWindowParams = ApplicationPreferences.LAST_CV_COMPARISON;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		firstButton.setText(bundle.getString("label.browse"));
		currentImplementation.setText(bundle.getString(sCurrentImplementationResourceLabel));
		chosenImplementation.setText(bundle.getString("radio.choosebackup"));
		setFirstBrowseButtonAndText();
		setCompareButtonDisable();
		// browser = new WebView();
		webEngine = browser.getEngine();
	}

	// Following getters and setters are for testing

	public TextField getDirectory1Field() {
		return directory1Field;
	}

	public void setDirectory1Field(TextField directory1Field) {
		this.directory1Field = directory1Field;
	}

	public TextField getDirectory2Field() {
		return directory2Field;
	}

	public RadioButton getCurrentImplementation() {
		return currentImplementation;
	}

	public void setCurrentImplementation(RadioButton currentImplementation) {
		this.currentImplementation = currentImplementation;
	}

	public RadioButton getChosenImplementation() {
		return chosenImplementation;
	}

	public void setChosenImplementation(RadioButton chosenImplementation) {
		this.chosenImplementation = chosenImplementation;
	}

	public Button getFirstButton() {
		return firstButton;
	}

	public void setFirstButton(Button firstButton) {
		this.firstButton = firstButton;
	}

	public Button getCompareButton() {
		return compareButton;
	}

	public void setCompareButton(Button compareButton) {
		this.compareButton = compareButton;
	}

	public BackupFile getBackupFile1() {
		return backupFile1;
	}

	public void setBackupFile1(BackupFile backupFile1) {
		this.backupFile1 = backupFile1;
	}

	public BackupFile getBackupFile2() {
		return backupFile2;
	}

	public void setBackupFile2(BackupFile backupFile2) {
		this.backupFile2 = backupFile2;
	}

	public String getBackupDirectory1() {
		return backupDirectory1;
	}

	public void setBackupDirectory1(String backupDirectory1) {
		this.backupDirectory1 = backupDirectory1;
	}

	public String getBackupDirectory2() {
		return backupDirectory2;
	}

	public void setBackupDirectory2(String backupDirectory2) {
		this.backupDirectory2 = backupDirectory2;
	}

	public void setCompareButtonDisable() {
		backupDirectory1 = directory1Field.getText();
		backupDirectory2 = directory2Field.getText();
		if (StringUtilities.isNullOrEmpty(backupDirectory1)
				|| StringUtilities.isNullOrEmpty(backupDirectory2)) {
			compareButton.setDisable(true);
		} else {
			compareButton.setDisable(false);
		}
	}

	protected void setFirstBrowseButtonAndText() {
		if (currentImplementation.isSelected()) {
			directory1Field.setText(bundle.getString(sCurrentImplementationResourceLabel));
			firstButton.setDisable(true);
		} else {
			directory1Field.setText("");
			firstButton.setDisable(false);
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
			handleCancel();
		});
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		preferences = mainApp.getApplicationPreferences();
		dialogStage = preferences.getLastWindowParameters(sWindowParams, dialogStage, 533.0, 637.0);
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setBackupDirectoryPath(String directory) {
		backupDirectory = directory;
	}

	public String getWindowParams() {
		return sWindowParams;
	}

	public void setWindowParams(String sWindowParams) {
		this.sWindowParams = sWindowParams;
	}

	@FXML
	protected void handleBrowse1() {
		try {
			String sResultChosen = chooseBackupFileToUse();
			if (!StringUtilities.isNullOrEmpty(sResultChosen)) {
				backupDirectory1 = sResultChosen;
				directory1Field.setText(sResultChosen);
			}
		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
		setCompareButtonDisable();
	}

	protected String chooseBackupFileToUse() throws IOException {
		// Load the fxml file and create a new stage for the popup.
		Stage dialogStage = new Stage();
		String resource = "fxml/BackupChooser.fxml";
		String title = bundle.getString("label.restoreproject");
		FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, dialogStage,
				title, ApproachViewNavigator.class.getResource(resource),
				Constants.RESOURCE_LOCATION);

		BackupChooserController controller = loader.getController();
		controller.setDialogStage(dialogStage);
		controller.setMainApp(mainApp);
		controller.initializeTableColumnWidths(mainApp.getApplicationPreferences());
		controller.setLocale(locale);
		String backupDirectoryPath = backupDirectory;
		controller.setData(backupDirectoryPath);

		dialogStage.showAndWait();
		String sResultChosen = controller.getBackupFileToUse();
		return sResultChosen;
	}

	@FXML
	protected void handleBrowse2() {
		try {
			String sResultChosen = chooseBackupFileToUse();
			if (!StringUtilities.isNullOrEmpty(sResultChosen)) {
				backupDirectory2 = sResultChosen;
				directory2Field.setText(sResultChosen);
			}
		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
		setCompareButtonDisable();
	}

	@FXML
	public void handleCurrentImplementation() {
		currentImplementation.setSelected(true); // needed by test for some
													// reason...
		firstButton.setDisable(true);
		setFirstBrowseButtonAndText();
	}

	@FXML
	public void handleChosenImplementation() {
		chosenImplementation.setSelected(true); // needed by test for some
												// reason...
		firstButton.setDisable(false);
		setFirstBrowseButtonAndText();
		setCompareButtonDisable();
	}

	/**
	 * Called when the user clicks Compare.
	 */
	@FXML
	abstract protected void handleCompare();

	protected String getBackupComment(String sBackupDirectory) throws FileNotFoundException,
			IOException {
		File file = new File(sBackupDirectory);
		String backupComment = BackupChooserController.getCommentInBackupFile(file.toPath());
		return backupComment;
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	protected void handleCancel() {
		preferences.setLastWindowParameters(sWindowParams, dialogStage);
		dialogStage.close();
	}

	/**
	 * Called when the user clicks help.
	 */
	@FXML
	protected void handleHelp() {
		mainApp.showNotImplementedYet();
	}
}
