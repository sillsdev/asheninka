/**
 * 
 */
package sil.org.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.Word;

/**
 * @author Andy Black
 *
 */
public class WordImporterTest {
	LanguageProject languageProject;
	ResourceBundle bundle;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File("src/sil/org/syllableparser/resources/starterFile.ashedata");
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
	public void importWordsTest() {
		String sUntested = "Untested";
		assertEquals(0, languageProject.getWords().size());
		ListWordImporter importer = new ListWordImporter(languageProject);
		File file = new File("test/sil/org/syllableparser/testData/NLNSP1All20151030.txt");
		importer.importWords(file, sUntested);
		assertEquals(26, languageProject.getWords().size());
		ObservableList<Word> words = languageProject.getWords(); 
		Word word = words.get(0);
		assertEquals("a", word.getWord());
		word = words.get(1);
		assertEquals("aaah", word.getWord());
		word = words.get(19);
		assertEquals("aceite", word.getWord());
		// now load it again: the size should not change
		file = new File("test/sil/org/syllableparser/testData/NLNSP1All20151030.txt");
		importer.importWords(file, sUntested);
		assertEquals(26, languageProject.getWords().size());
	}

	@Test
	public void ImportParaTExtHyphenatedWordsTest() {
		String sUntested = "Untested";
		assertEquals(0, languageProject.getWords().size());
		ParaTExtHyphenatedWordsImporter importer = new ParaTExtHyphenatedWordsImporter(languageProject);
		File file = new File("test/sil/org/syllableparser/testData/hyphenatedWords.txt");
		importer.importWords(file, sUntested);
//		System.out.println("got:");
//		System.out.print(languageProject.getParaTExtHyphenatedWordsPreamble());
//		System.out.println("expected:");
//		System.out.print(Constants.PARATEXT_HYPHENATED_WORDS_PREAMBLE);
		assertEquals(Constants.PARATEXT_HYPHENATED_WORDS_PREAMBLE,
				languageProject.getParaTExtHyphenatedWordsPreamble());
		assertEquals(35, languageProject.getWords().size());
		ObservableList<Word> words = languageProject.getWords(); 
		Word word = words.get(0);
		assertEquals("anmokwitsashlitsitsindi", word.getWord());
		assertEquals("an.mo.kwi.tsash.li.tsi.tsin.di", word.getCorrectSyllabification());
		word = words.get(1);
		assertEquals("anmonkwitashlitshithindi", word.getWord());
		assertEquals("an.mon.kwi.tash.li.tshit.hin.di", word.getCorrectSyllabification());
		word = words.get(19);
		assertEquals("Abimelech", word.getWord());
		assertEquals("", word.getCorrectSyllabification());
		// now load it again: the size should not change
		file = new File("test/sil/org/syllableparser/testData/hyphenatedWords.txt");
		importer.importWords(file, sUntested);
		assertEquals(35, languageProject.getWords().size());
	}
}