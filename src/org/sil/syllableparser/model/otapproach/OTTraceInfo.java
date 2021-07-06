/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

// Information used in tracing a word parse for the CV Approach

package org.sil.syllableparser.model.otapproach;

import org.sil.syllableparser.model.TraceInfo;
import org.sil.syllableparser.service.parsing.CVSegmenter;
import org.sil.syllableparser.service.parsing.OTSyllabifier;
import org.sil.syllableparser.service.parsing.OTSyllabifierResult;

public class OTTraceInfo extends TraceInfo {
	
	OTSyllabifier syllabifier;
	OTSyllabifierResult syllabifierResult;
	
	public OTTraceInfo(String sWord) {
		super(sWord);
		this.sWord = sWord;
	}

	public OTTraceInfo(String sWord, CVSegmenter segmenter,
			OTSyllabifier syllabifier) {
		super(sWord, segmenter);
		this.syllabifier = syllabifier;
	}

	public OTSyllabifier getSyllabifier() {
		return syllabifier;
	}

	public void setSyllabifier(OTSyllabifier syllabifier) {
		this.syllabifier = syllabifier;
	}

	public OTSyllabifierResult getSyllabifierResult() {
		return syllabifierResult;
	}

	public void setSyllabifierResult(OTSyllabifierResult syllabifierResult) {
		this.syllabifierResult = syllabifierResult;
	}
}
