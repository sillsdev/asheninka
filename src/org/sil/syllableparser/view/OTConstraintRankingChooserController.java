// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.otapproach.OTApproach;
import org.sil.syllableparser.model.otapproach.OTConstraint;
import org.sil.syllableparser.model.otapproach.OTConstraintRanking;
import org.sil.utility.view.ControllerUtilities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class OTConstraintRankingChooserController extends SplitPaneWithTableViewController {

	protected final class AnalysisWrappingTableCell extends TableCell<OTConstraint, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processAnalysisTableCell(this, text, item, empty);
		}
	}
	protected final class WrappingTableCell extends TableCell<OTConstraint, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processTableCell(this, text, item, empty);
		}
	}

	@FXML
	private TableView<OTConstraint> constraintsTable;
	@FXML
	private TableColumn<OTConstraint, String> constraintNameColumn;
	@FXML
	private TableColumn<OTConstraint, String> descriptionColumn;
	@FXML
	private Button buttonMoveUp;
	@FXML
	private Button buttonMoveDown;
	@FXML
	private Tooltip tooltipMoveUp;
	@FXML
	private Tooltip tooltipMoveDown;

	Stage dialogStage;
	private boolean okClicked = false;
	private ApplicationPreferences preferences;

	private OTConstraintRanking currentRanking;
	private OTConstraint constraint;
	private OTApproach otApproach;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		super.setApproach(ApplicationPreferences.CONSTRAINT_RANKING_CHOOSER);
		super.setTableView(constraintsTable);
		super.initialize(location, resources);
		bundle = resources;

		constraintNameColumn.setCellValueFactory(cellData -> {
			return cellData.getValue().constraintNameProperty();
		});
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		constraintsTable.setEditable(false);
		// Custom rendering of the table cell.
		constraintNameColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});

		// Initialize the button icons
		tooltipMoveUp = ControllerUtilities.createToolbarButtonWithImage("UpArrow.png",
				buttonMoveUp, tooltipMoveUp, bundle.getString("sh.view.sonorityhierarchy.up"),
				Constants.RESOURCE_SOURCE_LOCATION);
		tooltipMoveDown = ControllerUtilities.createToolbarButtonWithImage("DownArrow.png",
				buttonMoveDown, tooltipMoveDown,
				bundle.getString("sh.view.sonorityhierarchy.down"),
				Constants.RESOURCE_SOURCE_LOCATION);

		constraintsTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					constraint = newValue;
					setUpDownButtonDisabled();
				});
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

	public void setData(OTApproach otApproachData) {
		otApproach = otApproachData;
		this.languageProject = otApproach.getLanguageProject();
		prefs = this.mainApp.getApplicationPreferences();
		ObservableList<OTConstraint> constraints = FXCollections.observableArrayList();
		constraints.addAll(otApproach.getValidActiveOTConstraints());
		// Add observable list data to the table
		constraintsTable.setItems(constraints);
		if (constraintsTable.getItems().size() > 0) {
			// select one
			constraintsTable.requestFocus();
			constraintsTable.getSelectionModel().select(0);
			constraintsTable.getFocusModel().focus(0);
			constraint = constraintsTable.getItems().get(0);
		}
	}

	protected void setUpDownButtonDisabled() {
		int iThis = constraintsTable.getItems().indexOf(constraint) + 1;
		int iSize = constraintsTable.getItems().size();
		if (iThis > 1) {
			buttonMoveUp.setDisable(false);
		} else {
			buttonMoveUp.setDisable(true);
		}
		if (iThis == iSize) {
			buttonMoveDown.setDisable(true);
		} else {
			buttonMoveDown.setDisable(false);
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
		currentRanking.setRanking(constraintsTable.getItems());
		okClicked = true;
		handleCancel();
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		preferences.setLastWindowParameters(
				ApplicationPreferences.LAST_OT_CONSTRAINT_RANKING, dialogStage);
		dialogStage.close();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		preferences = mainApp.getApplicationPreferences();
		dialogStage = preferences.getLastWindowParameters(
				ApplicationPreferences.LAST_OT_CONSTRAINT_RANKING, dialogStage, 400., 400.);
	}

	public String getRankingRepresentation() {
		return "ranking representation here";
	}

	public void setRanking(OTConstraintRanking ranking) {
		this.currentRanking = ranking;
		constraintsTable.getItems().clear();
		constraintsTable.setItems(ranking.getRanking());
		setUpDownButtonDisabled();
	}

	public OTConstraintRanking getCurrentRanking() {
		return currentRanking;
	}

	public void setCurrentRanking(OTConstraintRanking currentRanking) {
		this.currentRanking = currentRanking;
	}

	@FXML
	void handleMoveDown() {
		int i = constraintsTable.getItems().indexOf(constraint);
		if ((i + 1) < constraintsTable.getItems().size()) {
			Collections.swap(constraintsTable.getItems(), i, i + 1);
		}
	}

	@FXML
	void handleMoveUp() {
		int i = constraintsTable.getItems().indexOf(constraint);
		if (i > 0) {
			Collections.swap(constraintsTable.getItems(), i, i - 1);
		}
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

	/* (non-Javadoc)
	 * @see org.sil.syllableparser.view.ApproachEditorController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.sil.syllableparser.view.ApproachEditorController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		// TODO Auto-generated method stub
		
	}
}
