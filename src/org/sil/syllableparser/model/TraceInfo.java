/**
 * Copyright (c) 2016-2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

// Information used in tracing a word parse for the CV Approach

package org.sil.syllableparser.model;

import org.sil.syllableparser.service.parsing.CVSegmenter;
import org.sil.syllableparser.service.parsing.CVSegmenterResult;

public abstract class TraceInfo {
	
	protected CVSegmenter segmenter;
	protected String sWord;
	protected CVSegmenterResult segmenterResult;
	
	public TraceInfo(String sWord) {
		this.sWord = sWord;
	}

	public TraceInfo(String sWord, CVSegmenter segmenter) {
		super();
		this.segmenter = segmenter;
		this.sWord = sWord;
	}

	public String getWord() {
		return sWord;
	}

	public void setWord(String sWord) {
		this.sWord = sWord;
	}

	public CVSegmenter getSegmenter() {
		return segmenter;
	}

	public void setSegmenter(CVSegmenter segmenter) {
		this.segmenter = segmenter;
	}

	public CVSegmenterResult getSegmenterResult() {
		return segmenterResult;
	}

	public void setSegmenterResult(CVSegmenterResult segmenterResult) {
		this.segmenterResult = segmenterResult;
	}
}
