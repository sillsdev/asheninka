// Copyright (c) 2016-2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package org.sil.environmentparser;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import org.sil.environmentparser.CheckGraphemeAndClassListener;
import org.sil.environmentparser.EnvironmentConstants;
import org.sil.environmentparser.EnvironmentErrorInfo;
import org.sil.environmentparser.EnvironmentErrorListener;
import org.sil.environmentparser.antlr4generated.EnvironmentLexer;
import org.sil.environmentparser.antlr4generated.EnvironmentParser;
import org.sil.environmentparser.GraphemeErrorInfo;
import org.sil.environmentparser.EnvironmentErrorListener.VerboseListener;

public class EnvironmentRecognizerTest {

	List<String> graphemesMasterList = Arrays.asList("a", "ai", "b", "c", "d", "e", "f", "fl",
			"fr", "a", // test duplicate
			"\u00ED", // single combined Unicode acute i (í)
			"i\u0301", // combined acute i
			"H");

	List<String> classesMasterList = Arrays.asList("V", "Vowels", "C", "+son", "C", "+lab, +vd",
			"+ant, -cor, -vd");

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void validEnvironmentsTest() {
		checkValidEnvironment("/ # _"); // word boundary
		checkValidEnvironment("/ _ #");
		checkValidEnvironment("/ # _ #");
		checkValidEnvironment("/ # _ a");
		checkValidEnvironment("/ a _ #");
		checkValidEnvironment("/ a _"); // segment
		checkValidEnvironment("/ _ b");
		checkValidEnvironment("/ a _ b");
		checkValidEnvironment("/ a b _ ai d #");
		checkValidEnvironment("/ a b _"); // segments
		checkValidEnvironment("/ _ a b");
		checkValidEnvironment("/ a b _ a b");
		checkValidEnvironment("/ ab _"); // segments without intervening spaces
		checkValidEnvironment("/ _ ab");
		checkValidEnvironment("/ ab _ ab");
		checkValidEnvironment("/ flaid _");
		checkValidEnvironment("/ _ flaid");
		checkValidEnvironment("/ flaid _ flaid");
		checkValidEnvironment("/ (a) b _"); // optionality
		checkValidEnvironment("/ _ (a) b");
		checkValidEnvironment("/(c) d _ (a) b");
		checkValidEnvironment("/ (f) (fl) _ ");
		checkValidEnvironment("/ _ (d) (c)");
		checkValidEnvironment("/ (f) (fl) _ (d) (c)");
		checkValidEnvironment("/ [V] _ "); // Natural Classes
		checkValidEnvironment("/ _ [V]");
		checkValidEnvironment("/ [V] _ [V]");
		checkValidEnvironment("/ [+lab, +vd] _ "); // Natural Classes with
													// spaces in the name
		checkValidEnvironment("/ _ [+lab, +vd]");
		checkValidEnvironment("/ [+lab, +vd] _ [+lab, +vd]");
		checkValidEnvironment("/ [+lab,  +vd] _ ");
		checkValidEnvironment("/ _ [+lab,  +vd]");
		checkValidEnvironment("/ [+lab,  +vd] _ [+lab,  +vd]");
		checkValidEnvironment("/ [+ant, -cor, -vd] _ ");
		checkValidEnvironment("/ _ [+ant, -cor, -vd]");
		checkValidEnvironment("/ [+ant, -cor, -vd] _ [+ant, -cor, -vd]");
		checkValidEnvironment("/ ([C]) [V] _ "); // Natural Classes with
													// optionality
		checkValidEnvironment("/ _ ([C]) [V]");
		checkValidEnvironment("/ [V] ([C]) _ ([C]) [V]");
		checkValidEnvironment("/# ([V]) b fr [C] _ a (c) #"); // Combo
		checkValidEnvironment("/ _ [C^1]"); // Reduplication
		checkValidEnvironment("/ _ [V^1]");
		checkValidEnvironment("/ _ [C^1][V^1]");
		checkValidEnvironment("/ _ [C^1] [V^1]");
		checkValidEnvironment("/ \u00ED _"); // single combined Unicode acute i
												// (í)
		checkValidEnvironment("/ i\u0301 _"); // Unicode i followed by combining
												// acute (í)
		checkValidEnvironment("/ H _");
	}

	private void checkValidEnvironment(String sEnv) {
		assertEquals(0, parseAString(sEnv));
	}

	private int parseAString(String sInput) {
		CharStream input = CharStreams.fromString(sInput);
		EnvironmentLexer lexer = new EnvironmentLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		EnvironmentParser parser = new EnvironmentParser(tokens);
		// begin parsing at rule 'environment'
		ParseTree tree = parser.environment();
		int numErrors = parser.getNumberOfSyntaxErrors();
		ParseTreeWalker walker = new ParseTreeWalker(); // create standard
														// walker
		CheckGraphemeAndClassListener validator = new CheckGraphemeAndClassListener(parser,
				graphemesMasterList, classesMasterList);
		validator.setCheckForReduplication(true);
		walker.walk(validator, tree); // initiate walk of tree with listener
		return numErrors;
	}

	private EnvironmentParser parseAStringExpectFailure(String sInput) {
		CharStream input = CharStreams.fromString(sInput);
		EnvironmentLexer lexer = new EnvironmentLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		EnvironmentParser parser = new EnvironmentParser(tokens);
		parser.removeErrorListeners();
		// to test for ambiguity: parser.addErrorListener(new
		// DiagnosticErrorListener());
		// parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
		VerboseListener errListener = new EnvironmentErrorListener.VerboseListener();
		errListener.clearErrorMessageList();
		parser.addErrorListener(errListener);
		// begin parsing at rule 'environment'
		ParseTree tree = parser.environment();
		//int iNumErrors = parser.getNumberOfSyntaxErrors();
		// List<EnvironmentErrorInfo> errors = errListener.getErrorMessages();
		// System.out.println(errors.size());
		// String sTree = tree.toStringTree(parser);
		return parser;
	}

	@Test
	public void invalidEnvironmentsTest() {
		// two slashes
		checkInvalidEnvironment("/ /_", EnvironmentConstants.TOO_MANY_SLASHES, 3, 1);
		// two underscores
		checkInvalidEnvironment("/ a _ _", EnvironmentConstants.TOO_MANY_UNDERSCORES, 7, 1);
		// two underscores
		checkInvalidEnvironment("/ _ _ a", EnvironmentConstants.TOO_MANY_UNDERSCORES, 7, 1);
		// two word boundaries before underscore
		checkInvalidEnvironment("/ # #_", EnvironmentConstants.TOO_MANY_WORD_INITIAL_BOUNDARIES, 5, 1);
		// two word boundaries before underscore
		checkInvalidEnvironment("/ # # a_", EnvironmentConstants.TOO_MANY_WORD_INITIAL_BOUNDARIES, 7, 1);
		// two word boundaries after underscore
		checkInvalidEnvironment("/ _##", EnvironmentConstants.TOO_MANY_WORD_FINAL_BOUNDARIES, 5, 1);
		// two word boundaries after underscore
		checkInvalidEnvironment("/ _a##", EnvironmentConstants.TOO_MANY_WORD_FINAL_BOUNDARIES, 6, 1);
		// letter after word boundary
		checkInvalidEnvironment("/ _# a", EnvironmentConstants.CONTENT_AFTER_WORD_FINAL_BOUNDARY, 6, 1);
		// letter before word boundary
		checkInvalidEnvironment("/a # _", EnvironmentConstants.CONTENT_BEFORE_WORD_INITIAL_BOUNDARY, 5, 1);
		// class after word boundary
		checkInvalidEnvironment("/ _# [C]", EnvironmentConstants.CONTENT_AFTER_WORD_FINAL_BOUNDARY, 8, 1);
		// class before word boundary
		checkInvalidEnvironment("/[C] # _", EnvironmentConstants.CONTENT_BEFORE_WORD_INITIAL_BOUNDARY, 7, 1);
		// two word boundaries and two underscores
		checkInvalidEnvironment("/ _# _ #", EnvironmentConstants.TOO_MANY_UNDERSCORES, 8, 2);
		// missing closing paren before underscore
		checkInvalidEnvironment("/ ( _", EnvironmentConstants.MISSING_CLASS_OR_GRAPHEME, 4, 1);
		// missing closing paren before underscore
		checkInvalidEnvironment("/ () _", EnvironmentConstants.MISSING_CLASS_OR_GRAPHEME, 5, 1);
		// missing closing paren before underscore
		checkInvalidEnvironment("/ (a _", EnvironmentConstants.MISSING_CLOSING_PAREN, 5, 1);
		// missing opening paren before underscore
		checkInvalidEnvironment("/ a) _", EnvironmentConstants.MISSING_OPENING_PAREN, 5, 1);
		// missing closing paren after underscore
		checkInvalidEnvironment("/ _ (a", EnvironmentConstants.MISSING_CLOSING_PAREN, 6, 1);
		// missing opening paren after underscore
		checkInvalidEnvironment("/ _ a)", EnvironmentConstants.MISSING_OPENING_PAREN, 6, 1);
		// missing class before underscore
		checkInvalidEnvironment("/ [ _", EnvironmentConstants.MISSING_CLASS_AFTER_OPENING_SQUARE_BRACKET, 4, 1);
		// missing class before underscore
		checkInvalidEnvironment("/ [] _", EnvironmentConstants.MISSING_CLASS_AFTER_OPENING_SQUARE_BRACKET, 5, 1);
		// missing closing bracket before underscore
		checkInvalidEnvironment("/ [C _", EnvironmentConstants.MISSING_CLOSING_SQUARE_BRACKET, 5, 1);
		// missing opening bracket before underscore
		checkInvalidEnvironment("/  ] _", EnvironmentConstants.MISSING_OPENING_SQUARE_BRACKET, 5, 1);
		// missing opening bracket before underscore
		checkInvalidEnvironment("/  C] _", EnvironmentConstants.MISSING_OPENING_SQUARE_BRACKET, 6, 1);
		// missing closing bracket after underscore
		checkInvalidEnvironment("/ _ [C", EnvironmentConstants.MISSING_CLOSING_SQUARE_BRACKET, 6, 1);
		// missing opening bracket after underscore
		checkInvalidEnvironment("/ _ C]", EnvironmentConstants.MISSING_OPENING_SQUARE_BRACKET, 6, 1);
		// missing closing bracket before closing paren
		checkInvalidEnvironment("/ ([C) _", EnvironmentConstants.MISSING_CLOSING_SQUARE_BRACKET, 5, 1);
		// missing closing paren after closing bracket
		checkInvalidEnvironment("/ ([C] _", EnvironmentConstants.MISSING_CLOSING_PAREN, 7, 1);
		// missing opening bracket and missing closing paren before underscore
		checkInvalidEnvironment("/  (C] _", EnvironmentConstants.MISSING_CLOSING_PAREN, 7, 2);
		// missing closing bracket and closing paren after underscore
		checkInvalidEnvironment("/ _ ([C", EnvironmentConstants.MISSING_CLOSING_PAREN, 7, 2);
		// missing closing bracket and closing paren after underscore
		checkInvalidEnvironment("/ _ ([C]", EnvironmentConstants.MISSING_CLOSING_PAREN, 8, 1);
		// missing opening bracket and closing paren after underscore
		checkInvalidEnvironment("/ _ (C]", EnvironmentConstants.MISSING_CLOSING_PAREN, 7, 2);
	}

	private void checkInvalidEnvironment(String sEnv, String sFailedPortion, int iPos,
			int iNumErrors) {
		// TODO: check position and message
		EnvironmentParser parser = parseAStringExpectFailure(sEnv);
		assertEquals(iNumErrors, parser.getNumberOfSyntaxErrors());
		VerboseListener errListener = (VerboseListener) parser.getErrorListeners().get(0);
		assertNotNull(errListener);
		int i = errListener.getErrorMessages().size();
		EnvironmentErrorInfo info = errListener.getErrorMessages().get(i - 1);
		assertNotNull(info);
		assertEquals(sFailedPortion, info.getMsg());
		assertEquals(iPos, info.getCharPositionInLine());
	}

	@Test
	public void validSyntaxBadContentEnvironmentTest() {
		// chr not in segment list (before underscore)
		checkValidSyntaxBadContent("/ chr _", "chr", 3, 0, 1, 0, 1);
		// chr not in segment list (after underscore)
		checkValidSyntaxBadContent("/ _ chr", "chr", 5, 0, 1, 0, 1);
		// g is not in segment list (before underscore)
		checkValidSyntaxBadContent("/ frage _", "frage", 3, 0, 1, 0, 3);
		// g is not in segment list (after underscore)
		checkValidSyntaxBadContent("/ _ frage", "frage", 5, 0, 1, 0, 3);
		// +lab not in class list (before underscore)
		checkValidSyntaxBadContent("/ [+lab] _", "+lab", 3, 0, 0, 1, 1);
		// +lab not in class list (after underscore)
		checkValidSyntaxBadContent("/ _ [+lab]", "+lab", 5, 0, 0, 1, 1);
		// +lab, -vd not in class list (before underscore)
		checkValidSyntaxBadContent("/ [+lab, -vd] _", "+lab, -vd", 3, 0, 0, 1, 1);
		// +lab, -vd not in class list (after underscore)
		checkValidSyntaxBadContent("/ _ [+lab, -vd]", "+lab, -vd", 5, 0, 0, 1, 1);
		// +lab, -vd not in class list (before underscore)
		checkValidSyntaxBadContent("/ [+lab, +vd, -cor] _", "+lab, +vd, -cor", 3, 0, 0, 1, 1);
		// +lab, -vd not in class list (after underscore)
		checkValidSyntaxBadContent("/ _ [+lab, +vd, -cor]", "+lab, +vd, -cor", 5, 0, 0, 1, 1);
		// wedge used as part of a class name: C^
		checkValidSyntaxBadContent("/ _ [C^]", "C^", 5, 0, 0, 1, 1);
		// wedge used as part of a class name: C^C
		checkValidSyntaxBadContent("/ _ [C^C]", "C^C", 5, 0, 0, 1, 1);
		// wedge used as part of a class name: C^1C
		checkValidSyntaxBadContent("/ _ [C^1C]", "C^1C", 5, 0, 0, 1, 1);
		// X not in class list
		checkValidSyntaxBadContent("/ _ [X][Y]", "X, Y", 5, 0, 0, 2, 1);

	}

	private EnvironmentParser parseAStringWithContentError(String sInput) {
		CharStream input = CharStreams.fromString(sInput);
		EnvironmentLexer lexer = new EnvironmentLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		EnvironmentParser parser = new EnvironmentParser(tokens);
		// begin parsing at rule 'environment'
		ParseTree tree = parser.environment();
		ParseTreeWalker walker = new ParseTreeWalker(); // create standard
														// walker
		CheckGraphemeAndClassListener validator = new CheckGraphemeAndClassListener(parser,
				graphemesMasterList, classesMasterList);
		validator.setCheckForReduplication(true);
		walker.walk(validator, tree); // initiate walk of tree with listener
		parser.addParseListener(validator);
		return parser;
	}

	private void checkValidSyntaxBadContent(String sEnv, String sFailedPortion, int iPosInEnv,
			int iNumSyntaxErrors, int iNumGraphemeErrors, int iNumClassErrors, int IPosInFailed) {
		// TODO: check position and message
		EnvironmentParser parser = parseAStringWithContentError(sEnv);
		assertEquals(iNumSyntaxErrors, parser.getNumberOfSyntaxErrors());
		CheckGraphemeAndClassListener listener = (CheckGraphemeAndClassListener) parser
				.getParseListeners().get(0);
		assertNotNull(listener);
		List<GraphemeErrorInfo> badGraphemes = listener.getBadGraphemes();
		int iBadGraphemes = badGraphemes.size();
		assertEquals(iNumGraphemeErrors, iBadGraphemes);
		if (iBadGraphemes > 0) {
			String sMsg = badGraphemes.stream().map(GraphemeErrorInfo::getGrapheme)
					.collect(Collectors.joining(", "));
			assertEquals(sFailedPortion, sMsg);
			int index = sEnv.indexOf(sFailedPortion);
			assertEquals(iPosInEnv, index + 1);
			Optional<GraphemeErrorInfo> info = badGraphemes.stream().collect(
					Collectors.maxBy(Comparator.comparing(GraphemeErrorInfo::getMaxDepth)));
			int iMax = (info.isPresent() ? info.get().getMaxDepth() : -1);
			assertEquals(IPosInFailed, iMax);
		}
		List<String> badClasses = listener.getBadClasses();
		int iBadClasses = badClasses.size();
		assertEquals(iNumClassErrors, iBadClasses);
		if (iBadClasses > 0) {
			String sMsg = badClasses.stream().collect(Collectors.joining(", "));
			assertEquals(sFailedPortion, sMsg);
		}
	}

}
