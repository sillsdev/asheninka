// Copyright (c) 2016-2018 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.Scene;

import org.controlsfx.control.StatusBar;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.Approach;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.view.ApproachController;
import org.sil.utility.view.ControllerUtilities;

/**
 * @author Andy Black
 *
 */
public class ParaTExtHyphenatedWordsExporter extends WordExporter {

	public ParaTExtHyphenatedWordsExporter() {
		super();
	}

	public ParaTExtHyphenatedWordsExporter(LanguageProject languageProject) {
		super(languageProject);
	}

	// for unit testing
	@Override
	public void exportWords(File file, Approach approach) {
		ArrayList<String> hyphenatedWords = approach.getHyphenatedWordsParaTExt(languageProject
				.getWords());
		try {
			Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					file.getPath()), Constants.UTF8_ENCODING));

			String sPreamble = languageProject.getParaTExtHyphenatedWordsPreamble();
			if (sPreamble == null || sPreamble.isEmpty()) {
				sPreamble = Constants.PARATEXT_HYPHENATED_WORDS_PREAMBLE;
			}
			fileWriter.write(sPreamble);
			for (String hyphenatedWord : hyphenatedWords) {
				fileWriter.write(hyphenatedWord);
				fileWriter.write("\n");
			}
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Used by the main application (including progress bar updating and
	// changing the mouse cursor which has to be in a separate thread,
	// apparently)
	// TODO: figure out how to get JUnit to deal with a thread so we do
	// not have two copies of the crucial code.
	// TODO: is there a way to avoid duplicating this code when just a method
	// call or two is what is different?
	@Override
	public void exportWords(File file, ApproachController controller, StatusBar statusBar, ResourceBundle bundle) {
		ArrayList<String> hyphenatedWords = controller.getHyphenatedWordsParaTExt(languageProject
				.getWords());
		long timeStart = System.currentTimeMillis();

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Scene scene = statusBar.getScene();
				Cursor currentCursor = scene.getCursor();
				scene.setCursor(Cursor.WAIT);
				try {
					Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
							file.getPath()), Constants.UTF8_ENCODING));

					long max = hyphenatedWords.size();
					AtomicInteger iProgress = new AtomicInteger();
					String sPreamble = languageProject.getParaTExtHyphenatedWordsPreamble();
					if (sPreamble == null || sPreamble.isEmpty()) {
						sPreamble = Constants.PARATEXT_HYPHENATED_WORDS_PREAMBLE;
					}
					fileWriter.write(sPreamble);
					for (String hyphenatedWord : hyphenatedWords) {
						updateMessage(bundle.getString("label.exporting") + hyphenatedWord);
						iProgress.incrementAndGet();
						updateProgress(iProgress.longValue(), max);
						fileWriter.write(hyphenatedWord);
						fileWriter.write("\n");
					}
					fileWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ControllerUtilities.formatTimePassed(timeStart, "Exporting ParaTExt hyphenatedWords.txt");
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
		Platform.runLater(task);
	}
}
