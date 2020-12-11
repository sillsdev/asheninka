// Generated from TemplateFilter.g4 by ANTLR 4.7

	package org.sil.antlr4.templatefilterparser.antlr4generated;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TemplateFilterParser}.
 */
public interface TemplateFilterListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TemplateFilterParser#description}.
	 * @param ctx the parse tree
	 */
	void enterDescription(TemplateFilterParser.DescriptionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateFilterParser#description}.
	 * @param ctx the parse tree
	 */
	void exitDescription(TemplateFilterParser.DescriptionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateFilterParser#termSequence}.
	 * @param ctx the parse tree
	 */
	void enterTermSequence(TemplateFilterParser.TermSequenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateFilterParser#termSequence}.
	 * @param ctx the parse tree
	 */
	void exitTermSequence(TemplateFilterParser.TermSequenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateFilterParser#slotPositionTerm}.
	 * @param ctx the parse tree
	 */
	void enterSlotPositionTerm(TemplateFilterParser.SlotPositionTermContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateFilterParser#slotPositionTerm}.
	 * @param ctx the parse tree
	 */
	void exitSlotPositionTerm(TemplateFilterParser.SlotPositionTermContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateFilterParser#constituentBeginMarkerTerm}.
	 * @param ctx the parse tree
	 */
	void enterConstituentBeginMarkerTerm(TemplateFilterParser.ConstituentBeginMarkerTermContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateFilterParser#constituentBeginMarkerTerm}.
	 * @param ctx the parse tree
	 */
	void exitConstituentBeginMarkerTerm(TemplateFilterParser.ConstituentBeginMarkerTermContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateFilterParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(TemplateFilterParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateFilterParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(TemplateFilterParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateFilterParser#optionalSegment}.
	 * @param ctx the parse tree
	 */
	void enterOptionalSegment(TemplateFilterParser.OptionalSegmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateFilterParser#optionalSegment}.
	 * @param ctx the parse tree
	 */
	void exitOptionalSegment(TemplateFilterParser.OptionalSegmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateFilterParser#segment}.
	 * @param ctx the parse tree
	 */
	void enterSegment(TemplateFilterParser.SegmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateFilterParser#segment}.
	 * @param ctx the parse tree
	 */
	void exitSegment(TemplateFilterParser.SegmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateFilterParser#ssp}.
	 * @param ctx the parse tree
	 */
	void enterSsp(TemplateFilterParser.SspContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateFilterParser#ssp}.
	 * @param ctx the parse tree
	 */
	void exitSsp(TemplateFilterParser.SspContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateFilterParser#natClass}.
	 * @param ctx the parse tree
	 */
	void enterNatClass(TemplateFilterParser.NatClassContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateFilterParser#natClass}.
	 * @param ctx the parse tree
	 */
	void exitNatClass(TemplateFilterParser.NatClassContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateFilterParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(TemplateFilterParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateFilterParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(TemplateFilterParser.LiteralContext ctx);
}