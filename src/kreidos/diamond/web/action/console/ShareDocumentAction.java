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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kreidos.diamond.constants.HTTPConstants;
import kreidos.diamond.model.AuditLogManager;
import kreidos.diamond.model.DocumentManager;
import kreidos.diamond.model.dao.DocumentDAO;
import kreidos.diamond.model.dao.DocumentRevisionDAO;
import kreidos.diamond.model.vo.AuditLogRecord;
import kreidos.diamond.model.vo.Document;
import kreidos.diamond.model.vo.DocumentRevision;
import kreidos.diamond.model.vo.SharedDocument;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.util.EmailMessage;
import kreidos.diamond.web.action.Action;
import kreidos.diamond.web.view.WebView;
import kreidos.diamond.web.view.console.AJAXResponseView;

import org.apache.commons.validator.GenericValidator;


/**
 * @author Rahul Kubadia
 */

public class ShareDocumentAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute(HTTPConstants.SESSION_KRYSTAL);
		try{
			String documentid = request.getParameter("documentid")!=null?request.getParameter("documentid"):"";
			String revisionid = request.getParameter("revisionid")!=null?request.getParameter("revisionid"):"";
			String emailId =  request.getParameter("txtEmail")!=null?request.getParameter("txtEmail"):"";
			String userComments = request.getParameter("txtComments")!=null?request.getParameter("txtComments"):"";
			String emailIDs[]  = emailId.split(",");

			for(String emailAddress : emailIDs){
				if(! GenericValidator.isEmail(emailAddress)){
					request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid input");
					return (new AJAXResponseView(request,response) );
				}
			}

			int documentId = Integer.parseInt(documentid);
			Document document = DocumentDAO.getInstance().readDocumentById(documentId);
			if(document == null){
				request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid document");
				return (new AJAXResponseView(request,response) );
			}
			DocumentRevision documentRevision = DocumentRevisionDAO.getInstance().readDocumentRevisionById(documentId, revisionid);
			if(documentRevision == null){
				request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid document");
				return (new AJAXResponseView(request,response) );
			}
			document.setRevisionId(revisionid);
			for(String emailAddress : emailIDs){
				SharedDocument sharedDocument = new SharedDocument();

				sharedDocument.setUsername(loggedInUser.getUserName());
				sharedDocument.setObjectId(documentId);
				sharedDocument.setRevisionId(revisionid);
				sharedDocument.setEmailId(emailAddress);
				String comments = "";

				EmailMessage emailMessage = new EmailMessage();
				emailMessage.setSubject("Shared Document");
				emailMessage.setFrom(loggedInUser.getUserEmail());

				StringBuffer message = new StringBuffer();
				message.append("Hello,");
				message.append("<p>" + loggedInUser.getRealName() + " has shared a document with you.</p>");
				emailMessage.setTo(emailAddress);

				sharedDocument.setValidity(Integer.MAX_VALUE);
				DocumentManager dm = new DocumentManager();
				documentRevision = dm.retreiveDocument(document);
				emailMessage.setAttachmentFile(documentRevision.getDocumentFile());
				message.append("<p>Please find attached document.</p>");

				if(userComments.trim().length()>0){//User has put some comments
					message.append("<p>Message from :" + loggedInUser.getRealName() + "</p>");
					message.append("<p><i>"+userComments+"</i></p>");
				}

				message.append("<p>Regards,<br/>DMS Administrator</p>");
				emailMessage.setMessage(message.toString());
				emailMessage.send();	

				document = DocumentDAO.getInstance().readDocumentById(documentId);
				Date lastAccessed = new Date();
				document.setLastAccessed(new java.sql.Timestamp(lastAccessed.getTime()));
				DocumentDAO.getInstance().updateDocument(document);

				AuditLogManager.log(new AuditLogRecord(
						Integer.parseInt(documentid),
						AuditLogRecord.OBJECT_DOCUMENT,
						AuditLogRecord.ACTION_SHARED,
						loggedInUser.getUserName(),
						request.getRemoteAddr(),
						AuditLogRecord.LEVEL_INFO,
						emailAddress,
						comments)
						);
			}
			request.setAttribute(HTTPConstants.REQUEST_MESSAGE, "Document shared successfully");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return (new AJAXResponseView(request,response) );

	}

}