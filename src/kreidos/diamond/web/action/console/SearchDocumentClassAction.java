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
import kreidos.diamond.model.dao.DocumentClassDAO;
import kreidos.diamond.model.dao.DocumentDAO;
import kreidos.diamond.model.dao.UserDAO;
import kreidos.diamond.model.vo.Document;
import kreidos.diamond.model.vo.DocumentClass;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.web.action.Action;
import kreidos.diamond.web.view.WebView;
import kreidos.diamond.web.view.console.SearchDocumentClassView;


/**
 * Author Rahul Kubadia
 */

public class SearchDocumentClassAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String classid = request.getParameter("classid")!=null?request.getParameter("classid"):"0";
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
		int documentCount = DocumentDAO.getInstance().countActiveDocuments(documentClass.getClassId());

		ArrayList<User> userList = new ArrayList<User>();


		userList = UserDAO.getInstance().readUsers("");
		User owner = UserDAO.getInstance().readUserByName(documentClass.getCreatedBy());
		int weekCount = DocumentDAO.getInstance().countActiveDocumentsForInterval(classId,7, Document.DATE_CREATED);
		int expringDocuments = DocumentDAO.getInstance().countExpiringDocumentsForInterval(documentClass.getClassId(), documentClass.getExpiryNotificationPeriod());
		request.setAttribute(documentClass.getClassName() + "_EXPIRY_COUNT",expringDocuments);
		request.setAttribute("DOCUMENTCOUNT", documentCount);
		request.setAttribute("WEEKCOUNT", weekCount);
		request.setAttribute("DOCUMENTCLASS", documentClass);
		request.setAttribute("USERLIST", userList);
		request.setAttribute("OWNER", owner);

		return (new SearchDocumentClassView(request, response));
	}
}

