// Copyright (c) 2019 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.comparison;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;

import javafx.collections.ObservableList;
import name.fraser.neil.plaintext.diff_match_patch;
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
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;
import org.sil.syllableparser.service.comparison.DifferentEnvironment;
import org.sil.syllableparser.service.comparison.DifferentGrapheme;
import org.sil.syllableparser.service.comparison.DifferentGraphemeNaturalClass;
import org.sil.syllableparser.service.comparison.DifferentSHNaturalClass;
import org.sil.syllableparser.service.comparison.DifferentSegment;
import org.sil.syllableparser.service.comparison.DifferentWord;
import org.sil.syllableparser.service.comparison.SHApproachLanguageComparer;

/**
 * @author Andy Black
 *
 */
public class SHApproachLanguageComparerTest {

	LanguageProject languageProject1;
	LanguageProject languageProject2;
	SHApproach sha1;
	SHApproach sha2;

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
		sha1 = languageProject1.getSHApproach();
		languageProject2 = new LanguageProject();
		xmlBackEndProvider = new XMLBackEndProvider(languageProject2, locale);
		file = new File(Constants.UNIT_TEST_DATA_FILE_2);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		sha2 = languageProject2.getSHApproach();
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
		ObservableList<SHNaturalClass> sonorityHierarchy;
		sonorityHierarchy = sha1.getSHSonorityHierarchy();
		assertEquals("Sonority Hierarchy size", 6, sonorityHierarchy.size());
		sonorityHierarchy = sha2.getSHSonorityHierarchy();
		assertEquals("Sonority Hierarchy size", 5, sonorityHierarchy.size());
		// words
		ObservableList<Word> words;
		words = sha1.getWords();
		assertEquals("SH words size", 10025, words.size());
		words = sha2.getWords();
		assertEquals("SH words size", 10025, words.size());
	}

	@Test
	public void compareLanguagesTest() {
		SHApproachLanguageComparer comparer = new SHApproachLanguageComparer(sha1, sha2);
		compareSegments(comparer);
		compareGraphemes(comparer);
		compareGraphemeNaturalClasses(comparer);
		compareEnvironments(comparer);
		compareSonorityHierarchy(comparer);
		compareWords(comparer);
	}

	@Test
	public void compareSameLanguagesTest() {
		SHApproachLanguageComparer comparer = new SHApproachLanguageComparer(sha1, sha1);
		compareSameSegments(comparer);
		compareSameGraphemes(comparer);
		compareSameGraphemeNaturalClasses(comparer);
		compareSameEnvironments(comparer);
		compareSameNaturalClasses(comparer);
		compareSameWords(comparer);
	}

	protected void compareSegments(SHApproachLanguageComparer comparer) {
		comparer.compareSegmentInventory();
		SortedSet<DifferentSegment> diffs = comparer.getSegmentsWhichDiffer();
		assertEquals("number of different segments", 18, diffs.size());
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
		diffSeg = listOfDiffs.get(0);
		assertNull("first's 1 is null", diffSeg.getObjectFrom1());
		seg2 = ((Segment) diffSeg.getObjectFrom2());
		assertEquals("first's 2 is a /\'/", "'", seg2.getSegment());
		assertEquals("number of graphemes in segment2", 1, seg2.getGraphs().size());
		assertEquals("first's 2's graphemes are '\''", "'",
				((Segment) diffSeg.getObjectFrom2()).getGraphemes());
		diffSeg = listOfDiffs.get(17);
		seg1 = ((Segment) diffSeg.getObjectFrom1());
		assertEquals("eleventh's 1 is a /ɲ/", "ɲ", seg1.getSegment());
		assertNull("eleventh's 2 is null", diffSeg.getObjectFrom2());
		assertEquals("number of graphemes in segment1", 2, seg1.getGraphs().size());
		assertEquals("tenth's 1's graphemes are 'ñ Ñ'", "ñ Ñ",
				((Segment) diffSeg.getObjectFrom1()).getGraphemes());
	}

	protected void compareGraphemes(SHApproachLanguageComparer comparer) {
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

	protected void compareGraphemeNaturalClasses(SHApproachLanguageComparer comparer) {
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
	
	protected void compareEnvironments(SHApproachLanguageComparer comparer) {
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

	protected void compareSonorityHierarchy(SHApproachLanguageComparer comparer) {
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

	protected void compareWords(SHApproachLanguageComparer comparer) {
		comparer.compareWords();
		SortedSet<DifferentWord> diffs = comparer.getWordsWhichDiffer();
		assertEquals("number of different words", 4611, diffs.size());
		List<DifferentWord> listOfDiffs = new ArrayList<DifferentWord>();
		listOfDiffs.addAll(diffs);
		DifferentWord diffWord = listOfDiffs.get(2026);
		assertEquals("2026's 1 is motankwakwetsak", "motankwakwetsak",
				((Word) diffWord.getObjectFrom1()).getWord());
		assertEquals("2026's 2 is motankwakwetsak", "motankwakwetsak",
				((Word) diffWord.getObjectFrom2()).getWord());
		assertEquals("2026's 1's parse is ''", "",
				((Word) diffWord.getObjectFrom1()).getCVPredictedSyllabification());
		assertEquals("2026's 2's parse is 'mo.tan.kwa.kwe.tsak'", "mo.tan.kwa.kwe.tsak",
				((Word) diffWord.getObjectFrom2()).getCVPredictedSyllabification());
		diffWord = listOfDiffs.get(0);
		assertEquals("first's 1 is null", null, ((Word) diffWord.getObjectFrom1()));
		assertEquals("first's 2 is aaah", "aaah", ((Word) diffWord.getObjectFrom2()).getWord());
		assertEquals("first's 2's parse is 'aaah'", "a.a.ah",
				((Word) diffWord.getObjectFrom2()).getCVPredictedSyllabification());
		diffWord = listOfDiffs.get(1);
		assertEquals("second's 1 is ababrastro", "ababrastro",
				((Word) diffWord.getObjectFrom1()).getWord());
		assertEquals("second's 2 is null", null, ((Word) diffWord.getObjectFrom2()));
		assertEquals("second's 1's parse is ''", "",
				((Word) diffWord.getObjectFrom1()).getCVPredictedSyllabification());
	}

	@Test
	public void compareSonorityHierarchyOrderTest() {
		SHApproachLanguageComparer comparer = new SHApproachLanguageComparer(sha1, sha2);
		// test (we only use this as a way to tell if the the orders are the
		// same or not)
		comparer.compareSonorityHierarchyOrder();
		LinkedList<Diff> differences = comparer.getSonorityHierarchyOrderDifferences();
		assertEquals(9, differences.size());
		Diff diff = differences.get(0);
		assertEquals(diff_match_patch.Operation.EQUAL, diff.operation);
		assertEquals("Vowels\ta, e, i, o, u\nGlides\tw, y\n", diff.text);
		diff = differences.get(1);
		assertEquals(diff_match_patch.Operation.DELETE, diff.operation);
		assertEquals("Liquids\tl, r\n", diff.text);
		diff = differences.get(2);
		assertEquals(diff_match_patch.Operation.EQUAL, diff.operation);
		assertEquals("Nasals\tm, n", diff.text);
		diff = differences.get(3);
		assertEquals(diff_match_patch.Operation.INSERT, diff.operation);
		assertEquals("\nLiquids\tl", diff.text);
		diff = differences.get(4);
		assertEquals(diff_match_patch.Operation.EQUAL, diff.operation);
		assertEquals(", ", diff.text);
		diff = differences.get(5);
		assertEquals(diff_match_patch.Operation.DELETE, diff.operation);
		assertEquals("ɲ", diff.text);
		diff = differences.get(6);
		assertEquals(diff_match_patch.Operation.INSERT, diff.operation);
		assertEquals("r", diff.text);
		diff = differences.get(7);
		assertEquals(diff_match_patch.Operation.EQUAL, diff.operation);
		assertEquals("\nObstruents\tch, d, f, g, h, k, p, s, sh, t, v, x, z\n", diff.text);
		diff = differences.get(8);
		assertEquals(diff_match_patch.Operation.DELETE, diff.operation);
		assertEquals("Voiced Obstruents	b, d, g, z\n", diff.text);
	}

	protected void compareSameSegments(SHApproachLanguageComparer comparer) {
		comparer.compareSegmentInventory();
		SortedSet<DifferentSegment> diffs = comparer.getSegmentsWhichDiffer();
		assertEquals("number of different segments", 0, diffs.size());
	}

	protected void compareSameGraphemes(SHApproachLanguageComparer comparer) {
		comparer.compareGraphemes();;
		SortedSet<DifferentGrapheme> diffs = comparer.getGraphemesWhichDiffer();
		assertEquals("number of different graphemes", 0, diffs.size());
	}

	protected void compareSameGraphemeNaturalClasses(SHApproachLanguageComparer comparer) {
		comparer.compareGraphemeNaturalClasses();;
		SortedSet<DifferentGraphemeNaturalClass> diffs = comparer.getGraphemeNaturalClassesWhichDiffer();
		assertEquals("number of different grapheme natural classes", 0, diffs.size());
	}

	protected void compareSameEnvironments(SHApproachLanguageComparer comparer) {
		comparer.compareEnvironments();;
		SortedSet<DifferentEnvironment> diffs = comparer.getEnvironmentsWhichDiffer();
		assertEquals("number of different environments", 0, diffs.size());
	}

	protected void compareSameNaturalClasses(SHApproachLanguageComparer comparer) {
		comparer.compareSonorityHierarchy();
		SortedSet<DifferentSHNaturalClass> diffs = comparer.getNaturalClassesWhichDiffer();
		assertEquals("number of different natural classes", 0, diffs.size());
	}

	protected void compareSameWords(SHApproachLanguageComparer comparer) {
		comparer.compareWords();
		SortedSet<DifferentWord> diffs = comparer.getWordsWhichDiffer();
		assertEquals("number of different words", 0, diffs.size());
	}

	@Test
	public void compareSameSyllablePatternOrderTest() {
		// setup
		LanguageProject languageProject3 = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject3, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE_3);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		SHApproach sha3 = languageProject3.getSHApproach();
		SHApproachLanguageComparer comparer = new SHApproachLanguageComparer(sha3, sha3);
		// test (we only use this as a way to tell if the two orders are the
		// same or not)
		comparer.compareSonorityHierarchyOrder();
		LinkedList<Diff> differences = comparer.getSonorityHierarchyOrderDifferences();
		assertEquals(0, differences.size());
	}
}
