/**
 * Copyright (c) 2020-2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.npapproach;

import java.util.ResourceBundle;

import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.TemplateFilter;
import org.sil.syllableparser.model.moraicapproach.MoraicSyllabificationStatus;
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
	private MoraicSyllabificationStatus status = MoraicSyllabificationStatus.UNKNOWN;
	private TemplateFilter templateFilterUsed;
	private String graphemesInMatchedSyllableTemplate;
	public String sMissingNaturalClass = "No Natural Class";
	public static final String NULL_REPRESENTATION = "&#xa0;&#x2014";
	protected ResourceBundle bundle;
	protected boolean successful;

	public NPTracingStep() {
		super();
	}

	public NPTracingStep(Segment segment, MoraicSyllabificationStatus status) {
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

	public MoraicSyllabificationStatus getStatus() {
		return status;
	}

	public void setStatus(MoraicSyllabificationStatus status) {
		setSuccessBasedOnStatus(status);
		this.status = status;
	}

	protected void setSuccessBasedOnStatus(MoraicSyllabificationStatus status) {
		switch (status) {
		case ADDED_AS_MORA:
		case ADDED_AS_TWO_MORAS:
		case ADDED_AS_THREE_MORAS:
		case ADDED_AS_ONSET:
		case ADDED_AS_WORD_FINAL_APPENDIX:
		case ADDED_AS_WORD_INITIAL_APPENDIX:
		case ADDING_FINAL_SYLLABLE_TO_WORD:
		case ADDING_SYLLABLE_TO_WORD:
		case CODA_TEMPLATE_MATCHED:
		case NON_INITIAL_ONSET_TEMPLATE_MATCHED_START_NEW_SYLLABLE:
		case ONSET_FILTER_REPAIR_APPLIED:
		case ONSET_TEMPLATE_MATCHED:
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
		case ADDED_AS_MORA:
			result = bundle.getString("label.moraicstiaddedwithmora");
			break;
		case ADDED_AS_ONSET:
			result = bundle.getString("label.moraicstiaddedasonsettosyllable");
			break;
		case ADDED_AS_TWO_MORAS:
			result = bundle.getString("label.moraicstiaddedwithtwomoras");
			break;
		case ADDED_AS_THREE_MORAS:
			result = bundle.getString("label.moraicstiaddedwiththreemoras");
			break;
		case ADDED_AS_WORD_FINAL_APPENDIX:
			result = addTemplateFilterIDToStatus(bundle.getString("label.oncstiaddedaswordfinalappendix"));
			break;
		case ADDED_AS_WORD_INITIAL_APPENDIX:
			result = addTemplateFilterIDToStatus(bundle.getString("label.oncstiaddedaswordinitialappendix"));
			break;
		case ADDING_FINAL_SYLLABLE_TO_WORD:
			result = bundle.getString("label.oncstiaddingfinalsyllabletoword");
			break;
		case ADDING_SYLLABLE_TO_WORD:
			result = bundle.getString("label.oncstiaddingsyllabletoword");
			break;
		case APPENDED_TO_MORA:
			result = bundle.getString("label.moraicstiappendedtomora");
			break;
		case CODA_FILTER_FAILED:
			result = addTemplateFilterIDToStatus(bundle.getString("label.oncsticodafilterfailed"));
			break;
		case CODA_FILTER_REPAIR_APPLIED:
			result = addTemplateFilterIDToStatus(bundle.getString("label.oncsticodarepairapplied"));
			break;
		case CODA_FILTER_REPAIR_COULD_NOT_APPLY:
			result = addTemplateFilterIDToStatus(bundle.getString("label.oncsticodarepairnotapply"));
			break;
		case CODA_TEMPLATE_MATCHED:
			result = addTemplateFilterIDToStatus(bundle.getString("label.oncsticodatemplatematched"));
			break;
		case EXPECTED_MORA_NOT_FOUND:
			result = bundle.getString("label.moraicstiexpectedmoranotfound");
			break;
		case MAXIMUM_MORAS_IN_SYLLABLE_FOUND_START_NEW_SYLLABLE:
			result = bundle.getString("label.moraicstimaxmorasstartnewsyllable");
			break;
		case NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT:
			result = bundle.getString("label.oncstinaturalclassnotfoundforsegment");
			break;
		case NO_WORD_INITIAL_TEMPLATE_MATCHED:
			result = bundle.getString("label.oncstinowordinitialtemplatematched");
			break;
		case NON_INITIAL_ONSET_TEMPLATE_MATCHED_START_NEW_SYLLABLE:
			result = bundle.getString("label.moraicstinoninitialonsettemplatematchedstartnewsyllable");
			break;
		case ONSET_FILTER_FAILED:
			result = addTemplateFilterIDToStatus(bundle.getString("label.oncstionsetfilterfailed"));
			break;
		case ONSET_FILTER_REPAIR_APPLIED:
			result = addTemplateFilterIDToStatus(bundle.getString("label.oncstionsetrepairapplied"));
			break;
		case ONSET_FILTER_REPAIR_COULD_NOT_APPLY_NO_PREVIOUS_SYLLABLE:
			result = addTemplateFilterIDToStatus(bundle.getString("label.oncstionsetrepairnotapplynoprevioussyllable"));
			break;
		case ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_COULD_NOT_GO_IN_PREVIOUS_SYLLABLE:
			result = addTemplateFilterIDToStatus(bundle.getString("label.oncstionsetrepairnotapplynotgoinprevioussyllable"));
			break;
		case ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_REQUIRED_BUT_WONT_BE_ONE:
			result = addTemplateFilterIDToStatus(bundle.getString("label.oncstionsetrepairnotapplyonsetrequiredbutwontbeone"));
			break;
		case ONSET_REQUIRED_BUT_SEGMENT_NOT_AN_ONSET:
			result = bundle.getString("label.oncstionsetrequiredbutsegmentnotanonset");
			break;
		case ONSET_TEMPLATE_MATCHED:
			result = addTemplateFilterIDToStatus(bundle.getString("label.oncstionsettemplatematched"));
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
		case SYLLABIFICATION_OF_FIRST_SYLLABLE_FAILED_TRYING_WORD_INITIAL_TEMPLATES:
			result = bundle.getString("label.oncstisyllabificationoffirstsyllablefailedtryingwordinitialtemplates");
			break;
		case SYLLABLE_FILTER_FAILED:
			result = addTemplateFilterIDToStatus(bundle.getString("label.oncstisyllablefilterfailed"));
			break;
		case SYLLABLE_FILTER_REPAIR_COULD_NOT_APPLY:
			result = addTemplateFilterIDToStatus(bundle.getString("label.oncstisyllablerepairnotapply"));
			break;
		case SYLLABLE_TEMPLATES_FAILED:
			result = bundle.getString("label.moraicstisyllabletemplatefailed");
			break;
		case SYLLABLE_TEMPLATE_MATCHED:
			result = addTemplateFilterIDToStatus(bundle.getString("label.moraicstisyllabletemplatematched"));
			result = addMatchedGraphemesToStatus(result);
			break;
		case UNKNOWN:
			result = bundle.getString("label.moraicstiunknown");
			break;
		default:
			break;
		}
		return result;
	}

	private String addTemplateFilterIDToStatus(String statusMessage) {
		return statusMessage.replace("{0}", "(" + templateFilterUsed.getTemplateFilterName() + " \""
				+ templateFilterUsed.getTemplateFilterRepresentation() + "\")");
	}

	private String addMatchedGraphemesToStatus(String statusMessage) {
		return statusMessage.replace("{1}", getGraphemesInMatchedSyllableTemplate());
	}

	public TemplateFilter getTemplateFilterUsed() {
		return templateFilterUsed;
	}

	public void setTemplateFilterUsed(TemplateFilter templateFilterUsed) {
		this.templateFilterUsed = templateFilterUsed;
	}

	public String getGraphemesInMatchedSyllableTemplate() {
		return graphemesInMatchedSyllableTemplate;
	}

	public void setGraphemesInMatchedSyllableTemplate(String segmentsInMatchedSyllableTemplate) {
		this.graphemesInMatchedSyllableTemplate = segmentsInMatchedSyllableTemplate;
	}
}
