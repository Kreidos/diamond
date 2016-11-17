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

import java.text.DecimalFormat;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import com.primeleaf.krystal.constants.HTTPConstants;
import com.primeleaf.krystal.model.vo.Document;
import com.primeleaf.krystal.model.vo.DocumentClass;
import com.primeleaf.krystal.model.vo.IndexDefinition;
import com.primeleaf.krystal.web.view.WebPageTemplate;
import com.primeleaf.krystal.web.view.WebView;

/**
 * @author Rahul Kubadia
 *
 */
public class CheckInDocumentView extends WebView {
	public CheckInDocumentView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}

	public void render() throws Exception{
		WebPageTemplate template = new WebPageTemplate(request,response);
		template.generateHeader();
		printCheckInDocumentForm();
		template.generateFooter();
	}
	private void printBreadCrumbs() throws Exception{
		out.println("<ol class=\"breadcrumb\">");
		out.println("<li><a href=\"/console\">My Workspace</a></li>");
		out.println("<li class=\"active\">Check In</li>");
		out.println("</ol>");
	}
	@SuppressWarnings("unchecked")
	private void printCheckInDocumentForm() throws Exception{
		printBreadCrumbs();
		Document document = (Document) request.getAttribute("DOCUMENT");
		DocumentClass documentClass = (DocumentClass) request.getAttribute("DOCUMENTCLASS");
		LinkedHashMap<String,String> documentIndexes = (LinkedHashMap<String,String>) request.getAttribute("DOCUMENTINDEXES");
		
		if(request.getAttribute(HTTPConstants.REQUEST_ERROR) != null){
			printError((String)request.getAttribute(HTTPConstants.REQUEST_ERROR));
		}
		if(request.getAttribute(HTTPConstants.REQUEST_MESSAGE) != null){
			printSuccess((String)request.getAttribute(HTTPConstants.REQUEST_MESSAGE));
		}
		if(document != null){
			try {
				out.println("<div class=\"panel panel-default\">");
				out.println("<div class=\"panel-heading\"><h4><i class=\"fa fa-lg fa-arrow-right\"></i> Check In - "+ documentClass.getClassName() +"</h4></div>");
				out.println("<div class=\"panel-body\">");

				out.println("<form action=\"/console/checkindocument\" method=\"post\" id=\"frmCheckInDocument\" class=\"form-horizontal\" enctype=\"multipart/form-data\" accept-charset=\"utf-8\">");
				out.println("<div class=\"form-group\">");
				out.println("<div class=\"col-sm-offset-3 col-sm-9\">");
				out.println("<p>Fields marked with <span style='color:red'>*</span> are mandatory</p>");
				out.println("</div>");
				out.println("</div>");
				
				out.println("<div class=\"form-group\">");
				out.println("<label for=\"fileDocument\" class=\"col-sm-3 control-label\">Select Document <span style='color:red'>*</span></label>");
				out.println("<div class=\"col-sm-9\">");
				out.println("<input type=\"file\" name=\"fileDocument\" class=\"required checkExtension\" title=\"Select document of type " + document.getExtension()  + " to check-in\">");
				out.println("</div>");
				out.println("</div>");

				for(IndexDefinition indexDefinition :documentClass.getIndexDefinitions()){
					String required = "";
					out.println("<div class=\"form-group\">");
					out.println("<label for=\""+indexDefinition.getIndexColumnName()+"\" class=\"col-sm-3 control-label\"> "+StringEscapeUtils.escapeHtml4(indexDefinition.getIndexDisplayName()));
					if(indexDefinition.isMandatory()){
						required = "required";
						out.println(" <span style='color:red'>*</span>");
					}
					out.println("</label>");

					String value = documentIndexes.get(indexDefinition.getIndexDisplayName());
					value = StringEscapeUtils.escapeHtml4(value);
					
					if(indexDefinition.getIndexType().equals(IndexDefinition.INDEXTYPE_DATE)){
						out.println("<div class=\"col-sm-2\">");
						out.println("<div class=\"input-group\">");
						out.println("<input type=\"text\" class=\"shortdate isdate form-control "+ required +"\" size=\""+indexDefinition.getIndexMaxLength()+"\" name=\""+indexDefinition.getIndexColumnName()+"\" id=\""+indexDefinition.getIndexColumnName()+"\" value=\""+value+"\" maxlength=\""+indexDefinition.getIndexMaxLength()+"\"  cid=\""+documentClass.getClassId()+"\">");
						out.println("<span class=\"input-group-addon\"><i class=\"fa fa-calendar\"></i></span>");
						out.println("</div>");
						out.println("</div>");
					}else if(indexDefinition.getIndexType().equals(IndexDefinition.INDEXTYPE_NUMBER)){
						out.println("<div class=\"col-sm-9\">");
						out.println("<input type=\"text\" class=\"number  form-control "+ required +" autocomplete\"  size=\""+indexDefinition.getIndexMaxLength()+"\"  id=\""+indexDefinition.getIndexColumnName()+"\" name=\""+indexDefinition.getIndexColumnName()+"\" value=\""+value+"\" maxlength=\""+indexDefinition.getIndexMaxLength()+"\"   cid=\""+documentClass.getClassId()+"\">");
						out.println("</div>");
					}else {
						out.println("<div class=\"col-sm-9\">");
						out.println("<input type=\"text\"  class=\"autocomplete form-control "+ required +" \" id=\""+indexDefinition.getIndexColumnName()+"\"  name=\""+indexDefinition.getIndexColumnName()+"\" value=\""+value+"\"maxlength=\""+indexDefinition.getIndexMaxLength()+"\"  cid=\""+documentClass.getClassId()+"\">");
						out.println("</div>");
					}
					out.println("</div>");
				}
				
				double rev = Double.parseDouble(document.getRevisionId());
				DecimalFormat onePlace = new DecimalFormat("0.0");
				// For minor revision id
				double minorRevisionId = rev + 0.1;
				// For major revision id
				rev = Math.floor(rev);
				double majorRevisionId = rev + 1.0;
				
				// revision number field
				out.println("<div class=\"form-group\">");
				out.println("<label for=\"version\" class=\"col-sm-3 control-label\">Version</label>");
				out.println("<div class=\"btn-group col-sm-9\" data-toggle=\"buttons\">");
				out.println("<label class=\"btn  btn-sm btn-default active\">");
				out.println("<input type=\"radio\" id=\"version1\" name=\"version\" value=\"minor\" checked>Minor (" + onePlace.format(minorRevisionId) + ")");
				out.println("</label>");
				out.println("<label class=\"btn  btn-sm btn-default\">");
				out.println("<input type=\"radio\" id=\"version2\" name=\"version\"  value=\"major\">Major (" + onePlace.format(majorRevisionId) + ")");
				out.println("</label>");
				out.println("</div>");
				out.println("</div>");

				out.println("<div class=\"form-group\">");
				out.println("<label for=\"txtNote\" class=\"col-sm-3 control-label\">Note / Comment </label>");
				out.println("<div class=\"col-sm-9\">");
				out.println("<textarea rows=\"3\" name=\"txtNote\" id=\"txtNote\" class=\"form-control\"></textarea>"); 
				out.println("</div>"); 
				out.println("</div>");
				out.println("<hr/>");
				out.println("<div class=\"form-group\">");
				out.println("<div class=\"col-sm-offset-3 col-sm-9\">");
				out.println("<input type=\"hidden\" name=\"documentid\" value=\""+document.getDocumentId()+"\">");
				out.println("<input type=\"hidden\" name=\"fileExtension\" id=\"fileExtension\" value=\""+document.getExtension().toUpperCase()+"\">");
				out.println("<input type=\"submit\"  name=\"btnSubmit\"  value=\"Check In\" class=\"btn btn-sm btn-default\">");
				out.println("</div>");
				out.println("</div>");
				out.println("</form>");
				
				out.println("</div>"); //panel-body
				out.println("</div>"); //panel
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
