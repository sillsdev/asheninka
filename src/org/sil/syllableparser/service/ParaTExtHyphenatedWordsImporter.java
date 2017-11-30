// Copyright (c) 2016-2017 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.controlsfx.control.StatusBar;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.view.ControllerUtilities;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.Scene;

/**
 * @author Andy Black
 *
 */
public class ParaTExtHyphenatedWordsImporter extends WordImporter {

	public ParaTExtHyphenatedWordsImporter() {
		super();
	}

	public ParaTExtHyphenatedWordsImporter(LanguageProject languageProject) {
		super(languageProject);
	}

	public void importWords(File file, String sUntested) {
		consumeParaTExtPreamble(file);

		// now add all words
		try (Stream<String> stream = Files.lines(file.toPath()).skip(7)) {
			stream.forEach(s -> languageProject.createNewWordFromParaTExt(s, sUntested));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void consumeParaTExtPreamble(File file) {
		StringBuilder sb = new StringBuilder();
		extractParaTExtPreamble(file, sb);
		languageProject.setParaTExtHyphenatedWordsPreamble(sb.toString());
	}

	// Used by the main application (including progress bar updating and
	// changing the mouse cursor which has to be in a separate thread,
	// apparently)
	// TODO: figure out how to get JUnit to deal with a thread so we do
	// not have two copies of the crucial code.
	// TODO: is there a way to avoid duplicating this code when just a method
	// call or two is what is different?
	public void importWords(File file, String sUntested, StatusBar statusBar, ResourceBundle bundle, MainApp mainApp) {
		mainApp.getSaveDataPeriodicallyService().cancel();
		long timeStart = System.currentTimeMillis();

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				consumeParaTExtPreamble(file);
				Scene scene = statusBar.getScene();
				Cursor currentCursor = scene.getCursor();
				scene.setCursor(Cursor.WAIT);
				Path path = file.toPath();
				try (Stream<String> stream = Files.lines(path).skip(7)) {
					long max = Files.lines(path).count();
					AtomicInteger iProgress = new AtomicInteger();
					stream.forEach(s -> {
						updateMessage(bundle.getString("label.importing") + s);
						iProgress.incrementAndGet();
						updateProgress(iProgress.longValue(), max);
						languageProject.createNewWordFromParaTExt(s, sUntested);
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
				ControllerUtilities.formatTimePassed(timeStart, "Importing ParaTExt hyphenatedWords.txt file");
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

	public void extractParaTExtPreamble(File file, StringBuilder sb) {
		// TODO: is there a way to use a stream for this? I did not find
		// anything online
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(file),
					Constants.UTF8_ENCODING);
			BufferedReader bufr = new BufferedReader(reader);

			int count = 1;
			// not sure why, but are getting something in the first character
			// position
			String line = bufr.readLine().substring(1);
			while (line != null && count < 8) {
				sb.append(line);
				sb.append("\n");
				line = bufr.readLine();
				count++;
			}
			bufr.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
