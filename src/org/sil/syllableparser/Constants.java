// Copyright (c) 2016-2021 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser;

import javafx.scene.paint.Color;

/**
 * @author Andy Black
 *
 */
public class Constants {
	// program wide constants
	public static final String VERSION_NUMBER = "0.8.0.0 Alpha";
	public static final int SAVE_DATA_PERIODICITY = 15;
	public static final int CURRENT_DATABASE_VERSION = 2;
	public static final String SYLLABLE_SYMBOL = "σ";
	public static final String MORA_SYMBOL = "μ";
	public static final String CODA_IN_MORA_SYMBOL = "c";
	public static final String OT_SET_PRECEDES_OPERATOR = " < ";

	// file-related constants
	public static final String ASHENINKA_BACKUP_FILE_EXTENSION = "ashebackup";
	public static final String ASHENINKA_DATA_FILE_EXTENSION = "ashedata";
	public static final String ASHENINKA_DATA_FILE_EXTENSIONS = "*."
			+ ASHENINKA_DATA_FILE_EXTENSION;
	public static final String LDML_FILE_EXTENSION = "ldml";
	public static final String LDML_FILE_EXTENSIONS = "*."
			+ LDML_FILE_EXTENSION;
	public static final String ASHENINKA_STARTER_FILE = "resources/starterFile.ashedata";
	public static final String DEFAULT_DIRECTORY_NAME = "My Asheninka";
	public static final String BACKUP_DIRECTORY_NAME = "Backups";
	public static final String PARATEXT_HYPHENATED_WORDS_FILE = "hyphenatedWords";
	public static final String PARATEXT_HYPHENATED_WORDS_PREAMBLE = "#Hyphenated Word List v2.0\n"
			+ "#You may edit words and force them to stay as you edit them by putting an * in front of the line.\n"
			+ "#Special equate lines used by Publishing Assistant\n"
			+ "HardHyphen = \"-\"\n"
			+ "SoftHyphen = \"=\"\n"
			+ "SoftHyphenOut = \"­\"\n"
			+ "HyphenatedMarkers = \"cd iex im imi imq ip ipi ipq ipr m mi nb p p1 p2 p3 ph ph1 ph2 ph3 pi pi1 pi2 pi3 pm pmc pmo pmr\"\n";
	public static final String PARATEXT_HYPHENATED_WORDS_TEXT_FILE = "hyphenatedWords.txt";
	public static final String RESOURCE_LOCATION = "org.sil.syllableparser.resources.SyllableParser";
	public static final String SIMPLE_LIST_HYPHENATION_FILE_EXTENSION = ".hyp";
	public static final String TEXT_FILE_EXTENSION = "*.txt";
	public static final String UTF8_ENCODING = "UTF8";
	public static final String XML_FILE_EXTENSION = ".xml";
	public static final String MIGRATION_XSLT_FILE_NAME = "resources/DataMigration";
	public static final String LDML_XSLT_FILE_NAME = "resources/LdmlExtractor.xsl";
	public static final String RESOURCE_SOURCE_LOCATION = "src/org/sil/syllableparser/";

	// chooser related constants
	public static final String WORD_BOUNDARY_SYMBOL = "#";
	public static final String FIRST_COMBO_BOX_IN_SYLLABLE_PATTERN = "cb1";

	// Miscellaneous constants
	public static final String NULL_AS_STRING = "null";
	public static final String PLEASE_WAIT_HTML_TITLE = "ignored";
	public static final String PLEASE_WAIT_HTML_BEGINNING = "<html>\n<head>\n"
			+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n"
			+ "<title>" + Constants.PLEASE_WAIT_HTML_TITLE + "</title>\n</head>\n<body><h2>";
	public static final String PLEASE_WAIT_HTML_MIDDLE = "</h2>\n<p>";
	public static final String PLEASE_WAIT_HTML_ENDING = "</p>\n</body>\n</html>\n";
	public static final String NATURAL_CLASS_PREFIX = "[";
	public static final String NATURAL_CLASS_SUFFIX = "]";
	public static final String SORT_VALUE_DIVIDER = ";";
	public static final String TEXT_COLOR_CSS_BEGIN = "-fx-text-inner-color: ";
	public static final String TEXT_COLOR_CSS_END = ";";
	public static final String PARSER_SUCCESS_COLOR_STRING = "green";
	public static final String PARSER_FAILURE_COLOR_STRING = "red";
	public static final String SVG_DASHED_LINE = " stroke-dasharray=\"7,7\"";

	// View constants
	public static final int CV_SEGMENT_INVENTORY_VIEW_INDEX = 0;
	public static final int CV_NATURAL_CLASSES_VIEW_INDEX = 1;
	public static final int CV_SYLLABLE_PATTERNS_VIEW_INDEX = 2;
	public static final int CV_WORDS_VIEW_INDEX = 3;
	public static final int CV_PREDICTED_VS_CORRECT_WORDS_VIEW_INDEX = 4;
	public static final int CV_GRAPHEME_NATURAL_CLASSES_VIEW_INDEX = 5;
	public static final int CV_ENVIRONMENTS_VIEW_INDEX = 6;

	public static final int SH_SEGMENT_INVENTORY_VIEW_INDEX = 0;
	public static final int SH_SONORITY_HIERARCHY_VIEW_INDEX = 1;
	public static final int SH_WORDS_VIEW_INDEX = 2;
	public static final int SH_PREDICTED_VS_CORRECT_WORDS_VIEW_INDEX = 3;
	public static final int SH_GRAPHEME_NATURAL_CLASSES_VIEW_INDEX = 4;
	public static final int SH_ENVIRONMENTS_VIEW_INDEX = 5;

	public static final int ONC_SEGMENT_INVENTORY_VIEW_INDEX = 0;
	public static final int ONC_SONORITY_HIERARCHY_VIEW_INDEX = 1;
	public static final int ONC_SYLLABIFICATION_PARAMETERS_VIEW_INDEX = 2;
	public static final int ONC_NATURAL_CLASSES_VIEW_INDEX = 3;
	public static final int ONC_TEMPLATES_VIEW_INDEX = 4;
	public static final int ONC_FILTERS_VIEW_INDEX = 5;
	public static final int ONC_WORDS_VIEW_INDEX = 6;
	public static final int ONC_PREDICTED_VS_CORRECT_WORDS_VIEW_INDEX = 7;
	public static final int ONC_GRAPHEME_NATURAL_CLASSES_VIEW_INDEX = 8;
	public static final int ONC_ENVIRONMENTS_VIEW_INDEX = 9;

	public static final int MORAIC_SEGMENT_INVENTORY_VIEW_INDEX = 0;
	public static final int MORAIC_SONORITY_HIERARCHY_VIEW_INDEX = 1;
	public static final int MORAIC_SYLLABIFICATION_PARAMETERS_VIEW_INDEX = 2;
	public static final int MORAIC_NATURAL_CLASSES_VIEW_INDEX = 3;
	public static final int MORAIC_TEMPLATES_VIEW_INDEX = 4;
	public static final int MORAIC_FILTERS_VIEW_INDEX = 5;
	public static final int MORAIC_WORDS_VIEW_INDEX = 6;
	public static final int MORAIC_PREDICTED_VS_CORRECT_WORDS_VIEW_INDEX = 7;
	public static final int MORAIC_GRAPHEME_NATURAL_CLASSES_VIEW_INDEX = 8;
	public static final int MORAIC_ENVIRONMENTS_VIEW_INDEX = 9;

	public static final int NP_SEGMENT_INVENTORY_VIEW_INDEX = 0;
	public static final int NP_SONORITY_HIERARCHY_VIEW_INDEX = 1;
	public static final int NP_SYLLABIFICATION_PARAMETERS_VIEW_INDEX = 2;
	public static final int NP_NATURAL_CLASSES_VIEW_INDEX = 3;
	public static final int NP_RULES_VIEW_INDEX = 4;
	public static final int NP_FILTERS_VIEW_INDEX = 5;
	public static final int NP_WORDS_VIEW_INDEX = 6;
	public static final int NP_PREDICTED_VS_CORRECT_WORDS_VIEW_INDEX = 7;
	public static final int NP_GRAPHEME_NATURAL_CLASSES_VIEW_INDEX = 8;
	public static final int NP_ENVIRONMENTS_VIEW_INDEX = 9;

	public static final int OT_SEGMENT_INVENTORY_VIEW_INDEX = 0;
	public static final int OT_NATURAL_CLASSES_VIEW_INDEX = 1;
	public static final int OT_CONSTRAINTS_VIEW_INDEX = 2;
	public static final int OT_CONSTRAINT_RANKINGS_VIEW_INDEX = 3;
	public static final int OT_WORDS_VIEW_INDEX = 4;
	public static final int OT_PREDICTED_VS_CORRECT_WORDS_VIEW_INDEX = 5;
	public static final int OT_GRAPHEME_NATURAL_CLASSES_VIEW_INDEX = 6;
	public static final int OT_ENVIRONMENTS_VIEW_INDEX = 7;

	// Unit Testing constants
	public static final String UNIT_TEST_DATA_FILE_NAME = "test/org/sil/syllableparser/testData/CVTestData.";
	public static final String UNIT_TEST_DATA_FILE = "test/org/sil/syllableparser/testData/CVTestData.ashedata";
	public static final String UNIT_TEST_BACKUP_FILE_DIRECTORY = "test/org/sil/syllableparser/testData";
	public static final String UNIT_TEST_BACKUP_FILE_NAME = "test/org/sil/syllableparser/testData/CVTestData.ashebackup";
	public static final String UNIT_TEST_BACKUP_ZIP_ENTRY_NAME = "CVTestData.ashedata";
	public static final String UNIT_TEST_BACKUP_ZIP_ENTRY_COMMENT = "Comment included in zipped file: m\u00e1s o menos.";
	public static final String UNIT_TEST_DATA_FILE_2_NAME = "test/org/sil/syllableparser/testData/CVTestData2.";
	public static final String UNIT_TEST_DATA_FILE_2 = "test/org/sil/syllableparser/testData/CVTestData2.ashedata";
	public static final String UNIT_TEST_DATA_FILE_3_NAME = "test/org/sil/syllableparser/testData/CVTestData3.";
	public static final String UNIT_TEST_DATA_FILE_3 = "test/org/sil/syllableparser/testData/CVTestData3.ashedata";
	public static final String UNIT_TEST_DATA_FILE_4_NAME = "test/org/sil/syllableparser/testData/CVTestData4.";
	public static final String UNIT_TEST_DATA_FILE_4 = "test/org/sil/syllableparser/testData/CVTestData4.ashedata";
	public static final String UNIT_TEST_DATA_FILE_VERSION_000 = "test/org/sil/syllableparser/testData/CVTestDataVersion000.ashedata";
	public static final String UNIT_TEST_DATA_FILE_VERSION_001 = "test/org/sil/syllableparser/testData/CVTestDataVersion001.ashedata";
	public static final String UNIT_TEST_DATA_FILE_VERSION_002 = "test/org/sil/syllableparser/testData/CVTestDataVersion002.ashedata";
	public static final String UNIT_TEST_DATA_FILE_VERSION_2 = "test/org/sil/syllableparser/testData/CVTestDataVersion2.ashedata";
	public static final String UNIT_TEST_DATA_FILE_ENVIRONMENTS = "test/org/sil/syllableparser/testData/CVTestDataEnvironments.ashedata";
	public static final String UNIT_TEST_DATA_FILE_TEMPLATES_FILTERS = "test/org/sil/syllableparser/testData/TemplatesFiltersTestData.ashedata";
	public static final String UNIT_TEST_DATA_FILE_ICU_RULES = "test/org/sil/syllableparser/testData/ICURulesTestData.ashedata";

	// Text colors
	public static final Color ACTIVE = Color.BLACK;
	public static final Color INACTIVE = Color.GRAY;
	public static final Color PARSER_SUCCESS = Color.GREEN;
	public static final Color PARSER_FAILURE = Color.RED;
	public static final Color ENVIRONMENT_ERROR_MESSAGE = Color.CRIMSON;
	public static final Color SLOTS_ERROR_MESSAGE = Color.CRIMSON;
	public static final Color TYPE_WARNING_MESSAGE = Color.ORANGERED;
	public static final String RULES_ERROR_MESSAGE = "crimson";
	
	// Try a Word constants
	public static final String TRY_A_WORD_INTERBLOCK_CSS = ".interblock {\n"
			+ "\tdisplay: -moz-inline-box;\n"
			+ "\tdisplay: inline-block;\n"
			+ "\tvertical-align: top;\n"
			+ "\tcursor: pointer;\n"
			+ "\t\n";
	public static final String TRY_A_WORD_JAVASCRIPT = "<script language=\"JavaScript\" id=\"clientEventHandlersJS\">\n"
			+ "	function ButtonShowDetails()\n"
			+ "	{\n"
			+ "	if (TraceSection.style.display == 'none')\n"
			+ "	{\n"
			+ "	  TraceSection.style.display = 'block';\n"
			+ "	  ShowDetailsButton.value = \"Hide Details\";\n"
			+ "	}\n"
			+ "	else\n"
			+ "	{\n"
			+ "	  TraceSection.style.display = 'none';\n"
			+ "	  ShowDetailsButton.value = \"Show Details\";\n"
			+ "	}\n"
			+ "	}\n"
			+ "	// Center the mouse position in the browser\n"
			+ "	function CenterNodeInBrowser(node)\n"
			+ "	{\n"
			+ "	var posx = 0;\n"
			+ "	var posy = 0;\n"
			+ "	if (!e) var e = window.event;\n"
			+ "	if (e.pageX || e.pageY)\n"
			+ "	{\n"
			+ "		posx = e.pageX;\n"
			+ "		posy = e.pageY;\n"
			+ "	}\n"
			+ "	else if (e.clientX || e.clientY)\n"
			+ "	{\n"
			+ "		posx = e.clientX + document.body.scrollLeft;\n"
			+ "		posy = e.clientY + document.body.scrollTop;\n"
			+ "	}\n"
			+ "	// posx and posy contain the mouse position relative to the document\n"
			+ "	curY = findPosY(node);\n"
			+ "	offset = document.body.clientHeight/2;\n"
			+ "	window.scrollTo(0, curY-offset); // scroll to about the middle if possible\n"
			+ "	}\n"
			+ "	// findPosX() and findPosY() are from http://www.quirksmode.org/js/findpos.html\n"
			+ "	function findPosX(obj)\n"
			+ "{\n"
			+ "	var curleft = 0;\n"
			+ "	if (obj.offsetParent)\n"
			+ "	{\n"
			+ "		while (obj.offsetParent)\n"
			+ "		{\n"
			+ "			curleft += obj.offsetLeft\n"
			+ "			obj = obj.offsetParent;\n"
			+ "		}\n"
			+ "	}\n"
			+ "	else if (obj.x)\n"
			+ "		curleft += obj.x;\n"
			+ "	return curleft;\n"
			+ "}\n"
			+ "\n"
			+ "function findPosY(obj)\n"
			+ "{\n"
			+ "	var curtop = 0;\n"
			+ "	if (obj.offsetParent)\n"
			+ "	{\n"
			+ "		while (obj.offsetParent)\n"
			+ "		{\n"
			+ "			curtop += obj.offsetTop\n"
			+ "			obj = obj.offsetParent;\n"
			+ "		}\n"
			+ "	}\n"
			+ "	else if (obj.y)\n"
			+ "		curtop += obj.y;\n"
			+ "	return curtop;\n"
			+ "}\n"
			+ "\n"
			+ "// nextSibling function that skips over textNodes.\n"
			+ "function NextNonTextSibling(node)\n"
			+ "{\n"
			+ "	while(node.nextSibling.nodeName == \"#text\")\n"
			+ "		node = node.nextSibling;\n"
			+ "\n"
			+ "	return node.nextSibling;\n"
			+ "}\n"
			+ "\n"
			+ "// This script based on the one given in http://www.codeproject.com/jscript/dhtml_treeview.asp.\n"
			+ "function Toggle(node, path, imgOffset)\n"
			+ "{\n"
			+ "\n"
			+ "	Images = new Array('beginminus.gif', 'beginplus.gif', 'lastminus.gif', 'lastplus.gif', 'minus.gif', 'plus.gif', 'singleminus.gif', 'singleplus.gif',\n"
			+ "										 'beginminusRTL.gif', 'beginplusRTL.gif', 'lastminusRTL.gif', 'lastplusRTL.gif', 'minusRTL.gif', 'plusRTL.gif', 'singleminusRTL.gif', 'singleplusRTL.gif');\n"
			+ "	// Unfold the branch if it isn't visible\n"
			+ "\n"
			+ "	if (NextNonTextSibling(node).style.display == 'none')\n"
			+ "	{\n"
			+ "		// Change the image (if there is an image)\n"
			+ "		if (node.childNodes.length > 0)\n"
			+ "		{\n"
			+ "			if (node.childNodes.item(0).nodeName == \"IMG\")\n"
			+ "			{\n"
			+ "				var str = node.childNodes.item(0).src;\n"
			+ "				var pos = str.indexOf(Images[1 + imgOffset]); // beginplus.gif\n"
			+ "				if (pos >= 0)\n"
			+ "				{\n"
			+ "					node.childNodes.item(0).src = path + Images[0 + imgOffset]; // \"beginminus.gif\";\n"
			+ "				}\n"
			+ "				else\n"
			+ "				{\n"
			+ "					pos = str.indexOf(Images[7 + imgOffset]); // \"singleplus.gif\");\n"
			+ "					if (pos >= 0)\n"
			+ "					{\n"
			+ "						node.childNodes.item(0).src = path + Images[6 + imgOffset]; // \"singleminus.gif\";\n"
			+ "					}\n"
			+ "					else\n"
			+ "					{\n"
			+ "						pos = str.indexOf(Images[3 + imgOffset]); // \"lastplus.gif\");\n"
			+ "						if (pos >= 0)\n"
			+ "						{\n"
			+ "							node.childNodes.item(0).src = path + Images[2 + imgOffset]; // \"lastminus.gif\";\n"
			+ "						}\n"
			+ "						else\n"
			+ "						{\n"
			+ "							node.childNodes.item(0).src = path + Images[4 + imgOffset]; // \"minus.gif\";\n"
			+ "						}\n"
			+ "					}\n"
			+ "				}\n"
			+ "			}\n"
			+ "		}\n"
			+ "		NextNonTextSibling(node).style.display = 'block';\n"
			+ "		CenterNodeInBrowser(node);\n"
			+ "	}\n"
			+ "	// Collapse the branch if it IS visible\n"
			+ "	else\n"
			+ "	{\n"
			+ "		// Change the image (if there is an image)\n"
			+ "		if (node.childNodes.length > 0)\n"
			+ "		{\n"
			+ "			if (node.childNodes.item(0).nodeName == \"IMG\")\n"
			+ "				var str = node.childNodes.item(0).src;\n"
			+ "				var pos = str.indexOf(Images[0 + imgOffset]); // \"beginminus.gif\");\n"
			+ "				if (pos >= 0)\n"
			+ "				{\n"
			+ "					node.childNodes.item(0).src = path + Images[1 + imgOffset]; // \"beginplus.gif\";\n"
			+ "				}\n"
			+ "				else\n"
			+ "				{\n"
			+ "					pos = str.indexOf(Images[6 + imgOffset]); // \"singleminus.gif\");\n"
			+ "					if (pos >= 0)\n"
			+ "					{\n"
			+ "						node.childNodes.item(0).src = path + Images[7 + imgOffset]; // \"singleplus.gif\";\n"
			+ "					}\n"
			+ "					else\n"
			+ "					{\n"
			+ "						pos = str.indexOf(Images[2 + imgOffset]); // \"lastminus.gif\");\n"
			+ "						if (pos >= 0)\n"
			+ "						{\n"
			+ "							node.childNodes.item(0).src = path + Images[3 + imgOffset]; // \"lastplus.gif\";\n"
			+ "						}\n"
			+ "						else\n"
			+ "						{\n"
			+ "							node.childNodes.item(0).src = path + Images[5 + imgOffset]; // \"plus.gif\";\n"
			+ "						}\n"
			+ "					}\n"
			+ "				}\n"
			+ "	}\n"
			+ "	NextNonTextSibling(node).style.display = 'none';\n"
			+ "}\n"
			+ "}\n"
			+ "			</script>\n";
}
