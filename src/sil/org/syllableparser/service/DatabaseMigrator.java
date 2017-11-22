// Copyright (c) 2016-2017 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import sil.org.syllableparser.Constants;
import sil.org.utility.HandleExceptionMessage;

/**
 * @author Andy Black
 *
 */
public class DatabaseMigrator {

	File databaseFile;

	public DatabaseMigrator(File dataFile) {
		this.databaseFile = dataFile;
	}

	public File getDatabaseFile() {
		return databaseFile;
	}

	public void setDatabaseFile(File databaseFile) {
		this.databaseFile = databaseFile;
	}

	public int getVersion() {
		int version = -1;
		InputStreamReader reader;
		try {
			reader = new InputStreamReader(new FileInputStream(databaseFile),
					Constants.UTF8_ENCODING);
			BufferedReader bufr = new BufferedReader(reader);
			String line = bufr.readLine();
			while (line != null && !line.contains("<languageProject")) {
				line = bufr.readLine();
			}
			int i = line.indexOf("databaseVersion=");
			if (i == -1) {
				// it's the first version which did not have a database version
				// number
				version = 1;
			} else {
				String s = line.substring(i + 17);
				int iEnd = s.indexOf("\"");
				if (iEnd > -1) {
					version = Integer.parseInt(s.substring(0, iEnd));
				}
			}
			bufr.close();
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}

	public void doMigration() {
		try {
			int version = getVersion();
			// make a backup of the database file just in case
			String sBackupFileName = databaseFile.getPath().replaceFirst(
					Constants.ASHENINKA_DATA_FILE_EXTENSION, "bak");
			Files.copy(Paths.get(databaseFile.getPath()), Paths.get(sBackupFileName),
					StandardCopyOption.REPLACE_EXISTING);

			File file = databaseFile;
			while (version < Constants.CURRENT_DATABASE_VERSION) {
				file = applyMigrationTransformToFile(version, file);
				version++;
			}
			Files.copy(Paths.get(file.getPath()), Paths.get(databaseFile.getPath()),
					StandardCopyOption.REPLACE_EXISTING);
			// TODO: delete the temp files
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private File applyMigrationTransformToFile(int version, File file) {
		String stylesheet = Constants.MIGRATION_XSLT_FILE_NAME + version + ".xsl";
		File tempSaveFile = null;
		try {
			tempSaveFile = File.createTempFile("AsheninkaDataMigration" + version, ".ashedata");
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
			HandleExceptionMessage.show("Migration Error", "Failed to transform", e.getMessage(), true);
		}
		return tempSaveFile;
	}
}
