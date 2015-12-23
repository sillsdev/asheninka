/**
 * 
 */
package sil.org.syllableparser;

/**
 * @author Andy Black
 *
 */
public class Constants {
	// program wide constants
	public static final String VERSION_NUMBER = "0.1.0.0";
	
	// file-related constants
	public static final String ASHENINKA_DATA_FILE_EXTENSION = "*.ashedata";
	public static final String ASHENINKA_BACKUP_FILE_EXTENSION = "*.ashebackup";
	public static final String SIMPLE_LIST_HYPHENATION_FILE_EXTENSION = "*.hyp";
	public static final String TEXT_FILE_EXTENSION = "*.txt";
	public static final String XML_FILE_EXTENSION = "*.xml";
	public static final String PARATEXT_HYPHENATED_WORDS_FILE = "hyphenatedWords";
	public static final String PARATEXT_HYPHENATED_WORDS_TEXT_FILE = "hyphenatedWords.txt";
	public static final String UTF8_ENCODING = "UTF8";
	public static final String RESOURCE_LOCATION="sil.org.syllableparser.resources.SyllableParser";
	
	// chooser related constants
	public static final String WORD_BOUNDARY_SYMBOL = "#";
	public static final String FIRST_COMBO_BOX_IN_SYLLABLE_PATTERN = "cb1";
	
	// UnitTesting constants
	public static final String UNIT_TEST_DATA_FILE_NAME="test/sil/org/syllableparser/testData/CVTestData.";
	public static final String UNIT_TEST_DATA_FILE="test/sil/org/syllableparser/testData/CVTestData.ashedata";
}
