// Copyright (c) 2025 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.Language;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.hyphenapproach.HyphenApproach;
import org.sil.syllableparser.model.hyphenapproach.HyphenClass;
import org.sil.syllableparser.model.hyphenapproach.SegmentInHyphenClass;
import org.sil.utility.service.keyboards.KeyboardChanger;
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
import javafx.scene.control.TextArea;
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

public class HyphenClassController extends SplitPaneWithTableViewController {

	protected final class AnalysisWrappingTableCell extends TableCell<HyphenClass, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processAnalysisTableCell(this, text, item, empty);
		}
	}

	protected final class VernacularWrappingTableCell extends TableCell<HyphenClass, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processVernacularTableCell(this, text, item, empty);
		}
	}

	protected final class WrappingTableCell extends TableCell<HyphenClass, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processTableCell(this, text, item, empty);
		}
	}

	@FXML
	private TableView<HyphenClass> hyphenClassTable;
	@FXML
	private TableColumn<HyphenClass, String> nameColumn;
	@FXML
	private TableColumn<HyphenClass, String> classColumn;
	@FXML
	private TableColumn<HyphenClass, String> descriptionColumn;
	@FXML
	private TableColumn<HyphenClass, Boolean> checkBoxColumn;
	@FXML
	private CheckBox checkBoxColumnHead;

	@FXML
	private TextField nameField;
	@FXML
	private TextField classField;
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
	@FXML
	private TextArea errorTextArea;

	private HyphenClass currentHyphenClass;

	public HyphenClassController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.setApproach(ApplicationPreferences.HYPHEN_CLASSES);
		super.setTableView(hyphenClassTable);
		super.initialize(location, resources);

		bundle = resources;
		// Initialize the button icons
		tooltipMoveUp = ControllerUtilities.createToolbarButtonWithImage("UpArrow.png",
				buttonMoveUp, tooltipMoveUp, bundle.getString("sh.view.sonorityhierarchy.up"),
				Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);
		tooltipMoveDown = ControllerUtilities.createToolbarButtonWithImage("DownArrow.png",
				buttonMoveDown, tooltipMoveDown, bundle.getString("sh.view.sonorityhierarchy.down"),
				Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);

		nameColumn.setCellValueFactory(cellData -> cellData.getValue().ncNameProperty());
		classColumn.setCellValueFactory(cellData -> cellData.getValue()
				.segmentsRepresentationProperty());
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

		// Custom rendering of the table cell.
		nameColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});
		classColumn.setCellFactory(column -> {
			return new VernacularWrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});

		makeColumnHeaderWrappable(nameColumn);
		makeColumnHeaderWrappable(classColumn);
		makeColumnHeaderWrappable(descriptionColumn);

		// Since hyphen class items are sorted manually, we do not
		// want the user to be able to click on a column header and sort it
		nameColumn.setSortable(false);
		classColumn.setSortable(false);
		descriptionColumn.setSortable(false);
		errorTextArea.setStyle(Constants.TEXT_COLOR_CSS_BEGIN + "red" + Constants.TEXT_COLOR_CSS_END);

		// Clear hyphen class details.
		showHyphenClassDetails(null);

		// Listen for selection changes and show the details when changed.
		hyphenClassTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showHyphenClassDetails(newValue));

		keyboardChanger = KeyboardChanger.getInstance();
		// Handle TextField text changes.
		nameField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentHyphenClass != null) {
				currentHyphenClass.setClassName(nameField.getText());
			}
			if (languageProject != null) {
				nameField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});
		nameField.focusedProperty().addListener((observable, wasFocused, isNowFocused) -> {
			if (isNowFocused) {
				keyboardChanger.tryToChangeKeyboardTo(languageProject.getAnalysisLanguage().getKeyboard(), MainApp.class);
			}
		});
		descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentHyphenClass != null) {
				currentHyphenClass.setDescription(descriptionField.getText());
			}
			if (languageProject != null) {
				descriptionField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});
		descriptionField.focusedProperty().addListener((observable, wasFocused, isNowFocused) -> {
			if (isNowFocused) {
				keyboardChanger.tryToChangeKeyboardTo(languageProject.getAnalysisLanguage().getKeyboard(), MainApp.class);
			}
		});

		activeCheckBox.setOnAction((event) -> {
			if (currentHyphenClass != null) {
				currentHyphenClass.setActive(activeCheckBox.isSelected());
				showHyphenClassContent();
				forceTableRowToRedisplayPerActiveSetting(currentHyphenClass);
			}
			displayFieldsPerActiveSetting(currentHyphenClass);
		});

		errorTextArea.setEditable(false);

		// Use of Enter move focus to next item.
		nameField.setOnAction((event) -> {
			descriptionField.requestFocus();
		});
		descriptionField.setOnAction((event) -> {
			classField.requestFocus();
		});

		nameField.requestFocus();

	}

	public void displayFieldsPerActiveSetting(HyphenClass hyphenClass) {
		boolean fIsActive;
		if (hyphenClass == null) {
			fIsActive = false;
		} else {
			fIsActive = hyphenClass.isActive();
		}
		nameField.setDisable(!fIsActive);
		ncsTextFlow.setDisable(!fIsActive);
		ncsButton.setDisable(!fIsActive);
		descriptionField.setDisable(!fIsActive);
	}

	private void hideErrors(){
		errorTextArea.setText("");
		errorTextArea.setVisible(false);
	}

	private void forceTableRowToRedisplayPerActiveSetting(HyphenClass naturalCLass) {
		// we need to make the content of the row cells change in order for
		// the cell factory to fire.
		// We do this by getting the value, blanking it, and then restoring it.
		String temp = naturalCLass.getClassName();
		naturalCLass.setClassName("");
		naturalCLass.setClassName(temp);
		temp = naturalCLass.getSegmentsRepresentation();
		naturalCLass.setSegmentsRepresentation("");
		naturalCLass.setSegmentsRepresentation(temp);
		temp = naturalCLass.getDescription();
		naturalCLass.setDescription("");
		naturalCLass.setDescription(temp);
	}

	/**
	 * Fills all text fields to show details about the CV natural class. If the
	 * specified segment is null, all text fields are cleared.
	 *
	 * @param hyphenClass
	 *            the segment or null
	 */
	private void showHyphenClassDetails(HyphenClass hyphenClass) {
		currentHyphenClass = hyphenClass;
		if (hyphenClass != null) {
			// Fill the text fields with info from the person object.
			nameField.setText(hyphenClass.getClassName());
			descriptionField.setText(hyphenClass.getDescription());
			NodeOrientation analysisOrientation = languageProject.getAnalysisLanguage()
					.getOrientation();
			nameField.setNodeOrientation(analysisOrientation);
			descriptionField.setNodeOrientation(analysisOrientation);
			ncsTextFlow.setNodeOrientation(languageProject.getVernacularLanguage()
					.getOrientation());
			activeCheckBox.setSelected(hyphenClass.isActive());
			showHyphenClassContent();
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
		displayFieldsPerActiveSetting(hyphenClass);

		if (hyphenApproach != null)
			showAnySegmentBasedErrrors();

		if (hyphenClass != null) {
			int currentItem = hyphenClassTable.getItems().indexOf(currentHyphenClass);
			this.mainApp.updateStatusBarNumberOfItems((currentItem + 1) + "/"
					+ hyphenClassTable.getItems().size() + " ");

			mainApp.getApplicationPreferences().setLastHyphenClassesViewItemUsed(currentItem);
		}
	}

	protected void showAnySegmentBasedErrrors() {
		hideErrors();
		Set<Segment> missingSegments = hyphenApproach.getMissingSegmentsFromClasses();
		if (missingSegments.size() > 0) {
			errorTextArea.setVisible(true);
			showMissingSegments(missingSegments);
		}
		List<SegmentInHyphenClass> duplicateSegments = hyphenApproach.getDuplicateSegmentsFromHyphenClass();
		if (duplicateSegments.size() > 0) {
			errorTextArea.setVisible(true);
			showDuplicateSegments(duplicateSegments);
		}
	}

	/**
	 * @param duplicateSegments
	 */
	private void showDuplicateSegments(List<SegmentInHyphenClass> duplicateSegments) {
		StringBuilder sb = new StringBuilder();
		sb.append(bundle.getString("sonorityhierarchyerror.duplicatesegmentsinhierarchy"));
		sb.append("\n");
		for (SegmentInHyphenClass segInClass : duplicateSegments) {
			sb.append(segInClass.getSegment().getSegment());
			sb.append("\t");
			sb.append(segInClass.getNaturalClass().getClassName());
			sb.append("\n");
		}
		errorTextArea.setText(sb.toString());
	}

	/**
	 * @param missingSegments
	 */
	private void showMissingSegments(Set<Segment> missingSegments) {
		StringBuilder sb = new StringBuilder();
		sb.append(bundle.getString("sonorityhierarchyerror.missingsegmentsinhierarchy"));
		sb.append("\n");
		for (Segment seg : missingSegments) {
			sb.append(seg.getSegment());
			sb.append("\n");
		}
		errorTextArea.setText(sb.toString());
	}

	@Override
	public void setViewItemUsed(int value) {
		int max = hyphenClassTable.getItems().size();
		value = adjustIndexValue(value, max);
		hyphenClassTable.getSelectionModel().clearAndSelect(value);
	}

	protected void setUpDownButtonDisabled() {
		int iThis = hyphenApproach.getHyphenClasses().indexOf(currentHyphenClass) + 1;
		int iSize = hyphenApproach.getHyphenClasses().size();
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

	private void showHyphenClassContent() {
		// TODO: can we do this with lambdas?
		StringBuilder sb = new StringBuilder();
		ncsTextFlow.getChildren().clear();
		ObservableList<Segment> segments = currentHyphenClass.getSegments();
		if (languageProject.getVernacularLanguage().getOrientation() == NodeOrientation.LEFT_TO_RIGHT) {
			fillNcsTextFlow(sb, segments);
		} else {
			FXCollections.reverse(segments);
			fillNcsTextFlow(sb, segments);
			FXCollections.reverse(segments);
		}
		currentHyphenClass.setSegmentsRepresentation(sb.toString());
	}

	protected void fillNcsTextFlow(StringBuilder sb, ObservableList<Segment> segments) {
		int i = 1;
		int iCount = segments.size();
		for (Segment seg : segments) {
			addNameToContent(sb, seg.getSegment(), seg.isActive());
			if (i++ < iCount) {
				sb.append(", ");
			}
		}
	}

	protected void addNameToContent(StringBuilder sb, String sName, boolean isActive) {
		Language vernacular = languageProject.getVernacularLanguage();
		Text t = new Text(sName);
		if (isActive && activeCheckBox.isSelected()) {
			t.setFont(vernacular.getFont());
			t.setFill(vernacular.getColor());
			t.setNodeOrientation(vernacular.getOrientation());
		} else {
			t.setFill(Constants.INACTIVE);
		}
		Text tBar = new Text(" | ");
		tBar.setStyle("-fx-stroke: lightgrey;");
		ncsTextFlow.getChildren().addAll(t, tBar);
		sb.append(sName);
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 *
	 * @param cvApproachController
	 */
	public void setData(HyphenApproach hypApproachData) {
		hyphenApproach = hypApproachData;
		languageProject = hyphenApproach.getLanguageProject();
		// no sorting needed

		// Add observable list data to the table
		hyphenClassTable.setItems(hypApproachData.getHyphenClasses());
		int max = hyphenClassTable.getItems().size();
		if (max > 0) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					int iLastIndex = 0;
					iLastIndex = mainApp.getApplicationPreferences().getLastHyphenClassesViewItemUsed();
					iLastIndex = adjustIndexValue(iLastIndex, max);
					hyphenClassTable.requestFocus();
					hyphenClassTable.getSelectionModel().select(iLastIndex);
					hyphenClassTable.getFocusModel().focus(iLastIndex);
					// want to do following only if the selected item is not
					// visible
					// SHNaturalClassTable.isVisible();
					hyphenClassTable.scrollTo(iLastIndex);
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
		HyphenClass newNaturalCLass = new HyphenClass();
		hyphenApproach.getHyphenClasses().add(newNaturalCLass);
		handleInsertNewItem(hyphenApproach.getHyphenClasses(), hyphenClassTable);
	}

	@Override
	void handleRemoveItem() {
		handleRemoveItem(hyphenApproach.getHyphenClasses(), currentHyphenClass, hyphenClassTable);
	}

	@Override
	void handlePreviousItem() {
		handlePreviousItem(hyphenApproach.getHyphenClasses(), currentHyphenClass, hyphenClassTable);
	}

	@Override
	void handleNextItem() {
		handleNextItem(hyphenApproach.getHyphenClasses(), currentHyphenClass, hyphenClassTable);
	}

	@FXML
	void handleLaunchSegmentChooser() {
		showSegmentChooser();
		showHyphenClassContent();
		showAnySegmentBasedErrrors();
	}

	/**
	 * Opens a dialog to show and set segments
	 */
	public void showSegmentChooser() {
		try {
			// Load the fxml file and create a new stage for the popup.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ApproachViewNavigator.class
					.getResource("fxml/HyphenSegmentChooser.fxml"));
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

			HyphenSegmentChooserController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setHyphenClass(currentHyphenClass);
			controller.setData(hyphenApproach);
			controller.initializeTableColumnWidths(mainApp.getApplicationPreferences());

			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
	}

	@FXML
	void handleMoveDown() {
		int i = hyphenApproach.getHyphenClasses().indexOf(currentHyphenClass);
		if ((i + 1) < hyphenApproach.getHyphenClasses().size()) {
			Collections.swap(hyphenApproach.getHyphenClasses(), i, i + 1);
		}
		tableView.refresh();
	}

	@FXML
	void handleMoveUp() {
		int i = hyphenApproach.getHyphenClasses().indexOf(currentHyphenClass);
		if (i > 0) {
			Collections.swap(hyphenApproach.getHyphenClasses(), i, i - 1);
		}
		tableView.refresh();
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return new TextField[] { nameField, descriptionField };
	}
}
