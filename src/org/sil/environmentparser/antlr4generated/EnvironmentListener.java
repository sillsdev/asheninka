// Generated from Environment.g4 by ANTLR 4.7

	package org.sil.environmentparser.antlr4generated;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link EnvironmentParser}.
 */
public interface EnvironmentListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link EnvironmentParser#environment}.
	 * @param ctx the parse tree
	 */
	void enterEnvironment(EnvironmentParser.EnvironmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link EnvironmentParser#environment}.
	 * @param ctx the parse tree
	 */
	void exitEnvironment(EnvironmentParser.EnvironmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link EnvironmentParser#leftContext}.
	 * @param ctx the parse tree
	 */
	void enterLeftContext(EnvironmentParser.LeftContextContext ctx);
	/**
	 * Exit a parse tree produced by {@link EnvironmentParser#leftContext}.
	 * @param ctx the parse tree
	 */
	void exitLeftContext(EnvironmentParser.LeftContextContext ctx);
	/**
	 * Enter a parse tree produced by {@link EnvironmentParser#rightContext}.
	 * @param ctx the parse tree
	 */
	void enterRightContext(EnvironmentParser.RightContextContext ctx);
	/**
	 * Exit a parse tree produced by {@link EnvironmentParser#rightContext}.
	 * @param ctx the parse tree
	 */
	void exitRightContext(EnvironmentParser.RightContextContext ctx);
	/**
	 * Enter a parse tree produced by {@link EnvironmentParser#termSequence}.
	 * @param ctx the parse tree
	 */
	void enterTermSequence(EnvironmentParser.TermSequenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link EnvironmentParser#termSequence}.
	 * @param ctx the parse tree
	 */
	void exitTermSequence(EnvironmentParser.TermSequenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link EnvironmentParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(EnvironmentParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link EnvironmentParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(EnvironmentParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link EnvironmentParser#optionalSegment}.
	 * @param ctx the parse tree
	 */
	void enterOptionalSegment(EnvironmentParser.OptionalSegmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link EnvironmentParser#optionalSegment}.
	 * @param ctx the parse tree
	 */
	void exitOptionalSegment(EnvironmentParser.OptionalSegmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link EnvironmentParser#segment}.
	 * @param ctx the parse tree
	 */
	void enterSegment(EnvironmentParser.SegmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link EnvironmentParser#segment}.
	 * @param ctx the parse tree
	 */
	void exitSegment(EnvironmentParser.SegmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link EnvironmentParser#orthClass}.
	 * @param ctx the parse tree
	 */
	void enterOrthClass(EnvironmentParser.OrthClassContext ctx);
	/**
	 * Exit a parse tree produced by {@link EnvironmentParser#orthClass}.
	 * @param ctx the parse tree
	 */
	void exitOrthClass(EnvironmentParser.OrthClassContext ctx);
	/**
	 * Enter a parse tree produced by {@link EnvironmentParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(EnvironmentParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link EnvironmentParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(EnvironmentParser.LiteralContext ctx);
}