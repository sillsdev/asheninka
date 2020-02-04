/**
 * Copyright (c) 2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.parsing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.FilterType;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.oncapproach.Coda;
import org.sil.syllableparser.model.oncapproach.Nucleus;
import org.sil.syllableparser.model.oncapproach.ONCConstituent;
import org.sil.syllableparser.model.oncapproach.ONCSegmentInSyllable;
import org.sil.syllableparser.model.oncapproach.ONCSyllabificationStatus;
import org.sil.syllableparser.model.oncapproach.ONCSyllable;
import org.sil.syllableparser.model.oncapproach.ONCTracingStep;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;

/**
 * @author Andy Black
 *
 */
public class ONCSyllabifierWithFilterTest extends ONCSyllabifierBase {

	ArrayList<ONCSegmentInSyllable> segsInSyllable = new ArrayList<ONCSegmentInSyllable>(
			Arrays.asList());
	ONCSyllable syl = new ONCSyllable(null);
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.service.parsing.ONCSyllabifierTest#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		syl = new ONCSyllable(segsInSyllable);
		this.projectFile = Constants.UNIT_TEST_DATA_FILE_TEMPLATES_FILTERS;
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sil.syllableparser.service.parsing.ONCSyllabifierTest#tearDown()
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void checkFailFilterTest() {
		List<Filter> failFilters = (List<Filter>) languageProject
				.getActiveAndValidFilters()
				.stream()
				.filter(f -> f.getTemplateFilterType() == FilterType.NUCLEUS
						&& !f.getAction().isDoRepair()).collect(Collectors.toList());
		Nucleus nuc = new Nucleus();
		doCheckFilterFail("", 0, failFilters, nuc, ONCType.NUCLEUS, ONCType.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		doCheckFilterFail("a", 0, failFilters, nuc, ONCType.NUCLEUS, ONCType.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		doCheckFilterFail("t", 1, failFilters, nuc, ONCType.NUCLEUS, ONCType.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		doCheckFilterFail("ta", 1, failFilters, nuc, ONCType.NUCLEUS, ONCType.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		ONCSegmentInSyllable segInSyl = createONCSegmentInSyllable("a");
		nuc.add(segInSyl);
		doCheckFilterFail("a", 0, failFilters, nuc, ONCType.NUCLEUS, ONCType.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		doCheckFilterFail("ta", 1, failFilters, nuc, ONCType.NUCLEUS, ONCType.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		segInSyl = createONCSegmentInSyllable("e");
		nuc.add(segInSyl);
		doCheckFilterFail("ae", 1, failFilters, nuc, ONCType.NUCLEUS, ONCType.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		doCheckFilterFail("tae", 2, failFilters, nuc, ONCType.NUCLEUS, ONCType.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		segInSyl = createONCSegmentInSyllable("i");
		nuc.add(segInSyl);
		doCheckFilterFail("aei", 1, failFilters, nuc, ONCType.NUCLEUS, ONCType.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		doCheckFilterFail("aei", 2, failFilters, nuc, ONCType.NUCLEUS, ONCType.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		doCheckFilterFail("taei", 2, failFilters, nuc, ONCType.NUCLEUS, ONCType.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);
		doCheckFilterFail("taei", 3, failFilters, nuc, ONCType.NUCLEUS, ONCType.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED);

		for (Filter f : failFilters) {
			f.setTemplateFilterType(FilterType.CODA);
		}
		Coda coda = new Coda();
		doCheckFilterFail("", 0, failFilters, coda, ONCType.CODA, ONCType.CODA,
				ONCSyllabificationStatus.CODA_FILTER_FAILED);
		doCheckFilterFail("a", 0, failFilters, coda, ONCType.CODA, ONCType.CODA,
				ONCSyllabificationStatus.CODA_FILTER_FAILED);
		segInSyl = createONCSegmentInSyllable("a");
		coda.add(segInSyl);
		doCheckFilterFail("a", 0, failFilters, coda, ONCType.CODA, ONCType.CODA,
				ONCSyllabificationStatus.CODA_FILTER_FAILED);
		segInSyl = createONCSegmentInSyllable("e");
		coda.add(segInSyl);
		doCheckFilterFail("ae", 1, failFilters, coda, ONCType.CODA, ONCType.FILTER_FAILED,
				ONCSyllabificationStatus.CODA_FILTER_FAILED);
		segInSyl = createONCSegmentInSyllable("i");
		coda.add(segInSyl);
		doCheckFilterFail("aei", 1, failFilters, coda, ONCType.CODA, ONCType.FILTER_FAILED,
				ONCSyllabificationStatus.CODA_FILTER_FAILED);
		doCheckFilterFail("aei", 2, failFilters, coda, ONCType.CODA, ONCType.FILTER_FAILED,
				ONCSyllabificationStatus.CODA_FILTER_FAILED);

	}

	public ONCSegmentInSyllable createONCSegmentInSyllable(String segString) {
		Optional<Segment> optSegment = languageProject.getActiveSegmentsInInventory().stream()
				.filter(s -> s.getSegment().equals(segString)).findFirst();
		assertNotNull(optSegment);
		Segment seg = optSegment.get();
		assertNotNull(seg);
		ONCSegmentInSyllable segInSyl = new ONCSegmentInSyllable(seg, segString);
		return segInSyl;
	}

	public void doCheckFilterFail(String word, int iSegmentInWord, List<Filter> failFilters,
			ONCConstituent constituent, ONCType currentType, ONCType expectedType,
			ONCSyllabificationStatus tracingStatus) {
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		boolean fSuccess = segResult.success;
		assertEquals(true, fSuccess);
		List<ONCSegmentInSyllable> segmentsInWord = (List<ONCSegmentInSyllable>) segmenter
				.getSegmentsInWord();
		ONCType returnType = oncSyllabifier.checkFailFilters(segmentsInWord, iSegmentInWord,
				currentType, failFilters, constituent, syl, tracingStatus);
		assertEquals(expectedType, returnType);
	}

	public void doCheckFilterFailWithTrace(String word, int iSegmentInWord,
			List<Filter> failFilters, ONCConstituent constituent, ONCType currentType,
			ONCType expectedType, ONCSyllabificationStatus status, int iTracingSteps) {
		oncSyllabifier.getTracingSteps().clear();
		doCheckFilterFail(word, iSegmentInWord, failFilters, constituent, currentType,
				expectedType, status);
		List<ONCTracingStep> tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(iTracingSteps, tracingSteps.size());
		if (iTracingSteps > 0) {
			ONCTracingStep tracingStep = tracingSteps.get(0);
			assertEquals(status, tracingStep.getStatus());
		}
	}

	@Test
	public void checkFailFilterTraceTest() {
		oncSyllabifier.setDoTrace(true);
		oncSyllabifier.setTracingStep(new ONCTracingStep());
		List<Filter> failFilters = (List<Filter>) languageProject
				.getActiveAndValidFilters()
				.stream()
				.filter(f -> f.getTemplateFilterType() == FilterType.NUCLEUS
						&& !f.getAction().isDoRepair()).collect(Collectors.toList());
		Nucleus nuc = new Nucleus();
		doCheckFilterFailWithTrace("", 0, failFilters, nuc, ONCType.NUCLEUS, ONCType.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 0);
		doCheckFilterFailWithTrace("a", 0, failFilters, nuc, ONCType.NUCLEUS, ONCType.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 0);
		doCheckFilterFailWithTrace("t", 1, failFilters, nuc, ONCType.NUCLEUS, ONCType.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 0);
		doCheckFilterFailWithTrace("ta", 1, failFilters, nuc, ONCType.NUCLEUS, ONCType.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 0);
		ONCSegmentInSyllable segInSyl = createONCSegmentInSyllable("a");
		nuc.add(segInSyl);
		doCheckFilterFailWithTrace("a", 0, failFilters, nuc, ONCType.NUCLEUS, ONCType.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 0);
		doCheckFilterFailWithTrace("ta", 1, failFilters, nuc, ONCType.NUCLEUS, ONCType.NUCLEUS,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 0);
		segInSyl = createONCSegmentInSyllable("e");
		nuc.add(segInSyl);
		doCheckFilterFailWithTrace("ae", 1, failFilters, nuc, ONCType.NUCLEUS, ONCType.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 1);
		doCheckFilterFailWithTrace("tae", 2, failFilters, nuc, ONCType.NUCLEUS, ONCType.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 1);
		segInSyl = createONCSegmentInSyllable("i");
		nuc.add(segInSyl);
		doCheckFilterFailWithTrace("aei", 1, failFilters, nuc, ONCType.NUCLEUS, ONCType.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 1);
		doCheckFilterFailWithTrace("aei", 2, failFilters, nuc, ONCType.NUCLEUS, ONCType.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 1);
		doCheckFilterFailWithTrace("taei", 2, failFilters, nuc, ONCType.NUCLEUS, ONCType.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 1);
		doCheckFilterFailWithTrace("taei", 3, failFilters, nuc, ONCType.NUCLEUS, ONCType.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, 1);
	}

	@Test
	public void filterFailTest() {
		assertEquals(2, languageProject.getActiveAndValidFilters().size());
		Filter f = languageProject.getActiveAndValidFilters().get(1);
		assertEquals(false, f.getAction().isDoRepair());
		assertEquals(FilterType.NUCLEUS, f.getTemplateFilterType());
		checkSyllabification("eɪoɹtə", false, 1, "eɪo", "nn",
				"(W(σ(R(N(\\L eɪ(\\G eɪ))(\\L o(\\G o))))))");
	}

	@Test
	public void traceFilterFailTest() {
		oncSyllabifier.setDoTrace(true);
		Filter f = languageProject.getActiveAndValidFilters().get(1);
		assertEquals(false, f.getAction().isDoRepair());
		assertEquals(FilterType.NUCLEUS, f.getTemplateFilterType());
		checkSyllabifyWord("eɪoɹtə", false, 1, "eɪo", "nn",
				"(W(σ(R(N(\\L eɪ(\\G eɪ))(\\L o(\\G o))))))");
		List<ONCTracingStep> tracingSteps = oncSyllabifier.getTracingSteps();
		assertEquals(4, tracingSteps.size());
		ONCTracingStep tracingStep = tracingSteps.get(0);
		checkTracingStep(tracingStep, "eɪ", "Vowels", "o", "Vowels", SHComparisonResult.EQUAL,
				ONCType.UNKNOWN, ONCSyllabificationStatus.SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET,
				false);
		tracingStep = tracingSteps.get(1);
		checkTracingStep(tracingStep, "eɪ", "Vowels", "o", "Vowels", SHComparisonResult.EQUAL,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(2);
		checkTracingStep(tracingStep, "o", "Vowels", "ɹ", "Liquids", SHComparisonResult.MORE,
				ONCType.NUCLEUS, ONCSyllabificationStatus.ADDED_AS_NUCLEUS, true);
		tracingStep = tracingSteps.get(3);
		checkTracingStep(tracingStep, null, null, null, null, null, ONCType.FILTER_FAILED,
				ONCSyllabificationStatus.NUCLEUS_FILTER_FAILED, false);
		assertEquals(f, tracingStep.getTemplateFilterUsed());
	}

	@Test
	public void filterRepairTest() {
		assertEquals(2, languageProject.getActiveAndValidFilters().size());
		Filter f = languageProject.getActiveAndValidFilters().get(0);
		assertEquals(true, f.getAction().isDoRepair());
		assertEquals(FilterType.ONSET, f.getTemplateFilterType());
		checkSyllabification("ætlæntɪk", true, 3, "æt.læn.tɪk", "nc.onc.onc",
				"(W(σ(R(N(\\L æ(\\G æ)))(C(\\L t(\\G t)))))(σ(O(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
		checkSyllabification("tlæntɪk", true, 2, "tlæn.tɪk", "oonc.onc",
				"(W(σ(O(\\L t(\\G t))(\\L l(\\G l)))(R(N(\\L æ(\\G æ)))(C(\\L n(\\G n)))))(σ(O(\\L t(\\G t)))(R(N(\\L ɪ(\\G ɪ)))(C(\\L k(\\G k))))))");
	}
}
