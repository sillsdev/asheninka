// Copyright (c) 2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package org.sil.antlr4.templatefilterparser;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.antlr4.templatefilterparser.SegmentSequenceValidator;

public class SegmentSequenceValidatorTest {
	List<String> segmentsMasterList = Arrays.asList("a", "ai", "b", "c", "d", "e", "f", "fl",
			"fr", "\u00ED", // single combined Unicode acute i (�)
			"i\u0301", // combined acute i
			"H");

	SegmentSequenceValidator validator;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		validator = new SegmentSequenceValidator(segmentsMasterList);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void findSequenceOfSegmentsTest() {
		checkSegmentSequenceSearch("a", true, 1);
		checkSegmentSequenceSearch("ae", true, 2);
		checkSegmentSequenceSearch("ak", false, 1);
		checkSegmentSequenceSearch("ka", false, 0);
		checkSegmentSequenceSearch("fl", true, 2);
		checkSegmentSequenceSearch("fr", true, 2);
		checkSegmentSequenceSearch("fra", true, 3);
		checkSegmentSequenceSearch("afle", true, 4);
		checkSegmentSequenceSearch("frage", false, 3);
		checkSegmentSequenceSearch("frafge", false, 4);
		checkSegmentSequenceSearch("fraflge", false, 5);
	}

	/**
	 * @param sSeq
	 * @param fExpectedResult
	 * @param iExpectedMaxDepth
	 */
	private void checkSegmentSequenceSearch(String sSeq, boolean fExpectedResult, int iExpectedMaxDepth) {
		boolean fResult = validator.findSequenceOfSegments(sSeq);
		assertEquals(fExpectedResult, fResult);
		int iMaxDepth = validator.getMaxDepth();
		assertEquals(iExpectedMaxDepth, iMaxDepth);
	}

}
