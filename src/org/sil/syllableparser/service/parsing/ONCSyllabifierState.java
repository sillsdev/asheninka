/**
 * Copyright (c) 2019-2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.parsing;

/**
 * @author Andy Black
 *
 */
public enum ONCSyllabifierState {
	CODA,
	FILTER_FAILED,
	FILTER_REPAIR_APPLIED,
	NUCLEUS,
	NUCLEUS_OR_CODA,
	ONSET,
	CODA_OR_ONSET,
	ONSET_OR_NUCLEUS,
	ONSET_TEMPLATE_APPLIED,
	TEMPLATE_FAILED,
	WORD_FINAL_TEMPLATE_APPLIED,
	WORD_INITIAL_TEMPLATE_APPLIED,
	UNKNOWN
}