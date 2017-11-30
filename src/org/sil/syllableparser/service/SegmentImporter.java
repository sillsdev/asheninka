// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service;

import java.io.File;
import java.util.ResourceBundle;

import org.sil.syllableparser.model.LanguageProject;

/**
 * @author Andy Black
 *
 */
public abstract class SegmentImporter {

	protected LanguageProject languageProject;

	abstract public void importSegments(File file) throws SegmentImporterException;

	public SegmentImporter() {
		super();
		languageProject = null;
	}

	public SegmentImporter(LanguageProject languageProject) {
		super();
		this.languageProject = languageProject;
	}

}
