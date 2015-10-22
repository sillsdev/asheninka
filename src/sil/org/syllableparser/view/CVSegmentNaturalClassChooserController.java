/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.CVApproach;
import sil.org.syllableparser.model.SylParserObject;
import sil.org.syllableparser.model.entity.CVNaturalClass;
import sil.org.syllableparser.model.entity.CVSegment;
import sil.org.syllableparser.model.entity.CVSegmentOrNaturalClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
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
	@FXML
	private CheckBox checkBoxColumnHead;
	private ContextMenu checkBoxContextMenu = new ContextMenu();
	private MenuItem selectAll = new MenuItem("Select All");
	private MenuItem clearAll = new MenuItem("Clear All");
	private MenuItem toggle = new MenuItem("Toggle");

	Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;

	private CVApproach cvApproach;
	private CVSegmentOrNaturalClass currentSegmentOrNaturalClass;
	private ObservableList<CVSegmentOrNaturalClass> cvSegmentsOrNaturalClasses = FXCollections
			.observableArrayList();
	private CVNaturalClass naturalClass;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize the table with the three columns.
		checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue()
				.checkedProperty());
		checkBoxColumnHead.setOnAction((event) -> {
			handleCheckBoxColumnHead();
		});
		segOrNCColumn.setCellValueFactory(cellData -> cellData.getValue()
				.segmentOrNaturalClassProperty());
		descriptionColumn.setCellValueFactory(cellData -> cellData.getValue()
				.descriptionProperty());
		checkBoxColumn.setCellFactory(CheckBoxTableCell
				.forTableColumn(checkBoxColumn));
		checkBoxColumn.setEditable(true);
		cvSegmentOrNaturalClassTable.setEditable(true);
		
		initializeCheckBoxContextMenu(resources);
	}

	private void initializeCheckBoxContextMenu(ResourceBundle bundle) {
		// set up context menu
		selectAll.setOnAction((event) -> {
			handleCheckBoxSelectAll();
		});
		clearAll.setOnAction((event) -> {
			handleCheckBoxClearAll();
		});
		toggle.setOnAction((event) -> {
			handleCheckBoxToggle();
		});
		selectAll.setText(bundle.getString("checkbox.context.menu.selectall"));
		clearAll.setText(bundle.getString("checkbox.context.menu.clearall"));
		toggle.setText(bundle.getString("checkbox.context.menu.toggle"));
		checkBoxContextMenu.getItems().addAll(selectAll, clearAll, toggle);
		checkBoxColumnHead.setContextMenu(checkBoxContextMenu);
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
			currentSegmentOrNaturalClass = new CVSegmentOrNaturalClass(
					cvSegment.getSegment(), cvSegment.getDescription(), true,
					cvSegment.getID());
			setCheckedStatus(cvSegment);
			cvSegmentsOrNaturalClasses.add(currentSegmentOrNaturalClass);
		}
		for (CVNaturalClass cvNaturalClass : cvApproach.getCVNaturalClasses()) {
			if (cvNaturalClass.getID() != naturalClass.getID()) {
				currentSegmentOrNaturalClass = new CVSegmentOrNaturalClass(
						cvNaturalClass.getNCName(),
						cvNaturalClass.getDescription(), false,
						cvNaturalClass.getID());
				cvSegmentsOrNaturalClasses.add(currentSegmentOrNaturalClass);
				setCheckedStatus(cvNaturalClass);
			}
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

	private void setCheckedStatus(SylParserObject sylParserObject) {
		if (SylParserObject.findIndexInSylParserObjectListByUuid(
				naturalClass.getSegmentsOrNaturalClasses(), sylParserObject.getID()) > -1) {
			currentSegmentOrNaturalClass.setChecked(true);
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
		// find the segment or natural class with
		// segmentOrNaturalClass.getUuid() and
		// add it to the natural class list
		// we use this method in order to guarantee we get the actual object and
		// not a copy
		for (CVSegmentOrNaturalClass segmentOrNaturalClass : cvSegmentsOrNaturalClasses) {
			if (segmentOrNaturalClass.isChecked()) {
				if (segmentOrNaturalClass.isSegment()) {
					int i = CVSegment.findIndexInSegmentsListByUuid(
							cvApproach.getCVSegmentInventory(),
							segmentOrNaturalClass.getUuid());
					naturalClass.getSegmentsOrNaturalClasses().add(
							cvApproach.getCVSegmentInventory().get(i));
				} else {
					int i = CVNaturalClass.findIndexInNaturaClassListByUuid(
							cvApproach.getCVNaturalClasses(),
							segmentOrNaturalClass.getUuid());
					naturalClass.getSegmentsOrNaturalClasses().add(
							cvApproach.getCVNaturalClasses().get(i));
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

	/**
	 * Called when the user clicks on the check box column header
	 */
	@FXML
	private void handleCheckBoxColumnHead() {
		// make sure the check box stays checked
		checkBoxColumnHead.setSelected(true);
		// show the check box context menu
		checkBoxColumnHead.contextMenuProperty().get().show(checkBoxColumnHead, Side.BOTTOM, 0.0, 0.0);
	}

	public CVNaturalClass getNaturalClass() {
		return naturalClass;
	}

	public void setNaturalClass(CVNaturalClass naturalClass) {
		this.naturalClass = naturalClass;
	}
	
	void handleCheckBoxSelectAll() {
		for (CVSegmentOrNaturalClass segmentOrNaturalClass : cvSegmentsOrNaturalClasses) {
			segmentOrNaturalClass.setChecked(true);
		}
	}
	
	void handleCheckBoxClearAll() {
		for (CVSegmentOrNaturalClass segmentOrNaturalClass : cvSegmentsOrNaturalClasses) {
			segmentOrNaturalClass.setChecked(false);
		}
	}

	void handleCheckBoxToggle() {
		for (CVSegmentOrNaturalClass segmentOrNaturalClass : cvSegmentsOrNaturalClasses) {
			if (segmentOrNaturalClass.isChecked()) {
				segmentOrNaturalClass.setChecked(false);
			} else {
				segmentOrNaturalClass.setChecked(true);
			}
		}
	}
}
