/**
 * Copyright (c) 2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.oncapproach;

/**
 * @author Andy Black
 *
 * An onset in a syllable using the Onset-Nucleus-Coda approach 
 *
 * A value object
 *
 */
public class Onset extends ONCConstituent {
	
	public void createLingTreeDescription(StringBuilder sb) {
		super.createLingTreeDescription(sb, "O");
	}
	
	public void getONCPattern(StringBuilder sb) {
		super.getONCPattern(sb, "o");
	}
}
