// Copyright (c) 2016-2019 SIL International
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
package org.sil.syllableparser.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.StatusBar;
import org.controlsfx.dialog.FontSelectorDialog;
import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.SyllableParserException;
import org.sil.syllableparser.MainApp.TimerService;
import org.sil.syllableparser.model.ApproachType;
import org.sil.syllableparser.model.ApproachView;
import org.sil.syllableparser.model.HyphenationParameters;
import org.sil.syllableparser.model.Language;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.service.BackupFileCreator;
import org.sil.syllableparser.service.CorrectSyllabificationCleaner;
import org.sil.syllableparser.service.importexport.FLExExportedWordformsAsTabbedListImporter;
import org.sil.syllableparser.service.importexport.ListWordExporter;
import org.sil.syllableparser.service.importexport.ListWordImporter;
import org.sil.syllableparser.service.importexport.ParaTExt7SegmentImporter;
import org.sil.syllableparser.service.importexport.ParaTExtExportedWordListImporter;
import org.sil.syllableparser.service.importexport.ParaTExtHyphenatedWordsExporter;
import org.sil.syllableparser.service.importexport.ParaTExtHyphenatedWordsImporter;
import org.sil.syllableparser.service.importexport.ParaTExtSegmentImporterNoCharactersException;
import org.sil.syllableparser.service.importexport.SegmentImporterException;
import org.sil.syllableparser.service.importexport.XLingPaperHyphenatedWordExporter;
import org.sil.utility.DateTimeNormalizer;
import org.sil.utility.view.ControllerUtilities;

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;

import static javafx.geometry.Orientation.VERTICAL;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.Separator;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The controller for the root layout. The root layout provides the basic
 * application layout containing a menu bar and space where other JavaFX
 * elements can be placed.
 * 
 * @author Originally based on a tutorial by Marco Jakob:
 *         http://code.makery.ch/library/javafx-8-tutorial/part5/ but greatly
 *         expanded.
 */
public class RootLayoutController implements Initializable {

	private MainApp mainApp;
	private LanguageProject languageProject;
	private Locale currentLocale;
	@FXML
	private Button buttonCVApproach;
	@FXML
	private Button buttonSonorityHierarchyApproach;
	@FXML
	private Button buttonONCApproach;
	@FXML
	private Button buttonMoraicApproach;
	@FXML
	private Button buttonNuclearProjectionApproach;
	@FXML
	private Button buttonOTApproach;
	@FXML
	private Button buttonToolbarFileOpen;
	@FXML
	private Button buttonToolbarFileNew;
	@FXML
	private Button buttonToolbarFileSave;
	@FXML
	private Button buttonToolbarEditCut;
	@FXML
	private Button buttonToolbarEditCopy;
	@FXML
	private Button buttonToolbarEditPaste;
	@FXML
	private Button buttonToolbarEditInsert;
	@FXML
	private Button buttonToolbarEditRemove;
	@FXML
	private Button buttonToolbarSyllabify;
	@FXML
	private Button buttonToolbarConvertPredictedToCorrectSyllabification;
	@FXML
	private Button buttonToolbarFindWord;
	private Button currentButtonSelected;
	@FXML
	private Tooltip tooltipToolbarFileOpen;
	@FXML
	private Tooltip tooltipToolbarFileNew;
	@FXML
	private Tooltip tooltipToolbarFileSave;
	@FXML
	private Tooltip tooltipToolbarEditCut;
	@FXML
	private Tooltip tooltipToolbarEditCopy;
	@FXML
	private Tooltip tooltipToolbarEditPaste;
	@FXML
	private Tooltip tooltipToolbarEditInsert;
	@FXML
	private Tooltip tooltipToolbarEditRemove;
	@FXML
	private Tooltip tooltipToolbarSyllabify;
	@FXML
	private Tooltip tooltipToolbarConvertPredictedToCorrectSyllabification;
	@FXML
	private Tooltip tooltipToolbarFindWord;
	@FXML
	private MenuItem menuItemEditCopy;
	@FXML
	private MenuItem menuItemEditCut;
	@FXML
	private MenuItem menuItemEditPaste;
	@FXML
	private MenuItem menuItemEditInsert;
	@FXML
	private MenuItem menuItemEditRemove;
	@FXML
	private MenuItem menuItemSyllabify;
	@FXML
	private MenuItem menuItemTryAWord;
	@FXML
	private MenuItem menuItemConvertPredictedToCorrectSyllabification;
	@FXML
	private MenuItem menuItemFindWord;
	@FXML
	private MenuItem menuItemClearWords;
	@FXML
	private MenuItem menuItemClearCorrectSyllabificationInWords;
	@FXML
	private MenuItem menuItemCompareImplementations;
	@FXML
	private MenuItem menuItemCompareApproachSyllabifications;
	@FXML
	private MenuItem menuItemVernacularFont;
	@FXML
	private MenuItem menuItemAnalysisFont;
	@FXML
	private MenuItem menuItemCVApproach;
	@FXML
	private MenuItem menuItemSonorityHierarchyApproach;
	@FXML
	private MenuItem menuItemONCApproach;
	@FXML
	private MenuItem menuItemMoraicApproach;
	@FXML
	private MenuItem menuItemNuclearProjectionApproach;
	@FXML
	private MenuItem menuItemOTApproach;

	@FXML
	private ListView<ApproachView> approachViews;
	private CVApproachController cvApproachController;
	private SHApproachController shApproachController;
	private ONCApproachController oncApproachController;
	private ApproachController currentApproachController;

	@FXML
	private StackPane approachViewContent;

	@FXML
	StatusBar statusBar = new StatusBar();
	Label numberOfItems = new Label("0/0");

	private String syllableParserFilterDescription;

	private ResourceBundle bundle;
	private String sAboutHeader;
	private String sAboutContent;
	private String sFileFilterDescription;
	private String sChangeInterfaceLanguage;
	private String sChooseInterfaceLanguage;
	private String sChooseLanguage;
	private String sLabelUntested;

	ApplicationPreferences applicationPreferences;

	Clipboard systemClipboard = Clipboard.getSystemClipboard();

	public ToolBarCutCopyPasteDelegate toolBarDelegate;

	public RootLayoutController() {
		toolBarDelegate = new ToolBarCutCopyPasteDelegate();
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 * @param locale
	 *            TODO
	 * @param languageProject
	 *            TODO
	 */
	public void setMainApp(MainApp mainApp, Locale locale, LanguageProject languageProject) {
		this.mainApp = mainApp;
		cvApproachController.setMainApp(mainApp);
		cvApproachController.setPrefs(mainApp.getApplicationPreferences());
		cvApproachController.setRootLayout(this);
		shApproachController.setMainApp(mainApp);
		shApproachController.setPrefs(mainApp.getApplicationPreferences());
		shApproachController.setRootLayout(this);
		oncApproachController.setMainApp(mainApp);
		oncApproachController.setPrefs(mainApp.getApplicationPreferences());
		oncApproachController.setRootLayout(this);
		applicationPreferences = mainApp.getApplicationPreferences();
		this.currentLocale = locale;
		this.setLanguageProject(languageProject);
		syllableParserFilterDescription = sFileFilterDescription + " ("
				+ Constants.ASHENINKA_DATA_FILE_EXTENSIONS + ")";
		ApproachViewNavigator.setMainController(this);
		cvApproachController.setCVApproachData(languageProject.getCVApproach(),
				languageProject.getWords());
		cvApproachController.setBackupDirectoryPath(getBackupDirectoryPath());
		shApproachController.setSHApproachData(languageProject.getSHApproach(),
				languageProject.getWords());
		shApproachController.setBackupDirectoryPath(getBackupDirectoryPath());
		oncApproachController.setONCApproachData(languageProject.getONCApproach(),
				languageProject.getWords());
		oncApproachController.setBackupDirectoryPath(getBackupDirectoryPath());
	}

	@FXML
	private void handleInsertNewItem() {
		currentApproachController.handleInsertNewItem();
	}

	@FXML
	private void handleRemoveItem() {
		currentApproachController.handleRemoveItem();
	}

	@FXML
	private void handleSyllabifyWords() {
		currentApproachController.handleSyllabifyWords(statusBar);
	}

	@FXML
	private void handleTryAWord() {
		currentApproachController.handleTryAWord();
	}

	@FXML
	private void handleConvertPredictedToCorrectSyllabification() {
		currentApproachController.handleConvertPredictedToCorrectSyllabification();
	}

	@FXML
	private void handleFindWord() {
		currentApproachController.handleFindWord();
	}

	@FXML
	private void handleClearWords() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(bundle.getString("program.name"));
		alert.setHeaderText(bundle.getString("label.clearwords"));
		alert.setContentText(bundle.getString("label.backupnowbeforeclearwords"));
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(mainApp.getNewMainIconImage());

		ButtonType buttonTypeYes = ButtonType.YES;
		ButtonType buttonTypeNo = ButtonType.NO;
		ButtonType buttonTypeCancel = ButtonType.CANCEL;

		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);
		alert = localizeConfirmationButtons(alert, buttonTypeYes, buttonTypeNo, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeYes) {
			handleBackUpProject();
			clearAllWords();
		} else if (result.get() == buttonTypeNo) {
			clearAllWords();
		}

	}

	private void clearAllWords() {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				languageProject.getWords().clear();
				return null;
			}
		};
		Platform.runLater(task);
	}

	@FXML
	private void handleClearCorrectSyllabificationInWords() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(bundle.getString("program.name"));
		alert.setHeaderText(bundle.getString("label.clearcorrectsyllabificationinwords"));
		alert.setContentText(bundle.getString("label.backupnowbeforeclearcorrectsyllabification"));
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(mainApp.getNewMainIconImage());

		ButtonType buttonTypeYes = ButtonType.YES;
		ButtonType buttonTypeNo = ButtonType.NO;
		ButtonType buttonTypeCancel = ButtonType.CANCEL;

		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);
		alert = localizeConfirmationButtons(alert, buttonTypeYes, buttonTypeNo, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeYes) {
			handleBackUpProject();
			clearAllCorrectSyllabifications();
		} else if (result.get() == buttonTypeNo) {
			clearAllCorrectSyllabifications();
		}

	}

	protected Alert localizeConfirmationButtons(Alert alert, ButtonType buttonTypeYes,
			ButtonType buttonTypeNo, ButtonType buttonTypeCancel) {
		Button buttonYes = (Button) alert.getDialogPane().lookupButton(buttonTypeYes);
		buttonYes.setText(bundle.getString("label.yes"));
		Button buttonNo = (Button) alert.getDialogPane().lookupButton(buttonTypeNo);
		buttonNo.setText(bundle.getString("label.no"));
		Button buttonCancel = (Button) alert.getDialogPane().lookupButton(buttonTypeCancel);
		buttonCancel.setText(bundle.getString("label.cancel"));
		return alert;
	}

	public void clearAllCorrectSyllabifications() {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				CorrectSyllabificationCleaner cleaner = new CorrectSyllabificationCleaner(
						languageProject.getWords());
				cleaner.ClearAllCorrectSyllabificationFromWords();
				return null;
			}
		};
		Platform.runLater(task);
	}

	@FXML
	private void handleVernacularFont() {
		handleFont(mainApp.getPrimaryStage(), languageProject.getVernacularLanguage());
	}

	@FXML
	private void handleAnalysisFont() {
		handleFont(mainApp.getPrimaryStage(), languageProject.getAnalysisLanguage());
	}

	public void handleFont(Stage stage, Language lang) {
		FontSelectorDialog dlg = new FontSelectorDialog(lang.getFont());
		dlg.initOwner(stage);
		// dlg.setResult(languageProject.getVernacularFont());
		dlg.showAndWait();
		Font chosenFont = dlg.getResult();
		if (chosenFont != null) {
			Font font = lang.createFont(chosenFont.getFamily(), chosenFont.getSize(),
					chosenFont.getStyle());
			lang.setFont(font);
		}
	}

	/**
	 * Creates an empty language project.
	 */
	@FXML
	public void handleNewProject() {
		TimerService timer = mainApp.getSaveDataPeriodicallyService();
		if (timer != null) {
			mainApp.getSaveDataPeriodicallyService().cancel();
		}
		String sDirectoryPath = applicationPreferences.getLastOpenedDirectoryPath();
		if (sDirectoryPath == "") {
			// probably creating a new file the first time the program is run;
			// set the directory to the closest we can to a reasonable default
			sDirectoryPath = tryToGetDefaultDirectoryPath();
		}
		applicationPreferences.setLastOpenedDirectoryPath(sDirectoryPath);
		File fileCreated = ControllerUtilities.doFileSaveAs(mainApp, currentLocale, false,
				syllableParserFilterDescription, bundle.getString("file.new"),
				Constants.ASHENINKA_DATA_FILE_EXTENSION, Constants.ASHENINKA_DATA_FILE_EXTENSIONS,
				Constants.RESOURCE_LOCATION);
		if (fileCreated != null) {
			File file = new File(Constants.ASHENINKA_STARTER_FILE);
			mainApp.loadLanguageData(file);
			mainApp.saveLanguageData(fileCreated);
			mainApp.updateStageTitle(fileCreated);
		}
		if (timer != null) {
			mainApp.getSaveDataPeriodicallyService().restart();
		}
	}

	protected String tryToGetDefaultDirectoryPath() {
		String sDirectoryPath = System.getProperty("user.home") + File.separator;
		File dir = new File(sDirectoryPath);
		if (dir.exists()) {
			// See if there is a "Documents" directory as Windows, Linux, and
			// Mac OS X tend to have
			String sDocumentsDirectoryPath = sDirectoryPath + "Documents" + File.separator;
			dir = new File(sDocumentsDirectoryPath);
			if (dir.exists()) {
				// Try and find or make the "My Asheninka" subdirectory of
				// Documents
				String sMyAsheninkaDirectoryPath = sDocumentsDirectoryPath
						+ Constants.DEFAULT_DIRECTORY_NAME + File.separator;
				dir = new File(sMyAsheninkaDirectoryPath);
				if (dir.exists()) {
					sDirectoryPath = sMyAsheninkaDirectoryPath;
				} else {
					boolean success = (dir.mkdir());
					if (success) {
						sDirectoryPath = sMyAsheninkaDirectoryPath;
					} else {
						sDirectoryPath = sDocumentsDirectoryPath;
					}
				}
			}
		} else { // give up; let user set it
			sDirectoryPath = "";
		}
		return sDirectoryPath;
	}

	@FXML
	private void handleCut() {
		currentApproachController.handleCut();
	}

	@FXML
	private void handleToolBarCut() {
		currentApproachController.handleToolBarCut();
	}

	@FXML
	private void handleCopy() {
		currentApproachController.handleCopy();
	}

	@FXML
	private void handleToolBarCopy() {
		currentApproachController.handleToolBarCopy();
	}

	@FXML
	private void handlePaste() {
		currentApproachController.handlePaste();
	}

	@FXML
	private void handleToolBarPaste() {
		currentApproachController.handleToolBarPaste();
	}

	/**
	 * Opens a FileChooser to let the user select a language project to load.
	 */
	@FXML
	public void handleOpenProject() {
		doFileOpen(false);
	}

	public void doFileOpen(Boolean fCloseIfCanceled) {
		File file = ControllerUtilities.getFileToOpen(mainApp, syllableParserFilterDescription,
				Constants.ASHENINKA_DATA_FILE_EXTENSIONS);
		if (file != null) {
			mainApp.loadLanguageData(file);
			String sDirectoryPath = file.getParent();
			applicationPreferences.setLastOpenedDirectoryPath(sDirectoryPath);
			mainApp.updateStageTitle(file);
		} else if (fCloseIfCanceled) {
			// probably first time running and user chose to open a file
			// but then canceled. We quit.
			System.exit(0);
		}
	}

	/**
	 * Saves the file to the language project file that is currently open. If
	 * there is no open file, the "save as" dialog is shown.
	 */
	@FXML
	public void handleSaveProject() {
		File file = mainApp.getLanguageProjectFilePath();
		if (file != null) {
			mainApp.saveLanguageData(file);
		} else {
			handleSaveProjectAs();
		}
	}

	/**
	 * Opens a FileChooser to let the user select a file to save to.
	 */
	@FXML
	private void handleSaveProjectAs() {
		ControllerUtilities.doFileSaveAs(mainApp, currentLocale, false,
				syllableParserFilterDescription, null, Constants.ASHENINKA_DATA_FILE_EXTENSION,
				Constants.ASHENINKA_DATA_FILE_EXTENSIONS, Constants.RESOURCE_LOCATION);
	}

	@FXML
	private void handleBackUpProject() {
		String title = bundle.getString("label.backupproject");
		String contentText = bundle.getString("label.backupcomment");
		TextInputDialog dialog = ControllerUtilities.getTextInputDialog(mainApp, title,
				contentText, bundle);
		dialog.setResizable(true);

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String dateTimeTag = DateTimeNormalizer
					.normalizeDateTimeWithDigits(LocalDateTime.now());
			String backupDirectoryPath = getBackupDirectoryPath();
			if (!Files.exists(Paths.get(backupDirectoryPath))) {
				try {
					Files.createDirectory(Paths.get(backupDirectoryPath));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			String nameWithoutExtension = mainApp.getLanguageProjectFilePath().getName()
					.replace("." + Constants.ASHENINKA_DATA_FILE_EXTENSION, "");
			String backupFileName = backupDirectoryPath + File.separator + nameWithoutExtension
					+ dateTimeTag + "." + Constants.ASHENINKA_BACKUP_FILE_EXTENSION;
			BackupFileCreator backupCreator = new BackupFileCreator(
					mainApp.getLanguageProjectFilePath(), backupFileName, result.get());
			backupCreator.doBackup();
		}
	}

	public String getBackupDirectoryPath() {
		File projectFile = mainApp.getLanguageProjectFilePath();
		if (projectFile == null) { // hasn't been set yet
			return null;
		}
		String parentPath = projectFile.getParent();
		String backupDirectoryPath = parentPath + File.separator + Constants.BACKUP_DIRECTORY_NAME;
		return backupDirectoryPath;
	}

	@FXML
	private void handleRestoreProject() {
		try {
			// Load the fxml file and create a new stage for the popup.
			Stage dialogStage = new Stage();
			String resource = "fxml/RestoreBackupChooser.fxml";
			String title = bundle.getString("label.restoreproject");
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, currentLocale, dialogStage,
					title, ApproachViewNavigator.class.getResource(resource),
					Constants.RESOURCE_LOCATION);

			RestoreBackupChooserController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setLocale(currentLocale);
			String backupDirectoryPath = getBackupDirectoryPath();
			controller.setData(backupDirectoryPath);

			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleCompareImplementations() {
		currentApproachController.handleCompareImplementations();
	}

	@FXML
	private void handleCompareApproachSyllabifications() {
		try {
			// Load the fxml file and create a new stage for the popup.
			Stage dialogStage = new Stage();
			String resource = "fxml/SyllabificationComparison.fxml";
			String title = bundle.getString("label.comparesyllabifications");
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, currentLocale, dialogStage, title,
					ApproachViewNavigator.class.getResource(resource), Constants.RESOURCE_LOCATION);

			SyllabificationComparisonController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setLocale(currentLocale);
			controller.setData(languageProject);

			dialogStage.initModality(Modality.NONE);
			dialogStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	private void handleExportHyphenatedWordsAsASimpleList() {
		FileChooser fileChooser = ControllerUtilities.initFileChooser(mainApp,
				bundle.getString("file.exportedhyphenationlistfilterdescription") + " (*"
						+ Constants.SIMPLE_LIST_HYPHENATION_FILE_EXTENSION + ")", "*"
						+ Constants.SIMPLE_LIST_HYPHENATION_FILE_EXTENSION);

		// Show save file dialog
		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

		if (file != null) {
			// Make sure it has the correct extension
			if (!file.getPath().endsWith(Constants.SIMPLE_LIST_HYPHENATION_FILE_EXTENSION)) {
				file = new File(file.getPath() + Constants.SIMPLE_LIST_HYPHENATION_FILE_EXTENSION);
			}
			ListWordExporter exporter = new ListWordExporter(languageProject);
			exporter.exportWords(file, currentApproachController, statusBar, bundle);
		}
	}

	@FXML
	private void handleExportHyphenatedWordsForParaTExt() {
		FileChooser fileChooser = ControllerUtilities.initFileChooser(mainApp,
				Constants.PARATEXT_HYPHENATED_WORDS_TEXT_FILE,
				Constants.PARATEXT_HYPHENATED_WORDS_FILE, Constants.TEXT_FILE_EXTENSION);
		// Show save file dialog
		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

		if (file != null) {
			// Make sure it has the correct extension
			if (!file.getPath().endsWith(Constants.PARATEXT_HYPHENATED_WORDS_TEXT_FILE)) {
				file = new File(file.getPath() + Constants.PARATEXT_HYPHENATED_WORDS_TEXT_FILE);
			}
			ParaTExtHyphenatedWordsExporter exporter = new ParaTExtHyphenatedWordsExporter(
					languageProject);
			exporter.exportWords(file, currentApproachController, statusBar, bundle);
		}
	}

	@FXML
	private void handleExportHyphenatedWordsForXLingPaper() {
		FileChooser fileChooser = ControllerUtilities.initFileChooser(mainApp,
				bundle.getString("file.xlingpaperhyphenationexceptionfilterdescription") + " (*"
						+ Constants.XML_FILE_EXTENSION + ")", "*" + Constants.XML_FILE_EXTENSION);
		// Show save file dialog
		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

		if (file != null) {
			// Make sure it has the correct extension
			if (!file.getPath().endsWith(Constants.XML_FILE_EXTENSION)) {
				file = new File(file.getPath() + Constants.XML_FILE_EXTENSION);
			}
			XLingPaperHyphenatedWordExporter exporter = new XLingPaperHyphenatedWordExporter(
					languageProject);
			exporter.exportWords(file, currentApproachController, statusBar, bundle);
		}
	}

	@FXML
	private void handleHyphenationParametersSimpleList() {
		launchHyphenationParametersController("label.hyphenationparametersindesign",
				languageProject.getHyphenationParametersListWord());
	}

	protected void launchHyphenationParametersController(String sHyphenationParametersType,
			HyphenationParameters hyphenationParamaters) {
		try {
			// Load the fxml file and create a new stage for the popup.
			Stage dialogStage = new Stage();
			String resource = "fxml/HyphenationParametersChooser.fxml";
			String title = bundle.getString("label.sethyphenationparameters");
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, currentLocale, dialogStage,
					title, ApproachViewNavigator.class.getResource(resource),
					Constants.RESOURCE_LOCATION);

			Object[] args = { bundle.getString(sHyphenationParametersType) };
			MessageFormat msgFormatter = new MessageFormat("");
			msgFormatter.setLocale(currentLocale);
			msgFormatter.applyPattern(bundle.getString("label.hyphenationparameters"));
			String sParametersForMessage = msgFormatter.format(args);

			HyphenationParametersController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setData(hyphenationParamaters);
			controller.setHyphenationParametersFor(sParametersForMessage);
			dialogStage.setResizable(false);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleHyphenationParametersParaTExt() {
		launchHyphenationParametersController("label.hyphenationparametersparatext",
				languageProject.getHyphenationParametersParaTExt());
	}

	@FXML
	private void handleHyphenationParametersXLingPaper() {
		launchHyphenationParametersController("label.hyphenationparametersxlingpaper",
				languageProject.getHyphenationParametersXLingPaper());
	}

	/**
	 * Opens an about dialog.
	 */
	@FXML
	private void handleAbout() {
		sAboutHeader = bundle.getString("about.header");
		Object[] args = { Constants.VERSION_NUMBER };
		MessageFormat msgFormatter = new MessageFormat("");
		msgFormatter.setLocale(currentLocale);
		msgFormatter.applyPattern(bundle.getString("about.content"));
		sAboutContent = msgFormatter.format(args);
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(sAboutHeader);
		alert.setHeaderText(null);
		alert.setContentText(sAboutContent);
		Image silLogo = ControllerUtilities.getIconImageFromURL(
				"file:resources/images/SILLogo.png", Constants.RESOURCE_SOURCE_LOCATION);
		// Image silLogo = new
		// Image("file:src/org/sil/syllableparser/resources/images/SILLogo.png");
		alert.setGraphic(new ImageView(silLogo));
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(mainApp.getNewMainIconImage());
		alert.showAndWait();
	}

	@FXML
	private void handleUserDocumentation() {
		showFileToUser("doc/UserDocumentation.pdf");
	}

	protected void showFileToUser(String sFileToShow) {
		if (!mainApp.getOperatingSystem().equals("Mac OS X")) {
			HostServicesDelegate hostServices = HostServicesFactory.getInstance(mainApp);
			hostServices.showDocument("file:" + sFileToShow);
		} else {
			if (Desktop.isDesktopSupported()) {
				try {
					File myFile = new File(sFileToShow);
					Desktop.getDesktop().open(myFile);
				} catch (IOException ex) {
					// no application registered for PDFs
				}
			}
		}
	}

	@FXML
	private void handleSuggestedSteps() {
		showFileToUser("doc/SuggestedSteps.pdf");
	}

	@FXML
	private void handleHelpIntro() {
		showFileToUser("doc/Overview.pdf");
	}

	/**
	 * Closes the application.
	 */
	@FXML
	private void handleExit() {
		mainApp.stop();
		System.exit(0);
	}

	/**
	 * CV Approach
	 */
	@FXML
	private void handleCVApproach() {
		rememberLastApproachViewUsed();
		toggleButtonSelectedStatus(buttonCVApproach);
		approachViews.setItems(cvApproachController.getViews());
		currentApproachController = cvApproachController;
		setInitialCVView();
		setDisableForSomeMenuAndToolbarItems();
	}

	protected void setDisableForSomeMenuAndToolbarItems() {
		buttonToolbarEditInsert.setDisable(true);
		menuItemEditInsert.setDisable(true);
		buttonToolbarEditRemove.setDisable(true);
		menuItemEditRemove.setDisable(true);
		buttonToolbarSyllabify.setDisable(false);
		menuItemSyllabify.setDisable(false);
		buttonToolbarConvertPredictedToCorrectSyllabification.setDisable(false);
		menuItemConvertPredictedToCorrectSyllabification.setDisable(false);
		buttonToolbarFindWord.setDisable(false);
		menuItemFindWord.setDisable(false);
	}

	private void rememberLastApproachViewUsed() {
		if (currentApproachController == null) {
			return;
		}
		String sCurrentView = currentApproachController.getViewUsed();
		if (currentApproachController instanceof CVApproachController) {
			applicationPreferences.setLastCVApproachViewUsed(sCurrentView);
		} else if (currentApproachController instanceof SHApproachController) {
			applicationPreferences.setLastSHApproachViewUsed(sCurrentView);
		} else if (currentApproachController instanceof ONCApproachController) {
			applicationPreferences.setLastONCApproachViewUsed(sCurrentView);
		}
	}

	@FXML
	private void handleCVSegmentInventory() {
		currentApproachController = cvApproachController;
		cvApproachController.handleCVSegmentInventory();
		selectApproachViewItem(0);
	}

	@FXML
	private void handleCVNaturalClasses() {
		currentApproachController = cvApproachController;
		cvApproachController.handleCVNaturalClasses();
		selectApproachViewItem(1);
	}

	@FXML
	private void handleCVSyllablePatterns() {
		currentApproachController = cvApproachController;
		cvApproachController.handleCVSyllablePatterns();
		selectApproachViewItem(2);
	}

	@FXML
	private void handleCVWords() {
		currentApproachController = cvApproachController;
		cvApproachController.handleCVWords();
		selectApproachViewItem(3);
	}

	@FXML
	private void handleCVWordsPredictedVsCorrect() {
		currentApproachController = cvApproachController;
		cvApproachController.handleCVWordsPredictedVsCorrect();
		selectApproachViewItem(4);
	}

	@FXML
	private void handleGraphemeNaturalClasses() {
		currentApproachController = cvApproachController;
		cvApproachController.handleGraphemeNaturalClasses();
		selectApproachViewItem(5);
	}

	@FXML
	private void handleEnvironments() {
		currentApproachController = cvApproachController;
		cvApproachController.handleEnvironments();
		selectApproachViewItem(6);
	}

	@FXML
	private void handleSHSegmentInventory() {
		currentApproachController = shApproachController;
		shApproachController.handleSHSegmentInventory();
		selectApproachViewItem(0);
	}

	@FXML
	private void handleSHSonorityHierarchy() {
		currentApproachController = shApproachController;
		shApproachController.handleSHSonorityHierarchy();
		selectApproachViewItem(1);
	}

	@FXML
	private void handleSHWords() {
		currentApproachController = shApproachController;
		shApproachController.handleSHWords();
		selectApproachViewItem(2);
	}

	@FXML
	private void handleSHWordsPredictedVsCorrect() {
		currentApproachController = shApproachController;
		shApproachController.handleSHWordsPredictedVsCorrect();
		selectApproachViewItem(3);
	}

	@FXML
	private void handleSHGraphemeNaturalClasses() {
		currentApproachController = shApproachController;
		shApproachController.handleGraphemeNaturalClasses();
		selectApproachViewItem(4);
	}

	@FXML
	private void handleSHEnvironments() {
		currentApproachController = shApproachController;
		shApproachController.handleEnvironments();
		selectApproachViewItem(5);
	}

	protected void selectApproachViewItem(int iItem) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				approachViews.scrollTo(iItem);
				approachViews.getSelectionModel().select(iItem);
				approachViews.getFocusModel().focus(iItem);
			}
		});
	}

	private void toggleButtonSelectedStatus(Button myButton) {
		if (currentButtonSelected != null) {
			currentButtonSelected.setStyle("-fx-background-color: #BFD0FF;");
		}
		currentButtonSelected = myButton;
		myButton.setStyle("-fx-background-color: #F5DEB3;");
	}

	/**
	 * Sonority Hierarchy Approach
	 */
	@FXML
	private void handleSonorityHierarchyApproach() {
		rememberLastApproachViewUsed();
		toggleButtonSelectedStatus(buttonSonorityHierarchyApproach);
		approachViews.setItems(shApproachController.getViews());
		currentApproachController = shApproachController;
		setInitialSHView();
		setDisableForSomeMenuAndToolbarItems();
	}

	/**
	 * ONC Approach
	 */
	@FXML
	private void handleONCApproach() {
		rememberLastApproachViewUsed();
		toggleButtonSelectedStatus(buttonONCApproach);
		approachViews.setItems(oncApproachController.getViews());
		currentApproachController = oncApproachController;
		setInitialONCView();
		setDisableForSomeMenuAndToolbarItems();
	}

	/**
	 * Moraic Approach
	 */
	@FXML
	private void handleMoraicApproach() {
		toggleButtonSelectedStatus(buttonMoraicApproach);
		mainApp.showNotImplementedYet();
	}

	/**
	 * Nuclear Projection Approach
	 */
	@FXML
	private void handleNuclearProjectionApproach() {
		toggleButtonSelectedStatus(buttonNuclearProjectionApproach);
		mainApp.showNotImplementedYet();
	}

	/**
	 * OT Approach
	 */
	@FXML
	private void handleOTApproach() {
		toggleButtonSelectedStatus(buttonOTApproach);
		mainApp.showNotImplementedYet();
	}

	/**
	 * Change interface language.
	 */
	@FXML
	private void handleChangeInterfaceLanguage() {
		Map<String, ResourceBundle> validLocales = ControllerUtilities.getValidLocales(
				currentLocale, Constants.RESOURCE_LOCATION);

		ChoiceDialog<String> dialog = new ChoiceDialog<>(
				currentLocale.getDisplayLanguage(currentLocale), validLocales.keySet());
		dialog.setTitle(sChangeInterfaceLanguage);
		dialog.setHeaderText(sChooseInterfaceLanguage);
		dialog.setContentText(sChooseLanguage);
		Button buttonOK = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
		buttonOK.setText(bundle.getString("label.ok"));
		Button buttonCancel = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
		buttonCancel.setText(bundle.getString("label.cancel"));

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(locale -> {
			Locale selectedLocale = validLocales.get(locale).getLocale();
			bundle = validLocales.get(locale);
			if (!currentLocale.equals(selectedLocale)) {
				TimerService timer = mainApp.getSaveDataPeriodicallyService();
				if (timer != null) {
					mainApp.getSaveDataPeriodicallyService().cancel();
				}
				mainApp.setLocale(selectedLocale);
				currentLocale = selectedLocale;
				if (timer != null) {
					mainApp.getSaveDataPeriodicallyService().restart();
				}
			}
		});
	}

	@FXML
	private void handleImportPlainWordList() {
		ListWordImporter importer = new ListWordImporter(languageProject);
		File file = ControllerUtilities.getFileToOpen(mainApp,
				bundle.getString("file.plainlistimportfilterdescription") + " ("
						+ Constants.TEXT_FILE_EXTENSION + ")", Constants.TEXT_FILE_EXTENSION);
		if (file != null) {
			importer.importWords(file, sLabelUntested, statusBar, bundle, mainApp);
		}
	}

	@FXML
	private void handleImportFLExExportedWordformsAsTabbedList() {
		FLExExportedWordformsAsTabbedListImporter importer = new FLExExportedWordformsAsTabbedListImporter(
				languageProject);
		File file = ControllerUtilities.getFileToOpen(mainApp,
				bundle.getString("file.plainlistimportfilterdescription") + " ("
						+ Constants.TEXT_FILE_EXTENSION + ")", Constants.TEXT_FILE_EXTENSION);
		if (file != null) {
			importer.importWords(file, sLabelUntested, statusBar, bundle, mainApp);
		}
	}

	@FXML
	private void handleImportParaTExtWordList() {
		File file = ControllerUtilities.getFileToOpen(mainApp,
				bundle.getString("file.paratextexportedwordlistfilterdescription") + " (*"
						+ Constants.XML_FILE_EXTENSION + ")", "*" + Constants.XML_FILE_EXTENSION);
		ParaTExtExportedWordListImporter importer = new ParaTExtExportedWordListImporter(
				languageProject);
		if (file != null) {
			importer.importWords(file, "Untested", statusBar, bundle, mainApp);
		}
	}

	@FXML
	private void handleImportParaTExtHyphenatedWords() {
		ParaTExtHyphenatedWordsImporter importer = new ParaTExtHyphenatedWordsImporter(
				languageProject);
		File file = ControllerUtilities.getFileToOpen(mainApp,
				Constants.PARATEXT_HYPHENATED_WORDS_TEXT_FILE,
				Constants.PARATEXT_HYPHENATED_WORDS_FILE, Constants.TEXT_FILE_EXTENSION);
		if (file != null) {
			importer.importWords(file, sLabelUntested, statusBar, bundle, mainApp);
		}
	}

	@FXML
	private void handleImportFLExPhonemes() {
		mainApp.showNotImplementedYet();
	}

	@FXML
	private void handleImportParaTExtCharacters() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(bundle.getString("program.name"));
		alert.setHeaderText(bundle.getString("label.importparatextcharacters"));
		alert.setContentText(bundle.getString("label.backupnowbeforeimport"));
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(mainApp.getNewMainIconImage());

		ButtonType buttonTypeYes = ButtonType.YES;
		ButtonType buttonTypeNo = ButtonType.NO;
		ButtonType buttonTypeCancel = ButtonType.CANCEL;
		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);
		alert = localizeConfirmationButtons(alert, buttonTypeYes, buttonTypeNo, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeYes) {
			handleBackUpProject();
			importParaTExtCharacters();
		} else if (result.get() == buttonTypeNo) {
			importParaTExtCharacters();
		}
	}

	public void importParaTExtCharacters() {
		Stage stage;
		ParaTExt7SegmentImporter importer = new ParaTExt7SegmentImporter(languageProject);
		File file = ControllerUtilities.getFileToOpen(mainApp, "*.lds", "ParaTExt Parameters",
				"*.lds");
		if (file != null) {
			try {
				importer.importSegments(file);
			} catch (SegmentImporterException e) {
				if (e instanceof ParaTExtSegmentImporterNoCharactersException) {
					//ParaTExtSegmentImporterNoCharactersException ptex = (ParaTExtSegmentImporterNoCharactersException) e;
					// ptex.getsFileName()
					Alert errorAlert = new Alert(AlertType.ERROR);
					errorAlert.setTitle(bundle.getString("program.name"));
					errorAlert.setHeaderText(bundle
							.getString("label.importparatextcharacterserror"));
					stage = (Stage) errorAlert.getDialogPane().getScene().getWindow();
					stage.getIcons().add(mainApp.getNewMainIconImage());
					errorAlert.showAndWait();
				}
			}
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		sFileFilterDescription = bundle.getString("file.filterdescription");
		sChangeInterfaceLanguage = bundle.getString("menu.changeinterfacelanguage");
		sChooseInterfaceLanguage = bundle.getString("dialog.chooseinterfacelanguage");
		sChooseLanguage = bundle.getString("dialog.chooselanguage");
		sLabelUntested = bundle.getString("label.untested");

		cvApproachController = new CVApproachController(bundle, bundle.getLocale());
		shApproachController = new SHApproachController(bundle, bundle.getLocale());
		oncApproachController = new ONCApproachController(bundle, bundle.getLocale());

		createToolbarButtons(bundle);

		toolBarDelegate.buttonToolbarEditCut = buttonToolbarEditCut;
		toolBarDelegate.buttonToolbarEditCopy = buttonToolbarEditCopy;
		toolBarDelegate.buttonToolbarEditPaste = buttonToolbarEditPaste;

		buttonToolbarSyllabify.setDisable(true);
		menuItemSyllabify.setDisable(true);
		buttonToolbarConvertPredictedToCorrectSyllabification.setDisable(true);
		menuItemConvertPredictedToCorrectSyllabification.setDisable(true);
		buttonToolbarFindWord.setDisable(true);
		menuItemFindWord.setDisable(true);

		statusBar.getRightItems().add(new Separator(VERTICAL));
		statusBar.getRightItems().add(numberOfItems);

		ControllerUtilities.setDateInStatusBar(statusBar, bundle);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				setInitialApproachAndView();
			}
		});

		listenForChangesInApproachViews();

		// do we need this? toolBarDelegate.init();
	}

	protected void setInitialApproachAndView() {
		String sLastApproachUsed = applicationPreferences.getLastApproachUsed();
		switch (sLastApproachUsed) {
		case "CV":
			handleCVApproach();
			setInitialCVView();
			break;

		case "MORAIC":
			handleMoraicApproach();
			break;

		case "NUCLEAR_PROJECTION":
			handleNuclearProjectionApproach();
			break;

		case "ONSET_NUCLEUS_CODA":
			handleONCApproach();
			setInitialONCView();
			break;

		case "OPTIMALITY_THEORY":
			handleOTApproach();
			break;

		case "SONORITY_HIERARCHY":
			handleSonorityHierarchyApproach();
			setInitialSHView();
			break;

		default:
			handleCVApproach();
			setInitialCVView();
			break;
		}
	}

	protected void setInitialCVView() {
		String sLastCVApproachViewUsed = applicationPreferences.getLastCVApproachViewUsed();
		switch (sLastCVApproachViewUsed) {
		case "ENVIRONMENTS":
			selectApproachViewItem(6);
			break;

		case "GRAPHEME_NATURAL_CLASSES":
			selectApproachViewItem(5);
			break;

		case "NATURAL_CLASSES":
			selectApproachViewItem(1);
			break;

		case "PREDICTED_VS_CORRECT_WORDS":
			selectApproachViewItem(4);
			break;

		case "SEGMENT_INVENTORY":
			selectApproachViewItem(0);
			break;

		case "SYLLABLE_PATTERNS":
			selectApproachViewItem(2);
			break;

		case "WORDS":
			selectApproachViewItem(3);
			break;

		default:
			selectApproachViewItem(0);
			break;
		}
	}

	protected void setInitialSHView() {
		String sLastSHApproachViewUsed = applicationPreferences.getLastSHApproachViewUsed();
		switch (sLastSHApproachViewUsed) {
		case "ENVIRONMENTS":
			selectApproachViewItem(5);
			break;

		case "GRAPHEME_NATURAL_CLASSES":
			selectApproachViewItem(4);
			break;

		case "PREDICTED_VS_CORRECT_WORDS":
			selectApproachViewItem(3);
			break;

		case "SEGMENT_INVENTORY":
			selectApproachViewItem(0);
			break;

		case "SONORITY_HIERARCHY":
			selectApproachViewItem(1);
			break;

		case "WORDS":
			selectApproachViewItem(2);
			break;

		default:
			selectApproachViewItem(0);
			break;
		}
	}

	protected void setInitialONCView() {
		String sLastONCApproachViewUsed = applicationPreferences.getLastONCApproachViewUsed();
		switch (sLastONCApproachViewUsed) {
		case "ENVIRONMENTS":
			selectApproachViewItem(9);
			break;

		case "FILTERS":
			selectApproachViewItem(5);
			break;

		case "GRAPHEME_NATURAL_CLASSES":
			selectApproachViewItem(8);
			break;

		case "NATURAL_CLASSES":
			selectApproachViewItem(3);
			break;

		case "PREDICTED_VS_CORRECT_WORDS":
			selectApproachViewItem(7);
			break;

		case "SEGMENT_INVENTORY":
			selectApproachViewItem(0);
			break;

		case "SONORITY_HIERARCHY":
			selectApproachViewItem(1);
			break;

		case "SYLLABIFICATION_PARAMETERS":
			selectApproachViewItem(2);
			break;

		case "TEMPLATES":
			selectApproachViewItem(4);
			break;

		case "WORDS":
			selectApproachViewItem(6);
			break;

		default:
			selectApproachViewItem(0);
			break;
		}
	}

	protected void createToolbarButtons(ResourceBundle bundle) {
		tooltipToolbarFileNew = ControllerUtilities.createToolbarButtonWithImage("newAction.png",
				buttonToolbarFileNew, tooltipToolbarFileNew, bundle.getString("tooltip.new"),
				Constants.RESOURCE_SOURCE_LOCATION);
		tooltipToolbarFileOpen = ControllerUtilities.createToolbarButtonWithImage("openAction.png",
				buttonToolbarFileOpen, tooltipToolbarFileOpen, bundle.getString("tooltip.open"),
				Constants.RESOURCE_SOURCE_LOCATION);
		tooltipToolbarFileSave = ControllerUtilities.createToolbarButtonWithImage("saveAction.png",
				buttonToolbarFileSave, tooltipToolbarFileSave, bundle.getString("tooltip.save"),
				Constants.RESOURCE_SOURCE_LOCATION);
		tooltipToolbarEditCut = ControllerUtilities.createToolbarButtonWithImage("cutAction.png",
				buttonToolbarEditCut, tooltipToolbarEditCut, bundle.getString("tooltip.cut"),
				Constants.RESOURCE_SOURCE_LOCATION);
		tooltipToolbarEditCopy = ControllerUtilities.createToolbarButtonWithImage("copyAction.png",
				buttonToolbarEditCopy, tooltipToolbarEditCopy, bundle.getString("tooltip.copy"),
				Constants.RESOURCE_SOURCE_LOCATION);
		tooltipToolbarEditPaste = ControllerUtilities.createToolbarButtonWithImage(
				"pasteAction.png", buttonToolbarEditPaste, tooltipToolbarEditPaste,
				bundle.getString("tooltip.paste"), Constants.RESOURCE_SOURCE_LOCATION);
		tooltipToolbarEditInsert = ControllerUtilities.createToolbarButtonWithImage(
				"insertAction.png", buttonToolbarEditInsert, tooltipToolbarEditInsert,
				bundle.getString("tooltip.insertnew"), Constants.RESOURCE_SOURCE_LOCATION);
		tooltipToolbarEditRemove = ControllerUtilities.createToolbarButtonWithImage(
				"deleteAction.png", buttonToolbarEditRemove, tooltipToolbarEditRemove,
				bundle.getString("tooltip.remove"), Constants.RESOURCE_SOURCE_LOCATION);
		tooltipToolbarSyllabify = ControllerUtilities.createToolbarButtonWithImage("syllabify.png",
				buttonToolbarSyllabify, tooltipToolbarSyllabify,
				bundle.getString("tooltip.syllabifywords"), Constants.RESOURCE_SOURCE_LOCATION);
		tooltipToolbarConvertPredictedToCorrectSyllabification = ControllerUtilities
				.createToolbarButtonWithImage("predictedToCorrect.png",
						buttonToolbarConvertPredictedToCorrectSyllabification,
						tooltipToolbarConvertPredictedToCorrectSyllabification,
						bundle.getString("tooltip.convertpredictedtocorrect"),
						Constants.RESOURCE_SOURCE_LOCATION);
		tooltipToolbarFindWord = ControllerUtilities.createToolbarButtonWithImage("FindWord.png",
				buttonToolbarFindWord, tooltipToolbarFindWord,
				bundle.getString("tooltip.findword"), Constants.RESOURCE_SOURCE_LOCATION);
	}

	private void listenForChangesInApproachViews() {
		approachViews.setCellFactory((list) -> {
			return new ListCell<ApproachView>() {
				@Override
				protected void updateItem(ApproachView item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
					} else {
						setText(item.getViewName());
					}
				}
			};
		});

		// Handle ListView selection changes.
		MultipleSelectionModel<ApproachView> m = approachViews.getSelectionModel();
		ReadOnlyObjectProperty<ApproachView> p = m.selectedItemProperty();
		p.addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				try {
					Class<?> c = Class.forName(currentApproachController.getClass().getName());
					Method method = c.getDeclaredMethod(newValue.getViewHandler(),
							(Class<?>[]) null);
					method.invoke(currentApproachController, (Object[]) null);
					buttonToolbarEditInsert.setDisable(false);
					menuItemEditInsert.setDisable(false);
					buttonToolbarEditRemove.setDisable(false);
					menuItemEditRemove.setDisable(false);
				} catch (NoSuchMethodException nsme) {
					SyllableParserException spe = new SyllableParserException(newValue
							.getViewHandler()
							+ " method not found in RootLayoutController\n"
							+ nsme.getMessage());
					System.out.println(spe.getMessage());
				}

				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Replaces the approach view displayed in the view content with a new
	 * view/StackPane.
	 *
	 * @param node
	 *            the view node to be swapped in.
	 */
	public void setApproachView(Node node) {
		approachViewContent.getChildren().setAll(node);
	}

	/**
	 * @return the languageProject
	 */
	public LanguageProject getLanguageProject() {
		return languageProject;
	}

	public String getApproachUsed() {
		String sApproach = "unknown";
		String sClass = currentApproachController.getClass().getName();
		switch (sClass) {
		case "org.sil.syllableparser.view.CVApproachController":
			sApproach = ApproachType.CV.name();
			break;

		case "org.sil.syllableparser.view.SHApproachController":
			sApproach = ApproachType.SONORITY_HIERARCHY.name();
			break;

		case "org.sil.syllableparser.view.ONCApproachController":
			sApproach = ApproachType.ONSET_NUCLEUS_CODA.name();
			break;

		default:
			break;
		}
		return sApproach;
	}

	public ApproachType getCurrentApproach() {
		ApproachType approach = ApproachType.CV;
		String sClass = currentApproachController.getClass().getName();
		switch (sClass) {
		case "org.sil.syllableparser.view.CVApproachController":
			approach = ApproachType.CV;
			break;

		case "org.sil.syllableparser.view.SHApproachController":
			approach = ApproachType.SONORITY_HIERARCHY;
			break;

		case "org.sil.syllableparser.view.ONCApproachController":
			approach = ApproachType.ONSET_NUCLEUS_CODA;
			break;

		default:
			break;
		}
		return approach;
	}

	public String getViewUsed() {
		return currentApproachController.getViewUsed();
	}

	/**
	 * @param languageProject
	 *            the languageProject to set
	 */
	public void setLanguageProject(LanguageProject languageProject) {
		this.languageProject = languageProject;
	}

	public void setNumberOfItems(String numberOfItems) {
		this.numberOfItems.setText(numberOfItems);
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	@FXML
	public void handleShowingEditMenu() {
		if (systemClipboard == null) {
			systemClipboard = Clipboard.getSystemClipboard();
		}

		if (systemClipboard.hasString()) {
			adjustForClipboardContents();
		} else {
			adjustForEmptyClipboard();
		}

		if (currentApproachController.anythingSelected()) {
			adjustForSelection();

		} else {
			adjustForDeselection();
		}
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	public void adjustForEmptyClipboard() {
		menuItemEditPaste.setDisable(true); // nothing to paste
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	private void adjustForClipboardContents() {
		menuItemEditPaste.setDisable(false); // something to paste
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	private void adjustForSelection() {
		menuItemEditCut.setDisable(false);
		menuItemEditCopy.setDisable(false);
	}

	// code taken from
	// http://bekwam.blogspot.com/2014/10/cut-copy-and-paste-from-javafx-menubar.html
	private void adjustForDeselection() {
		menuItemEditCut.setDisable(true);
		menuItemEditCopy.setDisable(true);
	}

}
