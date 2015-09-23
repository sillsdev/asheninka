/**
 * 
 */
package sil.org.syllableparser.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.model.CVApproach;
import sil.org.syllableparser.model.CVNaturalClass;
import sil.org.syllableparser.model.CVSegment;
import sil.org.syllableparser.model.SylParserObject;
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

public class CVNaturalClassesController extends ApproachController implements Initializable {
	@FXML
	private TableView<CVNaturalClass> cvNaturalClassTable;
	@FXML
	private TableColumn<CVNaturalClass, String> nameColumn;
	@FXML
	private TableColumn<CVNaturalClass, String> segmentOrNaturalClassColumn;
	@FXML
	private TableColumn<CVNaturalClass, String> descriptionColumn;

	@FXML
	private TextField nameField;
	@FXML
	private TextField segmentOrNaturalClassField;
	@FXML
	private TextField descriptionField;
	@FXML
	private FlowPane sncField;
	@FXML
	private TextFlow sncTextFlow;
	@FXML
	private Button sncButton;
//	@FXML
//	private TextField sncRepresentationField;
	
	
	private CVApproach cvApproach;
	private CVNaturalClass currentNaturalClass;

	public CVNaturalClassesController() {
		
	}
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	//public void initialize() {

		 // Initialize the table with the three columns.
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().ncNameProperty());
		descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());;
        
        // Clear cv natural class details.
        showCVNaturalClassDetails(null);
        
        // Listen for selection changes and show the  details when changed.
        cvNaturalClassTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showCVNaturalClassDetails(newValue));
        
		// Handle TextField text changes.
		nameField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					currentNaturalClass.setNCName(nameField.getText());
				});
		descriptionField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					currentNaturalClass.setDescription(descriptionField.getText());
				});

		// Use of Enter move focus to next item.
		nameField.setOnAction((event) -> {
			descriptionField.requestFocus();
		});
		descriptionField.setOnAction((event) -> {
			segmentOrNaturalClassField.requestFocus();
		});
		
		nameField.requestFocus();

	}

	/**
	 * Fills all text fields to show details about the CV natural class.
	 * If the specified segment is null, all text fields are cleared.
	 * 
	 * @param naturalClass the segment or null
	 */
	private void showCVNaturalClassDetails(CVNaturalClass naturalClass) {
		currentNaturalClass = naturalClass;
	    if (naturalClass != null) {
	        // Fill the text fields with info from the person object.
	        nameField.setText(naturalClass.getNCName());
	        descriptionField.setText(naturalClass.getDescription());
	        showSegmentOrNaturalClassContent();
	    } else {
	        // Segment is null, remove all the text.
	        nameField.setText("");
	        descriptionField.setText("");
	        sncTextFlow.getChildren().clear();
	    }
	    
	    
	}
	private void showSegmentOrNaturalClassContent() {
        sncTextFlow.getChildren().clear();
        for (SylParserObject snc : currentNaturalClass.getSnc()) {
			Text t;
			if (snc instanceof CVSegment) {
				t = new Text(((CVSegment) snc).getSegment());
			} else if (snc instanceof CVNaturalClass) {
				t = new Text(((CVNaturalClass) snc).getNCName());
			} else {
				t = new Text("ERROR!");
			}
		    Text tBar = new Text(" | ");
		    tBar.setStyle("-fx-stroke: lightgrey;");
		    sncTextFlow.getChildren().addAll(t, tBar);
		}
	}

	public void setNaturalClass(CVNaturalClass naturalClass) {
		nameField.setText(naturalClass.getNCName());
		descriptionField.setText(naturalClass.getDescription());
	}
	
	/**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param cvApproachController
     */
    public void setData(CVApproach cvApproachData) {
        cvApproach = cvApproachData;

        // Add observable list data to the table
        cvNaturalClassTable.setItems(cvApproachData.getCVNaturalClasses());
        if (cvNaturalClassTable.getItems().size() > 0) {
        	// select first one
        	cvNaturalClassTable.requestFocus();
        	cvNaturalClassTable.getSelectionModel().select(0);
        	cvNaturalClassTable.getFocusModel().focus(0);
        }
    }
	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		CVNaturalClass newNaturalClass = new CVNaturalClass();
		cvApproach.getCVNaturalClasses().add(newNaturalClass);
		int i = cvApproach.getCVNaturalClasses().size() - 1;
		cvNaturalClassTable.requestFocus();
		cvNaturalClassTable.getSelectionModel().select(i);
    	cvNaturalClassTable.getFocusModel().focus(i);
	}

	@FXML
	void handleLaunchSNCChooser() {
		showSNCChooser();
		showSegmentOrNaturalClassContent();
	}
	/**
	 * Opens a dialog to show birthday statistics.
	 */
	public void showSNCChooser() {
	    try {
	        // Load the fxml file and create a new stage for the popup.
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(ApproachViewNavigator.class.getResource("fxml/CVSegmentNaturalClassChooser.fxml"));
	        loader.setResources(ResourceBundle.getBundle("sil.org.syllableparser.resources.SyllableParser", locale));
	                	
	        AnchorPane page = loader.load();
	        Stage dialogStage = new Stage();
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(mainApp.getPrimaryStage());
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);
	        // set the icon
	        dialogStage.getIcons().add(mainApp.getNewMainIconImage());

	        CVSegmentNaturalClassChooserController controller = loader.getController();
	        controller.setDialogStage(dialogStage);
	        controller.setMainApp(mainApp);
	        controller.setNaturalClass(currentNaturalClass);
	        controller.setData(cvApproach);

	        dialogStage.showAndWait();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
