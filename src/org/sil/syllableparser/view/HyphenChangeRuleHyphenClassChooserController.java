// Copyright (c) 2025 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.hyphenapproach.HyphenApproach;
import org.sil.syllableparser.model.hyphenapproach.HyphenChangeRule;
import org.sil.syllableparser.model.hyphenapproach.HyphenClass;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * @author Andy Black
 *
 */
public class HyphenChangeRuleHyphenClassChooserController implements Initializable {
	@FXML
	private Label labelSequence;
	@FXML
	private List<ComboBox<HyphenClass>> comboBoxList;
	private List<ObservableList<HyphenClass>> comboBoxDataList = new ArrayList<ObservableList<HyphenClass>>();
	Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;

	private HyphenApproach hyphenApproach;
	private HyphenChangeRule changeRule;
	private ObservableList<HyphenClass> hyphenClasses;
	private HyphenClass removeHC;
	private HyphenClass wordBoundaryHC;
	private String sSequencePrompt;
	private boolean isChange = false;
	ResourceBundle bundle;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		removeHC = new HyphenClass(resources.getString("cv.view.syllablepatterns.remove"), null,
				"", Constants.SPECIAL_REMOVE_CODE);
		wordBoundaryHC = new HyphenClass(
				resources.getString("cv.view.syllablepatterns.wordboundary"), null, "",
				Constants.SPECIAL_WORD_BOUNDARY_CODE);
		sSequencePrompt = resources.getString("cv.view.syllablepatterns.ncsequence");

		int i = 0;
		for (ComboBox<HyphenClass> cb : comboBoxList) {
			ObservableList<HyphenClass> ol = FXCollections.observableArrayList();
			comboBoxDataList.add(ol);
			cb.setItems(comboBoxDataList.get(i++));
			cb.setCellFactory(renderHCsInComboBox(cb));
			cb.setConverter(renderSelectedHCInComboBox());
			if (i < comboBoxList.size()) {
				ComboBox<HyphenClass> cbNext = comboBoxList.get(i);
				handleComboBoxSelectionEvent(cb, cbNext);
			}
		}
		comboBoxList.get(comboBoxList.size() - 1).setOnAction((event) -> {
			labelSequence.setText(getHyphenClassSequenceFromComboBoxes());
		});
	}

	public void setChangeRule(HyphenChangeRule changeRule) {
		this.changeRule = changeRule;
	}

	private void handleComboBoxSelectionEvent(ComboBox<HyphenClass> cb,
			ComboBox<HyphenClass> cbNext) {
		cb.setOnAction((event) -> {
			HyphenClass hc = cb.getValue();
			if (hc != null) {
				if (hc.getSegmentsRepresentation() == Constants.SPECIAL_REMOVE_CODE) {
					// if we merely invoke the remove and update label code
					// directly, we get an IndexOutOfBoundsException. This is
					// because
					// "In JavaFX, you cannot change the contents of an
					// ObservableList while a change is already in progress."
					// See
					// http://stackoverflow.com/questions/32370394/javafx-combobox-change-value-causes-indexoutofboundsexception
					// To avoid this, we ask the system to run the change later
					// on the JavaFX platform thread.
					// Doing so avoids the exception.
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							removeContentFromComboBox(cb);
							labelSequence.setText(getHyphenClassSequenceFromComboBoxes());
							clearOptionFromComboBox(removeHC, cb);
						}
					});
				} else if (hc.getSegmentsRepresentation() == Constants.SPECIAL_WORD_BOUNDARY_CODE) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							labelSequence.setText(getHyphenClassSequenceFromComboBoxes());
							addOptionToComboBox(removeHC, cb);
							if (Constants.FIRST_COMBO_BOX_IN_SYLLABLE_PATTERN.equals(cb.getId())) {
								// it's the initial one; make sure the second
								// combo box is visible
								cbNext.setVisible(true);
							} else {
								// it's not initial; therefore this combo box
								// needs to be the final one
								makeAllFollowingComboBoxesInvisible(cb);
							}
						}
					});
				} else {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							labelSequence.setText(getHyphenClassSequenceFromComboBoxes());
							cbNext.setVisible(true);
							addOptionToComboBox(removeHC, cb);
						}
					});
				}
			}
		});
	}

	public void addOptionToComboBox(HyphenClass option, ComboBox<HyphenClass> cb) {
		int i = comboBoxList.indexOf(cb);
		ObservableList<HyphenClass> ol = comboBoxDataList.get(i);
		if (!ol.contains(option)) {
			ol.add(option);
		}
	}

	// is public for unit testing
	public void removeContentFromComboBox(ComboBox<HyphenClass> cb) {
		int i = comboBoxList.indexOf(cb);
		// shift values to the left
		while ((i + 1) < comboBoxList.size() && comboBoxList.get(i + 1).isVisible()) {
			comboBoxList.get(i).setValue(comboBoxList.get(i + 1).getValue());
			i++;
		}
		// set next to last one to no longer have a remove option
		ComboBox<HyphenClass> cbi = comboBoxList.get(i - 1);
		clearOptionFromComboBox(removeHC, cbi);
		// no longer show final one
		if (i < comboBoxList.size() && comboBoxList.get(i).isVisible()) {
			comboBoxList.get(i).setVisible(false);
		}
	}

	// is public for unit testing
	public void makeAllFollowingComboBoxesInvisible(ComboBox<HyphenClass> cb) {
		int i = comboBoxList.indexOf(cb);
		while ((i + 1) < comboBoxList.size() && comboBoxList.get(i + 1).isVisible()) {
			comboBoxList.get(i + 1).setVisible(false);
			i++;
		}
	}

	// is public for unit testing
	public String getHyphenClassSequenceFromComboBoxes() {
		StringBuilder sb = new StringBuilder();
		if (comboBoxList.get(0).getSelectionModel().getSelectedIndex() < 0) {
			sb.append(sSequencePrompt);
		} else {
			for (ComboBox<HyphenClass> cb : comboBoxList) {
				if (cb.equals(comboBoxList.get(0))) {
					sb.append(getHyphenClassNameToShow(cb.getSelectionModel().getSelectedItem()));
				} else {
					getComboBoxSelectedHyphenClassName(cb, sb);
				}
			}
		}
		return sb.toString();
	}

	protected void getComboBoxSelectedHyphenClassName(ComboBox<HyphenClass> cb, StringBuilder sb) {
		if (cb.isVisible()) {
			sb.append(" ");
			HyphenClass selectedNaturalClass = (HyphenClass) cb.getSelectionModel()
					.getSelectedItem();
			if (selectedNaturalClass != null) {
				sb.append(getHyphenClassNameToShow(selectedNaturalClass));
			}
		}
	}

	private String getHyphenClassNameToShow(HyphenClass nc) {
		if (nc.getSegmentsRepresentation() == Constants.SPECIAL_WORD_BOUNDARY_CODE) {
			return Constants.WORD_BOUNDARY_SYMBOL;
		} else if (nc.getSegmentsRepresentation() == Constants.SPECIAL_INSERT_CODE) {
			return Constants.INSERT_HYPHEN_SYMBOL;
		}
		else {
			return nc.getClassName();
		}
	}

	// Define rendering of the list of values in ComboBox drop down.
	protected Callback<ListView<HyphenClass>, ListCell<HyphenClass>> renderHCsInComboBox(ComboBox<HyphenClass> cb) {
		return (comboBox) -> {
			return new ListCell<HyphenClass>() {
				@Override
				protected void updateItem(HyphenClass item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
					} else {
						String sCode = item.getSegmentsRepresentation();
						switch (sCode) {
						case Constants.SPECIAL_INSERT_CODE:
							setText(item.getDescription());
							break;
						case Constants.SPECIAL_REMOVE_CODE:
							// fall through
						case Constants.SPECIAL_WORD_BOUNDARY_CODE:
							setText(item.getClassName());
							break;
						default:
							setText(item.getClassName() + " - " + item.getDescription());
							break;
						}
						// Include the "Remove' option only when some item has been selected
						HyphenClass selectedHyphenClass = (HyphenClass) cb.getSelectionModel()
								.getSelectedItem();
						if (selectedHyphenClass != null) {
							addOptionToComboBox(removeHC, cb);
						}
						if (item.isActive()) {
							this.setDisable(false);
							this.setTextFill(Constants.ACTIVE);
						} else {
							this.setDisable(true);
							this.setTextFill(Constants.INACTIVE);
						}
					}
				}
			};
		};
	}

	// Define rendering of selected value shown in ComboBox.
	protected StringConverter<HyphenClass> renderSelectedHCInComboBox() {
		return new StringConverter<HyphenClass>() {
			public String toString(HyphenClass natClass) {
				if (natClass == null) {
					return null;
				} else {
					return natClass.getClassName();
				}
			}

			@Override
			public HyphenClass fromString(String naturalClassString) {
				return null; // No conversion fromString needed.
			}
		};
	}
	
	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param cvApproachController
	 */
	public void setData(HyphenApproach approachData) {
		hyphenApproach = approachData;
		hyphenApproach.getInsertHereHC().setDescription(bundle.getString("hyphen.view.hyphenchangerules.inserthyphenafter"));
		for (ObservableList<HyphenClass> ol : comboBoxDataList) {
			setComboBoxData(ol);
		}
	}

	protected void setComboBoxData(ObservableList<HyphenClass> cbData) {
		cbData.addAll(hyphenApproach.getHyphenClasses());
		if (isChange) {
			cbData.add(hyphenApproach.getInsertHereHC());
		}
		cbData.add(wordBoundaryHC);
	}

	public boolean isChange() {
		return isChange;
	}

	public void setChange(boolean isChange) {
		this.isChange = isChange;
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
		getHyphenClassesFromComboBoxes(hyphenClasses);

		okClicked = true;
		dialogStage.close();
	}

	// is public for unit testing
	public void getHyphenClassesFromComboBoxes(ObservableList<HyphenClass> currentlySelectdHyphenClasses) {
		currentlySelectdHyphenClasses.clear();
		changeRule.setWordInitial(false);
		changeRule.setWordFinal(false);
		for (ComboBox<HyphenClass> cb : comboBoxList) {
			if (cb.isVisible()) {
				HyphenClass selectedHyphenClass = (HyphenClass) cb.getSelectionModel()
						.getSelectedItem();
				if (selectedHyphenClass != null) {
					if (selectedHyphenClass.getSegmentsRepresentation() == Constants.SPECIAL_WORD_BOUNDARY_CODE) {
						if (Constants.FIRST_COMBO_BOX_IN_SYLLABLE_PATTERN.equals(cb.getId())) {
							changeRule.setWordInitial(true);
						} else {
							changeRule.setWordFinal(true);
						}
					} else if (selectedHyphenClass.getSegmentsRepresentation() == Constants.SPECIAL_INSERT_CODE) {
						currentlySelectdHyphenClasses.add(hyphenApproach.getInsertHereHC());
					} else {
						int i = HyphenClass.findIndexInListByUuid(
								hyphenApproach.getHyphenClasses(), selectedHyphenClass.getID());
						currentlySelectdHyphenClasses.add(hyphenApproach.getHyphenClasses().get(i));
					}
				}
			}
		}
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * Called when the user clicks help.
	 */
	@FXML
	private void handleHelp() {
		mainApp.showNotImplementedYet();
	}

	public HyphenChangeRule getSyllablePattern() {
		return changeRule;
	}

	public void setPattern(ObservableList<HyphenClass> hyphenClasses) {
		this.hyphenClasses = hyphenClasses;
		int iHyphenClassesInPattern = hyphenClasses.size();
		int iCurrentHyphenClass = 0;
		if (iHyphenClassesInPattern > 0) {
			for (ComboBox<HyphenClass> cb : comboBoxList) {
				if (changeRule.isWordInitial()
						&& Constants.FIRST_COMBO_BOX_IN_SYLLABLE_PATTERN.equals(cb.getId())) {
					cb.setValue(wordBoundaryHC);
					cb.setVisible(true);
				} else {
					cb.setValue(hyphenClasses.get(iCurrentHyphenClass++));
					if (iCurrentHyphenClass < comboBoxList.size()) {
						comboBoxList.get(iCurrentHyphenClass).setVisible(true);
					}
					if (iCurrentHyphenClass >= iHyphenClassesInPattern) {
						if (changeRule.isWordInitial()) {
							comboBoxList.get(++iCurrentHyphenClass).setVisible(true);
						}
						if (changeRule.isWordFinal()) {
							cb = comboBoxList.get(iCurrentHyphenClass);
							cb.setValue(wordBoundaryHC);
							cb.setVisible(true);
						}
						break;
					}
				}
			}
			labelSequence.setText(getHyphenClassSequenceFromComboBoxes());
		}
	}

	public void setSyllablePatternForUnitTesting(HyphenChangeRule syllablePattern) {
		this.changeRule = syllablePattern;
	}

	void setCurrentCVNaturalClass(HyphenClass naturalClass) {
	}

	// ComboBox getter is for unit testing
	public ComboBox<HyphenClass> getComboBox(int index) {
		return comboBoxList.get(index);
	}

	protected void clearOptionFromComboBox(HyphenClass option, ComboBox<HyphenClass> cb) {
		int i = comboBoxList.indexOf(cb);
		ObservableList<HyphenClass> ol = comboBoxDataList.get(i);
		if (ol.contains(option)) {
			ol.remove(option);
		}
	}

}
