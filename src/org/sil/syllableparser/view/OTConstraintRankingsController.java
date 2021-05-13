// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.otapproach.OTApproach;
import org.sil.syllableparser.model.otapproach.OTConstraintRanking;
import org.sil.utility.view.ControllerUtilities;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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

public class OTConstraintRankingsController extends SplitPaneWithTableViewController {

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
		
		// TODO: do we need this?
		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
				processAnalysisTableCell(this, text, item, empty);
		}
	}

	@FXML
	private TableView<OTConstraintRanking> otRankingsTable;
	@FXML
	private TableColumn<OTConstraintRanking, String> nameColumn;
	@FXML
	private TableColumn<OTConstraintRanking, String> descriptionColumn;
	@FXML
	private TableColumn<OTConstraintRanking, String> rankingRepresentationColumn;

	@FXML
	private TextField nameField;
	@FXML
	private TextField descriptionField;
	@FXML
	private TextFlow rankingTextFlow;
	@FXML
	private TextField rankingRepresentationTextField;
	@FXML
	private Button rankingRepresentationButton;
	@FXML
	private CheckBox activeCheckBox;
	@FXML
	private Button buttonMoveUp;
	@FXML
	private Button buttonMoveDown;
	@FXML
	private Tooltip tooltipMoveUp;
	@FXML
	private Tooltip tooltipMoveDown;

	private OTConstraintRanking currentRanking;

	public OTConstraintRankingsController() {
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.setApproach(ApplicationPreferences.OT_CONSTRAINT_RANKINGS);
		super.setTableView(otRankingsTable);
		super.initialize(location, resources);

		bundle = resources;
		// Initialize the button icons
		tooltipMoveUp = ControllerUtilities.createToolbarButtonWithImage("UpArrow.png",
				buttonMoveUp, tooltipMoveUp, bundle.getString("sh.view.sonorityhierarchy.up"),
				Constants.RESOURCE_SOURCE_LOCATION);
		tooltipMoveDown = ControllerUtilities.createToolbarButtonWithImage("DownArrow.png",
				buttonMoveDown, tooltipMoveDown, bundle.getString("sh.view.sonorityhierarchy.down"),
				Constants.RESOURCE_SOURCE_LOCATION);

		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
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
			return new WrappingTableCell();
		});

		makeColumnHeaderWrappable(nameColumn);
		makeColumnHeaderWrappable(descriptionColumn);
		makeColumnHeaderWrappable(rankingRepresentationColumn);

		// Since these items are sorted manually, we do not
		// want the user to be able to click on a column header and sort it
		nameColumn.setSortable(false);
		descriptionColumn.setSortable(false);
		rankingRepresentationColumn.setSortable(false);

		// Clear details.
		showOTRankingDetails(null);

		// Listen for selection changes and show the details when changed.
		otRankingsTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showOTRankingDetails(newValue));
		
		// Handle TextField text changes.
		nameField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentRanking != null) {
				currentRanking.setName(nameField.getText());
			}
			if (languageProject != null) {
				nameField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});
		descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentRanking != null) {
				currentRanking.setDescription(descriptionField.getText());
			}
			if (languageProject != null) {
				descriptionField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});

		activeCheckBox.setOnAction((event) -> {
			if (currentRanking != null) {
				currentRanking.setActive(activeCheckBox.isSelected());
				forceTableRowToRedisplayPerActiveSetting(currentRanking);
			}
			displayFieldsPerActiveSetting(currentRanking);
		});

		// Use of Enter move focus to next item.
		nameField.setOnAction((event) -> {
			descriptionField.requestFocus();
		});
		descriptionField.setOnAction((event) -> {
			rankingRepresentationButton.requestFocus();
		});

		nameField.requestFocus();
	}

	public void displayFieldsPerActiveSetting(OTConstraintRanking ranking) {
		boolean fIsActive;
		if (ranking == null) {
			fIsActive = false;
		} else {
			fIsActive = ranking.isActive();
		}
		nameField.setDisable(!fIsActive);
		descriptionField.setDisable(!fIsActive);
		rankingTextFlow.setDisable(!fIsActive);
		rankingRepresentationButton.setDisable(!fIsActive);
	}

	private void forceTableRowToRedisplayPerActiveSetting(OTConstraintRanking ranking) {
		// we need to make the content of the row cells change in order for
		// the cell factory to fire.
		// We do this by getting the value, blanking it, and then restoring it.
		String temp = ranking.getName();
		ranking.setName("");
		ranking.setName(temp);
		temp = ranking.getDescription();
		ranking.setDescription("");
		ranking.setDescription(temp);
		temp = ranking.getRankingRepresentation();
		ranking.setRankingRepresentation("");
		ranking.setRankingRepresentation(temp);
	}

	private void showOTRankingDetails(OTConstraintRanking ranking) {
		currentRanking = ranking;
		if (ranking != null) {
			// Fill the text fields with info from the NPRule object.
			nameField.setText(ranking.getName());
			descriptionField.setText(ranking.getDescription());
			NodeOrientation analysisOrientation = languageProject.getAnalysisLanguage()
					.getOrientation();
			nameField.setNodeOrientation(analysisOrientation);
			descriptionField.setNodeOrientation(analysisOrientation);
			//rankingRepresentationTextField.setText(ranking.getRankingRepresentation());
			activeCheckBox.setSelected(ranking.isActive());
			setUpDownButtonDisabled();
			int currentItem = otRankingsTable.getItems().indexOf(currentRanking);
			this.mainApp.updateStatusBarNumberOfItems((currentItem + 1) + "/"
					+ otRankingsTable.getItems().size() + " ");
			mainApp.getApplicationPreferences().setLastOTConstraintRankingsViewItemUsed(currentItem);
		} else {
			if (nameField != null) {
				nameField.setText("");
			}
			if (descriptionField != null) {
				descriptionField.setText("");
			}
//			if (rankingRepresentationTextField != null) {
//				rankingRepresentationTextField.setText("");
//			}
			buttonMoveDown.setDisable(true);
			buttonMoveUp.setDisable(true);
		}
		displayFieldsPerActiveSetting(ranking);
	}

	@Override
	public void setViewItemUsed(int value) {
		int max = otRankingsTable.getItems().size();
		value = adjustIndexValue(value, max);
		otRankingsTable.getSelectionModel().clearAndSelect(value);
	}

	protected void setUpDownButtonDisabled() {
		int iThis = otApproach.getOTConstraintRankings().indexOf(currentRanking) + 1;
		int iSize = otApproach.getOTConstraintRankings().size();
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

	public void setData(OTApproach otApproachData) {
		otApproach = otApproachData;
		languageProject = otApproach.getLanguageProject();
		// no sorting needed

		// Add observable list data to the table
		otRankingsTable.setItems(otApproachData.getOTConstraintRankings());
		int max = otRankingsTable.getItems().size();
		if (max > 0) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					int iLastIndex = mainApp.getApplicationPreferences()
							.getLastOTConstraintRankingsViewItemUsed();
					iLastIndex = adjustIndexValue(iLastIndex, max);
					otRankingsTable.requestFocus();
					otRankingsTable.getSelectionModel().select(iLastIndex);
					otRankingsTable.getFocusModel().focus(iLastIndex);
					otRankingsTable.scrollTo(iLastIndex);
				}
			});
		}
		if (languageProject != null) {
			String sAnalysis = mainApp.getStyleFromColor(languageProject.getAnalysisLanguage().getColor());
			nameField.setStyle(sAnalysis);
			descriptionField.setStyle(sAnalysis);
		}
	}
	
	@Override
	void handleInsertNewItem() {
		// TODO: use a chooser that has two options:
		// start with the ranking of another ranking
		// use the current order of the contraints
		OTConstraintRanking newRanking = new OTConstraintRanking();
		otApproach.getOTConstraintRankings().add(newRanking);
		handleInsertNewItem(otApproach.getOTConstraintRankings(), otRankingsTable);
	}

	@Override
	void handleRemoveItem() {
		handleRemoveItem(otApproach.getOTConstraintRankings(), currentRanking, otRankingsTable);
	}

	@Override
	void handlePreviousItem() {
		handlePreviousItem(otApproach.getOTConstraintRankings(), currentRanking, otRankingsTable);
	}

	@Override
	void handleNextItem() {
		handleNextItem(otApproach.getOTConstraintRankings(), currentRanking, otRankingsTable);
	}

	@FXML
	void handleLaunchRankingChooser() {
		try {
			// Load the fxml file and create a new stage for the popup.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ApproachViewNavigator.class
					.getResource("fxml/OTConstraintRankingChooser.fxml"));
			loader.setResources(ResourceBundle.getBundle(
					Constants.RESOURCE_LOCATION, locale));

			AnchorPane page = loader.load();
			Stage dialogStage = new Stage();
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			// set the icon
			dialogStage.getIcons().add(mainApp.getNewMainIconImage());
			dialogStage.setTitle(MainApp.kApplicationTitle);

			OTConstraintRankingChooserController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setRanking(currentRanking);
			controller.setData(otApproach);
			controller.initializeTableColumnWidths(mainApp.getApplicationPreferences());

			dialogStage.showAndWait();
			
			if (controller.isOkClicked()) {
//				rankingRepresentationTextField.setText(controller.getRankingRepresentation());
				currentRanking.setRankingRepresentation(controller.getRankingRepresentation());
			}

		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
	}

	@FXML
	void handleMoveDown() {
		int i = otApproach.getOTConstraintRankings().indexOf(currentRanking);
		if ((i + 1) < otApproach.getOTConstraintRankings().size()) {
			Collections.swap(otApproach.getOTConstraintRankings(), i, i + 1);
		}
	}

	@FXML
	void handleMoveUp() {
		int i = otApproach.getOTConstraintRankings().indexOf(currentRanking);
		if (i > 0) {
			Collections.swap(otApproach.getOTConstraintRankings(), i, i - 1);
		}
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return new TextField[] { nameField, descriptionField };
	}
}
