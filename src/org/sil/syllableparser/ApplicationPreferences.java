// Copyright (c) 2016-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
package org.sil.syllableparser;

import java.io.File;
import java.util.prefs.Preferences;

import org.sil.syllableparser.model.ApproachType;
import org.sil.syllableparser.model.cvapproach.CVApproachView;
import org.sil.syllableparser.model.oncapproach.ONCApproachView;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproachView;
import org.sil.utility.*;

import javafx.stage.Stage;

public class ApplicationPreferences extends ApplicationPreferencesUtilities {

	static final String LAST_OPENED_FILE_PATH = "lastOpenedFilePath";
	static final String LAST_OPENED_DIRECTORY_PATH = "lastOpenedDirectoryPath";
	static final String LAST_LOCALE_LANGUAGE = "lastLocaleLanguage";
	// Not trying to be anglo-centric, but we have to start with something...
	static final String DEFAULT_LOCALE_LANGUAGE = "en";
	// last approach info
	static final String LAST_APPROACH_USED = "lastApproachUsed";
	static final String LAST_APPROACH_VIEW_USED = "lastApproachViewUsed";
	static final String LAST_APPROACH_VIEW_ITEM_USED = "lastApproachViewItemUsed";
	// last CV info
	static final String LAST_CV_APPROACH_VIEW_USED = "lastCVApproachViewUsed";
	static final String LAST_CV_NATURAL_CLASSES_VIEW_ITEM_USED = "lastCVNaturalClassesViewItemUsed";
	static final String LAST_CV_SEGMENT_INVENTORY_VIEW_ITEM_USED = "lastCVSegmentInventoryViewItemUsed";
	static final String LAST_CV_SYLLABLE_PATTERNS_VIEW_ITEM_USED = "lastCVSyllablePatternsViewItemUsed";
	static final String LAST_CV_TRY_A_WORD_USED = "lastCVTryAWordUsed";
	static final String LAST_CV_WORDS_VIEW_ITEM_USED = "lastCVWordsViewItemUsed";
	static final String LAST_CV_GRAPHEME_NATURAL_CLASSES_VIEW_ITEM_USED = "lastCVGraphemeNaturalClassesViewItemUsed";
	static final String LAST_CV_ENVIRONMENTS_VIEW_ITEM_USED = "lastCVEnvironmentsViewItemUsed";
	static final String LAST_CV_WORDS_PREDICTED_VS_CORRECT_VIEW_ITEM_USED = "lastCVWordPredictedVsCorrectViewItemUsed";

	// last Sonority Hierarchy used
	static final String LAST_SH_APPROACH_VIEW_USED = "lastSHApproachViewUsed";
	static final String LAST_SH_SEGMENT_INVENTORY_VIEW_ITEM_USED = "lastSHSegmentInventoryViewItemUsed";
	static final String LAST_SH_SONORITY_HIERARCHY_VIEW_ITEM_USED = "lastSHSonorityHierarchViewItemUsed";
	static final String LAST_SH_TRY_A_WORD_USED = "lastSHTryAWordUsed";
	static final String LAST_SH_WORDS_VIEW_ITEM_USED = "lastSHWordsViewItemUsed";
	static final String LAST_SH_GRAPHEME_NATURAL_CLASSES_VIEW_ITEM_USED = "lastSHGraphemeNaturalClassesViewItemUsed";
	static final String LAST_SH_ENVIRONMENTS_VIEW_ITEM_USED = "lastSHEnvironmentsViewItemUsed";
	static final String LAST_SH_WORDS_PREDICTED_VS_CORRECT_VIEW_ITEM_USED = "lastSHWordPredictedVsCorrectViewItemUsed";

	// last ONC view used
	static final String LAST_ONC_APPROACH_VIEW_USED = "lastONCApproachViewUsed";
	static final String LAST_ONC_SEGMENT_INVENTORY_VIEW_ITEM_USED = "lastONCSegmentInventoryViewItemUsed";
	static final String LAST_ONC_SONORITY_HIERARCHY_VIEW_ITEM_USED = "lastONCSonorityHierarchViewItemUsed";
	static final String LAST_ONC_TRY_A_WORD_USED = "lastONCTryAWordUsed";
	static final String LAST_ONC_WORDS_VIEW_ITEM_USED = "lastONCWordsViewItemUsed";
	static final String LAST_ONC_GRAPHEME_NATURAL_CLASSES_VIEW_ITEM_USED = "lastONCGraphemeNaturalClassesViewItemUsed";
	static final String LAST_ONC_ENVIRONMENTS_VIEW_ITEM_USED = "lastONCEnvironmentsViewItemUsed";
	static final String LAST_ONC_CV_NATURAL_CLASSES_VIEW_ITEM_USED = "lastONCCVNaturalClassesViewItemUsed";
	static final String LAST_ONC_TEMPLATES_VIEW_ITEM_USED = "lastONCTemplatesViewItemUsed";
	static final String LAST_ONC_FILTERS_VIEW_ITEM_USED = "lastONCFiltersViewItemUsed";
	static final String LAST_ONC_WORDS_PREDICTED_VS_CORRECT_VIEW_ITEM_USED = "lastONCWordPredictedVsCorrectViewItemUsed";

	// Controller table column widths and splitter position
	public static final String BACKUP_CHOOSER = "BACKUP_CHOOSER_";
	public static final String CV_NATURAL_CLASSES = "CV_NATURAL_CLASSES_";
	public static final String CV_SEGMENTS = "CV_SEGMENTS_";
	public static final String CV_SEGMENT_NATURAL_CLASS_CHOOSER = "CV_SEGMENT_NATURAL_CLASS_CHOOSER_";
	public static final String CV_SYLLABLE_PATTERNS = "CV_SYLLABLE_PATTERNS_";
	public static final String CV_WORDS = "CV_WORDS_";
	public static final String ENVIRONMENT_CHOOSER = "ENVIROMENT_CHOOSER_";
	public static final String ENVIRONMENTS = "ENVIROMENTS_";
	public static final String FILTERS = "FILTERS_";
	public static final String GRAPHEME_NATURAL_CLASS_CHOOSER = "GRAPHEME_NATURAL_CLASSE_CHOOSER_";
	public static final String GRAPHEME_NATURAL_CLASSES = "GRAPHEME_NATURAL_CLASSES_";
	public static final String PREDICTED_TO_CORRECT_CHOOSER = "PREDICTED_TO_CORRECT_CHOOSER_";
	public static final String ONC_SEGMENTS = "ONC_SEGMENTS_";
	public static final String ONC_WORDS = "ONC_WORDS_";
	public static final String SH_SEGMENT_CHOOSER = "SH_SEGMENT_CHOOSER_";
	public static final String SH_SEGMENTS = "SH_SEGMENTS_";
	public static final String SH_SONORITY_HIERARCHY = "SH_SONORITY_HIERARCHY_";
	public static final String SH_WORDS = "SH_WORDS_";
	public static final String TEMPLATES = "TEMPLATES_";

	// Syllabification comparison options
	static final String LAST_USE_CV_APPROACH_VALUE = "lastUseCVApproachValueUsed";
	static final String LAST_USE_SH_APPROACH_VALUE = "lastUseSHApproachValueUsed";
	static final String LAST_USE_ONC_APPROACH_VALUE = "lastUseONCApproachValueUsed";
	static final String LAST_USE_MORAIC_APPROACH_VALUE = "lastUseMoraicApproachValueUsed";
	static final String LAST_USE_NUCLEAR_PROJECTION_APPROACH_VALUE = "lastUseNuclearProjectionApproachValueUsed";
	static final String LAST_USE_OT_APPROACH_VALUE = "lastUseOTApproachValueUsed";

	// Window parameters to remember
	static final String POSITION_X = "PositionX";
	static final String POSITION_Y = "PositionY";
	static final String WIDTH = "Width";
	static final String HEIGHT = "Height";
	static final String MAXIMIZED = "Maximized";
	// Window parameters for main window and various dialogs
	public static final String LAST_WINDOW = "lastWindow";
	public static final String LAST_CV_COMPARISON = "lastCVComparision";
	public static final String LAST_CV_SEGMENT_OR_NATURAL_CLASS = "lastCVSegmentOrNaturalClass";
	public static final String LAST_CV_TRY_A_WORD = "lastCVTryAWord";
	public static final String LAST_CV_WORDS_PREDICTED_VS_CORRECT = "lastCVWordPredictedVsCorrect";
	public static final String LAST_CV_GRAPHEME_OR_NATURAL_CLASS = "lastCVGraphemeOrNaturalClass";
	public static final String LAST_CV_ENVIRONMENTS_CHOOSER = "lastCVEnvironmentsChooser";
	public static final String LAST_SH_COMPARISON = "lastSHComparision";
	public static final String LAST_SH_SEGMENT = "lastSHSegment";
	public static final String LAST_SH_TRY_A_WORD = "lastSHTryAWord";
	public static final String LAST_ONC_COMPARISON = "lastONCComparision";
	public static final String LAST_ONC_SEGMENT = "lastONCSegment";
	public static final String LAST_ONC_TRY_A_WORD = "lastONCTryAWord";
	public static final String LAST_SYLLABIFICATION_COMPARISON = "lastSyllabificationComparision";
	public static final String LAST_CV_FILTER_CORRECT_SYLLABIFICATIONS = "lastCVFilterCorrectSyllabifications";
	public static final String LAST_CV_FILTER_PREDICTED_SYLLABIFICATIONS = "lastCVFilterPredictedSyllabifications";
	public static final String LAST_CV_FILTER_WORDS = "lastCVFilterWords";
	public static final String LAST_CV_FILTER_WORDS_PREDICTED_VS_CORRECT = "lastCVFilterWordsPredictedVsCorrect";
	public static final String LAST_MORAIC_FILTER_CORRECT_SYLLABIFICATIONS = "lastMoraicFilterCorrectSyllabifications";
	public static final String LAST_MORAIC_FILTER_PREDICTED_SYLLABIFICATIONS = "lastMoraicFilterPredictedSyllabifications";
	public static final String LAST_MORAIC_FILTER_WORDS = "lastMoraicFilterWords";
	public static final String LAST_MORAIC_FILTER_WORDS_PREDICTED_VS_CORRECT = "lastMoraicFilterWordsPredictedVsCorrect";
	public static final String LAST_NUCLEAR_PROJECTION_FILTER_CORRECT_SYLLABIFICATIONS = "lastNuclearProjectionFilterCorrectSyllabifications";
	public static final String LAST_NUCLEAR_PROJECTION_FILTER_PREDICTED_SYLLABIFICATIONS = "lastNuclearProjectionFilterPredictedSyllabifications";
	public static final String LAST_NUCLEAR_PROJECTION_FILTER_WORDS = "lastNuclearProjectionFilterWords";
	public static final String LAST_NUCLEAR_PROJECTION_FILTER_WORDS_PREDICTED_VS_CORRECT = "lastNuclearProjectionFilterWordsPredictedVsCorrect";
	public static final String LAST_ONC_FILTER_CORRECT_SYLLABIFICATIONS = "lastONCFilterCorrectSyllabifications";
	public static final String LAST_ONC_FILTER_PREDICTED_SYLLABIFICATIONS = "lastONCFilterPredictedSyllabifications";
	public static final String LAST_ONC_FILTER_WORDS = "lastONCFilterWords";
	public static final String LAST_ONC_FILTER_WORDS_PREDICTED_VS_CORRECT = "lastONCFilterWordsPredictedVsCorrect";
	public static final String LAST_OT_FILTER_CORRECT_SYLLABIFICATIONS = "lastOTFilterCorrectSyllabifications";
	public static final String LAST_OT_FILTER_PREDICTED_SYLLABIFICATIONS = "lastOTFilterPredictedSyllabifications";
	public static final String LAST_OT_FILTER_WORDS = "lastOTFilterWords";
	public static final String LAST_OT_FILTER_WORDS_PREDICTED_VS_CORRECT = "lastOTFilterWordsPredictedVsCorrect";
	public static final String LAST_SH_FILTER_CORRECT_SYLLABIFICATIONS = "lastSHFilterCorrectSyllabifications";
	public static final String LAST_SH_FILTER_PREDICTED_SYLLABIFICATIONS = "lastSHFilterPredictedSyllabifications";
	public static final String LAST_SH_FILTER_WORDS = "lastSHFilterWords";
	public static final String LAST_SH_FILTER_WORDS_PREDICTED_VS_CORRECT = "lastSHFilterWordsPredictedVsCorrect";
	public static final String FILTER_ACTIVE = "Active";
	public static final String FILTER_SEARCH_TEXT = "SearchText";
	public static final String FILTER_SEARCH_TYPE = "SearchType";
	public static final String FILTER_MATCH_CASE = "MatchCase";
	public static final String FILTER_MATCH_DIACRITICS = "MatchDiacritics";

	Preferences prefs;

	public ApplicationPreferences(Object app) {
		prefs = Preferences.userNodeForPackage(app.getClass());
	}

	public String getLastOpenedFilePath() {
		return prefs.get(LAST_OPENED_FILE_PATH, null);
	}

	public void setLastOpenedFilePath(String lastOpenedFile) {
		setPreferencesKey(LAST_OPENED_FILE_PATH, lastOpenedFile);
	}

	public String getLastLocaleLanguage() {
		return prefs.get(LAST_LOCALE_LANGUAGE, DEFAULT_LOCALE_LANGUAGE);
	}

	public void setLastLocaleLanguage(String lastLocaleLanguage) {
		setPreferencesKey(LAST_LOCALE_LANGUAGE, lastLocaleLanguage);
	}

	public File getLastOpenedFile() {
		String filePath = prefs.get(LAST_OPENED_FILE_PATH, null);
		if (filePath != null) {
			return new File(filePath);
		} else {
			return null;
		}
	}

	public void setLastOpenedFilePath(File file) {
		if (file != null) {
			setPreferencesKey(LAST_OPENED_FILE_PATH, file.getPath());

		} else {
			prefs.remove(LAST_OPENED_FILE_PATH);
		}
	}

	public String getLastOpenedDirectoryPath() {
		return prefs.get(LAST_OPENED_DIRECTORY_PATH, "");
	}

	public void setLastOpenedDirectoryPath(String directoryPath) {
		setPreferencesKey(LAST_OPENED_DIRECTORY_PATH, directoryPath);
	}

	public String getLastApproachUsed() {
		return prefs.get(LAST_APPROACH_USED, ApproachType.CV.toString());
	}

	public void setLastApproachUsed(String lastApproachUsed) {
		setPreferencesKey(LAST_APPROACH_USED, lastApproachUsed);
	}

	public String getLastApproachViewUsed() {
		return prefs.get(LAST_APPROACH_VIEW_USED, CVApproachView.SEGMENT_INVENTORY.toString());
	}

	public void setLastApproachViewUsed(String lastApproachViewUsed) {
		setPreferencesKey(LAST_APPROACH_VIEW_USED, lastApproachViewUsed);
	}

	public String getLastCVApproachViewUsed() {
		return prefs.get(LAST_CV_APPROACH_VIEW_USED, CVApproachView.SEGMENT_INVENTORY.toString());
	}

	public void setLastCVApproachViewUsed(String lastApproachViewUsed) {
		setPreferencesKey(LAST_CV_APPROACH_VIEW_USED, lastApproachViewUsed);
	}

	public int getLastCVNaturalClassesViewItemUsed() {
		return prefs.getInt(LAST_CV_NATURAL_CLASSES_VIEW_ITEM_USED, 0);
	}

	public void setLastCVNaturalClassesViewItemUsed(int value) {
		setPreferencesKey(LAST_CV_NATURAL_CLASSES_VIEW_ITEM_USED, value);
	}

	public int getLastCVSegmentInventoryViewItemUsed() {
		return prefs.getInt(LAST_CV_SEGMENT_INVENTORY_VIEW_ITEM_USED, 0);
	}

	public void setLastCVSegmentInventoryViewItemUsed(int value) {
		setPreferencesKey(LAST_CV_SEGMENT_INVENTORY_VIEW_ITEM_USED, value);
	}

	public int getLastCVSyllablePatternsViewItemUsed() {
		return prefs.getInt(LAST_CV_SYLLABLE_PATTERNS_VIEW_ITEM_USED, 0);
	}

	public void setLastCVSyllablePatternsViewItemUsed(int value) {
		setPreferencesKey(LAST_CV_SYLLABLE_PATTERNS_VIEW_ITEM_USED, value);
	}

	public int getLastCVWordsViewItemUsed() {
		return prefs.getInt(LAST_CV_WORDS_VIEW_ITEM_USED, 0);
	}

	public void setLastCVWordsViewItemUsed(int value) {
		setPreferencesKey(LAST_CV_WORDS_VIEW_ITEM_USED, value);
	}

	public int getLastCVWordsPredictedVsCorrectViewItemUsed() {
		return prefs.getInt(LAST_CV_WORDS_PREDICTED_VS_CORRECT_VIEW_ITEM_USED, 0);
	}

	public void setLastCVWordsPredictedVsCorrectViewItemUsed(int value) {
		setPreferencesKey(LAST_CV_WORDS_PREDICTED_VS_CORRECT_VIEW_ITEM_USED, value);
	}

	public String getLastCVTryAWordUsed() {
		return prefs.get(LAST_CV_TRY_A_WORD_USED, null);
	}

	public void setLastCVTryAWordUsed(String lastCVTryAWordUsed) {
		setPreferencesKey(LAST_CV_TRY_A_WORD_USED, lastCVTryAWordUsed);
	}

	public int getLastCVGraphemeNaturalClassesViewItemUsed() {
		return prefs.getInt(LAST_CV_GRAPHEME_NATURAL_CLASSES_VIEW_ITEM_USED, 0);
	}

	public void setLastCVGraphemeNaturalClassesViewItemUsed(int value) {
		setPreferencesKey(LAST_CV_GRAPHEME_NATURAL_CLASSES_VIEW_ITEM_USED, value);
	}

	public int getLastCVEnvironmentsViewItemUsed() {
		return prefs.getInt(LAST_CV_ENVIRONMENTS_VIEW_ITEM_USED, 0);
	}

	public void setLastCVEnvironmentsViewItemUsed(int value) {
		setPreferencesKey(LAST_CV_ENVIRONMENTS_VIEW_ITEM_USED, value);
	}

	public String getLastONCApproachViewUsed() {
		return prefs.get(LAST_ONC_APPROACH_VIEW_USED, ONCApproachView.SEGMENT_INVENTORY.toString());
	}

	public void setLastONCApproachViewUsed(String lastApproachViewUsed) {
		setPreferencesKey(LAST_ONC_APPROACH_VIEW_USED, lastApproachViewUsed);
	}

	public int getLastONCSegmentInventoryViewItemUsed() {
		return prefs.getInt(LAST_ONC_SEGMENT_INVENTORY_VIEW_ITEM_USED, 0);
	}

	public void setLastONCSegmentInventoryViewItemUsed(int value) {
		setPreferencesKey(LAST_ONC_SEGMENT_INVENTORY_VIEW_ITEM_USED, value);
	}

	public int getLastONCSonorityHierarchyViewItemUsed() {
		return prefs.getInt(LAST_ONC_SONORITY_HIERARCHY_VIEW_ITEM_USED, 0);
	}

	public void setLastONCSonorityHierarchyViewItemUsed(int value) {
		setPreferencesKey(LAST_ONC_SONORITY_HIERARCHY_VIEW_ITEM_USED, value);
	}

	public int getLastONCWordsViewItemUsed() {
		return prefs.getInt(LAST_ONC_WORDS_VIEW_ITEM_USED, 0);
	}

	public void setLastONCWordsViewItemUsed(int value) {
		setPreferencesKey(LAST_ONC_WORDS_VIEW_ITEM_USED, value);
	}

	public int getLastONCWordsPredictedVsCorrectViewItemUsed() {
		return prefs.getInt(LAST_ONC_WORDS_PREDICTED_VS_CORRECT_VIEW_ITEM_USED, 0);
	}

	public void setLastONCWordsPredictedVsCorrectViewItemUsed(int value) {
		setPreferencesKey(LAST_ONC_WORDS_PREDICTED_VS_CORRECT_VIEW_ITEM_USED, value);
	}

	public String getLastONCTryAWordUsed() {
		return prefs.get(LAST_ONC_TRY_A_WORD_USED, null);
	}

	public void setLastONCTryAWordUsed(String lastONCTryAWordUsed) {
		setPreferencesKey(LAST_ONC_TRY_A_WORD_USED, lastONCTryAWordUsed);
	}

	public int getLastONCGraphemeNaturalClassesViewItemUsed() {
		return prefs.getInt(LAST_ONC_GRAPHEME_NATURAL_CLASSES_VIEW_ITEM_USED, 0);
	}

	public void setLastONCGraphemeNaturalClassesViewItemUsed(int value) {
		setPreferencesKey(LAST_ONC_GRAPHEME_NATURAL_CLASSES_VIEW_ITEM_USED, value);
	}

	public int getLastONCEnvironmentsViewItemUsed() {
		return prefs.getInt(LAST_ONC_ENVIRONMENTS_VIEW_ITEM_USED, 0);
	}

	public void setLastONCEnvironmentsViewItemUsed(int value) {
		setPreferencesKey(LAST_ONC_ENVIRONMENTS_VIEW_ITEM_USED, value);
	}

	public int getLastONCCVNaturalClassesViewItemUsed() {
		return prefs.getInt(LAST_ONC_CV_NATURAL_CLASSES_VIEW_ITEM_USED, 0);
	}

	public void setLastONCCVNaturalClassesViewItemUsed(int value) {
		setPreferencesKey(LAST_ONC_CV_NATURAL_CLASSES_VIEW_ITEM_USED, value);
	}

	public int getLastONCTemplatesViewItemUsed() {
		return prefs.getInt(LAST_ONC_TEMPLATES_VIEW_ITEM_USED, 0);
	}

	public void setLastONCTemplatesViewItemUsed(int value) {
		setPreferencesKey(LAST_ONC_TEMPLATES_VIEW_ITEM_USED, value);
	}

	public int getLastONCFiltersViewItemUsed() {
		return prefs.getInt(LAST_ONC_FILTERS_VIEW_ITEM_USED, 0);
	}

	public void setLastONCFiltersViewItemUsed(int value) {
		setPreferencesKey(LAST_ONC_FILTERS_VIEW_ITEM_USED, value);
	}

	public String getLastSHApproachViewUsed() {
		return prefs.get(LAST_SH_APPROACH_VIEW_USED, SHApproachView.SEGMENT_INVENTORY.toString());
	}

	public void setLastSHApproachViewUsed(String lastApproachViewUsed) {
		setPreferencesKey(LAST_SH_APPROACH_VIEW_USED, lastApproachViewUsed);
	}

	public int getLastSHSegmentInventoryViewItemUsed() {
		return prefs.getInt(LAST_SH_SEGMENT_INVENTORY_VIEW_ITEM_USED, 0);
	}

	public void setLastSHSegmentInventoryViewItemUsed(int value) {
		setPreferencesKey(LAST_SH_SEGMENT_INVENTORY_VIEW_ITEM_USED, value);
	}

	public int getLastSHSonorityHierarchyViewItemUsed() {
		return prefs.getInt(LAST_SH_SONORITY_HIERARCHY_VIEW_ITEM_USED, 0);
	}

	public void setLastSHSonorityHierarchyViewItemUsed(int value) {
		setPreferencesKey(LAST_SH_SONORITY_HIERARCHY_VIEW_ITEM_USED, value);
	}

	public int getLastSHWordsViewItemUsed() {
		return prefs.getInt(LAST_SH_WORDS_VIEW_ITEM_USED, 0);
	}

	public void setLastSHWordsViewItemUsed(int value) {
		setPreferencesKey(LAST_SH_WORDS_VIEW_ITEM_USED, value);
	}

	public int getLastSHWordsPredictedVsCorrectViewItemUsed() {
		return prefs.getInt(LAST_SH_WORDS_PREDICTED_VS_CORRECT_VIEW_ITEM_USED, 0);
	}

	public void setLastSHWordsPredictedVsCorrectViewItemUsed(int value) {
		setPreferencesKey(LAST_SH_WORDS_PREDICTED_VS_CORRECT_VIEW_ITEM_USED, value);
	}

	public String getLastSHTryAWordUsed() {
		return prefs.get(LAST_SH_TRY_A_WORD_USED, null);
	}

	public void setLastSHTryAWordUsed(String lastSHTryAWordUsed) {
		setPreferencesKey(LAST_SH_TRY_A_WORD_USED, lastSHTryAWordUsed);
	}

	public int getLastSHGraphemeNaturalClassesViewItemUsed() {
		return prefs.getInt(LAST_SH_GRAPHEME_NATURAL_CLASSES_VIEW_ITEM_USED, 0);
	}

	public void setLastSHGraphemeNaturalClassesViewItemUsed(int value) {
		setPreferencesKey(LAST_SH_GRAPHEME_NATURAL_CLASSES_VIEW_ITEM_USED, value);
	}

	public int getLastSHEnvironmentsViewItemUsed() {
		return prefs.getInt(LAST_SH_ENVIRONMENTS_VIEW_ITEM_USED, 0);
	}

	public void setLastSHEnvironmentsViewItemUsed(int value) {
		setPreferencesKey(LAST_SH_ENVIRONMENTS_VIEW_ITEM_USED, value);
	}

	public boolean getLastUseCVApproachValue() {
		return prefs.getBoolean(LAST_USE_CV_APPROACH_VALUE, true);
	}

	public void setLastUseCVApproachValue(boolean lastUseCVApproachValue) {
		setPreferencesKey(LAST_USE_CV_APPROACH_VALUE, lastUseCVApproachValue);
	}

	public boolean getLastUseSHApproachValue() {
		return prefs.getBoolean(LAST_USE_SH_APPROACH_VALUE, false);
	}

	public void setLastUseSHApproachValue(boolean lastUseShApproachValue) {
		setPreferencesKey(LAST_USE_SH_APPROACH_VALUE, lastUseShApproachValue);
	}

	public boolean getLastUseONCApproachValue() {
		return prefs.getBoolean(LAST_USE_ONC_APPROACH_VALUE, true);
	}

	public void setLastUseONCApproachValue(boolean lastUseOncApproachValue) {
		setPreferencesKey(LAST_USE_ONC_APPROACH_VALUE, lastUseOncApproachValue);
	}

	public boolean getLastUseMoraicApproachValue() {
		return prefs.getBoolean(LAST_USE_MORAIC_APPROACH_VALUE, false);
	}

	public void setLastUseMoraicApproachValue(boolean lastUseMoraicApproachValue) {
		setPreferencesKey(LAST_USE_MORAIC_APPROACH_VALUE, lastUseMoraicApproachValue);
	}

	public boolean getLastUseNuclearProjectionApproachValue() {
		return prefs.getBoolean(LAST_USE_NUCLEAR_PROJECTION_APPROACH_VALUE, false);
	}

	public void setLastUseNuclearProjectionApproachValue(
			boolean lastUseNuclearProjectionApproachValue) {
		setPreferencesKey(LAST_USE_NUCLEAR_PROJECTION_APPROACH_VALUE, lastUseNuclearProjectionApproachValue);
	}

	public boolean getLastUseOTApproachValue() {
		return prefs.getBoolean(LAST_USE_OT_APPROACH_VALUE, false);
	}

	public void setLastUseOTApproachValue(boolean lastUseOtApproachValue) {
		setPreferencesKey(LAST_USE_OT_APPROACH_VALUE, lastUseOtApproachValue);
	}

	public Stage getLastWindowParameters(String sWindow, Stage stage, Double defaultHeight, Double defaultWidth) {
		Double value = prefs.getDouble(sWindow + HEIGHT, defaultHeight);
		stage.setHeight(value);
		value = prefs.getDouble(sWindow + WIDTH, defaultWidth);
		stage.setWidth(value);
		value = prefs.getDouble(sWindow + POSITION_X, 10);
		stage.setX(value);
		value = prefs.getDouble(sWindow + POSITION_Y, 10);
		stage.setY(value);
		boolean fValue = prefs.getBoolean(sWindow + MAXIMIZED, false);
		stage.setMaximized(fValue);
		return stage;
	}

	public void setLastWindowParameters(String sWindow, Stage stage) {
		boolean isMaximized = stage.isMaximized();
		if (!isMaximized) {
			setPreferencesKey(sWindow + HEIGHT, stage.getHeight());
			setPreferencesKey(sWindow + WIDTH, stage.getWidth());
			setPreferencesKey(sWindow + POSITION_X, stage.getX());
			setPreferencesKey(sWindow + POSITION_Y, stage.getY());
		}
		setPreferencesKey(sWindow + MAXIMIZED, stage.isMaximized());
	}

	public boolean getBooleanValue(String sKey, boolean defaultValue) {
		return prefs.getBoolean(sKey, defaultValue);
	}

	public Double getDoubleValue(String sKey, Double defaultValue) {
		return prefs.getDouble(sKey, defaultValue);
	}

	public String getStringValue(String sKey, String defaultValue) {
		return prefs.get(sKey, defaultValue);
	}

	public void setPreferencesKey(String key, boolean value) {
		if (!StringUtilities.isNullOrEmpty(key)) {
			if (key != null) {
				prefs.putBoolean(key, value);

			} else {
				prefs.remove(key);
			}
		}
	}

	public void setPreferencesKey(String key, int value) {
		if (!StringUtilities.isNullOrEmpty(key)) {
			if (key != null) {
				prefs.putInt(key, value);

			} else {
				prefs.remove(key);
			}
		}
	}

	public void setPreferencesKey(String key, Double value) {
		if (!StringUtilities.isNullOrEmpty(key)) {
			if (key != null && value != null) {
				prefs.putDouble(key, value);

			} else {
				prefs.remove(key);
			}
		}
	}

	public void setPreferencesKey(String key, String value) {
		if (!StringUtilities.isNullOrEmpty(key) && !StringUtilities.isNullOrEmpty(value)) {
			prefs.put(key, value);

		} else {
			prefs.remove(key);
		}
	}
}
