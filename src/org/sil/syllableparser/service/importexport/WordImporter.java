// Copyright (c) 2016-2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.importexport;

import java.io.File;

import org.sil.syllableparser.model.LanguageProject;

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
