// Copyright (c) 2016-2022 SIL International
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.importexport;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.service.importexport.FLExExportedWordformsAsTabbedListImporter;
import org.sil.syllableparser.service.importexport.ListWordImporter;
import org.sil.syllableparser.service.importexport.ParaTExtExportedWordListImporter;
import org.sil.syllableparser.service.importexport.ParaTExtHyphenatedWordsImporter;

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
		File file = new File("src/org/sil/syllableparser/resources/starterFile.ashedata");
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
		String sFileName = "test/org/sil/syllableparser/testData/NLNSP1All20151030.txt";
		assertEquals(0, languageProject.getWords().size());
		ListWordImporter importer = new ListWordImporter(languageProject);
		File file = new File(sFileName);
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
		file = new File(sFileName);
		importer.importWords(file, sUntested);
		assertEquals(26, languageProject.getWords().size());
	}

	@Test
	public void ImportFLExExportedWordformsAsTabbedListTest() {
		String sUntested = "Untested";
		String sFileName = "test/org/sil/syllableparser/testData/FLExExportedWordformsAsTabbedList.txt";
		assertEquals(0, languageProject.getWords().size());
		FLExExportedWordformsAsTabbedListImporter importer = new FLExExportedWordformsAsTabbedListImporter(
				languageProject);
		File file = new File(sFileName);
		importer.importWords(file, sUntested);
		assertEquals(690, languageProject.getWords().size());
		ObservableList<Word> words = languageProject.getWords();
		Word word = words.get(0);
		assertEquals("p\u0268x", word.getWord());
		assertEquals("", word.getCorrectSyllabification());
		assertEquals(sUntested, word.getCVParserResult());
		word = words.get(1);
		assertEquals("n\u0268\ua78b", word.getWord());
		assertEquals("", word.getCorrectSyllabification());
		assertEquals(sUntested, word.getCVParserResult());
		word = words.get(2);
		assertEquals("kio", word.getWord());
		assertEquals("", word.getCorrectSyllabification());
		assertEquals(sUntested, word.getCVParserResult());
		word = words.get(3);
		assertEquals("ka\uf26a", word.getWord());
		assertEquals("", word.getCorrectSyllabification());
		assertEquals(sUntested, word.getCVParserResult());
		word = words.get(21);
		assertEquals("ñio\uf26aki", word.getWord());
		assertEquals("", word.getCorrectSyllabification());
		assertEquals(sUntested, word.getCVParserResult());
		// now load it again: the size should not change
		file = new File(sFileName);
		importer.importWords(file, sUntested);
		assertEquals(690, languageProject.getWords().size());
	}

	@Test
	public void ImportParaTExtHyphenatedWordsTest() {
		String sUntested = "Untested";
		String sFileName = "test/org/sil/syllableparser/testData/hyphenatedWords.txt";
		assertEquals(0, languageProject.getWords().size());
		ParaTExtHyphenatedWordsImporter importer = new ParaTExtHyphenatedWordsImporter(
				languageProject);
		File file = new File(sFileName);
		importer.importWords(file, sUntested);
		assertEquals(Constants.PARATEXT_HYPHENATED_WORDS_PREAMBLE,
				languageProject.getParaTExtHyphenatedWordsPreamble());
		assertEquals(35, languageProject.getWords().size());
		ObservableList<Word> words = languageProject.getWords();
		Word word = words.get(0);
		assertEquals("anmokwitsashlitsitsindi", word.getWord());
		assertEquals("an.mo.kwi.tsash.li.tsi.tsin.di", word.getCorrectSyllabification());
		assertEquals(sUntested, word.getCVParserResult());
		word = words.get(1);
		assertEquals("anmonkwitashlitshithindi", word.getWord());
		assertEquals("an.mon.kwi.tash.li.tshit.hin.di", word.getCorrectSyllabification());
		assertEquals(sUntested, word.getCVParserResult());
		word = words.get(19);
		assertEquals("Abimelech", word.getWord());
		assertEquals("", word.getCorrectSyllabification());
		assertEquals(sUntested, word.getCVParserResult());
		// now load it again: the size should not change
		file = new File(sFileName);
		importer.importWords(file, sUntested);
		assertEquals(35, languageProject.getWords().size());
	}

	@Test
	public void ImportParaTExtHyphenatedWordsNHSTest() {
		String sUntested = "Untested";
		String sFileName = "test/org/sil/syllableparser/testData/hyphenatedWordsNHS.txt";
		assertEquals(0, languageProject.getWords().size());
		ParaTExtHyphenatedWordsImporter importer = new ParaTExtHyphenatedWordsImporter(
				languageProject);
		File file = new File(sFileName);
		importer.importWords(file, sUntested);
		assertEquals(Constants.PARATEXT_HYPHENATED_WORDS_PREAMBLE,
				languageProject.getParaTExtHyphenatedWordsPreamble());
		assertEquals(2098, languageProject.getWords().size());
		ObservableList<Word> words = languageProject.getWords();
		Word word = words.get(36);
		assertEquals("ajkotzikuintiv", word.getWord());
		assertEquals("aj.ko.tzi.kuin.tiv", word.getCorrectSyllabification());
		assertEquals(sUntested, word.getCVParserResult());
		word = words.get(19);
		assertEquals("achto", word.getWord());
		assertEquals("", word.getCorrectSyllabification());
		assertEquals(sUntested, word.getCVParserResult());
		// now load it again: the size should not change
		file = new File(sFileName);
		importer.importWords(file, sUntested);
		assertEquals(2098, languageProject.getWords().size());
	}

	// For ParaText version 7.5
	@Test
	public void ImportParaTExtExportedWordList75Test() {
		String sUntested = "Untested";
		String sFileName = "test/org/sil/syllableparser/testData/ParaTExt7.5ExportedWordList.xml";
		assertEquals(0, languageProject.getWords().size());
		ParaTExtExportedWordListImporter importer = new ParaTExtExportedWordListImporter(
				languageProject);
		File file = new File(sFileName);
		importer.importWords(file, sUntested);
		assertEquals(24, languageProject.getWords().size());
		ObservableList<Word> words = languageProject.getWords();
		Word word = words.get(0);
		assertEquals("abel", word.getWord());
		assertEquals("", word.getCorrectSyllabification());
		assertEquals(sUntested, word.getCVParserResult());
		word = words.get(1);
		assertEquals("abida", word.getWord());
		assertEquals("", word.getCorrectSyllabification());
		assertEquals(sUntested, word.getCVParserResult());
		word = words.get(19);
		assertEquals("ahwisti", word.getWord());
		assertEquals("", word.getCorrectSyllabification());
		assertEquals(sUntested, word.getCVParserResult());
		// now load it again: the size should not change
		file = new File(sFileName);
		importer.importWords(file, sUntested);
		assertEquals(24, languageProject.getWords().size());
	}

	// For ParaText version 7.6
	@Test
	public void ImportParaTExtExportedWordList76Test() {
		String sUntested = "Untested";
		String sFileName = "test/org/sil/syllableparser/testData/ParaTExt7.6ExportedWordList.xml";
		assertEquals(0, languageProject.getWords().size());
		ParaTExtExportedWordListImporter importer = new ParaTExtExportedWordListImporter(
				languageProject);
		File file = new File(sFileName);
		importer.importWords(file, sUntested);
		assertEquals(24, languageProject.getWords().size());
		ObservableList<Word> words = languageProject.getWords();
		Word word = words.get(0);
		assertEquals("a", word.getWord());
		assertEquals("a", word.getCorrectSyllabification());
		assertEquals(sUntested, word.getCVParserResult());
		word = words.get(1);
		assertEquals("aarón", word.getWord());
		assertEquals("Aa.rón", word.getCorrectSyllabification());
		assertEquals(sUntested, word.getCVParserResult());
		word = words.get(19);
		assertEquals("abi\ua78c", word.getWord());
		assertEquals("", word.getCorrectSyllabification());
		assertEquals(sUntested, word.getCVParserResult());
		// now load it again: the size should not change
		file = new File(sFileName);
		importer.importWords(file, sUntested);
		assertEquals(24, languageProject.getWords().size());
	}
}
