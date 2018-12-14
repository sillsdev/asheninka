// Copyright (c) 2018 SIL International
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.view;

import javafx.application.Platform;

import org.sil.syllableparser.model.sonorityhierarchyapproach.SHApproach;

/**
 * @author Andy Black
 *
 */

public class SHSegmentInventoryController extends SegmentInventoryCommonController {

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param cvApproachController
	 */
	public void setData(SHApproach shApproachData) {
		shApproach = shApproachData;
		languageProject = shApproach.getLanguageProject();

		// Add observable list data to the table
		cvSegmentTable.setItems(languageProject.getSegmentInventory());
		int max = cvSegmentTable.getItems().size();
		if (max > 0) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					int iLastIndex = mainApp.getApplicationPreferences()
							.getLastCVSegmentInventoryViewItemUsed();
					iLastIndex = adjustIndexValue(iLastIndex, max);
					cvSegmentTable.requestFocus();
					cvSegmentTable.getSelectionModel().select(iLastIndex);
					cvSegmentTable.getFocusModel().focus(iLastIndex);
					cvSegmentTable.scrollTo(iLastIndex);
				}
			});
		}
	}
}
