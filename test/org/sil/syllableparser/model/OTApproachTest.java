// Copyright (c) 2021-2025 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.otapproach.OTApproach;
import org.sil.syllableparser.model.otapproach.OTConstraint;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter and CVNaturalClasser
 *         classes are functioning correctly
 */
public class OTApproachTest {

	OTApproach ota;
	private LanguageProject languageProject;
	ResourceBundle bundle;

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
		ota = languageProject.getOTApproach();
		bundle = ResourceBundle.getBundle(Constants.RESOURCE_LOCATION);
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
		assertEquals(8, ota.getOTConstraints().size());
		assertEquals(8, ota.getActiveOTConstraints().size());
		assertEquals(8, ota.getValidActiveOTConstraints().size());
		ota.getOTConstraints().get(0).setActive(false);
		assertEquals(8, ota.getOTConstraints().size());
		assertEquals(7, ota.getActiveOTConstraints().size());
		assertEquals(7, ota.getValidActiveOTConstraints().size());
		ota.getOTConstraints().get(1).setIsValid(false);
		assertEquals(8, ota.getOTConstraints().size());
		assertEquals(7, ota.getActiveOTConstraints().size());
		assertEquals(6, ota.getValidActiveOTConstraints().size());

		assertEquals(2, ota.getOTConstraintRankings().size());
		assertEquals(2, ota.getActiveOTConstraintRankings().size());
		ota.getOTConstraintRankings().get(0).setActive(false);
		assertEquals(2, ota.getOTConstraintRankings().size());
		assertEquals(1, ota.getActiveOTConstraintRankings().size());
	}

	@Test
	public void createDefaultSetOfConstraints() {
		ota.getOTConstraints().clear();
		ota.createDefaultSetOfConstraints(bundle);
		assertEquals(8, ota.getOTConstraints().size());
		assertEquals(8, ota.getActiveOTConstraints().size());
		assertEquals(8, ota.getValidActiveOTConstraints().size());
		OTConstraint constraint = ota.getOTConstraints().get(0);
		assertEquals("*Margin/V", constraint.getConstraintName());
		assertEquals("Vowels are neither onsets nor codas", constraint.getDescription());
		assertEquals(5, constraint.getStructuralOptions1());
		assertEquals(0, constraint.getStructuralOptions2());
		assertNotNull(constraint.getAffectedSegOrNC1());
		assertNull(constraint.getAffectedSegOrNC2());
		assertEquals("V", constraint.getAffectedElement1());
		assertEquals("", constraint.getAffectedElement2());

		constraint = ota.getOTConstraints().get(1);
		assertEquals("*Peak/C", constraint.getConstraintName());
		assertEquals("Syllable peaks are not consonants", constraint.getDescription());
		assertEquals(2, constraint.getStructuralOptions1());
		assertEquals(0, constraint.getStructuralOptions2());
		assertNotNull(constraint.getAffectedSegOrNC1());
		assertNull(constraint.getAffectedSegOrNC2());
		assertEquals("C", constraint.getAffectedElement1());
		assertEquals("", constraint.getAffectedElement2());

		constraint = ota.getOTConstraints().get(2);
		assertEquals("*Complex/Onset", constraint.getConstraintName());
		assertEquals("Avoid complex onsets", constraint.getDescription());
		assertEquals(1, constraint.getStructuralOptions1());
		assertEquals(1, constraint.getStructuralOptions2());
		assertNotNull(constraint.getAffectedSegOrNC1());
		assertNotNull(constraint.getAffectedSegOrNC2());
		assertEquals("C", constraint.getAffectedElement1());
		assertEquals("C", constraint.getAffectedElement2());

		constraint = ota.getOTConstraints().get(3);
		assertEquals("*Complex/Coda", constraint.getConstraintName());
		assertEquals("Avoid complex codas", constraint.getDescription());
		assertEquals(4, constraint.getStructuralOptions1());
		assertEquals(4, constraint.getStructuralOptions2());
		assertNotNull(constraint.getAffectedSegOrNC1());
		assertNotNull(constraint.getAffectedSegOrNC2());
		assertEquals("C", constraint.getAffectedElement1());
		assertEquals("C", constraint.getAffectedElement2());

		constraint = ota.getOTConstraints().get(4);
		assertEquals("NoCoda", constraint.getConstraintName());
		assertEquals("Codas not allowed", constraint.getDescription());
		assertEquals(4, constraint.getStructuralOptions1());
		assertEquals(0, constraint.getStructuralOptions2());
		assertNull(constraint.getAffectedSegOrNC1());
		assertNull(constraint.getAffectedSegOrNC2());
		assertEquals("<Any>", constraint.getAffectedElement1());
		assertEquals("", constraint.getAffectedElement2());

		constraint = ota.getOTConstraints().get(5);
		assertEquals("Parse", constraint.getConstraintName());
		assertEquals("Every segment should be parsed", constraint.getDescription());
		assertEquals(8, constraint.getStructuralOptions1());
		assertEquals(0, constraint.getStructuralOptions2());
		assertNull(constraint.getAffectedSegOrNC1());
		assertNull(constraint.getAffectedSegOrNC2());
		assertEquals("<Any>", constraint.getAffectedElement1());
		assertEquals("", constraint.getAffectedElement2());

		constraint = ota.getOTConstraints().get(6);
		assertEquals("Onset1", constraint.getConstraintName());
		assertEquals("Avoid anything other than an onset before a nucleus", constraint.getDescription());
		assertEquals(14, constraint.getStructuralOptions1());
		assertEquals(2, constraint.getStructuralOptions2());
		assertNull(constraint.getAffectedSegOrNC1());
		assertNull(constraint.getAffectedSegOrNC2());
		assertEquals("<Any>", constraint.getAffectedElement1());
		assertEquals("<Any>", constraint.getAffectedElement2());

		constraint = ota.getOTConstraints().get(7);
		assertEquals("Onset2", constraint.getConstraintName());
		assertEquals("Avoid a nucleus word initially", constraint.getDescription());
		assertEquals(18, constraint.getStructuralOptions1());
		assertEquals(0, constraint.getStructuralOptions2());
		assertNull(constraint.getAffectedSegOrNC1());
		assertNull(constraint.getAffectedSegOrNC2());
		assertEquals("<Any>", constraint.getAffectedElement1());
		assertEquals("", constraint.getAffectedElement2());
	}
}
