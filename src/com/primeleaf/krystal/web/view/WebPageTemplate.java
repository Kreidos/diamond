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
 * of Primeleaf Consulting (P) Ltd. (\"Confidential Information").  
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Primeleaf Consulting (P) Ltd.
 */
package com.primeleaf.krystal.web.view;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;

import com.primeleaf.krystal.constants.HTTPConstants;
import com.primeleaf.krystal.constants.ServerConstants;
import com.primeleaf.krystal.model.vo.User;


/**
 * @author Rahul Kubadia
 *
 */
public class WebPageTemplate {
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected HttpSession session;
	protected PrintWriter out;

	public WebPageTemplate (HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception{
		request = httpRequest;
		response =  httpResponse;
		out = httpResponse.getWriter();
		session = (HttpSession) request.getSession();
	} 

	public void generatePopupHeader() throws Exception{
		generateHead();
		out.println("<div class=\"container-fluid\">");
	}
	public void generatePopupFooter() throws Exception{
		out.println("</div>");
		out.println("</body>");
		out.println("</html>");
	}

	private void generateHead() throws Exception{
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>KRYSTAL DMS  " + ServerConstants.SERVER_VERSION + " - " + ServerConstants.SERVER_EDITION + "</title>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text-html; charset=utf-8\">");
		out.println("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge\">");
		out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
		out.println("<link rel=\"stylesheet\" href=\"/css/bootstrap.css\"/>");
		out.println("<link rel=\"stylesheet\" href=\"/css/bootstrap-dialog.css\"/>");
		out.println("<link rel=\"stylesheet\" href=\"/css/datepicker.css\"/>");
		out.println("<link rel=\"stylesheet\" href=\"/css/sticky-footer-navbar.css\"/>");
		out.println("<link rel=\"stylesheet\" href=\"/css/font-awesome.css\"/>");
		out.println("<link rel=\"stylesheet\" href=\"/css/colorbox.css\"/>");
		out.println("<link rel=\"stylesheet\" href=\"/css/morris.css\"/>");
		out.println("<link rel=\"stylesheet\" href=\"/css/krystal.css\"/>");
		out.println("<link rel=\"SHORTCUT ICON\" href=\"/images/favicon.ico\"/>");
		out.println("<script src=\"/js/jquery.js\"></script>");
		out.println("<script src=\"/js/jquery.validate.min.js\"></script>");
		out.println("<script src=\"/js/additional-methods.min.js\"></script>");
		out.println("<script src=\"/js/odometer.min.js\"></script>");
		out.println("<script src=\"/js/bootstrap-dialog.js\"></script>");
		out.println("<script src=\"/js/bootstrap-datepicker.js\"></script>");
		out.println("<script src=\"/js/bootstrap.js\"></script>");
		out.println("<script src=\"/js/typeahead.min.js\"></script>");
		out.println("<script src=\"/js/jquery.colorbox-min.js\"></script>");
		out.println("<script src=\"/js/jquery.highlight.js\"></script>");
		out.println("<script src=\"/js/morris.js\"></script>");
		out.println("<script src=\"/js/raphael-min.js\"></script>");
		out.println("<script src=\"/js/scripts.js\"></script>");
		out.println("<!--[if lt IE 9]>");
		out.println("<script src=\"/js/html5shiv.js\"></script>");
		out.println("<script src=\"/js/respond.min.js\"></script>");
		out.println("<![endif]-->");
		out.println("</head>");
		out.println("<body>");
	}

	public void generateHeader() throws Exception{
		HttpSession session = request.getSession();
		User loggedInUser = null;
		generateHead();
		out.println("<div id=\"wrap\">");
		String productHomePageURL = "http://www.krystaldms.in/community";
		if(session.getAttribute(HTTPConstants.SESSION_KRYSTAL) == null){
			out.println("<div class=\"navbar navbar-default navbar-inverse navbar-fixed-top\" role=\"navigation\">");
			out.println("<div class=\"container-fluid\">");
			out.println("<div class=\"navbar-header\">");
			out.println("<a class=\"navbar-brand\" href=\""+productHomePageURL+"\" target=\"_new\"><img src=\"/images/krystal.png\" width=\"32\" height=\"32\" align=\"left\"/>&nbsp;KRYSTAL DMS</a>");
			out.println("</div>");
			out.println("</div>");
			out.println("</div>");
		}else{
			out.println("<div class=\"navbar navbar-default navbar-inverse navbar-fixed-top\" role=\"navigation\">");
			out.println("<div class=\"container-fluid\">");
			out.println("<div class=\"navbar-header\">");
			out.println("<button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\".navbar-collapse\">");
			out.println("<span class=\"sr-only\">Toggle navigation</span>");
			out.println("<span class=\"icon-bar\"></span>");
			out.println("<span class=\"icon-bar\"></span>");
			out.println("<span class=\"icon-bar\"></span>");
			out.println("</button>");
			out.println("<a class=\"navbar-brand\" href=\""+productHomePageURL+"\" target=\"_new\"><img src=\"/images/krystal.png\" width=\"28\" height=\"28\" align=\"left\"/>&nbsp;KRYSTAL DMS</a>");
			out.println("</div>");

			loggedInUser = (User) session.getAttribute(HTTPConstants.SESSION_KRYSTAL);
			
			out.println("<div class=\"navbar-collapse collapse\">");
			out.println("<ul class=\"nav navbar-nav\">");
			String cssClass="";
			if(request.getServletPath().startsWith("/console")){
				cssClass ="active";
			}
			out.println("<li class=\""+cssClass+"\">");
			out.println("<a href=\"/console\" title=\"My Workspace\"><i class=\"fa fa-lg fa-home\"></i>  My Workspace</a>");
			out.println("</li>");
			
			cssClass="";

			if(loggedInUser.isAdmin()){
				if(request.getServletPath().startsWith("/cpanel")){
					cssClass ="active";
				}
				out.println("<li class=\""+cssClass+"\">");
				out.println("<a href=\"/cpanel\"  title=\"Control Panel\"><i class=\"fa fa-lg fa-cogs\"></i>  Control Panel</a>");
				out.println("</li>");
			}
			out.println("</ul>");

			out.println("<ul class=\"nav navbar-nav navbar-right\">");
			out.println("<li>");
			out.println("<a href=\"/console/newdocument\" id=\"adddocument\"><i class=\"fa fa-cloud-upload fa-lg\"></i> Add Document</a>");
			out.println("</li>");

			out.println("<li class=\"dropdown\">");
			out.println("<a href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\"><i class=\"fa fa-user fa-lg\" title=\"My Profile\"></i> <b class=\"caret\"></b></a>");
			out.println("<ul class=\"dropdown-menu\" role=\"menu\">");
			out.println("<li><a href=\"/console/myprofile\"><i class=\"fa fa-cog\"></i> My Profile</a></li>");
			out.println("<li class=\"divider\"></li>");
			out.println("<li><a href=\"/logout\" class=\"confirm\" title=\"Are you sure you want to logout?\"><i class=\"fa fa-sign-out\"></i> Logout</a></li>");
			out.println("</ul>");
			out.println("</li>");
			
			out.println("<li class=\"dropdown\">");
			out.println("<a href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\"><i class=\"fa fa-lg fa-question-circle fa-fw\"></i><b class=\"caret\"></b></a>");
			out.println("<ul class=\"dropdown-menu\" role=\"menu\">");
			out.println("<li><a href=\"http://www.krystaldms.in/resoruces/documentation/community/2015/user\" target=\"_new\"><i class=\"fa fa-book\"></i> Users Guide</a></li>");
			out.println("<li><a href=\"http://www.krystaldms.in/resoruces/documentation/community/2015/admin\"  target=\"_new\" ><i class=\"fa fa-book\"></i> Administrators Guide</a></li>");
			out.println("</ul>");
			out.println("</li>");
			
			out.println("</ul>");

			out.println("<form class=\"navbar-form\" role=\"form\" action=\"/console/search\" accept-charset=\"utf-8\" method=\"post\">");
			out.println("<div class=\"input-group\" style=\"padding:0px 10px;\">");
			String searchText = request.getParameter("txtSearch")!=null?request.getParameter("txtSearch"):"";
			out.println("<input type=\"text\" name=\"txtSearch\" id=\"txtSearch\" placeholder=\"Search Documents\" class=\"form-control required\" minlength=\"1\" value=\""+StringEscapeUtils.escapeHtml4(searchText)+"\">");
			out.println("<span class=\"input-group-btn\">");
			out.println("<button type=\"submit\" class=\"btn btn-default\"><i class=\"fa fa-lg fa-search\"></i></button>");
			out.println("</span>");
			out.println("</div>");
			out.println("</form>");

			out.println("</div>");
			out.println("</div>");
			out.println("</div>");
		}
		out.println("<div class=\"container-fluid\">");
	}

	public void generateFooter() throws Exception{
		out.println("</div>");//continer-fluid
		out.println("</div>");/*wrap ends here*/
		out.println("<div id=\"footer\">");
		out.println("<div class=\"container-fluid\" style=\"padding:10px;\">");
		out.println("<div class=\"pull-left\"><a href=\"http://www.primeleaf.in\" target=\"_new\"><img src=\"/images/primeleaf.jpg\"></a></div>");
		out.println("<div class=\"text-right text-muted\" style=\"padding:10px;\"><a href=\"http://www.primeleaf.in\" target=\"_new\">&copy;  Primeleaf Consulting (P) Ltd.</a></div>");
		out.println("</div>");
		out.println("</div>");
		out.println("</body>");
		out.println("</html>");
	}

}

