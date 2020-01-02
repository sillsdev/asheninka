// --------------------------------------------------------------------------------------------
// Copyright (c) 2019 SIL International
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

@header {
	package org.sil.antlr4.templatefilterparser.antlr4generated;
}

description : termSequence
			| EOF {notifyErrorListeners("missingClassOrSegment");}
	 ;

termSequence : slotPositionTerm
			 | slotPositionTerm termSequence
			 | term
			 | term termSequence
			 ;

slotPositionTerm : '|' term
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

ID : [\\~`=_|/<>#{},.;:^!?@$%&'"a-zA-Z\u0080-\uFFFF0-9+-]+ ; // Identifier
