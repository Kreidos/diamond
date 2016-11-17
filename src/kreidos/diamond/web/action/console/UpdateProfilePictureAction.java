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

package com.primeleaf.krystal.web.action.console;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.primeleaf.krystal.constants.HTTPConstants;
import com.primeleaf.krystal.model.AuditLogManager;
import com.primeleaf.krystal.model.dao.UserDAO;
import com.primeleaf.krystal.model.vo.AuditLogRecord;
import com.primeleaf.krystal.model.vo.User;
import com.primeleaf.krystal.web.action.Action;
import com.primeleaf.krystal.web.view.WebView;

/**
 * Author Rahul Kubadia
 */

public class UpdateProfilePictureAction implements Action {
	@SuppressWarnings("rawtypes")
	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding(HTTPConstants.CHARACTER_ENCODING);
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute(HTTPConstants.SESSION_KRYSTAL);
		if(request.getMethod().equalsIgnoreCase("POST")){
			try{
				String userName = loggedInUser.getUserName();
				String sessionid = (String)session.getId();

				String tempFilePath = System.getProperty("java.io.tmpdir");

				if ( !(tempFilePath.endsWith("/") || tempFilePath.endsWith("\\")) ){
					tempFilePath += System.getProperty("file.separator");
				}
				tempFilePath+=  userName+"_"+sessionid;

				//variables
				String fileName="",ext="";
				File file =null;
				// Create a factory for disk-based file items
				FileItemFactory factory = new DiskFileItemFactory();
				// Create a new file upload handler
				ServletFileUpload upload = new ServletFileUpload(factory);
				request.setCharacterEncoding(HTTPConstants.CHARACTER_ENCODING);
				upload.setHeaderEncoding(HTTPConstants.CHARACTER_ENCODING);
				List listItems = upload.parseRequest((HttpServletRequest)request);

				Iterator iter = listItems.iterator();
				FileItem fileItem = null;
				while (iter.hasNext()){
					fileItem = (FileItem) iter.next();
					if (! fileItem.isFormField()){
						try{
							fileName = fileItem.getName();  	
							file = new File(fileName);
							fileName = file.getName();
							ext = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
							if(! "JPEG".equalsIgnoreCase(ext) && ! "JPG".equalsIgnoreCase(ext) && ! "PNG".equalsIgnoreCase(ext)){
								request.setAttribute(HTTPConstants.REQUEST_ERROR,"Invalid image. Please upload JPG or PNG file");
								return (new MyProfileAction().execute(request, response));
							}
							file=new File(tempFilePath+"."+ext);				    
							fileItem.write(file);
						}catch(Exception ex){
							session.setAttribute("UPLOAD_ERROR",ex.getLocalizedMessage());
							return (new MyProfileAction().execute(request, response));
						}
					}
				}//if

				if( file.length() <= 0 )	{ //code for checking minimum size of file
					request.setAttribute(HTTPConstants.REQUEST_ERROR,"Zero length document");
					return (new MyProfileAction().execute(request, response));
				}
				if( file.length() > (1024*1024*2) )	{ //code for checking minimum size of file
					request.setAttribute(HTTPConstants.REQUEST_ERROR,"Image size too large. Upload upto 2MB file");
					return (new MyProfileAction().execute(request, response));
				}
				
				User user = loggedInUser;
				user.setProfilePicture(file);
				UserDAO.getInstance().setProfilePicture(user);
				
				AuditLogManager.log(new AuditLogRecord(
						user.getUserId(),
						AuditLogRecord.OBJECT_USER,
						AuditLogRecord.ACTION_EDITED,
						loggedInUser.getUserName(),
						request.getRemoteAddr(),
						AuditLogRecord.LEVEL_INFO,
						"",
						"Profile picture update"));
			}catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}
		request.setAttribute(HTTPConstants.REQUEST_MESSAGE,"Profile picture uploaded successfully");
		return (new MyProfileAction().execute(request, response));
	}
}

