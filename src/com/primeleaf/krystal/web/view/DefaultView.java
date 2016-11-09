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

package com.primeleaf.krystal.web.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.primeleaf.krystal.constants.HTTPConstants;

/**
 * Author Rahul Kubadia
 */

/**
 * @author Rahul Kubadia
 *
 */
public class DefaultView extends WebView {

	public DefaultView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}

	public void render() throws Exception{
		WebPageTemplate template = new WebPageTemplate(request,response);
		template.generateHeader();
		printLoginForm();
		template.generateFooter();
	}

	private void printLoginForm() throws Exception{

		out.println("<form method=\"post\" action=\"/login\" name=\"frmLogin\" id=\"frmLogin\" role=\"form\">");
		out.println("<div class=\"row\">");

		out.println("<div class=\"col-lg-4 col-lg-offset-4 pre-login\">");

		out.println("<div class=\"panel panel-default\">");
		out.println("<div class=\"panel-heading\">");
		out.println("<h5><i class=\"fa fa-sign-in fa-fw fa-lg\"></i> Document Management Console - Login</h5>");
		out.println("</div>");

		out.println("<div class=\"panel-body\">");
		printLoginError();

		out.println("<div class=\"form-group\">");
		out.println("<div class=\"input-group\">");
		out.println("<input name=\"txtLoginId\" id=\"txtLoginId\" type=\"text\" class=\"form-control required\" autocomplete=\"off\" placeholder=\"Username  or Email Id\"  title=\"Please enter username or email\"></td>");
		out.println("<span class=\"input-group-addon\"><i class=\"fa fa-fw fa-lg fa-user\"></i></span>");
		out.println("</div>");
		out.println("</div>");

		out.println("<div class=\"form-group\">");
		out.println("<div class=\"input-group\">");
		out.println("<input name=\"txtPassword\" id=\"txtPassword\" type=\"password\"  class=\"form-control required\" autocomplete=\"off\" placeholder=\"Password\"  title=\"Please enter password\"></td>");
		out.println("<span class=\"input-group-addon\"><i class=\"fa fa-fw fa-lg fa-lock \"></i></span>");
		out.println("</div>");
		out.println("</div>");

		out.println("<div class=\"form-group text-right\">");
		out.println("<button type=\"submit\" class=\"btn btn-default btn-sm\" data-loading-text=\"Please wait...\" id=\"btnLogin\"> <i class=\"fa fa-lg fa-sign-in \"></i> Login</button>");
		out.println("</div>"); //form-group

		out.println("</div>"); //Panel Body
		out.println("</div>"); //Panel
		out.println("</div>"); //col-lg-4

		out.println("</div>"); //row
		out.println("</form>");


	}

	private void printLoginError() throws Exception{
		String loginError = (String)request.getAttribute(HTTPConstants.REQUEST_ERROR);
		if(loginError != null){
			String errorMessage="";
			if(loginError.equals(HTTPConstants.ERROR_INVALIDUSER)){
				errorMessage = "Invalid Login";
			}else if(loginError.equals(HTTPConstants.ERROR_TOOMANYUSER)){
				errorMessage ="Too many users";
			}else if(loginError.equals(HTTPConstants.ERROR_NOTANADMIN)){
				errorMessage = "Insufficient privileges";
			}else if(loginError.equals(HTTPConstants.ERROR_ALREADY_LOGGED_IN)){
				errorMessage = "User already logged-in";
			}else if(loginError.equals(HTTPConstants.ERROR_ACCESS_DENIED)){
				errorMessage = "Access denied";
			}else if(loginError.equals(HTTPConstants.ERROR_INACTIVEUSER)){
				errorMessage = "Inactive User";
			}else{
				errorMessage = "Unknown error. Please contact support@krystaldms.in";
			}
			out.println("<div class=\"alert alert-danger alert-dismissable\"><button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button> " + errorMessage + "</div>");
			request.removeAttribute(HTTPConstants.REQUEST_ERROR);
		}
	}
}