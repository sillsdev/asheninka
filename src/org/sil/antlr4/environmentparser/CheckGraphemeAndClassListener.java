// Copyright (c) 2016-2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package org.sil.antlr4.environmentparser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.sil.antlr4.environmentparser.antlr4generated.EnvironmentBaseListener;
import org.sil.antlr4.environmentparser.antlr4generated.EnvironmentParser;

public class CheckGraphemeAndClassListener extends EnvironmentBaseListener {
	protected EnvironmentParser parser;

	protected List<String> graphemesMasterList;
	protected List<String> classesMasterList;
	protected LinkedList<GraphemeErrorInfo> badGraphemes = new LinkedList<GraphemeErrorInfo>(Arrays.asList());
	protected LinkedList<String> badClasses = new LinkedList<String>(Arrays.asList());
	protected GraphemeSequenceValidator validator;
	
	protected boolean fCheckForReduplication = false;
	
	public CheckGraphemeAndClassListener(EnvironmentParser parser,
			List<String> graphemesMasterList, List<String> classesMasterList) {
		super();
		this.parser = parser;
		this.graphemesMasterList = graphemesMasterList;
		this.classesMasterList = classesMasterList;
		validator = new GraphemeSequenceValidator(graphemesMasterList);
	}

	/** Listen to matches of classDeclaration */
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
	}

	@Override
	public void enterOrthClass(EnvironmentParser.OrthClassContext ctx) {
		int iSize = ctx.ID().size();
		if (iSize > 0) {
			String sClass = ctx.ID().stream().map(TerminalNode::getText)
					.collect(Collectors.joining(" "));
			if (!classesMasterList.contains(sClass) && fCheckForReduplication) {
				boolean fNotFound = true;
				int iCaret = sClass.indexOf('^');
				if (iCaret > -1) {
					boolean fHasDigit = sClass.substring(iCaret+1).matches("^[0-9]$");
					String sClassBeforeCaret = sClass.substring(0, iCaret);
					if (fHasDigit && classesMasterList.contains(sClassBeforeCaret)) {
						fNotFound = false;
					}
				}
				if (fNotFound) {
					badClasses.add(sClass);
				}
			}
		}
	}

	public List<String> getGraphemesMasterList() {
		return graphemesMasterList;
	}

	public void setGraphemesMasterList(List<String> graphemesMasterList) {
		this.graphemesMasterList = graphemesMasterList;
	}

	public List<String> getClassesMasterList() {
		return classesMasterList;
	}

	public void setClassesMasterList(List<String> classesMasterList) {
		this.classesMasterList = classesMasterList;
	}

	public boolean isfCheckForReduplication() {
		return fCheckForReduplication;
	}

	public void setCheckForReduplication(boolean fCheckForReduplication) {
		this.fCheckForReduplication = fCheckForReduplication;
	}

	public LinkedList<GraphemeErrorInfo> getBadGraphemes() {
		return badGraphemes;
	}

	public LinkedList<String> getBadClasses() {
		return badClasses;
	}
}
