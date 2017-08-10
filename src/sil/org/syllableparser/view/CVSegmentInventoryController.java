// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.SylParserObject;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVSegmentOrNaturalClass;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author Andy Black
 *
 */

public class CVSegmentInventoryController extends SylParserBaseController implements Initializable {

	protected final class AnalysisWrappingTableCell extends TableCell<Segment, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (item == null || empty) {
				setText(null);
				setStyle("");
			} else {
				setStyle("");
				text = new Text(item.toString());
				// Get it to wrap.
				text.wrappingWidthProperty().bind(getTableColumn().widthProperty());
				Segment seg = (Segment) this.getTableRow().getItem();
				if (seg != null && seg.isActive()) {
					text.setFill(Constants.ACTIVE);
				} else {
					text.setFill(Constants.INACTIVE);
				}
				text.setFont(languageProject.getAnalysisLanguage().getFont());
				setGraphic(text);
			}
		}
	}

	protected final class VernacularWrappingTableCell extends TableCell<Segment, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (item == null || empty) {
				setText(null);
				setStyle("");
			} else {
				setStyle("");
				text = new Text(item.toString());
				// Get it to wrap.
				text.wrappingWidthProperty().bind(getTableColumn().widthProperty());
				Segment seg = (Segment) this.getTableRow().getItem();
				if (seg != null && seg.isActive()) {
					text.setFill(Constants.ACTIVE);
				} else {
					text.setFill(Constants.INACTIVE);
				}
				text.setFont(languageProject.getVernacularLanguage().getFont());
				setGraphic(text);
			}
		}
	}

	@FXML
	private TableView<Segment> cvSegmentTable;
	@FXML
	private TableColumn<Segment, String> segmentColumn;
	@FXML
	private TableColumn<Segment, String> graphemesColumn;
	@FXML
	private TableColumn<Segment, String> descriptionColumn;
	@FXML
	private TableColumn<Segment, Boolean> checkBoxColumn;
	@FXML
	private CheckBox checkBoxColumnHead;

	@FXML
	private TextField segmentField;
	@FXML
	private TextField graphemesField;
	@FXML
	private TextField descriptionField;
	@FXML
	private CheckBox activeCheckBox;

	private Segment currentSegment;

	public CVSegmentInventoryController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		segmentColumn.setCellValueFactory(cellData -> cellData.getValue().segmentProperty());
		graphemesColumn.setCellValueFactory(cellData -> cellData.getValue().graphsRepresentationProperty());
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		;

		// Custom rendering of the table cell.
		segmentColumn.setCellFactory(column -> {
			return new VernacularWrappingTableCell();
		});
		graphemesColumn.setCellFactory(column -> {
			return new VernacularWrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});

		makeColumnHeaderWrappable(segmentColumn);
		makeColumnHeaderWrappable(graphemesColumn);
		makeColumnHeaderWrappable(descriptionColumn);

		// Clear cv segment details.
		showCVSegmentDetails(null);

		// Listen for selection changes and show the details when changed.
		cvSegmentTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showCVSegmentDetails(newValue));

		// Handle TextField text changes.
		segmentField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentSegment != null) {
				currentSegment.setSegment(segmentField.getText());
			}
			if (languageProject != null) {
				segmentField.setFont(languageProject.getVernacularLanguage().getFont());
			}
		});
		graphemesField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentSegment != null) {
				currentSegment.setGraphsRepresentation(graphemesField.getText());
			}
			if (languageProject != null) {
				graphemesField.setFont(languageProject.getVernacularLanguage().getFont());
			}
		});
		descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentSegment != null) {
				currentSegment.setDescription(descriptionField.getText());
			}
			if (languageProject != null) {
				descriptionField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});

		activeCheckBox.setOnAction((event) -> {
			if (currentSegment != null) {
				currentSegment.setActive(activeCheckBox.isSelected());
				forceTableRowToRedisplayPerActiveSetting(currentSegment);
				displayFieldsPerActiveSetting(currentSegment);
			}
		});

		// Use of Enter move focus to next item.
		segmentField.setOnAction((event) -> {
			graphemesField.requestFocus();
		});
		graphemesField.setOnAction((event) -> {
			descriptionField.requestFocus();
		});

		segmentField.requestFocus();
	}

	private void forceTableRowToRedisplayPerActiveSetting(Segment segment) {
		// we need to make the content of the row cells change in order for
		// the cell factory to fire.
		// We do this by getting the value, blanking it, and then restoring it.
		String temp = segment.getSegment();
		segment.setSegment("");
		segment.setSegment(temp);
		temp = segment.getGraphsRepresentation();
		segment.setGraphsRepresentation("");
		segment.setGraphsRepresentation(temp);
		temp = segment.getDescription();
		segment.setDescription("");
		segment.setDescription(temp);
	}

	/**
	 * Fills all text fields to show details about the CV segment. If the
	 * specified segment is null, all text fields are cleared.
	 * 
	 * @param segment
	 *            the segment or null
	 */
	private void showCVSegmentDetails(Segment segment) {
		currentSegment = segment;
		if (segment != null) {
			// Fill the text fields with info from the segment object.
			segmentField.setText(segment.getSegment());
			graphemesField.setText(segment.getGraphsRepresentation());
			descriptionField.setText(segment.getDescription());
			activeCheckBox.setSelected(segment.isActive());
			displayFieldsPerActiveSetting(segment);
		} else {
			// Segment is null, remove all the text.
			segmentField.setText("");
			graphemesField.setText("");
			descriptionField.setText("");
			activeCheckBox.setSelected(true);
		}

		if (segment != null) {
			int currentSegmentNumber = cvSegmentTable.getItems().indexOf(currentSegment);
			this.mainApp.updateStatusBarNumberOfItems((currentSegmentNumber + 1) + "/"
					+ cvSegmentTable.getItems().size() + " ");
			mainApp.getApplicationPreferences().setLastCVSegmentInventoryViewItemUsed(
					currentSegmentNumber);
		}
	}

	@Override
	public void setViewItemUsed(int value) {
		int max = cvSegmentTable.getItems().size();
		value = adjustIndexValue(value, max);
		cvSegmentTable.getSelectionModel().clearAndSelect(value);
	}

	public void displayFieldsPerActiveSetting(Segment segment) {
		segmentField.setDisable(!segment.isActive());
		graphemesField.setDisable(!segment.isActive());
		descriptionField.setDisable(!segment.isActive());
	}

	public void setSegment(Segment segment) {
		segmentField.setText(segment.getSegment());
		graphemesField.setText(segment.getGraphsRepresentation());
		descriptionField.setText(segment.getDescription());
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param cvApproachController
	 */
	public void setData(CVApproach cvApproachData) {
		cvApproach = cvApproachData;
		languageProject = cvApproach.getLanguageProject();

		// Add observable list data to the table
		cvSegmentTable.setItems(languageProject.getSegmentInventory());
		int max = cvSegmentTable.getItems().size();
		if (max > 0) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					int iLastIndex = mainApp.getApplicationPreferences()
							.getLastCVSegmentInventoryViewItemUsed();
					iLastIndex = adjustIndexValue(iLastIndex, max);
					cvSegmentTable.requestFocus();
					cvSegmentTable.getSelectionModel().select(iLastIndex);
					cvSegmentTable.getFocusModel().focus(iLastIndex);
					cvSegmentTable.scrollTo(iLastIndex);
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		Segment newSegment = new Segment();
		languageProject.getSegmentInventory().add(newSegment);
		int i = languageProject.getSegmentInventory().size() - 1;
		cvSegmentTable.requestFocus();
		cvSegmentTable.getSelectionModel().select(i);
		cvSegmentTable.getFocusModel().focus(i);
		cvSegmentTable.scrollTo(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		int i = languageProject.getSegmentInventory().indexOf(currentSegment);
		currentSegment = null;
		if (i >= 0) {
			languageProject.getSegmentInventory().remove(i);
		}

	}

	protected void handleCheckBoxSelectAll() {
		for (Segment segment : languageProject.getSegmentInventory()) {
			segment.setActive(true);
			forceTableRowToRedisplayPerActiveSetting(segment);
		}
	}

	protected void handleCheckBoxClearAll() {
		for (Segment segment : languageProject.getSegmentInventory()) {
			segment.setActive(false);
			forceTableRowToRedisplayPerActiveSetting(segment);
		}
	}

	protected void handleCheckBoxToggle() {
		for (Segment segment : languageProject.getSegmentInventory()) {
			if (segment.isActive()) {
				segment.setActive(false);
			} else {
				segment.setActive(true);
			}
			forceTableRowToRedisplayPerActiveSetting(segment);
		}
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return new TextField[] { segmentField, graphemesField, descriptionField };
	}

}
