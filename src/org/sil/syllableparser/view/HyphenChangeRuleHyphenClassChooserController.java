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

	private HyphenApproach cvApproach;
	private HyphenChangeRule syllablePattern;

	private HyphenClass removeNC;
	private HyphenClass wordBoundaryNC;
	private String sSequencePrompt;
	ResourceBundle bundle;
	// want unique strings for the next two so we can be sure we get the correct
	// one
	private static String kSpecialRemoveCode = "Asheninka!@#RemoveCode";
	private static String kSpecialWordBoundaryCode = "Asheninka!@#WordBoundaryCode";

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		removeNC = new HyphenClass(resources.getString("cv.view.syllablepatterns.remove"), null,
				"", kSpecialRemoveCode);
		wordBoundaryNC = new HyphenClass(
				resources.getString("cv.view.syllablepatterns.wordboundary"), null, "",
				kSpecialWordBoundaryCode);
		sSequencePrompt = resources.getString("cv.view.syllablepatterns.ncsequence");

		int i = 0;
		for (ComboBox<HyphenClass> cb : comboBoxList) {
			ObservableList<HyphenClass> ol = FXCollections.observableArrayList();
			comboBoxDataList.add(ol);
			cb.setItems(comboBoxDataList.get(i++));
			cb.setCellFactory(renderNCsInComboBox(cb));
			cb.setConverter(renderSelectedNCInCombox());
			if (i < comboBoxList.size()) {
				ComboBox<HyphenClass> cbNext = comboBoxList.get(i);
				handleComboBoxSelectionEvent(cb, cbNext);
			}
		}
		comboBoxList.get(comboBoxList.size() - 1).setOnAction((event) -> {
			labelSequence.setText(getNaturalClassSequenceFromComboBoxes());
		});
	}

	private void handleComboBoxSelectionEvent(ComboBox<HyphenClass> cb,
			ComboBox<HyphenClass> cbNext) {
		cb.setOnAction((event) -> {
			HyphenClass nc = cb.getValue();
			if (nc != null) {
				if (nc.getSegmentsRepresentation() == kSpecialRemoveCode) {
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
							labelSequence.setText(getNaturalClassSequenceFromComboBoxes());
							clearRemoveOptionFromComboBox(cb);
						}
					});
				} else if (nc.getSegmentsRepresentation() == kSpecialWordBoundaryCode) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							labelSequence.setText(getNaturalClassSequenceFromComboBoxes());
							addRemoveOptionToComboBox(cb);
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
							labelSequence.setText(getNaturalClassSequenceFromComboBoxes());
							cbNext.setVisible(true);
							addRemoveOptionToComboBox(cb);
						}
					});
				}
			}
		});
	}

	public void addRemoveOptionToComboBox(ComboBox<HyphenClass> cb) {
		int i = comboBoxList.indexOf(cb);
		ObservableList<HyphenClass> ol = comboBoxDataList.get(i);
		if (!ol.contains(removeNC)) {
			ol.add(removeNC);
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
		clearRemoveOptionFromComboBox(cbi);
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
	public String getNaturalClassSequenceFromComboBoxes() {
		StringBuilder sb = new StringBuilder();
		if (comboBoxList.get(0).getSelectionModel().getSelectedIndex() < 0) {
			sb.append(sSequencePrompt);
		} else {
			for (ComboBox<HyphenClass> cb : comboBoxList) {
				if (cb.equals(comboBoxList.get(0))) {
					sb.append(getNaturalClassNameToShow(cb.getSelectionModel().getSelectedItem()));
				} else {
					getComboBoxSelectedNaturalClassName(cb, sb);
				}
			}
		}
		return sb.toString();
	}

	protected void getComboBoxSelectedNaturalClassName(ComboBox<HyphenClass> cb, StringBuilder sb) {
		if (cb.isVisible()) {
			sb.append(" ");
			HyphenClass selectedNaturalClass = (HyphenClass) cb.getSelectionModel()
					.getSelectedItem();
			if (selectedNaturalClass != null) {
				sb.append(getNaturalClassNameToShow(selectedNaturalClass));
			}
		}
	}

	private String getNaturalClassNameToShow(HyphenClass nc) {
		if (nc.getSegmentsRepresentation() == kSpecialWordBoundaryCode) {
			return Constants.WORD_BOUNDARY_SYMBOL;
		} else {
			return nc.getClassName();
		}
	}

	// Define rendering of the list of values in ComboBox drop down.
	protected Callback<ListView<HyphenClass>, ListCell<HyphenClass>> renderNCsInComboBox(ComboBox<HyphenClass> cb) {
		return (comboBox) -> {
			return new ListCell<HyphenClass>() {
				@Override
				protected void updateItem(HyphenClass item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
					} else {
						String sCode = item.getSegmentsRepresentation();
						if (sCode != kSpecialRemoveCode && sCode != kSpecialWordBoundaryCode) {
							setText(item.getClassName() + " - " + item.getDescription());
						} else {
							setText(item.getClassName());
						}
						// Include the "Remove' option only when some item has been selected
						HyphenClass selectedNaturalClass = (HyphenClass) cb.getSelectionModel()
								.getSelectedItem();
						if (selectedNaturalClass != null) {
							addRemoveOptionToComboBox(cb);
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
	protected StringConverter<HyphenClass> renderSelectedNCInCombox() {
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
	public void setData(HyphenApproach cvApproachData) {
		cvApproach = cvApproachData;
		for (ObservableList<HyphenClass> ol : comboBoxDataList) {
			setComboBoxData(ol);
		}
	}

	protected void setComboBoxData(ObservableList<HyphenClass> cbData) {
		cbData.addAll(cvApproach.getHyphenClasses());
		cbData.add(wordBoundaryNC);
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
		getNaturalClassesFromComboBoxes();

		okClicked = true;
		dialogStage.close();
	}

	// is public for unit testing
	public void getNaturalClassesFromComboBoxes() {
		syllablePattern.getChangeHyphenClasses().clear();
		syllablePattern.setWordInitial(false);
		syllablePattern.setWordFinal(false);
		for (ComboBox<HyphenClass> cb : comboBoxList) {
			if (cb.isVisible()) {
				HyphenClass selectedNaturalClass = (HyphenClass) cb.getSelectionModel()
						.getSelectedItem();
				if (selectedNaturalClass != null) {
					if (selectedNaturalClass.getSegmentsRepresentation() == kSpecialWordBoundaryCode) {
						if (Constants.FIRST_COMBO_BOX_IN_SYLLABLE_PATTERN.equals(cb.getId())) {
							syllablePattern.setWordInitial(true);
						} else {
							syllablePattern.setWordFinal(true);
						}
					} else {
						int i = HyphenClass.findIndexInListByUuid(
								cvApproach.getHyphenClasses(), selectedNaturalClass.getID());
						syllablePattern.getChangeHyphenClasses().add(cvApproach.getHyphenClasses().get(i));
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
		return syllablePattern;
	}

	public void setSyllablePattern(HyphenChangeRule syllablePattern) {
		this.syllablePattern = syllablePattern;
		// Following assumes that setData() has been called
		ObservableList<HyphenClass> currentySetNCs = syllablePattern.getChangeHyphenClasses();

		int iNaturalClassesInPattern = currentySetNCs.size();
		int iCurrentNaturalClass = 0;
		if (iNaturalClassesInPattern > 0) {
			for (ComboBox<HyphenClass> cb : comboBoxList) {
				if (syllablePattern.isWordInitial()
						&& Constants.FIRST_COMBO_BOX_IN_SYLLABLE_PATTERN.equals(cb.getId())) {
					cb.setValue(wordBoundaryNC);
					cb.setVisible(true);
				} else {
					cb.setValue(currentySetNCs.get(iCurrentNaturalClass++));
					if (iCurrentNaturalClass < comboBoxList.size()) {
						comboBoxList.get(iCurrentNaturalClass).setVisible(true);
					}
					if (iCurrentNaturalClass >= iNaturalClassesInPattern) {
						if (syllablePattern.isWordInitial()) {
							comboBoxList.get(++iCurrentNaturalClass).setVisible(true);
						}
						if (syllablePattern.isWordFinal()) {
							cb = comboBoxList.get(iCurrentNaturalClass);
							cb.setValue(wordBoundaryNC);
							cb.setVisible(true);
						}
						break;
					}
				}
			}
			labelSequence.setText(getNaturalClassSequenceFromComboBoxes());
		}
	}

	public void setSyllablePatternForUnitTesting(HyphenChangeRule syllablePattern) {
		this.syllablePattern = syllablePattern;
	}

	void setCurrentCVNaturalClass(HyphenClass naturalClass) {
	}

	// ComboBox getter is for unit testing
	public ComboBox<HyphenClass> getComboBox(int index) {
		return comboBoxList.get(index);
	}

	protected void clearRemoveOptionFromComboBox(ComboBox<HyphenClass> cb) {
		int i = comboBoxList.indexOf(cb);
		ObservableList<HyphenClass> ol = comboBoxDataList.get(i);
		if (ol.contains(removeNC)) {
			ol.remove(removeNC);
		}
	}

}
