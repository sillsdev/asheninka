// Copyright (c) 2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model;

import javax.xml.bind.annotation.XmlElement;
import org.sil.syllableparser.model.cvapproach.CVNaturalClass;

/**
 * @author Andy Black
 *
 * an Object Value used in templates and filters
 */
public class TemplateFilterSlotSegmentOrNaturalClass {
	private String segmentString;
	private CVNaturalClass cvNaturalClass;
	private boolean isSegment = true;
	private boolean isOptional = false;
	private boolean obeysSSP = false;

	public TemplateFilterSlotSegmentOrNaturalClass() {
		this(null, true);
	}

	public TemplateFilterSlotSegmentOrNaturalClass(String segmentString, boolean isSegment) {
		this.segmentString = segmentString;
		this.isSegment = isSegment;
	}
	
	/**
	 * Properties
	 */
	@XmlElement(name="segmentString")
	public String getSegmentString() {
		return segmentString;
	}

	public void setSegmentString(String segmentString) {
		this.segmentString = segmentString;
	}

	/**
	 * @return the cvNaturalClass
	 */
	@XmlElement(name = "cvNaturalClass")
	public CVNaturalClass getCVNaturalClass() {
		return cvNaturalClass;
	}

	/**
	 * @param cvNaturalClass the cvNaturalClass to set
	 */
	public void setCVNaturalClass(CVNaturalClass cvNaturalClass) {
		this.cvNaturalClass = cvNaturalClass;
	}

	@XmlElement(name="isSegment")
	public boolean isSegment() {
		return isSegment;
	}

	public void setSegment(boolean isGrapheme) {
		this.isSegment = isGrapheme;
	}

	@XmlElement(name="isOptional")
	public boolean isOptional() {
		return isOptional;
	}

	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

	public boolean isObeysSSP() {
		return obeysSSP;
	}

	public void setObeysSSP(boolean obeysSSP) {
		this.obeysSSP = obeysSSP;
	}

}
