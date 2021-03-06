// Copyright (c) 2016-2019 SIL International 
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
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.service.importexport.ParaTExt7SegmentImporter;
import org.sil.syllableparser.service.importexport.ParaTExtSegmentImporterNoCharactersException;
import org.sil.syllableparser.service.importexport.SegmentImporterException;
import org.sil.utility.StringUtilities;

/**
 * @author Andy Black
 *
 */
public class SegmentImporterTest {
	LanguageProject languageProject;
	ResourceBundle bundle;

	final String sParaTExt7ParametersNoCharactersFileName = "test/org/sil/syllableparser/testData/ParaTextParametersNoChars.lds";

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
	public void importParaTExt7SegmentsFullTest() throws SegmentImporterException {
		String sFileName = "test/org/sil/syllableparser/testData/ParaTextParametersFull.lds";
		assertEquals(25, languageProject.getSegmentInventory().size());
		assertEquals(3, languageProject.getCVApproach().getCVNaturalClasses().size());
		ParaTExt7SegmentImporter importer = new ParaTExt7SegmentImporter(languageProject);
		File file = new File(sFileName);
		importer.importSegments(file);
		assertEquals(33, languageProject.getSegmentInventory().size());
		ObservableList<Segment> segments = languageProject.getSegmentInventory();
		Segment segment = segments.get(0);
		assertEquals("a", segment.getSegment());
		assertEquals(4, segment.getGraphs().size());
		Grapheme grapheme = segment.getGraphs().get(0);
		assertEquals("a", grapheme.getForm());
		grapheme = segment.getGraphs().get(1);
		assertEquals("A", grapheme.getForm());
		grapheme = segment.getGraphs().get(2);
		assertEquals("á", grapheme.getForm());
		grapheme = segment.getGraphs().get(3);
		assertEquals("Á", grapheme.getForm());
		//assertEquals("a A á Á", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		segment = segments.get(1);
		assertEquals("b", segment.getSegment());
		assertEquals(2, segment.getGraphs().size());
		grapheme = segment.getGraphs().get(0);
		assertEquals("b", grapheme.getForm());
		grapheme = segment.getGraphs().get(1);
		assertEquals("B", grapheme.getForm());
		//assertEquals("b B", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		segment = segments.get(18);
		assertEquals("o", segment.getSegment());
		assertEquals(4, segment.getGraphs().size());
		grapheme = segment.getGraphs().get(0);
		assertEquals("o", grapheme.getForm());
		grapheme = segment.getGraphs().get(1);
		assertEquals("O", grapheme.getForm());
		grapheme = segment.getGraphs().get(2);
		assertEquals("ó", grapheme.getForm());
		grapheme = segment.getGraphs().get(3);
		assertEquals("Ó", grapheme.getForm());
		//assertEquals("o O ó Ó", segment.getGraphemes());
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
		String sFileName = "test/org/sil/syllableparser/testData/ParaTextParametersCharsAtStart.lds";
		assertEquals(25, languageProject.getSegmentInventory().size());
		assertEquals(3, languageProject.getCVApproach().getCVNaturalClasses().size());
		ParaTExt7SegmentImporter importer = new ParaTExt7SegmentImporter(languageProject);
		File file = new File(sFileName);
		importer.importSegments(file);
		assertEquals(33, languageProject.getSegmentInventory().size());
		ObservableList<Segment> segments = languageProject.getSegmentInventory();
		Segment segment = segments.get(0);
		assertEquals("a", segment.getSegment());
		assertEquals(4, segment.getGraphs().size());
		Grapheme grapheme = segment.getGraphs().get(0);
		assertEquals("a", grapheme.getForm());
		grapheme = segment.getGraphs().get(1);
		assertEquals("A", grapheme.getForm());
		grapheme = segment.getGraphs().get(2);
		assertEquals("á", grapheme.getForm());
		grapheme = segment.getGraphs().get(3);
		assertEquals("Á", grapheme.getForm());
		//assertEquals("a A á Á", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		segment = segments.get(1);
		assertEquals("b", segment.getSegment());
		assertEquals(2, segment.getGraphs().size());
		grapheme = segment.getGraphs().get(0);
		assertEquals("b", grapheme.getForm());
		grapheme = segment.getGraphs().get(1);
		assertEquals("B", grapheme.getForm());
		//assertEquals("b B", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		segment = segments.get(18);
		assertEquals("o", segment.getSegment());
		assertEquals(4, segment.getGraphs().size());
		grapheme = segment.getGraphs().get(0);
		assertEquals("o", grapheme.getForm());
		grapheme = segment.getGraphs().get(1);
		assertEquals("O", grapheme.getForm());
		grapheme = segment.getGraphs().get(2);
		assertEquals("ó", grapheme.getForm());
		grapheme = segment.getGraphs().get(3);
		assertEquals("Ó", grapheme.getForm());
		//assertEquals("o O ó Ó", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		segment = segments.get(32);
		assertEquals("'", segment.getSegment());
		assertEquals(1, segment.getGraphs().size());
		grapheme = segment.getGraphs().get(0);
		assertEquals("'", grapheme.getForm());
		//assertEquals("'", segment.getGraphemes());
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
		String sFileName = "test/org/sil/syllableparser/testData/ParaTextParametersCharsAtEnd.lds";
		assertEquals(25, languageProject.getSegmentInventory().size());
		assertEquals(3, languageProject.getCVApproach().getCVNaturalClasses().size());
		ParaTExt7SegmentImporter importer = new ParaTExt7SegmentImporter(languageProject);
		File file = new File(sFileName);
		importer.importSegments(file);
		assertEquals(33, languageProject.getSegmentInventory().size());
		ObservableList<Segment> segments = languageProject.getSegmentInventory();
		Segment segment = segments.get(0);
		assertEquals("a", segment.getSegment());
		assertEquals(4, segment.getGraphs().size());
		Grapheme grapheme = segment.getGraphs().get(0);
		assertEquals("a", grapheme.getForm());
		grapheme = segment.getGraphs().get(1);
		assertEquals("A", grapheme.getForm());
		grapheme = segment.getGraphs().get(2);
		assertEquals("á", grapheme.getForm());
		grapheme = segment.getGraphs().get(3);
		assertEquals("Á", grapheme.getForm());
		//assertEquals("a A á Á", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		segment = segments.get(1);
		assertEquals("b", segment.getSegment());
		assertEquals(2, segment.getGraphs().size());
		grapheme = segment.getGraphs().get(0);
		assertEquals("b", grapheme.getForm());
		grapheme = segment.getGraphs().get(1);
		assertEquals("B", grapheme.getForm());
		//assertEquals("b B", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		segment = segments.get(18);
		assertEquals("o", segment.getSegment());
		assertEquals(4, segment.getGraphs().size());
		grapheme = segment.getGraphs().get(0);
		assertEquals("o", grapheme.getForm());
		grapheme = segment.getGraphs().get(1);
		assertEquals("O", grapheme.getForm());
		grapheme = segment.getGraphs().get(2);
		assertEquals("ó", grapheme.getForm());
		grapheme = segment.getGraphs().get(3);
		assertEquals("Ó", grapheme.getForm());
		//assertEquals("o O ó Ó", segment.getGraphemes());
		assertEquals("", segment.getDescription());
		segment = segments.get(32);
		assertEquals("'", segment.getSegment());
		assertEquals(1, segment.getGraphs().size());
		grapheme = segment.getGraphs().get(0);
		assertEquals("'", grapheme.getForm());
		//assertEquals("'", segment.getGraphemes());
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
