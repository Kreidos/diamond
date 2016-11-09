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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import com.primeleaf.krystal.model.vo.User;
import com.primeleaf.krystal.web.view.WebPageTemplate;
import com.primeleaf.krystal.web.view.WebView;

/**
 * @author Rahul Kubadia
 *
 */

public class EditUserView extends WebView {
	public EditUserView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}
	public void render() throws Exception{
		WebPageTemplate template = new WebPageTemplate(request,response);
		template.generateHeader();
		printEditUserForm();
		template.generateFooter();
	}
	private void printBreadCrumbs() throws Exception{
		out.println("<ol class=\"breadcrumb\">");
		out.println("<li><a href=\"/cpanel\">Control Panel</a></li>");
		out.println("<li><a href=\"/cpanel/users\">Manage Users</a></li>");
		out.println("<li class=\"active\">Edit User</li>");
		out.println("</ol>");
	}
	private void printEditUserForm() throws Exception{
		printBreadCrumbs();
		User user = (User)request.getAttribute("USER");
		try {
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\"><h4><i class=\"fa fa-user fa-lg\"></i> Edit User</h4></div>");
			out.println("<div class=\"panel-body\">");
			out.println("<form action=\"/cpanel/edituser\" method=\"post\" id=\"frmEditUser\" class=\"form-horizontal\"  accept-charset=\"utf-8\">");

			out.println("<div class=\"form-group\">");
			out.println("<div class=\"col-sm-offset-3 col-sm-9\">");
			out.println("<p>Fields marked with <span style='color:red'>*</span> are mandatory</p>");
			out.println("</div>");
			out.println("</div>");

			out.println("<div class=\"form-group\">");
			out.println("<label for=\"txtUserName\" class=\"col-sm-3 control-label\">User Name <span style='color:red'>*</span></label>");
			out.println("<div class=\"col-sm-9\">");
			out.println("<input type=\"text\" id=\"txtUserName\" name=\"txtUserName\" class=\"required form-control\" title=\"Please enter User Name\" maxlength=\"15\" readonly value=\""+StringEscapeUtils.escapeHtml4(user.getUserName())+"\">");
			out.println("</div>");
			out.println("</div>");

			out.println("<div class=\"form-group\">");
			out.println("<label for=\"txtRealName\" class=\"col-sm-3 control-label\">Real Name <span style='color:red'>*</span></label>");
			out.println("<div class=\"col-sm-9\">");
			out.println("<input type=\"text\" id=\"txtRealName\" name=\"txtRealName\" class=\"required form-control\" title=\"Please enter Real Name\" maxlength=\"50\" value=\""+StringEscapeUtils.escapeHtml4(user.getRealName())+"\">");
			out.println("</div>");
			out.println("</div>");

			out.println("<div class=\"form-group\">");
			out.println("<label for=\"txtUserEmail\" class=\"col-sm-3 control-label\">Email ID <span style='color:red'>*</span></label>");
			out.println("<div class=\"col-sm-9\">");
			out.println("<input type=\"text\" id=\"txtUserEmail\" name=\"txtUserEmail\"  class=\"required form-control email\" title=\"Please enter valid Email ID\" maxlength=\"50\"  value=\""+StringEscapeUtils.escapeHtml4(user.getUserEmail())+"\">");
			out.println("</div>");
			out.println("</div>");

			out.println("<div class=\"form-group\">");
			out.println("<label for=\"txtDescription\" class=\"col-sm-3 control-label\">Description <span style='color:red'>*</span> </label>");
			out.println("<div class=\"col-sm-9\">");
			out.println("<input type=\"text\" id=\"txtDescription\" name=\"txtDescription\" maxlength=\"50\" class=\"form-control required\" title=\"Please enter Description\" value=\""+StringEscapeUtils.escapeHtml4(user.getUserDescription())+"\">");
			out.println("</div>");
			out.println("</div>");


			out.println("<div class=\"form-group\">");
			out.println("<label for=\"radActive\" class=\"col-sm-3 control-label\">Active?</label>");
			out.println("<div class=\"btn-group col-sm-9\" data-toggle=\"buttons\">");
			out.println("<label class=\"btn btn-sm btn-default "); if(user.isActive()) {out.print(" active");} out.print("\">");
			out.println("<input type=\"radio\" id=\"radActive1\" name=\"radActive\" value=\"Y\"");if( user.isActive()) {out.print(" checked");}out.print(">Yes");
			out.println("</label>");
			out.println("<label class=\"btn  btn-sm btn-default "); if(!user.isActive()) { out.print(" active");} out.print("\">");
			out.println("<input type=\"radio\" id=\"radActive2\" name=\"radActive\"  value=\"N\"");if(!user.isActive()) {out.print(" checked");}out.print(">No");
			out.println("</label>");
			out.println("</div>");
			out.println("</div>");


			out.println("<div class=\"form-group\">");
			out.println("<label for=\"radUserType\" class=\"col-sm-3 control-label\">User Type</label>");
			out.println("<div class=\"btn-group col-sm-9\" data-toggle=\"buttons\">");
			out.println("<label class=\"btn btn-sm btn-default "); if( User.USER_TYPE_ADMIN.equalsIgnoreCase(user.getUserType())) {out.print(" active");} out.print("\">");
			out.println("<input type=\"radio\" id=\"radUserType1\" name=\"radUserType\"  value=\""+User.USER_TYPE_ADMIN+"\"");if( User.USER_TYPE_ADMIN.equalsIgnoreCase(user.getUserType())) {out.print(" checked");}out.print(">Administrator");
			out.println("</label>");
			out.println("<label class=\"btn btn-sm btn-default ");	if( User.USER_TYPE_USER.equalsIgnoreCase(user.getUserType())) { out.print(" active");} out.print("\">");
			out.println("<input type=\"radio\" id=\"radUserType2\" name=\"radUserType\"   value=\""+User.USER_TYPE_USER+"\"");	if( User.USER_TYPE_USER.equalsIgnoreCase(user.getUserType())) {out.print(" checked");}out.print(">User");  
			out.println("</label>");
			out.println("</div>");
			out.println("</div>");


			out.println("<hr/>");
			out.println("<div class=\"form-group\">");
			out.println("<div class=\"col-sm-offset-3 col-sm-9\">");
			out.println("<input type=\"hidden\" name=\"userid\" value=\""+ user.getUserId() + "\">");
			out.println("<input type=\"submit\"  name=\"btnSubmit\"  value=\"Submit\" class=\"btn  btn-sm btn-default\">");
			out.println("</div>");
			out.println("</div>");

			out.println("</form>");
			
			out.println("</div>");
			out.println("</div>");
			out.println("</div>");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}