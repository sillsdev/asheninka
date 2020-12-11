/**
 * Copyright (c) 2018-2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

// Information used in tracing a word parse for the CV Approach

package org.sil.syllableparser.model.sonorityhierarchyapproach;

import org.sil.syllableparser.model.TraceInfo;
import org.sil.syllableparser.service.parsing.CVSegmenter;
import org.sil.syllableparser.service.parsing.SHSyllabifier;
import org.sil.syllableparser.service.parsing.SHSyllabifierResult;

public class SHTraceInfo extends TraceInfo {

	SHSyllabifier syllabifier;
	SHSyllabifierResult syllabifierResult;

	public SHTraceInfo(String sWord) {
		super(sWord);
		this.sWord = sWord;
	}

	public SHTraceInfo(String sWord, CVSegmenter segmenter, SHSyllabifier syllabifier) {
		super(sWord, segmenter);
		this.syllabifier = syllabifier;
	}

	public SHSyllabifier getSyllabifier() {
		return syllabifier;
	}

	public void setSyllabifier(SHSyllabifier syllabifier) {
		this.syllabifier = syllabifier;
	}

	public SHSyllabifierResult getSyllabifierResult() {
		return syllabifierResult;
	}

	public void setSyllabifierResult(SHSyllabifierResult syllabifierResult) {
		this.syllabifierResult = syllabifierResult;
	}
}
