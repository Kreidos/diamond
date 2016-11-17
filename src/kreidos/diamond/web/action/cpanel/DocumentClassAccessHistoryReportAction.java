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
import kreidos.diamond.model.dao.DocumentClassDAO;
import kreidos.diamond.model.vo.AuditLogRecord;
import kreidos.diamond.model.vo.DocumentClass;
import kreidos.diamond.util.DBStringHelper;
import kreidos.diamond.web.action.Action;
import kreidos.diamond.web.view.WebView;
import kreidos.diamond.web.view.cpanel.DocumentClassAccessHistoryReportView;


/**
 * Author Rahul Kubadia
 */

public class DocumentClassAccessHistoryReportAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ArrayList<DocumentClass> documentClasses=new ArrayList<DocumentClass>();
		documentClasses = DocumentClassDAO.getInstance().readDocumentClasses("");
		request.setAttribute("DOCUMENTCLASSLIST", documentClasses);
		if("POST".equalsIgnoreCase(request.getMethod())){
			String classid = request.getParameter("classid")!=null?request.getParameter("classid"):"";
			int classId = 0;
			try{
				classId = Integer.parseInt(classid);
			}catch(Exception ex){
				request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid input");
				return (new DocumentClassAccessHistoryReportView(request, response));
			}
			DocumentClass documentClass =DocumentClassDAO.getInstance().readDocumentClassById(classId);
			if(documentClass == null){
				request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid document class");
				return (new DocumentClassAccessHistoryReportView(request, response));
			}
			String fromDate = request.getParameter("txtFromDate")!=null?request.getParameter("txtFromDate"):"";
			String toDate = request.getParameter("txtToDate")!=null?request.getParameter("txtToDate"):"";
			String logCriteria = "";
			logCriteria = "OBJECTID ="+ documentClass.getClassId() +" AND OBJECTTYPE='C' ";

			if(fromDate.length() > 0  && toDate.length() > 0 ){

				logCriteria += "  AND  ACTIONDATE BETWEEN '" + new java.sql.Date(DBStringHelper.getSQLDate(fromDate).getTime()) +" 00:00:00 " +  "' AND '" +  new java.sql.Date(DBStringHelper.getSQLDate(toDate).getTime()) +" 23:59:59 " + "' ";

			}
			logCriteria += "   ORDER BY ACTIONDATE DESC";

			ArrayList<AuditLogRecord> auditLogRecords = AuditLogManager.getAuditLogs(logCriteria);
			request.setAttribute("ACCESSHISTORY", auditLogRecords);
			request.setAttribute("DOCUMENTCLASS", documentClass);
			request.setAttribute("FROMDATE", fromDate);
			request.setAttribute("TODATE", toDate);
		}
		return (new DocumentClassAccessHistoryReportView(request, response));
	}
}

