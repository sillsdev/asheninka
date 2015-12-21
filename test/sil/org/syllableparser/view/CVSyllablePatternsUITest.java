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
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.syllableparser.model.cvapproach.CVSegment;
import sil.org.syllableparser.model.cvapproach.CVSyllablePattern;
import sil.org.syllableparser.service.CVSegmenter;

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
	public void removeComboBoxesTest() {
		// remove first
		createCCVNpattern();
		controller.removeContentFromComboBox(controller.getComboBox(0));
		String sPattern = controller.getNaturalClassSequenceFromComboBoxes();
		assertEquals("expect C V N", "C V N", sPattern);
		// remove second
		createCCVNpattern();
		controller.removeContentFromComboBox(controller.getComboBox(1));
		sPattern = controller.getNaturalClassSequenceFromComboBoxes();
		assertEquals("expect C V N", "C V N", sPattern);
		// remove third
		createCCVNpattern();
		controller.removeContentFromComboBox(controller.getComboBox(2));
		sPattern = controller.getNaturalClassSequenceFromComboBoxes();
		assertEquals("expect C C N", "C C N", sPattern);
		// remove fourth
		createCCVNpattern();
		controller.removeContentFromComboBox(controller.getComboBox(3));
		sPattern = controller.getNaturalClassSequenceFromComboBoxes();
		assertEquals("expect C C V", "C C V", sPattern);
		// remove first then third
		createCCVNpattern();
		controller.removeContentFromComboBox(controller.getComboBox(0));
		controller.removeContentFromComboBox(controller.getComboBox(2));
		sPattern = controller.getNaturalClassSequenceFromComboBoxes();
		assertEquals("expect C V", "C V", sPattern);
	}
	
	@Test
	public void addClearRemoveFromComboBoxTest() {
		ComboBox<CVNaturalClass> cb = controller.getComboBox(0);
		assertEquals("4 items in combo", 4, cb.getItems().size());
		// want C C V N
		cb.getSelectionModel().select(0);
		cb.setVisible(true);
		controller.addRemoveOptionToComboBox(cb);
		assertEquals("5 items in combo", 5, cb.getItems().size());
		controller.clearRemoveOptionFromComboBox(cb);
		assertEquals("4 items in combo", 4, cb.getItems().size());
	}
	
	@Test
	public void wordBoundaryTest() {
		createCCVNpattern();
		ComboBox<CVNaturalClass> cb = controller.getComboBox(0);
		cb.getSelectionModel().select(3);
		String sPattern = controller.getNaturalClassSequenceFromComboBoxes();
		assertEquals("expect # C V N", "# C V N", sPattern);
		CVSyllablePattern syllablePattern = new CVSyllablePattern();
		controller.setSyllablePatternForUnitTesting(syllablePattern);
		controller.getNaturalClassesFromComboBoxes();
		assertEquals(true, syllablePattern.isWordInitial());
		assertEquals(false, syllablePattern.isWordFinal());
		cb = controller.getComboBox(4);
		cb.setVisible(true);
		cb.getSelectionModel().select(3);
		sPattern = controller.getNaturalClassSequenceFromComboBoxes();
		assertEquals("expect # C V N #", "# C V N #", sPattern);
		cb = controller.getComboBox(2);
		cb.setVisible(true);
		cb.getSelectionModel().select(3);
		controller.makeAllFollowingComboBoxesInvisible(cb);
		sPattern = controller.getNaturalClassSequenceFromComboBoxes();
		assertEquals("expect # C #", "# C #", sPattern);
	}
	protected void createCCVNpattern() {
		// want C C V N
		ComboBox<CVNaturalClass> cb = controller.getComboBox(0);
		cb.getSelectionModel().select(0);
		cb.setVisible(true);
		cb = controller.getComboBox(1);
		cb.getSelectionModel().select(0);
		cb.setVisible(true);
		cb = controller.getComboBox(2);
		cb.getSelectionModel().select(1);
		cb.setVisible(true);
		cb = controller.getComboBox(3);
		cb.getSelectionModel().select(2);
		cb.setVisible(true);
		String sPattern = controller.getNaturalClassSequenceFromComboBoxes();
		assertEquals("expect C C V N", "C C V N", sPattern);
	}

}
