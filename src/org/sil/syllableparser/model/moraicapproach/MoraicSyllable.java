// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model.moraicapproach;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javafx.collections.FXCollections;

import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.service.parsing.SHSonorityComparer;


/**
 * @author Andy Black
 *
 * A syllable in a word using the Onset-Nucleus-Coda approach 
 *
 * A value object
 */
public class MoraicSyllable extends MoraicConstituent {

	private final List<MoraicSegmentInSyllable> segmentsInSyllable;
	private List<MoraicSegmentInSyllable> onset = FXCollections.observableArrayList();
	private List<Mora> moras = FXCollections.observableArrayList();

	public List<MoraicSegmentInSyllable> getGraphemes() {
		return segmentsInSyllable;
	}

	public MoraicSyllable(ArrayList<MoraicSegmentInSyllable> arrayList) {
		super();
		this.segmentsInSyllable = arrayList;
	}

	public List<MoraicSegmentInSyllable> getSegmentsInSyllable() {
		return segmentsInSyllable;
	}
	
	public List<MoraicSegmentInSyllable> getOnset() {
		return onset;
	}

	public void setOnset(List<MoraicSegmentInSyllable> onset) {
		this.onset = onset;
	}

	public List<Mora> getMoras() {
		return moras;
	}

	public void setMoras(List<Mora> moras) {
		this.moras = moras;
	}

	public void add(MoraicSegmentInSyllable seg) {
		segmentsInSyllable.add(seg);
	}
	
	public String getSegmentNamesInSyllable() {
		StringBuilder sb = new StringBuilder();
		
		for (MoraicSegmentInSyllable seg : segmentsInSyllable) {
			sb.append(seg.getSegmentName());
		}	
		return sb.toString();
	}

	public void applyAnyRepairFilters(List<MoraicSegmentInSyllable> segmentsInWord, int iSegmentInWord,
			MoraicSyllable syl, LinkedList<MoraicSyllable> syllablesInCurrentWord, SHSonorityComparer sonorityComparer, SHComparisonResult sspComparisonNeeded) {
		// TODO: flesh out
	}

}
