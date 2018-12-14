// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.domain;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Approach;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.cvapproach.*;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter and CVNaturalClasser
 *         classes are functioning correctly
 */
public class CVApproachTest {

	Approach cva;
	ObservableList<Word> words;
	
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
		words = languageProject.getWords();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getHyphenatedWordsTest() {
		assertEquals("Words size", 10025, words.size());
		ArrayList<String> hyphenatedWords = cva.getHyphenatedWordsListWord(words);
		assertEquals("Hyphenated words size", 1903, hyphenatedWords.size());
		String sHyphenatedWord = hyphenatedWords.get(0);
		assertEquals("abba\ua78c = ab=ba\ua78c", "ab=ba\ua78c", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords.get(1);
		assertEquals("ababrastro = a=ba=bras=tro", "a=ba=bras=tro", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords.get(2);
		assertEquals("babel = ba=bel", "ba=bel", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords.get(3);
		assertEquals("baka = ba=ka", "ba=ka", sHyphenatedWord);
		
	}

}
