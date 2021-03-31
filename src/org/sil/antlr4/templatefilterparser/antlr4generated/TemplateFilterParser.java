// Generated from TemplateFilter.g4 by ANTLR 4.7

	package org.sil.antlr4.templatefilterparser.antlr4generated;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TemplateFilterParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, WS=6, SLOT_POSITION=7, CONSTITUENT_BEGIN_MARKER=8, 
		ID=9;
	public static final int
		RULE_description = 0, RULE_termSequence = 1, RULE_slotPositionTerm = 2, 
		RULE_constituentBeginMarkerTerm = 3, RULE_term = 4, RULE_optionalSegment = 5, 
		RULE_segment = 6, RULE_ssp = 7, RULE_natClass = 8, RULE_literal = 9;
	public static final String[] ruleNames = {
		"description", "termSequence", "slotPositionTerm", "constituentBeginMarkerTerm", 
		"term", "optionalSegment", "segment", "ssp", "natClass", "literal"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "')'", "'*'", "'['", "']'", null, "'|'", "'_'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, "WS", "SLOT_POSITION", "CONSTITUENT_BEGIN_MARKER", 
		"ID"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "TemplateFilter.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TemplateFilterParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class DescriptionContext extends ParserRuleContext {
		public TermSequenceContext termSequence() {
			return getRuleContext(TermSequenceContext.class,0);
		}
		public TerminalNode EOF() { return getToken(TemplateFilterParser.EOF, 0); }
		public DescriptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_description; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).enterDescription(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).exitDescription(this);
		}
	}

	public final DescriptionContext description() throws RecognitionException {
		DescriptionContext _localctx = new DescriptionContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_description);
		try {
			setState(23);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
			case T__2:
			case T__3:
			case T__4:
			case CONSTITUENT_BEGIN_MARKER:
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(20);
				termSequence(0);
				}
				break;
			case EOF:
				enterOuterAlt(_localctx, 2);
				{
				setState(21);
				match(EOF);
				notifyErrorListeners("missingClassOrSegment");
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TermSequenceContext extends ParserRuleContext {
		public ConstituentBeginMarkerTermContext constituentBeginMarkerTerm() {
			return getRuleContext(ConstituentBeginMarkerTermContext.class,0);
		}
		public List<TermSequenceContext> termSequence() {
			return getRuleContexts(TermSequenceContext.class);
		}
		public TermSequenceContext termSequence(int i) {
			return getRuleContext(TermSequenceContext.class,i);
		}
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public SlotPositionTermContext slotPositionTerm() {
			return getRuleContext(SlotPositionTermContext.class,0);
		}
		public TermSequenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_termSequence; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).enterTermSequence(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).exitTermSequence(this);
		}
	}

	public final TermSequenceContext termSequence() throws RecognitionException {
		return termSequence(0);
	}

	private TermSequenceContext termSequence(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TermSequenceContext _localctx = new TermSequenceContext(_ctx, _parentState);
		TermSequenceContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_termSequence, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(26);
				constituentBeginMarkerTerm();
				setState(27);
				termSequence(6);
				}
				break;
			case 2:
				{
				setState(29);
				constituentBeginMarkerTerm();
				notifyErrorListeners("missingClassOrSegment");
				}
				break;
			case 3:
				{
				setState(32);
				term();
				}
				break;
			case 4:
				{
				setState(33);
				term();
				setState(34);
				termSequence(1);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(56);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(54);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
					case 1:
						{
						_localctx = new TermSequenceContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_termSequence);
						setState(38);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(39);
						slotPositionTerm();
						setState(40);
						termSequence(9);
						}
						break;
					case 2:
						{
						_localctx = new TermSequenceContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_termSequence);
						setState(42);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(43);
						constituentBeginMarkerTerm();
						setState(44);
						termSequence(8);
						}
						break;
					case 3:
						{
						_localctx = new TermSequenceContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_termSequence);
						setState(46);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(47);
						slotPositionTerm();
						notifyErrorListeners("missingClassOrSegment");
						}
						break;
					case 4:
						{
						_localctx = new TermSequenceContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_termSequence);
						setState(50);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(51);
						constituentBeginMarkerTerm();
						notifyErrorListeners("missingClassOrSegment");
						}
						break;
					}
					} 
				}
				setState(58);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class SlotPositionTermContext extends ParserRuleContext {
		public TerminalNode SLOT_POSITION() { return getToken(TemplateFilterParser.SLOT_POSITION, 0); }
		public SlotPositionTermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_slotPositionTerm; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).enterSlotPositionTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).exitSlotPositionTerm(this);
		}
	}

	public final SlotPositionTermContext slotPositionTerm() throws RecognitionException {
		SlotPositionTermContext _localctx = new SlotPositionTermContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_slotPositionTerm);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			match(SLOT_POSITION);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstituentBeginMarkerTermContext extends ParserRuleContext {
		public TerminalNode CONSTITUENT_BEGIN_MARKER() { return getToken(TemplateFilterParser.CONSTITUENT_BEGIN_MARKER, 0); }
		public ConstituentBeginMarkerTermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constituentBeginMarkerTerm; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).enterConstituentBeginMarkerTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).exitConstituentBeginMarkerTerm(this);
		}
	}

	public final ConstituentBeginMarkerTermContext constituentBeginMarkerTerm() throws RecognitionException {
		ConstituentBeginMarkerTermContext _localctx = new ConstituentBeginMarkerTermContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_constituentBeginMarkerTerm);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(61);
			match(CONSTITUENT_BEGIN_MARKER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TermContext extends ParserRuleContext {
		public OptionalSegmentContext optionalSegment() {
			return getRuleContext(OptionalSegmentContext.class,0);
		}
		public SegmentContext segment() {
			return getRuleContext(SegmentContext.class,0);
		}
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).exitTerm(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_term);
		try {
			setState(65);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(63);
				optionalSegment();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(64);
				segment();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OptionalSegmentContext extends ParserRuleContext {
		public SegmentContext segment() {
			return getRuleContext(SegmentContext.class,0);
		}
		public OptionalSegmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optionalSegment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).enterOptionalSegment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).exitOptionalSegment(this);
		}
	}

	public final OptionalSegmentContext optionalSegment() throws RecognitionException {
		OptionalSegmentContext _localctx = new OptionalSegmentContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_optionalSegment);
		try {
			setState(84);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(67);
				match(T__0);
				setState(68);
				segment();
				setState(69);
				match(T__1);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(71);
				match(T__0);
				notifyErrorListeners("missingClassOrSegment");
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(73);
				match(T__0);
				setState(74);
				match(T__1);
				notifyErrorListeners("missingClassOrSegment");
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(76);
				match(T__0);
				setState(77);
				segment();
				notifyErrorListeners("missingClosingParen");
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(80);
				segment();
				setState(81);
				match(T__1);
				notifyErrorListeners("missingOpeningParen");
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SegmentContext extends ParserRuleContext {
		public SspContext ssp() {
			return getRuleContext(SspContext.class,0);
		}
		public NatClassContext natClass() {
			return getRuleContext(NatClassContext.class,0);
		}
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public SegmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_segment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).enterSegment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).exitSegment(this);
		}
	}

	public final SegmentContext segment() throws RecognitionException {
		SegmentContext _localctx = new SegmentContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_segment);
		try {
			setState(97);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(86);
				ssp();
				setState(87);
				natClass();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(89);
				natClass();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(90);
				ssp();
				setState(91);
				literal();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(93);
				literal();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(94);
				ssp();
				notifyErrorListeners("missingClassOrSegment");
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SspContext extends ParserRuleContext {
		public SspContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ssp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).enterSsp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).exitSsp(this);
		}
	}

	public final SspContext ssp() throws RecognitionException {
		SspContext _localctx = new SspContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_ssp);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NatClassContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(TemplateFilterParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(TemplateFilterParser.ID, i);
		}
		public NatClassContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_natClass; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).enterNatClass(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).exitNatClass(this);
		}
	}

	public final NatClassContext natClass() throws RecognitionException {
		NatClassContext _localctx = new NatClassContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_natClass);
		int _la;
		try {
			setState(121);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(101);
				match(T__3);
				setState(103); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(102);
					match(ID);
					}
					}
					setState(105); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==ID );
				setState(107);
				match(T__4);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(108);
				match(T__3);
				setState(109);
				match(ID);
				notifyErrorListeners("missingClosingSquareBracket");
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(111);
				match(ID);
				setState(112);
				match(T__4);
				notifyErrorListeners("missingOpeningSquareBracket");
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(114);
				match(T__3);
				notifyErrorListeners("missingClassAfterOpeningSquareBracket");
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(116);
				match(T__3);
				setState(117);
				match(T__4);
				notifyErrorListeners("missingClassAfterOpeningSquareBracket");
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(119);
				match(T__4);
				notifyErrorListeners("missingOpeningSquareBracket");
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LiteralContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(TemplateFilterParser.ID, 0); }
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateFilterListener ) ((TemplateFilterListener)listener).exitLiteral(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(123);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1:
			return termSequence_sempred((TermSequenceContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean termSequence_sempred(TermSequenceContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 8);
		case 1:
			return precpred(_ctx, 7);
		case 2:
			return precpred(_ctx, 5);
		case 3:
			return precpred(_ctx, 4);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\13\u0080\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\3\2\3\2\3\2\5\2\32\n\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\5\3\'\n\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\7\39\n\3\f\3\16\3<\13\3\3\4\3\4\3\5\3\5\3\6\3\6\5\6D\n\6\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7W\n"+
		"\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\bd\n\b\3\t\3\t\3\n\3"+
		"\n\6\nj\n\n\r\n\16\nk\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\5\n|\n\n\3\13\3\13\3\13\2\3\4\f\2\4\6\b\n\f\16\20\22\24\2\2\2"+
		"\u008c\2\31\3\2\2\2\4&\3\2\2\2\6=\3\2\2\2\b?\3\2\2\2\nC\3\2\2\2\fV\3\2"+
		"\2\2\16c\3\2\2\2\20e\3\2\2\2\22{\3\2\2\2\24}\3\2\2\2\26\32\5\4\3\2\27"+
		"\30\7\2\2\3\30\32\b\2\1\2\31\26\3\2\2\2\31\27\3\2\2\2\32\3\3\2\2\2\33"+
		"\34\b\3\1\2\34\35\5\b\5\2\35\36\5\4\3\b\36\'\3\2\2\2\37 \5\b\5\2 !\b\3"+
		"\1\2!\'\3\2\2\2\"\'\5\n\6\2#$\5\n\6\2$%\5\4\3\3%\'\3\2\2\2&\33\3\2\2\2"+
		"&\37\3\2\2\2&\"\3\2\2\2&#\3\2\2\2\':\3\2\2\2()\f\n\2\2)*\5\6\4\2*+\5\4"+
		"\3\13+9\3\2\2\2,-\f\t\2\2-.\5\b\5\2./\5\4\3\n/9\3\2\2\2\60\61\f\7\2\2"+
		"\61\62\5\6\4\2\62\63\b\3\1\2\639\3\2\2\2\64\65\f\6\2\2\65\66\5\b\5\2\66"+
		"\67\b\3\1\2\679\3\2\2\28(\3\2\2\28,\3\2\2\28\60\3\2\2\28\64\3\2\2\29<"+
		"\3\2\2\2:8\3\2\2\2:;\3\2\2\2;\5\3\2\2\2<:\3\2\2\2=>\7\t\2\2>\7\3\2\2\2"+
		"?@\7\n\2\2@\t\3\2\2\2AD\5\f\7\2BD\5\16\b\2CA\3\2\2\2CB\3\2\2\2D\13\3\2"+
		"\2\2EF\7\3\2\2FG\5\16\b\2GH\7\4\2\2HW\3\2\2\2IJ\7\3\2\2JW\b\7\1\2KL\7"+
		"\3\2\2LM\7\4\2\2MW\b\7\1\2NO\7\3\2\2OP\5\16\b\2PQ\b\7\1\2QW\3\2\2\2RS"+
		"\5\16\b\2ST\7\4\2\2TU\b\7\1\2UW\3\2\2\2VE\3\2\2\2VI\3\2\2\2VK\3\2\2\2"+
		"VN\3\2\2\2VR\3\2\2\2W\r\3\2\2\2XY\5\20\t\2YZ\5\22\n\2Zd\3\2\2\2[d\5\22"+
		"\n\2\\]\5\20\t\2]^\5\24\13\2^d\3\2\2\2_d\5\24\13\2`a\5\20\t\2ab\b\b\1"+
		"\2bd\3\2\2\2cX\3\2\2\2c[\3\2\2\2c\\\3\2\2\2c_\3\2\2\2c`\3\2\2\2d\17\3"+
		"\2\2\2ef\7\5\2\2f\21\3\2\2\2gi\7\6\2\2hj\7\13\2\2ih\3\2\2\2jk\3\2\2\2"+
		"ki\3\2\2\2kl\3\2\2\2lm\3\2\2\2m|\7\7\2\2no\7\6\2\2op\7\13\2\2p|\b\n\1"+
		"\2qr\7\13\2\2rs\7\7\2\2s|\b\n\1\2tu\7\6\2\2u|\b\n\1\2vw\7\6\2\2wx\7\7"+
		"\2\2x|\b\n\1\2yz\7\7\2\2z|\b\n\1\2{g\3\2\2\2{n\3\2\2\2{q\3\2\2\2{t\3\2"+
		"\2\2{v\3\2\2\2{y\3\2\2\2|\23\3\2\2\2}~\7\13\2\2~\25\3\2\2\2\13\31&8:C"+
		"Vck{";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}