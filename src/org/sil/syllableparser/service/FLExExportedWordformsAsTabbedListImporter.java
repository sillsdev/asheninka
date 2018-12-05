// Copyright (c) 2016-2018 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.controlsfx.control.StatusBar;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.utility.view.ControllerUtilities;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.Scene;

/**
 * @author Andy Black
 *
 */
public class FLExExportedWordformsAsTabbedListImporter extends WordImporter {

	public FLExExportedWordformsAsTabbedListImporter() {
		super();
	}

	public FLExExportedWordformsAsTabbedListImporter(LanguageProject languageProject) {
		super(languageProject);
	}

	public void importWords(File file, String sUntested) {

		// now add all words
		try (Stream<String> stream = Files.lines(file.toPath()).skip(1)) {
			stream.forEach(s -> languageProject.createNewWordFromFLExExportedWordformsAsTabbedList(
					s, sUntested));

		} catch (IOException e) {
			e.printStackTrace();
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
				Path path = file.toPath();
				try (Stream<String> stream = Files.lines(path).skip(1)) {
					long max = Files.lines(path).count();
					AtomicInteger iProgress = new AtomicInteger();
					stream.forEach(s -> {
						updateMessage(bundle.getString("label.importing") + s);
						iProgress.incrementAndGet();
						updateProgress(iProgress.longValue(), max);
						languageProject.createNewWordFromFLExExportedWordformsAsTabbedList(
								s, sUntested);
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
				ControllerUtilities.formatTimePassed(timeStart,
						"Importing ParaTExt hyphenatedWords.txt file");
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
