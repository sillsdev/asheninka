/**
 * 
 */
package sil.org.syllableparser.view;

import java.net.URL;
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
// TODO: is there a way to use an array for all of these combo boxes?  Will the fxml still work?
	@FXML
	private Label labelSequence;
	@FXML
	private ComboBox<CVNaturalClass> cb1;
	private ObservableList<CVNaturalClass> cb1Data = FXCollections.observableArrayList();
	@FXML
	private ComboBox<CVNaturalClass> cb2;
	private ObservableList<CVNaturalClass> cb2Data = FXCollections.observableArrayList();
	@FXML
	private ComboBox<CVNaturalClass> cb3;
	private ObservableList<CVNaturalClass> cb3Data = FXCollections.observableArrayList();
	@FXML
	private ComboBox<CVNaturalClass> cb4;
	private ObservableList<CVNaturalClass> cb4Data = FXCollections.observableArrayList();
	@FXML
	private ComboBox<CVNaturalClass> cb5;
	private ObservableList<CVNaturalClass> cb5Data = FXCollections.observableArrayList();
	@FXML
	private ComboBox<CVNaturalClass> cb6;
	private ObservableList<CVNaturalClass> cb6Data = FXCollections.observableArrayList();
	@FXML
	private ComboBox<CVNaturalClass> cb7;
	private ObservableList<CVNaturalClass> cb7Data = FXCollections.observableArrayList();
	@FXML
	private ComboBox<CVNaturalClass> cb8;
	private ObservableList<CVNaturalClass> cb8Data = FXCollections.observableArrayList();
	@FXML
	private ComboBox<CVNaturalClass> cb9;
	private ObservableList<CVNaturalClass> cb9Data = FXCollections.observableArrayList();
	@FXML
	private ComboBox<CVNaturalClass> cb10;
	private ObservableList<CVNaturalClass> cb10Data = FXCollections.observableArrayList();
	@FXML
	private ComboBox<CVNaturalClass> cb11;
	private ObservableList<CVNaturalClass> cb11Data = FXCollections.observableArrayList();
	@FXML
	private ComboBox<CVNaturalClass> cb12;
	private ObservableList<CVNaturalClass> cb12Data = FXCollections.observableArrayList();
	@FXML
	private ComboBox<CVNaturalClass> cb13;
	private ObservableList<CVNaturalClass> cb13Data = FXCollections.observableArrayList();
	@FXML
	private ComboBox<CVNaturalClass> cb14;
	private ObservableList<CVNaturalClass> cb14Data = FXCollections.observableArrayList();
	@FXML
	private ComboBox<CVNaturalClass> cb15;
	private ObservableList<CVNaturalClass> cb15Data = FXCollections.observableArrayList();
	@FXML
	private ComboBox<CVNaturalClass> cb16;
	private ObservableList<CVNaturalClass> cb16Data = FXCollections.observableArrayList();
	@FXML
	private ComboBox<CVNaturalClass> cb17;
	private ObservableList<CVNaturalClass> cb17Data = FXCollections.observableArrayList();
	@FXML
	private ComboBox<CVNaturalClass> cb18;
	private ObservableList<CVNaturalClass> cb18Data = FXCollections.observableArrayList();
	@FXML
	private ComboBox<CVNaturalClass> cb19;
	private ObservableList<CVNaturalClass> cb19Data = FXCollections.observableArrayList();

	Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;

	private CVApproach cvApproach;
	private CVNaturalClass currentNaturalClass;
	private CVSyllablePattern syllablePattern;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		cb1.setItems(cb1Data);
		cb2.setItems(cb2Data);
		cb3.setItems(cb3Data);
		cb4.setItems(cb4Data);
		cb5.setItems(cb5Data);
		cb6.setItems(cb6Data);
		cb7.setItems(cb7Data);
		cb8.setItems(cb8Data);
		cb9.setItems(cb9Data);
		cb10.setItems(cb1Data);
		cb11.setItems(cb1Data);
		cb12.setItems(cb2Data);
		cb13.setItems(cb3Data);
		cb14.setItems(cb4Data);
		cb15.setItems(cb5Data);
		cb16.setItems(cb6Data);
		cb17.setItems(cb7Data);
		cb18.setItems(cb8Data);
		cb19.setItems(cb9Data);

		cb1.setCellFactory(renderNCsInComboBox());
		cb1.setConverter(renderSelectedNCInCombox());
		cb2.setCellFactory(renderNCsInComboBox());
		cb2.setConverter(renderSelectedNCInCombox());
		cb3.setCellFactory(renderNCsInComboBox());
		cb3.setConverter(renderSelectedNCInCombox());
		cb4.setCellFactory(renderNCsInComboBox());
		cb4.setConverter(renderSelectedNCInCombox());
		cb5.setCellFactory(renderNCsInComboBox());
		cb5.setConverter(renderSelectedNCInCombox());
		cb6.setCellFactory(renderNCsInComboBox());
		cb6.setConverter(renderSelectedNCInCombox());
		cb7.setCellFactory(renderNCsInComboBox());
		cb7.setConverter(renderSelectedNCInCombox());
		cb8.setCellFactory(renderNCsInComboBox());
		cb8.setConverter(renderSelectedNCInCombox());
		cb9.setCellFactory(renderNCsInComboBox());
		cb9.setConverter(renderSelectedNCInCombox());
		cb10.setCellFactory(renderNCsInComboBox());
		cb10.setConverter(renderSelectedNCInCombox());
		cb11.setCellFactory(renderNCsInComboBox());
		cb11.setConverter(renderSelectedNCInCombox());
		cb12.setCellFactory(renderNCsInComboBox());
		cb12.setConverter(renderSelectedNCInCombox());
		cb13.setCellFactory(renderNCsInComboBox());
		cb13.setConverter(renderSelectedNCInCombox());
		cb14.setCellFactory(renderNCsInComboBox());
		cb14.setConverter(renderSelectedNCInCombox());
		cb15.setCellFactory(renderNCsInComboBox());
		cb15.setConverter(renderSelectedNCInCombox());
		cb16.setCellFactory(renderNCsInComboBox());
		cb16.setConverter(renderSelectedNCInCombox());
		cb17.setCellFactory(renderNCsInComboBox());
		cb17.setConverter(renderSelectedNCInCombox());
		cb18.setCellFactory(renderNCsInComboBox());
		cb18.setConverter(renderSelectedNCInCombox());
		cb19.setCellFactory(renderNCsInComboBox());
		cb19.setConverter(renderSelectedNCInCombox());

		// Handle ComboBox event.
		handleComboBoxSelectionEvent(cb1, cb2);
		handleComboBoxSelectionEvent(cb2, cb3);
		handleComboBoxSelectionEvent(cb3, cb4);
		handleComboBoxSelectionEvent(cb4, cb5);
		handleComboBoxSelectionEvent(cb5, cb6);
		handleComboBoxSelectionEvent(cb6, cb7);
		handleComboBoxSelectionEvent(cb7, cb8);
		handleComboBoxSelectionEvent(cb8, cb9);
		handleComboBoxSelectionEvent(cb9, cb10);
		handleComboBoxSelectionEvent(cb10, cb11);
		handleComboBoxSelectionEvent(cb11, cb12);
		handleComboBoxSelectionEvent(cb12, cb13);
		handleComboBoxSelectionEvent(cb13, cb14);
		handleComboBoxSelectionEvent(cb14, cb15);
		handleComboBoxSelectionEvent(cb15, cb16);
		handleComboBoxSelectionEvent(cb16, cb17);
		handleComboBoxSelectionEvent(cb17, cb18);
		handleComboBoxSelectionEvent(cb18, cb19);
		cb19.setOnAction((event) -> {
			labelSequence.setText(getNaturalClassSequenceFromComboBoxes());
		});
	}

	private void handleComboBoxSelectionEvent(ComboBox<CVNaturalClass> cb,
			ComboBox<CVNaturalClass> cbNext) {
		cb.setOnAction((event) -> {
			labelSequence.setText(getNaturalClassSequenceFromComboBoxes());
			cbNext.setVisible(true);
		});

	}

	private String getNaturalClassSequenceFromComboBoxes() {
		StringBuilder sb = new StringBuilder();
		if (cb1.isVisible()) {
			sb.append(cb1.getSelectionModel().getSelectedItem().getNCName());
		}
		getComboBoxSelectedNaturalClassName(cb2, sb);
		getComboBoxSelectedNaturalClassName(cb3, sb);
		getComboBoxSelectedNaturalClassName(cb4, sb);
		getComboBoxSelectedNaturalClassName(cb5, sb);
		getComboBoxSelectedNaturalClassName(cb6, sb);
		getComboBoxSelectedNaturalClassName(cb7, sb);
		getComboBoxSelectedNaturalClassName(cb8, sb);
		getComboBoxSelectedNaturalClassName(cb9, sb);
		getComboBoxSelectedNaturalClassName(cb10, sb);
		getComboBoxSelectedNaturalClassName(cb11, sb);
		getComboBoxSelectedNaturalClassName(cb12, sb);
		getComboBoxSelectedNaturalClassName(cb13, sb);
		getComboBoxSelectedNaturalClassName(cb14, sb);
		getComboBoxSelectedNaturalClassName(cb15, sb);
		getComboBoxSelectedNaturalClassName(cb16, sb);
		getComboBoxSelectedNaturalClassName(cb17, sb);
		getComboBoxSelectedNaturalClassName(cb18, sb);
		getComboBoxSelectedNaturalClassName(cb19, sb);
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
						setText(item.getNCName() + " - " + item.getDescription());
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
			public CVNaturalClass fromString(String personString) {
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
		cb1Data.addAll(cvApproachData.getCVNaturalClasses());
		cb2Data.addAll(cvApproachData.getCVNaturalClasses());
		cb3Data.addAll(cvApproachData.getCVNaturalClasses());
		cb4Data.addAll(cvApproachData.getCVNaturalClasses());
		cb5Data.addAll(cvApproachData.getCVNaturalClasses());
		cb6Data.addAll(cvApproachData.getCVNaturalClasses());
		cb7Data.addAll(cvApproachData.getCVNaturalClasses());
		cb8Data.addAll(cvApproachData.getCVNaturalClasses());
		cb9Data.addAll(cvApproachData.getCVNaturalClasses());
		cb10Data.addAll(cvApproachData.getCVNaturalClasses());
		cb11Data.addAll(cvApproachData.getCVNaturalClasses());
		cb12Data.addAll(cvApproachData.getCVNaturalClasses());
		cb13Data.addAll(cvApproachData.getCVNaturalClasses());
		cb14Data.addAll(cvApproachData.getCVNaturalClasses());
		cb15Data.addAll(cvApproachData.getCVNaturalClasses());
		cb16Data.addAll(cvApproachData.getCVNaturalClasses());
		cb17Data.addAll(cvApproachData.getCVNaturalClasses());
		cb18Data.addAll(cvApproachData.getCVNaturalClasses());
		cb19Data.addAll(cvApproachData.getCVNaturalClasses());

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
		getNaturalClassFromComboBox(cb1);
		getNaturalClassFromComboBox(cb2);
		getNaturalClassFromComboBox(cb3);
		getNaturalClassFromComboBox(cb4);
		getNaturalClassFromComboBox(cb5);
		getNaturalClassFromComboBox(cb6);
		getNaturalClassFromComboBox(cb7);
		getNaturalClassFromComboBox(cb8);
		getNaturalClassFromComboBox(cb9);
		getNaturalClassFromComboBox(cb10);
		getNaturalClassFromComboBox(cb11);
		getNaturalClassFromComboBox(cb12);
		getNaturalClassFromComboBox(cb13);
		getNaturalClassFromComboBox(cb14);
		getNaturalClassFromComboBox(cb15);
		getNaturalClassFromComboBox(cb16);
		getNaturalClassFromComboBox(cb17);
		getNaturalClassFromComboBox(cb18);
		getNaturalClassFromComboBox(cb19);

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
		int i = currentySetNCs.size() - 1;
		if (i >= 0) {
			cb1.setValue(currentySetNCs.get(0));
			cb2.setVisible(true);
			if (i >= 1) {
				cb2.setValue(currentySetNCs.get(1));
				cb3.setVisible(true);
				if (i >= 2) {
					cb3.setValue(currentySetNCs.get(2));
					cb4.setVisible(true);
					if (i >= 3) {
						cb4.setValue(currentySetNCs.get(3));
						cb5.setVisible(true);
						if (i >= 4) {
							cb5.setValue(currentySetNCs.get(4));
							cb6.setVisible(true);
							if (i >= 5) {
								cb6.setValue(currentySetNCs.get(5));
								cb7.setVisible(true);
								if (i >= 6) {
									cb7.setValue(currentySetNCs.get(6));
									cb8.setVisible(true);
									if (i >= 7) {
										cb8.setValue(currentySetNCs.get(7));
										cb9.setVisible(true);
										if (i >= 8) {
											cb9.setValue(currentySetNCs.get(8));
											cb10.setVisible(true);
											if (i >= 9) {
												cb10.setValue(currentySetNCs.get(9));
												cb11.setVisible(true);
												if (i >= 10) {
													cb11.setValue(currentySetNCs.get(10));
													cb12.setVisible(true);
													if (i >= 11) {
														cb12.setValue(currentySetNCs.get(11));
														cb13.setVisible(true);
														if (i >= 12) {
															cb13.setValue(currentySetNCs.get(12));
															cb14.setVisible(true);
															if (i >= 13) {
																cb14.setValue(currentySetNCs
																		.get(13));
																cb15.setVisible(true);
																if (i >= 14) {
																	cb15.setValue(currentySetNCs
																			.get(14));
																	cb16.setVisible(true);
																	if (i >= 15) {
																		cb16.setValue(currentySetNCs
																				.get(15));
																		cb17.setVisible(true);
																		if (i >= 16) {
																			cb17.setValue(currentySetNCs
																					.get(16));
																			cb18.setVisible(true);
																			if (i >= 17) {
																				cb18.setValue(currentySetNCs
																						.get(17));
																				cb19.setVisible(true);
																				if (i >= 18) {
																					cb19.setValue(currentySetNCs
																							.get(18));
																				}
																			}
																		}
																	}
																}
															}

														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			labelSequence.setText(getNaturalClassSequenceFromComboBoxes());
		}
	}

	void setCurrentCVNaturalClass(CVNaturalClass naturalClass) {
		currentNaturalClass = naturalClass;
	}

}
