/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.parsing;

import java.util.ArrayList;
import java.util.List;

import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.npapproach.NPRule;
import org.sil.syllableparser.model.npapproach.NPSegmentInSyllable;
import org.sil.syllableparser.model.npapproach.NPSyllabificationStatus;
import org.sil.syllableparser.model.npapproach.NPSyllable;
import org.sil.syllableparser.model.npapproach.NPTracingStep;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

/**
 * @author Andy Black
 *
 *         Singleton pattern
 *
 */
public class NPTracer {

	private static NPTracer instance;
	private boolean tracing = false;
	NPTracingStep tracingStep = null;
	private List<NPTracingStep> tracingSteps = new ArrayList<NPTracingStep>();
	
	public static NPTracer getInstance() {
		if (instance == null) {
			instance = new NPTracer();
		}
		return instance;
	}
	
	public NPTracer() {
		tracingStep = new NPTracingStep();
	}

	public boolean isTracing() {
		return tracing;
	}

	public void setTracing(boolean tracing) {
		this.tracing = tracing;
	}

	public NPTracingStep getTracingStep() {
		return tracingStep;
	}

	public void setTracingStep(NPTracingStep tracingStep) {
		this.tracingStep = tracingStep;
	}

	public List<NPTracingStep> getTracingSteps() {
		return tracingSteps;
	}

	public void resetSteps() {
		tracingSteps.clear();
	}

	public void initStep(NPSyllabificationStatus status, Filter filter) {
		if (tracing) {
			tracingStep.setStatus(status);
			tracingStep.setFilterUsed(filter);
			tracingStep.setSuccessful(false);
		}
	}

	public void initStep(Segment seg1, SHNaturalClass nc1, Segment seg2, SHNaturalClass nc2,
			SHComparisonResult result, NPSyllabificationStatus status) {
		if (tracing) {
			tracingStep.setSegment1(seg1);
			tracingStep.setNaturalClass1(nc1);
			tracingStep.setSegment2(seg2);
			tracingStep.setNaturalClass2(nc2);
			tracingStep.setComparisonResult(result);
			tracingStep.setStatus(status);
		}
	}

	public void setRule(NPRule rule) {
		if (tracing) {
			tracingStep.setRule(rule);
		}
	}

	public void setSegment1(Segment seg) {
		if (tracing) {
			tracingStep.setSegment1(seg);
		}
	}

	public void setSegment2(Segment seg) {
		if (tracing) {
			tracingStep.setSegment2(seg);
		}
	}

	public void setNaturalClass1(SHNaturalClass cls) {
		if (tracing) {
			tracingStep.setNaturalClass1(cls);
		}
	}

	public void setNaturalClass2(SHNaturalClass cls) {
		if (tracing) {
			tracingStep.setNaturalClass2(cls);
		}
	}

	public void setSHComparisonResult(SHComparisonResult result) {
		if (tracing) {
			tracingStep.setComparisonResult(result);
		}
	}

	public void setSegmentsInWord(List<NPSegmentInSyllable> segmentsInWord) {
		if (tracing) {
			List<NPSegmentInSyllable> segsInWordNow = new ArrayList<NPSegmentInSyllable>(segmentsInWord.size());
			NPSyllable currentSyl = null;
			NPSyllable lastNewSyl = null;
			NPSyllable lastCurrentSyl = null;
			for (NPSegmentInSyllable sis : segmentsInWord) {
				currentSyl = sis.getSyllable();
				NPSegmentInSyllable sisNow = sis.clone();
				if (lastCurrentSyl != currentSyl) {
					lastCurrentSyl = currentSyl;
					lastNewSyl = sisNow.getSyllable();
				} else {
					sisNow.setSyllable(lastNewSyl);
				}
				segsInWordNow.add(sisNow);
			}
			tracingStep.setSegmentsInWord(segsInWordNow);
		}
	}

	public void setStatus(NPSyllabificationStatus status) {
		if (tracing) {
			tracingStep.setStatus(status);
		}
	}

	public void setSyllable(NPSyllable syl) {
		if (tracing) {
			tracingStep.setSyllable(syl);
		}
	}

	public void setSuccessful(boolean success) {
		if (tracing) {
			tracingStep.setSuccessful(success);
		}
	}

	public void setFilterUsed(Filter tf) {
		if (tracing) {
			tracingStep.setFilterUsed(tf);;
		}
	}

	public void setGraphemesInMatchedTemplate(String matchedGraphemes) {
		if (tracing) {
			tracingStep.setGraphemesInMatchedSyllableTemplate(matchedGraphemes);
		}
	}

	public void recordStep() {
		if (tracing) {
			tracingSteps.add(tracingStep);
			tracingStep = new NPTracingStep();
		}
	}
}
