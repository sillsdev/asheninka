// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVSegmentOrNaturalClass;
import org.sil.syllableparser.model.otapproach.OTConstraint;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class OTSegmentNaturalClassChooserController extends TableViewController {

	protected final class AnalysisWrappingTableCell extends
			TableCell<CVSegmentOrNaturalClass, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processAnalysisTableCell(this, text, item, empty);
		}
	}

	protected final class WrappingTableCell extends TableCell<CVSegmentOrNaturalClass, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			CVSegmentOrNaturalClass segOrNC = (CVSegmentOrNaturalClass) this.getTableRow().getItem();
			if (segOrNC != null) {
				if (segOrNC.isSegment()) {
					processVernacularTableCell(this, text, item, empty);
				} else {
					processAnalysisTableCell(this, text, item, empty);
				}
			} else {
				processTableCell(this, text, item, empty);
			}
		}
	}

	@FXML
	private TableView<CVSegmentOrNaturalClass> otSegmentOrNaturalClassTable;
	@FXML
	private TableColumn<CVSegmentOrNaturalClass, String> segOrNCColumn;
	@FXML
	private TableColumn<CVSegmentOrNaturalClass, String> descriptionColumn;
	Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;
	private ApplicationPreferences preferences;

	private CVSegmentOrNaturalClass currentSegmentOrNaturalClass;
	private CVSegmentOrNaturalClass anyOption;
	private CVSegmentOrNaturalClass noneOption;
	private ObservableList<CVSegmentOrNaturalClass> cvSegmentsOrNaturalClasses = FXCollections
			.observableArrayList();
	private OTConstraint constraint;
	private boolean isAffected;
	String any = "<Any>";

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		super.setApproach(ApplicationPreferences.NP_SEGMENT_NATURAL_CLASS_CHOOSER);
		super.setTableView(otSegmentOrNaturalClassTable);
		super.initialize(location, resources);

		segOrNCColumn.setCellValueFactory(cellData -> {
			if (cellData.getValue().isSegment()) {
				return cellData.getValue().segmentOrNaturalClassProperty();
			} else {
				SimpleStringProperty sp = new SimpleStringProperty(Constants.NATURAL_CLASS_PREFIX
						+ cellData.getValue().segmentOrNaturalClassProperty().getValue()
						+ Constants.NATURAL_CLASS_SUFFIX);
				return sp;
			}
		});
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		otSegmentOrNaturalClassTable.setEditable(true);
		// Custom rendering of the table cell.
		segOrNCColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});

		any = resources.getString("label.any");
		anyOption = new CVSegmentOrNaturalClass(any,
				resources.getString("label.anydescription"), false, null, true);
		noneOption = new CVSegmentOrNaturalClass(resources.getString("label.none"),
				resources.getString("label.nonedescription"), false, null, true);
	}

	/**
	 * Sets the stage of this dialog.
	 *
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
		this.dialogStage.setOnCloseRequest(event -> {
			handleCancel();
		});
	}

	public void setData(LanguageProject langProj, boolean isAffected) {
		this.isAffected = isAffected;
		generateCVSegmentsAndNaturalClasses(langProj.getCVApproach());
		// Add observable list data to the table
		otSegmentOrNaturalClassTable.setItems(cvSegmentsOrNaturalClasses);
		if (otSegmentOrNaturalClassTable.getItems().size() > 0) {
			// select one
			otSegmentOrNaturalClassTable.requestFocus();
			otSegmentOrNaturalClassTable.getSelectionModel().select(0);
			otSegmentOrNaturalClassTable.getFocusModel().focus(0);
		}
	}

	public void generateCVSegmentsAndNaturalClasses(CVApproach cvApproachData) {
		cvApproach = cvApproachData;
		languageProject = cvApproach.getLanguageProject();

		for (Segment cvSegment : languageProject.getActiveSegmentsInInventory()) {
			currentSegmentOrNaturalClass = new CVSegmentOrNaturalClass(cvSegment.getSegment(),
					cvSegment.getDescription(), true, cvSegment.getID(), true);
			cvSegmentsOrNaturalClasses.add(currentSegmentOrNaturalClass);
		}
		for (CVNaturalClass cvNaturalClass : cvApproach.getActiveCVNaturalClasses()) {
			if (cvNaturalClass.getID() != constraint.getID()) {
				currentSegmentOrNaturalClass = new CVSegmentOrNaturalClass(
						cvNaturalClass.getNCName(), cvNaturalClass.getDescription(), false,
						cvNaturalClass.getID(), true);
				cvSegmentsOrNaturalClasses.add(currentSegmentOrNaturalClass);
			}
		}
		cvSegmentsOrNaturalClasses.add(anyOption);
		if (!isAffected) {
			cvSegmentsOrNaturalClasses.add(noneOption);
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
		CVSegmentOrNaturalClass segOrClass = otSegmentOrNaturalClassTable.getSelectionModel()
				.getSelectedItem();
		if (!segOrClass.equals(anyOption) && !segOrClass.equals(noneOption)) {
			if (isAffected) {
				constraint.setAffectedSegOrNC1(segOrClass);
				constraint.setAffectedElement1(segOrClass.getSegmentOrNaturalClassForShow());
			} else {
				constraint.setAffectedSegOrNC2(segOrClass);
				constraint.setAffectedElement2(segOrClass.getSegmentOrNaturalClassForShow());
			}
		} else if (segOrClass.equals(anyOption)) {
			if (isAffected) {
				constraint.setAffectedSegOrNC1(null);
				constraint.setAffectedElement1(any);
			} else {
				constraint.setAffectedSegOrNC2(null);
				constraint.setAffectedElement2(any);
			}
		} else {
			constraint.setAffectedSegOrNC2(null);
			constraint.setAffectedElement2("");
		}

		okClicked = true;
		handleCancel();
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		preferences.setLastWindowParameters(
				ApplicationPreferences.LAST_OT_SEGMENT_OR_NATURAL_CLASS, dialogStage);
		dialogStage.close();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		preferences = mainApp.getApplicationPreferences();
		dialogStage = preferences.getLastWindowParameters(
				ApplicationPreferences.LAST_OT_SEGMENT_OR_NATURAL_CLASS, dialogStage, 400., 400.);
	}

	/**
	 * Called when the user clicks help.
	 */
	@FXML
	private void handleHelp() {
		mainApp.showNotImplementedYet();
	}

	public OTConstraint getConstraint() {
		return constraint;
	}

	public void setConstraint(OTConstraint constraint) {
		this.constraint = constraint;
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return null;
	}

	@Override
	void handlePreviousItem() {
		// nothing to do
	}

	@Override
	void handleNextItem() {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sil.syllableparser.view.ApproachEditorController#handleInsertNewItem
	 * ()
	 */
	@Override
	void handleInsertNewItem() {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sil.syllableparser.view.ApproachEditorController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		// nothing to do
	}
}
