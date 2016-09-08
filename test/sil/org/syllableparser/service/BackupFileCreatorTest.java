// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.service;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javafx.collections.ObservableList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.nio.zipfs.ZipDirectoryStream;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.backendprovider.XMLBackEndProvider;
import sil.org.syllableparser.model.LanguageProject;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.cvapproach.CVApproach;

/**
 * @author Andy Black
 *
 */
public class BackupFileCreatorTest {

	File backupFile;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		backupFile = new File(Constants.UNIT_TEST_BACKUP_FILE_NAME);
		if (backupFile.exists()) {
			backupFile.delete();
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertFalse(backupFile.exists());
		BackupFileCreator backupCreator = new BackupFileCreator(new File(
				Constants.UNIT_TEST_DATA_FILE), Constants.UNIT_TEST_BACKUP_FILE_NAME, 
				Constants.UNIT_TEST_BACKUP_ZIP_ENTRY_COMMENT);
		backupCreator.doBackup();
		assertTrue(backupFile.exists());
		try {
			ZipFile zipFile = new ZipFile(backupFile);
			assertEquals(1, zipFile.size());
			ZipEntry entry = zipFile.getEntry(Constants.UNIT_TEST_BACKUP_ZIP_ENTRY_NAME);
			assertEquals(Constants.UNIT_TEST_BACKUP_ZIP_ENTRY_NAME,entry.getName());
			assertEquals(3048978,entry.getSize());
			assertEquals(Constants.UNIT_TEST_BACKUP_ZIP_ENTRY_COMMENT, zipFile.getComment());
			zipFile.close();
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
