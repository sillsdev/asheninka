/**
 * 
 */
package sil.org.syllableparser.backendprovider;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.Word;
import sil.org.syllableparser.model.cvapproach.CVApproach;

/**
 * @author Andy Black
 *
 */
public class XMLBackEndProviderTest {

	XMLBackEndProvider xmlBackEndProvider;
	LanguageProject languageProject;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File("test/sil/org/syllableparser/testData/CVTestData.sylpdata");
		xmlBackEndProvider.loadLanguageDataFromFile(file);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void loadLanguageDataFromFileTest() {
		checkLoadedData();
	}

	public void checkLoadedData() {
		languageProject = xmlBackEndProvider.getLanguageProject();
		assertNotNull(languageProject);
		CVApproach cva = languageProject.getCVApproach();
		assertEquals(27,  cva.getCVSegmentInventory().size());
		assertEquals(3,  cva.getCVNaturalClasses().size());
		assertEquals(6,  cva.getCVSyllablePatterns().size());
		ObservableList<Word> words = languageProject.getWords();
		assertEquals(10026, words.size());
	}

	@Test
	public void saveLanguageDataToFileTest() {
		File tempSaveFile = null;
		try {
			tempSaveFile = File.createTempFile("AsheninkaTestSave", ".sylpdata");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    if (tempSaveFile != null) {
	    	tempSaveFile.deleteOnExit();
	    }
		xmlBackEndProvider.saveLanguageDataToFile(tempSaveFile);
		xmlBackEndProvider.loadLanguageDataFromFile(tempSaveFile);
		checkLoadedData();
	}
}
