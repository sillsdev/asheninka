/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.otapproach;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;

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
		createUUID();
	}

	public OTConstraintRanking(StringProperty name, StringProperty description,
			ObservableList<OTConstraint> ranking, StringProperty rankingRepresentation) {
		super();
		this.name = name;
		this.description = description;
		this.ranking = ranking;
		this.rankingRepresentation = rankingRepresentation;
		createUUID();
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

	@XmlAttribute(name="ranking")
	@XmlIDREF
	@XmlList
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

	@Override
	public int hashCode() {
		String sCombo = id + name.getValueSafe() + description.getValueSafe()
				+ rankingRepresentation.getValueSafe() + isActive();
		return sCombo.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		boolean result = true;
		OTConstraintRanking ranking = (OTConstraintRanking) obj;
		if (!id.equals(ranking.getID())) {
			result = false;
		} else if (!getName().equals(ranking.getName())) {
			result = false;
		} else if (!getDescription().equals(ranking.getDescription())) {
			result = false;
		} else if (!getRankingRepresentation().equals(ranking.getRankingRepresentation())) {
			result = false;
		} else if (isActive() != ranking.isActive()) {
			result = false;
		}
		return result;
	}
}
