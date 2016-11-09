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

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import com.primeleaf.krystal.constants.HTTPConstants;
import com.primeleaf.krystal.model.vo.DocumentClass;
import com.primeleaf.krystal.util.StringHelper;
import com.primeleaf.krystal.web.view.WebPageTemplate;
import com.primeleaf.krystal.web.view.WebView;

/**
 * @author Rahul Kubadia
 *
 */
public class SummaryReportView extends WebView {

	public SummaryReportView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}

	public void render() throws Exception{
		WebPageTemplate template = new WebPageTemplate(request,response);
		template.generateHeader();
		printSummaryReport();
		template.generateFooter();
	}
	private void printBreadCrumbs() throws Exception{
		out.println("<ol class=\"breadcrumb\">");
		out.println("<li><a href=\"/cpanel\">Control Panel</a></li>");
		out.println("<li><a href=\"/cpanel/reports\">System Reports</a></li>");
		out.println("<li class=\"active\">Repository Content Summary</li>");
		out.println("</ol>");
	}
	@SuppressWarnings("unchecked")
	private void printSummaryReport() throws Exception{
		printBreadCrumbs();
		if(request.getAttribute(HTTPConstants.REQUEST_ERROR) != null){
			printErrorDismissable((String)request.getAttribute(HTTPConstants.REQUEST_ERROR));
		}
		if(request.getAttribute(HTTPConstants.REQUEST_MESSAGE) != null){
			printSuccessDismissable((String)request.getAttribute(HTTPConstants.REQUEST_MESSAGE));
		}

		try{
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\"><h4><i class=\"fa fa-lg fa-bar-chart-o\"></i> Repository Content Summary</h4></div>");
			out.println("<div class=\"panel-body\">");

			out.println("<div class=\"row\">");

			out.println("<div class=\"col-lg-4\">");
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\">");
			out.println("<div class=\"row\">");
			out.println("<div class=\"col-xs-4\">");
			out.println("<i class=\"fa fa-folder-open fa-3x\"></i>");
			out.println("</div>");
			out.println("<div class=\"col-xs-8 text-right\">");
			out.println("<h3>"+request.getAttribute("DOCUMENT_CLASSES")+"</h3>");
			out.println("<p >Document Classes</p>");
			out.println("</div>");
			out.println("</div>");//row
			out.println("</div>");//panel-heading
			out.println("</div>");//panel
			out.println("</div>");//col-lg-4


			out.println("<div class=\"col-lg-4\">");
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\">");
			out.println("<div class=\"row\">");
			out.println("<div class=\"col-xs-6\">");
			out.println("<i class=\"fa fa-file fa-3x\"></i>");
			out.println("</div>");
			out.println("<div class=\"col-xs-6 text-right\">");
			out.println("<h3>"+request.getAttribute("DOCUMENTS")+"</h3>");
			out.println("<p >Documents</p>");
			out.println("</div>");
			out.println("</div>");//row
			out.println("</div>");//panel-heading
			out.println("</div>");//panel
			out.println("</div>");//col-lg-4

			out.println("<div class=\"col-lg-4\">");
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\">");
			out.println("<div class=\"row\">");
			out.println("<div class=\"col-xs-6\">");
			out.println("<i class=\"fa fa-user fa-3x\"></i>");
			out.println("</div>");
			out.println("<div class=\"col-xs-6 text-right\">");
			out.println("<h3>"+request.getAttribute("USERS")+"</h3>");
			out.println("<p >Users</p>");
			out.println("</div>");
			out.println("</div>");//row
			out.println("</div>");//panel-heading
			out.println("</div>");//panel
			out.println("</div>");//col-lg-4

			out.println("</div>");//row

			ArrayList<DocumentClass> documentClasses = 	(ArrayList<DocumentClass>) request.getAttribute("DOCUMENTCLASSLIST");
			if(documentClasses.size() > 0){
				//charts rendering starts here
				out.println("<div class=\"panel panel-default\">");
				out.println("<div class=\"panel-heading\">");
				out.println("<i class=\"fa fa-pie-chart fa-lg\"></i> Charts");
				out.println("</div>");
				out.println("<div class=\"panel-body\">");
				out.println("<div class=\"row\">");
				out.println("<div class=\"col-sm-6 text-center\">");
				out.println("<h3>Documents : "  + request.getAttribute("DOCUMENTS") +"</h3>");
				out.println("<div id=\"classchart\" style=\"height:280px;\">");
				out.println("<script>");
				out.println("new Morris.Donut({");
				out.println("  element: 'classchart',");
				out.println("  data: [");


				for(DocumentClass documentClass : documentClasses){
					int documentCount = documentClass.getActiveDocuments();
					out.println("    { label: \""+StringEscapeUtils.escapeHtml4(documentClass.getClassName())+"\", value: "+documentCount+" },");
				}
				out.println("  ],");
				out.println("});");
				out.println("</script>");
				out.println("</div>");
				out.println("</div>");//col-sm-6


				double totalSize = (Double)request.getAttribute("TOTALSIZE");
				out.println("<div class=\"col-sm-6 text-center\">");
				out.println("<h3>Total Size : " + StringHelper.formatSizeText(totalSize)+ "</h3>");
				out.println("<div id=\"sizechart\" style=\"height:280px;\">");
				out.println("<script>");
				out.println("new Morris.Donut({");
				out.println("  element: 'sizechart',");
				out.println("  data: [");

				for(DocumentClass documentClass : documentClasses){
					double documentSize = (Double) request.getAttribute(documentClass.getClassName()+"_SIZE");
					out.println("{ label: \""+StringEscapeUtils.escapeHtml4(documentClass.getClassName())+"\", value: "+documentSize+" },");
				}
				out.println("  ], "
						+ " formatter : function (y, data) { "
						+ " var result = '';"
						+ " if(y > 1024) { result = parseFloat(y/1024).toFixed(1)+ ' KB'} "
						+ " if(y > 1048576) { result = parseFloat(y/1048576).toFixed(1)+' MB'} "
						+ " if(y > 1073741824) { result = parseFloat(y/1073741824).toFixed(1)+' GB'} "
						+ "return result } ");
				out.println("});");
				out.println("</script>");
				out.println("</div>");
				out.println("</div>");//col-sm-6
				out.println("</div>");//row

				if(documentClasses.size() > 0){
					out.println("<div class=\"text-center\">");
					out.println("<div id=\"linechart\" style=\"height:280px;\">");
					out.println("<script>");
					out.println("new Morris.Line({");
					out.println("  element: 'linechart',");
					out.println("  data: [");
					LinkedHashMap<String,Integer> chartValues =(LinkedHashMap<String,Integer>) request.getAttribute(documentClasses.get(0).getClassName() + "_CHARTVALUES");

					for(String month : chartValues.keySet()){
						out.print("{y : '" + month + "'");
						for(DocumentClass documentClass : documentClasses){
							chartValues = (LinkedHashMap<String,Integer>) request.getAttribute(documentClass.getClassName() + "_CHARTVALUES");
							out.print(", c"+documentClass.getClassId() + " : "+ chartValues.get(month));
						}
						out.println("},");
					}
					out.println("  ],");
					out.println("   xkey: 'y',");
					out.print(" ykeys: [");
					for(DocumentClass documentClass : documentClasses){
						out.print("'c"+documentClass.getClassId()+"',");
					}
					out.println("],");
					out.println(" labels: [");
					for(DocumentClass documentClass : documentClasses){
						out.print("'"+StringEscapeUtils.escapeHtml4(documentClass.getClassName())+"',");
					}
					out.println("]");
					out.println("});");
					out.println("</script>");
					out.println("</div>");//line-chart
					out.println("</div>");//
				}

				out.println("</div>");//panel-body
				out.println("</div>");//panel
			}
			//charts rendering ends here

			out.println("<div class=\"panel  panel-default\">");
			out.println("<div class=\"panel-heading\">");
			out.println("<i class=\"fa fa-folder-open fa-lg\"></i> Document Classes");
			out.println("</div>");
			ArrayList<DocumentClass> documentClassList = (ArrayList<DocumentClass>) request.getAttribute("DOCUMENTCLASSLIST");
			if(documentClassList.size() > 0 ){
				out.println("<div class=\"table-responsive\">");
				out.println("<table class=\"table table-condensed table-stripped\">");
				out.println("<thead><tr>");
				out.println("<th>Document Class</th>");
				out.println("<th class=\"text-center\">Documents</th>");
				out.println("<th class=\"text-right\">Total Size</th></tr></thead>");
				out.println("<tbody>");
				for (DocumentClass documentClass : documentClassList) {
					int documentCount =  documentClass.getActiveDocuments();
					double documentSize = (Double) request.getAttribute(documentClass.getClassName()+"_SIZE");
					String ownerName = (String) request.getAttribute(documentClass.getClassName()+"_OWNER" );
					out.println("<tr>");
					out.println("<td style=\"width:80%;\">");
					out.println("<h4 class=\"text-danger\">" + StringEscapeUtils.escapeHtml4(documentClass.getClassName()) + "</h4>");
					out.println("<h5>" + StringEscapeUtils.escapeHtml4(documentClass.getClassDescription()) + "</h5>");
					out.println("<p>");
					out.println("<i>Created By " + ownerName);
					out.println(" , " +StringHelper.getFriendlyDateTime(documentClass.getCreated()) +"</i>");
					out.println("</p>");
					out.println("</td>");

					out.println("<td style=\"width:10%;\" class=\"text-center\">");
					out.println("<h4>" + documentCount + "</h4>");
					out.println("</td>");

					out.println("<td class=\"text-right\">");
					out.println("<h4>" + StringHelper.formatSizeText(documentSize) + "</h4>");
					out.println("</td>");
					out.println("</tr>");//row

				}// for
				out.println("</tbody>");
				out.println("</table>");
				out.println("</div>");
			}else{
				out.println("<div class=\"panel-body\">"); //panel
				out.println("No document class found");
				out.println("</div>"); //panel-body
			}
			out.println("</div>"); //panel

			out.println("</div>");
			out.println("</div>");
			out.println("</div>");

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}

