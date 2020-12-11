// Copyright (c) 2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package org.sil.antlr4.templatefilterparser;

import org.antlr.v4.runtime.RecognitionException;
import org.sil.antlr4.CommonErrorInfo;

public class TemplateFilterErrorInfo extends CommonErrorInfo {
		
        public TemplateFilterErrorInfo(Object offendingSymbol, int line, int charPositionInLine, String msg,
				RecognitionException e) {
			super(offendingSymbol, line, charPositionInLine, msg, e);
		}

		public TemplateFilterErrorInfo() {
			super();
		}
}
