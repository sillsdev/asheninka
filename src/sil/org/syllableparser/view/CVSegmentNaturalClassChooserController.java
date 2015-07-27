/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.CVApproach;
import sil.org.syllableparser.model.CVNaturalClass;
import sil.org.syllableparser.model.CVSegment;
import sil.org.syllableparser.model.CVSegmentOrNaturalClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class CVSegmentNaturalClassChooserController implements Initializable {

	@FXML
	private TableView<CVSegmentOrNaturalClass> cvSegmentOrNaturalClassTable;
	@FXML
	private TableColumn<CVSegmentOrNaturalClass, Boolean> checkBoxColumn;
	@FXML
	private TableColumn<CVSegmentOrNaturalClass, String> segOrNCColumn;
	@FXML
	private TableColumn<CVSegmentOrNaturalClass, String> descriptionColumn;

	Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;
	
	private CVApproach cvApproach;
	private CVSegmentOrNaturalClass currentSegmentOrNaturalClass;
    private ObservableList<CVSegmentOrNaturalClass> cvSegmentsOrNaturalClasses = FXCollections.observableArrayList();
    private CVNaturalClass naturalClass;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize the table with the three columns.
		checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().checkedProperty());
		segOrNCColumn.setCellValueFactory(cellData -> cellData.getValue().segmentOrNaturalClassProperty());
		descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));
		checkBoxColumn.setEditable(true);
		cvSegmentOrNaturalClassTable.setEditable(true);
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

		for (CVSegment cvSegment : cvApproach.getCVSegmentInventory()) {
			currentSegmentOrNaturalClass = new CVSegmentOrNaturalClass(cvSegment.getSegment(), 
					cvSegment.getDescription(), true, cvSegment.getUuid());
			cvSegmentsOrNaturalClasses.add(currentSegmentOrNaturalClass);
		}
		for (CVNaturalClass cvNaturalClass : cvApproach.getCVNaturalClasses()) {
			currentSegmentOrNaturalClass = new CVSegmentOrNaturalClass(cvNaturalClass.getNCName(), 
					cvNaturalClass.getDescription(), false, cvNaturalClass.getUuid());
			cvSegmentsOrNaturalClasses.add(currentSegmentOrNaturalClass);
		}
        // Add observable list data to the table
        cvSegmentOrNaturalClassTable.setItems(cvSegmentsOrNaturalClasses);
        if (cvSegmentOrNaturalClassTable.getItems().size() > 0) {
        	// select one
        	cvSegmentOrNaturalClassTable.requestFocus();
        	cvSegmentOrNaturalClassTable.getSelectionModel().select(0);
        	cvSegmentOrNaturalClassTable.getFocusModel().focus(0);
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
		naturalClass.getSegmentsOrNaturalClasses().clear();	
		naturalClass.getSnc().clear();
		// find the segment or natural class with segmentOrNaturalClass.getUuid() and 
		// add it to the natural class list
		// we use this method in order to guarantee we get the actual object and not a copy 
		for (CVSegmentOrNaturalClass segmentOrNaturalClass : cvSegmentsOrNaturalClasses) {
			if (segmentOrNaturalClass.isChecked()) {
				if (segmentOrNaturalClass.isSegment()) {
					int i = CVSegment.findIndexInListByUuid(cvApproach.getCVSegmentInventory(), 
							segmentOrNaturalClass.getUuid());
					naturalClass.getSnc().add(cvApproach.getCVSegmentInventory().get(i));
				} else {
					int i = CVNaturalClass.findIndexInListByUuid(cvApproach.getCVNaturalClasses(),
							segmentOrNaturalClass.getUuid());
					naturalClass.getSnc().add(cvApproach.getCVNaturalClasses().get(i));
				}
			}
		}

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

	public CVNaturalClass getNaturalClass() {
		return naturalClass;
	}

	public void setNaturalClass(CVNaturalClass naturalClass) {
		this.naturalClass = naturalClass;
	}
}
