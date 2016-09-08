// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import sil.org.syllableparser.Constants;

/**
 * @author Andy Black
 *
 */
public class BackupFile {

	private final StringProperty filePath;
	private final StringProperty fileName;
	private final StringProperty date;
	private final StringProperty description;
	
	public BackupFile() {
		filePath = new SimpleStringProperty("");
		fileName = new SimpleStringProperty("");
		date = new SimpleStringProperty("");
		description = new SimpleStringProperty("");
	}
	
	public BackupFile(String filePath, String description) {
		this.filePath = new SimpleStringProperty(filePath);
		this.fileName = new SimpleStringProperty(getFileNameFromPath()); 
		this.date = new SimpleStringProperty(getDateFromPath());
		this.description = new SimpleStringProperty(description);
	}

	protected String getFileNameFromPath() {
		// We assume it ends with yyyymmdd-hhmmss.ashebackup
		String path = filePath.get();
		int extensionIndex = path.lastIndexOf(Constants.ASHENINKA_BACKUP_FILE_EXTENSION);
		String fileName = path.substring(0, extensionIndex - 16);
		return fileName;
	}
	protected String getDateFromPath() {
		// We assume it ends with yyyymmdd-hhmmss.ashebackup
		String path = filePath.get();
		int length = path.length();
		int extensionIndex = path.lastIndexOf(Constants.ASHENINKA_BACKUP_FILE_EXTENSION);
		String dateTime = path.substring(length - 26, extensionIndex -1);
		String year = dateTime.substring(0, 4);
		String month = dateTime.substring(4, 6);
		String day = dateTime.substring(6, 8);
		String hour = dateTime.substring(9, 11);
		String minute = dateTime.substring(11, 13);
		String seconds = dateTime.substring(13, 15);
		String result = String.format("%s.%s.%s %s:%s:%s", year, month, day, hour, minute, seconds);
		return result;
	}
	public String getFilePath() {
		return filePath.get();
	}

	public void setFilePath(String filePath) {
		this.filePath.set(filePath);
	}

	public String getDate() {
		return date.get();
	}

	public StringProperty dateProperty() {
		return date;
	}

	public void setDate(String date) {
		this.date.set(date);;
	}

	public String getDescription() {
		return description.get();
	}
	
	public StringProperty descriptionProperty() {
		return description;
	}

	public void setDescription(String description) {
		this.description.set(description);;
	}

	public String getFileName() {
		return fileName.get();
	}
	/**
	 * @return the fileName
	 */
	public StringProperty fileNameProperty() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName.set(fileName);;
	}

}
