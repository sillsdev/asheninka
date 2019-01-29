// Copyright (c) 2016-2019 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service.importexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;
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
public class XLingPaperHyphenatedWordExporter extends WordExporter {

	public XLingPaperHyphenatedWordExporter() {
		super();
	}

	public XLingPaperHyphenatedWordExporter(LanguageProject languageProject) {
		super(languageProject);
	}

	@Override
	public void exportWords(File file, Approach approach) {
		ArrayList<String> hyphenatedWords = approach.getHyphenatedWordsXLingPaper(languageProject
				.getWords());
		try {
			SortedSet<Character> charsUsed = new TreeSet<Character>();
			Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					file.getPath()), Constants.UTF8_ENCODING));

			fileWriter.write("<exceptions>\n");
			for (String hyphenatedWord : hyphenatedWords) {
				fileWriter.write("<word>");
				fileWriter.write(hyphenatedWord);
				fileWriter.write("</word>\n");
				char[] chars = hyphenatedWord.toCharArray();
				for (char c : chars) {
					charsUsed.add(c);
				}
			}
			Iterator<Character> cIt = charsUsed.iterator();
			while (cIt.hasNext()) {
				char c = cIt.next();
				if (!Character.isLetterOrDigit(c) && c != '-') {
					fileWriter.write("<wordformingcharacter>");
					fileWriter.write(c);
					fileWriter.write("</wordformingcharacter>\n");
				}
			}
			fileWriter.write("</exceptions>\n");
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
	public void exportWords(File file, ApproachController controller, StatusBar statusBar,
			ResourceBundle bundle) {
		ArrayList<String> hyphenatedWords = controller.getHyphenatedWordsXLingPaper(languageProject
				.getWords());
		long timeStart = System.currentTimeMillis();

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Scene scene = statusBar.getScene();
				Cursor currentCursor = scene.getCursor();
				scene.setCursor(Cursor.WAIT);
				try {
					long max = hyphenatedWords.size();
					AtomicInteger iProgress = new AtomicInteger();
					SortedSet<Character> charsUsed = new TreeSet<Character>();
					Writer fileWriter = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(file.getPath()), Constants.UTF8_ENCODING));

					fileWriter.write("<exceptions>\n");
					for (String hyphenatedWord : hyphenatedWords) {
						updateMessage(bundle.getString("label.exporting") + hyphenatedWord);
						iProgress.incrementAndGet();
						updateProgress(iProgress.longValue(), max);
						fileWriter.write("<word>");
						fileWriter.write(hyphenatedWord);
						fileWriter.write("</word>\n");
						char[] chars = hyphenatedWord.toCharArray();
						for (char c : chars) {
							charsUsed.add(c);
						}
					}
					Iterator<Character> cIt = charsUsed.iterator();
					while (cIt.hasNext()) {
						char c = cIt.next();
						if (!Character.isLetterOrDigit(c) && c != '-') {
							fileWriter.write("<wordformingcharacter>");
							fileWriter.write(c);
							fileWriter.write("</wordformingcharacter>\n");
						}
					}
					fileWriter.write("</exceptions>\n");
					fileWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ControllerUtilities.formatTimePassed(timeStart,
						"Exporting XLingPaper hyphenation exceptions file");
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
