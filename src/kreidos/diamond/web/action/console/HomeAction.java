/**
 * Created On 15-Mar-2014
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

package kreidos.diamond.web.action.console;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kreidos.diamond.constants.HTTPConstants;
import kreidos.diamond.model.AccessControlManager;
import kreidos.diamond.model.dao.AuditLogRecordDAO;
import kreidos.diamond.model.dao.BookmarkDAO;
import kreidos.diamond.model.dao.CheckedOutDocumentDAO;
import kreidos.diamond.model.dao.DocumentClassDAO;
import kreidos.diamond.model.dao.DocumentDAO;
import kreidos.diamond.model.vo.AuditLogRecord;
import kreidos.diamond.model.vo.Bookmark;
import kreidos.diamond.model.vo.CheckedOutDocument;
import kreidos.diamond.model.vo.DocumentClass;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.security.ACL;
import kreidos.diamond.web.action.Action;
import kreidos.diamond.web.view.WebView;
import kreidos.diamond.web.view.console.HomeView;


/**
 * Author Rahul Kubadia
 */

public class HomeAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute(HTTPConstants.SESSION_KRYSTAL);
		AccessControlManager aclManager = new AccessControlManager();

		ArrayList<DocumentClass> allDocumentClasses = DocumentClassDAO.getInstance().readDocumentClasses("DOCUMENTCLASSES.ACTIVE = 'Y'");
		ArrayList<DocumentClass> userDocumentClasses = new ArrayList<DocumentClass>();
		
		
		int count   = 1 ;
		for(DocumentClass documentClass : allDocumentClasses){
			ACL acl = aclManager.getACL(documentClass, loggedInUser);
			if(acl.canRead()){//User has permission to access this document class so add to displayed list
				request.setAttribute(documentClass.getClassName() + "_ACL",acl);
				userDocumentClasses.add(documentClass);
			}
		}
		
		request.setAttribute("DOCUMENTCLASSLIST", userDocumentClasses);
		
		
		long usedStorage = DocumentDAO.getInstance().documentSize();
		request.setAttribute("USEDSTORAGE", usedStorage);

		ArrayList<CheckedOutDocument> checkedOutDocumentList = CheckedOutDocumentDAO.getInstance().readCheckedOutDocumentsByUser(loggedInUser.getUserName());
		ArrayList<CheckedOutDocument> top5Checkouts = new ArrayList<CheckedOutDocument>();
		count = 1;
		for(CheckedOutDocument checkedOutDocument : checkedOutDocumentList){
			top5Checkouts.add(checkedOutDocument);
		}
		request.setAttribute("CHECKOUTS", top5Checkouts);

		ArrayList<AuditLogRecord> auditLogsRecords  = AuditLogRecordDAO.getInstance().getAuditTrail("USERNAME ='"+loggedInUser.getUserName()+"' AND AUDITLEVEL = "+AuditLogRecord.LEVEL_INFO+" ORDER BY ACTIONDATE DESC ");
		ArrayList<AuditLogRecord> top10AuditRecords = new ArrayList<AuditLogRecord>();
		count = 1;
		for(AuditLogRecord auditLogRecord : auditLogsRecords){
			top10AuditRecords.add(auditLogRecord);
			count++;
			if(count > 10 )break;
		}
		request.setAttribute("AUDITLOGS", top10AuditRecords);

		ArrayList<Bookmark> bookmarkList = BookmarkDAO.getInstance().readBookmarkByUser(loggedInUser.getUserName());
		ArrayList<Bookmark> top5Bookmarks = new ArrayList<Bookmark>();
		count = 1;
		for(Bookmark bookmark : bookmarkList){
			top5Bookmarks.add(bookmark);
			count++;
			if(count > 5 )break;
		}
		request.setAttribute("BOOKMARKS", top5Bookmarks);
		return (new HomeView(request, response));
	}
}

