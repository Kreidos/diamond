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

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kreidos.diamond.constants.HTTPConstants;
import kreidos.diamond.model.AuditLogManager;
import kreidos.diamond.model.dao.DocumentDAO;
import kreidos.diamond.model.vo.AuditLogRecord;
import kreidos.diamond.model.vo.Document;
import kreidos.diamond.util.DBStringHelper;
import kreidos.diamond.web.action.Action;
import kreidos.diamond.web.view.WebView;
import kreidos.diamond.web.view.console.AJAXResponseView;
import kreidos.diamond.web.view.console.AccessHistoryView;


/**
 * Author Rahul Kubadia
 */

public class AccessHistoryAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String documentid = (request.getParameter("documentid")!=null?request.getParameter("documentid"):"").trim();// get object id
		String fromDate  = (request.getParameter("fromDate")!=null?request.getParameter("fromDate"):"").trim();
		String toDate  = (request.getParameter("toDate")!=null?request.getParameter("toDate"):"").trim();
		int documentId = 0;
		try{
			documentId = Integer.parseInt(documentid);
		}catch(Exception ex){
			request.setAttribute(HTTPConstants.REQUEST_ERROR,"Invalid Document");
			return new AJAXResponseView(request,response);
		}
		Document document = DocumentDAO.getInstance().readDocumentById(documentId);
		if(document == null){
			request.setAttribute(HTTPConstants.REQUEST_ERROR,"Invalid Document");
			return new AJAXResponseView(request,response);
		}
		String logCriteria = " OBJECTID="+ documentId + " AND OBJECTTYPE='D'"; 
		if(fromDate.trim().length() > 0 && toDate.trim().length() > 0){
			logCriteria += "  AND  ACTIONDATE BETWEEN '" + new java.sql.Date(DBStringHelper.getSQLDate(fromDate).getTime()) +" 00:00:00 " +  "' AND '" +  new java.sql.Date(DBStringHelper.getSQLDate(toDate).getTime()) +" 23:59:59 " + "' ";
		}
		logCriteria += " ORDER BY ACTIONDATE DESC";
		
		ArrayList<AuditLogRecord> auditLogRecords = AuditLogManager.getAuditLogs(logCriteria);
		request.setAttribute("FROMDATE", fromDate);
		request.setAttribute("TODATE", toDate);
		request.setAttribute("DOCUMENT", document);
		request.setAttribute("ACCESSHISTORY", auditLogRecords);
		return (new AccessHistoryView(request, response));
	}
}

