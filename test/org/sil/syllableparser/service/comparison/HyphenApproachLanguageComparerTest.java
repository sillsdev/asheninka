// Copyright (c) 2026 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.comparison;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import javafx.collections.ObservableList;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.hyphenapproach.HyphenApproach;
import org.sil.syllableparser.model.hyphenapproach.HyphenChangeRule;
import org.sil.syllableparser.model.hyphenapproach.HyphenClass;
import org.sil.syllableparser.service.parsing.HyphenTestBase;

/**
 * @author Andy Black
 *
 */
public class HyphenApproachLanguageComparerTest extends HyphenTestBase {

	LanguageProject languageProject1;
	LanguageProject languageProject2;
	HyphenApproach ha1;
	HyphenApproach ha2;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		languageProject1 = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject1, locale);
		File file = new File(Constants.UNIT_TEST_HYPHEN_DATA_FILE_NAME);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		ha1 = languageProject1.getHyphenApproach();
		languageProject2 = new LanguageProject();
		xmlBackEndProvider = new XMLBackEndProvider(languageProject2, locale);
		file = new File(Constants.UNIT_TEST_HYPHEN_DATA_FILE_2_NAME);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		ha2 = languageProject2.getHyphenApproach();
	}

	// make sure the setup is what we expect
	@Test
	public void languagesContentsTest() {
		// Segments
		ObservableList<Segment> segmentInventory;
		segmentInventory = languageProject1.getSegmentInventory();
		assertEquals("Segment inventory size", 27, segmentInventory.size());
		segmentInventory = languageProject2.getSegmentInventory();
		assertEquals("Segment inventory size", 28, segmentInventory.size());
		// natural classes
		ObservableList<HyphenClass> hyphenClasses;
		hyphenClasses = ha1.getHyphenClasses();
		assertEquals("Hyphen Classes size", 3, hyphenClasses.size());
		hyphenClasses = ha2.getHyphenClasses();
		assertEquals("Hyphen Classes size", 4, hyphenClasses.size());
		// syllable patterns
		ObservableList<HyphenChangeRule> changeRules;
		changeRules = ha1.getHyphenChangeRules();
		assertEquals("Change rules size", 3, changeRules.size());
		changeRules = ha2.getHyphenChangeRules();
		assertEquals("Change rules size", 4, changeRules.size());
		// words
		ObservableList<Word> words;
		words = ha1.getWords();
		assertEquals("Hyphen words size", 1716, words.size());
		words = ha2.getWords();
		assertEquals("Hyphen words size", 1716, words.size());
	}

	@Test
	public void compareLanguagesTest() {
		HyphenApproachLanguageComparer comparer = new HyphenApproachLanguageComparer(ha1, ha2);
		compareSegments(comparer);
		compareGraphemes(comparer);
		compareGraphemeNaturalClasses(comparer);
		compareEnvironments(comparer);
		compareHyphenClasses(comparer);
		compareHyphenChangeRules(comparer);
		compareWords(comparer);
	}

	@Test
	public void compareSameLanguagesTest() {
		HyphenApproachLanguageComparer comparer = new HyphenApproachLanguageComparer(ha1, ha1);
		compareSameSegments(comparer);
		compareSameGraphemes(comparer);
		compareSameGraphemeNaturalClasses(comparer);
		compareSameEnvironments(comparer);
		compareSameHyphenClasses(comparer);
		compareSameHyphenChangeRules(comparer);
		compareSameWords(comparer);
	}

	protected void compareSegments(ApproachLanguageComparer comparer) {
		comparer.compareSegmentInventory();
		SortedSet<DifferentSegment> diffs = comparer.getSegmentsWhichDiffer();
		assertEquals("number of different segments", 1, diffs.size());
		List<DifferentSegment> listOfDiffs = new ArrayList<DifferentSegment>();
		listOfDiffs.addAll(diffs);
		DifferentSegment diffSeg = listOfDiffs.get(0);
		Segment seg2 = ((Segment) diffSeg.getObjectFrom2());
		assertNull("first's 1 is null", diffSeg.getObjectFrom1());
		seg2 = ((Segment) diffSeg.getObjectFrom2());
		assertEquals("second's 1 is /ts/", "ts", seg2.getSegment());
		assertEquals("number of graphemes in segment2", 3, seg2.getGraphs().size());
		assertEquals("second's graphemes are 'ts Ts TS", "ts Ts TS",
				((Segment) diffSeg.getObjectFrom2()).getGraphemes());
	}

	protected void compareGraphemes(ApproachLanguageComparer comparer) {
		comparer.compareGraphemes();;
		SortedSet<DifferentGrapheme> diffs = comparer.getGraphemesWhichDiffer();
		assertEquals("number of different graphemes", 3, diffs.size());
		List<DifferentGrapheme> listOfDiffs = new ArrayList<DifferentGrapheme>();
		listOfDiffs.addAll(diffs);
		DifferentGrapheme diffGrapheme = listOfDiffs.get(0);
		Grapheme grapheme1 = ((Grapheme) diffGrapheme.getObjectFrom1());
		Grapheme grapheme2 = ((Grapheme) diffGrapheme.getObjectFrom2());
		assertNull(grapheme1);
		assertEquals("ts", grapheme2.getForm());
		assertEquals(0, grapheme2.getEnvs().size());
		diffGrapheme = listOfDiffs.get(1);
		assertNull(diffGrapheme.getObjectFrom1());
		grapheme2 = ((Grapheme) diffGrapheme.getObjectFrom2());
		assertEquals("Ts", grapheme2.getForm());
		assertEquals(0, grapheme2.getEnvs().size());
		assertEquals("Ts",
				((Grapheme) diffGrapheme.getObjectFrom2()).getForm());
		diffGrapheme = listOfDiffs.get(2);
		grapheme1 = ((Grapheme) diffGrapheme.getObjectFrom1());
		assertNull(diffGrapheme.getObjectFrom1());
		assertEquals("Ts", grapheme2.getForm());
		assertEquals(0, grapheme2.getEnvs().size());
	}

	protected void compareGraphemeNaturalClasses(ApproachLanguageComparer comparer) {
		comparer.compareGraphemeNaturalClasses();
		SortedSet<DifferentGraphemeNaturalClass> diffs = comparer.getGraphemeNaturalClassesWhichDiffer();
		assertEquals("number of different grapheme natural classes", 0, diffs.size());
	}
	
	protected void compareEnvironments(ApproachLanguageComparer comparer) {
		comparer.compareEnvironments();
		SortedSet<DifferentEnvironment> diffs = comparer.getEnvironmentsWhichDiffer();
		assertEquals(0, diffs.size());
	}

	protected void compareHyphenClasses(HyphenApproachLanguageComparer comparer) {
		SortedSet<DifferentHyphenClass> diffs = new TreeSet<>(
				Comparator.comparing(DifferentHyphenClass::getSortingValue));
		comparer.compareHyphenClasses(ha1.getActiveHyphenClasses(), ha2.getActiveHyphenClasses(), diffs);
		assertEquals("number of different hyphen classes", 2, diffs.size());
		List<DifferentHyphenClass> listOfDiffs = new ArrayList<DifferentHyphenClass>();
		listOfDiffs.addAll(diffs);
		DifferentHyphenClass diffHyphenClass = listOfDiffs.get(0);
		assertNull("first's 1 is null",	diffHyphenClass.getObjectFrom1());
		assertEquals("first's 2 is [Af]", "Af",
				((HyphenClass) diffHyphenClass.getObjectFrom2()).getClassName());
		assertEquals("first's 2's reps are ch, ts", "ch, ts",
				((HyphenClass) diffHyphenClass.getObjectFrom2()).getSegmentsRepresentation());
		diffHyphenClass = listOfDiffs.get(1);
		assertEquals("second's 1 is [C]", "C",
				((HyphenClass) diffHyphenClass.getObjectFrom1()).getClassName());
		assertEquals("second's 2 is [C]", "C",
				((HyphenClass) diffHyphenClass.getObjectFrom2()).getClassName());
		assertEquals("second's 1's reps are 'b, d, f, g, h, k, l, p, q, r, s, t, v, w, x, y, z, ch'",
				"b, d, f, g, h, k, l, p, q, r, s, t, v, w, x, y, z, ch",
				((HyphenClass) diffHyphenClass.getObjectFrom1()).getSegmentsRepresentation());
		assertEquals("second's 2's reps are 'b, d, f, g, h, k, l, p, q, r, s, t, v, w, x, y, z'",
				"b, d, f, g, h, k, l, p, q, r, s, t, v, w, x, y, z",
				((HyphenClass) diffHyphenClass.getObjectFrom2()).getSegmentsRepresentation());
	}

	protected void compareHyphenChangeRules(HyphenApproachLanguageComparer comparer) {
		comparer.compareHyphenChangeRules();
		SortedSet<DifferentHyphenChangeRule> diffs = comparer.getHyphenChangeRulesWhichDiffer();
		assertEquals("number of different change rules", 1, diffs.size());
		List<DifferentHyphenChangeRule> listOfDiffs = new ArrayList<DifferentHyphenChangeRule>();
		listOfDiffs.addAll(diffs);
		DifferentHyphenChangeRule diffChangeRule = listOfDiffs.get(0);
		assertNull("first's 1 is null", diffChangeRule.getObjectFrom1());
		assertEquals("first's 2 is '#-V'", "#-V",
				((HyphenChangeRule) diffChangeRule.getObjectFrom2()).getRuleName());
	}

	protected void compareWords(ApproachLanguageComparer comparer) {
		comparer.compareWords();
		SortedSet<DifferentWord> diffs = comparer.getWordsWhichDiffer();
		assertEquals("number of different words", 862, diffs.size());
		List<DifferentWord> listOfDiffs = new ArrayList<DifferentWord>();
		listOfDiffs.addAll(diffs);
		DifferentWord diffWord = listOfDiffs.get(0);
		assertEquals("0's 1 is aaka", "aaka",
				((Word) diffWord.getObjectFrom1()).getWord());
		assertEquals("0's 2 is aaka", "aaka",
				((Word) diffWord.getObjectFrom2()).getWord());
		assertEquals("0's 1's parse is 'aa.ka'", "aa.ka",
				((Word) diffWord.getObjectFrom1()).getHyphenPredictedSyllabification());
		assertEquals("0's 2's parse is '.aa.ka'", ".aa.ka",
				((Word) diffWord.getObjectFrom2()).getHyphenPredictedSyllabification());
		diffWord = listOfDiffs.get(1);
		assertEquals("second's 1 is aakameethatawakagaeya", "aakameethatawakagaeya", ((Word) diffWord.getObjectFrom1()).getWord());
		assertEquals("second's 2 is aakameethatawakagaeya", "aakameethatawakagaeya", ((Word) diffWord.getObjectFrom2()).getWord());
		assertEquals("second's 1's parse is 'aa.ka.mee.tha.ta.wa.ka.gae.ya'", "aa.ka.mee.tha.ta.wa.ka.gae.ya",
				((Word) diffWord.getObjectFrom1()).getHyphenPredictedSyllabification());
		assertEquals("second's 2's parse is '.aa.ka.mee.tha.ta.wa.ka.gae.ya'", ".aa.ka.mee.tha.ta.wa.ka.gae.ya",
				((Word) diffWord.getObjectFrom2()).getHyphenPredictedSyllabification());
		diffWord = listOfDiffs.get(2);
		assertEquals("third's 1 is aakiro", "aakiro",
				((Word) diffWord.getObjectFrom1()).getWord());
		assertEquals("third's 2 is aakiro", "aakiro",
				((Word) diffWord.getObjectFrom2()).getWord());
		assertEquals("third's 1's parse is 'aa.ki.ro", "aa.ki.ro",
				((Word) diffWord.getObjectFrom1()).getHyphenPredictedSyllabification());
		assertEquals("third's 2's parse is '.aa.ki.ro", ".aa.ki.ro",
				((Word) diffWord.getObjectFrom2()).getHyphenPredictedSyllabification());
	}

	@Test
	public void compareHyphenChangeRuleOrderTest() {
		// setup
		LanguageProject languageProject3 = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject3, locale);
		File file = new File(Constants.UNIT_TEST_HYPHEN_DATA_FILE_NAME);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		HyphenApproach cva3 = languageProject3.getHyphenApproach();
		LanguageProject languageProject4 = new LanguageProject();
		xmlBackEndProvider = new XMLBackEndProvider(languageProject4, locale);
		file = new File(Constants.UNIT_TEST_HYPHEN_DATA_FILE_2_NAME);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		HyphenApproach cva4 = languageProject4.getHyphenApproach();
		HyphenApproachLanguageComparer comparer = new HyphenApproachLanguageComparer(cva3, cva4);
		// test (we only use this as a way to tell if the the two orders are the
		// same or not)
		comparer.compareHyphenChangeRuleOrder();
		LinkedList<Diff> differences = comparer.getHyphenChangeRuleOrderDifferences();
		assertEquals(2, differences.size());
	}

	protected void compareSameSegments(ApproachLanguageComparer comparer) {
		comparer.compareSegmentInventory();
		SortedSet<DifferentSegment> diffs = comparer.getSegmentsWhichDiffer();
		assertEquals("number of different segments", 0, diffs.size());
	}

	protected void compareSameGraphemes(ApproachLanguageComparer comparer) {
		comparer.compareGraphemes();;
		SortedSet<DifferentGrapheme> diffs = comparer.getGraphemesWhichDiffer();
		assertEquals("number of different graphemes", 0, diffs.size());
	}

	protected void compareSameGraphemeNaturalClasses(ApproachLanguageComparer comparer) {
		comparer.compareGraphemeNaturalClasses();;
		SortedSet<DifferentGraphemeNaturalClass> diffs = comparer.getGraphemeNaturalClassesWhichDiffer();
		assertEquals("number of different grapheme natural classes", 0, diffs.size());
	}

	protected void compareSameEnvironments(ApproachLanguageComparer comparer) {
		comparer.compareEnvironments();;
		SortedSet<DifferentEnvironment> diffs = comparer.getEnvironmentsWhichDiffer();
		assertEquals("number of different environments", 0, diffs.size());
	}

	protected void compareSameHyphenClasses(HyphenApproachLanguageComparer comparer) {
		SortedSet<DifferentHyphenClass> diffs = new TreeSet<>(
				Comparator.comparing(DifferentHyphenClass::getSortingValue));
		comparer.compareHyphenClasses(ha1.getHyphenClasses(), ha1.getHyphenClasses(), diffs);
		assertEquals("number of different hyphen classes", 0, diffs.size());
	}

	protected void compareSameHyphenChangeRules(HyphenApproachLanguageComparer comparer) {
		comparer.compareHyphenChangeRules();;
		SortedSet<DifferentHyphenChangeRule> diffs = comparer.getHyphenChangeRulesWhichDiffer();
		assertEquals("number of different change rules", 0, diffs.size());
	}

	protected void compareSameWords(ApproachLanguageComparer comparer) {
		comparer.compareWords();
		SortedSet<DifferentWord> diffs = comparer.getWordsWhichDiffer();
		assertEquals("number of different words", 0, diffs.size());
	}

	@Test
	public void compareSameHyphenChangeRuleOrderTest() {
		// setup
		HyphenApproachLanguageComparer comparer = new HyphenApproachLanguageComparer(ha2, ha2);
		// test (we only use this as a way to tell if the the two orders are the
		// same or not)
		comparer.compareHyphenChangeRuleOrder();
		LinkedList<Diff> differences = comparer.getHyphenChangeRuleOrderDifferences();
		assertEquals(1, differences.size());
	}
}
