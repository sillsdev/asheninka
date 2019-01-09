// Copyright (c) 2016-2019 SIL International 
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
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHTraceSyllabifierInfo;
import org.sil.syllableparser.service.CVSegmenter;
import org.sil.syllableparser.service.CVSegmenterResult;

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
		System.out.println("syls=" + shSyllabifier.getSyllabificationOfCurrentWord());
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
		checkSyllabifyWord("A", true, "", "", 1, "A");
		checkSyllabifyWord("ta", true, "Obstruents, Vowels", "LESS", 1, "ta");
		checkSyllabifyWord("tad", true, "Obstruents, Vowels, Obstruents", "LESS, MORE", 1, "tad");

		checkSyllabifyWord("Chiko", true, "Obstruents, Vowels, Obstruents, Vowels",
				"LESS, MORE, LESS", 2, "Chi.ko");

		checkSyllabifyWord("tampidon", true,
				"Obstruents, Vowels, Nasals, Obstruents, Vowels, Obstruents, Vowels, Nasals",
				"LESS, MORE, MORE, LESS, MORE, LESS, MORE", 3, "tam.pi.don");

		checkSyllabifyWord("dapgek", true,
				"Obstruents, Vowels, Obstruents, Obstruents, Vowels, Obstruents",
				"LESS, MORE, EQUAL, LESS, MORE", 2, "dap.gek");
		checkSyllabifyWord("dapkgek", true,
				"Obstruents, Vowels, Obstruents, Obstruents, Obstruents, Vowels, Obstruents",
				"LESS, MORE, EQUAL, EQUAL, LESS, MORE", 2, "dap.kgek");
		checkSyllabifyWord("dovdek", true,
				"Obstruents, Vowels, Obstruents, Obstruents, Vowels, Obstruents",
				"LESS, MORE, EQUAL, LESS, MORE", 2, "dov.dek");
		checkSyllabifyWord("fuhgt", true, "Obstruents, Vowels, Obstruents, Obstruents, Obstruents",
				"LESS, MORE, EQUAL, EQUAL", 2, "fuh.gt");
		checkSyllabifyWord("dlofugh", true,
				"Obstruents, Liquids, Vowels, Obstruents, Vowels, Obstruents, Obstruents",
				"LESS, LESS, MORE, LESS, MORE, EQUAL", 3, "dlo.fug.h");
		checkSyllabifyWord("do", true, "Obstruents, Vowels", "LESS", 1, "do");
		checkSyllabifyWord("funglo", true,
				"Obstruents, Vowels, Nasals, Obstruents, Liquids, Vowels",
				"LESS, MORE, MORE, LESS, LESS", 2, "fun.glo");
		checkSyllabifyWord("fugh", true, "Obstruents, Vowels, Obstruents, Obstruents",
				"LESS, MORE, EQUAL", 2, "fug.h");
		checkSyllabifyWord("flu", true, "Obstruents, Liquids, Vowels", "LESS, LESS", 1, "flu");
		checkSyllabifyWord("fluk", true, "Obstruents, Liquids, Vowels, Obstruents",
				"LESS, LESS, MORE", 1, "fluk");
		checkSyllabifyWord("iae", true, "Vowels, Vowels, Vowels", "EQUAL, EQUAL", 1, "iae");
		checkSyllabifyWord("babe", false, "null, Vowels", "MISSING1", 0, ""); // b not in hierarchy
		checkSyllabifyWord("ibabe", false, "Vowels, null", "MISSING2", 0, ""); // b not in hierarchy
	}
}
