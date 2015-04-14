package sil.org.syllableparser;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import sil.org.syllableparser.view.RootLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
    private BorderPane rootLayout;
	private Locale locale;
	public static String kApplicationTitle = "Syllable Parser";
    
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle(kApplicationTitle);
		
		String x = ApplicationPreferences.getLastLocaleLanguage();
		String y = ApplicationPreferences.getLastLocaleCountry();
		
		locale = new Locale(ApplicationPreferences.getLastLocaleLanguage()); 
		
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
            controller.setMainApp(this, locale);

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
}
