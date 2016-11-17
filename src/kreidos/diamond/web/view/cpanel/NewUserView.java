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

package kreidos.diamond.web.view.cpanel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kreidos.diamond.constants.HTTPConstants;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.web.view.WebPageTemplate;
import kreidos.diamond.web.view.WebView;


/**
 * @author Rahul Kubadia
 *
 */

public class NewUserView extends WebView {

	public NewUserView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}
	public void render() throws Exception{
		WebPageTemplate template = new WebPageTemplate(request,response);
		template.generateHeader();
		printNewUserForm();
		template.generateFooter();
	}
	private void printBreadCrumbs() throws Exception{
		out.println("<ol class=\"breadcrumb\">");
		out.println("<li><a href=\"/cpanel\">Control Panel</a></li>");
		out.println("<li><a href=\"/cpanel/users\">Manage Users</a></li>");
		out.println("<li class=\"active\">Add User</li>");
		out.println("</ol>");
	}
	
	private void printNewUserForm() throws Exception{
		printBreadCrumbs();
		if(request.getAttribute(HTTPConstants.REQUEST_ERROR) != null){
			printError((String)request.getAttribute(HTTPConstants.REQUEST_ERROR));
		}
		if(request.getAttribute(HTTPConstants.REQUEST_MESSAGE) != null){
			printSuccess((String)request.getAttribute(HTTPConstants.REQUEST_MESSAGE));
		}
		try {
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\"><h4><i class=\"fa fa-user fa-lg\"></i> Add User</h4></div>");
			out.println("<div class=\"panel-body\">");
			out.println("<form action=\"/cpanel/newuser\" method=\"post\" id=\"frmNewUser\" class=\"form-horizontal\">");
			out.println("<div class=\"form-group\">");
			out.println("<div class=\"col-sm-offset-3 col-sm-9\">");
			out.println("<p>Fields marked with <span style='color:red'>*</span> are mandatory</p>");
			out.println("</div>");
			out.println("</div>");
			out.println("<div class=\"form-group\">");
			out.println("<label for=\"txtUserName\" class=\"col-sm-3 control-label\">User Name <span style='color:red'>*</span></label>");
			out.println("<div class=\"col-sm-9\">");
			out.println("<input type=\"text\" id=\"txtUserName\" name=\"txtUserName\" class=\"required form-control\" title=\"Please enter valid User Name\" maxlength=\"15\">");
			out.println("</div>");
			out.println("</div>");
			
			out.println("<div class=\"form-group\">");
			out.println("<label for=\"txtRealName\" class=\"col-sm-3 control-label\">Real Name <span style='color:red'>*</span></label>");
			out.println("<div class=\"col-sm-9\">");
			out.println("<input type=\"text\" id=\"txtRealName\" name=\"txtRealName\" class=\"required form-control\" title=\"Please enter Real Name\" maxlength=\"50\">");
			out.println("</div>");
			out.println("</div>");

			out.println("<div class=\"form-group\">");
			out.println("<label for=\"txtPassWord\" class=\"col-sm-3 control-label\">Password <span style='color:red'>*</span></label>");
			out.println("<div class=\"col-sm-9\">");
			out.println("<input type=\"password\" id=\"txtPassWord\" name=\"txtPassWord\" class=\"required form-control complexPassword\" minlength=\"8\" maxlength=\"50\">");
			out.println("</div>");
			out.println("</div>");

			out.println("<div class=\"form-group\">");
			out.println("<label for=\"txtConfirmPassWord\" class=\"col-sm-3 control-label\">Confirm Password <span style='color:red'>*</span></label>");
			out.println("<div class=\"col-sm-9\">");
			out.println("<input type=\"password\" id=\"txtConfirmPassWord\" name=\"txtConfirmPassWord\" class=\"form-control required\" equalTo=\"#txtPassWord\" title=\"Passwords must match\" maxlength=\"50\">");
			out.println("</div>");
			out.println("</div>");

			out.println("<div class=\"form-group\">");
			out.println("<label for=\"txtUserEmail\" class=\"col-sm-3 control-label\">Email ID <span style='color:red'>*</span></label>");
			out.println("<div class=\"col-sm-9\">");
			out.println("<input type=\"text\" id=\"txtUserEmail\" name=\"txtUserEmail\"  class=\"required form-control email\" title=\"Please enter a valid Email ID\" maxlength=\"50\">");
			out.println("</div>");
			out.println("</div>");

			out.println("<div class=\"form-group\">");
			out.println("<label for=\"txtDescription\" class=\"col-sm-3 control-label\">User Description <span style='color:red'>*</span> </label>");
			out.println("<div class=\"col-sm-9\">");
			out.println("<input type=\"text\" id=\"txtDescription\" name=\"txtDescription\" maxlength=\"50\" class=\"form-control required\" title=\"Please enter User Description\" value=\"\">");
			out.println("</div>");
			out.println("</div>");

			// access control fields
			out.println("<div class=\"form-group\">");
			out.println("<label for=\"radUserType\" class=\"col-sm-3 control-label\">User Type</label>");
			out.println("<div class=\"btn-group col-sm-9\" data-toggle=\"buttons\">");
			out.println("<label class=\"btn btn-default  btn-sm active\">");
			out.println("<input type=\"radio\" id=\"radUserType1\" name=\"radUserType\" value=\""+User.USER_TYPE_ADMIN+"\" checked>Administrator");
			out.println("</label>");
			out.println("<label class=\"btn  btn-sm btn-default\">");
			out.println("<input type=\"radio\" id=\"radUserType2\" name=\"radUserType\"  value=\""+User.USER_TYPE_USER+"\">User");
			out.println("</label>");
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
			out.println("</div>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}