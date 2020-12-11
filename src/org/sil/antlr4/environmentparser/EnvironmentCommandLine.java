// Copyright (c) 2016-2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */

package org.sil.antlr4.environmentparser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.sil.antlr4.environmentparser.antlr4generated.EnvironmentLexer;
import org.sil.antlr4.environmentparser.antlr4generated.EnvironmentParser;

public class EnvironmentCommandLine {
	
	public static void main( String[] args) throws Exception 
    {

        CharStream input = CharStreams.fromStream(System.in);
        EnvironmentLexer lexer = new EnvironmentLexer(input);

    	CommonTokenStream tokens = new CommonTokenStream(lexer);

    	EnvironmentParser parser = new EnvironmentParser(tokens);
    	// begin parsing at rule 'environment'
    	ParseTree tree = parser.environment();
        System.out.println(tree.toStringTree(parser)); // print LISP-style tree
    }
}
