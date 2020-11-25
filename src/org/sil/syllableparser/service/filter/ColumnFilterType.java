/**
 * Copyright (c) 2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.filter;

/**
 * @author Andy Black
 *
 */
public enum ColumnFilterType {
		ANYWHERE,
		AT_END,
		AT_START,
		BLANKS,
		NON_BLANKS,
		REGULAR_EXPRESSION,
		WHOLE_ITEM,
}
