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

package kreidos.diamond.web.action.console;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kreidos.diamond.constants.HTTPConstants;
import kreidos.diamond.model.AuditLogManager;
import kreidos.diamond.model.dao.BookmarkDAO;
import kreidos.diamond.model.dao.DocumentDAO;
import kreidos.diamond.model.dao.DocumentRevisionDAO;
import kreidos.diamond.model.vo.AuditLogRecord;
import kreidos.diamond.model.vo.Bookmark;
import kreidos.diamond.model.vo.Document;
import kreidos.diamond.model.vo.DocumentRevision;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.web.action.Action;
import kreidos.diamond.web.view.WebView;
import kreidos.diamond.web.view.console.AJAXResponseView;

import org.apache.commons.validator.GenericValidator;


/**
 * Author Rahul Kubadia
 */

public class NewBookmarkAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute(HTTPConstants.SESSION_KRYSTAL);
		
		String documentid=request.getParameter("documentid")!=null?request.getParameter("documentid"):"0";
		String revisionid=request.getParameter("revisionid")!=null?request.getParameter("revisionid"):"0";
		
		request.setAttribute("DOCUMENTID" ,documentid);
		request.setAttribute("REVISIONID" ,revisionid);
		
		if(request.getMethod().equalsIgnoreCase("POST")){
			try {
				String bookmarkName = request.getParameter("txtBookmarkName");
				int documentId = 0;
				try{
					documentId = Integer.parseInt(documentid);
					Double.parseDouble(revisionid);
				}catch(Exception e){
					request.setAttribute(HTTPConstants.REQUEST_ERROR,"Invalid input");
					return (new AJAXResponseView(request, response));
				}
				if(! GenericValidator.maxLength(bookmarkName, 50)){
					request.setAttribute(HTTPConstants.REQUEST_ERROR,"Value too large for bookmark name");
					return (new AJAXResponseView(request, response));
				}

				Document document = DocumentDAO.getInstance().readDocumentById(documentId);
				if(document == null){
					request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid input");
					return (new AJAXResponseView(request, response));
				}
				DocumentRevision documentRevision = DocumentRevisionDAO.getInstance().readDocumentRevisionById(documentId,revisionid); 
				if(documentRevision == null){
					request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid document");
					return (new AJAXResponseView(request, response));
				}

				Bookmark bookmark = new Bookmark();
				bookmark.setDocumentId(documentId);
				bookmark.setRevisionId(revisionid);
				bookmark.setBookmarkName(bookmarkName);
				bookmark.setUserName(loggedInUser.getUserName());
				BookmarkDAO.getInstance().addBookmark(bookmark);
				AuditLogManager.log(new AuditLogRecord(
						bookmark.getBookmarkId(),
						AuditLogRecord.OBJECT_BOOKMARK,
						AuditLogRecord.ACTION_CREATED,
						loggedInUser.getUserName(),
						request.getRemoteAddr(),
						AuditLogRecord.LEVEL_INFO,"",
						"Name : " + bookmarkName )
						);
				request.setAttribute(HTTPConstants.REQUEST_MESSAGE,"Bookmark "+ bookmarkName+" added successfully");
				return (new AJAXResponseView(request, response));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return (new HomeAction().execute(request, response));
	}
}