// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
package sil.org.syllableparser;

import java.io.File;
import java.util.Arrays;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import sil.org.syllableparser.model.ApproachType;
import sil.org.syllableparser.model.cvapproach.CVApproachView;
import sil.org.utility.*;

public class ApplicationPreferences {

	static final String LAST_OPENED_FILE_PATH = "lastOpenedFilePath";
	static final String LAST_OPENED_DIRECTORY_PATH = "lastOpenedDirectoryPath";
	static final String LAST_LOCALE_LANGUAGE = "lastLocaleLanguage";
	// Not trying to be anglo-centric, but we have to start with something...
	static final String DEFAULT_LOCALE_LANGUAGE = "en";
	static final String LAST_WINDOW_POSITION_X = "lastWindowPositionX";
	static final String LAST_WINDOW_POSITION_Y = "lastWindowPositionY";
	static final String LAST_WINDOW_WIDTH = "lastWindowWidth";
	static final String LAST_WINDOW_HEIGHT = "lastWindowHeight";
	static final String LAST_WINDOW_MAXIMIZED = "lastWindowMaximized";
	static final String LAST_APPROACH_USED = "lastApproachUsed";
	static final String LAST_APPROACH_VIEW_USED = "lastApproachViewUsed";
	static final String LAST_APPROACH_VIEW_ITEM_USED = "lastApproachViewItemUsed";
	
	static final String LAST_CV_NATURAL_CLASSES_VIEW_ITEM_USED = "lastCVNaturalClassesViewItemUsed";
	static final String LAST_CV_SEGMENT_INVENTORY_VIEW_ITEM_USED = "lastCVSegemntInventoryViewItemUsed";
	static final String LAST_CV_SYLLABLE_PATTERNS_VIEW_ITEM_USED = "lastCVSyllablePatternsViewItemUsed";
	static final String LAST_CV_WORDS_VIEW_ITEM_USED = "lastCVWordsViewItemUsed";
	// We have one for predicted vs. correct words, but we're not setting it
	// because it is not clear why it would be useful.
	static final String LAST_CV_WORDS_PREDICTED_VS_CORRECT_VIEW_ITEM_USED = "lastCVWordPredictedVsCorrectViewItemUsed";
	
	static final String LAST_CV_TRY_A_WORD_USED = "lastCVTryAWordUsed";
	
	
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

	public Double getLastWindowPositionX() {
		return prefs.getDouble(LAST_WINDOW_POSITION_X, 10);
	}

	public void setLastWindowPositionX(Double value) {
		setPreferencesKey(LAST_WINDOW_POSITION_X, value);
	}

	public Double getLastWindowPositionY() {
		return prefs.getDouble(LAST_WINDOW_POSITION_Y, 10);
	}

	public void setLastWindowPositionY(Double value) {
		setPreferencesKey(LAST_WINDOW_POSITION_Y, value);
	}

	public Double getLastWindowHeight() {
		return prefs.getDouble(LAST_WINDOW_HEIGHT, 660);
	}

	public void setLastWindowHeight(Double value) {
		setPreferencesKey(LAST_WINDOW_HEIGHT, value);
	}

	public Double getLastWindowWidth() {
		return prefs.getDouble(LAST_WINDOW_WIDTH, 1000);
	}

	public void setLastWindowWidth(Double value) {
		setPreferencesKey(LAST_WINDOW_WIDTH, value);
	}

	public boolean getLastWindowMaximized() {
		return prefs.getBoolean(LAST_WINDOW_MAXIMIZED, false);
	}

	public void setLastWindowMaximized(boolean value) {
		setPreferencesKey(LAST_WINDOW_MAXIMIZED, value);
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
			// if (key != null && value != null && !value.isEmpty()) {
			prefs.put(key, value);

		} else {
			prefs.remove(key);
		}
	}
}
