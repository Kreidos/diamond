/**
 * Created On 05-Jan-2014
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
import kreidos.diamond.model.dao.DocumentClassDAO;
import kreidos.diamond.model.vo.AuditLogRecord;
import kreidos.diamond.model.vo.DocumentClass;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.web.action.Action;
import kreidos.diamond.web.view.WebView;
import kreidos.diamond.web.view.cpanel.EditDocumentClassView;

import org.apache.commons.validator.GenericValidator;


/**
 * Author Rahul Kubadia
 */

public class EditDocumentClassAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute(HTTPConstants.SESSION_KRYSTAL);

		if(request.getMethod().equalsIgnoreCase("POST")){
			try {
				int documentClassId = 0;
				try{
					documentClassId = Integer.parseInt(request.getParameter("classid")!=null?request.getParameter("classid"):"0");
				}catch(Exception e){
					request.setAttribute(HTTPConstants.REQUEST_ERROR,"Invalid input");
					return (new ManageDocumentClassesAction().execute(request, response));
				}
				String className = request.getParameter("txtClassName")!=null?request.getParameter("txtClassName"):"";
				String classDescription = request.getParameter("txtClassDescription")!=null?request.getParameter("txtClassDescription"):"";
				String visible = request.getParameter("radActive")!=null?request.getParameter("radActive"):"N";
				String revisionControl = request.getParameter("radRevisionControl")!=null?request.getParameter("radRevisionControl"):"N";

				//24-Nov-2011 New fields added for controlling the file size and number of documents in the system
				int maximumFileSize = Integer.MAX_VALUE;
				int documentLimit = Integer.MAX_VALUE;

				//10-Sept-2012 New field added for controlling the expiry period
				int expiryPeriod = 0;

				//10-Nov-2012 New field added for controlling the accessibility of the class
				String accessType = request.getParameter("radAccessType")!=null ? request.getParameter("radAccessType"):DocumentClass.ACCESS_TYPE_PUBLIC;
				
				if(!GenericValidator.maxLength(className, 50)){
					request.setAttribute(HTTPConstants.REQUEST_ERROR,"Value too large for Document Class Name");
					return (new ManageDocumentClassesAction().execute(request, response));
				}
				if(!GenericValidator.maxLength(classDescription, 50)){
					request.setAttribute(HTTPConstants.REQUEST_ERROR,"Value too large for Document class description");
					return (new ManageDocumentClassesAction().execute(request, response));
				}
				if(!GenericValidator.isInRange(documentLimit, 10000, 2147483647)){
					request.setAttribute(HTTPConstants.REQUEST_ERROR,  "Invalid input");
					return (new ManageDocumentClassesAction().execute(request, response));
				}
				try{
					expiryPeriod=request.getParameter("txtExpiryPeriod")!=null ? Integer.parseInt(request.getParameter("txtExpiryPeriod")):0;
				}catch(Exception e){
					request.setAttribute(HTTPConstants.REQUEST_ERROR,  "Invalid input");
					return (new ManageDocumentClassesAction().execute(request, response));
				}
				if(!GenericValidator.isInRange(expiryPeriod, 0, 9999)){
					request.setAttribute(HTTPConstants.REQUEST_ERROR,  "Invalid input");
					return (new ManageDocumentClassesAction().execute(request, response));
				}
				
				
				if(!"Y".equals(visible)){
					visible="N";
				}
				if(!"Y".equals(revisionControl)){
					revisionControl="N";
				}
				if(!DocumentClass.ACCESS_TYPE_USER.equals(accessType)){
					accessType=DocumentClass.ACCESS_TYPE_PUBLIC;
				}
				
				DocumentClass documentClass = DocumentClassDAO.getInstance().readDocumentClassById(documentClassId);

				if(documentClass ==null){
					request.setAttribute(HTTPConstants.REQUEST_ERROR,"Document class with same name already exist");
					return (new ManageDocumentClassesAction().execute(request, response));
				}
				//Validate document class name if a class name has changed then check if the similar named class already exist in the database
				if(! documentClass.getClassName().equalsIgnoreCase(className)){
					boolean isDocumentClassExist= DocumentClassDAO.getInstance().validateDocumentClass(className);
					if(isDocumentClassExist){
						request.setAttribute(HTTPConstants.REQUEST_ERROR,"Invalid Document Class");
						return (new ManageDocumentClassesAction().execute(request, response));
					}
				}

				documentClass.setClassId((short)documentClassId);
				documentClass.setClassName(className);
				documentClass.setClassDescription(classDescription);

				//24-Nov-2011 New fields added for controlling the file size and number of documents in the system
				documentClass.setMaximumFileSize(maximumFileSize);
				documentClass.setDocumentLimit(documentLimit);
				
				if("Y".equals(visible)){
					documentClass.setVisible(true);
				}else{
					documentClass.setVisible(false);
				}

				if("Y".equals(revisionControl)){
					documentClass.setRevisionControlEnabled(true);
				}else{
					documentClass.setRevisionControlEnabled(false);
				}

				documentClass.setExpiryPeriod(expiryPeriod); //10-Sep-2012 Rahul Kubadia
				documentClass.setExpiryNotificationPeriod(-1);//08-May-2014 Rahul Kubadia

				DocumentClassDAO.getInstance().updateDocumentClass(documentClass);

				AuditLogManager.log(new AuditLogRecord(
						documentClass.getClassId(),
						AuditLogRecord.OBJECT_DOCUMENTCLASS,
						AuditLogRecord.ACTION_EDITED,
						loggedInUser.getUserName(),
						request.getRemoteAddr(),
						AuditLogRecord.LEVEL_INFO,
						"ID : " +  documentClass.getClassId(),
						"Name : " + documentClass.getClassName() )
						);
				request.setAttribute(HTTPConstants.REQUEST_MESSAGE,"Document class "+ className	+ " updated successfully");
				return (new ManageDocumentClassesAction().execute(request, response));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			int documentClassId  = 0;
			try{
				documentClassId = Integer.parseInt(request.getParameter("classid")!=null?request.getParameter("classid"):"0");
			}catch(Exception e){
				request.setAttribute(HTTPConstants.REQUEST_ERROR,"Invalid input");
				return (new ManageDocumentClassesAction().execute(request, response));
			}
			DocumentClass documentClass = DocumentClassDAO.getInstance().readDocumentClassById(documentClassId);
			if(documentClass == null){
				request.setAttribute(HTTPConstants.REQUEST_ERROR,"Invalid Document Class");
				return (new ManageDocumentClassesAction().execute(request, response));
			}
			request.setAttribute("DOCUMENTCLASS", documentClass);
		}
		return (new EditDocumentClassView(request, response));
	}
}

