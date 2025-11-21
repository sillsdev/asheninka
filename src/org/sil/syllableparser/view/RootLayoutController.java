// Copyright (c) 2016-2025 SIL International
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
import org.sil.utility.service.keyboards.KeyboardChanger;
import org.sil.utility.view.ControllerUtilities;
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
	private final String kMacOSInstallDirectory = "/Applications/Asheninka.app/Contents/app/";
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
	@FXML
	private Button buttonToolbarFilterCorrectSyllabifications;
	@FXML
	private Button buttonToolbarFilterPredictedSyllabifications;
	@FXML
	private Button buttonToolbarFilterWords;
	@FXML
	private Button buttonToolbarRemoveAllFilters;
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
	private Tooltip tooltipToolbarFilterCorrectSyllabifications;
	@FXML
	private Tooltip tooltipToolbarFilterPredictedSyllabifications;
	@FXML
	private Tooltip tooltipToolbarFilterWords;
	@FXML
	private Tooltip tooltipToolbarRemoveAllFilters;
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
	private MenuItem menuItemEditPrevious;
	@FXML
	private MenuItem menuItemEditNext;
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
	private MenuItem menuItemFilterWords;
	@FXML
	private MenuItem menuItemFilterPredictedSyllabifications;
	@FXML
	private MenuItem menuItemFilterCorrectSyllabifications;
	@FXML
	private MenuItem menuItemRemoveAllFilters;
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
	private MoraicApproachController moraicApproachController;
	private NPApproachController npApproachController;
	private OTApproachController otApproachController;
	private ApproachController currentApproachController;

	@FXML
	private StackPane approachViewContent;

	@FXML
	StatusBar statusBar = new StatusBar();
	Label numberOfItems = new Label("0/0");
	Label predictedToTotal = new Label("0/0 (0%)");
	Label predictedEqualsCorrectToTotal = new Label("0/0 (0%)");

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
	KeyboardChanger keyboardChanger;

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
		keyboardChanger.setStage(mainApp.getPrimaryStage());
		cvApproachController.setMainApp(mainApp);
		cvApproachController.setPrefs(mainApp.getApplicationPreferences());
		cvApproachController.setRootLayout(this);
		shApproachController.setMainApp(mainApp);
		shApproachController.setPrefs(mainApp.getApplicationPreferences());
		shApproachController.setRootLayout(this);
		oncApproachController.setMainApp(mainApp);
		oncApproachController.setPrefs(mainApp.getApplicationPreferences());
		oncApproachController.setRootLayout(this);
		moraicApproachController.setMainApp(mainApp);
		moraicApproachController.setPrefs(mainApp.getApplicationPreferences());
		moraicApproachController.setRootLayout(this);
		npApproachController.setMainApp(mainApp);
		npApproachController.setPrefs(mainApp.getApplicationPreferences());
		npApproachController.setRootLayout(this);
		otApproachController.setMainApp(mainApp);
		otApproachController.setPrefs(mainApp.getApplicationPreferences());
		otApproachController.setRootLayout(this);
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
		moraicApproachController.setMoraicApproachData(languageProject.getMoraicApproach(),
				languageProject.getWords());
		moraicApproachController.setBackupDirectoryPath(getBackupDirectoryPath());
		npApproachController.setNPApproachData(languageProject.getNPApproach(),
				languageProject.getWords());
		npApproachController.setBackupDirectoryPath(getBackupDirectoryPath());
		otApproachController.setOTApproachData(languageProject.getOTApproach(),
				languageProject.getWords());
		otApproachController.setBackupDirectoryPath(getBackupDirectoryPath());
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
	private void handlePreviousItem() {
		currentApproachController.handlePreviousItem();
	}

	@FXML
	private void handleNextItem() {
		currentApproachController.handleNextItem();
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
	private void handleFilterCorrectSyllabifications() {
		currentApproachController.handleFilterCorrectSyllabifications();
	}

	@FXML
	private void handleFilterPredictedSyllabifications() {
		currentApproachController.handleFilterPredictedSyllabifications();
	}

	@FXML
	private void handleFilterWords() {
		currentApproachController.handleFilterWords();
	}

	@FXML
	private void handleRemoveAllFilters() {
		currentApproachController.handleRemoveAllFilters();
	}

	@FXML
	private void handleFindWord() {
		keyboardChanger = KeyboardChanger.getInstance();
		keyboardChanger.tryToChangeKeyboardTo(languageProject.getVernacularLanguage().getKeyboard(), MainApp.class);
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
	private void handleVernacularWritingSystem() {
		launchWritingSystemController(languageProject.getVernacularLanguage(), "label.vernacularwritingsystem");
		File file = applicationPreferences.getLastOpenedFile();
		mainApp.updateStageTitle(file);
	}

	protected void launchWritingSystemController(Language language, String titleResource) {
		try {
			// Load the fxml file and create a new stage for the popup.
			Stage dialogStage = new Stage();
			String resource = "fxml/WritingSystem.fxml";
			String title = bundle.getString(titleResource);
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, currentLocale, dialogStage,
					title, ApproachViewNavigator.class.getResource(resource),
					bundle);
			WritingSystemController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setData(language);
			dialogStage.setResizable(false);
			dialogStage.showAndWait();
			if (controller.isOkClicked()) {
				currentApproachController.toggleView();
			}
		} catch (IOException | CloneNotSupportedException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
		}
	}

	@FXML
	private void handleAnalysisWritingSystem() {
		launchWritingSystemController(languageProject.getAnalysisLanguage(), "label.analysiswritingsystem");
	}

	public void handleFont(Stage stage, Language lang) {
		Font tempFont = lang.getFont();
		FontSelectorDialog dlg = new FontSelectorDialog(tempFont);
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
		keyboardChanger = KeyboardChanger.getInstance();
		keyboardChanger.tryToChangeKeyboardTo(languageProject.getAnalysisLanguage().getKeyboard(), MainApp.class);

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String dateTimeTag = DateTimeNormalizer
					.normalizeDateTimeWithDigits(LocalDateTime.now());
			String backupDirectoryPath = getBackupDirectoryPath();
			if (!Files.exists(Paths.get(backupDirectoryPath))) {
				try {
					Files.createDirectory(Paths.get(backupDirectoryPath));
				} catch (IOException e) {
					e.printStackTrace();
					MainApp.reportException(e, bundle);
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
	public void handleRestoreProject() {
		String backupDirectoryPath = getBackupDirectoryPath();
		processRestoreProject(backupDirectoryPath);
	}

	protected boolean processRestoreProject(String backupDirectoryPath) {
		try {
			// Load the fxml file and create a new stage for the popup.
			Stage dialogStage = new Stage();
			String resource = "fxml/RestoreBackupChooser.fxml";
			String title = bundle.getString("label.restoreproject");
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, currentLocale, dialogStage,
					title, ApproachViewNavigator.class.getResource(resource),
					bundle);

			RestoreBackupChooserController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.initializeTableColumnWidths(mainApp.getApplicationPreferences());
			controller.setLocale(currentLocale);
			controller.setData(backupDirectoryPath);

			dialogStage.showAndWait();
			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
			return false;
		}
	}

	public boolean restoreProject(String sBackupDirectoryPath) {
		return processRestoreProject(sBackupDirectoryPath);
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
					ApproachViewNavigator.class.getResource(resource), bundle);

			SyllabificationComparisonController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setLocale(currentLocale);
			controller.setData(languageProject);

			dialogStage.initModality(Modality.NONE);
			dialogStage.show();
		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
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
					bundle);

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
			keyboardChanger.tryToChangeKeyboardTo(languageProject.getAnalysisLanguage().getKeyboard(), MainApp.class);

			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, bundle);
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
				"file:resources/images/SILLogo.png", Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);
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
		if (Desktop.isDesktopSupported()) {
			try {
				File myFile = new File(sFileToShow);
				if (!myFile.exists()) {
					// this can happen on Linux
					String sUriOfProgram = ControllerUtilities.getUriOfProgram(MainApp.class);
					String sPathToTry = sUriOfProgram.substring(5) + sFileToShow;
					myFile = new File(sPathToTry);
				}
				String sOS = mainApp.getOperatingSystem().toLowerCase();
				if (sOS.contains("linux")) {
					Runtime.getRuntime().exec(new String[] { "xdg-open", myFile.getAbsolutePath() });
				} else if (sOS.contains("mac")) {
					if (!myFile.exists()) {
						String sFullPath = kMacOSInstallDirectory + sFileToShow;
						System.out
								.println("File '" + sFileToShow + "' does not exist; trying it as '" + sFullPath + "'");
						myFile = new File(sFullPath);
					}
					Desktop.getDesktop().open(myFile);
				} else {
					Desktop.getDesktop().open(myFile);
				}
			} catch (IOException ex) {
				// no application registered for PDFs
				MainApp.reportException(ex, null);
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

	@FXML
	private void handleHammond1997() {
		showFileToUser("doc/222-1097-HAMMOND-0-0.PDF");
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
		buttonToolbarFilterCorrectSyllabifications.setDisable(false);
		buttonToolbarFilterPredictedSyllabifications.setDisable(false);
		buttonToolbarFilterWords.setDisable(false);
		buttonToolbarRemoveAllFilters.setDisable(false);
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
		} else if (currentApproachController instanceof MoraicApproachController) {
			applicationPreferences.setLastMoraicApproachViewUsed(sCurrentView);
		} else if (currentApproachController instanceof NPApproachController) {
			applicationPreferences.setLastNPApproachViewUsed(sCurrentView);
		}
	}

	@FXML
	private void handleCVSegmentInventory() {
		currentApproachController = cvApproachController;
		cvApproachController.handleCVSegmentInventory();
		selectApproachViewItem(Constants.CV_SEGMENT_INVENTORY_VIEW_INDEX);
	}

	@FXML
	private void handleCVNaturalClasses() {
		currentApproachController = cvApproachController;
		cvApproachController.handleCVNaturalClasses();
		selectApproachViewItem(Constants.CV_NATURAL_CLASSES_VIEW_INDEX);
	}

	@FXML
	private void handleCVSyllablePatterns() {
		currentApproachController = cvApproachController;
		cvApproachController.handleCVSyllablePatterns();
		selectApproachViewItem(Constants.CV_SYLLABLE_PATTERNS_VIEW_INDEX);
	}

	@FXML
	private void handleCVWords() {
		currentApproachController = cvApproachController;
		cvApproachController.handleCVWords();
		selectApproachViewItem(Constants.CV_WORDS_VIEW_INDEX);
	}

	@FXML
	private void handleCVWordsPredictedVsCorrect() {
		currentApproachController = cvApproachController;
		cvApproachController.handleCVWordsPredictedVsCorrect();
		selectApproachViewItem(Constants.CV_PREDICTED_VS_CORRECT_WORDS_VIEW_INDEX);
	}

	@FXML
	private void handleCVGraphemeNaturalClasses() {
		currentApproachController = cvApproachController;
		cvApproachController.handleGraphemeNaturalClasses();
		selectApproachViewItem(Constants.CV_GRAPHEME_NATURAL_CLASSES_VIEW_INDEX);
	}

	@FXML
	private void handleCVEnvironments() {
		currentApproachController = cvApproachController;
		cvApproachController.handleEnvironments();
		selectApproachViewItem(Constants.CV_ENVIRONMENTS_VIEW_INDEX);
	}

	@FXML
	private void handleSHSegmentInventory() {
		currentApproachController = shApproachController;
		shApproachController.handleSHSegmentInventory();
		selectApproachViewItem(Constants.SH_SEGMENT_INVENTORY_VIEW_INDEX);
	}

	@FXML
	private void handleSHSonorityHierarchy() {
		currentApproachController = shApproachController;
		shApproachController.handleSHSonorityHierarchy();
		selectApproachViewItem(Constants.SH_SONORITY_HIERARCHY_VIEW_INDEX);
	}

	@FXML
	private void handleSHWords() {
		currentApproachController = shApproachController;
		shApproachController.handleSHWords();
		selectApproachViewItem(Constants.SH_WORDS_VIEW_INDEX);
	}

	@FXML
	private void handleSHWordsPredictedVsCorrect() {
		currentApproachController = shApproachController;
		shApproachController.handleSHWordsPredictedVsCorrect();
		selectApproachViewItem(Constants.SH_PREDICTED_VS_CORRECT_WORDS_VIEW_INDEX);
	}

	@FXML
	private void handleSHGraphemeNaturalClasses() {
		currentApproachController = shApproachController;
		shApproachController.handleGraphemeNaturalClasses();
		selectApproachViewItem(Constants.SH_GRAPHEME_NATURAL_CLASSES_VIEW_INDEX);
	}

	@FXML
	private void handleSHEnvironments() {
		currentApproachController = shApproachController;
		shApproachController.handleEnvironments();
		selectApproachViewItem(Constants.SH_ENVIRONMENTS_VIEW_INDEX);
	}

	public void selectApproachViewItem(int iItem) {
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

	@FXML
	private void handleONCSegmentInventory() {
		currentApproachController = oncApproachController;
		oncApproachController.handleONCSegmentInventory();
		selectApproachViewItem(Constants.ONC_SEGMENT_INVENTORY_VIEW_INDEX);
	}

	@FXML
	private void handleONCSonorityHierarchy() {
		currentApproachController = oncApproachController;
		oncApproachController.handleONCSonorityHierarchy();
		selectApproachViewItem(Constants.ONC_SONORITY_HIERARCHY_VIEW_INDEX);
	}

	@FXML
	private void handleONCNaturalClasses() {
		currentApproachController = oncApproachController;
		oncApproachController.handleCVNaturalClasses();
		selectApproachViewItem(Constants.ONC_NATURAL_CLASSES_VIEW_INDEX);
	}

	@FXML
	private void handleSyllabificationParameters() {
		currentApproachController = oncApproachController;
		oncApproachController.handleSyllabificationParameters();
		selectApproachViewItem(Constants.ONC_SYLLABIFICATION_PARAMETERS_VIEW_INDEX);
	}

	@FXML
	private void handleTemplates() {
		currentApproachController = oncApproachController;
		oncApproachController.handleTemplates();;
		selectApproachViewItem(Constants.ONC_TEMPLATES_VIEW_INDEX);
	}

	@FXML
	private void handleFilters() {
		currentApproachController = oncApproachController;
		oncApproachController.handleFilters();;
		selectApproachViewItem(Constants.ONC_FILTERS_VIEW_INDEX);
	}

	@FXML
	private void handleONCWords() {
		currentApproachController = oncApproachController;
		oncApproachController.handleONCWords();
		selectApproachViewItem(Constants.ONC_WORDS_VIEW_INDEX);
	}

	@FXML
	private void handleONCWordsPredictedVsCorrect() {
		currentApproachController = oncApproachController;
		oncApproachController.handleONCWordsPredictedVsCorrect();
		selectApproachViewItem(Constants.ONC_PREDICTED_VS_CORRECT_WORDS_VIEW_INDEX);
	}

	@FXML
	private void handleONCGraphemeNaturalClasses() {
		currentApproachController = oncApproachController;
		oncApproachController.handleGraphemeNaturalClasses();
		selectApproachViewItem(Constants.ONC_GRAPHEME_NATURAL_CLASSES_VIEW_INDEX);
	}

	@FXML
	private void handleONCEnvironments() {
		currentApproachController = oncApproachController;
		oncApproachController.handleEnvironments();
		selectApproachViewItem(Constants.ONC_ENVIRONMENTS_VIEW_INDEX);
	}

	/**
	 * Moraic Approach
	 */
	@FXML
	private void handleMoraicApproach() {
		rememberLastApproachViewUsed();
		toggleButtonSelectedStatus(buttonMoraicApproach);
		approachViews.setItems(moraicApproachController.getViews());
		currentApproachController = moraicApproachController;
		setInitialMoraicView();
		setDisableForSomeMenuAndToolbarItems();
	}

	@FXML
	private void handleMoraicSegmentInventory() {
		currentApproachController = moraicApproachController;
		moraicApproachController.handleMoraicSegmentInventory();
		selectApproachViewItem(Constants.MORAIC_SEGMENT_INVENTORY_VIEW_INDEX);
	}

	@FXML
	private void handleMoraicSonorityHierarchy() {
		currentApproachController = moraicApproachController;
		moraicApproachController.handleMoraicSonorityHierarchy();
		selectApproachViewItem(Constants.MORAIC_SONORITY_HIERARCHY_VIEW_INDEX);
	}

	@FXML
	private void handleMoraicNaturalClasses() {
		currentApproachController = moraicApproachController;
		moraicApproachController.handleCVNaturalClasses();
		selectApproachViewItem(Constants.MORAIC_NATURAL_CLASSES_VIEW_INDEX);
	}

	@FXML
	private void handleMoraicSyllabificationParameters() {
		currentApproachController = moraicApproachController;
		moraicApproachController.handleMoraicSyllabificationParameters();
		selectApproachViewItem(Constants.MORAIC_SYLLABIFICATION_PARAMETERS_VIEW_INDEX);
	}

	@FXML
	private void handleMoraicTemplates() {
		currentApproachController = moraicApproachController;
		moraicApproachController.handleTemplates();;
		selectApproachViewItem(Constants.MORAIC_TEMPLATES_VIEW_INDEX);
	}

	@FXML
	private void handleMoraicFilters() {
		currentApproachController = moraicApproachController;
		moraicApproachController.handleFilters();;
		selectApproachViewItem(Constants.MORAIC_FILTERS_VIEW_INDEX);
	}

	@FXML
	private void handleMoraicWords() {
		currentApproachController = moraicApproachController;
		moraicApproachController.handleMoraicWords();
		selectApproachViewItem(Constants.MORAIC_WORDS_VIEW_INDEX);
	}

	@FXML
	private void handleMoraicWordsPredictedVsCorrect() {
		currentApproachController = moraicApproachController;
		moraicApproachController.handleMoraicWordsPredictedVsCorrect();
		selectApproachViewItem(Constants.MORAIC_PREDICTED_VS_CORRECT_WORDS_VIEW_INDEX);
	}

	@FXML
	private void handleMoraicGraphemeNaturalClasses() {
		currentApproachController = moraicApproachController;
		moraicApproachController.handleGraphemeNaturalClasses();
		selectApproachViewItem(Constants.MORAIC_GRAPHEME_NATURAL_CLASSES_VIEW_INDEX);
	}

	@FXML
	private void handleMoraicEnvironments() {
		currentApproachController = moraicApproachController;
		moraicApproachController.handleEnvironments();
		selectApproachViewItem(Constants.MORAIC_ENVIRONMENTS_VIEW_INDEX);
	}

	/**
	 * Nuclear Projection Approach
	 */
	@FXML
	private void handleNuclearProjectionApproach() {
		rememberLastApproachViewUsed();
		toggleButtonSelectedStatus(buttonNuclearProjectionApproach);
		approachViews.setItems(npApproachController.getViews());
		currentApproachController = npApproachController;
		setInitialNPView();
		setDisableForSomeMenuAndToolbarItems();
	}

	@FXML
	private void handleNPSegmentInventory() {
		currentApproachController = npApproachController;
		npApproachController.handleNPSegmentInventory();
		selectApproachViewItem(Constants.NP_SEGMENT_INVENTORY_VIEW_INDEX);
	}

	@FXML
	private void handleNPSonorityHierarchy() {
		currentApproachController = npApproachController;
		npApproachController.handleNPSonorityHierarchy();
		selectApproachViewItem(Constants.NP_SONORITY_HIERARCHY_VIEW_INDEX);
	}

	@FXML
	private void handleNPNaturalClasses() {
		currentApproachController = npApproachController;
		npApproachController.handleCVNaturalClasses();
		selectApproachViewItem(Constants.NP_NATURAL_CLASSES_VIEW_INDEX);
	}

	@FXML
	private void handleNPSyllabificationParameters() {
		currentApproachController = npApproachController;
		npApproachController.handleNPSyllabificationParameters();
		selectApproachViewItem(Constants.NP_SYLLABIFICATION_PARAMETERS_VIEW_INDEX);
	}

	@FXML
	private void handleNPRules() {
		currentApproachController = npApproachController;
		npApproachController.handleRules();;
		selectApproachViewItem(Constants.NP_RULES_VIEW_INDEX);
	}

	@FXML
	private void handleNPFilters() {
		currentApproachController = npApproachController;
		npApproachController.handleFilters();;
		selectApproachViewItem(Constants.NP_FILTERS_VIEW_INDEX);
	}

	@FXML
	private void handleNPWords() {
		currentApproachController = npApproachController;
		npApproachController.handleNPWords();
		selectApproachViewItem(Constants.NP_WORDS_VIEW_INDEX);
	}

	@FXML
	private void handleNPWordsPredictedVsCorrect() {
		currentApproachController = npApproachController;
		npApproachController.handleNPWordsPredictedVsCorrect();
		selectApproachViewItem(Constants.NP_PREDICTED_VS_CORRECT_WORDS_VIEW_INDEX);
	}

	@FXML
	private void handleNPGraphemeNaturalClasses() {
		currentApproachController = npApproachController;
		npApproachController.handleGraphemeNaturalClasses();
		selectApproachViewItem(Constants.NP_GRAPHEME_NATURAL_CLASSES_VIEW_INDEX);
	}

	@FXML
	private void handleNPEnvironments() {
		currentApproachController = npApproachController;
		npApproachController.handleEnvironments();
		selectApproachViewItem(Constants.NP_ENVIRONMENTS_VIEW_INDEX);
	}

	/**
	 * OT Approach
	 */
	@FXML
	private void handleOTApproach() {
		rememberLastApproachViewUsed();
		toggleButtonSelectedStatus(buttonOTApproach);
		approachViews.setItems(otApproachController.getViews());
		currentApproachController = otApproachController;
		setInitialOTView();
		setDisableForSomeMenuAndToolbarItems();
	}

	@FXML
	private void handleOTSegmentInventory() {
		currentApproachController = otApproachController;
		otApproachController.handleCVSegmentInventory();
		selectApproachViewItem(Constants.OT_SEGMENT_INVENTORY_VIEW_INDEX);
	}

	@FXML
	private void handleOTNaturalClasses() {
		currentApproachController = otApproachController;
		otApproachController.handleCVNaturalClasses();
		selectApproachViewItem(Constants.OT_NATURAL_CLASSES_VIEW_INDEX);
	}

	@FXML
	private void handleOTConstraints() {
		currentApproachController = otApproachController;
		otApproachController.handleOTConstraints();
		selectApproachViewItem(Constants.OT_CONSTRAINTS_VIEW_INDEX);
	}

	@FXML
	private void handleOTConstraintRankings() {
		currentApproachController = otApproachController;
		otApproachController.handleOTConstraintRankings();
		selectApproachViewItem(Constants.OT_CONSTRAINT_RANKINGS_VIEW_INDEX);
	}

	@FXML
	private void handleOTWords() {
		currentApproachController = otApproachController;
		otApproachController.handleOTWords();
		selectApproachViewItem(Constants.OT_WORDS_VIEW_INDEX);
	}

	@FXML
	private void handleOTWordsPredictedVsCorrect() {
		currentApproachController = otApproachController;
		otApproachController.handleOTWordsPredictedVsCorrect();
		selectApproachViewItem(Constants.OT_PREDICTED_VS_CORRECT_WORDS_VIEW_INDEX);
	}

	@FXML
	private void handleOTGraphemeNaturalClasses() {
		currentApproachController = otApproachController;
		otApproachController.handleGraphemeNaturalClasses();
		selectApproachViewItem(Constants.OT_GRAPHEME_NATURAL_CLASSES_VIEW_INDEX);
	}

	@FXML
	private void handleOTEnvironments() {
		currentApproachController = otApproachController;
		otApproachController.handleEnvironments();
		selectApproachViewItem(Constants.OT_ENVIRONMENTS_VIEW_INDEX);
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
					keyboardChanger.initKeyboardHandler(MainApp.class);
					keyboardChanger.setStage(mainApp.getPrimaryStage());
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
		keyboardChanger = KeyboardChanger.getInstance();
		keyboardChanger.initKeyboardHandler(MainApp.class);

		sFileFilterDescription = bundle.getString("file.filterdescription");
		sChangeInterfaceLanguage = bundle.getString("menu.changeinterfacelanguage");
		sChooseInterfaceLanguage = bundle.getString("dialog.chooseinterfacelanguage");
		sChooseLanguage = bundle.getString("dialog.chooselanguage");
		sLabelUntested = bundle.getString("label.untested");

		cvApproachController = new CVApproachController(bundle, bundle.getLocale());
		shApproachController = new SHApproachController(bundle, bundle.getLocale());
		oncApproachController = new ONCApproachController(bundle, bundle.getLocale());
		moraicApproachController = new MoraicApproachController(bundle, bundle.getLocale());
		npApproachController = new NPApproachController(bundle, bundle.getLocale());
		otApproachController = new OTApproachController(bundle, bundle.getLocale());

		createToolbarButtons(bundle);

		toolBarDelegate.buttonToolbarEditCut = buttonToolbarEditCut;
		toolBarDelegate.buttonToolbarEditCopy = buttonToolbarEditCopy;
		toolBarDelegate.buttonToolbarEditPaste = buttonToolbarEditPaste;

		buttonToolbarSyllabify.setDisable(true);
		menuItemSyllabify.setDisable(true);
		buttonToolbarConvertPredictedToCorrectSyllabification.setDisable(true);
		menuItemConvertPredictedToCorrectSyllabification.setDisable(true);
		buttonToolbarFindWord.setDisable(true);
		buttonToolbarFilterCorrectSyllabifications.setDisable(true);
		buttonToolbarFilterPredictedSyllabifications.setDisable(true);
		buttonToolbarFilterWords.setDisable(true);
		buttonToolbarRemoveAllFilters.setDisable(true);
		menuItemFindWord.setDisable(true);

		statusBar.getRightItems().add(new Separator(VERTICAL));
		Tooltip tooltipPredictedToTotal = new Tooltip(bundle.getString("tooltip.predictedtototal"));
		Tooltip.install(predictedToTotal, tooltipPredictedToTotal);
		statusBar.getRightItems().add(predictedToTotal);
		statusBar.getRightItems().add(new Separator(VERTICAL));
		Tooltip tooltipPredictedEqualsCorrectToTotal = new Tooltip(bundle.getString("tooltip.predictedequalscorrecttototal"));
		Tooltip.install(predictedEqualsCorrectToTotal, tooltipPredictedEqualsCorrectToTotal);
		statusBar.getRightItems().add(predictedEqualsCorrectToTotal);
		statusBar.getRightItems().add(new Separator(VERTICAL));
		Tooltip tooltipNumberOfItemsToTotal = new Tooltip(bundle.getString("tooltip.numbertototal"));
		Tooltip.install(numberOfItems, tooltipNumberOfItemsToTotal);
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
			setInitialMoraicView();
			break;

		case "NUCLEAR_PROJECTION":
			handleNuclearProjectionApproach();
			setInitialNPView();
			break;

		case "ONSET_NUCLEUS_CODA":
			handleONCApproach();
			setInitialONCView();
			break;

		case "OPTIMALITY_THEORY":
			handleOTApproach();
			setInitialOTView();
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
			selectApproachViewItem(Constants.CV_ENVIRONMENTS_VIEW_INDEX);
			break;

		case "GRAPHEME_NATURAL_CLASSES":
			selectApproachViewItem(Constants.CV_GRAPHEME_NATURAL_CLASSES_VIEW_INDEX);
			break;

		case "NATURAL_CLASSES":
			selectApproachViewItem(Constants.CV_NATURAL_CLASSES_VIEW_INDEX);
			break;

		case "PREDICTED_VS_CORRECT_WORDS":
			selectApproachViewItem(Constants.CV_PREDICTED_VS_CORRECT_WORDS_VIEW_INDEX);
			break;

		case "SEGMENT_INVENTORY":
			selectApproachViewItem(Constants.CV_SEGMENT_INVENTORY_VIEW_INDEX);
			break;

		case "SYLLABLE_PATTERNS":
			selectApproachViewItem(Constants.CV_SYLLABLE_PATTERNS_VIEW_INDEX);
			break;

		case "WORDS":
			selectApproachViewItem(Constants.CV_WORDS_VIEW_INDEX);
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
			selectApproachViewItem(Constants.SH_ENVIRONMENTS_VIEW_INDEX);
			break;

		case "GRAPHEME_NATURAL_CLASSES":
			selectApproachViewItem(Constants.SH_GRAPHEME_NATURAL_CLASSES_VIEW_INDEX);
			break;

		case "PREDICTED_VS_CORRECT_WORDS":
			selectApproachViewItem(Constants.SH_PREDICTED_VS_CORRECT_WORDS_VIEW_INDEX);
			break;

		case "SEGMENT_INVENTORY":
			selectApproachViewItem(Constants.SH_SEGMENT_INVENTORY_VIEW_INDEX);
			break;

		case "SONORITY_HIERARCHY":
			selectApproachViewItem(Constants.SH_SONORITY_HIERARCHY_VIEW_INDEX);
			break;

		case "WORDS":
			selectApproachViewItem(Constants.SH_WORDS_VIEW_INDEX);
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
			selectApproachViewItem(Constants.ONC_ENVIRONMENTS_VIEW_INDEX);
			break;

		case "FILTERS":
			selectApproachViewItem(Constants.ONC_FILTERS_VIEW_INDEX);
			break;

		case "GRAPHEME_NATURAL_CLASSES":
			selectApproachViewItem(Constants.ONC_GRAPHEME_NATURAL_CLASSES_VIEW_INDEX);
			break;

		case "NATURAL_CLASSES":
			selectApproachViewItem(Constants.ONC_NATURAL_CLASSES_VIEW_INDEX);
			break;

		case "PREDICTED_VS_CORRECT_WORDS":
			selectApproachViewItem(Constants.ONC_PREDICTED_VS_CORRECT_WORDS_VIEW_INDEX);
			break;

		case "SEGMENT_INVENTORY":
			selectApproachViewItem(Constants.ONC_SEGMENT_INVENTORY_VIEW_INDEX);
			break;

		case "SONORITY_HIERARCHY":
			selectApproachViewItem(Constants.ONC_SONORITY_HIERARCHY_VIEW_INDEX);
			break;

		case "SYLLABIFICATION_PARAMETERS":
			selectApproachViewItem(Constants.ONC_SYLLABIFICATION_PARAMETERS_VIEW_INDEX);
			break;

		case "TEMPLATES":
			selectApproachViewItem(Constants.ONC_TEMPLATES_VIEW_INDEX);
			break;

		case "WORDS":
			selectApproachViewItem(Constants.ONC_WORDS_VIEW_INDEX);
			break;

		default:
			selectApproachViewItem(0);
			break;
		}
	}

	protected void setInitialMoraicView() {
		String sLastMoraicApproachViewUsed = applicationPreferences.getLastMoraicApproachViewUsed();
		switch (sLastMoraicApproachViewUsed) {
		case "ENVIRONMENTS":
			selectApproachViewItem(Constants.MORAIC_ENVIRONMENTS_VIEW_INDEX);
			break;

		case "FILTERS":
			selectApproachViewItem(Constants.MORAIC_FILTERS_VIEW_INDEX);
			break;

		case "GRAPHEME_NATURAL_CLASSES":
			selectApproachViewItem(Constants.MORAIC_GRAPHEME_NATURAL_CLASSES_VIEW_INDEX);
			break;

		case "NATURAL_CLASSES":
			selectApproachViewItem(Constants.MORAIC_NATURAL_CLASSES_VIEW_INDEX);
			break;

		case "PREDICTED_VS_CORRECT_WORDS":
			selectApproachViewItem(Constants.MORAIC_PREDICTED_VS_CORRECT_WORDS_VIEW_INDEX);
			break;

		case "SEGMENT_INVENTORY":
			selectApproachViewItem(Constants.MORAIC_SEGMENT_INVENTORY_VIEW_INDEX);
			break;

		case "SONORITY_HIERARCHY":
			selectApproachViewItem(Constants.MORAIC_SONORITY_HIERARCHY_VIEW_INDEX);
			break;

		case "SYLLABIFICATION_PARAMETERS":
			selectApproachViewItem(Constants.MORAIC_SYLLABIFICATION_PARAMETERS_VIEW_INDEX);
			break;

		case "TEMPLATES":
			selectApproachViewItem(Constants.MORAIC_TEMPLATES_VIEW_INDEX);
			break;

		case "WORDS":
			selectApproachViewItem(Constants.MORAIC_WORDS_VIEW_INDEX);
			break;

		default:
			selectApproachViewItem(0);
			break;
		}
	}

	protected void setInitialNPView() {
		String sLastNPApproachViewUsed = applicationPreferences.getLastNPApproachViewUsed();
		switch (sLastNPApproachViewUsed) {
		case "ENVIRONMENTS":
			selectApproachViewItem(Constants.NP_ENVIRONMENTS_VIEW_INDEX);
			break;

		case "FILTERS":
			selectApproachViewItem(Constants.NP_FILTERS_VIEW_INDEX);
			break;

		case "GRAPHEME_NATURAL_CLASSES":
			selectApproachViewItem(Constants.NP_GRAPHEME_NATURAL_CLASSES_VIEW_INDEX);
			break;

		case "NATURAL_CLASSES":
			selectApproachViewItem(Constants.NP_NATURAL_CLASSES_VIEW_INDEX);
			break;

		case "PREDICTED_VS_CORRECT_WORDS":
			selectApproachViewItem(Constants.NP_PREDICTED_VS_CORRECT_WORDS_VIEW_INDEX);
			break;

		case "RULES":
			selectApproachViewItem(Constants.NP_RULES_VIEW_INDEX);
			break;

		case "SEGMENT_INVENTORY":
			selectApproachViewItem(Constants.NP_SEGMENT_INVENTORY_VIEW_INDEX);
			break;

		case "SONORITY_HIERARCHY":
			selectApproachViewItem(Constants.NP_SONORITY_HIERARCHY_VIEW_INDEX);
			break;

		case "SYLLABIFICATION_PARAMETERS":
			selectApproachViewItem(Constants.NP_SYLLABIFICATION_PARAMETERS_VIEW_INDEX);
			break;

		case "WORDS":
			selectApproachViewItem(Constants.NP_WORDS_VIEW_INDEX);
			break;

		default:
			selectApproachViewItem(0);
			break;
		}
	}

	protected void setInitialOTView() {
		String sLastOTApproachViewUsed = applicationPreferences.getLastOTApproachViewUsed();
		switch (sLastOTApproachViewUsed) {
		case "CONSTRAINTS":
			selectApproachViewItem(Constants.OT_CONSTRAINTS_VIEW_INDEX);
			break;

		case "ENVIRONMENTS":
			selectApproachViewItem(Constants.OT_ENVIRONMENTS_VIEW_INDEX);
			break;

		case "GRAPHEME_NATURAL_CLASSES":
			selectApproachViewItem(Constants.OT_GRAPHEME_NATURAL_CLASSES_VIEW_INDEX);
			break;

		case "NATURAL_CLASSES":
			selectApproachViewItem(Constants.OT_NATURAL_CLASSES_VIEW_INDEX);
			break;

		case "PREDICTED_VS_CORRECT_WORDS":
			selectApproachViewItem(Constants.OT_PREDICTED_VS_CORRECT_WORDS_VIEW_INDEX);
			break;

		case "RANKINGS":
			selectApproachViewItem(Constants.OT_CONSTRAINT_RANKINGS_VIEW_INDEX);
			break;

		case "SEGMENT_INVENTORY":
			selectApproachViewItem(Constants.OT_SEGMENT_INVENTORY_VIEW_INDEX);
			break;

		case "WORDS":
			selectApproachViewItem(Constants.OT_WORDS_VIEW_INDEX);
			break;

		default:
			selectApproachViewItem(0);
			break;
		}
	}

	protected void createToolbarButtons(ResourceBundle bundle) {
		tooltipToolbarFileNew = ControllerUtilities.createToolbarButtonWithImage("newAction.png",
				buttonToolbarFileNew, tooltipToolbarFileNew, bundle.getString("tooltip.new"),
				Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);
		tooltipToolbarFileOpen = ControllerUtilities.createToolbarButtonWithImage("openAction.png",
				buttonToolbarFileOpen, tooltipToolbarFileOpen, bundle.getString("tooltip.open"),
				Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);
		tooltipToolbarFileSave = ControllerUtilities.createToolbarButtonWithImage("saveAction.png",
				buttonToolbarFileSave, tooltipToolbarFileSave, bundle.getString("tooltip.save"),
				Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);
		tooltipToolbarEditCut = ControllerUtilities.createToolbarButtonWithImage("cutAction.png",
				buttonToolbarEditCut, tooltipToolbarEditCut, bundle.getString("tooltip.cut"),
				Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);
		tooltipToolbarEditCopy = ControllerUtilities.createToolbarButtonWithImage("copyAction.png",
				buttonToolbarEditCopy, tooltipToolbarEditCopy, bundle.getString("tooltip.copy"),
				Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);
		tooltipToolbarEditPaste = ControllerUtilities.createToolbarButtonWithImage(
				"pasteAction.png", buttonToolbarEditPaste, tooltipToolbarEditPaste,
				bundle.getString("tooltip.paste"), Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);
		tooltipToolbarEditInsert = ControllerUtilities.createToolbarButtonWithImage(
				"insertAction.png", buttonToolbarEditInsert, tooltipToolbarEditInsert,
				bundle.getString("tooltip.insertnew"), Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);
		tooltipToolbarEditRemove = ControllerUtilities.createToolbarButtonWithImage(
				"deleteAction.png", buttonToolbarEditRemove, tooltipToolbarEditRemove,
				bundle.getString("tooltip.remove"), Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);
		tooltipToolbarSyllabify = ControllerUtilities.createToolbarButtonWithImage("syllabify.png",
				buttonToolbarSyllabify, tooltipToolbarSyllabify,
				bundle.getString("tooltip.syllabifywords"), Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);
		tooltipToolbarConvertPredictedToCorrectSyllabification = ControllerUtilities
				.createToolbarButtonWithImage("predictedToCorrect.png",
						buttonToolbarConvertPredictedToCorrectSyllabification,
						tooltipToolbarConvertPredictedToCorrectSyllabification,
						bundle.getString("tooltip.convertpredictedtocorrect"),
						Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);
		tooltipToolbarFindWord = ControllerUtilities.createToolbarButtonWithImage("FindWord.png",
				buttonToolbarFindWord, tooltipToolbarFindWord,
				bundle.getString("tooltip.findword"), Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);
		tooltipToolbarFilterCorrectSyllabifications = ControllerUtilities.createToolbarButtonWithImage("icons8-filter-64-Correct.png",
				buttonToolbarFilterCorrectSyllabifications, tooltipToolbarFilterCorrectSyllabifications,
				bundle.getString("tooltip.filtercorrectsyllablebreaks"), Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);
		buttonToolbarFilterCorrectSyllabifications = adjustImageSize(buttonToolbarFilterCorrectSyllabifications);
		tooltipToolbarFilterPredictedSyllabifications = ControllerUtilities.createToolbarButtonWithImage("icons8-filter-64-Predicted.png",
				buttonToolbarFilterPredictedSyllabifications, tooltipToolbarFilterPredictedSyllabifications,
				bundle.getString("tooltip.filterpredictedsyllablebreaks"), Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);
		buttonToolbarFilterPredictedSyllabifications = adjustImageSize(buttonToolbarFilterPredictedSyllabifications);
		tooltipToolbarFilterWords = ControllerUtilities.createToolbarButtonWithImage("icons8-filter-64.png",
				buttonToolbarFilterWords, tooltipToolbarFilterWords,
				bundle.getString("tooltip.filterwords"), Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);
		buttonToolbarFilterWords = adjustImageSize(buttonToolbarFilterWords);
		tooltipToolbarRemoveAllFilters = ControllerUtilities.createToolbarButtonWithImage("icons8-clear-filters-64.png",
				buttonToolbarRemoveAllFilters, tooltipToolbarRemoveAllFilters,
				bundle.getString("tooltip.removeallfilters"), Constants.RESOURCE_SOURCE_LOCATION, MainApp.class);
		buttonToolbarRemoveAllFilters = adjustImageSize(buttonToolbarRemoveAllFilters);
	}

	private Button adjustImageSize(Button button) {
		Node n = button.getGraphic();
		if (n instanceof ImageView) {
			ImageView image = (ImageView) n;
			image.setPreserveRatio(true);
			image.setFitHeight(16);
			button.setGraphic(image);
		}
		return button;
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
					e.printStackTrace();
					MainApp.reportException(e, bundle);
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

		case "org.sil.syllableparser.view.MoraicApproachController":
			sApproach = ApproachType.MORAIC.name();
			break;

		case "org.sil.syllableparser.view.NPApproachController":
			sApproach = ApproachType.NUCLEAR_PROJECTION.name();
			break;

		case "org.sil.syllableparser.view.OTApproachController":
			sApproach = ApproachType.OPTIMALITY_THEORY.name();
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

		case "org.sil.syllableparser.view.MoraicApproachController":
			approach = ApproachType.MORAIC;
			break;

		case "org.sil.syllableparser.view.NPApproachController":
			approach = ApproachType.NUCLEAR_PROJECTION;
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

	public void setNumberOfItems(String sNumberOfItems) {
		if (sNumberOfItems.equals("")) {
			statusBar.getRightItems().get(4).setVisible(false);
		} else {
			statusBar.getRightItems().get(4).setVisible(true);
		}
		this.numberOfItems.setText(sNumberOfItems);
	}

	public void setPredictedToTotal(String sPredictedToTotal) {
		if (sPredictedToTotal.equals("")) {
			statusBar.getRightItems().get(0).setVisible(false);
		} else {
			statusBar.getRightItems().get(0).setVisible(true);
		}
		this.predictedToTotal.setText(sPredictedToTotal);
	}

	public void setPredictedEqualsCorrectToTotal(String sPredictedEqualsCorrectToTotal) {
		if (sPredictedEqualsCorrectToTotal.equals("")) {
			statusBar.getRightItems().get(2).setVisible(false);
		} else {
			statusBar.getRightItems().get(2).setVisible(true);
		}
		this.predictedEqualsCorrectToTotal.setText(sPredictedEqualsCorrectToTotal);
	}

	public void setFiltersDisabled(boolean disable, boolean isPredictedVsCorrect) {
		buttonToolbarFilterWords.setDisable(disable);
		menuItemFilterWords.setDisable(disable);
		if (isPredictedVsCorrect) {
			buttonToolbarFilterCorrectSyllabifications.setDisable(true);
			buttonToolbarFilterPredictedSyllabifications.setDisable(true);
			menuItemFilterCorrectSyllabifications.setDisable(true);
			menuItemFilterPredictedSyllabifications.setDisable(true);
		} else {
			buttonToolbarFilterCorrectSyllabifications.setDisable(disable);
			buttonToolbarFilterPredictedSyllabifications.setDisable(disable);
			menuItemFilterCorrectSyllabifications.setDisable(disable);
			menuItemFilterPredictedSyllabifications.setDisable(disable);
		}
		buttonToolbarRemoveAllFilters.setDisable(disable);
		menuItemRemoveAllFilters.setDisable(disable);
	}

	public void setFiltersDisabled(boolean disable) {
		setFiltersDisabled(disable, false);
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
