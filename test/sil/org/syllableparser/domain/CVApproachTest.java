/**
 * 
 */
package sil.org.syllableparser.domain;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.cvapproach.*;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.Word;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter and CVNaturalClasser
 *         classes are functioning correctly
 */
public class CVApproachTest {

	CVApproach cva;
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
		assertEquals("Words size", 10026, words.size());
		ArrayList<String> hyphenatedWords = cva.getHyphenatedWords(words);
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
