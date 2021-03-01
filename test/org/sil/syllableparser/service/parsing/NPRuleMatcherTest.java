/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.parsing;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.npapproach.NPApproach;
import org.sil.syllableparser.model.npapproach.NPNodeInSyllable;
import org.sil.syllableparser.model.npapproach.NPRule;
import org.sil.syllableparser.model.npapproach.NPRuleAction;
import org.sil.syllableparser.model.npapproach.NPRuleLevel;
import org.sil.syllableparser.model.npapproach.NPSegmentInSyllable;
import org.sil.syllableparser.service.NPRuleMatcher;

/**
 * @author Andy Black
 *
 */
public class NPRuleMatcherTest {

	NPSegmenter segmenter;
	NPRuleMatcher matcher;
	NPRule rule;
	List<NPRule> rules;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		LanguageProject languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		List<Grapheme> activeGraphemes = languageProject.getActiveGraphemes();
		segmenter = new NPSegmenter(activeGraphemes,
				languageProject.getActiveGraphemeNaturalClasses());
		NPApproach npApproach = languageProject.getNPApproach();
		rules = npApproach.getValidActiveNPRules();
		matcher = NPRuleMatcher.getInstance();
		matcher.setLanguageProject(languageProject);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void nucleusBuildRuleMatcherTest() {
		Optional<NPRule> buildRule = rules.stream()
				.filter(r -> r.getID().equals("b558925e-ea0e-4bea-a9b2-903a08503bb9")).findFirst();
		assertTrue(buildRule.isPresent());
		rule = buildRule.get();
		checkMatch("", 0, 0, new int[1], -1);
		checkMatch("t", 1, 0, new int[1], -1);
		checkMatch("tly", 3, 0, new int[1], -1);
		checkMatch("a", 1, 1, new int[] {0}, -1);
		checkMatch("ta", 2, 1, new int[] {1}, -1);
		checkMatch("tla", 3, 1, new int[] {2}, -1);
		checkMatch("tlab", 4, 1, new int[] {2}, -1);
		checkMatch("tlabi", 5, 2, new int[] {2, 4}, -1);
		checkMatch("tlabit", 6, 2, new int[] {2, 4}, -1);
	}

	@Test
	public void attachOnsetRuleMatcherTest() {
		Optional<NPRule> attachOnsetRule = rules.stream()
				.filter(r -> r.getID().equals("c1a2c50d-870d-4b4c-90f2-e813704f5f47")).findFirst();
		assertTrue(attachOnsetRule.isPresent());
		rule = attachOnsetRule.get();
		checkMatch("", 0, 0, new int[1], -1);
		checkMatch("t", 1, 0, new int[1], -1);
		checkMatch("tly", 3, 0, new int[1], -1);
		checkMatch("a", 1, 0, new int[1], -1);
		checkMatch("ta", 2, 1, new int[] {0}, -1);
		checkMatch("tla", 3, 1, new int[] {1}, -1);
		checkMatch("tlab", 4, 1, new int[] {1}, -1);
		checkMatch("tlabi", 5, 2, new int[] {1, 3}, -1);
		checkMatch("tlabit", 6, 2, new int[] {1, 3}, -1);
		checkMatch("tlablit", 7, 2, new int[] {1, 4}, -1);
		checkMatch("tla", 3, 0, new int[1], 1);
	}

	@Test
	public void attachCodaRuleMatcherTest() {
		Optional<NPRule> attachCodaRule = rules.stream()
				.filter(r -> r.getID().equals("7d8c3b88-7d72-40ac-a8f2-0f1df56aeef1")).findFirst();
		assertTrue(attachCodaRule.isPresent());
		rule = attachCodaRule.get();
		checkMatch("", 0, 0, new int[1], -1);
		checkMatch("t", 1, 0, new int[1], -1);
		checkMatch("tly", 3, 0, new int[1], -1);
		checkMatch("a", 1, 0, new int[1], -1);
		checkMatch("ta", 2, 0, new int[1], -1);
		checkMatch("tla", 3, 0, new int[1], -1);
		checkMatch("tlam", 4, 1, new int[] {3}, -1);
		checkMatch("tlami", 5, 1, new int[] {3}, -1);
		checkMatch("tlamin", 6, 2, new int[] {3, 5}, -1);
		checkMatch("tlamlin", 7, 2, new int[] {3, 6}, -1);
		checkMatch("tlam", 4, 0, new int[1], 3);

		matcher.setCodasAllowed(false);
		checkMatch("", 0, 0, new int[1], -1);
		checkMatch("t", 1, 0, new int[1], -1);
		checkMatch("tly", 3, 0, new int[1], -1);
		checkMatch("a", 1, 0, new int[1], -1);
		checkMatch("ta", 2, 0, new int[1], -1);
		checkMatch("tla", 3, 0, new int[1], -1);
		checkMatch("tlam", 4, 0, new int[1], -1);
		checkMatch("tlami", 5, 0, new int[1], -1);
		checkMatch("tlamin", 6, 0, new int[1], -1);
		checkMatch("tlamlin", 7, 0, new int[1], -1);
		}

	@Test
	public void augmentOnsetRuleMatcherTest() {
		Optional<NPRule> augmentOnsetRule = rules.stream()
				.filter(r -> r.getID().equals("9ab72b84-1720-4f99-8e8b-17afa6cb2cda")).findFirst();
		assertTrue(augmentOnsetRule.isPresent());
		rule = augmentOnsetRule.get();
		checkMatch("", 0, 0, new int[1], -1);
		checkMatch("t", 1, 0, new int[1], -1);
		checkMatch("tly", 3, 2, new int[] {0, 1}, -1);
		checkMatch("a", 1, 0, new int[1], -1);
		checkMatch("ta", 2, 0, new int[1], -1);
		checkMatch("tla", 3, 1, new int[] {0}, -1);
		checkMatch("tlab", 4, 1, new int[] {0}, -1);
		checkMatch("tlabi", 5, 1, new int[] {0}, -1);
		checkMatch("tlabit", 6, 1, new int[] {0}, -1);
		checkMatch("tlablit", 7, 2, new int[] {0, 3}, -1);
		checkMatch("tla", 3, 0, new int[1], 0);
	}

	@Test
	public void augmentCodaRuleMatcherTest() {
		Optional<NPRule> augmentCodaRule = rules.stream()
				.filter(r -> r.getID().equals("b70cc8c6-13e8-4dc9-87ef-3a2f9cdcd7eb")).findFirst();
		assertTrue(augmentCodaRule.isPresent());
		rule = augmentCodaRule.get();
		checkMatch("", 0, 0, new int[1], -1);
		checkMatch("t", 1, 0, new int[1], -1);
		checkMatch("tly", 3, 2, new int[] {1, 2}, -1);
		checkMatch("a", 1, 0, new int[1], -1);
		checkMatch("ta", 2, 0, new int[1], -1);
		checkMatch("tla", 3, 1, new int[] {1}, -1);
		checkMatch("tlab", 4, 1, new int[] {1}, -1);
		checkMatch("tlabi", 5, 1, new int[] {1}, -1);
		checkMatch("tlabit", 6, 1, new int[] {1}, -1);
		checkMatch("tlablit", 7, 2, new int[] {1, 4}, -1);
		checkMatch("tlabl", 5, 1, new int[] {1}, 4);

		matcher.setCodasAllowed(false);
		checkMatch("", 0, 0, new int[1], -1);
		checkMatch("t", 1, 0, new int[1], -1);
		checkMatch("tly", 3, 0, new int[1], -1);
		checkMatch("a", 1, 0, new int[1], -1);
		checkMatch("ta", 2, 0, new int[1], -1);
		checkMatch("tla", 3, 0, new int[1], -1);
		checkMatch("tlab", 4, 0, new int[1], -1);
		checkMatch("tlabi", 5, 0, new int[1], -1);
		checkMatch("tlabit", 6, 0, new int[1], -1);
		checkMatch("tlablit", 7, 0, new int[1], -1);
		}

	@Test
	public void leftAdjoinRuleMatcherTest() {
		Optional<NPRule> leftAdjoinRule = rules.stream()
				.filter(r -> r.getID().equals("9ab72b84-1720-4f99-8e8b-17afa6cb2cda")).findFirst();
		assertTrue(leftAdjoinRule.isPresent());
		rule = leftAdjoinRule.get();
		rule.setRuleAction(NPRuleAction.LEFT_ADJOIN);
		rule.setRuleLevel(NPRuleLevel.N);
		checkMatch("", 0, 0, new int[1], -1);
		checkMatch("t", 1, 0, new int[1], -1);
		checkMatch("tly", 3, 2, new int[] {0, 1}, -1);
		checkMatch("a", 1, 0, new int[1], -1);
		checkMatch("ta", 2, 0, new int[1], -1);
		checkMatch("tla", 3, 1, new int[] {0}, -1);
		checkMatch("tlab", 4, 1, new int[] {0}, -1);
		checkMatch("tlabi", 5, 1, new int[] {0}, -1);
		checkMatch("tlabit", 6, 1, new int[] {0}, -1);
		checkMatch("tlablit", 7, 2, new int[] {0, 3}, -1);
		rule.setRuleLevel(NPRuleLevel.N_BAR);
		checkMatch("", 0, 0, new int[1], -1);
		checkMatch("t", 1, 0, new int[1], -1);
		checkMatch("tly", 3, 2, new int[] {0, 1}, -1);
		checkMatch("a", 1, 0, new int[1], -1);
		checkMatch("ta", 2, 0, new int[1], -1);
		checkMatch("tla", 3, 1, new int[] {0}, -1);
		checkMatch("tlab", 4, 1, new int[] {0}, -1);
		checkMatch("tlabi", 5, 1, new int[] {0}, -1);
		checkMatch("tlabit", 6, 1, new int[] {0}, -1);
		checkMatch("tlablit", 7, 2, new int[] {0, 3}, -1);
		rule.setRuleLevel(NPRuleLevel.N_DOUBLE_BAR);
		checkMatch("", 0, 0, new int[1], -1);
		checkMatch("t", 1, 0, new int[1], -1);
		checkMatch("tly", 3, 2, new int[] {0, 1}, -1);
		checkMatch("a", 1, 0, new int[1], -1);
		checkMatch("ta", 2, 0, new int[1], -1);
		checkMatch("tla", 3, 1, new int[] {0}, -1);
		checkMatch("tlab", 4, 1, new int[] {0}, -1);
		checkMatch("tlabi", 5, 1, new int[] {0}, -1);
		checkMatch("tlabit", 6, 1, new int[] {0}, -1);
		checkMatch("tlablit", 7, 2, new int[] {0, 3}, -1);
	}

	@Test
	public void rightAdjoinRuleMatcherTest() {
		Optional<NPRule> rightAdjoinRule = rules.stream()
				.filter(r -> r.getID().equals("9ab72b84-1720-4f99-8e8b-17afa6cb2cda")).findFirst();
		assertTrue(rightAdjoinRule.isPresent());
		rule = rightAdjoinRule.get();
		rule.setRuleAction(NPRuleAction.RIGHT_ADJOIN);
		rule.setRuleLevel(NPRuleLevel.N);
		checkMatch("", 0, 0, new int[1], -1);
		checkMatch("t", 1, 0, new int[1], -1);
		checkMatch("tly", 3, 2, new int[] {1, 2}, -1);
		checkMatch("a", 1, 0, new int[1], -1);
		checkMatch("ta", 2, 0, new int[1], -1);
		checkMatch("tla", 3, 1, new int[] {1}, -1);
		checkMatch("tlab", 4, 1, new int[] {1}, -1);
		checkMatch("tlabi", 5, 1, new int[] {1}, -1);
		checkMatch("tlabit", 6, 1, new int[] {1}, -1);
		checkMatch("tlablit", 7, 2, new int[] {1, 4}, -1);
		checkMatch("tlablits", 8, 3, new int[] {1, 4, 7}, -1);
		checkMatch("tlablitst", 9, 4, new int[] {1, 4, 7, 8}, -1);
		rule.setRuleLevel(NPRuleLevel.N_BAR);
		checkMatch("", 0, 0, new int[1], -1);
		checkMatch("t", 1, 0, new int[1], -1);
		checkMatch("tly", 3, 2, new int[] {1, 2}, -1);
		checkMatch("a", 1, 0, new int[1], -1);
		checkMatch("ta", 2, 0, new int[1], -1);
		checkMatch("tla", 3, 1, new int[] {1}, -1);
		checkMatch("tlab", 4, 1, new int[] {1}, -1);
		checkMatch("tlabi", 5, 1, new int[] {1}, -1);
		checkMatch("tlabit", 6, 1, new int[] {1}, -1);
		checkMatch("tlablit", 7, 2, new int[] {1, 4}, -1);
		checkMatch("tlablits", 8, 3, new int[] {1, 4, 7}, -1);
		checkMatch("tlablitst", 9, 4, new int[] {1, 4, 7, 8}, -1);
		rule.setRuleLevel(NPRuleLevel.N_DOUBLE_BAR);
		checkMatch("", 0, 0, new int[1], -1);
		checkMatch("t", 1, 0, new int[1], -1);
		checkMatch("tly", 3, 2, new int[] {1, 2}, -1);
		checkMatch("a", 1, 0, new int[1], -1);
		checkMatch("ta", 2, 0, new int[1], -1);
		checkMatch("tla", 3, 1, new int[] {1}, -1);
		checkMatch("tlab", 4, 1, new int[] {1}, -1);
		checkMatch("tlabi", 5, 1, new int[] {1}, -1);
		checkMatch("tlabit", 6, 1, new int[] {1}, -1);
		checkMatch("tlablit", 7, 2, new int[] {1, 4}, -1);
		checkMatch("tlablits", 8, 3, new int[] {1, 4, 7}, -1);
		checkMatch("tlablitst", 9, 4, new int[] {1, 4, 7, 8}, -1);
	}

	private void checkMatch(String word, int segCount, int matchCount, int[] expectedMatchIndexes, int segWithNode) {
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		boolean fSuccess = segResult.success;
		assertTrue(fSuccess);
		List<NPSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		assertEquals(segCount, segmentsInWord.size());
		if (segWithNode >= 0) {
			segmentsInWord.get(segWithNode).setNode(new NPNodeInSyllable());
		}
		List<Integer> matches = matcher.match(rule, segmentsInWord);
		assertEquals(matchCount, matches.size());
		for (int i=0; i < matches.size(); i++) {
			assertEquals(expectedMatchIndexes[i], (int)matches.get(i));
		}
	}
}
