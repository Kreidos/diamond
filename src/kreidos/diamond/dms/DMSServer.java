/**
 * Created on Aug 1, 2003
 *
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
 * Portions copyright Kreidos@users.noreply.github.com, 2016
 */

package kreidos.diamond.dms;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import kreidos.diamond.constants.ServerConstants;
import kreidos.diamond.model.ConnectionPoolManager;
import kreidos.diamond.model.PropertiesManager;
import kreidos.diamond.model.dao.DocumentClassDAO;
import kreidos.diamond.model.dao.UserDAO;
import kreidos.diamond.model.vo.DocumentClass;
import kreidos.diamond.model.vo.User;
import kreidos.diamond.util.DerbyQueries;
import kreidos.diamond.util.ExpiryProcessor;
import kreidos.diamond.util.DBDoctor;
import kreidos.diamond.web.WebServerManager;


/**
 * @author Rahul Kubadia
 * @since 1.0
 * @comments Krystal Server initialized , started and stopped using methods in this class.
 * @see kreidos.diamond.web.KrystalSession
 * 
 */

public class DMSServer {
	private Logger kLogger = Logger.getLogger(this.getClass().getPackage().getName());
	private static String KRYSTAL_HOME = "";
	private String SERVER_VERSION = "X.X";
	private static WebServerManager webServerManager;
	private static DMSServer serverInstance;
	private PropertiesManager properties = PropertiesManager.getInstance();
	
	public static synchronized DMSServer getInstance(){
		if(serverInstance == null){
			serverInstance = new DMSServer();
		}
		return serverInstance;
	}
	
	private DMSServer() {
		try {
			setEnvironment();
			configureDataStore();
			updateDatabase();		//Checks and updates obsolete DBs, Kreidos@github, 2016
			checkDatabase();
			startWebApplications();
			updateLoginStatus();
			adjustDocumentCount();
			processExpiry();
		} catch (Exception e) {
			kLogger.severe("Unable to start " + SERVER_VERSION + " due to " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void setEnvironment() {
		KRYSTAL_HOME = System.getProperty("krystal.home");
		if (KRYSTAL_HOME == null) {
			KRYSTAL_HOME = System.getProperty("user.dir");
			System.setProperty("krystal.home", KRYSTAL_HOME);
		}
		SERVER_VERSION = ServerConstants.SERVER_NAME + " " + ServerConstants.SERVER_VERSION ;
		//If shutdown file exist then delete it before starting the server
		File f = new File(KRYSTAL_HOME + "/shutdown");
		if (f.exists()) {
			f.delete();
		}
		kLogger.info("Initializing " + SERVER_VERSION + " On : " + System.getProperty("java.version"));
		kLogger.info("Starting " + SERVER_VERSION + "...");
	}

	private void startWebApplications() {
		try{
			kLogger.info("Starting web application server...");
			int httpPort = Integer.parseInt(properties.getPropertyValue("httpport"));
			webServerManager = new WebServerManager(httpPort,KRYSTAL_HOME);
			webServerManager.start();
		}catch (Exception ex) {
			kLogger.severe("Error starting web application server : " + ex.getLocalizedMessage());
			ex.printStackTrace();
			System.exit(0);
		}
	}



	/**
	 * This method sets login status of the all the users to "N"
	 * @author Rahul Kubadia
	 * @since 6.0
	 */
	private void updateLoginStatus(){
		try{
			for(User user: UserDAO.getInstance().readUsers("")){
				user.setLoggedIn(false);
				UserDAO.getInstance().updateLoginStatus(user);
			}
		}catch(Exception e){
			kLogger.severe("Error updating login status : " + e.getLocalizedMessage());
			e.printStackTrace(System.err);
		}
	}

	private void configureDataStore(){
		try{
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
			String connectionString = "jdbc:derby:" + System.getProperty("user.dir") + "/data/" + ServerConstants.KRYSTAL_DATABASE + ";";
			Connection databaseConnection = null;
			try{
				databaseConnection = DriverManager.getConnection(connectionString);
				if(databaseConnection != null){
					databaseConnection.close();
				}
			}catch(Exception ex){ //Database does not exist hence create it
				connectionString = "jdbc:derby:" + System.getProperty("user.dir") + "/data/" + ServerConstants.KRYSTAL_DATABASE + ";create=true;";
				databaseConnection = DriverManager.getConnection(connectionString);
				ResourceBundle queryResource =  DerbyQueries.getBundle("kreidos.diamond.util.DerbyQueries");
				Statement stat = databaseConnection.createStatement();
				stat.execute(" CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.user."+ ServerConstants.KRYSTAL_DATABASEOWNER + "', '" + ServerConstants.KRYSTAL_DATABASEPASSWORD + "')");
				stat.execute(" CREATE SCHEMA "+ServerConstants.KRYSTAL_DATABASEOWNER+" ");
				databaseConnection.close();
				connectionString += ";user=" + ServerConstants.KRYSTAL_DATABASEOWNER + ";password=" + ServerConstants.KRYSTAL_DATABASEPASSWORD;
				databaseConnection = DriverManager.getConnection(connectionString);
				stat = databaseConnection.createStatement();
				stat.execute(" SET SCHEMA "+ServerConstants.KRYSTAL_DATABASEOWNER+" ");
				Statement stmt = databaseConnection.createStatement();
				for (int i = 1; i <= 14; i++) {
					String sqlStat = queryResource.getString("query" + i);
					stmt.execute(sqlStat);
				}
				databaseConnection.commit();
				databaseConnection.close();
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void updateDatabase() throws SQLException{ //Checks and updates obsolete DBs. Kreidos@github 2016
		kLogger.info("Checking database for updates...");
		Connection databaseConnection = ConnectionPoolManager.getInstance().getConnection();
		Statement stat = databaseConnection.createStatement();
		try{
			stat.execute("SELECT FILENAME FROM DOCUMENTS");
		}catch(Exception e){
			kLogger.info("Update Required: FILENAMES entry not found in database, creating...");
			stat.execute("ALTER TABLE DOCUMENTS ADD COLUMN FILENAME VARCHAR (256)");
			databaseConnection.commit();
		}
		databaseConnection.close();
		kLogger.info("Database update complete.");
	}
	
	private void checkDatabase(){
		if(properties.getPropertyValue("dbcheck").equalsIgnoreCase("true")){
			DBDoctor.checkDatabase(); //Run database check.
			properties.setPropertyValue("dbcheck", "false"); //only runs once.
		}
	}
	
	private void adjustDocumentCount(){
		try{
			for(DocumentClass documentClass: DocumentClassDAO.getInstance().readDocumentClasses("")){
				DocumentClassDAO.getInstance().adjustDocumentCounts(documentClass);
			}
		}catch(Exception e){
			kLogger.severe("Error adjusting document count  : " + e.getLocalizedMessage());
			e.printStackTrace(System.err);
		}
	}
	
	private void processExpiry(){
		try{
			TimerTask task  = new ExpiryProcessor();
			Timer timer = new Timer();
			Date scheduleDate = Calendar.getInstance().getTime();
			timer.schedule(task,scheduleDate);
		}catch(Exception e){
			kLogger.severe("Error processing document expiry  : " + e.getLocalizedMessage());
			e.printStackTrace(System.err);
		}
	}
}