/**
 * 
 */
package sil.org.syllableparser.model;

import static org.junit.Assert.*;

import java.util.UUID;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
		ObservableList<CVSegment> cvSegmentInventory = FXCollections.observableArrayList();
		ObservableList<CVNaturalClass> cvNaturalClasses = FXCollections.observableArrayList();
		CVSegment segA = new CVSegment("a", "a A", "low mid unrounded vowel");
		CVSegment segB = new CVSegment("b", "b B", "voiced bilabial stop");
		CVSegment segD = new CVSegment("d", "d D", "voiced alveolar stop");
		cvSegmentInventory.add(segA);
		cvSegmentInventory.add(segB);
		cvSegmentInventory.add(segD);
		CVNaturalClass ncC = new CVNaturalClass("C", null, "Consonants", "bd");
		CVNaturalClass ncV = new CVNaturalClass("V", null, "Vowels", "a");
		cvNaturalClasses.add(ncC);
		cvNaturalClasses.add(ncV);
		
		int index = CVSegment.findIndexInSegmentsListByUuid(cvSegmentInventory, segA.getUuid());
		assertEquals("Expected 0", 0, index);
		index = CVSegment.findIndexInSegmentsListByUuid(cvSegmentInventory, segB.getUuid());
		assertEquals("Expected 1", 1, index);
		index = CVSegment.findIndexInSegmentsListByUuid(cvSegmentInventory, segD.getUuid());
		assertEquals("Expected 2", 2, index);
		UUID uuidNew = UUID.randomUUID();
		index = CVSegment.findIndexInSegmentsListByUuid(cvSegmentInventory, uuidNew);
		assertEquals("Expected -1", -1, index);
		index = CVNaturalClass.findIndexInNaturaClassListByUuid(cvNaturalClasses, ncC.getUuid());
		assertEquals("Expected 0", 0, index);
		index = CVNaturalClass.findIndexInNaturaClassListByUuid(cvNaturalClasses, ncV.getUuid());
		assertEquals("Expected 1", 1, index);
		index = CVNaturalClass.findIndexInNaturaClassListByUuid(cvNaturalClasses, uuidNew);
		assertEquals("Expected -1", -1, index);
	}

}
