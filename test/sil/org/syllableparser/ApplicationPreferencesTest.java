package sil.org.syllableparser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ApplicationPreferencesTest {

	ApplicationPreferences prefs;
	@Before
	public void setUp() throws Exception {
	prefs = new ApplicationPreferences("last opened file",  "en",  "EN");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void lastOpenedFile() {
		assertEquals("last opened file", "last opened file", ApplicationPreferences.getLastOpenedFilePath());
		ApplicationPreferences.setLastOpenedFilePath("mimi");
		assertEquals("last opened file", "mimi", ApplicationPreferences.getLastOpenedFilePath());
		try {
			File f = File.createTempFile("ApplicationPreferencesTest", null);
			ApplicationPreferences.setLastOpenedFilePath(f);
			String fPath = f.getPath();
			f.delete();
			assertEquals("file path", fPath, ApplicationPreferences.getLastOpenedFilePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Test
	public void lastLanguageAndCountry() {
		assertEquals("locale language", "en", ApplicationPreferences.getLastLocaleLanguage());
		ApplicationPreferences.setLastLocaleLanguage("es");
		assertEquals("locale language", "es", ApplicationPreferences.getLastLocaleLanguage());
		assertEquals("locale country", "EN", ApplicationPreferences.getLastLocaleCountry());
		ApplicationPreferences.setLastLocaleCountry("ES");
		assertEquals("locale country", "ES", ApplicationPreferences.getLastLocaleCountry());
		
	}
}
