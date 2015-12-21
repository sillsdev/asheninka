/**
 * 
 */
package sil.org.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.cvapproach.*;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.service.CVNaturalClasser;
import sil.org.syllableparser.service.CVSegmenter;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter class is functioning
 *         correctly
 */
public class CVNaturalClasserTest {

	CVApproach cva;
	ObservableList<CVNaturalClass> naturalClasses;
	CVSegmenter segmenter;
	ObservableList<CVSegment> segmentInventory;
	List<CVSegment> cvSegmentInventory;
	CVNaturalClasser naturalClasser;
	List<CVNaturalClass> cvNaturalClasses;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		LanguageProject languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(
				languageProject, locale);
		File file = new File(
				"test/sil/org/syllableparser/testData/CVTestData.sylpdata");
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		cva = languageProject.getCVApproach();
		segmentInventory = cva.getCVSegmentInventory();
		segmenter = new CVSegmenter(segmentInventory);
		cvSegmentInventory = segmenter.getSegmentInventory();
		naturalClasses = cva.getCVNaturalClasses();
		naturalClasser = new CVNaturalClasser(naturalClasses);
		cvNaturalClasses = naturalClasser.getNaturalClasses();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// make sure the setup is what we expect
	@Test
	public void naturalClassesTest() {
		assertEquals("Natural Classes size", 3, naturalClasses.size());
		assertEquals("Natural Classes in natural classer size", 3, cvNaturalClasses.size());
		String nc = cvNaturalClasses.get(0).getNCName().trim();
		assertEquals("First natural class is [C]", "C", nc);
		nc = cvNaturalClasses.get(1).getNCName().trim();
		assertEquals("Last natural class is [V]", "V", nc);
		HashMap<String, CVNaturalClass> segmentToNaturalClass = naturalClasser
				.getSegmentToNaturalClass();
		assertEquals("Hash map size is 26", 26, segmentToNaturalClass.size());
	}

	@Test
	public void wordToSegmentToNaturalClassesTest() {

		checkNaturalClassParsing("Chiko", true, 4, "C, V, C, V");
		checkNaturalClassParsing("champion", true, 7, "C, V, N, C, V, V, N");
		checkNaturalClassParsing("kaqui", false, 2, "C, V"); // there is no /q/
	}

	protected void checkNaturalClassParsing(String word, boolean success,
			int numberOfNaturalClasses, String expectedCVPattern) {
		boolean fSuccess = segmenter.segmentWord(word);
		assertEquals("word segmented", true, fSuccess);
		List<CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		fSuccess = naturalClasser
				.convertSegmentsToNaturalClasses(segmentsInWord);
		assertEquals("segments converted to natural classes", success, fSuccess);
		List<CVNaturalClassInSyllable> naturalClassesInWord = naturalClasser
				.getNaturalClassesInCurrentWord();
		assertEquals("Expect " + numberOfNaturalClasses
				+ " natural classes in word", numberOfNaturalClasses,
				naturalClassesInWord.size());
		String joined = naturalClassesInWord.stream()
				.map(CVNaturalClassInSyllable::getNaturalClassName)
				.collect(Collectors.joining(", "));
		assertEquals("Expected CV Pattern", expectedCVPattern, joined);
	}

}
