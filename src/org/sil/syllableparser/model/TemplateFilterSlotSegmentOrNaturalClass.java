// Copyright (c) 2019-2020 SIL International
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package org.sil.syllableparser.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import org.sil.syllableparser.model.cvapproach.CVNaturalClass;
import org.sil.syllableparser.model.Segment;

/**
 * @author Andy Black
 *
 * an Object Value used in templates and filters
 */
public class TemplateFilterSlotSegmentOrNaturalClass {
	private String segmentString;
	private Segment referringSegment;
	private CVNaturalClass cvNaturalClass;
	private boolean isSegment = true;
	private boolean isOptional = false;
	private boolean obeysSSP = true;
	private boolean repairLeftwardFromHere = false;
	private boolean constituentBeginsHere = false;

	public TemplateFilterSlotSegmentOrNaturalClass() {
	}

	public TemplateFilterSlotSegmentOrNaturalClass(String segmentString) {
		this.segmentString = segmentString;
		this.isSegment = true;
	}

	public TemplateFilterSlotSegmentOrNaturalClass(Segment segment) {
		this.referringSegment = segment;
		this.isSegment = true;
		this.segmentString = segment.getSegment();
	}

	public TemplateFilterSlotSegmentOrNaturalClass(CVNaturalClass natClass) {
		this.cvNaturalClass = natClass;
		this.isSegment = false;
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
	public CVNaturalClass getCVNaturalClass() {
		return cvNaturalClass;
	}

	/**
	 * @param cvNaturalClass the cvNaturalClass to set
	 */
	@XmlAttribute(name="natclass")
	@XmlIDREF
	public void setCVNaturalClass(CVNaturalClass cvNaturalClass) {
		this.cvNaturalClass = cvNaturalClass;
		this.isSegment = false;
	}

	/**
	 * @return the segment
	 */
	public Segment getReferringSegment() {
		return referringSegment;
	}

	/**
	 * @param referringSegment the segment to set
	 */
	@XmlAttribute(name="segment")
	@XmlIDREF
	public void setReferringSegment(Segment referringSegment) {
		this.referringSegment = referringSegment;
		this.isSegment = true;
	}

	@XmlElement(name="isSegment")
	public boolean isSegment() {
		return isSegment;
	}

	public void setIsSegment(boolean isSegment) {
		this.isSegment = isSegment;
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

	public boolean isRepairLeftwardFromHere() {
		return repairLeftwardFromHere;
	}

	public void setRepairLeftwardFromHere(boolean repairLeftwardFromHere) {
		this.repairLeftwardFromHere = repairLeftwardFromHere;
	}

	public boolean isConstituentBeginsHere() {
		return constituentBeginsHere;
	}

	public void setConstituentBeginsHere(boolean constituentBeginsHere) {
		this.constituentBeginsHere = constituentBeginsHere;
	}
}
