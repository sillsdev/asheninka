// Copyright (c) 2016-2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.npapproach.NPApproach;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter and CVNaturalClasser
 *         classes are functioning correctly
 */
public class NPApproachTest {

	NPApproach npa;
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
		npa = languageProject.getNPApproach();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// make sure the setup is what we expect
	@Test
	public void getActiveAndValidItemsTest() {
		assertEquals(6, npa.getNPRules().size());
		assertEquals(5, npa.getActiveNPRules().size());
		assertEquals(5, npa.getValidActiveNPRules().size());
		npa.getNPRules().get(0).setActive(false);
		assertEquals(6, npa.getNPRules().size());
		assertEquals(4, npa.getActiveNPRules().size());
		assertEquals(4, npa.getValidActiveNPRules().size());
		npa.getNPRules().get(1).setIsValid(false);
		assertEquals(6, npa.getNPRules().size());
		assertEquals(4, npa.getActiveNPRules().size());
		assertEquals(3, npa.getValidActiveNPRules().size());
	}
}
