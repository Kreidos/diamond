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

package com.primeleaf.krystal.web.action.console;

import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.primeleaf.krystal.constants.HTTPConstants;
import com.primeleaf.krystal.model.AccessControlManager;
import com.primeleaf.krystal.model.DocumentSearchManager;
import com.primeleaf.krystal.model.dao.DocumentClassDAO;
import com.primeleaf.krystal.model.dao.DocumentNoteDAO;
import com.primeleaf.krystal.model.dao.UserDAO;
import com.primeleaf.krystal.model.vo.DocumentClass;
import com.primeleaf.krystal.model.vo.DocumentNote;
import com.primeleaf.krystal.model.vo.Hit;
import com.primeleaf.krystal.model.vo.User;
import com.primeleaf.krystal.security.ACL;
import com.primeleaf.krystal.web.action.Action;
import com.primeleaf.krystal.web.view.WebView;
import com.primeleaf.krystal.web.view.console.SearchView;

/**
 * Author Rahul Kubadia
 */

public class SearchAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute(HTTPConstants.SESSION_KRYSTAL);
		String searchText = request.getParameter("txtSearch")!=null?request.getParameter("txtSearch"):"";
		searchText = searchText.trim();
		String[] searchWords = searchText.toUpperCase().split("\\s+");
		boolean resultsFound = false;
		long startTime = Calendar.getInstance().getTimeInMillis();
		request.setAttribute("SEARCHTEXT", searchText);
		AccessControlManager aclManager = new AccessControlManager();
		ArrayList<DocumentClass> documentClasses = new ArrayList<DocumentClass>();
		ArrayList<DocumentClass> searchNotesDocumentClasses = new ArrayList<DocumentClass>();
		ArrayList<DocumentClass> matchingDocumentClasses = new ArrayList<DocumentClass>();
		ArrayList<DocumentClass> availableDocumentClasses = DocumentClassDAO.getInstance().readDocumentClasses(" DOCUMENTCLASSES.ACTIVE = 'Y'");
		for(DocumentClass documentClass : availableDocumentClasses){
			ACL acl = aclManager.getACL(documentClass,loggedInUser);
			if(acl.canRead()){
				documentClasses.add(documentClass); //User has permission to access this document class so add to displayed list
				searchNotesDocumentClasses.add(documentClass);
				ArrayList<Hit> results = DocumentSearchManager.getInstance().searchDocuments(documentClass, searchWords);
				if(results.size() > 0 ){
					resultsFound = true;
				}
				for(String searchWord : searchWords){
					if(documentClass.getClassName().toLowerCase().contains(searchWord.toLowerCase()) || documentClass.getClassDescription().toLowerCase().contains(searchWord.toLowerCase())){
						matchingDocumentClasses.add(documentClass);
						resultsFound = true;
						break;
					}
				}
				request.setAttribute(documentClass.getClassId()+"_HITS", results);
			}
		}
		request.setAttribute("MATCHINGDOCUMENTCLASSLIST", matchingDocumentClasses);
		request.setAttribute("DOCUMENTCLASSLIST", documentClasses);
		
		ArrayList<User> userCollection = UserDAO.getInstance().readUsers("");
		ArrayList<DocumentNote> journalNotesList = DocumentNoteDAO.getInstance().searchDocumentNotes(searchText,userCollection,searchNotesDocumentClasses);
		ArrayList<DocumentNote> documentNotes = new ArrayList<DocumentNote>();
		for(DocumentNote documentNote : journalNotesList){
			if(! "P".equalsIgnoreCase(documentNote.getNoteType())){
				if(! loggedInUser.getUserName().equalsIgnoreCase(documentNote.getUserName())){
					continue;
				}
			}
			documentNotes.add(documentNote);
		}
		if(!resultsFound){
			if(documentNotes.size() > 0){
				resultsFound = true;
			}
		}
		if(!resultsFound){
			request.setAttribute(HTTPConstants.REQUEST_ERROR, "No Records Found");
		}
		request.setAttribute("NOTELIST" ,documentNotes);
		long endTime = Calendar.getInstance().getTimeInMillis();
		double executionTime = ((endTime-startTime)/1000.00);
		request.setAttribute("EXECUTIONTIME", executionTime);
		return (new SearchView(request, response));
	}
}

