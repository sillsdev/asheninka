// Copyright (c) 2019-2020 SIL International 
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
import org.sil.syllableparser.model.Segment;

public class CheckSegmentAndClassListener extends TemplateFilterBaseListener {
	protected TemplateFilterParser parser;
	protected boolean obligatorySegmentFound = false;
	protected int slotPositionIndicatorsFound = 0;
	protected int constituentBeginMarkersFound = 0;

	protected List<String> segmentsAsStringMasterList;
	protected List<Segment> segmentsMasterList;
	protected List<String> classesMasterList;
	protected LinkedList<SegmentErrorInfo> badSegments = new LinkedList<SegmentErrorInfo>(Arrays.asList());
	protected LinkedList<String> badClasses = new LinkedList<String>(Arrays.asList());
	protected SegmentSequenceValidator validator;
	
	public CheckSegmentAndClassListener(TemplateFilterParser parser,
			List<String> segmentsMasterList, List<String> classesMasterList) {
		super();
		this.parser = parser;
		this.segmentsAsStringMasterList = segmentsMasterList;
		this.classesMasterList = classesMasterList;
		validator = new SegmentSequenceValidator(segmentsMasterList);
	}

	@Override
	public void exitSlotPositionTerm(TemplateFilterParser.SlotPositionTermContext ctx) {
		slotPositionIndicatorsFound++;
	}

	@Override
	public void exitConstituentBeginMarkerTerm(TemplateFilterParser.ConstituentBeginMarkerTermContext ctx) {
		constituentBeginMarkersFound++;
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
		if (!segmentsAsStringMasterList.contains(sSegment)) {
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

	public List<String> getSegmentsAsStringsMasterList() {
		return segmentsAsStringMasterList;
	}

	public void setSegmentsAsStringsMasterList(List<String> segmentsMasterList) {
		this.segmentsAsStringMasterList = segmentsMasterList;
	}

	public List<Segment> getSegmentsMasterList() {
		return segmentsMasterList;
	}

	public void setSegmentsMasterList(List<Segment> segmentsMasterList) {
		this.segmentsMasterList = segmentsMasterList;
		validator.setSegmentsMasterList(segmentsMasterList);
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

	public int getSlotPositionIndicatorsFound() {
		return slotPositionIndicatorsFound;
	}

	public int getConstituentBeginMarkersFound() {
		return constituentBeginMarkersFound;
	}
}
