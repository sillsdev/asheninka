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
import org.sil.syllableparser.model.Language;
import org.sil.syllableparser.model.otapproach.OTApproach;
import org.sil.syllableparser.model.otapproach.OTConstraint;
import org.sil.syllableparser.model.otapproach.OTConstraintRanking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class OTConstraintRankingsInitializationChooserController extends TableViewController {

	protected final class AnalysisWrappingTableCell extends TableCell<OTConstraintRanking, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processAnalysisTableCell(this, text, item, empty);
		}
	}
	protected final class WrappingTableCell extends TableCell<OTConstraintRanking, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processTableCell(this, text, item, empty);
		}
	}

	@FXML
	private TableView<OTConstraintRanking> rankingsTable;
	@FXML
	private TableColumn<OTConstraintRanking, String> nameColumn;
	@FXML
	private TableColumn<OTConstraintRanking, String> descriptionColumn;
	@FXML
	private TableColumn<OTConstraintRanking, String> rankingRepresentationColumn;
	@FXML
	private CheckBox activeCheckBox = new CheckBox();

	Stage dialogStage;
	private boolean okClicked = false;
	private ApplicationPreferences preferences;

	private OTConstraintRanking currentRanking;
	private OTApproach otApproach;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		super.setApproach(ApplicationPreferences.CONSTRAINT_RANKINGS_INITIALIZATION_CHOOSER);
		super.setTableView(rankingsTable);
		super.initialize(location, resources);
		bundle = resources;

		activeCheckBox.setSelected(true);
		rankingsTable.setEditable(false);
		nameColumn.setCellValueFactory(cellData -> {
			return cellData.getValue().nameProperty();
		});
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		rankingRepresentationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.rankingRepresentationProperty());
		// Custom rendering of the table cell.
		nameColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});
		rankingRepresentationColumn.setCellFactory(column -> {
			return new TableCell<OTConstraintRanking, String>() {
				// We override computePrefHeight because by default, the graphic's height
				// gets set to the height of all items in the TextFlow as if none of them
				// wrapped.  So for now, we're doing this hack.
				@Override
				protected double computePrefHeight(double width) {
					Object g = getGraphic();
					if (g instanceof TextFlow) {
						return guessPrefHeightAnalysisOnly(g, column.widthProperty().get() - 20);
					}
					return super.computePrefHeight(-1);
				}

				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					OTConstraintRanking ranking = ((OTConstraintRanking) getTableRow().getItem());
					if (item == null || empty || ranking == null) {
						setGraphic(null);
						setText(null);
						setStyle("");
					} else {
						setGraphic(null);
						TextFlow tf = new TextFlow();
						if (languageProject.getVernacularLanguage().getOrientation() == NodeOrientation.LEFT_TO_RIGHT) {
							tf = buildConstraintTextFlow(ranking.getRanking());
							tf.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
						} else {
							FXCollections.reverse(ranking.getRanking());
							tf = buildConstraintTextFlow(ranking.getRanking());
							tf.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
							FXCollections.reverse(ranking.getRanking());
						}
						setGraphic(tf);
						setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
					}
				}
			};
		});

	}

	protected TextFlow buildConstraintTextFlow(ObservableList<OTConstraint> ranking) {
		TextFlow tf = new TextFlow();
		Language analysis = languageProject.getAnalysisLanguage();
		int i = 1;
		int iCount = ranking.size();
		for (OTConstraint constraint : ranking) {
			String s = constraint.getConstraintName();
			Text t = new Text(s);
			t.setFont(analysis.getFont());
			t.setFill(analysis.getColor());
			t.setNodeOrientation(analysis.getOrientation());
			if (currentRanking != null && !(currentRanking.isActive() && activeCheckBox.isSelected())) {
				t.setFill(Constants.INACTIVE);
			}
			Text tBar = new Text(Constants.OT_SET_SUCCEEDS_OPERATOR);
			tBar.setStyle("-fx-stroke: lightgrey;");
			if (i < iCount) {
				tf.getChildren().addAll(t, tBar);
				i++;
			} else {
				tf.getChildren().addAll(t);
			}
		}
		return tf;
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
		ObservableList<OTConstraintRanking> rankings = FXCollections.observableArrayList();
		rankings.addAll(otApproach.getActiveOTConstraintRankings());
		// Add observable list data to the table
		rankingsTable.setItems(rankings);
		if (rankingsTable.getItems().size() > 0) {
			// select one
			rankingsTable.requestFocus();
			rankingsTable.getSelectionModel().select(0);
			rankingsTable.getFocusModel().focus(0);
			currentRanking = rankingsTable.getItems().get(0);
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
		currentRanking = rankingsTable.getSelectionModel().getSelectedItem();
		okClicked = true;
		handleCancel();
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		preferences.setLastWindowParameters(
				ApplicationPreferences.LAST_OT_CONSTRAINT_RANKINGS_INITIALIZATION, dialogStage);
		dialogStage.close();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		preferences = mainApp.getApplicationPreferences();
		dialogStage = preferences.getLastWindowParameters(
				ApplicationPreferences.LAST_OT_CONSTRAINT_RANKINGS_INITIALIZATION, dialogStage, 400., 400.);
	}

	public OTConstraintRanking getCurrentRanking() {
		return currentRanking;
	}

	public void setCurrentRanking(OTConstraintRanking currentRanking) {
		this.currentRanking = currentRanking;
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
