/**
 * Copyright (c) 2020-2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.moraicapproach;


/**
 * @author Andy Black
 *
 */
public class Mora extends MoraicConstituent {

	public void createLingTreeDescription(StringBuilder sb) {
		super.createLingTreeDescription(sb, "μ");
	}

}
