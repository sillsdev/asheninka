// Copyright (c) 2016-2017 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javafx.collections.ObservableList;
import javafx.scene.text.Font;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.Language;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.service.DatabaseMigrator;
import org.sil.syllableparser.view.JavaFXThreadingRule;

import com.sun.nio.zipfs.ZipDirectoryStream;

/**
 * @author Andy Black
 *
 */
public class DatabaseMigratorTest {

	File databaseFile;
	DatabaseMigrator migrator;
	LanguageProject languageProject;
	@Rule
	public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// prime the pump by making version 1 be the same as version 000
		// because doMigration changes the content of the version 1 file
		Files.copy(Paths.get(Constants.UNIT_TEST_DATA_FILE_VERSION_000),
				Paths.get(Constants.UNIT_TEST_DATA_FILE_VERSION_001),
				StandardCopyOption.REPLACE_EXISTING);
		databaseFile = new File(Constants.UNIT_TEST_DATA_FILE_VERSION_001);
		migrator = new DatabaseMigrator(databaseFile);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		// restore version 001 back to its initial state
		Files.copy(Paths.get(Constants.UNIT_TEST_DATA_FILE_VERSION_000),
				Paths.get(Constants.UNIT_TEST_DATA_FILE_VERSION_001),
				StandardCopyOption.REPLACE_EXISTING);
	}

	@Test
	public void testGetVersion() {
		int version = migrator.getVersion();
		assertEquals(1, version);
		File version2Database = new File(Constants.UNIT_TEST_DATA_FILE_VERSION_2);
		DatabaseMigrator migrator2 = new DatabaseMigrator(version2Database);
		version = migrator2.getVersion();
		assertEquals(2, version);
	}

	@Test
	public void testMigrator() {
		int version = migrator.getVersion();
		assertEquals(1, version);
		migrator.doMigration();
		version = migrator.getVersion();
		assertEquals(2, version);
		languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		xmlBackEndProvider.loadLanguageDataFromFile(databaseFile);
		assertEquals(Constants.CURRENT_DATABASE_VERSION, languageProject.getDatabaseVersion());
		int size = languageProject.getSegmentInventory().size();
		assertEquals(27, size);
		size = languageProject.getEnvironments().size();
		assertEquals(0, size);
		size = languageProject.getGraphemes().size();
		assertEquals(56, size);
		size = languageProject.getWords().size();
		assertEquals(10025, size);
		CVApproach cvApproach = languageProject.getCVApproach();
		assertEquals(6, cvApproach.getCVNaturalClasses().size());
		assertEquals(9, cvApproach.getCVSyllablePatterns().size());
		checkHyphenationParameters();
		Language lang = languageProject.getAnalysisLanguage();
		checkFontInfo(lang, "System", 12.0, "Regular");
		lang = languageProject.getVernacularLanguage();
		checkFontInfo(lang, "System", 12.0, "Regular");
		Segment seg = languageProject.getSegmentInventory().get(0);
		assertEquals("a", seg.getSegment());
		assertEquals("a A", seg.getGraphemes());
		assertEquals("low mid unrounded vowel", seg.getDescription());
		assertEquals(2, seg.getGraphs().size());
		Grapheme graph = seg.getGraphs().get(0);
		assertEquals("a", graph.getForm());
		assertEquals("de664190-dd3c-4bb3-892e-16dc596255ce", graph.getOwningSegment().getID());
		}

	private void checkHyphenationParameters() {
		assertEquals("list word hyphenation string", "=", languageProject
				.getHyphenationParametersListWord().getDiscretionaryHyphen());
		assertEquals("list word start", 0, languageProject.getHyphenationParametersListWord()
				.getStartAfterCharactersFromBeginning());
		assertEquals("list word stop", 0, languageProject.getHyphenationParametersListWord()
				.getStopBeforeCharactersFromEnd());

		assertEquals("ParaTExt hyphenation string", "=", languageProject
				.getHyphenationParametersParaTExt().getDiscretionaryHyphen());
		assertEquals("ParaTExt start", 2, languageProject.getHyphenationParametersParaTExt()
				.getStartAfterCharactersFromBeginning());
		assertEquals("ParaTExt stop", 2, languageProject.getHyphenationParametersParaTExt()
				.getStopBeforeCharactersFromEnd());

		assertEquals("XLingPaper hyphenation string", "-", languageProject
				.getHyphenationParametersXLingPaper().getDiscretionaryHyphen());
		assertEquals("XLingPaper start", 2, languageProject.getHyphenationParametersXLingPaper()
				.getStartAfterCharactersFromBeginning());
		assertEquals("XLingPaper stop", 2, languageProject.getHyphenationParametersXLingPaper()
				.getStopBeforeCharactersFromEnd());
	}

	private void checkFontInfo(Language lang, String fontFamily, double fontSize, String fontStyle) {
		assertEquals(fontFamily, lang.getFontFamily());
		assertEquals(fontSize, lang.getFontSize(), 0.0);
		assertEquals(fontStyle, lang.getFontType());
		Font font = lang.getFont();
		assertEquals(fontFamily, font.getFamily());
		assertEquals(fontSize, font.getSize(), 0.0);
		assertEquals(fontStyle, font.getStyle());
	}
}
