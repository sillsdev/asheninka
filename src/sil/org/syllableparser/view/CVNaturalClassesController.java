/**
 * 
 */
package sil.org.syllableparser.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.SylParserObject;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
	
	public CVNaturalClassesController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// checkBoxColumn.setCellValueFactory(cellData ->
		// cellData.getValue().activeCheckBoxProperty());
		// checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));
		// checkBoxColumn.setEditable(true);
		// checkBoxColumnHead.setOnAction((event) -> {
		// handleCheckBoxColumnHead();
		// });
		// initializeCheckBoxContextMenu(resources);

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
				displayFieldsPerActiveSetting(currentNaturalClass);
			}
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
		nameField.setDisable(!nc.isActive());
		// Following does not show as grey for some reason...
		sncTextFlow.setDisable(!nc.isActive());
		sncButton.setDisable(!nc.isActive());
		descriptionField.setDisable(!nc.isActive());
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
			displayFieldsPerActiveSetting(naturalClass);
		} else {
			// Segment is null, remove all the text.
			nameField.setText("");
			descriptionField.setText("");
			sncTextFlow.getChildren().clear();
		}

		if (naturalClass != null) {
			int currentItem = cvNaturalClassTable.getItems().indexOf(currentNaturalClass) + 1;
			this.mainApp.updateStatusBarNumberOfItems(currentItem + "/"
					+ cvNaturalClassTable.getItems().size() + " ");
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
	 * 
	 * @param cvApproachController
	 */
	public void setData(CVApproach cvApproachData) {
		cvApproach = cvApproachData;
		languageProject = cvApproach.getLanguageProject();

		// Add observable list data to the table
		cvNaturalClassTable.setItems(cvApproachData.getCVNaturalClasses());
		if (cvNaturalClassTable.getItems().size() > 0) {
			// select first one
			cvNaturalClassTable.requestFocus();
			cvNaturalClassTable.getSelectionModel().select(0);
			cvNaturalClassTable.getFocusModel().focus(0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		CVNaturalClass newNaturalClass = new CVNaturalClass();
		cvApproach.getCVNaturalClasses().add(newNaturalClass);
		int i = cvApproach.getCVNaturalClasses().size() - 1;
		cvNaturalClassTable.requestFocus();
		cvNaturalClassTable.getSelectionModel().select(i);
		cvNaturalClassTable.getFocusModel().focus(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		// need to deal with all pointers to this natural class
		int i = cvApproach.getCVNaturalClasses().indexOf(currentNaturalClass);
		currentNaturalClass = null;
		if (i >= 0) {
			cvApproach.getCVNaturalClasses().remove(i);
		}
		// the last item in the middle pane will be repeated if we delete an
		// earlier one
		// how can we get it to show a blank?
		// chances are we do not have something set up properly - looks like it
		// is just supposed to work
		// Any (currently showing) item referring to the deleted item needs to
		// be have its sncField recalculated.
	}

	@FXML
	void handleLaunchSNCChooser() {
		showSNCChooser();
		showSegmentOrNaturalClassContent();
	}

	/**
	 * Opens a dialog to show birthday statistics.
	 */
	public void showSNCChooser() {
		try {
			// Load the fxml file and create a new stage for the popup.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ApproachViewNavigator.class
					.getResource("fxml/CVSegmentNaturalClassChooser.fxml"));
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

	// code taken from http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
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
