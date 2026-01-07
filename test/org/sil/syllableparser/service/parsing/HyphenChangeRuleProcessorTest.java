// Copyright (c) 2026 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.parsing;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.model.cvapproach.*;
import org.sil.syllableparser.model.hyphenapproach.HyphenChangeRule;
import org.sil.syllableparser.model.hyphenapproach.HyphenClass;
import org.sil.syllableparser.model.hyphenapproach.HyphenClassInWord;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter class is functioning
 *         correctly
 */
public class HyphenChangeRuleProcessorTest extends HyphenTestBase {

	List<HyphenChangeRule> changeRules;
	private HyphenChangeRuleProcessor hyphenRuleProcessor;
	HyphenChangeRule rule;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		changeRules = hyphenApproach.getActiveHyphenChangeRules();
		hyphenRuleProcessor = new HyphenChangeRuleProcessor(hyphenApproach);
	}

	// make sure the setup is what we expect
	@Test
	public void ruleProcessorTest() {
		assertEquals("Segment inventory size", 27, segmentInventory.size());
		assertEquals("Hyphen classes size", 3, hyphenClasses.size());
		assertEquals("Hyphen change rule size", 3, changeRules.size());
	}

//	@Test
	public void wordToSegmentToHyphenClassesTest() {

		checkProcessorResults("", false, 0, "", "");
		checkProcessorResults("a", true, 3, "#, V, #", "a");
		checkProcessorResults("d", true, 3, "#, C, #", "s");
		checkProcessorResults("n", true, 3, "#, N, #", "n");
		checkProcessorResults("Chiko", true, 6, "#, C, V, C, V, #", "Chi.ko");
		checkProcessorResults("dapbek", true, 8, "#, C, V, C, C, V, C, #", "dap.bak");
		checkProcessorResults("bampidon", true, 10, "#, C, V, N, C, V, C, V, N, #", "bam.pion");
		checkProcessorResults("bovdek", true, 8, "#, C, V, C, C, V, C, #", "bov.dek");
		checkProcessorResults("fuhgt", true, 7, "#, C, V, C, C, C, #", "fuhgt");
		checkProcessorResults("blofugh", true, 9, "#, C, C, V, C, V, C, C, #", "blo.fugh");
		checkProcessorResults("bo", true, 4, "#, C, V, #", "bo");
	}

	@Test
	public void matchRuleTest() {
		rule = hyphenApproach.getActiveHyphenChangeRules().get(2);
		checkRuleMatch("", false, 0, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("d", true, 3, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("a", true, 3, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("da", true, 4, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("dak", true, 5, rule, new boolean[] { true, false }, new int[] { 3, -1 });
		checkRuleMatch("dako", true, 6, rule, new boolean[] { true, false }, new int[] { 3, -1 });
		checkRuleMatch("dakot", true, 7, rule, new boolean[] { true, true }, new int[] { 3, 5 });
		checkRuleMatch("dakota", true, 8, rule, new boolean[] { true, true }, new int[] { 3, 5 });
		checkRuleMatch("dakotab", true, 9, rule, new boolean[] { true, true, true }, new int[] { 3, 5, 7 });
		rule = hyphenApproach.getActiveHyphenChangeRules().get(0);
		checkRuleMatch("", false, 0, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("n", true, 3, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("a", true, 3, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("na", true, 4, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("nak", true, 5, rule, new boolean[] { false, false }, new int[] { -1, -1 });
		checkRuleMatch("danko", true, 7, rule, new boolean[] { true, false }, new int[] { 4, -1 });
		checkRuleMatch("dankot", true, 8, rule, new boolean[] { true, false }, new int[] { 4, -1 });
		checkRuleMatch("dankonta", true, 10, rule, new boolean[] { true, true }, new int[] { 4, 7 });
		checkRuleMatch("dankontamb", true, 12, rule, new boolean[] { true, true, true }, new int[] { 4, 7, 10 });
		rule = hyphenApproach.getActiveHyphenChangeRules().get(1);
		checkRuleMatch("", false, 0, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("n", true, 3, rule, new boolean[] { true }, new int[] { 1 });
		checkRuleMatch("a", true, 3, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("na", true, 4, rule, new boolean[] { true, false }, new int[] { 1, -1 });
		checkRuleMatch("nak", true, 5, rule, new boolean[] { true, false }, new int[] { 1, -1 });
		checkRuleMatch("danko", true, 7, rule, new boolean[] { true, false }, new int[] { 3, -1 });
		checkRuleMatch("dankot", true, 8, rule, new boolean[] { true, false }, new int[] { 3, -1 });
		checkRuleMatch("dankonta", true, 10, rule, new boolean[] { true, true }, new int[] { 3, 6 });
		checkRuleMatch("dankontamb", true, 12, rule, new boolean[] { true, true, true }, new int[] { 3, 6, 9 });
	}

	protected void checkRuleMatch(String word, boolean expectedClasserSuccess, int numberOfClasses,
			HyphenChangeRule rule, boolean[] expectedRuleSuccess, int[] expectedClassIndex) {
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		boolean fSuccess = segResult.success;
		List<? extends CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		HyphenClasserResult ncResult = hyphenClasser.parseIntoHyphenClasses(segmentsInWord);
		fSuccess = ncResult.success;
		assertEquals("word classed into hyphen classes", expectedClasserSuccess, fSuccess);
		classesInWord = hyphenClasser.getClassesInWord();
		assertEquals("Expect " + numberOfClasses + " classes in word", numberOfClasses, classesInWord.size());
		HyphenChangeRuleState hcrs = new HyphenChangeRuleState(rule, classesInWord, -1);
		for (int i = 0; i < expectedRuleSuccess.length; i++) {
			fSuccess = hyphenRuleProcessor.ruleMatches(hcrs);
			assertEquals("rules processed", expectedRuleSuccess[i], fSuccess);
			assertEquals(expectedClassIndex[i], hcrs.getClassIndex());
		}
	}

	@Test
	public void applyRuleTest() {
		rule = hyphenApproach.getActiveHyphenChangeRules().get(2);
		checkApplyRule("", rule, "");
		checkApplyRule("d", rule, "#, C, #");
		checkApplyRule("a", rule, "#, V, #");
		checkApplyRule("da", rule, "#, C, V, #");
		checkApplyRule("dak", rule, "#, C, V, -, C, #");
		rule = hyphenApproach.getActiveHyphenChangeRules().get(0);
		checkApplyRule("", rule, "");
		checkApplyRule("n", rule, "#, N, #");
		checkApplyRule("a", rule, "#, V, #");
		checkApplyRule("na", rule, "#, N, V, #");
		checkApplyRule("nak", rule, "#, N, V, C, #");
		checkApplyRule("danko", rule, "#, C, V, N, -, C, V, #");
		checkApplyRule("dankot", rule, "#, C, V, N, -, C, V, C, #");
		checkApplyRule("onta", rule, "#, V, N, -, C, V, #");
		checkApplyRule("amb", rule, "#, V, N, -, C, #");
		rule = hyphenApproach.getActiveHyphenChangeRules().get(1);
		checkApplyRule("", rule, "");
		checkApplyRule("n", rule, "#, C, #");
		checkApplyRule("a", rule, "#, V, #");
		checkApplyRule("na", rule, "#, C, V, #");
		checkApplyRule("nak", rule, "#, C, V, C, #");
		checkApplyRule("danko", rule, "#, C, V, C, C, V, #");
		checkApplyRule("onta", rule, "#, V, C, C, V, #");
		checkApplyRule("amb", rule, "#, V, C, C, #");
	}

	protected void checkApplyRule(String word, HyphenChangeRule rule, String expectedClassRepresentation) {
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		boolean fSuccess = segResult.success;
		List<? extends CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		HyphenClasserResult ncResult = hyphenClasser.parseIntoHyphenClasses(segmentsInWord);
		fSuccess = ncResult.success;
		classesInWord = hyphenClasser.getClassesInWord();
		HyphenChangeRuleState hcrs = new HyphenChangeRuleState(rule, classesInWord, -1);
		fSuccess = hyphenRuleProcessor.ruleMatches(hcrs);
		hyphenRuleProcessor.applyRule(hcrs);
		assertEquals(expectedClassRepresentation, HyphenClasser.getClassesRepresentation(hcrs.getClassesInWord()));
	}

	protected void checkProcessorResults(String word, boolean success, int numberOfClasses, String expectedClasses,
			String expectedHyphenation) {
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		boolean fSuccess = segResult.success;
		List<? extends CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		HyphenClasserResult ncResult = hyphenClasser.parseIntoHyphenClasses(segmentsInWord);
		fSuccess = ncResult.success;
		assertEquals("word classed into hyphen classes", success, fSuccess);
		classesInWord = hyphenClasser.getClassesInWord();
		assertEquals("Expect " + numberOfClasses + " classes in word", numberOfClasses, classesInWord.size());
		assertEquals("Expected class sequence", expectedClasses, ncResult.sClasses);
		HyphenChangeRuleResult crResult = hyphenRuleProcessor.applyChangeRules(classesInWord);
		fSuccess = crResult.success;
		assertEquals("rules processed", success, fSuccess);
		assertEquals("Expected hyphenation", expectedHyphenation, crResult.sHyphenation);

	}
}
