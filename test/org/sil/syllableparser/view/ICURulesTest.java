/**
 * Copyright (c) 2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.view;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
import org.sil.utility.view.JavaFXThreadingRule;

/**
 * @author Andy Black
 *
 */
public class ICURulesTest {
	
	ONCApproach onc;
	ONCWordsController controller;
	
	@Rule
	public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		LanguageProject languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE_ICU_RULES);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		onc = languageProject.getONCApproach();

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ApproachViewNavigator.class
				.getResource("fxml/ONCWords.fxml"));
		loader.setResources(ResourceBundle.getBundle(Constants.RESOURCE_LOCATION, locale));
		StackPane page = loader.load();
		Stage dialogStage = new Stage();
		dialogStage.initModality(Modality.WINDOW_MODAL);
		Scene scene = new Scene(page);
		dialogStage.setScene(scene);
		controller = loader.getController();
		controller.setData(onc, languageProject.getWords());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@SuppressWarnings("unchecked")
	@Test
	public void vernacularICUSortTest() {
		TableView <Word> wordsTable = controller.getONCWordsTable();
		TableColumn<Word,String> wordColumn = (TableColumn<Word, String>) wordsTable.getColumns().get(0);
		String sICUrules = "";
		controller.setColumnICURules(wordColumn, sICUrules);
		wordsTable.getSortOrder().setAll(wordColumn);
		int last = wordsTable.getItems().size();
		String sWord = wordColumn.getCellData(0);
		assertEquals("ædvaɪzɪdli", sWord);
		sWord = wordColumn.getCellData(10);
		assertEquals("ætlæntɪk", sWord);
		sWord = wordColumn.getCellData(20);
		assertEquals("bɛl", sWord);
		sWord = wordColumn.getCellData(30);
		assertEquals("bʊk", sWord);
		sWord = wordColumn.getCellData(40);
		assertEquals("dwɛl", sWord);
		sWord = wordColumn.getCellData(50);
		assertEquals("faɪv", sWord);
		sWord = wordColumn.getCellData(last-1);
		assertEquals("θwaɹt", sWord);
		
		sICUrules = onc.getLanguageProject().getVernacularLanguage().getIcuRules();
		controller.setColumnICURules(wordColumn, sICUrules);
		wordsTable.getSortOrder().setAll(wordColumn);
		sWord = wordColumn.getCellData(0);
		assertEquals("aɹktɪk", sWord);
		sWord = wordColumn.getCellData(10);
		assertEquals("ætəm", sWord);
		sWord = wordColumn.getCellData(20);
		assertEquals("bəd͡ʒd", sWord);
		sWord = wordColumn.getCellData(30);
		assertEquals("bʊk", sWord);
		sWord = wordColumn.getCellData(40);
		assertEquals("dwɛl", sWord);
		sWord = wordColumn.getCellData(50);
		assertEquals("faɪv", sWord);
		sWord = wordColumn.getCellData(last-1);
		assertEquals("waɪld", sWord);
	}

}
