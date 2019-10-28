// Copyright (c) 2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package org.sil.antlr4.templatefilterparser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.sil.antlr4.templatefilterparser.antlr4generated.TemplateFilterBaseListener;
import org.sil.antlr4.templatefilterparser.antlr4generated.TemplateFilterParser;

public class CheckSegmentAndClassListener extends TemplateFilterBaseListener {
	protected TemplateFilterParser parser;
	protected boolean obligatorySegmentFound = false;

	protected List<String> segmentsMasterList;
	protected List<String> classesMasterList;
	protected LinkedList<SegmentErrorInfo> badSegments = new LinkedList<SegmentErrorInfo>(Arrays.asList());
	protected LinkedList<String> badClasses = new LinkedList<String>(Arrays.asList());
	protected SegmentSequenceValidator validator;
	
	public CheckSegmentAndClassListener(TemplateFilterParser parser,
			List<String> segmentsMasterList, List<String> classesMasterList) {
		super();
		this.parser = parser;
		this.segmentsMasterList = segmentsMasterList;
		this.classesMasterList = classesMasterList;
		validator = new SegmentSequenceValidator(segmentsMasterList);
	}

	@Override
	public void exitTerm(TemplateFilterParser.TermContext ctx) {
		if (ctx.segment() != null) {
			obligatorySegmentFound = true;
		}
	}

	@Override
	public void enterLiteral(TemplateFilterParser.LiteralContext ctx) {
		String sSegment = ctx.ID().getText();
		if (!segmentsMasterList.contains(sSegment)) {
			boolean fSequenceFound = validator.findSequenceOfSegments(sSegment);
			if (!fSequenceFound) {
				int iMaxDepth = validator.getMaxDepth();
				SegmentErrorInfo info = new SegmentErrorInfo(sSegment, iMaxDepth);
				badSegments.add(info);
			}
		}
	}

	@Override
	public void enterNatClass(TemplateFilterParser.NatClassContext ctx) {
		int iSize = ctx.ID().size();
		if (iSize > 0) {
			String sClass = ctx.ID().stream().map(TerminalNode::getText)
					.collect(Collectors.joining(" "));
			if (!classesMasterList.contains(sClass)) {
					badClasses.add(sClass);
			}
		}
	}

	public List<String> getSegmentsMasterList() {
		return segmentsMasterList;
	}

	public void setSegmentsMasterList(List<String> segmentsMasterList) {
		this.segmentsMasterList = segmentsMasterList;
	}

	public List<String> getClassesMasterList() {
		return classesMasterList;
	}

	public void setClassesMasterList(List<String> classesMasterList) {
		this.classesMasterList = classesMasterList;
	}

	public LinkedList<SegmentErrorInfo> getBadSegments() {
		return badSegments;
	}

	public LinkedList<String> getBadClasses() {
		return badClasses;
	}

	public boolean isObligatorySegmentFound() {
		return obligatorySegmentFound;
	}
}
