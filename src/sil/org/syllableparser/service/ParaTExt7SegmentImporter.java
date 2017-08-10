// Copyright (c) 2016-2017 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.controlsfx.control.StatusBar;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import sil.org.syllableparser.Constants;
import sil.org.syllableparser.MainApp;
import sil.org.syllableparser.model.Environment;
import sil.org.syllableparser.model.Grapheme;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.SylParserObject;
import sil.org.syllableparser.model.cvapproach.CVNaturalClass;
import sil.org.syllableparser.model.cvapproach.CVSegmentOrNaturalClass;
import sil.org.syllableparser.view.ControllerUtilities;

/**
 * @author Andy Black
 *
 */
public class ParaTExt7SegmentImporter extends SegmentImporter {

	final String PARATEXT_CHARACTERS_PARAMETER_SECTION_MARKER = "[Characters]";

	public ParaTExt7SegmentImporter() {
		super();
	}

	public ParaTExt7SegmentImporter(LanguageProject languageProject) {
		super(languageProject);
	}

	@Override
	public void importSegments(File file) throws SegmentImporterException {
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(file),
					Constants.UTF8_ENCODING);
			BufferedReader bufr = new BufferedReader(reader);

			String line = findStartOfCharacterDefinitions(bufr);
			if (line == null) {
				bufr.close();
				throw new ParaTExtSegmentImporterNoCharactersException(file.getPath());
			}
			line = bufr.readLine(); // eat the marker
			removeAllSegments();
			line = createSegmentsFromParaTExtDefinition(bufr, line);
			// We do not care about what else is in the file, so just close it
			bufr.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	protected String findStartOfCharacterDefinitions(BufferedReader bufr) throws IOException {
		String line = bufr.readLine();
		while (line != null && !line.equals(PARATEXT_CHARACTERS_PARAMETER_SECTION_MARKER)) {
			line = bufr.readLine();
		}
		return line;
	}

	protected String createSegmentsFromParaTExtDefinition(BufferedReader bufr, String line)
			throws IOException {
		while (line != null && !line.matches("^\\[.+\\]$")) {
			if (line.length() > 0) {
				int indexOfStartOfCharacters = line.indexOf("=") + 1;
				String[] chars = line.substring(indexOfStartOfCharacters).split("[/ ]");
				if (chars.length <= 0) {
					continue;
				}
				ObservableList<Grapheme> graphemes = FXCollections.observableArrayList();
				for (int i = 0; i < chars.length; i++) {
					Grapheme grapheme = new Grapheme(chars[i], "", new SimpleListProperty<Environment>(),
							"");
					graphemes.add(grapheme);
				}
				String sName = chars[0];
				Segment seg = new Segment(sName, "", "");
				seg.setGraphemes(graphemes);
				languageProject.getSegmentInventory().add(seg);
			}
			line = bufr.readLine();
		}
		return line;
	}

	protected void removeAllSegments() {
		for (CVNaturalClass nc : languageProject.getCVApproach().getCVNaturalClasses()) {
			// We use a list and a simple for loop to avoid getting a 
			// java.util.ConcurrentModificationException
			ObservableList<SylParserObject> list = nc.getSegmentsOrNaturalClasses();
			for (int i = 0; i < list.size(); i++) {
				SylParserObject snc = list.get(i);
				if (snc instanceof Segment) {
					list.remove(i);
					i--;
				}
			}
		}
		languageProject.getSegmentInventory().clear();
	}
}
