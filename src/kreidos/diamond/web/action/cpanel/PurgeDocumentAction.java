/**
 * Created On 09-Jan-2014
 * Copyright 2014 by Primeleaf Consulting (P) Ltd.,
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

package kreidos.diamond.web.action.cpanel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kreidos.diamond.constants.HTTPConstants;
import kreidos.diamond.model.AuditLogManager;
import kreidos.diamond.model.DocumentManager;
import kreidos.diamond.model.PropertiesManager;
import kreidos.diamond.model.dao.DocumentClassDAO;
import kreidos.diamond.model.dao.DocumentDAO;
import kreidos.diamond.model.vo.AuditLogRecord;
import kreidos.diamond.model.vo.Document;
import kreidos.diamond.model.vo.DocumentClass;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.web.action.Action;
import kreidos.diamond.web.view.UnauthorizedOrInvalidAccessView;
import kreidos.diamond.web.view.WebView;


/**
 * Author Rahul Kubadia
 */

public class PurgeDocumentAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute(HTTPConstants.SESSION_KRYSTAL);
		String documentid = request.getParameter("documentid")!=null?request.getParameter("documentid"):"0";
		int documentId = 0;
		try{
			documentId = Integer.parseInt(documentid);
		}catch(Exception ex){
			request.setAttribute(HTTPConstants.REQUEST_ERROR,"Invalid input");
			return (new RecycleBinAction().execute(request, response));
		}
		Document document = DocumentDAO.getInstance().readDocumentById(documentId);
		if(document == null){
			request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid document");
			return (new UnauthorizedOrInvalidAccessView(request, response));
		}
		
		if(PropertiesManager.getInstance().getPropertyValue("storage") == "folder")
			DocumentManager.deleteDocumentFromFolder(document);
		DocumentDAO.getInstance().deleteDocument(document);
		
		DocumentClass documentClass = DocumentClassDAO.getInstance().readDocumentClassById(document.getClassId());
		DocumentClassDAO.getInstance().decreaseDocumentCount(documentClass);
		
		AuditLogManager.log(new AuditLogRecord(
				documentId,
				AuditLogRecord.OBJECT_DOCUMENT,
				AuditLogRecord.ACTION_DELETED,
				loggedInUser.getUserName(),
				request.getRemoteAddr(),
				AuditLogRecord.LEVEL_INFO,"", "Document ID : " + documentId + " permanently deleted")
		);
		request.setAttribute(HTTPConstants.REQUEST_MESSAGE, "Document permanently deleted successfully");
		return (new RecycleBinAction().execute(request, response));
	}
}

