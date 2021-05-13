/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.otapproach;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.sil.syllableparser.model.SylParserObject;

/**
 * @author Andy Black
 *
 */
public class OTConstraintRanking extends SylParserObject {
	
	private final StringProperty name;
	private final StringProperty description;
	private final StringProperty rankingRepresentation;
	ObservableList<OTConstraint> ranking = FXCollections.observableArrayList();

	public OTConstraintRanking() {
		super();
		this.name = new SimpleStringProperty("");
		this.description = new SimpleStringProperty("");
		this.rankingRepresentation = new SimpleStringProperty("");
	}

	public OTConstraintRanking(StringProperty name, StringProperty description,
			ObservableList<OTConstraint> ranking, StringProperty rankingRepresentation) {
		super();
		this.name = name;
		this.description = description;
		this.ranking = ranking;
		this.rankingRepresentation = rankingRepresentation;
	}

	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty() {
		return name;
	}

	public void setName(String name) {
		this.name.set(name);
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

	public ObservableList<OTConstraint> getRanking() {
		return ranking;
	}

	public void setRanking(ObservableList<OTConstraint> ranking) {
		this.ranking = ranking;
	}

	public String getRankingRepresentation() {
		return rankingRepresentation.get();
	}

	public StringProperty rankingRepresentationProperty() {
		return rankingRepresentation;
	}

	public void setRankingRepresentation(String rankingRepresentation) {
		this.rankingRepresentation.set(rankingRepresentation);
	}

}
