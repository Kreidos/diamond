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
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kreidos.diamond.constants.HTTPConstants;
import kreidos.diamond.model.AccessControlManager;
import kreidos.diamond.model.AuditLogManager;
import kreidos.diamond.model.IndexRecordManager;
import kreidos.diamond.model.dao.DocumentClassDAO;
import kreidos.diamond.model.dao.DocumentDAO;
import kreidos.diamond.model.vo.AuditLogRecord;
import kreidos.diamond.model.vo.Document;
import kreidos.diamond.model.vo.DocumentClass;
import kreidos.diamond.model.vo.Hit;
import kreidos.diamond.model.vo.IndexDefinition;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.security.ACL;
import kreidos.diamond.util.StringHelper;
import kreidos.diamond.web.action.Action;
import kreidos.diamond.web.view.WebView;
import kreidos.diamond.web.view.console.AJAXResponseView;

import org.apache.commons.validator.GenericValidator;


/**
 * @author Rahul Kubadia
 */

public class EditDocumentIndexesAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute(HTTPConstants.SESSION_KRYSTAL);
		try{
			int documentId = 0;
			try{
				documentId = Integer.parseInt(request.getParameter("documentid"));
			}catch (Exception e) {
				request.setAttribute(HTTPConstants.REQUEST_ERROR,"Invalid input");
				return (new AJAXResponseView(request,response) );
			}
			//	read HTTP request parameters
			String revisionId=request.getParameter("revisionid");

			Document document = DocumentDAO.getInstance().readDocumentById(documentId);

			if(document == null){
				request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid document");
				return (new AJAXResponseView(request,response) );
			}
			if(Hit.STATUS_LOCKED.equalsIgnoreCase(document.getStatus())){
				request.setAttribute(HTTPConstants.REQUEST_ERROR, "Document already checkout");
				return (new AJAXResponseView(request,response) );
			}
			AccessControlManager aclManager = new AccessControlManager();
			
			DocumentClass documentClass = DocumentClassDAO.getInstance().readDocumentClassById(document.getClassId());
			ACL acl = aclManager.getACL(documentClass, loggedInUser);

			if(! acl.canWrite()){
				request.setAttribute(HTTPConstants.REQUEST_ERROR,"Access Denied");
				return (new AJAXResponseView(request,response) );
			}
			boolean isHeadRevision = false;
			if(document.getRevisionId().equalsIgnoreCase(revisionId)){
				isHeadRevision = true;
			}
			if(!isHeadRevision){
				request.setAttribute(HTTPConstants.REQUEST_ERROR,"Access Denied");
				aclManager = null;		 			
				return (new AJAXResponseView(request,response) );
			}
			//local variables
			String indexName="",indexValue="";
			String expiryDate = request.getParameter("txtExpiryDate")!=null? request.getParameter("txtExpiryDate"):"";
			Hashtable<String,String> indexRecord = new Hashtable<String,String>();

			for(IndexDefinition indexDefinition :documentClass.getIndexDefinitions()){
				indexName = indexDefinition.getIndexColumnName();
				indexValue = request.getParameter(indexName);
				if(indexValue != null){
					String errorMessage = "";
					if(indexDefinition.isMandatory()){
						if(indexValue.trim().length() <=0){
							errorMessage =  "Invalid input for "  + indexDefinition.getIndexDisplayName();
							request.setAttribute(HTTPConstants.REQUEST_ERROR,errorMessage);
							return (new AJAXResponseView(request,response) );
						}
					}
					if(IndexDefinition.INDEXTYPE_NUMBER.equalsIgnoreCase(indexDefinition.getIndexType())){
						if(indexValue.trim().length() > 0){
							if(!GenericValidator.matchRegexp(indexValue, HTTPConstants.NUMERIC_REGEXP)){
								errorMessage = "Invalid input for "  + indexDefinition.getIndexDisplayName();
								request.setAttribute(HTTPConstants.REQUEST_ERROR,errorMessage);
								return (new AJAXResponseView(request,response) );
							}
						}
					}else if(IndexDefinition.INDEXTYPE_DATE.equalsIgnoreCase(indexDefinition.getIndexType())){
						if(indexValue.trim().length() > 0){
							if(!GenericValidator.isDate(indexValue, "yyyy-MM-dd",true)){
								errorMessage = "Invalid input for "  + indexDefinition.getIndexDisplayName();
								request.setAttribute(HTTPConstants.REQUEST_ERROR,errorMessage);
								return (new AJAXResponseView(request,response) );
							}
						}
					}
					if (indexValue.trim().length() > indexDefinition.getIndexMaxLength()){ //code for checking maximum length of index field
						errorMessage = 	"Document index length exceeded " +
								"Index Name : " +
								indexDefinition.getIndexDisplayName() + " [ " +
								"Index Length : " + indexDefinition.getIndexMaxLength() + " , " +
								"Actual Length : " + indexValue.length() + " ]" ;

						request.setAttribute(HTTPConstants.REQUEST_ERROR,errorMessage);
						return (new AJAXResponseView(request,response) );
					}
				}else{
					indexValue = "";
				}
				indexRecord.put(indexName,indexValue);
			}

			IndexRecordManager.getInstance().updateIndexRecord(documentClass,documentId,revisionId,indexRecord);
			//Update document details like access count last modified etc

			Timestamp modified = new Timestamp(Calendar.getInstance().getTime().getTime());
			document.setStatus(Hit.STATUS_AVAILABLE);
			document.setModified(modified);
			document.setLastAccessed(modified);
			document.setAccessCount(document.getAccessCount() + 1);
			document.setModifiedBy(loggedInUser.getUserName());

			if(expiryDate.trim().length() > 0){
				Timestamp expiry  = new Timestamp(StringHelper.getDate(expiryDate).getTime());
				document.setExpiry(expiry);
			}else{
				document.setExpiry(null);
			}

			DocumentDAO.getInstance().updateDocument(document);


			//Log the entry to audit logs 
			String userName=loggedInUser.getUserName();
			AuditLogManager.log(new AuditLogRecord(documentId,AuditLogRecord.OBJECT_DOCUMENT,AuditLogRecord.ACTION_EDITED,userName,request.getRemoteAddr(),AuditLogRecord.LEVEL_INFO,"Revision No :"+revisionId));

			request.setAttribute(HTTPConstants.REQUEST_MESSAGE,"Indexes updated successfully");

		}catch(Exception e){
			e.printStackTrace();
		}
		return (new AJAXResponseView(request,response) );
	}
}

