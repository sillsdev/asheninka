// Copyright (c) 2016-2017 SIL International
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.BackupFile;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.service.BackupFileRestorer;
import org.sil.syllableparser.service.CVApproachLanguageComparer;
import org.sil.syllableparser.service.CVApproachLanguageComparisonHTMLFormatter;
import org.sil.syllableparser.view.BackupChooserController.WrappingTableCell;
import org.sil.utility.StringUtilities;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class CVComparisonController implements Initializable {

	@FXML
	private TextField directory1Field;
	@FXML
	private TextField directory2Field;
	@FXML
	private RadioButton currentImplementation;
	@FXML
	private RadioButton chosenImplementation;
	@FXML
	private Button firstButton = new Button();
	@FXML
	private Button compareButton = new Button();
	@FXML
	ToggleGroup group;
	@FXML
	WebView browser;
	@FXML
	WebEngine webEngine;

	Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;
	private ApplicationPreferences preferences;

	private String backupDirectory;
	private BackupFile backupFile1;
	private BackupFile backupFile2;
	private String backupDirectory1;
	private String backupDirectory2;
	private ResourceBundle bundle;
	private Locale locale;
	private CVApproach currentCva;
	private CVApproach cva1;
	private CVApproach cva2;
	private String sDataSet1Info;
	private String sDataSet2Info;
	private final String sCurrentImplementationResourceLabel = "radio.current";

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
		dialogStage = preferences.getLastWindowParameters(ApplicationPreferences.LAST_CV_COMPARISON, dialogStage, 533.0, 637.0);
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setBackupDirectoryPath(String directory) {
		backupDirectory = directory;
	}

	public void setData(CVApproach cvApproachData) {
		currentCva = cvApproachData;
	}

	@FXML
	private void handleBrowse1() {
		try {
			String sResultChosen = chooseBackupFileToUse();
			if (!StringUtilities.isNullOrEmpty(sResultChosen)) {
				backupDirectory1 = sResultChosen;
				directory1Field.setText(sResultChosen);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		setCompareButtonDisable();
	}

	protected String chooseBackupFileToUse() throws IOException {
		// Load the fxml file and create a new stage for the popup.
		Stage dialogStage = new Stage();
		String resource = "fxml/BackupChooser.fxml";
		String title = bundle.getString("label.restoreproject");
		FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, dialogStage, resource,
				title);

		BackupChooserController controller = loader.getController();
		controller.setDialogStage(dialogStage);
		controller.setMainApp(mainApp);
		controller.setLocale(locale);
		String backupDirectoryPath = backupDirectory;
		controller.setData(backupDirectoryPath);

		dialogStage.showAndWait();
		String sResultChosen = controller.getBackupFileToUse();
		return sResultChosen;
	}

	@FXML
	private void handleBrowse2() {
		try {
			String sResultChosen = chooseBackupFileToUse();
			if (!StringUtilities.isNullOrEmpty(sResultChosen)) {
				backupDirectory2 = sResultChosen;
				directory2Field.setText(sResultChosen);
			}
		} catch (IOException e) {
			e.printStackTrace();
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
	private void handleCompare() {
		setCVApproachesToUse();
		setCVApproachDataSetInfoToUse();
		
		if (sDataSet1Info.equals(sDataSet2Info)) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle(MainApp.kApplicationTitle);
			alert.setHeaderText(bundle.getString("label.sameimplementationsheader"));
			alert.setContentText(bundle.getString("label.sameimplementationscontent"));
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(mainApp.getNewMainIconImage());
			alert.showAndWait();
			return;
		}

		// sleeper code is from
		// http://stackoverflow.com/questions/26454149/make-javafx-wait-and-continue-with-code
		// We do this so the "Please wait..." message loads and shows in the web
		// view
		Task<Void> sleeper = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				return null;
			}
		};
		sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				CVApproachLanguageComparer comparer = new CVApproachLanguageComparer(cva1, cva2);
				comparer.setDataSet1Info(sDataSet1Info);
				comparer.setDataSet2Info(sDataSet2Info);
				comparer.compare();
				CVApproachLanguageComparisonHTMLFormatter formatter = new CVApproachLanguageComparisonHTMLFormatter(
						comparer, locale, LocalDateTime.now());
				String result = formatter.format();
				webEngine.loadContent(result);
			}
		});
		new Thread(sleeper).start();

		String sPleaseWaitMessage = Constants.PLEASE_WAIT_HTML_BEGINNING
				+ bundle.getString("label.pleasewait") + Constants.PLEASE_WAIT_HTML_MIDDLE
				+ bundle.getString("label.pleasewaitcomparer")
				+ Constants.PLEASE_WAIT_HTML_ENDING;
		webEngine.loadContent(sPleaseWaitMessage);

		// okClicked = true;
		// dialogStage.close();
	}

	protected void setCVApproachesToUse() {
		if (currentImplementation.isSelected()) {
			cva1 = currentCva;
		} else {
			cva1 = getCVApproachFromBackup(backupDirectory1);
		}
		cva2 = getCVApproachFromBackup(backupDirectory2);
	}

	protected void setCVApproachDataSetInfoToUse() {
		try {
			String backupComment;
			if (currentImplementation.isSelected()) {
				this.sDataSet1Info = bundle.getString(sCurrentImplementationResourceLabel);
			} else {
				backupComment = getBackupComment(backupDirectory1);
				sDataSet1Info = backupDirectory1 + " (" + backupComment + ")";
			}
			backupComment = getBackupComment(backupDirectory2);
			sDataSet2Info = backupDirectory2 + " (" + backupComment + ")";
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected String getBackupComment(String sBackupDirectory) throws FileNotFoundException,
			IOException {
		File file = new File(sBackupDirectory);
		String backupComment = BackupChooserController.getCommentInBackupFile(file.toPath());
		return backupComment;
	}

	private CVApproach getCVApproachFromBackup(String backupDirectory) {
		LanguageProject languageProject = new LanguageProject();
		File backupFile = new File(backupDirectory);
		BackupFileRestorer restorer = new BackupFileRestorer(backupFile);
		restorer.doRestore(languageProject, locale);
		return languageProject.getCVApproach();
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		preferences.setLastWindowParameters(ApplicationPreferences.LAST_CV_COMPARISON, dialogStage);
		dialogStage.close();
	}

	/**
	 * Called when the user clicks help.
	 */
	@FXML
	private void handleHelp() {
		mainApp.showNotImplementedYet();
	}
}
