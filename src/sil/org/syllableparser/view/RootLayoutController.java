package sil.org.syllableparser.view;

import java.awt.Desktop;
import java.awt.TextField;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.controlsfx.control.StatusBar;
import org.controlsfx.dialog.FontSelectorDialog;

import static javafx.geometry.Orientation.VERTICAL;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sil.org.syllableparser.ApplicationPreferences;
import sil.org.syllableparser.Constants;
import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.SyllableParserException;
import sil.org.syllableparser.model.ApproachView;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.service.FLExExportedWordformsAsTabbedListImporter;
import sil.org.syllableparser.service.ListWordExporter;
import sil.org.syllableparser.service.ListWordImporter;
import sil.org.syllableparser.service.ParaTExtExportedWordListImporter;
import sil.org.syllableparser.service.ParaTExtHyphenatedWordsExporter;
import sil.org.syllableparser.service.ParaTExtHyphenatedWordsImporter;
import sil.org.syllableparser.service.XLingPaperHyphenatedWordExporter;

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
	private MenuItem menuItemEditInsert;
	@FXML
	private MenuItem menuItemEditRemove;
	@FXML
	private MenuItem menuItemSyllabify;
	@FXML
	private MenuItem menuItemConvertPredictedToCorrectSyllabification;
	@FXML
	private MenuItem menuItemFindWord;
	@FXML
	private MenuItem menuItemVernacularFont;
	@FXML
	private MenuItem menuItemAnalysisFont;

	@FXML
	private ListView<ApproachView> approachViews;
	private CVApproachController cvApproachController;
	private ONCApproachController oncApproachController;
	private ApproachController currentApproachController;

	@FXML
	private StackPane approachViewContent;

	@FXML
	StatusBar statusBar = new StatusBar();
	Label numberOfItems = new Label("0/0");

	private static String kSyllableParserDataExtension = Constants.ASHENINKA_DATA_FILE_EXTENSION;
	private String syllableParserFilterDescription;
	private String syllableParserFilterExtensions;

	private ResourceBundle bundle;
	private String sAboutHeader;
	private String sAboutContent;
	private String sFileFilterDescription;
	private String sChangeInterfaceLanguage;
	private String sChooseInterfaceLanguage;
	private String sChooseLanguage;
	private String sLabelUntested;

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
		this.currentLocale = locale;
		this.setLanguageProject(languageProject);
		syllableParserFilterDescription = sFileFilterDescription + " (*."
				+ kSyllableParserDataExtension + ")";
		syllableParserFilterExtensions = "*." + kSyllableParserDataExtension;
		ApproachViewNavigator.setMainController(this);
		cvApproachController.setCVApproachData(languageProject.getCVApproach(),
				languageProject.getWords());
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
	private void handleConvertPredictedToCorrectSyllabification() {
		currentApproachController.handleConvertPredictedToCorrectSyllabification();
	}

	@FXML
	private void handleFindWord() {
		currentApproachController.handleFindWord();
	}

	@FXML
	private void handleVernacularFont() {
		languageProject.getVernacularLanguage().handleFont(mainApp.getPrimaryStage());
	}

	@FXML
	private void handleAnalysisFont() {
		languageProject.getAnalysisLanguage().handleFont(mainApp.getPrimaryStage());
	}

	/**
	 * Creates an empty language project.
	 */
	@FXML
	private void handleNew() {
		// mainApp.getLanguageProject().clear();
		// ApplicationPreferences.setLastOpenedFilePath((String) null);
		String sDirectoryPath = ApplicationPreferences.getLastOpenedDirectoryPath();
		File file = new File(Constants.ASHENINKA_STARTER_FILE);
		mainApp.loadLanguageData(file);
		ApplicationPreferences.setLastOpenedDirectoryPath(sDirectoryPath);
		handleSaveAs();
	}

	@FXML
	private void handleCut() {
		// TODO
	}

	@FXML
	private void handleCopy() {
		// TODO
	}

	@FXML
	private void handlePaste() {
		// TODO
	}

	/**
	 * Opens a FileChooser to let the user select a language project to load.
	 */
	@FXML
	private void handleOpen() {
		File file = ControllerUtilities.getFileToOpen(mainApp, syllableParserFilterDescription,
				syllableParserFilterExtensions);
		if (file != null) {
			mainApp.loadLanguageData(file);
			String sDirectoryPath = file.getParent();
			ApplicationPreferences.setLastOpenedDirectoryPath(sDirectoryPath);
			mainApp.updateStageTitle(file);
		}
	}

	/**
	 * Saves the file to the language project file that is currently open. If
	 * there is no open file, the "save as" dialog is shown.
	 */
	@FXML
	private void handleSave() {
		File file = mainApp.getLanguageProjectFilePath();
		if (file != null) {
			mainApp.saveLanguageData(file);
		} else {
			handleSaveAs();
		}
	}

	/**
	 * Opens a FileChooser to let the user select a file to save to.
	 */
	@FXML
	private void handleSaveAs() {
		FileChooser fileChooser = ControllerUtilities.initFileChooser(
				syllableParserFilterDescription, syllableParserFilterExtensions);

		// Show save file dialog
		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

		if (file != null) {
			// Make sure it has the correct extension
			if (!file.getPath().endsWith(kSyllableParserDataExtension)) {
				file = new File(file.getPath() + kSyllableParserDataExtension);
			}
			mainApp.saveLanguageData(file);
			String sDirectoryPath = file.getParent();
			ApplicationPreferences.setLastOpenedDirectoryPath(sDirectoryPath);
			mainApp.updateStageTitle(file);
		}
	}

	@FXML
	private void handleExportHyphenatedWordsAsASimpleList() {
		FileChooser fileChooser = ControllerUtilities.initFileChooser(
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
		FileChooser fileChooser = ControllerUtilities.initFileChooser(
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
		FileChooser fileChooser = ControllerUtilities.initFileChooser(
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
		Image silLogo = new Image("file:src/sil/org/syllableparser/resources/images/SILLogo.png");
		alert.setGraphic(new ImageView(silLogo));
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(mainApp.getNewMainIconImage());
		alert.showAndWait();
	}

	@FXML
	private void handleHelpIntro() {
		if (Desktop.isDesktopSupported()) {
			try {
				File myFile = new File("doc/Overview.pdf");
				Desktop.getDesktop().open(myFile);
			} catch (IOException ex) {
				// no application registered for PDFs
			}
		}
	}

	/**
	 * Closes the application.
	 */
	@FXML
	private void handleExit() {
		System.exit(0);
	}

	@FXML
	private void handleApproachClick() {

	}

	/**
	 * CV Approach
	 */
	@FXML
	private void handleCVApproach() {
		toggleButtonSelectedStatus(buttonCVApproach);
		approachViews.setItems(cvApproachController.getViews());
		currentApproachController = cvApproachController;
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
		toggleButtonSelectedStatus(buttonSonorityHierarchyApproach);
		mainApp.showNotImplementedYet();
	}

	/**
	 * ONC Approach
	 */
	@FXML
	private void handleONCApproach() {
		toggleButtonSelectedStatus(buttonONCApproach);
		mainApp.showNotImplementedYet();
		// approachViews.setItems(oncApproachController.getViews());
		// currentApproachController = oncApproachController;
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

		Map<String, ResourceBundle> validLocales = new TreeMap<String, ResourceBundle>();
		getListOfValidLocales(validLocales);

		ChoiceDialog<String> dialog = new ChoiceDialog<>(
				currentLocale.getDisplayLanguage(currentLocale), validLocales.keySet());
		dialog.setTitle(sChangeInterfaceLanguage);
		dialog.setHeaderText(sChooseInterfaceLanguage);
		dialog.setContentText(sChooseLanguage);

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(locale -> mainApp.setLocale(validLocales.get(locale).getLocale()));

	}

	private void getListOfValidLocales(Map<String, ResourceBundle> choices) {
		Locale[] locales = Locale.getAvailableLocales();
		for (Locale locale : locales) {
			ResourceBundle rb = ResourceBundle.getBundle(
					"sil.org.syllableparser.resources.SyllableParser", locale);
			if (rb != null) {
				String localeName = rb.getLocale().getDisplayName(currentLocale);
				choices.putIfAbsent(localeName, rb);
			}
		}
	}

	@FXML
	private void handleImportPlainWordList() {
		ListWordImporter importer = new ListWordImporter(languageProject);
		File file = ControllerUtilities.getFileToOpen(mainApp,
				bundle.getString("file.plainlistimportfilterdescription") + " ("
						+ Constants.TEXT_FILE_EXTENSION + ")", Constants.TEXT_FILE_EXTENSION);
		if (file != null) {
			importer.importWords(file, sLabelUntested, statusBar, bundle);
		}
	}

	@FXML
	private void handleImportFLExExportedWordformsAsTabbedList() {
		FLExExportedWordformsAsTabbedListImporter importer = new FLExExportedWordformsAsTabbedListImporter(languageProject);
		File file = ControllerUtilities.getFileToOpen(mainApp,
				bundle.getString("file.plainlistimportfilterdescription") + " ("
						+ Constants.TEXT_FILE_EXTENSION + ")", Constants.TEXT_FILE_EXTENSION);
		if (file != null) {
			importer.importWords(file, sLabelUntested, statusBar, bundle);
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
			importer.importWords(file, "Untested", statusBar, bundle);
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
			// Scene scene = approachViewContent.getScene();
			importer.importWords(file, sLabelUntested, statusBar, bundle);
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
		oncApproachController = new ONCApproachController(bundle);

		createToolbarButtons(bundle);
		buttonToolbarSyllabify.setDisable(true);
		menuItemSyllabify.setDisable(true);
		buttonToolbarConvertPredictedToCorrectSyllabification.setDisable(true);
		menuItemConvertPredictedToCorrectSyllabification.setDisable(true);
		buttonToolbarFindWord.setDisable(true);
		menuItemFindWord.setDisable(true);

		statusBar.getRightItems().add(new Separator(VERTICAL));
		statusBar.getRightItems().add(numberOfItems);

		ControllerUtilities.setDateInStatusBar(statusBar, bundle);

		// set initial approach (TODO: make it be based on user's last choice)
		handleCVApproach();

		listenForChangesInApproachViews();

	}

	protected void createToolbarButtons(ResourceBundle bundle) {
		ControllerUtilities.createToolbarButtonWithImage("newAction.png", buttonToolbarFileNew,
				tooltipToolbarFileNew, bundle.getString("menu.new"));
		ControllerUtilities.createToolbarButtonWithImage("openAction.png", buttonToolbarFileOpen,
				tooltipToolbarFileOpen, bundle.getString("menu.open"));
		ControllerUtilities.createToolbarButtonWithImage("saveAction.png", buttonToolbarFileSave,
				tooltipToolbarFileSave, bundle.getString("menu.save"));
		ControllerUtilities.createToolbarButtonWithImage("cutAction.png", buttonToolbarEditCut,
				tooltipToolbarEditCut, bundle.getString("menu.cut"));
		ControllerUtilities.createToolbarButtonWithImage("copyAction.png", buttonToolbarEditCopy,
				tooltipToolbarEditCopy, bundle.getString("menu.copy"));
		ControllerUtilities.createToolbarButtonWithImage("pasteAction.png", buttonToolbarEditPaste,
				tooltipToolbarEditPaste, bundle.getString("menu.paste"));
		ControllerUtilities.createToolbarButtonWithImage("insertAction.png",
				buttonToolbarEditInsert, tooltipToolbarEditInsert,
				bundle.getString("menu.insertnew"));
		ControllerUtilities.createToolbarButtonWithImage("deleteAction.png",
				buttonToolbarEditRemove, tooltipToolbarEditRemove, bundle.getString("menu.remove"));
		ControllerUtilities.createToolbarButtonWithImage("syllabify.png", buttonToolbarSyllabify,
				tooltipToolbarSyllabify, bundle.getString("menu.syllabifywords"));
		ControllerUtilities.createToolbarButtonWithImage("predictedToCorrect.png",
				buttonToolbarConvertPredictedToCorrectSyllabification,
				tooltipToolbarConvertPredictedToCorrectSyllabification,
				bundle.getString("menu.convertpredictedtocorrect"));
		ControllerUtilities.createToolbarButtonWithImage("FindWord.png", buttonToolbarFindWord,
				tooltipToolbarFindWord, bundle.getString("menu.findword"));
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
					Class<?> c = Class.forName(currentApproachController.getClass().getName());// this.getClass().getName());
					Method method = c.getDeclaredMethod(newValue.getViewHandler(),
							(Class<?>[]) null);
					method.invoke(currentApproachController, (Object[]) null);// this,
																				// (Object[])null);
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

}
