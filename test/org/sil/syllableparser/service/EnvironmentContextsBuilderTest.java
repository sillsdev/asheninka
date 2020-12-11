// Copyright (c) 2016-2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package org.sil.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Approach;
import org.sil.syllableparser.model.Environment;
import org.sil.syllableparser.model.EnvironmentContext;
import org.sil.syllableparser.model.EnvironmentContextGraphemeOrNaturalClass;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.GraphemeNaturalClass;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.service.AsheninkaGraphemeAndClassListener;
import org.sil.antlr4.environmentparser.antlr4generated.EnvironmentParser;

public class EnvironmentContextsBuilderTest extends EnvironmentParsingBase {

	Approach cva;
	List<Grapheme> activeGraphemes;

	@Before
	public void setUp() throws Exception {
		LanguageProject languageProject = new LanguageProject();
		Locale locale = new Locale("en");
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		File file = new File(Constants.UNIT_TEST_DATA_FILE);
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		cva = languageProject.getCVApproach();
		activeGraphemes = languageProject.getActiveGraphemes();
		classes = languageProject.getActiveGraphemeNaturalClasses();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void validEnvironmentsTest() {
		Environment env;
		EnvironmentContext leftContext;
		EnvironmentContext rightContext;
		List<EnvironmentContextGraphemeOrNaturalClass> gncList;
		EnvironmentContextGraphemeOrNaturalClass gnc;

		// word boundary
		env = checkValidEnvironment("/ # _", true, false, 0, 0);

		env = checkValidEnvironment("/ _ #", false, true, 0, 0);

		env = checkValidEnvironment("/ # _ #", true, true, 0, 0);

		env = checkValidEnvironment("/ # _ a", true, false, 0, 1);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ a _ #", false, true, 1, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		// segment
		env = checkValidEnvironment("/ a _", false, false, 1, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ _ b", false, false, 0, 1);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("b", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ a _ b", false, false, 1, 1);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("b", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ a b _ ai d #", false, true, 2, 2);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("b", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("ai", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("d", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		// segments
		env = checkValidEnvironment("/ a b _", false, false, 2, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("b", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ _ a b", false, false, 0, 2);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("b", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ a b _ a b", false, false, 2, 2);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("b", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("b", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		// segments without intervening spaces
		env = checkValidEnvironment("/ ab _", false, false, 1, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("ab", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ _ ab", false, false, 0, 1);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("ab", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ ab _ ab", false, false, 1, 1);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("ab", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("ab", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ flaid _", false, false, 1, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("flaid", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());

		env = checkValidEnvironment("/ _ flaid", false, false, 0, 1);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("flaid", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ flaid _ flaid", false, false, 1, 1);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("flaid", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("flaid", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		// optionality
		env = checkValidEnvironment("/ (a) b _", false, false, 2, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("b", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ _ (a) b", false, false, 0, 2);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("b", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/(c) d _ (a) b", false, false, 2, 2);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("c", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("d", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("b", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ (f) (fl) _ ", false, false, 2, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("f", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("fl", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());

		env = checkValidEnvironment("/ _ (d) (c)", false, false, 0, 2);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("d", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("c", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());

		env = checkValidEnvironment("/ (f) (fl) _ (d) (c)", false, false, 2, 2);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("f", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("fl", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("d", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("c", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());

		// Natural Classes
		env = checkValidEnvironment("/ [V] _ ", false, false, 1, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("V", gnc.getGraphemeString());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		checkGraphemeNaturalClass(gnc, 1);

		env = checkValidEnvironment("/ _ [V]", false, false, 0, 1);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("V", gnc.getGraphemeString());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		checkGraphemeNaturalClass(gnc, 1);

		env = checkValidEnvironment("/ [V] _ [V]", false, false, 1, 1);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("V", gnc.getGraphemeString());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		checkGraphemeNaturalClass(gnc, 1);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("V", gnc.getGraphemeString());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		checkGraphemeNaturalClass(gnc, 1);

		// Natural Classes with spaces in the name
		env = checkValidEnvironment("/ [+lab, +vd] _ ", false, false, 1, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("+lab, +vd", gnc.getGraphemeString());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		checkGraphemeNaturalClass(gnc, 0);

		env = checkValidEnvironment("/ _ [+lab, +vd]", false, false, 0, 1);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("+lab, +vd", gnc.getGraphemeString());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		checkGraphemeNaturalClass(gnc, 0);

		env = checkValidEnvironment("/ [+lab, +vd] _ [+lab, +vd]", false, false, 1, 1);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("+lab, +vd", gnc.getGraphemeString());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		checkGraphemeNaturalClass(gnc, 0);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("+lab, +vd", gnc.getGraphemeString());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		checkGraphemeNaturalClass(gnc, 0);

		// Natural Classes with optionality
		env = checkValidEnvironment("/ ([C]) [V] _ ", false, false, 2, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("C", gnc.getGraphemeString());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		checkGraphemeNaturalClass(gnc, 0);
		gnc = gncList.get(1);
		assertEquals("V", gnc.getGraphemeString());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		checkGraphemeNaturalClass(gnc, 1);

		env = checkValidEnvironment("/ _ ([C]) [V]", false, false, 0, 2);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("C", gnc.getGraphemeString());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		checkGraphemeNaturalClass(gnc, 0);
		gnc = gncList.get(1);
		assertEquals("V", gnc.getGraphemeString());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		checkGraphemeNaturalClass(gnc, 1);

		env = checkValidEnvironment("/ [V] ([C]) _ ([C]) [V]", false, false, 2, 2);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("V", gnc.getGraphemeString());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		checkGraphemeNaturalClass(gnc, 1);
		gnc = gncList.get(1);
		assertEquals("C", gnc.getGraphemeString());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		checkGraphemeNaturalClass(gnc, 0);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("C", gnc.getGraphemeString());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		checkGraphemeNaturalClass(gnc, 0);
		gnc = gncList.get(1);
		assertEquals("V", gnc.getGraphemeString());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		checkGraphemeNaturalClass(gnc, 1);

		// Combo
		env = checkValidEnvironment("/# ([V]) b fr [C] _ a (c) #", true, true, 4, 2);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("V", gnc.getGraphemeString());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		checkGraphemeNaturalClass(gnc, 1);
		gnc = gncList.get(1);
		assertEquals("b", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		gnc = gncList.get(2);
		assertEquals("fr", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		gnc = gncList.get(3);
		assertEquals("C", gnc.getGraphemeString());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		checkGraphemeNaturalClass(gnc, 0);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("c", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());

		// single combined Unicode acute i (í)
		env = checkValidEnvironment("/ \u00ED _", false, false, 1, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("í", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		// Unicode i followed by combining acute (í)
		env = checkValidEnvironment("/ i\u0301 _", false, false, 1, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("i\u0301", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ H _", false, false, 1, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("H", gnc.getGraphemeString());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
	}

	private void checkGraphemeNaturalClass(EnvironmentContextGraphemeOrNaturalClass gnc,
			int iMatches) {
		final String sName = gnc.getGraphemeString();
		List<GraphemeNaturalClass> matches = classes.stream()
				.filter(n -> n.getNCName().equals(sName)).collect(Collectors.toList());
		assertEquals(iMatches, matches.size());
		if (iMatches > 0) {
			GraphemeNaturalClass gnClassFound = matches.get(0);
			gnc.setGraphemeNaturalClass(gnClassFound);
			GraphemeNaturalClass gnClassStored = gnc.getGraphemeNaturalClass();
			assertEquals(gnClassFound.getID(), gnClassStored.getID());
		}
	}

	private Environment checkValidEnvironment(String sEnv, boolean fWordInitial,
			boolean fWordFinal, int iLeftItems, int iRightItems) {
		EnvironmentParser parser = parseEnvironmentString(sEnv);
		int iNumErrors = parser.getNumberOfSyntaxErrors();
		assertEquals(0, iNumErrors);
		AsheninkaGraphemeAndClassListener listener = (AsheninkaGraphemeAndClassListener) parser
				.getParseListeners().get(0);
		assertNotNull(listener);
		Environment env = listener.getEnvironment();
		assertNotNull(env);
		EnvironmentContext leftContext = env.getLeftContext();
		EnvironmentContext rightContext = env.getRightContext();
		assertEquals(fWordInitial, leftContext.isWordBoundary());
		assertEquals(fWordFinal, rightContext.isWordBoundary());
		List<EnvironmentContextGraphemeOrNaturalClass> gncList;
		gncList = leftContext.getEnvContext();
		assertEquals(iLeftItems, gncList.size());
		gncList = rightContext.getEnvContext();
		assertEquals(iRightItems, gncList.size());
		return env;
	}
}
