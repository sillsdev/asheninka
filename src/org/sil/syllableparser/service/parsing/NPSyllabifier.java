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
import java.util.Optional;
import java.util.stream.Collectors;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.FilterType;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.OnsetPrincipleType;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.TemplateFilterSlotSegmentOrNaturalClass;
import org.sil.syllableparser.model.npapproach.NPApproach;
import org.sil.syllableparser.model.npapproach.NPFilter;
import org.sil.syllableparser.model.npapproach.NPNodeInSyllable;
import org.sil.syllableparser.model.npapproach.NPNodeLevel;
import org.sil.syllableparser.model.npapproach.NPRule;
import org.sil.syllableparser.model.npapproach.NPRuleAction;
import org.sil.syllableparser.model.npapproach.NPRuleLevel;
import org.sil.syllableparser.model.npapproach.NPSegmentInSyllable;
import org.sil.syllableparser.model.npapproach.NPSyllabificationStatus;
import org.sil.syllableparser.model.npapproach.NPSyllable;
import org.sil.syllableparser.model.npapproach.NPTracingStep;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHComparisonResult;
import org.sil.syllableparser.service.NPRuleMatcher;
import org.sil.syllableparser.service.TemplateFilterMatcher;

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
	private TemplateFilterMatcher filterMatcher;

	LinkedList<NPSyllable> syllablesInCurrentWord = new LinkedList<NPSyllable>(
			Arrays.asList(new NPSyllable(null, null)));
	List<NPSegmentInSyllable> segmentsInWord = new ArrayList<NPSegmentInSyllable>();
	List<NPFilter> failFilters = new ArrayList<NPFilter>();
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
		filterMatcher = TemplateFilterMatcher.getInstance();
		filterMatcher.setActiveSegments(languageProject.getActiveSegmentsInInventory());
		filterMatcher.setActiveClasses(languageProject.getCVApproach().getActiveCVNaturalClasses());

	}

	public List<NPSyllable> getSyllablesInCurrentWord() {
		return syllablesInCurrentWord;
	}

	public void setSyllablesInCurrentWord(LinkedList<NPSyllable> syllablesInCurrentWord) {
		this.syllablesInCurrentWord = syllablesInCurrentWord;
	}

	public List<NPTracingStep> getTracingSteps() {
		tracer = NPTracer.getInstance();
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

	public void setSegmentsInWord(List<NPSegmentInSyllable> segmentsInWord) {
		this.segmentsInWord = segmentsInWord;
	}

	public boolean convertStringToSyllables(String word) {
		syllablesInCurrentWord.clear();
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
		this.segmentsInWord = segsInWord;
		tracer = NPTracer.getInstance();
		tracer.resetSteps();
		tracer.setTracing(fDoTrace);
		int segmentCount = segmentsInWord.size();
		if (segmentCount == 0) {
			return false;
		}
		failFilters = npApproach.getValidActiveNPFilters().stream().collect(Collectors.toList());
		NPRuleMatcher matcher = new NPRuleMatcher();
		matcher.setLanguageProject(languageProject);
		List<Integer> matches;
		for (NPRule rule : npApproach.getValidActiveNPRules()) {
			if (fDoTrace) {
				tracer.setStatus(NPSyllabificationStatus.APPLYING_RULE);
				tracer.setRule(rule);
				tracer.recordStep();
			}
			matches = matcher.match(rule, segmentsInWord);
			if (matches.size() == 0) {
				if (fDoTrace) {
					tracer.setStatus(NPSyllabificationStatus.NO_SEGMENTS_MATCHED_RULE);
					tracer.recordStep();
				}
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
					if (!applySSP(rule, i, iContext)) {
						continue;
					}
					addSegmentToSyllable(segmentsInWord, rule, i, iContext);
				}
				break;
			case BUILD:
				for (Integer i : matches) {
					buildNucleus(segmentsInWord, i);
				}
				break;
			case LEFT_ADJOIN:
				for (Integer i : matches) {
					int iContext = i + 1;
					if (!applySSP(rule, i, iContext)) {
						continue;
					}
					applyAdjunctionRule(rule, i, iContext);
				}
				break;
			case RIGHT_ADJOIN:
				for (Integer i : matches) {
					int iContext = i - 1;
					if (!applySSP(rule, i, iContext)) {
						continue;
					}
					applyAdjunctionRule(rule, i, iContext);
				}
				break;
			default:
				break;
				
			}
		}
		for (NPSegmentInSyllable seg : segmentsInWord) {
			if (seg.getSyllable() == null) {
				// not every segment was processed
				if (fDoTrace) {
					tracer.setStatus(NPSyllabificationStatus.SOME_SEGMENTS_NOT_SYLLABIFIED);
					tracer.recordStep();
				}
				return false;
			}
		}
		switch (onsetType) {
		case ALL_BUT_FIRST_HAS_ONSET:
			if (!foundRequiredOnsets(1)) {
				if (fDoTrace) {
					tracer.setStatus(NPSyllabificationStatus.ONSET_REQUIRED_IN_ALL_BUT_FIRST_SYLLABLE_BUT_SOME_NONINITIAL_SYLLABLE_DOES_NOT_HAVE_AN_ONSET);
					tracer.recordStep();
				}
				return false;				
			}
			break;
		case EVERY_SYLLABLE_HAS_ONSET:
			if (!foundRequiredOnsets(0)) {
				if (fDoTrace) {
					tracer.setStatus(NPSyllabificationStatus.ONSET_REQUIRED_IN_EVERY_SYLLABLE_BUT_SOME_SYLLABLE_DOES_NOT_HAVE_AN_ONSET);
					tracer.recordStep();
				}
				return false;				
			}
			break;
		case ONSETS_NOT_REQUIRED:
			// nothing to do
			break;
		}
		return true;
	}

	protected boolean applySSP(NPRule rule, int i, int iContext) {
		boolean result = true;
		if (!rule.isObeysSSP()) {
			tracer.setStatus(NPSyllabificationStatus.RULE_IGNORES_SSP);
			tracer.recordStep();
		} else if (!checkSonority(segmentsInWord, i, iContext)) {
			tracer.setStatus(NPSyllabificationStatus.SSP_FAILED);
			tracer.recordStep();
			result = false;
		} else {
			tracer.setStatus(NPSyllabificationStatus.SSP_PASSED);
			tracer.recordStep();
		}
		return result;
	}

	protected void applyAdjunctionRule(NPRule rule, Integer i, int iContext) {
		NPSyllable syl = segmentsInWord.get(iContext).getSyllable();
		if (syl != null) {
			NPNodeLevel level = getNodeLevelFromRuleLevel(rule);
			NPNodeInSyllable node = findNodeAtLevel(syl.getNode(), level);
			if (node != null) {
				NPNodeInSyllable newNodeAdjunct = new NPNodeInSyllable(level);
				NPNodeInSyllable newNodeOrig = new NPNodeInSyllable(level);
				List<NPNodeInSyllable> cloneOfNodes = new ArrayList<NPNodeInSyllable>();
				for (NPNodeInSyllable nis : node.getNodes()) {
					cloneOfNodes.add(nis.clone());
				}
				newNodeOrig.setNodes(cloneOfNodes);
				NPNodeInSyllable terminal = new NPNodeInSyllable(NPNodeLevel.TERMINAL);
				terminal.getSegments().add(segmentsInWord.get(i));
				newNodeAdjunct.getNodes().add(terminal);
				node.getNodes().clear();
				segmentsInWord.get(i).setSyllable(syl);
				if (rule.getRuleAction() == NPRuleAction.LEFT_ADJOIN) {
					node.getNodes().add(newNodeAdjunct);
					node.getNodes().add(newNodeOrig);
					syl.getSegmentsInSyllable().add(0, segmentsInWord.get(i));
				} else {
					node.getNodes().add(newNodeOrig);
					node.getNodes().add(newNodeAdjunct);
					syl.getSegmentsInSyllable().add(segmentsInWord.get(i));
				}
				if (fDoTrace) {
					tracer.setStatus(getAdjunctionStatus(rule));
					rememberSyllabificationStateInTracer(segmentsInWord, syl, i);
				}
			}
		}
	}

	protected NPSyllabificationStatus getAdjunctionStatus(NPRule rule) {
		NPSyllabificationStatus status = NPSyllabificationStatus.UNKNOWN;
		switch (rule.getRuleAction()) {
		case LEFT_ADJOIN:
			switch (rule.getRuleLevel()) {
			case N:
				status = NPSyllabificationStatus.LEFT_ADJOINED_SEGMENT_TO_N_NODE;
				break;
			case N_BAR:
				status = NPSyllabificationStatus.LEFT_ADJOINED_SEGMENT_TO_N_BAR_NODE;
				break;
			case N_DOUBLE_BAR:
				status = NPSyllabificationStatus.LEFT_ADJOINED_SEGMENT_TO_N_DOUBLE_BAR_NODE;
				break;
			default:
				break;
			}
			break;
		case RIGHT_ADJOIN:
			switch (rule.getRuleLevel()) {
			case N:
				status = NPSyllabificationStatus.RIGHT_ADJOINED_SEGMENT_TO_N_NODE;
				break;
			case N_BAR:
				status = NPSyllabificationStatus.RIGHT_ADJOINED_SEGMENT_TO_N_BAR_NODE;
				break;
			case N_DOUBLE_BAR:
				status = NPSyllabificationStatus.RIGHT_ADJOINED_SEGMENT_TO_N_DOUBLE_BAR_NODE;
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		return status;
	}

	protected NPNodeLevel getNodeLevelFromRuleLevel(NPRule rule) {
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
		return level;
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
		if (syl != null && applyFailFilters(segmentsInWord, rule, i, iContext)) {
			addSegmentToNode(segmentsInWord, rule, i, syl);
			if (i < iContext) {
				syl.getSegmentsInSyllable().add(0, segmentsInWord.get(i));
				segmentsInWord.get(i).setSyllable(syl);
				if (fDoTrace) {
					tracer.setStatus(NPSyllabificationStatus.PREPENDED_SEGMENT_TO_SYLLABLE);
					rememberSyllabificationStateInTracer(segmentsInWord, syl, i);
				}
			} else {
				syl.getSegmentsInSyllable().add(segmentsInWord.get(i));
				segmentsInWord.get(i).setSyllable(syl);
				if (fDoTrace) {
					tracer.setStatus(NPSyllabificationStatus.APPENDED_SEGMENT_TO_SYLLABLE);
					rememberSyllabificationStateInTracer(segmentsInWord, syl, i);
				}
			}
		}
	}

	protected boolean applyFailFilters(List<NPSegmentInSyllable> segmentsInWord,
			NPRule rule, Integer i, int iContext) {
		List<NPFilter> filters = new ArrayList<NPFilter>();
		if (rule.getRuleAction() == NPRuleAction.ATTACH || rule.getRuleAction() == NPRuleAction.AUGMENT) {
			SHComparisonResult sspComparisonNeeded = SHComparisonResult.MISSING1;
			int iStart = i;
			switch (rule.getRuleLevel()) {
			case N_BAR:
				filters = failFilters.stream().filter(f -> f.getTemplateFilterType() == FilterType.CODA
				|| f.getTemplateFilterType() == FilterType.RIME).collect(Collectors.toList());
				sspComparisonNeeded = SHComparisonResult.MORE;
				iStart = iContext;
				break;
			case N_DOUBLE_BAR:
				filters = failFilters.stream().filter(f -> f.getTemplateFilterType() == FilterType.ONSET).collect(Collectors.toList());
				sspComparisonNeeded = SHComparisonResult.LESS;
				break;
			default:
				break;
			}
			return applyAnyFailFilters(segmentsInWord, iStart, sonorityComparer, sspComparisonNeeded, filters);
		}
		return true;
	}

	protected boolean applyAnyFailFilters(List<NPSegmentInSyllable> segmentsInWord,
			int iSegmentInWord, SHSonorityComparer sonorityComparer,
			SHComparisonResult sspComparisonNeeded, List<NPFilter> filters) {
		NPTracer tracer = NPTracer.getInstance();
		for (NPFilter f : filters) {
			int iItemsInFilter = f.getSlots().size();
			int iStart = iSegmentInWord;
			Optional<TemplateFilterSlotSegmentOrNaturalClass> slot = f.getSlots().stream()
					.filter(s -> s.isConstituentBeginsHere()).findFirst();
			if (slot.isPresent()) {
				//adjust where we begin to match
				int iSlot = f.getSlots().indexOf(slot.get());
				int iSlotAdjust = calculateWhereToStartMatch(segmentsInWord, iSegmentInWord, slot,
						iSlot, f);
				iStart -= iSlotAdjust;
				if (iSlot < 0 || iStart < 0) {
					continue;
				}
			}
			int iEnd = Math.min(iSegmentInWord + iItemsInFilter, segmentsInWord.size());
			if (filterMatcher.matches(f,
					segmentsInWord.subList(iStart, iEnd),
					sonorityComparer, sspComparisonNeeded)) {
				if (fDoTrace) {
					tracer.setStatus(NPSyllabificationStatus.FILTER_FAILED);
					tracer.setFilterUsed(f);
					tracer.setSuccessful(false);
					tracer.recordStep();
				}
				return false;
			}
		}
		return true;
	}

	protected int calculateWhereToStartMatch(List<NPSegmentInSyllable> segmentsInWord,
			int iSegmentInWord, Optional<TemplateFilterSlotSegmentOrNaturalClass> slot, int iSlot,
			NPFilter f) {
		int iSlotAdjust = 0;
		for (int i = 0; i <= iSlot; i++) {
			if (f.getSlots().get(i).isOptional()) {
				int indexOfSegmentToMatch = iSegmentInWord - (iSlot-i) - 1;
				if (indexOfSegmentToMatch < 0) {
					continue;
				}
				Segment seg = segmentsInWord.get(indexOfSegmentToMatch).getSegment();
				if (filterMatcher.segMatchesSlot(seg, slot.get(), true)) {
					iSlotAdjust++;
				}
			} else {
				iSlotAdjust++;
			}
		}
		return iSlotAdjust;
	}

	protected boolean checkSonority(List<NPSegmentInSyllable> segmentsInWord, Integer iAffected, int iContext) {
		Segment seg1 = segmentsInWord.get(iContext).getSegment();
		Segment seg2 = segmentsInWord.get(iAffected).getSegment();
		SHComparisonResult result = sonorityComparer.compare(seg1, seg2);
		if (fDoTrace) {
			if (iAffected < iContext) {
				tracer.setSegment1(seg2);
				tracer.setNaturalClass1(npApproach.getNaturalClassContainingSegment(seg2));
				tracer.setSegment2(seg1);
				tracer.setNaturalClass2(npApproach.getNaturalClassContainingSegment(seg1));
				tracer.setSHComparisonResult(sonorityComparer.compare(seg2, seg1));
			} else {
				tracer.setSegment1(seg1);
				tracer.setNaturalClass1(npApproach.getNaturalClassContainingSegment(seg1));
				tracer.setSegment2(seg2);
				tracer.setNaturalClass2(npApproach.getNaturalClassContainingSegment(seg2));
				tracer.setSHComparisonResult(result);
			}
		}
		return result == SHComparisonResult.MORE;
	}

	protected void addSegmentToNode(List<NPSegmentInSyllable> segmentsInWord, NPRule rule,
			Integer i, NPSyllable syl) {
		NPNodeLevel level = getNodeLevelFromRuleLevel(rule);
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

	protected void buildNucleus(List<NPSegmentInSyllable> segmentsInWord, int i) {
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
		if (fDoTrace) {
			tracer.setStatus(NPSyllabificationStatus.BUILT_ALL_NODES);
			rememberSyllabificationStateInTracer(segmentsInWord, syl, i);
		}
	}

	protected void rememberSyllabificationStateInTracer(List<NPSegmentInSyllable> segmentsInWord,
			NPSyllable syl, int i) {
		tracer.setSegmentsInWord(segmentsInWord);
		tracer.setSegment1(segmentsInWord.get(i).getSegment());
		tracer.recordStep();
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
}
