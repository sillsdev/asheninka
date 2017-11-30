// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.view.JavaFXThreadingRule;

/**
 * @author Andy Black
 *
 */
public class LanguageProjectTest {
	LanguageProject languageProject;
	ResourceBundle bundle;
	
	@Rule
	public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

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
		bundle = ResourceBundle.getBundle(Constants.RESOURCE_LOCATION);
		}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void createNewWordTest() {
		String sUntested = "Untested";
		ObservableList<Word> words = languageProject.getWords();
		assertEquals("CV words size", 10025, words.size());
		languageProject.createNewWord("abel", sUntested);
		// 'abel' should already be there so the size should not change
		assertEquals("CV words size", 10025, words.size());
		languageProject.createNewWord("kinkos", sUntested);
		// 'kinkos' should not already be there so the size should change
		assertEquals("CV words size", 10026, words.size());
		Word lastWordAdded = words.get(words.size()-1);
		assertEquals("kinkos", lastWordAdded.getWord());
	}
	
	@Test
	public void getActiveSegmentsTest() {
		assertEquals("Segment inventory size", 27, languageProject.getSegmentInventory().size());
		assertEquals("Active segment inventory size", 26, languageProject.getActiveSegmentsInInventory().size());
	}
	
	@Test
	public void getHyphenationParametersTest() {
		assertEquals("list word hyphenation string", "=", languageProject.getHyphenationParametersListWord().getDiscretionaryHyphen());
		assertEquals("list word start", 0, languageProject.getHyphenationParametersListWord().getStartAfterCharactersFromBeginning());
		assertEquals("list word stop", 0, languageProject.getHyphenationParametersListWord().getStopBeforeCharactersFromEnd());
		
		assertEquals("ParaTExt hyphenation string", "=", languageProject.getHyphenationParametersParaTExt().getDiscretionaryHyphen());
		assertEquals("ParaTExt start", 2, languageProject.getHyphenationParametersParaTExt().getStartAfterCharactersFromBeginning());
		assertEquals("ParaTExt stop", 2, languageProject.getHyphenationParametersParaTExt().getStopBeforeCharactersFromEnd());
		
		assertEquals("XLingPaper hyphenation string", "-", languageProject.getHyphenationParametersXLingPaper().getDiscretionaryHyphen());
		assertEquals("XLingPaper start", 2, languageProject.getHyphenationParametersXLingPaper().getStartAfterCharactersFromBeginning());
		assertEquals("XLingPaper stop", 2, languageProject.getHyphenationParametersXLingPaper().getStopBeforeCharactersFromEnd());
	}
}
