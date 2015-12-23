/**
 * 
 */
package sil.org.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.cvapproach.*;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.service.CVSegmenter;

/**
 * @author Andy Black
 *
 */
public class CVSegmenterTest {

	CVApproach cva;
	ObservableList<CVSegment> segmentInventory;
	CVSegmenter segmenter;
	List<CVSegment> cvSegmentInventory;

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
		cva = languageProject.getCVApproach();
		segmentInventory = cva.getCVSegmentInventory();
		segmenter = new CVSegmenter(segmentInventory);
		cvSegmentInventory = segmenter.getSegmentInventory();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// make sure the setup is what we expect
	@Test
	public void inventoryTest() {
		assertEquals("Segment inventory size", 27, segmentInventory.size());
		assertEquals("Segment inventory in segmenter size", 27, cvSegmentInventory.size());
		String seg = cvSegmentInventory.get(0).getSegment().trim();
		assertEquals("First segment is /a/", "a", seg);
		seg = cvSegmentInventory.get(26).getSegment().trim();
		assertEquals("Last segment is /ɲ/", "ɲ", seg);
		HashMap<String, CVSegment> graphemes = segmenter.getGraphemeToSegmentMapping();
		assertEquals("Hash map size is 56", 56, graphemes.size());
	}

	@Test
	public void wordSegmentingTest() {
		checkSegmentation(null, "word is null", "", "", 0, true);
		checkSegmentation("", "word is empty", "", "", 0, true);
		checkSegmentation("añyicho", "Expect graphemes to be /a/, /ñ,ɲ/, /y/, /i/, /ch/, and /o/", "a, ɲ, y, i, ch, o",
				"a, ñ, y, i, ch, o", 6, true);
		checkSegmentation("Chiko", "Expect graphemes to be /Ch,ch/, /i/, /k/, and /o/", "ch, i, k, o", "Ch, i, k, o",
				4, true);
		checkSegmentation("SHiju", "Expect graphemes to be /SH,sh/, /i/, missing", "sh, i", "SH, i", 2, false);
	}

	protected void checkSegmentation(String word, String comment, String expectedSegments, String expectedGraphemes,
			int numberOfSegments, boolean success) {
		List<CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		boolean fSuccess = segmenter.segmentWord(word);
		assertEquals("Expected word to parse", success, fSuccess);
		assertEquals("number of segments should be = " + numberOfSegments, numberOfSegments, segmentsInWord.size());
		String joined = segmentsInWord.stream().map(CVSegmentInSyllable::getSegmentName)
				.collect(Collectors.joining(", "));
		assertEquals(comment, expectedSegments, joined);
		joined = segmentsInWord.stream().map(CVSegmentInSyllable::getGrapheme).collect(Collectors.joining(", "));
		assertEquals(comment, expectedGraphemes, joined);
	}
}
