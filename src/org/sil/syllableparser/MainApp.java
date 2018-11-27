// Copyright (c) 2016-2017 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
package org.sil.syllableparser;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.service.DatabaseMigrator;
import org.sil.syllableparser.view.ControllerUtilities;
import org.sil.syllableparser.view.RootLayoutController;

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
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

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
	private String sOperatingSystem = System.getProperty("os.name");

	public static String kApplicationTitle = "Asheninka";
	// private static String kLanguageProjectFilePath =
	// "languageProjectFilePath";
	private RootLayoutController controller;
	private String sNotImplementedYetHeader;
	private String sNotImplementedYetContent;
	private ApplicationPreferences applicationPreferences;
	private TimerService saveDataPeriodicallyService;

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
		restoreWindowSettings();

		initRootLayout();

		saveDataPeriodically(Constants.SAVE_DATA_PERIODICITY);

	}

	private void restoreWindowSettings() {
		primaryStage = applicationPreferences.getLastWindowParameters(
				ApplicationPreferences.LAST_WINDOW, primaryStage, 660.0, 1000.);
	}

	public TimerService getSaveDataPeriodicallyService() {
		return saveDataPeriodicallyService;
	}

	public void saveDataPeriodically(int duration) {
		saveDataPeriodicallyService = new TimerService();
		saveDataPeriodicallyService.setPeriod(Duration.seconds(duration));
		saveDataPeriodicallyService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent t) {
				File file = getLanguageProjectFilePath();
				if (file != null && saveDataPeriodicallyService.isRunning()) {
					saveLanguageData(file);
				}
			}
		});
		saveDataPeriodicallyService.start();
	}

	public static class TimerService extends ScheduledService<Void> {

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
		applicationPreferences.setLastWindowParameters(ApplicationPreferences.LAST_WINDOW,
				primaryStage);
		applicationPreferences.setLastLocaleLanguage(locale.getLanguage());
		applicationPreferences.setLastApproachUsed(controller.getApproachUsed());
		applicationPreferences.setLastApproachViewUsed(controller.getViewUsed());
		controller.handleSaveProject();
		if (saveDataPeriodicallyService != null) {
			saveDataPeriodicallyService.cancel();
		}
	}

	public void setLocale(Locale locale) {

		this.locale = locale;
		primaryStage.close();
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
			if (getOperatingSystem().equals("Mac OS X")) {
				adjustMenusForMacOSX();
			}

			sNotImplementedYetHeader = bundle.getString("misc.niyheader");
			sNotImplementedYetContent = bundle.getString("misc.niycontent");

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			controller = loader.getController();
			controller.setMainApp(this, locale, languageProject);

			// Try to load last opened file.
			File file = applicationPreferences.getLastOpenedFile();
			if (file != null && file.exists()) {
				loadLanguageData(file);
			} else {
				boolean fSucceeded = askUserForNewOrToOpenExistingFile(bundle, controller);
				if (!fSucceeded) {
					System.exit(0);
				}
			}

			updateStatusBarNumberOfItems("");

			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("non-IO Exception caught!");
			e.printStackTrace();
		}
	}

	protected void adjustMenusForMacOSX() {
		VBox vbox = (VBox) rootLayout.getChildren().get(0);
		MenuBar menuBar = (MenuBar) vbox.getChildren().get(0);
		menuBar.useSystemMenuBarProperty().set(true);
	}

	protected boolean askUserForNewOrToOpenExistingFile(ResourceBundle bundle,
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

		boolean fSucceeded = true;
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonCreateNewProject) {
			controller.handleNewProject();
			if (languageProject.getActiveSegmentsInInventory().size() == 0) {
				// The user canceled creating a new project
				fSucceeded = false;
			}
		} else if (result.get() == buttonOpenExistingProject) {
			controller.doFileOpen(true);
		} else {
			// ... user chose CANCEL or closed the dialog
			System.exit(0);
		}
		return fSucceeded;
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
		DatabaseMigrator migrator = new DatabaseMigrator(file);
		int version = migrator.getVersion();
		if (version < Constants.CURRENT_DATABASE_VERSION) {
			migrator.doMigration();
		}
		xmlBackEndProvider.loadLanguageDataFromFile(file);
		applicationPreferences.setLastOpenedFilePath(file);
		applicationPreferences.setLastOpenedDirectoryPath(file.getParent());
		updateStageTitle(file);
	}

	public void updateStageTitle(File file) {
		if (file != null) {
			String sFileNameToUse = file.getName().replace(
					"." + Constants.ASHENINKA_DATA_FILE_EXTENSION, "");
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

	public String getOperatingSystem() {
		return sOperatingSystem;
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
