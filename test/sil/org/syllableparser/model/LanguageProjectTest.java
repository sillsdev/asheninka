/**
 * 
 */
package sil.org.syllableparser.model;

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
public class LanguageProjectTest {
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
		assertEquals("CV words size", 10026, words.size());
		languageProject.createNewWord("abel", sUntested);
		// 'abel' should already be there so the size should not change
		assertEquals("CV words size", 10026, words.size());
		languageProject.createNewWord("kinkos", sUntested);
		// 'kinkos' should not already be there so the size should change
		assertEquals("CV words size", 10027, words.size());
		Word lastWordAdded = words.get(words.size()-1);
		assertEquals("kinkos", lastWordAdded.getWord());
	}
}
