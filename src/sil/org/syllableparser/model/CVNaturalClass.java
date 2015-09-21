/**
 * 
 */
package sil.org.syllableparser.model;

import java.util.UUID;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Andy Black
 *
 */
public class CVNaturalClass extends SylParserObject {
	private final StringProperty ncName;
	private final SimpleListProperty<Object> segmentsOrNaturalClasses;
	private final StringProperty description;
	private final StringProperty sncRepresentation;
	ObservableList<SylParserObject> snc = FXCollections.observableArrayList();

	public CVNaturalClass() {
		super();
		this.ncName = new SimpleStringProperty("");
		this.segmentsOrNaturalClasses = new SimpleListProperty<Object>();
		this.description = new SimpleStringProperty("");
		this.sncRepresentation = new SimpleStringProperty("");
		this.uuid = UUID.randomUUID();
	}

	public CVNaturalClass(String className, SimpleListProperty<Object> segmentsOrNaturalClasses, 
			String description, String sncRepresentation) {
		super();
		this.ncName = new SimpleStringProperty(className);
		this.segmentsOrNaturalClasses = new SimpleListProperty<Object>(segmentsOrNaturalClasses);
		this.description = new SimpleStringProperty(description);
		this.sncRepresentation = new SimpleStringProperty(sncRepresentation);
		uuid = UUID.randomUUID();
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
		return segmentsOrNaturalClasses;
	}

	public ObservableList<SylParserObject> getSnc() {
		return snc;
	}

	public void setSnc(ObservableList<SylParserObject> snc) {
		this.snc = snc;
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

	public static int findIndexInNaturaClassListByUuid(ObservableList<CVNaturalClass> list, UUID uuid) {
		// TODO: is there a way to do this with lambda expressions?
		// Is there a way to use SylParserObject somehow?
				int index = -1;
				for (SylParserObject sylParserObject : list) {
					index++;
					if (sylParserObject.getUuid() == uuid) {
						return index;
					}
				}		
				return -1;
			}

	/**
	 * @return
	 */
	public StringProperty naturalClassProperty() {
		return this.ncName;
	}

}
