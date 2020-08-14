// Copyright (c) 2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */

package org.sil.antlr4.templatefilterparser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.sil.antlr4.templatefilterparser.antlr4generated.TemplateFilterLexer;
import org.sil.antlr4.templatefilterparser.antlr4generated.TemplateFilterParser;

public class TemplateFilterCommandLine {
	
	public static void main( String[] args) throws Exception 
    {

        CharStream input = CharStreams.fromStream(System.in);
        TemplateFilterLexer lexer = new TemplateFilterLexer(input);

    	CommonTokenStream tokens = new CommonTokenStream(lexer);

    	TemplateFilterParser parser = new TemplateFilterParser(tokens);
    	// begin parsing at rule 'description'
    	ParseTree tree = parser.description();
        System.out.println(tree.toStringTree(parser)); // print LISP-style tree
    }
}
