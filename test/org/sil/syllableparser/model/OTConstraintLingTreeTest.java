/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.model.otapproach.OTConstraint;
import org.sil.syllableparser.model.otapproach.OTStructuralOptions;

/**
 * @author Andy Black
 *
 */
public class OTConstraintLingTreeTest {

	OTConstraint constraint;
	String lingTree = "";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void moreThanOneOptionSetTest() {
		constraint = new OTConstraint();
		// missing
		assertEquals(0, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.INITIALIZED));

		assertEquals(0, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.INITIALIZED
				| OTStructuralOptions.WORD_INITIAL));

		assertEquals(0, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.INITIALIZED
				| OTStructuralOptions.WORD_FINAL));

		assertEquals(0, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.INITIALIZED
				| OTStructuralOptions.WORD_INITIAL | OTStructuralOptions.WORD_FINAL));

		// singletons
		assertEquals(1, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET));
		assertEquals(1, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.NUCLEUS));
		assertEquals(1, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.CODA));
		assertEquals(1, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.UNPARSED));

		assertEquals(1, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.WORD_INITIAL));
		assertEquals(1, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.NUCLEUS
				| OTStructuralOptions.WORD_INITIAL));
		assertEquals(1, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.CODA
				| OTStructuralOptions.WORD_INITIAL));
		assertEquals(1, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.UNPARSED
				| OTStructuralOptions.WORD_INITIAL));

		assertEquals(1, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.WORD_FINAL));
		assertEquals(1, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.NUCLEUS
				| OTStructuralOptions.WORD_FINAL));
		assertEquals(1, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.CODA
				| OTStructuralOptions.WORD_FINAL));
		assertEquals(1, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.UNPARSED
				| OTStructuralOptions.WORD_FINAL));

		assertEquals(1, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.WORD_INITIAL | OTStructuralOptions.WORD_FINAL));
		assertEquals(1, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.NUCLEUS
				| OTStructuralOptions.WORD_INITIAL | OTStructuralOptions.WORD_FINAL));
		assertEquals(1, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.CODA
				| OTStructuralOptions.WORD_INITIAL | OTStructuralOptions.WORD_FINAL));
		assertEquals(1, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.UNPARSED
				| OTStructuralOptions.WORD_INITIAL | OTStructuralOptions.WORD_FINAL));

		// two
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.NUCLEUS));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.CODA));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.UNPARSED));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.NUCLEUS
				| OTStructuralOptions.CODA));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.NUCLEUS
				| OTStructuralOptions.UNPARSED));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.CODA
				| OTStructuralOptions.UNPARSED));

		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.NUCLEUS | OTStructuralOptions.WORD_INITIAL));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.CODA | OTStructuralOptions.WORD_INITIAL));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.UNPARSED | OTStructuralOptions.WORD_INITIAL));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.NUCLEUS
				| OTStructuralOptions.CODA | OTStructuralOptions.WORD_INITIAL));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.NUCLEUS
				| OTStructuralOptions.UNPARSED | OTStructuralOptions.WORD_INITIAL));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.CODA
				| OTStructuralOptions.UNPARSED | OTStructuralOptions.WORD_INITIAL));

		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.NUCLEUS | OTStructuralOptions.WORD_FINAL));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.CODA | OTStructuralOptions.WORD_FINAL));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.UNPARSED | OTStructuralOptions.WORD_FINAL));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.NUCLEUS
				| OTStructuralOptions.CODA | OTStructuralOptions.WORD_FINAL));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.NUCLEUS
				| OTStructuralOptions.UNPARSED | OTStructuralOptions.WORD_FINAL));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.CODA
				| OTStructuralOptions.UNPARSED | OTStructuralOptions.WORD_FINAL));

		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.NUCLEUS | OTStructuralOptions.WORD_INITIAL
				| OTStructuralOptions.WORD_FINAL));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.CODA | OTStructuralOptions.WORD_INITIAL
				| OTStructuralOptions.WORD_FINAL));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.UNPARSED | OTStructuralOptions.WORD_INITIAL
				| OTStructuralOptions.WORD_FINAL));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.NUCLEUS
				| OTStructuralOptions.CODA | OTStructuralOptions.WORD_INITIAL
				| OTStructuralOptions.WORD_FINAL));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.NUCLEUS
				| OTStructuralOptions.UNPARSED | OTStructuralOptions.WORD_INITIAL
				| OTStructuralOptions.WORD_FINAL));
		assertEquals(2, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.CODA
				| OTStructuralOptions.UNPARSED | OTStructuralOptions.WORD_INITIAL
				| OTStructuralOptions.WORD_FINAL));

		// three
		assertEquals(3, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.NUCLEUS | OTStructuralOptions.CODA));
		assertEquals(3, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.NUCLEUS | OTStructuralOptions.UNPARSED));
		assertEquals(3, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.CODA | OTStructuralOptions.UNPARSED));
		assertEquals(3, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.NUCLEUS
				| OTStructuralOptions.CODA | OTStructuralOptions.UNPARSED));

		assertEquals(3, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.NUCLEUS | OTStructuralOptions.CODA
				| OTStructuralOptions.WORD_INITIAL));
		assertEquals(3, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.NUCLEUS | OTStructuralOptions.UNPARSED
				| OTStructuralOptions.WORD_INITIAL));
		assertEquals(3, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.CODA | OTStructuralOptions.UNPARSED
				| OTStructuralOptions.WORD_INITIAL));
		assertEquals(3, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.NUCLEUS
				| OTStructuralOptions.CODA | OTStructuralOptions.UNPARSED
				| OTStructuralOptions.WORD_INITIAL));

		assertEquals(3, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.NUCLEUS | OTStructuralOptions.CODA
				| OTStructuralOptions.WORD_FINAL));
		assertEquals(3, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.NUCLEUS | OTStructuralOptions.UNPARSED
				| OTStructuralOptions.WORD_FINAL));
		assertEquals(3, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.CODA | OTStructuralOptions.UNPARSED
				| OTStructuralOptions.WORD_FINAL));
		assertEquals(3, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.NUCLEUS
				| OTStructuralOptions.CODA | OTStructuralOptions.UNPARSED
				| OTStructuralOptions.WORD_FINAL));

		assertEquals(3, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.NUCLEUS | OTStructuralOptions.CODA
				| OTStructuralOptions.WORD_INITIAL | OTStructuralOptions.WORD_FINAL));
		assertEquals(3, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.NUCLEUS | OTStructuralOptions.UNPARSED
				| OTStructuralOptions.WORD_INITIAL | OTStructuralOptions.WORD_FINAL));
		assertEquals(3, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.CODA | OTStructuralOptions.UNPARSED
				| OTStructuralOptions.WORD_INITIAL | OTStructuralOptions.WORD_FINAL));
		assertEquals(3, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.NUCLEUS
				| OTStructuralOptions.CODA | OTStructuralOptions.UNPARSED
				| OTStructuralOptions.WORD_INITIAL | OTStructuralOptions.WORD_FINAL));

		// four
		assertEquals(4, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.NUCLEUS | OTStructuralOptions.CODA
				| OTStructuralOptions.UNPARSED));

		assertEquals(4, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.NUCLEUS | OTStructuralOptions.CODA
				| OTStructuralOptions.UNPARSED | OTStructuralOptions.WORD_INITIAL));

		assertEquals(4, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.NUCLEUS | OTStructuralOptions.CODA
				| OTStructuralOptions.UNPARSED | OTStructuralOptions.WORD_FINAL));

		assertEquals(4, constraint.getNumberOfCoreOptionsSet(OTStructuralOptions.ONSET
				| OTStructuralOptions.NUCLEUS | OTStructuralOptions.CODA
				| OTStructuralOptions.UNPARSED | OTStructuralOptions.WORD_INITIAL
				| OTStructuralOptions.WORD_FINAL));
	}

	@Test
	public void constraintOneAffectedElementTest() {
		constraint = new OTConstraint("one", "", "[V]", "", OTStructuralOptions.NUCLEUS, 0, "", "", true);
		constraint.createLingTreeDescription();
		assertEquals("(\\O(\\O[V](n)))", constraint.getLingTreeDescription());

		constraint = new OTConstraint("one", "", "[V]", "", OTStructuralOptions.NUCLEUS
				+ OTStructuralOptions.WORD_FINAL, 0, "", "", true);
		constraint.createLingTreeDescription();
		assertEquals("(\\O(\\O[V] #(n)))", constraint.getLingTreeDescription());

		constraint = new OTConstraint("one", "", "[C]", "", OTStructuralOptions.CODA
				+ OTStructuralOptions.WORD_FINAL, 0, "", "", true);
		constraint.createLingTreeDescription();
		assertEquals("(\\O(\\O[C] #(c)))", constraint.getLingTreeDescription());

		constraint = new OTConstraint("one", "", "[C]", "", OTStructuralOptions.WORD_INITIAL
				+ OTStructuralOptions.ONSET + OTStructuralOptions.NUCLEUS
				+ OTStructuralOptions.CODA + OTStructuralOptions.UNPARSED
				+ OTStructuralOptions.WORD_FINAL, 0, "", "", true);
		constraint.createLingTreeDescription();
		assertEquals("(\\O(\\O# [C] #({o, n, c, u})))", constraint.getLingTreeDescription());
	}

	@Test
	public void constraintTwoAffectedElementsTest() {
		String affected1 = "X";
		String affected2 = "X";
		constraint = new OTConstraint("one", "", affected1, affected2, OTStructuralOptions.ONSET,
				OTStructuralOptions.ONSET, "", "", true);
		constraint.createLingTreeDescription();
		assertEquals("(\\O(\\OX(o))(\\OX(o)))", constraint.getLingTreeDescription());

		constraint = new OTConstraint("one", "", affected1, "[C]", OTStructuralOptions.ONSET,
				OTStructuralOptions.ONSET, "", "", true);
		constraint.createLingTreeDescription();
		assertEquals("(\\O(\\OX(o))(\\O[C](o)))", constraint.getLingTreeDescription());

		constraint = new OTConstraint("one", "", affected1, affected2, OTStructuralOptions.ONSET,
				OTStructuralOptions.WORD_INITIAL + OTStructuralOptions.ONSET, "", "", true);
		constraint.createLingTreeDescription();
		assertEquals("(\\O(\\OX(o))(\\O# X(o)))", constraint.getLingTreeDescription());

		constraint = new OTConstraint("one", "", "[V]", "[V]", OTStructuralOptions.ONSET | OTStructuralOptions.CODA,
				OTStructuralOptions.ONSET, "", "", true);
		constraint.createLingTreeDescription();
		assertEquals("(\\O(\\O[V]({o, c}))(\\O[V](o)))", constraint.getLingTreeDescription());

	}
}
