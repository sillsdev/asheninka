/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.view;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.model.TemplateFilter;
import org.sil.syllableparser.model.oncapproach.ONCApproach;

/**
 * @author Andy Black
 *
 */
public class ONCFiltersController extends FiltersController {

	protected void rememberSelection(int iCurrentIndex) {
		mainApp.getApplicationPreferences().setLastONCFiltersViewItemUsed(iCurrentIndex);
	}

	public void setData(ONCApproach oncApproachData) {
		oncApproach = oncApproachData;
		languageProject = oncApproach.getLanguageProject();
		setDataProcessing(ApplicationPreferences.LAST_ONC_FILTERS_VIEW_ITEM_USED);
	}

	@Override
	protected void showTypeWarning(TemplateFilter filter) {
		switch (filter.getType()) {
		case "Syllable":
			typeWarningMessage.setText(bundle.getString("label.oncfilterwarning"));
			typeWarningMessage.setVisible(true);
			break;
		default:
			typeWarningMessage.setVisible(false);
			break;
		}
	}
}
