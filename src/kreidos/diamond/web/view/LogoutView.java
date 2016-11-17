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

package kreidos.diamond.web.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author Rahul Kubadia
 */

/**
 * @author Rahul Kubadia
 *
 */
public class LogoutView extends WebView {

	public LogoutView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}
	
	public void render() throws Exception{
		WebPageTemplate template = new WebPageTemplate(request,response);
		template.generateHeader();
		printLogout();
		template.generateFooter();
	}
		
	private void printLogout() throws Exception{
		out.println("<div class=\"row\">");
		out.println("<div class=\"col-lg-6 col-lg-offset-3 pre-login\">");
		out.println("<div class=\"panel panel-default\">");
		out.println("<div class=\"panel-heading\">");
		out.println("<h4 class=\"text-primary\"><i class=\"fa fa-sign-out fa-fw fa-lg\"></i>Logout</h4>");
		out.println("</div>"); //panel - heading
		out.println("<div class=\"panel-body\">"); //Panel Body
		out.println("<h4>You are logged out successfully</h4>");
		out.println("<p><a href=\"/\" title=\"Click here to login\">Click here to login</a></p>");
		out.println("</div>"); //Panel Body
		out.println("</div>"); //Panel
		out.println("</div>"); //col-lg-4
		out.println("</div>"); //row
	}


}
 
