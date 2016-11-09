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

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import com.primeleaf.krystal.constants.HTTPConstants;
import com.primeleaf.krystal.model.vo.DocumentClass;
import com.primeleaf.krystal.model.vo.DocumentNote;
import com.primeleaf.krystal.model.vo.Hit;
import com.primeleaf.krystal.model.vo.IndexDefinition;
import com.primeleaf.krystal.util.StringHelper;
import com.primeleaf.krystal.web.view.WebPageTemplate;
import com.primeleaf.krystal.web.view.WebView;

/**
 * @author Rahul Kubadia
 *
 */
public class SearchView extends WebView {
	public SearchView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}

	public void render() throws Exception{
		WebPageTemplate template = new WebPageTemplate(request,response);
		template.generateHeader();
		printSearchResults();
		template.generateFooter();
	}
	private void printBreadCrumbs() throws Exception{
		out.println("<ol class=\"breadcrumb\">");
		out.println("<li><a href=\"/console\">My Workspace</a></li>");
		out.println("<li class=\"active\">Search Results</li>");
		out.println("</ol>");
	}
	private void printSearchResults() throws Exception{
		printBreadCrumbs();
		if(request.getAttribute(HTTPConstants.REQUEST_ERROR) != null){
			printError((String)request.getAttribute(HTTPConstants.REQUEST_ERROR));
		}

		String searchText = request.getAttribute("SEARCHTEXT").toString();
		searchText = StringEscapeUtils.escapeHtml4(searchText);

		out.println("<div id=\"searchresults\">");

		out.println("<div class=\"panel panel-default\">");
		out.println("<div class=\"panel-heading\"><h4><i class=\"fa fa-lg fa-search\"></i> Search Results : " + searchText + " </h4></div>");
		out.println("</div>");

		printDocumentClasses();
		printDocuments();
		printDocumentNotes();

		out.println("<div class=\"well well-sm\">");
		out.println("Total time taken to retreive results : " + request.getAttribute("EXECUTIONTIME") + " seconds");
		out.println("</div>");

		out.println("</div>");

		String[] searchWords = searchText.split("\\s+");
		String classNames[] = {"highlight","btn-danger","btn-success","btn-warning","btn-default"};
		int i = 0;
		for(String searchWord : searchWords){
			String className = classNames[i % 5];
			i++;
			out.println("<script>$(\"#searchresults \").highlight(\""+StringEscapeUtils.escapeHtml4(searchWord)+"\",  { element: 'span', className: '"+className+"' });</script>");
		}
	}

	@SuppressWarnings("unchecked")
	private void printDocumentClasses(){
		ArrayList<DocumentClass> documentClassList  =(ArrayList<DocumentClass>) request.getAttribute("MATCHINGDOCUMENTCLASSLIST");
		if(documentClassList.size() > 0 ){
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\">");
			out.println("<div class=\"row\">");
			out.println("<div class=\"col-xs-8\">");
			out.println("<h4><i class=\"fa fa-folder-open fa-lg\"></i> Document Classes</h4>");
			out.println("</div>");
			out.println("<div class=\"col-xs-4 text-right\">");
			out.println("<h4>" + documentClassList.size() + " Document Classes</h4>");
			out.println("</div>");
			out.println("</div>");
			out.println("</div>");
			out.println("<div class=\"list-group\">");
			for(DocumentClass documentClass : documentClassList){
				out.println("<li class=\"list-group-item\">");
				out.println("<h3>"+StringEscapeUtils.escapeHtml4(documentClass.getClassName())+"</h3>");
				out.println("<h4>"+ StringEscapeUtils.escapeHtml4(documentClass.getClassDescription())+"</h4>");

				out.println("<p>");
				out.println("<a href=\"/console/opendocumentclass?classid="+ documentClass.getClassId()+"\">View All</a> | ");
				out.println("<a href=\"/console/searchdocumentclass?classid="+ documentClass.getClassId()+"\">Search</a>");
				out.println("</p>");

				out.println("</li>");
			}
			out.println("</div>");
			out.println("</div>");
		}
	}

	@SuppressWarnings("unchecked")
	private void printDocuments(){
		ArrayList<DocumentClass> documentClasses = (ArrayList<DocumentClass>) request.getAttribute("DOCUMENTCLASSLIST");
		for(DocumentClass documentClass : documentClasses){
			ArrayList<Hit> hits = (ArrayList<Hit>) request.getAttribute(documentClass.getClassId() + "_HITS");
			if(hits.size() > 0 ) {
				out.println("<div class=\"panel panel-default\">");
				out.println("<div class=\"panel-heading\">");
				out.println("<div class=\"row\">");
				out.println("<div class=\"col-xs-8\">");
				out.println("<h4><i class=\"fa fa-file fa-lg\"></i> "+StringEscapeUtils.escapeHtml4(documentClass.getClassName()) + "");
				out.println(" - "+ StringEscapeUtils.escapeHtml4(documentClass.getClassDescription())+"</h4>");
				out.println("</div>");
				out.println("<div class=\"col-xs-4 text-right\">");
				out.println("<h4>"+ hits.size() + " Documents</h4>");
				out.println("</div>");
				out.println("</div>");//row
				out.println("</div>");//panel-heading

				out.println("<div class=\"table-responsive\">");
				out.println("<table class=\"table table-striped\">");
				out.println("<thead>");
				out.println("<tr>");
				out.println("<th class=\"text-center\">Document ID</th>");
				if(documentClass.isRevisionControlEnabled()){
					out.println("<th class=\"text-center\">Revision ID</th>");
				}
				for(IndexDefinition indexDefinition : documentClass.getIndexDefinitions()){
					String indexDescription = indexDefinition.getIndexDisplayName();
					out.println("<th>"+StringEscapeUtils.escapeHtml4(indexDescription)+"</th>");
				}
				out.println("<th class=\"text-right\">&nbsp;</th>");
				out.println("</tr>");
				out.println("</thead>");

				out.println("<tbody>");
				for(Hit hit : hits){
					out.println("<tr>");
					out.println("<td class=\"text-center\">"+ hit.documentId+ "</td>");
					if(documentClass.isRevisionControlEnabled()){
						out.println("<td class=\"text-center\">"+ hit.revisionId+ "</td>");
					}
					for (String value : hit.indexValues){
						out.println("<td>"+StringEscapeUtils.escapeHtml4(value)+"</td>");
					}
					out.println("<td class=\"text-right\">");
					out.println("<a href=\""+HTTPConstants.BASEURL+"/console/viewdocument?documentid=" + hit.documentId +"&revisionid="+ hit.revisionId+"\" title=\"View Document\">View Document</a>");
					out.println("</tr>");
				}
				out.println("</tbody>");
				out.println("</table>");
				out.println("</div>");//table-responsive
				out.println("</div>");//panel
			}
		}
	}


	@SuppressWarnings("unchecked")
	private void printDocumentNotes(){
		ArrayList<DocumentNote> documentNotes = (ArrayList<DocumentNote>) request.getAttribute("NOTELIST");
		if(documentNotes.size() > 0 ) {
			out.println("<div class=\"panel panel-default\">");
			out.println("<div class=\"panel-heading\">");
			out.println("<div class=\"row\">");
			out.println("<div class=\"col-xs-8\">");
			out.println("<h4><i class=\"fa fa-comments fa-lg\"></i> Document Notes</h4>");
			out.println("</div>");
			out.println("<div class=\"col-xs-4 text-right\">");
			out.println("<h4>"+ documentNotes.size() + " Document Notes</h4>");
			out.println("</div>");
			out.println("</div>");//row
			out.println("</div>");//panel-heading
			out.println("<ul class=\"list-group\">");
			for(DocumentNote note : documentNotes){
				String cssClass="";
				if("P".equalsIgnoreCase(note.getNoteType())){
					cssClass = "text-success";
				}
				out.println("<li class=\"list-group-item "+cssClass+"\">");
				out.println("<h4>"+StringEscapeUtils.escapeHtml4(note.getNoteData())+"</h4>");
				out.println("<h5>"+StringEscapeUtils.escapeHtml4(note.getUserName())+"</h5>");
				out.println("<p>" + StringHelper.getFriendlyDateTime(note.getCreated())+"</p>");
				if(note.getUserName().equalsIgnoreCase(loggedInUser.getUserName())){
					out.println("<p><a href=\""+HTTPConstants.BASEURL+"/console/viewdocument?documentid=" + note.getDocumentId() +"\" title=\"View Document\">View Document</a></p>");
				}
				out.println("</li>");
			}
			out.println("</ul>");
			out.println("</div>");//panel
		}
	}

}
