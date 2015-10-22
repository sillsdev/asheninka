/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.model.CVApproach;
import sil.org.syllableparser.model.entity.CVSegment;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * @author Andy Black
 *
 */

public class CVSegmentInventoryController extends SylParserBaseController
		implements Initializable {

	protected final class WrappingTableCell extends
			TableCell<CVSegment, String> {
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
				text.wrappingWidthProperty().bind(
						getTableColumn().widthProperty());
				setGraphic(text);
			}
		}
	}

	@FXML
	private TableView<CVSegment> cvSegmentTable;
	@FXML
	private TableColumn<CVSegment, String> segmentColumn;
	@FXML
	private TableColumn<CVSegment, String> graphemesColumn;
	@FXML
	private TableColumn<CVSegment, String> descriptionColumn;

	@FXML
	private TextField segmentField;
	@FXML
	private TextField graphemesField;
	@FXML
	private TextField descriptionField;

	private CVApproach cvApproach;
	private CVSegment currentSegment;

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
		segmentColumn.setCellValueFactory(cellData -> cellData.getValue()
				.segmentProperty());
		graphemesColumn.setCellValueFactory(cellData -> cellData.getValue()
				.graphemesProperty());
		descriptionColumn.setCellValueFactory(cellData -> cellData.getValue()
				.descriptionProperty());
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
		cvSegmentTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showCVSegmentDetails(newValue));

		// Handle TextField text changes.
		segmentField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					currentSegment.setSegment(segmentField.getText());
				});
		graphemesField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					currentSegment.setGraphemes(graphemesField.getText());
				});
		descriptionField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					currentSegment.setDescription(descriptionField.getText());
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
	private void showCVSegmentDetails(CVSegment segment) {
		currentSegment = segment;
		if (segment != null) {
			// Fill the text fields with info from the segment object.
			segmentField.setText(segment.getSegment());
			graphemesField.setText(segment.getGraphemes());
			descriptionField.setText(segment.getDescription());
		} else {
			// Segment is null, remove all the text.
			segmentField.setText("");
			graphemesField.setText("");
			descriptionField.setText("");
		}
	}

	public void setSegment(CVSegment segment) {
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

		// Add observable list data to the table
		cvSegmentTable.setItems(cvApproachData.getCVSegmentInventory());
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
		CVSegment newSegment = new CVSegment();
		cvApproach.getCVSegmentInventory().add(newSegment);
		int i = cvApproach.getCVSegmentInventory().size() - 1;
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
//		CVSegment newSegment = new CVSegment();
//		cvApproach.getCVSegmentInventory().add(newSegment);
//		int i = cvApproach.getCVSegmentInventory().size() - 1;
//		cvSegmentTable.requestFocus();
//		cvSegmentTable.getSelectionModel().select(i);
//		cvSegmentTable.getFocusModel().focus(i);
	}
}
