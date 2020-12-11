// Copyright (c) 2016-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.backendprovider;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Environment;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.utility.view.JavaFXThreadingRule;

/**
 * @author Andy Black
 *
 */
public class XMLBackEndProviderTest {

	XMLBackEndProvider xmlBackEndProvider;
	LanguageProject languageProject;
	@Rule
	public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

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
		assertEquals(2, languageProject.getDatabaseVersion());
		CVApproach cva = languageProject.getCVApproach();
		ObservableList<Segment> segInventory = languageProject.getSegmentInventory(); 
		assertEquals(27, segInventory.size());
		assertEquals(7, cva.getCVNaturalClasses().size());
		assertEquals(9, cva.getCVSyllablePatterns().size());
		ObservableList<Word> words = languageProject.getWords();
		assertEquals(10025, words.size());
		ObservableList<Environment> environments = languageProject.getEnvironments();
		assertEquals(4, environments.size());
//		ObservableList<Grapheme> graphemes = languageProject.getGraphemes();
//		assertEquals(56, graphemes.size());
		Segment seg = segInventory.get(0);
		assertEquals(2, seg.getGraphs().size());
		Grapheme grapheme = seg.getGraphs().get(0);
		assertEquals("a", grapheme.getForm());
		grapheme = seg.getGraphs().get(1);
		assertEquals("A", grapheme.getForm());
		seg = segInventory.get(2);
		assertEquals(3, seg.getGraphs().size());
		grapheme = seg.getGraphs().get(0);
		assertEquals("ch", grapheme.getForm());
		grapheme = seg.getGraphs().get(1);
		assertEquals("Ch", grapheme.getForm());
		grapheme = seg.getGraphs().get(2);
		assertEquals("CH", grapheme.getForm());
	}

	@Test
	public void saveLanguageDataToFileTest() {
		File tempSaveFile = null;
		try {
			tempSaveFile = File.createTempFile("AsheninkaTestSave", ".ashedata");
		} catch (IOException e) {
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
