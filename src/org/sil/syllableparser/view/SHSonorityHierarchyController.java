// Copyright (c) 2018 SIL International
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

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.SyllableParserException;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;
import org.sil.utility.view.ControllerUtilities;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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

public class SHSonorityHierarchyController extends SylParserBaseController implements Initializable {

	protected final class WrappingTableCell extends TableCell<SHNaturalClass, String> {
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
				SHNaturalClass syllablePattern = (SHNaturalClass) this.getTableRow()
						.getItem();
				if (syllablePattern != null && syllablePattern.isActive()) {
					text.setFill(Constants.ACTIVE);
				} else {
					text.setFill(Constants.INACTIVE);
				}
				text.setFont(languageProject.getAnalysisLanguage().getFont());
				setGraphic(text);
			}
		}
	}

	@FXML
	private TableView<SHNaturalClass> shSonorityHierarchyTable;
	@FXML
	private TableColumn<SHNaturalClass, String> nameColumn;
	@FXML
	private TableColumn<SHNaturalClass, String> naturalClassColumn;
	@FXML
	private TableColumn<SHNaturalClass, String> descriptionColumn;
	@FXML
	private TableColumn<SHNaturalClass, Boolean> checkBoxColumn;
	@FXML
	private CheckBox checkBoxColumnHead;

	@FXML
	private TextField nameField;
	@FXML
	private TextField naturalClassesField;
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
	// @FXML
	// private TextField sncRepresentationField;

	private SHNaturalClass currentNaturalClass;

	public SHSonorityHierarchyController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		// Initialize the button icons
		tooltipMoveUp = ControllerUtilities.createToolbarButtonWithImage("UpArrow.png",
				buttonMoveUp, tooltipMoveUp, bundle.getString("sh.view.sonorityhierarchy.up"),
				Constants.RESOURCE_SOURCE_LOCATION);
		tooltipMoveDown = ControllerUtilities.createToolbarButtonWithImage("DownArrow.png",
				buttonMoveDown, tooltipMoveDown, bundle.getString("sh.view.sonorityhierarchy.down"),
				Constants.RESOURCE_SOURCE_LOCATION);

		// checkBoxColumn.setCellValueFactory(cellData ->
		// cellData.getValue().activeCheckBoxProperty());
		// checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));
		// checkBoxColumn.setEditable(true);
		// checkBoxColumnHead.setOnAction((event) -> {
		// handleCheckBoxColumnHead();
		// });
		// initializeCheckBoxContextMenu(resources);

		nameColumn.setCellValueFactory(cellData -> cellData.getValue().ncNameProperty());
		naturalClassColumn.setCellValueFactory(cellData -> cellData.getValue()
				.segmentsRepresentationProperty());
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

		// Custom rendering of the table cell.
		nameColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		naturalClassColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new WrappingTableCell();
		});

		makeColumnHeaderWrappable(nameColumn);
		makeColumnHeaderWrappable(naturalClassColumn);
		makeColumnHeaderWrappable(descriptionColumn);

		// Since syllable patterns are sorted manually, we do not
		// want the user to be able to click on a column header and sort it
		nameColumn.setSortable(false);
		naturalClassColumn.setSortable(false);
		descriptionColumn.setSortable(false);

		// Clear cv syllable pattern details.
		showSHNaturalClassDetails(null);

		// Listen for selection changes and show the details when changed.
		shSonorityHierarchyTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showSHNaturalClassDetails(newValue));

		// Handle TextField text changes.
		nameField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentNaturalClass != null) {
				currentNaturalClass.setNCName(nameField.getText());
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
				showNaturalClassesContent();
				forceTableRowToRedisplayPerActiveSetting(currentNaturalClass);
			}
			displayFieldsPerActiveSetting(currentNaturalClass);
		});

		// Use of Enter move focus to next item.
		nameField.setOnAction((event) -> {
			descriptionField.requestFocus();
		});
		descriptionField.setOnAction((event) -> {
			naturalClassesField.requestFocus();
		});

		nameField.requestFocus();

	}

	public void displayFieldsPerActiveSetting(SHNaturalClass syllablePattern) {
		boolean fIsActive;
		if (syllablePattern == null) {
			fIsActive = false;
		} else {
			fIsActive = syllablePattern.isActive();
		}
		nameField.setDisable(!fIsActive);
		ncsTextFlow.setDisable(!fIsActive);
		ncsButton.setDisable(!fIsActive);
		descriptionField.setDisable(!fIsActive);
	}

	private void forceTableRowToRedisplayPerActiveSetting(SHNaturalClass naturalCLass) {
		// we need to make the content of the row cells change in order for
		// the cell factory to fire.
		// We do this by getting the value, blanking it, and then restoring it.
		String temp = naturalCLass.getNCName();
		naturalCLass.setNCName("");
		naturalCLass.setNCName(temp);
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
	 * @param syllablePattern
	 *            the segment or null
	 */
	private void showSHNaturalClassDetails(SHNaturalClass syllablePattern) {
		currentNaturalClass = syllablePattern;
		if (syllablePattern != null) {
			// Fill the text fields with info from the person object.
			nameField.setText(syllablePattern.getNCName());
			descriptionField.setText(syllablePattern.getDescription());
			activeCheckBox.setSelected(syllablePattern.isActive());
			showNaturalClassesContent();
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
		displayFieldsPerActiveSetting(syllablePattern);

		if (syllablePattern != null) {
			int currentItem = shSonorityHierarchyTable.getItems().indexOf(currentNaturalClass);
			this.mainApp.updateStatusBarNumberOfItems((currentItem + 1) + "/"
					+ shSonorityHierarchyTable.getItems().size() + " ");
			mainApp.getApplicationPreferences().setLastSHSonorityHierarchyViewItemUsed(currentItem);
		}
	}

	@Override
	public void setViewItemUsed(int value) {
		int max = shSonorityHierarchyTable.getItems().size();
		value = adjustIndexValue(value, max);
		shSonorityHierarchyTable.getSelectionModel().clearAndSelect(value);
	}

	protected void setUpDownButtonDisabled() {
		int iThis = shApproach.getSHSonorityHierarchy().indexOf(currentNaturalClass) + 1;
		int iSize = shApproach.getSHSonorityHierarchy().size();
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

	private void showNaturalClassesContent() {
		// TODO: can we do this with lambdas?
		StringBuilder sb = new StringBuilder();
		ncsTextFlow.getChildren().clear();
		int i = 1;
		int iCount = currentNaturalClass.getSegments().size();
		for (SylParserObject spo : currentNaturalClass.getSegments()) {
			Segment nc = (Segment) spo;
			if (nc != null) {
				addNameToContent(sb, nc.getSegment(), spo.isActive());
				if (i++ < iCount) {
					sb.append(", ");
				}
			}
		}
		currentNaturalClass.setSegmentsRepresentation(sb.toString());
	}

	protected void addNameToContent(StringBuilder sb, String sName, boolean isActive) {
		Text t = new Text(sName);
		t.setFont(languageProject.getAnalysisLanguage().getFont());
		if (isActive && activeCheckBox.isSelected()) {
			t.setFill(Constants.ACTIVE);
		} else {
			t.setFill(Constants.INACTIVE);
		}
		Text tBar = new Text(" | ");
		tBar.setStyle("-fx-stroke: lightgrey;");
		ncsTextFlow.getChildren().addAll(t, tBar);
		sb.append(sName);
	}

	public void setSyllablePattern(SHNaturalClass syllablePattern) {
		nameField.setText(syllablePattern.getNCName());
		descriptionField.setText(syllablePattern.getDescription());
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 *
	 * @param cvApproachController
	 */
	public void setData(SHApproach shApproachData) {
		shApproach = shApproachData;
		languageProject = shApproach.getLanguageProject();

		// Add observable list data to the table
		shSonorityHierarchyTable.setItems(shApproachData.getSHSonorityHierarchy());
		int max = shSonorityHierarchyTable.getItems().size();
		if (max > 0) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					int iLastIndex = mainApp.getApplicationPreferences()
							.getLastSHSonorityHierarchyViewItemUsed();
					iLastIndex = adjustIndexValue(iLastIndex, max);
					shSonorityHierarchyTable.requestFocus();
					shSonorityHierarchyTable.getSelectionModel().select(iLastIndex);
					shSonorityHierarchyTable.getFocusModel().focus(iLastIndex);
					// want to do following only if the selected item is not
					// visible
					// SHNaturalClassTable.isVisible();
					shSonorityHierarchyTable.scrollTo(iLastIndex);
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
	void handleInsertNewItem() {
		SHNaturalClass newNaturalCLass = new SHNaturalClass();
		shApproach.getSHSonorityHierarchy().add(newNaturalCLass);
		int i = shApproach.getSHSonorityHierarchy().size() - 1;
		shSonorityHierarchyTable.requestFocus();
		shSonorityHierarchyTable.getSelectionModel().select(i);
		shSonorityHierarchyTable.getFocusModel().focus(i);
		shSonorityHierarchyTable.scrollTo(i);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.sil.syllableparser.view.ApproachController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		int i = shApproach.getSHSonorityHierarchy().indexOf(currentNaturalClass);
		currentNaturalClass = null;
		if (i >= 0) {
			shApproach.getSHSonorityHierarchy().remove(i);
			int max = shSonorityHierarchyTable.getItems().size();
			i = adjustIndexValue(i, max);
			// select the last one used
			shSonorityHierarchyTable.requestFocus();
			shSonorityHierarchyTable.getSelectionModel().select(i);
			shSonorityHierarchyTable.getFocusModel().focus(i);
			shSonorityHierarchyTable.scrollTo(i);
		}
		shSonorityHierarchyTable.refresh();
	}

	@FXML
	void handleLaunchSegmentChooser() {
		showSegmentChooser();
		showNaturalClassesContent();
	}

	/**
	 * Opens a dialog to show and set segments
	 */
	public void showSegmentChooser() {
		try {
			// Load the fxml file and create a new stage for the popup.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ApproachViewNavigator.class
					.getResource("fxml/SHSegmentChooser.fxml"));
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

			SHSegmentChooserController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setNaturalClass(currentNaturalClass);
			controller.setData(shApproach);

			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void handleMoveDown() {
		int i = shApproach.getSHSonorityHierarchy().indexOf(currentNaturalClass);
		if ((i + 1) < shApproach.getSHSonorityHierarchy().size()) {
			Collections.swap(shApproach.getSHSonorityHierarchy(), i, i + 1);
		}
	}

	@FXML
	void handleMoveUp() {
		int i = shApproach.getSHSonorityHierarchy().indexOf(currentNaturalClass);
		if (i > 0) {
			Collections.swap(shApproach.getSHSonorityHierarchy(), i, i - 1);
		}
	}

	protected void handleCheckBoxSelectAll() {
		for (SHNaturalClass syllablePattern : shApproach.getSHSonorityHierarchy()) {
			syllablePattern.setActive(true);
			forceTableRowToRedisplayPerActiveSetting(syllablePattern);
		}
	}

	protected void handleCheckBoxClearAll() {
		for (SHNaturalClass syllablePattern : shApproach.getSHSonorityHierarchy()) {
			syllablePattern.setActive(false);
			forceTableRowToRedisplayPerActiveSetting(syllablePattern);
		}
	}

	protected void handleCheckBoxToggle() {
		for (SHNaturalClass syllablePattern : shApproach.getSHSonorityHierarchy()) {
			if (syllablePattern.isActive()) {
				syllablePattern.setActive(false);
			} else {
				syllablePattern.setActive(true);
			}
			forceTableRowToRedisplayPerActiveSetting(syllablePattern);
		}
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return new TextField[] { nameField, descriptionField };
	}
}
