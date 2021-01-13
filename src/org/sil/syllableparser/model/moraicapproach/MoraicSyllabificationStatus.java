/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.moraicapproach;

/**
 * @author Andy Black
 *
 */
public enum MoraicSyllabificationStatus {
	ADDED_AS_MORA,
	ADDED_AS_ONSET,
	ADDED_AS_WORD_FINAL_APPENDIX,
	ADDED_AS_WORD_INITIAL_APPENDIX,
	ADDING_FINAL_SYLLABLE_TO_WORD,
	ADDING_SYLLABLE_TO_WORD,
	APPENDED_TO_MORA,
	CODA_FILTER_FAILED,
	CODA_FILTER_REPAIR_APPLIED,
	CODA_FILTER_REPAIR_COULD_NOT_APPLY,
	CODA_TEMPLATE_MATCHED,
	EXPECTED_MORA_NOT_FOUND,
	FILTER_FAILED,
	MAXIMUM_MORAS_IN_SYLLABLE_FOUND_START_NEW_SYLLABLE,
	NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT,
	NO_WORD_INITIAL_TEMPLATE_MATCHED,
	ONSET_FILTER_FAILED,
	ONSET_FILTER_REPAIR_APPLIED,
	ONSET_FILTER_REPAIR_COULD_NOT_APPLY_NO_PREVIOUS_SYLLABLE,
	ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_COULD_NOT_GO_IN_PREVIOUS_SYLLABLE,
	ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_REQUIRED_BUT_WONT_BE_ONE,
	ONSET_REQUIRED_BUT_SEGMENT_NOT_AN_ONSET,
	ONSET_TEMPLATE_MATCHED,
	SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET,
	SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET,
	SEGMENT_IS_CODA_OR_ONSET_BUT_ONSET_MAXIMIZATION_BLOCKS_AS_CODA_START_NEW_SYLLABLE,
	SEGMENT_IS_CODA_OR_ONSET_BUT_ONSETS_REQUIRED_AND_NEXT_NOT_ONSET_START_NEW_SYLLABLE,
	SUCCESS,
	SYLLABIFICATION_OF_FIRST_SYLLABLE_FAILED_TRYING_WORD_INITIAL_TEMPLATES,
	SYLLABLE_FILTER_FAILED,
	SYLLABLE_FILTER_REPAIR_COULD_NOT_APPLY,
	UNKNOWN,
	WORD_FINAL_TEMPLATE_APPLIED,
	WORD_INITIAL_TEMPLATE_APPLIED,
}
