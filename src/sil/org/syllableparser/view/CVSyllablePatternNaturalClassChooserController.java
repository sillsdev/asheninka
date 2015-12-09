/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.syllableparser.model.cvapproach.CVSyllablePattern;
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
public class CVSyllablePatternNaturalClassChooserController implements Initializable {
	@FXML
	private Label labelSequence;
	@FXML
	private List<ComboBox<CVNaturalClass>> comboBoxList;
	private List<ObservableList<CVNaturalClass>> comboBoxDataList = new ArrayList<ObservableList<CVNaturalClass>>();
	Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;

	private CVApproach cvApproach;
	private CVSyllablePattern syllablePattern;

	private CVNaturalClass removeNC;
	private CVNaturalClass wordBoundaryNC;
	private String sSequencePrompt;
	// want unique strings for the next two so we can be sure we get the correct
	// one
	private static String kSpecialRemoveCode = "Asheninka!@#RemoveCode";
	private static String kSpecialWordBoundaryCode = "Asheninka!@#WordBoundaryCode";

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {

		removeNC = new CVNaturalClass(resources.getString("cv.view.syllablepatterns.remove"), null,
				"", kSpecialRemoveCode);
		wordBoundaryNC = new CVNaturalClass(
				resources.getString("cv.view.syllablepatterns.wordboundary"), null, "",
				kSpecialWordBoundaryCode);
		sSequencePrompt = resources.getString("cv.view.syllablepatterns.ncsequence");

		int i = 0;
		for (ComboBox<CVNaturalClass> cb : comboBoxList) {
			ObservableList<CVNaturalClass> ol = FXCollections.observableArrayList();
			comboBoxDataList.add(ol);
			cb.setItems(comboBoxDataList.get(i++));
			cb.setCellFactory(renderNCsInComboBox());
			cb.setConverter(renderSelectedNCInCombox());
			if (i < comboBoxList.size()) {
				ComboBox<CVNaturalClass> cbNext = comboBoxList.get(i);
				handleComboBoxSelectionEvent(cb, cbNext);
			}
		}
		comboBoxList.get(comboBoxList.size() - 1).setOnAction((event) -> {
			labelSequence.setText(getNaturalClassSequenceFromComboBoxes());
		});
	}

	private void handleComboBoxSelectionEvent(ComboBox<CVNaturalClass> cb,
			ComboBox<CVNaturalClass> cbNext) {
		cb.setOnAction((event) -> {
			CVNaturalClass nc = cb.getValue();
			if (nc != null) {
				if (nc.getSNCRepresentation() == kSpecialRemoveCode) {
					// if we merely invoke the remove and update label code
					// directly,
					// we get an IndexOutOfBoundsException. This is because
					// "In JavaFX, you cannot change the contents of an
					// ObservableList
					// while a change is already in progress." See
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
				} else if (nc.getSNCRepresentation() == kSpecialWordBoundaryCode) {
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

	public void addRemoveOptionToComboBox(ComboBox<CVNaturalClass> cb) {
		int i = comboBoxList.indexOf(cb);
		ObservableList<CVNaturalClass> ol = comboBoxDataList.get(i);
		if (!ol.contains(removeNC)) {
			ol.add(removeNC);
		}
	}

	// is public for unit testing
	public void removeContentFromComboBox(ComboBox<CVNaturalClass> cb) {
		int i = comboBoxList.indexOf(cb);
		// shift values to the left
		while ((i + 1) < comboBoxList.size() && comboBoxList.get(i + 1).isVisible()) {
			comboBoxList.get(i).setValue(comboBoxList.get(i + 1).getValue());
			i++;
		}
		// set next to last one to no longer have a remove option
		ComboBox<CVNaturalClass> cbi = comboBoxList.get(i - 1);
		clearRemoveOptionFromComboBox(cbi);
		// no longer show final one
		if (i < comboBoxList.size() && comboBoxList.get(i).isVisible()) {
			comboBoxList.get(i).setVisible(false);
		}
	}

	// is public for unit testing
	public void makeAllFollowingComboBoxesInvisible(ComboBox<CVNaturalClass> cb) {
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
			for (ComboBox<CVNaturalClass> cb : comboBoxList) {
				if (cb.equals(comboBoxList.get(0))) {
					sb.append(getNaturalClassNameToShow(cb.getSelectionModel().getSelectedItem()));
				} else {
					getComboBoxSelectedNaturalClassName(cb, sb);
				}
			}
		}
		return sb.toString();
	}

	protected void getComboBoxSelectedNaturalClassName(ComboBox<CVNaturalClass> cb, StringBuilder sb) {
		if (cb.isVisible()) {
			sb.append(" ");
			CVNaturalClass selectedNaturalClass = (CVNaturalClass) cb.getSelectionModel()
					.getSelectedItem();
			if (selectedNaturalClass != null) {
				sb.append(getNaturalClassNameToShow(selectedNaturalClass));
			}
		}
	}

	private String getNaturalClassNameToShow(CVNaturalClass nc) {
		if (nc.getSNCRepresentation() == kSpecialWordBoundaryCode) {
			return Constants.WORD_BOUNDARY_SYMBOL;
		} else {
			return nc.getNCName();
		}
	}

	// Define rendering of the list of values in ComboBox drop down.
	protected Callback<ListView<CVNaturalClass>, ListCell<CVNaturalClass>> renderNCsInComboBox() {
		return (comboBox) -> {
			return new ListCell<CVNaturalClass>() {
				@Override
				protected void updateItem(CVNaturalClass item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
					} else {
						String sCode = item.getSNCRepresentation();
						if (sCode != kSpecialRemoveCode && sCode != kSpecialWordBoundaryCode) {
							setText(item.getNCName() + " - " + item.getDescription());
						} else {
							setText(item.getNCName());
						}
					}
				}
			};
		};
	}

	// Define rendering of selected value shown in ComboBox.
	protected StringConverter<CVNaturalClass> renderSelectedNCInCombox() {
		return new StringConverter<CVNaturalClass>() {
			public String toString(CVNaturalClass natClass) {
				if (natClass == null) {
					return null;
				} else {
					return natClass.getNCName();
				}
			}

			@Override
			public CVNaturalClass fromString(String naturalClassString) {
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
	public void setData(CVApproach cvApproachData) {
		cvApproach = cvApproachData;
		for (ObservableList<CVNaturalClass> ol : comboBoxDataList) {
			setComboBoxData(ol);
		}
	}

	protected void setComboBoxData(ObservableList<CVNaturalClass> cbData) {
		cbData.addAll(cvApproach.getCVNaturalClasses());
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
		syllablePattern.getNCs().clear();
		syllablePattern.setWordInitial(false);
		syllablePattern.setWordFinal(false);
		for (ComboBox<CVNaturalClass> cb : comboBoxList) {
			if (cb.isVisible()) {
				CVNaturalClass selectedNaturalClass = (CVNaturalClass) cb.getSelectionModel()
						.getSelectedItem();
				if (selectedNaturalClass != null) {
					if (selectedNaturalClass.getSNCRepresentation() == kSpecialWordBoundaryCode) {
						if (Constants.FIRST_COMBO_BOX_IN_SYLLABLE_PATTERN.equals(cb.getId())) {
							syllablePattern.setWordInitial(true);
						} else {
							syllablePattern.setWordFinal(true);
						}
					} else {
						int i = CVNaturalClass.findIndexInNaturaClassListByUuid(
								cvApproach.getCVNaturalClasses(), selectedNaturalClass.getID());
						syllablePattern.getNCs().add(cvApproach.getCVNaturalClasses().get(i));
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

	public CVSyllablePattern getSyllablePattern() {
		return syllablePattern;
	}

	public void setSyllablePattern(CVSyllablePattern syllablePattern) {
		this.syllablePattern = syllablePattern;
		// Following assumes that setData() has been called
		ObservableList<CVNaturalClass> currentySetNCs = syllablePattern.getNCs();

		int iNaturalClassesInPattern = currentySetNCs.size();
		int iCurrentNaturalClass = 0;
		if (iNaturalClassesInPattern > 0) {
			for (ComboBox<CVNaturalClass> cb : comboBoxList) {
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
						if (syllablePattern.isWordFinal()) {
							cb = comboBoxList.get(iCurrentNaturalClass + 1);
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

	public void setSyllablePatternForUnitTesting(CVSyllablePattern syllablePattern) {
		this.syllablePattern = syllablePattern;
	}

	void setCurrentCVNaturalClass(CVNaturalClass naturalClass) {
	}

	// ComboBox getter is for unit testing
	public ComboBox<CVNaturalClass> getComboBox(int index) {
		return comboBoxList.get(index);
	}

	protected void clearRemoveOptionFromComboBox(ComboBox<CVNaturalClass> cb) {
		int i = comboBoxList.indexOf(cb);
		ObservableList<CVNaturalClass> ol = comboBoxDataList.get(i);
		if (ol.contains(removeNC)) {
			ol.remove(removeNC);
		}
	}

}
