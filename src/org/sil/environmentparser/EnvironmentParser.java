// Generated from Environment.g4 by ANTLR 4.7

	package org.sil.environmentparser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class EnvironmentParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, WS=8, ID=9;
	public static final int
		RULE_environment = 0, RULE_leftContext = 1, RULE_rightContext = 2, RULE_termSequence = 3, 
		RULE_term = 4, RULE_optionalSegment = 5, RULE_segment = 6, RULE_orthClass = 7, 
		RULE_literal = 8;
	public static final String[] ruleNames = {
		"environment", "leftContext", "rightContext", "termSequence", "term", 
		"optionalSegment", "segment", "orthClass", "literal"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'/'", "'_'", "'#'", "'('", "')'", "'['", "']'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, "WS", "ID"
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
	public String getGrammarFileName() { return "Environment.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public EnvironmentParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class EnvironmentContext extends ParserRuleContext {
		public RightContextContext rightContext() {
			return getRuleContext(RightContextContext.class,0);
		}
		public TerminalNode EOF() { return getToken(EnvironmentParser.EOF, 0); }
		public LeftContextContext leftContext() {
			return getRuleContext(LeftContextContext.class,0);
		}
		public EnvironmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_environment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EnvironmentListener ) ((EnvironmentListener)listener).enterEnvironment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EnvironmentListener ) ((EnvironmentListener)listener).exitEnvironment(this);
		}
	}

	public final EnvironmentContext environment() throws RecognitionException {
		EnvironmentContext _localctx = new EnvironmentContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_environment);
		try {
			setState(78);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(18);
				match(T__0);
				setState(19);
				match(T__1);
				setState(20);
				rightContext();
				setState(21);
				match(EOF);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(23);
				match(T__0);
				setState(24);
				leftContext();
				setState(25);
				match(T__1);
				setState(26);
				match(EOF);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(28);
				match(T__0);
				setState(29);
				leftContext();
				setState(30);
				match(T__1);
				setState(31);
				rightContext();
				setState(32);
				match(EOF);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(34);
				match(T__0);
				setState(35);
				match(T__0);
				notifyErrorListeners("tooManySlashes");
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(37);
				match(T__0);
				setState(38);
				match(T__1);
				setState(39);
				match(T__1);
				setState(40);
				rightContext();
				setState(41);
				match(EOF);
				notifyErrorListeners("tooManyUnderscores");
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(44);
				match(T__0);
				setState(45);
				match(T__1);
				setState(46);
				rightContext();
				setState(47);
				match(T__1);
				setState(48);
				match(EOF);
				notifyErrorListeners("tooManyUnderscores");
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(51);
				match(T__0);
				setState(52);
				leftContext();
				setState(53);
				match(T__1);
				setState(54);
				match(T__1);
				setState(55);
				match(EOF);
				notifyErrorListeners("tooManyUnderscores");
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(58);
				match(T__0);
				setState(59);
				leftContext();
				setState(60);
				match(T__1);
				setState(61);
				match(T__1);
				setState(62);
				rightContext();
				setState(63);
				match(EOF);
				notifyErrorListeners("tooManyUnderscores");
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(66);
				match(T__0);
				setState(67);
				leftContext();
				setState(68);
				match(T__1);
				setState(69);
				rightContext();
				setState(70);
				match(T__1);
				setState(71);
				match(EOF);
				notifyErrorListeners("tooManyUnderscores");
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(74);
				match(T__0);
				setState(75);
				match(T__1);
				setState(76);
				match(EOF);
				notifyErrorListeners("missingClassOrGrapheme");
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

	public static class LeftContextContext extends ParserRuleContext {
		public TermSequenceContext termSequence() {
			return getRuleContext(TermSequenceContext.class,0);
		}
		public LeftContextContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_leftContext; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EnvironmentListener ) ((EnvironmentListener)listener).enterLeftContext(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EnvironmentListener ) ((EnvironmentListener)listener).exitLeftContext(this);
		}
	}

	public final LeftContextContext leftContext() throws RecognitionException {
		LeftContextContext _localctx = new LeftContextContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_leftContext);
		try {
			setState(96);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(80);
				match(T__2);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(81);
				termSequence();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(82);
				match(T__2);
				setState(83);
				termSequence();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(84);
				termSequence();
				setState(85);
				match(T__2);
				notifyErrorListeners("contentBeforeWordInitialBoundary");
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(88);
				match(T__2);
				setState(89);
				match(T__2);
				notifyErrorListeners("tooManyWordInitialBoundaries");
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(91);
				match(T__2);
				setState(92);
				match(T__2);
				setState(93);
				termSequence();
				notifyErrorListeners("tooManyWordInitialBoundaries");
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

	public static class RightContextContext extends ParserRuleContext {
		public TermSequenceContext termSequence() {
			return getRuleContext(TermSequenceContext.class,0);
		}
		public RightContextContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rightContext; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EnvironmentListener ) ((EnvironmentListener)listener).enterRightContext(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EnvironmentListener ) ((EnvironmentListener)listener).exitRightContext(this);
		}
	}

	public final RightContextContext rightContext() throws RecognitionException {
		RightContextContext _localctx = new RightContextContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_rightContext);
		try {
			setState(115);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(98);
				match(T__2);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(99);
				termSequence();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(100);
				termSequence();
				setState(101);
				match(T__2);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(103);
				match(T__2);
				setState(104);
				termSequence();
				notifyErrorListeners("contentAfterWordFinalBoundary");
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(107);
				termSequence();
				setState(108);
				match(T__2);
				setState(109);
				match(T__2);
				notifyErrorListeners("tooManyWordFinalBoundaries");
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(112);
				match(T__2);
				setState(113);
				match(T__2);
				notifyErrorListeners("tooManyWordFinalBoundaries");
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

	public static class TermSequenceContext extends ParserRuleContext {
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public TermSequenceContext termSequence() {
			return getRuleContext(TermSequenceContext.class,0);
		}
		public TermSequenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_termSequence; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EnvironmentListener ) ((EnvironmentListener)listener).enterTermSequence(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EnvironmentListener ) ((EnvironmentListener)listener).exitTermSequence(this);
		}
	}

	public final TermSequenceContext termSequence() throws RecognitionException {
		TermSequenceContext _localctx = new TermSequenceContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_termSequence);
		try {
			setState(121);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(117);
				term();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(118);
				term();
				setState(119);
				termSequence();
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
			if ( listener instanceof EnvironmentListener ) ((EnvironmentListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EnvironmentListener ) ((EnvironmentListener)listener).exitTerm(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_term);
		try {
			setState(125);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(123);
				optionalSegment();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(124);
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
			if ( listener instanceof EnvironmentListener ) ((EnvironmentListener)listener).enterOptionalSegment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EnvironmentListener ) ((EnvironmentListener)listener).exitOptionalSegment(this);
		}
	}

	public final OptionalSegmentContext optionalSegment() throws RecognitionException {
		OptionalSegmentContext _localctx = new OptionalSegmentContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_optionalSegment);
		try {
			setState(144);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(127);
				match(T__3);
				setState(128);
				segment();
				setState(129);
				match(T__4);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(131);
				match(T__3);
				notifyErrorListeners("missingClassOrGrapheme");
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(133);
				match(T__3);
				setState(134);
				match(T__4);
				notifyErrorListeners("missingClassOrGrapheme");
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(136);
				match(T__3);
				setState(137);
				segment();
				notifyErrorListeners("missingClosingParen");
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(140);
				segment();
				setState(141);
				match(T__4);
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
		public OrthClassContext orthClass() {
			return getRuleContext(OrthClassContext.class,0);
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
			if ( listener instanceof EnvironmentListener ) ((EnvironmentListener)listener).enterSegment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EnvironmentListener ) ((EnvironmentListener)listener).exitSegment(this);
		}
	}

	public final SegmentContext segment() throws RecognitionException {
		SegmentContext _localctx = new SegmentContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_segment);
		try {
			setState(148);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(146);
				orthClass();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(147);
				literal();
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

	public static class OrthClassContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(EnvironmentParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(EnvironmentParser.ID, i);
		}
		public OrthClassContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orthClass; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EnvironmentListener ) ((EnvironmentListener)listener).enterOrthClass(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EnvironmentListener ) ((EnvironmentListener)listener).exitOrthClass(this);
		}
	}

	public final OrthClassContext orthClass() throws RecognitionException {
		OrthClassContext _localctx = new OrthClassContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_orthClass);
		int _la;
		try {
			setState(170);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(150);
				match(T__5);
				setState(152); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(151);
					match(ID);
					}
					}
					setState(154); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==ID );
				setState(156);
				match(T__6);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(157);
				match(T__5);
				setState(158);
				match(ID);
				notifyErrorListeners("missingClosingSquareBracket");
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(160);
				match(ID);
				setState(161);
				match(T__6);
				notifyErrorListeners("missingOpeningSquareBracket");
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(163);
				match(T__5);
				notifyErrorListeners("missingClassAfterOpeningSquareBracket");
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(165);
				match(T__5);
				setState(166);
				match(T__6);
				notifyErrorListeners("missingClassAfterOpeningSquareBracket");
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(168);
				match(T__6);
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
		public TerminalNode ID() { return getToken(EnvironmentParser.ID, 0); }
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EnvironmentListener ) ((EnvironmentListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EnvironmentListener ) ((EnvironmentListener)listener).exitLiteral(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(172);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\13\u00b1\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\5\2Q\n\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3c\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4v\n\4\3\5\3\5\3\5\3\5\5\5|\n\5"+
		"\3\6\3\6\5\6\u0080\n\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\5\7\u0093\n\7\3\b\3\b\5\b\u0097\n\b\3\t\3\t\6\t\u009b"+
		"\n\t\r\t\16\t\u009c\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\5\t\u00ad\n\t\3\n\3\n\3\n\2\2\13\2\4\6\b\n\f\16\20\22\2\2\2\u00c7"+
		"\2P\3\2\2\2\4b\3\2\2\2\6u\3\2\2\2\b{\3\2\2\2\n\177\3\2\2\2\f\u0092\3\2"+
		"\2\2\16\u0096\3\2\2\2\20\u00ac\3\2\2\2\22\u00ae\3\2\2\2\24\25\7\3\2\2"+
		"\25\26\7\4\2\2\26\27\5\6\4\2\27\30\7\2\2\3\30Q\3\2\2\2\31\32\7\3\2\2\32"+
		"\33\5\4\3\2\33\34\7\4\2\2\34\35\7\2\2\3\35Q\3\2\2\2\36\37\7\3\2\2\37 "+
		"\5\4\3\2 !\7\4\2\2!\"\5\6\4\2\"#\7\2\2\3#Q\3\2\2\2$%\7\3\2\2%&\7\3\2\2"+
		"&Q\b\2\1\2\'(\7\3\2\2()\7\4\2\2)*\7\4\2\2*+\5\6\4\2+,\7\2\2\3,-\b\2\1"+
		"\2-Q\3\2\2\2./\7\3\2\2/\60\7\4\2\2\60\61\5\6\4\2\61\62\7\4\2\2\62\63\7"+
		"\2\2\3\63\64\b\2\1\2\64Q\3\2\2\2\65\66\7\3\2\2\66\67\5\4\3\2\678\7\4\2"+
		"\289\7\4\2\29:\7\2\2\3:;\b\2\1\2;Q\3\2\2\2<=\7\3\2\2=>\5\4\3\2>?\7\4\2"+
		"\2?@\7\4\2\2@A\5\6\4\2AB\7\2\2\3BC\b\2\1\2CQ\3\2\2\2DE\7\3\2\2EF\5\4\3"+
		"\2FG\7\4\2\2GH\5\6\4\2HI\7\4\2\2IJ\7\2\2\3JK\b\2\1\2KQ\3\2\2\2LM\7\3\2"+
		"\2MN\7\4\2\2NO\7\2\2\3OQ\b\2\1\2P\24\3\2\2\2P\31\3\2\2\2P\36\3\2\2\2P"+
		"$\3\2\2\2P\'\3\2\2\2P.\3\2\2\2P\65\3\2\2\2P<\3\2\2\2PD\3\2\2\2PL\3\2\2"+
		"\2Q\3\3\2\2\2Rc\7\5\2\2Sc\5\b\5\2TU\7\5\2\2Uc\5\b\5\2VW\5\b\5\2WX\7\5"+
		"\2\2XY\b\3\1\2Yc\3\2\2\2Z[\7\5\2\2[\\\7\5\2\2\\c\b\3\1\2]^\7\5\2\2^_\7"+
		"\5\2\2_`\5\b\5\2`a\b\3\1\2ac\3\2\2\2bR\3\2\2\2bS\3\2\2\2bT\3\2\2\2bV\3"+
		"\2\2\2bZ\3\2\2\2b]\3\2\2\2c\5\3\2\2\2dv\7\5\2\2ev\5\b\5\2fg\5\b\5\2gh"+
		"\7\5\2\2hv\3\2\2\2ij\7\5\2\2jk\5\b\5\2kl\b\4\1\2lv\3\2\2\2mn\5\b\5\2n"+
		"o\7\5\2\2op\7\5\2\2pq\b\4\1\2qv\3\2\2\2rs\7\5\2\2st\7\5\2\2tv\b\4\1\2"+
		"ud\3\2\2\2ue\3\2\2\2uf\3\2\2\2ui\3\2\2\2um\3\2\2\2ur\3\2\2\2v\7\3\2\2"+
		"\2w|\5\n\6\2xy\5\n\6\2yz\5\b\5\2z|\3\2\2\2{w\3\2\2\2{x\3\2\2\2|\t\3\2"+
		"\2\2}\u0080\5\f\7\2~\u0080\5\16\b\2\177}\3\2\2\2\177~\3\2\2\2\u0080\13"+
		"\3\2\2\2\u0081\u0082\7\6\2\2\u0082\u0083\5\16\b\2\u0083\u0084\7\7\2\2"+
		"\u0084\u0093\3\2\2\2\u0085\u0086\7\6\2\2\u0086\u0093\b\7\1\2\u0087\u0088"+
		"\7\6\2\2\u0088\u0089\7\7\2\2\u0089\u0093\b\7\1\2\u008a\u008b\7\6\2\2\u008b"+
		"\u008c\5\16\b\2\u008c\u008d\b\7\1\2\u008d\u0093\3\2\2\2\u008e\u008f\5"+
		"\16\b\2\u008f\u0090\7\7\2\2\u0090\u0091\b\7\1\2\u0091\u0093\3\2\2\2\u0092"+
		"\u0081\3\2\2\2\u0092\u0085\3\2\2\2\u0092\u0087\3\2\2\2\u0092\u008a\3\2"+
		"\2\2\u0092\u008e\3\2\2\2\u0093\r\3\2\2\2\u0094\u0097\5\20\t\2\u0095\u0097"+
		"\5\22\n\2\u0096\u0094\3\2\2\2\u0096\u0095\3\2\2\2\u0097\17\3\2\2\2\u0098"+
		"\u009a\7\b\2\2\u0099\u009b\7\13\2\2\u009a\u0099\3\2\2\2\u009b\u009c\3"+
		"\2\2\2\u009c\u009a\3\2\2\2\u009c\u009d\3\2\2\2\u009d\u009e\3\2\2\2\u009e"+
		"\u00ad\7\t\2\2\u009f\u00a0\7\b\2\2\u00a0\u00a1\7\13\2\2\u00a1\u00ad\b"+
		"\t\1\2\u00a2\u00a3\7\13\2\2\u00a3\u00a4\7\t\2\2\u00a4\u00ad\b\t\1\2\u00a5"+
		"\u00a6\7\b\2\2\u00a6\u00ad\b\t\1\2\u00a7\u00a8\7\b\2\2\u00a8\u00a9\7\t"+
		"\2\2\u00a9\u00ad\b\t\1\2\u00aa\u00ab\7\t\2\2\u00ab\u00ad\b\t\1\2\u00ac"+
		"\u0098\3\2\2\2\u00ac\u009f\3\2\2\2\u00ac\u00a2\3\2\2\2\u00ac\u00a5\3\2"+
		"\2\2\u00ac\u00a7\3\2\2\2\u00ac\u00aa\3\2\2\2\u00ad\21\3\2\2\2\u00ae\u00af"+
		"\7\13\2\2\u00af\23\3\2\2\2\13Pbu{\177\u0092\u0096\u009c\u00ac";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}