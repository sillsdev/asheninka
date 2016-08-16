/**
 * 
 */
package sil.org.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.cvapproach.*;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.service.CVNaturalClasser;
import sil.org.syllableparser.service.CVSegmenter;
import sil.org.syllableparser.service.CVSyllabifier;

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
	ObservableList<Segment> segmentInventory;
	List<Segment> cvSegmentInventory;
	CVNaturalClasser naturalClasser;
	List<CVNaturalClass> cvNaturalClasses;
	ObservableList<CVSyllablePattern> patterns;
	CVSyllabifier patternSyllabifier;
	CVSyllabifier stringSyllabifier;
	List<CVSyllablePattern> cvPatterns;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		LanguageProject languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		cva = languageProject.getCVApproach();
		cvSegmentInventory = languageProject.getActiveSegmentsInInventory();
		segmenter = new CVSegmenter(cvSegmentInventory);
		cvNaturalClasses = cva.getActiveCVNaturalClasses();
		naturalClasser = new CVNaturalClasser(cvNaturalClasses);
		cvPatterns = cva.getActiveCVSyllablePatterns();
		patternSyllabifier = new CVSyllabifier(cvPatterns, null);
		stringSyllabifier = new CVSyllabifier(cva);
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
		assertEquals("CV patterns size", 6, cvPatterns.size());
		String pattern = cvPatterns.get(0).getSPName().trim();
		assertEquals("First CV pattern is [C][V]", "CV", pattern);
		pattern = cvPatterns.get(1).getSPName().trim();
		assertEquals("Last pattern is [CVC]", "CVC", pattern);
	}

	@Test
	public void wordToSegmentToNaturalClassesToSyllableTest() {
		checkSyllabification("Chiko", true, 2, "CV, CV", "Chi.ko");
		checkSyllabification("dapbek", true, 2, "CVC, CVC", "dap.bek");
		checkSyllabification("bampidon", true, 3, "CVN, CV, CVN", "bam.pi.don");
		checkSyllabification("bovdek", true, 2, "CVC, CVC", "bov.dek");
		checkSyllabification("fuhgt", false, 0, "", "");  // no CCC possible
		checkSyllabification("blofugh", true, 2, "CCV, CVCC", "blo.fugh");
		checkSyllabification("bo", true, 1, "CV", "bo");
		checkSyllabification("funglo", false, 0, "", "");  // CVCC only word finally; CCV not possible word medially
		checkSyllabification("fugh", true, 1, "CVCC", "fugh");
		checkSyllabification("flu", true, 1, "CCV", "flu");
	}

	protected void checkSyllabification(String word, boolean success, int numberOfSyllables,
			String expectedCVPatternsUsed, String expectedSyllabification) {
		boolean fSuccess = segmenter.segmentWord(word);
		List<CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
		fSuccess = naturalClasser.convertSegmentsToNaturalClasses(segmentsInWord);
		List<CVNaturalClassInSyllable> naturalClassesInWord = naturalClasser
				.getNaturalClassesInCurrentWord();
		patternSyllabifier = new CVSyllabifier(cvPatterns, naturalClassesInWord);
		fSuccess = patternSyllabifier.convertNaturalClassesToSyllables();
		assertEquals("word syllabified", success, fSuccess);
		List<CVSyllable> syllablesInWord = patternSyllabifier.getSyllablesInCurrentWord();
		assertEquals("Expect " + numberOfSyllables + " syllables in word", numberOfSyllables,
				syllablesInWord.size());
		String joined = syllablesInWord.stream().map(CVSyllable::getNaturalClassNamesInSyllable)
				.collect(Collectors.joining(", "));
		assertEquals("Expected Syllable CV Pattern", expectedCVPatternsUsed, joined);
		assertEquals("Expected Syllabification of word", expectedSyllabification,
				patternSyllabifier.getSyllabificationOfCurrentWord());
	}

	@Test
	public void syllabifyWordTest() {
		checkSyllabifyWord("Chiko", true, 2, "CV, CV", "Chi.ko");
		checkSyllabifyWord("dapbek", true, 2, "CVC, CVC", "dap.bek");
		checkSyllabifyWord("bampidon", true, 3, "CVN, CV, CVN", "bam.pi.don");
		checkSyllabifyWord("bovdek", true, 2, "CVC, CVC", "bov.dek");
		checkSyllabifyWord("fuhgt", false, 0, "", "");  // no CCC possible
		checkSyllabifyWord("blofugh", true, 2, "CCV, CVCC", "blo.fugh");
		checkSyllabifyWord("bo", true, 1, "CV", "bo");
		checkSyllabifyWord("funglo", false, 0, "", "");  // CVCC only word finally; CCV not possible word medially
		checkSyllabifyWord("fugh", true, 1, "CVCC", "fugh");
		checkSyllabifyWord("flu", true, 1, "CCV", "flu");
		checkSyllabifyWord("cat", false, 0, "", ""); // no c segment
	}

	protected void checkSyllabifyWord(String word, boolean success, int numberOfSyllables,
			String expectedCVPatternsUsed, String expectedSyllabification) {
		boolean fSuccess = stringSyllabifier.convertStringToSyllables(word);
		assertEquals("word syllabified", success, fSuccess);
		List<CVSyllable> syllablesInWord = stringSyllabifier.getSyllablesInCurrentWord();
		assertEquals("Expect " + numberOfSyllables + " syllables in word", numberOfSyllables,
				syllablesInWord.size());
		String joined = syllablesInWord.stream().map(CVSyllable::getNaturalClassNamesInSyllable)
				.collect(Collectors.joining(", "));
		assertEquals("Expected Syllable CV Pattern", expectedCVPatternsUsed, joined);
		assertEquals("Expected Syllabification of word", expectedSyllabification,
				stringSyllabifier.getSyllabificationOfCurrentWord());
	}


}
