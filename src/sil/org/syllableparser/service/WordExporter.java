// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.service;

import java.io.File;
import java.util.ResourceBundle;

import org.controlsfx.control.StatusBar;

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
