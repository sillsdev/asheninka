/**
 * 
 */
package sil.org.syllableparser.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Andy Black
 *
 */
public class BackupFileCreator {

	File dataFile;
	String backupFileName;
	String comment;
	
	public BackupFileCreator(File dataFile, String backupFileName, String comment) {
		this.dataFile = dataFile;
		this.backupFileName = backupFileName;
		this.comment = comment;
	}

	public File getDataFile() {
		return dataFile;
	}

	public void setDataFile(File dataFile) {
		this.dataFile = dataFile;
	}

	public String getBackupFileName() {
		return backupFileName;
	}

	public void setBackupFileName(String backupFileName) {
		this.backupFileName = backupFileName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void doBackup() {
		try {
			FileOutputStream fos = new FileOutputStream(backupFileName);
			ZipOutputStream zippedFile = new ZipOutputStream(fos);
			String filename = dataFile.getName();
            ZipEntry zipEntry = new ZipEntry(filename);
            zipEntry.setTime(dataFile.lastModified());
            zippedFile.putNextEntry(zipEntry);
            FileInputStream ins = new FileInputStream(dataFile);
            byte[] buf = new byte[1024];
            int len;
            while((len=ins.read(buf))>0){
                zippedFile.write(buf, 0, len);
            }
            ins.close();
            zippedFile.closeEntry();
            zippedFile.setComment(comment);
            zippedFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}