// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
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
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.Word;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.utility.StringUtilities;

/**
 * @author Andy Black
 *
 */
public class SegmentImporterTest {
	LanguageProject languageProject;
	ResourceBundle bundle;

	final String sParaTExt7ParametersNoCharactersFileName = "test/sil/org/syllableparser/testData/ParaTextParametersNoChars.lds";

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
	public void importParaTExt7SegmentsFullTest() throws SegmentImporterException {
		String sFileName = "test/sil/org/syllableparser/testData/ParaTextParametersFull.lds";
		assertEquals(25, languageProject.getSegmentInventory().size());
		assertEquals(3, languageProject.getCVApproach().getCVNaturalClasses().size());
		ParaTExt7SegmentImporter importer = new ParaTExt7SegmentImporter(languageProject);
		File file = new File(sFileName);
		importer.importSegments(file);
		assertEquals(33, languageProject.getSegmentInventory().size());
		ObservableList<Segment> segments = languageProject.getSegmentInventory();
		Segment segment = segments.get(0);
		assertEquals("a", segment.getSegment());
		assertEquals("a A á Á", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		segment = segments.get(1);
		assertEquals("b", segment.getSegment());
		assertEquals("b B", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		segment = segments.get(18);
		assertEquals("o", segment.getSegment());
		assertEquals("o O ó Ó", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		segment = segments.get(32);
		assertEquals("'", segment.getSegment());
		assertEquals("'", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		// Natural classes should no longer have any segments
		ObservableList<CVNaturalClass> ncs = languageProject.getCVApproach().getCVNaturalClasses();
		assertEquals(3, ncs.size());
		CVNaturalClass nc = ncs.get(0);
		assertEquals(1, nc.getSegmentsOrNaturalClasses().size());
		nc = ncs.get(1);
		assertEquals(0, nc.getSegmentsOrNaturalClasses().size());
		nc = ncs.get(2);
		assertEquals(0, nc.getSegmentsOrNaturalClasses().size());
	}

	@Test
	public void importParaTExt7SegmentsAtStartTest() throws SegmentImporterException {
		String sFileName = "test/sil/org/syllableparser/testData/ParaTextParametersCharsAtStart.lds";
		assertEquals(25, languageProject.getSegmentInventory().size());
		assertEquals(3, languageProject.getCVApproach().getCVNaturalClasses().size());
		ParaTExt7SegmentImporter importer = new ParaTExt7SegmentImporter(languageProject);
		File file = new File(sFileName);
		importer.importSegments(file);
		assertEquals(33, languageProject.getSegmentInventory().size());
		ObservableList<Segment> segments = languageProject.getSegmentInventory();
		Segment segment = segments.get(0);
		assertEquals("a", segment.getSegment());
		assertEquals("a A á Á", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		segment = segments.get(1);
		assertEquals("b", segment.getSegment());
		assertEquals("b B", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		segment = segments.get(18);
		assertEquals("o", segment.getSegment());
		assertEquals("o O ó Ó", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		segment = segments.get(32);
		assertEquals("'", segment.getSegment());
		assertEquals("'", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		// Natural classes should no longer have any segments
		ObservableList<CVNaturalClass> ncs = languageProject.getCVApproach().getCVNaturalClasses();
		assertEquals(3, ncs.size());
		CVNaturalClass nc = ncs.get(0);
		assertEquals(1, nc.getSegmentsOrNaturalClasses().size());
		nc = ncs.get(1);
		assertEquals(0, nc.getSegmentsOrNaturalClasses().size());
		nc = ncs.get(2);
		assertEquals(0, nc.getSegmentsOrNaturalClasses().size());
	}

	@Test
	public void importParaTExt7SegmentsAtEndTest() throws SegmentImporterException {
		String sFileName = "test/sil/org/syllableparser/testData/ParaTextParametersCharsAtEnd.lds";
		assertEquals(25, languageProject.getSegmentInventory().size());
		assertEquals(3, languageProject.getCVApproach().getCVNaturalClasses().size());
		ParaTExt7SegmentImporter importer = new ParaTExt7SegmentImporter(languageProject);
		File file = new File(sFileName);
		importer.importSegments(file);
		assertEquals(33, languageProject.getSegmentInventory().size());
		ObservableList<Segment> segments = languageProject.getSegmentInventory();
		Segment segment = segments.get(0);
		assertEquals("a", segment.getSegment());
		assertEquals("a A á Á", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		segment = segments.get(1);
		assertEquals("b", segment.getSegment());
		assertEquals("b B", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		segment = segments.get(18);
		assertEquals("o", segment.getSegment());
		assertEquals("o O ó Ó", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		segment = segments.get(32);
		assertEquals("'", segment.getSegment());
		assertEquals("'", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		// Natural classes should no longer have any segments
		ObservableList<CVNaturalClass> ncs = languageProject.getCVApproach().getCVNaturalClasses();
		assertEquals(3, ncs.size());
		CVNaturalClass nc = ncs.get(0);
		assertEquals(1, nc.getSegmentsOrNaturalClasses().size());
		nc = ncs.get(1);
		assertEquals(0, nc.getSegmentsOrNaturalClasses().size());
		nc = ncs.get(2);
		assertEquals(0, nc.getSegmentsOrNaturalClasses().size());
	}

	@Test
	public void importParaTExt7SegmentsNoCharsTest() {
		try {
			// the ParaTExt parameters file has no character definitions
			File file = new File(sParaTExt7ParametersNoCharactersFileName);
			ParaTExt7SegmentImporter importer = new ParaTExt7SegmentImporter(languageProject);
			importer.importSegments(file);
			assertEquals(25, languageProject.getSegmentInventory().size());
		} catch (SegmentImporterException e) {
			if (e instanceof ParaTExtSegmentImporterNoCharactersException) {
				ParaTExtSegmentImporterNoCharactersException ptex = (ParaTExtSegmentImporterNoCharactersException) e;
				assertEquals(sParaTExt7ParametersNoCharactersFileName,
						StringUtilities.adjustForWindowsFileSeparator(ptex.getsFileName()));
				assertEquals(25, languageProject.getSegmentInventory().size());
			}
		}
	}
}
