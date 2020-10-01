// Copyright (c) 2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later
// (http://www.gnu.org/licenses/lgpl-2.1.html)
/**
 *
 */
package org.sil.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

import javafx.scene.text.Font;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.Grapheme;
import org.sil.syllableparser.model.Language;
import org.sil.syllableparser.model.LanguageProject;
import org.sil.syllableparser.model.Segment;
import org.sil.syllableparser.model.cvapproach.CVApproach;
import org.sil.syllableparser.service.DatabaseMigrator;
import org.sil.utility.view.JavaFXThreadingRule;

/**
 * @author Andy Black
 *
 */
public class LDMLFileExtractorTest {

	File ldmlFile;
	LDMLFileExtractor extractor;
	LanguageProject languageProject;
	String sTestDataDirectory = "test/org/sil/syllableparser/testData/LDML";
	String sIcuRules = "";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAbq() {
		String sExpected = "&[before 1] [first regular] < А << а < Б << б < В << в "
				+ "< Г << г < Гв << гв < Гъ << гъ < Гъв << гъв < Гъь << гъь < Гь << гь < ГӀ << гӀ < ГӀв << гӀв "
				+ "< Д << д < Дж << дж < Джв << джв < Джь << джь < Дз << дз < Е << е < Ё << ё "
				+ "< Ж << ж < Жв << жв < Жь << жь < З << з < И << и < Й << й "
				+ "< К << к < Кв << кв < Къ << къ < Къв << къв < Къь << къь < Кь << кь < КӀ << кӀ < КӀв << кӀв < КӀь << кӀь "
				+ "< Л << л < Ль << ль < М << м < Н << н < О << о < П << п < ПӀ << пӀ < Р << р < С << с "
				+ "< Т << т < Тл << тл < Тш << тш < ТӀ << тӀ < У << у < Ф << ф "
				+ "< Х << х < Хв << хв < Хъ << хъ < Хъв << хъв < Хь << хь < ХӀ << хӀ < ХӀв << хӀв "
				+ "< Ц << ц < ЦӀ << цӀ < Ч << ч < Чв << чв < ЧӀ << чӀ < ЧӀв << чӀв "
				+ "< Ш << ш < Шв << шв < ШӀ << шӀ < Щ << щ < Ъ << ъ < Ы << ы < ь < Э << э < Ю << ю < Я << я";
		getAndcheckIcuRulesFromFile(sExpected, "abq.ldml");
	}

	@Test
	public void testBcc() {
		String sExpected = "& آ < ا\n" + "& ڑ < ز\n" + "& ۆ < ئو\n" + "& ئو < ه\n" + "& ێ < ے\n"
				+ "& ے < ئی\n" + "& ئی < ئے";
		getAndcheckIcuRulesFromFile(sExpected, "bcc.ldml");
	}

	@Test
	public void testChy() {
		String sExpected = "\n& k < '\n" + "& s < š <<< Š\n" + "& [last tertiary ignorable]-()/=";
		getAndcheckIcuRulesFromFile(sExpected, "chy.ldml");
	}

	@Test
	public void testEn() {
		String sExpected = "";
		getAndcheckIcuRulesFromFile(sExpected, "en.ldml");
	}

	@Test
	public void testEs() {
		String sExpected = "\n& N < ñ <<< Ñ\n" + "& C < ch <<< Ch <<< CH\n"
				+ "& l < ll <<< Ll <<< LL\n" + "& [last tertiary ignorable]¿\n"
				+ "& [last tertiary ignorable]¡";
		getAndcheckIcuRulesFromFile(sExpected, "es.ldml");
	}

	@Test
	public void testMxb() {
		String sExpected = "[alternate shifted]\n"
				+ "& [first primary ignorable] << ¿\n"
				+ "& [first primary ignorable] << ¡\n"
				+ "& [first primary ignorable] << ‘‘\n"
				+ "& [first primary ignorable] << ’’\n"
				+ "& [first primary ignorable] << ‘\n"
				+ "& [first secondary ignorable] << ­na << ­ná << ­na̱ << ­ña << ­ñá << ­ña̱ << ­rá << ­ri << ­ri̱ << ­xí\n"
				+ "& C < ch <<< Ch << CH\n" + "& N < ñ <<< Ñ\n"
				+ "& a << á << a̱ << aꞌ << áꞌ << a̱ꞌ <<< A << Á << A̱ << Aꞌ << Áꞌ << A̱ꞌ\n"
				+ "& e << é << e̱ << eꞌ << éꞌ << e̱ꞌ <<< E << É << E̱ << Eꞌ << Éꞌ << E̱ꞌ\n"
				+ "& i << í << i̱ << iꞌ << íꞌ << i̱ꞌ <<< I << Í << I̱ << Iꞌ << Íꞌ << I̱ꞌ\n"
				+ "& o << ó << o̱ << oꞌ << óꞌ << o̱ꞌ <<< O << Ó << O̱ << Oꞌ << Óꞌ << O̱ꞌ\n"
				+ "& u << ú << u̱ << uꞌ << úꞌ << u̱ꞌ <<< U << Ú << U̱ << Uꞌ << Úꞌ << U̱ꞌ";
		getAndcheckIcuRulesFromFile(sExpected, "mxb.ldml");
	}

	@Test
	public void testNoa() {
		String sExpected = "\n& [before 1] [first non ignorable] < A << a < Ã << ã < Ä << ä "
				+ "< B << b < Ch << ch < D << d < E << e < Ë << ẽ < ë < G << g < H << h < I << i < Ĩ << ĩ "
				+ "< J << j < K << k < K' << k' < L << l < M << m < N << n < Ñ << ñ < O << o < Õ << õ < Ö << ö "
				+ "< P << p < P' << p' < R << r < Rr << rr < S << s < T << t < T' << t' < U << u < Ũ << ũ "
				+ "< W << w < Y << y <  << ʌ < ̃ << ʌ̃ < ̈ << ʌ̈";
		getAndcheckIcuRulesFromFile(sExpected, "noa.ldml");
	}

	@Test
	public void testPugBF() {
		String sExpected = "[backwards 2]\n" + "& ae << æ <<< Æ\n" + "& A < ã <<< Ã\n"
				+ "& B < ɓ <<< Ɓ\n" + "& C < ch <<< Ch <<< CH\n" + "& D < ɗ <<< Ɗ\n"
				+ "& E < ẽ <<< Ẽ\n" + "& Ẽ < ɛ <<< Ɛ\n" + "& Ɛ < ɛ̃ <<< Ɛ̃\n"
				+ "& G < gb <<< Gb <<< GB\n" + "& I < ĩ <<< Ĩ\n" + "& Ĩ < ɩ <<< Ɩ\n"
				+ "& Ɩ < ɩ̃ <<< Ɩ̃\n" + "& K < kh <<< Kh <<< KH\n" + "& KH < kp <<< Kp <<< KP\n"
				+ "& N < ɲ <<< Ɲ\n" + "& Ɲ < ŋ <<< Ŋ\n" + "& Ŋ < ŋm <<< Ŋm <<< ŊM\n"
				+ "& O < õ <<< Õ\n" + "& Õ < ɔ <<< Ɔ\n" + "& Ɔ < ɔ̃ <<< Ɔ̃\n"
				+ "& P < ph <<< Ph <<< PH\n" + "& T < th <<< Th <<< TH\n" + "& U < ũ <<< Ũ\n"
				+ "& Ũ < ʋ <<< Ʋ\n" + "& Ʋ < ʋ̃ <<< Ʋ̃\n" + "& W <  <<< \n" + "& Y < ƴ <<< Ƴ";
		getAndcheckIcuRulesFromFile(sExpected, "pug-bf.ldml");
	}

	@Test
	public void testSei() {
		String sExpected = "&[before 1] [first regular] < a\\/A << á\\/Á < b\\/B < c\\/C < d\\/D < e\\/E << é\\/É "
				+ "< f\\/F < g\\/G < h\\/H < i\\/I << í\\/Í < j\\/J < k\\/K < l\\/L < m\\/M < n\\/N < ñ\\/Ñ "
				+ "< o\\/O << ó\\/Ó < p\\/P < q\\/Q < r\\/R < s\\/S < t\\/T < u\\/U << ú\\/Ú << ü\\/Ü < v\\/V "
				+ "< w\\/W < x\\/X < y\\/Y < z\\/Z";
		getAndcheckIcuRulesFromFile(sExpected, "sei.ldml");
	}

	@Test
	public void testStpNew() {
		String sExpected = "[alternate shifted]\n" + "&[last tertiary ignorable] = \\u02BC\n"
				+ "& [first primary ignorable] << ¿\n" + "& [first primary ignorable] << ¡\n"
				+ "& [first primary ignorable] << ‘‘\n" + "& [first primary ignorable] << ’’\n"
				+ "& [first primary ignorable] << ‘\n" + "& [first primary ignorable] << ’\n"
				+ "& B < bh <<< Bh <<< BH\n" + "& C < ch <<< Ch <<< CH\n"
				+ "& D < dh <<< Dh <<< DH\n" + "& E <  <<< \n" + "& I < ɨ <<< Ɨ\n"
				+ "& L < lh <<< Lh <<< LH\n" + "& N < ñ <<< Ñ";
		getAndcheckIcuRulesFromFile(sExpected, "stpNew.ldml");
	}

	@Test
	public void testStpOld() {
		String sExpected = "[alternate shifted]\n" + "& [first primary ignorable] << ¿\n"
				+ "& [first primary ignorable] << ¡\n" + "& [first primary ignorable] << ‘‘\n"
				+ "& [first primary ignorable] << ’’\n" + "& [first primary ignorable] << ‘\n"
				+ "& [first primary ignorable] << ’\n" + "& B < bh <<< Bh <<< BH\n"
				+ "& C < ch <<< Ch <<< CH\n" + "& D < dh <<< Dh <<< DH\n" + "& E <  <<< \n"
				+ "& I < ɨ <<< Ɨ\n" + "& L < lh <<< Lh <<< LH\n" + "& N < ñ <<< Ñ";
		getAndcheckIcuRulesFromFile(sExpected, "stpOld.ldml");
	}

	@Test
	public void testSv() {
		String sExpected = "\n& [before 1] [first non ignorable]abcdefghijklmnopqrstuvwxyz < å < ä < ö";
		getAndcheckIcuRulesFromFile(sExpected, "sv.ldml");
	}

	@Test
	public void testZtu() {
		String sExpected = "\n& a << á << à << â << aꞌ << áꞌ << àꞌ << âꞌ\n"
				+ "& C < ch <<< Ch <<< CH\n"
				+ "& e << é << è << ê << eꞌ << éꞌ << èꞌ << êꞌ\n"
				+ "& i << í << ì << î << iꞌ << íꞌ << ìꞌ << îꞌ < ɨ << ɨ́ << ɨ̀ << ɨ̂ << ɨꞌ << ɨ́ꞌ << ɨ̀ꞌ << ɨ̂ꞌ\n"
				+ "& o << ó << ò << ô << oꞌ << óꞌ << òꞌ << ôꞌ\n" + "& T < ts <<< Ts <<< TS\n"
				+ "& u << ú << ù << û << uꞌ << úꞌ << ùꞌ << ûꞌ";
		getAndcheckIcuRulesFromFile(sExpected, "ztu.ldml");
	}

	protected void getAndcheckIcuRulesFromFile(String sExpected, String sLdmlFile) {
		ldmlFile = new File(sTestDataDirectory + File.separator + sLdmlFile);
		extractor = new LDMLFileExtractor(ldmlFile);
		sIcuRules = extractor.getIcuRules();
		assertEquals(sExpected, sIcuRules.replaceAll("\r", ""));
	}
}
