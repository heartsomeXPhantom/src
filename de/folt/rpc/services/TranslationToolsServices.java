package de.folt.rpc.services;

import java.util.Hashtable;
import java.util.Vector;

import de.folt.rpc.webserver.ConfigurationHandler;

/**
 * @author   Klemens Waldhör
 */
public final class TranslationToolsServices
{
    /**
     * @uml.property  name="confHandler"
     * @uml.associationEnd  
     */
    ConfigurationHandler confHandler = null;

    /**
     */
    String configurationHandler = "";

    /**
     */
    String propertiesFile = "";

    /**
     * @return   the configurationHandler
     * @uml.property  name="configurationHandler"
     */
    public String getConfigurationHandler()
    {
        return configurationHandler;
    }

    /**
     * @return   the propertiesFile
     * @uml.property  name="propertiesFile"
     */
    public String getPropertiesFile()
    {
        return propertiesFile;
    }

    public void initTranslationToolsServices(String propertiesFile, String configurationHandler)
    {
        System.out.println("initTranslationToolsServices using: " + configurationHandler);
        confHandler = new ConfigurationHandler(configurationHandler);
    }

    /**
     * run
     * 
     * @param hashtable
     * @return result as a vector of the run of the Translation Tools Service
     */
    public Vector<String> run(Hashtable<String, String> hashtable)
    {
        Vector<String> vec = null;
        String message = (String) hashtable.get("message");
        if (message == null)
        {
            vec = new Vector<String>();
            vec.add("9000");
            vec.add("Service name was not supplied");
            return vec;
        }
        try
        {
            RPCMessage handler = null;

            // now we must search for the method in the configurations
            if (confHandler.bMethodSupported(message)) // message = <translet name="CreateDatasource">
            {
                vec = confHandler.executeTranslet(message, hashtable);
            }
            else
            {
                String classname = "de.folt.rpc.messages." + message;
                System.out.println("Execute class: " + classname);
                handler = (RPCMessage) Class.forName(classname).newInstance();
                vec = handler.execute(hashtable);
            }
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            vec = new Vector<String>();
            vec.add("9005");
            vec.add("No requested service : " + e.getMessage());
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
            vec = new Vector<String>();
            vec.add("9010");
            vec.add(e.getMessage());
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            vec = new Vector<String>();
            vec.add("9015");
            vec.add(e.getMessage());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            vec = new Vector<String>();
            vec.add("9020");
            vec.add(ex.getMessage());
        }
        System.runFinalization();
        System.gc();
        return vec;
    }

    /**
     * @param configurationHandler   the configurationHandler to set
     * @uml.property  name="configurationHandler"
     */
    public void setConfigurationHandler(String configurationHandler)
    {
        this.configurationHandler = configurationHandler;
    }

    /**
     * @param propertiesFile   - the propertiesFile to set
     * @uml.property  name="propertiesFile"
     */
    public void setPropertiesFile(String propertiesFile)
    {
        this.propertiesFile = propertiesFile;
    }

}