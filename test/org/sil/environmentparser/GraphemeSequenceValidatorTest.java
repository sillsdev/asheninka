// Copyright (c) 2016-2017 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package org.sil.environmentparser;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.sil.environmentparser.GraphemeSequenceValidator;

public class GraphemeSequenceValidatorTest {
	List<String> graphemesMasterList = Arrays.asList("a", "ai", "b", "c", "d", "e", "f", "fl",
			"fr", "\u00ED", // single combined Unicode acute i (�)
			"i\u0301", // combined acute i
			"H");

	GraphemeSequenceValidator validator;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		validator = new GraphemeSequenceValidator(graphemesMasterList);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void findSequenceOfGraphemesTest() {
		checkGraphemeSequenceSearch("a", true, 1);
		checkGraphemeSequenceSearch("ae", true, 2);
		checkGraphemeSequenceSearch("ak", false, 1);
		checkGraphemeSequenceSearch("ka", false, 0);
		checkGraphemeSequenceSearch("fl", true, 2);
		checkGraphemeSequenceSearch("fr", true, 2);
		checkGraphemeSequenceSearch("fra", true, 3);
		checkGraphemeSequenceSearch("afle", true, 4);
		checkGraphemeSequenceSearch("frage", false, 3);
		checkGraphemeSequenceSearch("frafge", false, 4);
		checkGraphemeSequenceSearch("fraflge", false, 5);
	}

	private void checkGraphemeSequenceSearch(String sSeq, boolean fExpectedResult, int iExpectedMaxDepth) {
		boolean fResult = validator.findSequenceOfGraphemes(sSeq);
		assertEquals(fExpectedResult, fResult);
		int iMaxDepth = validator.getMaxDepth();
		assertEquals(iExpectedMaxDepth, iMaxDepth);
	}

}
