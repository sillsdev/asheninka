// Copyright (c) 2016-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.ApproachType;
import org.sil.syllableparser.model.Environment;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.GraphemeNaturalClass;
import org.sil.syllableparser.model.Language;
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.utility.view.ControllerUtilities;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */

public class GraphemeNaturalClassesController extends SplitPaneWithTableViewController {

	protected final class AnalysisWrappingTableCell extends TableCell<GraphemeNaturalClass, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processAnalysisTableCell(this, text, item, empty);
		}
	}

	protected final class WrappingTableCell extends
			TableCell<GraphemeNaturalClass, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processTableCell(this, text, item, empty);
		}
	}

	@FXML
	private TableView<GraphemeNaturalClass> graphemeNaturalClassTable;
	@FXML
	private TableColumn<GraphemeNaturalClass, String> nameColumn;
	@FXML
	private TableColumn<GraphemeNaturalClass, String> graphemeOrNaturalClassColumn;
	@FXML
	private TableColumn<GraphemeNaturalClass, String> descriptionColumn;
	@FXML
	private TableColumn<GraphemeNaturalClass, Boolean> checkBoxColumn;
	@FXML
	private CheckBox checkBoxColumnHead;

	@FXML
	private TextField nameField;
	@FXML
	private TextField graphemeOrNaturalClassField;
	@FXML
	private TextField descriptionField;
	@FXML
	private FlowPane gncField;
	@FXML
	private TextFlow gncTextFlow;
	@FXML
	private Button gncButton;
	@FXML
	private CheckBox activeCheckBox;

	private GraphemeNaturalClass currentNaturalClass;

	private List<Environment> environmentsUsingThisClass;

	public GraphemeNaturalClassesController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.setApproach(ApplicationPreferences.GRAPHEME_NATURAL_CLASSES);
		super.setTableView(graphemeNaturalClassTable);
		super.initialize(location, resources);

		nameColumn.setCellValueFactory(cellData -> cellData.getValue().ncNameProperty());
		graphemeOrNaturalClassColumn.setCellValueFactory(cellData -> cellData.getValue()
				.gncRepresentationProperty());
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

		// Custom rendering of the table cell.
		nameColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});
		graphemeOrNaturalClassColumn.setCellFactory(column -> {
			return new TableCell<GraphemeNaturalClass, String>() {
				// We override computePrefHeight because by default, the graphic's height
				// gets set to the height of all items in the TextFlow as if none of them
				// wrapped.  So for now, we're doing this hack.
				@Override
				protected double computePrefHeight(double width) {
					Object g = getGraphic();
					if (g instanceof TextFlow) {
						return guessPrefHeight(g, column.widthProperty().get());
					}
					return super.computePrefHeight(-1);
				}

				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					GraphemeNaturalClass nc = ((GraphemeNaturalClass) getTableRow().getItem());
					if (item == null || empty || nc == null) {
						setGraphic(null);
						setText(null);
						setStyle("");
					} else {
						setGraphic(null);
						TextFlow tf = new TextFlow();
						if (languageProject.getVernacularLanguage().getOrientation() == NodeOrientation.LEFT_TO_RIGHT) {
							tf = buildTextFlow(nc.getGraphemesOrNaturalClasses());
							tf.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
						} else {
							FXCollections.reverse(nc.getGraphemesOrNaturalClasses());
							tf = buildTextFlow(nc.getGraphemesOrNaturalClasses());
							tf.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
							FXCollections.reverse(nc.getGraphemesOrNaturalClasses());
						}
						setGraphic(tf);
						setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
					}
				}
			};
		});

		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});

		makeColumnHeaderWrappable(nameColumn);
		makeColumnHeaderWrappable(graphemeOrNaturalClassColumn);
		makeColumnHeaderWrappable(descriptionColumn);

		// Clear cv natural class details.
		showGraphemeNaturalClassDetails(null);

		// Listen for selection changes and show the details when changed.
		graphemeNaturalClassTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showGraphemeNaturalClassDetails(newValue));

		// Handle TextField text changes.
		nameField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentNaturalClass != null) {
				currentNaturalClass.setNCName(nameField.getText());
				for (Environment env : environmentsUsingThisClass) {
					env.rebuildRepresentationFromContext();
				}
			}
			if (languageProject != null) {
				nameField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});

		descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentNaturalClass != null) {
				currentNaturalClass.setDescription(descriptionField.getText());
			}
			if (languageProject != null) {
				descriptionField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});

		activeCheckBox.setOnAction((event) -> {
			if (currentNaturalClass != null) {
				currentNaturalClass.setActive(activeCheckBox.isSelected());
				showGraphemeOrNaturalClassContent();
				forceTableRowToRedisplayPerActiveSetting(currentNaturalClass);
			}
			displayFieldsPerActiveSetting(currentNaturalClass);
		});

		// Use of Enter move focus to next item.
		nameField.setOnAction((event) -> {
			descriptionField.requestFocus();
		});
		descriptionField.setOnAction((event) -> {
			graphemeOrNaturalClassField.requestFocus();
		});

		nameField.requestFocus();

	}

	public void displayFieldsPerActiveSetting(GraphemeNaturalClass nc) {
		boolean fIsActive;
		if (nc == null) {
			fIsActive = false;
		} else {
			fIsActive = nc.isActive();
		}
		nameField.setDisable(!fIsActive);
		gncTextFlow.setDisable(!fIsActive);
		gncButton.setDisable(!fIsActive);
		descriptionField.setDisable(!fIsActive);
	}

	private void forceTableRowToRedisplayPerActiveSetting(GraphemeNaturalClass nc) {
		// we need to make the content of the row cells change in order for
		// the cell factory to fire.
		// We do this by getting the value, blanking it, and then restoring it.
		String temp = nc.getNCName();
		nc.setNCName("");
		nc.setNCName(temp);
		temp = nc.getGNCRepresentation();
		nc.setGNCRepresentation("");
		nc.setGNCRepresentation(temp);
		temp = nc.getDescription();
		nc.setDescription("");
		nc.setDescription(temp);
	}

	@Override
	public void setViewItemUsed(int value) {
		int max = graphemeNaturalClassTable.getItems().size();
		value = adjustIndexValue(value, max);
		graphemeNaturalClassTable.getSelectionModel().clearAndSelect(value);
	}

	/**
	 * Fills all text fields to show details about the CV natural class. If the
	 * specified segment is null, all text fields are cleared.
	 *
	 * @param naturalClass
	 *            the segment or null
	 */
	private void showGraphemeNaturalClassDetails(GraphemeNaturalClass naturalClass) {
		currentNaturalClass = naturalClass;
		if (languageProject != null) {
			environmentsUsingThisClass = languageProject.getActiveAndValidEnvironments().stream()
					.filter(env -> env.usesGraphemeNaturalClass(currentNaturalClass))
					.collect(Collectors.toList());
		}
		if (naturalClass != null) {
			// Fill the text fields with info from the object.
			nameField.setText(naturalClass.getNCName());
			descriptionField.setText(naturalClass.getDescription());
			NodeOrientation analysisOrientation = languageProject.getAnalysisLanguage()
					.getOrientation();
			nameField.setNodeOrientation(analysisOrientation);
			descriptionField.setNodeOrientation(analysisOrientation);
			gncTextFlow.setNodeOrientation(languageProject.getVernacularLanguage()
					.getOrientation());
			activeCheckBox.setSelected(naturalClass.isActive());
			showGraphemeOrNaturalClassContent();
		} else {
			// Segment is null, remove all the text.
			nameField.setText("");
			descriptionField.setText("");
			gncTextFlow.getChildren().clear();
		}
		displayFieldsPerActiveSetting(naturalClass);

		if (naturalClass != null) {
			int iCurrentIndex = graphemeNaturalClassTable.getItems().indexOf(currentNaturalClass);
			this.mainApp.updateStatusBarNumberOfItems((iCurrentIndex + 1) + "/"
					+ graphemeNaturalClassTable.getItems().size() + " ");
			// remember the selection
			String sApproach = this.rootController.getApproachUsed();
			if (sApproach.equals(ApproachType.CV.name())) {
				mainApp.getApplicationPreferences().setLastCVGraphemeNaturalClassesViewItemUsed(
						iCurrentIndex);
			} else if (sApproach.equals(ApproachType.SONORITY_HIERARCHY.name())) {
				mainApp.getApplicationPreferences().setLastSHGraphemeNaturalClassesViewItemUsed(
						iCurrentIndex);
			}
		}
	}

	private void showGraphemeOrNaturalClassContent() {
		StringBuilder sb = new StringBuilder();
		gncTextFlow.getChildren().clear();
		ObservableList<SylParserObject> graphemesOrNaturalClasses = currentNaturalClass.getGraphemesOrNaturalClasses();
		if (languageProject.getVernacularLanguage().getOrientation() == NodeOrientation.LEFT_TO_RIGHT) {
			fillGncTextFlow(sb, graphemesOrNaturalClasses);
		} else {
			FXCollections.reverse(graphemesOrNaturalClasses);
			fillGncTextFlow(sb, graphemesOrNaturalClasses);
			FXCollections.reverse(graphemesOrNaturalClasses);
		}
		currentNaturalClass.setGNCRepresentation(sb.toString());
	}

	protected void fillGncTextFlow(StringBuilder sb,
			ObservableList<SylParserObject> graphemesOrNaturalClasses) {
		Language analysis = languageProject.getAnalysisLanguage();
		Language vernacular = languageProject.getVernacularLanguage();
		int i = 1;
		int iCount = graphemesOrNaturalClasses.size();
		for (SylParserObject gnc : graphemesOrNaturalClasses) {
			Text t;
			String s;
			if (gnc instanceof Grapheme) {
				s = ((Grapheme) gnc).getForm();
				t = new Text(s);
				t.setFont(vernacular.getFont());
				t.setFill(vernacular.getColor());
				t.setNodeOrientation(vernacular.getOrientation());
				sb.append(s);
			} else if (gnc instanceof GraphemeNaturalClass) {
				s = ((GraphemeNaturalClass) gnc).getNCName();
				s = Constants.NATURAL_CLASS_PREFIX + s + Constants.NATURAL_CLASS_SUFFIX;
				t = new Text(s);
				t.setFont(analysis.getFont());
				t.setFill(analysis.getColor());
				t.setNodeOrientation(analysis.getOrientation());
				sb.append(s);
			} else {
				s = "ERROR!";
				t = new Text(s);
				sb.append(s);
			}
			if (!(gnc.isActive() && activeCheckBox.isSelected())) {
				t.setFill(Constants.INACTIVE);
			}
			Text tBar = new Text(" | ");
			tBar.setStyle("-fx-stroke: lightgrey;");
			gncTextFlow.getChildren().addAll(t, tBar);
			if (i++ < iCount) {
				sb.append(", ");
			}
		}
	}

	@Override
	protected TextFlow buildTextFlow(ObservableList<SylParserObject> graphemesOrNaturalClasses) {
		TextFlow tf = new TextFlow();
		Language analysis = languageProject.getAnalysisLanguage();
		Language vernacular = languageProject.getVernacularLanguage();
		int i = 1;
		int iCount = graphemesOrNaturalClasses.size();
		for (SylParserObject gnc : graphemesOrNaturalClasses) {
			Text t;
			String s;
			if (gnc instanceof Grapheme) {
				s = ((Grapheme) gnc).getForm();
				t = new Text(s);
				t.setFont(vernacular.getFont());
				t.setFill(vernacular.getColor());
				t.setNodeOrientation(vernacular.getOrientation());
			} else if (gnc instanceof GraphemeNaturalClass) {
				s = ((GraphemeNaturalClass) gnc).getNCName();
				s = Constants.NATURAL_CLASS_PREFIX + s + Constants.NATURAL_CLASS_SUFFIX;
				t = new Text(s);
				t.setFont(analysis.getFont());
				t.setFill(analysis.getColor());
				t.setNodeOrientation(analysis.getOrientation());
			} else {
				s = "ERROR!";
				t = new Text(s);
			}
			if (!(gnc.isActive() && activeCheckBox.isSelected())) {
				t.setFill(Constants.INACTIVE);
			}
			Text tBar = new Text(" | ");
			tBar.setStyle("-fx-stroke: lightgrey;");
			tf.getChildren().addAll(t, tBar);
		}
		return tf;
	}

	public void setNaturalClass(GraphemeNaturalClass naturalClass) {
		nameField.setText(naturalClass.getNCName());
		descriptionField.setText(naturalClass.getDescription());
	}

	public void setData(CVApproach cvApproachData) {
		cvApproach = cvApproachData;
		languageProject = cvApproach.getLanguageProject();
		setColumnICURules();
		setTextFieldColors();
		addDataToTable();
	}

	protected void setColumnICURules() {
		setColumnICURules(graphemeOrNaturalClassColumn, languageProject.getVernacularLanguage().getAnyIcuRules());
		setColumnICURules(nameColumn, languageProject.getAnalysisLanguage().getAnyIcuRules());
		setColumnICURules(descriptionColumn, languageProject.getAnalysisLanguage().getAnyIcuRules());
	}

	private void addDataToTable() {
		// Add observable list data to the table
		graphemeNaturalClassTable.setItems(languageProject
				.getGraphemeNaturalClasses());
		int max = graphemeNaturalClassTable.getItems().size();
		if (max > 0) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					int iLastIndex = mainApp.getApplicationPreferences()
							.getLastCVGraphemeNaturalClassesViewItemUsed();
					iLastIndex = adjustIndexValue(iLastIndex, max);
					// select the last one used
					graphemeNaturalClassTable.requestFocus();
					graphemeNaturalClassTable.getSelectionModel().select(iLastIndex);
					graphemeNaturalClassTable.getFocusModel().focus(iLastIndex);
					graphemeNaturalClassTable.scrollTo(iLastIndex);
				}
			});
		}
	}

	public void setData(SHApproach shApproachData) {
		shApproach = shApproachData;
		languageProject = shApproach.getLanguageProject();
		cvApproach = languageProject.getCVApproach();
		setColumnICURules();
		setTextFieldColors();
		addDataToTable();
	}

	public void setData(ONCApproach oncApproachData) {
		oncApproach = oncApproachData;
		languageProject = oncApproach.getLanguageProject();
		cvApproach = languageProject.getCVApproach();
		setColumnICURules();
		setTextFieldColors();
		addDataToTable();
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
		GraphemeNaturalClass newNaturalClass = new GraphemeNaturalClass();
		cvApproach.getLanguageProject().getGraphemeNaturalClasses().add(newNaturalClass);
		handleInsertNewItem(cvApproach.getLanguageProject().getGraphemeNaturalClasses(), graphemeNaturalClassTable);
	}

	@Override
	void handleRemoveItem() {
		handleRemoveItem(cvApproach.getLanguageProject().getGraphemeNaturalClasses(), currentNaturalClass, graphemeNaturalClassTable);
	}

	@Override
	void handlePreviousItem() {
		handlePreviousItem(cvApproach.getLanguageProject().getGraphemeNaturalClasses(), currentNaturalClass, graphemeNaturalClassTable);
	}

	@Override
	void handleNextItem() {
		handleNextItem(cvApproach.getLanguageProject().getGraphemeNaturalClasses(), currentNaturalClass, graphemeNaturalClassTable);
	}

	@FXML
	void handleLaunchGNCChooser() {
		if (cvApproach == null) {
			cvApproach = languageProject.getCVApproach();
		}
		showGNCChooser();
		showGraphemeOrNaturalClassContent();
	}

	/**
	 * Opens a dialog to show the chooser.
	 */
	public void showGNCChooser() {
		try {
			Stage dialogStage = new Stage();
			String resource = "fxml/GraphemeNaturalClassChooser.fxml";
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, dialogStage,
					MainApp.kApplicationTitle, ApproachViewNavigator.class.getResource(resource),
					Constants.RESOURCE_LOCATION);
			GraphemeNaturalClassChooserController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setNaturalClass(currentNaturalClass);
			controller.setData(cvApproach);
			controller.initializeTableColumnWidths(mainApp.getApplicationPreferences());

			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return new TextField[] { nameField, descriptionField };
	}

	protected void handleCheckBoxSelectAll() {
		for (GraphemeNaturalClass nc : cvApproach.getLanguageProject().getGraphemeNaturalClasses()) {
			nc.setActive(true);
			forceTableRowToRedisplayPerActiveSetting(nc);
		}
	}

	protected void handleCheckBoxClearAll() {
		for (GraphemeNaturalClass nc : cvApproach.getLanguageProject().getGraphemeNaturalClasses()) {
			nc.setActive(false);
			forceTableRowToRedisplayPerActiveSetting(nc);
		}
	}

	protected void handleCheckBoxToggle() {
		for (GraphemeNaturalClass nc : cvApproach.getLanguageProject().getGraphemeNaturalClasses()) {
			if (nc.isActive()) {
				nc.setActive(false);
			} else {
				nc.setActive(true);
			}
			forceTableRowToRedisplayPerActiveSetting(nc);
		}
	}
}
