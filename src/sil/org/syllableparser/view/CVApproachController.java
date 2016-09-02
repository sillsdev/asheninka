/**
 * 
 */
package sil.org.syllableparser.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.StatusBar;
import org.controlsfx.control.textfield.TextFields;

import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.*;
import sil.org.syllableparser.model.cvapproach.CVApproach;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.syllableparser.model.cvapproach.CVNaturalClassInSyllable;
import sil.org.syllableparser.model.cvapproach.CVSegmentInSyllable;
import sil.org.syllableparser.model.cvapproach.CVSyllable;
import sil.org.syllableparser.model.cvapproach.CVSyllablePattern;
import sil.org.syllableparser.service.CVNaturalClasser;
import sil.org.syllableparser.service.CVSegmenter;
import sil.org.syllableparser.service.CVSyllabifier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Andy Black
 *
 */
public class CVApproachController extends ApproachController {

	private ObservableList<ApproachView> views = FXCollections.observableArrayList();
	private LanguageProject languageProject;
	private CVApproach cvApproachData;
	private ObservableList<Word> words = FXCollections.observableArrayList();
	private String backupDirectoryPath;
	private ApproachEditorController currentCVApproachController;

	public CVApproachController(ResourceBundle bundle, Locale locale) {
		super();
		this.bundle = bundle;
		this.locale = locale;
		views.add(new ApproachView(bundle.getString("cv.view.segmentinventory"),
				"handleCVSegmentInventory"));
		views.add(new ApproachView(bundle.getString("cv.view.naturalclass"),
				"handleCVNaturalClasses"));
		views.add(new ApproachView(bundle.getString("cv.view.syllablepatterns"),
				"handleCVSyllablePatterns"));
		views.add(new ApproachView(bundle.getString("cv.view.words"), "handleCVWords"));
		views.add(new ApproachView(bundle.getString("cv.view.wordspredictedvscorrect"),
				"handleCVWordsPredictedVsCorrect"));
	}

	public ObservableList<ApproachView> getViews() {
		return views;
	}

	public void setCVApproachData(CVApproach cvApproach, ObservableList<Word> words) {
		this.cvApproachData = cvApproach;
		languageProject = cvApproach.getLanguageProject();
		this.words = words;
	}

	public void handleCVSegmentInventory() {
		FXMLLoader loader = createFXMLLoader("fxml/CVSegmentInventory.fxml");
		CVSegmentInventoryController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(cvApproachData);
	}
	
	private FXMLLoader createFXMLLoader(String sFxml) {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, sFxml, locale);
		return loader;
	}

	private void initializeApproachEditorController(ApproachEditorController controller) {
		currentCVApproachController = controller;
		controller.setMainApp(mainApp);
		controller.setRootLayout(rootController);
		controller.setLocale(locale);
		controller.setToolBarDelegate(rootController.toolBarDelegate);
	}
	public void handleCVNaturalClasses() {
		FXMLLoader loader = createFXMLLoader("fxml/CVNaturalClasses.fxml");
		CVNaturalClassesController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(cvApproachData);
	}

	public void handleCVSyllablePatterns() {
		FXMLLoader loader = createFXMLLoader("fxml/CVSyllablePatterns.fxml");
		CVSyllablePatternsController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(cvApproachData);
	}

	public void handleCVWords() {
		handleCVWords(0);
	}

	public void handleCVWords(int index) {
		FXMLLoader loader = createFXMLLoader("fxml/CVWords.fxml");
		CVWordsController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(cvApproachData, words);
		controller.setFocusOnWord(index);
	}

	public void handleCVWordsPredictedVsCorrect() {
		FXMLLoader loader = createFXMLLoader("fxml/CVWordsPredictedVsCorrect.fxml");
		CVWordsPredictedVsCorrectController controller = loader.getController();
		initializeApproachEditorController(controller);
		controller.setData(cvApproachData, words);
		controller.setFocusOnWord(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleInsertNewItem()
	 */
	@Override
	void handleInsertNewItem() {
		currentCVApproachController.handleInsertNewItem();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleRemoveItem()
	 */
	@Override
	void handleRemoveItem() {
		currentCVApproachController.handleRemoveItem();

	}

	@Override
	void handleSyllabifyWords(StatusBar statusBar) {
		String sSuccess = bundle.getString("label.success");
		String sSegmentFailure = bundle.getString("label.cvsegmentfailure");
		String sNaturalClassFailure = bundle.getString("label.cvnaturalclassfailure");
		String sSyllabificationFailure = bundle.getString("label.cvsyllabificationfailure");
		long timeStart = System.currentTimeMillis();

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Scene scene = statusBar.getScene();
				Cursor currentCursor = scene.getCursor();
				scene.setCursor(Cursor.WAIT);
				ObservableList<CVNaturalClass> naturalClasses;
				CVSegmenter segmenter;
				ObservableList<Segment> segmentInventory;
				List<Segment> cvSegmentInventory;
				CVNaturalClasser naturalClasser;
				List<CVNaturalClass> cvNaturalClasses;
				ObservableList<CVSyllablePattern> patterns;
				CVSyllabifier syllabifier;
				List<CVSyllablePattern> cvPatterns;

				segmentInventory = languageProject.getSegmentInventory();
				segmenter = new CVSegmenter(segmentInventory);
				cvSegmentInventory = segmenter.getActiveSegmentInventory();
				naturalClasses = cvApproachData.getCVNaturalClasses();
				naturalClasser = new CVNaturalClasser(naturalClasses);
				cvNaturalClasses = naturalClasser.getActiveNaturalClasses();
				patterns = cvApproachData.getCVSyllablePatterns();
				syllabifier = new CVSyllabifier(patterns, null);
				cvPatterns = syllabifier.getActiveCVPatterns();

				int max = words.size();
				int i = 0;
				for (Word word : words) {
					updateMessage(bundle.getString("label.syllabifying") + word.getWord());
					updateProgress(i++, max);

					boolean fSuccess = segmenter.segmentWord(word.getWord());
					if (!fSuccess) {
						word.setCVParserResult(sSegmentFailure);
						word.setCVPredictedSyllabification("");
						continue;
					}
					List<CVSegmentInSyllable> segmentsInWord = segmenter.getSegmentsInWord();
					fSuccess = naturalClasser.convertSegmentsToNaturalClasses(segmentsInWord);
					if (!fSuccess) {
						word.setCVParserResult(sNaturalClassFailure);
						word.setCVPredictedSyllabification("");
						continue;
					}
					List<CVNaturalClassInSyllable> naturalClassesInWord = naturalClasser
							.getNaturalClassesInCurrentWord();
					syllabifier = new CVSyllabifier(cvPatterns, naturalClassesInWord);
					fSuccess = syllabifier.convertNaturalClassesToSyllables();
					if (!fSuccess) {
						word.setCVParserResult(sSyllabificationFailure);
						word.setCVPredictedSyllabification("");
						continue;
					}
					//List<CVSyllable> syllablesInWord = syllabifier.getSyllablesInCurrentWord();
					word.setCVPredictedSyllabification(syllabifier
							.getSyllabificationOfCurrentWord());
					word.setCVParserResult(sSuccess);
				}
				ControllerUtilities.formatTimePassed(timeStart, "Syllabifying");
				scene.setCursor(currentCursor);
				// sleep for a second since it all happens so quickly
				Thread.sleep(1000);
				updateProgress(0, 0);
				done();
				return null;
			}
		};

		statusBar.textProperty().bind(task.messageProperty());
		statusBar.progressProperty().bind(task.progressProperty());

		// remove bindings again
		task.setOnSucceeded(event -> {
			statusBar.textProperty().unbind();
			statusBar.progressProperty().unbind();
			ControllerUtilities.setDateInStatusBar(statusBar, bundle);
		});

		new Thread(task).start();

	}

	@Override
	void handleConvertPredictedToCorrectSyllabification() {
		try {
			// Load the fxml file and create a new stage for the popup.
			Stage dialogStage = new Stage();
			String resource = "fxml/CVPredictedToCorrectSyllabificationChooser.fxml";
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, dialogStage,
					resource, MainApp.kApplicationTitle);

			CVPredictedToCorrectSyllabificationChooserController controller = loader
					.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setData(cvApproachData, words);

			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void handleCompareImplementations() {
		try {
			// Load the fxml file and create a new stage for the popup.
			Stage dialogStage = new Stage();
			String resource = "fxml/CVComparison.fxml";
			String title = bundle.getString("label.compareimplementations");
			FXMLLoader loader = ControllerUtilities.getLoader(mainApp, locale, dialogStage,
					resource, title);

			CVComparisonController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);
			controller.setLocale(locale);
			controller.setData(cvApproachData);
			controller.setBackupDirectoryPath(backupDirectoryPath);

			dialogStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	void handleFindWord() {
		try {
			// TextFields wordToFind
			String title = bundle.getString("program.name");
			String contentText = bundle.getString("label.wordtofind");
			TextInputDialog dialog = ControllerUtilities.getTextInputDialog(mainApp, title,
					contentText);

			ObservableList<String> listOfWords = FXCollections.observableArrayList();
			for (Word word : words) {
				listOfWords.add(word.getWord());
			}
			TextFields.bindAutoCompletion(dialog.getEditor(), listOfWords);
			Optional<String> result = dialog.showAndWait();
			result.ifPresent(word -> {
				int index = listOfWords.indexOf(result.get());
				handleCVWords(index);
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ArrayList<String> getHyphenatedWords(ObservableList<Word> words) {
		return cvApproachData.getHyphenatedWords(words);
	}

	@Override
	public ArrayList<String> getParaTExtHyphenatedWords(ObservableList<Word> words) {
		return cvApproachData.getParaTExtHyphenatedWords(words);
	}

	@Override
	public ArrayList<String> getXLingPaperHyphenatedWords(ObservableList<Word> words) {
		return cvApproachData.getXLingPaperHyphenatedWords(words);
	}

	public ObservableList<Word> getWords() {
		return words;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleCopy()
	 */
	@Override
	public void handleCopy() {
		currentCVApproachController.handleCopy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handleCut()
	 */
	@Override
	public void handleCut() {
		currentCVApproachController.handleCut();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#handlePaste()
	 */
	@Override
	public void handlePaste() {
		currentCVApproachController.handlePaste();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sil.org.syllableparser.view.ApproachController#anythingSelected()
	 */
	@Override
	boolean anythingSelected() {
		if (currentCVApproachController != null) {
			return currentCVApproachController.anythingSelected();
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#handleToolBarCopy()
	 */
	@Override
	public void handleToolBarCopy() {
		currentCVApproachController.handleToolBarCopy();
	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#handleToolBarPaste()
	 */
	@Override
	public void handleToolBarPaste() {
		currentCVApproachController.handleToolBarPaste();
	}

	/* (non-Javadoc)
	 * @see sil.org.syllableparser.view.ApproachController#handleToolBarCut()
	 */
	@Override
	public void handleToolBarCut() {
		currentCVApproachController.handleToolBarCut();
	}

	public String getBackupDirectoryPath() {
		return backupDirectoryPath;
	}

	/**
	 * @param backupDirectoryPath
	 */
	public void setBackupDirectoryPath(String backupDirectoryPath) {
		this.backupDirectoryPath = backupDirectoryPath; 
		
	}
}
