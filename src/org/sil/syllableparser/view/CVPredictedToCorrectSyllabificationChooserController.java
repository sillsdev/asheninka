// Copyright (c) 2016-2017 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.cvapproach.CVPredictedSyllabification;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class CVPredictedToCorrectSyllabificationChooserController implements Initializable {

	@FXML
	private TableView<CVPredictedSyllabification> cvPredictedSyllabificationTable;
	@FXML
	private Label whenTableIsEmptyMessage;
	@FXML
	private TableColumn<CVPredictedSyllabification, Boolean> checkBoxColumn;
	@FXML
	private TableColumn<CVPredictedSyllabification, String> predictedSyllabificationColumn;
	@FXML
	private CheckBox checkBoxColumnHead;
	private ContextMenu checkBoxContextMenu = new ContextMenu();
	private MenuItem selectAll = new MenuItem("Select All");
	private MenuItem clearAll = new MenuItem("Clear All");
	private MenuItem toggle = new MenuItem("Toggle");

	Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;
	private ApplicationPreferences preferences;

	private CVApproach cvApproach;
	private SHApproach shApproach;
	private ObservableList<Word> words = FXCollections.observableArrayList();
	private CVPredictedSyllabification currentPredictedSyllabification;
	private ObservableList<CVPredictedSyllabification> cvPredictedSyllabifications = FXCollections
			.observableArrayList();
	private Word word;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize the table with the three columns.
		checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().checkedProperty());
		checkBoxColumnHead.setOnAction((event) -> {
			handleCheckBoxColumnHead();
		});
		predictedSyllabificationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.predictedSyllabificationProperty());
		// not working... predictedSyllabificationColumn.getStyleClass().add("predictedSyllabificationColumn");
		checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));
		checkBoxColumn.setEditable(true);
		cvPredictedSyllabificationTable.setEditable(true);
		whenTableIsEmptyMessage = new Label(resources.getString("label.nopredicted") +
				resources.getString("menu.syllabifywords"));
		cvPredictedSyllabificationTable.setPlaceholder(whenTableIsEmptyMessage);

		initializeCheckBoxContextMenu(resources);
	}

	private void initializeCheckBoxContextMenu(ResourceBundle bundle) {
		// set up context menu
		selectAll.setOnAction((event) -> {
			handleCheckBoxSelectAll();
		});
		clearAll.setOnAction((event) -> {
			handleCheckBoxClearAll();
		});
		toggle.setOnAction((event) -> {
			handleCheckBoxToggle();
		});
		selectAll.setText(bundle.getString("checkbox.context.menu.selectall"));
		clearAll.setText(bundle.getString("checkbox.context.menu.clearall"));
		toggle.setText(bundle.getString("checkbox.context.menu.toggle"));
		checkBoxContextMenu.getItems().addAll(selectAll, clearAll, toggle);
		checkBoxColumnHead.setContextMenu(checkBoxContextMenu);
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

	/**
	 * Is called by the main application to give a reference back to itself.
	 * @param words TODO
	 * @param cvApproachController
	 */
	public void setData(CVApproach cvApproachData, ObservableList<Word> words) {
		cvApproach = cvApproachData;
		this.words = words;
		createListOfDifferentWords(words);
	}

	private void createListOfDifferentWords(ObservableList<Word> words) {
		for (Word cvWord : words) {
			if (!cvWord.cvPredictedSyllabificationProperty().isEmpty().getValue()) {
				currentPredictedSyllabification = new CVPredictedSyllabification(
						cvWord.getCVPredictedSyllabification(), cvWord.getID());
				cvPredictedSyllabifications.add(currentPredictedSyllabification);
			}
		}
		// Add observable list data to the table
		cvPredictedSyllabificationTable.setItems(cvPredictedSyllabifications);
		if (cvPredictedSyllabificationTable.getItems().size() > 0) {
			// select one
			cvPredictedSyllabificationTable.requestFocus();
			cvPredictedSyllabificationTable.getSelectionModel().select(0);
			cvPredictedSyllabificationTable.getFocusModel().focus(0);
		}
	}

	public void setData(SHApproach shApproachData, ObservableList<Word> words) {
		shApproach = shApproachData;
		this.words = words;
		createListOfDifferentWords(words);
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
	private void handleOk() {
		for (CVPredictedSyllabification predictedSyllabification : cvPredictedSyllabifications) {
			if (predictedSyllabification.isChecked()) {
				int i = Word.findIndexInListByUuid(words,
						predictedSyllabification.getUuid());
				word = words.get(i);
				word.setCorrectSyllabification(predictedSyllabification
						.getPredictedSyllabification());
			}
		}
		okClicked = true;
		handleCancel();
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		preferences.setLastWindowParameters(ApplicationPreferences.LAST_CV_WORDS_PREDICTED_VS_CORRECT, dialogStage);
		dialogStage.close();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		preferences = mainApp.getApplicationPreferences();
		dialogStage = preferences.getLastWindowParameters(ApplicationPreferences.LAST_CV_WORDS_PREDICTED_VS_CORRECT, dialogStage, 397., 607.);
	}

	/**
	 * Called when the user clicks help.
	 */
	@FXML
	private void handleHelp() {
		mainApp.showNotImplementedYet();
	}

	/**
	 * Called when the user clicks on the check box column header
	 */
	@FXML
	private void handleCheckBoxColumnHead() {
		// make sure the check box stays checked
		checkBoxColumnHead.setSelected(true);
		// show the check box context menu
		checkBoxColumnHead.contextMenuProperty().get()
				.show(checkBoxColumnHead, Side.BOTTOM, 0.0, 0.0);
	}

	// public void setNaturalClass(CVNaturalClass naturalClass) {
	// this.naturalClass = naturalClass;
	// }
	//
	void handleCheckBoxSelectAll() {
		for (CVPredictedSyllabification predictedSyllabification : cvPredictedSyllabifications) {
			predictedSyllabification.setChecked(true);
		}
	}

	void handleCheckBoxClearAll() {
		for (CVPredictedSyllabification predictedSyllabification : cvPredictedSyllabifications) {
			predictedSyllabification.setChecked(false);
		}
	}

	void handleCheckBoxToggle() {
		for (CVPredictedSyllabification predictedSyllabification : cvPredictedSyllabifications) {
			if (predictedSyllabification.isChecked()) {
				predictedSyllabification.setChecked(false);
			} else {
				predictedSyllabification.setChecked(true);
			}
		}
	}

}
