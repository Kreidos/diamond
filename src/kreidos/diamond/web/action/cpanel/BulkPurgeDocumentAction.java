package kreidos.diamond.web.action.cpanel;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kreidos.diamond.constants.HTTPConstants;
import kreidos.diamond.model.AuditLogManager;
import kreidos.diamond.model.dao.DocumentClassDAO;
import kreidos.diamond.model.dao.DocumentDAO;
import kreidos.diamond.model.vo.AuditLogRecord;
import kreidos.diamond.model.vo.Document;
import kreidos.diamond.model.vo.DocumentClass;
import kreidos.diamond.model.vo.Hit;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.web.action.Action;
import kreidos.diamond.web.view.WebView;

public class BulkPurgeDocumentAction implements Action {

	public WebView execute(HttpServletRequest request, HttpServletResponse response) throws Exception{
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute(HTTPConstants.SESSION_KRYSTAL);
		ArrayList<Document> documents = null;
		String type = request.getParameter("type")!=null?request.getParameter("type"):Hit.STATUS_DELETED;
		String classid = request.getParameter("classid")!=null?request.getParameter("classid"):"0";
		int classId = 0;
		try{
			classId = Integer.parseInt(classid);
		}catch(Exception ex){
			request.setAttribute(HTTPConstants.REQUEST_ERROR,"Invalid input");
			return new RecycleBinAction().execute(request, response);
		}
		DocumentClass documentClass = DocumentClassDAO.getInstance().readDocumentClassById(classId);
		if(documentClass == null){
			request.setAttribute(HTTPConstants.REQUEST_ERROR, "Invalid document class");
			return new RecycleBinAction().execute(request, response);
		}
		if(Hit.STATUS_DELETED.equalsIgnoreCase(type)){
			documents = DocumentDAO.getInstance().readDocuments("SELECT * FROM DOCUMENTS WHERE STATUS = '"+ Hit.STATUS_DELETED +"' AND CLASSID="+documentClass.getClassId());
		}else{
			documents = DocumentDAO.getInstance().readDocuments("SELECT * FROM DOCUMENTS WHERE STATUS = '"+ Hit.STATUS_EXPIRED +"' AND CLASSID="+documentClass.getClassId());
		}
		
		int delCount = 0;
		for(Document document : documents){
			DocumentDAO.getInstance().deleteDocument(document);
			DocumentClassDAO.getInstance().decreaseDocumentCount(documentClass);
			AuditLogManager.log(new AuditLogRecord(
					document.getDocumentId(),
					AuditLogRecord.OBJECT_DOCUMENT,
					AuditLogRecord.ACTION_DELETED,
					loggedInUser.getUserName(),
					request.getRemoteAddr(),
					AuditLogRecord.LEVEL_INFO,"", "Document ID : " + document.getDocumentId() + " permanently deleted")
			);
			delCount++;
		}
		
		request.setAttribute(HTTPConstants.REQUEST_MESSAGE, delCount + " documents permanently deleted successfully");
		return (new RecycleBinAction().execute(request, response));
	}

}
