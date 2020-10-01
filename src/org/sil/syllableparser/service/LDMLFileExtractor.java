// Copyright (c) 2020 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.sil.syllableparser.Constants;
import org.sil.utility.HandleExceptionMessage;
import org.w3c.dom.Document;

/**
 * @author Andy Black
 *
 */
public class LDMLFileExtractor {

	File ldmlFile;

	public LDMLFileExtractor(File dataFile) {
		this.ldmlFile = dataFile;
	}

	public File getLdmlFile() {
		return ldmlFile;
	}

	public void setLdmlFile(File ldmlFile) {
		this.ldmlFile = ldmlFile;
	}

	public String getIcuRules() {
		String sIcuRules = "";
		try {
			File result = applyExtractIcuRulesTransformToFile(ldmlFile);
			sIcuRules = new String(Files.readAllBytes(result.toPath()),
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sIcuRules;
	}

	private File applyExtractIcuRulesTransformToFile(File file) {
		String stylesheet = Constants.LDML_XSLT_FILE_NAME;
		File tempSaveFile = null;
		try {
			tempSaveFile = File.createTempFile("AsheninkaLdmlFileExtraction", ".ldml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// if (tempSaveFile != null) {
		// tempSaveFile.deleteOnExit();
		// }
		try {
			File xslt = new File(stylesheet);
			if (!xslt.exists()) {
				throw new DataMigrationException(xslt.getPath());
			}
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);
			// Use a Transformer for output
			TransformerFactory tFactory = TransformerFactory.newInstance();
			StreamSource stylesource = new StreamSource(stylesheet);
			Transformer transformer = tFactory.newTransformer(stylesource);

			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(tempSaveFile);
			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
			HandleExceptionMessage.show("LDML File Extraction Error", "Failed to transform", e.getMessage(), true);
		}
		return tempSaveFile;
	}
}
