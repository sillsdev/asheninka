/**
 * 
 */
package sil.org.syllableparser.domain;

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

import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.CVSegment;
import sil.org.syllableparser.model.CVApproach;
import sil.org.syllableparser.model.LanguageProject;

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
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(
				languageProject, locale);
		File file = new File(
				"test/sil/org/syllableparser/testData/CVTestData.sylpdata");
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

	@Test
	public void inventoryTest() {
		assertEquals("Segment inventory size", 27, segmentInventory.size());
		assertEquals("Segment inventory size", 27, cvSegmentInventory.size());
		String seg = cvSegmentInventory.get(0).getSegment().trim();
		assertEquals("First segment is /a/", "a", seg);
		seg = cvSegmentInventory.get(26).getSegment().trim();
		assertEquals("Last segment is /ɲ/", "ɲ", seg);
		HashMap<String, CVSegment> graphemes = segmenter.getGraphemes();
		assertEquals("Hash map size is 56", 56, graphemes.size());
	}

	@Test
	public void wordSegmentingTest() {
		checkSegmentation(null, "word is null", "", 0, true);
		checkSegmentation("", "word is empty", "", 0, true);
		checkSegmentation("añyicho", "Expect segments to be /a/, /ɲ/, /y/, /i/, /ch/, and /o/", "a, ɲ, y, i, ch, o", 6, true);
		checkSegmentation("Chiko", "Expect segments to be /ch/, /i/, /k/, and /o/", "ch, i, k, o", 4, true);
		checkSegmentation("SHiju", "Expect segments to be /sh/, /i/, missing", "sh, i", 2, false);
	}

	protected void checkSegmentation(String word, String comment,
			String expected, int numberOfSegments, boolean success) {
		List<CVSegment> segmentsInWord = segmenter.getSegmentsInWord();
		boolean fSuccess = segmenter.segmentWord(word);
		assertEquals("Expected word to parse", success, fSuccess);
		assertEquals("number of segments should be = " + numberOfSegments, numberOfSegments, segmentsInWord.size());
		String joined = segmentsInWord.stream().map(CVSegment::getSegment)
				.collect(Collectors.joining(", "));
		assertEquals(comment, expected, joined);
	}
}
