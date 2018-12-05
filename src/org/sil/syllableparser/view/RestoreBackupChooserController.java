// Copyright (c) 2016-2018 SIL International
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import java.io.File;

import org.sil.syllableparser.Constants;
import org.sil.syllableparser.model.BackupFile;
import org.sil.syllableparser.service.BackupFileRestorer;
import org.sil.utility.view.ControllerUtilities;

/**
 * @author Andy Black
 *
 */
public class RestoreBackupChooserController extends BackupChooserController {

	@Override
	protected void handleOk() {
		int i = restoreBackupTable.getSelectionModel().getSelectedIndex();
		BackupFile bup = backupFiles.get(i);
		String backupFileToUse = backupDirectory + File.separator + bup.getFilePath();
		File backupFile = new File(backupFileToUse);
		BackupFileRestorer restorer = new BackupFileRestorer(backupFile);
		restorer.doRestore(languageProject, locale);

		// Force user to do a file save as so they do not overwrite the current
		// data file
		// with the restored data (unless they so choose).
		String sFileFilterDescription = bundle.getString("file.filterdescription");
		String syllableParserFilterDescription = sFileFilterDescription + " ("
				+ Constants.ASHENINKA_DATA_FILE_EXTENSIONS + ")";
		ControllerUtilities.doFileSaveAs(mainApp, locale, true, syllableParserFilterDescription,
				bundle.getString("menu.projectmanagementrestore"),
				Constants.ASHENINKA_DATA_FILE_EXTENSION, Constants.ASHENINKA_DATA_FILE_EXTENSIONS,
				Constants.RESOURCE_LOCATION);

		okClicked = true;
		dialogStage.close();

	}
}
