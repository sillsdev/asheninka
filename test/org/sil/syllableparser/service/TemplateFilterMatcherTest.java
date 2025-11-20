/**
 * Copyright (c) 2019-2025 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
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
import org.sil.syllableparser.model.Template;
import org.sil.syllableparser.model.TemplateFilter;
import org.sil.syllableparser.model.TemplateFilterSlotSegmentOrNaturalClass;
import org.sil.syllableparser.model.TemplateType;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.oncapproach.ONCSegmentInSyllable;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.service.parsing.CVSegmenterResult;
import org.sil.syllableparser.service.parsing.ONCSegmenter;
import org.sil.syllableparser.service.parsing.SHSonorityComparer;

/**
 * @author Andy Black
 *
 */
public class TemplateFilterMatcherTest {

	LanguageProject languageProject;
	Approach cva;
	List<Segment> activeSegments;
	List<CVNaturalClass> activeClasses;
	TemplateFilter tf;
	ONCSegmenter segmenter;
	List<ONCSegmentInSyllable> segmentsInWord;
	TemplateFilterMatcher matcher;
	SHSonorityComparer sonorityComparer;
	
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
		activeClasses = languageProject.getCVApproach().getActiveCVNaturalClasses();
		segmenter = new ONCSegmenter(languageProject.getActiveGraphemes(),
				languageProject.getActiveGraphemeNaturalClasses());
		matcher = TemplateFilterMatcher.getInstance();
		matcher.setActiveSegments(activeSegments);
		matcher.setActiveClasses(activeClasses);
		sonorityComparer = new SHSonorityComparer(languageProject);
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
		checkMatch("tl", 2, true, 2);
		checkMatch("tladi", 5, true, 2);
		checkMatch("atl", 3, false, 0);
		checkMatch("tal", 3, false, 1);
		checkMatch("t", 1, false, 1);

		tf = languageProject.getTemplates().get(0);
		// [Coronal] ([Coronal]) ([Coronal])
		checkMatch("sa", 2, true, 1);
		checkMatch("ta", 2, true, 1);
		checkMatch("da", 2, true, 1);
		checkMatch("ða", 2, true, 1);
		checkMatch("na", 2, true, 1);
		checkMatch("za", 2, true, 1);
		checkMatch("θa", 2, true, 1);
		checkMatch("s", 1, true, 1);
		checkMatch("t", 1, true, 1);
		checkMatch("d", 1, true, 1);
		checkMatch("ð", 1, true, 1);
		checkMatch("n", 1, true, 1);
		checkMatch("z", 1, true, 1);
		checkMatch("θa", 2, true, 1);
		checkMatch("dθs", 3, true, 3);
		checkMatch("sθs", 3, true, 3);
		checkMatch("dts", 3, true, 3);
		checkMatch("adθs", 4, false, 0);
		checkMatch("ka", 2, false, 0);

		tf = languageProject.getTemplates().get(1);
		// s [VoicelessNonCont] ([SonorantCV])
		checkMatch("stap", 4, true, 2);
		checkMatch("stɹæp", 5, true, 3);
		checkMatch("skɪp", 4, true, 2);
		checkMatch("skɹɪp", 5, true, 3);
		checkMatch("astap", 5, false, 0);
		checkMatch("astɹæp", 6, false, 0);
		checkMatch("askɪp", 5, false, 0);
		checkMatch("askɹɪp", 6, false, 0);
		checkMatch("slap", 4, false, 1);

		tf.getSlots().get(2).setOptional(false);
		tf.getSlots().get(2).setObeysSSP(true);
		checkMatch("stɹ", 3, true, SHComparisonResult.LESS, 3);
		checkMatch("stl", 3, true, SHComparisonResult.LESS, 3);
		checkMatch("stn", 3, false, SHComparisonResult.LESS, 2);
		checkMatch("stɹ", 3, false, SHComparisonResult.EQUAL, 3);
		checkMatch("stl", 3, false, SHComparisonResult.MORE, 3);
		tf.getSlots().get(2).setOptional(true);
		tf.getSlots().get(2).setObeysSSP(false);

		ObservableList<TemplateFilterSlotSegmentOrNaturalClass> slots = FXCollections.observableArrayList();		
		tf = new TemplateFilter("test initial optional slot", "Onset", "", "(t) l", slots);
		Optional<Segment> seg = activeSegments.stream().filter(s -> s.getSegment().equals("t")).findFirst();
		TemplateFilterSlotSegmentOrNaturalClass slot1 = new TemplateFilterSlotSegmentOrNaturalClass(seg.get());
		slot1.setOptional(true);
		tf.getSlots().add(slot1);
		seg = activeSegments.stream().filter(s -> s.getSegment().equals("l")).findFirst();
		TemplateFilterSlotSegmentOrNaturalClass slot2 = new TemplateFilterSlotSegmentOrNaturalClass(seg.get());
		slot2.setOptional(false);
		tf.getSlots().add(slot2);
		checkMatch("tl", 2, true, 2);
		checkMatch("l", 1, true, 1);
		checkMatch("tla", 3, true, 2);
		checkMatch("la", 2, true, 1);
		checkMatch("atl", 3, false, 0);
		checkMatch("al", 2, false, 0);
	}

	protected void checkMatch(String word, int numberOfSegments, boolean fExpectedResult, int expectedMatchCount) {
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		assertEquals(true, segResult.success);
		segmentsInWord = (List<ONCSegmentInSyllable>) segmenter.getSegmentsInWord();
		assertEquals(numberOfSegments, segmentsInWord.size());
		boolean fResult = matcher.matches(tf, segmentsInWord, sonorityComparer, null);
		assertEquals(fExpectedResult, fResult);
		assertEquals(expectedMatchCount, matcher.getMatchCount());
	}

	protected void checkMatch(String word, int numberOfSegments, boolean fExpectedResult,
			SHComparisonResult sspComparisonNeeded, int expectedMatchCount) {
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		assertEquals(true, segResult.success);
		segmentsInWord = (List<ONCSegmentInSyllable>) segmenter.getSegmentsInWord();
		assertEquals(numberOfSegments, segmentsInWord.size());
		boolean fResult = matcher
				.matches(tf, segmentsInWord, sonorityComparer, sspComparisonNeeded);
		assertEquals(fExpectedResult, fResult);
		assertEquals(expectedMatchCount, matcher.getMatchCount());
	}
	@Test
	public void syllableMatcherTest() {
		createSyllableTemplate();
		// ([C]) ([C]) [V] ([C]) ([C])
		assertNotNull(tf);
		checkMatch("a", 1, true, 1);
		checkMatch("ta", 2, true, 2);
		checkMatch("tad", 3, true, 3);
		checkMatch("tlad", 4, true, 4);
		checkMatch("atl", 3, true, 3);
		checkMatch("lta", 3, false, 3);
		checkMatch("mtakn", 5, false, 5);
		checkMatch("t", 1, false, 1);
		// (*[C]) (*[C]) [V] (*[C]) (*[C])
		ObservableList<TemplateFilterSlotSegmentOrNaturalClass> slots = tf.getSlots();
		slots.get(0).setObeysSSP(false);
		slots.get(1).setObeysSSP(false);
		slots.get(3).setObeysSSP(false);
		slots.get(4).setObeysSSP(false);
		checkMatch("a", 1, true, 1);
		checkMatch("ta", 2, true, 2);
		checkMatch("tad", 3, true, 3);
		checkMatch("tlad", 4, true, 4);
		checkMatch("atl", 3, true, 3);
		checkMatch("lta", 3, true, 3);
		checkMatch("mtakn", 5, true, 5);
		checkMatch("t", 1, false, 1);
	}

	protected void createSyllableTemplate() {
		CVNaturalClass c = languageProject.getONCApproach().getActiveCVNaturalClasses().get(0);
		CVNaturalClass v = languageProject.getONCApproach().getActiveCVNaturalClasses().get(1);
		ObservableList<TemplateFilterSlotSegmentOrNaturalClass> slots = FXCollections.observableArrayList();
		TemplateFilterSlotSegmentOrNaturalClass slot1 = new TemplateFilterSlotSegmentOrNaturalClass(c);
		slot1.setObeysSSP(true);
		slot1.setOptional(true);
		slots.add(slot1);
		TemplateFilterSlotSegmentOrNaturalClass slot2 = new TemplateFilterSlotSegmentOrNaturalClass(c);
		slot2.setObeysSSP(true);
		slot2.setOptional(true);
		slots.add(slot2);
		TemplateFilterSlotSegmentOrNaturalClass slot3 = new TemplateFilterSlotSegmentOrNaturalClass(v);
		slot3.setObeysSSP(true);
		slot3.setOptional(false);
		slots.add(slot3);
		TemplateFilterSlotSegmentOrNaturalClass slot4 = new TemplateFilterSlotSegmentOrNaturalClass(c);
		slot4.setObeysSSP(true);
		slot4.setOptional(true);
		slots.add(slot4);
		TemplateFilterSlotSegmentOrNaturalClass slot5 = new TemplateFilterSlotSegmentOrNaturalClass(c);
		slot5.setObeysSSP(true);
		slot5.setOptional(true);
		slots.add(slot5);
		tf = new Template("Syllable template", "type", "description", "([C])([C])[V]([C])([C])", slots, TemplateType.SYLLABLE);
	}
}
