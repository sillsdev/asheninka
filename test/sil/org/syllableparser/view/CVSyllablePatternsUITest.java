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
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVSegment;
import sil.org.syllableparser.model.cvapproach.CVSegmenter;

/**
 * @author Andy Black
 *
 */
public class CVSyllablePatternsUITest {
	CVApproach cva;
	CVSyllablePatternNaturalClassChooserController controller;
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
		cva = languageProject.getCVApproach();
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ApproachViewNavigator.class
				.getResource("fxml/CVSyllablePatternNaturalClassChooser.fxml"));
		loader.setResources(ResourceBundle.getBundle(
				"sil.org.syllableparser.resources.SyllableParser", locale));
		AnchorPane page = loader.load();
		Stage dialogStage = new Stage();
		dialogStage.initModality(Modality.WINDOW_MODAL);
		Scene scene = new Scene(page);
		dialogStage.setScene(scene);
		controller = loader.getController();
		controller.setData(cva);
		}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void removeFirstComboBoxOf3Test() {
		createCCVNpattern();
		controller.getCb2().getSelectionModel().select(3);
		
		
	}

	protected void createCCVNpattern() {
		// want C C V N
		controller.getCb1().getSelectionModel().select(0);
		controller.getCb1().setVisible(true);
		controller.getCb2().getSelectionModel().select(0);
		controller.getCb2().setVisible(true);
		controller.getCb3().getSelectionModel().select(1);
		controller.getCb3().setVisible(true);
		controller.getCb4().getSelectionModel().select(2);
		controller.getCb4().setVisible(true);
		String sPattern = controller.getNaturalClassSequenceFromComboBoxes();
		assertEquals("expect C C V N", "C C V N", sPattern);
	}

}
