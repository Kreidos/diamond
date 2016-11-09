/**
 * Created On 05-Jan-2014
 * Copyright 2010 by Primeleaf Consulting (P) Ltd.,
 * #29,784/785 Hendre Castle,
 * D.S.Babrekar Marg,
 * Gokhale Road(North),
 * Dadar,Mumbai 400 028
 * India
 * 
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Primeleaf Consulting (P) Ltd. ("Confidential Information").  
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Primeleaf Consulting (P) Ltd.
 */

package com.primeleaf.krystal.web.action.console;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.primeleaf.krystal.util.FileUploadProgressListener;
import com.primeleaf.krystal.web.action.Action;
import com.primeleaf.krystal.web.view.WebView;

/**
 * Author Rahul Kubadia
 */

public class UploadProgressAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession(true);
		FileUploadProgressListener listener = null; 
		StringBuffer buffy = new StringBuffer();
		long bytesRead = 0,  contentLength = 0; 
		listener = (FileUploadProgressListener)session.getAttribute("LISTENER");
		if (listener == null){
			return null;
		} else {
			bytesRead = listener.getBytesRead();
			contentLength = listener.getContentLength();
		}
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache"); 
		buffy.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		buffy.append("<response>");
		buffy.append("<bytesRead>" + bytesRead + "</bytesRead>\n");
		buffy.append("<contentLength>" + contentLength + "</contentLength>\n");

		Long capturePercent = (Long)session.getAttribute("UPLOAD_PERCENT_COMPLETE");
		String uploadError=(String)session.getAttribute("UPLOAD_ERROR")!=null?(String)session.getAttribute("UPLOAD_ERROR"):"";

		if(uploadError.trim().length() > 0 ){
			buffy.append("<uploadError>" + uploadError + "</uploadError>");
		}else{
			if (capturePercent >= 100) {
				buffy.append("<finished/>");
			} else {
				if(bytesRead < contentLength){
					capturePercent = ((25 * bytesRead) / contentLength);  
					buffy.append("<percentComplete>" + capturePercent + "</percentComplete>");
				}else{
					buffy.append("<percentComplete>" + capturePercent + "</percentComplete>");
				}
			}
		}
		if(uploadError.trim().length() > 0 || capturePercent >= 100){
			session.removeAttribute("LISTENER");
			session.removeAttribute("UPLOAD_PERCENT_COMPLETE");
			session.removeAttribute("UPLOAD_COMPLETE");
		}
		buffy.append("</response>\n");
		response.getWriter().write(buffy.toString());
		response.getWriter().flush();
		response.getWriter().close();
		return null;
	}
}

