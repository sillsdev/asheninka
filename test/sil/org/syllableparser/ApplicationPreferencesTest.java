// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
package sil.org.syllableparser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ApplicationPreferencesTest {

	ApplicationPreferences applicationPreferences;
	File fileLastUsed;
	String languageLastUsed;

	@Before
	public void setUp() throws Exception {
		applicationPreferences = new ApplicationPreferences(this);
		fileLastUsed = applicationPreferences.getLastOpenedFile();
		languageLastUsed = applicationPreferences.getLastLocaleLanguage();
		applicationPreferences.setLastOpenedFilePath("last opened file");
		applicationPreferences.setLastLocaleLanguage("en");
	}

	@After
	public void tearDown() throws Exception {
		applicationPreferences.setLastOpenedFilePath(fileLastUsed);
		applicationPreferences.setLastLocaleLanguage(languageLastUsed);
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
	public void lastLanguage() {
		assertEquals("locale language", "en", applicationPreferences.getLastLocaleLanguage());
		applicationPreferences.setLastLocaleLanguage("es");
		assertEquals("locale language", "es", applicationPreferences.getLastLocaleLanguage());
	}
}
