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
import org.sil.syllableparser.model.npapproach.NPRule;
import org.sil.syllableparser.model.npapproach.NPRuleAction;
import org.sil.syllableparser.model.npapproach.NPRuleLevel;

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

	@Test
	public void createDefaultSetOfRules() {
		npa.getNPRules().clear();
		npa.createDefaultSetOfRules();
		assertEquals(5, npa.getNPRules().size());
		assertEquals(5, npa.getActiveNPRules().size());
		assertEquals(5, npa.getValidActiveNPRules().size());
		NPRule rule = npa.getNPRules().get(0);
		assertEquals("Nucleus", rule.getRuleName());
		assertEquals("Build nucleus", rule.getDescription());
		assertEquals(NPRuleAction.BUILD, rule.getRuleAction());
		assertEquals(NPRuleLevel.ALL, rule.getRuleLevel());
		assertNotNull(rule.getAffectedSegOrNC());
		assertNull(rule.getContextSegOrNC());
		assertEquals("V", rule.getAffectedSegmentOrNaturalClass());
		assertEquals("", rule.getContextSegmentOrNaturalClass());

		rule = npa.getNPRules().get(1);
		assertEquals("Onset", rule.getRuleName());
		assertEquals("Attach onset to N''", rule.getDescription());
		assertEquals(NPRuleAction.ATTACH, rule.getRuleAction());
		assertEquals(NPRuleLevel.N_DOUBLE_BAR, rule.getRuleLevel());
		assertNotNull(rule.getAffectedSegOrNC());
		assertNotNull(rule.getContextSegOrNC());
		assertEquals("C", rule.getAffectedSegmentOrNaturalClass());
		assertEquals("V", rule.getContextSegmentOrNaturalClass());

		rule = npa.getNPRules().get(2);
		assertEquals("Coda", rule.getRuleName());
		assertEquals("Attach coda to N'", rule.getDescription());
		assertEquals(NPRuleAction.ATTACH, rule.getRuleAction());
		assertEquals(NPRuleLevel.N_BAR, rule.getRuleLevel());
		assertNotNull(rule.getAffectedSegOrNC());
		assertNotNull(rule.getContextSegOrNC());
		assertEquals("C", rule.getAffectedSegmentOrNaturalClass());
		assertEquals("V", rule.getContextSegmentOrNaturalClass());

		rule = npa.getNPRules().get(3);
		assertEquals("Additional onset", rule.getRuleName());
		assertEquals("Augment onset with another onset", rule.getDescription());
		assertEquals(NPRuleAction.AUGMENT, rule.getRuleAction());
		assertEquals(NPRuleLevel.N_DOUBLE_BAR, rule.getRuleLevel());
		assertNotNull(rule.getAffectedSegOrNC());
		assertNotNull(rule.getContextSegOrNC());
		assertEquals("C", rule.getAffectedSegmentOrNaturalClass());
		assertEquals("C", rule.getContextSegmentOrNaturalClass());

		rule = npa.getNPRules().get(4);
		assertEquals("Additional coda", rule.getRuleName());
		assertEquals("Augment coda with another coda", rule.getDescription());
		assertEquals(NPRuleAction.AUGMENT, rule.getRuleAction());
		assertEquals(NPRuleLevel.N_BAR, rule.getRuleLevel());
		assertNotNull(rule.getAffectedSegOrNC());
		assertNotNull(rule.getContextSegOrNC());
		assertEquals("C", rule.getAffectedSegmentOrNaturalClass());
		assertEquals("C", rule.getContextSegmentOrNaturalClass());
	}
}
