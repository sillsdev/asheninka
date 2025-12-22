/**
 * Copyright (c) 2025 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service;

import static org.junit.Assert.*;

import java.util.ResourceBundle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.hyphenapproach.HyphenApproach;
import org.sil.syllableparser.model.hyphenapproach.HyphenChangeRule;
import org.sil.syllableparser.model.hyphenapproach.HyphenClass;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public class HyphenChangeRuleValidatorTest {

	HyphenApproach hyphenApproach = new HyphenApproach();
	HyphenChangeRuleValidator validator;
	HyphenChangeRule changeRule;
	String errorProperty = "";
	SimpleListProperty<HyphenClass> matches;
	SimpleListProperty<HyphenClass> changes;

	HyphenClass v = new HyphenClass("V", null, "", "");
	HyphenClass c = new HyphenClass("C", null, "", "");
	HyphenClass r = new HyphenClass("R", null, "", "");
	HyphenClass insertHC = new HyphenClass("insertHC", null, "", Constants.SPECIAL_INSERT_CODE);
	HyphenClass wordBoundary = new HyphenClass("insertHC", null, "", Constants.SPECIAL_WORD_BOUNDARY_CODE);

	@Before
	public void setUp() throws Exception {
		validator = HyphenChangeRuleValidator.getInstance();
		validator.setBundle(ResourceBundle.getBundle(Constants.RESOURCE_LOCATION));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void hyphenChangeRuleTest() {
		// both empty: pass
		matches = createSequence();
		changes = createSequence();
		changeRule = new HyphenChangeRule("", "", matches, changes);
		validator.setChangeRule(changeRule);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessage());

		// change is empty: fail
		matches = createSequence(v);
		changes = createSequence();
		changeRule = new HyphenChangeRule("V", "", matches, changes);
		validator.setChangeRule(changeRule);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("The number of classes in match and change must differ at most by one insert hyphen.\n",
				validator.getErrorMessage());

		// match is empty: fail
		matches = createSequence();
		changes = createSequence(v, insertHC);
		changeRule = new HyphenChangeRule("", "V-", matches, changes);
		validator.setChangeRule(changeRule);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("The number of classes in match and change must differ at most by one insert hyphen.\n"
				+ "When a change has an insert hyphen, the class names and orders\n"
				+ " must be the same between the match and change classes.\n", validator.getErrorMessage());

		// change has insert, but not all classes are the same: fail
		matches = createSequence(v, c);
		changes = createSequence(v, insertHC, v);
		changeRule = new HyphenChangeRule("VC", "V-V", matches, changes);
		validator.setChangeRule(changeRule);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("When a change has an insert hyphen, the class names and orders\n"
				+ " must be the same between the match and change classes.\n", validator.getErrorMessage());

		// change has insert, but not all classes are the same: fail
		matches = createSequence(v, c);
		changes = createSequence(v, insertHC);
		changeRule = new HyphenChangeRule("VC", "V-", matches, changes);
		validator.setChangeRule(changeRule);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("When a change has an insert hyphen, the class names and orders\n"
				+ " must be the same between the match and change classes.\n", validator.getErrorMessage());

		// change same as match, but no insert: fail
		matches = createSequence(v, c);
		changes = createSequence(v, c);
		changeRule = new HyphenChangeRule("VC", "VC", matches, changes);
		validator.setChangeRule(changeRule);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("When a change has no insert hyphen, it can have only one class.\n", validator.getErrorMessage());

		// single change, no insert: pass
		matches = createSequence(r);
		changes = createSequence(c);
		changeRule = new HyphenChangeRule("R", "C", matches, changes);
		validator.setChangeRule(changeRule);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessage());

		// change has insert and classes match: pass
		matches = createSequence(v, c);
		changes = createSequence(v, insertHC, c);
		changeRule = new HyphenChangeRule("VC", "V-C", matches, changes);
		validator.setChangeRule(changeRule);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessage());

		// change has insert and word initial and classes match: pass
		matches = createSequence(wordBoundary, v, c);
		changes = createSequence(wordBoundary, v, insertHC, c);
		changeRule = new HyphenChangeRule("#VC", "#V-C", matches, changes);
		validator.setChangeRule(changeRule);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessage());

		// match has word initial, but change does not: fail
		matches = createSequence(wordBoundary, v, c);
		changes = createSequence(v, insertHC, c);
		changeRule = new HyphenChangeRule("#VC", "V-C", matches, changes);
		validator.setChangeRule(changeRule);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("When a change has an insert hyphen, the class names and orders\n"
				+ " must be the same between the match and change classes.\n", validator.getErrorMessage());

		// change has word initial, but match does not: fail
		matches = createSequence(v, c);
		changes = createSequence(wordBoundary, v, insertHC, c);
		changeRule = new HyphenChangeRule("#VC", "V-C", matches, changes);
		validator.setChangeRule(changeRule);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("The number of classes in match and change must differ at most by one insert hyphen.\n"
				+ "When a change has an insert hyphen, the class names and orders\n"
				+ " must be the same between the match and change classes.\n", validator.getErrorMessage());

		// change has insert and word initial and classes match: pass
		matches = createSequence(v, c, wordBoundary);
		changes = createSequence(v, insertHC, c, wordBoundary);
		changeRule = new HyphenChangeRule("VC#", "V-C#", matches, changes);
		validator.setChangeRule(changeRule);
		validator.validate();
		assertTrue(validator.isValid());
		assertEquals("", validator.getErrorMessage());

		// match has word final, but change does not: fail
		matches = createSequence(v, c, wordBoundary);
		changes = createSequence(v, insertHC, c);
		changeRule = new HyphenChangeRule("#VC", "V-C", matches, changes);
		validator.setChangeRule(changeRule);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("When a change has an insert hyphen, the class names and orders\n"
				+ " must be the same between the match and change classes.\n", validator.getErrorMessage());

		// change has word initial, but match does not: fail
		matches = createSequence(v, c);
		changes = createSequence(v, insertHC, c, wordBoundary);
		changeRule = new HyphenChangeRule("#VC", "V-C", matches, changes);
		validator.setChangeRule(changeRule);
		validator.validate();
		assertFalse(validator.isValid());
		assertEquals("The number of classes in match and change must differ at most by one insert hyphen.\n"
				+ "When a change has an insert hyphen, the class names and orders\n"
				+ " must be the same between the match and change classes.\n", validator.getErrorMessage());
	}

	private SimpleListProperty<HyphenClass> createSequence(HyphenClass... c) {
		// not able to add directly to SimpleListProperty; need to use observable list,
		// first
		ObservableList<HyphenClass> seqOL = FXCollections.observableArrayList();
		for (HyphenClass hc : c) {
			seqOL.add(hc);
		}
		return new SimpleListProperty<>(seqOL);
	}
}
