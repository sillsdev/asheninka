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
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.Template;
import org.sil.syllableparser.model.TemplateType;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.moraicapproach.MoraicApproach;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
import org.sil.utility.StringUtilities;

/**
 * @author Andy Black
 *
 */
public class TemplatesController extends TemplatesFiltersController {

	@FXML
	protected TableView<Template> templateTable;
	@FXML
	protected ComboBox<TemplateType> typeComboBox;

	protected ObservableList<Template> templateList = FXCollections.observableArrayList();

	public void setData(ONCApproach oncApproachData) {
		oncApproach = oncApproachData;
		languageProject = oncApproach.getLanguageProject();
		setDataProcessing(ApplicationPreferences.LAST_ONC_TEMPLATES_VIEW_ITEM_USED);
	}

	protected void setDataProcessing(String sLastView) {
		// no sorting allowed
		templateList = languageProject.getTemplates();
		iRepresentationCaretPosition = 6;
		fSncChoicesUsingMouse = false;
		// Add observable list data to the table
		templateTable.setItems(templateList);
		int max = templateTable.getItems().size();
		if (max > 0) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					ApplicationPreferences prefs = mainApp.getApplicationPreferences();
					int iLastIndex = prefs.getIntegerValue(sLastView, 0);
					iLastIndex = adjustIndexValue(iLastIndex, max);
					selectAndScrollToItem(iLastIndex);
				}
			});
		}
		if (languageProject != null) {
			String sAnalysis = mainApp.getStyleFromColor(languageProject.getAnalysisLanguage().getColor());
			nameField.setStyle(sAnalysis);
			descriptionField.setStyle(sAnalysis);
		}
	}

	public void setData(MoraicApproach moraicApproachData) {
		moraicApproach = moraicApproachData;
		languageProject = moraicApproach.getLanguageProject();
		setDataProcessing(ApplicationPreferences.LAST_MORAIC_TEMPLATES_VIEW_ITEM_USED);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.setApproach(ApplicationPreferences.TEMPLATES);
		super.setTableView(templateTable);
		super.initialize(location, resources);

		showTemplateDetails(null);
		
		// Listen for selection changes and show the details when changed.
		templateTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showTemplateDetails(newValue));

		typeComboBox.setConverter(new StringConverter<TemplateType>() {
			@Override
			public String toString(TemplateType object) {
				String localizedName = bundle.getString("templatefilter.type." + object.toString().toLowerCase());
				if (currentTemplateFilter != null)
					currentTemplateFilter.setType(localizedName);
				return localizedName;
			}

			@Override
			public TemplateType fromString(String string) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		typeComboBox.getSelectionModel().selectedItemProperty()
		.addListener(new ChangeListener<TemplateType>() {
			@Override
			public void changed(ObservableValue<? extends TemplateType> selected,
					TemplateType oldValue, TemplateType selectedValue) {
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

	/**
	 * Fills all text fields to show details about the environment.
	 *
	 * @param tf = the template/filter
	 *
	 */
	protected void showTemplateDetails(Template tf) {
		currentTemplateFilter = tf;
		if (tf != null) {
			// Fill the text fields with info from the object.
			nameField.setText(tf.getTemplateFilterName());
			descriptionField.setText(tf.getDescription());
			representationField.setText(tf.getTemplateFilterRepresentation());
			NodeOrientation vernacularOrientation = languageProject.getVernacularLanguage()
					.getOrientation();
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
			typeComboBox.getItems().setAll(TemplateType.values());
			typeComboBox.getSelectionModel().select(tf.getTemplateFilterType());
			setUpDownButtonDisabled(templateList);
		} else {
			// TemplateFilter is null, remove all the text.
			nameField.setText("");
			descriptionField.setText("");
			representationField.setText("");
			activeCheckBox.setSelected(false);
		}
		displayFieldsPerActiveSetting(tf);

		if (tf != null) {
			int iCurrentIndex = templateTable.getItems().indexOf(currentTemplateFilter);
			this.mainApp.updateStatusBarNumberOfItems((iCurrentIndex + 1) + "/"
					+ templateTable.getItems().size() + " ");
			// remember the selection
			rememberSelection(iCurrentIndex);
		}
	}

	protected void rememberSelection(int iCurrentIndex) {
		mainApp.getApplicationPreferences().setLastONCTemplatesViewItemUsed(
				iCurrentIndex);
	}
	
	@Override
	public void setViewItemUsed(int value) {
		int max = templateTable.getItems().size();
		value = adjustIndexValue(value, max);
		templateTable.getSelectionModel().clearAndSelect(value);
	}
	
	protected void selectAndScrollToItem(int index) {
		templateTable.requestFocus();
		templateTable.getSelectionModel().select(index);
		templateTable.getFocusModel().focus(index);
		templateTable.scrollTo(index);
	}

	@Override
	protected void handleInsertNewItem() {
		Template newTemplate = new Template();
		templateList.add(newTemplate);
		newTemplate.setTemplateFilterRepresentation("");
		handleInsertNewItem(templateList, templateTable);
	}

	@Override
	void handleRemoveItem() {
		handleRemoveItem(templateList, currentTemplateFilter, templateTable);
	}

	@Override
	void handlePreviousItem() {
		handlePreviousItem(templateList, currentTemplateFilter, templateTable);
	}

	@Override
	void handleNextItem() {
		handleNextItem(templateList, currentTemplateFilter, templateTable);
	}

	@FXML
	void handleMoveDown() {
		handleMoveDown(templateList);
	}

	@FXML
	void handleMoveUp() {
		handleMoveUp(templateList);
	}

}
