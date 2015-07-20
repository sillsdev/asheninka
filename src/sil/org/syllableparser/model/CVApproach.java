/**
 * 
 */
package sil.org.syllableparser.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public class CVApproach {
	
	private ObservableList<CVSegment> cvSegmentInventory = FXCollections.observableArrayList();
	private ObservableList<CVNaturalClass> cvNaturalClasses = FXCollections.observableArrayList();

	/**
	 * @return the cvSegmentInventoryData
	 */
	@XmlElementWrapper(name = "cvSegments")
	@XmlElement(name = "cvSegment")
	public ObservableList<CVSegment> getCVSegmentInventory() {
		return cvSegmentInventory;
	}

	/**
	 * @param cvSegmentInventoryData the cvSegmentInventoryData to set
	 */
	public void setCVSegmentInventory(ObservableList<CVSegment> cvSegmentInventoryData) {
		this.cvSegmentInventory = cvSegmentInventoryData;
	}

	/**
	 * @return the cvNaturalClassesData
	 */
	@XmlElementWrapper(name = "cvNaturalClasses")
	@XmlElement(name = "cvNaturalClass")
	public ObservableList<CVNaturalClass> getCVNaturalClasses() {
		return cvNaturalClasses;
	}

	/**
	 * @param cvSegmentInventoryData the cvSegmentInventoryData to set
	 */
	public void setCVNaturalClasses(ObservableList<CVNaturalClass> cvNaturalClassesData) {
		this.cvNaturalClasses = cvNaturalClassesData;
	}

	/**
	 * Clear out all data in this CV approach
	 */
	public void clear() {
		cvSegmentInventory.clear();
		cvNaturalClasses.clear();
	}

	/**
	 * @param cvApproach
	 */
	public void load(CVApproach cvApproachLoaded) {
    	ObservableList<CVSegment> cvSegmentInventoryLoadedData = cvApproachLoaded.getCVSegmentInventory();
		for (CVSegment cvSegment : cvSegmentInventoryLoadedData) {
			cvSegmentInventory.add(cvSegment);
		}
		ObservableList<CVNaturalClass> cvNaturalClassesLoadedData = cvApproachLoaded.getCVNaturalClasses();
		for (CVNaturalClass cvNaturalClass : cvNaturalClassesLoadedData) {
			cvNaturalClasses.add(cvNaturalClass);
		}
	}
	

}
