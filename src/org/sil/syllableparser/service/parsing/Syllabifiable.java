/**
 * Copyright (c) 2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.parsing;

import org.sil.syllableparser.model.oncapproach.ONCSyllable;
import org.sil.syllableparser.model.oncapproach.Onset;
import org.sil.syllableparser.model.oncapproach.Rime;

/**
 * @author Andy Black
 *
 */
public interface Syllabifiable {

	public String getLingTreeDescriptionOfCurrentWord();

}
