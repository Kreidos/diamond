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
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kreidos.diamond.constants.HTTPConstants;
import kreidos.diamond.constants.ServerConstants;
import kreidos.diamond.model.dao.DocumentClassDAO;
import kreidos.diamond.model.dao.DocumentDAO;
import kreidos.diamond.model.dao.UserDAO;
import kreidos.diamond.model.vo.DocumentClass;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.web.action.Action;
import kreidos.diamond.web.view.UnauthorizedOrInvalidAccessView;
import kreidos.diamond.web.view.WebView;
import kreidos.diamond.web.view.cpanel.SummaryReportView;


/**
 * Author Rahul Kubadia
 */

public class SummaryReportAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute(HTTPConstants.SESSION_KRYSTAL);
		if(! loggedInUser.getUserName().equalsIgnoreCase(ServerConstants.SYSTEM_ADMIN_USER)){
			request.setAttribute(HTTPConstants.REQUEST_ERROR, "Access Denied");
			return (new UnauthorizedOrInvalidAccessView(request, response));
		}
		ArrayList<DocumentClass> documentClasses = DocumentClassDAO.getInstance().readDocumentClasses("");
		
		int documentClassCount = documentClasses.size();
		int userCount = UserDAO.getInstance().readUsers("").size();
		
		int totalDocuments=0;
		double totalSize = 0;
		
		request.setAttribute("DOCUMENTCLASSLIST", documentClasses);
		for(DocumentClass documentClass : documentClasses){
			int documentCount = DocumentDAO.getInstance().countActiveDocuments(documentClass.getClassId());
			double documentSize = DocumentDAO.getInstance().documentSize(documentClass.getClassId());
			User user = UserDAO.getInstance().readUserByName(documentClass.getCreatedBy());
			String ownerName = user.getRealName();
			request.setAttribute(documentClass.getClassName()+"_COUNT", documentCount);
			request.setAttribute(documentClass.getClassName()+"_SIZE", documentSize);
			request.setAttribute(documentClass.getClassName()+"_OWNER", ownerName);
			LinkedHashMap<String, Integer> chartValues = DocumentDAO.getInstance().getDocumentCountMonthWise(documentClass.getClassId());
			request.setAttribute(documentClass.getClassName() + "_CHARTVALUES",chartValues);
			totalDocuments+=documentCount;
			totalSize+=documentSize;
		}
		request.setAttribute("DOCUMENT_CLASSES", documentClassCount);
		request.setAttribute("DOCUMENTS", totalDocuments);
		request.setAttribute("TOTALSIZE", totalSize);
		request.setAttribute("USERS", userCount);
		return (new SummaryReportView(request, response));
	}
}

