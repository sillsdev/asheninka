// Copyright (c) 2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package org.sil.antlr4.templatefilterparser;

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
import org.sil.antlr4.templatefilterparser.CheckSegmentAndClassListener;
import org.sil.antlr4.templatefilterparser.SegmentErrorInfo;
import org.sil.antlr4.templatefilterparser.TemplateFilterConstants;
import org.sil.antlr4.templatefilterparser.TemplateFilterErrorInfo;
import org.sil.antlr4.templatefilterparser.TemplateFilterErrorListener;
import org.sil.antlr4.templatefilterparser.TemplateFilterErrorListener.VerboseListener;
import org.sil.antlr4.templatefilterparser.antlr4generated.TemplateFilterLexer;
import org.sil.antlr4.templatefilterparser.antlr4generated.TemplateFilterParser;

public class TemplateFilterRecognizerTest {

	List<String> segmentsMasterList = Arrays.asList("a", "ai", "b", "c", "d", "e", "f", "fl",
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
	public void validTemplateFiltersTest() {
		checkValidTemplateFilter("a"); // segment
		checkValidTemplateFilter("b");
		checkValidTemplateFilter("*a"); // -ssp segment
		checkValidTemplateFilter("*b");
		checkValidTemplateFilter("* a");
		checkValidTemplateFilter("* b");
		checkValidTemplateFilter("a b");
		checkValidTemplateFilter("a b ai d");
		checkValidTemplateFilter("a b a b");
		checkValidTemplateFilter("*a b");
		checkValidTemplateFilter("a *b ai *d");
		checkValidTemplateFilter("a b *a *b");
		checkValidTemplateFilter("ab"); // segments without intervening spaces
		checkValidTemplateFilter("ab ab");
		checkValidTemplateFilter("abab");
		checkValidTemplateFilter("flaid");
		checkValidTemplateFilter("flaidflaid");
		checkValidTemplateFilter("a*b");
		checkValidTemplateFilter("ab* a*b");
		checkValidTemplateFilter("ab*a*b");
		checkValidTemplateFilter("fl*aid");
		checkValidTemplateFilter("f*lai*dfl*aid");
		checkValidTemplateFilter("(a) b"); // optionality
		checkValidTemplateFilter("(a)b");
		checkValidTemplateFilter("(c) d (a) b");
		checkValidTemplateFilter("(*a) *b");
		checkValidTemplateFilter("(a)*b");
		checkValidTemplateFilter("(c) *d (*a) b");
		checkValidTemplateFilter("(f) f*l");
		checkValidTemplateFilter("d (*c)");
		checkValidTemplateFilter("(f) (*fl) d (*c)");
		checkValidTemplateFilter("[V]"); // Natural Classes
		checkValidTemplateFilter("[V] [V]");
		checkValidTemplateFilter("*[V]");
		checkValidTemplateFilter("*[V] [V]");
		checkValidTemplateFilter("[+lab, +vd]"); // Natural Classes with
											  	 // spaces in the name
		checkValidTemplateFilter("[+lab, +vd] [+lab, +vd]");
		checkValidTemplateFilter("[+ant, -cor, -vd]");
		checkValidTemplateFilter("[+ant, -cor, -vd][+ant, -cor, -vd]");
		checkValidTemplateFilter("*[+lab, +vd] * [+lab, +vd]");
		checkValidTemplateFilter("*[+ant, -cor, -vd]");
		checkValidTemplateFilter("*[+ant, -cor, -vd]*[+ant, -cor, -vd]");
		checkValidTemplateFilter("([C]) [V]"); // Natural Classes with
											   // optionality
		checkValidTemplateFilter("([C])[V]");
		checkValidTemplateFilter("[V] ([C])([C]) [V]");
		checkValidTemplateFilter("([V]) b fr [C] a (c)"); // Combo
		checkValidTemplateFilter("(*[C])*[V]");
		checkValidTemplateFilter("[V] (*[C])([C]) *[V]");
		checkValidTemplateFilter("([V]) b *fr [C] a (*c)"); // Combo
		checkValidTemplateFilter("\u00ED"); // single combined Unicode acute i
												// (í)
		checkValidTemplateFilter("*\u00ED");
		checkValidTemplateFilter("i\u0301"); // Unicode i followed by combining
												// acute (í)
		checkValidTemplateFilter("H");
		checkValidTemplateFilter("a|b");
		checkValidTemplateFilter("a [C] | [V]");
	}

	private void checkValidTemplateFilter(String sDesc) {
		assertEquals(0, parseAString(sDesc));
	}

	private int parseAString(String sInput) {
		CharStream input = CharStreams.fromString(sInput);
		TemplateFilterLexer lexer = new TemplateFilterLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		TemplateFilterParser parser = new TemplateFilterParser(tokens);
		// begin parsing at rule 'description'
		ParseTree tree = parser.description();
		int numErrors = parser.getNumberOfSyntaxErrors();
		ParseTreeWalker walker = new ParseTreeWalker(); // create standard
														// walker
		CheckSegmentAndClassListener validator = new CheckSegmentAndClassListener(parser,
				segmentsMasterList, classesMasterList);
		walker.walk(validator, tree); // initiate walk of tree with listener
		assertEquals(true, validator.isObligatorySegmentFound());
		return numErrors;
	}

	private TemplateFilterParser parseAStringExpectFailure(String sInput) {
		CharStream input = CharStreams.fromString(sInput);
		TemplateFilterLexer lexer = new TemplateFilterLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		TemplateFilterParser parser = new TemplateFilterParser(tokens);
		parser.removeErrorListeners();
		// to test for ambiguity: parser.addErrorListener(new
		// DiagnosticErrorListener());
		// parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
		VerboseListener errListener = new TemplateFilterErrorListener.VerboseListener();
		errListener.clearErrorMessageList();
		parser.addErrorListener(errListener);
		// begin parsing at rule 'description'
		ParseTree tree = parser.description();
		//int iNumErrors = parser.getNumberOfSyntaxErrors();
//		List<TemplateFilterErrorInfo> errors = errListener.getErrorMessages();
//		System.out.println(errors.size());
//		String sTree = tree.toStringTree(parser);
//		System.out.println("tree=" + sTree);
		return parser;
	}

	@Test
	public void invalidTemplateFiltersTest() {
		// missing segment or class
		checkInvalidTemplateFilter("", TemplateFilterConstants.MISSING_CLASS_OR_SEGMENT, 0, 1);
		checkInvalidTemplateFilter("*", TemplateFilterConstants.MISSING_CLASS_OR_SEGMENT, 1, 1);
		checkInvalidTemplateFilter("(", TemplateFilterConstants.MISSING_CLASS_OR_SEGMENT, 1, 1);
		checkInvalidTemplateFilter("()", TemplateFilterConstants.MISSING_CLASS_OR_SEGMENT, 2, 1);
		checkInvalidTemplateFilter("(*", TemplateFilterConstants.MISSING_CLOSING_PAREN, 2, 2);
		checkInvalidTemplateFilter("(*)", TemplateFilterConstants.MISSING_CLASS_OR_SEGMENT, 2, 1);
		// missing closing paren
		checkInvalidTemplateFilter("(a", TemplateFilterConstants.MISSING_CLOSING_PAREN, 2, 1);
		checkInvalidTemplateFilter("(*a", TemplateFilterConstants.MISSING_CLOSING_PAREN, 3, 1);
		// missing opening paren
		checkInvalidTemplateFilter("a)", TemplateFilterConstants.MISSING_OPENING_PAREN, 2, 1);
		checkInvalidTemplateFilter("*a)", TemplateFilterConstants.MISSING_OPENING_PAREN, 3, 1);
		// missing class
		checkInvalidTemplateFilter("[", TemplateFilterConstants.MISSING_CLASS_AFTER_OPENING_SQUARE_BRACKET, 1, 1);
		// missing class
		checkInvalidTemplateFilter("[]", TemplateFilterConstants.MISSING_CLASS_AFTER_OPENING_SQUARE_BRACKET, 2, 1);
		// missing closing bracket
		checkInvalidTemplateFilter("[C", TemplateFilterConstants.MISSING_CLOSING_SQUARE_BRACKET, 2, 1);
		checkInvalidTemplateFilter("*[C", TemplateFilterConstants.MISSING_CLOSING_SQUARE_BRACKET, 3, 1);
		// missing opening bracket
		checkInvalidTemplateFilter(" ]", TemplateFilterConstants.MISSING_OPENING_SQUARE_BRACKET, 2, 1);
		// missing opening bracket
		checkInvalidTemplateFilter(" C]", TemplateFilterConstants.MISSING_OPENING_SQUARE_BRACKET, 3, 1);
		// missing closing bracket before closing paren
		checkInvalidTemplateFilter("([C)", TemplateFilterConstants.MISSING_CLOSING_SQUARE_BRACKET, 3, 1);
		checkInvalidTemplateFilter("(*[C)", TemplateFilterConstants.MISSING_CLOSING_SQUARE_BRACKET, 4, 1);
		// missing closing paren after closing bracket
		checkInvalidTemplateFilter("([C]", TemplateFilterConstants.MISSING_CLOSING_PAREN, 4, 1);
		checkInvalidTemplateFilter("(*[C]", TemplateFilterConstants.MISSING_CLOSING_PAREN, 5, 1);
		// missing opening bracket and missing closing paren
		checkInvalidTemplateFilter("(C]", TemplateFilterConstants.MISSING_CLOSING_PAREN, 3, 2);
		// missing closing bracket and closing paren
		checkInvalidTemplateFilter("([C", TemplateFilterConstants.MISSING_CLOSING_PAREN, 3, 2);
		checkInvalidTemplateFilter("(*[C", TemplateFilterConstants.MISSING_CLOSING_PAREN, 4, 2);
		// asterisk in wrong position
		checkInvalidTemplateFilter("a*", TemplateFilterConstants.MISSING_CLASS_OR_SEGMENT, 2, 1);
		checkInvalidTemplateFilter("(a*)", TemplateFilterConstants.MISSING_OPENING_PAREN, 4, 3);
		checkInvalidTemplateFilter("(a)*", TemplateFilterConstants.MISSING_CLASS_OR_SEGMENT, 4, 1);
		checkInvalidTemplateFilter("[*C]", TemplateFilterConstants.MISSING_OPENING_SQUARE_BRACKET, 4, 2);
		checkInvalidTemplateFilter("[C*]", TemplateFilterConstants.MISSING_OPENING_SQUARE_BRACKET, 4, 2);
		checkInvalidTemplateFilter("[C]*", TemplateFilterConstants.MISSING_CLASS_OR_SEGMENT, 4, 1);
	}

	private void checkInvalidTemplateFilter(String sDesc, String sFailedPortion, int iPos,
			int iNumErrors) {
		// TODO: check position and message
		TemplateFilterParser parser = parseAStringExpectFailure(sDesc);
		assertEquals(iNumErrors, parser.getNumberOfSyntaxErrors());
		VerboseListener errListener = (VerboseListener) parser.getErrorListeners().get(0);
		assertNotNull(errListener);
		int i = errListener.getErrorMessages().size();
		TemplateFilterErrorInfo info = errListener.getErrorMessages().get(i - 1);
		assertNotNull(info);
		assertEquals(sFailedPortion, info.getMsg());
		assertEquals(iPos, info.getCharPositionInLine());
	}

	@Test
	public void validSyntaxBadContentTemplateFilterTest() {
		// chr not in segment list
		checkValidSyntaxBadContent("chr ", "chr", 1, 0, 1, 0, 1, 0);
		// g is not in segment list
		checkValidSyntaxBadContent("frage", "frage", 1, 0, 1, 0, 3, 0);
		// +lab not in class list
		checkValidSyntaxBadContent(" [+lab]", "+lab", 3, 0, 0, 1, 1, 0);
		// +lab, -vd not in class list
		checkValidSyntaxBadContent("[+lab, -vd]", "+lab, -vd", 3, 0, 0, 1, 1, 0);
		// +lab, -vd -cor not in class list
		checkValidSyntaxBadContent(" [+lab, +vd, -cor] ", "+lab, +vd, -cor", 3, 0, 0, 1, 1, 0);
		// X not in class list
		checkValidSyntaxBadContent("[X][Y]", "X, Y", 5, 0, 0, 2, 1, 0);
		// too many slot position indicators
		checkValidSyntaxBadContent("a | b | f", "f", 7, 0, 0, 0, 1, 2);
	}

	private TemplateFilterParser parseAStringWithContentError(String sInput) {
		CharStream input = CharStreams.fromString(sInput);
		TemplateFilterLexer lexer = new TemplateFilterLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		TemplateFilterParser parser = new TemplateFilterParser(tokens);
		// begin parsing at rule 'description'
		ParseTree tree = parser.description();
		ParseTreeWalker walker = new ParseTreeWalker(); // create standard
														// walker
		CheckSegmentAndClassListener validator = new CheckSegmentAndClassListener(parser,
				segmentsMasterList, classesMasterList);
		walker.walk(validator, tree); // initiate walk of tree with listener
		parser.addParseListener(validator);
		return parser;
	}

	private void checkValidSyntaxBadContent(String sDesc, String sFailedPortion, int iPosInDesc,
			int iNumSyntaxErrors, int iNumSegmentErrors, int iNumClassErrors, int iPosInFailed, int iSlotPositionsFound) {
		// TODO: check position and message
		TemplateFilterParser parser = parseAStringWithContentError(sDesc);
		assertEquals(iNumSyntaxErrors, parser.getNumberOfSyntaxErrors());
		CheckSegmentAndClassListener listener = (CheckSegmentAndClassListener) parser
				.getParseListeners().get(0);
		assertNotNull(listener);
		List<SegmentErrorInfo> badSegments = listener.getBadSegments();
		int iBadGraphemes = badSegments.size();
		assertEquals(iNumSegmentErrors, iBadGraphemes);
		if (iBadGraphemes > 0) {
			String sMsg = badSegments.stream().map(SegmentErrorInfo::getSegment)
					.collect(Collectors.joining(", "));
			assertEquals(sFailedPortion, sMsg);
			int index = sDesc.indexOf(sFailedPortion);
			assertEquals(iPosInDesc, index + 1);
			Optional<SegmentErrorInfo> info = badSegments.stream().collect(
					Collectors.maxBy(Comparator.comparing(SegmentErrorInfo::getMaxDepth)));
			int iMax = (info.isPresent() ? info.get().getMaxDepth() : -1);
			assertEquals(iPosInFailed, iMax);
		}
		List<String> badClasses = listener.getBadClasses();
		int iBadClasses = badClasses.size();
		assertEquals(iNumClassErrors, iBadClasses);
		if (iBadClasses > 0) {
			String sMsg = badClasses.stream().collect(Collectors.joining(", "));
			assertEquals(sFailedPortion, sMsg);
		}
		assertEquals(iSlotPositionsFound, listener.getSlotPositionIndicatorsFound());
	}

	@Test
	public void validSyntaxButAllOptionalTemplateFilterTest() {
		checkValidSyntaxButAllOptional("(t) ");
		checkValidSyntaxButAllOptional("(fr) (a) (g) (e)");
		checkValidSyntaxButAllOptional("( [+lab])");
		checkValidSyntaxButAllOptional("([X]) (e) ([Y])");
	}

	private void checkValidSyntaxButAllOptional(String sInput) {
		CharStream input = CharStreams.fromString(sInput);
		TemplateFilterLexer lexer = new TemplateFilterLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		TemplateFilterParser parser = new TemplateFilterParser(tokens);
		// begin parsing at rule 'description'
		ParseTree tree = parser.description();
		int numErrors = parser.getNumberOfSyntaxErrors();
		ParseTreeWalker walker = new ParseTreeWalker(); // create standard
														// walker
		CheckSegmentAndClassListener validator = new CheckSegmentAndClassListener(parser,
				segmentsMasterList, classesMasterList);
		walker.walk(validator, tree); // initiate walk of tree with listener
		assertEquals(false, validator.isObligatorySegmentFound());
	}


}
