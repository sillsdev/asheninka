// Copyright (c) 2016-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.importexport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.controlsfx.control.StatusBar;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Word;
import org.sil.utility.view.ControllerUtilities;
import org.xml.sax.SAXException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.Scene;

/**
 * @author Andy Black
 *
 */
public class ParaTExtExportedWordListImporter extends WordImporter {

	public ParaTExtExportedWordListImporter() {
		super();
	}

	public ParaTExtExportedWordListImporter(LanguageProject languageProject) {
		super(languageProject);
	}

	public void importWords(File file, String sUntested) {

		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = saxParserFactory.newSAXParser();
			ParaTExtExportedWordListXmlParserHandler handler = new ParaTExtExportedWordListXmlParserHandler();
			saxParser.parse(file, handler);
			List<Word> wordList = handler.getWordList();
			for (Word word : wordList) {
				languageProject.createNewWordFromParaTExt(word, sUntested);
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			MainApp.reportException(e, null);
		}
	}

	// Used by the main application (including progress bar updating and
	// changing the mouse cursor which has to be in a separate thread,
	// apparently)
	// TODO: figure out how to get JUnit to deal with a thread so we do
	// not have two copies of the crucial code.
	// TODO: is there a way to avoid duplicating this code when just a method
	// call or two
	// is what is different?
	public void importWords(File file, String sUntested, StatusBar statusBar, ResourceBundle bundle, MainApp mainApp) {
		mainApp.getSaveDataPeriodicallyService().cancel();
		long timeStart = System.currentTimeMillis();

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Scene scene = statusBar.getScene();
				Cursor currentCursor = scene.getCursor();
				scene.setCursor(Cursor.WAIT);

				long max = Files.lines(file.toPath()).count();
				AtomicInteger iProgress = new AtomicInteger();
				SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
				try {
					SAXParser saxParser = saxParserFactory.newSAXParser();
					ParaTExtExportedWordListXmlParserHandler handler = new ParaTExtExportedWordListXmlParserHandler();
					saxParser.parse(file, handler);
					List<Word> wordList = handler.getWordList();
					for (Word word : wordList) {
						updateMessage(bundle.getString("label.importing") + word.getWord());
						iProgress.incrementAndGet();
						updateProgress(iProgress.longValue(), max);
						languageProject.createNewWordFromParaTExt(word, sUntested);
					}
				} catch (ParserConfigurationException | SAXException | IOException e) {
					e.printStackTrace();
					MainApp.reportException(e, bundle);
				}

				ControllerUtilities.formatTimePassed(timeStart,
						"Importing ParaTExt exported XML file");
				scene.setCursor(currentCursor);
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
			mainApp.getSaveDataPeriodicallyService().restart();
		});
		Platform.runLater(task);
	}

}
