/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.otapproach;

import org.sil.syllableparser.model.Segment;

/**
 * @author Andy Black
 *
 */
public class OTCandidate {

	protected Segment segment;
	protected int structuralOptions;

	public OTCandidate(Segment segment, int structuralOptions) {
		super();
		this.segment = segment;
		this.structuralOptions = structuralOptions;
	}

	public Segment getSegment() {
		return segment;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
	}

	public int getStructuralOptions() {
		return structuralOptions;
	}

	public void setStructuralOptions(int structuralOptions) {
		this.structuralOptions = structuralOptions;
	}
}
