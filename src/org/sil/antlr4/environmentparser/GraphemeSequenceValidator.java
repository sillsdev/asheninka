// Copyright (c) 2016-2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package org.sil.antlr4.environmentparser;

import java.util.List;

import org.sil.antlr4.StringSequenceValidator;

public class GraphemeSequenceValidator extends StringSequenceValidator {
	List<String> graphemesMasterList;

	public GraphemeSequenceValidator(List<String> graphemesMasterList) {
		super(graphemesMasterList);
	}

	public List<String> getGraphemesMasterList() {
		return super.getStringsMasterList();
	}

	public void setGraphemesMasterList(List<String> graphemesMasterList) {
		super.setStringsMasterList(graphemesMasterList);
	}

	public boolean findSequenceOfGraphemes(String sInput) {
		return super.findSequenceOfStrings(sInput);
	}
}
