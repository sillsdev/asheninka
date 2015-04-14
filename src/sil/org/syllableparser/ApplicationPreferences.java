package sil.org.syllableparser;

import java.io.File;
import java.util.prefs.Preferences;

public class ApplicationPreferences {
	
	final static String kLastOpenedFilePath = "lastOpenedFilePath";
	final static String kLastLocaleLanguage = "lastLocaleLanguage";
	final static String kLastLocaleCountry = "lastLocaleCountry";
	
	public ApplicationPreferences() {
	}
	
	public ApplicationPreferences(String lastOpenedFilePath,
			String lastLocaleLanguage, String lastLocaleCountry) {
		super();
		setPreferencesKey(kLastOpenedFilePath, lastOpenedFilePath);
		setPreferencesKey(kLastLocaleLanguage, lastLocaleLanguage);
		setPreferencesKey(kLastLocaleCountry, lastLocaleCountry);
	}

	public static String getLastOpenedFilePath() {
		return getPreferencesKey(kLastOpenedFilePath);
	}

	public static void setLastOpenedFilePath(String lastOpenedFile) {
		setPreferencesKey(kLastOpenedFilePath, lastOpenedFile);
	}

	public static String getLastLocaleLanguage() {
		return getPreferencesKey(kLastLocaleLanguage);
	}

	public static void setLastLocaleLanguage(String lastLocaleLanguage) {
		setPreferencesKey(kLastLocaleLanguage, lastLocaleLanguage);
	}

	public static String getLastLocaleCountry() {
		return getPreferencesKey(kLastLocaleCountry);
	}

	public static void setLastLocaleCountry(String lastLocaleCountry) {
		setPreferencesKey(kLastLocaleCountry, lastLocaleCountry);;
	}

	public static File getLastOpenedFile() {
	    String filePath = getPreferencesKey(kLastOpenedFilePath);
	    if (filePath != null) {
	        return new File(filePath);
	    } else {
	        return null;
	    }
	}

	public static void setLastOpenedFilePath(File file) {
	    Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    if (file != null) {
	    	setPreferencesKey(kLastOpenedFilePath, file.getPath());

	    } else {
	        prefs.remove(kLastOpenedFilePath);
	    }
	}

	private static String getPreferencesKey(String key) {
	    Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    String value = prefs.get(key, null);
	    if (key != null && !key.isEmpty()) {
	        return prefs.get(key, value);
	    }
	    return null;
	}
	
	private static void setPreferencesKey(String key, String value) {
	    Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    if (value != null && !value.isEmpty()) {
	        prefs.put(key, value);

	    } else {
	        prefs.remove(key);
	    }
	}
}
