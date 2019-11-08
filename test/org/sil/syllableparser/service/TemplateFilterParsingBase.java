/**
 * Copyright (c) 2019 SIL International
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
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.TemplateFilter;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.service.AsheninkaGraphemeAndClassListener;
import org.sil.antlr4.environmentparser.antlr4generated.EnvironmentLexer;
import org.sil.antlr4.environmentparser.antlr4generated.EnvironmentParser;
import org.sil.antlr4.templatefilterparser.antlr4generated.TemplateFilterLexer;
import org.sil.antlr4.templatefilterparser.antlr4generated.TemplateFilterParser;

/**
 * @author Andy Black
 *
 */
public class TemplateFilterParsingBase {
	
	List<String> segmentsMasterList = Arrays.asList("a", "ai", "b", "c", "d", "e", "f", "fl",
			"fr", "a", // test duplicate
			"\u00ED", // single combined Unicode acute i (í)
			"i\u0301", // combined acute i
			"H");

	List<String> classesMasterList = Arrays.asList("V", "Vowels", "C", "+son", "C", "+lab, +vd",
			"+ant, -cor, -vd");

	protected List<CVNaturalClass> classes;
	
	public List<CVNaturalClass> getClasses() {
		return classes;
	}

	public void setClasses(List<CVNaturalClass> classes) {
		this.classes = classes;
	}

	protected TemplateFilterParser parseDescriptionString(String sInput, List<Segment> activeSegments) {
		CharStream input = CharStreams.fromString(sInput);
		TemplateFilterLexer lexer = new TemplateFilterLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		TemplateFilterParser parser = new TemplateFilterParser(tokens);
		// begin parsing at rule 'description'
		ParseTree tree = parser.description();
		ParseTreeWalker walker = new ParseTreeWalker(); // create standard
														// walker
		AsheninkaSegmentAndClassListener validator = new AsheninkaSegmentAndClassListener(parser,
				segmentsMasterList, classesMasterList);
		validator.setupSegmentsMasterList(activeSegments);
		TemplateFilter tf = new TemplateFilter();
		validator.setTemplateFilter(tf);
		validator.setClasses(classes);
		walker.walk(validator, tree); // initiate walk of tree with listener
		parser.addParseListener(validator);
		return parser;
	}

}
