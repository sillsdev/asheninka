/**
 * 
 */
package sil.org.syllableparser;

import javafx.scene.paint.Color;

/**
 * @author Andy Black
 *
 */
public class Constants {
	// program wide constants
	public static final String VERSION_NUMBER = "0.1.2.1 Alpha";
	
	// file-related constants
	public static final String ASHENINKA_BACKUP_FILE_EXTENSION = "ashebackup";
	public static final String ASHENINKA_DATA_FILE_EXTENSION = "ashedata";
	public static final String ASHENINKA_DATA_FILE_EXTENSIONS = "*." + ASHENINKA_DATA_FILE_EXTENSION;
	public static final String ASHENINKA_STARTER_FILE = "resources/starterFile.ashedata";
	public static final String DEFAULT_DIRECTORY_NAME = "My Asheninka";
	public static final String BACKUP_DIRECTORY_NAME = "Backups";
	public static final String PARATEXT_HYPHENATED_WORDS_FILE = "hyphenatedWords";
	public static final String PARATEXT_HYPHENATED_WORDS_PREAMBLE = "#Hyphenated Word List v2.0\n" +
			"#You may edit words and force them to stay as you edit them by putting an * in front of the line.\n" +
			"#Special equate lines used by Publishing Assistant\n" +
			"HardHyphen = \"-\"\n" +
			"SoftHyphen = \"=\"\n" +
			"SoftHyphenOut = \"­\"\n" +
			"HyphenatedMarkers = \"cd iex im imi imq ip ipi ipq ipr m mi nb p p1 p2 p3 ph ph1 ph2 ph3 pi pi1 pi2 pi3 pm pmc pmo pmr\"\n"; 
	public static final String PARATEXT_HYPHENATED_WORDS_TEXT_FILE = "hyphenatedWords.txt";
	public static final String RESOURCE_LOCATION="sil.org.syllableparser.resources.SyllableParser";
	public static final String SIMPLE_LIST_HYPHENATION_FILE_EXTENSION = ".hyp";
	public static final String TEXT_FILE_EXTENSION = "*.txt";
	public static final String UTF8_ENCODING = "UTF8";
	public static final String XML_FILE_EXTENSION = ".xml";
			
	// chooser related constants
	public static final String WORD_BOUNDARY_SYMBOL = "#";
	public static final String FIRST_COMBO_BOX_IN_SYLLABLE_PATTERN = "cb1";
	
	// UnitTesting constants
	public static final String UNIT_TEST_DATA_FILE_NAME="test/sil/org/syllableparser/testData/CVTestData.";
	public static final String UNIT_TEST_DATA_FILE="test/sil/org/syllableparser/testData/CVTestData.ashedata";
	public static final String UNIT_TEST_BACKUP_FILE_NAME="test/sil/org/syllableparser/testData/CVTestData.ashebackup";
	public static final String UNIT_TEST_BACKUP_ZIP_ENTRY_NAME="CVTestData.ashedata";
	public static final String UNIT_TEST_BACKUP_ZIP_ENTRY_COMMENT="Comment included in zipped file: m\u00e1s o menos.";
	public static final String UNIT_TEST_DATA_FILE_2_NAME="test/sil/org/syllableparser/testData/CVTestData2.";
	public static final String UNIT_TEST_DATA_FILE_2="test/sil/org/syllableparser/testData/CVTestData2.ashedata";
	
	// Text colors
	public static final Color ACTIVE=Color.BLACK;
	public static final Color INACTIVE=Color.GRAY;
	public static final Color PARSER_SUCCESS=Color.GREEN;
	public static final Color PARSER_FAILURE=Color.RED;
}
