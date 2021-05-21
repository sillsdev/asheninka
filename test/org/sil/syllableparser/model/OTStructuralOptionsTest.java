/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.model.otapproach.OTSegmentInSyllable;
import org.sil.syllableparser.model.otapproach.OTStructuralOptions;

/**
 * @author Andy Black
 *
 */
public class OTStructuralOptionsTest {

	int result;
	int structuredOptions;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		result= 0;
		structuredOptions = OTStructuralOptions.INITIALIZED;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void operationsTest() {
		OTSegmentInSyllable segInSyl = new OTSegmentInSyllable(null, null);
		assertEquals(OTStructuralOptions.FOUR_CORE_OPTIONS_SET, segInSyl.getStructuralOptions());
		assertEquals(4, segInSyl.getCoreOptionsLeft());
		assertEquals(true, segInSyl.removeOnset());
		assertEquals(3, segInSyl.getCoreOptionsLeft());
		assertEquals(false, segInSyl.removeOnset());
		assertEquals(3, segInSyl.getCoreOptionsLeft());

		assertEquals(true, segInSyl.removeNuleus());
		assertEquals(2, segInSyl.getCoreOptionsLeft());
		assertEquals(false, segInSyl.removeNuleus());
		assertEquals(2, segInSyl.getCoreOptionsLeft());

		assertEquals(true, segInSyl.removeCoda());
		assertEquals(1, segInSyl.getCoreOptionsLeft());
		assertEquals(false, segInSyl.removeCoda());
		assertEquals(1, segInSyl.getCoreOptionsLeft());

		assertEquals(false, segInSyl.removeUnparsed());
		assertEquals(1, segInSyl.getCoreOptionsLeft());
		assertEquals(false, segInSyl.removeUnparsed());
		assertEquals(1, segInSyl.getCoreOptionsLeft());

		segInSyl = new OTSegmentInSyllable(null, null);
		assertEquals(OTStructuralOptions.FOUR_CORE_OPTIONS_SET, segInSyl.getStructuralOptions());
		assertEquals(true, segInSyl.removeUnparsed());
		assertEquals(3, segInSyl.getCoreOptionsLeft());
		assertEquals(false, segInSyl.removeUnparsed());
		assertEquals(3, segInSyl.getCoreOptionsLeft());
	}

	@Test
	public void isOptionAfterOperationTest() {
		OTSegmentInSyllable segInSyl = new OTSegmentInSyllable(null, null);
		// onset
		segInSyl = new OTSegmentInSyllable(null, null);
		segInSyl.removeUnparsed();
		assertEquals(false, segInSyl.isUnparsed());
		segInSyl.removeNuleus();
		assertEquals(false, segInSyl.isNucleus());
		segInSyl.removeCoda();
		assertEquals(false, segInSyl.isCoda());
		assertEquals(true, segInSyl.isOnset());

		// nucleus
		segInSyl = new OTSegmentInSyllable(null, null);
		segInSyl.removeOnset();
		assertEquals(false, segInSyl.isOnset());
		segInSyl.removeUnparsed();
		assertEquals(false, segInSyl.isUnparsed());
		segInSyl.removeCoda();
		assertEquals(false, segInSyl.isCoda());
		assertEquals(true, segInSyl.isNucleus());

		// coda
		segInSyl = new OTSegmentInSyllable(null, null);
		segInSyl.removeOnset();
		assertEquals(false, segInSyl.isOnset());
		segInSyl.removeNuleus();
		assertEquals(false, segInSyl.isNucleus());
		segInSyl.removeUnparsed();
		assertEquals(false, segInSyl.isUnparsed());
		assertEquals(true, segInSyl.isCoda());

		// unparsed
		segInSyl = new OTSegmentInSyllable(null, null);
		segInSyl.removeOnset();
		assertEquals(false, segInSyl.isOnset());
		segInSyl.removeNuleus();
		assertEquals(false, segInSyl.isNucleus());
		segInSyl.removeCoda();
		assertEquals(false, segInSyl.isCoda());
		assertEquals(true, segInSyl.isUnparsed());
	}

	@Test
	public void removalTest() {
		result = 0;
		result = OTStructuralOptions.ALL_SET & OTStructuralOptions.REMOVE_ONSET;
		assertEquals(62, result);
		result = OTStructuralOptions.ALL_SET & OTStructuralOptions.REMOVE_NUCLEUS;
		assertEquals(61, result);
		result = OTStructuralOptions.ALL_SET & OTStructuralOptions.REMOVE_CODA;
		assertEquals(59, result);
		result = OTStructuralOptions.ALL_SET & OTStructuralOptions.REMOVE_UNPARSED;
		assertEquals(55, result);
	}
	
	@Test
	public void isOptionTest() {
		structuredOptions = OTStructuralOptions.ALL_SET;
		assertTrue((structuredOptions & OTStructuralOptions.WORD_INITIAL) > 0);
		assertTrue((structuredOptions & OTStructuralOptions.ONSET) > 0);
		assertTrue((structuredOptions & OTStructuralOptions.NUCLEUS) > 0);
		assertTrue((structuredOptions & OTStructuralOptions.CODA) > 0);
		assertTrue((structuredOptions & OTStructuralOptions.UNPARSED) > 0);
		assertTrue((structuredOptions & OTStructuralOptions.WORD_FINAL) > 0);

		structuredOptions = 0;
		assertFalse((structuredOptions & OTStructuralOptions.WORD_INITIAL) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.ONSET) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.NUCLEUS) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.CODA) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.UNPARSED) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.WORD_FINAL) > 0);
		
		structuredOptions = OTStructuralOptions.WORD_INITIAL + OTStructuralOptions.ONSET;
		assertTrue((structuredOptions & OTStructuralOptions.WORD_INITIAL) > 0);
		assertTrue((structuredOptions & OTStructuralOptions.ONSET) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.NUCLEUS) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.CODA) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.UNPARSED) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.WORD_FINAL) > 0);
		
		structuredOptions = OTStructuralOptions.WORD_INITIAL + OTStructuralOptions.NUCLEUS;
		assertTrue((structuredOptions & OTStructuralOptions.WORD_INITIAL) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.ONSET) > 0);
		assertTrue((structuredOptions & OTStructuralOptions.NUCLEUS) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.CODA) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.UNPARSED) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.WORD_FINAL) > 0);
		
		structuredOptions = OTStructuralOptions.ONSET + OTStructuralOptions.NUCLEUS;
		assertFalse((structuredOptions & OTStructuralOptions.WORD_INITIAL) > 0);
		assertTrue((structuredOptions & OTStructuralOptions.ONSET) > 0);
		assertTrue((structuredOptions & OTStructuralOptions.NUCLEUS) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.CODA) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.UNPARSED) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.WORD_FINAL) > 0);

		structuredOptions = OTStructuralOptions.ONSET + OTStructuralOptions.NUCLEUS + OTStructuralOptions.CODA;
		assertFalse((structuredOptions & OTStructuralOptions.WORD_INITIAL) > 0);
		assertTrue((structuredOptions & OTStructuralOptions.ONSET) > 0);
		assertTrue((structuredOptions & OTStructuralOptions.NUCLEUS) > 0);
		assertTrue((structuredOptions & OTStructuralOptions.CODA) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.UNPARSED) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.WORD_FINAL) > 0);

		structuredOptions = OTStructuralOptions.NUCLEUS + OTStructuralOptions.WORD_FINAL;
		assertFalse((structuredOptions & OTStructuralOptions.WORD_INITIAL) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.ONSET) > 0);
		assertTrue((structuredOptions & OTStructuralOptions.NUCLEUS) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.CODA) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.UNPARSED) > 0);
		assertTrue((structuredOptions & OTStructuralOptions.WORD_FINAL) > 0);

		structuredOptions = OTStructuralOptions.UNPARSED + OTStructuralOptions.WORD_FINAL;
		assertFalse((structuredOptions & OTStructuralOptions.WORD_INITIAL) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.ONSET) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.NUCLEUS) > 0);
		assertFalse((structuredOptions & OTStructuralOptions.CODA) > 0);
		assertTrue((structuredOptions & OTStructuralOptions.UNPARSED) > 0);
		assertTrue((structuredOptions & OTStructuralOptions.WORD_FINAL) > 0);
		}

}
