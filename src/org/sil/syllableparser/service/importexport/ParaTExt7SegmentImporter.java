// Copyright (c) 2016-2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service.importexport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.MainApp;
import org.sil.syllableparser.model.Environment;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.SylParserObject;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
			e1.printStackTrace();
			MainApp.reportException(e1, null);
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
							"", true);
					graphemes.add(grapheme);
				}
				String sName = chars[0];
				Segment seg = new Segment(sName, "", "", "0");
				seg.setGraphs(graphemes);
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
