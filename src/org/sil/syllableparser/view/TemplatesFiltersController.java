// Copyright (c) 2019-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.Collections;
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
import org.sil.antlr4.templatefilterparser.SegmentErrorInfo;
import org.sil.antlr4.templatefilterparser.TemplateFilterConstants;
import org.sil.antlr4.templatefilterparser.TemplateFilterErrorInfo;
import org.sil.antlr4.templatefilterparser.TemplateFilterErrorListener;
import org.sil.antlr4.templatefilterparser.TemplateFilterErrorListener.VerboseListener;
import org.sil.antlr4.templatefilterparser.antlr4generated.TemplateFilterLexer;
import org.sil.antlr4.templatefilterparser.antlr4generated.TemplateFilterParser;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.TemplateFilter;
import org.sil.syllableparser.model.TemplateType;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.service.AsheninkaSegmentAndClassListener;
import org.sil.utility.view.ControllerUtilities;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

/**
 * @author Andy Black
 *
 */

public abstract class TemplatesFiltersController extends SplitPaneWithTableViewController {

	protected final class AnalysisWrappingTableCell extends TableCell<TemplateFilter, String> {
		protected Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processAnalysisTableCell(this, text, item, empty);
		}
	}

	protected final class WrappingTableCell extends TableCell<TemplateFilter, String> {
		protected Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processTableCell(this, text, item, empty);
		}
	}

	@FXML
	protected TableView<TemplateFilter> templateFilterTable;
	@FXML
	protected TableColumn<TemplateFilter, String> nameColumn;
	@FXML
	protected TableColumn<TemplateFilter, String> typeColumn;
	@FXML
	protected TableColumn<TemplateFilter, String> representationColumn;
	@FXML
	protected TableColumn<TemplateFilter, String> descriptionColumn;
	@FXML
	protected TableColumn<TemplateFilter, Boolean> checkBoxColumn;
	@FXML
	protected CheckBox checkBoxColumnHead;

	@FXML
	private Button buttonMoveUp;
	@FXML
	private Button buttonMoveDown;
	@FXML
	private Tooltip tooltipMoveUp;
	@FXML
	private Tooltip tooltipMoveDown;
	@FXML
	protected TextField nameField;
	@FXML
	protected ComboBox<TemplateType> typeComboBox;
	@FXML
	protected TextField representationField;
	@FXML
	protected TextField descriptionField;
	@FXML
	protected CheckBox activeCheckBox;
	@FXML
	protected Label slotsErrorMessage;
	@FXML
	protected ComboBox<String> sncChoicesComboBox;

	protected TemplateFilter currentTemplateFilter;
	protected int iRepresentationCaretPosition = 6;
	// fSncChoicesUsingMouse distinguishes between using the keyboard or the
	// mouse while using the combo box.
	protected boolean fSncChoicesUsingMouse;
	// fJustSwitchedFocusFromSncComboBoxToRepField when true will put cursor at
	// the end of the class name in the representationField
	protected boolean fJustSwitchedFocusFromSncComboBoxToRepField = false;
	
	protected ObservableList<TemplateFilter> contentList = FXCollections.observableArrayList();
	protected boolean fAllowSlotPosition = false;

	public TemplatesFiltersController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		this.bundle = resources;
		// Initialize the button icons
		tooltipMoveUp = ControllerUtilities.createToolbarButtonWithImage("UpArrow.png",
				buttonMoveUp, tooltipMoveUp, bundle.getString("sh.view.sonorityhierarchy.up"),
				Constants.RESOURCE_SOURCE_LOCATION);
		tooltipMoveDown = ControllerUtilities.createToolbarButtonWithImage("DownArrow.png",
				buttonMoveDown, tooltipMoveDown, bundle.getString("sh.view.sonorityhierarchy.down"),
				Constants.RESOURCE_SOURCE_LOCATION);

		nameColumn.setCellValueFactory(cellData -> cellData.getValue().templateFilterNameProperty());
		typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
		representationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.templateFilterRepresentationProperty());
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

		// Custom rendering of the table cell.
		nameColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});
		typeColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});
		representationColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});

		makeColumnHeaderWrappable(nameColumn);
		makeColumnHeaderWrappable(typeColumn);
		makeColumnHeaderWrappable(representationColumn);
		makeColumnHeaderWrappable(descriptionColumn);

		// Since template/filter items are sorted manually, we do not
		// want the user to be able to click on a column header and sort it
		nameColumn.setSortable(false);
		typeColumn.setSortable(false);
		representationColumn.setSortable(false);
		descriptionColumn.setSortable(false);

		// Handle TextField text changes.
		nameField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentTemplateFilter != null) {
				currentTemplateFilter.setTemplateFilterName(nameField.getText());
			}
			if (languageProject != null) {
				nameField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});

		descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentTemplateFilter != null) {
				currentTemplateFilter.setDescription(descriptionField.getText());
			}
			if (languageProject != null) {
				descriptionField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});

		slotsErrorMessage.setTextFill(Constants.SLOTS_ERROR_MESSAGE);
		slotsErrorMessage.setWrapText(true);

		representationField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentTemplateFilter != null) {
				processRepresentationFieldContents();
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
										if (fJustSwitchedFocusFromSncComboBoxToRepField) {
											representationField
													.positionCaret(iRepresentationCaretPosition);
											fJustSwitchedFocusFromSncComboBoxToRepField = false;
										}
										sncChoicesComboBox.setVisible(false);
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
							sncChoicesComboBox.setVisible(false);
							representationField.requestFocus();
						}
					});
					break;
				case OPEN_BRACKET:
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							iRepresentationCaretPosition = representationField.getCaretPosition();
							sncChoicesComboBox.setVisible(true);
							// this does show the list, but the list remains
							// showing and we don't want that:
							// gncChoicesComboBox.show();
							sncChoicesComboBox.getSelectionModel().clearSelection();
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
			if (currentTemplateFilter != null) {
				currentTemplateFilter.setActive(activeCheckBox.isSelected());
				forceTableRowToRedisplayPerActiveSetting(currentTemplateFilter);
			}
			displayFieldsPerActiveSetting(currentTemplateFilter);
		});

		// When using the keyboard for the combo box, show the options with
		// first down arrow and select the item when the Enter key is pressed.
		// NB: for some reason, using setOnKeyPressed does not see the ENTER
		// key. Presumably it is being used by a child node to do the select
		// action.
		sncChoicesComboBox.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case ENTER:
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							String selectedValue = sncChoicesComboBox.getSelectionModel()
									.getSelectedItem();
//							System.out.println("ENTER: selectedValue=" + selectedValue);
							if (selectedValue != null) {
								fSncChoicesUsingMouse = true;
								updateRepresentationFieldPerClassChoice(selectedValue);
								fJustSwitchedFocusFromSncComboBoxToRepField = true;
								representationField.requestFocus();
								fSncChoicesUsingMouse = false;
							}
						}
					});
					break;
				case DOWN:
//					System.out.println("down arrow: using mouse = " + fGncChoicesUsingMouse);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							if (!sncChoicesComboBox.isShowing()) {
								sncChoicesComboBox.show();
							}
							fSncChoicesUsingMouse = false;
						}
					});
					break;
				default:
					break;
				}
			}
		});
		// User has used the mouse to click on the combo box
		sncChoicesComboBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
//						System.out.println("Mouse clicked");
						fSncChoicesUsingMouse = true;
					}
				});
			}
		});
		sncChoicesComboBox.getSelectionModel().selectedItemProperty()
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
								if (fSncChoicesUsingMouse && selectedValue != null) {
									updateRepresentationFieldPerClassChoice(selectedValue);
									fSncChoicesUsingMouse = false;
									fJustSwitchedFocusFromSncComboBoxToRepField = true;
									representationField.requestFocus();
								}
							}
						});
					}
				});
		String sChooseClass = resources.getString("label.chooseclass");
		sncChoicesComboBox.setPromptText(sChooseClass);

		// Use of Enter move focus to next item.
		nameField.setOnAction((event) -> {
			descriptionField.requestFocus();
		});
		descriptionField.setOnAction((event) -> {
			representationField.requestFocus();
		});

		nameField.requestFocus();

	}

	protected void processRepresentationFieldContents() {
		if (currentTemplateFilter != null) {
			String sRep = representationField.getText();
			currentTemplateFilter.setTemplateFilterRepresentation(sRep);
			currentTemplateFilter.getSlots().clear();
			boolean fParseSucceeded = parseSlotsRepresentation(sRep);
			currentTemplateFilter.setValid(fParseSucceeded);
			slotsErrorMessage.setVisible(!fParseSucceeded);
		}
	}

	protected void updateRepresentationFieldPerClassChoice(String selectedValue) {
		if (selectedValue != null) {
			String sLeftOfCaret = representationField.getText().substring(0,
					iRepresentationCaretPosition);
			String sRightOfCaret = representationField.getText().substring(
					iRepresentationCaretPosition);
			representationField.setText(sLeftOfCaret + selectedValue + sRightOfCaret);
			iRepresentationCaretPosition += selectedValue.length();
		}
	}

	protected boolean parseSlotsRepresentation(String sRep) {
		CharStream input = CharStreams.fromString(sRep);
		TemplateFilterLexer lexer = new TemplateFilterLexer(input);
		TemplateFilterLexer.slotPosition = fAllowSlotPosition;
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		TemplateFilterParser parser = new TemplateFilterParser(tokens);
		parser.removeErrorListeners();
		VerboseListener errListener = new TemplateFilterErrorListener.VerboseListener();
		errListener.clearErrorMessageList();
		parser.addErrorListener(errListener);
		ParseTree tree = parser.description();
		int iNumErrors = parser.getNumberOfSyntaxErrors();
		if (iNumErrors > 0) {
			reportTemplateFilterSyntaxError(errListener, iNumErrors);
			return false;
		}

		return buildTemplateFilter(parser, tree);

	}

	protected boolean buildTemplateFilter(TemplateFilterParser parser, ParseTree tree) {
		ParseTreeWalker walker = new ParseTreeWalker();
		List<String> segments = this.languageProject.getActiveSegmentsInInventory()
				.stream().map(Segment::getSegment)
				.collect(Collectors.toList());
		List<String> naturalClasses = this.languageProject.getCVApproach().getActiveCVNaturalClasses()
				.stream().map(CVNaturalClass::getNCName)
				.collect(Collectors.toList());
		AsheninkaSegmentAndClassListener validator = new AsheninkaSegmentAndClassListener(parser,
				segments, naturalClasses);
		validator.setupSegmentsMasterList(languageProject.getActiveSegmentsInInventory());
		validator.setClasses(languageProject.getCVApproach().getActiveCVNaturalClasses());
		validator.setTemplateFilter(currentTemplateFilter);
		walker.walk(validator, tree); // initiate walk of tree with listener
		parser.removeParseListeners();
		parser.addParseListener(validator);
		AsheninkaSegmentAndClassListener listener = (AsheninkaSegmentAndClassListener) parser
				.getParseListeners().get(0);
		String sMessage = "";
		sMessage = buildAnyMissingItems(listener, sMessage);
		if (sMessage.length() > 0) {
			slotsErrorMessage.setText(sMessage);
			return false;
		}
		TemplateFilter tf = validator.getTemplateFilter();
		currentTemplateFilter.setSlots(tf.getSlots());
		return true;
	}

	protected String buildAnyMissingItems(AsheninkaSegmentAndClassListener listener, String sMessage) {
		StringBuilder sb = new StringBuilder();
		List<SegmentErrorInfo> badSegments = listener.getBadSegments();
		int iBadSegments = badSegments.size();
		if (iBadSegments > 0) {
			String sMsg = badSegments.stream().map(SegmentErrorInfo::getSegment)
					.collect(Collectors.joining(", "));
			Optional<SegmentErrorInfo> info = badSegments.stream().collect(
					Collectors.maxBy(Comparator.comparing(SegmentErrorInfo::getMaxDepth)));
			int iMax = (info.isPresent() ? info.get().getMaxDepth() : -1);
			String sSyntaxErrorMessage = bundle.getString("templatefiltersyntaxerror.unknownsegment");
			sMessage = sSyntaxErrorMessage.replace("{0}", sMsg.substring(iMax));
			appendMessage(sMessage, sb);
		}
		List<String> badClasses = listener.getBadClasses();
		int iBadClasses = badClasses.size();
		if (iBadClasses > 0) {
			String sMsg = badClasses.stream().collect(Collectors.joining(", "));
			String sSyntaxErrorMessage = bundle
					.getString("templatefiltersyntaxerror.unknownnaturalclass");
			sMessage = sMessage + "  " + sSyntaxErrorMessage.replace("{0}", sMsg);
			appendMessage(sMessage, sb);
		}

		if (fAllowSlotPosition && listener.getSlotPositionIndicatorsFound() == 0) {
			sMessage = bundle.getString("templatefiltersyntaxerror.required_slot_position_indicator");
			appendMessage(sMessage, sb);
		}

		if (fAllowSlotPosition && listener.getSlotPositionIndicatorsFound() > 1) {
			sMessage = bundle.getString("templatefiltersyntaxerror.extra_slot_position_indicator");
			appendMessage(sMessage, sb);
		}

		if (!listener.isObligatorySegmentFound()) {
			sMessage = bundle.getString("templatefiltersyntaxerror.all_slots_optional");
			appendMessage(sMessage, sb);
		}
		return sb.toString();
	}

	protected void appendMessage(String sMessage, StringBuilder sb) {
		sb.append(sMessage);
		sb.append("\n");
	}

	protected void reportTemplateFilterSyntaxError(VerboseListener errListener, int iNumErrors) {
		StringBuilder sMessagesToReport = new StringBuilder();
		for (TemplateFilterErrorInfo info : errListener.getErrorMessages()) {
			String sSyntaxErrorMessage = bundle.getString("templatefiltersyntaxerror.unknown");

			switch (info.getMsg()) {
			case TemplateFilterConstants.MISSING_CLASS_AFTER_OPENING_SQUARE_BRACKET:
				sSyntaxErrorMessage = bundle
						.getString("templatefiltersyntaxerror.missing_class_after_opening_square_bracket");
				break;

			case TemplateFilterConstants.MISSING_CLASS_OR_SEGMENT:
				sSyntaxErrorMessage = bundle
						.getString("templatefiltersyntaxerror.missing_class_or_segment");
				break;

			case TemplateFilterConstants.MISSING_CLOSING_PAREN:
				sSyntaxErrorMessage = bundle
						.getString("templatefiltersyntaxerror.missing_closing_paren");
				break;

			case TemplateFilterConstants.MISSING_CLOSING_SQUARE_BRACKET:
				sSyntaxErrorMessage = bundle
						.getString("templatefiltersyntaxerror.missing_closing_square_bracket");
				break;

			case TemplateFilterConstants.MISSING_OPENING_PAREN:
				sSyntaxErrorMessage = bundle
						.getString("templatefiltersyntaxerror.missing_opening_paren");
				break;

			case TemplateFilterConstants.MISSING_OPENING_SQUARE_BRACKET:
				sSyntaxErrorMessage = bundle
						.getString("templatefiltersyntaxerror.missing_opening_square_bracket");
				break;

			case TemplateFilterConstants.EXTRA_SLOT_POSITION_INDICATOR:
				sSyntaxErrorMessage = bundle
						.getString("templatefiltersyntaxerror.extra_slot_position_indicator");
				break;

			case "extraneous input '|' expecting {<EOF>, '(', '*', '[', ']', ID}":
				sSyntaxErrorMessage = bundle
						.getString("templatefiltersyntaxerror.slot_position_needs_segment_or_class_before_it");
				break;

			case "mismatched input '<EOF>' expecting {'(', '*', '[', ']', ID}":
				sSyntaxErrorMessage = bundle
						.getString("templatefiltersyntaxerror.slot_position_needs_segment_or_class_after_it");
				break;

			default:
				System.out.println("error was: " + info.getMsg());
				System.out.println("number of errors was: " + iNumErrors);
				break;
			}
			int iPos = info.getCharPositionInLine();
			String sMessage = sSyntaxErrorMessage.replace("{0}", String.valueOf(iPos));
			appendMessage(sMessage, sMessagesToReport);
		}
		slotsErrorMessage.setText(sMessagesToReport.toString());
	}

	public void displayFieldsPerActiveSetting(TemplateFilter tf) {
		boolean fIsActive;
		if (tf == null) {
			fIsActive = false;
		} else {
			fIsActive = tf.isActive();
		}
		nameField.setDisable(!fIsActive);
		descriptionField.setDisable(!fIsActive);
		representationField.setDisable(!fIsActive);
		typeComboBox.setDisable(!fIsActive);
	}

	protected void forceTableRowToRedisplayPerActiveSetting(TemplateFilter tf) {
		// we need to make the content of the row cells change in order for
		// the cell factory to fire.
		// We do this by getting the value, blanking it, and then restoring it.
		String temp = tf.getTemplateFilterName();
		tf.setTemplateFilterName("");
		tf.setTemplateFilterName(temp);
		temp = tf.getTemplateFilterRepresentation();
		tf.setTemplateFilterRepresentation("");
		tf.setTemplateFilterRepresentation(temp);
		temp = tf.getDescription();
		tf.setDescription("");
		tf.setDescription(temp);
		temp = tf.getType();
		tf.setType("");
		tf.setType(temp);
	}

	protected abstract void rememberSelection(int iCurrentIndex);

	public void setTemplateFilter(TemplateFilter templateFilter) {
		nameField.setText(templateFilter.getTemplateFilterName());
		descriptionField.setText(templateFilter.getDescription());
	}

	protected void handleMoveDown(ObservableList<? extends TemplateFilter> list) {
		int i = list.indexOf(currentTemplateFilter);
		if ((i + 1) < list.size()) {
			Collections.swap(list, i, i + 1);
		}
	}

	protected void handleMoveUp(ObservableList<? extends TemplateFilter> list) {
		int i = list.indexOf(currentTemplateFilter);
		if (i > 0) {
			Collections.swap(list, i, i - 1);
		}
	}

	protected void setUpDownButtonDisabled(ObservableList<? extends TemplateFilter> list) {
		int iThis = list.indexOf(currentTemplateFilter) + 1;
		int iSize = list.size();
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

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return new TextField[] { nameField, descriptionField, representationField };
	}

	protected void handleCheckBoxSelectAll() {
		for (TemplateFilter tf : contentList) {
			tf.setActive(true);
			forceTableRowToRedisplayPerActiveSetting(tf);
		}
	}

	protected void handleCheckBoxClearAll() {
		for (TemplateFilter tf : contentList) {
			tf.setActive(false);
			forceTableRowToRedisplayPerActiveSetting(tf);
		}
	}

	protected void handleCheckBoxToggle() {
		for (TemplateFilter tf : contentList) {
			if (tf.isActive()) {
				tf.setActive(false);
			} else {
				tf.setActive(true);
			}
			forceTableRowToRedisplayPerActiveSetting(tf);
		}
	}
}
