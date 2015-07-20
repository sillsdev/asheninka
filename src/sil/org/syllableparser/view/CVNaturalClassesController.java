/**
 * 
 */
package sil.org.syllableparser.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.CVApproach;
import sil.org.syllableparser.model.CVNaturalClass;
import sil.org.syllableparser.model.CVSegment;
import javafx.beans.property.SimpleListProperty;
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
		//segmentOrNaturalClassColumn.setCellValueFactory(cellData -> cellData.getValue().segmentsOrNaturalClassesProperty());
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
		segmentOrNaturalClassField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					currentNaturalClass.setSNCRepresentation(segmentOrNaturalClassField.getText());
				});
		descriptionField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					currentNaturalClass.setDescription(descriptionField.getText());
				});
//		sncRepresentationField.textProperty().addListener(
//				(observable, oldValue, newValue) -> {
//					currentNaturalClass.setSNCRepresentation(sncRepresentationField.getText());
//				});

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
	        segmentOrNaturalClassField.setText(naturalClass.getSNCRepresentation());
	        descriptionField.setText(naturalClass.getDescription());
	        sncTextFlow.getChildren().clear();
	        Text t1 = new Text("One");
	        Text t2 = new Text("Two");
	        Text tBar = new Text(" | ");
	        sncTextFlow.getChildren().addAll(t1, tBar, t2);
	    } else {
	        // Segment is null, remove all the text.
	        nameField.setText("");
	        segmentOrNaturalClassField.setText("");
	        descriptionField.setText("");
	    }
	    
	    
	}

	public void setNaturalClass(CVNaturalClass naturalClass) {
		nameField.setText(naturalClass.getNCName());
		segmentOrNaturalClassField.setText(naturalClass.getSNCRepresentation());
		descriptionField.setText(naturalClass.getDescription());
		//sncRepresentationField.setText(naturalClass.getSNCRepresentation());
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
		System.out.println("handleLaunchSNCChooser reached");
		showSNCChooser();
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
        	
	        AnchorPane page = (AnchorPane) loader.load();
	        Stage dialogStage = new Stage();
	        //dialogStage.setTitle("Birthday Statistics");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(mainApp.getPrimaryStage());
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);
	        // set the icon
	        dialogStage.getIcons().add(mainApp.getNewMainIconImage());

	        // Set the persons into the controller.
	        CVSegmentNaturalClassChooserController controller = loader.getController();
	        controller.setDialogStage(dialogStage);
	        controller.setMainApp(mainApp);
	        // TODO: controller.setPersonData(personData);

	        dialogStage.show();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
