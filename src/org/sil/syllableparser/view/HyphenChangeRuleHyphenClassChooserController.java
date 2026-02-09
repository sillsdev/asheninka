// Copyright (c) 2025-2026 SIL International 
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
import org.sil.syllableparser.model.hyphenapproach.HyphenClassInChooser;

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
	private List<ComboBox<HyphenClassInChooser>> comboBoxList;
	private List<ObservableList<HyphenClassInChooser>> comboBoxDataList = new ArrayList<ObservableList<HyphenClassInChooser>>();
	Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;

	private HyphenApproach hyphenApproach;
	private HyphenChangeRule changeRule;
	private ObservableList<HyphenClassInChooser> hyphenClassesInChooser = FXCollections.observableArrayList();
	private ObservableList<HyphenClass> hyphenClasses = FXCollections.observableArrayList();
	private HyphenClassInChooser insertHereHCIC;
	private HyphenClassInChooser removeHCIC;
	private HyphenClassInChooser wordBoundaryHCIC;
	private String sSequencePrompt;
	private boolean isChange = false;
	ResourceBundle bundle;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		HyphenClass removeHC = new HyphenClass(resources.getString("cv.view.syllablepatterns.remove"), null,
				"", Constants.SPECIAL_REMOVE_CODE);
		removeHCIC = new HyphenClassInChooser(removeHC, false);
		HyphenClass wordBoundaryHC = new HyphenClass(
				resources.getString("cv.view.syllablepatterns.wordboundary"), null, "",
				Constants.SPECIAL_WORD_BOUNDARY_CODE);
		wordBoundaryHCIC = new HyphenClassInChooser(wordBoundaryHC, false);
		sSequencePrompt = resources.getString("cv.view.syllablepatterns.ncsequence");

		int i = 0;
		for (ComboBox<HyphenClassInChooser> cb : comboBoxList) {
			ObservableList<HyphenClassInChooser> ol = FXCollections.observableArrayList();
			comboBoxDataList.add(ol);
			cb.setItems(comboBoxDataList.get(i++));
			cb.setCellFactory(renderHCsInComboBox(cb));
			cb.setConverter(renderSelectedHCInComboBox());
			if (i < comboBoxList.size()) {
				ComboBox<HyphenClassInChooser> cbNext = comboBoxList.get(i);
				handleComboBoxSelectionEvent(cb, cbNext);
			}
		}
		comboBoxList.get(comboBoxList.size() - 1).setOnAction((event) -> {
			labelSequence.setText(getHyphenClassSequenceFromComboBoxes());
		});
	}

	public void setChangeRule(HyphenChangeRule changeRule) {
		this.changeRule = changeRule;
		ObservableList<HyphenClass> classes = isChange ? changeRule.getChangeHyphenClasses() : changeRule.getMatchHyphenClasses();
		int i = 0;
		for (HyphenClass hc : classes) {
			boolean doNotMatchClassAgain = changeRule.getDoNotMatchClassAgains().get(i++);
			HyphenClassInChooser hcic = new HyphenClassInChooser(hc, doNotMatchClassAgain);
			hyphenClassesInChooser.add(hcic);
		}
	}

	private void handleComboBoxSelectionEvent(ComboBox<HyphenClassInChooser> cb,
			ComboBox<HyphenClassInChooser> cbNext) {
		cb.setOnAction((event) -> {
			HyphenClassInChooser hcic = cb.getValue();
			if (hcic != null) {
				if (hcic.getSegmentsRepresentation() == Constants.SPECIAL_REMOVE_CODE) {
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
							clearOptionFromComboBox(removeHCIC, cb);
						}
					});
				} else if (hcic.getSegmentsRepresentation() == Constants.SPECIAL_WORD_BOUNDARY_CODE) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							labelSequence.setText(getHyphenClassSequenceFromComboBoxes());
							addOptionToComboBox(removeHCIC, cb);
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
							addOptionToComboBox(removeHCIC, cb);
						}
					});
				}
			}
		});
	}

	public void addOptionToComboBox(HyphenClassInChooser option, ComboBox<HyphenClassInChooser> cb) {
		int i = comboBoxList.indexOf(cb);
		ObservableList<HyphenClassInChooser> ol = comboBoxDataList.get(i);
		if (!ol.contains(option)) {
			ol.add(option);
		}
	}

	// is public for unit testing
	public void removeContentFromComboBox(ComboBox<HyphenClassInChooser> cb) {
		int i = comboBoxList.indexOf(cb);
		// shift values to the left
		while ((i + 1) < comboBoxList.size() && comboBoxList.get(i + 1).isVisible()) {
			comboBoxList.get(i).setValue(comboBoxList.get(i + 1).getValue());
			i++;
		}
		// set next to last one to no longer have a remove option
		ComboBox<HyphenClassInChooser> cbi = comboBoxList.get(i - 1);
		clearOptionFromComboBox(removeHCIC, cbi);
		// no longer show final one
		if (i < comboBoxList.size() && comboBoxList.get(i).isVisible()) {
			comboBoxList.get(i).setVisible(false);
		}
	}

	// is public for unit testing
	public void makeAllFollowingComboBoxesInvisible(ComboBox<HyphenClassInChooser> cb) {
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
			for (ComboBox<HyphenClassInChooser> cb : comboBoxList) {
				if (cb.equals(comboBoxList.get(0))) {
					sb.append(getHyphenClassNameToShow(cb.getSelectionModel().getSelectedItem()));
				} else {
					getComboBoxSelectedHyphenClassName(cb, sb);
				}
			}
		}
		return sb.toString();
	}

	protected void getComboBoxSelectedHyphenClassName(ComboBox<HyphenClassInChooser> cb, StringBuilder sb) {
		if (cb.isVisible()) {
			sb.append(" ");
			HyphenClassInChooser selectedNaturalClass = (HyphenClassInChooser) cb.getSelectionModel()
					.getSelectedItem();
			if (selectedNaturalClass != null) {
				sb.append(getHyphenClassNameToShow(selectedNaturalClass));
			}
		}
	}

	private String getHyphenClassNameToShow(HyphenClassInChooser nc) {
		if (nc.getHyphenClass().getSegmentsRepresentation() == Constants.SPECIAL_WORD_BOUNDARY_CODE) {
			return Constants.WORD_BOUNDARY_SYMBOL;
		} else if (nc.getSegmentsRepresentation() == Constants.SPECIAL_INSERT_CODE) {
			return Constants.INSERT_HYPHEN_SYMBOL;
		}
		else {
			return nc.getClassName();
		}
	}

	// Define rendering of the list of values in ComboBox drop down.
	protected Callback<ListView<HyphenClassInChooser>, ListCell<HyphenClassInChooser>> renderHCsInComboBox(ComboBox<HyphenClassInChooser> cb) {
		return (comboBox) -> {
			return new ListCell<HyphenClassInChooser>() {
				@Override
				protected void updateItem(HyphenClassInChooser item, boolean empty) {
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
							setText(item.getClassTextForComboBox());
							break;
						}
						// Include the "Remove' option only when some item has been selected
						HyphenClassInChooser selectedHyphenClass = (HyphenClassInChooser) cb.getSelectionModel()
								.getSelectedItem();
						if (selectedHyphenClass != null) {
							addOptionToComboBox(removeHCIC, cb);
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
	protected StringConverter<HyphenClassInChooser> renderSelectedHCInComboBox() {
		return new StringConverter<HyphenClassInChooser>() {
			public String toString(HyphenClassInChooser hypClass) {
				if (hypClass == null) {
					return null;
				} else {
					return hypClass.getClassName();
				}
			}

			@Override
			public HyphenClassInChooser fromString(String hyphenClassString) {
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
		insertHereHCIC = new HyphenClassInChooser(hyphenApproach.getInsertHereHC(), false);
		insertHereHCIC.setDescription(bundle.getString("hyphen.view.hyphenchangerules.inserthyphenafter"));
		hyphenClassesInChooser.clear();
		for (ObservableList<HyphenClassInChooser> ol : comboBoxDataList) {
			setComboBoxData(ol);
		}
	}

	protected void setComboBoxData(ObservableList<HyphenClassInChooser> cbData) {
		addHyphenClassesToComboBoxData(cbData, false);
		if (isChange) {
			cbData.add(insertHereHCIC);
		}
		cbData.add(wordBoundaryHCIC);
		if (isChange) {
			// now add the classes again but as the "do not match class again" ones
			addHyphenClassesToComboBoxData(cbData, true);
		}
	}

	protected void addHyphenClassesToComboBoxData(ObservableList<HyphenClassInChooser> cbData,
			boolean doNotMatchClassAgain) {
		for (HyphenClass hc : hyphenApproach.getActiveHyphenClasses()) {
			HyphenClassInChooser hcic = new HyphenClassInChooser(hc, doNotMatchClassAgain);
			cbData.add(hcic);
		}
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
		getHyphenClassesFromComboBoxes(hyphenClassesInChooser);
		hyphenClasses.clear();
		for (HyphenClassInChooser hcic : hyphenClassesInChooser) {
			hyphenClasses.add(hcic.getHyphenClass());
		}
		okClicked = true;
		dialogStage.close();
	}

	// is public for unit testing
	public void getHyphenClassesFromComboBoxes(ObservableList<HyphenClassInChooser> currentlySelectedHyphenClasses) {
		currentlySelectedHyphenClasses.clear();
		changeRule.setWordInitial(false);
		changeRule.setWordFinal(false);
		for (ComboBox<HyphenClassInChooser> cb : comboBoxList) {
			if (cb.isVisible()) {
				HyphenClassInChooser selectedHyphenClass = (HyphenClassInChooser) cb.getSelectionModel()
						.getSelectedItem();
				if (selectedHyphenClass != null) {
					HyphenClassInChooser hcic = cb.getValue();
					if (selectedHyphenClass.getSegmentsRepresentation() == Constants.SPECIAL_WORD_BOUNDARY_CODE) {
						if (Constants.FIRST_COMBO_BOX_IN_SYLLABLE_PATTERN.equals(cb.getId())) {
							changeRule.setWordInitial(true);
						} else {
							changeRule.setWordFinal(true);
						}
					} else if (selectedHyphenClass.getSegmentsRepresentation() == Constants.SPECIAL_INSERT_CODE) {
						currentlySelectedHyphenClasses.add(insertHereHCIC);
					} else {
						currentlySelectedHyphenClasses.add(hcic);
					}
					if (isChange) {
						// if the class is one of the "do no match again" ones, set its boolean in the rule
						int j = comboBoxList.indexOf(cb);
						if (changeRule.getDoNotMatchClassAgains().size() <= j) {
							changeRule.getDoNotMatchClassAgains().add(hcic.isDoNotMatchClassAgain());
						} else {
							changeRule.getDoNotMatchClassAgains().set(j, hcic.isDoNotMatchClassAgain());
						}
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
			for (ComboBox<HyphenClassInChooser> cb : comboBoxList) {
				if (changeRule.isWordInitial()
						&& Constants.FIRST_COMBO_BOX_IN_SYLLABLE_PATTERN.equals(cb.getId())) {
					cb.setValue(wordBoundaryHCIC);
					cb.setVisible(true);
				} else {
					cb.setValue(hyphenClassesInChooser.get(iCurrentHyphenClass++));
					if (iCurrentHyphenClass < comboBoxList.size()) {
						comboBoxList.get(iCurrentHyphenClass).setVisible(true);
					}
					if (iCurrentHyphenClass >= iHyphenClassesInPattern) {
						if (changeRule.isWordInitial()) {
							comboBoxList.get(++iCurrentHyphenClass).setVisible(true);
						}
						if (changeRule.isWordFinal()) {
							cb = comboBoxList.get(iCurrentHyphenClass);
							cb.setValue(wordBoundaryHCIC);
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

	void setCurrentCVNaturalClass(HyphenClassInChooser naturalClass) {
	}

	// ComboBox getter is for unit testing
	public ComboBox<HyphenClassInChooser> getComboBox(int index) {
		return comboBoxList.get(index);
	}

	protected void clearOptionFromComboBox(HyphenClassInChooser option, ComboBox<HyphenClassInChooser> cb) {
		int i = comboBoxList.indexOf(cb);
		ObservableList<HyphenClassInChooser> ol = comboBoxDataList.get(i);
		if (ol.contains(option)) {
			ol.remove(option);
		}
	}

}
