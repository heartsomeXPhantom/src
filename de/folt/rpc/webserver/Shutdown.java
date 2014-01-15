/*
 * Created on Jul 22, 2003
 *
 */
package de.folt.rpc.webserver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import de.folt.util.OpenTMSProperties;
import de.folt.util.OpenTMSSupportFunctions;

/**
 * @author klemens
 * <br>
 * Simple class with a main method to shutdown the OpenTMS XML RPC Server
 */
public class Shutdown
{

    private static String openTMSdir = "";

    /**
     * main - shuts down the OpenTMS XML RPC Server; properties are read from the default property file or the supplied property file
     * @param args -propertiesFile associated property file
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args)
    {
        try
        {
            Hashtable< String , String > arguments = OpenTMSSupportFunctions.argumentReader(args);
            
            String propertiesFile = "";
            if (arguments.containsKey("-propertiesFile"))
            {
                propertiesFile = (String) arguments.get("-propertiesFile");
                System.out.println("PropertiesFile = " + propertiesFile);
                File f = new File(propertiesFile);
                if (!f.exists())
                {
                    System.out.println("PropertiesFile = " + propertiesFile + " not found.");
                    propertiesFile = OpenTMSProperties.getPropfileName();
                    System.out.println("PropertiesFile = " + propertiesFile + " (default used)");
                }
            }
            else
            {
                propertiesFile = OpenTMSProperties.getPropfileName();
                System.out.println("PropertiesFile = " + propertiesFile + " (default)");
            }
            
            String serverPort = "";
            String connectString = OpenTMSProperties.getInstance(propertiesFile).getOpenTMSProperty("rpc.server.connectstring");
            if (connectString == null)
            {
                serverPort = OpenTMSProperties.getInstance(propertiesFile).getOpenTMSProperty("rpc.server.port");
                if (serverPort != null)
                {
                    serverPort = "http://localhost:" + serverPort;
                }
                else
                {
                    serverPort = "http://localhost:4050";
                }
            }
            else
                serverPort = connectString;
            XmlRpcClient client = new XmlRpcClient(serverPort);
            Vector vector = new Vector();
            client.execute("shutdown", vector);
            System.out.println("Server shutdown.");
            de.folt.util.OpenTMSLogger.close();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            de.folt.util.OpenTMSLogger.close();
        }
        catch (XmlRpcException e)
        {
            e.printStackTrace();
            de.folt.util.OpenTMSLogger.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            de.folt.util.OpenTMSLogger.close();
        }
        
        String serverFile = openTMSdir + "running";
        File f = new File(serverFile);
        f.delete();
    }
}