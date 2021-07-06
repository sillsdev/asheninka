/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.npapproach.NPRule;
import org.sil.syllableparser.model.npapproach.NPRuleAction;
import org.sil.syllableparser.model.npapproach.NPRuleLevel;
import org.sil.syllableparser.model.npapproach.NPSegmentInSyllable;

/**
 * @author Andy Black
 *
 *         Singleton pattern for validation of NP rules
 */
public class NPRuleMatcher {

	private NPRule rule;
	private List<NPSegmentInSyllable> segments = new ArrayList<NPSegmentInSyllable>();
	private ObservableList<Segment> segmentInventory;
	private ObservableList<CVNaturalClass> natClasses;
	private boolean codasAllowed = true;
	
	private static NPRuleMatcher instance;

	public static NPRuleMatcher getInstance() {
		if (instance == null) {
			instance = new NPRuleMatcher();
		}
		return instance;
	}

	public void setLanguageProject(LanguageProject languageProject) {
		if (languageProject != null) {
			segmentInventory = FXCollections.observableList(languageProject.getActiveSegmentsInInventory());
			natClasses = FXCollections.observableList(languageProject.getNPApproach().getActiveCVNaturalClasses());
			codasAllowed = languageProject.getSyllabificationParameters().isCodasAllowed();
		}
	}

	public NPRule getRule() {
		return rule;
	}

	public void setRule(NPRule rule) {
		this.rule = rule;
	};
	
	public List<NPSegmentInSyllable> getSegments() {
		return segments;
	}

	public void setSegments(List<NPSegmentInSyllable> segments) {
		this.segments = segments;
	}

	public void setCodasAllowed(boolean codasAllowed) {
		this.codasAllowed = codasAllowed;
	}

	public List<Integer> match(NPRule rule, List<NPSegmentInSyllable> segmentsInWord) {
		List<Integer> matches = new ArrayList<Integer>();
		Segment affectedSegment = null;
		Segment contextSegment = null;
		CVNaturalClass affectedNaturalClass = null;
		CVNaturalClass contextNaturalClass = null;
		if (!codasAllowed && rule.getRuleLevel() == NPRuleLevel.N_BAR
				&& (rule.getRuleAction() == NPRuleAction.ATTACH
				|| rule.getRuleAction() == NPRuleAction.AUGMENT)) {
			// no codas allowed so these cannot match anything
			return matches;
		}
		if (rule.getAffectedSegOrNC().isSegment()) {
			int i = SylParserObject.findIndexInListByUuid(segmentInventory, rule.getAffectedSegOrNC().getUuid());
			affectedSegment = segmentInventory.get(i);
		} else {
			int i = SylParserObject.findIndexInListByUuid(natClasses, rule.getAffectedSegOrNC().getUuid());
			affectedNaturalClass = natClasses.get(i);
		}
		if (rule.getContextSegOrNC() != null) {
			if (rule.getContextSegOrNC().isSegment()) {
				int i = SylParserObject.findIndexInListByUuid(segmentInventory, rule
						.getContextSegOrNC().getUuid());
				contextSegment = segmentInventory.get(i);
			} else {
				int i = SylParserObject.findIndexInListByUuid(natClasses, rule.getContextSegOrNC()
						.getUuid());
				contextNaturalClass = natClasses.get(i);
			}
		}
		int iSegInSyl = 0;
		for (NPSegmentInSyllable segInSyl : segmentsInWord) {
			boolean segMatches = false;
			if (segInSyl.getSyllable() != null) {
				// If it has a node, then it has been included in a syllable already;
				// skip it.
				iSegInSyl++;
				continue;
			}
			if (affectedNaturalClass != null) {
				segMatches = affectedNaturalClass.hasSegment(segInSyl.getSegment()); 
			} else {
				segMatches = segInSyl.getSegment().equals(affectedSegment);
			}
			switch (rule.getRuleAction()) {
			case ATTACH:
				// fall through
			case AUGMENT:
				if (segMatches) {
					switch (rule.getRuleLevel()) {
					case N:
						// fall through
					case N_BAR:
						segMatches = contextMatches(segmentsInWord, contextSegment,
								contextNaturalClass, iSegInSyl, -1);
						break;
					case N_DOUBLE_BAR:
						segMatches = contextMatches(segmentsInWord, contextSegment,
								contextNaturalClass, iSegInSyl, 1);
						break;
					default:
						segMatches = false;
						break;
					}
				}
				break;
			case LEFT_ADJOIN:
				if (segMatches) {
					segMatches = contextMatches(segmentsInWord, contextSegment,
							contextNaturalClass, iSegInSyl, 1);
				}
				break;
			case RIGHT_ADJOIN:
				if (segMatches) {
					segMatches = contextMatches(segmentsInWord, contextSegment,
							contextNaturalClass, iSegInSyl, -1);
				}
				break;
			default:
				break;
			}
			if (segMatches) {
				matches.add(iSegInSyl);
			}
			iSegInSyl++;
		}

		return matches;
	}

	protected boolean contextMatches(List<NPSegmentInSyllable> segmentsInWord,
			Segment contextSegment, CVNaturalClass contextNaturalClass, int iSegInSyl, int iOffset) {
		int index = iSegInSyl + iOffset; 
		if (index <0 || index >= segmentsInWord.size())
			return false;
		boolean segMatches;
		Segment segToCheck = segmentsInWord.get(iSegInSyl + iOffset).getSegment();
		if (contextNaturalClass != null) {
			segMatches = contextNaturalClass.hasSegment(segToCheck); 
		} else {
			segMatches = segToCheck.equals(contextSegment);
		}
		return segMatches;
	}
}
