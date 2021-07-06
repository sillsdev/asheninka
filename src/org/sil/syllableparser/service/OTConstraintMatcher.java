/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVSegmentOrNaturalClass;
import org.sil.syllableparser.model.otapproach.OTConstraint;
import org.sil.syllableparser.model.otapproach.OTSegmentInSyllable;
import org.sil.utility.StringUtilities;

/**
 * @author Andy Black
 *
 *         Singleton pattern for validation of NP rules
 */
public class OTConstraintMatcher {

	private OTConstraint constraint;
	private List<OTSegmentInSyllable> segments = new ArrayList<OTSegmentInSyllable>();
	private ObservableList<Segment> segmentInventory;
	private ObservableList<CVNaturalClass> natClasses;
	
	private static OTConstraintMatcher instance;

	public static OTConstraintMatcher getInstance() {
		if (instance == null) {
			instance = new OTConstraintMatcher();
		}
		return instance;
	}

	public void setLanguageProject(LanguageProject languageProject) {
		if (languageProject != null) {
			segmentInventory = FXCollections.observableList(languageProject.getActiveSegmentsInInventory());
			natClasses = FXCollections.observableList(languageProject.getOTApproach().getActiveCVNaturalClasses());
		}
	}

	public OTConstraint getConstraint() {
		return constraint;
	}

	public void setConstraint(OTConstraint constraint) {
		this.constraint = constraint;
	};
	
	public List<OTSegmentInSyllable> getSegments() {
		return segments;
	}

	public void setSegments(List<OTSegmentInSyllable> segments) {
		this.segments = segments;
	}

	public boolean match(OTConstraint constraint, OTSegmentInSyllable snc1, OTSegmentInSyllable snc2, int segInSylsAvailable) {
		if (segInSylsAvailable == 0) {
			return false;
		}
		boolean seg2Matches = true;
		boolean seg1Matches = segMatches(constraint.getAffectedSegOrNC1(), snc1);
		if (seg1Matches) {
			if ((snc1.getStructuralOptions() & constraint.getStructuralOptions1()) == 0) {
				// the structural options do not match
				return false;
			}
		}
		if (!StringUtilities.isNullOrEmpty(constraint.getAffectedElement2())) {
			if (segInSylsAvailable > 1) {
				seg2Matches = segMatches(constraint.getAffectedSegOrNC2(), snc2);
				if (seg2Matches) {
					if ((snc2.getStructuralOptions() & constraint.getStructuralOptions2()) == 0) {
						// the structural options do not match
						return false;
					}
				}
			} else {
				seg2Matches = false;
			}
		}
		return seg1Matches && seg2Matches;
	}

	protected boolean segMatches(CVSegmentOrNaturalClass snc, OTSegmentInSyllable segInSyl) {
		Segment affectedSegment = null;
		CVNaturalClass affectedNaturalClass = null;
		int index;
		boolean segMatches = false;
		if (snc == null || segInSyl == null) {
			return true;
		}
		if (snc.isSegment()) {
			index = SylParserObject.findIndexInListByUuid(segmentInventory, snc.getUuid());
			affectedSegment = segmentInventory.get(index);

		} else {
			index = SylParserObject.findIndexInListByUuid(natClasses, snc.getUuid());
			affectedNaturalClass = natClasses.get(index);

		}
		if (affectedNaturalClass != null) {
			segMatches = affectedNaturalClass.hasSegment(segInSyl.getSegment()); 
		} else {
			segMatches = segInSyl.getSegment().equals(affectedSegment);
		}
		return segMatches;
	}

}
