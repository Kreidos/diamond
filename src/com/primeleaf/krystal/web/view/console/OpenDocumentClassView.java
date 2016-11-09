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

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import com.primeleaf.krystal.constants.HTTPConstants;
import com.primeleaf.krystal.constants.ServerConstants;
import com.primeleaf.krystal.model.vo.DocumentClass;
import com.primeleaf.krystal.model.vo.Hit;
import com.primeleaf.krystal.model.vo.IndexDefinition;
import com.primeleaf.krystal.security.ACL;
import com.primeleaf.krystal.util.StringHelper;
import com.primeleaf.krystal.web.view.WebPageTemplate;
import com.primeleaf.krystal.web.view.WebView;

/**
 * @author Rahul Kubadia
 *
 */

public class OpenDocumentClassView extends WebView {

	public OpenDocumentClassView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}

	public void render() throws Exception{
		WebPageTemplate template = new WebPageTemplate(request,response);
		template.generateHeader();
		printHitlist();
		template.generateFooter();
	}
	private void printBreadCrumbs() throws Exception{
		DocumentClass documentClass = (DocumentClass) request.getAttribute("DOCUMENTCLASS");
		out.println("<ol class=\"breadcrumb\">");
		out.println("<li><a href=\"/console\">My Workspace</a></li>");
		out.println("<li><a href=\"/console/searchdocumentclass?classid="+documentClass.getClassId()+"\">Search Document Class</a></li>");
		out.println("<li class=\"active\">Document Hitlist</li>");
		out.println("</ol>");
	}

	@SuppressWarnings("unchecked")
	private void printHitlist() throws Exception{
		printBreadCrumbs();
		
		DocumentClass documentClass = (DocumentClass) request.getAttribute("DOCUMENTCLASS");
		ArrayList<Hit> documentHitlist =  (ArrayList<Hit>)request.getAttribute("HITLIST");
		ACL acl = (ACL) request.getAttribute("ACL");
		int totalHits = (Integer)request.getAttribute("TOTALHITS");
		int currentPage = (Integer)request.getAttribute("PAGE");
		int pageSize = loggedInUser.getHitlistSize();

		if(request.getAttribute(HTTPConstants.REQUEST_ERROR) != null){
			printError((String)request.getAttribute(HTTPConstants.REQUEST_ERROR));
		}
		if(request.getAttribute(HTTPConstants.REQUEST_MESSAGE) != null){
			printSuccess((String)request.getAttribute(HTTPConstants.REQUEST_MESSAGE));
		}
		int startRecord = (((currentPage-1) * pageSize) +1);
		int endRecord  = (pageSize * currentPage);
		if(endRecord > totalHits){
			endRecord = totalHits;
		}
		if(documentHitlist.size() <= 0 ){
			startRecord = 0;
		}
		
		out.println("<div class=\"panel panel-default\">");
		out.println("<div class=\"panel-heading\">");
		out.println("<div class=\"row\">");
		out.println("<div class=\"col-sm-6\">");
		out.println("<h4><i class=\"fa fa-lg fa-folder-open\"></i> ");
		out.println(StringEscapeUtils.escapeHtml4(documentClass.getClassName())+" - ");
		out.println("<small>"+StringEscapeUtils.escapeHtml4(documentClass.getClassDescription()) + "</small>");
		out.println("</h4>");
		out.println("</div>");
		out.println("<div class=\"col-sm-6 text-right\">");
		out.println("<h4><i class=\"fa fa-lg fa-list\"></i> Document Hitlist - ");
		out.println("<small>Showing " +  startRecord  + " to "  + endRecord  + " of " + totalHits+"</small>");
		out.println("</div>");
		out.println("</div>");//row
		out.println("</div>");//panel-heading
		
		if(documentHitlist.size() > 0 ){
			out.println("<form action=\"/console/bulkdelete\" method=\"post\" name=\"frmBulkAction\" id=\"frmBulkAction\" class=\"form-horizontal\">");
			out.println("<div class=\"table-responsive\">");
			out.println("<table class=\"table table-hover table-striped table-condensed\" style=\"font-size:12px;\">");
			out.println("<thead>");
			out.println("<tr>");
			if(acl.canDelete() || acl.canDownload()){
				out.println("<th class=\"text-center\"><input type=\"checkbox\" id=\"checkAll\"/></th>");
			}
			out.println("<th></th>");
			out.println("<th class=\"text-center\"><i class=\"fa fa-file text-primary\"></i></th>");
			if(loggedInUser.getMetaPreferences().isDocumentIdVisible()){
				printHitListColumnHeader("DOCUMENTID", "Document ID");
			}
			if(loggedInUser.getMetaPreferences().isRevisionIdVisible()){
				printHitListColumnHeader("REVISIONID", "Revision ID");
			}
			if(loggedInUser.getMetaPreferences().isCreatedByVisible()){
				printHitListColumnHeader("CREATEDBY", "Created By");
			}
			if(loggedInUser.getMetaPreferences().isModifiedByVisible()){
				printHitListColumnHeader("MODIFIEDBY", "Last Modified By");
			}
			for (IndexDefinition indexDefinition : documentClass.getIndexDefinitions()){
				printHitListColumnHeader(indexDefinition.getIndexColumnName(), StringEscapeUtils.escapeHtml4(indexDefinition.getIndexDisplayName()));
			}
			if(loggedInUser.getMetaPreferences().isFileSizeVisible()){
				printHitListColumnHeader("LENGTH", "File Size");
			}
			if(loggedInUser.getMetaPreferences().isCreatedVisible()){
				printHitListColumnHeader("CREATED", "Created On");
			}
			if(loggedInUser.getMetaPreferences().isModifiedVisible()){
				printHitListColumnHeader("MODIFIED", "Last Modified On");
			}
			if(loggedInUser.getMetaPreferences().isExpiryOnVisible()){
				printHitListColumnHeader("EXPIRY", "Expiry On");
			}
			out.println("<th></th>");
			out.println("</tr>");
			out.println("</thead>");
			out.println("<tbody>");
			for(Hit hit : documentHitlist){
				if(hit.userName.trim().length() > 0){
					out.println("<tr class=\"danger\">");	
				}else{
					out.println("<tr>");
				}
				if(acl.canDelete() || acl.canDownload()){
					out.println("<td class=\"text-center\">");
					if(hit.userName.trim().length() <= 0  && hit.isHeadRevision){
						out.println("<input type=\"checkbox\" name=\"chkDocumentId\" id=\"chkDocumentID\" value=\""+hit.documentId+"\" class=\"required\" title=\"Please select at least one document to peform an action.\" >");
					}else{
						out.println("&nbsp;");
					}
					out.println("</td>");
				}
				
				out.println("<td class=\"text-center\">");
				if(hit.userName.trim().length() > 0){
					out.println("<i class=\"fa fa-lock fa-lg tip\" data-toggle=\"tooltip\" data-placement=\"right\" title=\"Locked By : " + hit.userName.toUpperCase() +"\"></i> ");
				}
				out.println("</td>");
				out.println("<td class=\"text-center\" style=\"width:20px;\">");
				out.println("<a href=\"/console/viewdocument?view=quick&documentid="+hit.documentId+"&revisionid="+hit.revisionId+"\" class=\"viewdocument\" title=\"View Document\">");
				out.println("<img src=\"/images/"+StringHelper.getIconFileNameForExtension(hit.extension.toUpperCase())+ ".gif\" class=\"img-icon tip\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"View Document : "+hit.extension.toUpperCase()+"\">");
				out.println("</a>");
				out.println("</td>");
				
				if(loggedInUser.getMetaPreferences().isDocumentIdVisible()){
					out.println("<td class=\"text-center\">"+hit.documentId+"</td>");
				}
				if(loggedInUser.getMetaPreferences().isRevisionIdVisible()){
					out.println("<td class=\"text-center\">"+hit.revisionId+"</td>");
				}
				if(loggedInUser.getMetaPreferences().isCreatedByVisible()){
					out.println("<td class=\"text-center\">"+StringEscapeUtils.escapeHtml4(hit.createdBy)+"</td>");
				}
				if(loggedInUser.getMetaPreferences().isModifiedByVisible()){
					out.println("<td class=\"text-center\">"+StringEscapeUtils.escapeHtml4(hit.modifiedBy+"")+"</td>");
				}
				for (String value : hit.indexValues){
					out.println("<td>"+StringEscapeUtils.escapeHtml4(value)+"</td>");
				}
				if(loggedInUser.getMetaPreferences().isFileSizeVisible()){
					out.println("<td>"+StringHelper.formatSizeText(hit.fileLength)+"</td>");
				}
				if(loggedInUser.getMetaPreferences().isCreatedVisible()){
					out.println("<td>"+StringHelper.formatDate(hit.created)+"</td>");
				}
				if(loggedInUser.getMetaPreferences().isModifiedVisible()){
					out.println("<td>"+StringHelper.formatDate(hit.modified)+"</td>");
				}
				if(loggedInUser.getMetaPreferences().isExpiryOnVisible()){
					out.println("<td class=\"text-center\">");
					if(hit.expiryOn != null){
						out.println(StringHelper.formatDate(hit.expiryOn,ServerConstants.FORMAT_SHORT_DATE));
					}else{
						out.println("&nbsp;");
					}
					out.println("</td>");
				}
				out.println("<td class=\"text-center\" style=\"width:100px;\">");
				out.println("<a href=\"/console/viewdocument?documentid="+hit.documentId+"&revisionid="+hit.revisionId+"\" title=\"View Document\">");
				out.println("View Document");
				out.println("</a>");
				out.println("</td>");

				out.println("</tr>");
			}	
			out.println("</tbody>");
			out.println("</table>");
			out.println("</div>");//table-responsive
			out.println("</form>");

			out.println("<div class=\"panel-body\">");
			generatePagination("/console/opendocumentclass");
			out.println("<div id=\"errorWrapper\"><div id=\"errorDiv\"></div></div>");
			generateHistlistFooter(documentClass,acl);
			out.println("</div>");//panel-body
		}else{
			out.println("<div class=\"panel-body\">");
			out.println("There are no documents found");
			out.println("</div>");//panel-body
		}
		out.println("</div>");//panel
	}

	private void generateHistlistFooter(DocumentClass documentClass, ACL acl) throws Exception{
		Enumeration<String> enumRequest = request.getParameterNames(); // get all the requested parameters
		StringBuffer queryString = new StringBuffer("/console/opendocumentclass?");
		while(enumRequest.hasMoreElements()){
			String parameterName = (String) enumRequest.nextElement();
			if(parameterName.equalsIgnoreCase("page")){
				continue;
			}
			String parameterValue = request.getParameter(parameterName);
			queryString.append("&");
			queryString.append(parameterName);
			queryString.append("=");
			queryString.append(parameterValue);
		}
		out.println("<div class=\"row\"><hr/>");
		out.println("<div class=\"col-lg-6\">");
		if(acl.canDelete()){
			out.println("<a href=\"javascript:void(0);\" id=\"btnBulkDelete\" class=\"text-danger\"/>Delete</a> | ");
		}
		if(acl.canDownload()){
			out.println("<a href=\"javascript:void(0);\" id=\"btnBulkDownload\"/>Download as Zip</a> | ");
		}
		out.println("<a href=\""+StringEscapeUtils.escapeHtml4(queryString.toString()) +"&mode=pdf\">Export as PDF</a>");
		out.println("</div>");
		out.println("<div class=\"col-lg-6 text-right\">");
		out.println("<small>Total time taken to retreive the results <i>" + request.getAttribute("EXECUTIONTIME") + " seconds</i></small>");
		out.println("</div>");
		out.println("</div>");
	}
}