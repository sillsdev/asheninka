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
import org.sil.syllableparser.model.Language;
import org.sil.syllableparser.model.npapproach.NPApproach;
import org.sil.syllableparser.model.npapproach.NPRule;
import org.sil.syllableparser.model.npapproach.NPRuleAction;
import org.sil.syllableparser.model.npapproach.NPRuleLevel;
import org.sil.utility.view.ControllerUtilities;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * @author Andy Black
 *
 */

public class NPRulesController extends SplitPaneWithTableViewController {

	protected final class AnalysisWrappingTableCell extends TableCell<NPRule, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processAnalysisTableCell(this, text, item, empty);
		}
	}

	protected final class WrappingTableCell extends TableCell<NPRule, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			NPRule rule = (NPRule) this.getTableRow().getItem();
			if (rule != null) {
				if (rule.getAffectedSegOrNC().isSegment()) {
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
	private TableView<NPRule> npRulesTable;
	@FXML
	private TableColumn<NPRule, String> nameColumn;
	@FXML
	private TableColumn<NPRule, String> descriptionColumn;
	@FXML
	private TableColumn<NPRule, String> affectedSegmentOrNaturalClassColumn;
	@FXML
	private TableColumn<NPRule, String> contextColumn;
	@FXML
	private TableColumn<NPRule, String> actionColumn;
	@FXML
	private TableColumn<NPRule, String> levelColumn;
	@FXML
	private TableColumn<NPRule, Boolean> obeysSSPColumn;

	@FXML
	private TextField nameField;
	@FXML
	private TextField naturalClassesField;
	@FXML
	private TextField descriptionField;
	@FXML
	private TextField affectedTextField;
	@FXML
	private Button affectedButton;
	@FXML
	private TextField contextTextField;
	@FXML
	private Button contextButton;
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
	private TextArea ruleTextArea;
	@FXML
	protected ComboBox<NPRuleAction> actionComboBox;
	@FXML
	protected ComboBox<NPRuleLevel> levelComboBox;
	@FXML
	private CheckBox obeysSSPCheckBox;

	private NPRule currentRule;

	public NPRulesController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.setApproach(ApplicationPreferences.NP_RULES);
		super.setTableView(npRulesTable);
		super.initialize(location, resources);

		bundle = resources;
		// Initialize the button icons
		tooltipMoveUp = ControllerUtilities.createToolbarButtonWithImage("UpArrow.png",
				buttonMoveUp, tooltipMoveUp, bundle.getString("sh.view.sonorityhierarchy.up"),
				Constants.RESOURCE_SOURCE_LOCATION);
		tooltipMoveDown = ControllerUtilities.createToolbarButtonWithImage("DownArrow.png",
				buttonMoveDown, tooltipMoveDown, bundle.getString("sh.view.sonorityhierarchy.down"),
				Constants.RESOURCE_SOURCE_LOCATION);

		nameColumn.setCellValueFactory(cellData -> cellData.getValue().ruleNameProperty());
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		affectedSegmentOrNaturalClassColumn.setCellValueFactory(cellData -> cellData.getValue()
				.affectedSegmentOrNaturalClassProperty());
		contextColumn.setCellValueFactory(cellData -> cellData.getValue()
				.contextSegmentOrNaturalClassProperty());
		actionColumn.setCellValueFactory(cellData -> cellData.getValue()
				.ActionProperty());
		levelColumn.setCellValueFactory(cellData -> cellData.getValue()
				.LevelProperty());

		// Custom rendering of the table cell.
		nameColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});
		affectedSegmentOrNaturalClassColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		contextColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		actionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});
		levelColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});

		makeColumnHeaderWrappable(nameColumn);
		makeColumnHeaderWrappable(descriptionColumn);
		makeColumnHeaderWrappable(affectedSegmentOrNaturalClassColumn);
		makeColumnHeaderWrappable(contextColumn);
		makeColumnHeaderWrappable(actionColumn);
		makeColumnHeaderWrappable(levelColumn);

		// Since sonority items are sorted manually, we do not
		// want the user to be able to click on a column header and sort it
		nameColumn.setSortable(false);
		descriptionColumn.setSortable(false);
		affectedSegmentOrNaturalClassColumn.setSortable(false);
		contextColumn.setSortable(false);
		actionColumn.setSortable(false);
		levelColumn.setSortable(false);
		obeysSSPColumn.setSortable(false);

		// Clear sonority hierarchy details.
		showNPRuleDetails(null);

		// Listen for selection changes and show the details when changed.
		npRulesTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showNPRuleDetails(newValue));
		
		actionComboBox.setConverter(new StringConverter<NPRuleAction>() {
			@Override
			public String toString(NPRuleAction object) {
				String localizedName = bundle.getString("nprule.action." + object.toString().toLowerCase());
				if (currentRule != null)
					currentRule.setAction(localizedName);
				return localizedName;
			}

			@Override
			public NPRuleAction fromString(String string) {
				// nothing to do
				return null;
			}
		});
		actionComboBox.getSelectionModel().selectedItemProperty()
		.addListener(new ChangeListener<NPRuleAction>() {
			@Override
			public void changed(ObservableValue<? extends NPRuleAction> selected,
					NPRuleAction oldValue, NPRuleAction selectedValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						currentRule.setRuleAction(selectedValue);
					}
				});
			}
		});
		actionComboBox.setPromptText(resources.getString("label.chooseruleaction"));

		levelComboBox.setConverter(new StringConverter<NPRuleLevel>() {
			@Override
			public String toString(NPRuleLevel object) {
				String localizedName = bundle.getString("nprule.level." + object.toString().toLowerCase());
				if (currentRule != null)
					currentRule.setLevel(localizedName);
				return localizedName;
			}

			@Override
			public NPRuleLevel fromString(String string) {
				// nothing to do
				return null;
			}
		});
		levelComboBox.getSelectionModel().selectedItemProperty()
		.addListener(new ChangeListener<NPRuleLevel>() {
			@Override
			public void changed(ObservableValue<? extends NPRuleLevel> selected,
					NPRuleLevel oldValue, NPRuleLevel selectedValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						currentRule.setRuleLevel(selectedValue);
					}
				});
			}
		});
		levelComboBox.setPromptText(resources.getString("label.chooserulelevel"));

		// Handle TextField text changes.
		nameField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentRule != null) {
				currentRule.setRuleName(nameField.getText());
			}
			if (languageProject != null) {
				nameField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});
		descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentRule != null) {
				currentRule.setDescription(descriptionField.getText());
			}
			if (languageProject != null) {
				descriptionField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});
		affectedTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (languageProject != null) {
				Language lang = languageProject.getAnalysisLanguage();
				if (currentRule.getAffectedSegOrNC() != null && currentRule.getAffectedSegOrNC().isSegment()) {
					lang = languageProject.getVernacularLanguage();
				}
				affectedTextField.setFont(lang.getFont());
				affectedTextField.setNodeOrientation(lang.getOrientation());
				String sVernacular = mainApp.getStyleFromColor(lang.getColor());
				affectedTextField.setStyle(sVernacular);
			}
			if (currentRule != null) {
				affectedTextField.setText(newValue);
			}
		});
		contextTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (languageProject != null) {
				Language lang = languageProject.getAnalysisLanguage();
				if (currentRule.getContextSegOrNC() != null && currentRule.getContextSegOrNC().isSegment()) {
					lang = languageProject.getVernacularLanguage();
				}
				contextTextField.setFont(lang.getFont());
				contextTextField.setNodeOrientation(lang.getOrientation());
				String sVernacular = mainApp.getStyleFromColor(lang.getColor());
				contextTextField.setStyle(sVernacular);
			}
			if (currentRule != null) {
				contextTextField.setText(currentRule.getContextSegmentOrNaturalClass());
			}
		});

		obeysSSPColumn.setCellValueFactory(cellData -> cellData.getValue().obeysSSPProperty());
		obeysSSPColumn.setCellFactory(CheckBoxTableCell.forTableColumn(obeysSSPColumn));
		obeysSSPColumn.setEditable(true);

		npRulesTable.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Object o = event.getTarget();
				handleClickOnCheckBoxInTable(o);
			}
		});

		activeCheckBox.setOnAction((event) -> {
			if (currentRule != null) {
				currentRule.setActive(activeCheckBox.isSelected());
				showRuleContent();
				forceTableRowToRedisplayPerActiveSetting(currentRule);
			}
			displayFieldsPerActiveSetting(currentRule);
		});

		obeysSSPCheckBox.setOnAction((event) -> {
			if (currentRule != null) {
				currentRule.setObeysSSP(obeysSSPCheckBox.isSelected());
				showRuleContent();
				forceTableRowToRedisplayPerActiveSetting(currentRule);
			}
			displayFieldsPerActiveSetting(currentRule);
		});

		ruleTextArea.setEditable(false);

		// Use of Enter move focus to next item.
		nameField.setOnAction((event) -> {
			descriptionField.requestFocus();
		});
		descriptionField.setOnAction((event) -> {
			naturalClassesField.requestFocus();
		});

		nameField.requestFocus();

		actionComboBox.setConverter(new StringConverter<NPRuleAction>() {
			@Override
			public String toString(NPRuleAction object) {
				String localizedName = bundle.getString("nprule.action." + object.toString().toLowerCase());
				if (currentRule != null)
					currentRule.setAction(localizedName);
				return localizedName;
			}

			@Override
			public NPRuleAction fromString(String string) {
				// nothing to do
				return null;
			}
		});
		actionComboBox.getSelectionModel().selectedItemProperty()
		.addListener(new ChangeListener<NPRuleAction>() {
			@Override
			public void changed(ObservableValue<? extends NPRuleAction> selected,
					NPRuleAction oldValue, NPRuleAction selectedValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						currentRule.setRuleAction(selectedValue);
					}
				});
			}
		});
		actionComboBox.setPromptText(resources.getString("label.chooseruleaction"));

		levelComboBox.setConverter(new StringConverter<NPRuleLevel>() {
			@Override
			public String toString(NPRuleLevel object) {
				String localizedName = bundle.getString("nprule.level." + object.toString().toLowerCase());
				if (currentRule != null)
					currentRule.setLevel(localizedName);
				return localizedName;
			}

			@Override
			public NPRuleLevel fromString(String string) {
				// nothing to do
				return null;
			}
		});
		levelComboBox.getSelectionModel().selectedItemProperty()
		.addListener(new ChangeListener<NPRuleLevel>() {
			@Override
			public void changed(ObservableValue<? extends NPRuleLevel> selected,
					NPRuleLevel oldValue, NPRuleLevel selectedValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						currentRule.setRuleLevel(selectedValue);
					}
				});
			}
		});
		levelComboBox.setPromptText(resources.getString("label.chooserulelevel"));
	}

	protected void handleClickOnCheckBoxInTable(Object o) {
		if (o instanceof CheckBoxTableCell) {
			@SuppressWarnings("unchecked")
			CheckBoxTableCell<NPRule, Boolean> cbtc = (CheckBoxTableCell<NPRule, Boolean>) o;
			int index = cbtc.getIndex();
			if (index < 0) {
				return;
			}
			NPRule segment = npRulesTable.getItems().get(index);
			boolean value;
			if (segment != null) {
				value = !segment.isObeysSSP();
				segment.setObeysSSP(value);
				obeysSSPCheckBox.setSelected(value);
			}
		}
	}

	public void displayFieldsPerActiveSetting(NPRule rule) {
		boolean fIsActive;
		if (rule == null) {
			fIsActive = false;
		} else {
			fIsActive = rule.isActive();
		}
		nameField.setDisable(!fIsActive);
		descriptionField.setDisable(!fIsActive);
		affectedTextField.setDisable(!fIsActive);
		affectedButton.setDisable(!fIsActive);
		contextTextField.setDisable(!fIsActive);
		contextButton.setDisable(!fIsActive);
		actionComboBox.setDisable(!fIsActive);
		levelComboBox.setDisable(!fIsActive);
		obeysSSPCheckBox.setDisable(!fIsActive);
	}

	private void forceTableRowToRedisplayPerActiveSetting(NPRule rule) {
		// we need to make the content of the row cells change in order for
		// the cell factory to fire.
		// We do this by getting the value, blanking it, and then restoring it.
		String temp = rule.getRuleName();
		rule.setRuleName("");
		rule.setRuleName(temp);
		temp = rule.getDescription();
		rule.setDescription("");
		rule.setDescription(temp);
		temp = rule.getAffectedSegmentOrNaturalClass();
		rule.setAffectedSegmentOrNaturalClass("");
		rule.setAffectedSegmentOrNaturalClass(temp);
		temp = rule.getContextSegmentOrNaturalClass();
		rule.setContextSegmentOrNaturalClass("");
		rule.setContextSegmentOrNaturalClass(temp);
		temp = rule.getAction();
		rule.setAction("");
		rule.setAction(temp);
		temp = rule.getLevel();
		rule.setLevel("");
		rule.setLevel(temp);
	}

	/**
	 * Fills all text fields to show details about the CV natural class. If the
	 * specified segment is null, all text fields are cleared.
	 *
	 * @param rule
	 *            the segment or null
	 */
	private void showNPRuleDetails(NPRule rule) {
		currentRule = rule;
		if (rule != null) {
			// Fill the text fields with info from the NPRule object.
			nameField.setText(rule.getRuleName());
			descriptionField.setText(rule.getDescription());
			NodeOrientation analysisOrientation = languageProject.getAnalysisLanguage()
					.getOrientation();
			nameField.setNodeOrientation(analysisOrientation);
			descriptionField.setNodeOrientation(analysisOrientation);
			affectedTextField.setText(rule.getAffectedSegmentOrNaturalClass());
			contextTextField.setNodeOrientation(languageProject.getVernacularLanguage()
					.getOrientation());
			contextTextField.setText(rule.getContextSegmentOrNaturalClass());
			actionComboBox.getItems().setAll(NPRuleAction.values());
			actionComboBox.getSelectionModel().select(rule.getRuleAction());
			levelComboBox.getItems().setAll(NPRuleLevel.values());
			levelComboBox.getSelectionModel().select(rule.getRuleLevel());
			obeysSSPCheckBox.setSelected(rule.isObeysSSP());
			activeCheckBox.setSelected(rule.isActive());
			showRuleContent();
			setUpDownButtonDisabled();

		} else {
			// Segment is null, remove all the text.
			if (nameField != null) {
				nameField.setText("");
			}
			if (descriptionField != null) {
				descriptionField.setText("");
			}
			if (affectedTextField != null) {
				affectedTextField.setText("");
			}
			if (contextTextField != null) {
				contextTextField.setText("");
			}
			buttonMoveDown.setDisable(true);
			buttonMoveUp.setDisable(true);
		}
		displayFieldsPerActiveSetting(rule);

		if (rule != null) {
			int currentItem = npRulesTable.getItems().indexOf(currentRule);
			this.mainApp.updateStatusBarNumberOfItems((currentItem + 1) + "/"
					+ npRulesTable.getItems().size() + " ");
			mainApp.getApplicationPreferences().setLastNPRulesViewItemUsed(currentItem);
		}
	}

	@Override
	public void setViewItemUsed(int value) {
		int max = npRulesTable.getItems().size();
		value = adjustIndexValue(value, max);
		npRulesTable.getSelectionModel().clearAndSelect(value);
	}

	protected void setUpDownButtonDisabled() {
		int iThis = npApproach.getNPRules().indexOf(currentRule) + 1;
		int iSize = npApproach.getNPRules().size();
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

	private void showRuleContent() {
		StringBuilder sb = new StringBuilder();
//		affectedTextFlow.getChildren().clear();
//		ObservableList<Segment> segments = currentRule.getSegments();
//		if (languageProject.getVernacularLanguage().getOrientation() == NodeOrientation.LEFT_TO_RIGHT) {
//			fillNcsTextFlow(sb, segments);
//		} else {
//			FXCollections.reverse(segments);
//			fillNcsTextFlow(sb, segments);
//			FXCollections.reverse(segments);
//		}
		currentRule.setRuleRepresentation(sb.toString());
	}

	public void setData(NPApproach npApproachData) {
		npApproach = npApproachData;
		languageProject = npApproach.getLanguageProject();
		// no sorting needed

		// Add observable list data to the table
		npRulesTable.setItems(npApproachData.getNPRules());
		int max = npRulesTable.getItems().size();
		if (max > 0) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					int iLastIndex = mainApp.getApplicationPreferences()
							.getLastNPRulesViewItemUsed();
					iLastIndex = adjustIndexValue(iLastIndex, max);
					npRulesTable.requestFocus();
					npRulesTable.getSelectionModel().select(iLastIndex);
					npRulesTable.getFocusModel().focus(iLastIndex);
					npRulesTable.scrollTo(iLastIndex);
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
		NPRule newRule = new NPRule();
		npApproach.getNPRules().add(newRule);
		handleInsertNewItem(npApproach.getNPRules(), npRulesTable);
	}

	@Override
	void handleRemoveItem() {
		handleRemoveItem(npApproach.getNPRules(), currentRule, npRulesTable);
	}

	@Override
	void handlePreviousItem() {
		handlePreviousItem(npApproach.getNPRules(), currentRule, npRulesTable);
	}

	@Override
	void handleNextItem() {
		handleNextItem(npApproach.getNPRules(), currentRule, npRulesTable);
	}

	@FXML
	void handleLaunchAffectedSegOrNCChooser() {
		showSegmentChooser(true);
		showRuleContent();
	}

	@FXML
	void handleLaunchContextSegOrNCChooser() {
		showSegmentChooser(false);
		showRuleContent();
	}

	/**
	 * Opens a dialog to show and set segments
	 */
	public void showSegmentChooser(boolean isAffected) {
		try {
			// Load the fxml file and create a new stage for the popup.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ApproachViewNavigator.class
					.getResource("fxml/NPSegmentNaturalClassChooser.fxml"));
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

			NPSegmentNaturalClassChooserController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setRule(currentRule);
			controller.setData(npApproach.getLanguageProject(), isAffected);
			controller.initializeTableColumnWidths(mainApp.getApplicationPreferences());

			dialogStage.showAndWait();
			
			if (controller.isOkClicked()) {
				if (isAffected) {
					affectedTextField.setText(controller.getRule().getAffectedSegmentOrNaturalClass());
				} else {
					contextTextField.setText(controller.getRule().getContextSegmentOrNaturalClass());
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
	}

	@FXML
	void handleMoveDown() {
		int i = npApproach.getNPRules().indexOf(currentRule);
		if ((i + 1) < npApproach.getNPRules().size()) {
			Collections.swap(npApproach.getNPRules(), i, i + 1);
		}
	}

	@FXML
	void handleMoveUp() {
		int i = npApproach.getNPRules().indexOf(currentRule);
		if (i > 0) {
			Collections.swap(npApproach.getNPRules(), i, i - 1);
		}
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return new TextField[] { nameField, descriptionField };
	}
}
