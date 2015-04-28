/**
 * 
 */
package sil.org.syllableparser.backendprovider;

/**
 * @author Andy Black
 *
 */
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import sil.org.syllableparser.model.CVApproach;
import sil.org.syllableparser.model.CVSegment;
import sil.org.syllableparser.model.LanguageProject;

@XmlRootElement(name = "languageProject")
public class XMLLanguageProject {
	
	private LanguageProject languageProject;
	
	private XMLCVApproach xmlCVApproach;
	
	public XMLLanguageProject(LanguageProject languageProject) {
		super();
		this.languageProject = languageProject;
	}

	@XmlElement(name = "cvApproach")
	public XMLCVApproach getCVApproach() {
		return xmlCVApproach;
	}

	public void setCVApproach(XMLCVApproach cvApproach) {
		this.xmlCVApproach = xmlCVApproach;
	}

	/**
	 * Clear all data from a language project
	 */
	public void clear() {
		languageProject.clear();
		
	}

	/**
	 * load (i.e., fluff up) all data in a language project from the XML
	 */
	public void load() {
		languageProject.clear(); // no-op for now
		
	}

}
