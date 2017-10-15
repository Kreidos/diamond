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

package kreidos.diamond.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import kreidos.diamond.web.action.Action;
import kreidos.diamond.web.action.Action404;
import kreidos.diamond.web.action.DefaultAction;
import kreidos.diamond.web.action.LoginAction;
import kreidos.diamond.web.action.LogoutAction;
import kreidos.diamond.web.action.console.AccessHistoryAction;
import kreidos.diamond.web.action.console.AutoCompleteAction;
import kreidos.diamond.web.action.console.BulkDeleteDocumentAction;
import kreidos.diamond.web.action.console.BulkDownloadAction;
import kreidos.diamond.web.action.console.CancelCheckoutAction;
import kreidos.diamond.web.action.console.ChangePasswordAction;
import kreidos.diamond.web.action.console.CheckInDocumentAction;
import kreidos.diamond.web.action.console.CheckoutDocumentAction;
import kreidos.diamond.web.action.console.DeleteBookmarkAction;
import kreidos.diamond.web.action.console.DeleteDocumentAction;
import kreidos.diamond.web.action.console.DocumentClassPropertiesAction;
import kreidos.diamond.web.action.console.DocumentNotesAction;
import kreidos.diamond.web.action.console.DocumentViewerAction;
import kreidos.diamond.web.action.console.DownloadDocumentAction;
import kreidos.diamond.web.action.console.EditDocumentIndexesAction;
import kreidos.diamond.web.action.console.HomeAction;
import kreidos.diamond.web.action.console.MobileDocumentViewerAction;
import kreidos.diamond.web.action.console.MyProfileAction;
import kreidos.diamond.web.action.console.NewBookmarkAction;
import kreidos.diamond.web.action.console.NewDocumentAction;
import kreidos.diamond.web.action.console.OpenDocumentClassAction;
import kreidos.diamond.web.action.console.PreferencesAction;
import kreidos.diamond.web.action.console.ProfilePictureAction;
import kreidos.diamond.web.action.console.RevisionHistoryAction;
import kreidos.diamond.web.action.console.SearchAction;
import kreidos.diamond.web.action.console.SearchDocumentClassAction;
import kreidos.diamond.web.action.console.ShareDocumentAction;
import kreidos.diamond.web.action.console.UpdateProfilePictureAction;
import kreidos.diamond.web.action.console.UploadProgressAction;
import kreidos.diamond.web.action.console.ViewDocumentAction;
import kreidos.diamond.web.action.cpanel.BulkPurgeDocumentAction;
import kreidos.diamond.web.action.cpanel.CancelCheckoutAdminAction;
import kreidos.diamond.web.action.cpanel.ChangeUserPasswordAction;
import kreidos.diamond.web.action.cpanel.ClassIndexesAction;
import kreidos.diamond.web.action.cpanel.ControlPanelAction;
import kreidos.diamond.web.action.cpanel.DeleteClassIndexAction;
import kreidos.diamond.web.action.cpanel.DeleteDocumentClassAction;
import kreidos.diamond.web.action.cpanel.DeleteUserAction;
import kreidos.diamond.web.action.cpanel.DocumentClassAccessHistoryReportAction;
import kreidos.diamond.web.action.cpanel.EditClassIndexesAction;
import kreidos.diamond.web.action.cpanel.EditDocumentClassAction;
import kreidos.diamond.web.action.cpanel.EditUserAction;
import kreidos.diamond.web.action.cpanel.ManageCheckoutsAction;
import kreidos.diamond.web.action.cpanel.ManageDocumentClassesAction;
import kreidos.diamond.web.action.cpanel.NewClassIndexAction;
import kreidos.diamond.web.action.cpanel.NewDocumentClassAction;
import kreidos.diamond.web.action.cpanel.NewUserAction;
import kreidos.diamond.web.action.cpanel.PermissionsAction;
import kreidos.diamond.web.action.cpanel.PurgeDocumentAction;
import kreidos.diamond.web.action.cpanel.RecycleBinAction;
import kreidos.diamond.web.action.cpanel.RecycleBinContentAction;
import kreidos.diamond.web.action.cpanel.ReportsAction;
import kreidos.diamond.web.action.cpanel.RestoreDocumentAction;
import kreidos.diamond.web.action.cpanel.SetPermissionsAction;
import kreidos.diamond.web.action.cpanel.SummaryReportAction;
import kreidos.diamond.web.action.cpanel.UserAccessHistoryReportAction;
import kreidos.diamond.web.action.cpanel.UsersAction;



/**
 * Author Rahul Kubadia
 */

public class ActionFactory {
	private static ActionFactory instance;
	private static final Map<String, Action> actions = new HashMap<String,Action>();
	public static final ArrayList<String> AJAXActions = new ArrayList<String>();
	public static synchronized ActionFactory getInstance() { 
		if (instance == null) { 
			instance = new ActionFactory();
			actions.put("/", new DefaultAction());
			actions.put("/login", new LoginAction());
			actions.put("/logout", new LogoutAction());
			
			actions.put("/console", new HomeAction());
			actions.put("/console/autocomplete", new AutoCompleteAction());

			actions.put("/console/changepassword", new ChangePasswordAction());
			actions.put("/console/myprofile", new MyProfileAction());
			actions.put("/console/preferences", new PreferencesAction());
			
			actions.put("/console/documentclassproperties", new DocumentClassPropertiesAction());//Added by Rahul Kubadia on 30-Nov-2014 at home
			
			actions.put("/console/search", new SearchAction());
			actions.put("/console/newdocument", new NewDocumentAction());
			actions.put("/console/uploadprogress", new UploadProgressAction());


			actions.put("/console/searchdocumentclass", new SearchDocumentClassAction());
			
			
			actions.put("/console/opendocumentclass", new OpenDocumentClassAction());
			actions.put("/console/viewdocument", new ViewDocumentAction());
			actions.put("/console/mobiledocumentviewer", new MobileDocumentViewerAction());
			
			actions.put("/console/documentviewer", new DocumentViewerAction());
			actions.put("/console/editdocumentindexes", new EditDocumentIndexesAction());
			actions.put("/console/documentnotes",new DocumentNotesAction());
			actions.put("/console/accesshistory",new AccessHistoryAction());
			actions.put("/console/revisionhistory",new RevisionHistoryAction());

			actions.put("/console/downloaddocument",new DownloadDocumentAction());
			actions.put("/console/checkoutdocument",new CheckoutDocumentAction());
			actions.put("/console/cancelcheckout",new CancelCheckoutAction());
			actions.put("/console/checkindocument",new CheckInDocumentAction());
			actions.put("/console/sharedocument", new ShareDocumentAction());
			actions.put("/console/deletedocument",new DeleteDocumentAction());
			actions.put("/console/bulkdelete",new BulkDeleteDocumentAction());//Added by Rahul Kubadia on 27-Nov-2014 
			actions.put("/console/bulkdownload",new BulkDownloadAction());//Added by Rahul Kubadia on 28-Nov-2014 
			
			

			actions.put("/console/newbookmark", new NewBookmarkAction());
			actions.put("/console/deletebookmark", new DeleteBookmarkAction());
			actions.put("/console/profilepicture", new ProfilePictureAction());//15-June-2014 -Rahul Kubadia
			actions.put("/console/updateprofilepicture", new UpdateProfilePictureAction());//15-June-2014 -Rahul Kubadia
		
			
			/*Control Panel Actions start here*/
			actions.put("/cpanel", new ControlPanelAction());

			actions.put("/cpanel/users", new UsersAction());
			actions.put("/cpanel/newuser", new NewUserAction());
			actions.put("/cpanel/edituser", new EditUserAction());
			actions.put("/cpanel/deleteuser", new DeleteUserAction());
			actions.put("/cpanel/changeuserpassword", new ChangeUserPasswordAction());


			actions.put("/cpanel/managedocumentclasses", new ManageDocumentClassesAction());
			actions.put("/cpanel/newdocumentclass", new NewDocumentClassAction());
			actions.put("/cpanel/editdocumentclass", new EditDocumentClassAction());
			actions.put("/cpanel/deletedocumentclass", new DeleteDocumentClassAction());
			actions.put("/cpanel/classindexes", new ClassIndexesAction());
			actions.put("/cpanel/newclassindex", new NewClassIndexAction());
			actions.put("/cpanel/editclassindexes", new EditClassIndexesAction());
			actions.put("/cpanel/deleteclassindex", new DeleteClassIndexAction());
			actions.put("/cpanel/permissions", new PermissionsAction());
			actions.put("/cpanel/setpermissions", new SetPermissionsAction());
			
			actions.put("/cpanel/managecheckouts", new ManageCheckoutsAction());
			actions.put("/cpanel/cancelcheckoutadmin",new CancelCheckoutAdminAction());

			actions.put("/cpanel/reports", new ReportsAction());
			actions.put("/cpanel/summary", new SummaryReportAction());
			actions.put("/cpanel/useraccesshistory", new UserAccessHistoryReportAction());
			actions.put("/cpanel/documentclassaccesshistory", new DocumentClassAccessHistoryReportAction());
			
			actions.put("/cpanel/recyclebin", new RecycleBinAction());
			actions.put("/cpanel/recyclebincontent", new RecycleBinContentAction());
			actions.put("/cpanel/restoredocument", new RestoreDocumentAction());
			actions.put("/cpanel/purgedocument", new PurgeDocumentAction());
			actions.put("/cpanel/bulkpurgedocument", new BulkPurgeDocumentAction());
		} 
		return instance; 
	}

	public Action getAction(HttpServletRequest request){
		String strAction = request.getRequestURI();
		Action action = actions.get(strAction.toLowerCase());
		if(action == null){
			action = new Action404();
		}
		return action ;
	}

	private ActionFactory(){
		AJAXActions.add("/console/editdocumentindexes");
		AJAXActions.add("/console/attachments");
		AJAXActions.add("/console/documentnotes");
		AJAXActions.add("/console/revisionhistory");
		AJAXActions.add("/console/accesshistory");
		AJAXActions.add("/console/workflowcaseaudittrail");
		AJAXActions.add("/console/newbookmark");
		AJAXActions.add("/console/documenttags");
		AJAXActions.add("/console/newdocumenttag");
		AJAXActions.add("/console/deletedocumenttag");
		AJAXActions.add("/console/addtodocumentset");
		AJAXActions.add("/console/sharedocument");
		AJAXActions.add("/console/viewdiscussion");
		AJAXActions.add("/console/viewannouncement");
		AJAXActions.add("/console/preferences");
		AJAXActions.add("/console/changepassword");
		AJAXActions.add("/cpanel/changeuserpassword");
	}
}

