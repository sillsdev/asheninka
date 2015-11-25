/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.syllableparser.model.cvapproach.CVSyllablePattern;
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
			if (nc.getSNCRepresentation() == kSpecialRemoveCode) {
				System.out.println("remove found");
				removeContentFrom(cb);
			} else if (nc.getSNCRepresentation() == kSpecialWordBoundaryCode) {
				System.out.println("word boundary found");
			} else {
				labelSequence.setText(getNaturalClassSequenceFromComboBoxes());
				cbNext.setVisible(true);
			}
		});

	}

	public void removeContentFrom(ComboBox<CVNaturalClass> cb) {

	}

	// is public for unit testing
	public String getNaturalClassSequenceFromComboBoxes() {
		StringBuilder sb = new StringBuilder();
		for (ComboBox<CVNaturalClass> cb : comboBoxList) {
			if (cb.equals(comboBoxList.get(0))) {
				sb.append(cb.getSelectionModel().getSelectedItem().getNCName());
			} else {
				getComboBoxSelectedNaturalClassName(cb, sb);
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
				sb.append(selectedNaturalClass.getNCName());
			}
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
		cbData.add(removeNC);
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
		syllablePattern.getNCs().clear();
		for (ComboBox<CVNaturalClass> cb : comboBoxList) {
			getNaturalClassFromComboBox(cb);
		}

		okClicked = true;
		dialogStage.close();
	}

	protected void getNaturalClassFromComboBox(ComboBox<CVNaturalClass> cb) {
		if (cb.isVisible()) {
			CVNaturalClass selectedNaturalClass = (CVNaturalClass) cb.getSelectionModel()
					.getSelectedItem();
			if (selectedNaturalClass != null) {
				int i = CVNaturalClass.findIndexInNaturaClassListByUuid(
						cvApproach.getCVNaturalClasses(), selectedNaturalClass.getID());
				syllablePattern.getNCs().add(cvApproach.getCVNaturalClasses().get(i));
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
				cb.setValue(currentySetNCs.get(iCurrentNaturalClass++));
				if (iCurrentNaturalClass < comboBoxList.size()) {
					comboBoxList.get(iCurrentNaturalClass).setVisible(true);
				}
				if (iCurrentNaturalClass >= iNaturalClassesInPattern) {
					break;
				}

			}
			labelSequence.setText(getNaturalClassSequenceFromComboBoxes());
		}
	}

	void setCurrentCVNaturalClass(CVNaturalClass naturalClass) {
	}

	// ComboBox getter is for unit testing
	public ComboBox<CVNaturalClass> getComboBox(int index) {
		return comboBoxList.get(index);
	}

}
