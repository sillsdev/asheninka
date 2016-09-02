/**
 * Copyright (c) 2016 SIL International 
 * This software is licensed under the LGPL, version 2.1 or later 
 * (http://www.gnu.org/licenses/lgpl-2.1.html) 
 */
package sil.org.syllableparser.view;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.LanguageProject;

/**
 * @author Andy Black
 *
 */
public class CVComparisonControllerTest {

	CVComparisonController controller;
	@Rule
	public JavaFXThreadingRule jfxRule = new JavaFXThreadingRule();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Locale locale = new Locale("en");
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ApproachViewNavigator.class.getResource("fxml/CVComparison.fxml"));
		loader.setResources(ResourceBundle.getBundle(Constants.RESOURCE_LOCATION, locale));
		AnchorPane page = loader.load();
		Stage dialogStage = new Stage();
		dialogStage.initModality(Modality.WINDOW_MODAL);
		Scene scene = new Scene(page);
		dialogStage.setScene(scene);
		controller = loader.getController();
		controller.setBackupDirectoryPath(Constants.UNIT_TEST_BACKUP_FILE_DIRECTORY);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void controllerUIWidgetsTest() {
		// check initial states
		assertEquals(false, controller.getCurrentImplementation().isDisabled());
		assertEquals(true, controller.getCurrentImplementation().isSelected());
		assertEquals(true, controller.getFirstButton().isDisabled());
		assertEquals(true, controller.getCompareButton().isDisabled());
		// set choose implementation radio button
		controller.handleChosenImplementation();
		assertEquals(false, controller.getCurrentImplementation().isDisabled());
		assertEquals(false, controller.getCurrentImplementation().isSelected());
		assertEquals(false, controller.getFirstButton().isDisabled());
		assertEquals(false, controller.getChosenImplementation().isDisabled());
		assertEquals(true, controller.getChosenImplementation().isSelected());
		assertEquals(true, controller.getCompareButton().isDisabled());
		controller.getDirectory2Field().setText("testing 2");
		controller.setCompareButtonDisable();
		assertEquals(true, controller.getCompareButton().isDisabled());
		// set current implementation radio button
		controller.handleCurrentImplementation();
		assertEquals(false, controller.getCurrentImplementation().isDisabled());
		assertEquals(true, controller.getCurrentImplementation().isSelected());
		assertEquals(true, controller.getFirstButton().isDisabled());
		assertEquals(false, controller.getChosenImplementation().isDisabled());
		assertEquals(false, controller.getChosenImplementation().isSelected());
		assertEquals(true, controller.getCompareButton().isDisabled());
		// test compare button enabled/disabled
		controller.getDirectory1Field().setText(null);
		controller.getDirectory2Field().setText(null);
		controller.setCompareButtonDisable();
		assertEquals(true, controller.getCompareButton().isDisabled());
		controller.getDirectory1Field().setText("testing 1");
		controller.getDirectory2Field().setText("");
		controller.setCompareButtonDisable();
		assertEquals(true, controller.getCompareButton().isDisabled());
		controller.getDirectory2Field().setText("testing 2");
		controller.setCompareButtonDisable();
		assertEquals(false, controller.getCompareButton().isDisabled());
	}

}
