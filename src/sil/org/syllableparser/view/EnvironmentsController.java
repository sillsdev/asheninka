// Copyright (c) 2016-2017 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package sil.org.syllableparser.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
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

import sil.org.environmentparser.CheckGraphemeAndClassListener;
import sil.org.environmentparser.EnvironmentConstants;
import sil.org.environmentparser.EnvironmentErrorInfo;
import sil.org.environmentparser.EnvironmentErrorListener;
import sil.org.environmentparser.GraphemeErrorInfo;
import sil.org.environmentparser.EnvironmentErrorListener.VerboseListener;
import sil.org.environmentparser.EnvironmentLexer;
import sil.org.environmentparser.EnvironmentParser;
import sil.org.syllableparser.Constants;
import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.Grapheme;
import sil.org.syllableparser.model.GraphemeNaturalClass;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.SylParserObject;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.Environment;
import sil.org.syllableparser.service.AsheninkaGraphemeAndClassListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */

public class EnvironmentsController extends SylParserBaseController implements Initializable {

	protected final class AnalysisWrappingTableCell extends TableCell<Environment, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (item == null || empty) {
				setText(null);
				setStyle("");
			} else {
				setStyle("");
				text = new Text(item.toString());
				// Get it to wrap.
				text.wrappingWidthProperty().bind(getTableColumn().widthProperty());
				Environment env = (Environment) this.getTableRow().getItem();
				if (env != null && env.isActive()) {
					text.setFill(Constants.ACTIVE);
				} else {
					text.setFill(Constants.INACTIVE);
				}
				text.setFont(languageProject.getAnalysisLanguage().getFont());
				setGraphic(text);
			}
		}
	}

	protected final class VernacularWrappingTableCell extends TableCell<Environment, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (item == null || empty) {
				setText(null);
				setStyle("");
			} else {
				setStyle("");
				text = new Text(item.toString());
				// Get it to wrap.
				text.wrappingWidthProperty().bind(getTableColumn().widthProperty());
				Environment env = (Environment) this.getTableRow().getItem();
				if (env != null && env.isActive()) {
					text.setFill(Constants.ACTIVE);
				} else {
					text.setFill(Constants.INACTIVE);
				}
				text.setFont(languageProject.getVernacularLanguage().getFont());
				setGraphic(text);
			}
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

	private Environment currentEnvironment;

	public EnvironmentsController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

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
			return new VernacularWrappingTableCell();
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

		activeCheckBox.setOnAction((event) -> {
			if (currentEnvironment != null) {
				currentEnvironment.setActive(activeCheckBox.isSelected());
				forceTableRowToRedisplayPerActiveSetting(currentEnvironment);
			}
			displayFieldsPerActiveSetting(currentEnvironment);
		});

		// Use of Enter move focus to next item.
		nameField.setOnAction((event) -> {
			descriptionField.requestFocus();
		});
		descriptionField.setOnAction((event) -> {
			representationField.requestFocus();
		});

		nameField.requestFocus();

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
		Environment env =  validator.getEnvironment();
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
			activeCheckBox.setSelected(env.isActive());
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
			mainApp.getApplicationPreferences().setLastCVEnvironmentsViewItemUsed(iCurrentIndex);
		}

	}

	public void setEnvironment(Environment environment) {
		nameField.setText(environment.getEnvironmentName());
		descriptionField.setText(environment.getDescription());
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 *
	 * @param cvApproachController
	 */
	public void setData(CVApproach cvApproachData) {
		cvApproach = cvApproachData;
		languageProject = cvApproach.getLanguageProject();

		// Add observable list data to the table
		environmentTable.setItems(cvApproachData.getLanguageProject().getEnvironments());
		int max = environmentTable.getItems().size();
		if (max > 0) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					int iLastIndex = mainApp.getApplicationPreferences()
							.getLastCVEnvironmentsViewItemUsed();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		Environment newEnvironment = new Environment();
		cvApproach.getLanguageProject().getEnvironments().add(newEnvironment);
		newEnvironment.setEnvironmentRepresentation("/  _  ");
		int i = cvApproach.getLanguageProject().getEnvironments().size() - 1;
		environmentTable.requestFocus();
		environmentTable.getSelectionModel().select(i);
		environmentTable.getFocusModel().focus(i);
		environmentTable.scrollTo(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		// need to deal with all pointers to this environment
		int i = cvApproach.getLanguageProject().getEnvironments().indexOf(currentEnvironment);
		currentEnvironment = null;
		if (i >= 0) {
			cvApproach.getLanguageProject().getEnvironments().remove(i);
			int max = environmentTable.getItems().size();
			i = adjustIndexValue(i, max);
			// select the last one used
			environmentTable.requestFocus();
			environmentTable.getSelectionModel().select(i);
			environmentTable.getFocusModel().focus(i);
			environmentTable.scrollTo(i);
		}
		// the last item in the middle pane will be repeated if we delete an
		// earlier one
		// how can we get it to show a blank?
		// chances are we do not have something set up properly - looks like it
		// is just supposed to work
		// Any (currently showing) item referring to the deleted item needs to
		// be have its sncField recalculated.
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
