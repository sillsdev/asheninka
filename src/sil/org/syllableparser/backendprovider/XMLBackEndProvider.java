/**
 * 
 */
package sil.org.syllableparser.backendprovider;

import java.io.File;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import sil.org.syllableparser.ApplicationPreferences;
import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.backendprovider.XMLLanguageProject;
import sil.org.syllableparser.model.LanguageProject;

/**
 * @author Andy Black
 *
 */
public class XMLBackEndProvider extends BackEndProvider {

	LanguageProject languageProject;
	XMLLanguageProject xmlLanguageProjectData;

	/**
	 * @param languageProject
	 */
	public XMLBackEndProvider(LanguageProject languageProject) {
		this.languageProject = languageProject;
	}

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
					.newInstance(XMLLanguageProject.class);
			Unmarshaller um = context.createUnmarshaller();

			// Reading XML from the file and unmarshalling.
			xmlLanguageProjectData = (XMLLanguageProject) um
					.unmarshal(file);

			xmlLanguageProjectData.clear();
			xmlLanguageProjectData.load();

		} catch (Exception e) { // catches ANY exception
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not load data");
			alert.setContentText("Could not load data from file:\n"
					+ file.getPath());

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
					.newInstance(XMLLanguageProject.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Wrapping our person data.
			xmlLanguageProjectData = new XMLLanguageProject(languageProject);
			
			// Marshalling and saving XML to the file.
			m.marshal(xmlLanguageProjectData, file);

		} catch (Exception e) { // catches ANY exception
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not save data");
			alert.setContentText("Could not save data to file:\n"
					+ file.getPath());

			alert.showAndWait();
		}
	}

}
