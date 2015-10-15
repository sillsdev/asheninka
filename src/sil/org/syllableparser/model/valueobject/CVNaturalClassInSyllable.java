/**
 * 
 */
package sil.org.syllableparser.model.valueobject;

import sil.org.syllableparser.model.CVNaturalClass;

/**
 * @author Andy Black
 * 
 * A natural class which occurs in a syllable using the CV Patterns approach 
 *
 * A value object
 */

public class CVNaturalClassInSyllable extends Object {
	private final CVNaturalClass naturalClass;
	private final CVSegmentInSyllable segmentInSyllable;

	public CVNaturalClassInSyllable(CVNaturalClass naturalClass,
			CVSegmentInSyllable segmentInSyllable) {
		super();
		this.naturalClass = naturalClass;
		this.segmentInSyllable = segmentInSyllable;
	}

	public CVNaturalClass getNaturalClass() {
		return naturalClass;
	}

	public CVSegmentInSyllable getSegmentInSyllable() {
		return segmentInSyllable;
	}

	public String getNaturalClassName() {
		if (naturalClass != null) {
		return naturalClass.getNCName();
		}
		else {
			return "";
		}
	}

}
