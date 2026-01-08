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
import org.sil.syllableparser.model.hyphenapproach.HyphenClass;

import javafx.collections.ObservableList;

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
		checkClasserResults("", false, 0, "", "");
		checkClasserResults("a", true, 3, "#, V, #", "a");
		checkClasserResults("d", true, 3, "#, C, #", "d");
		checkClasserResults("n", true, 3, "#, N, #", "n");
		checkClasserResults("Chiko", true, 6, "#, C, V, C, V, #", "Ch, i, k, o");
		checkClasserResults("dapbek", true, 8, "#, C, V, C, C, V, C, #", "d, a, p, b, e, k");
		checkClasserResults("bampidon", true, 10, "#, C, V, N, C, V, C, V, N, #", "b, a, m, p, i, d, o, n");
		checkClasserResults("bovdek", true, 8, "#, C, V, C, C, V, C, #", "b, o, v, d, e, k");
		checkClasserResults("fuhgt", true, 7, "#, C, V, C, C, C, #", "f, u, h, g, t");
		checkClasserResults("blofugh", true, 9, "#, C, C, V, C, V, C, C, #", "b, l, o, f, u, g, h");
		checkClasserResults("bo", true, 4, "#, C, V, #", "b, o");
		// now test for failures
		hyphenClasses = hyphenApproach.getHyphenClasses();
		HyphenClass hc = hyphenApproach.getActiveHyphenClasses().get(0); // class V
		hc.getSegments().remove(0); // segment /a/
		hyphenClasses.set(0, hc);
		hyphenApproach.setHyphenClasses(hyphenClasses);
		checkClasserResults("a", false, 1, "#", "");
		checkClasserResults("ba", false, 2, "#, C", "b");
	}

	protected void checkClasserResults(String word, boolean success, int numberOfClasses,
			String expectedClasses, String expectedGraphemes) {
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		boolean fSuccess = segResult.success;
		List<? extends CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		HyphenClasserResult hcResult = hyphenClasser.parseIntoHyphenClasses(segmentsInWord);
		fSuccess = hcResult.success;
		assertEquals("word classed into hyphen classes", success, fSuccess);
		classesInWord = hyphenClasser.getClassesInWord();
		assertEquals("Expect " + numberOfClasses + " classes in word", numberOfClasses,
				classesInWord.size());
		assertEquals("Expected class sequence", expectedClasses, hcResult.sClassesSoFar);
		assertEquals("Expected grapheme sequence", expectedGraphemes, hcResult.sGraphemesSoFar);
	}
}
