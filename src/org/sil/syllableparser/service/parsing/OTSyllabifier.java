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

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.cvapproach.CVNaturalClassInSyllable;
import org.sil.syllableparser.model.cvapproach.CVSegmentInSyllable;
import org.sil.syllableparser.model.cvapproach.CVSyllable;
import org.sil.syllableparser.model.cvapproach.CVTraceSyllabifierInfo;
import org.sil.syllableparser.model.npapproach.NPSegmentInSyllable;
import org.sil.syllableparser.model.npapproach.NPTracingStep;
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
	private List<Segment> activeSegmentInventory;
	private List<CVNaturalClass> activeNaturalClasses;
	private OTSegmenter segmenter;
	OTTracer tracer = null;
	private boolean fDoTrace = false;
	private OTConstraintMatcher matcher;
	private List<CVTraceSyllabifierInfo> syllabifierTraceInfo = new ArrayList<CVTraceSyllabifierInfo>();
	LinkedList<OTSyllable> syllablesInCurrentWord = new LinkedList<OTSyllable>(
			Arrays.asList(new OTSyllable(null)));
	String sSyllabifiedWord;

	public OTSyllabifier(OTApproach ota) {
		super();
		this.ota = ota;
		languageProject = ota.getLanguageProject();
		activeSegmentInventory = languageProject.getActiveSegmentsInInventory();
		activeNaturalClasses = ota.getActiveCVNaturalClasses();
		segmenter = new OTSegmenter(languageProject.getActiveGraphemes(),
				languageProject.getActiveGraphemeNaturalClasses());
		matcher = OTConstraintMatcher.getInstance();
		matcher.setLanguageProject(languageProject);
		sSyllabifiedWord = "";
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
			fSuccess = parseIntoSyllables(segmentsInWord);
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
		OTTracingStep sylInfo = new OTTracingStep();
		if (ota.getActiveOTConstraintRankings().size() == 0)
			return false;
		OTConstraintRanking ranking = ota.getActiveOTConstraintRankings().get(0);
		applyHouseKeeping(segmentsInWord);
		for (OTConstraint constraint : ranking.getRanking()) {
			applyConstraint(segmentsInWord, constraint);
			applyHouseKeeping(segmentsInWord);
			if (evalNoMore(segmentsInWord)) {
				break;
			}
		}
		sSyllabifiedWord = getSyllabificationOfCurrentWord(segmentsInWord);
		buildSyllabificationOfCurrentWord(segmentsInWord);
		if (evalNoMore(segmentsInWord)) {
			return true;
		}
		return false;
	}

	private void applyHouseKeeping(List<OTSegmentInSyllable> segmentsInWord) {
		segmentsInWord.get(0).removeCoda();
		int wordFinalPos = segmentsInWord.size()-1;
		segmentsInWord.get(wordFinalPos).removeOnset();
		// TODO: remove an onset before a coda
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
				boolean applied = false;
				OTSegmentInSyllable segInSyl = segInSyl1;
				int so = segInSyl.getStructuralOptions();
				int constraintsSO = constraint.getStructuralOptions1();
				if ((constraintsSO & OTStructuralOptions.ONSET) > 0) {
					applied = segInSyl.removeOnset();
					// record in trace
				}
				if ((constraintsSO & OTStructuralOptions.NUCLEUS) > 0) {
					applied = segInSyl.removeNuleus();
					// record in trace
				}
				if ((constraintsSO & OTStructuralOptions.CODA) > 0) {
					applied = segInSyl.removeCoda();
					// record in trace
				}
				if ((constraintsSO & OTStructuralOptions.UNPARSED) > 0) {
					applied = segInSyl.removeUnparsed();
					// record in trace
				}
			}
			i++;
		}
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

	public void buildSyllabificationOfCurrentWord(List<OTSegmentInSyllable> segmentsInWord) {
		int i = 0;
		int iSize = segmentsInWord.size();
		OTSyllable syllable = new OTSyllable();
		for (OTSegmentInSyllable segInSyl: segmentsInWord) {
			if ((i+1) < iSize) {
				if (segInSyl.getCoreOptionsLeft() != 1) {
					// parsing failed; stop here
					break;
				}
				syllable.getSegmentsInSyllable().add(segInSyl);
				OTSegmentInSyllable segInSyl2 = segmentsInWord.get(++i);
				if (isSyllableBreak(segInSyl, segInSyl2)) {
					syllablesInCurrentWord.add(syllable);
					syllable = new OTSyllable();
				}
			} else {
				syllable.getSegmentsInSyllable().add(segInSyl);
			}
		}
		syllablesInCurrentWord.add(syllable);
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
