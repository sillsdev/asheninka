/**
 * Copyright (c) 2016-2025 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service;

import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.sil.syllableparser.model.Environment;
import org.sil.syllableparser.model.GraphemeNaturalClass;
import org.sil.antlr4.environmentparser.antlr4generated.EnvironmentLexer;
import org.sil.antlr4.environmentparser.antlr4generated.EnvironmentParser;

/**
 * @author Andy Black
 *
 */
public class EnvironmentParsingBase {
	
	List<String> graphemesMasterList = Arrays.asList("a", "ai", "b", "c", "d", "e", "f", "fl",
			"fr", "a", // test duplicate
			"\u00ED", // single combined Unicode acute i (í)
			"i\u0301", // combined acute i
			"H");

	List<String> classesMasterList = Arrays.asList("V", "Vowels", "C", "+son", "C", "+lab, +vd",
			"+ant, -cor, -vd");

	protected List<GraphemeNaturalClass> classes;
	
	public List<GraphemeNaturalClass> getClasses() {
		return classes;
	}

	public void setClasses(List<GraphemeNaturalClass> classes) {
		this.classes = classes;
	}

	protected EnvironmentParser parseEnvironmentString(String sInput) {
		CharStream input = CharStreams.fromString(sInput);
		EnvironmentLexer lexer = new EnvironmentLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		EnvironmentParser parser = new EnvironmentParser(tokens);
		// begin parsing at rule 'environment'
		ParseTree tree = parser.environment();
		ParseTreeWalker walker = new ParseTreeWalker(); // create standard
														// walker
		AsheninkaGraphemeAndClassListener validator = new AsheninkaGraphemeAndClassListener(parser,
				graphemesMasterList, classesMasterList);
		validator.setCheckForReduplication(true);
		Environment env = new Environment();
		validator.setEnvironment(env);
		validator.setClasses(classes);
		walker.walk(validator, tree); // initiate walk of tree with listener
		parser.addParseListener(validator);
		return parser;
	}

}
