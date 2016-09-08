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
	public void isNullOrEmpty() {
		String s = null;
		assertEquals(true, StringUtilities.isNullOrEmpty(s));
		s = "";
		assertEquals(true, StringUtilities.isNullOrEmpty(s));
		s = "123";
		assertEquals(false, StringUtilities.isNullOrEmpty(s));		
	}

}
