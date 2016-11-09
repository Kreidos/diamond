/**
 * Created on Aug 9, 2004
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
 */

package com.primeleaf.krystal.web;


import java.util.logging.Logger;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.valves.AccessLogValve;

import com.primeleaf.krystal.constants.ServerConstants;

/**
 * This class starts the embedded tomcat server and loads the web apps depending upon the configuration settings.
 * @author Rahul Kubadia
 * @since 2.0
 *  
 */

public class WebServerManager extends Thread{

	private Logger kLogger = Logger.getLogger(this.getClass().getPackage().getName());

	private Tomcat embeddedTomcat = null;

	private boolean isRunning = false;

	public WebServerManager(int port,String appBase) {
		try {
			embeddedTomcat = new Tomcat();
			embeddedTomcat.setBaseDir(appBase);
			embeddedTomcat.setPort(port);
			embeddedTomcat.enableNaming();
			embeddedTomcat.getConnector().setURIEncoding("UTF-8");
			embeddedTomcat.getEngine().setName("krystal");
			StandardContext edmcContext = (StandardContext) embeddedTomcat.addWebapp("/", appBase+ ServerConstants.SYSTEM_DMC_PATH);
			edmcContext.setUseHttpOnly(false);
			boolean enableLogging = true;
			if(enableLogging){
				AccessLogValve alv = new AccessLogValve();
				alv.setDirectory(appBase+"/log");
				alv.setPrefix("krystal_dms_access");
				alv.setPattern("%h %l %u %t %r %s %b"); 
				alv.setSuffix(".log");
				alv.setEnabled(true);
				edmcContext.addValve(alv);
			}
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					if(isRunning) {
						kLogger.info("Stopping the Tomcat server, through shutdown hook");  
						try { 
							if (embeddedTomcat != null) {
								embeddedTomcat.stop();
							}
						} catch (LifecycleException e) {
							kLogger.severe("Error while stopping the Tomcat server, through shutdown hook" + e.getLocalizedMessage()); 
							e.printStackTrace();
						}                     
					}      
				}        
			});        
		} catch (Exception e) {
			kLogger.severe("Could not start TOMCAT server"+ e.getLocalizedMessage());
		}
	}

	public void run(){
		try{
			if(isRunning){
				kLogger.severe("Server already running");
				return;
			}
			embeddedTomcat.start();
			embeddedTomcat.getServer().await();
			isRunning=true;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * This method Stops the Tomcat server.
	 */
	public void stopTomcat() throws LifecycleException {
		if(!isRunning){
			kLogger.warning("Tomcat server is not running");
			return;
		}
		// Stop the embedded server
		embeddedTomcat.stop();
		isRunning=false;
	}
}