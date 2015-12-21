/**
 * 
 */
package sil.org.syllableparser.view;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.Word;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.syllableparser.model.cvapproach.CVSegment;
import sil.org.syllableparser.model.cvapproach.CVSegmenter;
import sil.org.syllableparser.model.cvapproach.CVSyllablePattern;

/**
 * @author Andy Black
 *
 */
public class CVApproachControllerTest {
	CVApproach cva;
	CVApproachController controller;
	@Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();
	
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
//		ResourceBundle bundle = new ResourceBundle() {
//			
//			@Override
//			protected Object handleGetObject(String key) {
//				// TODO Auto-generated method stub
//				return null;
//			}
//			
//			@Override
//			public Enumeration<String> getKeys() {
//				// TODO Auto-generated method stub
//				return null;
//			}
//		};
		cva = languageProject.getCVApproach();
		controller = new CVApproachController(ResourceBundle.getBundle("sil.org.syllableparser.resources.SyllableParser"), locale);
		
//		FXMLLoader loader = new FXMLLoader();
//		loader.setLocation(ApproachViewNavigator.class
//				.getResource("fxml/CVApproachController.fxml"));
//		loader.setResources(ResourceBundle.getBundle(
//				"sil.org.syllableparser.resources.SyllableParser", locale));
//		AnchorPane page = loader.load();
//		Stage dialogStage = new Stage();
//		dialogStage.initModality(Modality.WINDOW_MODAL);
//		Scene scene = new Scene(page);
//		dialogStage.setScene(scene);
//		controller = loader.getController();
		controller.setCVApproachData(cva, cva.getWords());
		}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void createNewWordTest() {
		ObservableList<Word> words = cva.getWords();
		assertEquals("CV words size", 10026, words.size());
		controller.createNewWord("abel");
		// 'abel' should already be there so the size should not change
		assertEquals("CV words size", 10026, words.size());
		controller.createNewWord("kinkos");
		// 'kinkos' should not already be there so the size should change
		assertEquals("CV words size", 10027, words.size());
		Word lastWordAdded = words.get(words.size()-1);
		assertEquals("kinkos", lastWordAdded.getWord());
	}
}
