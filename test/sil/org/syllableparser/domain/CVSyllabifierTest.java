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
import sil.org.syllableparser.model.CVSyllablePattern;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.valueobject.CVNaturalClassInSyllable;
import sil.org.syllableparser.model.valueobject.CVSegmentInSyllable;
import sil.org.syllableparser.model.valueobject.CVSyllable;

/**
 * @author Andy Black
 *
 *         Note: this test assumes that the CVSegmenter and CVNaturalClasser
 *         classes are functioning correctly
 */
public class CVSyllabifierTest {

	CVApproach cva;
	ObservableList<CVNaturalClass> naturalClasses;
	CVSegmenter segmenter;
	ObservableList<CVSegment> segmentInventory;
	List<CVSegment> cvSegmentInventory;
	CVNaturalClasser naturalClasser;
	List<CVNaturalClass> cvNaturalClasses;
	ObservableList<CVSyllablePattern> patterns;
	CVSyllabifier syllabifier;
	List<CVSyllablePattern> cvPatterns;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		LanguageProject languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File("test/sil/org/syllableparser/testData/CVTestData.sylpdata");
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		cva = languageProject.getCVApproach();
		segmentInventory = cva.getCVSegmentInventory();
		segmenter = new CVSegmenter(segmentInventory);
		cvSegmentInventory = segmenter.getSegmentInventory();
		naturalClasses = cva.getCVNaturalClasses();
		naturalClasser = new CVNaturalClasser(naturalClasses);
		cvNaturalClasses = naturalClasser.getNaturalClasses();
		patterns = cva.getCVSyllablePatterns();
		syllabifier = new CVSyllabifier(patterns, null);
		cvPatterns = syllabifier.getCvPatterns();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// make sure the setup is what we expect
	@Test
	public void syllabifierTest() {
		assertEquals("CV patterns size", 3, patterns.size());
		assertEquals("CV patterns in syllabifier size", 3, cvPatterns.size());
		String pattern = cvPatterns.get(0).getSPName().trim();
		assertEquals("First CV pattern is [C][V]", "CV", pattern);
		pattern = cvPatterns.get(1).getSPName().trim();
		assertEquals("Last pattern is [CVC]", "CVC", pattern);
		// HashMap<String, CVNaturalClass> segmentToNaturalClass =
		// naturalClasser
		// .getSegmentToNaturalClass();
		// assertEquals("Hash map size is 26", 26,
		// segmentToNaturalClass.size());
	}

	@Test
	public void wordToSegmentToNaturalClassesToSyllableTest() {
		checkSyllabification("Chiko", true, 2, "CV, CV", "Chi.ko");
		checkSyllabification("dapbek", true, 2, "CVC, CVC", "dap.bek");
		checkSyllabification("bampidon", true, 3, "CVN, CV, CVN", "bam.pi.don");
		checkSyllabification("fuhg", false, 0, "", "");
	}

	protected void checkSyllabification(String word, boolean success, int numberOfSyllables,
			String expectedCVPatternsUsed, String expectedSyllabification) {
		boolean fSuccess = segmenter.segmentWord(word);
		List<CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		fSuccess = naturalClasser.convertSegmentsToNaturalClasses(segmentsInWord);
		List<CVNaturalClassInSyllable> naturalClassesInWord = naturalClasser
				.getNaturalClassesInCurrentWord();
		syllabifier = new CVSyllabifier(cvPatterns, naturalClassesInWord);
		fSuccess = syllabifier.convertNaturalClassesToSyllables();
		assertEquals("word syllabified", success, fSuccess);
		List<CVSyllable> syllablesInWord = syllabifier.getSyllablesInCurrentWord();
		assertEquals("Expect " + numberOfSyllables + " syllables in word", numberOfSyllables,
				syllablesInWord.size());
		String joined = syllablesInWord.stream().map(CVSyllable::getNaturalClassNamesInSyllable)
				.collect(Collectors.joining(", "));
		assertEquals("Expected Syllable CV Pattern", expectedCVPatternsUsed, joined);
		assertEquals("Expected Syllabification of word", expectedSyllabification,
				syllabifier.getSyllabificationOfCurrentWord());
	}

}
