// Copyright (c) 2021 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model.otapproach;

import java.util.List;

import javafx.collections.FXCollections;

import org.sil.syllableparser.model.cvapproach.CVNaturalClassInSyllable;


/**
 * @author Andy Black
 *
 * A syllable in a word using the CV Patterns approach 
 *
 * A value object
 */
public class OTSyllable {

	private List<OTSegmentInSyllable> segmentsInSyllable = FXCollections.observableArrayList();

	public OTSyllable() {
		super();
	}

	public OTSyllable(List<OTSegmentInSyllable> segmentsInSyllable) {
		super();
		this.segmentsInSyllable = segmentsInSyllable;
	}

	public List<OTSegmentInSyllable> getSegmentsInSyllable() {
		return segmentsInSyllable;
	}
	
	public String getStructuralOptionsInSyllable() {
		StringBuilder sb = new StringBuilder();
		
		for (OTSegmentInSyllable nc : segmentsInSyllable) {
			sb.append(OTStructuralOptions.getStructuralOptions(nc.getStructuralOptions()));
		}	
		return sb.toString();
	}
}
