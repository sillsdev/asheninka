/**
 * Copyright (c) 2019-2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;

import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.TemplateFilter;
import org.sil.syllableparser.model.TemplateFilterType;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.oncapproach.ONCApproach;

/**
 * @author Andy Black
 *
 */
public class FiltersController extends TemplatesFiltersController {

	@FXML
	protected TableView<Filter> filterTable;
	@FXML
	protected ToggleGroup group;
	@FXML
	private RadioButton repairRadioButton;
	@FXML
	private RadioButton failRadioButton;

	protected ObservableList<Filter> filterList = FXCollections.observableArrayList();

	public void setData(ONCApproach oncApproachData) {
		oncApproach = oncApproachData;
		languageProject = oncApproach.getLanguageProject();
		filterList = languageProject.getFilters();
		iRepresentationCaretPosition = 6;
		fSncChoicesUsingMouse = false;

		// Add observable list data to the table
		filterTable.setItems(filterList);
		int max = filterTable.getItems().size();
		if (max > 0) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					int iLastIndex = mainApp.getApplicationPreferences()
							.getLastONCFiltersViewItemUsed();
					iLastIndex = adjustIndexValue(iLastIndex, max);
					selectAndScrollToItem(iLastIndex);
				}
			});
		}
	}

	protected void showFilterDetails(Filter tf) {
		currentTemplateFilter = tf;
		if (tf != null) {
			// Fill the text fields with info from the object.
			nameField.setText(tf.getTemplateFilterName());
			descriptionField.setText(tf.getDescription());
			fAllowSlotPosition = tf.getAction().isDoRepair();
			representationField.setText(tf.getTemplateFilterRepresentation());
			repairRadioButton.setSelected(tf.getAction().isDoRepair());
			failRadioButton.setSelected(!tf.getAction().isDoRepair());

			activeCheckBox.setSelected(tf.isActive());
			List<String> choices = languageProject.getCVApproach().getActiveCVNaturalClasses().stream()
					.map(CVNaturalClass::getNCName).collect(Collectors.toList());
			ObservableList<String> choices2 = FXCollections.observableArrayList(choices);
			sncChoicesComboBox.setItems(choices2);
			sncChoicesComboBox.setVisible(false);
			typeComboBox.getItems().setAll(TemplateFilterType.values());
			typeComboBox.getSelectionModel().select(tf.getTemplateFilterType());

		} else {
			// TemplateFilter is null, remove all the text.
			nameField.setText("");
			descriptionField.setText("");
			representationField.setText("");
			activeCheckBox.setSelected(false);
		}
		displayFieldsPerActiveSetting(tf);

		if (tf != null) {
			int iCurrentIndex = filterTable.getItems().indexOf(currentTemplateFilter);
			this.mainApp.updateStatusBarNumberOfItems((iCurrentIndex + 1) + "/"
					+ filterTable.getItems().size() + " ");
			// remember the selection
			rememberSelection(iCurrentIndex);
		}
	}

	@Override
	public void displayFieldsPerActiveSetting(TemplateFilter tf) {
		super.displayFieldsPerActiveSetting(tf);
		boolean fIsActive;
		if (tf == null) {
			fIsActive = false;
		} else {
			fIsActive = tf.isActive();
		}
		repairRadioButton.setDisable(!fIsActive);
		failRadioButton.setDisable(!fIsActive);
	}

	protected void selectAndScrollToItem(int index) {
		filterTable.requestFocus();
		filterTable.getSelectionModel().select(index);
		filterTable.getFocusModel().focus(index);
		filterTable.scrollTo(index);
	}

	protected void rememberSelection(int iCurrentIndex) {
		mainApp.getApplicationPreferences().setLastONCFiltersViewItemUsed(
				iCurrentIndex);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);

		repairRadioButton.setText(bundle.getString("radio.repairaction"));
		failRadioButton.setText(bundle.getString("radio.failaction"));
		showFilterDetails(null);

		// Listen for selection changes and show the details when changed.
		filterTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showFilterDetails(newValue));
	}

	@Override
	public void setViewItemUsed(int value) {
		int max = filterTable.getItems().size();
		value = adjustIndexValue(value, max);
		filterTable.getSelectionModel().clearAndSelect(value);
	}


	@FXML
	public void handleRepairRadioButton() {
		int i = filterList.indexOf(currentTemplateFilter);
		Filter f = filterList.get(i);
		boolean currentValue = f.getAction().isDoRepair();
		f.getAction().setDoRepair(!currentValue);
		repairRadioButton.setSelected(true); // needed by test for some
													// reason...
		fAllowSlotPosition = true;
		processRepresentationFieldContents();
	}

	@FXML
	public void handleFailRadioButton() {
		int i = filterList.indexOf(currentTemplateFilter);
		Filter f = filterList.get(i);
		boolean currentValue = f.getAction().isDoRepair();
		f.getAction().setDoRepair(!currentValue);
		failRadioButton.setSelected(true); // needed by test for some
													// reason...
		fAllowSlotPosition = false;
		processRepresentationFieldContents();
	}

	@Override
	protected void handleInsertNewItem() {
		Filter newFilter = new Filter();
		filterList.add(newFilter);
		newFilter.setTemplateFilterRepresentation("");
		int i = oncApproach.getLanguageProject().getFilters().size() - 1;
		selectAndScrollToItem(i);
	}

	@Override
	void handleRemoveItem() {
		int i = filterList.indexOf(currentTemplateFilter);
		currentTemplateFilter = null;
		if (i >= 0) {
			filterList.remove(i);
			int max = filterTable.getItems().size();
			i = adjustIndexValue(i, max);
			selectAndScrollToItem(i);
		}
		filterTable.refresh();
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return new TextField[] { nameField, descriptionField, representationField };
	}
}