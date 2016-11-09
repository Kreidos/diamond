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
import com.primeleaf.krystal.web.view.WebPageTemplate;
import com.primeleaf.krystal.web.view.WebView;

/**
 * @author Rahul Kubadia
 *
 */

public class CancelDocumentCheckoutView extends WebView {

	public CancelDocumentCheckoutView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}
	public void render() throws Exception{
		WebPageTemplate edmcTemplate = new WebPageTemplate(request, response);
		edmcTemplate.generateHeader();
		cancelDocumentCheckoutResponse();
		edmcTemplate.generateFooter();
	}
	private void printBreadCrumbs() throws Exception{
		out.println("<ol class=\"breadcrumb\">");
		out.println("<li><a href=\"/console\">My Workspace</a></li>");
		out.println("<li class=\"active\">Cancel Checkout</li>");
		out.println("</ol>");
	}
	private void cancelDocumentCheckoutResponse() throws Exception{
		try {
			printBreadCrumbs();
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\"><h4><i class=\"fa fa-lg fa-unlock\"></i> Cancel Checkout</h4></div>");
			out.println("<div class=\"panel-body\">");
			if(request.getAttribute(HTTPConstants.REQUEST_ERROR) != null){
				printError((String)request.getAttribute(HTTPConstants.REQUEST_ERROR));
			}
			if(request.getAttribute(HTTPConstants.REQUEST_MESSAGE) != null){
				out.println((String)request.getAttribute(HTTPConstants.REQUEST_MESSAGE));
			}
			out.println("</div>"); //panel-body
			out.println("</div>"); //panel
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}