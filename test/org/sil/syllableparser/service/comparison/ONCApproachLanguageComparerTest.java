// Copyright (c) 2016-2019 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.comparison;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;

import javafx.collections.ObservableList;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Environment;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.GraphemeNaturalClass;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;
import org.sil.syllableparser.service.comparison.ONCApproachLanguageComparer;
import org.sil.syllableparser.service.comparison.DifferentEnvironment;
import org.sil.syllableparser.service.comparison.DifferentGrapheme;
import org.sil.syllableparser.service.comparison.DifferentGraphemeNaturalClass;
import org.sil.syllableparser.service.comparison.DifferentSegment;
import org.sil.syllableparser.service.comparison.DifferentWord;
import org.sil.utility.StringUtilities;

/**
 * @author Andy Black
 *
 */
public class ONCApproachLanguageComparerTest {

	LanguageProject languageProject1;
	LanguageProject languageProject2;
	ONCApproach onca1;
	ONCApproach onca2;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		languageProject1 = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject1, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		onca1 = languageProject1.getONCApproach();
		languageProject2 = new LanguageProject();
		xmlBackEndProvider = new XMLBackEndProvider(languageProject2, locale);
		file = new File(Constants.UNIT_TEST_DATA_FILE_2);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		onca2 = languageProject2.getONCApproach();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// make sure the setup is what we expect
	@Test
	public void languagesContentsTest() {
		// Segments
		ObservableList<Segment> segmentInventory;
		segmentInventory = languageProject1.getSegmentInventory();
		assertEquals("Segment inventory size", 27, segmentInventory.size());
		segmentInventory = languageProject2.getSegmentInventory();
		assertEquals("Segment inventory size", 33, segmentInventory.size());
		// natural classes
		List<SHNaturalClass> sonorityHierarchy;
		sonorityHierarchy = onca1.getONCSonorityHierarchy();
		assertEquals("Sonority Hierarchy size", 6, sonorityHierarchy.size());
		sonorityHierarchy = onca2.getONCSonorityHierarchy();
		assertEquals("Sonority Hierarchy size", 5, sonorityHierarchy.size());
		// words
		ObservableList<Word> words;
		words = onca1.getWords();
		assertEquals("ONC words size", 10025, words.size());
		words = onca2.getWords();
		assertEquals("ONC words size", 10025, words.size());
	}

	@Test
	public void compareLanguagesTest() {
		ONCApproachLanguageComparer comparer = new ONCApproachLanguageComparer(onca1, onca2);
		compareSyllabificationParameters(comparer);
		compareSegments(comparer);
		compareGraphemes(comparer);
		compareGraphemeNaturalClasses(comparer);
		compareEnvironments(comparer);
		compareSonorityHierarchy(comparer);
		compareSyllabificationParameters(comparer);
		compareWords(comparer);
	}

	@Test
	public void compareSameLanguagesTest() {
		ONCApproachLanguageComparer comparer = new ONCApproachLanguageComparer(onca1, onca1);
		compareSameSegments(comparer);
		compareSameGraphemes(comparer);
		compareSameGraphemeNaturalClasses(comparer);
		compareSameEnvironments(comparer);
		compareSameNaturalClasses(comparer);
		comparer.compareSonorityHierarchy();
		SortedSet<DifferentSHNaturalClass> diffs = comparer.getNaturalClassesWhichDiffer();
		assertEquals("number of different natural classes", 0, diffs.size());
		comparer.compareSyllabificationParameters();
		assertEquals(false, comparer.isCodasAllowedDifferent());
		assertEquals(false, comparer.isOnsetMaximizationDifferent());
		assertEquals(false, comparer.isOnsetPrincipleDifferent());
		compareSameWords(comparer);
	}

	protected void compareSegments(ApproachLanguageComparer comparer) {
		comparer.compareSegmentInventory();
		SortedSet<DifferentSegment> diffs = comparer.getSegmentsWhichDiffer();
		assertEquals("number of different segments", 25, diffs.size());
		List<DifferentSegment> listOfDiffs = new ArrayList<DifferentSegment>();
		listOfDiffs.addAll(diffs);
		DifferentSegment diffSeg = listOfDiffs.get(1);
		Segment seg1 = ((Segment) diffSeg.getObjectFrom1());
		Segment seg2 = ((Segment) diffSeg.getObjectFrom2());
		assertEquals("second's 1 is an /a/", "a", seg1.getSegment());
		assertEquals("second's 2 is an /a/", "a", seg2.getSegment());
		assertEquals("number of graphemes in segment1", 2, seg1.getGraphs().size());
		assertEquals("number of active graphemes in segment1", 2, seg1.getActiveGraphs().size());
		assertEquals("number of graphemes in segment2", 5, seg2.getGraphs().size());
		assertEquals("number of graphemes in segment2", 4, seg2.getActiveGraphs().size());
		assertEquals("second's 1's graphemes are 'a A'", "a A",
				((Segment) diffSeg.getObjectFrom1()).getGraphemes());
		assertEquals("second's 2's graphemes are 'a A á Á'", "a A á Á",
				((Segment) diffSeg.getObjectFrom2()).getGraphemes());
		assertFalse(seg1.isCoda());
		assertTrue(seg1.isNucleus());
		assertFalse(seg1.isOnset());
		assertFalse(seg2.isCoda());
		assertTrue(seg2.isNucleus());
		assertFalse(seg2.isOnset());
		diffSeg = listOfDiffs.get(0);
		assertNull("first's 1 is null", diffSeg.getObjectFrom1());
		seg2 = ((Segment) diffSeg.getObjectFrom2());
		assertEquals("first's 2 is a /\'/", "'", seg2.getSegment());
		assertEquals("number of graphemes in segment2", 1, seg2.getGraphs().size());
		assertEquals("first's 2's graphemes are '\''", "'",
				((Segment) diffSeg.getObjectFrom2()).getGraphemes());
		diffSeg = listOfDiffs.get(2);
		seg1 = ((Segment) diffSeg.getObjectFrom1());
		seg2 = ((Segment) diffSeg.getObjectFrom2());
		assertEquals("second's 1 is a /b/", "b", seg1.getSegment());
		assertEquals("second's 2 is a /b/", "b", seg2.getSegment());
		assertEquals("number of graphemes in segment1", 2, seg1.getGraphs().size());
		assertEquals("number of active graphemes in segment1", 2, seg1.getActiveGraphs().size());
		assertEquals("number of graphemes in segment2", 2, seg2.getGraphs().size());
		assertEquals("number of graphemes in segment2", 2, seg2.getActiveGraphs().size());
		assertEquals("second's 1's graphemes are 'b B'", "b B",
				((Segment) diffSeg.getObjectFrom1()).getGraphemes());
		assertEquals("second's 2's graphemes are 'b B'", "b B",
				((Segment) diffSeg.getObjectFrom2()).getGraphemes());
		assertFalse(seg1.isCoda());
		assertFalse(seg1.isNucleus());
		assertTrue(seg1.isOnset());
		assertTrue(seg2.isCoda());
		assertFalse(seg2.isNucleus());
		assertTrue(seg2.isOnset());
		diffSeg = listOfDiffs.get(24);
		seg1 = ((Segment) diffSeg.getObjectFrom1());
		assertEquals("eleventh's 1 is a /ɲ/", "ɲ", seg1.getSegment());
		assertNull("eleventh's 2 is null", diffSeg.getObjectFrom2());
		assertEquals("number of graphemes in segment1", 2, seg1.getGraphs().size());
		assertEquals("tenth's 1's graphemes are 'ñ Ñ'", "ñ Ñ",
				((Segment) diffSeg.getObjectFrom1()).getGraphemes());
	}

	protected void compareGraphemes(ApproachLanguageComparer comparer) {
		comparer.compareGraphemes();;
		SortedSet<DifferentGrapheme> diffs = comparer.getGraphemesWhichDiffer();
		assertEquals("number of different graphemes", 33, diffs.size());
		List<DifferentGrapheme> listOfDiffs = new ArrayList<DifferentGrapheme>();
		listOfDiffs.addAll(diffs);
		DifferentGrapheme diffGrapheme = listOfDiffs.get(0);
		Grapheme grapheme1 = ((Grapheme) diffGrapheme.getObjectFrom1());
		Grapheme grapheme2 = ((Grapheme) diffGrapheme.getObjectFrom2());
		assertNull(grapheme1);
		assertEquals("'", grapheme2.getForm());
		assertEquals(0, grapheme2.getEnvs().size());
		diffGrapheme = listOfDiffs.get(1);
		assertNull(diffGrapheme.getObjectFrom1());
		grapheme2 = ((Grapheme) diffGrapheme.getObjectFrom2());
		assertEquals("C", grapheme2.getForm());
		assertEquals(0, grapheme2.getEnvs().size());
		assertEquals("C",
				((Grapheme) diffGrapheme.getObjectFrom2()).getForm());
		diffGrapheme = listOfDiffs.get(3);
		grapheme1 = ((Grapheme) diffGrapheme.getObjectFrom1());
		assertEquals("Ch", grapheme1.getForm());
		assertNull(diffGrapheme.getObjectFrom2());
		assertEquals(0, grapheme1.getEnvs().size());
	}

	protected void compareGraphemeNaturalClasses(ApproachLanguageComparer comparer) {
		comparer.compareGraphemeNaturalClasses();
		SortedSet<DifferentGraphemeNaturalClass> diffs = comparer.getGraphemeNaturalClassesWhichDiffer();
		assertEquals("number of different grapheme natural classes", 3, diffs.size());
		List<DifferentGraphemeNaturalClass> listOfDiffs = new ArrayList<DifferentGraphemeNaturalClass>();
		listOfDiffs.addAll(diffs);
		DifferentGraphemeNaturalClass diffGnc = listOfDiffs.get(1);
		GraphemeNaturalClass gnc1 = ((GraphemeNaturalClass) diffGnc.getObjectFrom1());
		GraphemeNaturalClass gnc2 = ((GraphemeNaturalClass) diffGnc.getObjectFrom2());
		assertEquals("a, e, E, i, I, o, O, U", gnc1.getGNCRepresentation());
		assertNull(gnc2);
	}
	
	protected void compareEnvironments(ApproachLanguageComparer comparer) {
		comparer.compareEnvironments();
		SortedSet<DifferentEnvironment> diffs = comparer.getEnvironmentsWhichDiffer();
		assertEquals(5, diffs.size());
		List<DifferentEnvironment> listOfDiffs = new ArrayList<DifferentEnvironment>();
		listOfDiffs.addAll(diffs);
		DifferentEnvironment diffSeg = listOfDiffs.get(1);
		Environment env1 = ((Environment) diffSeg.getObjectFrom1());
		Environment env2 = ((Environment) diffSeg.getObjectFrom2());
		assertEquals("/  _  i", env1.getEnvironmentRepresentation());
		assertNull(env2);
	}

	protected void compareNaturalClasses(ONCApproachLanguageComparer comparer) {
		comparer.compareSonorityHierarchy();;
		SortedSet<DifferentSHNaturalClass> diffs = comparer.getNaturalClassesWhichDiffer();
		assertEquals("number of different natural classes", 6, diffs.size());
		List<DifferentSHNaturalClass> listOfDiffs = new ArrayList<DifferentSHNaturalClass>();
		listOfDiffs.addAll(diffs);
		DifferentSHNaturalClass diffNaturalClass = listOfDiffs.get(0);
		assertEquals("second's 1 is [C]", "C",
				((SHNaturalClass) diffNaturalClass.getObjectFrom1()).getNCName());
		assertEquals("second's 2 is [C]", "C",
				((SHNaturalClass) diffNaturalClass.getObjectFrom2()).getNCName());
		assertEquals(
				"second's 1's reps are 'b, ch, d, f, g, h, k, p, s, sh, t, v, w, x, y, z, S'",
				"b, ch, d, f, g, h, k, p, s, sh, t, v, w, x, y, z, [S]",
				((SHNaturalClass) diffNaturalClass.getObjectFrom1()).getSegmentsRepresentation());
		assertEquals(
				"second's 2's reps are '', b, c, ch, d, f, g, h, j, k, kw, ky, l, m, n, ñ, p, q, r, s, sh, t, ts, v, w, x, y, z'",
				"', b, c, ch, d, f, g, h, j, k, kw, ky, l, m, n, ñ, p, q, r, s, sh, t, ts, v, w, x, y, z",
				((SHNaturalClass) diffNaturalClass.getObjectFrom2()).getSegmentsRepresentation());
		diffNaturalClass = listOfDiffs.get(1);
		assertNull("third's 1 is null", diffNaturalClass.getObjectFrom1());
		assertEquals("third's 2 is [Gl]", "Gl",
				((SHNaturalClass) diffNaturalClass.getObjectFrom2()).getNCName());
		assertEquals("third's 2's reps are '\', h'", "', h",
				((SHNaturalClass) diffNaturalClass.getObjectFrom2()).getSegmentsRepresentation());
		diffNaturalClass = listOfDiffs.get(2);
		assertEquals("fourth's 1 is [N]", "N",
				((SHNaturalClass) diffNaturalClass.getObjectFrom1()).getNCName());
		assertNull("fourth's 2 is null", diffNaturalClass.getObjectFrom2());
		assertEquals("fourth's 1's reps are 'm, n, ɲ'", "m, n, ɲ",
				((SHNaturalClass) diffNaturalClass.getObjectFrom1()).getSegmentsRepresentation());
	}

	protected void compareSonorityHierarchy(ONCApproachLanguageComparer comparer) {
		comparer.compareSonorityHierarchy();
		SortedSet<DifferentSHNaturalClass> diffs = comparer.getNaturalClassesWhichDiffer();
		assertEquals("number of different natural classes", 2, diffs.size());
		List<DifferentSHNaturalClass> listOfDiffs = new ArrayList<DifferentSHNaturalClass>();
		listOfDiffs.addAll(diffs);
		DifferentSHNaturalClass diffNaturalClass = listOfDiffs.get(0);
		assertEquals("Nasals", ((SHNaturalClass) diffNaturalClass.getObjectFrom1()).getNCName());
		assertEquals("Nasals", ((SHNaturalClass) diffNaturalClass.getObjectFrom2()).getNCName());
		assertEquals("m, n, ɲ", ((SHNaturalClass) diffNaturalClass.getObjectFrom1()).getSegmentsRepresentation());
		assertEquals("m, n", ((SHNaturalClass) diffNaturalClass.getObjectFrom2()).getSegmentsRepresentation());
		diffNaturalClass = listOfDiffs.get(1);
		assertNull(diffNaturalClass.getObjectFrom2());
		assertEquals("Voiced Obstruents", ((SHNaturalClass) diffNaturalClass.getObjectFrom1()).getNCName());
		assertEquals("b, d, g, z", ((SHNaturalClass) diffNaturalClass.getObjectFrom1()).getSegmentsRepresentation());
	}

	protected void compareWords(ApproachLanguageComparer comparer) {
		comparer.compareWords();
		SortedSet<DifferentWord> diffs = comparer.getWordsWhichDiffer();
		assertEquals("number of different words", 3122, diffs.size());
		List<DifferentWord> listOfDiffs = new ArrayList<DifferentWord>();
		listOfDiffs.addAll(diffs);
		DifferentWord diffWord = listOfDiffs.get(0);
		assertEquals("a", ((Word) diffWord.getObjectFrom1()).getWord());
		assertEquals("a", ((Word) diffWord.getObjectFrom2()).getWord());
		assertEquals("", ((Word) diffWord.getObjectFrom1()).getONCPredictedSyllabification());
		assertEquals("a", ((Word) diffWord.getObjectFrom2()).getONCPredictedSyllabification());
		diffWord = listOfDiffs.get(1);
		assertEquals(null, ((Word) diffWord.getObjectFrom1()));
		assertEquals("aaah", ((Word) diffWord.getObjectFrom2()).getWord());
		assertEquals("", ((Word) diffWord.getObjectFrom2()).getONCPredictedSyllabification());
		assertEquals("Failure: could not parse into segments beginning at 'h'.",
				((Word) diffWord.getObjectFrom2()).getONCParserResult());
		diffWord = listOfDiffs.get(2);
		assertEquals("third's 1 is ababrastro", "ababrastro",
				((Word) diffWord.getObjectFrom1()).getWord());
		assertEquals("third's 2 is null", null, ((Word) diffWord.getObjectFrom2()));
		assertEquals("third's 1's parse is ''", "",
				((Word) diffWord.getObjectFrom1()).getONCPredictedSyllabification());
	}

	@Test
	public void compareSyllablePatternOrderTest() {
		// setup
		LanguageProject languageProject3 = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject3, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE_3);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		ONCApproach cva3 = languageProject3.getONCApproach();
		LanguageProject languageProject4 = new LanguageProject();
		xmlBackEndProvider = new XMLBackEndProvider(languageProject4, locale);
		file = new File(Constants.UNIT_TEST_DATA_FILE_4);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		ONCApproach cva4 = languageProject4.getONCApproach();
		ApproachLanguageComparer comparer = new ONCApproachLanguageComparer(cva3, cva4);
		// test (we only use this as a way to tell if the the two orders are the
		// same or not)
//		comparer.compareSyllablePatternOrder();
//		LinkedList<Diff> differences = comparer.getSyllablePatternOrderDifferences();
//		assertEquals(7, differences.size());
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

	protected void compareSameNaturalClasses(ONCApproachLanguageComparer comparer) {
		comparer.compareSonorityHierarchy();;
		SortedSet<DifferentSHNaturalClass> diffs = comparer.getNaturalClassesWhichDiffer();
		assertEquals("number of different natural classes", 0, diffs.size());
	}
	
	protected void compareSyllabificationParameters(ApproachLanguageComparer comparer) {
		comparer.compareSyllabificationParameters();
		assertEquals(true, comparer.isCodasAllowedDifferent());
		assertTrue(comparer.isLangProj1CodasAllowed());
		assertFalse(comparer.isLangProj2CodasAllowed());
		assertEquals(true, comparer.isOnsetMaximizationDifferent());
		assertFalse(comparer.isLangProj1OnsetMaximization());
		assertTrue(comparer.isLangProj2OnsetMaximization());
		assertEquals(true, comparer.isOnsetPrincipleDifferent());
		assertEquals(OnsetPrincipleType.EVERY_SYLLABLE_HAS_ONSET, comparer.getLangProj1OnsetPrinciple());
		assertEquals(OnsetPrincipleType.ONSETS_NOT_REQUIRED, comparer.getLangProj2OnsetPrinciple());
	}

	protected void compareSameWords(ApproachLanguageComparer comparer) {
		comparer.compareWords();
		SortedSet<DifferentWord> diffs = comparer.getWordsWhichDiffer();
		assertEquals("number of different words", 0, diffs.size());
	}
}
