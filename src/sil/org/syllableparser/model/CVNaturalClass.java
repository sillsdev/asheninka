/**
 * 
 */
package sil.org.syllableparser.model;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public class CVNaturalClass {
	private final StringProperty ncName;
	private final SimpleListProperty<Object> segmentsOrNaturalClasses;
	private final StringProperty description;
	private final StringProperty sncRepresentation;

	public CVNaturalClass() {
		super();
		this.ncName = new SimpleStringProperty("");
		this.segmentsOrNaturalClasses = new SimpleListProperty<Object>();
		this.description = new SimpleStringProperty("");
		this.sncRepresentation = new SimpleStringProperty("");
	}

	public CVNaturalClass(String segment, SimpleListProperty<Object> segmentsOrNaturalClasses, 
			String description, String sncRepresentation) {
		super();
		this.ncName = new SimpleStringProperty(segment);
		this.segmentsOrNaturalClasses = new SimpleListProperty<Object>(segmentsOrNaturalClasses);
		this.description = new SimpleStringProperty(description);
		this.sncRepresentation = new SimpleStringProperty(sncRepresentation);
	}

	public String getNCName() {
		return ncName.get();
	}

	public StringProperty ncNameProperty() {
		return ncName;
	}

	public void setNCName(String ncName) {
		this.ncName.set(ncName);
	}

	public ObservableList<Object> getSegmentsOrNaturalClasses() {
		return segmentsOrNaturalClasses.get();
	}

	public SimpleListProperty<Object> segmentsOrNaturalClassesProperty() {
		return segmentsOrNaturalClasses;
	}

	public void setSegmentsOrNaturalClasses(ObservableList<Object> segmentsOrNaturalClasses) {
		this.segmentsOrNaturalClasses.set(segmentsOrNaturalClasses);
	}

	public String getDescription() {
		return description.get();
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	public String getSNCRepresentation() {
		return sncRepresentation.get();
	}

	public StringProperty sncRepresentationProperty() {
		return sncRepresentation;
	}

	public void setSNCRepresentation(String sncRepresentation) {
		this.sncRepresentation.set(sncRepresentation);
	}

}
