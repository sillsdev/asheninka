/**
 * 
 */
package sil.org.syllableparser.view;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.BackupFile;
import sil.org.syllableparser.service.BackupFileRestorer;
import sil.org.syllableparser.view.BackupChooserController.WrappingTableCell;
import sil.org.utility.StringUtilities;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
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

	}

	protected void setCompareButtonDisable() {
		if (StringUtilities.isNullOrEmpty(backupDirectory1) || StringUtilities.isNullOrEmpty(backupDirectory2)) {
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
		//languageProject = mainApp.getLanguageProject();
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setData(String directory) {
		backupDirectory = directory;
	}
	@FXML
	private void handleBrowse1() {
//		RestoreBackupChooserController controller = new RestoreBackupChooserController();
//		controller.set
		// DirectoryChooser directoryChooser = new DirectoryChooser();
		// File currentDirectory = new File(directory1Field.getText());
		// directoryChooser.setInitialDirectory(currentDirectory);
		//
		// File file = directoryChooser.showDialog(dialogStage);
		// if (file != null) {
		// setData(file.getPath());
		// }
	}

	@FXML
	private void handleBrowse2() {
		// DirectoryChooser directoryChooser = new DirectoryChooser();
		// File currentDirectory = new File(directory1Field.getText());
		// directoryChooser.setInitialDirectory(currentDirectory);
		//
		// File file = directoryChooser.showDialog(dialogStage);
		// if (file != null) {
		// setData(file.getPath());
		// }
	}

	@FXML
	private void handleCurrentImplementation() {

	}
	@FXML
	private void handleChosenImplementation() {

	}
	/**
	 * Called when the user clicks Compare.
	 */
	@FXML
	private void handleCompare() {
//		int i = restoreBackupTable.getSelectionModel().getSelectedIndex();
//		BackupFile bup = backupFiles.get(i);
//		String backupFileToUse = backupDirectory + File.separator + bup.getFilePath();
//		File backupFile = new File(backupFileToUse);
//		BackupFileRestorer restorer = new BackupFileRestorer(backupFile);
//		restorer.doRestore(languageProject, locale);
//
//		// Force user to do a file save as so they do not overwrite the current data file
//		// with the restored data (unless they so choose).
//		String sFileFilterDescription = bundle.getString("file.filterdescription");
//		String syllableParserFilterDescription = sFileFilterDescription + " ("
//				+ Constants.ASHENINKA_DATA_FILE_EXTENSIONS + ")";
//		ControllerUtilities.doFileSaveAs(mainApp, true, syllableParserFilterDescription);
//
//		okClicked = true;
//		dialogStage.close();
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
