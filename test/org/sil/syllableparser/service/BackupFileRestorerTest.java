// Copyright (c) 2016-2025 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.LanguageProject;

/**
 * @author Andy Black
 *
 */
public class BackupFileRestorerTest {
	
	File backupFile;
	LanguageProject languageProject;
	Locale locale;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		languageProject = new LanguageProject();
		locale = new Locale("en");
		backupFile = new File(Constants.UNIT_TEST_BACKUP_FILE_NAME);
		//XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		//File file = new File(Constants.UNIT_TEST_DATA_FILE);
		//xmlBackEndProvider.loadLanguageDataFromFile(file);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		// expect no data to begin
		assertEquals(0, languageProject.getSegmentInventory().size());
		BackupFileRestorer restorer = new BackupFileRestorer(backupFile);
		restorer.doRestore(languageProject, locale);
		// now have data
		assertEquals(27, languageProject.getSegmentInventory().size());
		
	}

}
