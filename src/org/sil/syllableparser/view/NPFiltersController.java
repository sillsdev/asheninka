/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.view;

import org.sil.syllableparser.ApplicationPreferences;
import org.sil.syllableparser.model.Filter;
import org.sil.syllableparser.model.FilterAction;
import org.sil.syllableparser.model.TemplateFilter;
import org.sil.syllableparser.model.npapproach.NPApproach;
import org.sil.syllableparser.model.npapproach.NPFilter;

/**
 * @author Andy Black
 *
 */
public class NPFiltersController extends FiltersController {

	protected void rememberSelection(int iCurrentIndex) {
		mainApp.getApplicationPreferences().setLastNPFiltersViewItemUsed(iCurrentIndex);
	}

	public void setData(NPApproach npApproachData) {
		npApproach = npApproachData;
		languageProject = npApproach.getLanguageProject();
		setDataProcessing(ApplicationPreferences.LAST_NP_FILTERS_VIEW_ITEM_USED, npApproach.getNPFilters());
	}

	@Override
	protected void handleInsertNewItem() {
		Filter newFilter = new NPFilter();
		newFilter.setAction(new FilterAction(0, false));
		filterList.add(newFilter);
		newFilter.setTemplateFilterRepresentation("");
		handleInsertNewItem(filterList, filterTable);
	}

	@Override
	protected void showTypeWarning(TemplateFilter templateFilter) {
		Filter filter = (Filter) templateFilter;
		if (filter != null && filter.getAction().isDoRepair()) {
			typeWarningMessage.setText(bundle.getString("label.npfilterwarning"));
			typeWarningMessage.setVisible(true);
		} else {
			typeWarningMessage.setVisible(false);
		}
	}
}
