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

package kreidos.diamond.web.action.cpanel;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kreidos.diamond.constants.HTTPConstants;
import kreidos.diamond.model.AuditLogManager;
import kreidos.diamond.model.dao.UserDAO;
import kreidos.diamond.model.vo.AuditLogRecord;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.util.DBStringHelper;
import kreidos.diamond.web.action.Action;
import kreidos.diamond.web.view.WebView;
import kreidos.diamond.web.view.cpanel.UserAccessHistoryReportView;


/**
 * Author Rahul Kubadia
 */

public class UserAccessHistoryReportAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ArrayList<User> users=new ArrayList<User>();
		users = UserDAO.getInstance().readUsers("");
		request.setAttribute("USERLIST", users);

		if("POST".equalsIgnoreCase(request.getMethod())){
			String userid = request.getParameter("userid")!=null?request.getParameter("userid"):"";
			int userId = 0;
			try{
				userId = Integer.parseInt(userid);
			}catch(Exception ex){
				request.setAttribute(HTTPConstants.REQUEST_ERROR,"Invalid input");
				return (new UserAccessHistoryReportView(request, response));
			}
			User user = UserDAO.getInstance().readUserById(userId);
			if(user == null){
				request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid user");
				return (new UserAccessHistoryReportView(request, response));
			}
			String fromDate = request.getParameter("txtFromDate")!=null?request.getParameter("txtFromDate"):"";
			String toDate = request.getParameter("txtToDate")!=null?request.getParameter("txtToDate"):"";
			String logCriteria = "";
			logCriteria = "USERNAME ='"+ user.getUserName().toUpperCase() +"' ";
			if(fromDate.length() > 0  && toDate.length() > 0 ){

				logCriteria += "  AND  ACTIONDATE BETWEEN '" + new java.sql.Date(DBStringHelper.getSQLDate(fromDate).getTime()) +" 00:00:00 " +  "' AND '" +  new java.sql.Date(DBStringHelper.getSQLDate(toDate).getTime()) +" 23:59:59 " + "' ";

			}
			logCriteria += "   ORDER BY ACTIONDATE DESC";

			ArrayList<AuditLogRecord> auditLogRecords = AuditLogManager.getAuditLogs(logCriteria);
			request.setAttribute("ACCESSHISTORY", auditLogRecords);
			request.setAttribute("USER", user);
			request.setAttribute("FROMDATE", fromDate);
			request.setAttribute("TODATE", toDate);
		}
		return (new UserAccessHistoryReportView(request, response));
	}
}