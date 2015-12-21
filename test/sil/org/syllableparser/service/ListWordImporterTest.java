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

import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.Word;

/**
 * @author Andy Black
 *
 */
public class ListWordImporterTest {
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
		File file = new File("src/sil/org/syllableparser/resources/starterFile.sylpdata");
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		bundle = ResourceBundle.getBundle("sil.org.syllableparser.resources.SyllableParser");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void importWordsTest() {
		assertEquals(0, languageProject.getWords().size());
		ListWordImporter importer = new ListWordImporter(null, languageProject, "", "");
		File file = new File("test/sil/org/syllableparser/testData/NLNSP1All20151030.txt");
		importer.importWordsFromFile(file, bundle);
		assertEquals(10026, languageProject.getWords().size());
		ObservableList<Word> words = languageProject.getWords(); 
		Word word = words.get(0);
		assertEquals("a", word.getWord());
		word = words.get(1);
		assertEquals("aaah", word.getWord());
		word = words.get(19);
		assertEquals("aceite", word.getWord());
		// now load it again: the size should not change
		file = new File("test/sil/org/syllableparser/testData/NLNSP1All20151030.txt");
		importer.importWordsFromFile(file, bundle);
		assertEquals(10026, languageProject.getWords().size());
	}

}
