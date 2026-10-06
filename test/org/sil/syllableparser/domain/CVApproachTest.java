// Copyright (c) 2016-2025 SIL International 
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
import org.sil.syllableparser.model.HyphenationParametersListWord;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.service.parsing.CVSegmenter;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter and CVNaturalClasser
 *         classes are functioning correctly
 */
public class CVApproachTest {

	Approach cva;
	ObservableList<Word> words;
	LanguageProject languageProject;
	CVSegmenter segmenter;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		languageProject = new LanguageProject();
		Locale locale = Locale.of("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		cva = languageProject.getCVApproach();
		words = languageProject.getWords();
		segmenter = new CVSegmenter(languageProject.getActiveGraphemes(), languageProject.getActiveGraphemeNaturalClasses());
		cva.setSegmenter(segmenter);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getHyphenatedWordsTest() {
		String sHyphenatedWord = "";
		HyphenationParametersListWord hypLW = languageProject.getHyphenationParametersListWord();
		hypLW.setfCountSegments(false);
		hypLW.setStartAfterCharactersFromBeginning(0);
		hypLW.setStopBeforeCharactersFromEnd(0);
		assertEquals("Words size", 10025, words.size());
		ArrayList<String> hyphenatedWords0 = cva.getHyphenatedWordsListWord(words);
		assertEquals("Hyphenated words size", 1903, hyphenatedWords0.size());
		sHyphenatedWord = hyphenatedWords0.get(0);
		assertEquals("abba\ua78c = ab=ba\ua78c", "ab=ba\ua78c", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords0.get(1);
		assertEquals("ababrastro = a=ba=bras=tro", "a=ba=bras=tro", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords0.get(2);
		assertEquals("babel = ba=bel", "ba=bel", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords0.get(3);
		assertEquals("baka = ba=ka", "ba=ka", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords0.get(42);
		assertEquals("chichiltik = chi=chil=tik", "chi=chil=tik", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords0.get(1081);
		assertEquals("shiktapach = shik=ta=pach", "shik=ta=pach", sHyphenatedWord);
		
		hypLW.setfCountSegments(false);
		hypLW.setStartAfterCharactersFromBeginning(2);
		hypLW.setStopBeforeCharactersFromEnd(2);
		languageProject.setHyphenationParametersListWord(hypLW);
		ArrayList<String> hyphenatedWords2 = cva.getHyphenatedWordsListWord(words);
		assertEquals("Hyphenated words size", 1903, hyphenatedWords2.size());
		sHyphenatedWord = hyphenatedWords2.get(0);
		assertEquals("abba\ua78c = ab=ba\ua78c", "ab=ba\ua78c", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords2.get(1);
		assertEquals("ababrastro = aba=bras=tro", "aba=bras=tro", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords2.get(2);
		assertEquals("babel = ba=bel", "ba=bel", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords2.get(3);
		assertEquals("baka = ba=ka", "ba=ka", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords2.get(42);
		assertEquals("chichiltik = chi=chil=tik", "chi=chil=tik", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords2.get(1081);
		assertEquals("shiktapach = shik=ta=pach", "shik=ta=pach", sHyphenatedWord);

		hypLW.setfCountSegments(false);
		hypLW.setStartAfterCharactersFromBeginning(3);
		hypLW.setStopBeforeCharactersFromEnd(3);
		languageProject.setHyphenationParametersListWord(hypLW);
		ArrayList<String> hyphenatedWords3 = cva.getHyphenatedWordsListWord(words);
		assertEquals("Hyphenated words size", 1903, hyphenatedWords3.size());
		System.out.println(hyphenatedWords3);
		sHyphenatedWord = hyphenatedWords3.get(0);
		assertEquals("abba\ua78c = abba\ua78c", "abba\ua78c", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords3.get(1);
		assertEquals("ababrastro = aba=bras=tro", "aba=bras=tro", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords3.get(2);
		assertEquals("babel = babel", "babel", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords3.get(3);
		assertEquals("baka = baka", "baka", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords3.get(42);
		assertEquals("chichiltik = chi=chil=tik", "chi=chil=tik", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords3.get(1081);
		assertEquals("shiktapach = shik=ta=pach", "shik=ta=pach", sHyphenatedWord);

		hypLW.setfCountSegments(false);
		hypLW.setStartAfterCharactersFromBeginning(4);
		hypLW.setStopBeforeCharactersFromEnd(4);
		languageProject.setHyphenationParametersListWord(hypLW);
		ArrayList<String> hyphenatedWords4 = cva.getHyphenatedWordsListWord(words);
		assertEquals("Hyphenated words size", 1903, hyphenatedWords4.size());
		System.out.println(hyphenatedWords4);
		sHyphenatedWord = hyphenatedWords4.get(0);
		assertEquals("abba\ua78c = abba\ua78c", "abba\ua78c", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords4.get(1);
		assertEquals("ababrastro = aba=bras=tro", "ababrastro", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords4.get(2);
		assertEquals("babel = babel", "babel", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords4.get(3);
		assertEquals("baka = baka", "baka", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords4.get(42);
		assertEquals("chichiltik = chi=chil=tik", "chichiltik", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords4.get(1081);
		assertEquals("shiktapach = shik=ta=pach", "shik=ta=pach", sHyphenatedWord);

		hypLW.setfCountSegments(false);
		hypLW.setStartAfterCharactersFromBeginning(5);
		hypLW.setStopBeforeCharactersFromEnd(5);
		languageProject.setHyphenationParametersListWord(hypLW);
		ArrayList<String> hyphenatedWords5 = cva.getHyphenatedWordsListWord(words);
		assertEquals("Hyphenated words size", 1903, hyphenatedWords5.size());
		System.out.println(hyphenatedWords5);
		sHyphenatedWord = hyphenatedWords5.get(0);
		assertEquals("abba\ua78c = abba\ua78c", "abba\ua78c", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords5.get(1);
		assertEquals("ababrastro = aba=bras=tro", "ababrastro", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords5.get(2);
		assertEquals("babel = babel", "babel", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords5.get(3);
		assertEquals("baka = baka", "baka", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords5.get(42);
		assertEquals("chichiltik = chi=chil=tik", "chichiltik", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords5.get(1081);
		assertEquals("shiktapach = shik=ta=pach", "shiktapach", sHyphenatedWord);

		hypLW.setfCountSegments(true);
		hypLW.setStartAfterCharactersFromBeginning(3);
		hypLW.setStopBeforeCharactersFromEnd(3);
		hypLW.setStartAfterSegmentsFromBeginning(3);
		hypLW.setStopBeforeSegmentsFromEnd(3);
		languageProject.setHyphenationParametersListWord(hypLW);
		ArrayList<String> hyphenatedWordsSeg3 = cva.getHyphenatedWordsListWord(words);
		assertEquals("Hyphenated2 words size", 1903, hyphenatedWordsSeg3.size());
		System.out.println(hyphenatedWordsSeg3);
		sHyphenatedWord = hyphenatedWordsSeg3.get(0);
		assertEquals("abba\ua78c = abba\ua78c", "abba\ua78c", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWordsSeg3.get(1);
		assertEquals("ababrastro = aba=bras=tro", "aba=bras=tro", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWordsSeg3.get(2);
		assertEquals("babel = babel", "babel", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWordsSeg3.get(3);
		assertEquals("baka = baka", "baka", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWordsSeg3.get(42);
		assertEquals("chichiltik = chichil=tik", "chichil=tik", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWordsSeg3.get(1081);
		assertEquals("shiktapach = shik=ta=pach", "shik=ta=pach", sHyphenatedWord);

		hypLW.setfCountSegments(true);
		hypLW.setStartAfterCharactersFromBeginning(4);
		hypLW.setStopBeforeCharactersFromEnd(4);
		hypLW.setStartAfterSegmentsFromBeginning(4);
		hypLW.setStopBeforeSegmentsFromEnd(4);
		languageProject.setHyphenationParametersListWord(hypLW);
		ArrayList<String> hyphenatedWordsSeg4 = cva.getHyphenatedWordsListWord(words);
		assertEquals("Hyphenated2 words size", 1903, hyphenatedWordsSeg4.size());
		System.out.println(hyphenatedWordsSeg4);
		sHyphenatedWord = hyphenatedWordsSeg4.get(0);
		assertEquals("abba\ua78c = abba\ua78c", "abba\ua78c", sHyphenatedWord);
		String habby = cva.getHyphenatedWord(hypLW, "a.ba.bras.tro", "ababrastro");
		sHyphenatedWord = hyphenatedWordsSeg4.get(1);
		assertEquals("ababrastro = aba=bras=tro", "ababrastro", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWordsSeg4.get(2);
		assertEquals("babel = babel", "babel", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWordsSeg4.get(3);
		assertEquals("baka = baka", "baka", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWordsSeg4.get(42);
		assertEquals("chichiltik = chichiltik", "chichiltik", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWordsSeg4.get(1081);
		assertEquals("shiktapach = shik=ta=pach", "shiktapach", sHyphenatedWord);
		// chichiltik
	}

}
