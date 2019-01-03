/**
 * Copyright (c) 2016-2017 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

// Information used in tracing a word parse for the CV Approach

package org.sil.syllableparser.model.sonorityhierarchyapproach;

import org.sil.syllableparser.service.CVNaturalClasser;
import org.sil.syllableparser.service.CVNaturalClasserResult;
import org.sil.syllableparser.service.CVSegmenter;
import org.sil.syllableparser.service.CVSegmenterResult;
import org.sil.syllableparser.service.CVSyllabifier;
import org.sil.syllableparser.service.CVSyllabifierResult;

public class SHTraceInfo {
	
	CVSegmenter segmenter;
	CVNaturalClasser naturalClasser;
	CVSyllabifier syllabifier;
	String sWord;
	CVSegmenterResult segmenterResult;
	CVNaturalClasserResult naturalClasserResult;
	CVSyllabifierResult syllabifierResult;
	
	public SHTraceInfo(String sWord) {
		this.sWord = sWord;
	}

	public SHTraceInfo(String sWord, CVSegmenter segmenter,
			CVNaturalClasser naturalClasser, CVSyllabifier syllabifier) {
		super();
		this.segmenter = segmenter;
		this.naturalClasser = naturalClasser;
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

	public CVNaturalClasser getNaturalClasser() {
		return naturalClasser;
	}

	public void setNaturalClasser(CVNaturalClasser naturalClasser) {
		this.naturalClasser = naturalClasser;
	}

	public CVSyllabifier getSyllabifier() {
		return syllabifier;
	}

	public void setSyllabifier(CVSyllabifier syllabifier) {
		this.syllabifier = syllabifier;
	}

	public CVSegmenterResult getSegmenterResult() {
		return segmenterResult;
	}

	public void setSegmenterResult(CVSegmenterResult segmenterResult) {
		this.segmenterResult = segmenterResult;
	}

	public CVNaturalClasserResult getNaturalClasserResult() {
		return naturalClasserResult;
	}

	public void setNaturalClasserResult(CVNaturalClasserResult naturalClasserResult) {
		this.naturalClasserResult = naturalClasserResult;
	}

	public CVSyllabifierResult getSyllabifierResult() {
		return syllabifierResult;
	}

	public void setSyllabifierResult(CVSyllabifierResult syllabifierResult) {
		this.syllabifierResult = syllabifierResult;
	}

}
