package kreidos.diamond.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
	private static PropertiesManager instance = null;
	
	private PropertiesManager(){ //constructor
		getDefaults();
		readPropertiesFile();
	}

	public static PropertiesManager getInstance(){
		if(instance == null)
			instance = new PropertiesManager();
		return instance;
	}

	/**
	 * Returns the value associated with the supplied property.
	 * @author Kreidos
	 * @since Diamond 1.1
	 * @param property
	 * @return value
	 */
	
	public String getPropertyValue(String property){
		return properties.getProperty(property);
	}
	
	/**
	 * Sets the value of the supplied property.
	 * Settings are written out to the configuration file immediately.
	 * @author Kreidos
	 * @since Diamond 1.1
	 * @param property
	 * @param value
	 */
	public void setPropertyValue(String property, String value){
		properties.setProperty(property, value);
		writePropertiesFile();
	}
	
	private void readPropertiesFile(){
		try(FileReader file = new FileReader(configFile)){ 
			properties.load(file);
		}catch(FileNotFoundException e){
			System.out.println(configFile + " not found: Creating...");
			writePropertiesFile();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}	
	}
	
	private void writePropertiesFile(){
		try(FileWriter file = new FileWriter(configFile)){
			properties.store(file, "Diamond DMS Configuration File");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	private void getDefaults(){
			properties.setProperty("httpport", "8080");
			properties.setProperty("odometer", "true");
			properties.setProperty("dbcheck", "false");
	}
}