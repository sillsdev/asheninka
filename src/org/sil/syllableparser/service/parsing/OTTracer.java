/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.parsing;

import java.util.ArrayList;
import java.util.List;

import org.sil.syllableparser.model.otapproach.OTSegmentInSyllable;
import org.sil.syllableparser.model.otapproach.OTSyllable;
import org.sil.syllableparser.model.otapproach.OTTracingStep;

/**
 * @author Andy Black
 *
 *         Singleton pattern
 *
 */
public class OTTracer {

	private static OTTracer instance;
	private boolean tracing = false;
	OTTracingStep tracingStep = null;
	private List<OTTracingStep> tracingSteps = new ArrayList<OTTracingStep>();
	
	public static OTTracer getInstance() {
		if (instance == null) {
			instance = new OTTracer();
		}
		return instance;
	}
	
	public OTTracer() {
		tracingStep = new OTTracingStep();
	}

	public boolean isTracing() {
		return tracing;
	}

	public void setTracing(boolean tracing) {
		this.tracing = tracing;
	}

	public OTTracingStep getTracingStep() {
		return tracingStep;
	}

	public void setTracingStep(OTTracingStep tracingStep) {
		this.tracingStep = tracingStep;
		tracingSteps.add(tracingStep);
	}

	public List<OTTracingStep> getTracingSteps() {
		return tracingSteps;
	}

	public void resetSteps() {
		tracingSteps.clear();
	}

	public void setSegmentsInWord(List<OTSegmentInSyllable> segmentsInWord) {
		if (tracing) {
			List<OTSegmentInSyllable> segsInWordNow = new ArrayList<OTSegmentInSyllable>(segmentsInWord.size());
			for (OTSegmentInSyllable sis : segmentsInWord) {
				OTSegmentInSyllable sisNow = sis.clone();
				segsInWordNow.add(sisNow);
			}
			tracingStep.setSegmentsInWord(segsInWordNow);
		}
	}

	public void setConstraintName(String constraintName) {
		if (tracing) {
			tracingStep.setConstraintName(constraintName);
		}
	}

	public void setFailureMessage(String failureMessage) {
		if (tracing) {
			tracingStep.setFailureMessage(failureMessage);
		}
	}

	public void setSyllable(OTSyllable syllable) {
		if (tracing) {
			tracingStep.setSyllable(syllable);
			tracingStep.setAddedAsSyllable(true);
		}
	}

	public void setSuccessful(boolean success) {
		if (tracing) {
			tracingStep.setSuccessful(success);
		}
	}

	public void recordStep() {
		if (tracing) {
			tracingSteps.add(tracingStep);
			tracingStep = new OTTracingStep();
		}
	}
}
