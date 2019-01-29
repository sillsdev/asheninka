// Copyright (c) 2018-2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.parsing;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Locale;

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
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHTraceSyllabifierInfo;
import org.sil.syllableparser.service.parsing.CVSegmenter;
import org.sil.syllableparser.service.parsing.CVSegmenterResult;
import org.sil.syllableparser.service.parsing.SHSyllabifier;

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
		shSyllabifier = new SHSyllabifier(sonHierApproach);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void wordToSegmentToSyllableTest() {
		checkSyllabification("", false, 0, "");
		checkSyllabification("b", false, 0, "");
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
		checkSyllabification("ibabe", false, 0, ""); // b not in hierarchy
	}

	protected void checkSyllabification(String word, boolean success, int numberOfSyllables,
			String expectedSyllabification) {
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		boolean fSuccess = segResult.success;
		List<CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		fSuccess = shSyllabifier.syllabify(segmentsInWord);
		assertEquals("word syllabified", success, fSuccess);
		List<SHSyllable> syllablesInWord = shSyllabifier.getSyllablesInCurrentWord();
		assertEquals("Expect " + numberOfSyllables + " syllables in word", numberOfSyllables,
				syllablesInWord.size());
		assertEquals("Expected Syllabification of word", expectedSyllabification,
				shSyllabifier.getSyllabificationOfCurrentWord());
	}

	protected void checkSyllabifyWord(String word, boolean success, String expectedNaturalClasses,
			String expectedSonorityValues, int numberOfSyllables, String expectedSyllabification) {
		boolean fSuccess = shSyllabifier.convertStringToSyllables(word);
		assertEquals("word syllabified", success, fSuccess);
		String naturalClassesInWord = shSyllabifier.getNaturalClassesInCurrentWord();
		assertEquals(expectedNaturalClasses, naturalClassesInWord);
		String sonorityValues = shSyllabifier.getSonorityValuesInCurrentWord();
		assertEquals(expectedSonorityValues, sonorityValues);
		List<SHSyllable> syllablesInWord = shSyllabifier.getSyllablesInCurrentWord();
		assertEquals("Expect " + numberOfSyllables + " syllables in word", numberOfSyllables,
				syllablesInWord.size());
		assertEquals("Expected Syllabification of word", expectedSyllabification,
				shSyllabifier.getSyllabificationOfCurrentWord());
	}

	@Test
	public void traceSyllabifyWordTest() {
		shSyllabifier.setDoTrace(true);

		checkSyllabifyWord("", false, "", "", 0, "");
		List<SHTraceSyllabifierInfo> traceInfo = shSyllabifier.getSyllabifierTraceInfo();
		assertEquals(0, traceInfo.size());

		checkSyllabifyWord("A", true, "Vowels, null",  SHTraceSyllabifierInfo.NULL_REPRESENTATION, 1, "A");
		traceInfo = shSyllabifier.getSyllabifierTraceInfo();
		assertEquals(1, traceInfo.size());
		SHTraceSyllabifierInfo sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);

		checkSyllabifyWord("ta", true, "Obstruents, Vowels", "<", 1, "ta");
		traceInfo = shSyllabifier.getSyllabifierTraceInfo();
		assertEquals(1, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);

		checkSyllabifyWord("tad", true, "Obstruents, Vowels, Obstruents", "<, >", 1, "tad");
		traceInfo = shSyllabifier.getSyllabifierTraceInfo();
		assertEquals(2, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);

		checkSyllabifyWord("Chiko", true, "Obstruents, Vowels, Obstruents, Vowels",
				"<, >, <", 2, "Chi.ko");
		traceInfo = shSyllabifier.getSyllabifierTraceInfo();
		assertEquals(3, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(true, sylInfo.startsSyllable);

		checkSyllabifyWord("tampidon", true,
				"Obstruents, Vowels, Nasals, Obstruents, Vowels, Obstruents, Vowels, Nasals",
				"<, >, >, <, >, <, >", 3, "tam.pi.don");
		traceInfo = shSyllabifier.getSyllabifierTraceInfo();
		assertEquals(7, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(3);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(4);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(5);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(6);
		assertEquals(false, sylInfo.startsSyllable);

		checkSyllabifyWord("dapgek", true,
				"Obstruents, Vowels, Obstruents, Obstruents, Vowels, Obstruents",
				"<, >, =, <, >", 2, "dap.gek");
		traceInfo = shSyllabifier.getSyllabifierTraceInfo();
		assertEquals(5, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(3);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(4);
		assertEquals(false, sylInfo.startsSyllable);

		checkSyllabifyWord("dapkgek", true,
				"Obstruents, Vowels, Obstruents, Obstruents, Obstruents, Vowels, Obstruents",
				"<, >, =, =, <, >", 2, "dap.kgek");
		traceInfo = shSyllabifier.getSyllabifierTraceInfo();
		assertEquals(6, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(3);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(4);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(5);
		assertEquals(false, sylInfo.startsSyllable);

		checkSyllabifyWord("dovdek", true,
				"Obstruents, Vowels, Obstruents, Obstruents, Vowels, Obstruents",
				"<, >, =, <, >", 2, "dov.dek");
		traceInfo = shSyllabifier.getSyllabifierTraceInfo();
		assertEquals(5, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(3);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(4);
		assertEquals(false, sylInfo.startsSyllable);

		checkSyllabifyWord("fuhgt", true, "Obstruents, Vowels, Obstruents, Obstruents, Obstruents",
				"<, >, =, =", 2, "fuh.gt");
		traceInfo = shSyllabifier.getSyllabifierTraceInfo();
		assertEquals(4, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(3);
		assertEquals(true, sylInfo.startsSyllable);

		checkSyllabifyWord("dlofugh", true,
				"Obstruents, Liquids, Vowels, Obstruents, Vowels, Obstruents, Obstruents, null",
				"<, <, >, <, >, =, " + SHTraceSyllabifierInfo.NULL_REPRESENTATION, 3, "dlo.fug.h");
		traceInfo = shSyllabifier.getSyllabifierTraceInfo();
		assertEquals(7, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(3);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(4);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(5);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(6);
		assertEquals(true, sylInfo.startsSyllable);

		checkSyllabifyWord("do", true, "Obstruents, Vowels", "<", 1, "do");
		traceInfo = shSyllabifier.getSyllabifierTraceInfo();
		assertEquals(1, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);

		checkSyllabifyWord("funglo", true,
				"Obstruents, Vowels, Nasals, Obstruents, Liquids, Vowels",
				"<, >, >, <, <", 2, "fun.glo");
		traceInfo = shSyllabifier.getSyllabifierTraceInfo();
		assertEquals(5, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(3);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(4);
		assertEquals(false, sylInfo.startsSyllable);

		checkSyllabifyWord("fugh", true, "Obstruents, Vowels, Obstruents, Obstruents, null",
				"<, >, =, " + SHTraceSyllabifierInfo.NULL_REPRESENTATION, 2, "fug.h");
		traceInfo = shSyllabifier.getSyllabifierTraceInfo();
		assertEquals(4, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(3);
		assertEquals(true, sylInfo.startsSyllable);

		checkSyllabifyWord("flu", true, "Obstruents, Liquids, Vowels", "<, <", 1, "flu");
		traceInfo = shSyllabifier.getSyllabifierTraceInfo();
		assertEquals(2, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);

		checkSyllabifyWord("fluk", true, "Obstruents, Liquids, Vowels, Obstruents",
				"<, <, >", 1, "fluk");
		traceInfo = shSyllabifier.getSyllabifierTraceInfo();
		assertEquals(3, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);
		assertEquals(false, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(2);
		assertEquals(false, sylInfo.startsSyllable);

		checkSyllabifyWord("iae", true, "Vowels, Vowels, Vowels", "=, =", 1, "iae");
		traceInfo = shSyllabifier.getSyllabifierTraceInfo();
		assertEquals(2, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
		sylInfo = traceInfo.get(1);

		checkSyllabifyWord("babe", false, "null, null", "!!!", 0, ""); // b not in hierarchy
		traceInfo = shSyllabifier.getSyllabifierTraceInfo();
		assertEquals(1, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(false, sylInfo.startsSyllable);

		checkSyllabifyWord("ibabe", false, "Vowels, null", "!!!", 0, ""); // b not in hierarchy
		traceInfo = shSyllabifier.getSyllabifierTraceInfo();
		assertEquals(1, traceInfo.size());
		sylInfo = traceInfo.get(0);
		assertEquals(true, sylInfo.startsSyllable);
	}
}
