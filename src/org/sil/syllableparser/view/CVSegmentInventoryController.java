// Copyright (c) 2016-2019 SIL International
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.ApproachType;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.utility.view.ControllerUtilities;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * @author Andy Black
 *
 */

public class CVSegmentInventoryController extends CheckBoxColumnController implements Initializable {

	// TODO: how can we make the next four classes be more generic so we don't
	// have to have
	// one for Segment and one for Grapheme? (And one for vernacular vs
	// anaysis?)
	protected final class AnalysisWrappingTableCellGrapheme extends TableCell<Grapheme, String> {
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
				SylParserObject obj = (SylParserObject) this.getTableRow().getItem();
				if (obj != null && obj.isActive()) {
					text.setFill(Constants.ACTIVE);
				} else {
					text.setFill(Constants.INACTIVE);
				}
				text.setFont(languageProject.getAnalysisLanguage().getFont());
				setGraphic(text);
			}
		}
	}

	protected final class VernacularWrappingTableCellGrapheme extends TableCell<Grapheme, String> {
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
				SylParserObject obj = (SylParserObject) this.getTableRow().getItem();
				if (obj != null && obj.isActive()) {
					text.setFill(Constants.ACTIVE);
				} else {
					text.setFill(Constants.INACTIVE);
				}
				text.setFont(languageProject.getVernacularLanguage().getFont());
				setGraphic(text);
			}
		}
	}

	protected final class AnalysisWrappingTableCellSegment extends TableCell<Segment, String> {
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
				SylParserObject obj = (SylParserObject) this.getTableRow().getItem();
				if (obj != null && obj.isActive()) {
					text.setFill(Constants.ACTIVE);
				} else {
					text.setFill(Constants.INACTIVE);
				}
				text.setFont(languageProject.getAnalysisLanguage().getFont());
				setGraphic(text);
			}
		}
	}

	protected final class VernacularWrappingTableCellSegment extends TableCell<Segment, String> {
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
				SylParserObject obj = (SylParserObject) this.getTableRow().getItem();
				if (obj != null && obj.isActive()) {
					text.setFill(Constants.ACTIVE);
				} else {
					text.setFill(Constants.INACTIVE);
				}
				text.setFont(languageProject.getVernacularLanguage().getFont());
				setGraphic(text);
			}
		}
	}

	// button table cell code base on code from
	// http://java-buddy.blogspot.com/2014/08/javafx-get-row-data-from-tableview.html
	// accessed on September 27 2017
	// Define the button cell
	protected class ButtonCell extends TableCell<Grapheme, Boolean> {
		final Button cellButton = new Button("...");

		ButtonCell() {
			cellButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent t) {
					int selectedIndex = getTableRow().getIndex();
					Grapheme selectedGrapheme = (Grapheme) graphemesTable.getItems().get(
							selectedIndex);
					// show environments chooser for this grapheme
					// Alert alert = new Alert(AlertType.INFORMATION);
					// alert.setTitle("Information Dialog");
					// alert.setHeaderText("Look, an Information Dialog");
					// alert.setContentText("Selected grapheme: " +
					// selectedGrapheme.getForm());
					//
					// alert.showAndWait();
					showEnvironmentsChooser(selectedGrapheme);
				}
			});
		}

		// Display button if the row is not empty
		@Override
		protected void updateItem(Boolean t, boolean empty) {
			super.updateItem(t, empty);
			if (!empty) {
				setGraphic(cellButton);
			}
		}
	}

	@FXML
	protected TableView<Segment> cvSegmentTable;
	@FXML
	protected TableColumn<Segment, String> segmentColumn;
	@FXML
	protected TableColumn<Segment, String> graphemesColumn;
	@FXML
	protected TableColumn<Segment, String> descriptionColumn;
	@FXML
	protected TableView<Grapheme> graphemesTable;
	@FXML
	protected TableColumn<Grapheme, String> graphemeColumn;
	@FXML
	protected TableColumn<Grapheme, String> environmentsColumn;
	// @FXML
	// private TableColumn<Grapheme, String> graphemeDescriptionColumn;
	@FXML
	protected TableColumn<Grapheme, Boolean> checkBoxColumn;
	@FXML
	protected CheckBox checkBoxColumnHead;
	@FXML
	protected TableColumn<Grapheme, Boolean> environmentsButtonColumn;

	@FXML
	protected TextField segmentField;
	@FXML
	protected TextField graphemesField;
	@FXML
	protected TextField descriptionField;
	@FXML
	protected Button environmentsButton;
	// @FXML
	// private TextField graphemeDescriptionField;
	@FXML
	protected CheckBox activeCheckBox;

	protected Segment currentSegment;

	public CVSegmentInventoryController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Segment table
		segmentColumn.setCellValueFactory(cellData -> cellData.getValue().segmentProperty());
		graphemesColumn.setCellValueFactory(cellData -> cellData.getValue().graphemesProperty());
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		// Grapheme table
		graphemeColumn.setCellValueFactory(cellData -> cellData.getValue().formProperty());
		environmentsColumn.setCellValueFactory(cellData -> {
			cellData.getValue().recalulateEnvsRepresentation();
			return cellData.getValue().envsRepresentationProperty();
		});
		// graphemeDescriptionColumn
		// .setCellValueFactory(cellData ->
		// cellData.getValue().descriptionProperty());

		// Custom rendering of the table cell.
		segmentColumn.setCellFactory(column -> {
			return new VernacularWrappingTableCellSegment();
		});
		graphemesColumn.setCellFactory(column -> {
			return new VernacularWrappingTableCellSegment();
		});
		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCellSegment();
		});

		checkBoxColumn.setCellValueFactory(cellData -> {
			Grapheme graph = cellData.getValue();
			BooleanProperty bp = graph.checkedProperty();
			cellData.getValue().setActive(bp.get());
			forceTableRowToRedisplayPerActiveSetting(graph);
			redrawGraphemesField();
			return bp;
		});
		checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));
		checkBoxColumn.setEditable(true);
		checkBoxColumnHead.setOnAction((event) -> {
			handleCheckBoxColumnHead();
		});
		initializeCheckBoxContextMenu(resources);
		graphemeColumn.setCellFactory(column -> {
			return new VernacularWrappingTableCellGrapheme();
		});
		environmentsColumn.setCellFactory(column -> {
			return new VernacularWrappingTableCellGrapheme();
		});

		// button table cell code base on code from
		// http://java-buddy.blogspot.com/2014/08/javafx-get-row-data-from-tableview.html
		// accessed on September 27 2017
		environmentsButtonColumn
				.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Grapheme, Boolean>, ObservableValue<Boolean>>() {
					@Override
					public ObservableValue<Boolean> call(
							TableColumn.CellDataFeatures<Grapheme, Boolean> p) {
						return new SimpleBooleanProperty(p.getValue() != null);
					}
				});
		environmentsButtonColumn
				.setCellFactory(new Callback<TableColumn<Grapheme, Boolean>, TableCell<Grapheme, Boolean>>() {
					@Override
					public TableCell<Grapheme, Boolean> call(TableColumn<Grapheme, Boolean> p) {
						return new ButtonCell();
					}
				});
		environmentsButtonColumn.setEditable(true);

		graphemesTable.setEditable(true);

		makeColumnHeaderWrappable(segmentColumn);
		makeColumnHeaderWrappable(graphemesColumn);
		makeColumnHeaderWrappable(descriptionColumn);

		// Clear cv segment details.
		showCVSegmentDetails(null);

		// Listen for selection changes and show the details when changed.
		cvSegmentTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showCVSegmentDetails(newValue));

		// Following avoids getting the following message:
		// com.sun.javafx.scene.control.skin.VirtualFlow addTrailingCells
		// INFO: index exceeds maxCellCount.
		cvSegmentTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

		// Handle TextField text changes.
		segmentField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentSegment != null) {
				currentSegment.setSegment(segmentField.getText());
			}
			if (languageProject != null) {
				segmentField.setFont(languageProject.getVernacularLanguage().getFont());
			}
		});
		graphemesField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentSegment != null) {
				currentSegment.setGraphemes(graphemesField.getText());
				// following is temporary
				currentSegment.setGraphs(currentSegment.getGraphs());
			}
			if (languageProject != null) {
				graphemesField.setFont(languageProject.getVernacularLanguage().getFont());
			}
		});
		graphemesField.focusedProperty().addListener((observable, wasFocused, isNowFocused) -> {
			if (!isNowFocused && currentSegment != null) {
				currentSegment.updateGraphemes();
				graphemesTable.refresh();
			}
		});
		descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentSegment != null) {
				currentSegment.setDescription(descriptionField.getText());
			}
			if (languageProject != null) {
				descriptionField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});

		activeCheckBox.setOnAction((event) -> {
			if (currentSegment != null) {
				currentSegment.setActive(activeCheckBox.isSelected());
				forceTableRowToRedisplayPerActiveSetting(currentSegment);
				displayFieldsPerActiveSetting(currentSegment);
			}
		});

		// Use of Enter move focus to next item.
		segmentField.setOnAction((event) -> {
			graphemesField.requestFocus();
		});
		graphemesField.setOnAction((event) -> {
			descriptionField.requestFocus();
		});

		segmentField.requestFocus();
	}

	protected void forceTableRowToRedisplayPerActiveSetting(Segment segment) {
		// we need to make the content of the row cells change in order for
		// the cell factory to fire.
		// We do this by getting the value, blanking it, and then restoring it.
		String temp = segment.getSegment();
		segment.setSegment("");
		segment.setSegment(temp);
		temp = segment.getGraphemes();
		segment.setGraphemes("");
		segment.setGraphemes(temp);
		temp = segment.getDescription();
		segment.setDescription("");
		segment.setDescription(temp);
	}

	protected void forceTableRowToRedisplayPerActiveSetting(Grapheme grapheme) {
		// we need to make the content of the row cells change in order for
		// the cell factory to fire.
		// We do this by getting the value, blanking it, and then restoring it.
		String temp = grapheme.getForm();
		grapheme.setForm("");
		grapheme.setForm(temp);
		temp = grapheme.getEnvsRepresentation();
		grapheme.setEnvsRepresentation("");
		grapheme.setEnvsRepresentation(temp);
		grapheme.recalulateEnvsRepresentation();
	}

	/**
	 * Fills all text fields to show details about the CV segment. If the
	 * specified segment is null, all text fields are cleared.
	 * 
	 * @param segment
	 *            the segment or null
	 */
	protected void showCVSegmentDetails(Segment segment) {
		currentSegment = segment;
		if (segment != null) {
			// Fill the text fields with info from the segment object.
			segmentField.setText(segment.getSegment());
			graphemesField.setText(segment.getGraphemes());
			descriptionField.setText(segment.getDescription());
			activeCheckBox.setSelected(segment.isActive());
			displayFieldsPerActiveSetting(segment);

			// Add observable list data to the graphemes table and force it to
			// redisplay
			graphemesTable.setItems(currentSegment.getGraphs());
			graphemesTable.refresh();
		} else {
			// Segment is null, remove all the text.
			segmentField.setText("");
			graphemesField.setText("");
			descriptionField.setText("");
			activeCheckBox.setSelected(true);
		}

		if (segment != null) {
			int currentSegmentNumber = cvSegmentTable.getItems().indexOf(currentSegment);
			this.mainApp.updateStatusBarNumberOfItems((currentSegmentNumber + 1) + "/"
					+ cvSegmentTable.getItems().size() + " ");

			String sApproach = this.rootController.getApproachUsed();
			if (sApproach.equals(ApproachType.CV.name())) {
				mainApp.getApplicationPreferences().setLastCVSegmentInventoryViewItemUsed(
						currentSegmentNumber);
			} else if (sApproach.equals(ApproachType.SONORITY_HIERARCHY.name())) {
				mainApp.getApplicationPreferences().setLastSHSegmentInventoryViewItemUsed(
						currentSegmentNumber);
			} else if (sApproach.equals(ApproachType.ONSET_NUCLEUS_CODA.name())) {
				mainApp.getApplicationPreferences().setLastONCSegmentInventoryViewItemUsed(
						currentSegmentNumber);
			}
		}
	}

	@Override
	public void setViewItemUsed(int value) {
		int max = cvSegmentTable.getItems().size();
		value = adjustIndexValue(value, max);
		cvSegmentTable.getSelectionModel().clearAndSelect(value);
	}

	public void displayFieldsPerActiveSetting(Segment segment) {
		segmentField.setDisable(!segment.isActive());
		graphemesField.setDisable(!segment.isActive());
		descriptionField.setDisable(!segment.isActive());
		graphemesTable.setDisable(!segment.isActive());
	}

	public void setSegment(Segment segment) {
		segmentField.setText(segment.getSegment());
		graphemesField.setText(segment.getGraphemes());
		descriptionField.setText(segment.getDescription());
	}

	public void setData(CVApproach cvApproachData) {
		cvApproach = cvApproachData;
		languageProject = cvApproach.getLanguageProject();
		populateSegmentTable(ApproachType.CV);
	}

	public void setData(SHApproach shApproachData) {
		shApproach = shApproachData;
		languageProject = shApproach.getLanguageProject();
		populateSegmentTable(ApproachType.SONORITY_HIERARCHY);
	}

	public void setData(ONCApproach oncApproachData) {
		oncApproach = oncApproachData;
		languageProject = oncApproach.getLanguageProject();
		populateSegmentTable(ApproachType.ONSET_NUCLEUS_CODA);
	}

	protected void populateSegmentTable(ApproachType appType) {
		cvSegmentTable.setItems(languageProject.getSegmentInventory());
		int max = cvSegmentTable.getItems().size();
		if (max > 0) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					int iLastIndex = 0;
					if (appType == ApproachType.CV) {
						iLastIndex = mainApp.getApplicationPreferences()
								.getLastCVSegmentInventoryViewItemUsed();
					} else if (appType == ApproachType.SONORITY_HIERARCHY) {
						iLastIndex = mainApp.getApplicationPreferences()
								.getLastSHSegmentInventoryViewItemUsed();
					} else if (appType == ApproachType.ONSET_NUCLEUS_CODA) {
						iLastIndex = mainApp.getApplicationPreferences()
								.getLastONCSegmentInventoryViewItemUsed();
					}
					iLastIndex = adjustIndexValue(iLastIndex, max);
					cvSegmentTable.requestFocus();
					cvSegmentTable.getSelectionModel().select(iLastIndex);
					cvSegmentTable.getFocusModel().focus(iLastIndex);
					cvSegmentTable.scrollTo(iLastIndex);
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleInsertNewItem()
	 */
	@Override
	protected void handleInsertNewItem() {
		Segment newSegment = new Segment();
		languageProject.getSegmentInventory().add(newSegment);
		int i = languageProject.getSegmentInventory().size() - 1;
		cvSegmentTable.requestFocus();
		cvSegmentTable.getSelectionModel().select(i);
		cvSegmentTable.getFocusModel().focus(i);
		cvSegmentTable.scrollTo(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.view.ApproachController#handleRemoveItem()
	 */
	@Override
	protected void handleRemoveItem() {
		int i = languageProject.getSegmentInventory().indexOf(currentSegment);
		currentSegment = null;
		if (i >= 0) {
			languageProject.getSegmentInventory().remove(i);
		}
		cvSegmentTable.refresh();
	}

	protected void handleCheckBoxSelectAll() {
		for (Grapheme grapheme : currentSegment.getGraphs()) {
			grapheme.setActive(true);
			forceTableRowToRedisplayPerActiveSetting(grapheme);
		}
	}

	protected void handleCheckBoxClearAll() {
		for (Grapheme grapheme : currentSegment.getGraphs()) {
			grapheme.setActive(false);
			forceTableRowToRedisplayPerActiveSetting(grapheme);
		}
	}

	protected void handleCheckBoxToggle() {
		for (Grapheme grapheme : currentSegment.getGraphs()) {
			if (grapheme.isActive()) {
				grapheme.setActive(false);
			} else {
				grapheme.setActive(true);
			}
			forceTableRowToRedisplayPerActiveSetting(grapheme);
		}
	}

	protected void redrawGraphemesField() {
		if (currentSegment != null) {
			String sGraphemes = currentSegment.getGraphs().stream()
					.filter(graph -> graph.isChecked()).map(Grapheme::getForm)
					.collect(Collectors.joining(" "));
			graphemesField.setText(sGraphemes);
		}

	}

	public void showEnvironmentsChooser(Grapheme grapheme) {
		try {
			Stage dialogStage = new Stage();
			String resource = "fxml/EnvironmentChooser.fxml";
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, dialogStage,
					MainApp.kApplicationTitle, ApproachViewNavigator.class.getResource(resource),
					Constants.RESOURCE_LOCATION);
			EnvironmentChooserController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setGrapheme(grapheme);
			controller.setData(cvApproach);

			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	protected TextField[] createTextFields() {
		return new TextField[] { segmentField, graphemesField, descriptionField };
	}
}
