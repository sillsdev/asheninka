// Copyright (c) 2016-2021 SIL International
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
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
public class SHSegmentChooserController extends TableViewWithCheckBoxColumnController {

	protected final class AnalysisWrappingTableCell extends
			TableCell<Segment, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processAnalysisTableCell(this, text, item, empty);
		}
	}

	protected final class WrappingTableCell extends TableCell<Segment, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processVernacularTableCell(this, text, item, empty);
		}
	}

	@FXML
	private TableView<Segment> shSegmentTable;
	@FXML
	private TableColumn<Segment, Boolean> checkBoxColumn;
	@FXML
	private TableColumn<Segment, String> segmentColumn;
	@FXML
	private TableColumn<Segment, String> descriptionColumn;
	Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;
	private ApplicationPreferences preferences;

	private Segment currentSegment;
	private ObservableList<Segment> shSegments = FXCollections.observableArrayList();
	private SHNaturalClass naturalClass;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		super.setApproach(ApplicationPreferences.SH_SEGMENT_CHOOSER);
		super.setTableView(shSegmentTable);
		super.initialize(location, resources);

		// Initialize the table with the three columns.
		checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().checkedProperty());
		checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));
		checkBoxColumn.setEditable(true);
		checkBoxColumnHead.setOnAction((event) -> {
			handleCheckBoxColumnHead();
		});
		segmentColumn.setCellValueFactory(cellData -> {
			return cellData.getValue().segmentProperty();
		});
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		shSegmentTable.setEditable(true);
		// Custom rendering of the table cell.
		segmentColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});

		initializeCheckBoxContextMenu(resources);

		shSegmentTable.setOnKeyPressed(keyEvent -> {
			switch (keyEvent.getCode()) {
			case SPACE: {
				keyEvent.consume();
				Segment seg = shSegmentTable.getSelectionModel().getSelectedItem();
				if (seg != null) {
					seg.setChecked(!seg.isChecked());
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

	public void setData(SHApproach shApproachData) {
		generateSegments(shApproachData);
		// Add observable list data to the table
		shSegmentTable.setItems(shSegments);
		if (shSegmentTable.getItems().size() > 0) {
			// select one
			shSegmentTable.requestFocus();
			shSegmentTable.getSelectionModel().select(0);
			shSegmentTable.getFocusModel().focus(0);
		}
	}

	public void generateSegments(SHApproach shApproachData) {
		shApproach = shApproachData;
		languageProject = shApproach.getLanguageProject();

		for (Segment segment : languageProject.getSegmentInventory()) {
			if (segment.isActive()) {
				shSegments.add(segment);
				if (naturalClass.getSegments().contains(segment)) {
					segment.setChecked(true);
				} else {
					segment.setChecked(false);
				}
			}
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
		naturalClass.getSegments().clear();
		// find the segment or natural class with
		// segmentOrNaturalClass.getUuid() and
		// add it to the natural class list
		// we use this method in order to guarantee we get the actual object and
		// not a copy
		for (Segment segment : shSegments) {
			if (segment.isChecked()) {
				int i = Segment.findIndexInListByUuid(languageProject.getSegmentInventory(),
						segment.getID());
				naturalClass.getSegments().add(languageProject.getSegmentInventory().get(i));
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
		preferences.setLastWindowParameters(ApplicationPreferences.LAST_SH_SEGMENT, dialogStage);
		dialogStage.close();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		preferences = mainApp.getApplicationPreferences();
		dialogStage = preferences.getLastWindowParameters(ApplicationPreferences.LAST_SH_SEGMENT,
				dialogStage, 400., 400.);
	}

	/**
	 * Called when the user clicks help.
	 */
	@FXML
	private void handleHelp() {
		mainApp.showNotImplementedYet();
	}

	public SHNaturalClass getNaturalClass() {
		return naturalClass;
	}

	public void setNaturalClass(SHNaturalClass naturalClass) {
		this.naturalClass = naturalClass;
	}

	protected void handleCheckBoxSelectAll() {
		for (Segment segment : shSegments) {
			segment.setChecked(true);
		}
	}

	protected void handleCheckBoxClearAll() {
		for (Segment segment : shSegments) {
			segment.setChecked(false);
		}
	}

	protected void handleCheckBoxToggle() {
		for (Segment segment : shSegments) {
			if (segment.isChecked()) {
				segment.setChecked(false);
			} else {
				segment.setChecked(true);
			}
		}
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
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
