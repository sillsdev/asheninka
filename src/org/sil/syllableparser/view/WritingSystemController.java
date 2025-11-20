// Copyright (c) 2020-2025 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.Language;
import org.sil.syllableparser.model.SortingOption;
import org.sil.syllableparser.service.LDMLFileExtractor;
import org.sil.utility.service.keyboards.KeyboardChanger;
import org.sil.utility.service.keyboards.KeyboardInfo;
import org.sil.utility.view.ControllerUtilities;

import com.ibm.icu.text.Collator;
import com.ibm.icu.text.RuleBasedCollator;
import com.ibm.icu.util.ULocale;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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
	private Tab keyboardTab;
	@FXML
	private Tab sortingTab;
	@FXML
	private TextField languageNameField;
	@FXML
	private TextField codeField;
	@FXML
	private CheckBox rightToLeftCheckBox;
	@FXML
	private Label fontFamilyName;
	@FXML
	private Button fontButton;
	@FXML
	private Button okButton;
	@FXML
	private ColorPicker colorPicker;
	@FXML
	private ComboBox<KeyboardInfo> keyboardCombo = new ComboBox<KeyboardInfo>();
	// following are public for unit tests
	@FXML
	public Label icuRules;
	@FXML
	public ChoiceBox<SortingOption> sortingChoiceBox;
	@FXML
	public TextArea icuRulesTextArea;
	@FXML
	public Label icuRuleError;
	@FXML
	public TextArea icuRulesErrorArea;
	@FXML
	public Label languageToUse;
	@FXML
	public ComboBox<String> languageToUseComboBox;
	@FXML
	public Button browseButton;
	@FXML
	public TextField directoryField;

	Stage dialogStage;
	private boolean okClicked = false;
	private Language currentLanguage;
	private Color color = Color.BLACK;
	private String sFileFilterDescription = "";
	KeyboardInfo keyboardInfo = new KeyboardInfo(new Locale("en"), "English");
	private Text keyboardText = new Text();
	KeyboardChanger keyboardChanger;

	private String sFileChooserFilterDescription = "*.ldml";

	public WritingSystemController() {
		
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		this.bundle = resources;
		sFileFilterDescription = bundle.getString("file.ldmlfilterdescription");
		icuRulesErrorArea.setEditable(false);

		sortingChoiceBox.getItems().setAll(SortingOption.values());
		if (currentLanguage != null) {
			sortingChoiceBox.getSelectionModel().select(currentLanguage.getSortingOption());
		}
		sortingChoiceBox.setConverter(new StringConverter<SortingOption>() {
			@Override
			public String toString(SortingOption object) {
				String localizedName = bundle.getString("language.sortingoption." + object.toString().toLowerCase());
				return localizedName;
			}

			@Override
			public SortingOption fromString(String string) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		sortingChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SortingOption>() {
			public void changed(ObservableValue ov, SortingOption old_value, SortingOption new_value) {
				displaySortingTabItemsPerSortingOption(new_value);
			}
		});
		icuRulesTextArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				try {
					icuRulesErrorArea.setText("");
					icuRulesErrorArea.setStyle(Constants.TEXT_COLOR_CSS_BEGIN + "red" + Constants.TEXT_COLOR_CSS_END);
					icuRulesErrorArea.setVisible(false);
					icuRuleError.setVisible(false);
					okButton.setDisable(false);
					@SuppressWarnings("unused")
					RuleBasedCollator collatorViaRules = new RuleBasedCollator(newValue);
				} catch (ParseException e) {
					String details = e.getMessage();
					int i = details.indexOf("near \"");
					int end = details.lastIndexOf('"');
					icuRuleError.setVisible(true);
					icuRulesErrorArea.setVisible(true);
					icuRulesErrorArea.setText(details.substring(i+6, end));
					icuRulesErrorArea.setEditable(false);
					okButton.setDisable(true);
				} catch (Exception e) {
					e.printStackTrace();
					MainApp.reportException(e, bundle);
				}
		    }
		});
		ULocale[] availableLocales = ULocale.getAvailableLocales();
		for (ULocale l : availableLocales) {
			languageToUseComboBox.getItems().add(l.getName());
		}
		languageToUseComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue ov, String old_value, String new_value) {
				currentLanguage.setUseSameLanguage(new_value);
				ULocale myLocale = new ULocale(new_value);
				RuleBasedCollator rbc = (RuleBasedCollator) Collator.getInstance(myLocale);
				String sRules = rbc.getRules();
				if (currentLanguage.getSortingOption() == SortingOption.SAME_AS_ANOTHER_LANGUAGE) {
					currentLanguage.setIcuRules(sRules);
					icuRulesTextArea.setText(sRules);
				}
			}
		});
		colorPicker.setOnAction(new EventHandler() {
		     public void handle(Event t) {
		         color = colorPicker.getValue();
		     }
		 });
		browseButton.setText(bundle.getString("label.browse"));

		keyboardChanger = KeyboardChanger.getInstance();
		keyboardChanger.initKeyboardHandler(MainApp.class);
		ObservableList<KeyboardInfo> activeKeyboards = FXCollections
				.observableArrayList(keyboardChanger.getActiveKeyboards());
		keyboardCombo.setItems(activeKeyboards);
		keyboardCombo.setConverter(new StringConverter<KeyboardInfo>() {
			@Override
			public String toString(KeyboardInfo object) {
				return object.getDescription();
			}
			@Override
			public KeyboardInfo fromString(String string) {
				return null;
			}
		});
		keyboardCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
			keyboardText.setText(newValue.getDescription());
			keyboardInfo = newValue;
		});

		SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		selectionModel.select(fontTab);
	}
	
	protected void displaySortingTabItemsPerSortingOption(SortingOption option) {
		switch (option) {
		case CUSTOM_ICU_RULES:
			icuRules.setVisible(true);
			icuRulesTextArea.setVisible(true);
			languageToUse.setVisible(false);
			languageToUseComboBox.setVisible(false);
			directoryField.setVisible(false);
			browseButton.setVisible(false);
			icuRulesErrorArea.setVisible(false);
			icuRuleError.setVisible(false);
			break;
		case DEFAULT_ORDER:
			icuRules.setVisible(false);
			icuRulesTextArea.setVisible(false);
			languageToUse.setVisible(false);
			languageToUseComboBox.setVisible(false);
			directoryField.setVisible(false);
			browseButton.setVisible(false);
			icuRulesErrorArea.setVisible(false);
			icuRuleError.setVisible(false);
			break;
		case SAME_AS_ANOTHER_LANGUAGE:
			icuRules.setVisible(true);
			icuRulesTextArea.setVisible(true);
			languageToUse.setVisible(true);
			languageToUseComboBox.setVisible(true);
			directoryField.setVisible(false);
			browseButton.setVisible(false);
			icuRulesErrorArea.setVisible(false);
			icuRuleError.setVisible(false);
			break;
		case USE_LDML_FILE:
			icuRules.setVisible(true);
			icuRulesTextArea.setVisible(true);
			languageToUse.setVisible(false);
			languageToUseComboBox.setVisible(false);
			directoryField.setVisible(true);
			browseButton.setVisible(true);
			icuRulesErrorArea.setVisible(false);
			icuRuleError.setVisible(false);
			break;
		}
	}

	public void setData(Language language) throws CloneNotSupportedException {
		if (language == null)
			return;
		currentLanguage = language;
		if (rightToLeftCheckBox != null) {
			rightToLeftCheckBox.setSelected(currentLanguage.isRightToLeft());
		}
		if (languageNameField != null) {
			languageNameField.setText(currentLanguage.getLanguageName());
		}
		if (codeField != null) {
			codeField.setText(currentLanguage.getCode());
		}
		if (fontFamilyName != null) {
			fontFamilyName.setText(currentLanguage.getFontFamily());
		}
		if (colorPicker != null) {
			if (currentLanguage.getColor() != null) {
				color = currentLanguage.getColor();
				colorPicker.setValue(color);
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
		if (languageToUseComboBox != null) {
			int i = languageToUseComboBox.getItems().indexOf(currentLanguage.getUseSameLanguage());
			languageToUseComboBox.selectionModelProperty().get().select(i);
		}
		if (directoryField != null) {
			directoryField.setText(currentLanguage.getLdmlFile());
		}
		sFileChooserFilterDescription = sFileFilterDescription + " ("
				+ Constants.LDML_FILE_EXTENSIONS + ")";
		keyboardInfo = currentLanguage.getKeyboard();
		keyboardCombo.setValue(keyboardInfo);

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
		currentLanguage.setLanguageName(languageNameField.getText());
		currentLanguage.setCode(codeField.getText());
		currentLanguage.setSortingOption(sortingChoiceBox.selectionModelProperty().get().getSelectedItem());
		currentLanguage.setIcuRules(icuRulesTextArea.getText());
		currentLanguage.setLdmlFile(directoryField.getText());
		currentLanguage.setKeyboard(keyboardInfo);
		
		dialogStage.close();
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	@FXML
	protected void handleBrowse() throws IOException {
		File file = ControllerUtilities.getFileToOpen(getMainApp(),
				currentLanguage.getLdmlFile(), sFileChooserFilterDescription,
				Constants.LDML_FILE_EXTENSIONS);
		if (file != null && file.exists() && file.isFile()) {
			directoryField.setText(file.getAbsolutePath());
			LDMLFileExtractor extractor = new LDMLFileExtractor(file);
			String sLDMLFileContent = extractor.getIcuRules();
			if (sLDMLFileContent != null) {
				icuRulesTextArea.setText(sLDMLFileContent);
			}

		}
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
