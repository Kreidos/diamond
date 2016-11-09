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

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.validator.GenericValidator;

import com.primeleaf.krystal.constants.HTTPConstants;
import com.primeleaf.krystal.model.AccessControlManager;
import com.primeleaf.krystal.model.AuditLogManager;
import com.primeleaf.krystal.model.DocumentManager;
import com.primeleaf.krystal.model.dao.DocumentClassDAO;
import com.primeleaf.krystal.model.vo.AuditLogRecord;
import com.primeleaf.krystal.model.vo.DocumentClass;
import com.primeleaf.krystal.model.vo.DocumentRevision;
import com.primeleaf.krystal.model.vo.IndexDefinition;
import com.primeleaf.krystal.model.vo.User;
import com.primeleaf.krystal.security.ACL;
import com.primeleaf.krystal.util.FileUploadProgressListener;
import com.primeleaf.krystal.web.action.Action;
import com.primeleaf.krystal.web.view.WebView;
import com.primeleaf.krystal.web.view.console.NewDocumentView;

/**
 * Author Rahul Kubadia
 */

public class NewDocumentAction implements Action {
	@SuppressWarnings("rawtypes")
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute(HTTPConstants.SESSION_KRYSTAL);
		String classId = request.getParameter("classid")!=null? request.getParameter("classid"):"0";
		
		if(request.getMethod().equalsIgnoreCase("POST")){
			try{
				String userName = loggedInUser.getUserName();
				String sessionid = (String)session.getId();

				String tempFilePath = System.getProperty("java.io.tmpdir");

				if ( !(tempFilePath.endsWith("/") || tempFilePath.endsWith("\\")) ){
					tempFilePath += System.getProperty("file.separator");
				}
				tempFilePath+=  userName+"_"+sessionid;

				//variables
				String fileName="",ext="",comments="";
				File file =null;
				// Create a factory for disk-based file items
				FileItemFactory factory = new DiskFileItemFactory();
				// Create a new file upload handler
				ServletFileUpload upload = new ServletFileUpload(factory);
				upload.setHeaderEncoding(HTTPConstants.CHARACTER_ENCODING);

				//Create a file upload progress listener
				FileUploadProgressListener listener = new FileUploadProgressListener();
				upload.setProgressListener(listener);
				//put the listener in session
				session.setAttribute("LISTENER", listener);
				session.setAttribute("UPLOAD_ERROR", null);
				session.setAttribute("UPLOAD_PERCENT_COMPLETE", new Long(0));

				DocumentClass documentClass = null;

				Hashtable<String,String> indexRecord = new Hashtable<String,String>();
				String name="";
				String value="";

				List listItems = upload.parseRequest((HttpServletRequest)request);

				Iterator iter = listItems.iterator();
				FileItem fileItem = null;
				while (iter.hasNext()){
					fileItem = (FileItem) iter.next();
					if (fileItem.isFormField()){
						name = fileItem.getFieldName();
						value = fileItem.getString(HTTPConstants.CHARACTER_ENCODING);
						if(name.equals("classid")){
							classId=value;
						}
						if (name.equals("txtNote")){
							comments = value;				   
						}
					}else{
						try{
							fileName = fileItem.getName();  	
							file = new File(fileName);
							fileName = file.getName();
							ext = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
							file=new File(tempFilePath+"."+ext);				    
							fileItem.write(file);
						}catch(Exception ex){
							session.setAttribute("UPLOAD_ERROR",ex.getLocalizedMessage());
							return null;
						}
					}
				}//if

				if( file.length() <= 0 )	{ //code for checking minimum size of file
					session.setAttribute("UPLOAD_ERROR","Zero length document");
					return null;
				}
				documentClass = DocumentClassDAO.getInstance().readDocumentClassById(Integer.parseInt(classId));
				if(documentClass == null){
					session.setAttribute("UPLOAD_ERROR","Invalid document class");
					return null;
				}
				AccessControlManager aclManager = new AccessControlManager();
				ACL acl = aclManager.getACL(documentClass, loggedInUser);

				if(! acl.canCreate()){
					session.setAttribute("UPLOAD_ERROR","Access Denied");
					return null;
				}

				String indexValue="";
				String indexName="";
				session.setAttribute("UPLOAD_PERCENT_COMPLETE",  new Long(50));

				for(IndexDefinition indexDefinition : documentClass.getIndexDefinitions()){
					indexName=indexDefinition.getIndexColumnName();
					Iterator iter1 = listItems.iterator();
					while (iter1.hasNext()){
						FileItem item1 = (FileItem) iter1.next();
						if (item1.isFormField()){
							name = item1.getFieldName();
							value = item1.getString(HTTPConstants.CHARACTER_ENCODING);
							if(name.equals(indexName)){
								indexValue=value;
								String errorMessage = "";
								if(indexValue != null){
									if(indexDefinition.isMandatory()){
										if(indexValue.trim().length() <=0){
											errorMessage ="Invalid input for "  + indexDefinition.getIndexDisplayName();
											session.setAttribute("UPLOAD_ERROR",errorMessage);
											return null;
										}
									}
									if(IndexDefinition.INDEXTYPE_NUMBER.equalsIgnoreCase(indexDefinition.getIndexType())){
										if(indexValue.trim().length() > 0){
											if(!GenericValidator.matchRegexp(indexValue, HTTPConstants.NUMERIC_REGEXP)){
												errorMessage ="Invalid input for "  + indexDefinition.getIndexDisplayName();
												session.setAttribute("UPLOAD_ERROR",errorMessage);
												return null;
											}
										}
									}else if(IndexDefinition.INDEXTYPE_DATE.equalsIgnoreCase(indexDefinition.getIndexType())){
										if(indexValue.trim().length() > 0){
											if(!GenericValidator.isDate(indexValue, "yyyy-MM-dd",true)){
												errorMessage = "Invalid input for "  + indexDefinition.getIndexDisplayName();
												session.setAttribute("UPLOAD_ERROR",errorMessage);
												return null;
											}
										}
									}
									if (indexValue.trim().length() > indexDefinition.getIndexMaxLength()){ //code for checking index field length
										 errorMessage = 	"Document index size exceeded for " +
												"Index Name : " +
												indexDefinition.getIndexDisplayName() + " [ " +
												"Index Length : " + indexDefinition.getIndexMaxLength() + " , " +
												"Actual Length : " + indexValue.length() + " ]" ;
										session.setAttribute("UPLOAD_ERROR",errorMessage);
										return null;
									}
								}
								indexRecord.put(indexName,indexValue);
							}
						}
					}//while iter
				}//while indexCfgList
				session.setAttribute("UPLOAD_PERCENT_COMPLETE", new Long(70));

				DocumentRevision documentRevision = new DocumentRevision();
				documentRevision.setClassId(documentClass.getClassId());
				documentRevision.setDocumentId(0);
				documentRevision.setRevisionId("1.0");
				documentRevision.setDocumentFile(file);
				documentRevision.setUserName(loggedInUser.getUserName());
				documentRevision.setIndexRecord(indexRecord);
				documentRevision.setComments(comments);

				DocumentManager documentManager = new DocumentManager();
				documentManager.storeDocument(documentRevision, documentClass);

				//Log the entry to audit logs 
				AuditLogManager.log(new AuditLogRecord(
						documentRevision.getDocumentId(),
						AuditLogRecord.OBJECT_DOCUMENT,
						AuditLogRecord.ACTION_CREATED,
						userName,
						request.getRemoteAddr(),
						AuditLogRecord.LEVEL_INFO,
						"",
						"Document created"));

				session.setAttribute("UPLOAD_PERCENT_COMPLETE",  new Long(100));
			}catch (Exception e) {
				e.printStackTrace(System.out);
			}
			return null;
		}else{
			try{
				ArrayList<DocumentClass> availableDocumentClasses = DocumentClassDAO.getInstance().readDocumentClasses(" ACTIVE = 'Y'");
				ArrayList<DocumentClass> documentClasses = new ArrayList<DocumentClass>();
				AccessControlManager aclManager = new AccessControlManager();
				for(DocumentClass documentClass : availableDocumentClasses){
					ACL acl = aclManager.getACL(documentClass, loggedInUser);
					if(acl.canCreate()){
						documentClasses.add(documentClass);
					}
				}
				int documentClassId = 0;
				try{
					documentClassId = Integer.parseInt(classId);
				}catch(Exception ex){
					request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid input");
					return (new NewDocumentView(request, response));
				}
				if(documentClassId > 0 ){
					DocumentClass selectedDocumentClass = DocumentClassDAO.getInstance().readDocumentClassById(documentClassId);
					request.setAttribute("DOCUMENTCLASS", selectedDocumentClass);
				}
				request.setAttribute("CLASSID", documentClassId);
				request.setAttribute("CLASSLIST", documentClasses);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return (new NewDocumentView(request, response));
	}
}

