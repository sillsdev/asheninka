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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Language;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.SortingOption;
import org.sil.syllableparser.model.Word;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
import org.sil.utility.view.JavaFXThreadingRule;

/**
 * @author Andy Black
 *
 */
public class ICURulesTest {
	
	ONCApproach onc;
	ONCWordsController oncWordsController;
	WritingSystemController wsController;
	String ksIcuRules = "& a < æ <ɑ < aɪ < aʊ\n& d < d͡ʒ < ð\n& e < eɪ < ɛ < ə\n& i < ɪ\n& l < l̩\n& m < m̩\n& n < n̩ < ŋ\n& o < oɪ < oʊ\n& r < ɹ < ɹ̩\n& s < ʃ\n& t < ɾ <t͡ʃ < θ \n& u < ʊ\n& z < ʒ < ʔ\n";
	
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

		FXMLLoader oncWordsLoader = new FXMLLoader();
		oncWordsLoader.setLocation(ApproachViewNavigator.class
				.getResource("fxml/ONCWords.fxml"));
		oncWordsLoader.setResources(ResourceBundle.getBundle(Constants.RESOURCE_LOCATION, locale));
		StackPane oncWordsPage = oncWordsLoader.load();
		Stage oncWordsDialogStage = new Stage();
		oncWordsDialogStage.initModality(Modality.WINDOW_MODAL);
		Scene ncWordsScene = new Scene(oncWordsPage);
		oncWordsDialogStage.setScene(ncWordsScene);
		oncWordsController = oncWordsLoader.getController();
		oncWordsController.setData(onc, languageProject.getWords());

		FXMLLoader wsLoader = new FXMLLoader();
		wsLoader.setLocation(ApproachViewNavigator.class
				.getResource("fxml/WritingSystem.fxml"));
		wsLoader.setResources(ResourceBundle.getBundle(Constants.RESOURCE_LOCATION, locale));
		AnchorPane wsPage = wsLoader.load();
		Stage wsDialogStage = new Stage();
		wsDialogStage.initModality(Modality.WINDOW_MODAL);
		Scene wsScene = new Scene(wsPage);
		wsDialogStage.setScene(wsScene);
		wsController = wsLoader.getController();
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
		TableView <Word> wordsTable = oncWordsController.getONCWordsTable();
		TableColumn<Word,String> wordColumn = (TableColumn<Word, String>) wordsTable.getColumns().get(0);
		String sICUrules = "";
		oncWordsController.setColumnICURules(wordColumn, sICUrules);
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
		oncWordsController.setColumnICURules(wordColumn, sICUrules);
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

	@Test
	public void getAnyIcuRulesTest() {
		Language vernacular = onc.getLanguageProject().getVernacularLanguage();
		SortingOption initialOption = vernacular.getSortingOption();
		vernacular.setSortingOption(SortingOption.DEFAULT_ORDER);
		String icuRules = vernacular.getAnyIcuRules();
		assertEquals("", icuRules);
		vernacular.setSortingOption(SortingOption.CUSTOM_ICU_RULES);
		icuRules = vernacular.getAnyIcuRules();
		assertEquals(ksIcuRules, icuRules);
		vernacular.setSortingOption(SortingOption.SAME_AS_ANOTHER_LANGUAGE);
		icuRules = vernacular.getAnyIcuRules();
		assertEquals(ksIcuRules, icuRules);
		vernacular.setSortingOption(SortingOption.USE_LDML_FILE);
		icuRules = vernacular.getAnyIcuRules();
		assertEquals(ksIcuRules, icuRules);
		vernacular.setSortingOption(initialOption);
	}

	@Test
	public void writingSystemDisplayTest() throws Exception {
		Language vernacular = onc.getLanguageProject().getVernacularLanguage();
		SortingOption initialOption = vernacular.getSortingOption();
		wsController.sortingChoiceBox.setValue(SortingOption.DEFAULT_ORDER);
		assertEquals(false, wsController.icuRules.isVisible());
		assertEquals(false, wsController.icuRulesTextArea.isVisible());
		assertEquals(false, wsController.languageToUse.isVisible());
		assertEquals(false, wsController.languageToUseComboBox.isVisible());
		assertEquals(false, wsController.directoryField.isVisible());
		assertEquals(false, wsController.browseButton.isVisible());
		assertEquals(false, wsController.icuRulesErrorArea.isVisible());
		assertEquals(false, wsController.icuRuleError.isVisible());

		wsController.sortingChoiceBox.setValue(SortingOption.CUSTOM_ICU_RULES);
		assertEquals(true, wsController.icuRules.isVisible());
		assertEquals(true, wsController.icuRulesTextArea.isVisible());
		assertEquals(false, wsController.languageToUse.isVisible());
		assertEquals(false, wsController.languageToUseComboBox.isVisible());
		assertEquals(false, wsController.directoryField.isVisible());
		assertEquals(false, wsController.browseButton.isVisible());
		assertEquals(false, wsController.icuRulesErrorArea.isVisible());
		assertEquals(false, wsController.icuRuleError.isVisible());

		wsController.sortingChoiceBox.setValue(SortingOption.SAME_AS_ANOTHER_LANGUAGE);
		assertEquals(true, wsController.icuRules.isVisible());
		assertEquals(true, wsController.icuRulesTextArea.isVisible());
		assertEquals(true, wsController.languageToUse.isVisible());
		assertEquals(true, wsController.languageToUseComboBox.isVisible());
		assertEquals(false, wsController.directoryField.isVisible());
		assertEquals(false, wsController.browseButton.isVisible());
		assertEquals(false, wsController.icuRulesErrorArea.isVisible());
		assertEquals(false, wsController.icuRuleError.isVisible());

		wsController.sortingChoiceBox.setValue(SortingOption.USE_LDML_FILE);
		assertEquals(true, wsController.icuRules.isVisible());
		assertEquals(true, wsController.icuRulesTextArea.isVisible());
		assertEquals(false, wsController.languageToUse.isVisible());
		assertEquals(false, wsController.languageToUseComboBox.isVisible());
		assertEquals(true, wsController.directoryField.isVisible());
		assertEquals(true, wsController.browseButton.isVisible());
		assertEquals(false, wsController.icuRulesErrorArea.isVisible());
		assertEquals(false, wsController.icuRuleError.isVisible());

		vernacular.setSortingOption(initialOption);
	}
}
