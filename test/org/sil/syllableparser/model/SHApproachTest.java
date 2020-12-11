// Copyright (c) 2016-2018 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SegmentInSHNaturalClass;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter and CVNaturalClasser
 *         classes are functioning correctly
 */
public class SHApproachTest {

	SHApproach sha;
	private LanguageProject languageProject;

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
		sha = languageProject.getSHApproach();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// make sure the setup is what we expect
	@Test
	public void getActiveItemsTest() {
		assertEquals("Sonority hierarchy size", 6, sha.getSHSonorityHierarchy().size());
		assertEquals("Active sonority hierarchy levels size", 5, sha.getActiveSHNaturalClasses().size());
	}

	@Test
	public void missingSegmentsInSonorityHierarchyTest() {
		Set<Segment> missingSegments = sha.getMissingSegmentsFromSonorityHierarchy();
		assertEquals(1, missingSegments.size());
		Segment segs[] = new Segment[missingSegments.size()];
		segs = missingSegments.toArray(segs);
		assertEquals("b", segs[0].getSegment());
		List<SHNaturalClass> ncs = sha.getActiveSHNaturalClasses();
		ncs.get(0).getSegments().remove(1);
		ncs.get(0).getSegments().remove(0);
		missingSegments = sha.getMissingSegmentsFromSonorityHierarchy();
		assertEquals(3, missingSegments.size());
		segs = missingSegments.toArray(segs);
		assertEquals("b", segs[0].getSegment());
		assertEquals("e", segs[1].getSegment());
		assertEquals("a", segs[2].getSegment());
	}

	@Test
	public void duplicateSegmentsInSonorityHierarchyTest() {
		List<SegmentInSHNaturalClass> duplicateSegments = sha.getDuplicateSegmentsFromSonorityHierarchy();
		assertEquals(0, duplicateSegments.size());
		List<SHNaturalClass> ncs = sha.getActiveSHNaturalClasses();
		Segment seg = ncs.get(0).getSegments().get(0);
		ncs.get(0).getSegments().add(seg);
		ncs.get(1).getSegments().add(seg);
		duplicateSegments = sha.getDuplicateSegmentsFromSonorityHierarchy();
		assertEquals(3, duplicateSegments.size());
		SegmentInSHNaturalClass segInClass = duplicateSegments.get(0);
		assertEquals("a", segInClass.getSegment().getSegment());
		assertEquals("Vowels", segInClass.getNaturalClass().getNCName());
		segInClass = duplicateSegments.get(1);
		assertEquals("a", segInClass.getSegment().getSegment());
		assertEquals("Vowels", segInClass.getNaturalClass().getNCName());
		segInClass = duplicateSegments.get(2);
		assertEquals("a", segInClass.getSegment().getSegment());
		assertEquals("Glides", segInClass.getNaturalClass().getNCName());
	}
}
