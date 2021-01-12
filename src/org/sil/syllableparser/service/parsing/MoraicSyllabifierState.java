/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.parsing;

/**
 * @author Andy Black
 *
 */
public enum MoraicSyllabifierState {
	FILTER_FAILED,
	FILTER_REPAIR_APPLIED,
	MORA,
	ONSET,
	ONSET_TEMPLATE_APPLIED,
	TEMPLATE_FAILED,
	WORD_FINAL_TEMPLATE_APPLIED,
	WORD_INITIAL_TEMPLATE_APPLIED,
	UNKNOWN,
}