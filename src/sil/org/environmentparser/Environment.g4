// --------------------------------------------------------------------------------------------
// Copyright (c) 2017 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
//
// File: Environment.g4
// Responsibility: 
// Last reviewed:
//
// <remarks>
// ANTLR v.4 grammar for recognizing phonological environments
// </remarks>
// --------------------------------------------------------------------------------------------

grammar Environment;

@header {
	package sil.org.environmentparser;
}
environment : '/'             '_' rightContext EOF
			| '/' leftContext '_'              EOF
			| '/' leftContext '_' rightContext EOF
			| '/' '/'                               {notifyErrorListeners("tooManySlashes");}
			| '/'             '_' '_' rightContext EOF {notifyErrorListeners("tooManyUnderscores");}
			| '/'             '_' rightContext '_' EOF {notifyErrorListeners("tooManyUnderscores");}
			| '/' leftContext '_' '_'              EOF {notifyErrorListeners("tooManyUnderscores");}
			| '/' leftContext '_' '_' rightContext EOF {notifyErrorListeners("tooManyUnderscores");}
			| '/' leftContext '_' rightContext '_' EOF {notifyErrorListeners("tooManyUnderscores");}
			;

leftContext : '#'
			| termSequence
			| '#' termSequence
 		    | termSequence '#'   {notifyErrorListeners("contentBeforeWordInitialBoundary");}
			| '#' '#'            {notifyErrorListeners("tooManyWordInitialBoundaries");}
			;

rightContext : '#'
			 | termSequence
			 | termSequence '#'
			 | '#' termSequence   {notifyErrorListeners("contentAfterWordFinalBoundary");}
			 | '#' '#'            {notifyErrorListeners("tooManyWordFinalBoundaries");}
			 ;

termSequence : term
			 | term termSequence
			 ;

term : optionalSegment
	 |  segment
	 ;

optionalSegment : '(' segment ')'
				| '(' segment     {notifyErrorListeners("missingClosingParen");}
				|     segment ')' {notifyErrorListeners("missingOpeningParen");}
				;
				
segment : orthClass
		| literal
		;
		
orthClass : '[' ID+ ']'
 	      | '[' ID      {notifyErrorListeners("missingClosingSquareBracket");}
	      | ID ']'      {notifyErrorListeners("missingOpeningSquareBracket");}
	      ;

literal : ID
		;

WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines
//ID : [^/()_# \t\n\r]+ ; // Identifier
ID : [,.;:^!?@$%&'"a-zA-Z\u0080-\uFFFF0-9+-]+ ; // Identifier
