/**
 * Created On 05-Dec-2014
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

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kreidos.diamond.constants.HTTPConstants;
import kreidos.diamond.model.AccessControlManager;
import kreidos.diamond.model.dao.DocumentClassDAO;
import kreidos.diamond.model.dao.DocumentDAO;
import kreidos.diamond.model.dao.UserDAO;
import kreidos.diamond.model.vo.Document;
import kreidos.diamond.model.vo.DocumentClass;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.security.ACL;
import kreidos.diamond.web.action.Action;
import kreidos.diamond.web.view.WebView;
import kreidos.diamond.web.view.console.DocumentClassPropertiesView;


/**
 * Author Rahul Kubadia
 */

public class DocumentClassPropertiesAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute(HTTPConstants.SESSION_KRYSTAL);
		String classid = request.getParameter("classid")!=null?request.getParameter("classid"):"";
		int classId = 0;
		try{
			classId = Integer.parseInt(classid);
		}catch(Exception ex){
			request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid input");
			return (new HomeAction().execute(request,response));
		}
		DocumentClass documentClass = DocumentClassDAO.getInstance().readDocumentClassById(classId);
		if(documentClass == null){
			request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid document class");
			return (new HomeAction().execute(request,response));
		}

		ACL acl = null;
		AccessControlManager aclManager = new AccessControlManager();
		acl = aclManager.getACL(documentClass, loggedInUser);

		if(!acl.canRead() || ! documentClass.isVisible()){
			request.setAttribute(HTTPConstants.REQUEST_ERROR,"Access Denied");
			return (new HomeAction().execute(request,response));
		}
		User owner = UserDAO.getInstance().readUserByName(documentClass.getCreatedBy());
		LinkedHashMap<String, Integer> chartValues = DocumentDAO.getInstance().getDocumentCountMonthWise(documentClass.getClassId());
		request.setAttribute("DOCUMENTCLASS",documentClass);
		request.setAttribute("OWNER",owner);
		request.setAttribute("CHARTVALUES",chartValues);
		int expringDocuments = DocumentDAO.getInstance().countExpiringDocumentsForInterval(documentClass.getClassId(), documentClass.getExpiryNotificationPeriod());
		int weekCount = DocumentDAO.getInstance().countActiveDocumentsForInterval(documentClass.getClassId(),7, Document.DATE_CREATED);
		request.setAttribute("WEEK_COUNT",weekCount);
		request.setAttribute("EXPIRY_COUNT",expringDocuments);
		return (new DocumentClassPropertiesView(request, response));
	}
}

