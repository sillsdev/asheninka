/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.model.CVApproach;
import sil.org.syllableparser.model.CVSegment;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * @author Andy Black
 *
 */

public class CVSegmentInventoryController extends ApproachController implements Initializable {
	@FXML
	private TableView<CVSegment> cvSegmentTable;
	@FXML
	private TableColumn<CVSegment, String> segmentColumn;
	@FXML
	private TableColumn<CVSegment, String> graphemesColumn;
	@FXML
	private TableColumn<CVSegment, String> commentColumn;

	@FXML
	private TextField segmentField;
	@FXML
	private TextField graphemesField;
	@FXML
	private TextField commentField;
	
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
	//public void initialize() {

		 // Initialize the table with the three columns.
        segmentColumn.setCellValueFactory(cellData -> cellData.getValue().segmentProperty());
        graphemesColumn.setCellValueFactory(cellData -> cellData.getValue().graphemesProperty());
        commentColumn.setCellValueFactory(cellData -> cellData.getValue().commentPropery());;
        
        // Clear cv segment details.
        showCVSegmentDetails(null);
        
        // Listen for selection changes and show the  details when changed.
        cvSegmentTable.getSelectionModel().selectedItemProperty().addListener(
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
		commentField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					currentSegment.setComment(commentField.getText());
				});

		// Use of Enter move focus to next item.
		segmentField.setOnAction((event) -> {
			graphemesField.requestFocus();
		});
		graphemesField.setOnAction((event) -> {
			commentField.requestFocus();
		});
		
		segmentField.requestFocus();

	}

	/**
	 * Fills all text fields to show details about the CV segment.
	 * If the specified segment is null, all text fields are cleared.
	 * 
	 * @param segment the segment or null
	 */
	private void showCVSegmentDetails(CVSegment segment) {
		currentSegment = segment;
	    if (segment != null) {
	        // Fill the text fields with info from the person object.
	        segmentField.setText(segment.getSegment());
	        graphemesField.setText(segment.getGraphemes());
	        commentField.setText(segment.getComment());
	    } else {
	        // Segment is null, remove all the text.
	        segmentField.setText("");
	        graphemesField.setText("");
	        commentField.setText("");
	    }
	}

	public void setSegment(CVSegment segment) {
		segmentField.setText(segment.getSegment());
		graphemesField.setText(segment.getGraphemes());
		commentField.setText(segment.getComment());
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
	/* (non-Javadoc)
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

}
