/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.MainApp;
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

public class CVSegmentInventoryController implements Initializable {
	@FXML
	private TableView<CVSegment> cvSegmentTable;
	@FXML
	private TableColumn<CVSegment, String> segmentColumn;
	@FXML
	private TableColumn<CVSegment, String> graphemesColumn;
	@FXML
	private TableColumn<CVSegment, String> commentColumn;

	// Reference to the main application.
	/* hopefully we can figure out a way to not need this */
	private MainApp mainApp;
	@FXML
	private TextField segmentField;
	@FXML
	private TextField graphemesField;
	@FXML
	private TextField commentField;

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
    
		// Handle TextField text changes.
		segmentField
				.textProperty()
				.addListener(
						(observable, oldValue, newValue) -> {
							System.out
									.println("segment TextField Text Changed (newValue: "
											+ newValue + ")");
						});

		// Handle TextField enter key event.
		segmentField.setOnAction((event) -> {
			System.out.println("Enter key in segment TextField");
		});

	}

	public void setSegment(CVSegment segment) {
		segmentField.setText(segment.getSegment());
		graphemesField.setText(segment.getGraphemes());
		commentField.setText(segment.getComment());
	}
	
	/**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setData(CVApproachController mainApp) {
        // needed? this.mainApp = mainApp;

        // Add observable list data to the table
        cvSegmentTable.setItems(mainApp.getCVSegmentInventoryData());
    }

}
