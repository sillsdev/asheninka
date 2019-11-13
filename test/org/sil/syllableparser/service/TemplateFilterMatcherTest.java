/**
 * Copyright (c) 2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Approach;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.TemplateFilter;
import org.sil.syllableparser.model.TemplateFilterSlotSegmentOrNaturalClass;
import org.sil.syllableparser.model.TemplateFilterType;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.oncapproach.ONCSegmentInSyllable;
import org.sil.syllableparser.service.parsing.CVSegmenterResult;
import org.sil.syllableparser.service.parsing.ONCSegmenter;

/**
 * @author Andy Black
 *
 */
public class TemplateFilterMatcherTest {

	LanguageProject languageProject;
	Approach cva;
	List<Segment> activeSegments;
	List<CVNaturalClass> classes;
	TemplateFilter tf;
	ONCSegmenter segmenter;
	List<ONCSegmentInSyllable> segmentsInWord;
	TemplateFilterMatcher matcher;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE_TEMPLATES_FILTERS);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		cva = languageProject.getCVApproach();
		activeSegments = languageProject.getActiveSegmentsInInventory();
		classes = languageProject.getCVApproach().getActiveCVNaturalClasses();
		segmenter = new ONCSegmenter(languageProject.getActiveGraphemes(),
				languageProject.getActiveGraphemeNaturalClasses());
		matcher = new TemplateFilterMatcher(activeSegments, classes);
		}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void matcherTest() {
		tf = languageProject.getFilters().get(0);
		// t l
		assertNotNull(tf);
		checkMatch("tl", 2, true);
		checkMatch("tladi", 5, true);
		checkMatch("atl", 3, false);
		checkMatch("tal", 3, false);
		checkMatch("t", 1, false);

		tf = languageProject.getTemplates().get(0);
		// [Coronal] ([Coronal]) ([Coronal])
		checkMatch("sa", 2, true);
		checkMatch("ta", 2, true);
		checkMatch("da", 2, true);
		checkMatch("ða", 2, true);
		checkMatch("na", 2, true);
		checkMatch("za", 2, true);
		checkMatch("θa", 2, true);
		checkMatch("s", 1, true);
		checkMatch("t", 1, true);
		checkMatch("d", 1, true);
		checkMatch("ð", 1, true);
		checkMatch("n", 1, true);
		checkMatch("z", 1, true);
		checkMatch("θa", 2, true);
		checkMatch("dθs", 3, true);
		checkMatch("sθs", 3, true);
		checkMatch("dts", 3, true);
		checkMatch("adθs", 4, false);
		checkMatch("ka", 2, false);

		tf = languageProject.getTemplates().get(1);
		// s [VoicelessNonCont] ([SonorantCV])
		checkMatch("stap", 4, true);
		checkMatch("stɹæp", 5, true);
		checkMatch("skɪp", 4, true);
		checkMatch("skɹɪp", 5, true);
		checkMatch("astap", 5, false);
		checkMatch("astɹæp", 6, false);
		checkMatch("askɪp", 5, false);
		checkMatch("askɹɪp", 6, false);
		checkMatch("slap", 4, false);
		
		ObservableList<TemplateFilterSlotSegmentOrNaturalClass> slots = FXCollections.observableArrayList();		
		tf = new TemplateFilter("test initial optional slot", "Onset", "", "(t) l", slots, TemplateFilterType.ONSET);
		Optional<Segment> seg = activeSegments.stream().filter(s -> s.getSegment().equals("t")).findFirst();
		TemplateFilterSlotSegmentOrNaturalClass slot1 = new TemplateFilterSlotSegmentOrNaturalClass(seg.get());
		slot1.setOptional(true);
		tf.getSlots().add(slot1);
		seg = activeSegments.stream().filter(s -> s.getSegment().equals("l")).findFirst();
		TemplateFilterSlotSegmentOrNaturalClass slot2 = new TemplateFilterSlotSegmentOrNaturalClass(seg.get());
		slot2.setOptional(false);
		tf.getSlots().add(slot2);
		checkMatch("tl", 2, true);
		checkMatch("l", 1, true);
		checkMatch("tla", 3, true);
		checkMatch("la", 2, true);
		checkMatch("atl", 3, false);
		checkMatch("al", 2, false);
	}

	protected void checkMatch(String word, int numberOfSegments, boolean fExpectedResult) {
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		assertEquals(true, segResult.success);
		segmentsInWord = (List<ONCSegmentInSyllable>) segmenter.getSegmentsInWord();
		assertEquals(numberOfSegments, segmentsInWord.size());
		boolean fResult = matcher.matches(tf, segmentsInWord);
		assertEquals(fExpectedResult, fResult);
	}

}
