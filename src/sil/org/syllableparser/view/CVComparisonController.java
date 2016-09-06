/**
 * 
 */
package sil.org.syllableparser.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.BackupFile;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.service.BackupFileRestorer;
import sil.org.syllableparser.service.CVApproachLanguageComparer;
import sil.org.syllableparser.service.CVApproachLanguageComparisonHTMLFormatter;
import sil.org.syllableparser.view.BackupChooserController.WrappingTableCell;
import sil.org.utility.StringUtilities;
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
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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
		currentImplementation.setText(bundle.getString("radio.current"));
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
			directory1Field.setText(bundle.getString("radio.current"));
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
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		// languageProject = mainApp.getLanguageProject();
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
		firstButton.setDisable(true);
		setFirstBrowseButtonAndText();
	}

	@FXML
	public void handleChosenImplementation() {
		firstButton.setDisable(false);
		setFirstBrowseButtonAndText();
		setCompareButtonDisable();
	}

	/**
	 * Called when the user clicks Compare.
	 */
	@FXML
	private void handleCompare() {

		// sleeper code is from
		// http://stackoverflow.com/questions/26454149/make-javafx-wait-and-continue-with-code
		// We do this so the "Please wait..." message loads and shows in the web view
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
				setCVApproachesToUse();
				CVApproachLanguageComparer comparer = new CVApproachLanguageComparer(cva1, cva2);
				comparer.compare();
				CVApproachLanguageComparisonHTMLFormatter formatter = new CVApproachLanguageComparisonHTMLFormatter(
						comparer, locale, LocalDateTime.now());
				String result = formatter.format();
				webEngine.loadContent(result);
			}
		});
		new Thread(sleeper).start();

		String sPleaseWaitMessage = Constants.PLEASE_WAIT_HTML_BEGINNING
				+ bundle.getString("label.pleasewait") + Constants.PLEASE_WAIT_HTML_ENDING;
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
