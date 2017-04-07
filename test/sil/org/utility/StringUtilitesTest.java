// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
package sil.org.utility;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StringUtilitesTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void isNullOrEmptyTest() {
		String s = null;
		assertEquals(true, StringUtilities.isNullOrEmpty(s));
		s = "";
		assertEquals(true, StringUtilities.isNullOrEmpty(s));
		s = "123";
		assertEquals(false, StringUtilities.isNullOrEmpty(s));
	}

	@Test
	public void removeFromStartTest() {
		// basic test
		String s = "a.b.c.d.e.f";
		String sMatch = ".";
		String sResult = StringUtilities.removeFromStart(s, sMatch, 0);
		assertEquals("a.b.c.d.e.f", sResult);
		sResult = StringUtilities.removeFromStart(s, sMatch, 1);
		assertEquals("a.b.c.d.e.f", sResult);
		sResult = StringUtilities.removeFromStart(s, sMatch, 2);
		assertEquals("ab.c.d.e.f", sResult);
		sResult = StringUtilities.removeFromStart(s, sMatch, 3);
		assertEquals("abc.d.e.f", sResult);
		sResult = StringUtilities.removeFromStart(s, sMatch, 4);
		assertEquals("abcd.e.f", sResult);
		sResult = StringUtilities.removeFromStart(s, sMatch, 5);
		assertEquals("abcde.f", sResult);
		sResult = StringUtilities.removeFromStart(s, sMatch, 6);
		assertEquals("abcdef", sResult);
		sResult = StringUtilities.removeFromStart(s, sMatch, 7);
		assertEquals("abcdef", sResult);
		// empty string
		s = "";
		sResult = StringUtilities.removeFromStart(s, sMatch, 3);
		assertEquals("", sResult);
		// basic test
		s = "ta.da";
		sResult = StringUtilities.removeFromStart(s, sMatch, 2);
		assertEquals("ta.da", sResult);
		sResult = StringUtilities.removeFromStart(s, sMatch, 3);
		assertEquals("tada", sResult);
		// two characters treated as one
		s = "tá.dà";
		sResult = StringUtilities.removeFromStart(s, sMatch, 2);
		assertEquals("tá.dà", sResult);
		sResult = StringUtilities.removeFromStart(s, sMatch, 3);
		assertEquals("tádà", sResult);
		s = "tǎ.da̋";
		sResult = StringUtilities.removeFromStart(s, sMatch, 2);
		assertEquals("tǎ.da̋", sResult);
		sResult = StringUtilities.removeFromStart(s, sMatch, 3);
		assertEquals("tǎda̋", sResult);
		// tie-bar treated as a single character
		s = "t͡ʃa.da";
		sResult = StringUtilities.removeFromStart(s, sMatch, 2);
		assertEquals("t͡ʃa.da", sResult);
		sResult = StringUtilities.removeFromStart(s, sMatch, 3);
		assertEquals("t͡ʃa.da", sResult);
		sResult = StringUtilities.removeFromStart(s, sMatch, 4);
		assertEquals("t͡ʃada", sResult);
	}

	@Test
	public void removeFromEndTest() {
		// basic test
		String s = "a.b.c.d.e.f";
		String sMatch = ".";
		String sResult = StringUtilities.removeFromEnd(s, sMatch, 0);
		assertEquals("a.b.c.d.e.f", sResult);
		sResult = StringUtilities.removeFromEnd(s, sMatch, 1);
		assertEquals("a.b.c.d.e.f", sResult);
		sResult = StringUtilities.removeFromEnd(s, sMatch, 2);
		assertEquals("a.b.c.d.ef", sResult);
		sResult = StringUtilities.removeFromEnd(s, sMatch, 3);
		assertEquals("a.b.c.def", sResult);
		sResult = StringUtilities.removeFromEnd(s, sMatch, 4);
		assertEquals("a.b.cdef", sResult);
		sResult = StringUtilities.removeFromEnd(s, sMatch, 5);
		assertEquals("a.bcdef", sResult);
		sResult = StringUtilities.removeFromEnd(s, sMatch, 6);
		assertEquals("abcdef", sResult);
		sResult = StringUtilities.removeFromEnd(s, sMatch, 7);
		assertEquals("abcdef", sResult);
		// empty string
		s = "";
		sResult = StringUtilities.removeFromEnd(s, sMatch, 3);
		assertEquals("", sResult);
		// basic test
		s = "ta.da";
		sResult = StringUtilities.removeFromEnd(s, sMatch, 2);
		assertEquals("ta.da", sResult);
		sResult = StringUtilities.removeFromEnd(s, sMatch, 3);
		assertEquals("tada", sResult);
		// two characters treated as one
		s = "tá.dà";
		sResult = StringUtilities.removeFromEnd(s, sMatch, 2);
		assertEquals("tá.dà", sResult);
		sResult = StringUtilities.removeFromEnd(s, sMatch, 3);
		assertEquals("tádà", sResult);
		s = "tǎ.da̋";
		sResult = StringUtilities.removeFromEnd(s, sMatch, 2);
		assertEquals("tǎ.da̋", sResult);
		sResult = StringUtilities.removeFromEnd(s, sMatch, 3);
		assertEquals("tǎda̋", sResult);
		// tie-bar treated as a single character
		s = "da.t͡ʃa";
		sResult = StringUtilities.removeFromEnd(s, sMatch, 2);
		assertEquals("da.t͡ʃa", sResult);
		sResult = StringUtilities.removeFromEnd(s, sMatch, 3);
		assertEquals("da.t͡ʃa", sResult);
		sResult = StringUtilities.removeFromEnd(s, sMatch, 4);
		assertEquals("dat͡ʃa", sResult);
	}

	@Test
	public void adjustForWindowsFileSeparatorTest() {
		// basic test
		String s = "/src/sil/org";
		String sResult = StringUtilities.adjustForWindowsFileSeparator(s);
		assertEquals(s, sResult);
		if (System.getProperty("os.name").contains("Windows")) {
			String sWindows = "\\src\\sil\\org";
			sResult = StringUtilities.adjustForWindowsFileSeparator(sWindows);
			assertEquals(s, sResult);
		}
	}
}
