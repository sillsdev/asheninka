// Copyright (c) 2016-2017 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package org.sil.antlr4.templatefilterparser;

import org.antlr.v4.runtime.RecognitionException;

public class TemplateFilterErrorInfo {
		Object offendingSymbol;
        int line;
        int charPositionInLine;
        String msg;
        RecognitionException e;
		
        public TemplateFilterErrorInfo(Object offendingSymbol, int line, int charPositionInLine, String msg,
				RecognitionException e) {
			super();
			this.offendingSymbol = offendingSymbol;
			this.line = line;
			this.charPositionInLine = charPositionInLine;
			this.msg = msg;
			this.e = e;
		}

		public TemplateFilterErrorInfo() {
			this.offendingSymbol = null;
			this.line = -1;
			this.charPositionInLine = -1;
			this.msg = "";
			this.e = null;
		}

		public Object getOffendingSymbol() {
			return offendingSymbol;
		}

		public int getLine() {
			return line;
		}

		public int getCharPositionInLine() {
			return charPositionInLine;
		}

		public String getMsg() {
			return msg;
		}

		public RecognitionException getException() {
			return e;
		}
		

}
