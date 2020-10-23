/**
 * Copyright (c) 2019-2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.FilterType;
import org.sil.syllableparser.model.TemplateFilter;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
import org.sil.utility.StringUtilities;

/**
 * @author Andy Black
 *
 */
public class FiltersController extends TemplatesFiltersController {

	@FXML
	protected ComboBox<FilterType> typeComboBox;
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
		// no sorting allowed
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
		if (languageProject != null) {
			Color textColor = languageProject.getAnalysisLanguage().getColor();
			String sRGB= StringUtilities.toRGBCode(textColor);
			String sAnalysis = Constants.TEXT_COLOR_CSS_BEGIN + sRGB + Constants.TEXT_COLOR_CSS_END;
			nameField.setStyle(sAnalysis);
			descriptionField.setStyle(sAnalysis);
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
			NodeOrientation analysisOrientation = languageProject.getAnalysisLanguage()
					.getOrientation();
			nameField.setNodeOrientation(analysisOrientation);
			descriptionField.setNodeOrientation(analysisOrientation);

			activeCheckBox.setSelected(tf.isActive());
			List<String> choices = languageProject.getCVApproach().getActiveCVNaturalClasses().stream()
					.map(CVNaturalClass::getNCName).collect(Collectors.toList());
			ObservableList<String> choices2 = FXCollections.observableArrayList(choices);
			sncChoicesComboBox.setItems(choices2);
			sncChoicesComboBox.setVisible(false);
			typeComboBox.getItems().setAll(FilterType.values());
			typeComboBox.getSelectionModel().select(tf.getTemplateFilterType());
			setUpDownButtonDisabled(filterList);
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
		super.setApproach(ApplicationPreferences.FILTERS);
		super.setTableView(filterTable);
		super.initialize(location, resources);

		repairRadioButton.setText(bundle.getString("radio.repairaction"));
		failRadioButton.setText(bundle.getString("radio.failaction"));
		showFilterDetails(null);

		// Listen for selection changes and show the details when changed.
		filterTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showFilterDetails(newValue));

		typeComboBox.setConverter(new StringConverter<FilterType>() {
			@Override
			public String toString(FilterType object) {
				String localizedName = bundle.getString("templatefilter.type." + object.toString().toLowerCase());
				if (currentTemplateFilter != null)
					currentTemplateFilter.setType(localizedName);
				return localizedName;
			}

			@Override
			public FilterType fromString(String string) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		typeComboBox.getSelectionModel().selectedItemProperty()
		.addListener(new ChangeListener<FilterType>() {
			@Override
			public void changed(ObservableValue<? extends FilterType> selected,
					FilterType oldValue, FilterType selectedValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
//						System.out.println("SelectedValue="	+ selectedValue);
						currentTemplateFilter.setTemplateFilterType(selectedValue);
					}
				});
			}
		});
		typeComboBox.setPromptText(resources.getString("label.choosetype"));

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
		handleInsertNewItem(filterList, filterTable);
	}

	@Override
	void handleRemoveItem() {
		handleRemoveItem(filterList, currentTemplateFilter, filterTable);
	}

	@Override
	void handlePreviousItem() {
		handlePreviousItem(filterList, currentTemplateFilter, filterTable);
	}

	@Override
	void handleNextItem() {
		handleNextItem(filterList, currentTemplateFilter, filterTable);
	}

	@FXML
	void handleMoveDown() {
		handleMoveDown(filterList);
	}

	@FXML
	void handleMoveUp() {
		handleMoveUp(filterList);
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@Override
	TextField[] createTextFields() {
		return new TextField[] { nameField, descriptionField, representationField };
	}

}
