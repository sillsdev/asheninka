/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service;

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
public class OTConstraintValidatorTest {

	OTConstraintValidator validator;
	OTConstraint constraint;
	String errorProperty = "";

	@Before
	public void setUp() throws Exception {
		validator = OTConstraintValidator.getInstance();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void constraintOneAffectedElementTest() {
		constraint = new OTConstraint("one", "", "[V]", "", OTStructuralOptions.NUCLEUS, 0, "", "", true);
		validator.setConstraint(constraint);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessageProperty());

		constraint = new OTConstraint("one", "", "[V]", "", OTStructuralOptions.WORD_INITIAL, 0,
				"", "", true);
		validator.setConstraint(constraint);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("otconstraint.message.element1.invalidoptions",
				validator.getErrorMessageProperty());

		constraint = new OTConstraint("one", "", "[V]", "", OTStructuralOptions.WORD_FINAL, 0, "",
				"", true);
		validator.setConstraint(constraint);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("otconstraint.message.element1.invalidoptions",
				validator.getErrorMessageProperty());

		constraint = new OTConstraint("one", "", "[V]", "", OTStructuralOptions.WORD_INITIAL
				+ OTStructuralOptions.WORD_FINAL, 0, "", "", true);
		validator.setConstraint(constraint);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("otconstraint.message.element1.invalidoptions",
				validator.getErrorMessageProperty());

		constraint = new OTConstraint("one", "", "", "", OTStructuralOptions.WORD_INITIAL
				+ OTStructuralOptions.WORD_FINAL, 0, "", "", true);
		validator.setConstraint(constraint);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("otconstraint.message.element1.cannotbeempty",
				validator.getErrorMessageProperty());

		constraint = new OTConstraint("one", "", "[V]", "", OTStructuralOptions.NUCLEUS
				+ OTStructuralOptions.WORD_FINAL, 0, "", "", true);
		validator.setConstraint(constraint);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessageProperty());

		constraint = new OTConstraint("one", "", "[C]", "", OTStructuralOptions.CODA
				+ OTStructuralOptions.WORD_FINAL, 0, "", "", true);
		validator.setConstraint(constraint);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessageProperty());

		constraint = new OTConstraint("one", "", "[C]", "", OTStructuralOptions.WORD_INITIAL
				+ OTStructuralOptions.ONSET + OTStructuralOptions.NUCLEUS
				+ OTStructuralOptions.CODA + OTStructuralOptions.UNPARSED
				+ OTStructuralOptions.WORD_FINAL, 0, "", "", true);
		validator.setConstraint(constraint);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessageProperty());

	}

	@Test
	public void constraintTwoAffectedElementsTest() {
		// action Attach cannot be level All and has both an affected item and a
		// context item
		String affected1 = null;
		String affected2 = null;
		constraint = new OTConstraint("one", "", affected1, affected2, OTStructuralOptions.ONSET,
				OTStructuralOptions.ONSET, "", "", true);
		validator.setConstraint(constraint);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessageProperty());

		constraint = new OTConstraint("one", "", affected1, "[C]", OTStructuralOptions.ONSET,
				OTStructuralOptions.ONSET, "", "", true);
		validator.setConstraint(constraint);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessageProperty());

		constraint = new OTConstraint("one", "", affected1, affected2, OTStructuralOptions.WORD_INITIAL + OTStructuralOptions.ONSET,
				OTStructuralOptions.ONSET, "", "", true);
		validator.setConstraint(constraint);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessageProperty());

		constraint = new OTConstraint("one", "", affected1, affected2, OTStructuralOptions.ONSET,
				0, "", "", true);
		validator.setConstraint(constraint);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("otconstraint.message.element2.invalidoptions", validator.getErrorMessageProperty());

		constraint = new OTConstraint("one", "", affected1, affected2, OTStructuralOptions.ONSET,
				OTStructuralOptions.WORD_FINAL, "", "", true);
		validator.setConstraint(constraint);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("otconstraint.message.element2.invalidoptions", validator.getErrorMessageProperty());

		constraint = new OTConstraint("one", "", affected1, "", OTStructuralOptions.ONSET,
				OTStructuralOptions.ONSET, "", "", true);
		validator.setConstraint(constraint);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("otconstraint.message.element2.optionsignored", validator.getErrorMessageProperty());

		constraint = new OTConstraint("one", "", affected1, affected2, OTStructuralOptions.ONSET + OTStructuralOptions.WORD_FINAL,
				OTStructuralOptions.ONSET + OTStructuralOptions.WORD_FINAL, "", "", true);
		validator.setConstraint(constraint);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("otconstraint.message.element1.nowordfinal", validator.getErrorMessageProperty());

		constraint = new OTConstraint("one", "", affected1, affected2, OTStructuralOptions.ONSET + OTStructuralOptions.WORD_INITIAL,
				OTStructuralOptions.ONSET + OTStructuralOptions.WORD_INITIAL, "", "", true);
		validator.setConstraint(constraint);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("otconstraint.message.element2.nowordinitial", validator.getErrorMessageProperty());

		constraint = new OTConstraint("one", "", affected1, "", OTStructuralOptions.ONSET,
				OTStructuralOptions.WORD_FINAL, "", "", true);
		validator.setConstraint(constraint);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("otconstraint.message.element2.wordfinalignored", validator.getErrorMessageProperty());

	}
}
