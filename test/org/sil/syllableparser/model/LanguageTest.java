// Copyright (c) 2016-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model;

import static org.junit.Assert.*;
import javafx.scene.text.Font;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.model.Language;

/**
 * @author Andy Black
 *
 */
public class LanguageTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Language lang = new Language();
		checkFontInfo(lang, "System", 12.0, "Regular");
		
		lang = new Language("System", 9.0, "Bold", null, null, null, null);
		checkFontInfo(lang, "System",  9.0, "Bold");
		
		lang = new Language("System", 10.0, "Italic", null, null, null, null);
		checkFontInfo(lang, "System",  10.0, "Italic");
		
		lang = new Language("Charis SIL", 14.0, "Bold Italic", null, null, null, null);
		checkFontInfo(lang, "Charis SIL", 14.0, "Bold Italic");
	}
	
	private void checkFontInfo(Language lang, String fontFamily, double fontSize, String fontStyle) {
		assertEquals(fontFamily, lang.getFontFamily());
		assertEquals(fontSize, lang.getFontSize(), 0.0);
		assertEquals(fontStyle, lang.getFontType());
		Font font = lang.getFont();
		assertEquals(fontFamily, font.getFamily());
		assertEquals(fontSize, font.getSize(), 0.0);
		assertEquals(fontStyle, font.getStyle());
	}

}
