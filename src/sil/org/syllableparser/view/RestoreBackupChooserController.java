// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.view;

import java.io.File;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.model.BackupFile;
import sil.org.syllableparser.service.BackupFileRestorer;

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

		// Force user to do a file save as so they do not overwrite the current data file
		// with the restored data (unless they so choose).
		String sFileFilterDescription = bundle.getString("file.filterdescription");
		String syllableParserFilterDescription = sFileFilterDescription + " ("
				+ Constants.ASHENINKA_DATA_FILE_EXTENSIONS + ")";
		ControllerUtilities.doFileSaveAs(mainApp, locale, true, syllableParserFilterDescription);

		okClicked = true;
		dialogStage.close();

	}
}
