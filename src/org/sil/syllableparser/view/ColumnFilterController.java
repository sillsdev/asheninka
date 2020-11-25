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
	protected String sTextToSearchFor;
	protected ResourceBundle bundle;
	protected Locale locale;
	private ObservableList<Word> words = FXCollections.observableArrayList();
	private boolean resultIsOK = false;
	WordsFilterType wordsFilterType = WordsFilterType.WORDS;
	ColumnFilterType columnFilterType = ColumnFilterType.ANYWHERE;
	WordsFilter filter;
	
	public ObservableList<Word> getWords() {
		return words;
	}
	
	public void setWords(ObservableList<Word> words) {
		this.words = words;
	}

	public boolean isResultIsOK() {
		return resultIsOK;
	}

	public WordsFilterType getWordsFilterType() {
		return wordsFilterType;
	}

	public void setWordsFilterType(WordsFilterType wordsFilterType) {
		this.wordsFilterType = wordsFilterType;
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
		this.filter = filter;
	}

	@FXML
	private void handleOK() {
		String sTextToSearchFor = textToSearchFor.getText().trim();
		if (sTextToSearchFor.length() == 0) {
			return; // just in case...
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

	protected void processFilter() {
		resultIsOK = true;
		// create list of words that match the pattern
		filter = new WordsFilter(wordsFilterType, columnFilterType,
				matchCaseCheckBox.isSelected(), matchDiacriticsCheckBox.isSelected(),
				textToSearchFor.getText());
		words = filter.applyFilter(words);
		dialogStage.close();
	}

	@FXML
	private void handleClose() {
		//preferences.setLastWindowParameters(lastTryAWord, dialogStage);
		resultIsOK = false;
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

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
}
