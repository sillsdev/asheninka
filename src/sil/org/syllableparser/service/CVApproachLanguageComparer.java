/**
 * 
 */
package sil.org.syllableparser.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.cvapproach.CVApproach;

/**
 * @author Andy Black
 *
 */
public class CVApproachLanguageComparer {

	// Embedded class used in this particular implementation

	CVApproach cva1;
	CVApproach cva2;
	Set<Segment> difference;

	Set<DifferentSegment> segmentsWhichDiffer = new HashSet<DifferentSegment>();

	public CVApproachLanguageComparer(CVApproach cva1, CVApproach cva2) {
		super();
		this.cva1 = cva1;
		this.cva2 = cva2;
	}

	public CVApproach getCVAa1() {
		return cva1;
	}

	public void setCVA1(CVApproach cva1) {
		this.cva1 = cva1;
	}

	public CVApproach getCVA2() {
		return cva2;
	}

	public void setCVA2(CVApproach cva2) {
		this.cva2 = cva2;
	}

	public Set<DifferentSegment> getSegmentsWhichDiffer() {
		return segmentsWhichDiffer;
	}

	public Set<Segment> getDifference() {
		return difference;
	}

	public void compareSegmentInventory() {
		ObservableList<Segment> segments1 = cva1.getLanguageProject().getSegmentInventory();
		ObservableList<Segment> segments2 = cva2.getLanguageProject().getSegmentInventory();

		Set<Segment> difference1from2 = new HashSet<Segment>(segments1);
		// use set difference (removeAll)
		difference1from2.removeAll(segments2);
		difference1from2.stream().forEach(
				segment -> segmentsWhichDiffer.add(new DifferentSegment(segment, null)));

		Set<Segment> difference2from1 = new HashSet<Segment>(segments2);
		difference2from1.removeAll(segments1);
		difference2from1.stream().forEach(segment -> mergeSimilarSegments(segment));
	}

	protected void mergeSimilarSegments(Segment segment) {
		List<DifferentSegment> sameSegmentsName = segmentsWhichDiffer
				.stream()
				.filter(ds -> ds.getSegmentFrom1() != null
						&& ds.getSegmentFrom1().getSegment().equals(segment.getSegment()))
				.collect(Collectors.toList());
		if (sameSegmentsName.size() > 0) {
			DifferentSegment diffSeg = sameSegmentsName.get(0);
			diffSeg.setSegmentFrom2(segment);
		} else {
			DifferentSegment diffSegment = new DifferentSegment(null, segment);
			segmentsWhichDiffer.add(diffSegment);
		}
	}

}
