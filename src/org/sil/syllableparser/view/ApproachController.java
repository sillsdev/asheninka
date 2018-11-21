// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import org.controlsfx.control.StatusBar;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.Word;

/**
 * @author Andy Black
 *
 */
public abstract class ApproachController {
	
	protected MainApp mainApp;
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
	abstract void handleSyllabifyWords(StatusBar statusBar);
	abstract void handleTryAWord();
	abstract void handleConvertPredictedToCorrectSyllabification();
	abstract void handleFindWord();
	abstract boolean anythingSelected();
	public abstract ArrayList<String> getHyphenatedWordsListWord(ObservableList<Word> words);
	public abstract ArrayList<String> getHyphenatedWordsParaTExt(ObservableList<Word> words);
	public abstract ArrayList<String> getHyphenatedWordsXLingPaper(ObservableList<Word> words);
	public abstract String getViewUsed();
	
	/**
	 * @return the mainApp
	 */
	public MainApp getMainApp() {
		return mainApp;
	}

	/**
	 * @param mainApp the mainApp to set
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
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


}