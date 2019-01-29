// Copyright (c) 2016-2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.importexport;

import java.io.File;
import java.util.ResourceBundle;

import org.controlsfx.control.StatusBar;
import org.sil.syllableparser.model.Approach;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.view.ApproachController;

/**
 * @author Andy Black
 *
 */
public abstract class WordExporter {
	
	protected LanguageProject languageProject;
	
	abstract public void exportWords(File file, Approach approach);
	abstract public void exportWords(File file, ApproachController controller, StatusBar statusBar, ResourceBundle bundle);

	public WordExporter() {
		super();
		languageProject = null;
	}

	public WordExporter(LanguageProject languageProject) {
		super();
		this.languageProject = languageProject;
	}
}
