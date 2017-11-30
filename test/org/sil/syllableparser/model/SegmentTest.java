// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVSyllablePattern;
import org.sil.syllableparser.service.CVSegmenter;
import org.sil.syllableparser.view.ApproachViewNavigator;
import org.sil.syllableparser.view.CVSegmentInventoryController;
import org.sil.syllableparser.view.JavaFXThreadingRule;

/**
 * @author Andy Black
 *
 */
public class SegmentTest {
	CVApproach cva;
	Segment segment;
	@Rule
	public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		LanguageProject languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		cva = languageProject.getCVApproach();
		segment = cva.getLanguageProject().getSegmentInventory().get(0);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void addNewGraphemesAndRemoveOldOnesTest() {
		// prime the pump: make sure we have what we expect
		assertNotNull(segment);
		assertEquals("a", segment.getSegment());
		ObservableList<Grapheme> graphs = segment.getGraphs();
		assertEquals(2, graphs.size());
		Grapheme grapheme = graphs.get(0);
		assertEquals("a", grapheme.getForm());
		grapheme = graphs.get(1);
		assertEquals("A", grapheme.getForm());
		String sGraphemes = segment.getGraphemes();
		assertNotNull(sGraphemes);
		assertEquals("a A", sGraphemes);

		// now add some new graphemes and make sure the list of graphemes has them.
		// add at end of list
		segment.setGraphemes("a A á");
		segment.updateGraphemes();
		graphs = segment.getGraphs();
		assertEquals(3, graphs.size());
		grapheme = graphs.get(0);
		assertEquals("a", grapheme.getForm());
		grapheme = graphs.get(1);
		assertEquals("A", grapheme.getForm());
		grapheme = graphs.get(2);
		assertEquals("á", grapheme.getForm());

		// add in middle of list
		segment.setGraphemes("a A æ̀ á");
		segment.updateGraphemes();
		graphs = segment.getGraphs();
		assertEquals(4, graphs.size());
		grapheme = graphs.get(0);
		assertEquals("a", grapheme.getForm());
		grapheme = graphs.get(1);
		assertEquals("A", grapheme.getForm());
		grapheme = graphs.get(2);
		assertEquals("á", grapheme.getForm());
		grapheme = graphs.get(3);
		assertEquals("æ̀", grapheme.getForm());

		// add at front of list
		segment.setGraphemes("ɐ͡ᶤ a A æ̀ á");
		segment.updateGraphemes();
		graphs = segment.getGraphs();
		assertEquals(5, graphs.size());
		grapheme = graphs.get(0);
		assertEquals("a", grapheme.getForm());
		grapheme = graphs.get(1);
		assertEquals("A", grapheme.getForm());
		grapheme = graphs.get(2);
		assertEquals("á", grapheme.getForm());
		grapheme = graphs.get(3);
		assertEquals("æ̀", grapheme.getForm());
		grapheme = graphs.get(4);
		assertEquals("ɐ͡ᶤ", grapheme.getForm());

		// now remove some graphemes
		// remove from end of the list
		segment.setGraphemes("ɐ͡ᶤ a A æ̀");
		segment.updateGraphemes();
		graphs = segment.getGraphs();
		assertEquals(4, graphs.size());
		grapheme = graphs.get(0);
		assertEquals("a", grapheme.getForm());
		grapheme = graphs.get(1);
		assertEquals("A", grapheme.getForm());
		grapheme = graphs.get(2);
		assertEquals("æ̀", grapheme.getForm());
		grapheme = graphs.get(3);
		assertEquals("ɐ͡ᶤ", grapheme.getForm());
		
		// remove from the middle of the list
		segment.setGraphemes("ɐ͡ᶤ a  æ̀");
		segment.updateGraphemes();
		graphs = segment.getGraphs();
		assertEquals(3, graphs.size());
		grapheme = graphs.get(0);
		assertEquals("a", grapheme.getForm());
		grapheme = graphs.get(1);
		assertEquals("æ̀", grapheme.getForm());
		grapheme = graphs.get(2);
		assertEquals("ɐ͡ᶤ", grapheme.getForm());
		
		// remove from the front of the list
		segment.setGraphemes(" a  æ̀");
		segment.updateGraphemes();
		graphs = segment.getGraphs();
		assertEquals(2, graphs.size());
		grapheme = graphs.get(0);
		assertEquals("a", grapheme.getForm());
		grapheme = graphs.get(1);
		assertEquals("æ̀", grapheme.getForm());
		
		// test for duplicates
		segment.setGraphemes("a a  æ̀");
		segment.updateGraphemes();
		graphs = segment.getGraphs();
		assertEquals(2, graphs.size());
		grapheme = graphs.get(0);
		assertEquals("a", grapheme.getForm());
		grapheme = graphs.get(1);
		assertEquals("æ̀", grapheme.getForm());

		// add all new ones
		segment.setGraphemes("à A á");
		segment.updateGraphemes();
		graphs = segment.getGraphs();
		assertEquals(3, graphs.size());
		grapheme = graphs.get(0);
		assertEquals("à", grapheme.getForm());
		grapheme = graphs.get(1);
		assertEquals("A", grapheme.getForm());
		grapheme = graphs.get(2);
		assertEquals("á", grapheme.getForm());
	
		// Set two to inactive and then try and remove them.
		// They should still be there.
		grapheme = graphs.get(0);
		grapheme.setActive(false);
		grapheme = graphs.get(2);
		grapheme.setActive(false);
		segment.setGraphemes(" A ");
		segment.updateGraphemes();
		graphs = segment.getGraphs();
		assertEquals(3, graphs.size());
		grapheme = graphs.get(0);
		assertEquals("à", grapheme.getForm());
		grapheme = graphs.get(1);
		assertEquals("A", grapheme.getForm());
		grapheme = graphs.get(2);
		assertEquals("á", grapheme.getForm());

		// reset inactive ones to active
		grapheme = graphs.get(0);
		grapheme.setActive(true);
		grapheme = graphs.get(2);
		grapheme.setActive(true);
		// test for duplicates when one is inactive
		segment.setGraphemes("a A á");
		segment.updateGraphemes();
		graphs = segment.getGraphs();
		assertEquals(3, graphs.size());
		grapheme = graphs.get(0);
		assertEquals("A", grapheme.getForm());
		grapheme = graphs.get(1);
		assertEquals("á", grapheme.getForm());
		grapheme = graphs.get(2);
		assertEquals("a", grapheme.getForm());
		// set second one to inactive
		grapheme = graphs.get(1);
		grapheme.setActive(false);
		segment.setGraphemes("a A á");
		segment.updateGraphemes();
		graphs = segment.getGraphs();
		assertEquals(3, graphs.size());
		grapheme = graphs.get(0);
		assertEquals("A", grapheme.getForm());
		grapheme = graphs.get(1);
		assertEquals("á", grapheme.getForm());
		grapheme = graphs.get(2);
		assertEquals("a", grapheme.getForm());

	}
}
