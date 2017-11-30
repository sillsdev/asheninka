// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.backendprovider.XMLBackEndProvider;
import org.sil.syllableparser.model.LanguageProject;

/**
 * @author Andy Black
 *
 */
public class BackupFileRestorer {

	File backupFile;

	public BackupFileRestorer(File backupFile) {
		this.backupFile = backupFile;
	}

	public File getBackupFile() {
		return backupFile;
	}

	public void setBackupFile(File backupFile) {
		this.backupFile = backupFile;
	}

	public void doRestore(LanguageProject languageProject, Locale locale) {
		try {
			File temp = unzipFileToRestore();
			loadData(languageProject, locale, temp);
			temp.delete();
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private File unzipFileToRestore() throws FileNotFoundException, IOException {
		ZipInputStream zis = new ZipInputStream(new FileInputStream(backupFile));
		ZipEntry entry = zis.getNextEntry();
		String restoredFileName = entry.getName();
		File temp = File.createTempFile(restoredFileName, null);
		extractFileToRestore(zis, temp);
		zis.closeEntry();
		zis.close();
		return temp;
	}

	public void loadData(LanguageProject languageProject, Locale locale, File temp) {
		XMLBackEndProvider xmlBackEndProvider = new XMLBackEndProvider(languageProject, locale);
		xmlBackEndProvider.loadLanguageDataFromFile(temp);
	}

	public void extractFileToRestore(ZipInputStream zis, File temp) throws FileNotFoundException,
			IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(temp));
		byte[] bytesIn = new byte[4096];
		int read = 0;
		while ((read = zis.read(bytesIn)) != -1) {
		    bos.write(bytesIn, 0, read);
		}
		bos.close();
	}
}
