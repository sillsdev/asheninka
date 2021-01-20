/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.view;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.model.moraicapproach.MoraicApproach;

/**
 * @author Andy Black
 *
 */
public class MoraicTemplatesController extends TemplatesController {

	public void setData(MoraicApproach moraicApproachData) {
		moraicApproach = moraicApproachData;
		languageProject = moraicApproach.getLanguageProject();
		setDataProcessing(ApplicationPreferences.LAST_MORAIC_TEMPLATES_VIEW_ITEM_USED);
	}

	protected void rememberSelection(int iCurrentIndex) {
		mainApp.getApplicationPreferences().setLastMoraicTemplatesViewItemUsed(iCurrentIndex);
	}

}
