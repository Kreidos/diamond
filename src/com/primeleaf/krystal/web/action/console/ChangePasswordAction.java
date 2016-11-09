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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.primeleaf.krystal.constants.HTTPConstants;
import com.primeleaf.krystal.model.AuditLogManager;
import com.primeleaf.krystal.model.dao.PasswordHistoryDAO;
import com.primeleaf.krystal.model.dao.UserDAO;
import com.primeleaf.krystal.model.vo.AuditLogRecord;
import com.primeleaf.krystal.model.vo.PasswordHistory;
import com.primeleaf.krystal.model.vo.User;
import com.primeleaf.krystal.security.PasswordService;
import com.primeleaf.krystal.web.action.Action;
import com.primeleaf.krystal.web.view.WebView;
import com.primeleaf.krystal.web.view.console.AJAXResponseView;

/**
 * @author Rahul Kubadia
 */

public class ChangePasswordAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if("POST".equalsIgnoreCase(request.getMethod())){
			HttpSession session = request.getSession();
			User loggedInUser = (User)session.getAttribute(HTTPConstants.SESSION_KRYSTAL);
			try{
				String oldPassword = (request.getParameter("txtOldPassword")!=null?request.getParameter("txtOldPassword"):"").trim();
				String newPassword = (request.getParameter("txtNewPassword")!=null?request.getParameter("txtNewPassword"):"").trim();
				String confirmPassword = (request.getParameter("txtConfirmPassword")!=null?request.getParameter("txtConfirmPassword"):"").trim();
				
				oldPassword = PasswordService.getInstance().encrypt(oldPassword);
				
				if(! newPassword.equals(confirmPassword)){
					request.setAttribute(HTTPConstants.REQUEST_ERROR, "Passwords do not match");
					return (new AJAXResponseView(request, response));
				}
				
				if(! oldPassword.equals(loggedInUser.getPassword())){
					request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid Password");
					return (new AJAXResponseView(request, response));
				}
				
				if(PasswordHistoryDAO.getInstance().isPasswordExistInHistory(loggedInUser.getUserId(), newPassword)){
					request.setAttribute(HTTPConstants.REQUEST_ERROR, "Password already used");
					return (new AJAXResponseView(request, response));
				}
				loggedInUser.setPassword(newPassword);
				UserDAO.getInstance().updateUser(loggedInUser,true);

				PasswordHistory passwordHistory = new PasswordHistory();
				passwordHistory.setUserId(loggedInUser.getUserId());
				passwordHistory.setPassword(newPassword);
				if(PasswordHistoryDAO.getInstance().readByUserId(loggedInUser.getUserId()).size()>=5){
					PasswordHistoryDAO.getInstance().deleteLastHistory(loggedInUser.getUserId());
				}
				//Password is again set in loggedInUser in encrypted form 
				loggedInUser.setPassword(PasswordService.getInstance().encrypt(newPassword));
				PasswordHistoryDAO.getInstance().create(passwordHistory);
				AuditLogManager.log(new AuditLogRecord(
						loggedInUser.getUserId(),
						AuditLogRecord.OBJECT_USER,
						AuditLogRecord.ACTION_PASSWORDCHANGED,
						loggedInUser.getUserName(),
						request.getRemoteAddr(),
						AuditLogRecord.LEVEL_INFO,
						"",
						"Password Changed"));
				request.setAttribute(HTTPConstants.REQUEST_MESSAGE, "Password changed successfully");
				return (new AJAXResponseView(request, response));
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return (new AJAXResponseView(request, response));
	}
}