/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.SylParserObject;
import sil.org.syllableparser.model.Word;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.syllableparser.model.cvapproach.CVPredictedSyllabification;
import sil.org.syllableparser.model.cvapproach.CVSegment;
import sil.org.syllableparser.model.cvapproach.CVSegmentOrNaturalClass;
import sil.org.syllableparser.model.cvapproach.CVWord;
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
public class CVPredictedToCorrectSyllabificationChooserController implements Initializable {

	@FXML
	private TableView<CVPredictedSyllabification> cvPredictedSyllabificationTable;
	@FXML
	private TableColumn<CVPredictedSyllabification, Boolean> checkBoxColumn;
	@FXML
	private TableColumn<CVPredictedSyllabification, String> predictedSyllabificationColumn;
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
	private CVPredictedSyllabification currentPredictedSyllabification;
	private ObservableList<CVPredictedSyllabification> cvPredictedSyllabifications = FXCollections
			.observableArrayList();
	private CVWord cvWord;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize the table with the three columns.
		checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().checkedProperty());
		checkBoxColumnHead.setOnAction((event) -> {
			handleCheckBoxColumnHead();
		});
		predictedSyllabificationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.predictedSyllabificationProperty());
		// not working... predictedSyllabificationColumn.getStyleClass().add("predictedSyllabificationColumn");
		checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));
		checkBoxColumn.setEditable(true);
		cvPredictedSyllabificationTable.setEditable(true);

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

		for (CVWord cvWord : cvApproach.getCVWords()) {
			if (!cvWord.predictedSyllabificationProperty().isEmpty().getValue()) {
				currentPredictedSyllabification = new CVPredictedSyllabification(
						cvWord.getPredictedSyllabification(), cvWord.getID());
				cvPredictedSyllabifications.add(currentPredictedSyllabification);
			}
		}
		// Add observable list data to the table
		cvPredictedSyllabificationTable.setItems(cvPredictedSyllabifications);
		if (cvPredictedSyllabificationTable.getItems().size() > 0) {
			// select one
			cvPredictedSyllabificationTable.requestFocus();
			cvPredictedSyllabificationTable.getSelectionModel().select(0);
			cvPredictedSyllabificationTable.getFocusModel().focus(0);
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
		for (CVPredictedSyllabification predictedSyllabification : cvPredictedSyllabifications) {
			if (predictedSyllabification.isChecked()) {
				int i = CVWord.findIndexInCVWordListByUuid(cvApproach.getCVWords(),
						predictedSyllabification.getUuid());
				cvWord = cvApproach.getCVWords().get(i);
				cvWord.setCorrectSyllabification(predictedSyllabification
						.getPredictedSyllabification());
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
		checkBoxColumnHead.contextMenuProperty().get()
				.show(checkBoxColumnHead, Side.BOTTOM, 0.0, 0.0);
	}

	// public void setNaturalClass(CVNaturalClass naturalClass) {
	// this.naturalClass = naturalClass;
	// }
	//
	void handleCheckBoxSelectAll() {
		for (CVPredictedSyllabification predictedSyllabification : cvPredictedSyllabifications) {
			predictedSyllabification.setChecked(true);
		}
	}

	void handleCheckBoxClearAll() {
		for (CVPredictedSyllabification predictedSyllabification : cvPredictedSyllabifications) {
			predictedSyllabification.setChecked(false);
		}
	}

	void handleCheckBoxToggle() {
		for (CVPredictedSyllabification predictedSyllabification : cvPredictedSyllabifications) {
			if (predictedSyllabification.isChecked()) {
				predictedSyllabification.setChecked(false);
			} else {
				predictedSyllabification.setChecked(true);
			}
		}
	}
}
