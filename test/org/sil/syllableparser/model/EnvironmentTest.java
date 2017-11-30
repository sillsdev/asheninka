/**
 * Copyright (c) 2016-2017 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Environment;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.service.AsheninkaGraphemeAndClassListener;

import org.sil.environmentparser.EnvironmentParser;

/**
 * @author Andy Black
 *
 */
public class EnvironmentTest extends org.sil.syllableparser.service.EnvironmentParsingBase {

	LanguageProject languageProject;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		classes = languageProject.getActiveGraphemeNaturalClasses();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void matchesEnvironmentTest() {
		// word boundaries
		Environment env = createEnvironmentFromRepresentation("/ # _");
		assertEquals(false, env.matches("a", "def", classes));
		assertEquals(false, env.matches("asbo", "def", classes));
		assertEquals(true, env.matches("", "def", classes));
		assertEquals(true, env.matches("", "", classes));

		env = createEnvironmentFromRepresentation("/ _ #");
		assertEquals(false, env.matches("a", "d", classes));
		assertEquals(false, env.matches("asbo", "def", classes));
		assertEquals(true, env.matches("a", "", classes));
		assertEquals(true, env.matches("", "", classes));

		env = createEnvironmentFromRepresentation("/ # _ #");
		assertEquals(false, env.matches("a", "d", classes));
		assertEquals(false, env.matches("asbo", "def", classes));
		assertEquals(false, env.matches("a", "", classes));
		assertEquals(true, env.matches("", "", classes));

		// graphemes
		env = createEnvironmentFromRepresentation("/ a _");
		assertEquals(true, env.matches("a", "d", classes));
		assertEquals(false, env.matches("b", "def", classes));
		assertEquals(true, env.matches("a", "", classes));
		assertEquals(false, env.matches("", "", classes));

		env = createEnvironmentFromRepresentation("/ _ a");
		assertEquals(true, env.matches("a", "a", classes));
		assertEquals(false, env.matches("b", "def", classes));
		assertEquals(true, env.matches("", "a", classes));
		assertEquals(false, env.matches("", "i", classes));
		assertEquals(false, env.matches("", "", classes));

		env = createEnvironmentFromRepresentation("/ a _ a");
		assertEquals(true, env.matches("a", "a", classes));
		assertEquals(true, env.matches("bia", "abd", classes));
		assertEquals(false, env.matches("b", "def", classes));
		assertEquals(false, env.matches("a", "", classes));
		assertEquals(false, env.matches("", "a", classes));
		assertEquals(false, env.matches("", "", classes));

		env = createEnvironmentFromRepresentation("/ b a _");
		assertEquals(true, env.matches("ba", "d", classes));
		assertEquals(false, env.matches("a", "d", classes));
		assertEquals(false, env.matches("b", "def", classes));
		assertEquals(true, env.matches("ba", "", classes));
		assertEquals(false, env.matches("", "", classes));

		env = createEnvironmentFromRepresentation("/ _ b a");
		assertEquals(true, env.matches("a", "ba", classes));
		assertEquals(false, env.matches("a", "b", classes));
		assertEquals(false, env.matches("b", "def", classes));
		assertEquals(true, env.matches("", "ba", classes));
		assertEquals(false, env.matches("", "", classes));

		env = createEnvironmentFromRepresentation("/ b a _ b a");
		assertEquals(true, env.matches("ba", "ba", classes));
		assertEquals(true, env.matches("biba", "bad", classes));
		assertEquals(false, env.matches("b", "def", classes));
		assertEquals(false, env.matches("a", "", classes));
		assertEquals(false, env.matches("", "a", classes));
		assertEquals(false, env.matches("", "", classes));

		env = createEnvironmentFromRepresentation("/ ba _");
		assertEquals(true, env.matches("ba", "d", classes));
		assertEquals(false, env.matches("a", "d", classes));
		assertEquals(false, env.matches("b", "def", classes));
		assertEquals(true, env.matches("ba", "", classes));
		assertEquals(false, env.matches("", "", classes));

		env = createEnvironmentFromRepresentation("/ _ ba");
		assertEquals(true, env.matches("a", "ba", classes));
		assertEquals(false, env.matches("a", "b", classes));
		assertEquals(false, env.matches("b", "def", classes));
		assertEquals(true, env.matches("", "ba", classes));
		assertEquals(false, env.matches("", "", classes));

		env = createEnvironmentFromRepresentation("/ ba _ ba");
		assertEquals(true, env.matches("ba", "ba", classes));
		assertEquals(true, env.matches("biba", "bad", classes));
		assertEquals(false, env.matches("b", "def", classes));
		assertEquals(false, env.matches("a", "", classes));
		assertEquals(false, env.matches("", "a", classes));
		assertEquals(false, env.matches("", "", classes));

		// optional graphemes
		env = createEnvironmentFromRepresentation("/ (a) _");
		assertEquals(true, env.matches("a", "d", classes));
		assertEquals(true, env.matches("b", "def", classes));
		assertEquals(true, env.matches("a", "", classes));
		assertEquals(true, env.matches("", "", classes));

		env = createEnvironmentFromRepresentation("/ _ (a)");
		assertEquals(true, env.matches("a", "a", classes));
		assertEquals(true, env.matches("b", "def", classes));
		assertEquals(true, env.matches("", "a", classes));
		assertEquals(true, env.matches("", "", classes));

		env = createEnvironmentFromRepresentation("/ (a) _ a");
		assertEquals(true, env.matches("a", "a", classes));
		assertEquals(true, env.matches("bia", "abd", classes));
		assertEquals(false, env.matches("b", "def", classes));
		assertEquals(false, env.matches("a", "", classes));
		assertEquals(true, env.matches("", "a", classes));
		assertEquals(false, env.matches("", "", classes));

		env = createEnvironmentFromRepresentation("/ a _ (a)");
		assertEquals(true, env.matches("a", "a", classes));
		assertEquals(true, env.matches("bia", "bad", classes));
		assertEquals(false, env.matches("b", "def", classes));
		assertEquals(true, env.matches("a", "", classes));
		assertEquals(false, env.matches("", "a", classes));
		assertEquals(false, env.matches("", "", classes));

		env = createEnvironmentFromRepresentation("/ (b)a _");
		assertEquals(true, env.matches("ba", "d", classes));
		assertEquals(true, env.matches("a", "d", classes));
		assertEquals(false, env.matches("b", "def", classes));
		assertEquals(true, env.matches("ba", "", classes));
		assertEquals(false, env.matches("", "", classes));

		env = createEnvironmentFromRepresentation("/ _ (b)a");
		assertEquals(true, env.matches("a", "ba", classes));
		assertEquals(false, env.matches("a", "b", classes));
		assertEquals(true, env.matches("b", "aef", classes));
		assertEquals(true, env.matches("", "a", classes));
		assertEquals(false, env.matches("", "", classes));

		env = createEnvironmentFromRepresentation("/ (b)a _ (b)a");
		assertEquals(true, env.matches("ba", "ba", classes));
		assertEquals(true, env.matches("a", "ba", classes));
		assertEquals(true, env.matches("ba", "a", classes));
		assertEquals(true, env.matches("a", "a", classes));
		assertEquals(true, env.matches("biba", "bad", classes));
		assertEquals(false, env.matches("b", "def", classes));
		assertEquals(false, env.matches("a", "", classes));
		assertEquals(false, env.matches("", "a", classes));

		env = createEnvironmentFromRepresentation("/ b(a) _");
		assertEquals(true, env.matches("ba", "d", classes));
		assertEquals(false, env.matches("a", "d", classes));
		assertEquals(true, env.matches("b", "def", classes));

		env = createEnvironmentFromRepresentation("/ _ b(a)");
		assertEquals(true, env.matches("a", "ba", classes));
		assertEquals(true, env.matches("a", "b", classes));
		assertEquals(false, env.matches("b", "def", classes));
		assertEquals(true, env.matches("", "bad", classes));

		env = createEnvironmentFromRepresentation("/ b(a) _ b(a)");
		assertEquals(true, env.matches("ba", "ba", classes));
		assertEquals(true, env.matches("b", "ba", classes));
		assertEquals(true, env.matches("ba", "bd", classes));
		assertEquals(true, env.matches("db", "bd", classes));
		assertEquals(true, env.matches("biba", "bad", classes));
		assertEquals(false, env.matches("b", "def", classes));
		assertEquals(false, env.matches("a", "", classes));
		assertEquals(false, env.matches("", "a", classes));
		
		// natural classes
		env = createEnvironmentFromRepresentation("/ [V] _ ");
		assertEquals(true, env.matches("a", "ba", classes));
		assertEquals(true, env.matches("ba", "ba", classes));
		assertEquals(true, env.matches("e", "ba", classes));
		assertEquals(true, env.matches("be", "ba", classes));
		assertEquals(true, env.matches("i", "ba", classes));
		assertEquals(true, env.matches("bi", "ba", classes));
		assertEquals(true, env.matches("o", "ba", classes));
		assertEquals(true, env.matches("bo", "ba", classes));
		assertEquals(false, env.matches("u", "ba", classes)); // no u in V
		assertEquals(false, env.matches("bu", "ba", classes));
		assertEquals(false, env.matches("", "a", classes));
		assertEquals(false, env.matches("b", "a", classes));
		
		env = createEnvironmentFromRepresentation("/ _ [V]");
		assertEquals(true, env.matches("ba", "a", classes));
		assertEquals(true, env.matches("ba", "ab", classes));
		assertEquals(true, env.matches("ba", "e", classes));
		assertEquals(true, env.matches("ba", "eb", classes));
		assertEquals(true, env.matches("ba", "i", classes));
		assertEquals(true, env.matches("ba", "ib", classes));
		assertEquals(true, env.matches("ba", "o", classes));
		assertEquals(true, env.matches("ba", "ob", classes));
		assertEquals(false, env.matches("ba", "u", classes));
		assertEquals(false, env.matches("ba", "ub", classes));
		assertEquals(false, env.matches("a", "", classes));
		assertEquals(false, env.matches("", "b", classes));
		
		env = createEnvironmentFromRepresentation("/ [V] _[V] ");
		assertEquals(true, env.matches("a", "ab", classes));
		assertEquals(true, env.matches("ba", "ab", classes));
		assertEquals(true, env.matches("e", "eb", classes));
		assertEquals(true, env.matches("be", "eb", classes));
		assertEquals(true, env.matches("i", "ib", classes));
		assertEquals(true, env.matches("bi", "ib", classes));
		assertEquals(true, env.matches("o", "ob", classes));
		assertEquals(true, env.matches("bo", "ob", classes));
		assertEquals(false, env.matches("u", "ub", classes)); // no u in V
		assertEquals(false, env.matches("bu", "ub", classes));
		assertEquals(false, env.matches("", "a", classes));
		assertEquals(false, env.matches("e", "", classes));
		assertEquals(false, env.matches("b", "", classes));
		assertEquals(false, env.matches("", "b", classes));
		assertEquals(false, env.matches("b", "b", classes));
		
		env = createEnvironmentFromRepresentation("/ [uV] _ ");
		// uV has u as well as all V
		assertEquals(true, env.matches("a", "ba", classes));
		assertEquals(true, env.matches("ba", "ba", classes));
		assertEquals(true, env.matches("e", "ba", classes));
		assertEquals(true, env.matches("be", "ba", classes));
		assertEquals(true, env.matches("i", "ba", classes));
		assertEquals(true, env.matches("bi", "ba", classes));
		assertEquals(true, env.matches("o", "ba", classes));
		assertEquals(true, env.matches("bo", "ba", classes));
		assertEquals(true, env.matches("u", "ba", classes));
		assertEquals(true, env.matches("bu", "ba", classes));
		assertEquals(false, env.matches("", "a", classes));
		assertEquals(false, env.matches("b", "", classes));
		
		env = createEnvironmentFromRepresentation("/ _ [uV]");
		assertEquals(true, env.matches("ba", "a", classes));
		assertEquals(true, env.matches("ba", "ab", classes));
		assertEquals(true, env.matches("ba", "e", classes));
		assertEquals(true, env.matches("ba", "eb", classes));
		assertEquals(true, env.matches("ba", "i", classes));
		assertEquals(true, env.matches("ba", "ib", classes));
		assertEquals(true, env.matches("ba", "o", classes));
		assertEquals(true, env.matches("ba", "ob", classes));
		assertEquals(true, env.matches("ba", "u", classes));
		assertEquals(true, env.matches("ba", "ub", classes));
		assertEquals(false, env.matches("a", "", classes));
		assertEquals(false, env.matches("", "b", classes));
		
		env = createEnvironmentFromRepresentation("/ [uV] _[uV] ");
		assertEquals(true, env.matches("a", "ab", classes));
		assertEquals(true, env.matches("ba", "ab", classes));
		assertEquals(true, env.matches("e", "eb", classes));
		assertEquals(true, env.matches("be", "eb", classes));
		assertEquals(true, env.matches("i", "ib", classes));
		assertEquals(true, env.matches("bi", "ib", classes));
		assertEquals(true, env.matches("o", "ob", classes));
		assertEquals(true, env.matches("bo", "ob", classes));
		assertEquals(true, env.matches("u", "ub", classes));
		assertEquals(true, env.matches("bu", "ub", classes));
		assertEquals(false, env.matches("", "a", classes));
		assertEquals(false, env.matches("e", "", classes));
		assertEquals(false, env.matches("b", "", classes));
		assertEquals(false, env.matches("", "b", classes));
		assertEquals(false, env.matches("b", "b", classes));
		
		// optional natural classes
		env = createEnvironmentFromRepresentation("/ b([V]) _ ");
		assertEquals(false, env.matches("a", "ba", classes));
		assertEquals(true, env.matches("ba", "ba", classes));
		assertEquals(false, env.matches("e", "ba", classes));
		assertEquals(true, env.matches("be", "ba", classes));
		assertEquals(false, env.matches("i", "ba", classes));
		assertEquals(true, env.matches("bi", "ba", classes));
		assertEquals(false, env.matches("o", "ba", classes));
		assertEquals(true, env.matches("bo", "ba", classes));
		assertEquals(false, env.matches("u", "ba", classes)); // no u in V
		assertEquals(false, env.matches("bu", "ba", classes));
		assertEquals(false, env.matches("", "a", classes));
		assertEquals(true, env.matches("b", "a", classes));
		
		env = createEnvironmentFromRepresentation("/ _ ([V])b");
		assertEquals(false, env.matches("ba", "a", classes));
		assertEquals(true, env.matches("ba", "ab", classes));
		assertEquals(false, env.matches("ba", "e", classes));
		assertEquals(true, env.matches("ba", "eb", classes));
		assertEquals(false, env.matches("ba", "i", classes));
		assertEquals(true, env.matches("ba", "ib", classes));
		assertEquals(false, env.matches("ba", "o", classes));
		assertEquals(true, env.matches("ba", "ob", classes));
		assertEquals(false, env.matches("ba", "u", classes));
		assertEquals(false, env.matches("ba", "ub", classes));
		assertEquals(false, env.matches("a", "", classes));
		assertEquals(true, env.matches("", "b", classes));
		
		env = createEnvironmentFromRepresentation("/ b([V]) _[V] ");
		assertEquals(false, env.matches("a", "ab", classes));
		assertEquals(true, env.matches("ba", "ab", classes));
		assertEquals(false, env.matches("e", "eb", classes));
		assertEquals(true, env.matches("be", "eb", classes));
		assertEquals(false, env.matches("i", "ib", classes));
		assertEquals(true, env.matches("bi", "ib", classes));
		assertEquals(false, env.matches("o", "ob", classes));
		assertEquals(true, env.matches("bo", "ob", classes));
		assertEquals(false, env.matches("u", "ub", classes)); // no u in V
		assertEquals(false, env.matches("bu", "ub", classes));
		assertEquals(false, env.matches("", "a", classes));
		assertEquals(false, env.matches("e", "", classes));
		assertEquals(false, env.matches("b", "", classes));
		assertEquals(false, env.matches("", "b", classes));
		assertEquals(false, env.matches("b", "b", classes));
		
		env = createEnvironmentFromRepresentation("/ [V] _([V])b ");
		assertEquals(true, env.matches("a", "ab", classes));
		assertEquals(true, env.matches("ba", "ab", classes));
		assertEquals(true, env.matches("e", "eb", classes));
		assertEquals(true, env.matches("be", "eb", classes));
		assertEquals(true, env.matches("i", "ib", classes));
		assertEquals(true, env.matches("bi", "ib", classes));
		assertEquals(true, env.matches("o", "ob", classes));
		assertEquals(true, env.matches("bo", "ob", classes));
		assertEquals(false, env.matches("u", "ub", classes)); // no u in V
		assertEquals(false, env.matches("bu", "ub", classes));
		assertEquals(false, env.matches("", "a", classes));
		assertEquals(false, env.matches("e", "", classes));
		assertEquals(false, env.matches("b", "", classes));
		assertEquals(false, env.matches("", "b", classes));
		assertEquals(false, env.matches("b", "b", classes));
		
		env = createEnvironmentFromRepresentation("/ b([V]) _([V])b ");
		assertEquals(false, env.matches("a", "ab", classes));
		assertEquals(true, env.matches("ba", "ab", classes));
		assertEquals(false, env.matches("e", "eb", classes));
		assertEquals(true, env.matches("be", "eb", classes));
		assertEquals(false, env.matches("i", "ib", classes));
		assertEquals(true, env.matches("bi", "ib", classes));
		assertEquals(false, env.matches("o", "ob", classes));
		assertEquals(true, env.matches("bo", "ob", classes));
		assertEquals(false, env.matches("u", "ub", classes)); // no u in V
		assertEquals(false, env.matches("bu", "ub", classes));
		assertEquals(false, env.matches("", "a", classes));
		assertEquals(false, env.matches("e", "", classes));
		assertEquals(false, env.matches("b", "", classes));
		assertEquals(false, env.matches("", "b", classes));
		assertEquals(true, env.matches("b", "b", classes));
		
		// combinations
		env = createEnvironmentFromRepresentation("/ #b([V]) _([V])b# ");
		assertEquals(false, env.matches("a", "ab", classes));
		assertEquals(true, env.matches("ba", "ab", classes));
		assertEquals(false, env.matches("oba", "ab", classes));
		assertEquals(false, env.matches("oba", "abi", classes));
		assertEquals(false, env.matches("oba", "abi", classes));
		assertEquals(false, env.matches("bi", "", classes));
		assertEquals(false, env.matches("", "eb", classes));
		assertEquals(true, env.matches("be", "eb", classes));
		assertEquals(false, env.matches("bo", "ebd", classes));
		assertEquals(false, env.matches("dbo", "eb", classes));
		assertEquals(false, env.matches("b", "", classes));
		assertEquals(false, env.matches("", "b", classes));
		assertEquals(true, env.matches("b", "b", classes));
		
		
	}

	private Environment createEnvironmentFromRepresentation(String sEnv) {
		EnvironmentParser parser = parseEnvironmentString(sEnv);
		int iNumErrors = parser.getNumberOfSyntaxErrors();
		assertEquals(0, iNumErrors);
		AsheninkaGraphemeAndClassListener listener = (AsheninkaGraphemeAndClassListener) parser
				.getParseListeners().get(0);
		assertNotNull(listener);
		Environment env = listener.getEnvironment();
		return env;
	}

	@Test
	public void rebuildRepresentationFromContextTest() {
		// just word boundaries
		checkRebuildRepresentationFromContext("/ # _");
		checkRebuildRepresentationFromContext("/ _ #");
		checkRebuildRepresentationFromContext("/ # _ #");

		// graphemes
		checkRebuildRepresentationFromContext("/ _ a");
		checkRebuildRepresentationFromContext("/ a _ a");
		checkRebuildRepresentationFromContext("/ b a _");
		checkRebuildRepresentationFromContext("/ _ b a");
		checkRebuildRepresentationFromContext("/ b a _ b a");
		checkRebuildRepresentationFromContext("/ ba _");
		checkRebuildRepresentationFromContext("/ _ ba");
		checkRebuildRepresentationFromContext("/ ba _ ba");

		// optional graphemes
		checkRebuildRepresentationFromContext("/ (a) _");
		checkRebuildRepresentationFromContext("/ _ (a)");
		checkRebuildRepresentationFromContext("/ (a) _ a");
		checkRebuildRepresentationFromContext("/ a _ (a)");
		checkRebuildRepresentationFromContext("/ (b) a _");
		checkRebuildRepresentationFromContext("/ _ (b) a");
		checkRebuildRepresentationFromContext("/ (b) a _ (b) a");
		checkRebuildRepresentationFromContext("/ b (a) _");
		checkRebuildRepresentationFromContext("/ _ b (a)");
		checkRebuildRepresentationFromContext("/ b (a) _ b (a)");

		// natural classes
		checkRebuildRepresentationFromContext("/ [V] _");
		checkRebuildRepresentationFromContext("/ _ [V]");
		checkRebuildRepresentationFromContext("/ [V] _ [V]");
		checkRebuildRepresentationFromContext("/ [uV] _");

		// uV has u as well as all V
		checkRebuildRepresentationFromContext("/ _ [uV]");
		checkRebuildRepresentationFromContext("/ [uV] _ [uV]");

		// optional natural classes
		checkRebuildRepresentationFromContext("/ b ([V]) _");
		checkRebuildRepresentationFromContext("/ _ ([V]) b");
		checkRebuildRepresentationFromContext("/ b ([V]) _ [V]");
		checkRebuildRepresentationFromContext("/ [V] _ ([V]) b");
		checkRebuildRepresentationFromContext("/ b ([V]) _ ([V]) b");

		// combinations
		checkRebuildRepresentationFromContext("/ #b ([V]) _ ([V]) b#");
	}

	private void checkRebuildRepresentationFromContext(String sEnv) {
		Environment env = createEnvironmentFromRepresentation(sEnv);
		env.rebuildRepresentationFromContext();
		assertEquals(sEnv, env.getEnvironmentRepresentation());
	}
}
