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

package kreidos.diamond.web.view.cpanel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kreidos.diamond.constants.HTTPConstants;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.web.view.WebView;

import org.apache.commons.lang3.StringEscapeUtils;



/**
 * @author Rahul Kubadia
 *
 */
public class ChangeUserPasswordView extends WebView {

	public ChangeUserPasswordView (HttpServletRequest request, HttpServletResponse response) throws Exception{
		init(request, response);
	}

	public void render() throws Exception{
		printChangeUserPasswordForm();
	}
	private void printChangeUserPasswordForm() throws Exception{
		out.println("<div class=\"modal-header\">");
		out.println("<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>");
		out.println("<h4 class=\"modal-title\" id=\"myModalLabel\"><i class=\"fa fa-key fa-lg\"></i> Change Password</h4>");
		out.println("</div>");
		out.println("<div class=\"modal-body\">");
		User user = (User) request.getAttribute("USER");
		if(request.getAttribute(HTTPConstants.REQUEST_ERROR) != null){
			printError((String) request.getAttribute(HTTPConstants.REQUEST_ERROR));
		}
		if(request.getAttribute(HTTPConstants.REQUEST_MESSAGE) != null){
			printSuccess((String) request.getAttribute(HTTPConstants.REQUEST_MESSAGE));
		}
		out.println("<div id=\"resultChangePassword\"></div>");
		out.println("<form action=\"/cpanel/changeuserpassword\" method=\"post\" id=\"frmChangeUserPassword\" form-type=\"ajax\" datatarget=\"#resultChangePassword\" class=\"form-horizontal modalForm\">");

		out.println("<div class=\"form-group\">");
		out.println("<label for=\"txtOldPassword\" class=\"col-sm-3 control-label\">User <span style='color:red'>*</span></label>");
		out.println("<div class=\"col-sm-9\">");
		out.println("<p class=\"form-control-static\"><strong>"+ StringEscapeUtils.escapeHtml4(user.getUserName()) + "</strong> " + StringEscapeUtils.escapeHtml4(user.getRealName()) +"</p>");
		out.println("</div>");
		out.println("</div>");

		out.println("<div class=\"form-group\">");
		out.println("<label for=\"txtNewPassword\" class=\"col-sm-3 control-label\">New Password <span style='color:red'>*</span></label>");
		out.println("<div class=\"col-sm-9\">");
		out.println("<input type=\"password\" maxlength=\"30\" name=\"txtNewPassword\" id=\"txtNewPassword\" class=\"form-control required complexPassword\" placeholder=\"New Password\" minlength=\"8\" autocomplete=\"off\">");
		out.println("</div>");
		out.println("</div>");

		out.println("<div class=\"form-group\">");
		out.println("<label for=\"txtConfirmPassword\" class=\"col-sm-3 control-label\">Confirm Password <span style='color:red'>*</span></label>");
		out.println("<div class=\"col-sm-9\">");
		out.println("<input  type=\"password\" maxlength=\"30\" name=\"txtConfirmPassword\" id=\"txtConfirmPassword\" class=\"form-control required\" equalTo= \"#txtNewPassword\" placeholder=\"Confirm Password\" autocomplete=\"off\" title=\"Password must match\">");
		out.println("</div>");
		out.println("</div>");

		out.println("<hr/>");
		out.println("<div class=\"form-group\">");
		out.println("<div class=\"col-sm-offset-3 col-sm-9\">");
		out.println("<input type=\"hidden\"  value=\""+user.getUserId()+"\" name=\"userid\"/>");
		out.println("<input class=\"btn btn-sm btn-default\" type=\"submit\"  value=\"Submit\" name=\"btnSubmit\"/>&nbsp;");
		out.println("</div>");
		out.println("</div>");
		out.println("</form>");
		out.println("</div>");//modal-body
		out.println("<div class=\"modal-footer\">");
		out.println("<button type=\"button\" class=\"btn btn-sm btn-default\" data-dismiss=\"modal\">Close</button>");
		out.println("</div>");
	}
}

