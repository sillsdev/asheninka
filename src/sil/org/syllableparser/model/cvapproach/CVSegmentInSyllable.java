/**
 * 
 */
package sil.org.syllableparser.model.cvapproach;

import sil.org.syllableparser.model.Segment;


/**
 * @author Andy Black
 *
 * A segment which occurs in a syllable using the CV Patterns approach 
 *
 * A value object
 */

public class CVSegmentInSyllable extends Object {
	private final Segment segment;
	private final String grapheme;

	public CVSegmentInSyllable(Segment segment, String grapheme) {
		super();
		this.segment = segment;
		this.grapheme = grapheme;
	}

	public Segment getSegment() {
		return segment;
	}

	public String getGrapheme() {
		return grapheme;
	}

	public String getSegmentName() {
		return segment.getSegment();
	}

}
