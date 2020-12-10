// Copyright (c) 2016-2020 SIL International
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
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVSyllablePattern;
import org.sil.utility.view.ControllerUtilities;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class CVSyllablePatternsController extends SplitPaneWithTableViewController {

	protected final class AnalysisWrappingTableCell extends TableCell<CVSyllablePattern, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processAnalysisTableCell(this, text, item, empty);
		}
	}

	@FXML
	private TableView<CVSyllablePattern> cvSyllablePatternTable;
	@FXML
	private TableColumn<CVSyllablePattern, String> nameColumn;
	@FXML
	private TableColumn<CVSyllablePattern, String> naturalClassColumn;
	@FXML
	private TableColumn<CVSyllablePattern, String> descriptionColumn;
	@FXML
	private TableColumn<CVSyllablePattern, Boolean> checkBoxColumn;
	@FXML
	private CheckBox checkBoxColumnHead;

	@FXML
	private TextField nameField;
	@FXML
	private TextField naturalClassesField;
	@FXML
	private TextField descriptionField;
	@FXML
	private FlowPane ncsField;
	@FXML
	private TextFlow ncsTextFlow;
	@FXML
	private Button ncsButton;
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
	// @FXML
	// private TextField sncRepresentationField;

	private CVSyllablePattern currentSyllablePattern;

	public CVSyllablePatternsController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.setApproach(ApplicationPreferences.CV_SYLLABLE_PATTERNS);
		super.setTableView(cvSyllablePatternTable);
		super.initialize(location, resources);

		bundle = resources;
		// Initialize the button icons
		tooltipMoveUp = ControllerUtilities.createToolbarButtonWithImage("UpArrow.png",
				buttonMoveUp, tooltipMoveUp, bundle.getString("cv.view.syllablepatterns.up"),
				Constants.RESOURCE_SOURCE_LOCATION);
		tooltipMoveDown = ControllerUtilities.createToolbarButtonWithImage("DownArrow.png",
				buttonMoveDown, tooltipMoveDown, bundle.getString("cv.view.syllablepatterns.down"),
				Constants.RESOURCE_SOURCE_LOCATION);

		// checkBoxColumn.setCellValueFactory(cellData ->
		// cellData.getValue().activeCheckBoxProperty());
		// checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));
		// checkBoxColumn.setEditable(true);
		// checkBoxColumnHead.setOnAction((event) -> {
		// handleCheckBoxColumnHead();
		// });
		// initializeCheckBoxContextMenu(resources);

		nameColumn.setCellValueFactory(cellData -> cellData.getValue().spNameProperty());
		naturalClassColumn.setCellValueFactory(cellData -> cellData.getValue()
				.ncsRepresentationProperty());
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

		// Custom rendering of the table cell.
		nameColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});
		naturalClassColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});

		makeColumnHeaderWrappable(nameColumn);
		makeColumnHeaderWrappable(naturalClassColumn);
		makeColumnHeaderWrappable(descriptionColumn);

		// Since syllable patterns are sorted manually, we do not
		// want the user to be able to click on a column header and sort it
		nameColumn.setSortable(false);
		naturalClassColumn.setSortable(false);
		descriptionColumn.setSortable(false);

		// Clear cv syllable pattern details.
		showCVSyllablePatternDetails(null);

		// Listen for selection changes and show the details when changed.
		cvSyllablePatternTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showCVSyllablePatternDetails(newValue));

		// Handle TextField text changes.
		nameField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentSyllablePattern != null) {
				currentSyllablePattern.setSPName(nameField.getText());
			}
			if (languageProject != null) {
				nameField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});
		descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentSyllablePattern != null) {
				currentSyllablePattern.setDescription(descriptionField.getText());
			}
			if (languageProject != null) {
				descriptionField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});

		activeCheckBox.setOnAction((event) -> {
			if (currentSyllablePattern != null) {
				currentSyllablePattern.setActive(activeCheckBox.isSelected());
				showNaturalClassesContent();
				forceTableRowToRedisplayPerActiveSetting(currentSyllablePattern);
			}
			displayFieldsPerActiveSetting(currentSyllablePattern);
		});

		// Use of Enter move focus to next item.
		nameField.setOnAction((event) -> {
			descriptionField.requestFocus();
		});
		descriptionField.setOnAction((event) -> {
			naturalClassesField.requestFocus();
		});

		nameField.requestFocus();

	}

	public void displayFieldsPerActiveSetting(CVSyllablePattern syllablePattern) {
		boolean fIsActive;
		if (syllablePattern == null) {
			fIsActive = false;
		} else {
			fIsActive = syllablePattern.isActive();
		}
		nameField.setDisable(!fIsActive);
		ncsTextFlow.setDisable(!fIsActive);
		ncsButton.setDisable(!fIsActive);
		descriptionField.setDisable(!fIsActive);
	}

	private void forceTableRowToRedisplayPerActiveSetting(CVSyllablePattern syllablePattern) {
		// we need to make the content of the row cells change in order for
		// the cell factory to fire.
		// We do this by getting the value, blanking it, and then restoring it.
		String temp = syllablePattern.getSPName();
		syllablePattern.setSPName("");
		syllablePattern.setSPName(temp);
		temp = syllablePattern.getNCSRepresentation();
		syllablePattern.setNCSRepresentation("");
		syllablePattern.setNCSRepresentation(temp);
		temp = syllablePattern.getDescription();
		syllablePattern.setDescription("");
		syllablePattern.setDescription(temp);
	}

	/**
	 * Fills all text fields to show details about the CV natural class. If the
	 * specified segment is null, all text fields are cleared.
	 *
	 * @param syllablePattern
	 *            the segment or null
	 */
	private void showCVSyllablePatternDetails(CVSyllablePattern syllablePattern) {
		currentSyllablePattern = syllablePattern;
		if (syllablePattern != null) {
			// Fill the text fields with info from the person object.
			nameField.setText(syllablePattern.getSPName());
			descriptionField.setText(syllablePattern.getDescription());
			NodeOrientation analysisOrientation = languageProject.getAnalysisLanguage()
					.getOrientation();
			nameField.setNodeOrientation(analysisOrientation);
			descriptionField.setNodeOrientation(analysisOrientation);
			ncsTextFlow.setNodeOrientation(analysisOrientation);
			activeCheckBox.setSelected(syllablePattern.isActive());
			showNaturalClassesContent();
			setUpDownButtonDisabled();
		} else {
			// Segment is null, remove all the text.
			if (nameField != null) {
				nameField.setText("");
			}
			if (descriptionField != null) {
				descriptionField.setText("");
			}
			if (ncsTextFlow != null) {
				ncsTextFlow.getChildren().clear();
			}
			buttonMoveDown.setDisable(true);
			buttonMoveUp.setDisable(true);
		}
		displayFieldsPerActiveSetting(syllablePattern);

		if (syllablePattern != null) {
			int currentItem = cvSyllablePatternTable.getItems().indexOf(currentSyllablePattern);
			this.mainApp.updateStatusBarNumberOfItems((currentItem + 1) + "/"
					+ cvSyllablePatternTable.getItems().size() + " ");
			mainApp.getApplicationPreferences().setLastCVSyllablePatternsViewItemUsed(currentItem);
		}
	}

	@Override
	public void setViewItemUsed(int value) {
		int max = cvSyllablePatternTable.getItems().size();
		value = adjustIndexValue(value, max);
		cvSyllablePatternTable.getSelectionModel().clearAndSelect(value);
	}

	protected void setUpDownButtonDisabled() {
		int iThis = cvApproach.getCVSyllablePatterns().indexOf(currentSyllablePattern) + 1;
		int iSize = cvApproach.getCVSyllablePatterns().size();
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

	private void showNaturalClassesContent() {
		// TODO: can we do this with lambdas?
		StringBuilder sb = new StringBuilder();
		ncsTextFlow.getChildren().clear();
		if (currentSyllablePattern.isWordInitial()) {
			addNameToContent(sb, Constants.WORD_BOUNDARY_SYMBOL, true);
			sb.append(", ");
		}
		ObservableList<CVNaturalClass> naturalClasses = currentSyllablePattern.getNCs();
		if (languageProject.getAnalysisLanguage().getOrientation() ==  NodeOrientation.LEFT_TO_RIGHT) {
			createStringOfNaturalClasses(sb, naturalClasses);
		} else {
			FXCollections.reverse(naturalClasses);
			createStringOfNaturalClasses(sb, naturalClasses);
			FXCollections.reverse(naturalClasses);
		}
		if (currentSyllablePattern.isWordFinal()) {
			sb.append(", ");
			addNameToContent(sb, Constants.WORD_BOUNDARY_SYMBOL, true);
		}
		currentSyllablePattern.setNCSRepresentation(sb.toString());
	}

	protected void createStringOfNaturalClasses(StringBuilder sb,
			ObservableList<CVNaturalClass> naturalClasses) {
		int i = 1;
		int iCount = naturalClasses.size();
		for (SylParserObject spo : naturalClasses) {
			CVNaturalClass nc = (CVNaturalClass) spo;
			if (nc != null) {
				addNameToContent(sb, nc.getNCName(), spo.isActive());
				if (i++ < iCount) {
					sb.append(", ");
				}
			}
		}
	}

	protected void addNameToContent(StringBuilder sb, String sName, boolean isActive) {
		Text t = new Text(sName);
		t.setFont(languageProject.getAnalysisLanguage().getFont());
		if (isActive && activeCheckBox.isSelected()) {
			t.setFill(languageProject.getAnalysisLanguage().getColor());
			t.setNodeOrientation(languageProject.getAnalysisLanguage().getOrientation());
		} else {
			t.setFill(Constants.INACTIVE);
		}
		Text tBar = new Text(" | ");
		tBar.setStyle("-fx-stroke: lightgrey;");
		ncsTextFlow.getChildren().addAll(t, tBar);
		sb.append(sName);
	}

	public void setSyllablePattern(CVSyllablePattern syllablePattern) {
		nameField.setText(syllablePattern.getSPName());
		descriptionField.setText(syllablePattern.getDescription());
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 *
	 * @param cvApproachController
	 */
	public void setData(CVApproach cvApproachData) {
		cvApproach = cvApproachData;
		languageProject = cvApproach.getLanguageProject();
		// no sorting allowed

		// Add observable list data to the table
		cvSyllablePatternTable.setItems(cvApproachData.getCVSyllablePatterns());
		int max = cvSyllablePatternTable.getItems().size();
		if (max > 0) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					int iLastIndex = mainApp.getApplicationPreferences()
							.getLastCVSyllablePatternsViewItemUsed();
					iLastIndex = adjustIndexValue(iLastIndex, max);
					cvSyllablePatternTable.requestFocus();
					cvSyllablePatternTable.getSelectionModel().select(iLastIndex);
					cvSyllablePatternTable.getFocusModel().focus(iLastIndex);
					// want to do following only if the selected item is not
					// visible
					// cvSyllablePatternTable.isVisible();
					cvSyllablePatternTable.scrollTo(iLastIndex);
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
		CVSyllablePattern newSyllablePattern = new CVSyllablePattern();
		cvApproach.getCVSyllablePatterns().add(newSyllablePattern);
		handleInsertNewItem(cvApproach.getCVSyllablePatterns(), cvSyllablePatternTable);
	}

	@Override
	void handleRemoveItem() {
		handleRemoveItem(cvApproach.getCVSyllablePatterns(), currentSyllablePattern, cvSyllablePatternTable);
	}

	@Override
	void handlePreviousItem() {
		handlePreviousItem(cvApproach.getCVSyllablePatterns(), currentSyllablePattern, cvSyllablePatternTable);
	}

	@Override
	void handleNextItem() {
		handleNextItem(cvApproach.getCVSyllablePatterns(), currentSyllablePattern, cvSyllablePatternTable);
	}

	@FXML
	void handleLaunchNCSequenceChooser() {
		showNCSequenceChooser();
		showNaturalClassesContent();
	}

	/**
	 * Opens a dialog to show and set sequence of natural classes
	 */
	public void showNCSequenceChooser() {
		try {
			// Load the fxml file and create a new stage for the popup.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ApproachViewNavigator.class
					.getResource("fxml/CVSyllablePatternNaturalClassChooser.fxml"));
			loader.setResources(ResourceBundle.getBundle(
					"org.sil.syllableparser.resources.SyllableParser", locale));

			AnchorPane page = loader.load();
			Stage dialogStage = new Stage();
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			// set the icon
			dialogStage.getIcons().add(mainApp.getNewMainIconImage());
			dialogStage.setTitle(MainApp.kApplicationTitle);

			CVSyllablePatternNaturalClassChooserController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setData(cvApproach);
			controller.setSyllablePattern(currentSyllablePattern);

			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
	}

	@FXML
	void handleMoveDown() {
		int i = cvApproach.getCVSyllablePatterns().indexOf(currentSyllablePattern);
		if ((i + 1) < cvApproach.getCVSyllablePatterns().size()) {
			Collections.swap(cvApproach.getCVSyllablePatterns(), i, i + 1);
		}
	}

	@FXML
	void handleMoveUp() {
		int i = cvApproach.getCVSyllablePatterns().indexOf(currentSyllablePattern);
		if (i > 0) {
			Collections.swap(cvApproach.getCVSyllablePatterns(), i, i - 1);
		}
	}

	protected void handleCheckBoxSelectAll() {
		for (CVSyllablePattern syllablePattern : cvApproach.getCVSyllablePatterns()) {
			syllablePattern.setActive(true);
			forceTableRowToRedisplayPerActiveSetting(syllablePattern);
		}
	}

	protected void handleCheckBoxClearAll() {
		for (CVSyllablePattern syllablePattern : cvApproach.getCVSyllablePatterns()) {
			syllablePattern.setActive(false);
			forceTableRowToRedisplayPerActiveSetting(syllablePattern);
		}
	}

	protected void handleCheckBoxToggle() {
		for (CVSyllablePattern syllablePattern : cvApproach.getCVSyllablePatterns()) {
			if (syllablePattern.isActive()) {
				syllablePattern.setActive(false);
			} else {
				syllablePattern.setActive(true);
			}
			forceTableRowToRedisplayPerActiveSetting(syllablePattern);
		}
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return new TextField[] { nameField, descriptionField };
	}
}
