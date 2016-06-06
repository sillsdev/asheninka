package sil.org.syllableparser;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.syllableparser.view.ControllerUtilities;
import sil.org.syllableparser.view.RootLayoutController;

public class MainApp extends Application {

	/**
	 * 
	 */
	private static final String kApplicationIconResource = "file:resources/images/CushmaSmall128x128.png";
	private Stage primaryStage;
	private BorderPane rootLayout;
	private Locale locale;
	private XMLBackEndProvider xmlBackEndProvider;
	private LanguageProject languageProject;

	public static String kApplicationTitle = "Asheninka";
	// private static String kLanguageProjectFilePath =
	// "languageProjectFilePath";
	private RootLayoutController controller;
	private String sNotImplementedYetHeader;
	private String sNotImplementedYetContent;
	private ApplicationPreferences applicationPreferences;

	// private Image mainIconImage;

	@Override
	public void start(Stage primaryStage) {
		applicationPreferences = new ApplicationPreferences(this);
		locale = new Locale(applicationPreferences.getLastLocaleLanguage());

		languageProject = new LanguageProject();
		xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle(kApplicationTitle);
		this.primaryStage.getIcons().add(getNewMainIconImage());

		initRootLayout();

		saveDataPeriodically(30);

	}

	public void saveDataPeriodically(int duration) {
		TimerService service = new TimerService();
		service.setPeriod(Duration.seconds(duration));
		service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent t) {
				File file = getLanguageProjectFilePath();
				if (file != null) {
					saveLanguageData(file);
				}
			}
		});
		service.start();
	}

	private static class TimerService extends ScheduledService<Void> {

		protected Task<Void> createTask() {
			return new Task<Void>() {
				protected Void call() {
					// System.out.println("in TimerService");
					return null;
				}
			};
		}
	}

	@Override
	public void stop() {
		applicationPreferences.setLastLocaleLanguage(locale.getLanguage());
		controller.handleSave();
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
			ResourceBundle bundle = ResourceBundle.getBundle(Constants.RESOURCE_LOCATION, locale);
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
			File file = applicationPreferences.getLastOpenedFile();
			if (file != null) {
				loadLanguageData(file);
			} else {
				askUserForNewOrToOpenExisingFile(bundle, controller);
			}

			updateStatusBarNumberOfItems("");

			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void askUserForNewOrToOpenExisingFile(ResourceBundle bundle,
			RootLayoutController controller) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(bundle.getString("program.name"));
		alert.setHeaderText(bundle.getString("file.initiallynotfound"));
		alert.setContentText(bundle.getString("file.chooseanoption"));
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(getNewMainIconImage());

		ButtonType buttonCreateNewProject = new ButtonType(
				bundle.getString("label.createnewproject"));
		ButtonType buttonOpenExistingProject = new ButtonType(
				bundle.getString("label.openexistingproject"));
		ButtonType buttonCancel = new ButtonType(bundle.getString("label.cancel"),
				ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonCreateNewProject, buttonOpenExistingProject,
				buttonCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonCreateNewProject) {
			controller.handleNew();
		} else if (result.get() == buttonOpenExistingProject) {
			controller.handleOpen();
		} else {
			// ... user chose CANCEL or closed the dialog
			System.exit(0);
		}
	}

	/**
	 * Returns the main stage.
	 * 
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void loadLanguageData(File file) {
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		applicationPreferences.setLastOpenedFilePath(file);
		applicationPreferences.setLastOpenedDirectoryPath(file.getParent());
		updateStageTitle(file);
	}

	public void updateStageTitle(File file) {
		if (file != null) {
			String sFileNameToUse = file.getName().replace("." + Constants.ASHENINKA_DATA_FILE_EXTENSION, "");
			primaryStage.setTitle(kApplicationTitle + " - " + sFileNameToUse);
		} else {
			primaryStage.setTitle(kApplicationTitle);
		}
	}

	public void saveLanguageData(File file) {
		xmlBackEndProvider.saveLanguageDataToFile(file);
		applicationPreferences.setLastOpenedFilePath(file);
		applicationPreferences.setLastOpenedDirectoryPath(file.getParent());
	}

	public LanguageProject getLanguageProject() {
		return languageProject;
	}

	/**
	 * Returns the language project file preference, i.e. the file that was last
	 * opened. The preference is read from the OS specific registry. If no such
	 * preference can be found, null is returned.
	 * 
	 * @return
	 */
	public File getLanguageProjectFilePath() {
		String filePath = applicationPreferences.getLastOpenedFilePath();
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
		Image img = ControllerUtilities.getIconImageFromURL(kApplicationIconResource);
		return img;
	}

	public ApplicationPreferences getApplicationPreferences() {
		return applicationPreferences;
	}

	public void updateStatusBarNumberOfItems(String numberOfItems) {
		if (numberOfItems != null) {
			controller.setNumberOfItems(numberOfItems);
		} else {
			controller.setNumberOfItems("");
		}
	}

	public RootLayoutController getController() {
		return controller;
	}

}
