/**
 * Created On 27-Jan-2014
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

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kreidos.diamond.constants.HTTPConstants;
import kreidos.diamond.model.AuditLogManager;
import kreidos.diamond.model.dao.DocumentClassDAO;
import kreidos.diamond.model.dao.PermissionDAO;
import kreidos.diamond.model.dao.UserDAO;
import kreidos.diamond.model.vo.AuditLogRecord;
import kreidos.diamond.model.vo.DocumentClass;
import kreidos.diamond.model.vo.Permission;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.security.ACL;
import kreidos.diamond.web.action.Action;
import kreidos.diamond.web.view.WebView;


/**
 * @author Rahul Kubadia
 */

public class SetPermissionsAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute(HTTPConstants.SESSION_KRYSTAL);
		ArrayList<User> userList =  UserDAO.getInstance().readUsers("USERID > 0");	
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
		try{
			
			PermissionDAO.getInstance().delete(documentClassId); //Delete all existing permissions first
			
			for (User user : userList){
				ACL acl = new ACL();
				acl.setCreate(request.getParameter("cbCreate_"+ user.getUserId()) != null ? true : false);
				acl.setRead(request.getParameter("cbRead_"+ user.getUserId()) != null ? true : false);
				acl.setWrite(request.getParameter("cbWrite_"+ user.getUserId()) != null ? true : false);
				acl.setDelete(request.getParameter("cbDelete_"+ user.getUserId()) != null ? true : false);
				acl.setPrint(request.getParameter("cbPrint_"+ user.getUserId()) != null ? true : false);
				acl.setEmail(request.getParameter("cbEmail_"+ user.getUserId()) != null ? true : false);
				acl.setCheckin(request.getParameter("cbCheckin_"+ user.getUserId()) != null ? true : false);
				acl.setCheckout(request.getParameter("cbCheckout_"+ user.getUserId()) != null ? true : false);
				acl.setDownload(request.getParameter("cbDownload_"+ user.getUserId()) != null ? true : false);
				short aclValue = (short)acl.getACLValue();
				
				Permission permission = new Permission();
				permission.setAclValue(aclValue);
				permission.setClassId(documentClassId);
				permission.setUserId(user.getUserId());
				
				PermissionDAO.getInstance().create(permission);
			}
			
			AuditLogManager.log(new AuditLogRecord(
					documentClass.getClassId(),
					AuditLogRecord.OBJECT_DOCUMENTCLASS,
					AuditLogRecord.ACTION_PERMISSIONCHANGED,
					loggedInUser.getUserName(),
					request.getRemoteAddr(),
					AuditLogRecord.LEVEL_INFO,
					"ID :" + documentClass.getClassId(),
					"Name : " + documentClass.getClassDescription()) );
			request.setAttribute(HTTPConstants.REQUEST_MESSAGE,  "Permissions set for document class " + documentClass.getClassName() + " successfully");
			return (new ManageDocumentClassesAction().execute(request, response));
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return (new ManageDocumentClassesAction().execute(request, response));
	}
}

