// Copyright (c) 2016-2017 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package sil.org.syllableparser.backendprovider;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.model.Environment;
import sil.org.syllableparser.model.Grapheme;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.Word;
import sil.org.syllableparser.model.cvapproach.CVApproach;

/**
 * @author Andy Black
 *
 */
public class XMLBackEndProviderTest {

	XMLBackEndProvider xmlBackEndProvider;
	LanguageProject languageProject;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void loadLanguageDataFromFileTest() {
		checkLoadedData();
	}

	public void checkLoadedData() {
		languageProject = xmlBackEndProvider.getLanguageProject();
		assertNotNull(languageProject);
		assertEquals(0, languageProject.getDatabaseVersion());
		CVApproach cva = languageProject.getCVApproach();
		assertEquals(27, languageProject.getSegmentInventory().size());
		assertEquals(6, cva.getCVNaturalClasses().size());
		assertEquals(9, cva.getCVSyllablePatterns().size());
		ObservableList<Word> words = languageProject.getWords();
		assertEquals(10025, words.size());
		ObservableList<Environment> environments = languageProject.getEnvironments();
		assertEquals(0, environments.size());
		ObservableList<Grapheme> graphemes = languageProject.getGraphemes();
		assertEquals(0, graphemes.size());
	}

	@Test
	public void saveLanguageDataToFileTest() {
		File tempSaveFile = null;
		try {
			tempSaveFile = File.createTempFile("AsheninkaTestSave", ".ashedata");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (tempSaveFile != null) {
			tempSaveFile.deleteOnExit();
		}
		xmlBackEndProvider.saveLanguageDataToFile(tempSaveFile);
		xmlBackEndProvider.loadLanguageDataFromFile(tempSaveFile);
		checkLoadedData();
	}
}
