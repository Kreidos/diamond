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

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kreidos.diamond.constants.HTTPConstants;
import kreidos.diamond.model.AccessControlManager;
import kreidos.diamond.model.AuditLogManager;
import kreidos.diamond.model.DocumentManager;
import kreidos.diamond.model.dao.DocumentClassDAO;
import kreidos.diamond.model.dao.DocumentDAO;
import kreidos.diamond.model.dao.DocumentRevisionDAO;
import kreidos.diamond.model.vo.AuditLogRecord;
import kreidos.diamond.model.vo.Document;
import kreidos.diamond.model.vo.DocumentClass;
import kreidos.diamond.model.vo.DocumentRevision;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.security.ACL;
import kreidos.diamond.web.action.Action;
import kreidos.diamond.web.view.WebView;
import kreidos.diamond.web.view.console.DownloadDocumentView;


/**
 * Author Rahul Kubadia
 */

public class DocumentViewerAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute(HTTPConstants.SESSION_KRYSTAL);
		try{
			int documentId = 0;
			try{
				documentId=Integer.parseInt(request.getParameter("documentid")!=null?request.getParameter("documentid"):"0");
			}catch(Exception e){
				request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid input");
				return (new DownloadDocumentView(request,response));
			}
			Document document =  DocumentDAO.getInstance().readDocumentById(documentId);
			if(document == null){
				request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid document");
				return (new DownloadDocumentView(request,response));
			}

			String revisionid = request.getParameter("revisionid")!=null?request.getParameter("revisionid"):document.getRevisionId();
			DocumentRevision documentRevision = DocumentRevisionDAO.getInstance().readDocumentRevisionById(documentId, revisionid);

			if(documentRevision == null){
				request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid document");
				return (new DownloadDocumentView(request,response));
			}
			AccessControlManager aclManager = new AccessControlManager();
			DocumentClass documentClass = DocumentClassDAO.getInstance().readDocumentClassById(document.getClassId());
			ACL acl = aclManager.getACL(documentClass,loggedInUser);
			if(document.getClassId() > 1 ){ //check access only for classes other than attachments
				if(! acl.canDownload()){
					request.setAttribute(HTTPConstants.REQUEST_ERROR, "Access Denied");
					return (new DownloadDocumentView(request,response));
				}
			}
			String basePath = System.getProperty("java.io.tmpdir") ;
			if ( !(basePath.endsWith("/") || basePath.endsWith("\\")) ){
				basePath +=  System.getProperty("file.separator");
			}
			document.setAccessCount(document.getAccessCount()+1);
			Date lastAccessed = new Date();
			document.setLastAccessed(new java.sql.Timestamp(lastAccessed.getTime()));
			DocumentDAO.getInstance().updateDocument(document);

			// Initialize retrieval operation
			DocumentManager documentManager = new DocumentManager();
			document.setRevisionId(documentRevision.getRevisionId());
			documentRevision = documentManager.retreiveDocument(document);
			
			String fileName = document.getFullFilename();		
			
			ServletContext servletContext = request.getServletContext();
			String targetName = "";
			File downloadFile = new File(documentRevision.getDocumentFile().getAbsolutePath());
			FileInputStream fis = new FileInputStream(downloadFile);
			int fileSize = fis.available();
			String mimeType = servletContext.getMimeType(targetName.toLowerCase());
			response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");
			response.setContentType(mimeType);
			response.setContentLength(fileSize);
			OutputStream os = response.getOutputStream();
			byte buf[] = new byte[fileSize];
			fis.read(buf);
			os.write(buf, 0, fileSize);
			os.flush();
			os.close();
			fis.close();

			AuditLogManager.log(new AuditLogRecord(
					documentId,
					AuditLogRecord.OBJECT_DOCUMENT,
					AuditLogRecord.ACTION_ACCESS,
					loggedInUser.getUserName(),
					request.getRemoteAddr(),
					AuditLogRecord.LEVEL_INFO,
					"",
					"Document ID  : " + documentId + " accessed"));
			return null;
		}catch(Exception e){
			e.printStackTrace();
		}
		return (new DownloadDocumentView(request,response));
	}
}

