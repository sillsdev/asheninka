/**
 * Copyright (c) 2019-2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;

import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.FilterType;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.Template;
import org.sil.syllableparser.model.TemplateFilter;
import org.sil.syllableparser.model.TemplateFilterSlotSegment;
import org.sil.syllableparser.model.TemplateFilterSlotSegmentOrNaturalClass;
import org.sil.syllableparser.model.TemplateType;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.service.parsing.SHSonorityComparer;

/**
 * @author Andy Black
 *
 * Singleton (which needs to have its values initialized)
 */
public class TemplateFilterMatcher {

	private static TemplateFilterMatcher instance;
	List<Segment> activeSegments = new ArrayList<>();
	List<CVNaturalClass> activeClasses = new ArrayList<>();
	int iSegMatchCount = 0;
	List<TemplateFilterSlotSegment> slotAndSegment = new ArrayList<>();

	public static TemplateFilterMatcher getInstance() {
		if (instance == null) {
			instance = new TemplateFilterMatcher();
		}
		return instance;
	}

	public TemplateFilterMatcher() {
	}

	public List<Segment> getActiveSegments() {
		return activeSegments;
	}

	public void setActiveSegments(List<Segment> activeSegments) {
		this.activeSegments = activeSegments;
	}

	public List<CVNaturalClass> getActiveClasses() {
		return activeClasses;
	}

	public void setActiveClasses(List<CVNaturalClass> activeClasses) {
		this.activeClasses = activeClasses;
	}

	public int getMatchCount() {
		return iSegMatchCount;
	}

	public boolean matches(TemplateFilter tf, List<? extends CVSegmentInSyllable> segmentsToMatch,
			SHSonorityComparer sonorityComparer, SHComparisonResult sspComparisonNeeded) {
		slotAndSegment.clear();
		int iSlot = 0;
		List<TemplateFilterSlotSegmentOrNaturalClass> slots = tf.getSlots();
		iSegMatchCount = 0;
		TemplateFilterSlotSegmentOrNaturalClass slot;
		while (iSlot < slots.size() && iSegMatchCount < segmentsToMatch.size()) {
			slot = slots.get(iSlot);
			CVSegmentInSyllable segInSyl = segmentsToMatch.get(iSegMatchCount);
			Segment seg = segInSyl.getSegment();
			if (slot.isSegment()) {
				Segment referringSegment = slot.getReferringSegment();
				if (!activeSegments.contains(referringSegment)) {
					return false;
				}
				if (seg.equals(referringSegment)) {
					iSegMatchCount++;
					TemplateFilterSlotSegment ss = new TemplateFilterSlotSegment(slot, seg);
					slotAndSegment.add(ss);
				} else if (!slot.isOptional()) {
					return false;
				}
			} else {
				CVNaturalClass natClass = slot.getCVNaturalClass();
				if (!activeClasses.contains(natClass)) {
					return false;
				}
				int index = SylParserObject.findIndexInListByUuid(
						FXCollections.observableList(natClass.getAllSegments()), seg.getID());
				if (index >= 0) {
					iSegMatchCount++;
					TemplateFilterSlotSegment ss = new TemplateFilterSlotSegment(slot, seg);
					slotAndSegment.add(ss);
				} else if (!slot.isOptional()) {
					return false;
				}
			}
			iSlot++;
//			TemplateFilterSlotSegment ss = new TemplateFilterSlotSegment(slot, seg);
//			slotAndSegment.add(ss);
		}
		while (iSlot < slots.size()) {
			// not all slots were tested; remaining slots must be optional
			slot = slots.get(iSlot);
			if (!slot.isOptional()) {
				return false;
			}
			iSlot++;
		}
		if (iSlot == slots.size()) {
			// now check for sonority requirements
			return sonorityIsValid(tf, sonorityComparer, sspComparisonNeeded, slotAndSegment);
		}	
		return false;
	}

	private boolean sonorityIsValid(TemplateFilter tf, SHSonorityComparer sonorityComparer,
			SHComparisonResult sspComparisonNeeded, List<TemplateFilterSlotSegment> slotAndSegment) {
		if (slotAndSegment.size() <= 1)
			return true;
		if (sspComparisonNeeded != null) {
			return checkSonorityToNeeded(slotAndSegment, sonorityComparer, sspComparisonNeeded);
		} else {
			boolean isSyllableTemplate = tf instanceof Template
					&& ((Template) tf).getTemplateFilterType() == TemplateType.SYLLABLE;
			boolean isSyllableFilter = tf instanceof Filter
					&& ((Filter) tf).getTemplateFilterType() == FilterType.SYLLABLE;
			if (isSyllableTemplate || isSyllableFilter) {
				return checkSonorityOverSyllable(slotAndSegment, sonorityComparer);
			}
		}
		return true;
	}

	protected boolean checkSonorityToNeeded(List<TemplateFilterSlotSegment> slotAndSegment,
			SHSonorityComparer sonorityComparer, SHComparisonResult sspComparisonNeeded) {
		TemplateFilterSlotSegmentOrNaturalClass slot;
		TemplateFilterSlotSegment ssPrevious = slotAndSegment.get(0);
		for (int i = 1; i < slotAndSegment.size(); i++) {
			TemplateFilterSlotSegment ssCurrent = slotAndSegment.get(i);
			slot = ssCurrent.getSlot();
			if (slot.isObeysSSP()) {
				TemplateFilterSlotSegmentOrNaturalClass slotPrevious = ssPrevious.getSlot();
				if (slotPrevious.isObeysSSP()) {
					Segment segPrevious = ssPrevious.getSegment();
					Segment segCurrent = ssCurrent.getSegment();
					SHComparisonResult result = sonorityComparer.compare(segPrevious,
							segCurrent);
					if (result != SHComparisonResult.MISSING1
							&& result != SHComparisonResult.MISSING2) {
						if (result != sspComparisonNeeded) {
							return false;
						}
					}
				}
				ssPrevious = ssCurrent;
			}
		}
		return true;
	}

	protected boolean checkSonorityOverSyllable(List<TemplateFilterSlotSegment> slotAndSegment,
			SHSonorityComparer sonorityComparer) {
		TemplateFilterSlotSegmentOrNaturalClass slot;
		int iPeak = getPeak(sonorityComparer, slotAndSegment);
		TemplateFilterSlotSegment ssPrevious = slotAndSegment.get(0);
		for (int i = 1; i < slotAndSegment.size(); i++) {
			TemplateFilterSlotSegment ssCurrent = slotAndSegment.get(i);
			slot = ssCurrent.getSlot();
			if (slot.isObeysSSP()) {
				TemplateFilterSlotSegmentOrNaturalClass slotPrevious = ssPrevious.getSlot();
				if (slotPrevious.isObeysSSP()) {
					Segment segPrevious = ssPrevious.getSegment();
					Segment segCurrent = ssCurrent.getSegment();
					SHComparisonResult result = sonorityComparer.compare(segPrevious,
							segCurrent);
					int currentIndex = i - 1;
					switch (result) {
					case EQUAL:
						// is OK only if at top level of hierarchy
						if (currentIndex != iPeak
								&& sonorityComparer.getLevel(segCurrent) != iPeak)
							return false;
						break;
					case LESS:
						// is OK only if before top level and is optional
						if (currentIndex >= iPeak && !ssCurrent.getSlot().isOptional())
							return false;
						break;
					case MISSING1:
						break;
					case MISSING2:
						break;
					case MORE:
						// is OK only if at or after top level
						if (currentIndex < iPeak)
							return false;
						break;
					default:
						break;
					}
				}
				ssPrevious = ssCurrent;
			}
		}
		return true;
	}

	protected int getPeak(SHSonorityComparer sonorityComparer,
			List<TemplateFilterSlotSegment> slotAndSegment) {
		int topLevel = 1000000;
		int iPeak = -1;
		for (int i = 0; i < slotAndSegment.size(); i++) {
			TemplateFilterSlotSegment ss = slotAndSegment.get(i);
			Segment segment = ss.getSegment();
			int level = sonorityComparer.getLevel(segment);
			if (level < topLevel) {
				topLevel = level;
				iPeak = i;
			}
		}
		return iPeak;
	}
}
