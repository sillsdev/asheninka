// Copyright (c) 2016-2025 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
package org.sil.syllableparser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javafx.stage.Stage;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.sil.syllableparser.model.ApproachType;
import org.sil.syllableparser.model.cvapproach.CVApproachView;
import org.sil.utility.view.JavaFXThreadingRule;

public class ApplicationPreferencesTest {

	@Rule
	public JavaFXThreadingRule jfxRule = new JavaFXThreadingRule();

	ApplicationPreferences applicationPreferences;
	File fileLastUsed;
	String languageLastUsed;
	String approachLastUsed;
	String approachViewLastUsed;
	String cvTryAWordLastUsed;
	Stage windowStageLastUsed;
	Stage cvComparisonStageLastUsed;
	Stage cvSegmentNaturalClassLastUsed;
	Stage cvTryAWordStageLastUsed;
	Stage cvWordsPredictedVsCorrectStageLastUsed;
	int approachViewItemLastUsed;

	@Before
	public void setUp() throws Exception {
		applicationPreferences = new ApplicationPreferences(this);
		fileLastUsed = applicationPreferences.getLastOpenedFile();
		languageLastUsed = applicationPreferences.getLastLocaleLanguage();
		approachLastUsed = applicationPreferences.getLastApproachUsed();
		approachViewLastUsed = applicationPreferences.getLastApproachViewUsed();
		cvTryAWordLastUsed = applicationPreferences.getLastCVTryAWordUsed();
		applicationPreferences.setLastOpenedFilePath("last opened file");
		applicationPreferences.setLastLocaleLanguage("en");
		applicationPreferences.setLastCVTryAWordUsed("abcdef");
		windowStageLastUsed = new Stage();
		windowStageLastUsed = applicationPreferences.getLastWindowParameters(ApplicationPreferences.LAST_WINDOW, windowStageLastUsed, 400., 400.);
		cvComparisonStageLastUsed = new Stage();
		cvComparisonStageLastUsed = applicationPreferences.getLastWindowParameters(ApplicationPreferences.LAST_CV_COMPARISON, cvComparisonStageLastUsed, 400., 400.);
		cvSegmentNaturalClassLastUsed = new Stage();
		cvSegmentNaturalClassLastUsed = applicationPreferences.getLastWindowParameters(ApplicationPreferences.LAST_CV_SEGMENT_OR_NATURAL_CLASS, cvSegmentNaturalClassLastUsed, 400., 400.);
		cvTryAWordStageLastUsed = new Stage();
		cvTryAWordStageLastUsed = applicationPreferences.getLastWindowParameters(ApplicationPreferences.LAST_CV_TRY_A_WORD, cvTryAWordStageLastUsed, 400., 400.);
		cvWordsPredictedVsCorrectStageLastUsed = new Stage();
		cvWordsPredictedVsCorrectStageLastUsed = applicationPreferences.getLastWindowParameters(ApplicationPreferences.LAST_CV_WORDS_PREDICTED_VS_CORRECT, cvWordsPredictedVsCorrectStageLastUsed, 400., 400.);
	}

	@After
	public void tearDown() throws Exception {
		applicationPreferences.setLastOpenedFilePath(fileLastUsed);
		applicationPreferences.setLastLocaleLanguage(languageLastUsed);
		applicationPreferences.setLastApproachUsed(approachLastUsed);
		applicationPreferences.setLastApproachViewUsed(approachViewLastUsed);
		applicationPreferences.setLastCVTryAWordUsed(cvTryAWordLastUsed);
		applicationPreferences.setLastWindowParameters(ApplicationPreferences.LAST_WINDOW, windowStageLastUsed);
		applicationPreferences.setLastWindowParameters(ApplicationPreferences.LAST_CV_COMPARISON, cvComparisonStageLastUsed);
		applicationPreferences.setLastWindowParameters(ApplicationPreferences.LAST_CV_SEGMENT_OR_NATURAL_CLASS, cvSegmentNaturalClassLastUsed);
		applicationPreferences.setLastWindowParameters(ApplicationPreferences.LAST_CV_TRY_A_WORD, cvTryAWordStageLastUsed);
		applicationPreferences.setLastWindowParameters(ApplicationPreferences.LAST_CV_WORDS_PREDICTED_VS_CORRECT, cvWordsPredictedVsCorrectStageLastUsed);
	}

	@Test
	public void lastOpenedFile() {
		assertEquals("last opened file", "last opened file",
				applicationPreferences.getLastOpenedFilePath());
		applicationPreferences.setLastOpenedFilePath("mimi");
		assertEquals("last opened file", "mimi", applicationPreferences.getLastOpenedFilePath());
		try {
			File f = File.createTempFile("ApplicationPreferencesTest", null);
			applicationPreferences.setLastOpenedFilePath(f);
			String fPath = f.getPath();
			f.delete();
			assertEquals("file path", fPath, applicationPreferences.getLastOpenedFilePath());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void lastApproach() {
		String sMoraic = ApproachType.MORAIC.toString();
		applicationPreferences.setLastApproachUsed(sMoraic);
		assertEquals("last approach used", sMoraic, applicationPreferences.getLastApproachUsed());
		String sWords = CVApproachView.WORDS.toString();
		applicationPreferences.setLastApproachViewUsed(sWords);
		assertEquals("last approach view used", sWords, applicationPreferences.getLastApproachViewUsed());
	}
	
	@Test
	public void lastLanguage() {
		assertEquals("locale language", "en", applicationPreferences.getLastLocaleLanguage());
		applicationPreferences.setLastLocaleLanguage("es");
		assertEquals("locale language", "es", applicationPreferences.getLastLocaleLanguage());
	}
	
	@Test
	public void lastMainWindow() {
		checkWindowParameters(560., 860., 20., 21., ApplicationPreferences.LAST_WINDOW);
	}

	@Test
	public void lastCVTryAWordUsed() {
		assertEquals("CV Try a Word", "abcdef", applicationPreferences.getLastCVTryAWordUsed());
		applicationPreferences.setLastCVTryAWordUsed("xyz");
		assertEquals("CV Try a Word", "xyz", applicationPreferences.getLastCVTryAWordUsed());
	}

	@Test
	public void lastCVSegmentNaturalClassWindow() {
		checkWindowParameters(105., 42., 36., 58., ApplicationPreferences.LAST_CV_SEGMENT_OR_NATURAL_CLASS);
	}

	@Test
	public void lastCVTryAWordWindow() {
		checkWindowParameters(550., 870., 25., 35., ApplicationPreferences.LAST_CV_TRY_A_WORD);
	}

	@Test
	public void lastCVWordsPredictedVsCorrectWindow() {
		checkWindowParameters(450., 370., 45., 15., ApplicationPreferences.LAST_CV_WORDS_PREDICTED_VS_CORRECT);
	}

	@Test
	public void lastCVComparisonWindow() {
		checkWindowParameters(250., 270., 15., 32., ApplicationPreferences.LAST_CV_COMPARISON);
	}

	public void checkWindowParameters(Double height, Double width, Double positionX,
			Double positionY, String sWindow) {
		Stage testSetStage = new Stage();
		Stage testGetStage = new Stage();
		testSetStage.setHeight(height);
		testSetStage.setWidth(width);
		testSetStage.setX(positionX);
		testSetStage.setY(positionY);
		testSetStage.setMaximized(false);
		applicationPreferences.setLastWindowParameters(sWindow, testSetStage);
		testGetStage = applicationPreferences.getLastWindowParameters(sWindow, testGetStage, 400., 400.);
		assertEquals(height, testGetStage.getHeight(), 0);
		assertEquals(width, testGetStage.getWidth(), 0);
		assertEquals(positionX, testGetStage.getX(), 0);
		assertEquals(positionY, testGetStage.getY(), 0);
		assertEquals(false, testGetStage.isMaximized());
		testSetStage.setMaximized(true);
		applicationPreferences.setLastWindowParameters(sWindow, testSetStage);
		testGetStage = applicationPreferences.getLastWindowParameters(sWindow, testGetStage, 400., 400.);
		assertEquals(height, testGetStage.getHeight(), 0);
		assertEquals(width, testGetStage.getWidth(), 0);
		assertEquals(positionX, testGetStage.getX(), 0);
		assertEquals(positionY, testGetStage.getY(), 0);
		assertEquals(true, testGetStage.isMaximized());
	}
	
}
