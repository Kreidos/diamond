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
import com.primeleaf.krystal.model.vo.CheckedOutDocument;
import com.primeleaf.krystal.model.vo.DocumentClass;
import com.primeleaf.krystal.model.vo.User;
import com.primeleaf.krystal.util.StringHelper;
import com.primeleaf.krystal.web.view.WebPageTemplate;
import com.primeleaf.krystal.web.view.WebView;

/**
 * @author Rahul Kubadia
 *
 */

public class ManageCheckoutsView extends WebView {
	public ManageCheckoutsView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}
	public void render() throws Exception{
		WebPageTemplate template = new WebPageTemplate(request,response);
		template.generateHeader();
		printCheckOuts();
		template.generateFooter();
	}
	private void printBreadCrumbs() throws Exception{
		out.println("<ol class=\"breadcrumb\">");
		out.println("<li><a href=\"/cpanel\">Control Panel</a></li>");
		out.println("<li class=\"active\">Manage Checked Out Documents</li>");
		out.println("</ol>");
	}
	@SuppressWarnings("unchecked")
	private void printCheckOuts() throws Exception{
		printBreadCrumbs() ;

		out.println("<div class=\"panel panel-default\">");
		out.println("<div class=\"panel-heading\">");
		out.println("<h4><i class=\"fa fa-lock fa-lg\"></i> Manage Checkouts</h4>");
		out.println("</div>");

		out.println("<div class=\"panel-body\">");
		
		if(request.getAttribute(HTTPConstants.REQUEST_ERROR) != null){
			printErrorDismissable((String)request.getAttribute(HTTPConstants.REQUEST_ERROR));
		}
		if(request.getAttribute(HTTPConstants.REQUEST_MESSAGE) != null){
			printSuccessDismissable((String)request.getAttribute(HTTPConstants.REQUEST_MESSAGE));
		}
		try {
			ArrayList<DocumentClass> documentClassList = (ArrayList<DocumentClass>) request.getAttribute("CLASSLIST");
			ArrayList<User> userList = (ArrayList<User>) request.getAttribute("USERLIST");
			String selectedDocumentClass = (String) request.getAttribute("DOCUMENTCLASS");
			String selectedUser = (String) request.getAttribute("USER");

			ArrayList<CheckedOutDocument> checkedOutDocuments =  (ArrayList<CheckedOutDocument>)request.getAttribute("CHECKOUTLIST");
			if(checkedOutDocuments != null){
				if(checkedOutDocuments.size() > 0 ){
					int count = 0;
					out.println("<legend><h4>Currently Checked Out Documents</h4></legend>");
					out.println("<div class=\"row\">");
					for (CheckedOutDocument checkedOutDocument : checkedOutDocuments) {
						count++;
						out.println("<div class=\"col-sm-6\">");
						out.println("<div class=\"panel panel-default\">");
						out.println("<div class=\"panel-body\">");
						out.println("<h4 class=\"text-danger\">" + StringEscapeUtils.escapeHtml4(checkedOutDocument.getCheckOutPath().toLowerCase()) + "");
						out.println("<small><span class=\"label label-success tip\" data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Document ID\">"+checkedOutDocument.getDocumentId()+"</span>&nbsp;");
						out.println("<span class=\"label label-default tip\" data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Revision ID\">"+checkedOutDocument.getRevisionId()+"</span>&nbsp;");
						out.println("</small></h3>");
						out.println("<h5><i class=\"fa fa-folder-open\"></i> "+  checkedOutDocument.getDocumentClass().getClassName()+ "</h5>");
						out.println("<h5><i class=\"fa fa-user\"></i> "+  checkedOutDocument.getUserName() + " <i class=\"fa fa-clock-o\"></i> " + StringHelper.formatDate(checkedOutDocument.getCheckOutDate()) + " ,  " + StringHelper.getFriendlyDateTime( checkedOutDocument.getCheckOutDate())+"  </h5>");
						out.println("</div>");
						out.println("<div class=\"panel-footer\">");
						out.println("<a href=\"/cpanel/cancelcheckoutadmin?documentid="+checkedOutDocument.getDocumentId()+"&revisionid="+checkedOutDocument.getRevisionId()+"\" class=\"confirm\" title=\"Are you sure, you want to cancel this checkout?\">Cancel Checkout</a>");
						out.println("</div>"); //panel-footer
						out.println("</div>"); //panel
						
						out.println("</div>");//col-sm-6
						if(count % 2 == 0){
							out.println("</div><div class=\"row\">");//row
						}
					}// for
					out.println("</div>");
				}else{
					printInfoDismissable("There are no documents checked out");
				}
				
			}
			

			out.println("<form action=\"/cpanel/managecheckouts\" method=\"post\" id=\"frmManageCheckouts\" class=\"form-horizontal\">");
			out.println("<legend><h4>View Checked Out Documents</h4></legend>");
			out.println("<div class=\"form-group\">");
			out.println("<div class=\"col-sm-offset-3 col-sm-9\">");
			out.println("<p>Fields marked with <span style='color:red'>*</span> are mandatory</p>");
			out.println("</div>");
			out.println("</div>");

			out.println("<div class=\"form-group\">");
			out.println("<label for=\"cmbDocumentClass\" class=\"col-sm-3 control-label\">Document Class <span style='color:red'>*</span></label>");
			out.println("<div class=\"col-sm-9\">");
			out.println("<select name=\"cmbDocumentClass\" class=\"form-control required\">");
			out.println("<option value=\"ALL\">All Document Classes</option>");
			String selected = "";
			for(DocumentClass documentClass : documentClassList){
				selected = "";
				if(documentClass.getClassName().equalsIgnoreCase(selectedDocumentClass)){
					selected = " selected";
				}
				out.println("<option value=\""+documentClass.getClassId()+"\"" +selected+ ">"+documentClass.getClassName()+"</option>");
			}
			out.println("</select>");
			out.println("</div>");
			out.println("</div>");

			out.println("<div class=\"form-group\">");
			out.println("<label for=\"txtListDescription\" class=\"col-sm-3 control-label\">User <span style='color:red'>*</span></label>");
			out.println("<div class=\"col-sm-9\">");
			out.println("<select name=\"cmbUser\" class=\"form-control required\">");
			out.println("<option value=\"ALL\">All Users</option>");
			for(User user : userList){
				selected = "";
				if(user.getUserName().equalsIgnoreCase(selectedUser)){
					selected = " selected";
				}
				out.println("<option value=\""+user.getUserName()+"\"" +selected+ ">"+user.getUserName()+" ("+ user.getRealName()+")</option>");
			}
			out.println("</select>");
			out.println("</div>");
			out.println("</div>");

			out.println("<hr/>");
			out.println("<div class=\"form-group\">");
			out.println("<div class=\"col-sm-offset-3 col-sm-9\">");
			out.println("<input type=\"submit\"  name=\"btnSubmit\"  value=\"Submit\" class=\"btn btn-sm btn-default\">");
			out.println("</div>");
			out.println("</div>");

			out.println("</form>");
			out.println("</div>");
			out.println("</div>");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}