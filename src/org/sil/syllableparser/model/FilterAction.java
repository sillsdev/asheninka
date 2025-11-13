/**
 * Copyright (c) 2019-2025 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model;

import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * @author Andy Black
 *
 */
public class FilterAction {

	private int repairStartingSlotPosition;
	private boolean doRepair;
	
	public FilterAction() {	
	}

	public FilterAction(int repairStartingSlotPosition, boolean doRepair) {
		super();
		this.repairStartingSlotPosition = repairStartingSlotPosition;
		this.doRepair = doRepair;
	}

	@XmlAttribute(name="repairStartPos")
	public int getRepairStartingSlotPosition() {
		return repairStartingSlotPosition;
	}

	public void setRepairStartingSlotPosition(int repairStartingSlotPosition) {
		this.repairStartingSlotPosition = repairStartingSlotPosition;
	}

	@XmlAttribute(name="doRepair")
	public boolean isDoRepair() {
		return doRepair;
	}

	public void setDoRepair(boolean doRepair) {
		this.doRepair = doRepair;
	}	
}
