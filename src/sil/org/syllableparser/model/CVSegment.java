/**
 * 
 */
package sil.org.syllableparser.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Andy Black
 *
 */
public class CVSegment {
	private final StringProperty segment;
	private final StringProperty graphemes;
	private final StringProperty description;

	public CVSegment() {
		super();
		this.segment = new SimpleStringProperty("");
		this.graphemes = new SimpleStringProperty("");
		this.description = new SimpleStringProperty("");
	}

	public CVSegment(String segment, String graphemes, String comment) {
		super();
		this.segment = new SimpleStringProperty(segment);
		this.graphemes = new SimpleStringProperty(graphemes);
		this.description = new SimpleStringProperty(comment);
	}

	public String getSegment() {
		return segment.get();
	}

	public StringProperty segmentProperty() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment.set(segment);
	}

	public String getGraphemes() {
		return graphemes.get();
	}

	public StringProperty graphemesProperty() {
		return graphemes;
	}

	public void setGraphemes(String graphemes) {
		this.graphemes.set(graphemes);
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

}
