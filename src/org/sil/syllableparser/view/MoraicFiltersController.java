/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.view;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.model.TemplateFilter;
import org.sil.syllableparser.model.moraicapproach.MoraicApproach;

/**
 * @author Andy Black
 *
 */
public class MoraicFiltersController extends FiltersController {

	protected void rememberSelection(int iCurrentIndex) {
		mainApp.getApplicationPreferences().setLastMoraicFiltersViewItemUsed(iCurrentIndex);
	}

	public void setData(MoraicApproach moraicApproachData) {
		moraicApproach = moraicApproachData;
		languageProject = moraicApproach.getLanguageProject();
		setDataProcessing(ApplicationPreferences.LAST_MORAIC_FILTERS_VIEW_ITEM_USED);
	}

	@Override
	protected void showTypeWarning(TemplateFilter filter) {
		switch (filter.getType()) {
		case "Onset":
			typeWarningMessage.setVisible(false);
			break;
		default:
			typeWarningMessage.setText(bundle.getString("label.moraicfilterwarning"));
			typeWarningMessage.setVisible(true);
			break;
		}
	}

}
