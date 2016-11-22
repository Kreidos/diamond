package kreidos.diamond.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import kreidos.diamond.model.ConnectionPoolManager;
import kreidos.diamond.model.dao.DocumentClassDAO;
import kreidos.diamond.model.dao.DocumentDAO;
import kreidos.diamond.model.vo.Document;
import kreidos.diamond.model.vo.DocumentClass;

/**
 * Contains methods to help update and repair databases.
 * @author Kreidos
 * @since Diamond 1.1.2
 */
public class DBDoctor {
	

	public static void checkDatabase(){
		System.out.println("DB Doctor: Database check requested.");
		try {
			cleanOrphans();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(9);
		}
		System.out.println("DB Doctor: Database check complete.");
	}
	
	/** Walks through all Data tables and removes orphaned entries.
	 * This is an intensive process and may be slow on large databases.
	 * @author Kreidos
	 * @since Diamond 1.1.2
	 * @return int entriesPurged
	 * @throws SQLException
	 */

	private static int cleanOrphans() throws SQLException{
		ArrayList<DocumentClass> allDocumentClasses = DocumentClassDAO.getInstance().readDocumentClasses("");
		int entriesPurged = 0;
		for(DocumentClass documentClass: allDocumentClasses){
			System.out.println("Checking Class " + documentClass.getClassName());
			ArrayList<Integer> dataIds = getDataIds(documentClass);
			String dataTableName = documentClass.getDataTableName();
			for(Integer dataId: dataIds){
				if(!validateDataId(dataTableName, dataId))
					entriesPurged++;
			}
		}
		if(entriesPurged > 0)
			System.out.println("Removed " + entriesPurged + " orphaned files.");
		return entriesPurged;

	}

	private static ArrayList<Integer> getDataIds(DocumentClass documentClass) throws SQLException{
		Connection connection = ConnectionPoolManager.getInstance().getConnection();
		String dataTableName = documentClass.getDataTableName();

		PreparedStatement ps = connection.prepareStatement("SELECT DATAID FROM " + dataTableName);
		ResultSet rs = ps.executeQuery(); 

		ArrayList<Integer> dataIds = new ArrayList<>();
		while(rs.next()){
			dataIds.add(rs.getInt("DATAID"));
		}
		ps.close();
		connection.close();
		return dataIds;		
	}


	private static Boolean validateDataId(String dataTableName, int dataId) throws SQLException{
		Connection connection = ConnectionPoolManager.getInstance().getConnection();
		PreparedStatement psQuery = connection.prepareStatement("SELECT DOCUMENTID FROM DOCUMENTREVISIONS WHERE SOFFSET=" + dataId);
		ResultSet rs = psQuery.executeQuery();
		Boolean match = false;
		while(rs.next()){ //Check if any of the potential matches actually owns this file.
			int docId = rs.getInt(1);
			DocumentClass docClass = null;
			try {
				Document document = DocumentDAO.getInstance().readDocumentById(docId);
				docClass = DocumentClassDAO.getInstance().readDocumentClassById(document.getClassId());
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			if(docClass.getDataTableName().equals(dataTableName)){
				match = true;
				break;
			}
		}
		psQuery.close();
		if(!match){ //no match. Delete the entry.
			PreparedStatement psDelete = connection.prepareStatement("DELETE FROM " + dataTableName + " WHERE DATAID=" + dataId);
			psDelete.execute();
			psDelete.close();
			System.out.println("Removed orphan file with DATAID of " + dataId);
		}
		connection.commit();
		connection.close();
		return match;

	}



}
