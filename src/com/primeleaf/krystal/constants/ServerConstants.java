/**
 * Created on Sep 22, 2005 
 *
 * Copyright 2005 by Primeleaf Consutling (P) Ltd.,
 * #29,784/785 Hendre Castle,
 * D.S.Babrekar Marg,
 * Gokhale Road(North),
 * Dadar,Mumbai 400 028
 * India
 * 
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Primeleaf Consutling (P) Ltd. ("Confidential Information").  
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Primeleaf Consutling (P) Ltd. 
 */
package com.primeleaf.krystal.constants;

/**
 * @author Rahul Kubadia
 * @since 2.0
 * @see com.primeleaf.krystal.web.WebServerManager
 * @comments This class keeps all constants reqiured for server 
 */
public class ServerConstants {
	public static final String SERVER_VERSION = "1.1";
	public static final String SERVER_NAME = "Diamond DMS";
	
	public static final String KRYSTAL_DATABASE="KRYSTALCE";
	public static final String KRYSTAL_DATABASEOWNER="KRYSTALDBO";
	public static final String KRYSTAL_DATABASEPASSWORD="Krystal2015";
	
	public static final String INITIALCONTEXT_NAME = "/server";
	public static final String SERVERCONTEXT_ENV = INITIALCONTEXT_NAME + "/comp/env";
	public static final String JDBC_DATASOURCE = "/jdbc/data";
	public static final String JDBC_POOLDATASOURCE  = "/jdbc/source";
	public static final String JDBC_POOL  = "/jdbc/pool";
	
	public static final String SYSTEM_DMC_PATH ="/webapps/DMC";
	public static final String SYSTEM_SETUP_PATH ="/webapps/SETUP";
	
	public static final String CONFIG_FILE = "/conf/config.xml";
	
	public static final String CONFIG_DBNAME="DatabaseName";
	public static final String CONFIG_DBOWNER="DatabaseOwner";
	public static final String CONFIG_DBPORT="DatabasePort";
	public static final String CONFIG_DBSYSTEM="DatabaseSystem";
	public static final String CONFIG_DBPASSWORD="DatabasePassword";
	public static final String CONFIG_DBBLOCKSIZE="DatabaseBlockSize";
	public static final String CONFIG_DBPLATFORM="DatabasePlatform";
	public static final String CONFIG_DBINSTANCE="DatabaseInstance";
	
	public static final String CONFIG_WORKFLOW="EnableWorkflow";
	
	public static final String CONFIG_LOGFILE="LogFile";
	
	public static final String CONFIG_SMTPAUTH="SMTPAuth";
	public static final String CONFIG_SMTPHOST="SMTPHost";
	public static final String CONFIG_SMTPPORT="SMTPPort";
	public static final String CONFIG_SMTPFROM="SMTPFrom";
	public static final String CONFIG_SMTPNAME="SMTPFromName";
	public static final String CONFIG_SMTPUSER="SMTPUserName";
	public static final String CONFIG_SMTPPASS="SMTPPassword";
	
	public static final String CONFIG_AUTOLOGIN="AutoLogin";
	public static final String CONFIG_ALWAYSHITLIST="AlwaysHitList";
	public static final String CONFIG_NEWWINDOW="NewWindow";
	
	public static final String CONFIG_HTTPPORT="HTTPPort";
	public static final String CONFIG_SESSIONTIMEOUT="SessionTimeout";
	
	public static final String CONFIG_SKIN="Skin";
	public static final String CONFIG_COMPLETED="Configured";
	public static final String CONFIG_IPADDRESS="IPAddress";
	
	public static final String SYSTEM_USER="SYSTEM";
	public static final String SYSTEM_ADMIN_USER="ADMINISTRATOR";
	
	public static final String DEFAULT_EXPIRY_DATE="12/31/2099";
	public static final String STORAGE_DIRECTORY_FORMAT="yyyy-MM";
	
	public static final String FORMAT_SHORT_DATE="dd-MMM-yyyy";
	
	public static final String ENCRYPTION_ALGORITHM="HMACMD5";
	
	/**
	 * 
	 */
	public ServerConstants() {
		super();
	}

}
