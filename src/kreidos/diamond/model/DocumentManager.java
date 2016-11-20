/**
 * Copyright 2005 by Arysys Technologies (P) Ltd.,
 * #3,Shop line,
 * Sasmira Marg,
 * Worli,Mumbai 400 025
 * India
 * 
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Arysys Technologies (P) Ltd. ("Confidential Information").  
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Arysys Technologies (P) Ltd.
 * 
 */
package kreidos.diamond.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.logging.Logger;

import kreidos.diamond.constants.ServerConstants;
import kreidos.diamond.model.dao.DocumentClassDAO;
import kreidos.diamond.model.dao.DocumentDAO;
import kreidos.diamond.model.dao.DocumentNoteDAO;
import kreidos.diamond.model.dao.DocumentRevisionDAO;
import kreidos.diamond.model.dao.UserDAO;
import kreidos.diamond.model.vo.Document;
import kreidos.diamond.model.vo.DocumentClass;
import kreidos.diamond.model.vo.DocumentNote;
import kreidos.diamond.model.vo.DocumentRevision;
import kreidos.diamond.model.vo.Hit;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.util.FileHelper;

import org.apache.commons.io.FileUtils;


/**
 * @author Rahul Kubadia
 * 
 */

public class DocumentManager {
	private PropertiesManager properties = PropertiesManager.getInstance();
	private Logger kLogger = Logger.getLogger(this.getClass().getPackage().getName());
	public DocumentManager() {}

	public void storeDocument(DocumentRevision documentRevision,DocumentClass documentClass)throws Exception{
	
		String fileName = documentRevision.getDocumentFile().getName();
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
		int fileLength = (int) documentRevision.getDocumentFile().length();

		//Check if document is new
		if(documentRevision.getDocumentId() == 0){ //New document hence create a record in DOCUMENTS Table
			Document document = new Document();
			document.setClassId(documentClass.getClassId());
			document.setDocumentType("IMAGE");
			document.setStatus(Hit.STATUS_AVAILABLE);
			document.setExtension(extension);
			document.setFilename(fileName.substring(0, fileName.lastIndexOf(".")));
			document.setHasNote((byte)0);
			document.setAccessCount(0);
			document.setRevisionId("1.0");
			documentRevision.setLength(fileLength);
			document.setCreatedBy(documentRevision.getUserName());
			document.setModifiedBy(documentRevision.getUserName());
			
			if(documentClass.getExpiryPeriod() > 0 ){
				Calendar today = Calendar.getInstance();
				today.set(Calendar.HOUR_OF_DAY,0);
				today.set(Calendar.MINUTE,0);
				today.set(Calendar.SECOND,0);
				today.set(Calendar.MILLISECOND,0);

				today.add(Calendar.DAY_OF_MONTH, documentClass.getExpiryPeriod());
				Timestamp expiry = new Timestamp(today.getTime().getTime());
				document.setExpiry(expiry);
			}

			DocumentDAO.getInstance().addDocument(document);
			//Increase the count of document when it is added 
			DocumentClassDAO.getInstance().increaseDocumentCount(documentClass);
			DocumentClassDAO.getInstance().increaseActiveDocumentCount(documentClass);
			documentRevision.setDocumentId(document.getDocumentId()); //set the document id in document revision
		}

		if(properties.getPropertyValue("storage").equalsIgnoreCase("folder")){ //Folder File System Support Kreidos 2016
			DocumentManager docMan = new DocumentManager();
			documentRevision.setDocumentFile( docMan.storeDocumentFileInFolder(documentRevision, documentClass) );
		}else{
			int dataId = 0;
			dataId = storeDocumentFileInDatabase(documentRevision,documentClass);

//			Store the document revision in the DOCUMENTREVISIONS TABLE
			documentRevision.setOffset(dataId);
		}
		
		documentRevision.setLength(fileLength);
		DocumentRevisionDAO.getInstance().addDocumentRevision(documentRevision);

		IndexRecordManager idxRecManager  = IndexRecordManager.getInstance();
		idxRecManager.createIndexRecord(documentClass,documentRevision.getDocumentId(),documentRevision.getRevisionId(),documentRevision.getIndexRecord());

		//Put the comments as Journal Notes
		saveCommentsAsJournalNotes(documentRevision.getDocumentId(),documentRevision.getComments(),documentRevision.getUserName());

	}
	
	private File storeDocumentFileInFolder(DocumentRevision documentRevision, DocumentClass documentClass) throws Exception{
		File destFile = null;
		File sourceFile = documentRevision.getDocumentFile();
		destFile = new File(FileHelper.getFilePath(documentRevision, documentClass));
		FileUtils.moveFile(sourceFile, destFile);
		return destFile;
	}
	
	private DocumentRevision retreiveDocumentFromFolder(Document document){
		DocumentRevision documentRevision = null;
		try {
			documentRevision = DocumentRevisionDAO.getInstance().readDocumentRevisionById(document.getDocumentId(), document.getRevisionId());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return documentRevision;
	}
	
	public static void deleteDocumentFromFolder(Document document){
		String separator = ServerConstants.SEPARATOR;
		String folder = ServerConstants.DATA_DIR + separator;
		try {
		folder += DocumentClassDAO.getInstance().readDocumentClassById(document.getClassId()).getClassName() + separator + document.getDocumentId();
			FileUtils.deleteDirectory(new File(folder));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private int storeDocumentFileInDatabase(DocumentRevision documentRevision,DocumentClass documentClass)throws Exception{
		return storeOthers(documentRevision,documentClass);
	}
	
	


	private int storeOthers(DocumentRevision documentRevision,DocumentClass documentClass) throws Exception {
		InputStream is = new FileInputStream(documentRevision.getDocumentFile());
		int dataId = 0;
		String dataTableName = documentClass.getDataTableName(); 
		Connection connection = ConnectionPoolManager.getInstance().getConnection();

		PreparedStatement psInsert = null;
		psInsert = connection.prepareStatement("INSERT INTO " + dataTableName+ "( DATA ) VALUES ( ? )");
		psInsert.setBinaryStream(1, is, (int) documentRevision.getDocumentFile().length());
		psInsert.executeUpdate();
		psInsert.close();
		connection.commit();

		String sqlStat = "SELECT MAX(DATAID) FROM " + dataTableName;
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(sqlStat);
		if(rs.next()){
			dataId = rs.getInt(1);
		}
		rs.close();
		statement.close();
		connection.close();
		is.close();
		is = null;

		return dataId;
	}

	private void saveCommentsAsJournalNotes(int documentId,String comments,String userName)throws Exception{
		if (comments.trim().length() > 0 ) {
			Document document = DocumentDAO.getInstance().readDocumentById(documentId);
			User user = UserDAO.getInstance().readUserByName(userName);
			DocumentNote jNote = new DocumentNote();
			jNote.setActive(true);
			jNote.setNoteData(comments);
			jNote.setDocumentId(documentId);
			jNote.setUserName(user.getUserName());
			jNote.setNoteType("P");

			DocumentNoteDAO.getInstance().addJournalNote(jNote);
			document.setHasNote(Hit.FLAG_HASNOTE);
			DocumentDAO.getInstance().updateDocument(document);
		}
	}

	public DocumentRevision retreiveDocument (Document document)throws Exception{
		DocumentRevision documentRevision = null;
		
		//		Check if Folder-based storage is enabled.
		if (properties.getPropertyValue("storage").equalsIgnoreCase("folder")){
			documentRevision = retreiveDocumentFromFolder(document);
		}
		else{
			DocumentClass documentClass = DocumentClassDAO.getInstance().readDocumentClassById(document.getClassId()); 
			documentRevision = DocumentRevisionDAO.getInstance().readDocumentRevisionById(document.getDocumentId(),document.getRevisionId());
			if(documentRevision == null) return documentRevision;
	
			ResultSet resultSet = null;
			Connection connection = ConnectionPoolManager.getInstance().getConnection();
			String sqlStat = "SELECT DATA FROM " + documentClass.getDataTableName() + " WHERE DATAID= ?";
			PreparedStatement  psSelect = connection.prepareStatement(sqlStat);
	
			try {
				psSelect.setInt(1, documentRevision.getOffset());
				resultSet = psSelect.executeQuery();
				if(resultSet.next()){
					InputStream inputStream = resultSet.getBinaryStream(1);
					String basePath = System.getProperty("java.io.tmpdir") ;
					if ( !(basePath.endsWith("/") || basePath.endsWith("\\")) ){
						basePath +=  System.getProperty("file.separator");
					}
					File documentFile = new File(basePath + document.getFullFilename() );
					OutputStream out=new FileOutputStream(documentFile);
					byte buf[]=new byte[1024];
					int len;
					while((len=inputStream.read(buf))>0){
						out.write(buf,0,len);
					}
					out.close();
					inputStream.close();
					documentRevision.setDocumentFile(documentFile);
				}
				resultSet.close();
				psSelect.close();
				connection.close();
			}catch (Exception e) {
				kLogger.severe("Unable to read record in data table");
				e.printStackTrace();
			}
		}
		return documentRevision;
		
	}

	/**
	 * @param documentRevision
	 * @param destinationDocumentClass
	 * @param sourceDocumentClassId
	 * @param document
	 * @param indexRecord
	 * @throws Exception
	 */
	public void moveDocument(Document document,DocumentRevision documentRevision,DocumentClass sourceDocumentClass,DocumentClass destinationDocumentClass,Hashtable<String,String> indexRecord)throws Exception{

		int movedDataId = documentRevision.getOffset();

		DocumentDAO.getInstance().updateDocument(document);
		IndexRecordManager idxRecManager  = IndexRecordManager.getInstance();
		idxRecManager.deleteIndexRecord(sourceDocumentClass.getClassId(), documentRevision.getDocumentId(),documentRevision.getRevisionId());
		idxRecManager.createIndexRecord(destinationDocumentClass, documentRevision.getDocumentId(), documentRevision.getRevisionId(), indexRecord);
		int dataId = 0;
		dataId = storeDocumentFileInDatabase(documentRevision,destinationDocumentClass);
		//Put the comments as Journal Notes
		saveCommentsAsJournalNotes(documentRevision.getDocumentId(),documentRevision.getComments(),documentRevision.getUserName());
		documentRevision.setOffset(dataId);
		DocumentRevisionDAO.getInstance().updateDocumentRevision(documentRevision);

		deleteMovedDocument(movedDataId,document,sourceDocumentClass);
	}

	private void deleteMovedDocument(int dataId,Document movedDocument,DocumentClass sourceDocumentClass) throws Exception {
		String dataTableName = sourceDocumentClass.getDataTableName(); 
		Connection connection = ConnectionPoolManager.getInstance().getConnection();

		PreparedStatement psDelete = null;
		psDelete = connection.prepareStatement("DELETE FROM " + dataTableName+ " WHERE DATAID="+dataId);
		psDelete.executeUpdate();
		psDelete.close();
		connection.commit();
		connection.close();


	}
}