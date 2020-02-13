/**
 * Copyright (c) 2019-2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.oncapproach;

/**
 * @author Andy Black
 *
 */
public enum ONCSyllabificationStatus {
	ADDED_AS_NUCLEUS,
	ADDED_AS_ONSET,
	ADDED_AS_CODA,
	ADDED_AS_CODA_START_NEW_SYLLABLE,
	ADDING_FINAL_SYLLABLE_TO_WORD,
	ADDING_SYLLABLE_TO_WORD,
	CODA_FILTER_FAILED,
	CODA_FILTER_REPAIR_COULD_NOT_APPLY,
	EXPECTED_NUCLEUS_NOT_FOUND,
	EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_CODAS_NOT_ALLOWED_START_NEW_SYLLABLE,
	EXPECTED_NUCLEUS_OR_CODA_BUT_NOT_NUCLEUS_AND_NOT_CODA_START_NEW_SYLLABLE,
	EXPECTED_ONSET_OR_NUCLEUS_NOT_FOUND,
	NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT,
	NUCLEUS_FILTER_FAILED,
	NUCLEUS_FILTER_REPAIR_COULD_NOT_APPLY,
	NUCLEUS_TEMPLATE_BLOCKS_ADDING_ANOTHER_NUCLEUS_CREATE_NEW_SYLLABLE,
	NUCLEUS_TEMPLATE_BLOCKS_ADDING_NUCLEUS_ONSET_REQUIRED_BUT_WONT_BE_ONE,
	NUCLEUS_TEMPLATE_MATCHED,
	NUCLEUS_TEMPLATES_ALL_FAIL,
	ONSET_FILTER_FAILED,
	ONSET_FILTER_REPAIR_APPLIED,
	ONSET_FILTER_REPAIR_COULD_NOT_APPLY_NO_PREVIOUS_SYLLABLE,
	ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_COULD_NOT_GO_IN_PREVIOUS_SYLLABLE,
	ONSET_FILTER_REPAIR_COULD_NOT_APPLY_ONSET_REQUIRED_BUT_WONT_BE_ONE,
	ONSET_REQUIRED_BUT_SEGMENT_NOT_AN_ONSET,
	RIME_FILTER_FAILED,
	RIME_FILTER_REPAIR_COULD_NOT_APPLY,
	SEGMENT_TRIED_AS_ONSET_BUT_NOT_AN_ONSET,
	SEGMENT_TRIED_AS_ONSET_BUT_SONORITY_BLOCKS_IT_AS_AN_ONSET,
	SEGMENT_IS_CODA_OR_ONSET_BUT_ONSET_MAXIMIZATION_BLOCKS_AS_CODA_START_NEW_SYLLABLE,
	SEGMENT_IS_CODA_OR_ONSET_BUT_ONSETS_REQUIRED_AND_NEXT_NOT_ONSET_START_NEW_SYLLABLE,
	SUCCESS,
	SYLLABLE_FILTER_FAILED,
	SYLLABLE_FILTER_REPAIR_COULD_NOT_APPLY,
	UNKNOWN,
}
