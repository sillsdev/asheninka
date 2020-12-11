// Copyright (c) 2019 SIL International 
// This software is licensed under the LGPL, version 2.1 or later 
// (http://www.gnu.org/licenses/lgpl-2.1.html) 
/**
 * @author Andy Black
 * 
 */
package org.sil.antlr4.templatefilterparser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.*;

public class TemplateFilterErrorListener {
	
	public static class VerboseListener extends BaseErrorListener {
		TemplateFilterErrorListener listener = new TemplateFilterErrorListener();
	    LinkedList<TemplateFilterErrorInfo> errorMessages = new LinkedList<TemplateFilterErrorInfo>(
				Arrays.asList(new TemplateFilterErrorInfo()));
		
	    @Override
	    public void syntaxError(Recognizer<?, ?> recognizer,
	                            Object offendingSymbol,
	                            int line, int charPositionInLine,
	                            String msg,
	                            RecognitionException e)
	    {
	    	TemplateFilterErrorInfo info = new TemplateFilterErrorInfo(offendingSymbol, line, charPositionInLine, msg, e);
	        errorMessages.add(info);
	    }

		public List<TemplateFilterErrorInfo> getErrorMessages() {
			return errorMessages;
		}
		
		public void clearErrorMessageList() {
			errorMessages.clear();
		}

	}

}
