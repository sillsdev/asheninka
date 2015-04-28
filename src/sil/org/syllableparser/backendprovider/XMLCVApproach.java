/**
 * 
 */
package sil.org.syllableparser.backendprovider;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import sil.org.syllableparser.model.CVSegment;

/**
 * @author Andy Black
 *
 */
public class XMLCVApproach {

	private List<CVSegment> cvSegments;

	@XmlElement(name = "cvSegment")
	public List<CVSegment> getCVSegments() {
		return cvSegments;
	}

	public void setCVSegments(List<CVSegment> cvSegments) {
		this.cvSegments = cvSegments;
	}
}
