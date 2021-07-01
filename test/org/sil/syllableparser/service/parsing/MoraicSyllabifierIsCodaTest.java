/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.parsing;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.moraicapproach.MoraicSegmentInSyllable;
import org.sil.syllableparser.model.moraicapproach.MoraicSyllable;

/**
 * @author Andy Black
 *
 */
public class MoraicSyllabifierIsCodaTest extends MoraicSyllabifierTestBase {

	ArrayList<MoraicSegmentInSyllable> segsInSyllable = new ArrayList<MoraicSegmentInSyllable>(
			Arrays.asList());
	MoraicSyllable syl = new MoraicSyllable(null);
	SHSonorityComparer sonorityComparer;
	@Before
	public void setUp() throws Exception {
		syl = new MoraicSyllable(segsInSyllable);
		this.projectFile = Constants.UNIT_TEST_DATA_FILE_MORAIC_IS_CODA;
		super.setUp();
		muSyllabifier = new MoraicSyllabifier(moraicApproach);
		sonorityComparer = new SHSonorityComparer(moraicApproach.getLanguageProject());
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
	public void isCodaTest() {
		checkSyllabification("Ajaa", true, 2, "A.jaa", "μ.σμμ", "(W(σ(μ(\\L a(\\G A))))(σ(O(\\L j(\\G j)))(μ μ(\\L aa(\\G aa)))))");
	}
}
