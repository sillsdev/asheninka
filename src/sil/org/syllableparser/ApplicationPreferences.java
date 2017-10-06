// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
package sil.org.syllableparser;

import java.io.File;
import java.util.prefs.Preferences;

import javafx.stage.Stage;
import sil.org.syllableparser.model.ApproachType;
import sil.org.syllableparser.model.cvapproach.CVApproachView;
import sil.org.utility.*;

public class ApplicationPreferences {

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
	static final String LAST_CV_NATURAL_CLASSES_VIEW_ITEM_USED = "lastCVNaturalClassesViewItemUsed";
	static final String LAST_CV_SEGMENT_INVENTORY_VIEW_ITEM_USED = "lastCVSegmentInventoryViewItemUsed";
	static final String LAST_CV_SYLLABLE_PATTERNS_VIEW_ITEM_USED = "lastCVSyllablePatternsViewItemUsed";
	static final String LAST_CV_TRY_A_WORD_USED = "lastCVTryAWordUsed";
	static final String LAST_CV_WORDS_VIEW_ITEM_USED = "lastCVWordsViewItemUsed";
	static final String LAST_CV_GRAPHEME_NATURAL_CLASSES_VIEW_ITEM_USED = "lastCVGraphemeNaturalClassesViewItemUsed";
	static final String LAST_CV_ENVIRONMENTS_VIEW_ITEM_USED = "lastCVEnvironmentsViewItemUsed";
	// We have a last item used for predicted vs. correct words, but we're not
	// setting it because it is not clear why it would be useful. We'll use it if users
	// request it.
	static final String LAST_CV_WORDS_PREDICTED_VS_CORRECT_VIEW_ITEM_USED = "lastCVWordPredictedVsCorrectViewItemUsed";

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

	private void setPreferencesKey(String key, boolean value) {
		if (!StringUtilities.isNullOrEmpty(key)) {
			if (key != null) {
				prefs.putBoolean(key, value);

			} else {
				prefs.remove(key);
			}
		}
	}

	private void setPreferencesKey(String key, int value) {
		if (!StringUtilities.isNullOrEmpty(key)) {
			if (key != null) {
				prefs.putInt(key, value);

			} else {
				prefs.remove(key);
			}
		}
	}

	private void setPreferencesKey(String key, Double value) {
		if (!StringUtilities.isNullOrEmpty(key)) {
			if (key != null && value != null) {
				prefs.putDouble(key, value);

			} else {
				prefs.remove(key);
			}
		}
	}

	private void setPreferencesKey(String key, String value) {
		if (!StringUtilities.isNullOrEmpty(key) && !StringUtilities.isNullOrEmpty(value)) {
			prefs.put(key, value);

		} else {
			prefs.remove(key);
		}
	}
}
