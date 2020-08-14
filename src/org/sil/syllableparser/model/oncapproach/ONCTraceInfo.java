/**
 * Copyright (c) 2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

// Information used in tracing a word parse for the CV Approach

package org.sil.syllableparser.model.oncapproach;

import org.sil.syllableparser.model.TraceInfo;
import org.sil.syllableparser.service.parsing.CVSegmenter;
import org.sil.syllableparser.service.parsing.ONCSyllabifier;
import org.sil.syllableparser.service.parsing.ONCSyllabifierResult;

public class ONCTraceInfo extends TraceInfo {

	ONCSyllabifier syllabifier;
	ONCSyllabifierResult syllabifierResult;

	public ONCTraceInfo(String sWord) {
		super(sWord);
		this.sWord = sWord;
	}

	public ONCTraceInfo(String sWord, CVSegmenter segmenter, ONCSyllabifier syllabifier2) {
		super(sWord, segmenter);
		this.syllabifier = syllabifier2;
	}

	public ONCSyllabifier getSyllabifier() {
		return syllabifier;
	}

	public void setSyllabifier(ONCSyllabifier syllabifier) {
		this.syllabifier = syllabifier;
	}

	public ONCSyllabifierResult getSyllabifierResult() {
		return syllabifierResult;
	}

	public void setSyllabifierResult(ONCSyllabifierResult syllabifierResult) {
		this.syllabifierResult = syllabifierResult;
	}
}
