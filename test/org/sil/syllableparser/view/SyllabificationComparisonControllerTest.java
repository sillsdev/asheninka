/**
 * Copyright (c) 2016-2018 SIL International 
 * This software is licensed under the LGPL, version 2.1 or later 
 * (http://www.gnu.org/licenses/lgpl-2.1.html) 
 */
package org.sil.syllableparser.view;

import static org.junit.Assert.*;

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
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.view.ApproachViewNavigator;
import org.sil.utility.view.JavaFXThreadingRule;

/**
 * @author Andy Black
 *
 */
public class SyllabificationComparisonControllerTest {

	SyllabificationComparisonController controller;
	@Rule
	public JavaFXThreadingRule jfxRule = new JavaFXThreadingRule();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Locale locale = new Locale("en");
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ApproachViewNavigator.class.getResource("fxml/SyllabificationComparison.fxml"));
		loader.setResources(ResourceBundle.getBundle(Constants.RESOURCE_LOCATION, locale));
		AnchorPane page = loader.load();
		Stage dialogStage = new Stage();
		dialogStage.initModality(Modality.WINDOW_MODAL);
		Scene scene = new Scene(page);
		dialogStage.setScene(scene);
		controller = loader.getController();
		//controller.setBackupDirectoryPath(Constants.UNIT_TEST_BACKUP_FILE_DIRECTORY);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void atLeastTwoApproachesCheckedTest() {
		// check initial states
		assertEquals(false, controller.isUseCVApproach());
		assertEquals(false, controller.isUseCVApproachDisabled());
		assertEquals(false, controller.isUseSHApproach());
		assertEquals(false, controller.isUseSHApproachDisabled());
		assertEquals(false, controller.isUseONCApproach());
		assertEquals(false, controller.isUseONCApproachDisabled());
		assertEquals(false, controller.isUseMoraicApproach());
		assertEquals(true, controller.isUseMoraicApproachDisabled());
		assertEquals(false, controller.isUseNuclearProjectionApproach());
		assertEquals(true, controller.isUseNuclearProjectionApproachDisabled());
		assertEquals(false, controller.isUseOTApproach());
		assertEquals(true, controller.isUseOTApproachDisabled());
		
		// set comparison buttons
		controller.setUseCVApproach(true);
		assertEquals(true, controller.isUseCVApproach());
		controller.setUseSHApproach(true);
		assertEquals(true, controller.isUseSHApproach());
		controller.setUseONCApproach(true);
		assertEquals(true, controller.isUseONCApproach());
		controller.setUseMoraicApproach(true);
		assertEquals(true, controller.isUseMoraicApproach());
		controller.setUseNuclearProjectionApproach(true);
		assertEquals(true, controller.isUseNuclearProjectionApproach());
		controller.setUseOTApproach(true);
		assertEquals(true, controller.isUseOTApproach());
		
		// check at least two options selected
		assertEquals(true, controller.twoOrMoreToCompare());
		controller.setUseSHApproach(false);
		assertEquals(true, controller.twoOrMoreToCompare());
		controller.setUseCVApproach(false);
		assertEquals(false, controller.twoOrMoreToCompare());
		controller.setUseCVApproach(true);
		assertEquals(true, controller.twoOrMoreToCompare());

	}

}
