// Copyright (c) 2021 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.npapproach.NPApproach;
import org.sil.syllableparser.model.npapproach.NPNodeInSyllable;
import org.sil.syllableparser.model.npapproach.NPNodeLevel;
import org.sil.syllableparser.model.npapproach.NPRule;
import org.sil.syllableparser.model.npapproach.NPRuleAction;
import org.sil.syllableparser.model.npapproach.NPRuleLevel;
import org.sil.syllableparser.model.npapproach.NPSegmentInSyllable;
import org.sil.syllableparser.model.npapproach.NPSyllable;
import org.sil.syllableparser.model.npapproach.NPTracingStep;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHNaturalClass;
import org.sil.syllableparser.service.NPRuleMatcher;

/**
 * @author Andy Black
 *
 *         a Service Takes a sequence of natural classes and parses them into a
 *         sequence of syllables
 */
public class NPSyllabifier implements Syllabifiable {

	private LanguageProject languageProject;
	private NPApproach npApproach;
	private NPSegmenter segmenter;
	private SHSonorityComparer sonorityComparer;
	NPTracer tracer = null;
	private boolean fDoTrace = false;
	private OnsetPrincipleType onsetType = OnsetPrincipleType.ALL_BUT_FIRST_HAS_ONSET;
	private List<NPTracingStep> syllabifierTraceInfoList = new ArrayList<NPTracingStep>();

	LinkedList<NPSyllable> syllablesInCurrentWord = new LinkedList<NPSyllable>(
			Arrays.asList(new NPSyllable(null, null)));
	List<NPSegmentInSyllable> segmentsInWord;
	String sSyllabifiedWord;
	String sLevelNDoubleBar = "N''";
	String sLevelNBar = "N'";
	String sLevelN = "N";

	public NPSyllabifier(NPApproach npApproach) {
		super();
		this.npApproach = npApproach;
		languageProject = npApproach.getLanguageProject();
		onsetType = languageProject.getSyllabificationParameters().getOnsetPrincipleEnum();
		segmenter = new NPSegmenter(languageProject.getActiveGraphemes(),
				languageProject.getActiveGraphemeNaturalClasses());
		sonorityComparer = new SHSonorityComparer(npApproach.getLanguageProject());
		sSyllabifiedWord = "";
	}

	public List<NPSyllable> getSyllablesInCurrentWord() {
		return syllablesInCurrentWord;
	}

	public void setSyllablesInCurrentWord(LinkedList<NPSyllable> syllablesInCurrentWord) {
		this.syllablesInCurrentWord = syllablesInCurrentWord;
	}

	public List<NPTracingStep> getSyllabifierTraceInfo() {
		return syllabifierTraceInfoList;
	}

	public List<NPTracingStep> getTracingSteps() {
		return tracer.getTracingSteps();
	}

	// following used for Unit Testing
	public void setTracingStep(NPTracingStep tracingStep) {
		tracer.setTracingStep(tracingStep);
	}


	public boolean isDoTrace() {
		return fDoTrace;
	}

	public void setDoTrace(boolean fDoTrace) {
		this.fDoTrace = fDoTrace;
	}

	public String getLevelNDoubleBar() {
		return sLevelNDoubleBar;
	}

	public void setLevelNDoubleBar(String sLevelNDoubleBar) {
		this.sLevelNDoubleBar = sLevelNDoubleBar;
	}

	public String getLevelNBar() {
		return sLevelNBar;
	}

	public void setLevelNBar(String sLevelNBar) {
		this.sLevelNBar = sLevelNBar;
	}

	public String getLevelN() {
		return sLevelN;
	}

	public void setLevelN(String sLevelN) {
		this.sLevelN = sLevelN;
	}

	public boolean convertStringToSyllables(String word) {
		syllablesInCurrentWord.clear();
		syllabifierTraceInfoList.clear();
		boolean fSuccess = false;
		CVSegmenterResult segResult = segmenter.segmentWord(word);
		fSuccess = segResult.success;
		if (fSuccess) {
			List<NPSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
			fSuccess = parseIntoSyllables(segmentsInWord);
		}
		return fSuccess;
	}

	private boolean parseIntoSyllables(List<NPSegmentInSyllable> segmentsInWord) {
		if (segmentsInWord.size() == 0) {
			return false;
		}
		boolean fResult = syllabify(segmentsInWord);
		return fResult;
	}

	public boolean syllabify(List<NPSegmentInSyllable> segsInWord) {
		syllablesInCurrentWord.clear();
		syllabifierTraceInfoList.clear();
		this.segmentsInWord = segsInWord;
		NPTracingStep traceInfo = null;
		int segmentCount = segmentsInWord.size();
		if (segmentCount == 0) {
			return false;
		}
		NPRuleMatcher matcher = new NPRuleMatcher();
		matcher.setLanguageProject(languageProject);
		List<Integer> matches;
		for (NPRule rule : npApproach.getValidActiveNPRules()) {
			matches = matcher.match(rule, segmentsInWord);
			if (matches.size() == 0) {
				continue;
			}
			if (matches.size() > 1 && (rule.getRuleAction() == NPRuleAction.LEFT_ADJOIN
					|| (rule.getRuleAction() == NPRuleAction.AUGMENT
					&& rule.getRuleLevel() == NPRuleLevel.N_DOUBLE_BAR))) {
				// Need to reverse order of matches to avoid the left-to-right processing
				Collections.sort(matches, Collections.reverseOrder());
			}
			switch (rule.getRuleAction()) {
			case ATTACH:
			case AUGMENT:
				for (Integer i : matches) {
					int iContext = i + 1;
					if (rule.getRuleLevel() != NPRuleLevel.N_DOUBLE_BAR) {
						iContext = i - 1;
					}
					if (rule.isObeysSSP() && !checkSonority(segmentsInWord, i, iContext)) {
						// TODO: report in trace
						continue;
					}
					addSegmentToSyllable(segmentsInWord, rule, i, iContext);
				}
				break;
			case BUILD:
				buildNucleus(segmentsInWord, matches);
				break;
			case LEFT_ADJOIN:
				break;
			case RIGHT_ADJOIN:
				break;
			default:
				break;
				
			}
		}
//		NPSyllable syl = new NPSyllable(new ArrayList<NPSegmentInSyllable>(), new NPNodeInSyllable());
//		syl.getSegmentsInSyllable().add(segmentsInWord.get(0));
//		Segment seg1 = segmentsInWord.get(0).getSegment();
//		SHNaturalClass natClass = npApproach.getNaturalClassContainingSegment(seg1);
//		if (natClass == null) {
//			if (fDoTrace) {
//				traceInfo = new NPTracingStep(seg1, null, null, null, SHComparisonResult.MISSING1);
//				syllabifierTraceInfoList.add(traceInfo);
//			}
//			return false;
//		}
//		int i = 1;
//		while (i < segmentCount) {
//			seg1 = segmentsInWord.get(i - 1).getSegment();
//			Segment seg2 = segmentsInWord.get(i).getSegment();
//			SHComparisonResult result = sonorityComparer.compare(seg1, seg2);
//			if (fDoTrace) {
//				traceInfo = new NPTracingStep(seg1,
//						npApproach.getNaturalClassContainingSegment(seg1), seg2,
//						npApproach.getNaturalClassContainingSegment(seg2), result);
//				if (fLastStartedSyllable) {
//					traceInfo.startsSyllable = true;
//					fLastStartedSyllable = false;
//				}
//				syllabifierTraceInfoList.add(traceInfo);
//			}
//			if (result == SHComparisonResult.MORE) {
//				int j = i + 1;
//				if (j < segmentCount) {
//					Segment seg3 = segmentsInWord.get(j).getSegment();
//					result = sonorityComparer.compare(seg2, seg3);
//					if (result == SHComparisonResult.EQUAL || result == SHComparisonResult.MORE) {
//						syl.add(segmentsInWord.get(i));
//						i++;
//						if (fDoTrace) {
//							traceInfo = new SHTracingStep(seg2,
//									npApproach.getNaturalClassContainingSegment(seg2), seg3,
//									npApproach.getNaturalClassContainingSegment(seg3), result);
//							syllabifierTraceInfoList.add(traceInfo);
//						}
//					}
//					syl = endThisSyllableStartNew(segmentsInWord, syl, i);
//					fLastStartedSyllable = true;
//				} else {
//					syl.add(segmentsInWord.get(i));
//				}
//			} else if (result == SHComparisonResult.LESS) {
//				syl.add(segmentsInWord.get(i));
//			} else if (result == SHComparisonResult.EQUAL) {
//				syl = endThisSyllableStartNew(segmentsInWord, syl, i);
//				fLastStartedSyllable = true;
//			} else {
//				return false;
//			}
//			i++;
//		}
//		if (syl.getSegmentsInSyllable().size() > 0) {
//			syllablesInCurrentWord.add(syl);
//			if (fDoTrace && fLastStartedSyllable) {
//				Segment seg = segmentsInWord.get(segmentCount -1).getSegment();
//				traceInfo = new SHTracingStep(seg,
//						npApproach.getNaturalClassContainingSegment(seg), null,
//						null, null);
//				traceInfo.startsSyllable = true;
//				syllabifierTraceInfoList.add(traceInfo);
//			}
//		}
		for (NPSegmentInSyllable seg : segmentsInWord) {
			if (seg.getSyllable() == null) {
				// not every segment was processed
				return false;
			}
		}
		switch (onsetType) {
		case ALL_BUT_FIRST_HAS_ONSET:
			if (!foundRequiredOnsets(1)) {
				return false;				
			}
			break;
		case EVERY_SYLLABLE_HAS_ONSET:
			if (!foundRequiredOnsets(0)) {
				return false;				
			}
			break;
		case ONSETS_NOT_REQUIRED:
			// nothing to do
			break;
		}
		return true;
	}

	protected boolean foundRequiredOnsets(int initialSegment) {
		for (int i = initialSegment; i < syllablesInCurrentWord.size(); i++) {
			NPSyllable syl = syllablesInCurrentWord.get(i);
			NPNodeInSyllable node = findNodeAtLevel(syl.getNode(), NPNodeLevel.N_DOUBLE_BAR);
			if (node == null || node.getNodes().get(0).getLevel() != NPNodeLevel.TERMINAL) {
				return false;
			}
		}
		return true;
	}

	protected void addSegmentToSyllable(List<NPSegmentInSyllable> segmentsInWord,
			NPRule rule, Integer i, int iContext) {
		NPSyllable syl = segmentsInWord.get(iContext).getSyllable();
		addSegmentToNode(segmentsInWord, rule, i, syl);
		if (i < iContext) {
			syl.getSegmentsInSyllable().add(0, segmentsInWord.get(i));
		} else {
			syl.getSegmentsInSyllable().add(segmentsInWord.get(i));
		}
		segmentsInWord.get(i).setSyllable(syl);
	}

	protected boolean checkSonority(List<NPSegmentInSyllable> segmentsInWord, Integer iAffected, int iContext) {
		Segment seg1 = segmentsInWord.get(iContext).getSegment();
		Segment seg2 = segmentsInWord.get(iAffected).getSegment();
		SHComparisonResult result = sonorityComparer.compare(seg1, seg2);
//						if (fDoTrace) {
//							traceInfo = new NPTracingStep(seg1,
//									npApproach.getNaturalClassContainingSegment(seg1), seg2,
//									npApproach.getNaturalClassContainingSegment(seg2), result);
//							syllabifierTraceInfoList.add(traceInfo);
//						}
		return result == SHComparisonResult.MORE;
	}

	protected void addSegmentToNode(List<NPSegmentInSyllable> segmentsInWord, NPRule rule,
			Integer i, NPSyllable syl) {
		NPNodeLevel level = NPNodeLevel.UNKNOWN;
		switch (rule.getRuleLevel()) {
		case ALL:
			break;
		case N:
			level = NPNodeLevel.N;
			break;
		case N_BAR:
			level = NPNodeLevel.N_BAR;
			break;
		case N_DOUBLE_BAR:
			level = NPNodeLevel.N_DOUBLE_BAR;
			break;
		default:
			break;
		}
		NPNodeInSyllable node = findNodeAtLevel(syl.getNode(), level);
		if (node != null) {
			NPNodeInSyllable terminal = new NPNodeInSyllable(NPNodeLevel.TERMINAL);
			terminal.getSegments().add(segmentsInWord.get(i));
			if (rule.getRuleLevel() == NPRuleLevel.N_DOUBLE_BAR) {
				node.getNodes().add(0,terminal);
			} else {
				node.getNodes().add(terminal);
			}
		}
	}

	protected void buildNucleus(List<NPSegmentInSyllable> segmentsInWord, List<Integer> matches) {
		for (Integer i : matches) {
			List<NPSegmentInSyllable> nodeNSegments = new ArrayList<NPSegmentInSyllable>();
			List<NPSegmentInSyllable> syllableSegments = new ArrayList<NPSegmentInSyllable>();
			nodeNSegments.add(segmentsInWord.get(i));
			syllableSegments.add(segmentsInWord.get(i));
			NPNodeInSyllable nodeNDoubleBar = new NPNodeInSyllable();
			nodeNDoubleBar.setLevel(NPNodeLevel.N_DOUBLE_BAR);
			NPNodeInSyllable nodeNBar = new NPNodeInSyllable();
			nodeNBar.setLevel(NPNodeLevel.N_BAR);
			NPNodeInSyllable nodeN = new NPNodeInSyllable();
			nodeN.setLevel(NPNodeLevel.N);
			NPNodeInSyllable nodeTerminal = new NPNodeInSyllable();
			nodeTerminal.setLevel(NPNodeLevel.TERMINAL);
			nodeTerminal.setSegments(nodeNSegments);
			nodeN.getNodes().add(nodeTerminal);
			nodeNBar.getNodes().add(nodeN);
			nodeNDoubleBar.getNodes().add(nodeNBar);
			NPSyllable syl = new NPSyllable(syllableSegments, nodeNDoubleBar);
			segmentsInWord.get(i).setSyllable(syl);
			syllablesInCurrentWord.add(syl);
		}
	}

	protected NPNodeInSyllable findNodeAtLevel(NPNodeInSyllable node, NPNodeLevel level) {
		if (node.getLevel() == level) {
			return node;
		}
		for (NPNodeInSyllable subnode : node.getNodes()) {
			NPNodeInSyllable foundNode = findNodeAtLevel(subnode, level);
			if (foundNode != null) {
				return foundNode;
			}
		}
		return null;
	}

//	protected SHSyllable endThisSyllableStartNew(
//			List<? extends CVSegmentInSyllable> segmentsInWord, SHSyllable syl, int i) {
//		syllablesInCurrentWord.add(syl);
//		syl = new SHSyllable(new ArrayList<CVSegmentInSyllable>());
//		syl.add(segmentsInWord.get(i));
//		return syl;
//	}

	public String getSyllabificationOfCurrentWord() {
		StringBuilder sb = new StringBuilder();
		int iSize = syllablesInCurrentWord.size();
		int i = 1;
		for (NPSyllable syl : syllablesInCurrentWord) {
			for (NPSegmentInSyllable seg : syl.getSegmentsInSyllable()) {
				sb.append(seg.getGrapheme());
			}
			if (i++ < iSize) {
				sb.append(".");
			}
		}
		return sb.toString();
	}

	@Override
	public String getLingTreeDescriptionOfCurrentWord() {
		StringBuilder sb = new StringBuilder();
		sb.append("(W");
		NPSyllable currentSyllable = null;
		for (NPSegmentInSyllable segInSyl : segmentsInWord) {
			NPSyllable syl = segInSyl.getSyllable();
			if (syl == null) {
				sb.append("(\\O \\L ");
				sb.append(segInSyl.getSegmentName());
				sb.append("(\\G ");
				sb.append(segInSyl.getGrapheme());
				sb.append("))");
			} else if (currentSyllable != syl) {
				currentSyllable = syl;
				sb.append("(");
				sb.append(Constants.SYLLABLE_SYMBOL);
				walkNodesForLingTree(syl.getNode(), null, sb);
				sb.append(")");
			}
		}
		sb.append(")");
		return sb.toString();
	}

	private void walkNodesForLingTree(NPNodeInSyllable node, NPNodeInSyllable nbarNode, StringBuilder sb) {
		if (node == null)
			return;
		switch (node.getLevel()) {
		case N:
			sb.append("(");
			sb.append(sLevelN);
			break;
		case N_BAR:
			sb.append("(");
			sb.append(sLevelNBar);
			nbarNode = node;
			break;
		case N_DOUBLE_BAR:
			sb.append("(");
			sb.append(sLevelNDoubleBar);
			break;
		case TERMINAL:
			createSegmentInfoForLingTree(node, sb);
			break;
		case UNKNOWN:
			break;
		default:
			break;
		}
		for (NPNodeInSyllable subnode : node.getNodes()) {
			walkNodesForLingTree(subnode, nbarNode, sb);
		}
		if (node.getLevel() != NPNodeLevel.TERMINAL && node.getLevel() != NPNodeLevel.UNKNOWN) {
			sb.append(")");
		}
	}

	protected void createSegmentInfoForLingTree(NPNodeInSyllable node, StringBuilder sb) {
		for (NPSegmentInSyllable seg : node.getSegments()) {
			sb.append("(\\L ");
			sb.append(seg.getSegmentName());
			sb.append("(\\G ");
			sb.append(seg.getGrapheme());
			sb.append("))");
		}
	}

	public String getNaturalClassesInCurrentWord() {
		StringBuilder sb = new StringBuilder();
		int iSize = syllabifierTraceInfoList.size();
		for (int i = 0; i < iSize; i++) {
			NPTracingStep info = syllabifierTraceInfoList.get(i);
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(getNCName(info.naturalClass1));
			if (i == iSize - 1) {
				sb.append(", ");
				sb.append(getNCName(info.naturalClass2));
			}
		}
		return sb.toString();
	}

	private String getNCName(SHNaturalClass natClass) {
		if (natClass == null) {
			return "null";
		} else {
			return natClass.getNCName();
		}
	}

//	public String getSonorityValuesInCurrentWord() {
//		return syllabifierTraceInfoList.stream().map(NPTracingStep::getComparisonResult)
//				.collect(Collectors.joining(", "));
//	}
}
