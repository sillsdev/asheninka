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
import org.sil.syllableparser.model.cvapproach.CVSegmentOrNaturalClass;
import org.sil.syllableparser.model.npapproach.NPRule;
import org.sil.syllableparser.model.npapproach.NPRuleAction;
import org.sil.syllableparser.model.npapproach.NPRuleLevel;

/**
 * @author Andy Black
 *
 */
public class NPRuleValidatorTest {

	NPRuleValidator validator;
	NPRule rule;
	
	@Before
	public void setUp() throws Exception {
		validator = NPRuleValidator.getInstance();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void ruleBuildAllTest() {
		// action Build must be level All with an affected item but not a context item
		rule = new NPRule("build", "", new CVSegmentOrNaturalClass(), null, NPRuleAction.BUILD, NPRuleLevel.ALL, true, false, "");
		validator.setRule(rule);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessageProperty());
		
		rule.setRuleLevel(NPRuleLevel.N);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("nprule.message.buildall", validator.getErrorMessageProperty());
		rule.setRuleLevel(NPRuleLevel.N_BAR);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("nprule.message.buildall", validator.getErrorMessageProperty());
		rule.setRuleLevel(NPRuleLevel.N_DOUBLE_BAR);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("nprule.message.buildall", validator.getErrorMessageProperty());
		rule.setRuleLevel(NPRuleLevel.ALL);
		rule.setContextSegOrNC(new CVSegmentOrNaturalClass());
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("nprule.message.buildall", validator.getErrorMessageProperty());
		rule.setContextSegOrNC(null);
		rule.setAffectedSegOrNC(null);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("nprule.message.buildall", validator.getErrorMessageProperty());
	}

	@Test
	public void ruleAttachTest() {
		// action Attach cannot be level All and has both an affected item and a context item
		rule = new NPRule("attach", "", new CVSegmentOrNaturalClass(),
				new CVSegmentOrNaturalClass(), NPRuleAction.ATTACH, NPRuleLevel.N, true, false, "");
		validator.setRule(rule);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessageProperty());
		rule.setRuleLevel(NPRuleLevel.N_BAR);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessageProperty());
		rule.setRuleLevel(NPRuleLevel.N_DOUBLE_BAR);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessageProperty());
		
		rule.setRuleLevel(NPRuleLevel.ALL);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("nprule.message.attach", validator.getErrorMessageProperty());
		rule.setRuleLevel(NPRuleLevel.N_BAR);
		rule.setContextSegOrNC(null);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("nprule.message.attach", validator.getErrorMessageProperty());
		rule.setContextSegOrNC(new CVSegmentOrNaturalClass());
		rule.setAffectedSegOrNC(null);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("nprule.message.attach", validator.getErrorMessageProperty());
	}

	@Test
	public void ruleLeftAdjoinTest() {
		// action Left Adjoin cannot be level All and has both an affected item and a context item
		rule = new NPRule("left adjoin", "", new CVSegmentOrNaturalClass(),
				new CVSegmentOrNaturalClass(), NPRuleAction.LEFT_ADJOIN, NPRuleLevel.N, true, false, "");
		validator.setRule(rule);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessageProperty());
		rule.setRuleLevel(NPRuleLevel.N_BAR);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessageProperty());
		rule.setRuleLevel(NPRuleLevel.N_DOUBLE_BAR);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessageProperty());
		
		rule.setRuleLevel(NPRuleLevel.ALL);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("nprule.message.adjoin", validator.getErrorMessageProperty());
		rule.setRuleLevel(NPRuleLevel.N_BAR);
		rule.setContextSegOrNC(null);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("nprule.message.adjoin", validator.getErrorMessageProperty());
		rule.setContextSegOrNC(new CVSegmentOrNaturalClass());
		rule.setAffectedSegOrNC(null);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("nprule.message.adjoin", validator.getErrorMessageProperty());
	}

	@Test
	public void ruleRightAdjoinTest() {
		// action Right Adjoin cannot be level All and has both an affected item and a context item
		rule = new NPRule("right adjoin", "", new CVSegmentOrNaturalClass(),
				new CVSegmentOrNaturalClass(), NPRuleAction.RIGHT_ADJOIN, NPRuleLevel.N, true, false, "");
		validator.setRule(rule);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessageProperty());
		rule.setRuleLevel(NPRuleLevel.N_BAR);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessageProperty());
		rule.setRuleLevel(NPRuleLevel.N_DOUBLE_BAR);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessageProperty());
		
		rule.setRuleLevel(NPRuleLevel.ALL);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("nprule.message.adjoin", validator.getErrorMessageProperty());
		rule.setRuleLevel(NPRuleLevel.N_BAR);
		rule.setContextSegOrNC(null);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("nprule.message.adjoin", validator.getErrorMessageProperty());
		rule.setContextSegOrNC(new CVSegmentOrNaturalClass());
		rule.setAffectedSegOrNC(null);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("nprule.message.adjoin", validator.getErrorMessageProperty());
	}

}
