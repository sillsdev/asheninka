// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
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
import sil.org.syllableparser.service.CVNaturalClasser;
import sil.org.syllableparser.service.CVSegmenter;
import sil.org.syllableparser.service.CVSyllabifier;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter and CVNaturalClasser
 *         classes are functioning correctly
 */
public class CVSyllabifierTest {

	CVApproach cva;
	ObservableList<CVNaturalClass> naturalClasses;
	CVSegmenter segmenter;
	ObservableList<Segment> segmentInventory;
	List<Segment> cvSegmentInventory;
	CVNaturalClasser naturalClasser;
	List<CVNaturalClass> cvNaturalClasses;
	ObservableList<CVSyllablePattern> patterns;
	CVSyllabifier patternSyllabifier;
	CVSyllabifier stringSyllabifier;
	List<CVSyllablePattern> cvPatterns;

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
		cvNaturalClasses = cva.getActiveCVNaturalClasses();
		naturalClasser = new CVNaturalClasser(cvNaturalClasses);
		cvPatterns = cva.getActiveCVSyllablePatterns();
		patternSyllabifier = new CVSyllabifier(cvPatterns, null);
		stringSyllabifier = new CVSyllabifier(cva);
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
		assertEquals("CV patterns size", 7, cvPatterns.size());
		String pattern = cvPatterns.get(0).getSPName().trim();
		assertEquals("First CV pattern is [C][V]", "CV", pattern);
		pattern = cvPatterns.get(1).getSPName().trim();
		assertEquals("Last pattern is [CVC]", "CVC", pattern);
	}

	@Test
	public void wordToSegmentToNaturalClassesToSyllableTest() {
		checkSyllabification("Chiko", true, 2, "CV, CV", "Chi.ko");
		checkSyllabification("dapbek", true, 2, "CVC, CVC", "dap.bek");
		checkSyllabification("bampidon", true, 3, "CVN, CV, CVN", "bam.pi.don");
		checkSyllabification("bovdek", true, 2, "CVC, CVC", "bov.dek");
		checkSyllabification("fuhgt", false, 0, "", "");  // no CCC possible
		checkSyllabification("blofugh", true, 2, "CCV, CVCC", "blo.fugh");
		checkSyllabification("bo", true, 1, "CV", "bo");
		checkSyllabification("funglo", false, 0, "", "");  // CVCC only word finally; CCV not possible word medially
		checkSyllabification("fugh", true, 1, "CVCC", "fugh");
		checkSyllabification("flu", true, 1, "CCV", "flu");
		checkSyllabification("fluk", true, 1, "CCV+hiC", "fluk");
	}

	protected void checkSyllabification(String word, boolean success, int numberOfSyllables,
			String expectedCVPatternsUsed, String expectedSyllabification) {
		CVSegmenterResult segResult =  segmenter.segmentWord(word);
		boolean fSuccess = segResult.success;
		List<CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		CVNaturalClasserResult ncResult = naturalClasser.convertSegmentsToNaturalClasses(segmentsInWord);
		fSuccess = ncResult.success;
		List<List<CVNaturalClassInSyllable>> naturalClassesInWord = naturalClasser
				.getNaturalClassListsInCurrentWord();
		patternSyllabifier = new CVSyllabifier(cvPatterns, naturalClassesInWord);
		fSuccess = patternSyllabifier.convertNaturalClassesToSyllables();
		assertEquals("word syllabified", success, fSuccess);
		List<CVSyllable> syllablesInWord = patternSyllabifier.getSyllablesInCurrentWord();
		assertEquals("Expect " + numberOfSyllables + " syllables in word", numberOfSyllables,
				syllablesInWord.size());
		String joined = syllablesInWord.stream().map(CVSyllable::getNaturalClassNamesInSyllable)
				.collect(Collectors.joining(", "));
		assertEquals("Expected Syllable CV Pattern", expectedCVPatternsUsed, joined);
		assertEquals("Expected Syllabification of word", expectedSyllabification,
				patternSyllabifier.getSyllabificationOfCurrentWord());
	}

	@Test
	public void syllabifyWordTest() {
		checkSyllabifyWord("Chiko", true, 2, "CV, CV", "Chi.ko");
		checkSyllabifyWord("dapbek", true, 2, "CVC, CVC", "dap.bek");
		checkSyllabifyWord("bampidon", true, 3, "CVN, CV, CVN", "bam.pi.don");
		checkSyllabifyWord("bovdek", true, 2, "CVC, CVC", "bov.dek");
		checkSyllabifyWord("fuhgt", false, 0, "", "");  // no CCC possible
		checkSyllabifyWord("blofugh", true, 2, "CCV, CVCC", "blo.fugh");
		checkSyllabifyWord("bo", true, 1, "CV", "bo");
		checkSyllabifyWord("funglo", false, 0, "", "");  // CVCC only word finally; CCV not possible word medially
		checkSyllabifyWord("fugh", true, 1, "CVCC", "fugh");
		checkSyllabifyWord("flu", true, 1, "CCV", "flu");
		checkSyllabifyWord("cat", false, 0, "", ""); // no c segment
	}

	protected void checkSyllabifyWord(String word, boolean success, int numberOfSyllables,
			String expectedCVPatternsUsed, String expectedSyllabification) {
		boolean fSuccess = stringSyllabifier.convertStringToSyllables(word);
		assertEquals("word syllabified", success, fSuccess);
		List<CVSyllable> syllablesInWord = stringSyllabifier.getSyllablesInCurrentWord();
		assertEquals("Expect " + numberOfSyllables + " syllables in word", numberOfSyllables,
				syllablesInWord.size());
		String joined = syllablesInWord.stream().map(CVSyllable::getNaturalClassNamesInSyllable)
				.collect(Collectors.joining(", "));
		assertEquals("Expected Syllable CV Pattern", expectedCVPatternsUsed, joined);
		assertEquals("Expected Syllabification of word", expectedSyllabification,
				stringSyllabifier.getSyllabificationOfCurrentWord());
	}

	@Test
	public void traceSyllabifyWordTest() {
		stringSyllabifier.setDoTrace(true);
		checkSyllabifyWord("Chiko", true, 2, "CV, CV", "Chi.ko");
		List<CVTraceSyllabifierInfo> traceInfo = stringSyllabifier.getSyllabifierTraceInfo();
		assertEquals(1, traceInfo.size());
		CVTraceSyllabifierInfo sylInfo = traceInfo.get(0);
		assertNotNull(sylInfo);
		assertEquals("C, V", sylInfo.sCVSyllablePattern);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(1, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertNotNull(sylInfo);
		assertEquals("C, V", sylInfo.sCVSyllablePattern);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(0, traceInfo.size());
		
		checkSyllabifyWord("bampidon", true, 3, "CVN, CV, CVN", "bam.pi.don");
		traceInfo = stringSyllabifier.getSyllabifierTraceInfo();
		assertEquals(3, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertNotNull(sylInfo);
		assertEquals("C, V", sylInfo.sCVSyllablePattern);
		assertEquals(false,sylInfo.parseWasSuccessful);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(7, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertNotNull(sylInfo);
		assertEquals("C, V", sylInfo.sCVSyllablePattern);
		assertEquals(false,sylInfo.parseWasSuccessful);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(0, traceInfo.size());
		
		traceInfo = stringSyllabifier.getSyllabifierTraceInfo();
		sylInfo = traceInfo.get(2);
		assertNotNull(sylInfo);
		assertEquals("C, V, N", sylInfo.sCVSyllablePattern);
		assertEquals(true,sylInfo.parseWasSuccessful);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(1, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertNotNull(sylInfo);
		assertEquals("C, V", sylInfo.sCVSyllablePattern);
		assertEquals(true,sylInfo.parseWasSuccessful);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(3, traceInfo.size());
		sylInfo = traceInfo.get(2);
		assertNotNull(sylInfo);
		assertEquals("C, V, N", sylInfo.sCVSyllablePattern);
		assertEquals(true,sylInfo.parseWasSuccessful);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(0, traceInfo.size());
		
		checkSyllabifyWord("funglo", false, 0, "", "");  // CVCC only word finally; CCV not possible word medially
		traceInfo = stringSyllabifier.getSyllabifierTraceInfo();
		assertEquals(7, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertNotNull(sylInfo);
		assertEquals("C, V", sylInfo.sCVSyllablePattern);
		assertEquals(false,sylInfo.parseWasSuccessful);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(7, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertNotNull(sylInfo);
		assertEquals("C, V", sylInfo.sCVSyllablePattern);
		assertEquals(false,sylInfo.parseWasSuccessful);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(0, traceInfo.size());
		
		traceInfo = stringSyllabifier.getSyllabifierTraceInfo();
		sylInfo = traceInfo.get(2);
		assertNotNull(sylInfo);
		assertEquals("C, V, N", sylInfo.sCVSyllablePattern);
		assertEquals(false,sylInfo.parseWasSuccessful);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(7, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertNotNull(sylInfo);
		assertEquals("C, V", sylInfo.sCVSyllablePattern);
		assertEquals(false,sylInfo.parseWasSuccessful);
		traceInfo = sylInfo.daughterInfo;
		assertEquals(0, traceInfo.size());

	}
}
