/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

// Information used in tracing a word parse for the NP Approach

package org.sil.syllableparser.model.npapproach;

import org.sil.syllableparser.model.TraceInfo;
import org.sil.syllableparser.service.parsing.CVSegmenter;
import org.sil.syllableparser.service.parsing.NPSyllabifier;
import org.sil.syllableparser.service.parsing.NPSyllabifierResult;

public class NPTraceInfo extends TraceInfo {

	NPSyllabifier syllabifier;
	NPSyllabifierResult syllabifierResult;

	public NPTraceInfo(String sWord) {
		super(sWord);
		this.sWord = sWord;
	}

	public NPTraceInfo(String sWord, CVSegmenter segmenter, NPSyllabifier syllabifier2) {
		super(sWord, segmenter);
		this.syllabifier = syllabifier2;
	}

	public NPSyllabifier getSyllabifier() {
		return syllabifier;
	}

	public void setSyllabifier(NPSyllabifier syllabifier2) {
		this.syllabifier = syllabifier2;
	}

	public NPSyllabifierResult getSyllabifierResult() {
		return syllabifierResult;
	}

	public void setSyllabifierResult(NPSyllabifierResult syllabifierResult) {
		this.syllabifierResult = syllabifierResult;
	}
}
