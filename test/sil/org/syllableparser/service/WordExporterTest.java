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

	// make sure the setup is what we expect
	@Test
	public void exportWordsAsSimpleListTest() {
		long lineCount = 0;
		ListWordExporter exporter = new ListWordExporter(languageProject);
		exporter.exportWords(tempSaveFile, cva);
		try (Stream<String> stream = Files.lines(tempSaveFile.toPath())) {
			lineCount = stream.count();
			assertEquals(1902,  lineCount);
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
			assertEquals("ab=ba", line);
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

}
