/**
 * 
 */
package com.snakindia.cmd.exec.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;



/**
 * @author darora
 * 
 */
public class ApplicationProperties {
	static Logger log = Logger.getLogger(ApplicationProperties.class);

	public static String getProperty(String key) {
		String value = null;
		FileInputStream fis = null;
		try {Properties mainProperties = new Properties();
		String fileName = "application.properties";
		File jarPath = new File(ApplicationProperties.class.getProtectionDomain().getCodeSource().getLocation().getPath());

		String propertiesPath = jarPath.getParentFile().getAbsolutePath();
		if (null != propertiesPath && !"".equals(propertiesPath)) 
		{
			propertiesPath = propertiesPath.replace("\\", "/");
		}
		if(!propertiesPath.endsWith("/"))
		{
			propertiesPath=propertiesPath+"/";
		}
		fis = new FileInputStream(propertiesPath + fileName);
		mainProperties.load(fis);
		value = mainProperties.getProperty(key);

		fileName=  null;
		jarPath = null;
		propertiesPath = null;
		} 
		catch (IOException ioe) {
			Utilities.printStackTraceToLogs(ApplicationProperties.class.getName(), "getProperty()", ioe);
		} catch (Exception e) {
			Utilities.printStackTraceToLogs(ApplicationProperties.class.getName(), "getProperty()", e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					Utilities.printStackTraceToLogs(ApplicationProperties.class.getName(), "getProperty()", e);
				}
			}
		}
		return value;
	}
}
