/**
 * 
 */
package sil.org.syllableparser.view;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.BackupFile;
import sil.org.syllableparser.view.RestoreBackupChooserController.WrappingTableCell;
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
	private RadioButton currentImplementation;
	@FXML
	private RadioButton chosenImplementation;
	@FXML
	private Button firstButton;

	Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;

	private BackupFile currentBackupFile1;
	private BackupFile currentBackupFile2;
	private String backupDirectory1;
	private String backupDirectory2;
	private ResourceBundle bundle;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
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


	@FXML
	private void handleBrowse1() {
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
}
