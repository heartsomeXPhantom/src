package de.folt.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class implements an adapted properties class.
 * @author klemens
 */
public class OpenTMSProperties
{
    protected static final String FALSE = new Boolean(false).toString();

    /**
     * @uml.property name="openTMSInstance"
     * @uml.associationEnd
     */
    protected static OpenTMSProperties OpenTMSInstance = null;

    private static String path = new String();

    private static String urlClassPath = new String();

    private Exception exception = null;

    /**
     * @return the urlClassPath
     */
    public static String getUrlClassPath()
    {
        return urlClassPath;
    }

    /**
     * @param urlClassPath the urlClassPath to set
     */
    public static void setUrlClassPath(String urlClassPath)
    {
        OpenTMSProperties.urlClassPath = urlClassPath;
    }

    /**
     */
    protected static String propfileName = "OpenTMS.properties";

    protected static String propInitialFileName = "OpenTMS.properties";

    /**
     */
    private static String shortpath = new String();

    protected static final String TRUE = new Boolean(true).toString();

    /**
     * getInstance return an OpenTMSProperties property instance; only one per process; if OpenTMSInstance = null create an OpenTMSProperties
     * 
     * @return an OpenTMSProperties property instance
     */
    public static OpenTMSProperties getInstance()
    {
        try
        {
            if (OpenTMSInstance == null)
                OpenTMSInstance = new OpenTMSProperties();
            return OpenTMSInstance;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * getInstance return an OpenTMSProperties property instance; only one per process based on a property file; if OpenTMSInstance = null create an OpenTMSProperties
     * 
     * @param propertiesFile
     *            the properties file for the instance
     * @return an OpenTMSProperties property instance
     */
    public static OpenTMSProperties getInstance(String propertiesFile)
    {
        try
        {
            if (OpenTMSInstance == null)
            {
                OpenTMSInstance = new OpenTMSProperties(propertiesFile);
            }
            else
            {
                if (propertiesFile.equals(getPropfileName()))
                    return OpenTMSInstance;
                OpenTMSInstance = null;
                OpenTMSInstance = new OpenTMSProperties(propertiesFile);
            }

            return OpenTMSInstance;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * @return the propfileName
     * @uml.property name="propfileName"
     */
    public static String getPropfileName()
    {
        try
        {
            File f = new File(propfileName);
            if (f.exists())
            {
                String canName = f.getCanonicalPath();
                propfileName = canName;
                return canName;
            }
        }
        catch (Exception e)
        {

            e.printStackTrace();
        }
        return propfileName;
    }

    /**
     * getShortpath - get the short path of the properties file
     * 
     * @return the short path of the properties file
     * @uml.property name="shortpath"
     */
    public static String getShortpath()
    {
        return shortpath;
    }

    /**
     * @param propfileName
     *            the propfileName to set
     * @uml.property name="propfileName"
     */
    public static void setPropfileName(String propfileName)
    {
    	OpenTMSProperties.propfileName = propfileName;
    }

    /**
     * setShortpath
     * 
     * @param shortpath
     *            set the short path of the properties file
     * @uml.property name="shortpath"
     */
    public static void setShortpath(String shortpath)
    {
        OpenTMSProperties.shortpath = shortpath;
    }

    /**
     */
    protected Properties OpenTMSPropertiesInstance;

    /**
     * @throws EMXException
     */
    protected OpenTMSProperties()
    {
        OpenTMSPropertiesInstance = new Properties();
        InputStream systemResource = null;
        OpenTMSProperties.path = initOpenTMSPropertiesPath();

        try
        {
            systemResource = new URL(OpenTMSProperties.path).openStream();
        }
        catch (IOException exc1)
        {
            try
            {
                systemResource = new FileInputStream(propfileName);
                path = propfileName;
            }
            catch (IOException exc2)
            {
                systemResource = getClass().getClassLoader().getResourceAsStream(propfileName);
            }
        }
        catch (Exception exc1)
        {
            try
            {
                systemResource = new FileInputStream(propfileName);
                path = propfileName;
            }
            catch (IOException exc2)
            {
                systemResource = getClass().getClassLoader().getResourceAsStream(propfileName);
            }
        }
        if (systemResource == null)
        {
            return;
        }
        try
        {
            OpenTMSPropertiesInstance.load(systemResource);
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
    }

    /**
     * @param propFile
     *            set the OpenTMS property file path
     * @throws EMXException
     */

    public OpenTMSProperties(String propFile)
    {
        OpenTMSPropertiesInstance = new Properties();
        propfileName = propFile;
        path = propfileName;
        InputStream systemResource = null;
        try
        {
            systemResource = new FileInputStream(propFile);
            OpenTMSPropertiesInstance.load(systemResource);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            exception = ex;
        }
        OpenTMSInstance = this;
    }

    /**
     * getBooleanEMXProperty
     * 
     * @param key
     *            the key to retrieve
     * @return the value of the property key as a boolean
     */
    public boolean getBooleanOpenTMSProperty(String key)
    {
        return TRUE.equals(getInstance().OpenTMSPropertiesInstance.getProperty(key));
    }

    /**
     * getIntEMXProperty
     * 
     * @param key
     *            the key to retrieve
     * @return the value of the property key as an int
     */
    public int getIntOpenTMSProperty(String key)
    {
        String retValue = getInstance().OpenTMSPropertiesInstance.getProperty(key);
        if (retValue == null || retValue.equals(""))
        {
            return 0;
        }
        else
        {
            retValue = retValue.trim();
            return Integer.parseInt(retValue);
        }
    }

    /**
     * getLogLevel this method returns the log level for a specific class. The log level is defined in the OpenTMS.properties file.<br>
     * Example:
     * <pre>
     * # OpenTMS LogLevel - this is the general log level which will be used if no log level is defined for the class
     * # OpenTMS.LogLevel=0
     * # this allows to set specific LogLevels for packages + classnames
     * # Structure OpenTMS.LogLevel.<packagename>.<classname>
     * OpenTMS.LogLevel.de.folt.models.datamodel.sql.OpenTMSSQLDataSource=0
     * @param canonicalName the name of the class for the log level
     * @return
     */
    public int getLogLevel(String canonicalName)
    {
        try
        {
            String logLevelName = "OpenTMS.LogLevel." + canonicalName;
            String logValue = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty(logLevelName);
            if (logValue == null)
                logValue = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.LogLevel");
            if (logValue != null)
            {
                try
                {
                    return Integer.parseInt(logValue);
                }
                catch (NumberFormatException e)
                {

                }
            }
            else
            {

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * getOpenTMSProperties
     * 
     * @return the associated properties
     */
    public Properties getOpenTMSProperties()
    {
        return getInstance().OpenTMSPropertiesInstance;
    }

    /**
     * getOpenTMSPropertiesAsString return all openTMS properties in a string separated by \n
     * @return the openTMS properties string
     */
    public String getOpenTMSPropertiesAsString()
    {
        String propString = "";
        Properties prop = getOpenTMSProperties();
        Vector<String> pr = new Vector<String>();
        Enumeration<Object> enumkeys = prop.keys();

        while (enumkeys.hasMoreElements())
        {
            String key = (String) enumkeys.nextElement();
            String value = getOpenTMSProperty(key);

            pr.add(key + "=" + value);
        }

        Collections.sort(pr);
        for (int i = 0; i < pr.size(); i++)
        {
            propString = propString + pr.get(i) + "\n";
        }

        return propString;
    }

    /**
     * @return the openTMSPropertiesInstance
     * @uml.property name="openTMSPropertiesInstance"
     */
    public Properties getOpenTMSPropertiesInstance()
    {
        return OpenTMSPropertiesInstance;
    }

    /**
     * getEMXProperty retrieves an openTMS property as a string. It supports %<name>% notation. This means that the value of %<name>% is replaced by the value with the name of this property. If %CURRENTDIRECTOrY% is used the value is replaced with the name of the current directory. CURRENTDIRECTOrYASURL returns the current directory as URL
     * 
     * @param key
     *            the key to retrieve
     * @return the value of the property key as a string
     */
    public String getOpenTMSProperty(String key)
    {
        String retValue = getInstance().OpenTMSPropertiesInstance.getProperty(key);
        if (retValue == null)
            return null;
        // we must check now if the retValue contains one or more %<name>% and try to replace it
        if (retValue.indexOf("%") > -1)
        {
            try
            {
                Pattern pat = Pattern.compile(".*%(.+)%.*");
                Matcher m = pat.matcher(retValue);
                while (m.matches())
                {
                    String group = m.group(1);
                    String replValue = "";
                    if (group.equals("CURRENTDIRECTORY"))
                    {
                        replValue = de.folt.util.OpenTMSSupportFunctions.getCurrentDirectory();
                        replValue = replValue.replace('\\', '/');
                    }
                    else if (group.equals("CURRENTDIRECTORYASURL"))
                    {
                        replValue = de.folt.util.OpenTMSSupportFunctions.getCurrentDirectory();
                        replValue = replValue.replace('\\', '/');
                        File f = new File(replValue);
                        replValue = f.toURI().toURL().toString();
                        replValue = replValue.replaceAll("/$", "");
                    }
                    else
                    {
                        replValue = getInstance().OpenTMSPropertiesInstance.getProperty(group);
                        if ((replValue != null) && replValue.matches(".*%(.+)%.*"))
                        {
                            if (replValue.equals("CURRENTDIRECTORY"))
                            {
                                replValue = de.folt.util.OpenTMSSupportFunctions.getCurrentDirectory();
                                replValue = replValue.replace('\\', '/');
                                return replValue.trim();
                            }
                            else if (replValue.equals("CURRENTDIRECTORYASURL"))
                            {
                                replValue = de.folt.util.OpenTMSSupportFunctions.getCurrentDirectory();
                                replValue = replValue.replace('\\', '/');
                                File f = new File(replValue);
                                replValue = f.toURI().toURL().toString();
                                replValue = replValue.replaceAll("/$", "");
                                return replValue.trim();
                            }
                        }
                        else
                        {
                            return retValue == null ? null : retValue.trim();
                        }
                    }
                    retValue = retValue.replaceAll("%" + group + "%", replValue);
                    m = pat.matcher(retValue);
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        return retValue == null ? null : retValue.trim();
    }

    /**
     * initOpenTMSPropertiesPath set default OpenTMS property file path using getClass().getClassLoader().getResource("de/folt/util/OpenTMSProperties.class")
     * 
     * @return the path name of the property file
     */
    public String initOpenTMSPropertiesPath()
    {
        String filename = initOpenTMSPropertiesPath(true);
        if (propInitialFileName.equals(filename))
            return initOpenTMSPropertiesPath(false);
        else
            return filename;
    }

    /**
     * initOpenTMSPropertiesPath set default OpenTMS property file path using getClass().getClassLoader().getResource("de/folt/util/OpenTMSProperties.class")
     * 
     * @return the path name of the property file
     */
    public String initOpenTMSPropertiesPath(boolean bWithUserName)
    {
        URL urlOpenTMSPropertiesClass = getClass().getClassLoader().getResource("de/folt/util/OpenTMSProperties.class");
        String pathurlOpenTMSProperties = urlOpenTMSPropertiesClass.getFile();
        urlClassPath = pathurlOpenTMSProperties;
        int index = pathurlOpenTMSProperties.indexOf("openTMS.jar!");

        String newpropFileName = propfileName;
        if (bWithUserName)
        {
            String username = de.folt.util.OpenTMSSupportFunctions.getCurrentUser();
            newpropFileName = propfileName.replaceAll("(.*?)\\.(.*?)", "$1." + username + ".$2");
        }
        // String newpropFileName = propfileName;

        if ((index = pathurlOpenTMSProperties.indexOf("docliff-openTMS.jar!")) != -1)
        {
            pathurlOpenTMSProperties = pathurlOpenTMSProperties.substring(0, index);
            String newpath = pathurlOpenTMSProperties + newpropFileName;
            newpath = newpath.replaceAll("file:/", "");
            File f = new File(newpath);
            if (f.exists())
                propfileName = newpropFileName;
            pathurlOpenTMSProperties += propfileName;
        }
        else if (index != -1)
        {
            pathurlOpenTMSProperties = pathurlOpenTMSProperties.substring(0, index);
            String newpath = pathurlOpenTMSProperties + newpropFileName;
            newpath = newpath.replaceAll("file:/", "");
            File f = new File(newpath);
            if (f.exists())
                propfileName = newpropFileName;
            pathurlOpenTMSProperties += propfileName;
        }
        else if ((index = pathurlOpenTMSProperties.indexOf("openTMS.jar!")) != -1)
        {
            pathurlOpenTMSProperties = pathurlOpenTMSProperties.substring(0, index);
            String newpath = pathurlOpenTMSProperties + newpropFileName;
            newpath = newpath.replaceAll("file:/", "");
            File f = new File(newpath);
            if (f.exists())
                propfileName = newpropFileName;
            pathurlOpenTMSProperties += propfileName;
        }
        else if ((index = pathurlOpenTMSProperties.indexOf("openTMS_TM.jar!")) != -1)
        {
            pathurlOpenTMSProperties = pathurlOpenTMSProperties.substring(0, index);
            String newpath = pathurlOpenTMSProperties + newpropFileName;
            newpath = newpath.replaceAll("file:/", "");
            File f = new File(newpath);
            if (f.exists())
                propfileName = newpropFileName;
            pathurlOpenTMSProperties += propfileName;
        }
        else if ((index = pathurlOpenTMSProperties.indexOf("arayaserver.jar!")) != -1)
        {
            pathurlOpenTMSProperties = pathurlOpenTMSProperties.substring(0, index);
            String newpath = pathurlOpenTMSProperties + newpropFileName;
            newpath = newpath.replaceAll("file:/", "");
            File f = new File(newpath);
            if (f.exists())
                propfileName = newpropFileName;
            pathurlOpenTMSProperties += propfileName;
        }
        else
        {
            String newpath = newpropFileName;
            newpath = newpath.replaceAll("file:/", "");
            File f = new File(newpath);
            if (f.exists())
                propfileName = newpropFileName;
            pathurlOpenTMSProperties = propfileName;
        }

        shortpath = pathurlOpenTMSProperties.replaceFirst("file:", "");
        System.out.println("initOpenTMSPropertiesPath=" + pathurlOpenTMSProperties);
        return pathurlOpenTMSProperties;
    }

    /**
     * loadOpenTMSPropertiesToXML loads the properties from an property xml file
     */
    public void loadOpenTMSPropertiesToXML()
    {
        try
        {
            if (OpenTMSInstance == null)
            {
                OpenTMSProperties.getInstance();
            }
            FileInputStream os = new FileInputStream(shortpath + ".xml");
            getInstance().OpenTMSPropertiesInstance.loadFromXML(os);
        }
        catch (Exception e)
        {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    /**
     * makeCopyOpenTMSProperties - create a backup copy the OpenTMSPropertiesInstance
     */
    public void makeCopyOpenTMSProperties()
    {
        try
        {
            FileOutputStream outfile = new FileOutputStream(shortpath + ".copy");
            getInstance().OpenTMSPropertiesInstance.store(outfile, "OpenTMS Properties " + de.folt.util.OpenTMSSupportFunctions.returnCurrentDate());
            outfile.close();
            return;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return;
        }
    }

    /**
     * returnPropFilePath
     * 
     * @return property file path
     */
    public String returnPropFilePath()
    {
        return path;
    }

    /**
     * sameValue
     * 
     * @param value1
     * @param value2
     * @return
     */
    private boolean sameValue(String value1, String value2)
    {
        return (value1 == value2 || value1 != null && value1.equals(value2));
    }

    /**
     * saveOpenTMSProperties
     */
    public void saveOpenTMSProperties()
    {
        try
        {
            FileOutputStream outfile = new FileOutputStream(shortpath);
            getInstance().OpenTMSPropertiesInstance.store(outfile, "OpenTMS Properties");
            outfile.close();
            OpenTMSPropertiesInstance = null;
            OpenTMSProperties.getInstance();
            return;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return;
        }
    }

    /**
     * saveOpenTMSPropertiesToXML create an xml file from the OpenTMSPropertiesInstance
     */
    public void saveOpenTMSPropertiesToXML()
    {
        try
        {
            String encoding = "UTF8";
            FileOutputStream os = new FileOutputStream(shortpath + ".xml");
            getInstance().OpenTMSPropertiesInstance.storeToXML(os, "OpenTMS Properties " + de.folt.util.OpenTMSSupportFunctions.returnCurrentDate(), encoding);
        }
        catch (Exception e)
        {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    /**
     * @param openTMSPropertiesInstance
     *            the openTMSPropertiesInstance to set
     * @uml.property name="openTMSPropertiesInstance"
     */
    public void setOpenTMSPropertiesInstance(Properties openTMSPropertiesInstance)
    {
        OpenTMSPropertiesInstance = openTMSPropertiesInstance;
    }

    /**
     * setEMXProperty
     * 
     * @param key
     * @param value
     */
    public void setOpenTMSProperty(String key, boolean value)
    {
        setOpenTMSProperty(key, value ? TRUE : FALSE);
    }

    /**
     * setOpenTMSProperty
     * 
     * @param key
     * @param value
     */
    public void setOpenTMSProperty(String key, int value)
    {
        setOpenTMSProperty(key, new Integer(value).toString());
    }

    /**
     * setEMXProperty
     * 
     * @param key
     * @param value
     */
    public void setOpenTMSProperty(String key, String value)
    {
        String oldValue = getOpenTMSProperty(key);
        if (!sameValue(oldValue, value))
        {
            if (value == null)
                getInstance().OpenTMSPropertiesInstance.remove(key);
            else
                getInstance().OpenTMSPropertiesInstance.setProperty(key, value);
        }
    }

    /**
     * @param exception the exception to set
     */
    public void setException(Exception exception)
    {
        this.exception = exception;
    }

    /**
     * @return the exception
     */
    public Exception getException()
    {
        return exception;
    }

}