// Copyright (c) 2025 SIL International
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
import org.sil.syllableparser.model.hyphenapproach.HyphenApproach;
import org.sil.syllableparser.model.hyphenapproach.HyphenClass;

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
public class HyphenSegmentChooserController extends TableViewWithCheckBoxColumnController {

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
	private TableView<Segment> hyphenSegmentTable;
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

	private ObservableList<Segment> hyphenSegments = FXCollections.observableArrayList();
	private HyphenClass hyphenClass;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		super.setApproach(ApplicationPreferences.HYPHEN_SEGMENT_CHOOSER);
		super.setTableView(hyphenSegmentTable);
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
		hyphenSegmentTable.setEditable(true);
		// Custom rendering of the table cell.
		segmentColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});

		initializeCheckBoxContextMenu(resources);

		hyphenSegmentTable.setOnKeyPressed(keyEvent -> {
			switch (keyEvent.getCode()) {
			case SPACE: {
				keyEvent.consume();
				Segment seg = hyphenSegmentTable.getSelectionModel().getSelectedItem();
				if (seg != null) {
					seg.setChecked(!seg.isChecked());
				}
				break;
			}
			case ENTER:
				handleOk();
				break;
			default:
				break;
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

	public void setData(HyphenApproach hypApproachData) {
		generateSegments(hypApproachData);
		// Add observable list data to the table
		hyphenSegmentTable.setItems(hyphenSegments);
		if (hyphenSegmentTable.getItems().size() > 0) {
			// select one
			hyphenSegmentTable.requestFocus();
			hyphenSegmentTable.getSelectionModel().select(0);
			hyphenSegmentTable.getFocusModel().focus(0);
		}
	}

	public void generateSegments(HyphenApproach hypApproachData) {
		hyphenApproach = hypApproachData;
		languageProject = hyphenApproach.getLanguageProject();

		for (Segment segment : languageProject.getSegmentInventory()) {
			if (segment.isActive()) {
				hyphenSegments.add(segment);
				if (hyphenClass.getSegments().contains(segment)) {
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
		hyphenClass.getSegments().clear();
		// find the segment or natural class with
		// segmentOrNaturalClass.getUuid() and
		// add it to the natural class list
		// we use this method in order to guarantee we get the actual object and
		// not a copy
		for (Segment segment : hyphenSegments) {
			if (segment.isChecked()) {
				int i = Segment.findIndexInListByUuid(languageProject.getSegmentInventory(),
						segment.getID());
				hyphenClass.getSegments().add(languageProject.getSegmentInventory().get(i));
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
		preferences.setLastWindowParameters(ApplicationPreferences.LAST_HYPHEN_SEGMENT, dialogStage);
		dialogStage.close();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		preferences = mainApp.getApplicationPreferences();
		dialogStage = preferences.getLastWindowParameters(ApplicationPreferences.LAST_HYPHEN_SEGMENT,
				dialogStage, 400., 400.);
	}

	/**
	 * Called when the user clicks help.
	 */
	@FXML
	private void handleHelp() {
		mainApp.showNotImplementedYet();
	}

	public HyphenClass getHyphenClass() {
		return hyphenClass;
	}

	public void setHyphenClass(HyphenClass naturalClass) {
		this.hyphenClass = naturalClass;
	}

	protected void handleCheckBoxSelectAll() {
		for (Segment segment : hyphenSegments) {
			segment.setChecked(true);
		}
	}

	protected void handleCheckBoxClearAll() {
		for (Segment segment : hyphenSegments) {
			segment.setChecked(false);
		}
	}

	protected void handleCheckBoxToggle() {
		for (Segment segment : hyphenSegments) {
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
