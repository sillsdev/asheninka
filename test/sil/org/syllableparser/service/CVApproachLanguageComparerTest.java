// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;

import javafx.collections.ObservableList;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.Word;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.syllableparser.model.cvapproach.CVSyllablePattern;

/**
 * @author Andy Black
 *
 */
public class CVApproachLanguageComparerTest {

	LanguageProject languageProject1;
	LanguageProject languageProject2;
	CVApproach cva1;
	CVApproach cva2;

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
		cva1 = languageProject1.getCVApproach();
		languageProject2 = new LanguageProject();
		xmlBackEndProvider = new XMLBackEndProvider(languageProject2, locale);
		file = new File(Constants.UNIT_TEST_DATA_FILE_2);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		cva2 = languageProject2.getCVApproach();
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
		ObservableList<CVNaturalClass> naturalClasses;
		naturalClasses = cva1.getCVNaturalClasses();
		assertEquals("Natural Classes size", 4, naturalClasses.size());
		naturalClasses = cva2.getCVNaturalClasses();
		assertEquals("Natural Classes size", 3, naturalClasses.size());
		// syllable patterns
		ObservableList<CVSyllablePattern> patterns;
		patterns = cva1.getCVSyllablePatterns();
		assertEquals("CV patterns size", 7, patterns.size());
		patterns = cva2.getCVSyllablePatterns();
		assertEquals("CV patterns size", 4, patterns.size());
		// words
		ObservableList<Word> words;
		words = cva1.getWords();
		assertEquals("CV words size", 10025, words.size());
		words = cva2.getWords();
		assertEquals("CV words size", 10025, words.size());
	}

	@Test
	public void compareLanguagesTest() {
		CVApproachLanguageComparer comparer = new CVApproachLanguageComparer(cva1, cva2);
		compareSegments(comparer);
		compareNaturalClasses(comparer);
		compareSyllablePatterns(comparer);
		compareWords(comparer);
	}

	@Test
	public void compareSameLanguagesTest() {
		CVApproachLanguageComparer comparer = new CVApproachLanguageComparer(cva1, cva1);
		compareSameSegments(comparer);
		compareSameNaturalClasses(comparer);
		compareSameSyllablePatterns(comparer);
		compareSameWords(comparer);
	}
	protected void compareSegments(CVApproachLanguageComparer comparer) {
		comparer.compareSegmentInventory();
		SortedSet<DifferentSegment> diffs = comparer.getSegmentsWhichDiffer();
		assertEquals("number of different segments", 13, diffs.size());
		List<DifferentSegment> listOfDiffs = new ArrayList<DifferentSegment>();
		listOfDiffs.addAll(diffs);
		DifferentSegment diffSeg = listOfDiffs.get(1);
		assertEquals("second's 1 is an /a/", "a", ((Segment) diffSeg.getObjectFrom1()).getSegment());
		assertEquals("second's 2 is an /a/", "a", ((Segment) diffSeg.getObjectFrom2()).getSegment());
		assertEquals("second's 1's graphemes are 'a A'", "a A",
				((Segment) diffSeg.getObjectFrom1()).getGraphemes());
		assertEquals("second's 2's graphemes are 'a A á Á'", "a A á Á",
				((Segment) diffSeg.getObjectFrom2()).getGraphemes());
		diffSeg = listOfDiffs.get(0);
		assertNull("first's 1 is null", diffSeg.getObjectFrom1());
		assertEquals("first's 2 is a /\'/", "'", ((Segment) diffSeg.getObjectFrom2()).getSegment());
		assertEquals("first's 2's graphemes are '\''", "'",
				((Segment) diffSeg.getObjectFrom2()).getGraphemes());
		diffSeg = listOfDiffs.get(12);
		assertEquals("tenth's 1 is a /ɲ/", "ɲ", ((Segment) diffSeg.getObjectFrom1()).getSegment());
		assertNull("tenth's 2 is null", diffSeg.getObjectFrom2());
		assertEquals("tenth's 1's graphemes are 'ñ Ñ'", "ñ Ñ",
				((Segment) diffSeg.getObjectFrom1()).getGraphemes());
	}

	protected void compareNaturalClasses(CVApproachLanguageComparer comparer) {
		comparer.compareNaturalClasses();
		SortedSet<DifferentCVNaturalClass> diffs = comparer.getNaturalClassesWhichDiffer();
		assertEquals("number of different natural classes", 4, diffs.size());
		List<DifferentCVNaturalClass> listOfDiffs = new ArrayList<DifferentCVNaturalClass>();
		listOfDiffs.addAll(diffs);
		DifferentCVNaturalClass diffNaturalClass = listOfDiffs.get(1);
		assertEquals("second's 1 is [C]", "C",
				((CVNaturalClass) diffNaturalClass.getObjectFrom1()).getNCName());
		assertEquals("second's 2 is [C]", "C",
				((CVNaturalClass) diffNaturalClass.getObjectFrom2()).getNCName());
		assertEquals(
				"second's 1's reps are 'b, ch, d, f, g, h, k, l, p, s, sh, t, v, w, x, y, z, N'",
				"b, ch, d, f, g, h, k, l, p, s, sh, t, v, w, x, y, z, N",
				((CVNaturalClass) diffNaturalClass.getObjectFrom1()).getSNCRepresentation());
		assertEquals(
				"second's 2's reps are '', b, c, ch, d, f, g, h, j, k, kw, ky, l, m, n, ñ, p, q, r, s, sh, t, ts, v, w, x, y, z'",
				"', b, c, ch, d, f, g, h, j, k, kw, ky, l, m, n, ñ, p, q, r, s, sh, t, ts, v, w, x, y, z",
				((CVNaturalClass) diffNaturalClass.getObjectFrom2()).getSNCRepresentation());
		diffNaturalClass = listOfDiffs.get(2);
		assertNull("third's 1 is null", diffNaturalClass.getObjectFrom1());
		assertEquals("third's 2 is [Gl]", "Gl",
				((CVNaturalClass) diffNaturalClass.getObjectFrom2()).getNCName());
		assertEquals("third's 2's reps are '\', h'", "', h",
				((CVNaturalClass) diffNaturalClass.getObjectFrom2()).getSNCRepresentation());
		diffNaturalClass = listOfDiffs.get(3);
		assertEquals("fourth's 1 is [N]", "N",
				((CVNaturalClass) diffNaturalClass.getObjectFrom1()).getNCName());
		assertNull("fourth's 2 is null", diffNaturalClass.getObjectFrom2());
		assertEquals("fourth's 1's reps are 'm, n, ɲ'", "m, n, ɲ",
				((CVNaturalClass) diffNaturalClass.getObjectFrom1()).getSNCRepresentation());
	}

	protected void compareSyllablePatterns(CVApproachLanguageComparer comparer) {
		comparer.compareSyllablePatterns();
		SortedSet<DifferentCVSyllablePattern> diffs = comparer.getSyllablePatternsWhichDiffer();
		assertEquals("number of different syllable patterns", 8, diffs.size());
		List<DifferentCVSyllablePattern> listOfDiffs = new ArrayList<DifferentCVSyllablePattern>();
		listOfDiffs.addAll(diffs);
		DifferentCVSyllablePattern diffSyllablePattern = listOfDiffs.get(2);
		assertEquals("third's 1 is 'CV", "CV", ((CVSyllablePattern) diffSyllablePattern.getObjectFrom1()).getSPName());
		assertEquals("third's 2 is 'CV'", "CV", ((CVSyllablePattern) diffSyllablePattern.getObjectFrom2()).getSPName());
		assertEquals("third's 1's natural classes are 'C, V'", "C, V",
				((CVSyllablePattern) diffSyllablePattern.getObjectFrom1()).getNCSRepresentation());
		assertEquals("fouthird'srth's 2's natural classes are 'C, v'", "C, v",
				((CVSyllablePattern) diffSyllablePattern.getObjectFrom2()).getNCSRepresentation());
		diffSyllablePattern = listOfDiffs.get(6);
		assertNull("seventh's 1 is null", diffSyllablePattern.getObjectFrom1());
		assertEquals("seventh's 2 is 'V'", "V", ((CVSyllablePattern) diffSyllablePattern.getObjectFrom2()).getSPName());
		assertEquals("seventh's 2's natural classesare 'V'", "V",
				((CVSyllablePattern) diffSyllablePattern.getObjectFrom2()).getNCSRepresentation());
		diffSyllablePattern = listOfDiffs.get(3);
		assertEquals("fourth's 1 is 'CVCC#'", "CVCC#", ((CVSyllablePattern) diffSyllablePattern.getObjectFrom1()).getSPName());
		assertNull("fourth's 2 is null", diffSyllablePattern.getObjectFrom2());
		assertEquals("fourth's 1's natural classes are 'C, V, C, C,#'", "C, V, C, C, #",
				((CVSyllablePattern) diffSyllablePattern.getObjectFrom1()).getNCSRepresentation());
	}
	protected void compareWords(CVApproachLanguageComparer comparer) {
		comparer.compareWords();
		SortedSet<DifferentWord> diffs = comparer.getWordsWhichDiffer();
		assertEquals("number of different words", 8140, diffs.size());
		List<DifferentWord> listOfDiffs = new ArrayList<DifferentWord>();
		listOfDiffs.addAll(diffs);
		DifferentWord diffWord = listOfDiffs.get(3804);
		assertEquals("3804's 1 is motankwakwetsak", "motankwakwetsak",
				((Word) diffWord.getObjectFrom1()).getWord());
		assertEquals("3804's 2 is motankwakwetsak", "motankwakwetsak",
				((Word) diffWord.getObjectFrom2()).getWord());
		assertEquals("3804's 1's parse is ''", "",
				((Word) diffWord.getObjectFrom1()).getCVPredictedSyllabification());
		assertEquals("3804's 2's parse is 'mo.tan.kwa.kwe.tsak'", "mo.tan.kwa.kwe.tsak",
				((Word) diffWord.getObjectFrom2()).getCVPredictedSyllabification());
		diffWord = listOfDiffs.get(1);
		assertEquals("second's 1 is null", null, ((Word) diffWord.getObjectFrom1()));
		assertEquals("second's 2 is aaah", "aaah", ((Word) diffWord.getObjectFrom2()).getWord());
		assertEquals("second's 2's parse is 'aaah'", "a.a.ah",
				((Word) diffWord.getObjectFrom2()).getCVPredictedSyllabification());
		diffWord = listOfDiffs.get(2);
		assertEquals("third's 1 is ababrastro", "ababrastro",
				((Word) diffWord.getObjectFrom1()).getWord());
		assertEquals("third's 2 is null", null, ((Word) diffWord.getObjectFrom2()));
		assertEquals("third's 1's parse is ''", "",
				((Word) diffWord.getObjectFrom1()).getCVPredictedSyllabification());
	}
	
	@Test
	public void compareSyllablePatternOrderTest() {
		// setup
		LanguageProject languageProject3 = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject3, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE_3);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		CVApproach cva3 = languageProject3.getCVApproach();
		LanguageProject languageProject4 = new LanguageProject();
		xmlBackEndProvider = new XMLBackEndProvider(languageProject4, locale);
		file = new File(Constants.UNIT_TEST_DATA_FILE_4);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		CVApproach cva4 = languageProject4.getCVApproach();
		CVApproachLanguageComparer comparer = new CVApproachLanguageComparer(cva3, cva4);
		// test (we only use this as a way to tell if the the two orders are the same or not)
		comparer.compareSyllablePatternOrder();
		LinkedList<Diff> differences = comparer.getSyllablePatternOrderDifferences();
		assertEquals(7, differences.size());
	}
	protected void compareSameSegments(CVApproachLanguageComparer comparer) {
		comparer.compareSegmentInventory();
		SortedSet<DifferentSegment> diffs = comparer.getSegmentsWhichDiffer();
		assertEquals("number of different segments", 0, diffs.size());
	}

	protected void compareSameNaturalClasses(CVApproachLanguageComparer comparer) {
		comparer.compareNaturalClasses();
		SortedSet<DifferentCVNaturalClass> diffs = comparer.getNaturalClassesWhichDiffer();
		assertEquals("number of different natural classes", 0, diffs.size());
	}

	protected void compareSameSyllablePatterns(CVApproachLanguageComparer comparer) {
		comparer.compareSyllablePatterns();
		SortedSet<DifferentCVSyllablePattern> diffs = comparer.getSyllablePatternsWhichDiffer();
		assertEquals("number of different syllable patterns", 0, diffs.size());
	}
	protected void compareSameWords(CVApproachLanguageComparer comparer) {
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
		CVApproach cva3 = languageProject3.getCVApproach();
		CVApproachLanguageComparer comparer = new CVApproachLanguageComparer(cva3, cva3);
		// test (we only use this as a way to tell if the the two orders are the same or not)
		comparer.compareSyllablePatternOrder();
		LinkedList<Diff> differences = comparer.getSyllablePatternOrderDifferences();
		assertEquals(1, differences.size());
	}
}
