// Copyright (c) 2016-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.Approach;
import org.sil.syllableparser.model.Environment;
import org.sil.syllableparser.model.Grapheme;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class EnvironmentChooserController extends TableViewWithCheckBoxColumnController {

	protected final class AnalysisWrappingTableCell extends TableCell<Environment, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processAnalysisTableCell(this, text, item, empty);
		}
	}

	protected final class WrappingTableCell extends TableCell<Environment, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processTableCell(this, text, item, empty);
		}
	}

	@FXML
	private TableView<Environment> environmentTable;
	@FXML
	private TableColumn<Environment, Boolean> checkBoxColumn;
	@FXML
	private TableColumn<Environment, String> environmentColumn;
	@FXML
	private TableColumn<Environment, String> descriptionColumn;
	Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;
	private ApplicationPreferences preferences;

	private ObservableList<Environment> environments = FXCollections.observableArrayList();
	private Grapheme grapheme;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		super.setApproach(ApplicationPreferences.ENVIRONMENT_CHOOSER);
		super.setTableView(environmentTable);
		super.initialize(location, resources);

		// Initialize the table with the three columns.
		checkBoxColumn
				.setCellValueFactory(cellData -> cellData.getValue().activeCheckBoxProperty());
		checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));
		checkBoxColumn.setEditable(true);
		checkBoxColumnHead.setOnAction((event) -> {
			handleCheckBoxColumnHead();
		});
		environmentColumn.setCellValueFactory(cellData -> cellData.getValue()
				.environmentRepresentationProperty());
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		environmentTable.setEditable(true);
		// Custom rendering of the table cell.
		environmentColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});

		initializeCheckBoxContextMenu(resources);

		environmentTable.setOnKeyPressed(keyEvent -> {
			switch (keyEvent.getCode()) {
			case SPACE: {
				keyEvent.consume();
				Environment env = environmentTable.getSelectionModel().getSelectedItem();
				if (env != null) {
					env.setActiveCheckBox(!env.isActiveCheckBox());
				}
				break;
			}
			default:
				break;
			}
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

	public void setData(Approach cvApproachData) {
		languageProject = cvApproachData.getLanguageProject();
		environments = FXCollections.observableList(languageProject.getActiveAndValidEnvironments());
		for (Environment env : environments) {
			if (Environment.findIndexInListByUuid(grapheme.getEnvs(),
					env.getID()) > -1) {
				env.setActiveCheckBox(true);
			} else {
				env.setActiveCheckBox(false);
			}
		}
		// Add observable list data to the table
		environmentTable.setItems(environments);
		if (environmentTable.getItems().size() > 0) {
			// select one
			environmentTable.requestFocus();
			environmentTable.getSelectionModel().select(0);
			environmentTable.getFocusModel().focus(0);
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
		grapheme.getEnvs().clear();
		// find the environment by ID and
		// add it to the environment list;
		// we use this method in order to guarantee we get the actual object and
		// not a copy
		for (Environment env : environments) {
			if (env.isActiveCheckBox()) {
				int i = Environment.findIndexInListByUuid(languageProject.getEnvironments(),
						env.getID());
				grapheme.getEnvs().add(languageProject.getEnvironments().get(i));
			}
		}
		grapheme.recalulateEnvsRepresentation();

		okClicked = true;
		handleCancel();
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		preferences.setLastWindowParameters(ApplicationPreferences.LAST_CV_ENVIRONMENTS_CHOOSER,
				dialogStage);
		dialogStage.close();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		preferences = mainApp.getApplicationPreferences();
		dialogStage = preferences.getLastWindowParameters(
				ApplicationPreferences.LAST_CV_ENVIRONMENTS_CHOOSER, dialogStage, 400., 400.);
	}

	/**
	 * Called when the user clicks help.
	 */
	@FXML
	private void handleHelp() {
		mainApp.showNotImplementedYet();
	}

	public Grapheme getGrapheme() {
		return grapheme;
	}

	public void setGrapheme(Grapheme grapheme) {
		this.grapheme = grapheme;
	}

	protected void handleCheckBoxSelectAll() {
		for (Environment env : environments) {
			env.setActiveCheckBox(true);
		}
	}

	protected void handleCheckBoxClearAll() {
		for (Environment env : environments) {
			env.setActiveCheckBox(false);
		}
	}

	protected void handleCheckBoxToggle() {
		for (Environment env : environments) {
			if (env.isActiveCheckBox()) {
				env.setActiveCheckBox(false);
			} else {
				env.setActiveCheckBox(true);
			}
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
		//  nothing to do
	}
}
