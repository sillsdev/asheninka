// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.model;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.cvapproach.*;
import sil.org.syllableparser.model.LanguageProject;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter and CVNaturalClasser
 *         classes are functioning correctly
 */
public class CVApproachTest {

	CVApproach cva;
	private LanguageProject languageProject;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		cva = languageProject.getCVApproach();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// make sure the setup is what we expect
	@Test
	public void getActiveItemsTest() {
		assertEquals("CV natural classes size", 4, cva.getCVNaturalClasses().size());
		assertEquals("Active CV natural classes size", 3, cva.getActiveCVNaturalClasses().size());
		
		assertEquals("CV patterns size", 7, cva.getCVSyllablePatterns().size());
		assertEquals("Active CV patterns size", 6, cva.getActiveCVSyllablePatterns().size());
	}
}
