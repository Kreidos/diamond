/**
 * Created On 27-Jan-2014
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

package kreidos.diamond.web.action.cpanel;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kreidos.diamond.model.dao.DocumentClassDAO;
import kreidos.diamond.model.dao.DocumentDAO;
import kreidos.diamond.model.vo.DocumentClass;
import kreidos.diamond.model.vo.Hit;
import kreidos.diamond.web.action.Action;
import kreidos.diamond.web.view.WebView;
import kreidos.diamond.web.view.cpanel.RecycleBinView;


/**
 * @author Rahul Kubadia
 */

public class RecycleBinAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ArrayList<DocumentClass> classListWithDeletedDocuments = new ArrayList<DocumentClass>();
		ArrayList<DocumentClass> classList = null;
		classList=DocumentClassDAO.getInstance().readDocumentClasses("");

		for(DocumentClass documentClass : classList){
			int deletedDocumentCount = DocumentDAO.getInstance().countDocuments(Hit.STATUS_DELETED, "CLASSID="+documentClass.getClassId());
			int expiredDocumentCount = DocumentDAO.getInstance().countDocuments(Hit.STATUS_EXPIRED, "CLASSID="+documentClass.getClassId());
			if(deletedDocumentCount>0 || expiredDocumentCount > 0 ){
				classListWithDeletedDocuments.add(documentClass);
				request.setAttribute(documentClass.getClassName()+"_DELETED", deletedDocumentCount);
				request.setAttribute(documentClass.getClassName()+"_EXPIRED", expiredDocumentCount);
			}
		}
		request.setAttribute("CLASSLIST", classListWithDeletedDocuments);
		return (new RecycleBinView(request, response));
	}
}

