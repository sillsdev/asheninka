/**
 * 
 */
package sil.org.syllableparser.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.BackupFile;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.service.BackupFileRestorer;
import sil.org.syllableparser.view.BackupChooserController.WrappingTableCell;
import sil.org.utility.StringUtilities;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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
	private RadioButton currentImplementation = new RadioButton();
	@FXML
	private RadioButton chosenImplementation = new RadioButton();
	@FXML
	private Button firstButton = new Button();
	@FXML
	private Button compareButton = new Button();
	@FXML
	ToggleGroup group;

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
		currentImplementation.setSelected(true);
		chosenImplementation.setText(bundle.getString("radio.choosebackup"));
		setFirstBrowseButtonAndText();
		setCompareButtonDisable();

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
		currentImplementation.setSelected(true);
		firstButton.setDisable(true);
		setFirstBrowseButtonAndText();
	}

	@FXML
	public void handleChosenImplementation() {
		chosenImplementation.setSelected(true);
		firstButton.setDisable(false);
		setFirstBrowseButtonAndText();
		setCompareButtonDisable();
	}

	/**
	 * Called when the user clicks Compare.
	 */
	@FXML
	private void handleCompare() {
		// int i = restoreBackupTable.getSelectionModel().getSelectedIndex();
		// BackupFile bup = backupFiles.get(i);
		// String backupFileToUse = backupDirectory + File.separator +
		// bup.getFilePath();
		// File backupFile = new File(backupFileToUse);
		// BackupFileRestorer restorer = new BackupFileRestorer(backupFile);
		// restorer.doRestore(languageProject, locale);
		//
		// // Force user to do a file save as so they do not overwrite the
		// current data file
		// // with the restored data (unless they so choose).
		// String sFileFilterDescription =
		// bundle.getString("file.filterdescription");
		// String syllableParserFilterDescription = sFileFilterDescription +
		// " ("
		// + Constants.ASHENINKA_DATA_FILE_EXTENSIONS + ")";
		// ControllerUtilities.doFileSaveAs(mainApp, true,
		// syllableParserFilterDescription);
		//
		// okClicked = true;
		// dialogStage.close();
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