/**
 * 
 */
package sil.org.syllableparser.model.cvapproach;


/**
 * @author Andy Black
 *
 * A segment which occurs in a syllable using the CV Patterns approach 
 *
 * A value object
 */

public class CVSegmentInSyllable extends Object {
	private final CVSegment segment;
	private final String grapheme;

	public CVSegmentInSyllable(CVSegment segment, String grapheme) {
		super();
		this.segment = segment;
		this.grapheme = grapheme;
	}

	public CVSegment getSegment() {
		return segment;
	}

	public String getGrapheme() {
		return grapheme;
	}

	public String getSegmentName() {
		return segment.getSegment();
	}

}
