// Copyright (c) 2016-2025 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model;

import static org.junit.Assert.*;

import java.util.UUID;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.model.cvapproach.*;

/**
 * @author Andy Black
 *
 */
public class SylParserObjectTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void findIndexInListByUuidTest() {
		ObservableList<Segment> cvSegmentInventory = FXCollections.observableArrayList();
		ObservableList<CVNaturalClass> cvNaturalClasses = FXCollections.observableArrayList();
		Segment segA = new Segment("a", "a A", "low mid unrounded vowel", "0");
		Segment segB = new Segment("b", "b B", "voiced bilabial stop", "0");
		Segment segD = new Segment("d", "d D", "voiced alveolar stop", "0");
		cvSegmentInventory.add(segA);
		cvSegmentInventory.add(segB);
		cvSegmentInventory.add(segD);
		CVNaturalClass ncC = new CVNaturalClass("C", null, "Consonants", "bd");
		CVNaturalClass ncV = new CVNaturalClass("V", null, "Vowels", "a");
		cvNaturalClasses.add(ncC);
		cvNaturalClasses.add(ncV);
		
		int index = Segment.findIndexInListByUuid(cvSegmentInventory, segA.getID());
		assertEquals("Expected 0", 0, index);
		index = Segment.findIndexInListByUuid(cvSegmentInventory, segB.getID());
		assertEquals("Expected 1", 1, index);
		index = Segment.findIndexInListByUuid(cvSegmentInventory, segD.getID());
		assertEquals("Expected 2", 2, index);
		String uuidNew = UUID.randomUUID().toString();
		index = Segment.findIndexInListByUuid(cvSegmentInventory, uuidNew);
		assertEquals("Expected -1", -1, index);
		index = CVNaturalClass.findIndexInListByUuid(cvNaturalClasses, ncC.getID());
		assertEquals("Expected 0", 0, index);
		index = CVNaturalClass.findIndexInListByUuid(cvNaturalClasses, ncV.getID());
		assertEquals("Expected 1", 1, index);
		index = CVNaturalClass.findIndexInListByUuid(cvNaturalClasses, uuidNew);
		assertEquals("Expected -1", -1, index);
	}

}
