/**
 * Copyright (c) 2019-2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.oncapproach;

import java.util.ResourceBundle;

import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.TemplateFilter;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;
import org.sil.syllableparser.service.parsing.ONCType;

/**
 * @author Andy Black
 *
 */
public class ONCTracingStep {

	public Segment segment1 = null;
	public SHNaturalClass naturalClass1 = null;
	public Segment segment2 = null;
	public SHNaturalClass naturalClass2 = null;
	public SHComparisonResult comparisonResult = null;
	private ONCSyllabificationStatus status = ONCSyllabificationStatus.UNKNOWN;
	private ONCType oncType = ONCType.UNKNOWN;
	private TemplateFilter templateFilterUsed;
	public String sMissingNaturalClass = "No Natural Class";
	public static final String NULL_REPRESENTATION = "&#xa0;&#x2014";
	protected ResourceBundle bundle;
	protected boolean successful;

	public ONCTracingStep() {
		super();
	}

	public ONCTracingStep(Segment segment, ONCSyllabificationStatus status) {
		super();
		this.segment1 = segment;
		this.status = status;
	}

	public ONCTracingStep(Segment segment, ONCType oncType) {
		super();
		this.segment1 = segment;
		this.oncType = oncType;
	}

	public ONCTracingStep(Segment segment1, SHNaturalClass naturalClass1, Segment segment2,
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

	public String getComparisonResult() {
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

	public ONCSyllabificationStatus getStatus() {
		return status;
	}

	public void setStatus(ONCSyllabificationStatus status) {
		setSuccessBasedOnStatus(status);
		this.status = status;
	}

	protected void setSuccessBasedOnStatus(ONCSyllabificationStatus status) {
		switch (status) {
		case EXPECTED_NUCLEUS_NOT_FOUND:
		case EXPECTED_ONSET_OR_NUCLEUS_NOT_FOUND:
		case NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT:
		case ONSET_REQUIRED_BUT_SEGMENT_NOT_AN_ONSET:
		case SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET:
		case SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET:
			successful = false;
			break;
		default:
			successful = true;
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

	public ONCType getOncType() {
		return oncType;
	}

	public void setOncType(ONCType oncType) {
		this.oncType = oncType;
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

	public String getOncTypeLocalized() {
		String result = "";
		switch (oncType) {
		case CODA:
			result = bundle.getString("label.onctypecoda");
			break;
		case CODA_OR_ONSET:
			result = bundle.getString("label.onctypecodaoronset");
			break;
		case NUCLEUS:
			result = bundle.getString("label.onctypenucleus");
			break;
		case NUCLEUS_OR_CODA:
			result = bundle.getString("label.onctypenucleusorcoda");
			break;
		case ONSET:
			result = bundle.getString("label.onctypeonset");
			break;
		case ONSET_OR_NUCLEUS:
			result = bundle.getString("label.onctypeonsetornucleus");
			break;
		case UNKNOWN:
			result = bundle.getString("label.onctypeunknown");
			break;
		case FILTER_FAILED:
			break;
		case FILTER_REPAIR_APPLIED:
			break;
		default:
			break;
		}
		return result;
	}

	public String getStatusLocalized() {
		String result = "";
		switch (status) {
		case ADDED_AS_CODA:
			result = bundle.getString("label.oncstiaddedascoda");
			break;
		case ADDED_AS_CODA_START_NEW_SYLLABLE:
			result = bundle.getString("label.oncstiaddedascodastartnewsyllable");
			break;
		case ADDED_AS_NUCLEUS:
			result = bundle.getString("label.oncstiaddedasnucleus");
			break;
		case ADDED_AS_ONSET:
			result = bundle.getString("label.oncstiaddedasonset");
			break;
		case ADDING_FINAL_SYLLABLE_TO_WORD:
			result = bundle.getString("label.oncstiaddingfinalsyllabletoword");
			break;
		case ADDING_SYLLABLE_TO_WORD:
			result = bundle.getString("label.oncstiaddingsyllabletoword");
			break;
		case CODA_FILTER_FAILED:
			result = addTemplateFilterIDToStatus(bundle.getString("label.oncsticodafilterfailed"));
			break;
		case EXPECTED_NUCLEUS_NOT_FOUND:
			result = bundle.getString("label.oncstiexpectednucleusnotfound");
			break;
		case EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_CODAS_NOT_ALLOWED_START_NEW_SYLLABLE:
			result = bundle
					.getString("label.oncstiexpectednucleusorcodabutnotnucleusandcodasnotallowedstartnewsyllable");
			break;
		case EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE:
			result = bundle
					.getString("label.oncstiexpectednucleusorcodabutnotnucleusandnotcodastartnewsyllable");
			break;
		case EXPECTED_ONSET_OR_NUCLEUS_NOT_FOUND:
			result = bundle.getString("label.oncstiexpectedonsetornucleusnotfound");
			break;
		case NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT:
			result = bundle.getString("label.oncstinaturalclassnotfoundforsegment");
			break;
		case NUCLEUS_FILTER_FAILED:
			result = addTemplateFilterIDToStatus(bundle.getString("label.oncstinucleusfilterfailed"));
			break;
		case ONSET_FILTER_FAILED:
			result = addTemplateFilterIDToStatus(bundle.getString("label.oncstionsetfilterfailed"));
			break;
		case ONSET_REQUIRED_BUT_SEGMENT_NOT_AN_ONSET:
			result = bundle.getString("label.oncstionsetrequiredbutsegmentnotanonset");
			break;
		case RIME_FILTER_FAILED:
			result = addTemplateFilterIDToStatus(bundle.getString("label.oncstirimefilterfailed"));
			break;
		case SEGMENT_IS_CODA_OR_ONSET_BUT_ONSET_MAXIMIZATION_BLOCKS_AS_CODA_START_NEW_SYLLABLE:
			result = bundle
					.getString("label.oncstisegmentiscodaoronsetbutonsetmaximizationblocksascodastartnewsyllable");
			break;
		case SEGMENT_IS_CODA_OR_ONSET_BUT_ONSETS_REQUIRED_AND_NEXT_NOT_ONSET_START_NEW_SYLLABLE:
			result = bundle
					.getString("label.oncstisegmentiscodaoronsetbutonsetsrequiredandnextnotonsetstartnewsyllable");
			break;
		case SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET:
			result = bundle.getString("label.oncstisegmenttriedasonsetbutnotanonset");
			break;
		case SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET:
			result = bundle
					.getString("label.oncstisegmenttriedasonsetbutsonorityblocksitasanonset");
			break;
		case SUCCESS:
			result = bundle.getString("label.oncstisuccess");
			break;
		case SYLLABLE_FILTER_FAILED:
			result = addTemplateFilterIDToStatus(bundle.getString("label.oncstisyllablefilterfailed"));
			break;
		case UNKNOWN:
			result = bundle.getString("label.oncstiunknown");
			break;
		}
		return result;
	}

	private String addTemplateFilterIDToStatus(String statusMessage) {
		return statusMessage.replace("{0}", templateFilterUsed.getTemplateFilterName() + " ("
				+ templateFilterUsed.getTemplateFilterRepresentation() + ")");
	}

	public TemplateFilter getTemplateFilterUsed() {
		return templateFilterUsed;
	}

	public void setTemplateFilterUsed(TemplateFilter templateFilterUsed) {
		this.templateFilterUsed = templateFilterUsed;
	}
}
