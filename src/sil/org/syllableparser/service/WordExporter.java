/**
 * 
 */
package sil.org.syllableparser.service;

import sil.org.syllableparser.model.LanguageProject;

/**
 * @author Andy Black
 *
 */
public class WordExporter {
	
	protected LanguageProject languageProject;
	

	public WordExporter() {
		super();
		languageProject = null;
	}

	public WordExporter(LanguageProject languageProject) {
		super();
		this.languageProject = languageProject;
	}
}
