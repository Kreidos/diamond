/**
 * Created On 09-Jan-2014
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

package com.primeleaf.krystal.web.view.cpanel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.primeleaf.krystal.web.view.WebPageTemplate;
import com.primeleaf.krystal.web.view.WebView;

/**
 * @author Rahul Kubadia
 *
 */
public class ControlPanelView extends WebView {

	public ControlPanelView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}

	public void render() throws Exception{
		WebPageTemplate template = new WebPageTemplate(request,response);
		template.generateHeader();
		printDomains();
		template.generateFooter();
	}

	private void printDomains() throws Exception{
		try{
			
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\">");
			out.println("<h3><i class=\"fa fa-cogs fa-lg \"></i> Control Panel</h3>");
			out.println("</div>");
			
			out.println("<div class=\"list-group\">");
			
			out.println("<a href=\"/cpanel/users\" class=\"list-group-item\">");
			out.println("<h4><i class=\"fa fa-user fa-lg \"></i> Manage Users</h4>");
			out.println("</a>");

			out.println("<a href=\"/cpanel/managedocumentclasses\"  class=\" list-group-item\">");
			out.println("<h4><i class=\"fa fa-folder-open fa-lg \"></i> Manage Document Classes</h4>");
			out.println("</a>");

			out.println("<a href=\"/cpanel/managecheckouts\"  class=\" list-group-item\">");
			out.println("<h4><i class=\"fa fa-lock fa-lg \"></i> Manage Checkouts</h4>");
			out.println("</a>");

			out.println("<a href=\"/cpanel/recyclebin\"  class=\" list-group-item\">");
			out.println("<h4><i class=\"fa fa-trash-o fa-lg \"></i> Recycle Bin</h4>");
			out.println("</a>");

			out.println("<a href=\"/cpanel/reports\" class=\"list-group-item\">");
			out.println("<h4><i class=\"fa fa-bar-chart-o fa-lg\"></i> System Reports</h4>");
			out.println("</a>");
			
			out.println("</div>");//list-group
			out.println("</div>");//panel
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

