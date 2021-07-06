/**
 * Copyright (c) 2016-2021 SIL International 
 * This software is licensed under the LGPL, version 2.1 or later 
 * (http://www.gnu.org/licenses/lgpl-2.1.html) 
 */
package org.sil.syllableparser.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.zip.ZipFile;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.BackupFile;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class BackupChooserController extends TableViewController {
	protected final class WrappingTableCell extends TableCell<BackupFile, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (item == null || empty) {
				setText(null);
				setStyle("");
			} else {
				text = new Text(item.toString());
				// Get it to wrap.
				text.wrappingWidthProperty().bind(getTableColumn().widthProperty());
				setGraphic(text);
			}
		}
	}

	@FXML
	protected TableView<BackupFile> restoreBackupTable;
	@FXML
	private TableColumn<BackupFile, String> fileNameColumn;
	@FXML
	private TableColumn<BackupFile, String> dateColumn;
	@FXML
	private TableColumn<BackupFile, String> descriptionColumn;
	@FXML
	private TextField directoryField;

	ObservableList<BackupFile> backupFiles = FXCollections.observableArrayList();

	Stage dialogStage;
	protected boolean okClicked = false;
	protected MainApp mainApp;

	protected String backupDirectory;
	protected ResourceBundle bundle;
	protected String backupFileToUse;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		super.setApproach(ApplicationPreferences.BACKUP_CHOOSER);
		super.setTableView(restoreBackupTable);
		super.initialize(location, resources);

		bundle = resources;
		// Initialize the table with the three columns.
		fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().fileNameProperty());
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

		// Custom rendering of the table cell.
		descriptionColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		makeColumnHeaderWrappable(descriptionColumn);
		restoreBackupTable.setEditable(true);
		backupFileToUse = "";
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
	 * @param directory
	 *            TODO
	 * @param cvApproachController
	 */
	public void setData(String directory) {
		backupDirectory = directory;
		directoryField.setText(directory);

		backupFiles.clear();
		Path path = Paths.get(directory);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*."
				+ Constants.ASHENINKA_BACKUP_FILE_EXTENSION)) {
			for (Path fileInDirectory : stream) {
				String fileName = fileInDirectory.getFileName().toString();
				String backupComment = getCommentInBackupFile(fileInDirectory);
				BackupFile bup = new BackupFile(fileName, backupComment);
				backupFiles.add(bup);
			}
		} catch (IOException x) {
			// IOException can never be thrown by the iteration.
			// In this snippet, it can // only be thrown by newDirectoryStream.
			System.err.println(x);
		}

		// set up default sorting: by file name (in regular order) and then by
		// date (in reverse order)
		Comparator<BackupFile> myComparator = (bup1, bup2) -> {
			if (bup1.getFileName().equals(bup2.getFileName())) {
				if (bup1.getDate().compareTo(bup2.getDate()) > 0) {
					return -1;
				} else {
					return 1;
				}
			} else {
				return bup1.getFileName().compareTo(bup2.getFileName());
			}
		};
		backupFiles.sort(myComparator);

		// Add observable list data to the table
		restoreBackupTable.setItems(backupFiles);
		restoreBackupTable.refresh();
		if (restoreBackupTable.getItems().size() > 0) {
			// select one
			restoreBackupTable.requestFocus();
			restoreBackupTable.getSelectionModel().select(0);
			restoreBackupTable.getFocusModel().focus(0);
		}
	}

	public static String getCommentInBackupFile(Path fileName) throws FileNotFoundException, IOException {
		ZipFile zipFile = new ZipFile(fileName.toFile());
		String backupComment = zipFile.getComment();
		zipFile.close();
		return backupComment;
	}

	/**
	 * Returns true if the user clicked OK, false otherwise.
	 * 
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	/**
	 * Called when the user clicks OK.
	 */
	@FXML
	protected void handleOk() {
		int i = restoreBackupTable.getSelectionModel().getSelectedIndex();
		BackupFile bup = backupFiles.get(i);
		backupFileToUse = backupDirectory + File.separator + bup.getFilePath();
		
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
		languageProject = mainApp.getLanguageProject();
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getBackupFileToUse() {
		return backupFileToUse;
	}

	/**
	 * Called when the user clicks help.
	 */
	@FXML
	private void handleHelp() {
		mainApp.showNotImplementedYet();
	}

	@FXML
	private void handleBrowse() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File currentDirectory = new File(directoryField.getText());
		directoryChooser.setInitialDirectory(currentDirectory);

		File file = directoryChooser.showDialog(dialogStage);
		if (file != null) {
			setData(file.getPath());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sil.syllableparser.view.ApproachEditorController#handleInsertNewItem
	 * ()
	 */
	@Override
	void handleInsertNewItem() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sil.syllableparser.view.ApproachEditorController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		// TODO Auto-generated method stub

	}

	@Override
	void handlePreviousItem() {
	}

	@Override
	void handleNextItem() {
	}


	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return new TextField[] { directoryField };
	}

}
