// Copyright (c) 2026 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.parsing;

import static org.junit.Assert.*;

import java.util.List;
import org.junit.Test;
import org.sil.syllableparser.model.cvapproach.*;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter class is functioning correctly
 */
public class HyphenClasserTest extends HyphenTestBase {

	// make sure the setup is what we expect
	@Test
	public void classerTest() {
		assertEquals("Segment inventory size", 27, segmentInventory.size());
		assertEquals("Hyphen classes size", 3, hyphenClasses.size());
	}

	@Test
	public void wordToSegmentToHyphenClassesTest() {
		checkClasserResults("", false, 0, "");
		checkClasserResults("a", true, 3, "#, V, #");
		checkClasserResults("d", true, 3, "#, C, #");
		checkClasserResults("n", true, 3, "#, N, #");
		checkClasserResults("Chiko", true, 6, "#, C, V, C, V, #");
		checkClasserResults("dapbek", true, 8, "#, C, V, C, C, V, C, #");
		checkClasserResults("bampidon", true, 10, "#, C, V, N, C, V, C, V, N, #");
		checkClasserResults("bovdek", true, 8, "#, C, V, C, C, V, C, #");
		checkClasserResults("fuhgt", true, 7, "#, C, V, C, C, C, #");
		checkClasserResults("blofugh", true, 9, "#, C, C, V, C, V, C, C, #");
		checkClasserResults("bo", true, 4, "#, C, V, #");
	}

	protected void checkClasserResults(String word, boolean success, int numberOfClasses,
			String expectedClasses) {
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		boolean fSuccess = segResult.success;
		List<? extends CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		HyphenClasserResult hcResult = hyphenClasser.parseIntoHyphenClasses(segmentsInWord);
		fSuccess = hcResult.success;
		assertEquals("word classed into hyphen classes", success, fSuccess);
		classesInWord = hyphenClasser.getClassesInWord();
		assertEquals("Expect " + numberOfClasses + " classes in word", numberOfClasses,
				classesInWord.size());
		assertEquals("Expected class sequence", expectedClasses, hcResult.sClasses);
	}
}
