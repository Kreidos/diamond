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

import java.sql.Timestamp;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kreidos.diamond.constants.HTTPConstants;
import kreidos.diamond.model.AccessControlManager;
import kreidos.diamond.model.AuditLogManager;
import kreidos.diamond.model.dao.DocumentClassDAO;
import kreidos.diamond.model.dao.DocumentDAO;
import kreidos.diamond.model.vo.AuditLogRecord;
import kreidos.diamond.model.vo.Document;
import kreidos.diamond.model.vo.DocumentClass;
import kreidos.diamond.model.vo.Hit;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.security.ACL;
import kreidos.diamond.web.action.Action;
import kreidos.diamond.web.view.WebView;
import kreidos.diamond.web.view.console.DeleteDocumentView;


/**
 * Author Rahul Kubadia
 */

public class DeleteDocumentAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute(HTTPConstants.SESSION_KRYSTAL);
		try{
			int documentId = 0;
			try{
				documentId=Integer.parseInt(request.getParameter("documentid")!=null?request.getParameter("documentid"):"0");
			}catch(Exception e){
				request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid input");
				return (new DeleteDocumentView(request,response));
			}
			Document document =  DocumentDAO.getInstance().readDocumentById(documentId);
			if(document == null){
				request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid document");
				return (new DeleteDocumentView(request,response));
			}
			
			if(Hit.STATUS_LOCKED.equalsIgnoreCase(document.getStatus())){
				request.setAttribute(HTTPConstants.REQUEST_ERROR,  "Delete failed. Document is checked out");
				return (new DeleteDocumentView(request,response));
			}
			if(!Hit.STATUS_AVAILABLE.equalsIgnoreCase(document.getStatus())){
				request.setAttribute(HTTPConstants.REQUEST_ERROR,  "Invalid document");
				return (new DeleteDocumentView(request,response));
			}

			AccessControlManager aclManager = new AccessControlManager();
			
			DocumentClass documentClass = DocumentClassDAO.getInstance().readDocumentClassById(document.getClassId());
			ACL acl = aclManager.getACL(documentClass, loggedInUser);

			if(! acl.canDelete()){
				request.setAttribute(HTTPConstants.REQUEST_ERROR, "Access Denied");
				return (new DeleteDocumentView(request,response));
			}

			document.setStatus(Hit.STATUS_DELETED);
			Timestamp modified = new Timestamp(Calendar.getInstance().getTime().getTime());
			document.setModified(modified);
			document.setModifiedBy(loggedInUser.getUserName());
			DocumentDAO.getInstance().updateDocument(document);
			request.setAttribute(HTTPConstants.REQUEST_MESSAGE, "Document deleted successfully");
			
			//Decrease the document count in the document class
			DocumentClassDAO.getInstance().decreaseActiveDocumentCount(documentClass);
			
			String userName = loggedInUser.getUserName();
			AuditLogManager.log(new AuditLogRecord(
					documentId,
					AuditLogRecord.OBJECT_DOCUMENT,
					AuditLogRecord.ACTION_DELETED,
					userName,
					request.getRemoteAddr(),
					AuditLogRecord.LEVEL_INFO,
					"",
					"Document ID  " + documentId + " deleted"));

		}catch(Exception e){
			e.printStackTrace();
		}
		return (new DeleteDocumentView(request,response));
	}
}

