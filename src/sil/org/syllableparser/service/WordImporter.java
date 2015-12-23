/**
 * 
 */
package sil.org.syllableparser.service;

import java.io.File;
import java.util.ResourceBundle;

import sil.org.syllableparser.model.LanguageProject;

/**
 * @author Andy Black
 *
 */
public abstract class WordImporter {

	protected LanguageProject languageProject;

	abstract public void importWords(File file, String sUntested);

	public WordImporter() {
		super();
		languageProject = null;
	}

	public WordImporter(LanguageProject languageProject) {
		super();
		this.languageProject = languageProject;
	}

}
