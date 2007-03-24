package uk.ac.sanger.cgp.bioview.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.sanger.cgp.bioview.exceptions.BiodrawRuntimeException;

/**
 * A class which encapsulates common acitivties against property files, system
 * properties and resource bundles
 * 
 * @author Original: kr2
 * @author  $Author: kr2 $
 * @version $Revision: 1.3 $
 */
public class PropertyUtils {
  
  protected static final Log log = LogFactory.getLog(PropertyUtils.class.getName());

  /**
   * For a given location this method will load the file into a properties
   * object
   * 
   * @param classpathLocation The loation of the properties file on the 
   * classpath as defined by {@link Class#getResourceAsStream(String)}
   * @return The loaded properties bundle
   * @throws CsaRuntimeException Thrown if an IOException is detected 
   * whilst loading the properties file
   */
  public static Properties getClasspathProperties(String classpathLocation) {
    Properties output = null;
    InputStream is = null;
    try {
      
      if(log.isDebugEnabled()) log.debug("Attempting to load "+classpathLocation);
      
      Class loaderClass = PropertyUtils.class;
      is = new BufferedInputStream(loaderClass.getResourceAsStream(classpathLocation));
      output = new Properties();
      output.load(is);
      
      if(log.isDebugEnabled()) log.debug("Loaded library " + classpathLocation);
    }
    catch(IOException e) {
      throw new BiodrawRuntimeException("Could not load library " + classpathLocation, e);
    }
    finally {
      try {
        if(is != null) {
          is.close();
        }
      }
      catch(IOException e) {
        if(log.isWarnEnabled()) log.debug("Could not load library "+ classpathLocation);
      }
    }
    return output;
  }
  
  public static Properties getProperties(String configFileLocation) {
    Properties newProperties = null;
    File requestedInput = new File(configFileLocation);
    if(requestedInput.exists()) {
      newProperties = new Properties();
      InputStream is = null;
      try {
        is = new FileInputStream(configFileLocation);
        newProperties.load(is);
      }
      catch(FileNotFoundException e) {
        throw new BiodrawRuntimeException("Configuration file could not be found at "+ configFileLocation, e);
      }
      catch(IOException e) {
        throw new BiodrawRuntimeException("Configuration file could not be read at "+ configFileLocation, e);
      }
      finally {
        try {
          if(is != null) {
            is.close();
          }
        }
        catch(IOException e) {
          log.info("Failed to close InputStream for "+ configFileLocation, e);
        }
      }
    }
		return newProperties;
	}
}
