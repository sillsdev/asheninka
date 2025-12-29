// Copyright (c) 2025 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.parsing;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
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
import org.sil.syllableparser.model.hyphenapproach.HyphenApproach;
import org.sil.syllableparser.model.hyphenapproach.HyphenClass;
import org.sil.syllableparser.model.hyphenapproach.HyphenClassInWord;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter class is functioning correctly
 */
public class HyphenClasserTest {

	HyphenApproach cva;
	ObservableList<HyphenClass> hyphenClasses;
	CVSegmenter segmenter;
	ObservableList<Segment> segmentInventory;
	List<Grapheme> activeGraphemes;
	HyphenClasser hyphenClasser;
	private List<HyphenClassInWord> classesInWord = new ArrayList<HyphenClassInWord>();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		LanguageProject languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File("test/org/sil/syllableparser/testData/hyphen.ashedata");
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		cva = languageProject.getHyphenApproach();
		segmentInventory = languageProject.getSegmentInventory();
		activeGraphemes = languageProject.getActiveGraphemes();
		segmenter = new CVSegmenter(activeGraphemes,
				languageProject.getActiveGraphemeNaturalClasses());
		hyphenClasses = cva.getHyphenClasses();
		hyphenClasser = new HyphenClasser(cva);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// make sure the setup is what we expect
	@Test
	public void classerTest() {
		assertEquals("Segment inventory size", 27, segmentInventory.size());
		assertEquals("Hyphen classes size", 3, hyphenClasses.size());
	}

	@Test
	public void wordToSegmentToHyphenClassesTest() {
		checkClasserResults("", false, 0, "");
		checkClasserResults("a", true, 3, "#, V, #");
		checkClasserResults("d", true, 3, "#, C, #");
		checkClasserResults("n", true, 3, "#, N, #");
		checkClasserResults("Chiko", true, 6, "#, C, V, C, V, #");
		checkClasserResults("dapbek", true, 8, "#, C, V, C, C, V, C, #");
		checkClasserResults("bampidon", true, 10, "#, C, V, N, C, V, C, V, N, #");
		checkClasserResults("bovdek", true, 8, "#, C, V, C, C, V, C, #");
		checkClasserResults("fuhgt", true, 7, "#, C, V, C, C, C, #");
		checkClasserResults("blofugh", true, 9, "#, C, C, V, C, V, C, C, #");
		checkClasserResults("bo", true, 4, "#, C, V, #");
	}

	protected void checkClasserResults(String word, boolean success, int numberOfClasses,
			String expectedClasses) {
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		boolean fSuccess = segResult.success;
		List<? extends CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		HyphenClasserResult ncResult = hyphenClasser.parseIntoHyphenClasses(segmentsInWord);
		fSuccess = ncResult.success;
		assertEquals("word classed into hyphen classes", success, fSuccess);
		classesInWord = hyphenClasser.getClassesInWord();
		assertEquals("Expect " + numberOfClasses + " classes in word", numberOfClasses,
				classesInWord.size());
		assertEquals("Expected class sequence", expectedClasses, ncResult.sClasses);
	}
}
