// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model;

import static org.junit.Assert.*;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.model.Environment;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.*;

;

/**
 * @author Andy Black
 *
 */
public class CVSegmentOrNaturalClassTest {
	private ObservableList<Segment> cvSegmentInventory = FXCollections.observableArrayList();
	private ObservableList<CVNaturalClass> cvNaturalClasses = FXCollections.observableArrayList();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Grapheme aLower = new Grapheme("a", "lowercase a", new SimpleListProperty<Environment>(),
				"", true);
		Grapheme aUpper = new Grapheme("A", "uppercase a", new SimpleListProperty<Environment>(),
				"", true);
		Grapheme bLower = new Grapheme("b", "lowercase b", new SimpleListProperty<Environment>(),
				"", true);
		Grapheme bUpper = new Grapheme("B", "uppercase b", new SimpleListProperty<Environment>(),
				"", true);
		Grapheme dLower = new Grapheme("d", "lowercase d", new SimpleListProperty<Environment>(),
				"", true);
		Grapheme dUpper = new Grapheme("D", "uppercase d", new SimpleListProperty<Environment>(),
				"", true);
		ObservableList<Grapheme> aGraphemes = FXCollections.observableArrayList();
		aGraphemes.add(aLower);
		aGraphemes.add(aUpper);
		ObservableList<Grapheme> bGraphemes = FXCollections.observableArrayList();
		bGraphemes.add(bLower);
		bGraphemes.add(bUpper);
		ObservableList<Grapheme> dGraphemes = FXCollections.observableArrayList();
		dGraphemes.add(dLower);
		dGraphemes.add(dUpper);
		Segment segA = new Segment("a", "a A", "low mid unrounded vowel");
		segA.setGraphs(aGraphemes);
		Segment segB = new Segment("b", "b B", "voiced bilabial stop");
		segB.setGraphs(bGraphemes);
		Segment segD = new Segment("d", "d D", "voiced alveolar stop");
		segD.setGraphs(dGraphemes);
		cvSegmentInventory.add(segA);
		cvSegmentInventory.add(segB);
		cvSegmentInventory.add(segD);
		cvNaturalClasses.add(new CVNaturalClass("C", null, "Consonants", "bd"));
		cvNaturalClasses.add(new CVNaturalClass("V", null, "Vowels", "a"));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void createSegmentOrNaturalClassList() {
		ObservableList<CVSegmentOrNaturalClass> cvSegmentOrNaturalClass = FXCollections
				.observableArrayList();
		for (Segment cvSegment : cvSegmentInventory) {
			CVSegmentOrNaturalClass segOrNC = new CVSegmentOrNaturalClass(cvSegment.getSegment(),
					cvSegment.getDescription(), true, null, true);
			cvSegmentOrNaturalClass.add(segOrNC);
		}
		for (CVNaturalClass cvNaturalClass : cvNaturalClasses) {
			CVSegmentOrNaturalClass segOrNC = new CVSegmentOrNaturalClass(
					cvNaturalClass.getNCName(), cvNaturalClass.getDescription(), true, null, true);
			cvSegmentOrNaturalClass.add(segOrNC);
		}
		assertEquals("Expected count to be 5", 5, cvSegmentOrNaturalClass.size());
		CVSegmentOrNaturalClass segOrNC = cvSegmentOrNaturalClass.get(0);
		assertEquals("Expected first item to be a", "a", segOrNC.getSegmentOrNaturalClass());
		segOrNC = cvSegmentOrNaturalClass.get(1);
		assertEquals("Expected second item to be b", "b", segOrNC.getSegmentOrNaturalClass());
		segOrNC = cvSegmentOrNaturalClass.get(2);
		assertEquals("Expected third item to be d", "d", segOrNC.getSegmentOrNaturalClass());
		segOrNC = cvSegmentOrNaturalClass.get(3);
		assertEquals("Expected fourth item to be C", "C", segOrNC.getSegmentOrNaturalClass());
		segOrNC = cvSegmentOrNaturalClass.get(4);
		assertEquals("Expected fifth item to be V", "V", segOrNC.getSegmentOrNaturalClass());
	}

}
