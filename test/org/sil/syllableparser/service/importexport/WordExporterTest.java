// Copyright (c) 2016-2025 SIL International
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.importexport;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Approach;
import org.sil.syllableparser.model.LanguageProject;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter and CVNaturalClasser
 *         classes are functioning correctly
 */
public class WordExporterTest {

	File tempSaveFile = null;
	LanguageProject languageProject = null;
	Approach cva;

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
		cva = languageProject.getCVApproach();
		try {
			tempSaveFile = File.createTempFile("AsheninkaTestSave",
					".ashedata");
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (tempSaveFile != null) {
			tempSaveFile.deleteOnExit();
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void exportWordsAsSimpleListTest() {
		long lineCount = 0;
		ListWordExporter exporter = new ListWordExporter(languageProject);
		exporter.exportWords(tempSaveFile, cva);
		try (Stream<String> stream = Files.lines(tempSaveFile.toPath(), StandardCharsets.UTF_8)) {
			lineCount = stream.count();
			assertEquals(1903,  lineCount);
		} catch (IOException | UncheckedIOException e1) {
			e1.printStackTrace();
		}
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(tempSaveFile),
					Constants.UTF8_ENCODING);
			BufferedReader bufr = new BufferedReader(reader);
			String line = bufr.readLine();		
			assertEquals("ab=ba\ua78c", line);
			line = bufr.readLine();
			assertEquals("a=ba=bras=tro", line);
			line = bufr.readLine();
			assertEquals("ba=bel", line);
			line = bufr.readLine();
			assertEquals("ba=ka", line);
			bufr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void exportWordsAsSimpleListHyphenationParametersTest() {
		long lineCount = 0;
		languageProject.getHyphenationParametersListWord().setDiscretionaryHyphen("-");
		languageProject.getHyphenationParametersListWord().setStartAfterCharactersFromBeginning(3);
		languageProject.getHyphenationParametersListWord().setStopBeforeCharactersFromEnd(3);
		ListWordExporter exporter = new ListWordExporter(languageProject);
		exporter.exportWords(tempSaveFile, cva);
		try (Stream<String> stream = Files.lines(tempSaveFile.toPath(), StandardCharsets.UTF_8)) {
			lineCount = stream.count();
			assertEquals(1903,  lineCount);
		} catch (IOException | UncheckedIOException e1) {
			e1.printStackTrace();
		}
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(tempSaveFile),
					Constants.UTF8_ENCODING);
			BufferedReader bufr = new BufferedReader(reader);
			String line = bufr.readLine();		
			assertEquals("abba\ua78c", line);
			line = bufr.readLine();
			assertEquals("aba-bras-tro", line);
			line = bufr.readLine();
			assertEquals("babel", line);
			line = bufr.readLine();
			assertEquals("baka", line);
			bufr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void exportWordsAsParaTExtHyphenatedWordsTest() {
		long lineCount = 0;
		ParaTExtHyphenatedWordsExporter exporter = new ParaTExtHyphenatedWordsExporter(languageProject);
		exporter.exportWords(tempSaveFile, cva);
		try (Stream<String> stream = Files.lines(tempSaveFile.toPath(), StandardCharsets.UTF_8)) {
			lineCount = stream.count();
			assertEquals(1910,  lineCount);
		} catch (IOException | UncheckedIOException e1) {
			e1.printStackTrace();
		}
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(tempSaveFile),
					Constants.UTF8_ENCODING);
			BufferedReader bufr = new BufferedReader(reader);
			// not sure why, but are getting something in the first character position
			String line = bufr.readLine();
			checkParaTExtPreambleContent(bufr, line);
			// now check for some words
			line = bufr.readLine();		
			assertEquals("*ab=ba\ua78c", line);
			line = bufr.readLine();
			assertEquals("*aba=bras=tro", line);
			line = bufr.readLine();
			assertEquals("ba=bel", line);
			line = bufr.readLine();
			assertEquals("ba=ka", line);
			bufr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	@Test
	public void exportWordsAsParaTExtHyphenatedWordsHyphenationParametersTest() {
		long lineCount = 0;
		languageProject.getHyphenationParametersParaTExt().setDiscretionaryHyphen("-");
		languageProject.getHyphenationParametersParaTExt().setStartAfterCharactersFromBeginning(3);
		languageProject.getHyphenationParametersParaTExt().setStopBeforeCharactersFromEnd(3);
		ParaTExtHyphenatedWordsExporter exporter = new ParaTExtHyphenatedWordsExporter(languageProject);
		exporter.exportWords(tempSaveFile, cva);
		try (Stream<String> stream = Files.lines(tempSaveFile.toPath(), StandardCharsets.UTF_8)) {
			lineCount = stream.count();
			assertEquals(1910,  lineCount);
		} catch (IOException | UncheckedIOException e1) {
			e1.printStackTrace();
		}
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(tempSaveFile),
					Constants.UTF8_ENCODING);
			BufferedReader bufr = new BufferedReader(reader);
			// not sure why, but are getting something in the first character position
			String line = bufr.readLine();
			checkParaTExtPreambleContent(bufr, line);
			// now check for some words
			line = bufr.readLine();		
			assertEquals("*abba\ua78c", line);
			line = bufr.readLine();
			assertEquals("*aba-bras-tro", line);
			line = bufr.readLine();
			assertEquals("babel", line);
			line = bufr.readLine();
			assertEquals("baka", line);
			bufr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void checkParaTExtPreambleContent(BufferedReader bufr, String line) throws IOException {
		assertEquals("#Hyphenated Word List v2.0", line);
		line = bufr.readLine();		
		assertEquals("#You may edit words and force them to stay as you edit them by putting an * in front of the line.", line);
		line = bufr.readLine();		
		assertEquals("#Special equate lines used by Publishing Assistant", line);
		line = bufr.readLine();		
		assertEquals("HardHyphen = \"-\"", line);
		line = bufr.readLine();		
		assertEquals("SoftHyphen = \"=\"", line);
		line = bufr.readLine();		
		assertEquals("SoftHyphenOut = \"­\"", line);
		line = bufr.readLine();		
		assertEquals("HyphenatedMarkers = \"cd iex im imi imq ip ipi ipq ipr m mi nb p p1 p2 p3 ph ph1 ph2 ph3 pi pi1 pi2 pi3 pm pmc pmo pmr\"", line);
	}

	@Test
	public void exportWordsAsXLingPaperHyphenatedWordsTest() {
		long lineCount = 0;
		XLingPaperHyphenatedWordExporter exporter = new XLingPaperHyphenatedWordExporter(languageProject);
		exporter.exportWords(tempSaveFile, cva);
		try (Stream<String> stream = Files.lines(tempSaveFile.toPath(), StandardCharsets.UTF_8)) {
			lineCount = stream.count();
			assertEquals(1907,  lineCount);
		} catch (IOException | UncheckedIOException e1) {
			e1.printStackTrace();
		}
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(tempSaveFile),
					Constants.UTF8_ENCODING);
			BufferedReader bufr = new BufferedReader(reader);
			String line = bufr.readLine();		
			assertEquals("<exceptions>", line);
			line = bufr.readLine();
			assertEquals("<word>ab-ba\ua78c</word>", line);
			line = bufr.readLine();
			assertEquals("<word>aba-bras-tro</word>", line);
			line = bufr.readLine();
			assertEquals("<word>ba-bel</word>", line);
			line = bufr.readLine();
			assertEquals("<word>ba-ka</word>", line);
			for (int i = 5; i < 1903; i++) {
				line = bufr.readLine();
			}
			line = bufr.readLine();
			assertEquals("<word>yu\u2019-di-yo'</word>", line);
			line = bufr.readLine();
			assertEquals("<wordformingcharacter>'</wordformingcharacter>", line);
			line = bufr.readLine();
			assertEquals("<wordformingcharacter>\u2019</wordformingcharacter>", line);
			line = bufr.readLine();
			assertEquals("</exceptions>", line);
			bufr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void exportWordsAsXLingPaperHyphenatedWordsHyphenationParametersTest() {
		long lineCount = 0;
		languageProject.getHyphenationParametersXLingPaper().setDiscretionaryHyphen("-");
		languageProject.getHyphenationParametersXLingPaper().setStartAfterCharactersFromBeginning(0);
		languageProject.getHyphenationParametersXLingPaper().setStopBeforeCharactersFromEnd(0);
		XLingPaperHyphenatedWordExporter exporter = new XLingPaperHyphenatedWordExporter(languageProject);
		exporter.exportWords(tempSaveFile, cva);
		try (Stream<String> stream = Files.lines(tempSaveFile.toPath(), StandardCharsets.UTF_8)) {
			lineCount = stream.count();
			assertEquals(1907,  lineCount);
		} catch (IOException | UncheckedIOException e1) {
			e1.printStackTrace();
		}
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(tempSaveFile),
					Constants.UTF8_ENCODING);
			BufferedReader bufr = new BufferedReader(reader);
			String line = bufr.readLine();		
			assertEquals("<exceptions>", line);
			line = bufr.readLine();
			assertEquals("<word>ab-ba\ua78c</word>", line);
			line = bufr.readLine();
			assertEquals("<word>a-ba-bras-tro</word>", line);
			line = bufr.readLine();
			assertEquals("<word>ba-bel</word>", line);
			line = bufr.readLine();
			assertEquals("<word>ba-ka</word>", line);
			for (int i = 5; i < 1903; i++) {
				line = bufr.readLine();
			}
			line = bufr.readLine();
			assertEquals("<word>yu\u2019-di-yo'</word>", line);
			line = bufr.readLine();
			assertEquals("<wordformingcharacter>'</wordformingcharacter>", line);
			line = bufr.readLine();
			assertEquals("<wordformingcharacter>\u2019</wordformingcharacter>", line);
			line = bufr.readLine();
			assertEquals("</exceptions>", line);
			bufr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
