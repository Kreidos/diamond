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

/**
 * Created on 05-Jan-2014
 *
 * Copyright 2003-09 by Primeleaf Consulting (P) Ltd.,
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

package com.primeleaf.krystal.web.view.console;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.primeleaf.krystal.constants.HTTPConstants;
import com.primeleaf.krystal.model.vo.Document;
import com.primeleaf.krystal.web.view.WebPageTemplate;
import com.primeleaf.krystal.web.view.WebView;

/**
 * @author Rahul Kubadia
 *
 */

public class CheckoutDocumentView extends WebView {

	public CheckoutDocumentView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}
	public void render() throws Exception{
		WebPageTemplate edmcTemplate = new WebPageTemplate(request, response);
		edmcTemplate.generateHeader();
		deleteDocumentResponse();
		edmcTemplate.generateFooter();
	}
	private void printBreadCrumbs() throws Exception{
		out.println("<ol class=\"breadcrumb\">");
		out.println("<li><a href=\"/console\">My Workspace</a></li>");
		out.println("<li class=\"active\">Checkout</li>");
		out.println("</ol>");
	}
	private void deleteDocumentResponse() throws Exception{
		try {
			Document document = (Document) request.getAttribute("DOCUMENT");
			printBreadCrumbs();

			if(request.getAttribute(HTTPConstants.REQUEST_ERROR) != null){
				printError((String)request.getAttribute(HTTPConstants.REQUEST_ERROR));
			}
			if(request.getAttribute(HTTPConstants.REQUEST_MESSAGE) != null){
				printSuccess((String)request.getAttribute(HTTPConstants.REQUEST_MESSAGE));
			}
			if(document != null){
				String fileName  = (String) request.getAttribute("FILENAME");
				out.println("<div class=\"panel panel-default\">");
				out.println("<div class=\"panel-heading\"><h4><i class=\"fa fa-lg fa-lock\"></i> Checkout</h4></div>");
				out.println("<div class=\"panel-body\">");
				String baseURL = request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+ request.getContextPath() ;
				out.println("<object type=\"application/x-java-applet\" width=\"100%\" height=\"60\">");
				out.println("<param name=\"code\" VALUE = \"com.primeleaf.edms.applet.CheckOutApplet.class\" />");
				out.println("<param name=\"codebase_lookup\" value=\"false\"/>");
				out.println("<param name=\"archive\" VALUE = \"/applet/co.jar\" />");
				out.println("<param name=\"scriptable\" VALUE = \"false\" />");
				out.println("<PARAM NAME = \"BASEURL\" VALUE=\"" + response.encodeRedirectURL(baseURL) + "\">");
				out.println("<PARAM NAME = \"FILENAME\" VALUE=\"" + response.encodeRedirectURL(fileName) + "\">");
				out.println("<PARAM NAME = \"DOWNLOADURL\" VALUE=\""+ response.encodeRedirectURL(baseURL + "/console/downloaddocument?documentid="+document.getDocumentId()) + "\">");
				out.println("<PARAM NAME = \"CHECKOUTDIR\" VALUE=\""+ loggedInUser.getCheckOutPath() + "\">");
				out.println("</object>");
				out.println("</div>");//panel-body
				out.println("</div>");//panel
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}