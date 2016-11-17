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

import sil.org.syllableparser.model.ApproachType;
import sil.org.syllableparser.model.cvapproach.CVApproachView;

public class ApplicationPreferencesTest {

	ApplicationPreferences applicationPreferences;
	File fileLastUsed;
	String languageLastUsed;
	Double windowXLastUsed;
	Double windowYLastUsed;
	Double windowHeightLastUsed;
	Double windowWidthLastUsed;
	boolean windowLastMaximized;
	String approachLastUsed;
	String approachViewLastUsed;

	@Before
	public void setUp() throws Exception {
		applicationPreferences = new ApplicationPreferences(this);
		fileLastUsed = applicationPreferences.getLastOpenedFile();
		languageLastUsed = applicationPreferences.getLastLocaleLanguage();
		windowHeightLastUsed = applicationPreferences.getLastWindowHeight();
		windowWidthLastUsed = applicationPreferences.getLastWindowWidth();
		windowXLastUsed = applicationPreferences.getLastWindowPositionX();
		windowYLastUsed = applicationPreferences.getLastWindowPositionY();
		windowLastMaximized = applicationPreferences.getLastWindowMaximized();
		approachLastUsed = applicationPreferences.getLastApproachUsed();
		approachViewLastUsed = applicationPreferences.getLastApproachViewUsed();
		applicationPreferences.setLastOpenedFilePath("last opened file");
		applicationPreferences.setLastLocaleLanguage("en");
	}

	@After
	public void tearDown() throws Exception {
		applicationPreferences.setLastOpenedFilePath(fileLastUsed);
		applicationPreferences.setLastLocaleLanguage(languageLastUsed);
		applicationPreferences.setLastWindowHeight(windowHeightLastUsed);
		applicationPreferences.setLastWindowWidth(windowWidthLastUsed);
		applicationPreferences.setLastWindowPositionX(windowXLastUsed);
		applicationPreferences.setLastWindowPositionY(windowYLastUsed);
		applicationPreferences.setLastWindowMaximized(windowLastMaximized);
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
		applicationPreferences.setLastWindowHeight(560.);
		assertEquals("main window height", 560., applicationPreferences.getLastWindowHeight(), 0);
		applicationPreferences.setLastWindowWidth(860.);
		assertEquals("main window width", 860., applicationPreferences.getLastWindowWidth(), 0);
		applicationPreferences.setLastWindowPositionX(20.);
		assertEquals("main window X position", 20., applicationPreferences.getLastWindowPositionX(), 0);
		applicationPreferences.setLastWindowPositionY(20.);
		assertEquals("main window Y position", 20., applicationPreferences.getLastWindowPositionY(), 0);
		applicationPreferences.setLastWindowMaximized(true);
		assertEquals("main window maximized", true, applicationPreferences.getLastWindowMaximized());
		applicationPreferences.setLastWindowMaximized(false);
		assertEquals("main window minimized", false, applicationPreferences.getLastWindowMaximized());
	}

}
