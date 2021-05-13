/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.otapproach;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andy Black
 *
 */
public class OTCandidateSet {

	protected List<OTCandidate> candidateSet = new  ArrayList<OTCandidate>();

	public OTCandidateSet(List<OTCandidate> candidateSet) {
		super();
		this.candidateSet = candidateSet;
	}

	public List<OTCandidate> getCandidateSet() {
		return candidateSet;
	}

	public void setCandidateSet(List<OTCandidate> candidateSet) {
		this.candidateSet = candidateSet;
	}
	
	public String produceCandidateGrid() {
		return "";
	}
}
