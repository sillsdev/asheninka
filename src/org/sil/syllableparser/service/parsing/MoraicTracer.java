/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.parsing;

import java.util.ArrayList;
import java.util.List;

import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.TemplateFilter;
import org.sil.syllableparser.model.moraicapproach.MoraicTracingStep;
import org.sil.syllableparser.model.moraicapproach.MoraicSyllabificationStatus;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;

/**
 * @author Andy Black
 *
 *         Singleton pattern
 *
 */
public class MoraicTracer {

	private static MoraicTracer instance;
	private boolean tracing = false;
	MoraicTracingStep tracingStep = null;
	private List<MoraicTracingStep> tracingSteps = new ArrayList<MoraicTracingStep>();
	
	public static MoraicTracer getInstance() {
		if (instance == null) {
			instance = new MoraicTracer();
		}
		return instance;
	}
	
	public MoraicTracer() {
		tracingStep = new MoraicTracingStep();
	}

	public boolean isTracing() {
		return tracing;
	}

	public void setTracing(boolean tracing) {
		this.tracing = tracing;
	}

	public MoraicTracingStep getTracingStep() {
		return tracingStep;
	}

	public void setTracingStep(MoraicTracingStep tracingStep) {
		this.tracingStep = tracingStep;
	}

	public List<MoraicTracingStep> getTracingSteps() {
		return tracingSteps;
	}

	public void resetSteps() {
		tracingSteps.clear();
	}

	public void initStep(MoraicSyllabificationStatus status, TemplateFilter filter) {
		if (tracing) {
			tracingStep.setStatus(status);
			tracingStep.setTemplateFilterUsed(filter);
			tracingStep.setSuccessful(false);
		}
	}

	public void initStep(Segment seg1, SHNaturalClass nc1, Segment seg2, SHNaturalClass nc2,
			SHComparisonResult result, MoraicSyllabificationStatus status) {
		if (tracing) {
			tracingStep.setSegment1(seg1);
			tracingStep.setNaturalClass1(nc1);
			tracingStep.setSegment2(seg2);
			tracingStep.setNaturalClass2(nc2);
			tracingStep.setComparisonResult(result);
			tracingStep.setStatus(status);
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

	public void setStatus(MoraicSyllabificationStatus status) {
		if (tracing) {
			tracingStep.setStatus(status);
		}
	}

	public void setSuccessful(boolean success) {
		if (tracing) {
			tracingStep.setSuccessful(success);
		}
	}

	public void setTemplateFilterUsed(TemplateFilter tf) {
		if (tracing) {
			tracingStep.setTemplateFilterUsed(tf);;
		}
	}

	public void recordStep() {
		if (tracing) {
			tracingSteps.add(tracingStep);
			tracingStep = new MoraicTracingStep();
		}
	}
}
