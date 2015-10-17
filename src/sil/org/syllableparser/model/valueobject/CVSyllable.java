/**
 * 
 */
package sil.org.syllableparser.model.valueobject;

import java.util.List;


/**
 * @author Andy Black
 *
 * A syllable in a word using the CV Patterns approach 
 *
 * A value object
 */
public class CVSyllable extends Object {

	private final List<CVNaturalClassInSyllable> naturalClassesInSyllable;

	public CVSyllable(List<CVNaturalClassInSyllable> naturalClassesInSyllable) {
		super();
		this.naturalClassesInSyllable = naturalClassesInSyllable;
	}

	public List<CVNaturalClassInSyllable> getNaturalClassesInSyllable() {
		return naturalClassesInSyllable;
	}
	
	public String getNaturalClassNamesInSyllable() {
		StringBuilder sb = new StringBuilder();
		
		for (CVNaturalClassInSyllable nc : naturalClassesInSyllable) {
			sb.append(nc.getNaturalClassName());
		}	
		return sb.toString();
	}
}
