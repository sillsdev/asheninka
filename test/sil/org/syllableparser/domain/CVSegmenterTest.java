/**
 * 
 */
package sil.org.syllableparser.domain;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Locale;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sil.org.syllableparser.ApplicationPreferences;
import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.CVSegment;
import sil.org.syllableparser.model.CVApproach;
import sil.org.syllableparser.model.LanguageProject;

/**
 * @author Andy Black
 *
 */
public class CVSegmenterTest {
	
	CVApproach cva;
	ObservableList<CVSegment> segmentInventory;
	CVSegmenter segmenter;
	List<CVSegment> cvSegmentInventory;
	
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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void inventoryTest() {
		assertEquals("Segment inventory size", 27, segmentInventory.size());
		assertEquals("Segment inventory size", 27, cvSegmentInventory.size());
		String seg = cvSegmentInventory.get(0).getSegment().trim();
		assertEquals("First segment is /a/", "a", seg);
		seg = cvSegmentInventory.get(26).getSegment().trim();
		assertEquals("Last segment is /ɲ/", "ɲ", seg);
	}

}
