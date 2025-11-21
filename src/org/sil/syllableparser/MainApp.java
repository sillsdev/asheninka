// Copyright (c) 2016-2025 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
package org.sil.syllableparser;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.service.DatabaseMigrator;
import org.sil.utility.MainAppUtilities;
import org.sil.utility.StringUtilities;
import org.sil.utility.view.ControllerUtilities;
import org.sil.syllableparser.view.ApproachViewNavigator;
import org.sil.syllableparser.view.CreateOpenRestorePromptController;
import org.sil.syllableparser.view.CreateOpenRestorePromptController.Result;
import org.sil.syllableparser.view.RootLayoutController;

import javafx.application.Application;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainApp extends Application implements MainAppUtilities {

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
			ControllerUtilities.adjustMenusIfNeeded(sOperatingSystem, rootLayout);

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
				boolean fSucceeded = useCreateOpenRestorePrompt(bundle, controller);
				if (!fSucceeded) {
					System.exit(0);
				}
			}

			updateStatusBarNumberOfItems("");

			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, null);
		} catch (Exception e) {
			e.printStackTrace();
			MainApp.reportException(e, null);
		}
	}

	protected boolean useCreateOpenRestorePrompt(ResourceBundle bundle,
			RootLayoutController controller) {
		boolean fSucceeded = true;
		Result result = Result.CANCEL;
		try {
			Stage useCreateOpenRestoreDialogStage = new Stage();
			String resource = "fxml/CreateOpenRestorePrompt.fxml";
			String title = bundle.getString("program.name");
			FXMLLoader loader = ControllerUtilities.getLoader(this, locale, useCreateOpenRestoreDialogStage,
					title, ApproachViewNavigator.class.getResource(resource),
					Constants.RESOURCE_LOCATION);
			CreateOpenRestorePromptController corController = loader.getController();
			corController.setDialogStage(useCreateOpenRestoreDialogStage);
			useCreateOpenRestoreDialogStage.showAndWait();
			result = corController.getResult();
			switch (result) {
			case CANCEL:
				System.exit(0);
				break;
			case CREATE:
				controller.handleNewProject();
				if (languageProject.getActiveSegmentsInInventory().size() == 0) {
					// The user canceled creating a new project
					fSucceeded = false;
				}
				break;
			case OPEN:
				controller.doFileOpen(true);
				break;
			case RESTORE:
				DirectoryChooser directoryChooser = new DirectoryChooser();
				File file = directoryChooser.showDialog(primaryStage);
				if (file != null) {
					if (!controller.restoreProject(file.getPath())) {
						System.exit(0);
					}
				} else {
					System.exit(0);
				}
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, null);
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
			String sLangName = languageProject.getVernacularLanguage().getLanguageName();
			String sCode = languageProject.getVernacularLanguage().getCode();
			if (!StringUtilities.isNullOrEmpty(sLangName)) {
				if (!StringUtilities.isNullOrEmpty(sCode)) {
					primaryStage.setTitle(kApplicationTitle + " - " + sLangName + " - " + sCode + " (" + sFileNameToUse + ")");
				} else {
					primaryStage.setTitle(kApplicationTitle + " - " + sLangName + " (" + sFileNameToUse + ")");
				}
			} else {
				primaryStage.setTitle(kApplicationTitle + " - " + sFileNameToUse);
			}
		} else {
			primaryStage.setTitle(kApplicationTitle);
		}
	}

	public void saveFile(File file) {
		saveLanguageData(file);
	}

	public void saveLanguageData(File file) {
		xmlBackEndProvider.saveLanguageDataToFile(file);
		applicationPreferences.setLastOpenedFilePath(file);
		applicationPreferences.setLastOpenedDirectoryPath(file.getParent());
	}

	public LanguageProject getLanguageProject() {
		return languageProject;
	}

	public String getStyleFromColor(Color textColor) {
		String sRGB= StringUtilities.toRGBCode(textColor);
		String sVernacular = Constants.TEXT_COLOR_CSS_BEGIN + sRGB + Constants.TEXT_COLOR_CSS_END;
		return sVernacular;
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
		Image img = ControllerUtilities.getIconImageFromURL(kApplicationIconResource,
				Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);
		return img;
	}

	public ApplicationPreferences getApplicationPreferences() {
		return applicationPreferences;
	}

	public String getOperatingSystem() {
		return sOperatingSystem;
	}

	public void updateStatusBarNumberOfItems(String sNumberOfItems) {
		controller.setPredictedToTotal("");
		controller.setPredictedEqualsCorrectToTotal("");
		controller.setNumberOfItems(sNumberOfItems);
	}

	public void updateStatusBarWordItems(String sPredictedToTotal,
			String sPredictedEqualsCorrectToTotal, String sNumberOfItems) {
		controller.setPredictedToTotal(sPredictedToTotal);
		controller.setPredictedEqualsCorrectToTotal(sPredictedEqualsCorrectToTotal);
		controller.setNumberOfItems(sNumberOfItems);
	}

	public RootLayoutController getController() {
		return controller;
	}

	public static void reportException(Exception ex, ResourceBundle bundle) {
		String sTitle = "Error Found!";
		String sHeader = "A serious error happened.";
		String sContent = "Please copy the exception information below, email it to asheninka_support_lsdev@sil.org along with a description of what you were doing.";
		String sLabel = "The exception stacktrace was:";
		if (bundle != null) {
			sTitle = bundle.getString("exception.title");
			sHeader = bundle.getString("exception.header");
			sContent = bundle.getString("exception.content");
			sLabel = bundle.getString("exception.label");
		}
		ControllerUtilities.showExceptionInErrorDialog(ex, sTitle, sHeader, sContent, sLabel);
		System.exit(1);
	}
}
