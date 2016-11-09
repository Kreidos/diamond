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

import com.primeleaf.krystal.constants.HTTPConstants;
import com.primeleaf.krystal.model.vo.DocumentClass;
import com.primeleaf.krystal.web.view.WebPageTemplate;
import com.primeleaf.krystal.web.view.WebView;

/**
 * @author Rahul Kubadia
 *
 */

public class EditDocumentClassView extends WebView {

	public EditDocumentClassView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}

	public void render() throws Exception{
		WebPageTemplate template = new WebPageTemplate(request,response);
		template.generateHeader();
		printNewDocumentClassForm();
		template.generateFooter();
	}
	private void printBreadCrumbs() throws Exception{
		out.println("<ol class=\"breadcrumb\">");
		out.println("<li><a href=\"/cpanel\">Control Panel</a></li>");
		out.println("<li><a href=\"/cpanel/managedocumentclasses\">Manage Document Classes</a></li>");
		out.println("<li class=\"active\">Edit Document Class</li>");
		out.println("</ol>");

	}
	private void printNewDocumentClassForm() throws Exception{
		printBreadCrumbs();
		if(request.getAttribute(HTTPConstants.REQUEST_ERROR) != null){
			printError((String)request.getAttribute(HTTPConstants.REQUEST_ERROR));
		}
		if(request.getAttribute(HTTPConstants.REQUEST_MESSAGE) != null){
			printSuccess((String) request.getAttribute(HTTPConstants.REQUEST_MESSAGE));
		}
		try {
			DocumentClass documentClass = (DocumentClass) request.getAttribute("DOCUMENTCLASS");
			
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\"><h4><i class=\"fa fa-folder-open fa-lg\"></i> Edit Document Class</h4></div>");
			out.println("<div class=\"panel-body\">");

			out.println("<form action=\"/cpanel/editdocumentclass\" method=\"post\" id=\"frmEditDocumentClass\" class=\"form-horizontal\">");
			out.println("<div class=\"form-group\">");
			out.println("<div class=\"col-sm-offset-3 col-sm-9\">");
			out.println("<p>Fields marked with <span style='color:red'>*</span> are mandatory</p>");
			out.println("</div>");
			out.println("</div>");

			out.println("<div class=\"form-group\">");
			out.println("<label for=\"txtClassName\" class=\"col-sm-3 control-label\">Document Class Name <span style='color:red'>*</span></label>");
			out.println("<div class=\"col-sm-9\">");
			out.println("<input type=\"text\" id=\"txtClassName\" name=\"txtClassName\" class=\"required form-control\" title=\"Please enter Document Class Name\" maxlength=\"50\" value=\""+StringEscapeUtils.escapeHtml4(documentClass.getClassName())+"\">");
			out.println("</div>");
			out.println("</div>");

			out.println("<div class=\"form-group\">");
			out.println("<label for=\"txtClassDescription\" class=\"col-sm-3 control-label\">Document Class Description <span style='color:red'>*</span></label>");
			out.println("<div class=\"col-sm-9\">");
			out.println("<input type=\"text\" id=\"txtClassDescription\" name=\"txtClassDescription\" class=\"required form-control\" title=\"Please enter Document Class Description\" maxlength=\"50\" value=\""+StringEscapeUtils.escapeHtml4(documentClass.getClassDescription())+"\">");
			out.println("</div>");
			out.println("</div>");


			out.println("<div class=\"form-group\">");
			out.println("<label for=\"txtExpiryPeriod\" class=\"col-sm-3 control-label\">Default Expiry Period <span style='color:red'>*</span></label>");
			out.println("<div class=\"col-sm-1\"><input type=\"text\" id=\"txtExpiryPeriod\" name=\"txtExpiryPeriod\"  class=\"required form-control digits range\" min=\"0\" max=\"9999\" title=\"Please enter Default Expiry Period\" value=\""+ documentClass.getExpiryPeriod() + "\" maxlength=\"4\"></div>" );
			out.println("<div class=\"col-sm-8\">Days</div>");
			out.println("</div>");
			
			out.println("<div class=\"form-group\">");
			out.println("<label for=\"radActive\" class=\"col-sm-3 control-label\">Active?</label>");
			out.println("<div class=\"btn-group col-sm-9\" data-toggle=\"buttons\">");
			out.println("<label class=\"btn btn-sm btn-default "); if(documentClass.isVisible()) {out.print(" active");} out.print("\">");
			out.println("<input type=\"radio\" id=\"radActive1\" name=\"radActive\" value=\"Y\"");if( documentClass.isVisible()) {out.print(" checked");}out.print(">Yes");
			out.println("</label>");
			out.println("<label class=\"btn btn-sm btn-default "); if(!documentClass.isVisible()) { out.print(" active");} out.print("\">");
			out.println("<input type=\"radio\" id=\"radActive2\" name=\"radActive\"  value=\"N\"");if(!documentClass.isVisible()) {out.print(" checked");}out.print(">No");
			out.println("</label>");
			out.println("</div>");
			out.println("</div>");

			out.println("<div class=\"form-group\">");
			out.println("<label for=\"radRevisionControl\" class=\"col-sm-3 control-label\">Enable Version Control?</label>");
			out.println("<div class=\"btn-group col-sm-9\" data-toggle=\"buttons\">");
			out.println("<label class=\"btn btn-sm btn-default "); if(documentClass.isRevisionControlEnabled()) {out.print(" active");} out.print("\">");
			out.println("<input type=\"radio\" id=\"radRevisionControl1\" name=\"radRevisionControl\" value=\"Y\"");if( documentClass.isRevisionControlEnabled()) {out.print(" checked");}out.print(">Yes");
			out.println("</label>");
			out.println("<label class=\"btn btn-sm btn-default "); if(!documentClass.isRevisionControlEnabled()) { out.print(" active");} out.print("\">");
			out.println("<input type=\"radio\" id=\"radRevisionControl2\" name=\"radRevisionControl\"  value=\"N\"");if(!documentClass.isRevisionControlEnabled()) {out.print(" checked");}out.print(">No");
			out.println("</label>");
			out.println("</div>");
			out.println("</div>");

			out.println("<hr/>");
			out.println("<div class=\"form-group\">");
			out.println("<div class=\"col-sm-offset-3 col-sm-9\">");
			out.println("<input type=\"hidden\" name=\"classid\" 	value=\""+ documentClass.getClassId() + "\"/>");
			out.println("<input type=\"submit\" name=\"btnSubmit\"  value=\"Submit\" class=\"btn btn-sm btn-default\">");
			out.println("</div>");
			out.println("</div>");

			out.println("</form>");
			out.println("</div>");
			out.println("</div>");
			out.println("</div>");
						
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		out.println("</div>"); //panel body
		out.println("</div>"); //panel
	}
}