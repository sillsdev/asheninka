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
	public void removalTest() {
		result = 0;
		result = OTStructuralOptions.INITIALIZED & OTStructuralOptions.REMOVE_ONSET;
		assertEquals(62, result);
		result = OTStructuralOptions.INITIALIZED & OTStructuralOptions.REMOVE_NUCLEUS;
		assertEquals(61, result);
		result = OTStructuralOptions.INITIALIZED & OTStructuralOptions.REMOVE_CODA;
		assertEquals(59, result);
		result = OTStructuralOptions.INITIALIZED & OTStructuralOptions.REMOVE_UNPARSED;
		assertEquals(55, result);
	}
	
	@Test
	public void isOptionTest() {
		structuredOptions = OTStructuralOptions.INITIALIZED;
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
