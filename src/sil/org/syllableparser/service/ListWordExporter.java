/**
 * 
 */
package sil.org.syllableparser.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.Scene;

import org.controlsfx.control.StatusBar;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.model.Approach;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.view.ApproachController;
import sil.org.syllableparser.view.ControllerUtilities;

/**
 * @author Andy Black
 *
 */
public class ListWordExporter extends WordExporter {

	public ListWordExporter() {
		super();
	}

	public ListWordExporter(LanguageProject languageProject) {
		super(languageProject);
	}

	// Used by unit test
	@Override
	public void exportWords(File file, Approach approach) {
		ArrayList<String> hyphenatedWords = approach.getHyphenatedWords(languageProject.getWords());
		try {
			Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					file.getPath()), Constants.UTF8_ENCODING));

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
	// call or two
	// is what is different?
	@Override
	public void exportWords(File file, ApproachController controller, StatusBar statusBar,
			ResourceBundle bundle) {
		ArrayList<String> hyphenatedWords = controller.getHyphenatedWords(languageProject
				.getWords());
		long timeStart = System.currentTimeMillis();

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Scene scene = statusBar.getScene();
				Cursor currentCursor = scene.getCursor();
				scene.setCursor(Cursor.WAIT);
				try {
					Writer fileWriter = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(file.getPath()), Constants.UTF8_ENCODING));
					long max = hyphenatedWords.size();
					AtomicInteger iProgress = new AtomicInteger();
					for (String hyphenatedWord : hyphenatedWords) {
						// sample of handling cancellation (see https://docs.oracle.com/javase/8/javafx/interoperability-tutorial/concurrency.htm)
						if (isCancelled()) {
							updateMessage(bundle.getString("label.exportingcanceled"));
							break;
						}
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
				ControllerUtilities.formatTimePassed(timeStart, "Exporting plain list");
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

}
