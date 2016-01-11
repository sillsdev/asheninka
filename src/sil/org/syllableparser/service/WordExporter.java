/**
 * 
 */
package sil.org.syllableparser.service;

import java.io.File;

import sil.org.syllableparser.model.Approach;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.view.ApproachController;

/**
 * @author Andy Black
 *
 */
public abstract class WordExporter {
	
	protected LanguageProject languageProject;
	
	abstract public void exportWords(File file, Approach approach);
	abstract public void exportWords(File file, ApproachController controller);

	public WordExporter() {
		super();
		languageProject = null;
	}

	public WordExporter(LanguageProject languageProject) {
		super();
		this.languageProject = languageProject;
	}
}
