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
	boolean fSuccess;
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
		rule.setWordInitial(true);
		checkRuleMatch("", false, 0, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("n", true, 3, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("a", true, 3, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("na", true, 4, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("nak", true, 5, rule, new boolean[] { false, false }, new int[] { -1, -1 });
		checkRuleMatch("nko", true, 5, rule, new boolean[] { true, false }, new int[] { 2, -1 });
		checkRuleMatch("nkonta", true, 8, rule, new boolean[] { true, false }, new int[] { 2, -1 });
		rule.setWordInitial(false);
		rule.setWordFinal(true);
		checkRuleMatch("", false, 0, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("n", true, 3, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("a", true, 3, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("na", true, 4, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("nak", true, 5, rule, new boolean[] { false, false }, new int[] { -1, -1 });
		checkRuleMatch("onk", true, 5, rule, new boolean[] { true }, new int[] { 3 });
		checkRuleMatch("nkont", true, 7, rule, new boolean[] { true }, new int[] { 5 });
		rule.setWordInitial(true);
		checkRuleMatch("", false, 0, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("n", true, 3, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("a", true, 3, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("na", true, 4, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("nak", true, 5, rule, new boolean[] { false, false }, new int[] { -1, -1 });
		checkRuleMatch("ank", true, 5, rule, new boolean[] { false, false }, new int[] { -1, -1 });
		checkRuleMatch("nka", true, 5, rule, new boolean[] { false, false }, new int[] { -1, -1 });
		checkRuleMatch("nk", true, 4, rule, new boolean[] { true }, new int[] { 2 });
		// Create rule: # V V # -> # V - V #
		rule.getMatchHyphenClasses().clear();
		rule.getMatchHyphenClasses().add(hyphenApproach.getActiveHyphenClasses().get(0));
		rule.getMatchHyphenClasses().add(hyphenApproach.getActiveHyphenClasses().get(0));
		rule.getChangeHyphenClasses().clear();
		rule.getChangeHyphenClasses().add(hyphenApproach.getActiveHyphenClasses().get(0));
		rule.getChangeHyphenClasses().add(hyphenApproach.getInsertHereHC());
		rule.getChangeHyphenClasses().add(hyphenApproach.getActiveHyphenClasses().get(0));
		checkRuleMatch("", false, 0, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("n", true, 3, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("a", true, 3, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("na", true, 4, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("aa", true, 4, rule, new boolean[] { true }, new int[] { 2 });
		checkRuleMatch("aaa", true, 5, rule, new boolean[] { false }, new int[] { -1 });

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
		rule.setWordInitial(true);
		checkRuleMatch("", false, 0, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("n", true, 3, rule, new boolean[] { true }, new int[] { 1 });
		checkRuleMatch("a", true, 3, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("na", true, 4, rule, new boolean[] { true, false }, new int[] { 1, -1 });
		checkRuleMatch("nak", true, 5, rule, new boolean[] { true, false }, new int[] { 1, -1 });
		checkRuleMatch("danko", true, 7, rule, new boolean[] { false, false }, new int[] { -1, -1 });
		rule.setWordInitial(false);
		rule.setWordFinal(true);
		checkRuleMatch("", false, 0, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("n", true, 3, rule, new boolean[] { true }, new int[] { 1 });
		checkRuleMatch("a", true, 3, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("na", true, 4, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("nan", true, 5, rule, new boolean[] { true }, new int[] { 3 });
		rule.setWordInitial(true);
		checkRuleMatch("", false, 0, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("n", true, 3, rule, new boolean[] { true }, new int[] { 1 });
		checkRuleMatch("a", true, 3, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("na", true, 4, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("an", true, 4, rule, new boolean[] { false }, new int[] { -1 });
		checkRuleMatch("nan", true, 5, rule, new boolean[] { false }, new int[] { -1 });
}

	protected void checkRuleMatch(String word, boolean expectedClasserSuccess, int numberOfClasses,
			HyphenChangeRule rule, boolean[] expectedRuleSuccess, int[] expectedClassIndex) {
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		fSuccess = segResult.success;
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
		segmenter.segmentWord(word);
		List<? extends CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		HyphenClasserResult ncResult = hyphenClasser.parseIntoHyphenClasses(segmentsInWord);
		fSuccess = ncResult.success;
		classesInWord = hyphenClasser.getClassesInWord();
		HyphenChangeRuleState hcrs = new HyphenChangeRuleState(rule, classesInWord, -1);
		fSuccess = hyphenRuleProcessor.ruleMatches(hcrs);
		hyphenRuleProcessor.applyRule(hcrs);
		assertEquals(expectedClassRepresentation, HyphenClasser.getClassesRepresentation(hcrs.getClassesInWord()));
	}

	@Test
	public void applyChangeRulesTest() {
		checkProcessorResults("", true, "");
		checkProcessorResults("a", true, "a");
		checkProcessorResults("d", true, "d");
		checkProcessorResults("n", true, "n");
		checkProcessorResults("Chiko", true, "Chi.ko");
		checkProcessorResults("dapbek", true, "da.pbe.k");
		checkProcessorResults("bampidon", true, "ba.m.pi.do.n");
		checkProcessorResults("bovdek", true, "bo.vde.k");
		checkProcessorResults("fuhgt", true, "fu.hgt");
		checkProcessorResults("blofugh", true, "blo.fu.gh");
		checkProcessorResults("bo", true, "bo");

		// insert hyphen before
		hyphenApproach.getHyphenChangeRules().clear();
		rule = new HyphenChangeRule();
		rule.getMatchHyphenClasses().add(hyphenClasses.get(1)); // C
		rule.getMatchHyphenClasses().add(hyphenClasses.get(0)); // V
		rule.getChangeHyphenClasses().add(hyphenApproach.getInsertHereHC());
		rule.getChangeHyphenClasses().add(hyphenClasses.get(1)); // C
		rule.getChangeHyphenClasses().add(hyphenClasses.get(0)); // V
		hyphenApproach.getHyphenChangeRules().add(rule);
		hyphenRuleProcessor = new HyphenChangeRuleProcessor(hyphenApproach);
		checkProcessorResults("", true, "");
		checkProcessorResults("d", true, "d");
		checkProcessorResults("a", true, "a");
		checkProcessorResults("da", true, "da");
		checkProcessorResults("dak", true, "dak");
		checkProcessorResults("dako", true, "da.ko");
		checkProcessorResults("dakot", true, "da.kot");
		checkProcessorResults("dakota", true, "da.ko.ta");

		// insert hyphen after
		hyphenApproach.getHyphenChangeRules().clear();
		rule = new HyphenChangeRule();
		rule.getMatchHyphenClasses().add(hyphenClasses.get(1)); // C
		rule.getMatchHyphenClasses().add(hyphenClasses.get(0)); // V
		rule.getChangeHyphenClasses().add(hyphenClasses.get(1)); // C
		rule.getChangeHyphenClasses().add(hyphenClasses.get(0)); // V
		rule.getChangeHyphenClasses().add(hyphenApproach.getInsertHereHC());
		hyphenApproach.getHyphenChangeRules().add(rule);
		hyphenRuleProcessor = new HyphenChangeRuleProcessor(hyphenApproach);
		checkProcessorResults("", true, "");
		checkProcessorResults("d", true, "d");
		checkProcessorResults("a", true, "a");
		checkProcessorResults("da", true, "da");
		checkProcessorResults("dak", true, "da.k");
		checkProcessorResults("dako", true, "da.ko");
		checkProcessorResults("dakot", true, "da.ko.t");
		checkProcessorResults("dakota", true, "da.ko.ta");

		// test do not match class again: rule VVC -> v-VC followed by VV -> V-V
		hyphenApproach.getHyphenChangeRules().clear();
		// create VVC -> vV-C rule
		rule = new HyphenChangeRule();
		rule.getMatchHyphenClasses().add(hyphenClasses.get(0)); // V
		rule.getMatchHyphenClasses().add(hyphenClasses.get(0)); // V
		rule.getMatchHyphenClasses().add(hyphenClasses.get(1)); // C
		rule.getChangeHyphenClasses().add(hyphenClasses.get(0)); // V
		rule.getChangeHyphenClasses().add(hyphenClasses.get(0)); // V
		rule.getChangeHyphenClasses().add(hyphenApproach.getInsertHereHC());
		rule.getChangeHyphenClasses().add(hyphenClasses.get(1)); // C
		rule.getDoNotMatchClassAgains().set(0,true);
		hyphenApproach.getHyphenChangeRules().add(rule);
		// create VV -> V-V rule
		rule = new HyphenChangeRule();
		rule.getMatchHyphenClasses().add(hyphenClasses.get(0)); // V
		rule.getMatchHyphenClasses().add(hyphenClasses.get(0)); // V
		rule.getChangeHyphenClasses().add(hyphenClasses.get(0)); // V
		rule.getChangeHyphenClasses().add(hyphenApproach.getInsertHereHC());
		rule.getChangeHyphenClasses().add(hyphenClasses.get(0)); // V
		hyphenApproach.getHyphenChangeRules().add(rule);
		hyphenRuleProcessor = new HyphenChangeRuleProcessor(hyphenApproach);
		checkProcessorResults("", true, "");
		checkProcessorResults("d", true, "d");
		checkProcessorResults("a", true, "a");
		checkProcessorResults("aa", true, "a.a");
		checkProcessorResults("aad", true, "aa.d");
		checkProcessorResults("daad", true, "daa.d");

		// test do not match class again: rule VVV -> Vv-V followed by VV -> V-V
		hyphenApproach.getHyphenChangeRules().clear();
		// create VVV -> Vv-V rule
		rule = new HyphenChangeRule();
		rule.getMatchHyphenClasses().add(hyphenClasses.get(0)); // V
		rule.getMatchHyphenClasses().add(hyphenClasses.get(0)); // V
		rule.getMatchHyphenClasses().add(hyphenClasses.get(0)); // V
		rule.getChangeHyphenClasses().add(hyphenClasses.get(0)); // V
		rule.getChangeHyphenClasses().add(hyphenClasses.get(0)); // V
		rule.getChangeHyphenClasses().add(hyphenApproach.getInsertHereHC());
		rule.getChangeHyphenClasses().add(hyphenClasses.get(0)); // V
		rule.getDoNotMatchClassAgains().set(1,true);
		hyphenApproach.getHyphenChangeRules().add(rule);
		// create VV -> V-V rule
		rule = new HyphenChangeRule();
		rule.getMatchHyphenClasses().add(hyphenClasses.get(0)); // V
		rule.getMatchHyphenClasses().add(hyphenClasses.get(0)); // V
		rule.getChangeHyphenClasses().add(hyphenClasses.get(0)); // V
		rule.getChangeHyphenClasses().add(hyphenApproach.getInsertHereHC());
		rule.getChangeHyphenClasses().add(hyphenClasses.get(0)); // V
		hyphenApproach.getHyphenChangeRules().add(rule);
		hyphenRuleProcessor = new HyphenChangeRuleProcessor(hyphenApproach);
		checkProcessorResults("", true, "");
		checkProcessorResults("d", true, "d");
		checkProcessorResults("a", true, "a");
		checkProcessorResults("aa", true, "a.a");
		checkProcessorResults("aaa", true, "aa.a");
		checkProcessorResults("daaa", true, "daa.a");
	}

	protected void checkProcessorResults(String word, boolean success, String expectedHyphenation) {
		segmenter.segmentWord(word);
		List<? extends CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		hyphenClasser.parseIntoHyphenClasses(segmentsInWord);
		classesInWord = hyphenClasser.getClassesInWord();
		HyphenChangeRuleResult crResult = hyphenRuleProcessor.applyChangeRules(classesInWord);
		fSuccess = crResult.success;
		assertEquals("rules processed", success, fSuccess);
		assertEquals("Expected hyphenation", expectedHyphenation, crResult.sHyphenation);

	}
}
