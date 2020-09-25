// Copyright (c) 2016-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.ApproachType;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.utility.view.ControllerUtilities;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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

public class CVNaturalClassesController extends SylParserBaseController implements Initializable {

	protected final class AnalysisWrappingTableCell extends TableCell<CVNaturalClass, String> {
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
				CVNaturalClass nc = (CVNaturalClass) this.getTableRow().getItem();
				if (nc != null && nc.isActive()) {
					text.setFill(Constants.ACTIVE);
				} else {
					text.setFill(Constants.INACTIVE);
				}
				text.setFont(languageProject.getAnalysisLanguage().getFont());
				setGraphic(text);
			}
		}
	}

	protected final class VernacularWrappingTableCell extends TableCell<CVNaturalClass, String> {
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
				CVNaturalClass nc = (CVNaturalClass) this.getTableRow().getItem();
				if (nc != null && nc.isActive()) {
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
	private TableView<CVNaturalClass> cvNaturalClassTable;
	@FXML
	private TableColumn<CVNaturalClass, String> nameColumn;
	@FXML
	private TableColumn<CVNaturalClass, String> segmentOrNaturalClassColumn;
	@FXML
	private TableColumn<CVNaturalClass, String> descriptionColumn;
	@FXML
	private TableColumn<CVNaturalClass, Boolean> checkBoxColumn;
	@FXML
	private CheckBox checkBoxColumnHead;

	@FXML
	private TextField nameField;
	@FXML
	private TextField segmentOrNaturalClassField;
	@FXML
	private TextField descriptionField;
	@FXML
	private FlowPane sncField;
	@FXML
	private TextFlow sncTextFlow;
	@FXML
	private Button sncButton;
	@FXML
	private CheckBox activeCheckBox;

	private CVNaturalClass currentNaturalClass;
	private ApproachType approachType = ApproachType.CV;

	public CVNaturalClassesController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		nameColumn.setCellValueFactory(cellData -> cellData.getValue().ncNameProperty());
		segmentOrNaturalClassColumn.setCellValueFactory(cellData -> cellData.getValue()
				.sncRepresentationProperty());
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

		// Custom rendering of the table cell.
		nameColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});
		segmentOrNaturalClassColumn.setCellFactory(column -> {
			return new VernacularWrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});

		makeColumnHeaderWrappable(nameColumn);
		makeColumnHeaderWrappable(segmentOrNaturalClassColumn);
		makeColumnHeaderWrappable(descriptionColumn);

		// Clear cv natural class details.
		showCVNaturalClassDetails(null);

		// Listen for selection changes and show the details when changed.
		cvNaturalClassTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showCVNaturalClassDetails(newValue));

		// Handle TextField text changes.
		nameField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentNaturalClass != null) {
				currentNaturalClass.setNCName(nameField.getText());
			}
			if (languageProject != null) {
				nameField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});
		// segmentOrNaturalClassField.textProperty().addListener(
		// (observable, oldValue, newValue) -> {
		// currentNaturalClass.setSNCRepresentation(segmentOrNaturalClassField.getText());
		// });
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
				showSegmentOrNaturalClassContent();
				forceTableRowToRedisplayPerActiveSetting(currentNaturalClass);
			}
			displayFieldsPerActiveSetting(currentNaturalClass);
		});

		// Use of Enter move focus to next item.
		nameField.setOnAction((event) -> {
			descriptionField.requestFocus();
		});
		descriptionField.setOnAction((event) -> {
			segmentOrNaturalClassField.requestFocus();
		});

		nameField.requestFocus();

	}

	public void displayFieldsPerActiveSetting(CVNaturalClass nc) {
		boolean fIsActive;
		if (nc == null) {
			fIsActive = false;
		} else {
			fIsActive = nc.isActive();
		}
		nameField.setDisable(!fIsActive);
		sncTextFlow.setDisable(!fIsActive);
		sncButton.setDisable(!fIsActive);
		descriptionField.setDisable(!fIsActive);
	}

	private void forceTableRowToRedisplayPerActiveSetting(CVNaturalClass nc) {
		// we need to make the content of the row cells change in order for
		// the cell factory to fire.
		// We do this by getting the value, blanking it, and then restoring it.
		String temp = nc.getNCName();
		nc.setNCName("");
		nc.setNCName(temp);
		temp = nc.getSNCRepresentation();
		nc.setSNCRepresentation("");
		nc.setSNCRepresentation(temp);
		temp = nc.getDescription();
		nc.setDescription("");
		nc.setDescription(temp);
	}

	@Override
	public void setViewItemUsed(int value) {
		int max = cvNaturalClassTable.getItems().size();
		value = adjustIndexValue(value, max);
		cvNaturalClassTable.getSelectionModel().clearAndSelect(value);
	}

	/**
	 * Fills all text fields to show details about the CV natural class. If the
	 * specified segment is null, all text fields are cleared.
	 *
	 * @param naturalClass
	 *            the segment or null
	 */
	private void showCVNaturalClassDetails(CVNaturalClass naturalClass) {
		currentNaturalClass = naturalClass;
		if (naturalClass != null) {
			// Fill the text fields with info from the person object.
			nameField.setText(naturalClass.getNCName());
			descriptionField.setText(naturalClass.getDescription());
			activeCheckBox.setSelected(naturalClass.isActive());
			showSegmentOrNaturalClassContent();
		} else {
			// Segment is null, remove all the text.
			nameField.setText("");
			descriptionField.setText("");
			sncTextFlow.getChildren().clear();
		}
		displayFieldsPerActiveSetting(naturalClass);

		if (naturalClass != null) {
			int iCurrentIndex = cvNaturalClassTable.getItems().indexOf(currentNaturalClass);
			this.mainApp.updateStatusBarNumberOfItems((iCurrentIndex + 1) + "/"
					+ cvNaturalClassTable.getItems().size() + " ");
			// remember the selection
			switch (approachType) {
			case ONSET_NUCLEUS_CODA:
				mainApp.getApplicationPreferences().setLastONCCVNaturalClassesViewItemUsed(
						iCurrentIndex);
				break;
			default:
				mainApp.getApplicationPreferences().setLastCVNaturalClassesViewItemUsed(
						iCurrentIndex);
				break;
			}
		}
	}

	private void showSegmentOrNaturalClassContent() {
		StringBuilder sb = new StringBuilder();
		sncTextFlow.getChildren().clear();
		int i = 1;
		int iCount = currentNaturalClass.getSegmentsOrNaturalClasses().size();
		for (SylParserObject snc : currentNaturalClass.getSegmentsOrNaturalClasses()) {
			Text t;
			String s;
			if (snc instanceof Segment) {
				s = ((Segment) snc).getSegment();
				t = new Text(s);
				t.setFont(languageProject.getVernacularLanguage().getFont());
				sb.append(s);
			} else if (snc instanceof CVNaturalClass) {
				s = ((CVNaturalClass) snc).getNCName();
				s = Constants.NATURAL_CLASS_PREFIX + s + Constants.NATURAL_CLASS_SUFFIX;
				t = new Text(s);
				t.setFont(languageProject.getAnalysisLanguage().getFont());
				sb.append(s);
			} else {
				s = "ERROR!";
				t = new Text(s);
				sb.append(s);
			}
			if (snc.isActive() && activeCheckBox.isSelected()) {
				t.setFill(Constants.ACTIVE);
			} else {
				t.setFill(Constants.INACTIVE);
			}
			Text tBar = new Text(" | ");
			tBar.setStyle("-fx-stroke: lightgrey;");
			sncTextFlow.getChildren().addAll(t, tBar);
			if (i++ < iCount) {
				sb.append(", ");
			}
		}
		currentNaturalClass.setSNCRepresentation(sb.toString());
	}

	public void setNaturalClass(CVNaturalClass naturalClass) {
		nameField.setText(naturalClass.getNCName());
		descriptionField.setText(naturalClass.getDescription());
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * @param approachType = which approach invoked this
	 * @param cvApproachController = CV data
	 */
	public void setData(CVApproach cvApproachData, ApproachType approachType) {
		cvApproach = cvApproachData;
		languageProject = cvApproach.getLanguageProject();
		this.approachType = approachType;
		setColumnICURules(nameColumn, languageProject.getAnalysisLanguage().getIcuRules());
		setColumnICURules(segmentOrNaturalClassColumn, languageProject.getVernacularLanguage().getIcuRules());
		setColumnICURules(descriptionColumn, languageProject.getAnalysisLanguage().getIcuRules());

		// Add observable list data to the table
		cvNaturalClassTable.setItems(cvApproachData.getCVNaturalClasses());
		int max = cvNaturalClassTable.getItems().size();
		if (max > 0) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					// retrieve selection
					int iLastIndex = 0;
					switch (approachType) {
					case ONSET_NUCLEUS_CODA:
						iLastIndex = mainApp.getApplicationPreferences()
								.getLastONCCVNaturalClassesViewItemUsed();
						break;
					default:
						iLastIndex = mainApp.getApplicationPreferences()
								.getLastCVNaturalClassesViewItemUsed();
						break;
					}
					iLastIndex = adjustIndexValue(iLastIndex, max);
					// select the last one used
					cvNaturalClassTable.requestFocus();
					cvNaturalClassTable.getSelectionModel().select(iLastIndex);
					cvNaturalClassTable.getFocusModel().focus(iLastIndex);
					cvNaturalClassTable.scrollTo(iLastIndex);
				}
			});
		}
	}

	@Override
	void handleInsertNewItem() {
		CVNaturalClass newNaturalClass = new CVNaturalClass();
		cvApproach.getCVNaturalClasses().add(newNaturalClass);
		handleInsertNewItem(cvApproach.getCVNaturalClasses(), cvNaturalClassTable);
	}

	@Override
	void handleRemoveItem() {
		handleRemoveItem(cvApproach.getCVNaturalClasses(), currentNaturalClass, cvNaturalClassTable);
	}

	@Override
	void handlePreviousItem() {
		handlePreviousItem(cvApproach.getCVNaturalClasses(), currentNaturalClass, cvNaturalClassTable);
	}

	@Override
	void handleNextItem() {
		handleNextItem(cvApproach.getCVNaturalClasses(), currentNaturalClass, cvNaturalClassTable);
	}

	@FXML
	void handleLaunchSNCChooser() {
		showSNCChooser();
		showSegmentOrNaturalClassContent();
	}

	/**
	 * Opens a dialog to show the chooser.
	 */
	public void showSNCChooser() {
		try {
			Stage dialogStage = new Stage();
			String resource = "fxml/CVSegmentNaturalClassChooser.fxml";
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, dialogStage,
					MainApp.kApplicationTitle, ApproachViewNavigator.class.getResource(resource),
					Constants.RESOURCE_LOCATION);
			CVSegmentNaturalClassChooserController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setNaturalClass(currentNaturalClass);
			controller.setData(cvApproach);

			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return new TextField[] { nameField, descriptionField };
	}

	protected void handleCheckBoxSelectAll() {
		for (CVNaturalClass nc : cvApproach.getCVNaturalClasses()) {
			nc.setActive(true);
			forceTableRowToRedisplayPerActiveSetting(nc);
		}
	}

	protected void handleCheckBoxClearAll() {
		for (CVNaturalClass nc : cvApproach.getCVNaturalClasses()) {
			nc.setActive(false);
			forceTableRowToRedisplayPerActiveSetting(nc);
		}
	}

	protected void handleCheckBoxToggle() {
		for (CVNaturalClass nc : cvApproach.getCVNaturalClasses()) {
			if (nc.isActive()) {
				nc.setActive(false);
			} else {
				nc.setActive(true);
			}
			forceTableRowToRedisplayPerActiveSetting(nc);
		}
	}

}
