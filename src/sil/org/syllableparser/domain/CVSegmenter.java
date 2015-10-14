/**
 * 
 */
package sil.org.syllableparser.domain;

import java.util.List;

import sil.org.syllableparser.model.CVSegment;

/**
 * @author Andy Black
 *
 */
public class CVSegmenter {
	
	List<CVSegment> segmentInventory;
	List<CVSegment> segments;
	public CVSegmenter(List<CVSegment> segmentInventory) {
		super();
		this.segmentInventory = segmentInventory;
	}
	
	public List<CVSegment> getSegmentInventory() {
		return segmentInventory;
	}
	public void setSegmentInventory(List<CVSegment> segmentInventory) {
		this.segmentInventory = segmentInventory;
	}
	public List<CVSegment> getSegments() {
		return segments;
	}
	public void setSegments(List<CVSegment> segments) {
		this.segments = segments;
	}


}
