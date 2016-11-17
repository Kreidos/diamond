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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kreidos.diamond.constants.HTTPConstants;
import kreidos.diamond.model.AuditLogManager;
import kreidos.diamond.model.dao.UserDAO;
import kreidos.diamond.model.vo.AuditLogRecord;
import kreidos.diamond.model.vo.HitList;
import kreidos.diamond.model.vo.MetaColumnPreferences;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.web.action.Action;
import kreidos.diamond.web.view.WebView;
import kreidos.diamond.web.view.console.AJAXResponseView;


/**
 * @author Rahul Kubadia
 */

public class PreferencesAction implements Action {
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute(HTTPConstants.SESSION_KRYSTAL);
		if("POST".equalsIgnoreCase(request.getMethod())){
			try {
				String pageSize=request.getParameter("cmbHitListSize")!=null?request.getParameter("cmbHitListSize"):"10";
				int hitListSize = Integer.parseInt(pageSize);
				String checkOutDir=request.getParameter("txtCheckOutDir")!=null?request.getParameter("txtCheckOutDir"):"C:/TEMP";;
				String showThumbNail = request.getParameter("radThumbNail")!=null?request.getParameter("radThumbNail"):"TRUE";
				String createdColumn=request.getParameter(HitList.META_CREATED)!=null?request.getParameter(HitList.META_CREATED):"";
				String createdByColumn=request.getParameter(HitList.META_CREATEDBY)!=null?request.getParameter(HitList.META_CREATEDBY):"";
				String lengthColumn=request.getParameter(HitList.META_LENGTH)!=null?request.getParameter(HitList.META_LENGTH):"";
				String revisionColumn=request.getParameter(HitList.META_REVISIONID)!=null?request.getParameter(HitList.META_REVISIONID):"";
				String documentIdColumn=request.getParameter(HitList.META_DOCUMENTID)!=null?request.getParameter(HitList.META_DOCUMENTID):"";
				String expiryOnColumn=request.getParameter(HitList.META_EXPIRYON)!=null?request.getParameter(HitList.META_EXPIRYON):"";
				String fileNameColumn=request.getParameter(HitList.META_FILENAME)!=null?request.getParameter(HitList.META_FILENAME):"";
				String modifiedColumn=request.getParameter(HitList.META_MODIFIED)!=null?request.getParameter(HitList.META_MODIFIED):"";
				String modifiedByColumn=request.getParameter(HitList.META_MODIFIEDBY)!=null?request.getParameter(HitList.META_MODIFIEDBY):"";
				
				MetaColumnPreferences metaPreferences = new MetaColumnPreferences();

				metaPreferences.setCreatedByVisible("TRUE".equalsIgnoreCase(createdByColumn));
				metaPreferences.setCreatedVisible("TRUE".equalsIgnoreCase(createdColumn));
				metaPreferences.setFileSizeVisible("TRUE".equalsIgnoreCase(lengthColumn));
				metaPreferences.setRevisionIdVisible("TRUE".equalsIgnoreCase(revisionColumn));
				metaPreferences.setDocumentIdVisible("TRUE".equalsIgnoreCase(documentIdColumn));
				metaPreferences.setExpiryOnVisible("TRUE".equalsIgnoreCase(expiryOnColumn));
				metaPreferences.setFileNameVisible("TRUE".equalsIgnoreCase(fileNameColumn));
				metaPreferences.setModifiedVisible("TRUE".equalsIgnoreCase(modifiedColumn));
				metaPreferences.setModifiedByVisible("TRUE".equalsIgnoreCase(modifiedByColumn));
				
				UserDAO.getInstance().setReadCompleteObject(true);
				loggedInUser.setCheckOutPath(checkOutDir);
				loggedInUser.setHitlistSize(hitListSize);
				loggedInUser.setMetaPreferences(metaPreferences);
				loggedInUser.setShowThumbNail(showThumbNail);
				
				loggedInUser.setCheckOutPath(checkOutDir);
				loggedInUser.setHitlistSize(hitListSize);
				loggedInUser.setMetaPreferences(metaPreferences);
				loggedInUser.setShowThumbNail(showThumbNail);
				
				UserDAO.getInstance().updateUser(loggedInUser, false);
				AuditLogManager.log(new AuditLogRecord(loggedInUser.getUserId(),AuditLogRecord.OBJECT_USER,AuditLogRecord.ACTION_PREFERENCESCHANGED,loggedInUser.getUserName(),request.getRemoteAddr(),AuditLogRecord.LEVEL_FINE));
				request.setAttribute(HTTPConstants.REQUEST_MESSAGE, "Preferences set successfully");
				return (new AJAXResponseView(request, response));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return (new AJAXResponseView(request, response));
	}
}

