/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.npapproach;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

/**
 * @author Andy Black
 *
 */
public class NPTracingStep {

	public Segment segment1 = null;
	public SHNaturalClass naturalClass1 = null;
	public Segment segment2 = null;
	public SHNaturalClass naturalClass2 = null;
	public SHComparisonResult comparisonResult = null;
	private NPSyllabificationStatus status = NPSyllabificationStatus.UNKNOWN;
	private NPSyllable syllable = null;
	List<NPSegmentInSyllable> segmentsInWord = new ArrayList<NPSegmentInSyllable>();
	private Filter filterUsed;
	private String graphemesInMatchedSyllableTemplate;
	private NPRule rule;
	public String sMissingNaturalClass = "No Natural Class";
	public static final String NULL_REPRESENTATION = "&#xa0;&#x2014";
	protected ResourceBundle bundle;
	protected boolean successful;

	public NPTracingStep() {
		super();
	}

	public NPTracingStep(Segment segment, NPSyllabificationStatus status) {
		super();
		this.segment1 = segment;
		this.status = status;
	}

	public NPTracingStep(Segment segment1, SHNaturalClass naturalClass1, Segment segment2,
			SHNaturalClass naturalClass2, SHComparisonResult result) {
		super();
		this.segment1 = segment1;
		this.naturalClass1 = naturalClass1;
		this.segment2 = segment2;
		this.naturalClass2 = naturalClass2;
		this.comparisonResult = result;
	}

	public String getSegment1Result() {
		if (segment1 == null) {
			return NULL_REPRESENTATION;
		} else {
			return segment1.getSegment();
		}
	}

	public String getNaturalClass1Result() {
		if (naturalClass1 == null) {
			if (comparisonResult == SHComparisonResult.MISSING1) {
				return sMissingNaturalClass;
			} else {
				return NULL_REPRESENTATION;
			}
		} else {
			return naturalClass1.getNCName();
		}
	}

	public String getSegment2Result() {
		if (segment2 == null) {
			return NULL_REPRESENTATION;
		} else {
			return segment2.getSegment();
		}
	}

	public String getNaturalClass2Result() {
		if (naturalClass2 == null) {
			if (comparisonResult == SHComparisonResult.MISSING2) {
				return sMissingNaturalClass;
			} else {
				return NULL_REPRESENTATION;
			}
		} else {
			return naturalClass2.getNCName();
		}
	}

	public String getComparisonResultAsString() {
		String result = "";
		if (comparisonResult == null) {
			return NULL_REPRESENTATION;
		} else {
			switch (comparisonResult) {
			case LESS:
				result = "<";
				break;
			case EQUAL:
				result = "=";
				break;
			case MORE:
				result = ">";
				break;
			case MISSING1:
				result = "!!!";
				break;
			case MISSING2:
				result = "!!!";
				break;
			default:
				break;
			}
			return result;
		}
	}

	public NPSyllabificationStatus getStatus() {
		return status;
	}

	public void setStatus(NPSyllabificationStatus status) {
		setSuccessBasedOnStatus(status);
		this.status = status;
	}

	protected void setSuccessBasedOnStatus(NPSyllabificationStatus status) {
		switch (status) {
		case APPENDED_SEGMENT_TO_SYLLABLE:
		case APPLYING_RULE:
		case BUILT_ALL_NODES:
		case LEFT_ADJOINED_SEGMENT_TO_N_BAR_NODE:
		case LEFT_ADJOINED_SEGMENT_TO_N_DOUBLE_BAR_NODE:
		case LEFT_ADJOINED_SEGMENT_TO_N_NODE:
		case NO_SEGMENTS_MATCHED_RULE:
		case PREPENDED_SEGMENT_TO_SYLLABLE:
		case RIGHT_ADJOINED_SEGMENT_TO_N_BAR_NODE:
		case RIGHT_ADJOINED_SEGMENT_TO_N_DOUBLE_BAR_NODE:
		case RIGHT_ADJOINED_SEGMENT_TO_N_NODE:
		case RULE_IGNORES_SSP:
		case SSP_PASSED:
		case SUCCESS:
			successful = true;
			break;
		default:
			successful = false;
			break;
		}
	}

	public Segment getSegment1() {
		return segment1;
	}

	public void setSegment1(Segment segment1) {
		this.segment1 = segment1;
	}

	public SHNaturalClass getNaturalClass1() {
		return naturalClass1;
	}

	public void setNaturalClass1(SHNaturalClass naturalClass1) {
		this.naturalClass1 = naturalClass1;
	}

	public Segment getSegment2() {
		return segment2;
	}

	public void setSegment2(Segment segment2) {
		this.segment2 = segment2;
	}

	public SHNaturalClass getNaturalClass2() {
		return naturalClass2;
	}

	public void setNaturalClass2(SHNaturalClass naturalClass2) {
		this.naturalClass2 = naturalClass2;
	}

	public SHComparisonResult getComparisonResult() {
		return comparisonResult;
	}

	public void setComparisonResult(SHComparisonResult comparisonResult) {
		this.comparisonResult = comparisonResult;
	}

	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public String getStatusLocalized() {
		String result = "";
		switch (status) {
		case APPENDED_SEGMENT_TO_SYLLABLE:
			result = addSegment1ToStatus(bundle.getString("label.npstiappendedsegmenttosyllable"));
			break;
		case APPLYING_RULE:
			result = bundle.getString("label.npstiapplyingrule");
			result = result.replace("{0}", "(" + rule.getRuleName() + " \""
					+ rule.getDescription() + "\")");
			break;
		case BUILT_ALL_NODES:
			result = addSegment1ToStatus(bundle.getString("label.npstibuiltallnodes"));
			break;
		case FILTER_FAILED:
			result = addFilterIDToStatus(bundle.getString("label.npstifilterfailed"));
			break;
		case LEFT_ADJOINED_SEGMENT_TO_N_BAR_NODE:
			result = addSegment1ToStatus(bundle.getString("label.npstileftadjoinedsegmenttonbarnode"));
			break;
		case LEFT_ADJOINED_SEGMENT_TO_N_DOUBLE_BAR_NODE:
			result = addSegment1ToStatus(bundle.getString("label.npstileftadjoinedsegmenttondoublebarnode"));
			break;
		case LEFT_ADJOINED_SEGMENT_TO_N_NODE:
			result = addSegment1ToStatus(bundle.getString("label.npstileftadjoinedsegmenttonnode"));
			break;
		case NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT:
			result = bundle.getString("label.oncstinaturalclassnotfoundforsegment");
			break;
		case NO_SEGMENTS_MATCHED_RULE:
			result = bundle.getString("label.npstinosegmentsmatchedrule");
			break;
		case ONSET_REQUIRED_IN_ALL_BUT_FIRST_SYLLABLE_BUT_SOME_NONINITIAL_SYLLABLE_DOES_NOT_HAVE_AN_ONSET:
			result = bundle.getString("label.npstionsetsonallbutfirstbutmissingonset");
			break;
		case ONSET_REQUIRED_IN_EVERY_SYLLABLE_BUT_SOME_SYLLABLE_DOES_NOT_HAVE_AN_ONSET:
			result = bundle.getString("label.npstionsetsoneverybutmissingonset");
			break;
		case PREPENDED_SEGMENT_TO_SYLLABLE:
			result = addSegment1ToStatus(bundle.getString("label.npstiprependedsegmenttosyllable"));
			break;
		case RIGHT_ADJOINED_SEGMENT_TO_N_BAR_NODE:
			result = addSegment1ToStatus(bundle.getString("label.npstirightadjoinedsegmenttonbarnode"));
			break;
		case RIGHT_ADJOINED_SEGMENT_TO_N_DOUBLE_BAR_NODE:
			result = addSegment1ToStatus(bundle.getString("label.npstirightadjoinedsegmenttondoublebarnode"));
			break;
		case RIGHT_ADJOINED_SEGMENT_TO_N_NODE:
			result = addSegment1ToStatus(bundle.getString("label.npstirightadjoinedsegmenttonnode"));
			break;
		case RULE_IGNORES_SSP:
			result = bundle.getString("label.npstiruleignoresssp");
			break;
		case SOME_SEGMENTS_NOT_SYLLABIFIED:
			result = bundle.getString("label.npstisomesegmentsnotsyllabified");
			break;
		case SSP_FAILED:
			result = bundle.getString("label.npstisspfailed");
			break;
		case SSP_PASSED:
			result = bundle.getString("label.npstissppassed");
			break;
		case SUCCESS:
			result = bundle.getString("label.oncstisuccess");
			break;
		case UNKNOWN:
			result = bundle.getString("label.moraicstiunknown");
			break;
		default:
			break;
		}
		return result;
	}

	private String addFilterIDToStatus(String statusMessage) {
		return statusMessage.replace("{0}", "(" + filterUsed.getTemplateFilterName() + " \""
				+ filterUsed.getTemplateFilterRepresentation() + "\")");
	}

	private String addSegment1ToStatus(String statusMessage) {
		return statusMessage.replace("{0}", getSegment1Result());
	}

	public Filter getFilterUsed() {
		return filterUsed;
	}

	public void setFilterUsed(Filter filterUsed) {
		this.filterUsed = filterUsed;
	}

	public String getGraphemesInMatchedSyllableTemplate() {
		return graphemesInMatchedSyllableTemplate;
	}

	public void setGraphemesInMatchedSyllableTemplate(String segmentsInMatchedSyllableTemplate) {
		this.graphemesInMatchedSyllableTemplate = segmentsInMatchedSyllableTemplate;
	}

	public NPRule getRule() {
		return rule;
	}

	public void setRule(NPRule rule) {
		this.rule = rule;
	}

	public NPSyllable getSyllable() {
		return syllable;
	}

	public void setSyllable(NPSyllable syllable) {
		this.syllable = syllable;
	}

	public List<NPSegmentInSyllable> getSegmentsInWord() {
		return segmentsInWord;
	}

	public void setSegmentsInWord(List<NPSegmentInSyllable> segmentsInWord) {
		this.segmentsInWord = new ArrayList<NPSegmentInSyllable>(segmentsInWord);
	}
}
