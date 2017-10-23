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
import sil.org.syllableparser.model.Grapheme;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.service.CVNaturalClasser;
import sil.org.syllableparser.service.CVSegmenter;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter class is functioning
 *         correctly
 */
public class CVNaturalClasserTest {

	CVApproach cva;
	ObservableList<CVNaturalClass> naturalClasses;
	CVSegmenter segmenter;
	ObservableList<Segment> segmentInventory;
	List<Grapheme> activeGraphemes;
	CVNaturalClasser naturalClasser;
	List<CVNaturalClass> cvNaturalClasses;

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
		activeGraphemes = languageProject.getActiveGraphemes();
		segmenter = new CVSegmenter(activeGraphemes);
		cvNaturalClasses = cva.getActiveCVNaturalClasses();
		naturalClasser = new CVNaturalClasser(cvNaturalClasses);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// make sure the setup is what we expect
	@Test
	public void naturalClassesTest() {
		assertEquals("Active natural Classes in natural classer size", 6, cvNaturalClasses.size());
		String nc = cvNaturalClasses.get(0).getNCName().trim();
		assertEquals("First natural class is [C]", "C", nc);
		nc = cvNaturalClasses.get(1).getNCName().trim();
		assertEquals("Second natural class is [V]", "V", nc);
		nc = cvNaturalClasses.get(2).getNCName().trim();
		assertEquals("Third natural class is [N]", "N", nc);
		HashMap<String, List<CVNaturalClass>> segmentToNaturalClasses = naturalClasser
				.getSegmentToNaturalClasses();
		assertEquals("Hash map size is 25", 25, segmentToNaturalClasses.size());
	}

	@Test
	public void wordToSegmentToNaturalClassesTest() {

		checkNaturalClassParsing("Chiko", true, 4, "C, {V,V+hi}, C, V", "C, V, C, V", "abc");
		checkNaturalClassParsing("champion", true, 7, "C, V, {C,N,S,[+lab, +vd]}, C, {V,V+hi}, V, {C,N,S}", "C, V, N, C, V, V, N",
				"abc");
		checkNaturalClassParsing("namo", true, 4, "{C,N,S}, V, {C,N,S,[+lab, +vd]}, V", "C, V, C, V", "abc");
		// next three fail because /r/ is not in any class
		checkNaturalClassParsing("karui", false, 2, "C, V", "C, V", "ka");
		checkNaturalClassParsing("kiro", false, 2, "C, {V,V+hi}", "C, {V,V+hi}", "ki");
		checkNaturalClassParsing("riko", false, 0, "", "", "");
	}

	protected void checkNaturalClassParsing(String word, boolean success,
			int numberOfNaturalClasses, String expectedCVPattern, String sClasesSoFar,
			String sGraphemesSoFar) {
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		boolean fSuccess = segResult.success;
		assertEquals("word segmented", true, fSuccess);
		List<CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		CVNaturalClasserResult ncResult = naturalClasser
				.convertSegmentsToNaturalClasses(segmentsInWord);
		fSuccess = ncResult.success;
		assertEquals("segments converted to natural classes", success, fSuccess);
		List<List<CVNaturalClassInSyllable>> naturalClassesInWord = naturalClasser
				.getNaturalClassListsInCurrentWord();
		assertEquals("Expect " + numberOfNaturalClasses + " natural classes in word",
				numberOfNaturalClasses, naturalClassesInWord.size());
		String joined = naturalClasser.getNaturalClassListsInCurrentWordAsString();
		assertEquals("Expected CV Pattern", expectedCVPattern, joined);
		if (!fSuccess) {
			assertEquals("Expected classes so far", sClasesSoFar, ncResult.sClassesSoFar);
			assertEquals("Expected graphemes so far", sGraphemesSoFar, ncResult.sGraphemesSoFar);
		}
	}

}
