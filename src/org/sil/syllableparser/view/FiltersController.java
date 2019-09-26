/**
 * Copyright (c) 2019 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.view;

import org.sil.syllableparser.model.oncapproach.ONCApproach;

/**
 * @author Andy Black
 *
 */
public class FiltersController extends TemplatesFiltersController {

	public void setData(ONCApproach oncApproachData) {
		oncApproach = oncApproachData;
		languageProject = oncApproach.getLanguageProject();
		contentList = languageProject.getFilters();
		super.setData(oncApproachData);
	}
}
