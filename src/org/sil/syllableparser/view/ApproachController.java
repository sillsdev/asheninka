// Copyright (c) 2016-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import org.controlsfx.control.StatusBar;
import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.Word;

/**
 * @author Andy Black
 *
 */
public abstract class ApproachController {
	
	protected MainApp mainApp;
	protected ApplicationPreferences prefs;
	protected ResourceBundle bundle;
	protected Locale locale;
	protected RootLayoutController rootController;
	
	public abstract void handleCompareImplementations();
	public abstract void handleCopy();
	public abstract void handleToolBarCopy();
	public abstract void handleCut();
	public abstract void handleToolBarCut();
	public abstract void handlePaste();
	public abstract void handleToolBarPaste();
	abstract void handleInsertNewItem();
	abstract void handleRemoveItem();
	abstract void handlePreviousItem();
	abstract void handleNextItem();
	abstract void handleSyllabifyWords(StatusBar statusBar);
	abstract void handleTryAWord();
	abstract void handleConvertPredictedToCorrectSyllabification();
	abstract void handleFindWord();
	abstract void handleFilterCorrectSyllabifications();
	abstract void handleFilterPredictedSyllabifications();
	abstract void handleFilterWords();
	abstract void handleRemoveAllFilters();
	abstract boolean anythingSelected();
	public abstract ArrayList<String> getHyphenatedWordsListWord(ObservableList<Word> words);
	public abstract ArrayList<String> getHyphenatedWordsParaTExt(ObservableList<Word> words);
	public abstract ArrayList<String> getHyphenatedWordsXLingPaper(ObservableList<Word> words);
	public abstract String getViewUsed();
	public abstract void toggleView();
	
	public MainApp getMainApp() {
		return mainApp;
	}
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	public ApplicationPreferences getPrefs() {
		return prefs;
	}
	public void setPrefs(ApplicationPreferences prefs) {
		this.prefs = prefs;
	}
	public void setRootLayout(RootLayoutController controller) {
		rootController = controller;
	}
	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	protected void setWordForTryAWord(TryAWordController controller, TableView<Word> wordsTable) {
		Word word = (Word) wordsTable.getSelectionModel().getSelectedItem();
		if (word != null) {
			String sCurrentWord = word.getWord();
			controller.getWordToTry().setText(sCurrentWord);
		}
	}

}
