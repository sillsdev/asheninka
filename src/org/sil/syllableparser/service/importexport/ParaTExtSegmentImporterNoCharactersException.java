// Copyright (c) 2016-2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 

package org.sil.syllableparser.service.importexport;

public class ParaTExtSegmentImporterNoCharactersException extends SegmentImporterException {

	/**
	 * Used for case where the ParaTExt lds file does not have any characters defined
	 */

	private static final long serialVersionUID = 1L;
	private String sFileName = "";
	
	public ParaTExtSegmentImporterNoCharactersException(String filename) {
		sFileName = filename;
	}

	public String getsFileName() {
		return sFileName;
	}

}
