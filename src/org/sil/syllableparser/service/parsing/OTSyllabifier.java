// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.cvapproach.CVTraceSyllabifierInfo;
import org.sil.syllableparser.model.otapproach.OTApproach;
import org.sil.syllableparser.model.otapproach.OTConstraint;
import org.sil.syllableparser.model.otapproach.OTConstraintRanking;
import org.sil.syllableparser.model.otapproach.OTSegmentInSyllable;
import org.sil.syllableparser.model.otapproach.OTStructuralOptions;
import org.sil.syllableparser.model.otapproach.OTSyllable;
import org.sil.syllableparser.model.otapproach.OTTracingStep;
import org.sil.syllableparser.service.OTConstraintMatcher;

/**
 * @author Andy Black
 *
 *         a Service Takes a sequence of natural classes and parses them into a
 *         sequence of syllables
 */
public class OTSyllabifier implements Syllabifiable {

	private LanguageProject languageProject;
	private OTApproach ota;
	private OTSegmenter segmenter;
	OTTracer tracer = null;
	private boolean fDoTrace = false;
	private OTConstraintMatcher matcher;
	private List<CVTraceSyllabifierInfo> syllabifierTraceInfo = new ArrayList<CVTraceSyllabifierInfo>();
	LinkedList<OTSyllable> syllablesInCurrentWord = new LinkedList<OTSyllable>(
			Arrays.asList(new OTSyllable(null)));
	String sSyllabifiedWord;
	ResourceBundle bundle;

	public OTSyllabifier(OTApproach ota) {
		super();
		this.ota = ota;
		languageProject = ota.getLanguageProject();
		segmenter = new OTSegmenter(languageProject.getActiveGraphemes(),
				languageProject.getActiveGraphemeNaturalClasses());
		matcher = OTConstraintMatcher.getInstance();
		matcher.setLanguageProject(languageProject);
		sSyllabifiedWord = "";
		tracer = OTTracer.getInstance();
	}

	public List<OTSyllable> getSyllablesInCurrentWord() {
		return syllablesInCurrentWord;
	}

	public void setSyllablesInCurrentWord(LinkedList<OTSyllable> syllablesInCurrentWord) {
		this.syllablesInCurrentWord = syllablesInCurrentWord;
	}

	public List<CVTraceSyllabifierInfo> getSyllabifierTraceInfo() {
		return syllabifierTraceInfo;
	}

	public List<OTTracingStep> getTracingSteps() {
		tracer = OTTracer.getInstance();
		return tracer.getTracingSteps();
	}


	public boolean isDoTrace() {
		return fDoTrace;
	}

	public void setDoTrace(boolean fDoTrace) {
		this.fDoTrace = fDoTrace;
	}

	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public String getStructuralOptionsInParse() {
		return "";
	}
	
	public boolean convertStringToSyllables(String word) {
		syllablesInCurrentWord.clear();
		syllabifierTraceInfo.clear();
		sSyllabifiedWord = "";
		boolean fSuccess = false;
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		fSuccess = segResult.success;
		if (fSuccess) {
			List<OTSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
			fSuccess = syllabify(segmentsInWord);
		}
		return fSuccess;
	}

	public boolean syllabify(List<OTSegmentInSyllable> segsInWord) {
		syllablesInCurrentWord.clear();
		syllabifierTraceInfo.clear();
		tracer = OTTracer.getInstance();
		tracer.resetSteps();
		tracer.setTracing(fDoTrace);
		boolean fSuccess = parseIntoSyllables(segsInWord);
		return fSuccess;
	}
	
	public boolean parseIntoSyllables(List<OTSegmentInSyllable> segmentsInWord) {
		sSyllabifiedWord = "";
		rememberSyllabificationStateInTracer(bundle.getString("report.tawotinitialization"), segmentsInWord);
		if (ota.getActiveOTConstraintRankings().size() == 0) {
			tracer.setSuccessful(false);
			tracer.setFailureMessage(bundle.getString("report.tawotnoconstraints"));
			tracer.recordStep();
			return false;
		}
		OTConstraintRanking ranking = ota.getActiveOTConstraintRankings().get(0);
		applyHouseKeeping(segmentsInWord);
		for (OTConstraint constraint : ranking.getRanking()) {
			applyConstraint(segmentsInWord, constraint);
			//TODO: do we need the onset before a coda part of housekeeping?
			// If so, do we remove the onset or the coda or both?
			//applyHouseKeeping(segmentsInWord);
			if (evalNoMore(segmentsInWord)) {
				break;
			}
		}
		boolean buildWasGood = buildSyllabificationOfCurrentWord(segmentsInWord);
		sSyllabifiedWord = getSyllabificationOfCurrentWord(segmentsInWord);
		if (buildWasGood && evalNoMore(segmentsInWord)) {
			if (allSegmentsParsed(segmentsInWord)) {
				tracer.setSuccessful(true);
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	private boolean allSegmentsParsed(List<OTSegmentInSyllable> segmentsInWord) {
		for (OTSegmentInSyllable sis : segmentsInWord) {
			if (sis.isUnparsed()) {
				return false;
			}
		}
		return true;
	}

	private void applyHouseKeeping(List<OTSegmentInSyllable> segmentsInWord) {
		segmentsInWord.get(0).removeCoda();
		int wordFinalPos = segmentsInWord.size()-1;
		segmentsInWord.get(wordFinalPos).removeOnset();
		// TODO: remove an onset before a coda
		rememberSyllabificationStateInTracer(bundle.getString("report.tawothousekeeping"), segmentsInWord);
	}
	
	private void applyConstraint(List<OTSegmentInSyllable> segmentsInWord, OTConstraint constraint) {
		int i = 0;
		int iSize = segmentsInWord.size();
		for (OTSegmentInSyllable segInSyl1 : segmentsInWord) {
			OTSegmentInSyllable segInSyl2 = null;
			if ((i+1) < iSize) {
				segInSyl2 = segmentsInWord.get(i+1);
			}
			if (matcher.match(constraint, segInSyl1, segInSyl2, iSize - i)) {
				OTSegmentInSyllable segInSyl = segInSyl1;
				if (constraint.isPruneElement2()) {
					segInSyl = segInSyl2;
				}
				if (segInSyl.getCoreOptionsLeft() == 1) {
					// nothing to do
				i++;
					continue;
				}
				int constraintsSO = constraint.getStructuralOptions1();
				if (constraintsSO == segInSyl.getStructuralOptions()) {
					// we'll remove what is left; not possible so try next match
					i++;
					continue;
				}
				if ((constraintsSO & OTStructuralOptions.ONSET) > 0) {
					segInSyl.removeOnset();
				}
				if ((constraintsSO & OTStructuralOptions.NUCLEUS) > 0) {
					 segInSyl.removeNuleus();
				}
				if ((constraintsSO & OTStructuralOptions.CODA) > 0) {
					segInSyl.removeCoda();
				}
				if ((constraintsSO & OTStructuralOptions.UNPARSED) > 0) {
					segInSyl.removeUnparsed();
				}
			}
			i++;
		}
		rememberSyllabificationStateInTracer(constraint.getConstraintName(), segmentsInWord);
	}

	private boolean evalNoMore(List<OTSegmentInSyllable> segmentsInWord) {
		for (OTSegmentInSyllable segInSyl: segmentsInWord) {
			if (segInSyl.getCoreOptionsLeft() > 1) {
				return false;
			}
		}
		return true;
	}

	public String getSyllabificationOfCurrentWord() {
		return sSyllabifiedWord;
	}
	
	public String getSyllabificationOfCurrentWord(List<OTSegmentInSyllable> segmentsInWord) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		int iSize = segmentsInWord.size();
		for (OTSegmentInSyllable segInSyl: segmentsInWord) {
			if ((i+1) < iSize) {
				if (segInSyl.getCoreOptionsLeft() != 1) {
					// parsing failed; stop here
					break;
				}
				sb.append(segInSyl.getGrapheme());
				OTSegmentInSyllable segInSyl2 = segmentsInWord.get(++i);
				if (isSyllableBreak(segInSyl, segInSyl2)) {
					sb.append(".");
				}
			} else {
				sb.append(segInSyl.getGrapheme());
			}
		}
		return sb.toString();
	}

	private boolean isSyllableBreak(OTSegmentInSyllable segInSyl1, OTSegmentInSyllable segInSyl2) {
		if (segInSyl2 == null) {
			return false;
		}
		boolean result = false;
		if (segInSyl1.isCoda() && segInSyl2.isOnset()) {
			result = true;;
		}
		if (segInSyl1.isCoda() && segInSyl2.isNucleus()) {
			result = true;;
		}
		if (segInSyl1.isNucleus() && segInSyl2.isOnset()) {
			result = true;;
		}
		if (segInSyl1.isNucleus() && segInSyl2.isNucleus()) {
			result = true;;
		}
		return result;
	}

	protected void rememberSyllabificationStateInTracer(String constraintName, List<OTSegmentInSyllable> segmentsInWord) {
		tracer.setConstraintName(constraintName);
		tracer.setSegmentsInWord(segmentsInWord);
		tracer.recordStep();
	}

	public boolean buildSyllabificationOfCurrentWord(List<OTSegmentInSyllable> segmentsInWord) {
		int i = 0;
		int iSize = segmentsInWord.size();
		OTSyllable syllable = new OTSyllable();
		for (OTSegmentInSyllable segInSyl: segmentsInWord) {
			if (segInSyl.getCoreOptionsLeft() != 1) {
				tracer.setSuccessful(false);
				tracer.setFailureMessage(bundle.getString("report.tawotsomesegmentsareambiguous"));
				tracer.recordStep();
				return false;
			}
			if (segInSyl.isUnparsed()) {
				tracer.setSuccessful(false);
				tracer.setFailureMessage(bundle.getString("report.tawotunparsedsegments"));
				tracer.recordStep();
				return false;
			}
			if ((i + 1) < iSize) {
				syllable.getSegmentsInSyllable().add(segInSyl);
				OTSegmentInSyllable segInSyl2 = segmentsInWord.get(++i);
				if (isSyllableBreak(segInSyl, segInSyl2)) {
					if (syllableHasNucleus(syllable)) {
						syllablesInCurrentWord.add(syllable);
						tracer.setSyllable(syllable);
						tracer.setSuccessful(true);
						tracer.recordStep();
						syllable = new OTSyllable();
					} else {
						tracer.setSuccessful(false);
						tracer.setFailureMessage(bundle.getString("report.tawotnonucleusinsyllable"));
						tracer.recordStep();
						return false;
					}
				}
			} else {
				syllable.getSegmentsInSyllable().add(segInSyl);
				tracer.setSyllable(syllable);
				tracer.recordStep();
			}
		}
		if (syllable.getSegmentsInSyllable().size() > 0) {
			syllablesInCurrentWord.add(syllable);
			int lastStepIndex = tracer.getTracingSteps().size() - 1;
			if (lastStepIndex >= 0) {
				OTTracingStep lastStep = tracer.getTracingSteps().get(lastStepIndex);
				if (!syllableHasNucleus(syllable)) {
					lastStep.setSuccessful(false);
					lastStep.setFailureMessage(bundle.getString("report.tawotnonucleusinsyllable"));
					return false;
				} else {
					lastStep.setSuccessful(true);
				}
				tracer.getTracingSteps().set(lastStepIndex, lastStep);
			} else {
				if (!syllableHasNucleus(syllable)) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean syllableHasNucleus(OTSyllable syllable) {
		for (OTSegmentInSyllable sis : syllable.getSegmentsInSyllable()) {
			if ((sis.getStructuralOptions() & OTStructuralOptions.NUCLEUS) > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getLingTreeDescriptionOfCurrentWord() {
		StringBuilder sb = new StringBuilder();
		sb.append("(W");
		for (OTSyllable syl : syllablesInCurrentWord) {
			sb.append("(");
			sb.append(Constants.SYLLABLE_SYMBOL);
			for (OTSegmentInSyllable segInSyl: syl.getSegmentsInSyllable()) {
				sb.append("( ");
				sb.append(segInSyl.getOptionsInSegment());
				sb.append("(\\L ");
				sb.append(segInSyl.getSegmentName());
				sb.append("(\\G ");
				sb.append(segInSyl.getGrapheme());
				sb.append(")))");
			}
			sb.append(")");
		}
		sb.append(")");
		return sb.toString();
	}

}
