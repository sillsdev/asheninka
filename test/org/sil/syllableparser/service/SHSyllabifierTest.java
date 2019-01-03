// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.*;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHSyllable;
import org.sil.syllableparser.service.CVNaturalClasser;
import org.sil.syllableparser.service.CVNaturalClasserResult;
import org.sil.syllableparser.service.CVSegmenter;
import org.sil.syllableparser.service.CVSegmenterResult;
import org.sil.syllableparser.service.CVSyllabifier;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter and CVNaturalClasser
 *         classes are functioning correctly
 */
public class SHSyllabifierTest {

	SHApproach sonHierApproach;
	ObservableList<SHNaturalClass> naturalClasses;
	CVSegmenter segmenter;
	ObservableList<Segment> segmentInventory;
	List<Grapheme> activeGraphemes;
	List<SHNaturalClass> shNaturalClasses;
	SHSyllabifier shSyllabifier;

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
		sonHierApproach = languageProject.getSHApproach();
		activeGraphemes = languageProject.getActiveGraphemes();
		segmenter = new CVSegmenter(activeGraphemes,
				languageProject.getActiveGraphemeNaturalClasses());
		shNaturalClasses = sonHierApproach.getActiveSHNaturalClasses();
//		shSyllabifier = new SHSyllabifier(cvPatterns, null);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// make sure the setup is what we expect
	@Test
	public void syllabifierTest() {
//		assertEquals("CV patterns size", 7, cvPatterns.size());
//		String pattern = cvPatterns.get(0).getSPName().trim();
//		assertEquals("First CV pattern is [C][V]", "CV", pattern);
//		pattern = cvPatterns.get(2).getSPName().trim();
//		assertEquals("Third pattern is [CVC]", "CVC", pattern);
	}

	@Test
	public void wordToSegmentToSyllableTest() {
		checkSyllabification("", false, 0, "");
		checkSyllabification("A", true, 1, "A");
		checkSyllabification("ta", true, 1, "ta");
		checkSyllabification("tad", true, 1, "tad");
		checkSyllabification("Chiko", true, 2, "Chi.ko");
		checkSyllabification("dapgek", true, 2, "dap.gek");
		checkSyllabification("dapkgek", true, 2, "dap.kgek");
		checkSyllabification("dampidon", true, 3, "dam.pi.don");
		checkSyllabification("dovdek", true, 2, "dov.dek");
		checkSyllabification("fuhgt", true, 2, "fuh.gt");
		checkSyllabification("dlofugh", true, 3, "dlo.fug.h");
		checkSyllabification("do", true, 1, "do");
		checkSyllabification("funglo", true, 2, "fun.glo");
		checkSyllabification("fugh", true, 2, "fug.h");
		checkSyllabification("flu", true, 1, "flu");
		checkSyllabification("fluk", true, 1, "fluk");
		checkSyllabification("iae", true, 1, "iae");
	}

	protected void checkSyllabification(String word, boolean success, int numberOfSyllables,
			String expectedSyllabification) {
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		boolean fSuccess = segResult.success;
		List<CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		shSyllabifier = new SHSyllabifier(sonHierApproach);
		fSuccess = shSyllabifier.syllabify(segmentsInWord);
		assertEquals("word syllabified", success, fSuccess);
		List<SHSyllable> syllablesInWord = shSyllabifier.getSyllablesInCurrentWord();
		System.out.println("syls=" + shSyllabifier.getSyllabificationOfCurrentWord());
		assertEquals("Expect " + numberOfSyllables + " syllables in word", numberOfSyllables,
				syllablesInWord.size());
		assertEquals("Expected Syllabification of word", expectedSyllabification,
				shSyllabifier.getSyllabificationOfCurrentWord());
	}

	protected void checkSyllabifyWord(String word, boolean success, int numberOfSyllables,
			String expectedCVPatternsUsed, String expectedSyllabification) {
//		boolean fSuccess = stringSyllabifier.convertStringToSyllables(word);
//		assertEquals("word syllabified", success, fSuccess);
//		List<CVSyllable> syllablesInWord = stringSyllabifier.getSyllablesInCurrentWord();
//		assertEquals("Expect " + numberOfSyllables + " syllables in word", numberOfSyllables,
//				syllablesInWord.size());
//		String joined = syllablesInWord.stream().map(CVSyllable::getNaturalClassNamesInSyllable)
//				.collect(Collectors.joining(", "));
//		assertEquals("Expected Syllable CV Pattern", expectedCVPatternsUsed, joined);
//		assertEquals("Expected Syllabification of word", expectedSyllabification,
//				stringSyllabifier.getSyllabificationOfCurrentWord());
	}

	@Test
	public void traceSyllabifyWordTest() {
//		stringSyllabifier.setDoTrace(true);
//		checkSyllabifyWord("Chiko", true, 2, "CV, CV", "Chi.ko");
//		List<CVTraceSyllabifierInfo> traceInfo = stringSyllabifier.getSyllabifierTraceInfo();
//		assertEquals(1, traceInfo.size());
//		CVTraceSyllabifierInfo sylInfo = traceInfo.get(0);
//		assertNotNull(sylInfo);
//		assertEquals("C, V", sylInfo.sCVSyllablePattern);
//		traceInfo = sylInfo.daughterInfo;
//		assertEquals(1, traceInfo.size());
//		sylInfo = traceInfo.get(0);
//		assertNotNull(sylInfo);
//		assertEquals("C, V", sylInfo.sCVSyllablePattern);
//		traceInfo = sylInfo.daughterInfo;
//		assertEquals(0, traceInfo.size());
//
//		checkSyllabifyWord("bampidon", true, 3, "CVN, CV, CVN", "bam.pi.don");
//		traceInfo = stringSyllabifier.getSyllabifierTraceInfo();
//		assertEquals(2, traceInfo.size());
//		sylInfo = traceInfo.get(0);
//		assertNotNull(sylInfo);
//		assertEquals("C, V", sylInfo.sCVSyllablePattern);
//		assertEquals(false, sylInfo.parseWasSuccessful);
//		traceInfo = sylInfo.daughterInfo;
//		assertEquals(7, traceInfo.size());
//		sylInfo = traceInfo.get(0);
//		assertNotNull(sylInfo);
//		assertEquals("C, V", sylInfo.sCVSyllablePattern);
//		assertEquals(false, sylInfo.parseWasSuccessful);
//		traceInfo = sylInfo.daughterInfo;
//		assertEquals(0, traceInfo.size());
//
//		traceInfo = stringSyllabifier.getSyllabifierTraceInfo();
//		sylInfo = traceInfo.get(1);
//		assertNotNull(sylInfo);
//		assertEquals("C, V, N", sylInfo.sCVSyllablePattern);
//		assertEquals(true, sylInfo.parseWasSuccessful);
//		traceInfo = sylInfo.daughterInfo;
//		assertEquals(1, traceInfo.size());
//		sylInfo = traceInfo.get(0);
//		assertNotNull(sylInfo);
//		assertEquals("C, V", sylInfo.sCVSyllablePattern);
//		assertEquals(true, sylInfo.parseWasSuccessful);
//		traceInfo = sylInfo.daughterInfo;
//		assertEquals(2, traceInfo.size());
//		sylInfo = traceInfo.get(1);
//		assertNotNull(sylInfo);
//		assertEquals("C, V, N", sylInfo.sCVSyllablePattern);
//		assertEquals(true, sylInfo.parseWasSuccessful);
//		traceInfo = sylInfo.daughterInfo;
//		assertEquals(0, traceInfo.size());
//
//		checkSyllabifyWord("funglo", false, 0, "", ""); // CVCC only word
//														// finally; CCV not
//														// possible word
//														// medially
//		traceInfo = stringSyllabifier.getSyllabifierTraceInfo();
//		assertEquals(7, traceInfo.size());
//		sylInfo = traceInfo.get(0);
//		assertNotNull(sylInfo);
//		assertEquals("C, V", sylInfo.sCVSyllablePattern);
//		assertEquals(false, sylInfo.parseWasSuccessful);
//		traceInfo = sylInfo.daughterInfo;
//		assertEquals(7, traceInfo.size());
//		sylInfo = traceInfo.get(0);
//		assertNotNull(sylInfo);
//		assertEquals("C, V", sylInfo.sCVSyllablePattern);
//		assertEquals(false, sylInfo.parseWasSuccessful);
//		traceInfo = sylInfo.daughterInfo;
//		assertEquals(0, traceInfo.size());
//
//		traceInfo = stringSyllabifier.getSyllabifierTraceInfo();
//		sylInfo = traceInfo.get(1);
//		assertNotNull(sylInfo);
//		assertEquals("C, V, N", sylInfo.sCVSyllablePattern);
//		assertEquals(false, sylInfo.parseWasSuccessful);
//		traceInfo = sylInfo.daughterInfo;
//		assertEquals(7, traceInfo.size());
//		sylInfo = traceInfo.get(0);
//		assertNotNull(sylInfo);
//		assertEquals("C, V", sylInfo.sCVSyllablePattern);
//		assertEquals(false, sylInfo.parseWasSuccessful);
//		traceInfo = sylInfo.daughterInfo;
//		assertEquals(0, traceInfo.size());

	}
}
