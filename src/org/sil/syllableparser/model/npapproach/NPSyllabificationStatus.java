/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.npapproach;

/**
 * @author Andy Black
 *
 */
public enum NPSyllabificationStatus {
	APPENDED_SEGMENT_TO_SYLLABLE,
	APPLYING_RULE,
	BUILT_ALL_NODES,
	PREPENDED_SEGMENT_TO_SYLLABLE,
	NO_SEGMENTS_MATCHED_RULE,
	RULE_IGNORES_SSP,
	SSP_FAILED,
	SSP_PASSED,
	CODA_FILTER_FAILED,
	FILTER_FAILED,
	NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT,
	ONSET_FILTER_FAILED,
	ONSET_REQUIRED_IN_ALL_BUT_FIRST_SYLLABLE_BUT_SOME_NONINITIAL_SYLLABLE_DOES_NOT_HAVE_AN_ONSET,
	ONSET_REQUIRED_IN_EVERY_SYLLABLE_BUT_SOME_SYLLABLE_DOES_NOT_HAVE_AN_ONSET,
	SOME_SEGMENTS_NOT_SYLLABIFIED,
	SUCCESS,
	UNKNOWN,
}
