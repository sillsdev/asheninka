// Copyright (c) 2016-2017 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.service;

import sil.org.syllableparser.Constants;
import sil.org.syllableparser.model.Environment;
import sil.org.syllableparser.model.Segment;
import sil.org.syllableparser.model.SylParserObject;

/**
 * @author Andy Black
 *
 */
public class DifferentEnvironment extends DifferentSylParserObject {

	public DifferentEnvironment(Environment environmentFrom1, Environment environmentFrom2) {
		super((SylParserObject) environmentFrom1, (SylParserObject) environmentFrom2);
	}

	public String getSortingValue() {
		if (objectFrom1 != null) {
			return ((Environment) objectFrom1).getEnvironmentRepresentation();
		} else if (objectFrom2 != null) {
			return ((Environment) objectFrom2).getEnvironmentRepresentation();
		}
		return Constants.NULL_AS_STRING;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DifferentEnvironment other = (DifferentEnvironment) obj;
		if (objectFrom1 == null) {
			if (other.objectFrom1 != null)
				return false;
		} else if (!((Environment) objectFrom1).getEnvironmentRepresentation().equals(
				((Environment) other.objectFrom1).getEnvironmentRepresentation()))
			return false;
		if (objectFrom2 == null) {
			if (other.objectFrom2 != null)
				return false;
		} else if (!((Environment) objectFrom2).getEnvironmentRepresentation().equals(
				((Environment) other.objectFrom2).getEnvironmentRepresentation()))
			return false;
		return true;
	}
}
