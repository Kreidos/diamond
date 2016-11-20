package kreidos.diamond.util;

import kreidos.diamond.constants.ServerConstants;
import kreidos.diamond.model.dao.DocumentDAO;
import kreidos.diamond.model.vo.DocumentClass;
import kreidos.diamond.model.vo.DocumentRevision;

/**
 * returns the full path to a file given documentRevision and documentclass
 * 
 * @author Kreidos
 * @since 1.2
 *
 */

public class FileHelper {
	private static String dataDir = ServerConstants.DATA_DIR;
	private static String separator = ServerConstants.SEPARATOR;
	
	public static String getFilePath(DocumentRevision documentRevision, DocumentClass documentclass){
		String filePath = null;
		try {
			 filePath = dataDir + separator + documentclass.getClassName() + separator + documentRevision.getDocumentId()
					+ separator + documentRevision.getRevisionId() + separator + DocumentDAO.getInstance().readDocumentById(documentRevision.getDocumentId()).getFullFilename();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePath;
	}
	
}
