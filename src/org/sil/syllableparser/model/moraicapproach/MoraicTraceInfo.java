/**
 * Copyright (c) 2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

// Information used in tracing a word parse for the CV Approach

package org.sil.syllableparser.model.moraicapproach;

import org.sil.syllableparser.model.TraceInfo;
import org.sil.syllableparser.service.parsing.CVSegmenter;
import org.sil.syllableparser.service.parsing.MoraicSyllabifier;
import org.sil.syllableparser.service.parsing.MoraicSyllabifierResult;

public class MoraicTraceInfo extends TraceInfo {

	MoraicSyllabifier syllabifier;
	MoraicSyllabifierResult syllabifierResult;

	public MoraicTraceInfo(String sWord) {
		super(sWord);
		this.sWord = sWord;
	}

	public MoraicTraceInfo(String sWord, CVSegmenter segmenter, MoraicSyllabifier syllabifier2) {
		super(sWord, segmenter);
		this.syllabifier = syllabifier2;
	}

	public MoraicSyllabifier getSyllabifier() {
		return syllabifier;
	}

	public void setSyllabifier(MoraicSyllabifier syllabifier2) {
		this.syllabifier = syllabifier2;
	}

	public MoraicSyllabifierResult getSyllabifierResult() {
		return syllabifierResult;
	}

	public void setSyllabifierResult(MoraicSyllabifierResult syllabifierResult) {
		this.syllabifierResult = syllabifierResult;
	}
}
