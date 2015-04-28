package sil.org.syllableparser;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.CVApproach;
import sil.org.syllableparser.model.CVSegment;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.view.RootLayoutController;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
    private BorderPane rootLayout;
	private Locale locale;
	private XMLBackEndProvider xmlBackEndProvider;
	private LanguageProject languageProject;
	
	public static String kApplicationTitle = "Syllable Parser";
	private static String kLanguageProjectFilePath = "languageProjectFilePath";
	
		@Override
	public void start(Stage primaryStage) {
		languageProject = new LanguageProject();
		xmlBackEndProvider = new XMLBackEndProvider(languageProject);
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle(kApplicationTitle);
		
		ApplicationPreferences.getLastLocaleLanguage();
		ApplicationPreferences.getLastLocaleCountry();
		
		locale = new Locale(ApplicationPreferences.getLastLocaleLanguage()); 
		
		CVApproach cvApproach = languageProject.getCVApproach();
		ObservableList<CVSegment> cvSegmentInventoryData = cvApproach.getCVSegmentInventory();
	    cvSegmentInventoryData.add(new CVSegment("a", "a A", "low mid unrounded vowel"));
	    cvSegmentInventoryData.add(new CVSegment("b", "b B", "voiced bilabial stop"));
	    cvSegmentInventoryData.add(new CVSegment("d", "d D", "voiced alveolar stop"));


		initRootLayout();
		
	}

	@Override
	public void stop() {
		ApplicationPreferences.setLastLocaleLanguage(locale.getLanguage());
		// TODO: add last file opened
	}

	public void setLocale(Locale locale) {
		
		this.locale = locale;
		initRootLayout();
	}

	public static void main(String[] args) {
		launch(args);
	}
	 /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            loader.setResources(ResourceBundle.getBundle("sil.org.syllableparser.resources.SyllableParser", locale));
            rootLayout = (BorderPane) loader.load();
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
         // Give the controller access to the main app.
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this, locale, languageProject);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
		return primaryStage;
	}
    
    public void loadLanguageData(File file) {
    	xmlBackEndProvider.loadLanguageDataFromFile(file);
    	setLanguageProjectFilePath(file);
    }
    public void saveLanguageData(File file) {
    	xmlBackEndProvider.saveLanguageDataToFile(file);
    	setLanguageProjectFilePath(file);
    }
	/**
	 * Returns the languge project file preference, i.e. the file that was last opened.
	 * The preference is read from the OS specific registry. If no such
	 * preference can be found, null is returned.
	 * 
	 * @return
	 */
	public File getLanguageProjectFilePath() {
	    Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    String filePath = prefs.get(kLanguageProjectFilePath, null);
	    if (filePath != null) {
	        return new File(filePath);
	    } else {
	        return null;
	    }
	}

	/**
	 * Sets the file path of the currently loaded file. The path is persisted in
	 * the OS specific registry.
	 * 
	 * @param file the file or null to remove the path
	 */
	public void setLanguageProjectFilePath(File file) {
	    Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    if (file != null) {
	        prefs.put(kLanguageProjectFilePath, file.getPath());

	        // Update the stage title.
	        primaryStage.setTitle(kApplicationTitle + " - " + file.getName());
	    } else {
	        prefs.remove("filePath");

	        // Update the stage title.
	        primaryStage.setTitle(kApplicationTitle);
	    }
	}
}
