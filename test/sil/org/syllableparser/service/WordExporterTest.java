/**
 * 
 */
package sil.org.syllableparser.service;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.cvapproach.*;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.Word;
import sil.org.syllableparser.service.CVNaturalClasser;
import sil.org.syllableparser.service.CVSegmenter;
import sil.org.syllableparser.service.CVSyllabifier;
import sil.org.syllableparser.view.ApproachController;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter and CVNaturalClasser
 *         classes are functioning correctly
 */
public class WordExporterTest {

	File tempSaveFile = null;
	LanguageProject languageProject = null;
	CVApproach cva;

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
			// TODO Auto-generated catch block
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
		try (Stream<String> stream = Files.lines(tempSaveFile.toPath())) {
			lineCount = stream.count();
			assertEquals(1903,  lineCount);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void exportWordsAsParaTExtHyphenatedWordsTest() {
		long lineCount = 0;
		ParaTExtHyphenatedWordsExporter exporter = new ParaTExtHyphenatedWordsExporter(languageProject);
		exporter.exportWords(tempSaveFile, cva);
		try (Stream<String> stream = Files.lines(tempSaveFile.toPath())) {
			lineCount = stream.count();
			assertEquals(1910,  lineCount);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
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
			assertEquals("*a=ba=bras=tro", line);
			line = bufr.readLine();
			assertEquals("ba=bel", line);
			line = bufr.readLine();
			assertEquals("ba=ka", line);
			bufr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		try (Stream<String> stream = Files.lines(tempSaveFile.toPath())) {
			lineCount = stream.count();
			assertEquals(1907,  lineCount);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
