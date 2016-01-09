/**
 * 
 */
package sil.org.syllableparser.view;

import static org.junit.Assert.assertEquals;

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
import sil.org.syllableparser.model.cvapproach.CVSegment;
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
import javafx.fxml.FXMLLoader;
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
	private CVApproach cvApproachData;
	private ObservableList<Word> words = FXCollections.observableArrayList();

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
		this.words = words;
	}

	public void handleCVSegmentInventory() {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, "fxml/CVSegmentInventory.fxml", locale);
		CVSegmentInventoryController controller = loader.getController();
		currentCVApproachController = controller;
		controller.setMainApp(mainApp);
		controller.setLocale(locale);

		controller.setData(cvApproachData);
	}

	public void handleCVNaturalClasses() {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, "fxml/CVNaturalClasses.fxml", locale);
		CVNaturalClassesController controller = loader.getController();
		currentCVApproachController = controller;

		controller.setMainApp(mainApp);
		controller.setLocale(locale);
		controller.setData(cvApproachData);
	}

	public void handleCVSyllablePatterns() {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, "fxml/CVSyllablePatterns.fxml", locale);
		CVSyllablePatternsController controller = loader.getController();
		currentCVApproachController = controller;

		controller.setMainApp(mainApp);
		controller.setLocale(locale);
		controller.setData(cvApproachData);
	}

	public void handleCVWords() {
		handleCVWords(0);
	}

	public void handleCVWords(int index) {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, "fxml/CVWords.fxml", locale);
		CVWordsController controller = loader.getController();
		currentCVApproachController = controller;

		controller.setMainApp(mainApp);
		controller.setLocale(locale);
		controller.setData(cvApproachData, words);
		controller.setFocusOnWord(index);
	}

	public void handleCVWordsPredictedVsCorrect() {
		FXMLLoader loader = new FXMLLoader();
		ApproachViewNavigator.loadApproachView(loader, "fxml/CVWordsPredictedVsCorrect.fxml",
				locale);
		CVWordsPredictedVsCorrectController controller = loader.getController();
		currentCVApproachController = controller;

		controller.setMainApp(mainApp);
		controller.setLocale(locale);
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
				ObservableList<CVNaturalClass> naturalClasses;
				CVSegmenter segmenter;
				ObservableList<CVSegment> segmentInventory;
				List<CVSegment> cvSegmentInventory;
				CVNaturalClasser naturalClasser;
				List<CVNaturalClass> cvNaturalClasses;
				ObservableList<CVSyllablePattern> patterns;
				CVSyllabifier syllabifier;
				List<CVSyllablePattern> cvPatterns;

				segmentInventory = cvApproachData.getCVSegmentInventory();
				segmenter = new CVSegmenter(segmentInventory);
				cvSegmentInventory = segmenter.getSegmentInventory();
				naturalClasses = cvApproachData.getCVNaturalClasses();
				naturalClasser = new CVNaturalClasser(naturalClasses);
				cvNaturalClasses = naturalClasser.getNaturalClasses();
				patterns = cvApproachData.getCVSyllablePatterns();
				syllabifier = new CVSyllabifier(patterns, null);
				cvPatterns = syllabifier.getCvPatterns();

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
					List<CVSyllable> syllablesInWord = syllabifier.getSyllablesInCurrentWord();
					word.setCVPredictedSyllabification(syllabifier.getSyllabificationOfCurrentWord());
					word.setCVParserResult(sSuccess);
				}
				long timePassed = System.currentTimeMillis() - timeStart;
				System.out.println("Syllabification took " + timePassed + " milliseconds");
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
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ApproachViewNavigator.class
					.getResource("fxml/CVPredictedToCorrectSyllabificationChooser.fxml"));
			loader.setResources(ResourceBundle.getBundle(
					"sil.org.syllableparser.resources.SyllableParser", locale));

			AnchorPane page = loader.load();
			Stage dialogStage = new Stage();
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			// set the icon
			dialogStage.getIcons().add(mainApp.getNewMainIconImage());
			dialogStage.setTitle(MainApp.kApplicationTitle);

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

	@Override
	void handleFindWord() {
		try {
			// TextFields wordToFind
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle(bundle.getString("program.name"));
			dialog.setHeaderText("");
			dialog.setContentText(bundle.getString("label.wordtofind"));
			dialog.setGraphic(null);
			Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
			stage.getIcons().add(mainApp.getNewMainIconImage());
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
	public ObservableList<Word> getWords() {
		return words;
	}
}
