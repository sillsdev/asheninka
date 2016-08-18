/**
 * 
 */
package sil.org.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javafx.collections.ObservableList;

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
		compareWords(comparer);
	}

	protected void compareSegments(CVApproachLanguageComparer comparer) {
		comparer.compareSegmentInventory();
		Set<DifferentSegment> diffs = comparer.getSegmentsWhichDiffer();
		assertEquals("number of different segments", 13, diffs.size());
		List<DifferentSegment> listOfDiffs = new ArrayList<DifferentSegment>();
		listOfDiffs.addAll(diffs);
		DifferentSegment diffSeg = listOfDiffs.get(0);
		assertEquals("first's 1 is an /e/", "e", ((Segment) diffSeg.getObjectFrom1()).getSegment());
		assertEquals("first's 2 is an /e/", "e", ((Segment) diffSeg.getObjectFrom2()).getSegment());
		assertEquals("first's 1's graphemes are 'e E'", "e E",
				((Segment) diffSeg.getObjectFrom1()).getGraphemes());
		assertEquals("first's 2's graphemes are 'e E é É'", "e E é É",
				((Segment) diffSeg.getObjectFrom2()).getGraphemes());
		diffSeg = listOfDiffs.get(3);
		assertNull("fourth's 1 is null", diffSeg.getObjectFrom1());
		assertEquals("fourth's 2 is an /ñ/", "ñ", ((Segment) diffSeg.getObjectFrom2()).getSegment());
		assertEquals("fourth's 2's graphemes are 'ñ Ñ'", "ñ Ñ",
				((Segment) diffSeg.getObjectFrom2()).getGraphemes());
		diffSeg = listOfDiffs.get(9);
		assertEquals("tenth's 1 is a /ɲ/", "ɲ", ((Segment) diffSeg.getObjectFrom1()).getSegment());
		assertNull("tenth's 2 is null", diffSeg.getObjectFrom2());
		assertEquals("tenth's 1's graphemes are 'ñ Ñ'", "ñ Ñ",
				((Segment) diffSeg.getObjectFrom1()).getGraphemes());
	}

	protected void compareNaturalClasses(CVApproachLanguageComparer comparer) {
		comparer.compareNaturalClasses();
		Set<DifferentCVNaturalClass> diffs = comparer.getNaturalClassesWhichDiffer();
		assertEquals("number of different natural classes", 4, diffs.size());
		List<DifferentCVNaturalClass> listOfDiffs = new ArrayList<DifferentCVNaturalClass>();
		listOfDiffs.addAll(diffs);
		DifferentCVNaturalClass diffNaturalClass = listOfDiffs.get(0);
		assertEquals("first's 1 is [C]", "C",
				((CVNaturalClass) diffNaturalClass.getObjectFrom1()).getNCName());
		assertEquals("first's 2 is [C]", "C",
				((CVNaturalClass) diffNaturalClass.getObjectFrom2()).getNCName());
		assertEquals(
				"first's 1's reps are 'b, ch, d, f, g, h, k, l, p, s, sh, t, v, w, x, y, z, N'",
				"b, ch, d, f, g, h, k, l, p, s, sh, t, v, w, x, y, z, N",
				((CVNaturalClass) diffNaturalClass.getObjectFrom1()).getSNCRepresentation());
		assertEquals(
				"first's 2's reps are '', b, c, ch, d, f, g, h, j, k, kw, ky, l, m, n, ñ, p, q, r, s, sh, t, ts, v, w, x, y, z'",
				"', b, c, ch, d, f, g, h, j, k, kw, ky, l, m, n, ñ, p, q, r, s, sh, t, ts, v, w, x, y, z",
				((CVNaturalClass) diffNaturalClass.getObjectFrom2()).getSNCRepresentation());
		diffNaturalClass = listOfDiffs.get(3);
		assertNull("fourth's 1 is null", diffNaturalClass.getObjectFrom1());
		assertEquals("fourth's 2 is [Gl]", "Gl",
				((CVNaturalClass) diffNaturalClass.getObjectFrom2()).getNCName());
		assertEquals("fourth's 2's reps are '\', h'", "', h",
				((CVNaturalClass) diffNaturalClass.getObjectFrom2()).getSNCRepresentation());
		diffNaturalClass = listOfDiffs.get(1);
		assertEquals("first's 1 is [N]", "N",
				((CVNaturalClass) diffNaturalClass.getObjectFrom1()).getNCName());
		assertNull("first's 2 is null", diffNaturalClass.getObjectFrom2());
		assertEquals("fourth's 1's reps are 'm, n, ɲ'", "m, n, ɲ",
				((CVNaturalClass) diffNaturalClass.getObjectFrom1()).getSNCRepresentation());
	}

	protected void compareWords(CVApproachLanguageComparer comparer) {
		comparer.compareWords();
		Set<DifferentWord> diffs = comparer.getWordsWhichDiffer();
		assertEquals("number of different words", 8140, diffs.size());
		List<DifferentWord> listOfDiffs = new ArrayList<DifferentWord>();
		listOfDiffs.addAll(diffs);
		DifferentWord diffWord = listOfDiffs.get(0);
		assertEquals("first's 1 is motankwakwetsak", "motankwakwetsak",
				((Word) diffWord.getObjectFrom1()).getWord());
		assertEquals("first's 2 is motankwakwetsak", "motankwakwetsak",
				((Word) diffWord.getObjectFrom2()).getWord());
		assertEquals("first's 1's parse is ''", "",
				((Word) diffWord.getObjectFrom1()).getCVPredictedSyllabification());
		assertEquals("first's 2's parse is 'mo.tan.kwa.kwe.tsak'", "mo.tan.kwa.kwe.tsak",
				((Word) diffWord.getObjectFrom2()).getCVPredictedSyllabification());
		diffWord = listOfDiffs.get(2596);
		assertEquals("2597's 1 is null", null, ((Word) diffWord.getObjectFrom1()));
		assertEquals("2597's 2 is aaah", "aaah", ((Word) diffWord.getObjectFrom2()).getWord());
		assertEquals("2597's 2's parse is 'aaah'", "a.a.ah",
				((Word) diffWord.getObjectFrom2()).getCVPredictedSyllabification());
		diffWord = listOfDiffs.get(5357);
		assertEquals("5358's 1 is ababrastro", "ababrastro",
				((Word) diffWord.getObjectFrom1()).getWord());
		assertEquals("5358's 2 is null", null, ((Word) diffWord.getObjectFrom2()));
		assertEquals("5358's 1's parse is ''", "",
				((Word) diffWord.getObjectFrom1()).getCVPredictedSyllabification());
	}
}
