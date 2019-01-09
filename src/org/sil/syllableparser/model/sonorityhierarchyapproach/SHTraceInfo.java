/**
 * Copyright (c) 2018-2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

// Information used in tracing a word parse for the CV Approach

package org.sil.syllableparser.model.sonorityhierarchyapproach;

import org.sil.syllableparser.service.CVSegmenter;
import org.sil.syllableparser.service.CVSegmenterResult;
import org.sil.syllableparser.service.SHSyllabifier;
import org.sil.syllableparser.service.SHSyllabifierResult;

public class SHTraceInfo {

	CVSegmenter segmenter;
	SHSyllabifier syllabifier;
	String sWord;
	CVSegmenterResult segmenterResult;
	SHSyllabifierResult syllabifierResult;

	public SHTraceInfo(String sWord) {
		this.sWord = sWord;
	}

	public SHTraceInfo(String sWord, CVSegmenter segmenter, SHSyllabifier syllabifier) {
		super();
		this.segmenter = segmenter;
		this.syllabifier = syllabifier;
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

	public SHSyllabifier getSyllabifier() {
		return syllabifier;
	}

	public void setSyllabifier(SHSyllabifier syllabifier) {
		this.syllabifier = syllabifier;
	}

	public CVSegmenterResult getSegmenterResult() {
		return segmenterResult;
	}

	public void setSegmenterResult(CVSegmenterResult segmenterResult) {
		this.segmenterResult = segmenterResult;
	}

	public SHSyllabifierResult getSyllabifierResult() {
		return syllabifierResult;
	}

	public void setSyllabifierResult(SHSyllabifierResult syllabifierResult) {
		this.syllabifierResult = syllabifierResult;
	}

}
