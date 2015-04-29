/**
 * 
 */
package sil.org.syllableparser.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public class CVApproach {
	
	private ObservableList<CVSegment> cvSegmentInventory = FXCollections.observableArrayList();

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
	 * Clear out all data in this CV approach
	 */
	public void clear() {
		cvSegmentInventory.clear();
	}

	/**
	 * @param cvApproach
	 */
	public void load(CVApproach cvApproachLoaded) {
    	ObservableList<CVSegment> cvSegmentInventoryLoadedData = cvApproachLoaded.getCVSegmentInventory();
		for (CVSegment cvSegment : cvSegmentInventoryLoadedData) {
			cvSegmentInventory.add(cvSegment);
		}
	}
	

}
