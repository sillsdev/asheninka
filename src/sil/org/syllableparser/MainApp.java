package sil.org.syllableparser;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.syllableparser.model.cvapproach.CVSegment;
import sil.org.syllableparser.view.RootLayoutController;

public class MainApp extends Application {

	/**
	 * 
	 */
	private static final String kApplicationIconResource = "file:src/sil/org/syllableparser/resources/images/CushmaSmall128x128.png";
	private Stage primaryStage;
    private BorderPane rootLayout;
	private Locale locale;
	private XMLBackEndProvider xmlBackEndProvider;
	private LanguageProject languageProject;
	
	public static String kApplicationTitle = "Asheninka";
	//private static String kLanguageProjectFilePath = "languageProjectFilePath";
	private RootLayoutController controller;
	private String sNotImplementedYetHeader;
	private String sNotImplementedYetContent;
	//private Image mainIconImage;
	
		@Override
	public void start(Stage primaryStage) {
		locale = new Locale(ApplicationPreferences.getLastLocaleLanguage()); 
			
		languageProject = new LanguageProject();
		xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle(kApplicationTitle);
		this.primaryStage.getIcons().add(getNewMainIconImage());
		
		ApplicationPreferences.getLastLocaleLanguage();
		ApplicationPreferences.getLastLocaleCountry();
		
		final boolean usePrecannedData = false;
		if (usePrecannedData) {
			CVApproach cvApproach = languageProject.getCVApproach();
			ObservableList<CVSegment> cvSegmentInventoryData = cvApproach.getCVSegmentInventory();
			CVSegment segA = new CVSegment("a", "a A", "low mid unrounded vowel");
			CVSegment segB = new CVSegment("b", "b B", "voiced bilabial stop");
			CVSegment segD = new CVSegment("d", "d D", "voiced alveolar stop");
			cvSegmentInventoryData.add(segA);
			cvSegmentInventoryData.add(segB);
			cvSegmentInventoryData.add(segD);
			ObservableList<CVNaturalClass> cvNaturalClassData = cvApproach.getCVNaturalClasses();
			ObservableList<Object> consonants = new SimpleListProperty<Object>();
			consonants.add(segB);
			consonants.add(segD);
			cvNaturalClassData.add(new CVNaturalClass("C", (SimpleListProperty<Object>) consonants, "Consonants", "bd"));
			ObservableList<Object> vowels = new SimpleListProperty<Object>();
			vowels.add(segA);
			cvNaturalClassData.add(new CVNaturalClass("V", (SimpleListProperty<Object>) vowels, "Vowels", "a"));
		}

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
            loader.setLocation(MainApp.class.getResource("view/fxml/RootLayout.fxml"));
            ResourceBundle bundle = ResourceBundle.getBundle("sil.org.syllableparser.resources.SyllableParser", locale); 
            loader.setResources(bundle);
            rootLayout = (BorderPane) loader.load();
            
    		sNotImplementedYetHeader = bundle.getString("misc.niyheader");
    		sNotImplementedYetContent = bundle.getString("misc.niycontent");

            
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            controller = loader.getController();
            controller.setMainApp(this, locale, languageProject);

         // Try to load last opened file.
            File file = ApplicationPreferences.getLastOpenedFile();
            if (file != null) {
            	loadLanguageData(file);
            }
            
            updateStatusBarNumberOfItems("");
            
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
    	ApplicationPreferences.setLastOpenedFilePath(file);
    	ApplicationPreferences.setLastOpenedDirectoryPath(file.getParent());
	    updateStageTitle(file);
	}

	public void updateStageTitle(File file) {
		if (file != null) {
	        primaryStage.setTitle(kApplicationTitle + " - " + file.getName());
	    } else {
	        primaryStage.setTitle(kApplicationTitle);
	    }
	}
    public void saveLanguageData(File file) {
    	xmlBackEndProvider.saveLanguageDataToFile(file);
    	ApplicationPreferences.setLastOpenedFilePath(file);
    	ApplicationPreferences.setLastOpenedDirectoryPath(file.getParent());
	}
	public LanguageProject getLanguageProject() {
		return languageProject;
	}

	/**
	 * Returns the languge project file preference, i.e. the file that was last opened.
	 * The preference is read from the OS specific registry. If no such
	 * preference can be found, null is returned.
	 * 
	 * @return
	 */
	public File getLanguageProjectFilePath() {
	//Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    String filePath = ApplicationPreferences.getLastOpenedFilePath(); //prefs.get(kLanguageProjectFilePath, null);
	    if (filePath != null) {
	        return new File(filePath);
	    } else {
	        return null;
	    }
	}

	public void showNotImplementedYet() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(MainApp.kApplicationTitle);
		alert.setHeaderText(sNotImplementedYetHeader);
		alert.setContentText(sNotImplementedYetContent);
		
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(getNewMainIconImage());
	
		alert.showAndWait();
	}

	/**
	 * @return the mainIconImage
	 */
	public Image getNewMainIconImage() {
		return new Image(kApplicationIconResource);
	}


	public void updateStatusBarNumberOfItems(String numberOfItems) {
		if (numberOfItems != null) {
	        controller.setNumberOfItems(numberOfItems);
	    } else {
	    	controller.setNumberOfItems("");
	    }
	}

}
