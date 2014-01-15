/*
 * Created on 10.09.2008
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.rpc.webserver;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.WebServer;

import de.folt.constants.OpenTMSVersionConstants;
import de.folt.util.OpenTMSInitialJarFileLoader;
import de.folt.util.OpenTMSProperties;
import de.folt.util.OpenTMSSupportFunctions;

public class OpenTMSServer
{

    private static String openTMSdir = "";

    private static WebServer server = null;

    private static String version = de.folt.constants.OpenTMSVersionConstants.getVersionString();

    private static String startLogString = "";

    /**
     * @return the version of the OpenTMS Implementation
     */
    public static String getVersion()
    {
        return de.folt.constants.OpenTMSVersionConstants.getVersionString();
    }

    /**
     * @return the startLogString returns the log starting string
     */
    public static String getStartLogString()
    {
        return startLogString;
    }

    /**
     * main start the OpenTMS XML RPC server. <br>
     * -shutdown shuts down the server<br>
     * -logfile the log file name for the log output<br>
     * -configurationHandler the name of the xml OpenTMS configuration file: Defaults to OpenTMSProperties.getPropfileName()<br>
     * -propertiesFile the associated properties file. Defaults to OpenTMS.properties
     * <p>
     * Example call: call java -Xmx1024m -cp ".;lib/OpenTMS.jar;lib/arayaserver.jar;lib/external.jar;lib/Win32/swt.jar;%DATABASES%" -Djava.library.path=lib\Win32 de.folt.rpc.webserver.OpenTMSServer
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        String logfile = "";
        String configurationHandler = "";
        String propertiesFile = "";
        String startDate = de.folt.util.OpenTMSSupportFunctions.getDateString();
        startLogString = startLogString + "FOLT OpenTMS XML-RPC Server" + "\n" + "FOLT OpenTMS XML-RPC Version: " + version + "\n";
        startLogString = startLogString + "Full Version Info:\n" + de.folt.constants.OpenTMSVersionConstants.getAllVersionsAsString();
        startLogString = startLogString + "Start Date: " + startDate + "\n";

        for (int i = 0; i < args.length; i++)
        {
            startLogString = startLogString + "Argument " + i + ": \"" + args[i] + "\"" + "\n";
        }

        System.out.println(startLogString);

        String webserverClasspath = "opentmsserverclasspath";
        if (new File(webserverClasspath).exists())
            new de.folt.util.JarFileLoader(webserverClasspath, true);

        try
        {
            Hashtable<String, String> arguments = OpenTMSSupportFunctions.argumentReader(args);

            // Invoke me as <http://localhost:4040>.

            if (arguments.containsKey("-shutdown"))
            {
                System.out.println("Shutting down ...");
                de.folt.rpc.webserver.Shutdown.main(args);
                return;
            }
            else
            {
                System.out.println("Starting web server ...");
            }

            if (arguments.containsKey("-logfile"))
            {
                logfile = (String) arguments.get("-logfile");
                System.out.println("Log file = " + logfile);
            }

            if (arguments.containsKey("-configurationHandler"))
            {
                configurationHandler = (String) arguments.get("-configurationHandler");
                System.out.println("ConfigurationHandler = " + configurationHandler);
            }

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

            OpenTMSProperties.getInstance();
            if (OpenTMSProperties.getInstance() == null)
            {
                OpenTMSProperties.getInstance(propertiesFile);
            }
            System.out.println("PropertiesFile now = " + OpenTMSProperties.getPropfileName());
            System.out.flush();
            // set the log file
            if ((logfile == null) || logfile.equals(""))
            {
                logfile = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.rpc.log.file");
                if ((logfile == null) || logfile.equals(""))
                    logfile = "OpenTMSXMLRPCServer.log";
                logfile = logfile + "." + de.folt.util.OpenTMSSupportFunctions.getDateStringFine();
            }

            if ((logfile == null) || logfile.equals(""))
            {
                logfile = "OpenTMS" + de.folt.util.OpenTMSSupportFunctions.getDateStringFine() + ".log";
            }

            System.out.println("OpenTMS Log File: " + logfile);
            System.out.flush();
            
            String serverPort = OpenTMSProperties.getInstance().getOpenTMSProperty("rpc.server.port");

            de.folt.util.OpenTMSLogger.setLogFile(logfile);

            OpenTMSProperties.getInstance(propertiesFile); // init the instance of the property file

            System.out.println(startLogString);

            openTMSdir = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.dir");
            if (openTMSdir == null)
                openTMSdir = "";
            System.out.println("openTMSdir " + openTMSdir);
            String serverService = OpenTMSProperties.getInstance().getOpenTMSProperty("rpc.server.service.name");
            String transToolsService = OpenTMSProperties.getInstance().getOpenTMSProperty("rpc.translation.service.name");
            if ((configurationHandler == null) || configurationHandler.equals(""))
            {
                configurationHandler = OpenTMSProperties.getInstance().getOpenTMSProperty("ConfigurationHandler");
                System.out.println("ConfigurationHandler = " + configurationHandler + " (default from " + propertiesFile + ")");
            }

            int port = 0;
            try
            {
                port = Integer.parseInt(serverPort);
            }
            catch (NumberFormatException nonumber)
            {
                System.err.println("Invalid port number: " + port);
                System.exit(1);
            }
            server = new WebServer(port);
            // Add service classes here
            System.out.println("OpenTMS Service Class 1: " + serverService);
            server.addHandler(serverService, new OpenTMSServer());
            System.out.println("OpenTMS Service Class 2: " + transToolsService);

            de.folt.rpc.services.TranslationToolsServices transService = new de.folt.rpc.services.TranslationToolsServices();
            transService.initTranslationToolsServices(propertiesFile, configurationHandler);
            server.addHandler(transToolsService, transService);

            // set the log file
            if ((logfile == null) || logfile.equals(""))
            {
                logfile = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.rpc.log.file");
                if ((logfile == null) || logfile.equals(""))
                    logfile = "OpenTMSXMLRPCServer.log";
                logfile = logfile + "." + de.folt.util.OpenTMSSupportFunctions.getDateStringFine();
            }

            if ((logfile == null) || logfile.equals(""))
            {
                logfile = "openTMS" + de.folt.util.OpenTMSSupportFunctions.getDateStringFine() + ".log";
            }

            // End
            server.start();

            @SuppressWarnings("rawtypes")
			Vector<Class> classes = OpenTMSVersionConstants.getAllLoadedClasses();
            if (classes != null)
                System.out.println("Known openTMS classes = " + classes.size());
            for (int i = 0; i < classes.size(); i++)
            {
                System.out.println(i + ": " + classes.get(i).getName() + " " + de.folt.util.OpenTMSSupportFunctions.getCompileDate(classes.get(i)) + " "
                        + OpenTMSVersionConstants.getVersionString(classes.get(i)));
            }
            
            System.out.println("Loading hibernate and data source jar file");
            @SuppressWarnings("unused")
            OpenTMSInitialJarFileLoader openTMSInitialJarFileLoader = new OpenTMSInitialJarFileLoader();

            String propstring = OpenTMSProperties.getInstance().getOpenTMSPropertiesAsString();
            System.out.println("OpenTMS Properties " + OpenTMSProperties.getPropfileName() + ":\n" + propstring);

            System.out.println("openTMS Listening on port " + port);

            String serverFile = serverRunningFile();
            File f = new File(serverFile);
            f.createNewFile();
            System.out.println("Server running created:" + serverFile);

            serverPort = null;
            serverService = null;
        }
        catch (Exception exception)
        {
            de.folt.util.OpenTMSLogger.close();
            System.err.println("openTMS server main: " + exception.toString());
            exception.printStackTrace();
            String serverFile = serverRunningFile();
            File f = new File(serverFile);
            f.delete();
            System.out.println("Exception: Server running delete " + serverFile);
        }
    }

    /**
     * Method shutdown shuts down the OpenTMS Server
     * 
     * @return int
     */
    public int shutdown()
    {
        System.out.println("Shuting down FOLT OpenTMS XML-RPC Server");
        System.out.println("Shutdown Date: " + de.folt.util.OpenTMSSupportFunctions.getDateString());
        server.shutdown();
        String serverFile = serverRunningFile();
        File f = new File(serverFile);
        f.delete();
        System.out.println("Server running shutdown delete " + serverFile);
        return de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS;
    }

    /**
     * serverRunningFile return the name of the file which indicates if the server is running. If the file "running" exists the server is active.
     * @return the name of the server running file combined of openTMSdir + "running";
     */
    public static String serverRunningFile()
    {
        String serverFile = "running";
        if (openTMSdir == null)
            return serverFile;
        int iLastPos = openTMSdir.length() - 1;
        if (iLastPos < 0)
            iLastPos = 0;
        if (openTMSdir.equals(""))
            return serverFile;
        if (openTMSdir.charAt(iLastPos) == '/')
        {
            serverFile = openTMSdir + "running";
        }
        else if (openTMSdir.charAt(iLastPos) == '\\')
        {
            serverFile = openTMSdir + "running";
        }
        else
        {
            serverFile = openTMSdir + "/running";
        }
        return serverFile;
    }
}
