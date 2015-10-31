/**
 * 
 */
package sil.org.syllableparser.view;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import sil.org.syllableparser.MainApp;

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
	abstract void handleSyllabifyWords();
	abstract void createNewWord(String word);
	abstract ArrayList<String> getHyphenatedWords();

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
