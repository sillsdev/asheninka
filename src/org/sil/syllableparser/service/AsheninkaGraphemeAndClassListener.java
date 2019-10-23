// Copyright (c) 2016-2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package org.sil.syllableparser.service;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.sil.syllableparser.model.Environment;
import org.sil.syllableparser.model.EnvironmentContextGraphemeOrNaturalClass;
import org.sil.syllableparser.model.GraphemeNaturalClass;

import org.sil.environmentparser.CheckGraphemeAndClassListener;
import org.sil.environmentparser.antlr4generated.EnvironmentParser;
import org.sil.environmentparser.GraphemeErrorInfo;

public class AsheninkaGraphemeAndClassListener extends CheckGraphemeAndClassListener {

	Environment environment;
	List<GraphemeNaturalClass> classes;
	org.sil.syllableparser.model.EnvironmentContext leftContext;
	org.sil.syllableparser.model.EnvironmentContext rightContext;
	org.sil.syllableparser.model.EnvironmentContext currentContext;
	final String kWordBoundary = "#";

	public AsheninkaGraphemeAndClassListener(EnvironmentParser parser,
			List<String> graphemesMasterList, List<String> classesMasterList) {
		super(parser, graphemesMasterList, classesMasterList);
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public List<GraphemeNaturalClass> getClasses() {
		return classes;
	}

	public void setClasses(List<GraphemeNaturalClass> classes) {
		this.classes = classes;
	}

	@Override
	public void enterEnvironment(EnvironmentParser.EnvironmentContext ctx) {
		environment = new Environment();
	}

	@Override
	public void enterLeftContext(EnvironmentParser.LeftContextContext ctx) {
		leftContext = new org.sil.syllableparser.model.EnvironmentContext();
		if (ctx.getText().startsWith(kWordBoundary)) {
			leftContext.setWordBoundary(true);
		}
		currentContext = leftContext;
	}

	@Override
	public void exitLeftContext(EnvironmentParser.LeftContextContext ctx) {
		environment.setLeftContext(leftContext);
	}

	@Override
	public void enterRightContext(EnvironmentParser.RightContextContext ctx) {
		rightContext = new org.sil.syllableparser.model.EnvironmentContext();
		currentContext = rightContext;
	}

	@Override
	public void exitRightContext(EnvironmentParser.RightContextContext ctx) {
		if (ctx.getText().endsWith(kWordBoundary)) {
			rightContext.setWordBoundary(true);
		}
		environment.setRightContext(rightContext);
	}

	@Override
	public void exitOptionalSegment(EnvironmentParser.OptionalSegmentContext ctx) {
		int iLast = currentContext.getEnvContext().size();
		EnvironmentContextGraphemeOrNaturalClass gnc = currentContext.getEnvContext().get(iLast-1);
		gnc.setOptional(true);
}

	@Override
	public void enterLiteral(EnvironmentParser.LiteralContext ctx) {
		String sGrapheme = ctx.ID().getText();
		if (!graphemesMasterList.contains(sGrapheme)) {
			boolean fSequenceFound = validator.findSequenceOfGraphemes(sGrapheme);
			if (!fSequenceFound) {
				int iMaxDepth = validator.getMaxDepth();
				GraphemeErrorInfo info = new GraphemeErrorInfo(sGrapheme, iMaxDepth);
				badGraphemes.add(info);
			}
		}
		// while there may be more than one grapheme in a row, we only need the
		// string
		EnvironmentContextGraphemeOrNaturalClass gnc = new EnvironmentContextGraphemeOrNaturalClass(
				sGrapheme, true);
		currentContext.getEnvContext().add(gnc);
	}

	@Override
	public void enterOrthClass(EnvironmentParser.OrthClassContext ctx) {
		int iSize = ctx.ID().size();
		if (iSize > 0) {
			String sClass = ctx.ID().stream().map(TerminalNode::getText)
					.collect(Collectors.joining(" "));
			EnvironmentContextGraphemeOrNaturalClass gnc = new EnvironmentContextGraphemeOrNaturalClass(
					sClass, false);
			List<GraphemeNaturalClass> matchingClasses = classes.stream().filter(nc -> nc.getNCName().equals(sClass)).collect(Collectors.toList());
			if (matchingClasses.size() > 0) {
				gnc.setGraphemeNaturalClass(matchingClasses.get(0));
			}
			currentContext.getEnvContext().add(gnc);
			if (!classesMasterList.contains(sClass)) {
				badClasses.add(sClass);
			}
		}
	}
}
