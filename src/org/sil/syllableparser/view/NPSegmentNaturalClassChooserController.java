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
import org.sil.syllableparser.model.npapproach.NPRule;

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
public class NPSegmentNaturalClassChooserController extends TableViewController {

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
	private TableView<CVSegmentOrNaturalClass> npSegmentOrNaturalClassTable;
	@FXML
	private TableColumn<CVSegmentOrNaturalClass, String> segOrNCColumn;
	@FXML
	private TableColumn<CVSegmentOrNaturalClass, String> descriptionColumn;
	Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;
	private ApplicationPreferences preferences;

	private CVSegmentOrNaturalClass currentSegmentOrNaturalClass;
	private CVSegmentOrNaturalClass noneOption;
	private ObservableList<CVSegmentOrNaturalClass> cvSegmentsOrNaturalClasses = FXCollections
			.observableArrayList();
	private NPRule rule;
	private boolean isAffected;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		super.setApproach(ApplicationPreferences.NP_SEGMENT_NATURAL_CLASS_CHOOSER);
		super.setTableView(npSegmentOrNaturalClassTable);
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
		npSegmentOrNaturalClassTable.setEditable(true);
		// Custom rendering of the table cell.
		segOrNCColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});

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
		npSegmentOrNaturalClassTable.setItems(cvSegmentsOrNaturalClasses);
		if (npSegmentOrNaturalClassTable.getItems().size() > 0) {
			// select one
			npSegmentOrNaturalClassTable.requestFocus();
			npSegmentOrNaturalClassTable.getSelectionModel().select(0);
			npSegmentOrNaturalClassTable.getFocusModel().focus(0);
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
			if (cvNaturalClass.getID() != rule.getID()) {
				currentSegmentOrNaturalClass = new CVSegmentOrNaturalClass(
						cvNaturalClass.getNCName(), cvNaturalClass.getDescription(), false,
						cvNaturalClass.getID(), true);
				cvSegmentsOrNaturalClasses.add(currentSegmentOrNaturalClass);
			}
		}
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
		CVSegmentOrNaturalClass segOrClass = npSegmentOrNaturalClassTable.getSelectionModel()
				.getSelectedItem();
		if (!segOrClass.equals(noneOption)) {
			if (isAffected) {
				rule.setAffectedSegOrNC(segOrClass);
				rule.setAffectedSegmentOrNaturalClass(segOrClass.getSegmentOrNaturalClassForShow());
			} else {
				rule.setContextSegOrNC(segOrClass);
				rule.setContextSegmentOrNaturalClass(segOrClass.getSegmentOrNaturalClassForShow());
			}
		} else if (isAffected) {
			rule.setAffectedSegOrNC(null);
			rule.setAffectedSegmentOrNaturalClass("");
		} else {
			rule.setContextSegOrNC(null);
			rule.setContextSegmentOrNaturalClass("");
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
				ApplicationPreferences.LAST_NP_SEGMENT_OR_NATURAL_CLASS, dialogStage);
		dialogStage.close();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		preferences = mainApp.getApplicationPreferences();
		dialogStage = preferences.getLastWindowParameters(
				ApplicationPreferences.LAST_NP_SEGMENT_OR_NATURAL_CLASS, dialogStage, 400., 400.);
	}

	/**
	 * Called when the user clicks help.
	 */
	@FXML
	private void handleHelp() {
		mainApp.showNotImplementedYet();
	}

	public NPRule getRule() {
		return rule;
	}

	public void setRule(NPRule rule) {
		this.rule = rule;
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
