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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import com.primeleaf.krystal.constants.HTTPConstants;
import com.primeleaf.krystal.model.vo.HitList;
import com.primeleaf.krystal.util.StringHelper;
import com.primeleaf.krystal.web.view.WebPageTemplate;
import com.primeleaf.krystal.web.view.WebView;


/**
 * @author Rahul Kubadia
 *
 */
public class MyProfileView extends WebView {

	public MyProfileView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}

	public void render() throws Exception{
		WebPageTemplate template = new WebPageTemplate(request,response);
		template.generateHeader();
		printMyProfle();
		template.generateFooter();
	}
	private void printBreadCrumbs() throws Exception{
		out.println("<ol class=\"breadcrumb\">");
		out.println("<li><a href=\"/console\">My Workspace</a></li>");
		out.println("<li class=\"active\">My Profile</li>");
		out.println("</ol>");
	}
	
	private void printMyProfle() throws Exception{
		printBreadCrumbs();

		if(request.getAttribute(HTTPConstants.REQUEST_ERROR) != null){
			printErrorDismissable((String) request.getAttribute(HTTPConstants.REQUEST_ERROR));
		}
		if(request.getAttribute(HTTPConstants.REQUEST_MESSAGE) != null){
			printSuccessDismissable((String) request.getAttribute(HTTPConstants.REQUEST_MESSAGE));
		}
		String showThumbNail = loggedInUser.getShowThumbNail();
		String checkOutDirectory=StringEscapeUtils.escapeHtml4(loggedInUser.getCheckOutPath());


		out.println("<ul class=\"nav nav-tabs\">");
		out.println("<li class=\"active\"><a href=\"#profile\" data-toggle=\"tab\">My Profile</a></li>");
		out.println("<li class=\"\"><a href=\"#preferences\" data-toggle=\"tab\">Preferences</a></li>");
		out.println("<li class=\"\"><a href=\"#changepassword\" data-toggle=\"tab\">Change Password</a></li>");
		out.println("</ul>");

		out.println("<div class=\"tab-content\">"); 

		out.println("<div class=\"tab-pane fade in active\" id=\"profile\">");
		out.println("<p>&nbsp;</p>");
		out.println("<form action=\"/console/updateprofilepicture\" method=\"post\" id=\"frmMyProfile\" class=\"form-horizontal\" enctype=\"multipart/form-data\">");

		out.println("<div class=\"form-group\">");
		out.println("<label class=\"col-sm-3 control-label\">User Name </label>");
		out.println("<div class=\"col-sm-9\">");
		out.println("<p class=\"form-control-static\">"+loggedInUser.getUserName()+"</p>");
		out.println("</div>");
		out.println("</div>");

		out.println("<div class=\"form-group\">");
		out.println("<label class=\"col-sm-3 control-label\">Real Name </label>");
		out.println("<div class=\"col-sm-9\">");
		out.println("<p class=\"form-control-static\">"+StringEscapeUtils.escapeHtml4(loggedInUser.getRealName())+"</p>");
		out.println("</div>");
		out.println("</div>");
		
		
		out.println("<div class=\"form-group\">");
		out.println("<label class=\"col-sm-3 control-label\">My Profile Picture</label>");
		out.println("<div class=\"col-sm-9\">");
		out.println("<div class=\"profile\">");
		out.println("<a href=\"javascript:void(0);\" id=\"profilePic\">");
		out.println("<img src=\"/console/profilepicture?username="+loggedInUser.getUserName()+"\" class=\"thumbnail\"/>");
		out.println("<span class=\"text-content\"><span id=\"profilepiccontent\">Change Profile Picture</span></span>");
		out.println("</a>");
		out.println("</div>");
		out.println("<input type=\"file\" name=\"profilepicture\" id=\"profilepicture\" class=\"required\" title=\"Select Profile Picture\" style=\"display:none;\">");
		out.println("</div>");
		out.println("</div>");

		out.println("<div class=\"form-group\">");
		out.println("<label class=\"col-sm-3 control-label\">Email ID</label>");
		out.println("<div class=\"col-sm-9\">");
		out.println("<p class=\"form-control-static\">"+loggedInUser.getUserEmail()+"</p>");
		out.println("</div>");
		out.println("</div>");
		
		String lastLogin = loggedInUser.getLastLoginDate()!=null?StringHelper.formatDate(loggedInUser.getLastLoginDate()):"0000-00-00 00:00:00";

		out.println("<div class=\"form-group\">");
		out.println("<label class=\"col-sm-3 control-label\">Last Login Date</label>");
		out.println("<div class=\"col-sm-9\">");
		out.println("<p class=\"form-control-static\">"+lastLogin+"</p>");
		out.println("</div>");
		out.println("</div>");

		out.println("</form>");
		out.println("</div>");


		out.println("<div class=\"tab-pane fade in\" id=\"preferences\">");
		out.println("<p>&nbsp;</p>");
		out.println("<div id=\"resultPreferences\"></div>");
		out.println("<form action=\"/console/preferences\" method=\"post\" id=\"frmPreferences\" form-type=\"ajax\" datatarget=\"#resultPreferences\"  class=\"form-horizontal\">");
		out.println("<div class=\"row\">");
		out.println("<div class=\"col-md-6 col-sm-12\">");
		out.println("<div class=\"panel panel-default\">");
		out.println("<div class=\"panel-heading\"><h4>Hitlist Preferences</h4></div>");
		out.println("<div class=\"panel-body\">");
		out.println("<div class=\"form-group\">");
		out.println("<label for=\"cmbHitListSize\" class=\"col-sm-3 control-label\">No of Hits per page</label>");
		out.println("<div class=\"col-sm-9\">");
		out.println("<select class=\"form-control\" name=\"cmbHitListSize\">");
		out.println("<option value=\"5\""); 	if(loggedInUser.getHitlistSize() == 5 ){ out.print(" selected");} out.println(">5</option>");
		out.println("<option value=\"10\""); 	if(loggedInUser.getHitlistSize() == 10 ){ out.print(" selected");} out.println(">10</option>");
		out.println("<option value=\"25\""); 	if(loggedInUser.getHitlistSize() == 25 ){ out.print(" selected");} out.println(">25</option>");
		out.println("<option value=\"50\""); 	if(loggedInUser.getHitlistSize() == 50 ){ out.print(" selected");} out.println(">50</option>");
		out.println("<option value=\"100\""); 	if(loggedInUser.getHitlistSize() == 100 ){ out.print(" selected");} out.println(">100</option>");
		out.println("</select>");
		out.println("</div>");
		out.println("</div>");

		out.println("<div class=\"form-group\">");
		out.println("<label for=\"txtCheckOutDir\" class=\"col-sm-3 control-label\">Checkout Directory <span style='color:red'>*</span></label>");
		out.println("<div class=\"col-sm-9\">");
		out.println("<input type=\"text\" maxlength=\"250\" name=\"txtCheckOutDir\" id=\"txtCheckOutDir\" class=\"form-control required\" autocomplete=\"off\" placeholder=\"Checkout Directory\" title=\"Please enter checkout directory path\" value=\""+checkOutDirectory+"\">");
		out.println("</div>");
		out.println("</div>");

		out.println("<div class=\"form-group\">");
		out.println("<label for=\"radCreatedOn\" class=\"col-sm-3 col-xs-2 control-label\">Created On</label>");
		out.println("<div class=\"btn-group col-sm-3 col-xs-4\" data-toggle=\"buttons\">");
		out.println("<label class=\"btn btn-xs btn-default "); if(loggedInUser.getMetaPreferences().isCreatedVisible()) {out.print(" active");} out.print("\">");
		out.println("<input type=\"radio\" id=\"radCreatedOn1\" name=\""+HitList.META_CREATED+"\" value=\"TRUE\"");if(loggedInUser.getMetaPreferences().isCreatedVisible()) {out.print(" checked");}out.print(">Yes");
		out.println("</label>");
		out.println("<label class=\"btn btn-xs btn-default "); if(!loggedInUser.getMetaPreferences().isCreatedVisible()) { out.print(" active");} out.print("\">");
		out.println("<input type=\"radio\" id=\"radCreatedOn2\" name=\""+HitList.META_CREATED+"\"  value=\"FALSE\"");if(!loggedInUser.getMetaPreferences().isCreatedVisible()) {out.print(" checked");}out.print(">No");
		out.println("</label>");
		out.println("</div>");

		out.println("<label for=\"radActive\" class=\"col-sm-3 col-xs-2 control-label\">Created By</label>");
		out.println("<div class=\"btn-group col-sm-3 col-xs-4\" data-toggle=\"buttons\">");
		out.println("<label class=\"btn btn-xs btn-default "); if(loggedInUser.getMetaPreferences().isCreatedByVisible()) {out.print(" active");} out.print("\">");
		out.println("<input type=\"radio\" id=\"radActive1\" name=\""+HitList.META_CREATEDBY+"\" value=\"TRUE\"");if(loggedInUser.getMetaPreferences().isCreatedByVisible()) {out.print(" checked");}out.print(">Yes");
		out.println("</label>");
		out.println("<label class=\"btn btn-xs btn-default "); if(!loggedInUser.getMetaPreferences().isCreatedByVisible()) { out.print(" active");} out.print("\">");
		out.println("<input type=\"radio\" id=\"radActive2\" name=\""+HitList.META_CREATEDBY+"\"  value=\"FALSE\"");if(!loggedInUser.getMetaPreferences().isCreatedByVisible()) {out.print(" checked");}out.print(">No");
		out.println("</label>");
		out.println("</div>");
		
		out.println("</div>");

		out.println("<div class=\"form-group\">");
		out.println("<label for=\"modifiedOn\" class=\"col-sm-3 col-xs-2 control-label\">Last Modified On</label>");
		out.println("<div class=\"btn-group col-sm-3 col-xs-4\" data-toggle=\"buttons\">");
		out.println("<label class=\"btn btn-xs btn-default "); if(loggedInUser.getMetaPreferences().isModifiedVisible()) {out.print(" active");} out.print("\">");
		out.println("<input type=\"radio\" id=\"modifiedOn1\" name=\""+HitList.META_MODIFIED+"\" value=\"TRUE\"");if(loggedInUser.getMetaPreferences().isModifiedVisible()) {out.print(" checked");}out.print(">Yes");
		out.println("</label>");
		out.println("<label class=\"btn btn-xs btn-default "); if(!loggedInUser.getMetaPreferences().isModifiedVisible()) { out.print(" active");} out.print("\">");
		out.println("<input type=\"radio\" id=\"modifiedOn2\" name=\""+HitList.META_MODIFIED+"\"  value=\"FALSE\"");if(!loggedInUser.getMetaPreferences().isModifiedVisible()) {out.print(" checked");}out.print(">No");
		out.println("</label>");
		out.println("</div>");

		out.println("<label for=\"modifiedBy\" class=\"col-sm-3 col-xs-2 control-label\">Last Modified By</label>");
		out.println("<div class=\"btn-group col-sm-3 col-xs-4\" data-toggle=\"buttons\">");
		out.println("<label class=\"btn btn-xs btn-default "); if(loggedInUser.getMetaPreferences().isModifiedByVisible()) {out.print(" active");} out.print("\">");
		out.println("<input type=\"radio\" id=\"modifiedBy1\" name=\""+HitList.META_MODIFIEDBY+"\" value=\"TRUE\"");if(loggedInUser.getMetaPreferences().isModifiedByVisible()) {out.print(" checked");}out.print(">Yes");
		out.println("</label>");
		out.println("<label class=\"btn btn-xs btn-default "); if(!loggedInUser.getMetaPreferences().isModifiedByVisible()) { out.print(" active");} out.print("\">");
		out.println("<input type=\"radio\" id=\"modifiedBy2\" name=\""+HitList.META_MODIFIEDBY+"\"  value=\"FALSE\"");if(!loggedInUser.getMetaPreferences().isModifiedByVisible()) {out.print(" checked");}out.print(">No");
		out.println("</label>");
		out.println("</div>");
		out.println("</div>");

		
		out.println("<div class=\"form-group\">");
		out.println("<label for=\"fileSize\" class=\"col-sm-3 col-xs-2 control-label\">File Size</label>");
		out.println("<div class=\"btn-group col-sm-3 col-xs-4\" data-toggle=\"buttons\">");
		out.println("<label class=\"btn btn-xs btn-default "); if(loggedInUser.getMetaPreferences().isFileSizeVisible()) {out.print(" active");} out.print("\">");
		out.println("<input type=\"radio\" id=\"fileSize1\" name=\""+HitList.META_LENGTH+"\" value=\"TRUE\"");if(loggedInUser.getMetaPreferences().isFileSizeVisible()) {out.print(" checked");}out.print(">Yes");
		out.println("</label>");
		out.println("<label class=\"btn btn-xs btn-default "); if(!loggedInUser.getMetaPreferences().isFileSizeVisible()) { out.print(" active");} out.print("\">");
		out.println("<input type=\"radio\" id=\"fileSize2\" name=\""+HitList.META_LENGTH+"\"  value=\"FALSE\"");if(!loggedInUser.getMetaPreferences().isFileSizeVisible()) {out.print(" checked");}out.print(">No");
		out.println("</label>");
		out.println("</div>");
		out.println("</div>");


		out.println("<div class=\"form-group\">");
		out.println("<label for=\"documentId\" class=\"col-sm-3 col-xs-2 control-label\">Document ID</label>");
		out.println("<div class=\"btn-group col-sm-3 col-xs-4\" data-toggle=\"buttons\">");
		out.println("<label class=\"btn btn-xs btn-default "); if(loggedInUser.getMetaPreferences().isDocumentIdVisible()) {out.print(" active");} out.print("\">");
		out.println("<input type=\"radio\" id=\"documentId1\" name=\""+HitList.META_DOCUMENTID+"\" value=\"TRUE\"");if(loggedInUser.getMetaPreferences().isDocumentIdVisible()) {out.print(" checked");}out.print(">Yes");
		out.println("</label>");
		out.println("<label class=\"btn btn-xs btn-default "); if(!loggedInUser.getMetaPreferences().isDocumentIdVisible()) { out.print(" active");} out.print("\">");
		out.println("<input type=\"radio\" id=\"documentId2\" name=\""+HitList.META_DOCUMENTID+"\"  value=\"FALSE\"");if(!loggedInUser.getMetaPreferences().isDocumentIdVisible()) {out.print(" checked");}out.print(">No");
		out.println("</label>");
		out.println("</div>");

		out.println("<label for=\"revisionId\" class=\"col-sm-3 col-xs-2 control-label\">Revision ID</label>");
		out.println("<div class=\"btn-group col-sm-3 col-xs-4\" data-toggle=\"buttons\">");
		out.println("<label class=\"btn btn-xs btn-default "); if(loggedInUser.getMetaPreferences().isRevisionIdVisible()) {out.print(" active");} out.print("\">");
		out.println("<input type=\"radio\" id=\"revisionId1\" name=\""+HitList.META_REVISIONID+"\" value=\"TRUE\"");if(loggedInUser.getMetaPreferences().isRevisionIdVisible()) {out.print(" checked");}out.print(">Yes");
		out.println("</label>");
		out.println("<label class=\"btn btn-xs btn-default "); if(!loggedInUser.getMetaPreferences().isRevisionIdVisible()) { out.print(" active");} out.print("\">");
		out.println("<input type=\"radio\" id=\"revisionId2\" name=\""+HitList.META_REVISIONID+"\"  value=\"FALSE\"");if(!loggedInUser.getMetaPreferences().isRevisionIdVisible()) {out.print(" checked");}out.print(">No");
		out.println("</label>");
		out.println("</div>");
		out.println("</div>");

		out.println("<div class=\"form-group\">");
		out.println("<label for=\"expiryOn\" class=\"col-sm-3 col-xs-2 control-label\">Expiry On</label>");
		out.println("<div class=\"btn-group col-sm-3 col-xs-4\" data-toggle=\"buttons\">");
		out.println("<label class=\"btn btn-xs btn-default "); if(loggedInUser.getMetaPreferences().isExpiryOnVisible()) {out.print(" active");} out.print("\">");
		out.println("<input type=\"radio\" id=\"expiryOn1\" name=\""+HitList.META_EXPIRYON+"\" value=\"TRUE\"");if(loggedInUser.getMetaPreferences().isExpiryOnVisible()) {out.print(" checked");}out.print(">Yes");
		out.println("</label>");
		out.println("<label class=\"btn btn-xs btn-default "); if(!loggedInUser.getMetaPreferences().isExpiryOnVisible()) { out.print(" active");} out.print("\">");
		out.println("<input type=\"radio\" id=\"expiryOn2\" name=\""+HitList.META_EXPIRYON+"\"  value=\"FALSE\"");if(!loggedInUser.getMetaPreferences().isExpiryOnVisible()) {out.print(" checked");}out.print(">No");
		out.println("</label>");
		out.println("</div>");

		out.println("<label for=\"radThumbNail\" class=\"col-sm-3 col-xs-2 control-label\">Show Thumbnail in Viewer</label>");
		out.println("<div class=\"btn-group col-sm-3 col-xs-4\" data-toggle=\"buttons\">");
		out.println("<label class=\"btn btn-xs btn-default "); if("TRUE".equalsIgnoreCase(showThumbNail)) {out.print(" active");} out.print("\">");
		out.println("<input type=\"radio\" id=\"radThumbNail1\" name=\"radThumbNail\" value=\"TRUE\"");if( "TRUE".equalsIgnoreCase(showThumbNail)) {out.print(" checked");}out.print(">Yes");
		out.println("</label>");
		out.println("<label class=\"btn btn-xs btn-default "); if("FALSE".equalsIgnoreCase(showThumbNail)) { out.print(" active");} out.print("\">");
		out.println("<input type=\"radio\" id=\"radThumbNail2\" name=\"radThumbNail\"  value=\"FALSE\"");if("FALSE".equalsIgnoreCase(showThumbNail)) {out.print(" checked");}out.print(">No");
		out.println("</label>");
		out.println("</div>");
		out.println("</div>");
		
		out.println("</div>");//panel-body
		out.println("</div>");//panel
		out.println("</div>");//col-md-6
		
		out.println("</div>");//row
		out.println("<hr/>");
		out.println("<div class=\"form-group\">");
		out.println("<div class=\"col-sm-offset-3 col-sm-9\">");
		out.println("<input class=\"btn btn-sm btn-default\" type=\"submit\"  value=\"Submit\" name=\"btnSubmit\">&nbsp;");
		out.println("</div>");
		out.println("</div>");

		out.println("</form>");
		out.println("</div>");


		out.println("<div class=\"tab-pane fade in\" id=\"changepassword\">");
		out.println("<p>&nbsp;</p>");
		out.println("<div id=\"resultChangePassword\"></div>");
		out.println("<form action=\"/console/changepassword\" method=\"post\" id=\"frmChangePassword\" form-type=\"ajax\" datatarget=\"#resultChangePassword\" class=\"form-horizontal\">");
		out.println("<div class=\"form-group\">");
		out.println("<label for=\"txtOldPassword\" class=\"col-sm-3 control-label\">Current Password <span style='color:red'>*</span></label>");
		out.println("<div class=\"col-sm-9\">");
		out.println("<input type=\"password\" maxlength=\"30\" name=\"txtOldPassword\" id=\"txtOldPassword\" class=\"form-control required\" autocomplete=\"off\" placeholder=\"Current Password\" title=\"Please enter password\">");
		out.println("</div>");
		out.println("</div>");
		out.println("<div class=\"form-group\">");
		out.println("<label for=\"txtNewPassword\" class=\"col-sm-3 control-label\">New Password <span style='color:red'>*</span></label>");
		out.println("<div class=\"col-sm-9\">");
		out.println("<input type=\"password\" maxlength=\"30\" name=\"txtNewPassword\" id=\"txtNewPassword\" class=\"form-control required complexPassword\" placeholder=\"New Password\" minlength=\"8\" autocomplete=\"off\">");
		out.println("</div>");
		out.println("</div>");

		out.println("<div class=\"form-group\">");
		out.println("<label for=\"txtConfirmPassword\" class=\"col-sm-3 control-label\">Confirm Password <span style='color:red'>*</span></label>");
		out.println("<div class=\"col-sm-9\">");
		out.println("<input  type=\"password\" maxlength=\"30\" name=\"txtConfirmPassword\" id=\"txtConfirmPassword\" class=\"form-control required\" equalTo= \"#txtNewPassword\" placeholder=\"Confirm Password\" autocomplete=\"off\" title=\"Password must match\">");
		out.println("</div>");
		out.println("</div>");

		out.println("<hr/>");
		out.println("<div class=\"form-group\">");
		out.println("<div class=\"col-sm-offset-3 col-sm-9\">");
		out.println("<input class=\"btn btn-sm btn-default\" type=\"submit\"  value=\"Submit\" name=\"btnSubmit\">&nbsp;");
		out.println("</div>");
		out.println("</div>");
		out.println("</form>");
		out.println("</div>");
		out.println("</div>");
	}
}

