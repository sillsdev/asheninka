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
import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.cvapproach.*;
import sil.org.syllableparser.model.LanguageProject;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter and CVNaturalClasser
 *         classes are functioning correctly
 */
public class CVApproachTest {

	CVApproach cva;
	ObservableList<CVWord> cvWords;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		LanguageProject languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File("test/sil/org/syllableparser/testData/CVTestData.sylpdata");
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		cva = languageProject.getCVApproach();
		cvWords = cva.getCVWords();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// make sure the setup is what we expect
	@Test
	public void getHyphenatedWordsTest() {
		assertEquals("CV words size", 10026, cvWords.size());
		ArrayList<String> hyphenatedWords = cva.getHyphenatedWords();
		assertEquals("Hyphenated words size", 1822, hyphenatedWords.size());
		String sHyphenatedWord = hyphenatedWords.get(0);
		assertEquals("abba = ab=ba", "ab=ba", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords.get(1);
		assertEquals("ababrastro = a=ba=bras=tro", "a=ba=bras=tro", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords.get(2);
		assertEquals("babel = ba=bel", "ba=bel", sHyphenatedWord);
		sHyphenatedWord = hyphenatedWords.get(3);
		assertEquals("baka = ba=ka", "ba=ka", sHyphenatedWord);
		
	}



}
