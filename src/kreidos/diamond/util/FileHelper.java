package kreidos.diamond.util;

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
	private static String separator = System.getProperty("file.separator");
	private static final String DATA_DIR = "data" + separator + "filestore";
	
	public static String getFilePath(DocumentRevision documentRevision, DocumentClass documentclass){
		String filePath = null;
		try {
			 filePath = DATA_DIR + separator + documentclass.getClassName() + separator + documentRevision.getDocumentId()
					+ separator + documentRevision.getRevisionId() + separator + DocumentDAO.getInstance().readDocumentById(documentRevision.getDocumentId()).getFullFilename();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePath;
	}
	
}
