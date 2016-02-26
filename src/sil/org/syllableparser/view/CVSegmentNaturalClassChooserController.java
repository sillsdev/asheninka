/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.SylParserObject;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.syllableparser.model.cvapproach.CVSegmentOrNaturalClass;
import sil.org.syllableparser.view.CVNaturalClassesController.WrappingTableCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class CVSegmentNaturalClassChooserController extends CheckBoxColumnController implements Initializable {

	protected final class WrappingTableCell extends TableCell<CVSegmentOrNaturalClass, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (item == null || empty) {
				setText(null);
				setStyle("");
			} else {
				setStyle("");
				text = new Text(item.toString());
				// Get it to wrap.
				text.wrappingWidthProperty().bind(getTableColumn().widthProperty());
				CVSegmentOrNaturalClass snc = (CVSegmentOrNaturalClass) this.getTableRow().getItem();
				if (snc != null && snc.isActive()) {
					text.setFill(Constants.ACTIVE);
				} else {
					text.setFill(Constants.INACTIVE);
				}
				setGraphic(text);
			}
		}
	}
	
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

	private LanguageProject languageProject;
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
		checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().checkedProperty());
		checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));
		checkBoxColumn.setEditable(true);
		checkBoxColumnHead.setOnAction((event) -> {
			handleCheckBoxColumnHead();
		});
		segOrNCColumn.setCellValueFactory(cellData -> cellData.getValue()
				.segmentOrNaturalClassProperty());
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		cvSegmentOrNaturalClassTable.setEditable(true);
		// Custom rendering of the table cell.
		segOrNCColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});

		initializeCheckBoxContextMenu(resources);
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
		generateCVSegmentsAndNaturalClasses(cvApproachData);
		// Add observable list data to the table
		cvSegmentOrNaturalClassTable.setItems(cvSegmentsOrNaturalClasses);
		if (cvSegmentOrNaturalClassTable.getItems().size() > 0) {
			// select one
			cvSegmentOrNaturalClassTable.requestFocus();
			cvSegmentOrNaturalClassTable.getSelectionModel().select(0);
			cvSegmentOrNaturalClassTable.getFocusModel().focus(0);
		}
	}

	public void generateCVSegmentsAndNaturalClasses(CVApproach cvApproachData) {
		cvApproach = cvApproachData;
		languageProject = cvApproach.getLanguageProject();

		for (Segment cvSegment : languageProject.getSegmentInventory()) {
			if (cvSegment.isActive()) {
				currentSegmentOrNaturalClass = new CVSegmentOrNaturalClass(cvSegment.getSegment(),
						cvSegment.getDescription(), true, cvSegment.getID(), true);
				cvSegmentsOrNaturalClasses.add(currentSegmentOrNaturalClass);
				setCheckedStatus(cvSegment);
			}
		}
		for (CVNaturalClass cvNaturalClass : cvApproach.getCVNaturalClasses()) {
			if (cvNaturalClass.isActive()) {
				if (cvNaturalClass.getID() != naturalClass.getID()) {
					currentSegmentOrNaturalClass = new CVSegmentOrNaturalClass(
							cvNaturalClass.getNCName(), cvNaturalClass.getDescription(), false,
							cvNaturalClass.getID(), true);
					cvSegmentsOrNaturalClasses.add(currentSegmentOrNaturalClass);
					setCheckedStatus(cvNaturalClass);
				}
			}
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
					int i = Segment.findIndexInSegmentsListByUuid(
							languageProject.getSegmentInventory(), segmentOrNaturalClass.getUuid());
					naturalClass.getSegmentsOrNaturalClasses().add(
							languageProject.getSegmentInventory().get(i));
				} else {
					int i = CVNaturalClass.findIndexInNaturaClassListByUuid(
							cvApproach.getCVNaturalClasses(), segmentOrNaturalClass.getUuid());
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

	public CVNaturalClass getNaturalClass() {
		return naturalClass;
	}

	public void setNaturalClass(CVNaturalClass naturalClass) {
		this.naturalClass = naturalClass;
	}

	protected void handleCheckBoxSelectAll() {
		for (CVSegmentOrNaturalClass segmentOrNaturalClass : cvSegmentsOrNaturalClasses) {
			segmentOrNaturalClass.setChecked(true);
		}
	}

	protected void handleCheckBoxClearAll() {
		for (CVSegmentOrNaturalClass segmentOrNaturalClass : cvSegmentsOrNaturalClasses) {
			segmentOrNaturalClass.setChecked(false);
		}
	}

	protected void handleCheckBoxToggle() {
		for (CVSegmentOrNaturalClass segmentOrNaturalClass : cvSegmentsOrNaturalClasses) {
			if (segmentOrNaturalClass.isChecked()) {
				segmentOrNaturalClass.setChecked(false);
			} else {
				segmentOrNaturalClass.setChecked(true);
			}
		}
	}
}
