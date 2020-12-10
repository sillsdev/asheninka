// Copyright (c) 2016-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.backendprovider;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.utility.HandleExceptionMessage;

/**
 * @author Andy Black
 *
 */
public class XMLBackEndProvider extends BackEndProvider {

	LanguageProject languageProject;
	String sFileError;
	String sFileErrorLoadHeader;
	String sFileErrorLoadContent;
	String sFileErrorSaveHeader;
	String sFileErrorSaveContent;

	/**
	 * @param languageProject
	 */
	public XMLBackEndProvider(LanguageProject languageProject, Locale locale) {
		this.languageProject = languageProject;
		setResourceStrings(locale);
	}

	private void setResourceStrings(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(Constants.RESOURCE_LOCATION, locale);
		sFileError = bundle.getString("file.error");
		sFileErrorLoadHeader = bundle.getString("file.error.load.header");
		sFileErrorLoadContent = bundle.getString("file.error.load.content");
		sFileErrorSaveHeader = bundle.getString("file.error.save.header");
		sFileErrorSaveContent = bundle.getString("file.error.save.content");
	}

	public LanguageProject getLanguageProject() {
		return languageProject;
	}

	final boolean useXMLClasses = false;

	/**
	 * Loads language data from the specified file. The current language data
	 * will be replaced.
	 * 
	 * @param file
	 */
	@Override
	public void loadLanguageDataFromFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(LanguageProject.class);
			Unmarshaller um = context.createUnmarshaller();
			// Reading XML from the file and unmarshalling.
			LanguageProject languageProjectLoaded = (LanguageProject) um.unmarshal(file);
			languageProject.clear();
			languageProject.load(languageProjectLoaded);
		} catch (Exception e) { // catches ANY exception
			e.printStackTrace();
			HandleExceptionMessage.show(sFileError, sFileErrorLoadHeader, sFileErrorLoadContent
					+ file.getPath(), true);
		}
	}

	/**
	 * Saves the current language data to the specified file.
	 * 
	 * @param file
	 */
	@Override
	public void saveLanguageDataToFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(LanguageProject.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// Marshalling and saving XML to the file.
			m.marshal(languageProject, file);
		} catch (Exception e) { // catches ANY exception
			e.printStackTrace();
			HandleExceptionMessage.show(sFileError, sFileErrorSaveHeader, sFileErrorSaveContent
					+ file.getPath(), true);
		}
	}

}
