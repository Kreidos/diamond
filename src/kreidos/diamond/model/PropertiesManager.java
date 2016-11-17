package kreidos.diamond.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

/**
 * @author Kreidos
 * @since Diamond 1.1
 * Configuration file support added 2016 in Diamond v1.1
 * This class manages the properties file and its values.
 */

public class PropertiesManager {
	private String configFile = "diamond.properties";
	private Properties properties = new Properties();
	private static PropertiesManager ref = null;
	
	private PropertiesManager(){ //constructor
		getDefaults();
		readPropertiesFile();
	}
	
	public static PropertiesManager getInstance(){
		if(ref == null)
			ref = new PropertiesManager();
		return ref;
	}
	
	public String getPropertyValue(String property){
		return properties.getProperty(property);
	}
	
	public void setPropertyValue(String Property, String value){
		properties.setProperty(Property, value);
		writePropertiesFile();
	}
	
	private void readPropertiesFile(){
		FileReader file = null;
		
		try{ 
			file = new FileReader(configFile);
			properties.load(file);
			file.close();
		}catch(FileNotFoundException e){
			System.out.println(configFile + " not found: Creating...");
			writePropertiesFile();
		}catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	private void writePropertiesFile(){
		FileWriter file = null;
		
		try{
			file = new FileWriter(configFile);
			properties.store(file, "Diamond DMS Configuration File");
			file.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	private void getDefaults(){
			properties.setProperty("httpport", "8080");
	}
}