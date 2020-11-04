// --------------------------------------------------------------------------------------------
// Copyright (c) 2019-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
//
// File: TemplateFilter.g4
// Responsibility: 
// Last reviewed:
//
// <remarks>
// ANTLR v.4 grammar for parsing template/filter descriptions
// </remarks>
// --------------------------------------------------------------------------------------------

grammar TemplateFilter;
// @parser::members {public static boolean slotPosition;}
@lexer::members {public static boolean slotPosition;}
@header {
	package org.sil.antlr4.templatefilterparser.antlr4generated;
}

description : termSequence
			| EOF {notifyErrorListeners("missingClassOrSegment");}
	 ;

termSequence : termSequence slotPositionTerm termSequence
			 | termSequence constituentBeginMarkerTerm termSequence
			 | constituentBeginMarkerTerm termSequence
			 | termSequence slotPositionTerm {notifyErrorListeners("missingClassOrSegment");}
			 | termSequence constituentBeginMarkerTerm {notifyErrorListeners("missingClassOrSegment");}
			 | term
			 | term termSequence
			 ;

slotPositionTerm : SLOT_POSITION
	             ;

constituentBeginMarkerTerm : CONSTITUENT_BEGIN_MARKER
	             ;

term : optionalSegment
	 |  segment
	 ;

optionalSegment : '(' segment ')'
				| '('             {notifyErrorListeners("missingClassOrSegment");}
				| '(' ')'         {notifyErrorListeners("missingClassOrSegment");}
				| '(' segment     {notifyErrorListeners("missingClosingParen");}
				|     segment ')' {notifyErrorListeners("missingOpeningParen");}
				;
				
segment : ssp natClass
		| natClass
		| ssp literal
		| literal
		| ssp			{notifyErrorListeners("missingClassOrSegment");}
		;

ssp : '*'
	;
		
natClass : '[' ID+ ']'
 	      | '[' ID      {notifyErrorListeners("missingClosingSquareBracket");}
	      | ID ']'      {notifyErrorListeners("missingOpeningSquareBracket");}
 	      | '['         {notifyErrorListeners("missingClassAfterOpeningSquareBracket");}
 	      | '[' ']'     {notifyErrorListeners("missingClassAfterOpeningSquareBracket");}
 	      | ']'         {notifyErrorListeners("missingOpeningSquareBracket");}
	      ;

literal : ID
		;

WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines
SLOT_POSITION: '|' {slotPosition}? ;
CONSTITUENT_BEGIN_MARKER: '_' {slotPosition}? ;
ID : [\\~`=_|/<>#{},.;:^!?@$%&'"a-zA-Z\u0080-\uFFFF0-9+-]+ ; // Identifier
