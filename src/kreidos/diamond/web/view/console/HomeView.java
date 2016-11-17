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

package com.primeleaf.krystal.web.view.console;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import com.primeleaf.krystal.constants.HTTPConstants;
import com.primeleaf.krystal.model.vo.AuditLogRecord;
import com.primeleaf.krystal.model.vo.Bookmark;
import com.primeleaf.krystal.model.vo.CheckedOutDocument;
import com.primeleaf.krystal.model.vo.DocumentClass;
import com.primeleaf.krystal.security.ACL;
import com.primeleaf.krystal.util.StringHelper;
import com.primeleaf.krystal.web.view.WebPageTemplate;
import com.primeleaf.krystal.web.view.WebView;

/**
 * @author Rahul Kubadia
 *
 */
public class HomeView extends WebView {

	public HomeView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}

	public void render() throws Exception{
		WebPageTemplate template = new WebPageTemplate(request,response);
		template.generateHeader();
		printConsole();
		template.generateFooter();
	}

	private void printConsole() throws Exception{
		out.println("<div class=\"panel panel-default\">");
		out.println("<div class=\"panel-heading\">");
		out.println("<h3><i class=\"fa fa-home fa-lg\"></i> My Workspace</h3>");
		out.println("</div>");
		out.println("<div class=\"panel-body\">");

		if(request.getAttribute(HTTPConstants.REQUEST_ERROR) != null){
			printErrorDismissable((String)request.getAttribute(HTTPConstants.REQUEST_ERROR));
		}
		if(request.getAttribute(HTTPConstants.REQUEST_MESSAGE) != null){
			printSuccessDismissable((String)request.getAttribute(HTTPConstants.REQUEST_MESSAGE));
		}
		out.println("<div class=\"row\">");
		out.println("<div class=\"col-lg-8\">");
		printDocumentClasses();
		printCheckouts();
		printRecentActivity();
		out.println("</div>");
		out.println("<div class=\"col-lg-4\">");
		printGreetings();
		printStorage();
		printChart();
		printBookmarks();
		out.println("</div>");
		out.println("</div>");
		
		out.println("</div>");//panel-body
		out.println("</div>");//panel
	}

	@SuppressWarnings("unchecked")
	private void printDocumentClasses(){
		out.println("<div class=\"panel panel-default\">");
		out.println("<div class=\"panel-heading\">");
		out.println("<h5><i class=\"fa fa-folder-open fa-lg \"></i> Document Classes</h5>");
		out.println("</div>");//panel-heading
		try {
			ArrayList<DocumentClass> documentClasses = 	(ArrayList<DocumentClass>) request.getAttribute("DOCUMENTCLASSLIST");
			if(documentClasses.size() > 0){
				out.println("<ul class=\"list-group\">");
				for(DocumentClass documentClass : documentClasses){
					ACL acl = (ACL)request.getAttribute(documentClass.getClassName()+"_ACL");
					int documentCount = documentClass.getActiveDocuments();
					out.println("<li class=\"list-group-item\">");
					out.println("<div class=\"row\">");
					out.println("<div class=\"col-xs-12 col-sm-10\">");
					out.println("<a href=\"/console/opendocumentclass?classid="+documentClass.getClassId()+"\" class=\"\">");
					out.println("<h3>"+StringEscapeUtils.escapeHtml4(documentClass.getClassName())+"</h3>");
					out.println("</a>");
					out.println("<p><h6>");
					out.println("<a href=\"/console/opendocumentclass?classid="+ documentClass.getClassId()+"\">View All ("+documentCount+") </a> | ");
					out.println("<a href=\"/console/searchdocumentclass?classid="+ documentClass.getClassId()+"\">Search</a>");
					if(acl.canCreate()){
						out.println(" | <a href=\"/console/newdocument?classid="+ documentClass.getClassId()+"\">Add Document</a>");
					}
					out.println("</h6></p>");
					out.println("</div>");

					out.println("<div class=\"col-xs-12 col-sm-2 text-right\">");
					out.println("<a href=\"/console/opendocumentclass?classid="+ documentClass.getClassId()+"\" title=\"Total Documents\">");
					out.println("<h3 class=\"odometer totaldocs"+documentClass.getClassId()+"\">0</h3>");
					if(documentCount > 0 ){
						out.println("<script>setTimeout(function(){$('.totaldocs"+documentClass.getClassId()+"').html('"+documentCount+"');},1000);</script>");
					}
					out.println("<p><h6>Total Documents</h6></p>");
					out.println("</a>");
					out.println("</div>");

					out.println("</div>");
					out.println("</li>");
				}
				out.println("</ul>");
			}else{
				out.println("<div class=\"panel-body\">");
				out.println("There are no document classes currently available");
				out.println("</div>");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		out.println("</div>");
	}


	@SuppressWarnings("unchecked")
	private void printCheckouts(){
		try {
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\">");
			out.println("<h5><i class=\"fa fa-lock fa-lg \"></i>  Checked Out Documents</h5>");
			out.println("</div>");

			ArrayList<CheckedOutDocument> checkedOutDocumentList = 	(ArrayList<CheckedOutDocument>) request.getAttribute("CHECKOUTS");
			if(checkedOutDocumentList.size() > 0){
				out.println("<div class=\"list-group\">");
				for(CheckedOutDocument checkedOutDocument : checkedOutDocumentList){
					out.println("<li class=\"list-group-item\">");
					out.println("<a href=\"/console/viewdocument?documentid="+checkedOutDocument.getDocumentId()+"&revisionid="+checkedOutDocument.getRevisionId()+"\" class=\"\">");
					out.println("<h4>"+StringEscapeUtils.escapeHtml4(checkedOutDocument.getCheckOutPath().toLowerCase())+"</h4>");
					out.println("</a>");
					out.println("<h5>Document Class : "+StringEscapeUtils.escapeHtml4(checkedOutDocument.getDocumentClass().getClassName())+"</h5>");
					out.println("<p><h6><a href=\""+HTTPConstants.BASEURL+"/console/viewdocument?documentid="+checkedOutDocument.getDocumentId()+"&revisionid="+checkedOutDocument.getRevisionId()+"\">"+"View Document"+"</a>");
					out.println(" | <a href=\""+HTTPConstants.BASEURL+"/console/checkindocument?documentid="+checkedOutDocument.getDocumentId()+"&revisionid="+checkedOutDocument.getRevisionId()+"\">Check In</a>");
					out.println(" | <a href=\""+HTTPConstants.BASEURL+"/console/cancelcheckout?documentid="+checkedOutDocument.getDocumentId()+"&revisionid="+checkedOutDocument.getRevisionId()+"\" class=\"confirm\" title=\"Are you sure? you want to cancel checkout?\">Cancel Checkout</a>");
					out.println(" | <a href=\""+HTTPConstants.BASEURL+"/console/revisionhistory?documentid="+checkedOutDocument.getDocumentId()+"&revisionid="+checkedOutDocument.getRevisionId()+"\" class=\"revisionhistory\" data-toggle=\"modal\" data-target=\"#revisionHistoryModal\">Revision History</a></h6></p>");
					out.println("</li>");
				}
				out.println("</div>");
				printModal("revisionHistoryModal");
			}else{
				out.println("<div class=\"panel-body\">");
				out.println("There are no documents checked out currently");
				out.println("</div>");
			}
			out.println("</div>");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void printBookmarks(){
		try{
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\">");
			out.println("<h5><i class=\"fa fa-bookmark fa-lg \"></i> Bookmarks</h5>");
			out.println("</div>");
			ArrayList<Bookmark> bookmarkList = (ArrayList<Bookmark>)request.getAttribute("BOOKMARKS");
			if(bookmarkList.size() > 0){
				out.println("<ul class=\"list-group\">");
				for(Bookmark bookmark : bookmarkList){
					out.println("<li class=\"list-group-item\">");
					out.println("<h4 class=\"\">" + StringEscapeUtils.escapeHtml4(bookmark.getBookmarkName())+ "</h4>");
					out.println("<h5>Document ID : " +  bookmark.getDocumentId() + "&nbsp;Revision ID : " +  bookmark.getRevisionId() + "</h5>");
					out.println("<p><h6>");
					out.println("<a href=\"/console/viewdocument?documentid="+bookmark.getDocumentId() + "&revisionid="+bookmark.getRevisionId()+"\"  title=\"View Document\">View Document</a>");
					out.println(" | <a href=\"/console/deletebookmark?bookmarkid="+ bookmark.getBookmarkId()+"\"  class=\"confirm\" title=\"Are you sure you want to delete bookmark?\">Delete Bookmark</a>");
					out.println("</h6></p>");
					out.println("</li>");//list-group-item
				}// for
			}else{
				out.println("<div class=\"panel-body\">");
				out.println("There are no bookmarks available currently");
				out.println("</div>");
			}
			out.println("</div>");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}


	@SuppressWarnings("unchecked")
	private void printChart(){
		try{
			ArrayList<DocumentClass> documentClasses = 	(ArrayList<DocumentClass>) request.getAttribute("DOCUMENTCLASSLIST");
			if(documentClasses.size() >  0 ){
				out.println("<div class=\"panel panel-default\">");
				out.println("<div class=\"panel-heading\">");
				out.println("<h5><i class=\"fa fa-pie-chart fa-lg \"></i> Charts</h5>");
				out.println("</div>");
				out.println("<div class=\"panel-body text-center\">");
				out.println("<div id=\"homechart\" style=\"height:220px;\">");
				out.println("<script>");
				out.println("new Morris.Donut({");
				out.println("  element: 'homechart',");
				out.println("  data: [");

				for(DocumentClass documentClass : documentClasses){
					int documentCount = documentClass.getActiveDocuments();
					out.println("    { label: \""+StringEscapeUtils.escapeHtml4(documentClass.getClassName())+"\", value: "+documentCount+" },");
				}
				out.println("  ],");
				out.println("});");
				out.println("</script>");
				out.println("</div>");
				out.println("</div>");
				out.println("</div>");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void printRecentActivity(){
		out.println("<div class=\"panel panel-default\">");
		out.println("<div class=\"panel-heading\"><h5><i class=\"fa fa-clock-o fa-lg  \"></i> Recent Access History</h5></div>");
		out.println("<div class=\"table-responsive\">");
		out.println("<table class=\"table table-condensed table-hover table-striped\">");

		int count=0; //for showing only first 10 records
		int size=10;
		ArrayList<AuditLogRecord> auditLogs = (ArrayList<AuditLogRecord>)request.getAttribute("AUDITLOGS");
		out.println("<thead>");
		out.println("<tr>");
		out.println("<th>Action</th>");
		out.println("<th>Type</th>");
		out.println("<th>Action Date</th>");
		out.println("<th>IP Address</th>");
		out.println("<th>Comments</th>");
		out.println("</tr>");
		out.println("</thead>");
		out.println("<tbody>");
		for(AuditLogRecord auditLogRecord:auditLogs){
			if(count >= size)break;
			out.println("<tr>");
			out.println("<td>" + auditLogRecord.getAction()+ "</td>");
			out.println("<td>" + auditLogRecord.getObjectDescription()+ "</td>");
			out.println("<td>" + StringHelper.formatDate(auditLogRecord.getActionDate(),"dd-MMM-yyyy HH:mm")+ "</td>");
			out.println("<td>" + auditLogRecord.getIpAddress()+ "</td>");
			out.println("<td>" + StringEscapeUtils.escapeHtml4(auditLogRecord.getComments())+ "</td>");
			out.println("</tr>");
			count++;
		}	
		out.println("</tbody>");
		out.println("</table>");
		out.println("</div>");
		out.println("</div>");
	}

	private void printGreetings(){
		out.println("<div class=\"panel panel-default\">");
		out.println("<div class=\"panel-heading\">");
		out.println("<a href=\"/console/myprofile\">");
		out.println("<h5><i class=\"fa fa-user fa-lg\"></i> My Profile</h5>");
		out.println("</a>");
		out.println("</div>");//panel-heading
		out.println("<div class=\"panel-body\">");
		out.println("<div class=\"row\">");
		out.println("<div class=\"col-sm-3\">");
		out.println("<img src=\"/console/profilepicture?size=medium&username="+loggedInUser.getUserName()+"\" class=\"thumbnail\"/ style=\"margin:10px 2px; 0px;\">");
		out.println("</div>");
		out.println("<div class=\"col-sm-9\">");
		out.println("<h3>Hello "+StringEscapeUtils.escapeHtml4(loggedInUser.getRealName())+" ! </h3>");
		String lastLogin = loggedInUser.getLastLoginDate()!=null?StringHelper.formatDate(loggedInUser.getLastLoginDate()):"0000-00-00 00:00:00";
		out.println("<p>Your last login : "+lastLogin+"</p>");
		out.println("</div>");
		out.println("</div>");
		out.println("</div>");
		out.println("</div>");
	}
	
	private void printStorage(){

		Long usedSpace = (Long)request.getAttribute("USEDSTORAGE");
		out.println("<div class=\"panel panel-default\">");
		out.println("<div class=\"panel-heading\">");
		out.println("<h5 class=\"\"><i class=\"fa fa-database\"></i> Storage Details</h5>");
		out.println("</div>");
		out.println("<div class=\"panel-body\">");

		out.println("<p>Current Used Storage Space : <strong>"+StringHelper.formatSizeText(usedSpace) +"</strong></p>");
		out.println("</div>");//panel-body
		out.println("</div>");//panel
	
	}
}
