// Copyright (c) 2016-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.ApproachType;
import org.sil.syllableparser.model.Environment;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.GraphemeNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.moraicapproach.MoraicApproach;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.service.AsheninkaGraphemeAndClassListener;
import org.sil.antlr4.environmentparser.EnvironmentConstants;
import org.sil.antlr4.environmentparser.EnvironmentErrorInfo;
import org.sil.antlr4.environmentparser.EnvironmentErrorListener;
import org.sil.antlr4.environmentparser.GraphemeErrorInfo;
import org.sil.antlr4.environmentparser.EnvironmentErrorListener.VerboseListener;
import org.sil.antlr4.environmentparser.antlr4generated.EnvironmentLexer;
import org.sil.antlr4.environmentparser.antlr4generated.EnvironmentParser;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

/**
 * @author Andy Black
 *
 */

public class EnvironmentsController extends SplitPaneWithTableViewController {

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
	private TableColumn<Environment, String> nameColumn;
	@FXML
	private TableColumn<Environment, String> representationColumn;
	@FXML
	private TableColumn<Environment, String> descriptionColumn;
	@FXML
	private TableColumn<Environment, Boolean> checkBoxColumn;
	@FXML
	private CheckBox checkBoxColumnHead;

	@FXML
	private TextField nameField;
	@FXML
	private TextField representationField;
	@FXML
	private TextField descriptionField;
	@FXML
	private CheckBox activeCheckBox;
	@FXML
	private Label environmentErrorMessage;
	@FXML
	private ComboBox<String> gncChoicesComboBox;

	private Environment currentEnvironment;
	private int iRepresentationCaretPosition = 6;
	// fGncChoicesUsingMouse distinguishes between using the keyboard or the
	// mouse
	// while using the combo box.
	private boolean fGncChoicesUsingMouse;
	// fJustSwitchedFocusFromGncComboBoxToRepField when true will put cursor at
	// the
	// end of the class name in the representationField
	private boolean fJustSwitchedFocusFromGncComboBoxToRepField = false;

	public EnvironmentsController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.setApproach(ApplicationPreferences.ENVIRONMENTS);
		super.setTableView(environmentTable);
		super.initialize(location, resources);


		this.bundle = resources;
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().environmentNameProperty());
		representationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.environmentRepresentationProperty());
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

		// Custom rendering of the table cell.
		nameColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});
		representationColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});

		makeColumnHeaderWrappable(nameColumn);
		makeColumnHeaderWrappable(representationColumn);
		makeColumnHeaderWrappable(descriptionColumn);

		// Clear cv natural class details.
		showEnvironmentDetails(null);

		// Listen for selection changes and show the details when changed.
		environmentTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showEnvironmentDetails(newValue));

		// Handle TextField text changes.
		nameField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentEnvironment != null) {
				currentEnvironment.setEnvironmentName(nameField.getText());
			}
			if (languageProject != null) {
				nameField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});

		descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentEnvironment != null) {
				currentEnvironment.setDescription(descriptionField.getText());
			}
			if (languageProject != null) {
				descriptionField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});

		environmentErrorMessage.setTextFill(Constants.ENVIRONMENT_ERROR_MESSAGE);
		environmentErrorMessage.setWrapText(true);

		representationField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentEnvironment != null) {
				String sRep = representationField.getText();
				currentEnvironment.setEnvironmentRepresentation(sRep);
				boolean fParseSucceeded = parseEnvironmentRepresentation(sRep);
				currentEnvironment.setValid(fParseSucceeded);
				environmentErrorMessage.setVisible(!fParseSucceeded);
			}
			if (languageProject != null) {
				representationField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});
		// Want to have the cursor appear in the given location when coming back
		// from the combo box
		representationField.focusedProperty()
				.addListener(
						(ObservableValue<? extends Boolean> observable, Boolean oldValue,
								Boolean newValue) -> {
							if (newValue) {
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										if (fJustSwitchedFocusFromGncComboBoxToRepField) {
											representationField
													.positionCaret(iRepresentationCaretPosition);
											fJustSwitchedFocusFromGncComboBoxToRepField = false;
										}
										gncChoicesComboBox.setVisible(false);
									}
								});
							}
						});
		// Want to show the combo box chooser when the user presses the [ key
		// and stop showing it when the user presses the ] key.
		representationField.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case CLOSE_BRACKET:
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							gncChoicesComboBox.setVisible(false);
							representationField.requestFocus();
						}
					});
					break;
				case OPEN_BRACKET:
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							iRepresentationCaretPosition = representationField.getCaretPosition();
							gncChoicesComboBox.setVisible(true);
							// this does show the list, but the list remains
							// showing and we don't want that:
							// gncChoicesComboBox.show();
							gncChoicesComboBox.getSelectionModel().clearSelection();
							// No: forcing the combo box to have the focus is
							// confusing when the user wants to just type:
							// gncChoicesComboBox.requestFocus();
						}
					});
					break;
				default:
					break;
				}
			}
		});

		activeCheckBox.setOnAction((event) -> {
			if (currentEnvironment != null) {
				currentEnvironment.setActive(activeCheckBox.isSelected());
				forceTableRowToRedisplayPerActiveSetting(currentEnvironment);
			}
			displayFieldsPerActiveSetting(currentEnvironment);
		});

		// When using the keyboard for the combo box, show the options with
		// first down arrow and select the item when the Enter key is pressed.
		// NB: for some reason, using setOnKeyPressed does not see the ENTER
		// key. Presumably it is being used by a child node to do the select
		// action.
		gncChoicesComboBox.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case ENTER:
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							String selectedValue = gncChoicesComboBox.getSelectionModel()
									.getSelectedItem();
//							System.out.println("ENTER: selectedValue=" + selectedValue);
							if (selectedValue != null) {
								fGncChoicesUsingMouse = true;
								updateRepresentationFieldPerClassChoice(selectedValue);
								fJustSwitchedFocusFromGncComboBoxToRepField = true;
								representationField.requestFocus();
								fGncChoicesUsingMouse = false;
							}
						}
					});
					break;
				case DOWN:
//					System.out.println("down arrow: using mouse = " + fGncChoicesUsingMouse);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							if (!gncChoicesComboBox.isShowing()) {
								gncChoicesComboBox.show();
							}
							fGncChoicesUsingMouse = false;
						}
					});
					break;
				default:
					break;
				}
			}
		});
		// User has used the mouse to click on the combo box
		gncChoicesComboBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
//						System.out.println("Mouse clicked");
						fGncChoicesUsingMouse = true;
					}
				});
			}
		});
		gncChoicesComboBox.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> selected,
							String oldValue, String selectedValue) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
//								System.out
//										.println("SelectedItemProperty: fGncChoicesUsingMouse= "
//												+ fGncChoicesUsingMouse + " selectedValue="
//												+ selectedValue);
//
								if (fGncChoicesUsingMouse && selectedValue != null) {
									updateRepresentationFieldPerClassChoice(selectedValue);
									fGncChoicesUsingMouse = false;
									fJustSwitchedFocusFromGncComboBoxToRepField = true;
									representationField.requestFocus();
								}
							}
						});
					}
				});
		String sChooseClass = resources.getString("label.chooseclass");
		gncChoicesComboBox.setPromptText(sChooseClass);

		// Use of Enter move focus to next item.
		nameField.setOnAction((event) -> {
			descriptionField.requestFocus();
		});
		descriptionField.setOnAction((event) -> {
			representationField.requestFocus();
		});

		nameField.requestFocus();

	}

	private void updateRepresentationFieldPerClassChoice(String selectedValue) {
		if (selectedValue != null) {
			String sLeftOfCaret = representationField.getText().substring(0,
					iRepresentationCaretPosition);
			String sRightOfCaret = representationField.getText().substring(
					iRepresentationCaretPosition);
			representationField.setText(sLeftOfCaret + selectedValue + sRightOfCaret);
			iRepresentationCaretPosition += selectedValue.length();
		}
	}

	private boolean parseEnvironmentRepresentation(String sRep) {
		CharStream input = CharStreams.fromString(sRep);
		EnvironmentLexer lexer = new EnvironmentLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		EnvironmentParser parser = new EnvironmentParser(tokens);
		parser.removeErrorListeners();
		VerboseListener errListener = new EnvironmentErrorListener.VerboseListener();
		errListener.clearErrorMessageList();
		parser.addErrorListener(errListener);
		ParseTree tree = parser.environment();
		int iNumErrors = parser.getNumberOfSyntaxErrors();
		if (iNumErrors > 0) {
			reportEnvironmentSyntaxError(errListener, iNumErrors);
			return false;
		}

		return buildEnvironmentContexts(parser, tree);

	}

	private boolean buildEnvironmentContexts(EnvironmentParser parser, ParseTree tree) {
		ParseTreeWalker walker = new ParseTreeWalker();
		List<String> graphemes = this.languageProject.getActiveGraphemes().stream()
				.map(Grapheme::getForm).collect(Collectors.toList());
		List<String> graphemeNaturalClasses = this.languageProject
				.getActiveGraphemeNaturalClasses().stream().map(GraphemeNaturalClass::getNCName)
				.collect(Collectors.toList());
		AsheninkaGraphemeAndClassListener validator = new AsheninkaGraphemeAndClassListener(parser,
				graphemes, graphemeNaturalClasses);
		validator.setClasses(languageProject.getActiveGraphemeNaturalClasses());
		validator.setEnvironment(currentEnvironment);
		validator.setCheckForReduplication(false);
		walker.walk(validator, tree); // initiate walk of tree with listener
		parser.removeParseListeners();
		parser.addParseListener(validator);
		AsheninkaGraphemeAndClassListener listener = (AsheninkaGraphemeAndClassListener) parser
				.getParseListeners().get(0);
		String sMessage = "";
		sMessage = buildAnyMissingItems(listener, sMessage);
		if (sMessage.length() > 0) {
			environmentErrorMessage.setText(sMessage);
			return false;
		}
		Environment env = validator.getEnvironment();
		currentEnvironment.setLeftContext(env.getLeftContext());
		currentEnvironment.setRightContext(env.getRightContext());
		return true;
	}

	private String buildAnyMissingItems(AsheninkaGraphemeAndClassListener listener, String sMessage) {
		List<GraphemeErrorInfo> badGraphemes = listener.getBadGraphemes();
		int iBadGraphemes = badGraphemes.size();
		if (iBadGraphemes > 0) {
			String sMsg = badGraphemes.stream().map(GraphemeErrorInfo::getGrapheme)
					.collect(Collectors.joining(", "));
			Optional<GraphemeErrorInfo> info = badGraphemes.stream().collect(
					Collectors.maxBy(Comparator.comparing(GraphemeErrorInfo::getMaxDepth)));
			int iMax = (info.isPresent() ? info.get().getMaxDepth() : -1);
			String sSyntaxErrorMessage = bundle.getString("environmentsyntaxerror.unknowngrapheme");
			sMessage = sSyntaxErrorMessage.replace("{0}", sMsg.substring(iMax));
		}
		List<String> badClasses = listener.getBadClasses();
		int iBadClasses = badClasses.size();
		if (iBadClasses > 0) {
			String sMsg = badClasses.stream().collect(Collectors.joining(", "));
			String sSyntaxErrorMessage = bundle
					.getString("environmentsyntaxerror.unknowngraphemenaturalclass");
			sMessage = sMessage + "  " + sSyntaxErrorMessage.replace("{0}", sMsg);
		}
		return sMessage;
	}

	private void reportEnvironmentSyntaxError(VerboseListener errListener, int iNumErrors) {
		int i = errListener.getErrorMessages().size();
		EnvironmentErrorInfo info = errListener.getErrorMessages().get(i - 1);
		String sSyntaxErrorMessage = bundle.getString("environmentsyntaxerror.unknown");

		switch (info.getMsg()) {
		case EnvironmentConstants.CONTENT_AFTER_WORD_FINAL_BOUNDARY:
			sSyntaxErrorMessage = bundle
					.getString("environmentsyntaxerror.content_after_word_final_boundary");
			break;

		case EnvironmentConstants.CONTENT_BEFORE_WORD_INITIAL_BOUNDARY:
			sSyntaxErrorMessage = bundle
					.getString("environmentsyntaxerror.content_before_word_initial_boundary");
			break;

		case EnvironmentConstants.MISSING_CLASS_AFTER_OPENING_SQUARE_BRACKET:
			sSyntaxErrorMessage = bundle
					.getString("environmentsyntaxerror.missing_class_after_opening_square_bracket");
			break;

		case EnvironmentConstants.MISSING_CLASS_OR_GRAPHEME:
			sSyntaxErrorMessage = bundle
					.getString("environmentsyntaxerror.missing_class_or_grapheme");
			break;

		case EnvironmentConstants.MISSING_CLOSING_PAREN:
			sSyntaxErrorMessage = bundle.getString("environmentsyntaxerror.missing_closing_paren");
			break;

		case EnvironmentConstants.MISSING_CLOSING_SQUARE_BRACKET:
			sSyntaxErrorMessage = bundle
					.getString("environmentsyntaxerror.missing_closing_square_bracket");
			break;

		case EnvironmentConstants.MISSING_OPENING_PAREN:
			sSyntaxErrorMessage = bundle.getString("environmentsyntaxerror.missing_opening_paren");
			break;

		case EnvironmentConstants.MISSING_OPENING_SQUARE_BRACKET:
			sSyntaxErrorMessage = bundle
					.getString("environmentsyntaxerror.missing_opening_square_bracket");
			break;

		case EnvironmentConstants.TOO_MANY_SLASHES:
			sSyntaxErrorMessage = bundle.getString("environmentsyntaxerror.too_many_slashes");
			break;

		case EnvironmentConstants.TOO_MANY_UNDERSCORES:
			sSyntaxErrorMessage = bundle.getString("environmentsyntaxerror.too_many_underscores");
			break;

		case EnvironmentConstants.TOO_MANY_WORD_FINAL_BOUNDARIES:
			sSyntaxErrorMessage = bundle
					.getString("environmentsyntaxerror.too_many_word_final_boundaries");
			break;

		case EnvironmentConstants.TOO_MANY_WORD_INITIAL_BOUNDARIES:
			sSyntaxErrorMessage = bundle
					.getString("environmentsyntaxerror.too_many_word_initial_boundaries");
			break;

		default:
			System.out.println("error was: " + info.getMsg());
			System.out.println("number of errors was: " + iNumErrors);
			break;
		}
		int iPos = info.getCharPositionInLine();
		String sMessage = sSyntaxErrorMessage.replace("{0}", String.valueOf(iPos));
		environmentErrorMessage.setText(sMessage);
	}

	public void displayFieldsPerActiveSetting(Environment env) {
		boolean fIsActive;
		if (env == null) {
			fIsActive = false;
		} else {
			fIsActive = env.isActive();
		}
		nameField.setDisable(!fIsActive);
		descriptionField.setDisable(!fIsActive);
		representationField.setDisable(!fIsActive);
	}

	private void forceTableRowToRedisplayPerActiveSetting(Environment env) {
		// we need to make the content of the row cells change in order for
		// the cell factory to fire.
		// We do this by getting the value, blanking it, and then restoring it.
		String temp = env.getEnvironmentName();
		env.setEnvironmentName("");
		env.setEnvironmentName(temp);
		temp = env.getEnvironmentRepresentation();
		env.setEnvironmentRepresentation("");
		env.setEnvironmentRepresentation(temp);
		temp = env.getDescription();
		env.setDescription("");
		env.setDescription(temp);
	}

	@Override
	public void setViewItemUsed(int value) {
		int max = environmentTable.getItems().size();
		value = adjustIndexValue(value, max);
		environmentTable.getSelectionModel().clearAndSelect(value);
	}

	/**
	 * Fills all text fields to show details about the environment.
	 *
	 * @param env
	 *            the environment
	 */
	private void showEnvironmentDetails(Environment env) {
		currentEnvironment = env;
		if (env != null) {
			// Fill the text fields with info from the person object.
			nameField.setText(env.getEnvironmentName());
			descriptionField.setText(env.getDescription());
			representationField.setText(env.getEnvironmentRepresentation());
			NodeOrientation analysisOrientation = languageProject.getAnalysisLanguage()
					.getOrientation();
			nameField.setNodeOrientation(analysisOrientation);
			descriptionField.setNodeOrientation(analysisOrientation);
			activeCheckBox.setSelected(env.isActive());
			List<String> choices = languageProject.getActiveGraphemeNaturalClasses().stream()
					.map(GraphemeNaturalClass::getNCName).collect(Collectors.toList());
			ObservableList<String> choices2 = FXCollections.observableArrayList(choices);
			gncChoicesComboBox.setItems(choices2);
			gncChoicesComboBox.setNodeOrientation(languageProject.getAnalysisLanguage().getOrientation());
			gncChoicesComboBox.setVisible(false);
		} else {
			// Environment is null, remove all the text.
			nameField.setText("");
			descriptionField.setText("");
			representationField.setText("");
			activeCheckBox.setSelected(false);
		}
		displayFieldsPerActiveSetting(env);

		if (env != null) {
			int iCurrentIndex = environmentTable.getItems().indexOf(currentEnvironment);
			this.mainApp.updateStatusBarNumberOfItems((iCurrentIndex + 1) + "/"
					+ environmentTable.getItems().size() + " ");
			// remember the selection
			ApproachType approach = this.rootController.getCurrentApproach();
			switch (approach) {
			case CV:
				mainApp.getApplicationPreferences()
						.setLastCVEnvironmentsViewItemUsed(iCurrentIndex);
				break;

			case SONORITY_HIERARCHY:
				mainApp.getApplicationPreferences().setLastSHEnvironmentsViewItemUsed(
						iCurrentIndex);
				break;

			case ONSET_NUCLEUS_CODA:
				mainApp.getApplicationPreferences().setLastONCEnvironmentsViewItemUsed(
						iCurrentIndex);
				break;
			default:
				break;
			}
		}
	}

	public void setEnvironment(Environment environment) {
		nameField.setText(environment.getEnvironmentName());
		descriptionField.setText(environment.getDescription());
	}

	public void setData(CVApproach cvApproachData) {
		cvApproach = cvApproachData;
		languageProject = cvApproach.getLanguageProject();
		setDataCommon(ApplicationPreferences.LAST_CV_ENVIRONMENTS_VIEW_ITEM_USED);
	}

	protected void setColumnICURules() {
		setColumnICURules(nameColumn, languageProject.getAnalysisLanguage().getIcuRules());
		setColumnICURules(representationColumn, languageProject.getAnalysisLanguage().getIcuRules());
		setColumnICURules(descriptionColumn, languageProject.getAnalysisLanguage().getIcuRules());
	}

	public void setData(SHApproach shApproachData) {
		shApproach = shApproachData;
		languageProject = shApproach.getLanguageProject();
		setDataCommon(ApplicationPreferences.LAST_SH_ENVIRONMENTS_VIEW_ITEM_USED);
	}

	protected void setDataCommon(String sPref) {
		cvApproach = languageProject.getCVApproach();
		setColumnICURules();
		setTextFieldColors();
		iRepresentationCaretPosition = 6;
		fGncChoicesUsingMouse = false;

		// Add observable list data to the table
		environmentTable.setItems(languageProject.getEnvironments());
		int max = environmentTable.getItems().size();
		if (max > 0) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					int iLastIndex = mainApp.getApplicationPreferences().getIntegerValue(sPref, 0);
					iLastIndex = adjustIndexValue(iLastIndex, max);
					// select the last one used
					environmentTable.requestFocus();
					environmentTable.getSelectionModel().select(iLastIndex);
					environmentTable.getFocusModel().focus(iLastIndex);
					environmentTable.scrollTo(iLastIndex);
				}
			});
		}
	}

	public void setData(ONCApproach oncApproachData) {
		oncApproach = oncApproachData;
		languageProject = oncApproach.getLanguageProject();
		setDataCommon(ApplicationPreferences.LAST_ONC_ENVIRONMENTS_VIEW_ITEM_USED);
	}

	public void setData(MoraicApproach muApproachData) {
		muApproach = muApproachData;
		languageProject = muApproach.getLanguageProject();
		setDataCommon(ApplicationPreferences.LAST_MORAIC_ENVIRONMENTS_VIEW_ITEM_USED);
	}

	protected void setTextFieldColors() {
		if (languageProject != null) {
			String sAnalysis = mainApp.getStyleFromColor(languageProject.getAnalysisLanguage().getColor());
			nameField.setStyle(sAnalysis);
			descriptionField.setStyle(sAnalysis);
		}
	}

	@Override
	void handleInsertNewItem() {
		Environment newEnvironment = new Environment();
		cvApproach.getLanguageProject().getEnvironments().add(newEnvironment);
		newEnvironment.setEnvironmentRepresentation("/  _  ");
		handleInsertNewItem(cvApproach.getLanguageProject().getEnvironments(), environmentTable);
	}

	@Override
	void handleRemoveItem() {
		handleRemoveItem(cvApproach.getLanguageProject().getEnvironments(), currentEnvironment, environmentTable);
	}

	@Override
	void handlePreviousItem() {
		handlePreviousItem(cvApproach.getLanguageProject().getEnvironments(), currentEnvironment, environmentTable);
	}

	@Override
	void handleNextItem() {
		handleNextItem(cvApproach.getLanguageProject().getEnvironments(), currentEnvironment, environmentTable);
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return new TextField[] { nameField, descriptionField, representationField };
	}

	protected void handleCheckBoxSelectAll() {
		for (Environment env : cvApproach.getLanguageProject().getEnvironments()) {
			env.setActive(true);
			forceTableRowToRedisplayPerActiveSetting(env);
		}
	}

	protected void handleCheckBoxClearAll() {
		for (Environment env : cvApproach.getLanguageProject().getEnvironments()) {
			env.setActive(false);
			forceTableRowToRedisplayPerActiveSetting(env);
		}
	}

	protected void handleCheckBoxToggle() {
		for (Environment env : cvApproach.getLanguageProject().getEnvironments()) {
			if (env.isActive()) {
				env.setActive(false);
			} else {
				env.setActive(true);
			}
			forceTableRowToRedisplayPerActiveSetting(env);
		}
	}
}
