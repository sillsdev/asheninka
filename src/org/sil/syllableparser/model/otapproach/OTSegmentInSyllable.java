// Copyright (c) 2021 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model.otapproach;

import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;


/**
 * @author Andy Black
 *
 * A segment which occurs in a syllable using the CV Patterns approach 
 *
 * A value object
 */

public class OTSegmentInSyllable extends CVSegmentInSyllable {
	protected int structuralOptions;
	protected int coreOptionsLeft;
	

	public OTSegmentInSyllable(Segment segment, String grapheme) {
		super(segment, grapheme);
		this.structuralOptions = OTStructuralOptions.COMBO_O_N_C_U;
		coreOptionsLeft = 4;
	}

	public int getStructuralOptions() {
		return structuralOptions;
	}

	public void setStructuralOptions(int structuralOptions) {
		this.structuralOptions = structuralOptions;
	}

	public int getCoreOptionsLeft() {
		return coreOptionsLeft;
	}

	public boolean removeCoda() {
		return removeCoreOption(OTStructuralOptions.REMOVE_CODA);
	}
	
	public boolean removeNuleus() {
		return removeCoreOption(OTStructuralOptions.REMOVE_NUCLEUS);
	}
	
	public boolean removeOnset() {
		return removeCoreOption(OTStructuralOptions.REMOVE_ONSET);
	}

	public boolean removeUnparsed() {
		return removeCoreOption(OTStructuralOptions.REMOVE_UNPARSED);
	}
	
	protected boolean removeCoreOption(int optionToRemove) {
		boolean result = false;
		int before = structuralOptions;
		int after = structuralOptions & optionToRemove;
		if (after != before) {
			if (coreOptionsLeft > 1) {
				--coreOptionsLeft;
				structuralOptions = after;
				result = true;
			}
		}
		return result;
	}

	public boolean hasOnset() {
		if ((structuralOptions & OTStructuralOptions.ONSET) > 0) {
			return true;
		}
		return false;
	}

	public boolean hasNucleus() {
		if ((structuralOptions & OTStructuralOptions.NUCLEUS) > 0) {
			return true;
		}
		return false;
	}

	public boolean hasCoda() {
		if ((structuralOptions & OTStructuralOptions.CODA) > 0) {
			return true;
		}
		return false;
	}

	public boolean hasUnparsed() {
		if ((structuralOptions & OTStructuralOptions.UNPARSED) > 0) {
			return true;
		}
		return false;
	}

	public boolean isOnset() {
		if (coreOptionsLeft == 1 && (structuralOptions & OTStructuralOptions.ONSET) > 0) {
			return true;
		}
		return false;
	}
	
	public boolean isNucleus() {
		if (coreOptionsLeft == 1 && (structuralOptions & OTStructuralOptions.NUCLEUS) > 0) {
			return true;
		}
		return false;
	}
	
	public boolean isCoda() {
		if (coreOptionsLeft == 1 && (structuralOptions & OTStructuralOptions.CODA) > 0) {
			return true;
		}
		return false;
	}
	
	public boolean isUnparsed() {
		if (coreOptionsLeft == 1 && (structuralOptions & OTStructuralOptions.UNPARSED) > 0) {
			return true;
		}
		return false;
	}
	
	public String getOptionsInSegment() {
		StringBuilder sb = new StringBuilder();
		sb.append(OTStructuralOptions.getStructuralOptions(structuralOptions));
		return sb.toString();
	}

	@Override
	public OTSegmentInSyllable clone() {
		OTSegmentInSyllable sis = new OTSegmentInSyllable(segment, grapheme);
		sis.setStructuralOptions(structuralOptions);
		return sis;
	}
}
