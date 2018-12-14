// Copyright (c) 2018 SIL International 
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
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVSyllablePattern;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

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
public class SHNaturalClassChooserController implements Initializable {
	@FXML
	private Label labelSequence;
	@FXML
	private List<ComboBox<SHNaturalClass>> comboBoxList;
	private List<ObservableList<SHNaturalClass>> comboBoxDataList = new ArrayList<ObservableList<SHNaturalClass>>();
	Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;

	private SHApproach shApproach;
	private CVSyllablePattern syllablePattern;

	private String sSequencePrompt;
	ResourceBundle bundle;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		sSequencePrompt = resources.getString("cv.view.syllablepatterns.ncsequence");

		int i = 0;
		for (ComboBox<SHNaturalClass> cb : comboBoxList) {
			ObservableList<SHNaturalClass> ol = FXCollections.observableArrayList();
			comboBoxDataList.add(ol);
			cb.setItems(comboBoxDataList.get(i++));
			cb.setCellFactory(renderNCsInComboBox(cb));
			cb.setConverter(renderSelectedNCInCombox());
			if (i < comboBoxList.size()) {
				ComboBox<SHNaturalClass> cbNext = comboBoxList.get(i);
				handleComboBoxSelectionEvent(cb, cbNext);
			}
		}
		comboBoxList.get(comboBoxList.size() - 1).setOnAction((event) -> {
			labelSequence.setText(getNaturalClassSequenceFromComboBoxes());
		});
	}

	private void handleComboBoxSelectionEvent(ComboBox<SHNaturalClass> cb,
			ComboBox<SHNaturalClass> cbNext) {
		cb.setOnAction((event) -> {
			SHNaturalClass nc = cb.getValue();
			if (nc != null) {
				{
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							labelSequence.setText(getNaturalClassSequenceFromComboBoxes());
							cbNext.setVisible(true);
						}
					});
				}
			}
		});
	}

	// is public for unit testing
	public void removeContentFromComboBox(ComboBox<SHNaturalClass> cb) {
		int i = comboBoxList.indexOf(cb);
		// shift values to the left
		while ((i + 1) < comboBoxList.size() && comboBoxList.get(i + 1).isVisible()) {
			comboBoxList.get(i).setValue(comboBoxList.get(i + 1).getValue());
			i++;
		}
		// set next to last one to no longer have a remove option
		ComboBox<SHNaturalClass> cbi = comboBoxList.get(i - 1);
		// no longer show final one
		if (i < comboBoxList.size() && comboBoxList.get(i).isVisible()) {
			comboBoxList.get(i).setVisible(false);
		}
	}

	// is public for unit testing
	public void makeAllFollowingComboBoxesInvisible(ComboBox<SHNaturalClass> cb) {
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
			for (ComboBox<SHNaturalClass> cb : comboBoxList) {
				if (cb.equals(comboBoxList.get(0))) {
					sb.append(getNaturalClassNameToShow(cb.getSelectionModel().getSelectedItem()));
				} else {
					getComboBoxSelectedNaturalClassName(cb, sb);
				}
			}
		}
		return sb.toString();
	}

	protected void getComboBoxSelectedNaturalClassName(ComboBox<SHNaturalClass> cb, StringBuilder sb) {
		if (cb.isVisible()) {
			sb.append(" ");
			SHNaturalClass selectedNaturalClass = (SHNaturalClass) cb.getSelectionModel()
					.getSelectedItem();
			if (selectedNaturalClass != null) {
				sb.append(getNaturalClassNameToShow(selectedNaturalClass));
			}
		}
	}

	private String getNaturalClassNameToShow(SHNaturalClass nc) {
			return nc.getNCName();
	}

	// Define rendering of the list of values in ComboBox drop down.
	protected Callback<ListView<SHNaturalClass>, ListCell<SHNaturalClass>> renderNCsInComboBox(ComboBox<SHNaturalClass> cb) {
		return (comboBox) -> {
			return new ListCell<SHNaturalClass>() {
				@Override
				protected void updateItem(SHNaturalClass item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
					} else {
						String sCode = item.getSegmentsRepresentation();
							setText(item.getNCName() + " - " + item.getDescription());
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
	protected StringConverter<SHNaturalClass> renderSelectedNCInCombox() {
		return new StringConverter<SHNaturalClass>() {
			public String toString(SHNaturalClass natClass) {
				if (natClass == null) {
					return null;
				} else {
					return natClass.getNCName();
				}
			}

			@Override
			public SHNaturalClass fromString(String naturalClassString) {
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
	public void setData(SHApproach shApproachData) {
		shApproach = shApproachData;
		for (ObservableList<SHNaturalClass> ol : comboBoxDataList) {
			setComboBoxData(ol);
		}
	}

	protected void setComboBoxData(ObservableList<SHNaturalClass> cbData) {
		cbData.addAll(shApproach.getSHSonorityHierarchy());
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
		for (ComboBox<SHNaturalClass> cb : comboBoxList) {
			if (cb.isVisible()) {
				SHNaturalClass selectedNaturalClass = (SHNaturalClass) cb.getSelectionModel()
						.getSelectedItem();
				if (selectedNaturalClass != null) {
						int i = SHNaturalClass.findIndexInListByUuid(
								shApproach.getSHSonorityHierarchy(), selectedNaturalClass.getID());
//						syllablePattern.getNCs().add(shApproach.getSHSonorityHierarchy().get(i));
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

//	public void setSyllablePattern(CVSyllablePattern syllablePattern) {
//		this.syllablePattern = syllablePattern;
//		// Following assumes that setData() has been called
//		ObservableList<SHNaturalClass> currentySetNCs = syllablePattern.getNCs();
//
//		int iNaturalClassesInPattern = currentySetNCs.size();
//		int iCurrentNaturalClass = 0;
//		if (iNaturalClassesInPattern > 0) {
//			for (ComboBox<SHNaturalClass> cb : comboBoxList) {
//				if (syllablePattern.isWordInitial()
//						&& Constants.FIRST_COMBO_BOX_IN_SYLLABLE_PATTERN.equals(cb.getId())) {
//					cb.setValue(wordBoundaryNC);
//					cb.setVisible(true);
//				} else {
//					cb.setValue(currentySetNCs.get(iCurrentNaturalClass++));
//					if (iCurrentNaturalClass < comboBoxList.size()) {
//						comboBoxList.get(iCurrentNaturalClass).setVisible(true);
//					}
//					if (iCurrentNaturalClass >= iNaturalClassesInPattern) {
//						if (syllablePattern.isWordInitial()) {
//							comboBoxList.get(++iCurrentNaturalClass).setVisible(true);
//						}
//						if (syllablePattern.isWordFinal()) {
//							cb = comboBoxList.get(iCurrentNaturalClass);
//							cb.setValue(wordBoundaryNC);
//							cb.setVisible(true);
//						}
//						break;
//					}
//				}
//			}
//			labelSequence.setText(getNaturalClassSequenceFromComboBoxes());
//		}
//	}

	public void setSyllablePatternForUnitTesting(CVSyllablePattern syllablePattern) {
		this.syllablePattern = syllablePattern;
	}

	void setCurrentCVNaturalClass(SHNaturalClass naturalClass) {
	}

	// ComboBox getter is for unit testing
	public ComboBox<SHNaturalClass> getComboBox(int index) {
		return comboBoxList.get(index);
	}

}
