// Copyright (c) 2016 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * 
 */
package sil.org.syllableparser.service;

import sil.org.syllableparser.model.SylParserObject;

/**
 * @author Andy Black
 *
 */
public abstract class DifferentSylParserObject {
	protected SylParserObject objectFrom1;
	protected SylParserObject objectFrom2;

	public DifferentSylParserObject(SylParserObject objectFrom1, SylParserObject objectFrom2) {
		super();
		this.objectFrom1 = objectFrom1;
		this.objectFrom2 = objectFrom2;
	}

	public SylParserObject getObjectFrom1() {
		return objectFrom1;
	}

	public void setObjectFrom1(SylParserObject objectFrom1) {
		this.objectFrom1 = objectFrom1;
	}

	public SylParserObject getObjectFrom2() {
		return objectFrom2;
	}

	public void setObjectFrom2(SylParserObject objectFrom2) {
		this.objectFrom2 = objectFrom2;
	}
	
	public abstract String getSortingValue();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((objectFrom1 == null) ? 0 : objectFrom1.hashCode());
		result = prime * result + ((objectFrom2 == null) ? 0 : objectFrom2.hashCode());
		return result;
	}

	@Override
	abstract public boolean equals(Object obj);
}
