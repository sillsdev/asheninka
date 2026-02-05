// Copyright (c) 2025 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.model.hyphenapproach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.Approach;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.Word;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
// @XmlAccessorType(XmlAccessType.FIELD)
public class HyphenApproach extends Approach {

	private ObservableList<HyphenClass> hyphenClasses = FXCollections.observableArrayList();
	private ObservableList<HyphenChangeRule> hyphenRules = FXCollections
			.observableArrayList();
	private HyphenClass insertHereHC = new HyphenClass(Constants.INSERT_HYPHEN_SYMBOL, null,
			"Insert hyphen here", Constants.SPECIAL_INSERT_CODE, "1eb98e78-8d56-478a-aae0-92c1866ca3fc");
	private HyphenClass wordBoundaryHC = new HyphenClass(Constants.WORD_BOUNDARY_SYMBOL, null,
			"word boundary", Constants.SPECIAL_WORD_BOUNDARY_CODE, "5f50fa4b-6f46-4937-9564-9bb9be2d58b0");

	public void afterUnmarshal(Unmarshaller u, Object parent) {
		this.languageProject = (LanguageProject) parent;
	}

	@XmlElement(name = "insertHereHC")
	public HyphenClass getInsertHereHC() {
		return insertHereHC;
	}

	public void setInsertHereHC(HyphenClass insertHereHC) {
		this.insertHereHC = insertHereHC;
	}

	public HyphenClass getWordBoundaryHC() {
		return wordBoundaryHC;
	}

	public void setWordBoundaryHC(HyphenClass wordBoundaryHC) {
		this.wordBoundaryHC = wordBoundaryHC;
	}

	/**
	 * @return the hyphenClassesData
	 */
	@XmlElementWrapper(name = "hyphenClasses")
	@XmlElement(name = "hyphenClass")
	public ObservableList<HyphenClass> getHyphenClasses() {
		return hyphenClasses;
	}

	public void setHyphenClasses(ObservableList<HyphenClass> value) {
		this.hyphenClasses = value;
	}

	public List<HyphenClass> getActiveHyphenClasses() {
		return hyphenClasses.stream().filter(hypCVlass -> hypCVlass.isActive())
				.collect(Collectors.toList());
	}

	@XmlElementWrapper(name = "hyphenChangeRules")
	@XmlElement(name = "hyphenChangeRule")
	public ObservableList<HyphenChangeRule> getHyphenChangeRules() {
		return hyphenRules;
	}

	public void setHyphenChangeRules(ObservableList<HyphenChangeRule> value) {
		this.hyphenRules = value;
	}

	public List<HyphenChangeRule> getActiveHyphenChangeRules() {
		return hyphenRules.stream()
				.filter(hypRule -> hypRule.isActive() && hypRule.matchClasses.size() != 0 && hypRule.changeClasses.size() != 0)
				.collect(Collectors.toList());
	}

	/**
	 * Clear out all data in this Hyphen approach
	 */
	public void clear() {
		hyphenClasses.clear();
		hyphenRules.clear();
	}

	/**
	 * @param hyphenApproach
	 */
	public void load(HyphenApproach hyphenApproachLoaded) {
		ObservableList<HyphenClass> hyphenClassesLoadedData = hyphenApproachLoaded
				.getHyphenClasses();
		for (HyphenClass hyphenClass : hyphenClassesLoadedData) {
			hyphenClasses.add(hyphenClass);
		}
		ObservableList<HyphenChangeRule> hyphenChangeRulesLoadedData = hyphenApproachLoaded
				.getHyphenChangeRules();
		for (HyphenChangeRule hyphenRule : hyphenChangeRulesLoadedData) {
			hyphenRules.add(hyphenRule);
		}
	}

	public Set<Segment> getMissingSegmentsFromClasses() {
		Set<Segment> segmentsInInventory = new HashSet<Segment>();
		Set<Segment> segmentsInHyphenClasses = new HashSet<Segment>();
		segmentsInInventory.addAll(getLanguageProject().getActiveSegmentsInInventory());
		for (HyphenClass hc : getActiveHyphenClasses()) {
			segmentsInHyphenClasses.addAll(hc.getSegments());
		}
		segmentsInInventory.removeAll(segmentsInHyphenClasses);
		return segmentsInInventory;
	}

	public List<SegmentInHyphenClass> getDuplicateSegmentsFromHyphenClass() {
		Map<Segment, HyphenClass> segmentInHyphenClasses = new HashMap<Segment, HyphenClass>();
		List<SegmentInHyphenClass> duplicateSegsInHyphenClasses = new ArrayList<SegmentInHyphenClass>();
		for (HyphenClass hc : getActiveHyphenClasses()) {
			for (Segment seg : hc.getSegments()) {
				if (segmentInHyphenClasses.containsKey(seg)) {
					SegmentInHyphenClass segInClass = new SegmentInHyphenClass(seg, segmentInHyphenClasses.get(seg));
					if (!duplicateSegsInHyphenClasses.contains(segInClass)) {
						duplicateSegsInHyphenClasses.add(segInClass);
					}
					SegmentInHyphenClass segInClass2 = new SegmentInHyphenClass(seg, hc);
					duplicateSegsInHyphenClasses.add(segInClass2);
				} else {
					segmentInHyphenClasses.put(seg, hc);
				}
			}
		}
		return duplicateSegsInHyphenClasses;
	}

	protected String getPredictedSyllabificationOfWord(Word word) {
		return word.getCVPredictedSyllabification();
	}
}
