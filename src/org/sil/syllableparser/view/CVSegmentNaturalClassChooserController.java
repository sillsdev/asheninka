// Copyright (c) 2016-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVSegmentOrNaturalClass;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class CVSegmentNaturalClassChooserController extends CheckBoxColumnController implements Initializable {

	protected final class AnalysisWrappingTableCell extends TableCell<CVSegmentOrNaturalClass, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processAnalysisTableCell(this, text, item, empty);
		}
	}

	protected final class WrappingTableCell extends TableCell<CVSegmentOrNaturalClass, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processTableCell(this, text, item, empty);
		}
	}

	@FXML
	private TableView<CVSegmentOrNaturalClass> cvSegmentOrNaturalClassTable;
	@FXML
	private TableColumn<CVSegmentOrNaturalClass, Boolean> checkBoxColumn;
	@FXML
	private TableColumn<CVSegmentOrNaturalClass, String> segOrNCColumn;
	@FXML
	private TableColumn<CVSegmentOrNaturalClass, String> descriptionColumn;
	Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;
	private ApplicationPreferences preferences;

	private CVSegmentOrNaturalClass currentSegmentOrNaturalClass;
	private ObservableList<CVSegmentOrNaturalClass> cvSegmentsOrNaturalClasses = FXCollections
			.observableArrayList();
	private CVNaturalClass naturalClass;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize the table with the three columns.
		checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().checkedProperty());
		checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));
		checkBoxColumn.setEditable(true);
		checkBoxColumnHead.setOnAction((event) -> {
			handleCheckBoxColumnHead();
		});
		segOrNCColumn.setCellValueFactory(cellData -> {
			if (cellData.getValue().isSegment()) {
				return cellData.getValue().segmentOrNaturalClassProperty();
			} else {
				SimpleStringProperty sp = new SimpleStringProperty(Constants.NATURAL_CLASS_PREFIX
						+ cellData.getValue().segmentOrNaturalClassProperty().getValue()
						+ Constants.NATURAL_CLASS_SUFFIX);
				return sp;
			}
		});
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		cvSegmentOrNaturalClassTable.setEditable(true);
		// Custom rendering of the table cell.
		segOrNCColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});

		initializeCheckBoxContextMenu(resources);

		cvSegmentOrNaturalClassTable.setOnKeyPressed(keyEvent -> {
			switch (keyEvent.getCode()) {
			case SPACE: {
				keyEvent.consume();
				CVSegmentOrNaturalClass snc = cvSegmentOrNaturalClassTable.getSelectionModel()
						.getSelectedItem();
				if (snc != null) {
					snc.setChecked(!snc.isChecked());
				}
				break;
			}
			}
		});

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

	public void setData(CVApproach cvApproachData) {
		generateCVSegmentsAndNaturalClasses(cvApproachData);
		// Add observable list data to the table
		cvSegmentOrNaturalClassTable.setItems(cvSegmentsOrNaturalClasses);
		if (cvSegmentOrNaturalClassTable.getItems().size() > 0) {
			// select one
			cvSegmentOrNaturalClassTable.requestFocus();
			cvSegmentOrNaturalClassTable.getSelectionModel().select(0);
			cvSegmentOrNaturalClassTable.getFocusModel().focus(0);
		}
	}

	public void generateCVSegmentsAndNaturalClasses(CVApproach cvApproachData) {
		cvApproach = cvApproachData;
		languageProject = cvApproach.getLanguageProject();

		for (Segment cvSegment : languageProject.getSegmentInventory()) {
			if (cvSegment.isActive()) {
				currentSegmentOrNaturalClass = new CVSegmentOrNaturalClass(cvSegment.getSegment(),
						cvSegment.getDescription(), true, cvSegment.getID(), true);
				cvSegmentsOrNaturalClasses.add(currentSegmentOrNaturalClass);
				setCheckedStatus(cvSegment);
			}
		}
		for (CVNaturalClass cvNaturalClass : cvApproach.getCVNaturalClasses()) {
			if (cvNaturalClass.isActive()) {
				if (cvNaturalClass.getID() != naturalClass.getID()) {
					currentSegmentOrNaturalClass = new CVSegmentOrNaturalClass(
							cvNaturalClass.getNCName(), cvNaturalClass.getDescription(), false,
							cvNaturalClass.getID(), true);
					cvSegmentsOrNaturalClasses.add(currentSegmentOrNaturalClass);
					setCheckedStatus(cvNaturalClass);
				}
			}
		}
	}

	private void setCheckedStatus(SylParserObject sylParserObject) {
		if (SylParserObject.findIndexInListByUuid(
				naturalClass.getSegmentsOrNaturalClasses(), sylParserObject.getID()) > -1) {
			currentSegmentOrNaturalClass.setChecked(true);
		}
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
		naturalClass.getSegmentsOrNaturalClasses().clear();
		// find the segment or natural class with
		// segmentOrNaturalClass.getUuid() and
		// add it to the natural class list
		// we use this method in order to guarantee we get the actual object and
		// not a copy
		for (CVSegmentOrNaturalClass segmentOrNaturalClass : cvSegmentsOrNaturalClasses) {
			if (segmentOrNaturalClass.isChecked()) {
				if (segmentOrNaturalClass.isSegment()) {
					int i = Segment.findIndexInListByUuid(
							languageProject.getSegmentInventory(), segmentOrNaturalClass.getUuid());
					naturalClass.getSegmentsOrNaturalClasses().add(
							languageProject.getSegmentInventory().get(i));
				} else {
					int i = CVNaturalClass.findIndexInListByUuid(
							cvApproach.getCVNaturalClasses(), segmentOrNaturalClass.getUuid());
					naturalClass.getSegmentsOrNaturalClasses().add(
							cvApproach.getCVNaturalClasses().get(i));
				}
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
		preferences.setLastWindowParameters(ApplicationPreferences.LAST_CV_SEGMENT_OR_NATURAL_CLASS, dialogStage);
		dialogStage.close();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		preferences = mainApp.getApplicationPreferences();
		dialogStage = preferences.getLastWindowParameters(ApplicationPreferences.LAST_CV_SEGMENT_OR_NATURAL_CLASS, dialogStage, 400., 400.);
	}

	/**
	 * Called when the user clicks help.
	 */
	@FXML
	private void handleHelp() {
		mainApp.showNotImplementedYet();
	}

	public CVNaturalClass getNaturalClass() {
		return naturalClass;
	}

	public void setNaturalClass(CVNaturalClass naturalClass) {
		this.naturalClass = naturalClass;
	}

	protected void handleCheckBoxSelectAll() {
		for (CVSegmentOrNaturalClass segmentOrNaturalClass : cvSegmentsOrNaturalClasses) {
			segmentOrNaturalClass.setChecked(true);
		}
	}

	protected void handleCheckBoxClearAll() {
		for (CVSegmentOrNaturalClass segmentOrNaturalClass : cvSegmentsOrNaturalClasses) {
			segmentOrNaturalClass.setChecked(false);
		}
	}

	protected void handleCheckBoxToggle() {
		for (CVSegmentOrNaturalClass segmentOrNaturalClass : cvSegmentsOrNaturalClasses) {
			if (segmentOrNaturalClass.isChecked()) {
				segmentOrNaturalClass.setChecked(false);
			} else {
				segmentOrNaturalClass.setChecked(true);
			}
		}
	}

	// code taken from http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return null;
	}

	@Override
	void handlePreviousItem() {
		// nothing to do
	}

	@Override
	void handleNextItem() {
		// nothing to do
	}
}
