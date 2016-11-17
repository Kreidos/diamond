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

package com.primeleaf.krystal.web.action.console;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.primeleaf.krystal.constants.HTTPConstants;
import com.primeleaf.krystal.model.AccessControlManager;
import com.primeleaf.krystal.model.dao.DocumentClassDAO;
import com.primeleaf.krystal.model.dao.DocumentDAO;
import com.primeleaf.krystal.model.dao.UserDAO;
import com.primeleaf.krystal.model.vo.Document;
import com.primeleaf.krystal.model.vo.DocumentClass;
import com.primeleaf.krystal.model.vo.User;
import com.primeleaf.krystal.security.ACL;
import com.primeleaf.krystal.web.action.Action;
import com.primeleaf.krystal.web.view.WebView;
import com.primeleaf.krystal.web.view.console.DocumentClassPropertiesView;

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

