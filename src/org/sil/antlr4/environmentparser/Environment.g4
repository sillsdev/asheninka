// --------------------------------------------------------------------------------------------
// Copyright (c) 2017-2019 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
//
// File: Environment.g4
// Responsibility: 
// Last reviewed:
//
// <remarks>
// ANTLR v.4 grammar for parsing phonological environments
// </remarks>
// --------------------------------------------------------------------------------------------

grammar Environment;

@header {
	package org.sil.environmentparser.antlr4generated;
}
environment : '/'             '_' rightContext EOF
			| '/' leftContext '_'              EOF
			| '/' leftContext '_' rightContext EOF
			| '/' '/'                                  {notifyErrorListeners("tooManySlashes");}
			| '/'             '_' '_' rightContext EOF {notifyErrorListeners("tooManyUnderscores");}
			| '/'             '_' rightContext '_' EOF {notifyErrorListeners("tooManyUnderscores");}
			| '/' leftContext '_' '_'              EOF {notifyErrorListeners("tooManyUnderscores");}
			| '/' leftContext '_' '_' rightContext EOF {notifyErrorListeners("tooManyUnderscores");}
			| '/' leftContext '_' rightContext '_' EOF {notifyErrorListeners("tooManyUnderscores");}
			| '/' '_'                              EOF {notifyErrorListeners("missingClassOrGrapheme");}
			;

leftContext : '#'
			| termSequence
			| '#' termSequence
 		    | termSequence '#'       {notifyErrorListeners("contentBeforeWordInitialBoundary");}
			| '#' '#'                {notifyErrorListeners("tooManyWordInitialBoundaries");}
			| '#' '#' termSequence   {notifyErrorListeners("tooManyWordInitialBoundaries");}
			;

rightContext : '#'
			 | termSequence
			 | termSequence '#'
			 | '#' termSequence      {notifyErrorListeners("contentAfterWordFinalBoundary");}
			 | termSequence '#' '#'  {notifyErrorListeners("tooManyWordFinalBoundaries");}
			 | '#' '#'               {notifyErrorListeners("tooManyWordFinalBoundaries");}
			 ;

termSequence : term
			 | term termSequence
			 ;

term : optionalSegment
	 |  segment
	 ;

optionalSegment : '(' segment ')'
				| '('             {notifyErrorListeners("missingClassOrGrapheme");}
				| '(' ')'         {notifyErrorListeners("missingClassOrGrapheme");}
				| '(' segment     {notifyErrorListeners("missingClosingParen");}
				|     segment ')' {notifyErrorListeners("missingOpeningParen");}
				;
				
segment : orthClass
		| literal
		;
		
orthClass : '[' ID+ ']'
 	      | '[' ID      {notifyErrorListeners("missingClosingSquareBracket");}
	      | ID ']'      {notifyErrorListeners("missingOpeningSquareBracket");}
 	      | '['         {notifyErrorListeners("missingClassAfterOpeningSquareBracket");}
 	      | '[' ']'     {notifyErrorListeners("missingClassAfterOpeningSquareBracket");}
 	      | ']'         {notifyErrorListeners("missingOpeningSquareBracket");}
	      ;

literal : ID
		;

WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines

ID : [,.;:^!?@$%&'"a-zA-Z\u0080-\uFFFF0-9+-]+ ; // Identifier
