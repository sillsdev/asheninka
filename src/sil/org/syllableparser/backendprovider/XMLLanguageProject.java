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
	
	public XMLLanguageProject() {
		xmlCVApproach = new XMLCVApproach();
	}

	public XMLLanguageProject(LanguageProject languageProject) {
		super();
		this.languageProject = languageProject;
		xmlCVApproach = new XMLCVApproach(languageProject.getCVApproach());
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
		if (languageProject != null) {
			languageProject.clear();
		}
		
	}

	/**
	 * load (i.e., fluff up) all data in a language project from the XML
	 */
	public void load() {
		if (languageProject == null) {
			languageProject = new LanguageProject();
		}
		languageProject.clear(); // no-op for now
		
	}

}
