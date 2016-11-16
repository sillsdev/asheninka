// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
package sil.org.syllableparser;

import java.io.File;
import java.util.Arrays;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

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
		return prefs.getDouble(LAST_WINDOW_HEIGHT, 10);
	}

	public void setLastWindowHeight(Double value) {
		setPreferencesKey(LAST_WINDOW_HEIGHT, value);
	}

	public Double getLastWindowWidth() {
		return prefs.getDouble(LAST_WINDOW_WIDTH, 10);
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

	private void setPreferencesKey(String key, boolean value) {
		if (!StringUtilities.isNullOrEmpty(key)) {
			if (key != null) {
				prefs.putBoolean(key, value);

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
