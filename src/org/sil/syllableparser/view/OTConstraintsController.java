// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.sil.lingtree.model.FontInfo;
import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.Language;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVSegmentOrNaturalClass;
import org.sil.syllableparser.model.otapproach.OTApproach;
import org.sil.syllableparser.model.otapproach.OTConstraint;
import org.sil.syllableparser.service.LingTreeInteractor;
import org.sil.syllableparser.service.OTConstraintValidator;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */

public class OTConstraintsController extends SplitPaneWithTableViewController {

	protected final class AnalysisWrappingTableCell extends TableCell<OTConstraint, String> {
		private Text text;

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			processAnalysisTableCell(this, text, item, empty);
		}
	}

	protected final class WrappingTableCell extends TableCell<OTConstraint, String> {
		private Text text;
		private boolean isAffected = false;

		WrappingTableCell(boolean isAffected) {
			this.isAffected = isAffected;
		}

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			OTConstraint constraint = (OTConstraint) this.getTableRow().getItem();
			if (constraint != null) {
				CVSegmentOrNaturalClass segOrNC = constraint.getAffectedSegOrNC1();
				if (!isAffected) {
					segOrNC = constraint.getAffectedSegOrNC2();
				}
				if (segOrNC == null) {
					processTableCell(this, text, item, empty);
				} else if (segOrNC.isSegment()) {
					processVernacularTableCell(this, text, item, empty);
				} else {
					processAnalysisTableCell(this, text, item, empty);
				}
			} else {
				processTableCell(this, text, item, empty);
			}
		}
	}

	@FXML
	private TableView<OTConstraint> otConstraintsTable;
	@FXML
	private TableColumn<OTConstraint, String> nameColumn;
	@FXML
	private TableColumn<OTConstraint, String> descriptionColumn;
//	@FXML
//	private TableColumn<OTConstraint, WebView> representationColumn;

	@FXML
	private TextField nameField;
	@FXML
	private TextField descriptionField;
	@FXML
	private TextField affectedElement1TextField;
	@FXML
	private Button affectedElement1Button;
	@FXML
	private CheckBox isWordInitial1CheckBox;
	@FXML
	private CheckBox isOnset1CheckBox;
	@FXML
	private CheckBox isNucleus1CheckBox;
	@FXML
	private CheckBox isCoda1CheckBox;
	@FXML
	private CheckBox isUnparsed1CheckBox;
	@FXML
	private CheckBox isWordFinal1CheckBox;
	@FXML
	private TextField affectedElement2TextField;
	@FXML
	private Button affectedElement2Button;
	@FXML
	private CheckBox isWordInitial2CheckBox;
	@FXML
	private CheckBox isOnset2CheckBox;
	@FXML
	private CheckBox isNucleus2CheckBox;
	@FXML
	private CheckBox isCoda2CheckBox;
	@FXML
	private CheckBox isUnparsed2CheckBox;
	@FXML
	private CheckBox isWordFinal2CheckBox;
	@FXML
	private CheckBox activeCheckBox;
	@FXML
	protected Label constraintTreeLabel;
	@FXML
	protected WebView constraintLingTreeSVG;
	@FXML
	protected WebEngine webEngine;

	private OTConstraint currentConstraint;
	private OTConstraintValidator validator;
	protected LingTreeInteractor ltInteractor;

	public OTConstraintsController() {
		ltInteractor = LingTreeInteractor.getInstance();
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.setApproach(ApplicationPreferences.OT_CONSTRAINTS);
		super.setTableView(otConstraintsTable);
		super.initialize(location, resources);
		webEngine = constraintLingTreeSVG.getEngine();

		bundle = resources;

		nameColumn.setCellValueFactory(cellData -> cellData.getValue().constraintNameProperty());
		descriptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
//		representationColumn.setCellValueFactory(cellData -> cellData.getValue().getConstraintLingTreeSVG());

		// Custom rendering of the table cell.
		nameColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});
		descriptionColumn.setCellFactory(column -> {
			return new AnalysisWrappingTableCell();
		});
//		representationColumn.setCellFactory(column -> {
//			return new WrappingTableCell(true);
//		});

		makeColumnHeaderWrappable(nameColumn);
		makeColumnHeaderWrappable(descriptionColumn);
//		makeColumnHeaderWrappable(representationColumn);

		// Clear rule details.
		showConstraintDetails(null);

		// Listen for selection changes and show the details when changed.
		otConstraintsTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showConstraintDetails(newValue));
		

		// Handle TextField text changes.
		nameField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentConstraint != null) {
				currentConstraint.setConstraintName(nameField.getText());
			}
			if (languageProject != null) {
				nameField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});
		descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (currentConstraint != null) {
				currentConstraint.setDescription(descriptionField.getText());
			}
			if (languageProject != null) {
				descriptionField.setFont(languageProject.getAnalysisLanguage().getFont());
			}
		});
		affectedElement1TextField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (languageProject != null && currentConstraint != null) {
				Language lang = languageProject.getAnalysisLanguage();
				if (currentConstraint.getAffectedSegOrNC1() != null && currentConstraint.getAffectedSegOrNC1().isSegment()) {
					lang = languageProject.getVernacularLanguage();
				}
				affectedElement1TextField.setFont(lang.getFont());
				affectedElement1TextField.setNodeOrientation(lang.getOrientation());
				String sVernacular = mainApp.getStyleFromColor(lang.getColor());
				affectedElement1TextField.setStyle(sVernacular);
			}
			if (currentConstraint != null) {
				affectedElement1TextField.setText(newValue);
				currentConstraint.setAffectedElement1(newValue);
				reportAnyValidationMessage();
			}
		});
		affectedElement2TextField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (languageProject != null && currentConstraint != null) {
				Language lang = languageProject.getAnalysisLanguage();
				if (currentConstraint.getAffectedSegOrNC2() != null && currentConstraint.getAffectedSegOrNC2().isSegment()) {
					lang = languageProject.getVernacularLanguage();
				}
				affectedElement2TextField.setFont(lang.getFont());
				affectedElement2TextField.setNodeOrientation(lang.getOrientation());
				String sVernacular = mainApp.getStyleFromColor(lang.getColor());
				affectedElement2TextField.setStyle(sVernacular);
			}
			if (currentConstraint != null) {
				affectedElement2TextField.setText(newValue);
				currentConstraint.setAffectedElement2(newValue);
				reportAnyValidationMessage();
			}
		});

		activeCheckBox.setOnAction((event) -> {
			if (currentConstraint != null) {
				currentConstraint.setActive(activeCheckBox.isSelected());
				reportAnyValidationMessage();
				forceTableRowToRedisplayPerActiveSetting(currentConstraint);
			}
			displayFieldsPerActiveSetting(currentConstraint);
		});

		isWordInitial1CheckBox.setOnAction((event) -> {
			if (currentConstraint != null) {
				currentConstraint.setIsWordInitial1(isWordInitial1CheckBox.isSelected());
				reportAnyValidationMessage();
				forceTableRowToRedisplayPerActiveSetting(currentConstraint);
			}
			displayFieldsPerActiveSetting(currentConstraint);
		});
		isOnset1CheckBox.setOnAction((event) -> {
			if (currentConstraint != null) {
				currentConstraint.setIsOnset1(isOnset1CheckBox.isSelected());
				reportAnyValidationMessage();
				forceTableRowToRedisplayPerActiveSetting(currentConstraint);
			}
			displayFieldsPerActiveSetting(currentConstraint);
		});
		isNucleus1CheckBox.setOnAction((event) -> {
			if (currentConstraint != null) {
				currentConstraint.setIsNucleus1(isNucleus1CheckBox.isSelected());
				reportAnyValidationMessage();
				forceTableRowToRedisplayPerActiveSetting(currentConstraint);
			}
			displayFieldsPerActiveSetting(currentConstraint);
		});
		isCoda1CheckBox.setOnAction((event) -> {
			if (currentConstraint != null) {
				currentConstraint.setIsCoda1(isCoda1CheckBox.isSelected());
				reportAnyValidationMessage();
				forceTableRowToRedisplayPerActiveSetting(currentConstraint);
			}
			displayFieldsPerActiveSetting(currentConstraint);
		});
		isUnparsed1CheckBox.setOnAction((event) -> {
			if (currentConstraint != null) {
				currentConstraint.setIsUnparsed1(isUnparsed1CheckBox.isSelected());
				reportAnyValidationMessage();
				forceTableRowToRedisplayPerActiveSetting(currentConstraint);
			}
			displayFieldsPerActiveSetting(currentConstraint);
		});
		isWordFinal1CheckBox.setOnAction((event) -> {
			if (currentConstraint != null) {
				currentConstraint.setIsWordFinal1(isWordFinal1CheckBox.isSelected());
				reportAnyValidationMessage();
				forceTableRowToRedisplayPerActiveSetting(currentConstraint);
			}
			displayFieldsPerActiveSetting(currentConstraint);
		});

		isWordInitial2CheckBox.setOnAction((event) -> {
			if (currentConstraint != null) {
				currentConstraint.setIsWordInitial2(isWordInitial2CheckBox.isSelected());
				reportAnyValidationMessage();
				forceTableRowToRedisplayPerActiveSetting(currentConstraint);
			}
			displayFieldsPerActiveSetting(currentConstraint);
		});
		isOnset2CheckBox.setOnAction((event) -> {
			if (currentConstraint != null) {
				currentConstraint.setIsOnset2(isOnset2CheckBox.isSelected());
				reportAnyValidationMessage();
				forceTableRowToRedisplayPerActiveSetting(currentConstraint);
			}
			displayFieldsPerActiveSetting(currentConstraint);
		});
		isNucleus2CheckBox.setOnAction((event) -> {
			if (currentConstraint != null) {
				currentConstraint.setIsNucleus2(isNucleus2CheckBox.isSelected());
				reportAnyValidationMessage();
				forceTableRowToRedisplayPerActiveSetting(currentConstraint);
			}
			displayFieldsPerActiveSetting(currentConstraint);
		});
		isCoda2CheckBox.setOnAction((event) -> {
			if (currentConstraint != null) {
				currentConstraint.setIsCoda2(isCoda2CheckBox.isSelected());
				reportAnyValidationMessage();
				forceTableRowToRedisplayPerActiveSetting(currentConstraint);
			}
			displayFieldsPerActiveSetting(currentConstraint);
		});
		isUnparsed2CheckBox.setOnAction((event) -> {
			if (currentConstraint != null) {
				currentConstraint.setIsUnparsed2(isUnparsed2CheckBox.isSelected());
				reportAnyValidationMessage();
				forceTableRowToRedisplayPerActiveSetting(currentConstraint);
			}
			displayFieldsPerActiveSetting(currentConstraint);
		});
		isWordFinal2CheckBox.setOnAction((event) -> {
			if (currentConstraint != null) {
				currentConstraint.setIsWordFinal2(isWordFinal2CheckBox.isSelected());
				reportAnyValidationMessage();
				forceTableRowToRedisplayPerActiveSetting(currentConstraint);
			}
			displayFieldsPerActiveSetting(currentConstraint);
		});
		// Use of Enter move focus to next item.
		nameField.setOnAction((event) -> {
			descriptionField.requestFocus();
		});
		descriptionField.setOnAction((event) -> {
			this.affectedElement1Button.requestFocus();
		});

		nameField.requestFocus();
	}

	protected void reportAnyValidationMessage() {
		validator = OTConstraintValidator.getInstance();
		validator.setConstraint(currentConstraint);
		validator.validate();
		constraintLingTreeSVG.setVisible(true);
		if (validator.isValid()) {
			currentConstraint.setIsValid(true);
			constraintTreeLabel.setVisible(true);
			showConstraintContent();
		} else {
			constraintTreeLabel.setVisible(false);
			StringBuilder sb = new StringBuilder();
			sb.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head>");
			sb.append("<body><div style=\"text-align:left;");
			sb.append("color:");
			sb.append(Constants.RULES_ERROR_MESSAGE);
			sb.append("\">");
			sb.append(bundle.getString(validator.getErrorMessageProperty()));
			sb.append("</div></body></html>");
			webEngine.loadContent(sb.toString());
		}
		currentConstraint.setIsValid(validator.isValid());
	}

	public void displayFieldsPerActiveSetting(OTConstraint constraint) {
		boolean fIsActive;
		if (constraint == null) {
			fIsActive = false;
		} else {
			fIsActive = constraint.isActive();
		}
		nameField.setDisable(!fIsActive);
		descriptionField.setDisable(!fIsActive);
		affectedElement1TextField.setDisable(!fIsActive);
		affectedElement1Button.setDisable(!fIsActive);
		affectedElement2TextField.setDisable(!fIsActive);
		affectedElement2Button.setDisable(!fIsActive);
		isWordInitial1CheckBox.setDisable(!fIsActive);
		isOnset1CheckBox.setDisable(!fIsActive);
		isNucleus1CheckBox.setDisable(!fIsActive);
		isCoda1CheckBox.setDisable(!fIsActive);
		isUnparsed1CheckBox.setDisable(!fIsActive);
		isWordFinal1CheckBox.setDisable(!fIsActive);
		isWordInitial2CheckBox.setDisable(!fIsActive);
		isOnset2CheckBox.setDisable(!fIsActive);
		isNucleus2CheckBox.setDisable(!fIsActive);
		isCoda2CheckBox.setDisable(!fIsActive);
		isUnparsed2CheckBox.setDisable(!fIsActive);
		isWordFinal2CheckBox.setDisable(!fIsActive);
		constraintLingTreeSVG.setDisable(!fIsActive);
	}

	private void forceTableRowToRedisplayPerActiveSetting(OTConstraint constraint) {
		// we need to make the content of the row cells change in order for
		// the cell factory to fire.
		// We do this by getting the value, blanking it, and then restoring it.
		String temp = constraint.getConstraintName();
		constraint.setConstraintName("");
		constraint.setConstraintName(temp);
		temp = constraint.getDescription();
		constraint.setDescription("");
		constraint.setDescription(temp);
		// TODO: need to make these be whatever is shown in a table row
		temp = constraint.getAffectedElement1();
		constraint.setAffectedElement1("");
		constraint.setAffectedElement1(temp);
	}

	private void showConstraintDetails(OTConstraint constraint) {
		currentConstraint = constraint;
		if (constraint != null) {
			// Fill the text fields with info from the NPRule object.
			nameField.setText(constraint.getConstraintName());
			descriptionField.setText(constraint.getDescription());
			NodeOrientation analysisOrientation = languageProject.getAnalysisLanguage()
					.getOrientation();
			nameField.setNodeOrientation(analysisOrientation);
			descriptionField.setNodeOrientation(analysisOrientation);
			affectedElement1TextField.setText(constraint.getAffectedElement1());
			affectedElement2TextField.setText(constraint.getAffectedElement2());
			isWordInitial1CheckBox.setSelected(constraint.isWordInitial1());
			isOnset1CheckBox.setSelected(constraint.isOnset1());
			isNucleus1CheckBox.setSelected(constraint.isNucleus1());
			isCoda1CheckBox.setSelected(constraint.isCoda1());
			isUnparsed1CheckBox.setSelected(constraint.isUnparsed1());
			isWordFinal1CheckBox.setSelected(constraint.isWordFinal1());
			isWordInitial2CheckBox.setSelected(constraint.isWordInitial2());
			isOnset2CheckBox.setSelected(constraint.isOnset2());
			isNucleus2CheckBox.setSelected(constraint.isNucleus2());
			isCoda2CheckBox.setSelected(constraint.isCoda2());
			isUnparsed2CheckBox.setSelected(constraint.isUnparsed2());
			isWordFinal2CheckBox.setSelected(constraint.isWordFinal2());
			activeCheckBox.setSelected(constraint.isActive());
			int currentItem = otConstraintsTable.getItems().indexOf(currentConstraint);
			this.mainApp.updateStatusBarNumberOfItems((currentItem + 1) + "/"
					+ otConstraintsTable.getItems().size() + " ");
			mainApp.getApplicationPreferences().setLastNPRulesViewItemUsed(currentItem);
			reportAnyValidationMessage();
		} else {
			if (nameField != null) {
				nameField.setText("");
			}
			if (descriptionField != null) {
				descriptionField.setText("");
			}
			if (affectedElement1TextField != null) {
				affectedElement1TextField.setText("");
			}
			if (affectedElement2TextField != null) {
				affectedElement2TextField.setText("");
			}
		}
		displayFieldsPerActiveSetting(constraint);
	}

	@Override
	public void setViewItemUsed(int value) {
		int max = otConstraintsTable.getItems().size();
		value = adjustIndexValue(value, max);
		otConstraintsTable.getSelectionModel().clearAndSelect(value);
	}

	private void showConstraintContent() {
		if (currentConstraint != null) {
			String sLingTreeDescription = currentConstraint.createLingTreeDescription();
			if (!sLingTreeDescription.equals("")) {
				currentConstraint.setLingTreeDescription(sLingTreeDescription);
				showLingTreeSVG(sLingTreeDescription);
			}
		}
	}

	public void setData(OTApproach otApproachData) {
		otApproach = otApproachData;
		languageProject = otApproach.getLanguageProject();
		// no sorting needed

		// Add observable list data to the table
		otConstraintsTable.setItems(otApproachData.getOTConstraints());
		int max = otConstraintsTable.getItems().size();
		if (max > 0) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					int iLastIndex = mainApp.getApplicationPreferences()
							.getLastNPRulesViewItemUsed();
					iLastIndex = adjustIndexValue(iLastIndex, max);
					otConstraintsTable.requestFocus();
					otConstraintsTable.getSelectionModel().select(iLastIndex);
					otConstraintsTable.getFocusModel().focus(iLastIndex);
					otConstraintsTable.scrollTo(iLastIndex);
				}
			});
		}
		if (languageProject != null) {
			String sAnalysis = mainApp.getStyleFromColor(languageProject.getAnalysisLanguage().getColor());
			nameField.setStyle(sAnalysis);
			descriptionField.setStyle(sAnalysis);
			validator = OTConstraintValidator.getInstance();
			for (OTConstraint constraint : otApproachData.getOTConstraints()) {
				if (constraint.getAffectedSegOrNC1() != null) {
					constraint.setAffectedSegOrNC1(createCVSegmentOrNaturalClass(constraint.getAffectedSegOrNC1()));
				}
				if (constraint.getAffectedSegOrNC2() != null) {
					constraint.setAffectedSegOrNC2(createCVSegmentOrNaturalClass(constraint.getAffectedSegOrNC2()));
				}
				validator.setConstraint(constraint);
				validator.validate();
				constraint.setIsValid(validator.isValid());
			}
		}
	}
	
	protected CVSegmentOrNaturalClass createCVSegmentOrNaturalClass(CVSegmentOrNaturalClass segOrNC) {
		String name = "";
		String description = "";
		if (segOrNC.isSegment()) {
			int i = SylParserObject.findIndexInListByUuid(languageProject.getSegmentInventory(), segOrNC.getUuid());
			Segment segment = languageProject.getSegmentInventory().get(i);
			name = segment.getSegment();
			description = segment.getDescription();
		} else {
			int i = SylParserObject.findIndexInListByUuid(languageProject.getCVApproach().getCVNaturalClasses(), segOrNC.getUuid());
			CVNaturalClass segment = languageProject.getCVApproach().getCVNaturalClasses().get(i);
			name = segment.getNCName();
			description = segment.getDescription();
		}
		segOrNC.setSegmentOrNaturalClass(name);
		segOrNC.setDescription(description);
		return segOrNC;
	}

	@Override
	void handleInsertNewItem() {
		OTConstraint newConstraint = new OTConstraint();
		otApproach.getOTConstraints().add(newConstraint);
		handleInsertNewItem(otApproach.getOTConstraints(), otConstraintsTable);
	}

	@Override
	void handleRemoveItem() {
		handleRemoveItem(otApproach.getOTConstraints(), currentConstraint, otConstraintsTable);
	}

	@Override
	void handlePreviousItem() {
		handlePreviousItem(otApproach.getOTConstraints(), currentConstraint, otConstraintsTable);
	}

	@Override
	void handleNextItem() {
		handleNextItem(otApproach.getOTConstraints(), currentConstraint, otConstraintsTable);
	}

	@FXML
	void handleLaunchAffectedSegOrNC1Chooser() {
		showSegmentChooser(true);
		reportAnyValidationMessage();
	}

	@FXML
	void handleLaunchAffectedSegOrNC2Chooser() {
		showSegmentChooser(false);
		reportAnyValidationMessage();
	}

	public WebView getConstraintLingTreeSVG() {
		return constraintLingTreeSVG;
	}

	/**
	 * Opens a dialog to show and set segments
	 */
	public void showSegmentChooser(boolean isFirst) {
		try {
			// Load the fxml file and create a new stage for the popup.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ApproachViewNavigator.class
					.getResource("fxml/OTSegmentNaturalClassChooser.fxml"));
			loader.setResources(ResourceBundle.getBundle(
					Constants.RESOURCE_LOCATION, locale));

			AnchorPane page = loader.load();
			Stage dialogStage = new Stage();
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			// set the icon
			dialogStage.getIcons().add(mainApp.getNewMainIconImage());
			dialogStage.setTitle(MainApp.kApplicationTitle);

			OTSegmentNaturalClassChooserController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setConstraint(currentConstraint);
			controller.setData(otApproach.getLanguageProject(), isFirst);
			controller.initializeTableColumnWidths(mainApp.getApplicationPreferences());

			dialogStage.showAndWait();
			
			if (controller.isOkClicked()) {
				if (isFirst) {
					affectedElement1TextField.setText(controller.getConstraint().getAffectedElement1());
					currentConstraint.setAffectedSegOrNC1(controller.getConstraint().getAffectedSegOrNC1());
				} else {
					affectedElement2TextField.setText(controller.getConstraint().getAffectedElement2());
					currentConstraint.setAffectedSegOrNC2(controller.getConstraint().getAffectedSegOrNC2());
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
	}

	protected void showLingTreeSVG(String sLingTreeDescription) {
		ltInteractor.initializeParameters(languageProject);
		FontInfo fiAnalysis = new FontInfo(languageProject.getAnalysisLanguage().getFont());
		fiAnalysis.setColor(Color.BLACK);
		ltInteractor.setLexicalFontInfo(fiAnalysis);
		double yInit = ltInteractor.getInitialYCoordinate();
		ltInteractor.setInitialYCoordinate(yInit-(2*ltInteractor.getVerticalGap()));

		ltInteractor.setVerticalGap(30.0);
		String ltSVG = ltInteractor.createSVG(sLingTreeDescription, true);
		StringBuilder sb = new StringBuilder();
		sb.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head>");
		sb.append("<body><div style=\"text-align:left\">");
		sb.append(ltSVG);
		sb.append("</div></body></html>");
		webEngine.loadContent(sb.toString());
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return new TextField[] { nameField, descriptionField };
	}
}
