package sil.org.syllableparser;

import java.io.File;
import java.util.Arrays;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ApplicationPreferences {

	final static String kLastOpenedFilePath = "lastOpenedFilePath";
	final static String kLastOpenedDirectoryPath = "lastOpenedDirectoryPath";
	final static String kLastLocaleLanguage = "lastLocaleLanguage";
	// Not trying to be anglo-centric, but we have to start with something...
	final static String kDefaultLocaleLanguage = "en";

	Preferences prefs;

	public ApplicationPreferences(Object app) {
		prefs = Preferences.userNodeForPackage(app.getClass());
	}

	public String getLastOpenedFilePath() {
		return prefs.get(kLastOpenedFilePath, null);
	}

	public void setLastOpenedFilePath(String lastOpenedFile) {
		setPreferencesKey(kLastOpenedFilePath, lastOpenedFile);
	}

	public String getLastLocaleLanguage() {
		return prefs.get(kLastLocaleLanguage, kDefaultLocaleLanguage);
	}

	public void setLastLocaleLanguage(String lastLocaleLanguage) {
		setPreferencesKey(kLastLocaleLanguage, lastLocaleLanguage);
	}

	public File getLastOpenedFile() {
		String filePath = prefs.get(kLastOpenedFilePath, null);
		if (filePath != null) {
			return new File(filePath);
		} else {
			return null;
		}
	}

	public void setLastOpenedFilePath(File file) {
		if (file != null) {
			setPreferencesKey(kLastOpenedFilePath, file.getPath());

		} else {
			prefs.remove(kLastOpenedFilePath);
		}
	}

	public String getLastOpenedDirectoryPath() {
		return prefs.get(kLastOpenedDirectoryPath, "");
	}

	public void setLastOpenedDirectoryPath(String directoryPath) {
		setPreferencesKey(kLastOpenedDirectoryPath, directoryPath);
	}

	private void setPreferencesKey(String key, String value) {
		if (key != null && value != null && !value.isEmpty()) {
			prefs.put(key, value);

		} else {
			prefs.remove(key);
		}
	}
}
