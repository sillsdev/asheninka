// Copyright (c) 2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.sil.syllableparser.model.FilterType;
import org.sil.syllableparser.model.Language;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.SortingOption;
import org.sil.syllableparser.model.SyllabificationParameters;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * @author Andy Black
 *
 */

public class WritingSystemController extends SylParserBaseController implements
		Initializable {

	@FXML
	private TabPane tabPane;
	@FXML
	private Tab generalTab;
	@FXML
	private Tab fontTab;
	@FXML
	private Tab sortingTab;
	@FXML
	private TextField codeField;
	@FXML
	private CheckBox rightToLeftCheckBox;
	@FXML
	private Label fontFamilyName;
	@FXML
	private Button fontButton;
	@FXML
	private ChoiceBox<SortingOption> sortingChoiceBox;
	@FXML
	private ColorPicker colorPicker;
	@FXML
	private TextArea icuRulesTextArea;

	Stage dialogStage;
	private boolean okClicked = false;
	private Language currentLanguage;
	private Color color = Color.BLACK;

	public WritingSystemController() {
		
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		this.bundle = resources;

		sortingChoiceBox.getItems().setAll(SortingOption.values());
		if (currentLanguage != null) {
			sortingChoiceBox.getSelectionModel().select(currentLanguage.getSortingOption());
		}
		sortingChoiceBox.setConverter(new StringConverter<SortingOption>() {
			@Override
			public String toString(SortingOption object) {
				String localizedName = bundle.getString("language.sortingoption." + object.toString().toLowerCase());
				if (currentLanguage != null)
					currentLanguage.setSortOption(localizedName);
				return localizedName;
			}

			@Override
			public SortingOption fromString(String string) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		colorPicker.setOnAction(new EventHandler() {
		     public void handle(Event t) {
		         color = colorPicker.getValue();
//		         System.out.println("New Color's RGB = "+ color.getRed()+" "+color.getGreen()+" "+color.getBlue());
		     }
		 });
		sortingChoiceBox.requestFocus();
	}
	
	public void setData(Language language) throws CloneNotSupportedException {
		if (language == null)
			return;
		currentLanguage = language;
		if (rightToLeftCheckBox != null) {
			rightToLeftCheckBox.setSelected(currentLanguage.isRightToLeft());
		}
		if (codeField != null) {
			codeField.setText(currentLanguage.getCode());
		}
		if (fontFamilyName != null) {
			fontFamilyName.setText(currentLanguage.getFontFamily());
		}
		if (colorPicker != null) {
			if (currentLanguage.getColor() != null) {
				colorPicker.setValue(currentLanguage.getColor());
			}
		}
		if (sortingChoiceBox != null) {
			SortingOption option = currentLanguage.getSortingOption();
			int i = sortingChoiceBox.getItems().indexOf(option);
			sortingChoiceBox.selectionModelProperty().get().select(i);
		}
		if (icuRulesTextArea != null) {
			icuRulesTextArea.setText(currentLanguage.getIcuRules());
		}
	}
	
	@Override
	void handleInsertNewItem() {
		// nothing to do
	}

	@Override
	void handleRemoveItem() {
		// nothing to do
	}
	
	@FXML
	private void handleFont() {
		mainApp.getController().handleFont(mainApp.getPrimaryStage(), currentLanguage);
		fontFamilyName.setText(currentLanguage.getFontFamily());
	}

	
	public boolean isOkClicked() {
		return okClicked;
	}

	/**
	 * Called when the user clicks OK.
	 */
	@FXML
	private void handleOk() {
		okClicked = true;
		currentLanguage.setColor(color);
		currentLanguage.setRightToLeft(rightToLeftCheckBox.isSelected());
		currentLanguage.setCode(codeField.getText());
		currentLanguage.setSortingOption(sortingChoiceBox.selectionModelProperty().get().getSelectedItem());
		currentLanguage.setIcuRules(icuRulesTextArea.getText());
		
		dialogStage.close();
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	@Override
	TextField[] createTextFields() {
		// nothing to do
		return null;
	}

	@Override
	void handlePreviousItem() {
		// nothing to do
	}

	@Override
	void handleNextItem() {
		// nothing to do
	}

	/**
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;		
	}
}
