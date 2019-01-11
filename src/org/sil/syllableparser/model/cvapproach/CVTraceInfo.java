/**
 * Copyright (c) 2016-2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

// Information used in tracing a word parse for the CV Approach

package org.sil.syllableparser.model.cvapproach;

import org.sil.syllableparser.model.TraceInfo;
import org.sil.syllableparser.service.CVNaturalClasser;
import org.sil.syllableparser.service.CVNaturalClasserResult;
import org.sil.syllableparser.service.CVSegmenter;
import org.sil.syllableparser.service.CVSyllabifier;
import org.sil.syllableparser.service.CVSyllabifierResult;

public class CVTraceInfo extends TraceInfo {
	
	CVNaturalClasser naturalClasser;
	CVSyllabifier syllabifier;
	CVNaturalClasserResult naturalClasserResult;
	CVSyllabifierResult syllabifierResult;
	
	public CVTraceInfo(String sWord) {
		super(sWord);
		this.sWord = sWord;
	}

	public CVTraceInfo(String sWord, CVSegmenter segmenter,
			CVNaturalClasser naturalClasser, CVSyllabifier syllabifier) {
		super(sWord, segmenter);
		this.naturalClasser = naturalClasser;
		this.syllabifier = syllabifier;
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
