package kreidos.diamond.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * This class manages the properties file.
 * 
 * @author Kreidos
 * @since Diamond 1.1
 * @comments Configuration file support added 2016 in Diamond v1.1
 */

public class PropertiesManager{
	private String configFile = "diamond.properties";
	private Properties properties = new Properties(getDefaults());
	private static PropertiesManager instance = null;

	private static final String[][] NEW_FILE_DEFAULTS = {
			{ "httpport", "8080" },
			{ "odometer", "true" } };
	private static final String[][] INTERNAL_DEFAULTS = {
			{ "httpport", "8080" },
			{ "odometer", "true" },
			{ "dbcheck", "false" } };

	private PropertiesManager(){ // constructor
		readPropertiesFile();
	}

	public static PropertiesManager getInstance(){
		if(instance == null)
			instance = new PropertiesManager();
		return instance;
	}

	/**
	 * Returns the value associated with the supplied property.
	 * 
	 * @author Kreidos
	 * @param property
	 * @return value
	 */

	public String getPropertyValue(String property){
		return properties.getProperty(property);
	}

	/**
	 * Sets the value of the supplied property. Settings are written out to the properties file
	 * immediately.
	 * 
	 * @author Kreidos
	 * @param property
	 * @param value
	 */

	public void setPropertyValue(String property, String value){
		properties.setProperty(property, value);
		writePropertiesFile();
	}

	/**
	 * Removes the specified property. Settings are written out to the properties file immediately.
	 * 
	 * @author Kreidos
	 * @param property
	 */

	public void clearPropertyValue(String property){
		properties.remove(property);
		writePropertiesFile();
	}

	private void readPropertiesFile(){
		try(FileReader file = new FileReader(configFile)){
			properties.load(file);
		} catch(FileNotFoundException e){
			System.out.println(configFile + " not found: Creating...");
			createDefaultFile();
		} catch(IOException e){
			System.out.println(e.getMessage());
		}
	}

	private void createDefaultFile(){
		try(FileWriter file = new FileWriter(configFile)){
			for(int i = 0; i < NEW_FILE_DEFAULTS.length; i++ )
				properties.setProperty(NEW_FILE_DEFAULTS[i][0], NEW_FILE_DEFAULTS[i][1]);
			properties.store(file, "Diamond DMS Configuration File");
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	private void writePropertiesFile(){
		try(FileWriter file = new FileWriter(configFile)){
			properties.store(file, "Diamond DMS Configuration File");
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	private Properties getDefaults(){
		Properties defaults = new Properties();
		for(int i = 0; i < INTERNAL_DEFAULTS.length; i++ )
			defaults.setProperty(INTERNAL_DEFAULTS[i][0], INTERNAL_DEFAULTS[i][1]);
		return defaults;
	}
}
