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
public class ONCTemplatesController extends TemplatesController {

	public void setData(ONCApproach oncApproachData) {
		oncApproach = oncApproachData;
		languageProject = oncApproach.getLanguageProject();
		setDataProcessing(ApplicationPreferences.LAST_ONC_TEMPLATES_VIEW_ITEM_USED);
	}

	protected void rememberSelection(int iCurrentIndex) {
		mainApp.getApplicationPreferences().setLastONCTemplatesViewItemUsed(iCurrentIndex);
	}

	@Override
	protected void showTypeWarning(TemplateFilter templateFilter) {
		switch (templateFilter.getType()) {
		case "Syllable":
			typeWarningMessage.setText(bundle.getString("label.onctemplatewarning"));
			typeWarningMessage.setVisible(true);
			break;
		default:
			typeWarningMessage.setVisible(false);
			break;
		}
	}
}
