// Copyright (c) 2016-2017 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package sil.org.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import sil.org.environmentparser.EnvironmentConstants;
import sil.org.environmentparser.EnvironmentErrorInfo;
import sil.org.environmentparser.EnvironmentErrorListener;
import sil.org.environmentparser.EnvironmentErrorListener.VerboseListener;
import sil.org.environmentparser.EnvironmentLexer;
import sil.org.environmentparser.EnvironmentParser;
import sil.org.environmentparser.GraphemeErrorInfo;
import sil.org.syllableparser.Constants;
import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.Environment;
import sil.org.syllableparser.model.EnvironmentContext;
import sil.org.syllableparser.model.EnvironmentContextGraphemeOrNaturalClass;
import sil.org.syllableparser.model.Grapheme;
import sil.org.syllableparser.model.GraphemeNaturalClass;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.cvapproach.CVApproach;

public class EnvironmentContextsBuilderTest {

	List<String> graphemesMasterList = Arrays.asList("a", "ai", "b", "c", "d", "e", "f", "fl",
			"fr", "a", // test duplicate
			"\u00ED", // single combined Unicode acute i (í)
			"i\u0301", // combined acute i
			"H");

	List<String> classesMasterList = Arrays.asList("V", "Vowels", "C", "+son", "C", "+lab, +vd",
			"+ant, -cor, -vd");

	CVApproach cva;
	List<Grapheme> activeGraphemes;
	List<GraphemeNaturalClass> classes;

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
		assertEquals("a", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ a _ #", false, true, 1, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		// segment
		env = checkValidEnvironment("/ a _", false, false, 1, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ _ b", false, false, 0, 1);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("b", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ a _ b", false, false, 1, 1);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("b", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		
		env = checkValidEnvironment("/ a b _ ai d #", false, true, 2, 2);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("b", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("ai", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("d", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		
		
		// segments
		env = checkValidEnvironment("/ a b _", false, false, 2, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("b", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ _ a b", false, false, 0, 2);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("b", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ a b _ a b", false, false, 2, 2);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("b", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("b", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		
		// segments without intervening spaces
		env = checkValidEnvironment("/ ab _", false, false, 1, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("ab", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ _ ab", false, false, 0, 1);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("ab", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ ab _ ab", false, false, 1, 1);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("ab", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("ab", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ flaid _", false, false, 1, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("flaid", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());

		env = checkValidEnvironment("/ _ flaid", false, false, 0, 1);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("flaid", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ flaid _ flaid", false, false, 1, 1);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("flaid", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("flaid", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		// optionality
		env = checkValidEnvironment("/ (a) b _", false, false, 2, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("b", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		
		env = checkValidEnvironment("/ _ (a) b", false, false, 0, 2);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("b", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/(c) d _ (a) b", false, false, 2, 2);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("c", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("d", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("b", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ (f) (fl) _ ", false, false, 2, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("f", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("fl", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());

		env = checkValidEnvironment("/ _ (d) (c)", false, false, 0, 2);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("d", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("c", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());

		env = checkValidEnvironment("/ (f) (fl) _ (d) (c)", false, false, 2, 2);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("f", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("fl", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("d", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("c", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());

		// Natural Classes
		env = checkValidEnvironment("/ [V] _ ", false, false, 1, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("V", gnc.getGraphemeOrNaturalClass());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		
		env = checkValidEnvironment("/ _ [V]", false, false, 0, 1);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("V", gnc.getGraphemeOrNaturalClass());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ [V] _ [V]", false, false, 1, 1);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("V", gnc.getGraphemeOrNaturalClass());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("V", gnc.getGraphemeOrNaturalClass());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		// Natural Classes with spaces in the name
		env = checkValidEnvironment("/ [+lab, +vd] _ ", false, false, 1, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("+lab, +vd", gnc.getGraphemeOrNaturalClass());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ _ [+lab, +vd]", false, false, 0, 1);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("+lab, +vd", gnc.getGraphemeOrNaturalClass());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ [+lab, +vd] _ [+lab, +vd]", false, false, 1, 1);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("+lab, +vd", gnc.getGraphemeOrNaturalClass());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("+lab, +vd", gnc.getGraphemeOrNaturalClass());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		// Natural Classes with optionality
		env = checkValidEnvironment("/ ([C]) [V] _ ", false, false, 2, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("C", gnc.getGraphemeOrNaturalClass());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("V", gnc.getGraphemeOrNaturalClass());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ _ ([C]) [V]", false, false, 0, 2);
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("C", gnc.getGraphemeOrNaturalClass());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("V", gnc.getGraphemeOrNaturalClass());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ [V] ([C]) _ ([C]) [V]", false, false, 2, 2);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("V", gnc.getGraphemeOrNaturalClass());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("C", gnc.getGraphemeOrNaturalClass());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("C", gnc.getGraphemeOrNaturalClass());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("V", gnc.getGraphemeOrNaturalClass());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		// Combo
		env = checkValidEnvironment("/# ([V]) b fr [C] _ a (c) #", true, true, 4, 2);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("V", gnc.getGraphemeOrNaturalClass());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("b", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		gnc = gncList.get(2);
		assertEquals("fr", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		gnc = gncList.get(3);
		assertEquals("C", gnc.getGraphemeOrNaturalClass());
		assertEquals(false, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		rightContext = env.getRightContext();
		gncList = rightContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("a", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		gnc = gncList.get(1);
		assertEquals("c", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(true, gnc.isOptional());

		// single combined Unicode acute i (í)
		env = checkValidEnvironment("/ \u00ED _", false, false, 1, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("í", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
		
		// Unicode i followed by combining acute (í) 
		env = checkValidEnvironment("/ i\u0301 _", false, false, 1, 0); 
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("i\u0301", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());

		env = checkValidEnvironment("/ H _", false, false, 1, 0);
		leftContext = env.getLeftContext();
		gncList = leftContext.getEnvContext();
		gnc = gncList.get(0);
		assertEquals("H", gnc.getGraphemeOrNaturalClass());
		assertEquals(true, gnc.isGrapheme());
		assertEquals(false, gnc.isOptional());
	}

	private Environment checkValidEnvironment(String sEnv, boolean fWordInitial, boolean fWordFinal, int iLeftItems, int iRightItems) {
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

	private EnvironmentParser parseEnvironmentString(String sInput) {
		CharStream input = CharStreams.fromString(sInput);
		EnvironmentLexer lexer = new EnvironmentLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		EnvironmentParser parser = new EnvironmentParser(tokens);
		// begin parsing at rule 'environment'
		ParseTree tree = parser.environment();
		ParseTreeWalker walker = new ParseTreeWalker(); // create standard
														// walker
		AsheninkaGraphemeAndClassListener validator = new AsheninkaGraphemeAndClassListener(parser,
				graphemesMasterList, classesMasterList);
		validator.setCheckForReduplication(true);
		Environment env = new Environment();
		validator.setEnvironment(env);
		walker.walk(validator, tree); // initiate walk of tree with listener
		parser.addParseListener(validator);
		return parser;
	}
}
