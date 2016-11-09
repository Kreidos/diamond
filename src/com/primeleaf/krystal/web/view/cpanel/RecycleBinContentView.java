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

package com.primeleaf.krystal.web.view.cpanel;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import com.primeleaf.krystal.constants.HTTPConstants;
import com.primeleaf.krystal.constants.ServerConstants;
import com.primeleaf.krystal.model.dao.UserDAO;
import com.primeleaf.krystal.model.vo.Document;
import com.primeleaf.krystal.model.vo.DocumentClass;
import com.primeleaf.krystal.model.vo.User;
import com.primeleaf.krystal.util.StringHelper;
import com.primeleaf.krystal.web.view.WebPageTemplate;
import com.primeleaf.krystal.web.view.WebView;

/**
 * @author Rahul Kubadia
 *
 */

public class RecycleBinContentView extends WebView {
	public RecycleBinContentView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}
	public void render() throws Exception{
		WebPageTemplate template = new WebPageTemplate(request,response);
		template.generateHeader();
		printRecycleBinContent();
		template.generateFooter();
	}
	private void printBreadCrumbs() throws Exception{
		out.println("<ol class=\"breadcrumb\">");
		out.println("<li><a href=\"/cpanel\">Control Panel</a></li>");
		out.println("<li><a href=\"/cpanel/recyclebin\">Recycle Bin</a></li>");
		out.println("<li class=\"active\">View Documents</li>");
		out.println("</ol>");
	}
	@SuppressWarnings("unchecked")
	private void printRecycleBinContent() throws Exception{
		printBreadCrumbs();
		if(request.getAttribute(HTTPConstants.REQUEST_ERROR) != null){
			printErrorDismissable((String)request.getAttribute(HTTPConstants.REQUEST_ERROR));
		}
		if(request.getAttribute(HTTPConstants.REQUEST_MESSAGE) != null){
			printSuccessDismissable((String)request.getAttribute(HTTPConstants.REQUEST_MESSAGE));
		}
		try {
			ArrayList<Document> documentList = (ArrayList<Document>) request.getAttribute("DOCUMENTLIST");
			DocumentClass documentClass = (DocumentClass) request.getAttribute("DOCUMENTCLASS");
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\">");
			
			out.println("<div class=\"row\">");
			out.println("<div class=\"col-sm-6\">");
			out.println("<h4><i class=\"fa fa-lg fa-trash-o\"></i> ");
			out.println(StringEscapeUtils.escapeHtml4(documentClass.getClassName())+" - ");
			out.println("<small>"+StringEscapeUtils.escapeHtml4(documentClass.getClassDescription())+"</small>");
			out.println("</div>");//col-sm-6
			
			out.println("<div class=\"col-sm-6 text-right\">");
			out.println("<h4><i class=\"fa fa-file \"></i>  View Documents</h4>");
			out.println("</div>");//col-sm-6
			out.println("</div>");//row
			out.println("</div>");//panel-heading
			
			if(documentList.size() > 0 ){
				out.println("<div class=\"table-responsive\">");
				out.println("<table class=\"table table-condensed table-stripped\">");
				out.println("<thead>");
				out.println("<tr>");
				out.println("<th>&nbsp;</td>");
				out.println("<th class=\"text-center\">Document ID</th>");
				out.println("<th>Last Modified On</th>");
				out.println("<th>Last Modified By</th>");
				out.println("<th>Expiry On</th>");
				out.println("<th class=\"text-center\">Action</th>");
				out.println("</tr>");
				out.println("</thead>");
				out.println("<tbody>");
				for (Document document : documentList) {
					User user = UserDAO.getInstance().readUserByName(document.getCreatedBy());
					out.println("<tr>");
					out.println("<td><a href=\"/console/viewdocument?documentid="+document.getDocumentId()+"&revisionid="+document.getRevisionId()+"\" title=\""+"View Document"+"\" target=\"_new\"><img src=\""+HTTPConstants.BASEURL+"/images/"+StringHelper.getIconFileNameForExtension(document.getExtension().toUpperCase())+ ".gif\"\" class=\"img-icon\" title=\""+document.getExtension().toUpperCase()+"\"></td>");
					out.println("<td class=\"text-center\">"+document.getDocumentId()+"</td>");
					out.println("<td>"+StringHelper.formatDate(document.getModified())+"</td>");
					out.println("<td>"+user.getUserName()+"</td>");
					if(document.getExpiry() != null){
						out.println("<td>"+StringHelper.formatDate(document.getExpiry(),ServerConstants.FORMAT_SHORT_DATE)+"</td>");
					}else{
						out.println("<td>&nbsp;</td>");
					}
					out.println("<td class=\"text-center\">");
					out.println("<a href=\""+HTTPConstants.BASEURL+"/cpanel/restoredocument?documentid="+document.getDocumentId()+"&revisionid="+document.getRevisionId()+"&classid="+documentClass.getClassId()+"&mode=R\" title=\"Restore Document\">Restore Document</a>");
					out.println(" | <a href=\""+HTTPConstants.BASEURL+"/cpanel/purgedocument?documentid="+document.getDocumentId()+"\" class=\"confirm\" title=\"Are you sure, you want to permanently delete this document?\">Delete Document</a>");
					out.println("</td>");
					out.println("</tr>");
				}// for
				out.println("</tbody>");
				out.println("</table>");
				out.println("</div>");
				
			}else{
				out.println("<div class=\"panel-body\">");
				out.println("There are no documents currrently available.");
				out.println("</div>");//panel-body
			}
			out.println("</div>");//panel
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}