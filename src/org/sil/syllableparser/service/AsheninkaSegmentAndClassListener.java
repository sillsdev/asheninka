// Copyright (c) 2019 SIL International 
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
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.TemplateFilter;
import org.sil.syllableparser.model.TemplateFilterSlotSegmentOrNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.antlr4.templatefilterparser.CheckSegmentAndClassListener;
import org.sil.antlr4.templatefilterparser.SegmentErrorInfo;
import org.sil.antlr4.templatefilterparser.antlr4generated.TemplateFilterParser;

public class AsheninkaSegmentAndClassListener extends CheckSegmentAndClassListener {

	TemplateFilter templateFilter;
	List<CVNaturalClass> classes;
	TemplateFilterSlotSegmentOrNaturalClass currentSlot;

	public AsheninkaSegmentAndClassListener(TemplateFilterParser parser,
			List<String> segmentsMasterList, List<String> classesMasterList) {
		super(parser, segmentsMasterList, classesMasterList);
	}

	public TemplateFilter getTemplateFilter() {
		return templateFilter;
	}

	public void setTemplateFilter(TemplateFilter templateFilter) {
		this.templateFilter = templateFilter;
	}

	public void setupSegmentsMasterList(List<Segment> masterSegmentsList) {
		super.setSegmentsMasterList(masterSegmentsList);
	}

	public List<CVNaturalClass> getClasses() {
		return classes;
	}

	public void setClasses(List<CVNaturalClass> classes) {
		this.classes = classes;
	}

	@Override
	public void enterDescription(TemplateFilterParser.DescriptionContext ctx) {
		templateFilter = new TemplateFilter();
	}

	@Override
	public void exitOptionalSegment(TemplateFilterParser.OptionalSegmentContext ctx) {
		currentSlot.setOptional(true);
}

	@Override
	public void enterLiteral(TemplateFilterParser.LiteralContext ctx) {
		String sSegment = ctx.ID().getText();
		if (!segmentsAsStringMasterList.contains(sSegment)) {
			int initialSlotPosition = templateFilter.getSlots().size();
			boolean fSequenceFound = validator.findSequenceOfSegments(sSegment, templateFilter.getSlots());
			if (!fSequenceFound) {
				int iMaxDepth = validator.getMaxDepth();
				SegmentErrorInfo info = new SegmentErrorInfo(sSegment, iMaxDepth);
				badSegments.add(info);
			} else {
				currentSlot = templateFilter.getSlots().get(initialSlotPosition);
			}
		} else {
			currentSlot = new TemplateFilterSlotSegmentOrNaturalClass(
					sSegment);
			List<Segment> matchingSegments = segmentsMasterList.stream().filter(nc -> nc.getSegment().equals(sSegment)).collect(Collectors.toList());
			if (matchingSegments.size() > 0) {
				currentSlot.setReferringSegment(matchingSegments.get(0));
			}
			templateFilter.getSlots().add(currentSlot);
		}
	}

	@Override
	public void enterNatClass(TemplateFilterParser.NatClassContext ctx) {
		int iSize = ctx.ID().size();
		if (iSize > 0) {
			String sClass = ctx.ID().stream().map(TerminalNode::getText)
					.collect(Collectors.joining(" "));
			currentSlot = new TemplateFilterSlotSegmentOrNaturalClass();
			List<CVNaturalClass> matchingClasses = classes.stream().filter(nc -> nc.getNCName().equals(sClass)).collect(Collectors.toList());
			if (matchingClasses.size() > 0) {
				currentSlot.setCVNaturalClass(matchingClasses.get(0));
			}
			templateFilter.getSlots().add(currentSlot);
			if (!classesMasterList.contains(sClass)) {
				badClasses.add(sClass);
			}
		}
	}
	
	@Override
	public void exitSegment(TemplateFilterParser.SegmentContext ctx) {
		if (ctx.getChildCount() > 1) {
			currentSlot.setObeysSSP(false);
		}
	}

}
