/**
 * 
 */
package sil.org.syllableparser.model;

/**
 * @author Andy Black
 *
 */
public class LanguageProject {
	
	public LanguageProject() {
		super();
		cvApproach = new CVApproach();
	}

	private CVApproach cvApproach;

	/**
	 * Clear out all data in this language project
	 */
	public void clear() {
		cvApproach.clear();
		
	}

	public CVApproach getCVApproach() {
		return cvApproach;
	}

	public void setCVApproach(CVApproach cvApproach) {
		this.cvApproach = cvApproach;
	}

}
