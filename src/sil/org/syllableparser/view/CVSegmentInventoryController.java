/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVSegmentOrNaturalClass;
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
import javafx.scene.text.Text;

/**
 * @author Andy Black
 *
 */

public class CVSegmentInventoryController extends CheckBoxColumnController implements Initializable {

	protected final class WrappingTableCell extends TableCell<Segment, String> {
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
					text.setFill(Color.BLACK);
				} else {
					text.setFill(Color.GRAY);
				}
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

	private LanguageProject languageProject;
	private CVApproach cvApproach;
	private Segment currentSegment;

	public CVSegmentInventoryController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// public void initialize() {

		// Initialize the table with the three columns.
		checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().activeProperty());
		checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));
		checkBoxColumn.setEditable(true);
		checkBoxColumnHead.setOnAction((event) -> {
			handleCheckBoxColumnHead();
		});
		initializeCheckBoxContextMenu(resources);
		segmentColumn.setCellValueFactory(cellData -> cellData.getValue().segmentProperty());
		graphemesColumn.setCellValueFactory(cellData -> cellData.getValue().graphemesProperty());
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		;

		// Custom rendering of the table cell.
		segmentColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		graphemesColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new WrappingTableCell();
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
		});
		graphemesField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentSegment != null) {
				currentSegment.setGraphemes(graphemesField.getText());
			}
		});
		descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentSegment != null) {
				currentSegment.setDescription(descriptionField.getText());
			}
		});

		activeCheckBox.setOnAction((event) -> {
			if (currentSegment != null) {
				currentSegment.setActive(activeCheckBox.isSelected());
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
			graphemesField.setText(segment.getGraphemes());
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
			int currentSegmentNumber = cvSegmentTable.getItems().indexOf(currentSegment) + 1;
			this.mainApp.updateStatusBarNumberOfItems(currentSegmentNumber + "/"
					+ cvSegmentTable.getItems().size() + " ");
		}
	}

	public void displayFieldsPerActiveSetting(Segment segment) {
		segmentField.setDisable(!segment.isActive());
		graphemesField.setDisable(!segment.isActive());
		descriptionField.setDisable(!segment.isActive());
	}

	public void setSegment(Segment segment) {
		segmentField.setText(segment.getSegment());
		graphemesField.setText(segment.getGraphemes());
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
		if (cvSegmentTable.getItems().size() > 0) {
			// select one
			cvSegmentTable.requestFocus();
			cvSegmentTable.getSelectionModel().select(0);
			cvSegmentTable.getFocusModel().focus(0);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachEditorController#handleCut()
	 */
	@Override
	void handleCut() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachEditorController#handleCopy()
	 */
	@Override
	void handleCopy() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachEditorController#handlePaste()
	 */
	@Override
	void handlePaste() {
		// TODO Auto-generated method stub

	}

	protected void handleCheckBoxSelectAll() {
		for (Segment segment : languageProject.getSegmentInventory()) {
			segment.setActive(true);
		}
	}

	protected void handleCheckBoxClearAll() {
		for (Segment segment : languageProject.getSegmentInventory()) {
			segment.setActive(false);
		}
	}

	protected void handleCheckBoxToggle() {
		for (Segment segment : languageProject.getSegmentInventory()) {
			if (segment.isActive()) {
				segment.setActive(false);
			} else {
				segment.setActive(true);
			}
		}
	}
}
