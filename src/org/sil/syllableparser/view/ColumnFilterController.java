// Copyright (c) 2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.PatternSyntaxException;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.Language;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.service.filter.ColumnFilterType;
import org.sil.syllableparser.service.filter.WordsFilter;
import org.sil.syllableparser.service.filter.WordsFilterType;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class ColumnFilterController implements Initializable {

	@FXML
	protected TextField textToSearchFor;
	@FXML
	protected ToggleGroup group;
	@FXML
	protected RadioButton showBlanksRadio;
	@FXML
	protected RadioButton showNonBlanksRadio;
	@FXML
	protected RadioButton anywhereRadio;
	@FXML
	protected RadioButton atStartRadio;
	@FXML
	protected RadioButton atEndRadio;
	@FXML
	protected RadioButton wholeItemRadio;
	@FXML
	protected RadioButton matchRegularExpressionRadio;
	@FXML
	protected CheckBox matchCaseCheckBox;
	@FXML
	protected CheckBox matchDiacriticsCheckBox;
	
	private MainApp mainApp;
	Stage dialogStage;
	protected ApplicationPreferences preferences;
	String sAppPreferencesBase = ApplicationPreferences.LAST_CV_FILTER_WORDS;
	protected String sTextToSearchFor;
	protected ResourceBundle bundle;
	protected Locale locale;
	private ObservableList<Word> words = FXCollections.observableArrayList();
	private boolean resultIsOK = false;
	WordsFilterType wordsFilterType = WordsFilterType.CV_WORDS;
	ColumnFilterType columnFilterType = ColumnFilterType.ANYWHERE;
	WordsFilter filter;
	boolean filterIsActive = false;
	
	public ObservableList<Word> getWords() {
		return words;
	}
	
	public void setWords(ObservableList<Word> words) {
		this.words = words;
		Language vernacular = mainApp.getLanguageProject().getVernacularLanguage();
		NodeOrientation vernacularOrientation = vernacular.getOrientation();
		textToSearchFor.setNodeOrientation(vernacularOrientation);
		textToSearchFor.setFont(vernacular.getFont());
		String sVernacular = mainApp.getStyleFromColor(vernacular.getColor());
		textToSearchFor.setStyle(sVernacular);
//		textToSearchFor.setText(lastWordTried);

	}

	public boolean isResultIsOK() {
		return resultIsOK;
	}

	public WordsFilterType getWordsFilterType() {
		return wordsFilterType;
	}

	public void setWordsFilterType(WordsFilterType wordsFilterType) {
		this.wordsFilterType = wordsFilterType;
		switch (wordsFilterType) {
		// all words and words predicted vs. correct are the same
		case CV_WORDS:
		case CV_WORDS_PREDICTED_VS_CORRECT:
		case MORAIC_WORDS:
		case MORAIC_WORDS_PREDICTED_VS_CORRECT:
		case NUCLEAR_PROJECTION_WORDS:
		case NUCLEAR_PROJECTION_WORDS_PREDICTED_VS_CORRECT:
		case ONC_WORDS:
		case ONC_WORDS_PREDICTED_VS_CORRECT:
		case OT_WORDS:
		case OT_WORDS_PREDICTED_VS_CORRECT:
		case SH_WORDS:
		case SH_WORDS_PREDICTED_VS_CORRECT:
			showBlanksRadio.setDisable(true);
			showNonBlanksRadio.setDisable(true);
			break;
		default:
			showBlanksRadio.setDisable(false);
			showNonBlanksRadio.setDisable(false);
			break;
		}
	}

	public ColumnFilterType getColumnFilterType() {
		return columnFilterType;
	}

	public void setColumnFilterType(ColumnFilterType columnFilterType) {
		this.columnFilterType = columnFilterType;
		Toggle toggle = group.getToggles().filtered(t -> t.getUserData() == columnFilterType).get(0);
		toggle.setSelected(true);
	}

	public void setTextToSearchFor(String sTextToSearchFor) {
		this.sTextToSearchFor = sTextToSearchFor;
		textToSearchFor.setText(sTextToSearchFor);
	}

	public boolean isMatchCase() {
		return matchCaseCheckBox.isSelected();
	}

	public void setMatchCase(boolean matchCase) {
		matchCaseCheckBox.setSelected(matchCase);
	}

	public boolean isMatchDiacritics() {
		return matchDiacriticsCheckBox.isSelected();
	}

	public void setMatchDiacritics(boolean matchDiacritics) {
		matchDiacriticsCheckBox.setSelected(matchDiacritics);
	}

	public WordsFilter getFilter() {
		return filter;
	}

	public void setFilter(WordsFilter filter) {
		filter.setActive(filterIsActive);
		filter.setFilterType(columnFilterType);
		filter.setMatchCase(isMatchCase());
		filter.setMatchDiacritics(isMatchDiacritics());
		filter.setTextToMatch(textToSearchFor.getText());
		this.filter = filter;
	}

	@FXML
	private void handleOK() {
		String sTextToSearchFor = textToSearchFor.getText().trim();
		if (sTextToSearchFor.length() == 0 && !(showBlanksRadio.isSelected() || showNonBlanksRadio.isSelected())) {
			return;
		}
		if (matchRegularExpressionRadio.isSelected()) {
			try {
				String s = "aaa";
				s.matches(textToSearchFor.getText());
				processFilter();
			} catch (PatternSyntaxException e) {
				System.out.println("re error: " + e.getMessage());
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle(MainApp.kApplicationTitle);
				alert.setHeaderText(bundle.getString("label.errorinregularexpression"));
				alert.setContentText(e.getMessage());
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().add(mainApp.getNewMainIconImage());
				alert.showAndWait();
			}
		} else {
			processFilter();
		}
	}

	@SuppressWarnings("static-access")
	protected void processFilter() {
		resultIsOK = true;
		// create list of words that match the pattern
		filter = new WordsFilter(wordsFilterType, columnFilterType,
				matchCaseCheckBox.isSelected(), matchDiacriticsCheckBox.isSelected(),
				textToSearchFor.getText());
		filter.setActive(filterIsActive);
		words = filter.applyFilter(words);
		preferences.setLastWindowParameters(sAppPreferencesBase, dialogStage);
		preferences.setPreferencesKey(sAppPreferencesBase + preferences.FILTER_ACTIVE, true);
		preferences.setPreferencesKey(sAppPreferencesBase + preferences.FILTER_MATCH_CASE, isMatchCase());
		preferences.setPreferencesKey(sAppPreferencesBase + preferences.FILTER_MATCH_DIACRITICS, isMatchDiacritics());
		preferences.setPreferencesKey(sAppPreferencesBase + preferences.FILTER_SEARCH_TEXT, textToSearchFor.getText());
		preferences.setPreferencesKey(sAppPreferencesBase + preferences.FILTER_SEARCH_TYPE, columnFilterType.toString());
		dialogStage.close();
	}

	@SuppressWarnings("static-access")
	@FXML
	private void handleClose() {
		resultIsOK = false;
		preferences.setLastWindowParameters(sAppPreferencesBase, dialogStage);
		preferences.setPreferencesKey(sAppPreferencesBase + preferences.FILTER_ACTIVE, false);
		dialogStage.close();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		anywhereRadio.setUserData(ColumnFilterType.ANYWHERE);
		atEndRadio.setUserData(ColumnFilterType.AT_END);
		atStartRadio.setUserData(ColumnFilterType.AT_START);
		matchRegularExpressionRadio.setUserData(ColumnFilterType.REGULAR_EXPRESSION);
		showBlanksRadio.setUserData(ColumnFilterType.BLANKS);
		showNonBlanksRadio.setUserData(ColumnFilterType.NON_BLANKS);
		wholeItemRadio.setUserData(ColumnFilterType.WHOLE_ITEM);
		matchDiacriticsCheckBox.setDisable(true);
		matchDiacriticsCheckBox.setVisible(false); // does not work the same as in FLEx
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle newToggle) {
				columnFilterType = (ColumnFilterType) newToggle.getUserData();
			}
		});
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				textToSearchFor.requestFocus();
			}
		});
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public static String getAppPreferencesBaseToUse(WordsFilterType wordsFilterType) {
		String appBase = "";
		switch (wordsFilterType) {
		case CV_CORRECT:
			appBase = ApplicationPreferences.LAST_CV_FILTER_CORRECT_SYLLABIFICATIONS;
			break;
		case CV_PREDICTED:
			appBase = ApplicationPreferences.LAST_CV_FILTER_PREDICTED_SYLLABIFICATIONS;
			break;
		case CV_WORDS:
			appBase = ApplicationPreferences.LAST_CV_FILTER_WORDS;
			break;
		case CV_WORDS_PREDICTED_VS_CORRECT:
			appBase = ApplicationPreferences.LAST_CV_FILTER_WORDS_PREDICTED_VS_CORRECT;
			break;
		case MORAIC_CORRECT:
			appBase = ApplicationPreferences.LAST_MORAIC_FILTER_CORRECT_SYLLABIFICATIONS;
			break;
		case MORAIC_PREDICTED:
			appBase = ApplicationPreferences.LAST_MORAIC_FILTER_PREDICTED_SYLLABIFICATIONS;
			break;
		case MORAIC_WORDS:
			appBase = ApplicationPreferences.LAST_MORAIC_FILTER_WORDS;
			break;
		case MORAIC_WORDS_PREDICTED_VS_CORRECT:
			appBase = ApplicationPreferences.LAST_MORAIC_FILTER_WORDS_PREDICTED_VS_CORRECT;
			break;
		case NUCLEAR_PROJECTION_CORRECT:
			appBase = ApplicationPreferences.LAST_NUCLEAR_PROJECTION_FILTER_CORRECT_SYLLABIFICATIONS;
			break;
		case NUCLEAR_PROJECTION_PREDICTED:
			appBase = ApplicationPreferences.LAST_NUCLEAR_PROJECTION_FILTER_PREDICTED_SYLLABIFICATIONS;
			break;
		case NUCLEAR_PROJECTION_WORDS:
			appBase = ApplicationPreferences.LAST_NUCLEAR_PROJECTION_FILTER_WORDS;
			break;
		case NUCLEAR_PROJECTION_WORDS_PREDICTED_VS_CORRECT:
			appBase = ApplicationPreferences.LAST_NUCLEAR_PROJECTION_FILTER_WORDS_PREDICTED_VS_CORRECT;
			break;
		case ONC_CORRECT:
			appBase = ApplicationPreferences.LAST_ONC_FILTER_CORRECT_SYLLABIFICATIONS;
			break;
		case ONC_PREDICTED:
			appBase = ApplicationPreferences.LAST_ONC_FILTER_PREDICTED_SYLLABIFICATIONS;
			break;
		case ONC_WORDS:
			appBase = ApplicationPreferences.LAST_ONC_FILTER_WORDS;
			break;
		case ONC_WORDS_PREDICTED_VS_CORRECT:
			appBase = ApplicationPreferences.LAST_ONC_FILTER_WORDS_PREDICTED_VS_CORRECT;
			break;
		case OT_CORRECT:
			appBase = ApplicationPreferences.LAST_OT_FILTER_CORRECT_SYLLABIFICATIONS;
			break;
		case OT_PREDICTED:
			appBase = ApplicationPreferences.LAST_OT_FILTER_PREDICTED_SYLLABIFICATIONS;
			break;
		case OT_WORDS:
			appBase = ApplicationPreferences.LAST_OT_FILTER_WORDS;
			break;
		case OT_WORDS_PREDICTED_VS_CORRECT:
			appBase = ApplicationPreferences.LAST_OT_FILTER_WORDS_PREDICTED_VS_CORRECT;
			break;
		case SH_CORRECT:
			appBase = ApplicationPreferences.LAST_SH_FILTER_CORRECT_SYLLABIFICATIONS;
			break;
		case SH_PREDICTED:
			appBase = ApplicationPreferences.LAST_SH_FILTER_PREDICTED_SYLLABIFICATIONS;
			break;
		case SH_WORDS:
			appBase = ApplicationPreferences.LAST_SH_FILTER_WORDS;
			break;
		case SH_WORDS_PREDICTED_VS_CORRECT:
			appBase = ApplicationPreferences.LAST_SH_FILTER_WORDS_PREDICTED_VS_CORRECT;
			break;
		}
		return appBase;
	}

	protected void findAppPreferencesBaseToUse() {
		sAppPreferencesBase = getAppPreferencesBaseToUse(wordsFilterType);
	}

	public static ColumnFilterType getColumnFilterType(ApplicationPreferences prefs, String sPrefKey) {
		String sType = prefs.getStringValue(sPrefKey, ColumnFilterType.ANYWHERE.toString());
		ColumnFilterType cft = ColumnFilterType.ANYWHERE;
		switch (sType) {
		case "ANYWHERE":
			cft = ColumnFilterType.ANYWHERE;
			break;
		case "AT_END":
			cft = ColumnFilterType.AT_END;
			break;
		case "AT_START":
			cft = ColumnFilterType.AT_START;
			break;
		case "BLANKS":
			cft = ColumnFilterType.BLANKS;
			break;
		case "NON_BLANKS":
			cft = ColumnFilterType.NON_BLANKS;
			break;
		case "REGULAR_EXPRESSION":
			cft = ColumnFilterType.REGULAR_EXPRESSION;
			break;
		case "WHOLE_ITEM":
			cft = ColumnFilterType.WHOLE_ITEM;
			break;
		}
		return cft;
	}

	@SuppressWarnings("static-access")
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		preferences = mainApp.getApplicationPreferences();
		findAppPreferencesBaseToUse();
		dialogStage = preferences.getLastWindowParameters(sAppPreferencesBase, dialogStage, 410.0, 600.0);
		filterIsActive = preferences.getBooleanValue(sAppPreferencesBase + preferences.FILTER_ACTIVE, false);
		matchCaseCheckBox.setSelected(preferences.getBooleanValue(sAppPreferencesBase + preferences.FILTER_MATCH_CASE, matchCaseCheckBox.isSelected()));
		matchDiacriticsCheckBox.setSelected(preferences.getBooleanValue(sAppPreferencesBase + preferences.FILTER_MATCH_DIACRITICS, matchDiacriticsCheckBox.isSelected()));
		textToSearchFor.setText(preferences.getStringValue(sAppPreferencesBase + preferences.FILTER_SEARCH_TEXT, textToSearchFor.getText()));
		sTextToSearchFor = textToSearchFor.getText();
		setColumnFilterTypeFromPreferences();
	}

	@SuppressWarnings("static-access")
	protected void setColumnFilterTypeFromPreferences() {
		columnFilterType = getColumnFilterType(preferences, sAppPreferencesBase + preferences.FILTER_SEARCH_TYPE);
		setColumnFilterType(columnFilterType);
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
}
