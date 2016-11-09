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


package com.primeleaf.krystal.web.view.cpanel;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import com.primeleaf.krystal.constants.HTTPConstants;
import com.primeleaf.krystal.model.vo.DocumentClass;
import com.primeleaf.krystal.model.vo.Hit;
import com.primeleaf.krystal.web.view.WebPageTemplate;
import com.primeleaf.krystal.web.view.WebView;

/**
 * @author Rahul Kubadia
 *
 */

public class RecycleBinView extends WebView {
	public RecycleBinView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}
	public void render() throws Exception{
		WebPageTemplate template = new WebPageTemplate(request,response);
		template.generateHeader();
		printDocumentClasses();
		template.generateFooter();
	}
	private void printBreadCrumbs() throws Exception{
		out.println("<ol class=\"breadcrumb\">");
		out.println("<li><a href=\"/cpanel\">Control Panel</a></li>");
		out.println("<li class=\"active\">Recycle Bin</li>");
		out.println("</ol>");
	}
	@SuppressWarnings("unchecked")
	private void printDocumentClasses() throws Exception{
		printBreadCrumbs();
		
		if(request.getAttribute(HTTPConstants.REQUEST_ERROR) != null){
			printErrorDismissable((String)request.getAttribute(HTTPConstants.REQUEST_ERROR));
		}
		if(request.getAttribute(HTTPConstants.REQUEST_MESSAGE) != null){
			printSuccessDismissable((String)request.getAttribute(HTTPConstants.REQUEST_MESSAGE));
		}
		
		out.println("<div class=\"panel panel-default\">");
		out.println("<div class=\"panel-heading\">");
		out.println("<h4><i class=\"fa fa-trash-o fa-lg\"></i> Recyle Bin</h4>");
		out.println("</div>");
		
		try {
			ArrayList<DocumentClass> documentClassList = (ArrayList<DocumentClass>) request.getAttribute("CLASSLIST");
			if(documentClassList.size() > 0 ){
				
				
				out.println("<ul class=\"list-group\">");
				for (DocumentClass documentClass : documentClassList) {
					int deletedDocuments = (Integer) request.getAttribute(documentClass.getClassName() +"_DELETED");
					int expiredDocuments = (Integer) request.getAttribute(documentClass.getClassName() +"_EXPIRED");
					out.println("<li class=\"list-group-item\">");
					out.println("<div class=\"row\">");
					out.println("<div class=\"col-xs-8\">");
					out.println("<h4 class=\"text-danger\">" + StringEscapeUtils.escapeHtml4(documentClass.getClassName()) + "</h4>");
					out.println("<h5>" + StringEscapeUtils.escapeHtml4(documentClass.getClassDescription()) + "</h5>");
					out.println("</div>");
					
					out.println("<a href=\""+HTTPConstants.BASEURL+"/cpanel/recyclebincontent?type="+Hit.STATUS_DELETED+"&classid="	+ documentClass.getClassId()+ "\" class=\"text-success\">"); 
					out.println("<div class=\"col-xs-2 text-right\">");
					out.println("<p>Deleted</p>");
					out.println("<h4>" + deletedDocuments + "</h4>");
					out.println("<p>Documents</p>");
					out.println("</div>");
					out.println("</a>");

					out.println("<a href=\""+HTTPConstants.BASEURL+"/cpanel/recyclebincontent?type="+Hit.STATUS_EXPIRED+"&classid="	+ documentClass.getClassId()+ "\" class=\"text-success\">"); 
					out.println("<div class=\"col-xs-2 text-right\">");
					out.println("<p>Expired</p>");
					out.println("<h4>" + expiredDocuments + "</h4>");
					out.println("<p>Documents</p>");
					out.println("</div>");
					out.println("</a>");
					
					out.println("</div>");
					out.println("</li>");
					
				}// for
				out.println("</ul>");
			}else{
				out.println("<div class=\"panel-body\">");
				out.println("There are no Deleted or Expired documents found in any of the Document Classes");
				out.println("</div>");
			}
			
			out.println("</div>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}