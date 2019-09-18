/**
 * Copyright (c) 2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.oncapproach;

/**
 * @author Andy Black
 *
 * A coda in a rime using the Onset-Nucleus-Coda approach 
 *
 * A value object
 *
 */
public class Coda extends ONCConstituent {

	public void createLingTreeDescription(StringBuilder sb) {
		super.createLingTreeDescription(sb, "C");
	}
	public void getONCPattern(StringBuilder sb) {
		super.getONCPattern(sb, "c");
	}
}
