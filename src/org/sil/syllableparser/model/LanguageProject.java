// Copyright (c) 2016-2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.model.oncapproach.ONCApproach;
import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;

/**
 * @author Andy Black
 *
 */
@XmlRootElement(name = "languageProject")
public class LanguageProject {

	private CVApproach cvApproach;
	private SHApproach shApproach;
	private ONCApproach oncApproach;
	private ObservableList<Word> words = FXCollections.observableArrayList();
	private String sParaTExtHyphenatedWordsPreamble;
	private ObservableList<Segment> segmentInventory = FXCollections.observableArrayList();
	private ObservableList<GraphemeNaturalClass> graphemeNaturalClasses = FXCollections
			.observableArrayList();
	private Language vernacularLanguage;
	private Language analysisLanguage;
	private HyphenationParametersListWord hyphenationParametersListWord;
	private HyphenationParametersParaTExt hyphenationParametersParaTExt;
	private HyphenationParametersXLingPaper hyphenationParametersXLingPaper;
	private int databaseVersion;
	private ObservableList<Environment> environments = FXCollections.observableArrayList();
	private ObservableList<TemplateFilter> templates = FXCollections.observableArrayList();
	private ObservableList<TemplateFilter> filters = FXCollections.observableArrayList();
	private SyllabificationParameters syllabificationParameters;

	public LanguageProject() {
		super();
		cvApproach = new CVApproach();
		cvApproach.setLanguageProject(this);
		shApproach = new SHApproach();
		shApproach.setLanguageProject(this);
		oncApproach = new ONCApproach();
		oncApproach.setLanguageProject(this);
		vernacularLanguage = new Language();
		analysisLanguage = new Language();
		hyphenationParametersListWord = new HyphenationParametersListWord("=", 0, 0);
		hyphenationParametersParaTExt = new HyphenationParametersParaTExt("=", 2, 2);
		hyphenationParametersXLingPaper = new HyphenationParametersXLingPaper("-", 2, 2);
		syllabificationParameters = new SyllabificationParameters();
	}

	/**
	 * Clear out all data in this language project
	 */
	public void clear() {
		cvApproach.clear();
		shApproach.clear();
		oncApproach.clear();
		segmentInventory.clear();
		words.clear();
		environments.clear();
		graphemeNaturalClasses.clear();
		templates.clear();
		filters.clear();
	}

	public int getDatabaseVersion() {
		return databaseVersion;
	}

	@XmlAttribute(name = "databaseVersion")
	public void setDatabaseVersion(int databaseVersion) {
		this.databaseVersion = databaseVersion;
	}

	public CVApproach getCVApproach() {
		return cvApproach;
	}

	@XmlElement(name = "cvApproach")
	public void setCVApproach(CVApproach cvApproach) {
		this.cvApproach = cvApproach;
	}

	public SHApproach getSHApproach() {
		return shApproach;
	}

	@XmlElement(name = "shApproach")
	public void setSHApproach(SHApproach shApproach) {
		this.shApproach = shApproach;
	}

	public ONCApproach getONCApproach() {
		return oncApproach;
	}

	@XmlElement(name = "oncApproach")
	public void setONCApproach(ONCApproach oncApproach) {
		this.oncApproach = oncApproach;
	}

	/**
	 * @return the cvSegmentInventoryData
	 */
	@XmlElementWrapper(name = "segments")
	@XmlElement(name = "segment")
	public ObservableList<Segment> getSegmentInventory() {
		return segmentInventory;
	}

	public List<Segment> getActiveSegmentsInInventory() {
		return segmentInventory.stream().filter(segment -> segment.isActive())
				.collect(Collectors.toList());
	}

	public void setSegmentInventory(ObservableList<Segment> segmentInventoryData) {
		this.segmentInventory = segmentInventoryData;
	}

	@XmlElementWrapper(name = "graphemeNaturalClassess")
	@XmlElement(name = "graphemeNaturalClass")
	public ObservableList<GraphemeNaturalClass> getGraphemeNaturalClasses() {
		return graphemeNaturalClasses;
	}

	public void setGraphemeNaturalClasses(
			ObservableList<GraphemeNaturalClass> graphemeNaturalClassesData) {
		this.graphemeNaturalClasses = graphemeNaturalClassesData;
	}

	public List<Grapheme> getActiveGraphemes() {
		List<Grapheme> graphemes = new ArrayList<Grapheme>();
		for (Segment segment : getActiveSegmentsInInventory()) {
			graphemes.addAll(segment.getActiveGraphs());
		}
		return graphemes;
	}

	public List<GraphemeNaturalClass> getActiveGraphemeNaturalClasses() {
		return graphemeNaturalClasses.stream().filter(gnc -> gnc.isActive())
				.collect(Collectors.toList());
	}

	public List<Environment> getActiveAndValidEnvironments() {
		return environments.stream().filter(env -> env.isActive() && env.isValid())
				.collect(Collectors.toList());
	}

	public List<TemplateFilter> getActiveAndValidTemplates() {
		return templates.stream().filter(template -> template.isActive() && template.isValid())
				.collect(Collectors.toList());
	}

	public List<TemplateFilter> getActiveAndValidFilters() {
		return filters.stream().filter(filter -> filter.isActive() && filter.isValid())
				.collect(Collectors.toList());
	}

	/**
	 * @return the word Data
	 */
	@XmlElementWrapper(name = "words")
	@XmlElement(name = "word")
	public ObservableList<Word> getWords() {
		return words;
	}

	/**
	 * @param word
	 *            Data to set
	 */
	public void setWords(ObservableList<Word> words) {
		this.words = words;
	}

	@XmlElementWrapper(name = "environments")
	@XmlElement(name = "environment")
	public ObservableList<Environment> getEnvironments() {
		return environments;
	}

	public void setEnvironments(ObservableList<Environment> environments) {
		this.environments = environments;
	}

	public ObservableList<Grapheme> getGraphemes() {
		ObservableList<Grapheme> graphemes = FXCollections.observableArrayList();
		for (Segment segment : getSegmentInventory()) {
			graphemes.addAll(segment.getGraphs());
		}
		return graphemes;
	}

	@XmlElementWrapper(name = "templates")
	@XmlElement(name = "template")
	public ObservableList<TemplateFilter> getTemplates() {
		return templates;
	}

	public void setTemplates(ObservableList<TemplateFilter> templates) {
		this.templates = templates;
	}

	@XmlElementWrapper(name = "filters")
	@XmlElement(name = "filter")
	public ObservableList<TemplateFilter> getFilters() {
		return filters;
	}

	public void setFilters(ObservableList<TemplateFilter> filters) {
		this.filters = filters;
	}

	public String getParaTExtHyphenatedWordsPreamble() {
		return sParaTExtHyphenatedWordsPreamble;
	}

	public void setParaTExtHyphenatedWordsPreamble(String sParaTExtHyphenatedWordsPreamble) {
		this.sParaTExtHyphenatedWordsPreamble = sParaTExtHyphenatedWordsPreamble;
	}

	/**
	 * @param languageProjectLoaded
	 */
	public void load(LanguageProject languageProjectLoaded) {
		databaseVersion = languageProjectLoaded.getDatabaseVersion();
		cvApproach.load(languageProjectLoaded.getCVApproach());
		cvApproach.setLanguageProject(this);
		shApproach.load(languageProjectLoaded.getSHApproach());
		shApproach.setLanguageProject(this);
		oncApproach.load(languageProjectLoaded.getONCApproach());
		oncApproach.setLanguageProject(this);
		ObservableList<Segment> segmentInventoryLoadedData = languageProjectLoaded
				.getSegmentInventory();
		for (Segment segment : segmentInventoryLoadedData) {
			segmentInventory.add(segment);
		}
		ObservableList<Word> wordsLoadedData = languageProjectLoaded.getWords();
		for (Word word : wordsLoadedData) {
			words.add(word);
		}
		ObservableList<GraphemeNaturalClass> graphemeNaturalClassesLoadedData = languageProjectLoaded
				.getGraphemeNaturalClasses();
		for (GraphemeNaturalClass gnc : graphemeNaturalClassesLoadedData) {
			graphemeNaturalClasses.add(gnc);
		}
		ObservableList<Environment> environmentsLoadedData = languageProjectLoaded
				.getEnvironments();
		for (Environment environment : environmentsLoadedData) {
			environments.add(environment);
		}
		ObservableList<TemplateFilter> templatesLoadedData = languageProjectLoaded
				.getTemplates();
		for (TemplateFilter template : templatesLoadedData) {
			templates.add(template);
		}
		ObservableList<TemplateFilter> filtersLoadedData = languageProjectLoaded
				.getFilters();
		for (TemplateFilter filter : filtersLoadedData) {
			filters.add(filter);
		}
		analysisLanguage = languageProjectLoaded.getAnalysisLanguage();
		vernacularLanguage = languageProjectLoaded.getVernacularLanguage();
		hyphenationParametersListWord = languageProjectLoaded.getHyphenationParametersListWord();
		hyphenationParametersParaTExt = languageProjectLoaded.getHyphenationParametersParaTExt();
		hyphenationParametersXLingPaper = languageProjectLoaded.getHyphenationParametersXLingPaper();
		syllabificationParameters = languageProjectLoaded.getSyllabificationParameters();
	}

	public void createNewWord(String word, String sUntested) {
		String wordContentOnly = word.trim();
		int indexOfHashMark = word.indexOf('#');
		if (indexOfHashMark > 0) {
			wordContentOnly = word.substring(0, indexOfHashMark - 1);
		} else if (indexOfHashMark == 0) {
			wordContentOnly = "";
		}
		if (!wordContentOnly.isEmpty()) {
			final String wordToCheck = wordContentOnly;
			Word newWord = new Word(wordToCheck, "", sUntested);
			addWordIfDistinct(wordToCheck, newWord);
		}
	}

	public void createNewWordFromFLExExportedWordformsAsTabbedList(String line, String sUntested) {
		int iTabIndex = line.indexOf('\t');
		if (iTabIndex > 0) {
			String wordContentOnly = line.substring(0, iTabIndex);
			String[] wordsInLine = wordContentOnly.split(" ");
			for (String word : wordsInLine) {
				if (!word.isEmpty()) {
					Word newWord = new Word(word, "", sUntested);
					addWordIfDistinct(word, newWord);
				}
			}
		}
	}

	public void createNewWordFromParaTExt(String word, String sUntested) {
		String wordContentOnly = word.trim();
		String wordWithCorrectSyllabification = "";
		int indexOfHashMark = word.indexOf('*');
		if (indexOfHashMark == 0) {
			wordContentOnly = word.substring(1);
			wordWithCorrectSyllabification = wordContentOnly.replace("=", ".");
		}
		if (!wordContentOnly.isEmpty()) {
			final String wordToCheck = wordContentOnly.replace("=", "");
			Word newWord = new Word(wordToCheck, wordWithCorrectSyllabification, sUntested);
			addWordIfDistinct(wordToCheck, newWord);
		}
	}

	public void addWordIfDistinct(final String wordToCheck, Word newWord) {
		ObservableList<Word> matchingWords = words.filtered(extantWord -> extantWord.getWord()
				.equals(wordToCheck));
		if (matchingWords.size() == 0) {
			words.add(newWord);
		}
	}

	/**
	 * @param word
	 * @param sUntested
	 *
	 */
	public void createNewWordFromParaTExt(Word word, String sUntested) {
		word.setCVParserResult(sUntested);
		addWordIfDistinct(word.getWord(), word);
	}

	/**
	 * @return the vernacular language
	 */
	public Language getVernacularLanguage() {
		return vernacularLanguage;
	}

	/**
	 * @param language
	 *            the vernacular language to set
	 */
	public void setVernacularLanguage(Language language) {
		this.vernacularLanguage = language;
	}

	/**
	 * @return the analysis language
	 */
	public Language getAnalysisLanguage() {
		return analysisLanguage;
	}

	/**
	 * @param language
	 *            the analysis language to set
	 */
	public void setAnalysisLanguage(Language language) {
		this.analysisLanguage = language;
	}

	public HyphenationParametersListWord getHyphenationParametersListWord() {
		return hyphenationParametersListWord;
	}

	public void setHyphenationParametersListWord(
			HyphenationParametersListWord hyphenationParametersListWord) {
		this.hyphenationParametersListWord = hyphenationParametersListWord;
	}

	public HyphenationParametersParaTExt getHyphenationParametersParaTExt() {
		return hyphenationParametersParaTExt;
	}

	public void setHyphenationParametersParaTExt(
			HyphenationParametersParaTExt hyphenationParametersParaTExt) {
		this.hyphenationParametersParaTExt = hyphenationParametersParaTExt;
	}

	public HyphenationParametersXLingPaper getHyphenationParametersXLingPaper() {
		return hyphenationParametersXLingPaper;
	}

	public void setHyphenationParametersXLingPaper(
			HyphenationParametersXLingPaper hyphenationParametersXLingPaper) {
		this.hyphenationParametersXLingPaper = hyphenationParametersXLingPaper;
	}

	public SyllabificationParameters getSyllabificationParameters() {
		return syllabificationParameters;
	}

	public void setSyllabificationParameters(SyllabificationParameters sylParameters) {
		this.syllabificationParameters = sylParameters;
	}
}
