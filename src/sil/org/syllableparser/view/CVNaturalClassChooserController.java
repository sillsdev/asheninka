/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.syllableparser.model.cvapproach.CVSyllablePattern;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class CVNaturalClassChooserController implements Initializable {

	@FXML
	private TableView<CVNaturalClass> cvNaturalClassTable;
	@FXML
	private TableColumn<CVNaturalClass, String> naturalClassColumn;
	@FXML
	private TableColumn<CVNaturalClass, String> descriptionColumn;

	Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;

	private CVApproach cvApproach;
	private CVNaturalClass currentNaturalClass;
	private CVSyllablePattern syllablePattern;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize the table with the three columns.
		naturalClassColumn.setCellValueFactory(cellData -> cellData.getValue()
				.naturalClassProperty());
		descriptionColumn.setCellValueFactory(cellData -> cellData.getValue()
				.descriptionProperty());
		cvNaturalClassTable.setEditable(true);
		  // Listen for selection changes and show the  details when changed.
        cvNaturalClassTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> setCurrentCVNaturalClass(newValue));
	}

	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param cvApproachController
	 */
	public void setData(CVApproach cvApproachData) {
		cvApproach = cvApproachData;
		// Add observable list data to the table
		cvNaturalClassTable.setItems(cvApproach.getCVNaturalClasses());
		if (cvNaturalClassTable.getItems().size() > 0) {
			// select one
			cvNaturalClassTable.requestFocus();
			cvNaturalClassTable.getSelectionModel().select(0);
			cvNaturalClassTable.getFocusModel().focus(0);
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
		int i = CVNaturalClass
				.findIndexInNaturaClassListByUuid(
						cvApproach.getCVNaturalClasses(),
						currentNaturalClass.getID());
		syllablePattern.getNCs().add(cvApproach.getCVNaturalClasses().get(i));

		okClicked = true;
		dialogStage.close();
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * Called when the user clicks help.
	 */
	@FXML
	private void handleHelp() {
		mainApp.showNotImplementedYet();
	}

	public CVSyllablePattern getSyllablePattern() {
		return syllablePattern;
	}

	public void setSyllablePattern(CVSyllablePattern syllablePattern) {
		this.syllablePattern = syllablePattern;
	}
	
	void setCurrentCVNaturalClass(CVNaturalClass naturalClass) {
		currentNaturalClass = naturalClass;
	}

}
