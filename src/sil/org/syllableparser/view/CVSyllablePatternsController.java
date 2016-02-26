/**
 * 
 */
package sil.org.syllableparser.view;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.SyllableParserException;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.SylParserObject;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.syllableparser.model.cvapproach.CVSyllablePattern;
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

public class CVSyllablePatternsController extends CheckBoxColumnController implements Initializable {
	protected final class WrappingTableCell extends TableCell<CVSyllablePattern, String> {
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
				CVSyllablePattern syllablePattern = (CVSyllablePattern) this.getTableRow().getItem();
				if (syllablePattern != null && syllablePattern.isActive()) {
					text.setFill(Constants.ACTIVE);
				} else {
					text.setFill(Constants.INACTIVE);
				}
				setGraphic(text);
			}
		}
	}

	@FXML
	private TableView<CVSyllablePattern> cvSyllablePatternTable;
	@FXML
	private TableColumn<CVSyllablePattern, String> nameColumn;
	@FXML
	private TableColumn<CVSyllablePattern, String> naturalClassColumn;
	@FXML
	private TableColumn<CVSyllablePattern, String> descriptionColumn;
	@FXML
	private TableColumn<CVSyllablePattern, Boolean> checkBoxColumn;
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

	private CVApproach cvApproach;
	private CVSyllablePattern currentSyllablePattern;

	public CVSyllablePatternsController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		// Initialize the button icons
		ControllerUtilities.createToolbarButtonWithImage("UpArrow.png", buttonMoveUp,
				tooltipMoveUp, bundle.getString("cv.view.syllablepatterns.up"));
		ControllerUtilities.createToolbarButtonWithImage("DownArrow.png", buttonMoveDown,
				tooltipMoveDown, bundle.getString("cv.view.syllablepatterns.down"));

// 		checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().activeCheckBoxProperty());
//		checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));
//		checkBoxColumn.setEditable(true);
//		checkBoxColumnHead.setOnAction((event) -> {
//			handleCheckBoxColumnHead();
//		});
//		initializeCheckBoxContextMenu(resources);

		nameColumn.setCellValueFactory(cellData -> cellData.getValue().spNameProperty());
		naturalClassColumn.setCellValueFactory(cellData -> cellData.getValue()
				.ncsRepresentationProperty());
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

		// Clear cv natural class details.
		showCVSyllablePatternDetails(null);

		// Listen for selection changes and show the details when changed.
		cvSyllablePatternTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showCVSyllablePatternDetails(newValue));

		// Handle TextField text changes.
		nameField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentSyllablePattern != null) {
				currentSyllablePattern.setSPName(nameField.getText());
			}
		});
		descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentSyllablePattern != null) {
				currentSyllablePattern.setDescription(descriptionField.getText());
			}
		});

		activeCheckBox.setOnAction((event) -> {
			if (currentSyllablePattern != null) {
				currentSyllablePattern.setActive(activeCheckBox.isSelected());
				showNaturalClassesContent();
				forceTableRowToRedisplayPerActiveSetting(currentSyllablePattern);
				displayFieldsPerActiveSetting(currentSyllablePattern);
			}
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
	
	public void displayFieldsPerActiveSetting(CVSyllablePattern syllablePattern) {
		nameField.setDisable(!syllablePattern.isActive());
		ncsTextFlow.setDisable(!syllablePattern.isActive());
		ncsButton.setDisable(!syllablePattern.isActive());
		descriptionField.setDisable(!syllablePattern.isActive());
	}

	private void forceTableRowToRedisplayPerActiveSetting(CVSyllablePattern syllablePattern) {
		// we need to make the content of the row cells change in order for
		// the cell factory to fire.
		// We do this by getting the value, blanking it, and then restoring it.
		String temp = syllablePattern.getSPName();
		syllablePattern.setSPName("");
		syllablePattern.setSPName(temp);
		temp = syllablePattern.getNCSRepresentation();
		syllablePattern.setNCSRepresentation("");
		syllablePattern.setNCSRepresentation(temp);
		temp = syllablePattern.getDescription();
		syllablePattern.setDescription("");
		syllablePattern.setDescription(temp);
	}


	/**
	 * Fills all text fields to show details about the CV natural class. If the
	 * specified segment is null, all text fields are cleared.
	 * 
	 * @param syllablePattern
	 *            the segment or null
	 */
	private void showCVSyllablePatternDetails(CVSyllablePattern syllablePattern) {
		currentSyllablePattern = syllablePattern;
		if (syllablePattern != null) {
			// Fill the text fields with info from the person object.
			nameField.setText(syllablePattern.getSPName());
			descriptionField.setText(syllablePattern.getDescription());
			activeCheckBox.setSelected(syllablePattern.isActive());
			showNaturalClassesContent();
			displayFieldsPerActiveSetting(syllablePattern);
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

		if (syllablePattern != null) {
			int currentItem = cvSyllablePatternTable.getItems().indexOf(currentSyllablePattern) + 1;
			this.mainApp.updateStatusBarNumberOfItems(currentItem + "/"
					+ cvSyllablePatternTable.getItems().size() + " ");
		}

	}

	protected void setUpDownButtonDisabled() {
		int iThis = cvApproach.getCVSyllablePatterns().indexOf(currentSyllablePattern) + 1;
		int iSize = cvApproach.getCVSyllablePatterns().size();
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
		if (currentSyllablePattern.isWordInitial()) {
			addNameToContent(sb, Constants.WORD_BOUNDARY_SYMBOL, true);
			sb.append(", ");
		}
		int i = 1;
		int iCount = currentSyllablePattern.getNCs().size();
		for (SylParserObject spo : currentSyllablePattern.getNCs()) {
			CVNaturalClass nc = (CVNaturalClass) spo;
			if (nc != null) {
				addNameToContent(sb, nc.getNCName(), spo.isActive());
				if (i++ < iCount) {
					sb.append(", ");
				}
			}
		}
		if (currentSyllablePattern.isWordFinal()) {
			sb.append(", ");
			addNameToContent(sb, Constants.WORD_BOUNDARY_SYMBOL, true);
		}
		currentSyllablePattern.setNCSRepresentation(sb.toString());
	}

	protected void addNameToContent(StringBuilder sb, String sName, boolean isActive) {
		Text t = new Text(sName);
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

	public void setSyllablePattern(CVSyllablePattern syllablePattern) {
		nameField.setText(syllablePattern.getSPName());
		descriptionField.setText(syllablePattern.getDescription());
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param cvApproachController
	 */
	public void setData(CVApproach cvApproachData) {
		cvApproach = cvApproachData;

		// Add observable list data to the table
		cvSyllablePatternTable.setItems(cvApproachData.getCVSyllablePatterns());
		if (cvSyllablePatternTable.getItems().size() > 0) {
			// select first one
			cvSyllablePatternTable.requestFocus();
			cvSyllablePatternTable.getSelectionModel().select(0);
			cvSyllablePatternTable.getFocusModel().focus(0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		CVSyllablePattern newSyllablePattern = new CVSyllablePattern();
		cvApproach.getCVSyllablePatterns().add(newSyllablePattern);
		int i = cvApproach.getCVSyllablePatterns().size() - 1;
		cvSyllablePatternTable.requestFocus();
		cvSyllablePatternTable.getSelectionModel().select(i);
		cvSyllablePatternTable.getFocusModel().focus(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		int i = cvApproach.getCVSyllablePatterns().indexOf(currentSyllablePattern);
		currentSyllablePattern = null;
		if (i >= 0) {
			cvApproach.getCVSyllablePatterns().remove(i);
		}
	}

	@FXML
	void handleLaunchNCSequenceChooser() {
		showNCSequenceChooser();
		showNaturalClassesContent();
	}

	/**
	 * Opens a dialog to show and set sequence of natural classes
	 */
	public void showNCSequenceChooser() {
		try {
			// Load the fxml file and create a new stage for the popup.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ApproachViewNavigator.class
					.getResource("fxml/CVSyllablePatternNaturalClassChooser.fxml"));
			loader.setResources(ResourceBundle.getBundle(
					"sil.org.syllableparser.resources.SyllableParser", locale));

			AnchorPane page = loader.load();
			Stage dialogStage = new Stage();
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			// set the icon
			dialogStage.getIcons().add(mainApp.getNewMainIconImage());
			dialogStage.setTitle(MainApp.kApplicationTitle);

			CVSyllablePatternNaturalClassChooserController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setData(cvApproach);
			controller.setSyllablePattern(currentSyllablePattern);

			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void handleMoveDown() {
		int i = cvApproach.getCVSyllablePatterns().indexOf(currentSyllablePattern);
		if ((i + 1) < cvApproach.getCVSyllablePatterns().size()) {
			Collections.swap(cvApproach.getCVSyllablePatterns(), i, i + 1);
		}
	}

	@FXML
	void handleMoveUp() {
		int i = cvApproach.getCVSyllablePatterns().indexOf(currentSyllablePattern);
		if (i > 0) {
			Collections.swap(cvApproach.getCVSyllablePatterns(), i, i - 1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachEditorController#handleCut()
	 */
	@Override
	void handleCut() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachEditorController#handleCopy()
	 */
	@Override
	void handleCopy() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachEditorController#handlePaste()
	 */
	@Override
	void handlePaste() {
		// TODO Auto-generated method stub

	}

	protected void handleCheckBoxSelectAll() {
		for (CVSyllablePattern syllablePattern : cvApproach.getCVSyllablePatterns()) {
			syllablePattern.setActive(true);
			forceTableRowToRedisplayPerActiveSetting(syllablePattern);
		}
	}

	protected void handleCheckBoxClearAll() {
		for (CVSyllablePattern syllablePattern : cvApproach.getCVSyllablePatterns()) {
			syllablePattern.setActive(false);
			forceTableRowToRedisplayPerActiveSetting(syllablePattern);
		}
	}

	protected void handleCheckBoxToggle() {
		for (CVSyllablePattern syllablePattern : cvApproach.getCVSyllablePatterns()) {
			if (syllablePattern.isActive()) {
				syllablePattern.setActive(false);
			} else {
				syllablePattern.setActive(true);
			}
			forceTableRowToRedisplayPerActiveSetting(syllablePattern);
		}
	}
}
