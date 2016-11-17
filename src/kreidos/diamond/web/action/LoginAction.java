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

package kreidos.diamond.web.action;

import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kreidos.diamond.constants.HTTPConstants;
import kreidos.diamond.model.AuditLogManager;
import kreidos.diamond.model.dao.UserDAO;
import kreidos.diamond.model.vo.AuditLogRecord;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.security.LoginModule;
import kreidos.diamond.util.StringHelper;
import kreidos.diamond.web.view.DefaultView;
import kreidos.diamond.web.view.WebView;


/**
 * Author Rahul Kubadia
 */

public class LoginAction implements Action {

	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String loginId ="" ,password ="",rememberMe="";
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute(HTTPConstants.SESSION_KRYSTAL);
		if(loggedInUser == null){
			if(request.getMethod().equalsIgnoreCase("POST")){
				loginId = request.getParameter("txtLoginId")!=null ? request.getParameter("txtLoginId"):"";
				password = request.getParameter("txtPassword")!=null ? request.getParameter("txtPassword"):"";
				rememberMe = request.getParameter("chkRememberMe")!=null ? request.getParameter("chkRememberMe") : "";
			}else{ //request coming from get method so read from cookie
				String cookieName = HTTPConstants.AUTH_EDMC_COOKIE;
				Cookie[] arrCookie = request.getCookies();
				loginId="";
				password="";
				if(arrCookie != null){
					for(int i = 0; i < arrCookie.length; i++){
						if(arrCookie[i].getName().equals(cookieName)){
							String cookieValue = StringHelper.decodeString(arrCookie[i].getValue());
							StringTokenizer st = new StringTokenizer(cookieValue,HTTPConstants.AUTH_DELIMETER);
							loginId = st.nextToken();
							password = st.nextToken();
							break;
						}
					}
				}
			}
			
			int loginStatus = LoginModule.getInstance().authenticate(loginId,password);
			switch (loginStatus){
			case LoginModule.DOMAIN_INVALID:
				request.setAttribute(HTTPConstants.REQUEST_ERROR,HTTPConstants.ERROR_INVALIDDOMAIN);
				break;
			case LoginModule.INACTIVE_USER:
				request.setAttribute(HTTPConstants.REQUEST_ERROR,HTTPConstants.ERROR_INACTIVEUSER);
				break;
			case LoginModule.LOGIN_INVALID:
				request.setAttribute(HTTPConstants.REQUEST_ERROR,HTTPConstants.ERROR_INVALIDUSER);
				break;
			case LoginModule.ALREADY_LOGGEDIN:
				request.setAttribute(HTTPConstants.REQUEST_ERROR,HTTPConstants.ERROR_ALREADY_LOGGED_IN);
				break;
			case LoginModule.TOO_MANY_USERS:
				request.setAttribute(HTTPConstants.REQUEST_ERROR,HTTPConstants.ERROR_TOOMANYUSER);
				break;
			case LoginModule.TRIAL_EXPIRED:
				request.setAttribute(HTTPConstants.REQUEST_ERROR,HTTPConstants.ERROR_TRIAL_EXPIRED);
				break;
			case LoginModule.LOGIN_SUCCESSFUL:
				session.invalidate();
				session = request.getSession(true);
				session.setMaxInactiveInterval(15 * 60);
				User user = UserDAO.getInstance().readUser(loginId);
				user.setIpAddress(request.getRemoteAddr());
				session.setAttribute(HTTPConstants.SESSION_KRYSTAL,user);
				UserDAO.getInstance().updateUserLoginHistory(user);
				//Log the entry to audit logs 
				AuditLogManager.log(new AuditLogRecord(
						user.getUserId(),
						AuditLogRecord.OBJECT_USER,
						AuditLogRecord.ACTION_LOGIN,
						user.getUserName(),
						request.getRemoteAddr(),
						AuditLogRecord.LEVEL_INFO ));
				if(rememberMe.trim().length() >0 ){	
					setAuthCookies(loginId,password,response);
				}
				response.sendRedirect("/console");
				return null;
			default:
				request.setAttribute(HTTPConstants.REQUEST_ERROR,"UNKNOWN ERROR");
				break;
			}

		}else{
			response.sendRedirect("/console");
			return null;
		}
		return (new DefaultView(request,response));
	}

	private void setAuthCookies(String userName,String password,HttpServletResponse response) {
		String cookieName = HTTPConstants.AUTH_EDMC_COOKIE;
		Cookie authCookie = new Cookie(cookieName,StringHelper.encodeString(userName+HTTPConstants.AUTH_DELIMETER+password));
		authCookie.setMaxAge(1000 * 60 * 60);
		authCookie.setPath("/");
		response.addCookie(authCookie);
	}
}

