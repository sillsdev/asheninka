/**
 * 
 */
package sil.org.syllableparser.view;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;

import org.controlsfx.control.StatusBar;

import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.Word;

/**
 * @author Andy Black
 *
 */
public abstract class ApproachController {
	
	protected MainApp mainApp;
	protected ResourceBundle bundle;
	protected Locale locale;
	
	abstract void handleInsertNewItem();
	abstract void handleRemoveItem();
	abstract void handleSyllabifyWords(StatusBar statusBar);
	abstract void handleConvertPredictedToCorrectSyllabification();
	abstract void handleFindWord();
	public abstract ArrayList<String> getHyphenatedWords(ObservableList<Word> words);

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
