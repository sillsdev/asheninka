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
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.hyphenapproach.HyphenApproach;
import org.sil.syllableparser.model.hyphenapproach.HyphenChangeRule;
import org.sil.syllableparser.model.hyphenapproach.HyphenClass;
import org.sil.syllableparser.model.hyphenapproach.SegmentInHyphenClass;
import org.sil.syllableparser.service.HyphenChangeRuleValidator;
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

public class HyphenChangeRulesController extends SplitPaneWithTableViewController {

	protected final class AnalysisWrappingTableCell extends TableCell<HyphenChangeRule, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processAnalysisTableCell(this, text, item, empty);
		}
	}

	@FXML
	private TableView<HyphenChangeRule> hyphenChangeRulesTable;
	@FXML
	private TableColumn<HyphenChangeRule, String> nameColumn;
	@FXML
	private TableColumn<HyphenChangeRule, String> matchColumn;
	@FXML
	private TableColumn<HyphenChangeRule, String> changeColumn;
	@FXML
	private TableColumn<HyphenChangeRule, String> descriptionColumn;
	@FXML
	private TableColumn<HyphenChangeRule, Boolean> checkBoxColumn;
	@FXML
	private CheckBox checkBoxColumnHead;

	@FXML
	private TextField nameField;
	@FXML
	private TextField matchField;
	@FXML
	private TextField changeField;
	@FXML
	private TextField descriptionField;
	@FXML
	private FlowPane matchesField;
	@FXML
	private FlowPane changesField;
	@FXML
	private TextFlow matchesTextFlow;
	@FXML
	private TextFlow changesTextFlow;
	@FXML
	private Button matchButton;
	@FXML
	private Button changeButton;
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

	private HyphenChangeRule currentHyphenChangeRule;

	public HyphenChangeRulesController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.setApproach(ApplicationPreferences.HYPHEN_CHANGE_RULES);
		super.setTableView(hyphenChangeRulesTable);
		super.initialize(location, resources);

		bundle = resources;
		// Initialize the button icons
		tooltipMoveUp = ControllerUtilities.createToolbarButtonWithImage("UpArrow.png",
				buttonMoveUp, tooltipMoveUp, bundle.getString("cv.view.syllablepatterns.up"),
				Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);
		tooltipMoveDown = ControllerUtilities.createToolbarButtonWithImage("DownArrow.png",
				buttonMoveDown, tooltipMoveDown, bundle.getString("cv.view.syllablepatterns.down"),
				Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);

		// checkBoxColumn.setCellValueFactory(cellData ->
		// cellData.getValue().activeCheckBoxProperty());
		// checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));
		// checkBoxColumn.setEditable(true);
		// checkBoxColumnHead.setOnAction((event) -> {
		// handleCheckBoxColumnHead();
		// });
		// initializeCheckBoxContextMenu(resources);

		nameColumn.setCellValueFactory(cellData -> cellData.getValue().ruleNameProperty());
		matchColumn.setCellValueFactory(cellData -> cellData.getValue()
				.matchRepresentationProperty());
		changeColumn.setCellValueFactory(cellData -> cellData.getValue()
				.changeRepresentationProperty());
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		errorTextArea.setStyle(Constants.TEXT_COLOR_CSS_BEGIN + "red" + Constants.TEXT_COLOR_CSS_END);
		errorTextArea.setEditable(false);

		// Custom rendering of the table cell.
		nameColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});
		matchColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});
		changeColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});

		makeColumnHeaderWrappable(nameColumn);
		makeColumnHeaderWrappable(matchColumn);
		makeColumnHeaderWrappable(changeColumn);
		makeColumnHeaderWrappable(descriptionColumn);

		// Since syllable patterns are sorted manually, we do not
		// want the user to be able to click on a column header and sort it
		nameColumn.setSortable(false);
		matchColumn.setSortable(false);
		changeColumn.setSortable(false);
		descriptionColumn.setSortable(false);

		// Clear cv syllable pattern details.
		showHyphenChangeRuleDetails(null);

		// Listen for selection changes and show the details when changed.
		hyphenChangeRulesTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showHyphenChangeRuleDetails(newValue));

		keyboardChanger = KeyboardChanger.getInstance();
		// Handle TextField text changes.
		nameField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentHyphenChangeRule != null) {
				currentHyphenChangeRule.setRuleName(nameField.getText());
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
			if (currentHyphenChangeRule != null) {
				currentHyphenChangeRule.setDescription(descriptionField.getText());
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
			if (currentHyphenChangeRule != null) {
				currentHyphenChangeRule.setActive(activeCheckBox.isSelected());
				showMatchesContent();
				showChangesContent();
				forceTableRowToRedisplayPerActiveSetting(currentHyphenChangeRule);
			}
			displayFieldsPerActiveSetting(currentHyphenChangeRule);
		});

		// Use of Enter move focus to next item.
		nameField.setOnAction((event) -> {
			descriptionField.requestFocus();
		});
		descriptionField.setOnAction((event) -> {
			changeField.requestFocus();
		});

		nameField.requestFocus();
	}

	public void displayFieldsPerActiveSetting(HyphenChangeRule hyphenChangeRule) {
		boolean fIsActive;
		if (hyphenChangeRule == null) {
			fIsActive = false;
		} else {
			fIsActive = hyphenChangeRule.isActive();
		}
		nameField.setDisable(!fIsActive);
		matchesTextFlow.setDisable(!fIsActive);
		matchButton.setDisable(!fIsActive);
		changesTextFlow.setDisable(!fIsActive);
		changeButton.setDisable(!fIsActive);
		descriptionField.setDisable(!fIsActive);
	}

	private void hideErrors(){
		errorTextArea.setText("");
		errorTextArea.setVisible(false);
	}

	private void forceTableRowToRedisplayPerActiveSetting(HyphenChangeRule hyphenChangeRule) {
		// we need to make the content of the row cells change in order for
		// the cell factory to fire.
		// We do this by getting the value, blanking it, and then restoring it.
		String temp = hyphenChangeRule.getRuleName();
		hyphenChangeRule.setRuleName("");
		hyphenChangeRule.setRuleName(temp);
		temp = hyphenChangeRule.getMatchRepresentation();
		hyphenChangeRule.setMatchRepresentation("");
		hyphenChangeRule.setMatchRepresentation(temp);
		temp = hyphenChangeRule.getChangeRepresentation();
		hyphenChangeRule.setChangeRepresentation("");
		hyphenChangeRule.setChangeRepresentation(temp);
		temp = hyphenChangeRule.getDescription();
		hyphenChangeRule.setDescription("");
		hyphenChangeRule.setDescription(temp);
	}

	/**
	 * Fills all text fields to show details about the CV natural class. If the
	 * specified segment is null, all text fields are cleared.
	 *
	 * @param hyphenChangeRule
	 *            the segment or null
	 */
	private void showHyphenChangeRuleDetails(HyphenChangeRule hyphenChangeRule) {
		currentHyphenChangeRule = hyphenChangeRule;
		if (hyphenChangeRule != null) {
			// Fill the text fields with info from the person object.
			nameField.setText(hyphenChangeRule.getRuleName());
			descriptionField.setText(hyphenChangeRule.getDescription());
			NodeOrientation analysisOrientation = languageProject.getAnalysisLanguage()
					.getOrientation();
			nameField.setNodeOrientation(analysisOrientation);
			descriptionField.setNodeOrientation(analysisOrientation);
			matchesTextFlow.setNodeOrientation(analysisOrientation);
			changesTextFlow.setNodeOrientation(analysisOrientation);
			activeCheckBox.setSelected(hyphenChangeRule.isActive());
			showMatchesContent();
			showChangesContent();
			setUpDownButtonDisabled();
		} else {
			// Segment is null, remove all the text.
			if (nameField != null) {
				nameField.setText("");
			}
			if (descriptionField != null) {
				descriptionField.setText("");
			}
			if (changesTextFlow != null) {
				changesTextFlow.getChildren().clear();
			}
			buttonMoveDown.setDisable(true);
			buttonMoveUp.setDisable(true);
		}
		displayFieldsPerActiveSetting(hyphenChangeRule);

		if (hyphenChangeRule != null) {
			int currentItem = hyphenChangeRulesTable.getItems().indexOf(currentHyphenChangeRule);
			this.mainApp.updateStatusBarNumberOfItems((currentItem + 1) + "/"
					+ hyphenChangeRulesTable.getItems().size() + " ");
			mainApp.getApplicationPreferences().setLastHyphenChangeRulesViewItemUsed(currentItem);
		}
		if (hyphenApproach != null && currentHyphenChangeRule != null)
			showAnyChangeRuleErrrors();
	}

	@Override
	public void setViewItemUsed(int value) {
		int max = hyphenChangeRulesTable.getItems().size();
		value = adjustIndexValue(value, max);
		hyphenChangeRulesTable.getSelectionModel().clearAndSelect(value);
	}

	protected void setUpDownButtonDisabled() {
		int iThis = hyphenApproach.getHyphenChangeRules().indexOf(currentHyphenChangeRule) + 1;
		int iSize = hyphenApproach.getHyphenChangeRules().size();
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

	private String buildContent(TextFlow textFlow, ObservableList<HyphenClass> hyphenClasses) {
		// TODO: can we do this with lambdas?
		StringBuilder sb = new StringBuilder();
		textFlow.getChildren().clear();
		if (currentHyphenChangeRule.isWordInitial()) {
			addNameToContent(sb, matchesTextFlow, Constants.WORD_BOUNDARY_SYMBOL, true);
			sb.append(", ");
		}
		if (languageProject.getAnalysisLanguage().getOrientation() ==  NodeOrientation.LEFT_TO_RIGHT) {
			createStringOfHyphenClasses(sb, textFlow, hyphenClasses);
		} else {
			FXCollections.reverse(hyphenClasses);
			createStringOfHyphenClasses(sb, textFlow, hyphenClasses);
			FXCollections.reverse(hyphenClasses);
		}
		if (currentHyphenChangeRule.isWordFinal()) {
			sb.append(", ");
			addNameToContent(sb, textFlow, Constants.WORD_BOUNDARY_SYMBOL, true);
		}
		return sb.toString();
	}

	private void showMatchesContent() {
		String rep = buildContent(matchesTextFlow, currentHyphenChangeRule.getMatchHyphenClasses());
		currentHyphenChangeRule.setMatchRepresentation(rep);
	}

	private void showChangesContent() {
		String rep = buildContent(changesTextFlow, currentHyphenChangeRule.getChangeHyphenClasses());
		currentHyphenChangeRule.setChangeRepresentation(rep);
	}

	protected void createStringOfHyphenClasses(StringBuilder sb,
			TextFlow textFlow, ObservableList<HyphenClass> hyphenClasses) {
		int i = 1;
		int iCount = hyphenClasses.size();
		for (SylParserObject spo : hyphenClasses) {
			HyphenClass nh = (HyphenClass) spo;
			if (nh != null) {
				if (nh.getSegmentsRepresentation().equals(Constants.SPECIAL_INSERT_CODE)) {
					addNameToContent(sb, textFlow, "-", true);
				} else {
					addNameToContent(sb, textFlow, nh.getClassName(), spo.isActive());
				}
				if (i++ < iCount) {
					sb.append(", ");
				}
			}
		}
	}

	protected void addNameToContent(StringBuilder sb, TextFlow textFlow, String sName, boolean isActive) {
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
		textFlow.getChildren().addAll(t, tBar);
		sb.append(sName);
	}

	public void setSyllablePattern(HyphenChangeRule syllablePattern) {
		nameField.setText(syllablePattern.getRuleName());
		descriptionField.setText(syllablePattern.getDescription());
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 *
	 * @param cvApproachController
	 */
	public void setData(HyphenApproach hyphenApproachData) {
		hyphenApproach = hyphenApproachData;
		languageProject = hyphenApproach.getLanguageProject();
		// no sorting allowed

		// Add observable list data to the table
		hyphenChangeRulesTable.setItems(hyphenApproachData.getHyphenChangeRules());
		int max = hyphenChangeRulesTable.getItems().size();
		if (max > 0) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					int iLastIndex = mainApp.getApplicationPreferences()
							.getLastHyphenChangeRulesViewItemUsed();
					iLastIndex = adjustIndexValue(iLastIndex, max);
					hyphenChangeRulesTable.requestFocus();
					hyphenChangeRulesTable.getSelectionModel().select(iLastIndex);
					hyphenChangeRulesTable.getFocusModel().focus(iLastIndex);
					// want to do following only if the selected item is not
					// visible
					// cvSyllablePatternTable.isVisible();
					hyphenChangeRulesTable.scrollTo(iLastIndex);
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
		HyphenChangeRule newHyphenChangeRulen = new HyphenChangeRule();
		hyphenApproach.getHyphenChangeRules().add(newHyphenChangeRulen);
		handleInsertNewItem(hyphenApproach.getHyphenChangeRules(), hyphenChangeRulesTable);
	}

	@Override
	void handleRemoveItem() {
		handleRemoveItem(hyphenApproach.getHyphenChangeRules(), currentHyphenChangeRule, hyphenChangeRulesTable);
	}

	@Override
	void handlePreviousItem() {
		handlePreviousItem(hyphenApproach.getHyphenChangeRules(), currentHyphenChangeRule, hyphenChangeRulesTable);
	}

	@Override
	void handleNextItem() {
		handleNextItem(hyphenApproach.getHyphenChangeRules(), currentHyphenChangeRule, hyphenChangeRulesTable);
	}

	@FXML
	void handleLaunchMatchSequenceChooser() {
		ObservableList<HyphenClass> hyphenClasses = currentHyphenChangeRule.getMatchHyphenClasses();
		showHCSequenceChooser(hyphenClasses, false);
		showMatchesContent();
		currentHyphenChangeRule.setMatchClasses(hyphenClasses);
	}

	@FXML
	void handleLaunchChangeSequenceChooser() {
		ObservableList<HyphenClass> hyphenClasses = currentHyphenChangeRule.getChangeHyphenClasses();
		showHCSequenceChooser(hyphenClasses, true);
		showChangesContent();
		currentHyphenChangeRule.setChangeClasses(hyphenClasses);
	}

	/**
	 * Opens a dialog to show and set sequence of hyphen classes
	 * @param fIsChange TODO
	 * @param isMatch TODO
	 */
	public void showHCSequenceChooser(ObservableList<HyphenClass> hyphenClasses, boolean fIsChange) {
		try {
			// Load the fxml file and create a new stage for the popup.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ApproachViewNavigator.class
					.getResource("fxml/HyphenChangeRuleHyphenClassChooser.fxml"));
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

			HyphenChangeRuleHyphenClassChooserController controller = loader.getController();
			controller.setChange(fIsChange);
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setData(hyphenApproach);
			controller.setChangeRule(currentHyphenChangeRule);
			controller.setPattern(hyphenClasses);

			dialogStage.showAndWait();
			showAnyChangeRuleErrrors();
		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
	}

	@FXML
	void handleMoveDown() {
		int i = hyphenApproach.getHyphenChangeRules().indexOf(currentHyphenChangeRule);
		if ((i + 1) < hyphenApproach.getHyphenChangeRules().size()) {
			Collections.swap(hyphenApproach.getHyphenChangeRules(), i, i + 1);
		}
	}

	@FXML
	void handleMoveUp() {
		int i = hyphenApproach.getHyphenChangeRules().indexOf(currentHyphenChangeRule);
		if (i > 0) {
			Collections.swap(hyphenApproach.getHyphenChangeRules(), i, i - 1);
		}
	}

	protected void handleCheckBoxSelectAll() {
		for (HyphenChangeRule syllablePattern : hyphenApproach.getHyphenChangeRules()) {
			syllablePattern.setActive(true);
			forceTableRowToRedisplayPerActiveSetting(syllablePattern);
		}
	}

	protected void handleCheckBoxClearAll() {
		for (HyphenChangeRule syllablePattern : hyphenApproach.getHyphenChangeRules()) {
			syllablePattern.setActive(false);
			forceTableRowToRedisplayPerActiveSetting(syllablePattern);
		}
	}

	protected void handleCheckBoxToggle() {
		for (HyphenChangeRule syllablePattern : hyphenApproach.getHyphenChangeRules()) {
			if (syllablePattern.isActive()) {
				syllablePattern.setActive(false);
			} else {
				syllablePattern.setActive(true);
			}
			forceTableRowToRedisplayPerActiveSetting(syllablePattern);
		}
	}

	protected void showAnyChangeRuleErrrors() {
		hideErrors();
		HyphenChangeRuleValidator validator = HyphenChangeRuleValidator.getInstance();
		validator.setChangeRule(currentHyphenChangeRule);
		validator.setBundle(bundle);
		StringBuilder sb = new StringBuilder();
		validator.validate();
		sb.append(validator.getErrorMessage());
		if (sb.toString().length() > 0) {
			errorTextArea.setVisible(true);
			errorTextArea.setText(sb.toString());
		}
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return new TextField[] { nameField, descriptionField };
	}
}
