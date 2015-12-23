/**
 * 
 */
package sil.org.syllableparser.backendprovider;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.model.LanguageProject;

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
				JAXBContext context = JAXBContext
						.newInstance(LanguageProject.class);
				Unmarshaller um = context.createUnmarshaller();
				// Reading XML from the file and unmarshalling.
				LanguageProject languageProjectLoaded = (LanguageProject) um
						.unmarshal(file);
				languageProject.clear();
				languageProject.load(languageProjectLoaded);				
		} catch (Exception e) { // catches ANY exception
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle(sFileError);
			alert.setHeaderText(sFileErrorLoadHeader);
			alert.setContentText(sFileErrorLoadContent + file.getPath());
			alert.showAndWait();
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
				JAXBContext context = JAXBContext
						.newInstance(LanguageProject.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				// Marshalling and saving XML to the file.
				m.marshal(languageProject, file);
		} catch (Exception e) { // catches ANY exception
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle(sFileError);
			alert.setHeaderText(sFileErrorSaveHeader);
			alert.setContentText(sFileErrorSaveContent + file.getPath());
			alert.showAndWait();
		}
	}

}
