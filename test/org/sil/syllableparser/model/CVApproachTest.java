// Copyright (c) 2016-2017 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.cvapproach.*;

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
		assertEquals("CV natural classes size", 7, cva.getCVNaturalClasses().size());
		assertEquals("Active CV natural classes size", 6, cva.getActiveCVNaturalClasses().size());

		assertEquals("CV patterns size", 9, cva.getCVSyllablePatterns().size());
		assertEquals("Active CV patterns size", 7, cva.getActiveCVSyllablePatterns().size());
	}

	@Test
	public void getAllSegmentsFromNaturalClassTest() {
		CVNaturalClass nc = cva.getCVNaturalClasses().get(0);
		assertEquals("C", nc.getNCName());
		List<Segment> segments = nc.getAllSegments();
		assertEquals(20, segments.size());
	}
}
