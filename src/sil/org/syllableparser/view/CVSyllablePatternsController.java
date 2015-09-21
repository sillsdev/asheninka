/**
 * 
 */
package sil.org.syllableparser.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.model.CVApproach;
import sil.org.syllableparser.model.CVNaturalClass;
import sil.org.syllableparser.model.CVSyllablePattern;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */

public class CVSyllablePatternsController extends ApproachController implements Initializable {
	@FXML
	private TableView<CVSyllablePattern> cvSyllablePatternTable;
	@FXML
	private TableColumn<CVSyllablePattern, String> nameColumn;
	@FXML
	private TableColumn<CVSyllablePattern, String> naturalClassColumn;
	@FXML
	private TableColumn<CVSyllablePattern, String> descriptionColumn;

	@FXML
	private TextField nameField;
	@FXML
	private TextField naturalClassesField;
	@FXML
	private TextField descriptionField;
	@FXML
	private FlowPane ncsField;
	@FXML
	private TextFlow ncsTextFlow;
	@FXML
	private Button ncsButton;
//	@FXML
//	private TextField sncRepresentationField;
	
	
	private CVApproach cvApproach;
	private CVSyllablePattern currentSyllablePattern;

	public CVSyllablePatternsController() {
		
	}
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	//public void initialize() {

		 // Initialize the table with the three columns.
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().spNameProperty());
		descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());;
        
        // Clear cv natural class details.
        showCVSyllablePatternDetails(null);
        
        // Listen for selection changes and show the  details when changed.
        cvSyllablePatternTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showCVSyllablePatternDetails(newValue));
        
		// Handle TextField text changes.
		nameField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					currentSyllablePattern.setSPName(nameField.getText());
				});
		descriptionField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					currentSyllablePattern.setDescription(descriptionField.getText());
				});

		// Use of Enter move focus to next item.
		nameField.setOnAction((event) -> {
			descriptionField.requestFocus();
		});
		descriptionField.setOnAction((event) -> {
			naturalClassesField.requestFocus();
		});
		
		nameField.requestFocus();

	}

	/**
	 * Fills all text fields to show details about the CV natural class.
	 * If the specified segment is null, all text fields are cleared.
	 * 
	 * @param syllablePattern the segment or null
	 */
	private void showCVSyllablePatternDetails(CVSyllablePattern syllablePattern) {
		currentSyllablePattern = syllablePattern;
	    if (syllablePattern != null) {
	        // Fill the text fields with info from the person object.
	        nameField.setText(syllablePattern.getSPName());
	        descriptionField.setText(syllablePattern.getDescription());
	        showNaturalClassesContent();
	    } else {
	        // Segment is null, remove all the text.
	        nameField.setText("");
	        descriptionField.setText("");
	        ncsTextFlow.getChildren().clear();
	    }
	    
	    
	}
	private void showNaturalClassesContent() {
        ncsTextFlow.getChildren().clear();
        for (CVNaturalClass nc : currentSyllablePattern.getNCs()) {
			Text t = new Text(nc.getNCName());
			Text tBar = new Text(" | ");
		    tBar.setStyle("-fx-stroke: lightgrey;");
		    ncsTextFlow.getChildren().addAll(t, tBar);
		}
	}

	public void setSyllablePattern(CVSyllablePattern syllablePattern) {
		nameField.setText(syllablePattern.getSPName());
		descriptionField.setText(syllablePattern.getDescription());
	}
	
	/**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param cvApproachController
     */
    public void setData(CVApproach cvApproachData) {
        cvApproach = cvApproachData;

        // Add observable list data to the table
        cvSyllablePatternTable.setItems(cvApproachData.getCVSyllablePatterns());
        if (cvSyllablePatternTable.getItems().size() > 0) {
        	// select first one
        	cvSyllablePatternTable.requestFocus();
        	cvSyllablePatternTable.getSelectionModel().select(0);
        	cvSyllablePatternTable.getFocusModel().focus(0);
        }
    }
	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		CVSyllablePattern newSyllablePattern = new CVSyllablePattern();
		cvApproach.getCVSyllablePatterns().add(newSyllablePattern);
		int i = cvApproach.getCVSyllablePatterns().size() - 1;
		cvSyllablePatternTable.requestFocus();
		cvSyllablePatternTable.getSelectionModel().select(i);
    	cvSyllablePatternTable.getFocusModel().focus(i);
	}

	@FXML
	void handleLaunchNCChooser() {
		showNCChooser();
		showNaturalClassesContent();
	}
	/**
	 * Opens a dialog to show birthday statistics.
	 */
	public void showNCChooser() {
	    try {
	        // Load the fxml file and create a new stage for the popup.
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(ApproachViewNavigator.class.getResource("fxml/CVNaturalClassChooser.fxml"));
	        loader.setResources(ResourceBundle.getBundle("sil.org.syllableparser.resources.SyllableParser", locale));
	                	
	        AnchorPane page = loader.load();
	        Stage dialogStage = new Stage();
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(mainApp.getPrimaryStage());
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);
	        // set the icon
	        dialogStage.getIcons().add(mainApp.getNewMainIconImage());

	        CVNaturalClassChooserController controller = loader.getController();
	        controller.setDialogStage(dialogStage);
	        controller.setMainApp(mainApp);
	        controller.setSyllablePattern(currentSyllablePattern);
	        controller.setData(cvApproach);

	        dialogStage.showAndWait();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
