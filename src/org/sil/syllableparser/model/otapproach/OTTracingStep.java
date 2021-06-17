/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.otapproach;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Andy Black
 *
 */
public class OTTracingStep {

	private	List<OTSegmentInSyllable> segmentsInWord = new ArrayList<OTSegmentInSyllable>();
	private String constraintName;
	public static final String NULL_REPRESENTATION = "&#xa0;&#x2014";
	protected ResourceBundle bundle;
	protected boolean addedAsSyllable = false;
	protected boolean successful;
	protected OTSyllable syllable;

	public OTTracingStep() {
		super();
	}

	public OTTracingStep(String constraintName, List<OTSegmentInSyllable> segmentsInWord) {
		this.constraintName = constraintName;
		this.segmentsInWord = segmentsInWord;
	}

	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public boolean isAddedAsSyllable() {
		return addedAsSyllable;
	}

	public void setAddedAsSyllable(boolean addedAsSyllable) {
		this.addedAsSyllable = addedAsSyllable;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public List<OTSegmentInSyllable> getSegmentsInWord() {
		return segmentsInWord;
	}

	public void setSegmentsInWord(List<OTSegmentInSyllable> segmentsInWord) {
		this.segmentsInWord = new ArrayList<OTSegmentInSyllable>(segmentsInWord);
	}

	public String getConstraintName() {
		return constraintName;
	}

	public void setConstraintName(String constraintName) {
		this.constraintName = constraintName;
	}

	public OTSyllable getSyllable() {
		return syllable;
	}

	public void setSyllable(OTSyllable syllable) {
		this.syllable = syllable;
	}
}
