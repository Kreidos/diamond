/**
 * Created On 28-Nov-2014
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.primeleaf.krystal.constants.HTTPConstants;
import com.primeleaf.krystal.model.AccessControlManager;
import com.primeleaf.krystal.model.DocumentManager;
import com.primeleaf.krystal.model.dao.DocumentClassDAO;
import com.primeleaf.krystal.model.dao.DocumentDAO;
import com.primeleaf.krystal.model.dao.DocumentRevisionDAO;
import com.primeleaf.krystal.model.vo.Document;
import com.primeleaf.krystal.model.vo.DocumentClass;
import com.primeleaf.krystal.model.vo.DocumentRevision;
import com.primeleaf.krystal.model.vo.Hit;
import com.primeleaf.krystal.model.vo.User;
import com.primeleaf.krystal.security.ACL;
import com.primeleaf.krystal.web.action.Action;
import com.primeleaf.krystal.web.view.WebView;
import com.primeleaf.krystal.web.view.console.DownloadDocumentView;

/**
 * Author Rahul Kubadia
 */

public class BulkDownloadAction implements Action {
	static final int BUFFER = 2048;

	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute(HTTPConstants.SESSION_KRYSTAL);
		try{
			DocumentClass documentClass  = null;
			AccessControlManager aclManager = new AccessControlManager();
			DocumentManager documentManager = new DocumentManager();
			Document document = null;
			DocumentRevision documentRevision;
			ACL acl = null;
			String[] documentids = request.getParameterValues("chkDocumentId");
			for(String documentID  : documentids){
				int documentId = 0;
				try{
					documentId=Integer.parseInt(documentID);
				}catch(Exception e){
					request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid input");
					return (new DownloadDocumentView(request,response));
				}
				
				document =  DocumentDAO.getInstance().readDocumentById(documentId);
				if(document == null){
					request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid document");
					return (new DownloadDocumentView(request,response));
				}
				
				if(Hit.STATUS_DELETED.equalsIgnoreCase(document.getStatus())){
					request.setAttribute(HTTPConstants.REQUEST_ERROR,  "Invalid document");
					return (new DownloadDocumentView(request,response));
				}
				
				if(documentClass == null){
					documentClass = DocumentClassDAO.getInstance().readDocumentClassById(document.getClassId());
				}
				
				acl = aclManager.getACL(documentClass, loggedInUser);
				
				if(! acl.canDownload()){
					request.setAttribute(HTTPConstants.REQUEST_ERROR, "Access Denied");
					return (new DownloadDocumentView(request,response));
				}

			}

			byte data[] = new byte[BUFFER];
			BufferedInputStream origin = null;
			String fileName = documentClass.getClassName() + ".zip";		
			File zipFile = new File( System.getProperty("java.io.tmpdir") + File.separator + session.getLastAccessedTime() + "_" +  fileName );
			FileOutputStream dest = new FileOutputStream(zipFile);
			@SuppressWarnings("resource")
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			for(String documentID  : documentids){
				int documentId = 0;
				try{
					documentId=Integer.parseInt(documentID);
				}catch(Exception e){
					request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid input");
					return (new DownloadDocumentView(request,response));
				}
				document =  DocumentDAO.getInstance().readDocumentById(documentId);
				documentRevision = DocumentRevisionDAO.getInstance().readDocumentRevisionById(documentId, document.getRevisionId());
				document.setRevisionId(documentRevision.getRevisionId());
				documentRevision = documentManager.retreiveDocument(document);
				File downloadFile = new File(documentRevision.getDocumentFile().getAbsolutePath());
				FileInputStream fis = new FileInputStream(downloadFile);
				origin = new BufferedInputStream(fis, BUFFER);
				String curName = null;
				if(document.getFilename() == null){ //catch for old pre-1.1 files
					curName = "Krystal_" + documentRevision.getDocumentId() + "_" + documentRevision.getRevisionId().replace('.','_') + "_Obj." + document.getExtension().toUpperCase();
				}else {
					curName = document.getFilename() + "." + document.getExtension().toLowerCase(); 
				}
				ZipEntry entry = new ZipEntry(curName);
				out.putNextEntry(entry);
				int count;
				while((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
				fis.close();
			}
			out.close();
			
			ServletContext servletContext = request.getServletContext();
			String mimeType = servletContext.getMimeType(fileName);
			FileInputStream fis = new FileInputStream(zipFile);
			response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");
			response.setContentType(mimeType);
			int fileSize = (int) zipFile.length();
			response.setContentLength(fileSize);
			OutputStream os = response.getOutputStream();
			byte buf[] = new byte[fileSize];
			fis.read(buf);
			os.write(buf, 0, fileSize);
			os.flush();
			os.close();
			fis.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}

