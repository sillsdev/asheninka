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
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.GraphemeNaturalClass;
import org.sil.syllableparser.model.GraphemeOrNaturalClass;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.cvapproach.CVApproach;

import javafx.beans.property.SimpleStringProperty;
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
public class GraphemeNaturalClassChooserController extends TableViewWithCheckBoxColumnController {

	protected final class AnalysisWrappingTableCell extends TableCell<GraphemeOrNaturalClass, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processAnalysisTableCell(this, text, item, empty);
		}
	}
	protected final class WrappingTableCell extends TableCell<GraphemeOrNaturalClass, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processTableCell(this, text, item, empty);
		}
	}

	@FXML
	private TableView<GraphemeOrNaturalClass> graphemeOrNaturalClassTable;
	@FXML
	private TableColumn<GraphemeOrNaturalClass, Boolean> checkBoxColumn;
	@FXML
	private TableColumn<GraphemeOrNaturalClass, String> graphemeOrNCColumn;
	@FXML
	private TableColumn<GraphemeOrNaturalClass, String> descriptionColumn;
	Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;
	private ApplicationPreferences preferences;

	private GraphemeOrNaturalClass currentGraphemeOrNaturalClass;
	private ObservableList<GraphemeOrNaturalClass> graphemesOrNaturalClasses = FXCollections
			.observableArrayList();
	private GraphemeNaturalClass naturalClass;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		super.setApproach(ApplicationPreferences.GRAPHEME_NATURAL_CLASS_CHOOSER);
		super.setTableView(graphemeOrNaturalClassTable);
		super.initialize(location, resources);

		// Initialize the table with the three columns.
		checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().checkedProperty());
		checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));
		checkBoxColumn.setEditable(true);
		checkBoxColumnHead.setOnAction((event) -> {
			handleCheckBoxColumnHead();
		});
		graphemeOrNCColumn.setCellValueFactory(cellData -> {
			if (cellData.getValue().isGrapheme()) {
				return cellData.getValue().graphemeOrNaturalClassProperty();
			} else {
				SimpleStringProperty sp = new SimpleStringProperty(Constants.NATURAL_CLASS_PREFIX
						+ cellData.getValue().graphemeOrNaturalClassProperty().getValue()
						+ Constants.NATURAL_CLASS_SUFFIX);
				return sp;
			}
		});
		descriptionColumn
		.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		graphemeOrNaturalClassTable.setEditable(true);
		// Custom rendering of the table cell.
		graphemeOrNCColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});

		initializeCheckBoxContextMenu(resources);

		graphemeOrNaturalClassTable.setOnKeyPressed(keyEvent -> {
			switch (keyEvent.getCode()) {
			case SPACE: {
				keyEvent.consume();
				GraphemeOrNaturalClass gnc = graphemeOrNaturalClassTable.getSelectionModel()
						.getSelectedItem();
				if (gnc != null) {
					gnc.setChecked(!gnc.isChecked());
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

	public void setData(CVApproach cvApproachData) {
		generateGraphemesAndNaturalClasses(cvApproachData);
		// Add observable list data to the table
		graphemeOrNaturalClassTable.setItems(graphemesOrNaturalClasses);
		if (graphemeOrNaturalClassTable.getItems().size() > 0) {
			// select one
			graphemeOrNaturalClassTable.requestFocus();
			graphemeOrNaturalClassTable.getSelectionModel().select(0);
			graphemeOrNaturalClassTable.getFocusModel().focus(0);
		}
	}

	public void generateGraphemesAndNaturalClasses(CVApproach cvApproachData) {
		cvApproach = cvApproachData;
		languageProject = cvApproach.getLanguageProject();

		for (Grapheme grapheme : languageProject.getGraphemes()) {
			Segment owningSegment = grapheme.getOwningSegment();
			if (owningSegment == null) {
				System.out.println("grapheme with null owning segment:" + grapheme.getForm());
				continue;
			}
			if (grapheme.isActive() && owningSegment.isActive()) {
				currentGraphemeOrNaturalClass = new GraphemeOrNaturalClass(grapheme.getForm(),
						grapheme.getDescription(), true, grapheme.getID(), true);
				graphemesOrNaturalClasses.add(currentGraphemeOrNaturalClass);
				setCheckedStatus(grapheme);
			}
		}
		for (GraphemeNaturalClass gNaturalClass : cvApproach.getLanguageProject()
				.getGraphemeNaturalClasses()) {
			if (gNaturalClass.isActive()) {
				if (gNaturalClass.getID() != naturalClass.getID()) {
					currentGraphemeOrNaturalClass = new GraphemeOrNaturalClass(
							gNaturalClass.getNCName(), gNaturalClass.getDescription(), false,
							gNaturalClass.getID(), true);
					graphemesOrNaturalClasses.add(currentGraphemeOrNaturalClass);
					setCheckedStatus(gNaturalClass);
				}
			}
		}
	}

	private void setCheckedStatus(SylParserObject sylParserObject) {
		if (SylParserObject.findIndexInListByUuid(naturalClass.getGraphemesOrNaturalClasses(),
				sylParserObject.getID()) > -1) {
			currentGraphemeOrNaturalClass.setChecked(true);
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
		naturalClass.getGraphemesOrNaturalClasses().clear();
		// find the grapheme or natural class with
		// graphemeOrNaturalClass.getUuid() and
		// add it to the natural class list
		// we use this method in order to guarantee we get the actual object and
		// not a copy
		for (GraphemeOrNaturalClass graphemeOrNaturalClass : graphemesOrNaturalClasses) {
			if (graphemeOrNaturalClass.isChecked()) {
				if (graphemeOrNaturalClass.isGrapheme()) {
					int i = Grapheme.findIndexInListByUuid(languageProject.getGraphemes(),
							graphemeOrNaturalClass.getUuid());
					naturalClass.getGraphemesOrNaturalClasses().add(
							languageProject.getGraphemes().get(i));
				} else {
					int i = GraphemeNaturalClass.findIndexInListByUuid(cvApproach
							.getLanguageProject().getGraphemeNaturalClasses(),
							graphemeOrNaturalClass.getUuid());
					naturalClass.getGraphemesOrNaturalClasses().add(
							cvApproach.getLanguageProject().getGraphemeNaturalClasses().get(i));
				}
			}
		}

		okClicked = true;
		handleCancel();
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		preferences.setLastWindowParameters(
				ApplicationPreferences.LAST_CV_GRAPHEME_OR_NATURAL_CLASS, dialogStage);
		dialogStage.close();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		preferences = mainApp.getApplicationPreferences();
		dialogStage = preferences.getLastWindowParameters(
				ApplicationPreferences.LAST_CV_GRAPHEME_OR_NATURAL_CLASS, dialogStage, 400., 400.);
	}

	/**
	 * Called when the user clicks help.
	 */
	@FXML
	private void handleHelp() {
		mainApp.showNotImplementedYet();
	}

	public GraphemeNaturalClass getNaturalClass() {
		return naturalClass;
	}

	public void setNaturalClass(GraphemeNaturalClass naturalClass) {
		this.naturalClass = naturalClass;
	}

	protected void handleCheckBoxSelectAll() {
		for (GraphemeOrNaturalClass graphemeOrNaturalClass : graphemesOrNaturalClasses) {
			graphemeOrNaturalClass.setChecked(true);
		}
	}

	protected void handleCheckBoxClearAll() {
		for (GraphemeOrNaturalClass graphemeOrNaturalClass : graphemesOrNaturalClasses) {
			graphemeOrNaturalClass.setChecked(false);
		}
	}

	protected void handleCheckBoxToggle() {
		for (GraphemeOrNaturalClass graphemeOrNaturalClass : graphemesOrNaturalClasses) {
			if (graphemeOrNaturalClass.isChecked()) {
				graphemeOrNaturalClass.setChecked(false);
			} else {
				graphemeOrNaturalClass.setChecked(true);
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
		// nothing to do
	}
}
