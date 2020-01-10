// Copyright (c) 2019-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package org.sil.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Approach;
import org.sil.syllableparser.model.Environment;
import org.sil.syllableparser.model.EnvironmentContext;
import org.sil.syllableparser.model.EnvironmentContextGraphemeOrNaturalClass;
import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.GraphemeNaturalClass;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.TemplateFilter;
import org.sil.syllableparser.model.TemplateFilterSlotSegmentOrNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.service.AsheninkaGraphemeAndClassListener;
import org.sil.antlr4.environmentparser.antlr4generated.EnvironmentParser;
import org.sil.antlr4.templatefilterparser.antlr4generated.TemplateFilterParser;

public class TemplateFilterBuilderTest extends TemplateFilterParsingBase {

	Approach cva;
	List<Segment> activeSegments;

	@Before
	public void setUp() throws Exception {
		LanguageProject languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		cva = languageProject.getCVApproach();
		activeSegments = languageProject.getActiveSegmentsInInventory();
		classes = languageProject.getCVApproach().getActiveCVNaturalClasses();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void validDescriptionsTest() {
		TemplateFilter tf;
		List<TemplateFilterSlotSegmentOrNaturalClass> slots;
		TemplateFilterSlotSegmentOrNaturalClass slot;

		// segment
		tf = checkValidDescription("a", 1);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsSegment(slot, "a", true, false, true);

		tf = checkValidDescription("*a", 1);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsSegment(slot, "a", true, false, false);

		tf = checkValidDescription("b", 1);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsSegment(slot, "b", true, false, true);

		tf = checkValidDescription("a b", 2);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsSegment(slot, "a", true, false, true);
		slot = slots.get(1);
		checkSlotAsSegment(slot, "b", true, false, true);

		tf = checkValidDescription("a *b ai *d", 4);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsSegment(slot, "a", true, false, true);
		slot = slots.get(1);
		checkSlotAsSegment(slot, "b", true, false, false);
		slot = slots.get(2);
		checkSlotAsSegment(slot, "ai", true, false, true);
		slot = slots.get(3);
		checkSlotAsSegment(slot, "d", true, false, false);

		// segments without intervening spaces
		tf = checkValidDescription("ab", 2);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsSegment(slot, "a", true, false, true);
		slot = slots.get(1);
		checkSlotAsSegment(slot, "b", true, false, true);

		tf = checkValidDescription("*ab", 2);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsSegment(slot, "a", true, false, false);
		slot = slots.get(1);
		checkSlotAsSegment(slot, "b", true, false, true);

		tf = checkValidDescription("*a*b", 2);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsSegment(slot, "a", true, false, false);
		slot = slots.get(1);
		checkSlotAsSegment(slot, "b", true, false, false);
		
		tf = checkValidDescription("a*b", 2);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsSegment(slot, "a", true, false, true);
		slot = slots.get(1);
		checkSlotAsSegment(slot, "b", true, false, false);		

		tf = checkValidDescription("flaid", 5);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsSegment(slot, "f", true, false, true);
		slot = slots.get(1);
		checkSlotAsSegment(slot, "l", true, false, true);
		slot = slots.get(2);
		checkSlotAsSegment(slot, "a", true, false, true);
		slot = slots.get(3);
		checkSlotAsSegment(slot, "i", true, false, true);
		slot = slots.get(4);
		checkSlotAsSegment(slot, "d", true, false, true);

		// optionality
		tf = checkValidDescription("(a) b ", 2);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsSegment(slot, "a", true, true, true);
		slot = slots.get(1);
		checkSlotAsSegment(slot, "b", true, false, true);

		tf = checkValidDescription("(*a) *b", 2);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsSegment(slot, "a", true, true, false);
		slot = slots.get(1);
		checkSlotAsSegment(slot, "b", true, false, false);

		tf = checkValidDescription("(c) *d (*a) b", 4);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsSegment(slot, "c", true, true, true);
		slot = slots.get(1);
		checkSlotAsSegment(slot, "d", true, false, false);
		slot = slots.get(2);
		checkSlotAsSegment(slot, "a", true, true, false);
		slot = slots.get(3);
		checkSlotAsSegment(slot, "b", true, false, true);

		tf = checkValidDescription("(f)(fl)", 2);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsSegment(slot, "f", true, true, true);
		slot = slots.get(1);
		checkSlotAsSegment(slot, "fl", true, true, true);

		// Natural Classes
		tf = checkValidDescription("[V]", 1);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsNaturalClass(slot, "V", false, false, true);

		tf = checkValidDescription(" *[V]", 1);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsNaturalClass(slot, "V", false, false, false);

		tf = checkValidDescription("[V] [C]", 2);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsNaturalClass(slot, "V", false, false, true);
		slot = slots.get(1);
		checkSlotAsNaturalClass(slot, "C", false, false, true);

		tf = checkValidDescription("*[V] [C]", 2);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsNaturalClass(slot, "V", false, false, false);
		slot = slots.get(1);
		checkSlotAsNaturalClass(slot, "C", false, false, true);

		tf = checkValidDescription("[V] *[C]", 2);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsNaturalClass(slot, "V", false, false, true);
		slot = slots.get(1);
		checkSlotAsNaturalClass(slot, "C", false, false, false);

		tf = checkValidDescription("*[V] *[C]", 2);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsNaturalClass(slot, "V", false, false, false);
		slot = slots.get(1);
		checkSlotAsNaturalClass(slot, "C", false, false, false);

		// Natural Classes with optionality
		tf = checkValidDescription("([C]) [V] ", 2);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsNaturalClass(slot, "C", false, true, true);
		slot = slots.get(1);
		checkSlotAsNaturalClass(slot, "V", false, false, true);

		tf = checkValidDescription("(*[C]) [V] ", 2);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsNaturalClass(slot, "C", false, true, false);
		slot = slots.get(1);
		checkSlotAsNaturalClass(slot, "V", false, false, true);

		tf = checkValidDescription("([C]) *[V] ", 2);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsNaturalClass(slot, "C", false, true, true);
		slot = slots.get(1);
		checkSlotAsNaturalClass(slot, "V", false, false, false);

		tf = checkValidDescription("(*[C]) *[V] ", 2);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsNaturalClass(slot, "C", false, true, false);
		slot = slots.get(1);
		checkSlotAsNaturalClass(slot, "V", false, false, false);

		tf = checkValidDescription("[C] ([V]) ", 2);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsNaturalClass(slot, "C", false, false, true);
		slot = slots.get(1);
		checkSlotAsNaturalClass(slot, "V", false, true, true);

		tf = checkValidDescription("[C] (*[V]) ", 2);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsNaturalClass(slot, "C", false, false, true);
		slot = slots.get(1);
		checkSlotAsNaturalClass(slot, "V", false, true, false);

		tf = checkValidDescription("*[C] (*[V]) ", 2);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsNaturalClass(slot, "C", false, false, false);
		slot = slots.get(1);
		checkSlotAsNaturalClass(slot, "V", false, true, false);

		// Combo
		tf = checkValidDescription("([V]) b ch [C] a (t)", 6);
		slots = tf.getSlots();
		slot = slots.get(0);
		checkSlotAsNaturalClass(slot, "V", false, true, true);
		slot = slots.get(1);
		checkSlotAsSegment(slot, "b", true, false, true);
		slot = slots.get(2);
		checkSlotAsSegment(slot, "ch", true, false, true);
		slot = slots.get(3);
		checkSlotAsNaturalClass(slot, "C", false, false, true);
		slot = slots.get(4);
		checkSlotAsSegment(slot, "a", true, false, true);
		slot = slots.get(5);
		checkSlotAsSegment(slot, "t", true, true, true);
			}

	protected void checkSlotAsSegment(TemplateFilterSlotSegmentOrNaturalClass slot,
			String sSegment, boolean isSegment, boolean isOptional, boolean isObeysSSP) {
		assertEquals(sSegment, slot.getSegmentString());
		assertEquals(isSegment, slot.isSegment());
		assertEquals(isOptional, slot.isOptional());
		assertEquals(isObeysSSP, slot.isObeysSSP());
	}

	protected void checkSlotAsNaturalClass(TemplateFilterSlotSegmentOrNaturalClass slot,
			String sNatClass, boolean isSegment, boolean isOptional, boolean isObeysSSP) {
		CVNaturalClass natClass = slot.getCVNaturalClass();
		assertNotNull(natClass);
		assertEquals(sNatClass, natClass.getNCName());
		assertEquals(isSegment, slot.isSegment());
		assertEquals(isOptional, slot.isOptional());
		assertEquals(isObeysSSP, slot.isObeysSSP());
	}

	private TemplateFilter checkValidDescription(String sEnv, int iItems) {
		TemplateFilterParser parser = parseDescriptionString(sEnv, activeSegments, new TemplateFilter(), false);
		int iNumErrors = parser.getNumberOfSyntaxErrors();
		assertEquals(0, iNumErrors);
		AsheninkaSegmentAndClassListener listener = (AsheninkaSegmentAndClassListener) parser
				.getParseListeners().get(0);
		assertNotNull(listener);
		listener.setupSegmentsMasterList(activeSegments);
		TemplateFilter tf = listener.getTemplateFilter();
		assertNotNull(tf);
		List<TemplateFilterSlotSegmentOrNaturalClass> slots;
		slots = tf.getSlots();
		assertEquals(iItems, slots.size());
		return tf;
	}

	@Test
	public void FiltersTest() {
		TemplateFilter tfilter;
		List<TemplateFilterSlotSegmentOrNaturalClass> slots;
		TemplateFilterSlotSegmentOrNaturalClass slot;

		tfilter = checkFilterDescription("", 0, 1, true);

		tfilter = checkFilterDescription("|", 0, 1, true);

		tfilter = checkFilterDescription("|", 0, 0, false);

		tfilter = checkFilterDescription("| t", 1, 0, true);
		slot = tfilter.getSlots().get(0);
		assertEquals(false, slot.isRepairLeftwardFromHere());

		tfilter = checkFilterDescription("| t", 1, 0, false);
		slot = tfilter.getSlots().get(0);
		assertEquals(false, slot.isRepairLeftwardFromHere());

		tfilter = checkFilterDescription("t l |", 2, 1, true);
		slot = tfilter.getSlots().get(0);
		assertEquals(false, slot.isRepairLeftwardFromHere());
		slot = tfilter.getSlots().get(1);
		assertEquals(false, slot.isRepairLeftwardFromHere());

		tfilter = checkFilterDescription("t l |", 2, 0, false);
		slot = tfilter.getSlots().get(0);
		assertEquals(false, slot.isRepairLeftwardFromHere());
		slot = tfilter.getSlots().get(1);
		assertEquals(false, slot.isRepairLeftwardFromHere());

		tfilter = checkFilterDescription("t | l", 2, 0, true);
		slot = tfilter.getSlots().get(0);
		assertEquals(true, slot.isRepairLeftwardFromHere());
		slot = tfilter.getSlots().get(1);
		assertEquals(false, slot.isRepairLeftwardFromHere());
	}

	private TemplateFilter checkFilterDescription(String sEnv, int iItems, int iErrors, boolean useSlotPosition) {
		TemplateFilterParser parser = parseDescriptionString(sEnv, activeSegments, new Filter(), useSlotPosition);
		int iNumErrors = parser.getNumberOfSyntaxErrors();
		assertEquals(iErrors, iNumErrors);
		AsheninkaSegmentAndClassListener listener = (AsheninkaSegmentAndClassListener) parser
				.getParseListeners().get(0);
		assertNotNull(listener);
		listener.setupSegmentsMasterList(activeSegments);
		TemplateFilter tf = listener.getTemplateFilter();
		assertNotNull(tf);
		assertTrue(tf instanceof Filter);
		List<TemplateFilterSlotSegmentOrNaturalClass> slots;
		slots = tf.getSlots();
		assertEquals(iItems, slots.size());
		return tf;
	}

}
