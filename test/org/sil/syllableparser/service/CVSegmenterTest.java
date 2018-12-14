// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service;

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
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Approach;
import org.sil.syllableparser.model.Environment;
import org.sil.syllableparser.model.EnvironmentContext;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.*;
import org.sil.syllableparser.service.CVSegmenter;
import org.sil.syllableparser.service.CVSegmenterResult;

/**
 * @author Andy Black
 *
 */
public class CVSegmenterTest {

	Approach cva;
	ObservableList<Segment> segmentInventory;
	CVSegmenter segmenter;
	List<Grapheme> activeGraphemes;
	LanguageProject languageProject;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		cva = languageProject.getCVApproach();
		activeGraphemes = languageProject.getActiveGraphemes();
		segmenter = new CVSegmenter(activeGraphemes,
				languageProject.getActiveGraphemeNaturalClasses());
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
		assertEquals("Active graphemes size", 54, activeGraphemes.size());
		String grapheme = activeGraphemes.get(0).getForm().trim();
		assertEquals("First grapheme is /a/", "a", grapheme);
		grapheme = activeGraphemes.get(53).getForm().trim();
		assertEquals("Last grapheme is /ɲ/", "Ñ", grapheme);
		HashMap<String, List<Grapheme>> graphemes = segmenter.getGraphemeToSegmentMapping();
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
		checkSegmentation("aqba", "Expect graphemes to be /a/, missing", "a", "a", 1, false, 1);
		checkSegmentation("shomu", "Expect sh environment to fail: it is not before /i/", "s", "s",
				1, false, 1);
	}

	@Test
	public void wordSegmentingWithEnvironmentsTest() {
		languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE_ENVIRONMENTS);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		cva = languageProject.getCVApproach();
		activeGraphemes = languageProject.getActiveGraphemes();
		segmenter = new CVSegmenter(activeGraphemes,
				languageProject.getActiveGraphemeNaturalClasses());

		checkSegmentation("tlaqa", "Expect graphemes to be /tl/, /a/, qk/, and /a/",
				"tl, a, k, a", "tl, a, q, a", 4, true, 0);
		checkSegmentation("tliqa", "Expect q environment to fail: it is not after /tla/", "tl, i", "tl, i",
				2, false, 3);
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
