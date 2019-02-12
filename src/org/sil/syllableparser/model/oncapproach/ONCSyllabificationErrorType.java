/**
 * Copyright (c) 2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.oncapproach;

/**
 * @author Andy Black
 *
 */
public enum ONCSyllabificationErrorType {
	EXPECTED_CODA_NOT_FOUND,
	EXPECTED_NUCLEUS_NOT_FOUND,
	EXPECTED_NUCLEUS_OR_CODA_NOT_FOUND,
	EXPECTED_ONSET_OR_NUCLEUS_NOT_FOUND,
	NATURAL_CLASS_NOT_FOUND_FOR_SEGMENT,
	ONSET_REQUIRED_BUT_SEGMENT_NOT_AN_ONSET,
}
