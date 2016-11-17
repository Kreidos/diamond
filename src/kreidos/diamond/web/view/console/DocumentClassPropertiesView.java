/**
 * Created On 05-Dec-2014
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

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.primeleaf.krystal.constants.ServerConstants;
import com.primeleaf.krystal.model.vo.DocumentClass;
import com.primeleaf.krystal.model.vo.IndexDefinition;
import com.primeleaf.krystal.model.vo.User;
import com.primeleaf.krystal.util.StringHelper;
import com.primeleaf.krystal.web.view.WebView;

/**
 * @author Rahul Kubadia
 *
 */

public class DocumentClassPropertiesView extends WebView {
	public DocumentClassPropertiesView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}
	public void render() throws Exception{
		printDocumentClassProperties();
	}
	
	@SuppressWarnings("unchecked")
	private void printDocumentClassProperties() throws Exception{
		try {
			DocumentClass documentClass = (DocumentClass) request.getAttribute("DOCUMENTCLASS");
			User owner  = (User) request.getAttribute("OWNER");
			
			int documentCount = documentClass.getActiveDocuments();
			int expiringDocuments = (Integer)request.getAttribute("EXPIRY_COUNT");
			int weekCount = (Integer)request.getAttribute("WEEK_COUNT");
			
			out.println("<div class=\"modal-header\">");
			out.println("<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>");
			out.println("<h4 class=\"modal-title\" id=\"myModalLabel\"><i class=\"fa fa-folder-open fa-lg\"></i> Document Class Properties</h4>");
			out.println("</div>");
			out.println("<div class=\"modal-body\">");
			out.println("<div class=\"row\">");
			out.println("<div class=\"col-lg-6\">");
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\">Document Class Properties</div>");
			out.println("<div class=\"table-responsive\">");
			out.println("<table class=\"table table-bordered  table-hover\">");
			out.println("<thead>");
			out.println("<tr class=\"info\"><th>Property</th><th>Value</th></tr>");
			out.println("</thead>");
			out.println("<tbody>");
			out.println("<tr><td>Document Class Name</td><td>"+documentClass.getClassName()+"</td></tr>");
			out.println("<tr><td>Document Class Description</td><td>"+documentClass.getClassDescription()+"</td></tr>");
			out.println("<tr><td>Created By</td><td>"+owner.getRealName()+"</td></tr>");
			out.println("<tr><td>Created On</td><td>"+ StringHelper.formatDate(documentClass.getCreated(),ServerConstants.FORMAT_SHORT_DATE) + " , "  + StringHelper.getFriendlyDateTime(documentClass.getCreated())+"</td></tr>");
			out.println("<tr class=\"text-primary\"><td>New Documents</td><td>"+weekCount+"</td></tr>");
			out.println("<tr class=\"text-danger\"><td>Expiring Documents</td><td>"+expiringDocuments+"</td></tr>");
			out.println("<tr class=\"text-success\"><td>Total Documents</td><td>"+documentCount+"</td></tr>");
			out.println("</tbody>");
			out.println("</table>");
			out.println("</div>");//table-responsive
			out.println("</div>");//panel
			out.println("</div>");//col-lg-6
			out.println("<div class=\"col-lg-6\">");
			
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\">Storage Limits</div>");
			out.println("<div class=\"table-responsive\">");
			out.println("<table class=\"table table-bordered table-hover\">");
			out.println("<thead>");
			out.println("<tr class=\"info\"><th>Property</th><th>Value</th></tr>");
			out.println("</thead>");
			out.println("<tbody>");
			out.println("<tr><td>Maximum File Size</td>");
			out.println("<td>");
			if(documentClass.getMaximumFileSize()==1048576)   			out.println("1 MB");
			if(documentClass.getMaximumFileSize()==2097152)   			out.println("2 MB");
			if(documentClass.getMaximumFileSize()==5242880)  			out.println("5 MB");
			if(documentClass.getMaximumFileSize()==10485760) 			out.println("10 MB");
			if(documentClass.getMaximumFileSize()==26214400) 			out.println("25 MB");
			if(documentClass.getMaximumFileSize()==52428800) 			out.println("50 MB");
			if(documentClass.getMaximumFileSize()==104857600)			out.println("100 MB");
			if(documentClass.getMaximumFileSize()==209715200)			out.println("200 MB");
			if(documentClass.getMaximumFileSize()==524288000)			out.println("500 MB");
			if(documentClass.getMaximumFileSize()==1048576000)			out.println("1000 MB");
			if(documentClass.getMaximumFileSize()==Integer.MAX_VALUE) 	out.println("No Limit");

			out.println("</td>");
			out.println("</tr>");
			out.println("<tr><td>Maximum Document Limit</td>");
			out.println("<td>");
			
			if(documentClass.getDocumentLimit()==10000)					out.println("10000");
			if(documentClass.getDocumentLimit()==20000) 				out.println("20000");
			if(documentClass.getDocumentLimit()==50000) 				out.println("50000");
			if(documentClass.getDocumentLimit()==100000)				out.println("100000");
			if(documentClass.getDocumentLimit()==200000) 				out.println("200000");
			if(documentClass.getDocumentLimit()==500000) 				out.println("500000");
			if(documentClass.getDocumentLimit()==Integer.MAX_VALUE)		out.println("No Limit");
	
			out.println("</td>");
			out.println("</tr>");
			out.println("</tbody>");
			out.println("</table>");
			out.println("</div>");//table-responsive
			out.println("</div>");//panel
			
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\">Document Expiry</div>");
			out.println("<div class=\"table-responsive\">");
			out.println("<table class=\"table table-bordered table-hover\">");
			out.println("<thead>");
			out.println("<tr class=\"info\"><th>Property</th><th>Value</th></tr>");
			out.println("</thead>");
			out.println("<tbody>");
			out.println("<tr><td>Default Expiry Period</td><td>"+documentClass.getExpiryPeriod()+" Days</td></tr>");
			out.println("<tr><td>Expiry Notification Period</td><td>"+documentClass.getExpiryNotificationPeriod()+" Days</td></tr>");
			out.println("</tbody>");
			out.println("</table>");
			out.println("</div>");//table-responsive
			out.println("</div>");//panel
			out.println("</div>");//col-lg-6
			out.println("</div>");//row
			
			
			out.println("<div class=\"row\">");
			out.println("<div class=\"col-lg-6\">");
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\">");
			out.println("Available Document Class Indexes");//panel-heading
			out.println("</div>");//panel-heading
			out.println("<div class=\"table-responsive\">");
			out.println("<table class=\"table table-bordered  table-hover\">");
			out.println("<thead>");
			out.println("<tr class=\"info\">");
			out.println("<th>Index Description</th>");
			out.println("<th>Index Type</th>");
			out.println("<th class=\"text-center\">Index Length</th>");
			out.println("</tr>");
			out.println("</thead>");
			out.println("<tbody>");
			for(IndexDefinition indexDefinition : documentClass.getIndexDefinitions()){
				out.println("<tr>");
				out.println("<td>"+indexDefinition.getIndexDisplayName()+"</td>");
				out.println("<td>");
				if(IndexDefinition.INDEXTYPE_DATE.equalsIgnoreCase(indexDefinition.getIndexType())){
					out.print("Date");
				}else if(IndexDefinition.INDEXTYPE_STRING.equalsIgnoreCase(indexDefinition.getIndexType())){
					out.print("String");
				}else{
					out.print("Number");
				};
				out.println("</td>");
				out.println("<td class=\"text-center\">"+indexDefinition.getIndexMaxLength()+"</td>");
				out.println("</tr>");
			}
			out.println("</tbody>");
			out.println("</table>");//table
			out.println("</div>");//table-responsive
			out.println("</div>");//panel
			out.println("</div>");//col-lg-6
			
			out.println("<div class=\"col-lg-6\">");
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\">");
			out.println("Documents");
			out.println("</div>");//panel-heading
			out.println("<div class=\"panel-body\">");
			out.println("<div id=\"linechart\" style=\"height:250px;width:385px;\">");
			out.println("<script>");
			out.println("new Morris.Line({");
			out.println("  element: 'linechart',");
			out.println("  data: [");
			LinkedHashMap<String,Integer> chartValues =(LinkedHashMap<String,Integer>) request.getAttribute("CHARTVALUES");
			for(String month : chartValues.keySet()){
				out.print("{y : '" + month + "'");
				out.print(", a: "+ chartValues.get(month));
				out.println("},");
			}
			out.println("  ],");
			out.println("xkey: 'y',");
			out.println("ykeys: ['a'],");
			out.println("lineColors: ['#C17702'],");
			out.println("hideHover: true,");
			out.println("labels: ['Documents']");
			out.println("});");
			out.println("</script>");
			out.println("</div>");//line-chart
			out.println("</div>");//panel-body
			out.println("</div>");//panel
			out.println("</div>");//col-sm-7
			out.println("</div>");//row
			
			out.println("</div>");//modal-body
			out.println("<div class=\"modal-footer\">");
			out.println("<button type=\"button\" class=\"btn btn-sm btn-default\" data-dismiss=\"modal\">Close</button>");
			out.println("</div>");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}