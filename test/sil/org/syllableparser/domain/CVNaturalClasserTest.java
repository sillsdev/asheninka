/**
 * 
 */
package sil.org.syllableparser.domain;

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
import sil.org.syllableparser.model.CVNaturalClass;
import sil.org.syllableparser.model.CVSegment;
import sil.org.syllableparser.model.CVApproach;
import sil.org.syllableparser.model.LanguageProject;

/**
 * @author Andy Black
 *
 * Note: this test assumes that the CVSegmenter class is functioning correctly
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

	@Test
	public void naturalClassesyTest() {
		assertEquals("Natural Classes size", 2, naturalClasses.size());
		assertEquals("Natural Classes size", 2, cvNaturalClasses.size());
		String nc = cvNaturalClasses.get(0).getNCName().trim();
		assertEquals("First natural class is [C]", "C", nc);
		nc = cvNaturalClasses.get(1).getNCName().trim();
		assertEquals("Last natural class is [V]", "V", nc);
		HashMap<String, CVNaturalClass> segmentToNaturalClass = naturalClasser.getSegmentToNaturalClass();
		assertEquals("Hash map size is 27", 27, segmentToNaturalClass.size());
	}

	@Test
	public void wordToSegmentToNaturalClassesTest() {
		String word = "Chiko";
		List<CVSegment> segmentsInWord = segmenter.getSegmentsInWord();
		boolean fSuccess = segmenter.segmentWord(word);
		assertEquals("word segmented",  true,fSuccess);
		fSuccess = naturalClasser.convertSegmentsToNaturalClasses(segmentsInWord);
		assertEquals("segments converted to natural classes", true, fSuccess);
		List<CVNaturalClass> naturalClassesInWord = naturalClasser.getNaturalClassesInCurrentWord();
		assertEquals("Expect 4 natural classes in word", 4, naturalClassesInWord.size());
		String joined = naturalClassesInWord.stream().map(CVNaturalClass::getNCName)
				.collect(Collectors.joining(", "));
		assertEquals("Expect C V C V", "C, V, C, V", joined);
	}

}
