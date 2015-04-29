/**
 * 
 */
package sil.org.syllableparser.backendprovider;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import sil.org.syllableparser.model.CVApproach;
import sil.org.syllableparser.model.CVSegment;

/**
 * @author Andy Black
 *
 */
public class XMLCVApproach {

	private CVApproach cvApproach;
	private List<CVSegment> cvSegments = new ArrayList<CVSegment>();

	public XMLCVApproach() {
		super();
	}


	/**
	 * @param cvApproach
	 */
	public XMLCVApproach(CVApproach cvApproach) {
		this.cvApproach = cvApproach;
		cvSegments = cvApproach.getCVSegmentInventory();
	}


	@XmlElementWrapper(name = "cvSegments")
	@XmlElement(name = "cvSegment")
	public List<CVSegment> getCVSegments() {
		return cvSegments;
	}

	public void setCVSegments(List<CVSegment> cvSegments) {
		this.cvSegments = cvSegments;
	}
}
