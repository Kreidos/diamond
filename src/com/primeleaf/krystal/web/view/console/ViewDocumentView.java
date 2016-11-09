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

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import com.primeleaf.krystal.constants.HTTPConstants;
import com.primeleaf.krystal.constants.ServerConstants;
import com.primeleaf.krystal.model.vo.Document;
import com.primeleaf.krystal.model.vo.DocumentClass;
import com.primeleaf.krystal.model.vo.DocumentRevision;
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

public class ViewDocumentView extends WebView {
	String view = "";
	public ViewDocumentView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}

	public void render() throws Exception{
		WebPageTemplate template = new WebPageTemplate(request,response);
		view = request.getParameter("view")!=null?request.getParameter("view"):"";
		if(view.trim().length() <= 0 ){
			template.generateHeader();
			printDocument();
			template.generateFooter();
		}else{
			template.generatePopupHeader();
			printDocument();
			template.generatePopupFooter();
		}
	}
	private void printBreadCrumbs() throws Exception{
		DocumentClass documentClass = (DocumentClass) request.getAttribute("DOCUMENTCLASS");
		out.println("<ol class=\"breadcrumb\">");
		out.println("<li><a href=\"/console\">My Workspace</a></li>");
		out.println("<li><a href=\"/console/searchdocumentclass?classid="+documentClass.getClassId()+"\">Search Document Class</a></li>");
		out.println("<li><a href=\"/console/opendocumentclass?classid="+documentClass.getClassId()+"\">Document Hitlist</a></li>");
		out.println("<li class=\"active\">View Document</li>");
		out.println("</ol>");
	}
	@SuppressWarnings("unchecked")
	private void printDocument() throws Exception{
		Document document = (Document) request.getAttribute("DOCUMENT");
		DocumentRevision documentRevision = (DocumentRevision) request.getAttribute("DOCUMENTREVISION");
		DocumentClass documentClass = (DocumentClass) request.getAttribute("DOCUMENTCLASS");
		
		LinkedHashMap<String,String> documentIndexes = (LinkedHashMap<String,String>) request.getAttribute("DOCUMENTINDEXES");
		boolean isHeadRevision = document.getRevisionId().equalsIgnoreCase(documentRevision.getRevisionId());

		ACL acl = (ACL) request.getAttribute("ACL");

		if(view.trim().length() <= 0 ){//do not show document id and revision id for attachment or quick view
			printBreadCrumbs();
		}

		out.println("<h3><i class=\"fa fa-folder-open\"></i> "+StringEscapeUtils.escapeHtml4(documentClass.getClassName()));
		out.println("<small>");
		out.println("<span class=\"label label-success tip\" data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Document ID\">"+documentRevision.getDocumentId()+"</span>&nbsp;");
		out.println("<span class=\"label label-default tip\" data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Revision ID\">"+documentRevision.getRevisionId()+"</span>&nbsp;");
		out.println("<span class=\"label label-success tip\" data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"File Size\">"+StringHelper.formatSizeText(documentRevision.getLength())+"</span></small></h3>");

		String lastModified = "";
		if(document.getModified() == null){
			lastModified = document.getCreated();
		}else{
			lastModified = document.getModified().toString();
		}
		out.println("Last Modified By &nbsp;" +  StringEscapeUtils.escapeHtml4(document.getModifiedBy()));
		out.println("&nbsp;");
		out.println("On &nbsp;" +  StringHelper.formatDate(lastModified));
		out.println(" ("+ StringHelper.getFriendlyDateTime(lastModified)+ ")" );

		out.println("<hr/>");

		if(request.getAttribute(HTTPConstants.REQUEST_ERROR) != null){
			printErrorDismissable((String)request.getAttribute(HTTPConstants.REQUEST_ERROR));
		}
		if(request.getAttribute(HTTPConstants.REQUEST_MESSAGE) != null){
			printSuccessDismissable((String)request.getAttribute(HTTPConstants.REQUEST_MESSAGE));
		}

		out.println("<ul class=\"nav nav-tabs\" id=\"documenTabs\">");
		out.println("<li class=\"active\"><a href=\"#document\" data-toggle=\"tab\"><img src=\"/images/"+StringHelper.getIconFileNameForExtension(document.getExtension())+".gif\" >&nbsp;Document</a></li>");
		out.println("<li><a href=\"#documentNotes\" data-toggle=\"tab\" class=\"internal\"  datatarget=\"#resultNotes\" data-src=\"/console/documentnotes?documentid="+document.getDocumentId()+"&revisionid="+documentRevision.getRevisionId()+"\"><i class=\"fa fa-comments-o\"></i> Notes</a></li>");
		out.println("<li><a href=\"#accessHistory\" data-toggle=\"tab\" class=\"internal\"  datatarget=\"#resultAccessHistory\" data-src=\"/console/accesshistory?documentid="+document.getDocumentId()+"&revisionid="+documentRevision.getRevisionId()+"\"><i class=\"fa fa-clock-o\"></i> Access History</a></li>");
		out.println("</ul>");
		out.println("<div class=\"tab-content\">");
		out.println("<div class=\"tab-pane in active\" id=\"document\"><br/>");
		out.println("<div class=\"row\">");
		out.println("<div class=\"col-sm-9\">");
		//viewer applet starts here
		try {
			
			out.println("<div class=\"well well-sm\" id=\"viewer\">");
			out.println("<iframe src = \"/js/ViewerJS/?type=pdf#/console/mobiledocumentviewer?documentid="+documentRevision.getDocumentId()+"&revisionid="+documentRevision.getRevisionId()+"\" width='100%' height='800'></iframe>");
			out.println("</div>");//visible-xs


			if(acl.canEmail()){
				out.println("<div class=\"panel panel-default\">");
				out.println("<div class=\"panel-heading\"><i class=\"fa fa-envelope-o\"></i> Share Document</div>");
				out.println("<div class=\"panel-body\">");
				out.println("<form action=\"/console/sharedocument\" method=\"post\" id=\"frmShareDocument\" form-type=\"ajax\" datatarget=\"#resultShareDocument\">");
				out.println("<div id=\"resultShareDocument\"></div>");
				out.println("<p>Fields marked with <span style='color:red'>*</span> are mandatory</p>");

				out.println("<div class=\"form-group\">");
				out.println("<label for=\"txtEmail\">Email ID <span style='color:red'>*</span></label>");
				out.println("<input type=\"text\" id=\"txtEmail\" name=\"txtEmail\" class=\"required form-control multiemail\" maxlength=\"500\" title=\"Please use a comma to separate multiple email addresses\"/>");
				out.println("</div>");

				out.println("<div class=\"form-group\">");
				out.println("<label for=\"txtComments\">Comments <span style='color:red'>*</span></label>");
				out.println("<textarea class=\"form-control required\" rows=\"4\" name=\"txtComments\"  id=\"txtComments\"  placeholder=\"Put your comments here\"></textarea>");
				out.println("</div>");
				out.println("<input type=\"hidden\"  name=\"documentid\"  	value=\""+ document.getDocumentId() + "\" />");
				out.println("<input type=\"hidden\"  name=\"revisionid\"  	value=\""+ documentRevision.getRevisionId() + "\" />");
				out.println("<input type=\"submit\"  name=\"btnSubmit\" 	id=\"btnShare\" 	value=\"Share Document\" 	class=\"btn btn-sm btn-default\"  data-loading-text=\"Sharing Document...\"/>");
				out.println("</form>");
				out.println("</div>");//panel-body
				out.println("</div>");//panel
			}

			out.println("</div>");//col-sm-9

			out.println("<div class=\"col-sm-3\">");
			String expiryOn = "";
			if( document.getExpiry() == null){
				expiryOn = "";
			}else{
				expiryOn = document.getExpiry().toString();
				expiryOn = StringHelper.formatDate(expiryOn,ServerConstants.FORMAT_SHORT_DATE);
			}
			String lastAccessed = "";
			if( document.getLastAccessed() == null){
				lastAccessed = document.getCreated();
			}else{
				lastAccessed = document.getLastAccessed().toString();
			}


			if(acl.canWrite() && document.getStatus().equalsIgnoreCase(Hit.STATUS_AVAILABLE) && isHeadRevision){
				out.println("<div class=\"panel panel-default\">");
				out.println("<div class=\"panel-heading\"><i class=\"fa fa-list\"></i> Edit Indexes</div>");
				out.println("<div class=\"panel-body\">");
				out.println("<form action=\"/console/editdocumentindexes\" method=\"post\" id=\"frmEditIndexes\" accept-charset=\"utf-8\"  form-type=\"ajax\" datatarget=\"#resultIndexes\">");
				out.println("<div id=\"resultIndexes\"></div>");
				out.println("<p>Fields marked with <span style='color:red'>*</span> are mandatory</p>");
				for(IndexDefinition indexDefinition :documentClass.getIndexDefinitions()){
					String required = "";
					out.println("<div class=\"form-group\">");
					out.println("<label for=\""+indexDefinition.getIndexColumnName()+"\"> "+StringEscapeUtils.escapeHtml4(indexDefinition.getIndexDisplayName()));
					if(indexDefinition.isMandatory() ){
						required = "required";
						out.println(" <span style='color:red'>*</span>");
					}
					out.println("</label>");

					String value = (String)documentIndexes.get(indexDefinition.getIndexDisplayName());
					value = StringEscapeUtils.escapeHtml4(value);

					if(indexDefinition.getIndexType().equals(IndexDefinition.INDEXTYPE_DATE)){
						out.println("<div class=\"input-group\">");
						out.println("<input type=\"text\" class=\"shortdate isdate form-control "+ required +"\" name=\""+indexDefinition.getIndexColumnName()+"\" id=\""+indexDefinition.getIndexColumnName()+"\" value=\""+value+"\" maxlength=\""+indexDefinition.getIndexMaxLength()+"\"  cid=\""+documentClass.getClassId()+"\">");
						out.println("<span class=\"input-group-addon\"><i class=\"fa fa-calendar\"></i></span>");
						out.println("</div>");
					}else if(indexDefinition.getIndexType().equals(IndexDefinition.INDEXTYPE_NUMBER)){
						out.println("<input type=\"text\" class=\"number  form-control "+ required +" autocomplete\"    id=\""+indexDefinition.getIndexColumnName()+"\" name=\""+indexDefinition.getIndexColumnName()+"\" value=\""+value+"\" maxlength=\""+indexDefinition.getIndexMaxLength()+"\"   cid=\""+documentClass.getClassId()+"\">");
					}else {
						out.println("<input type=\"text\" class=\"autocomplete form-control "+ required +" \" id=\""+indexDefinition.getIndexColumnName()+"\"  name=\""+indexDefinition.getIndexColumnName()+"\" value=\""+value+"\" maxlength=\""+indexDefinition.getIndexMaxLength()+"\"  cid=\""+documentClass.getClassId()+"\">");
					}
					out.println("</div>");
				}

				out.println("<hr/>");
				out.println("<div class=\"form-group\">");
				out.println("<label for=\"txtExpiryDate\"> Expiry On</label>");
				out.println("<div class=\"input-group\">");
				out.println("<input type=\"text\" class=\"shortdate isdate form-control\" name=\"txtExpiryDate\" id=\"txtExpiryDate\" maxlength=\"12\" value=\""+expiryOn+"\"/>");
				out.println("<span class=\"input-group-addon\"><i class=\"fa fa-calendar\"></i></span>");
				out.println("</div>");
				out.println("</div>");

				out.println("<input type=\"hidden\" name=\"revisionid\" value=\""+documentRevision.getRevisionId()+"\">");
				out.println("<input type=\"hidden\"  name=\"documentid\"  value=\""+ document.getDocumentId() + "\"/>");
				out.println("<input type=\"submit\"  name=\"btnSubmit\"  value=\"Submit\" class=\"btn btn-sm btn-default\">");

				out.println("</form>");
				out.println("</div>");//panel-body
				out.println("</div>");//panel

			}else{
				out.println("<div class=\"panel panel-default\">");
				out.println("<div class=\"panel-heading\"><i class=\"fa fa-check-square-o\"></i> Document Indexes</div>");
				out.println("<div class=\"panel-body\">");
				out.println("<div class=\"table-responsive\">");
				out.println("<table class=\"table table-hover\">");
				if(! documentIndexes.keySet().isEmpty()){
					out.println("<thead>");
					out.println("<tr>");
					out.println("<th>Index Field</th>");
					out.println("<th>Index Value</th>");
					out.println("</tr>");
					out.println("</thead>");

					out.println("<tbody>");
					for(String indexName:documentIndexes.keySet()){
						String indexValue = (String)documentIndexes.get(indexName);
						out.println("<tr>");
						out.println("<td>"+StringEscapeUtils.escapeHtml4(indexName)+"</td>");
						out.println("<td>"+StringEscapeUtils.escapeHtml4(indexValue)+"</td>");
						out.println("</tr>");
					}
				}
				out.println("<tr class=\"text-success\">");
				out.println("<td >Expiry On</td>");
				out.println("<td>"+expiryOn+"</td>");
				out.println("</tr>");

				out.println("</tbody>");
				out.println("</table>");
				out.println("</div>");//table-responsive
				out.println("</div>");//panel-body
				out.println("</div>");//panel
			}

			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\"><i class=\"fa fa-file\"></i> Document Properties</div>");
			out.println("<div class=\"table-responsive\">");
			out.println("<table class=\"table table-hover\">");
			out.println("<thead><tr><th>Property</th><th>Value</th></tr></thead>");
			out.println("<tbody><tr><td>Created By</td><td> "+ document.getCreatedBy()+"</td></tr>");
			out.println("<tr><td>Created On</td><td>" +  StringHelper.getFriendlyDateTime(document.getCreated()) +"</td></tr>");
			out.println("<tr><td>Last Accessed On</td><td>"+StringHelper.getFriendlyDateTime(lastAccessed) + "</td></tr>");
			out.println("<tr><td>Access Count</td><td>"+ document.getAccessCount()+"</td></tr>");
			out.println("</tbody></table>");//
			out.println("</div>");//table-responsive
			out.println("</div>");//panel

			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\"><i class=\"fa fa-bookmark\"></i> Bookmark Document</div>");
			out.println("<div class=\"panel-body\">");
			out.println("<form action=\"/console/newbookmark\" method=\"post\" id=\"frmNewBookmark\"  form-type=\"ajax\" datatarget=\"#resultBookmark\" accept-charset=\"utf-8\">");
			out.println("<div id=\"resultBookmark\"></div>");
			out.println("<p>Fields marked with <span style='color:red'>*</span> are mandatory</p>");
			out.println("<div class=\"form-group\">");
			out.println("<label for=\"txtBookmarkName\">Bookmark Name<span style='color:red'>*</span></label>");
			out.println("<input type=\"text\" id=\"txtBookmarkName\" name=\"txtBookmarkName\" class=\"required form-control\" maxlength=\"50\" title=\"Please enter Bookmark Name\"/>");
			out.println("</div>");
			out.println("<input type=\"hidden\"  name=\"documentid\"  	value=\""+ document.getDocumentId() + "\" />");
			out.println("<input type=\"hidden\"  name=\"revisionid\"  	value=\""+ documentRevision.getRevisionId() + "\" />");
			out.println("<input type=\"submit\"  name=\"btnSubmit\"  	value=\""+ "Save" + "\" 	class=\"btn btn-sm btn-default\"/>");
			out.println("</form>");
			out.println("</div>");//panel-body
			out.println("</div>");//panel


			if(view.trim().length() <= 0 ){
				out.println("<div class=\"well well-sm\">");
				if(acl.canDownload()){
					out.println("<a href=\"/console/downloaddocument?documentid="+document.getDocumentId()+"&revisionid="+documentRevision.getRevisionId()+"\" class=\"btn btn-default btn-block \" title=\"Download Document\" ><i class=\"fa fa-download\"></i> Download Document</a>");
				}
				if(documentClass.isRevisionControlEnabled()){
					if(document.getStatus().equalsIgnoreCase(Hit.STATUS_AVAILABLE) && isHeadRevision){
						if(acl.canCheckout()){
							out.println("<a href=\"/console/checkoutdocument?documentid="+document.getDocumentId()+"\" class=\"btn btn-default btn-block \" title=\"Check Out Document\" ><i class=\"fa fa-lock\"></i> Check Out Document</a>");
						}
					}
					if(document.getStatus().equalsIgnoreCase(Hit.STATUS_LOCKED)){
						out.println("<a href=\"/console/cancelcheckout?documentid="+document.getDocumentId()+"\" class=\"btn  btn-default btn-block confirm\" title=\"Are you sure, you want to cancel checkout of this document?\" ><i class=\"fa fa-unlock-alt\"></i> Cancel Checkout</a>");
						if(acl.canCheckin()){
							out.println("<a href=\"/console/checkindocument?documentid="+document.getDocumentId()+"\" class=\"btn  btn-default btn-block \" title=\"Check In\" ><i class=\"fa fa-arrow-right\"></i> Check In</a>");
						}
					}
					out.println("<a href=\"/console/revisionhistory?documentid="+document.getDocumentId()+"\" data-toggle=\"modal\" data-target=\"#revisionHistoryModal\" class=\"btn  btn-default btn-block \" title=\"Revision History\"><i class=\"fa fa-clock-o\"></i> Revision History</a>");
				}
				if(acl.canDelete() && document.getStatus().equalsIgnoreCase(Hit.STATUS_AVAILABLE) && isHeadRevision){
					out.println("<a href=\"/console/deletedocument?documentid="+document.getDocumentId()+"\" class=\"btn btn-danger btn-block confirm\" title=\"Are you sure, you want to mark this document as deleted?\" ><i class=\"fa fa-trash-o\"></i> Delete Document</a>");
				}
				out.println("</div>");//well well-sm
			}
			out.println("</div>");//col-sm-3
			out.println("</div>");//row
			out.println("</div>");//tab-pane active #document

			out.println("<div class=\"tab-pane fade\" id=\"attachment\">");
			out.println("<iframe frameborder=\"0\" border=\"0\" width=\"0\" height=\"0\" name=\"attachmentFrame\" id=\"attachmentFrame\" src=\"\" style=\"border:0px;\"></iframe>");

			out.println("<div class=\"row\">");
			out.println("<div class=\"col-sm-9\">");
			out.println("<div id=\"resultAttachments\"></div>");
			out.println("</div>");//col-sm-9

			out.println("<div class=\"col-sm-3\">");
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\"><i class=\"fa fa-paperclip\"></i> Add Attachment</div>");
			out.println("<div class=\"panel-body\">");
			out.println("<form action=\"/console/attachments\" method=\"post\" id=\"frmAttachments\" name=\"frmAttachments\" enctype=\"multipart/form-data\" target=\"attachmentFrame\" accept-charset=\"utf-8\" class=\"internal\">");
			out.println("<p>Fields marked with <span style='color:red'>*</span> are mandatory</p>");

			out.println("<div class=\"form-group\">");
			out.println("<label for=\"fileDocument\">Select Document <span style='color:red'>*</span></label>");
			out.println("<input type=\"file\" name=\"fileDocument\" class=\"required\" title=\"Please select document\">");
			out.println("</div>");

			out.println("<div class=\"form-group\">");
			out.println("<label for=\"TITLE\">Attachment Title <span class=\"text-success\">*</span></label>");
			out.println("<input type=\"text\" name=\"TITLE\" id=\"TITLE\" class=\"form-control required\" title=\"Please enter attachment title\" placeholder=\"Attachment Title\"/>");
			out.println("</div>");

			out.println("<div class=\"form-group\">");
			out.println("<label for=\"KEYWORDS\">Attachment Keywords </label>");
			out.println("<textarea name=\"KEYWORDS\" id=\"KEYWORDS\" rows=\"5\" class=\"form-control\" placeholder=\"Attachment Keywords\"></textarea>");
			out.println("</div>");

			out.println("<div class=\"form-group\" id=\"pbContainer\" style=\"display:none;\">");
			out.println("<div class=\"progress progress-striped active\" id=\"progressbarMain\">");
			out.println("<div class=\"progress-bar progress-bar-success\" id=\"progressbar\" role=\"progressbar\" aria-valuenow=\"0\" aria-valuemin=\"0\" aria-valuemax=\"100\" style=\"width: 0%\">");
			out.println("<span class=\"sr-only\">0% Complete</span>");
			out.println("</div>");
			out.println("</div>");
			out.println("<div id=\"progressMessage\" class=\"alert alert-success alert-dismissable\"><button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button></div>");
			out.println("</div>");

			out.println("<input type=\"hidden\" name=\"DOCID\" value=\""+document.getDocumentId()+"\">");
			out.println("<input type=\"hidden\" name=\"REVISIONID\" value=\""+documentRevision.getRevisionId()+"\">");
			out.println("<input type=\"submit\"  name=\"btnSubmit\"  value=\"Submit\" class=\"btn btn-sm btn-default\">");
			out.println("</form>");
			out.println("</div>");//col-sm-3
			out.println("</div>");//row
			out.println("</div>");//panel-body
			out.println("</div>");//panel
			out.println("</div>");//tab-pane active #attachment

			out.println("<div class=\"tab-pane fade\" id=\"documentNotes\"><br/>");
			out.println("<div class=\"row\">");
			out.println("<div class=\"col-sm-9\">");
			out.println("<div id=\"resultNotes\"></div>");//notes will be loaded here
			out.println("</div>");//col-sm-9
			out.println("<div class=\"col-sm-3\">");
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\"><i class=\"fa fa-comment-o fa-lg\"></i> Add Note / Comments</div>");
			out.println("<div class=\"panel-body\">");
			out.println("<form action=\"/console/documentnotes\" id=\"frmNotes\" method=\"post\"  form-type=\"ajax\" datatarget=\"#resultNotes\">");
			out.println("<p>Fields marked with <span style='color:red'>*</span> are mandatory</p>");
			out.println("<div class=\"form-group\">");
			out.println("<label for=\"txtNote\">Note / Comments <span class=\"text-success\">*</span></label>");
			out.println("<textarea name=\"txtNote\" id=\"txtNote\" rows=\"5\" class=\"form-control required alphaNumericSpace\" title=\"Please enter note\" placeholder=\"Note / Comments\"></textarea>");
			out.println("</div>");

			out.println("<div class=\"form-group\">");
			out.println("<div class=\"btn-group\" data-toggle=\"buttons\">");
			out.println("<label class=\"btn btn-sm btn-default active\">");
			out.println("<input type=\"radio\" id=\"radNoteType1\" name=\"radNoteType\" value=\"P\" checked>Public");
			out.println("</label>");
			out.println("<label class=\"btn btn-sm btn-default\">");
			out.println("<input type=\"radio\" id=\"radNoteType2\" name=\"radNoteType\"  value=\"U\">Private");
			out.println("</label>");
			out.println("</div>");
			out.println("</div>");

			out.println("<hr/>");
			out.println("<input type=\"hidden\" name=\"documentid\" value=\""+document.getDocumentId()+"\">");
			out.println("<input type=\"submit\"  name=\"btnSubmit\"  value=\"Submit\" class=\"btn btn-sm btn-default\">");

			out.println("</form>");
			out.println("</div>");//col-sm-3
			out.println("</div>");//row

			out.println("</div>");//panel-body
			out.println("</div>");//panel
			out.println("</div>");//tab-pane active #notes

			out.println("<div class=\"tab-pane fade\" id=\"accessHistory\"><br/>");
			out.println("<div class=\"row\">");
			out.println("<div class=\"col-sm-9\">");
			out.println("<div id=\"resultAccessHistory\"></div>");//access history will be loaded here
			out.println("</div>");//col-sm-9
			out.println("<div class=\"col-sm-3\">");
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\"><i class=\"fa fa-filter fa-lg\"></i> Filter Access History</div>");
			out.println("<div class=\"panel-body\">");
			out.println("<form action=\"/console/accesshistory\" id=\"frmAccessHistory\" method=\"post\" form-type=\"ajax\" datatarget=\"#resultAccessHistory\">");
			out.println("<div class=\"form-group\">");
			out.println("<label for=\"fromDate\">From</label>");
			out.println("<div class=\"input-group\">");
			out.println("<input type=\"text\"  id=\"fromDate\" name=\"fromDate\" class=\"form-control shortdate isDate\">");
			out.println("<span class=\"input-group-addon\"><i class=\"fa fa-calendar\"></i></span>");
			out.println("</div>");
			out.println("</div>");

			out.println("<div class=\"form-group\">");
			out.println("<label for=\"toDate\">To</label>");
			out.println("<div class=\"input-group\">");
			out.println("<input type=\"text\"  id=\"toDate\" name=\"toDate\" class=\" form-control shortdate isDate\">");
			out.println("<span class=\"input-group-addon\"><i class=\"fa fa-calendar\"></i></span>");
			out.println("</div>");
			out.println("</div>");

			out.println("<input type=\"hidden\" name=\"documentid\" value=\""+document.getDocumentId()+"\">");
			out.println("<input type=\"submit\"  name=\"btnSubmit\"  value=\"Show Access History\" class=\"btn btn-sm btn-default btn-block\">");

			out.println("</form>");
			out.println("</div>");//col-sm-3
			out.println("</div>");//row
			out.println("</div>");//panel-body
			out.println("</div>");//panel

			out.println("</div>");//tab-pane active #accessHistory
			
			printModal("revisionHistoryModal");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		out.println("</div>");//tab-content
	}
}