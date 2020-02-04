/**
 * Copyright (c) 2020 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.parsing;

import java.util.ArrayList;
import java.util.List;

import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.TemplateFilter;
import org.sil.syllableparser.model.oncapproach.ONCSyllabificationStatus;
import org.sil.syllableparser.model.oncapproach.ONCTracingStep;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;
import org.sil.syllableparser.service.LingTreeInteractor;

/**
 * @author Andy Black
 *
 *         Singleton pattern
 *
 */
public class ONCTracer {

	private static ONCTracer instance;
	private boolean tracing = false;
	ONCTracingStep tracingStep = null;
	private List<ONCTracingStep> tracingSteps = new ArrayList<ONCTracingStep>();
	
	public static ONCTracer getInstance() {
		if (instance == null) {
			instance = new ONCTracer();
		}
		return instance;
	}
	
	public ONCTracer() {
		tracingStep = new ONCTracingStep();
	}

	public boolean isTracing() {
		return tracing;
	}

	public void setTracing(boolean tracing) {
		this.tracing = tracing;
	}

	public ONCTracingStep getTracingStep() {
		return tracingStep;
	}

	public void setTracingStep(ONCTracingStep tracingStep) {
		this.tracingStep = tracingStep;
	}

	public List<ONCTracingStep> getTracingSteps() {
		return tracingSteps;
	}

	public void resetSteps() {
		tracingSteps.clear();
	}

	public void initStep(ONCType oncType, ONCSyllabificationStatus status, TemplateFilter filter) {
		if (tracing) {
			tracingStep.setOncType(oncType);
			tracingStep.setStatus(status);
			tracingStep.setTemplateFilterUsed(filter);
			tracingStep.setSuccessful(false);
		}
	}

	public void initStep(Segment seg1, SHNaturalClass nc1, Segment seg2, SHNaturalClass nc2,
			SHComparisonResult result, ONCSyllabificationStatus status, ONCType oncType) {
		if (tracing) {
			tracingStep.setSegment1(seg1);
			tracingStep.setNaturalClass1(nc1);
			tracingStep.setSegment2(seg2);
			tracingStep.setNaturalClass2(nc2);
			tracingStep.setComparisonResult(result);
			tracingStep.setStatus(status);
			tracingStep.setOncType(oncType);
		}
	}

	public void setSegment1(Segment seg) {
		if (tracing) {
			tracingStep.setSegment1(seg);
		}
	}

	public void setStatus(ONCSyllabificationStatus status) {
		if (tracing) {
			tracingStep.setStatus(status);
		}
	}

	public void setOncType(ONCType oncType) {
		if (tracing) {
			tracingStep.setOncType(oncType);
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
			tracingStep = new ONCTracingStep();
		}
	}
}
