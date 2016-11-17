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
import com.primeleaf.krystal.model.vo.User;
import com.primeleaf.krystal.web.view.WebPageTemplate;
import com.primeleaf.krystal.web.view.WebView;

/**
 * @author Rahul Kubadia
 *
 */

public class UsersView extends WebView {

	public UsersView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}
	public void render() throws Exception{
		WebPageTemplate template = new WebPageTemplate(request,response);
		template.generateHeader();
		printUsers();
		template.generateFooter();
	}
	private void printBreadCrumbs() throws Exception{
		out.println("<ol class=\"breadcrumb\">");
		out.println("<li><a href=\"/cpanel\">Control Panel</a></li>");
		out.println("<li class=\"active\">Manage Users</li>");
		out.println("</ol>");
	}

	@SuppressWarnings("unchecked")
	private void printUsers() throws Exception{
		printBreadCrumbs();
		
		out.println("<div class=\"panel panel-default\">");
		out.println("<div class=\"panel-heading\">");
		out.println("<div class=\"row\">");
		out.println("<div class=\"col-xs-6\">");
		out.println("<h4><i class=\"fa fa-user fa-lg \"></i> Manage Users</h4>");
		out.println("</div>");
		out.println("<div class=\"col-xs-6 text-right\">");
		out.println("<h4><a href=\"/cpanel/newuser\">Add User</a></h4>");
		out.println("</div>");
		out.println("</div>");
		out.println("</div>");
		out.println("<div class=\"panel-body\">");

		if(request.getAttribute(HTTPConstants.REQUEST_ERROR) != null){
			printErrorDismissable((String)request.getAttribute(HTTPConstants.REQUEST_ERROR));
		}
		if(request.getAttribute(HTTPConstants.REQUEST_MESSAGE) != null){
			printSuccessDismissable((String)request.getAttribute(HTTPConstants.REQUEST_MESSAGE));
		}

		try {
			ArrayList<User> userList= (ArrayList<User>)request.getAttribute("USERLIST");
			int count=0;
			if(userList.size() > 0 ){
				out.println("<div class=\"row\">");
				for (User user:userList) {
					count++;
					out.println("<div class=\"col-sm-3\">");
					out.println("<div class=\"panel panel-default\">");
					out.println("<div class=\"panel-body\">");
					out.println("<h4>");
					out.println(user.getUserName());
					if(user.isLoggedIn()){
						out.println("<i class=\"fa fa-check text-success\"></i> ");
					}
					out.println("</h4>");
					out.println("<h5>"+StringEscapeUtils.escapeHtml4(user.getRealName())+"</h5>");
					out.println("<p><i>Email ID :" + StringEscapeUtils.escapeHtml4(user.getUserEmail()) + "</i></p>");
					out.println("</div>"); //panel-body
					out.println("<div class=\"panel-footer\">");
					out.println("<a href=\""+HTTPConstants.BASEURL+"/cpanel/edituser?userid="+user.getUserId()+"\">Edit</a>");
					out.println(" | <a href=\""+HTTPConstants.BASEURL+"/cpanel/changeuserpassword?userid="+user.getUserId()+"\"  title=\"Change Password\" data-toggle=\"modal\" data-target=\"#changePasswordModal\">Change Password</a>");
					if(user.getUserId() != 1 && ! user.isLoggedIn() && loggedInUser.getUserId() != user.getUserId()){
						out.println(" | <a href=\""+HTTPConstants.BASEURL+"/cpanel/deleteuser?userid="+user.getUserId()+"\"  title=\"Are you sure, you want to delete this user?\" class=\"confirm\">Delete</a>");
					}
					out.println("</div>"); //panel-footer
					
					out.println("</div>"); //panel
					out.println("</div>");//col-lg-4
					
					if(count % 4 == 0){
						out.println("</div><div class=\"row\">");//row
					}
				}// for
				out.println("</div>");
				printModal("changePasswordModal");
			}else{
				out.println("No users");
			}
			
			out.println("</div>");//panel-body
			out.println("</div>");//panel
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

