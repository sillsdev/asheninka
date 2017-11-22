// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
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
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.service.CVSegmenter;

/**
 * @author Andy Black
 *
 */
public class CVSegmenterTest {

	CVApproach cva;
	ObservableList<Segment> segmentInventory;
	CVSegmenter segmenter;
	List<Segment> cvSegmentInventory;

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
		cvSegmentInventory = languageProject.getActiveSegmentsInInventory();
		segmenter = new CVSegmenter(cvSegmentInventory);
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
		assertEquals("Active segment inventory size", 26, cvSegmentInventory.size());
		String seg = cvSegmentInventory.get(0).getSegment().trim();
		assertEquals("First segment is /a/", "a", seg);
		seg = cvSegmentInventory.get(25).getSegment().trim();
		assertEquals("Last segment is /ɲ/", "ɲ", seg);
		HashMap<String, Segment> graphemes = segmenter.getGraphemeToSegmentMapping();
		assertEquals("Hash map size is 54", 54, graphemes.size());
	}

	@Test
	public void wordSegmentingTest() {
		checkSegmentation(null, "word is null", "", "", 0, true, 0);
		checkSegmentation("", "word is empty", "", "", 0, true, 0);
		checkSegmentation("añyicho", "Expect graphemes to be /a/, /ñ,ɲ/, /y/, /i/, /ch/, and /o/",
				"a, ɲ, y, i, ch, o", "a, ñ, y, i, ch, o", 6, true, 0);
		checkSegmentation("Chiko", "Expect graphemes to be /Ch,ch/, /i/, /k/, and /o/",
				"ch, i, k, o", "Ch, i, k, o", 4, true, 0);
		checkSegmentation("SHiju", "Expect graphemes to be /SH,sh/, /i/, missing", "sh, i",
				"SH, i", 2, false, 3);
		checkSegmentation("aqba", "Expect graphemes to be /a/, missing", "a", "a", 1, false,
				1);
	}

	protected void checkSegmentation(String word, String comment, String expectedSegments,
			String expectedGraphemes, int numberOfSegments, boolean success, int iPositionOfFailure) {
		List<CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		CVSegmenterResult result = segmenter.segmentWord(word);
		boolean fSuccess = result.success;
		assertEquals("Expected word to parse", success, fSuccess);
		if (!success) {
			assertEquals("position of failure should be = " + iPositionOfFailure,
					iPositionOfFailure, result.iPositionOfFailure);
		}
		assertEquals("number of segments should be = " + numberOfSegments, numberOfSegments,
				segmentsInWord.size());
		String joined = segmentsInWord.stream().map(CVSegmentInSyllable::getSegmentName)
				.collect(Collectors.joining(", "));
		assertEquals(comment, expectedSegments, joined);
		joined = segmentsInWord.stream().map(CVSegmentInSyllable::getGrapheme)
				.collect(Collectors.joining(", "));
		assertEquals(comment, expectedGraphemes, joined);
	}
}
