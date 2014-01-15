/*
 * Created on 18.02.2006
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.rpc.client;

import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;

import de.folt.util.OpenTMSProperties;

/**
 * @author klemens.waldhoer
 *  <br>
 *   A java client which allows to access the OpenTMS XML RPC Server
 */
public class OpenTMSClient
{
    private static XmlRpcClient client = null;

    private static String method = "TranslationTools.run";

    /**
     * Execute executes a defined method and returns a vector containing the execution results. The method is called "TranslationTools.run" and currently hard coded.<br>
     * The argument vector contains the parameters to be executed:<p>
     * Example:<br>
     * Vector vector = new Vector();<br>
     * Hashtable message = new Hashtable();<br>
     * message.put("testString", "OpenTMS Test Message");<br>
     * message.put("message", "TestMessage");<br>
     * vector.add(message);<br>
     * Vector result = execute(vector);
     * 
     * @param  vector (argument) of type Vector  contains the values for the execute method of the XmlRpcClient. For an example see testmessage.
     * @return The result is of type Vector and contains the results from calling the messages defined in the vector argument.
     */
    @SuppressWarnings("unchecked")
    public static Vector execute(Vector vector)
    {
        try
        {
            if (client == null)
                init();
            Vector result = null;
            Hashtable hash = (Hashtable) vector.get(0);
            Enumeration enumhash = hash.keys();
            while (enumhash.hasMoreElements())
            {
                String key = (String) enumhash.nextElement();
                String value = (String) hash.get(key);
                System.out.println(key + " = \"" + value + "\"");
            }
            
            System.out.println("Execute: method=" + method);
            
            result = (Vector) client.execute(method, vector);

            if (result == null)
            {
                System.out.println("Result = null");
            }
            else if (result.size() > 0)
            {
                System.out.println("Execute: method=" + method + " result=" + result.get(0));
                for (int i = 0; i < result.size(); i++)
                {
                    System.out.println("[" + i + ":] " + result.get(i));
                }
            }
            else
                System.out.println("Result size 0");
            return result;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.println("Error sending message");
            Vector result = new Vector();
            result.add("-1");
            result.add(ex.getMessage());
            return result;
        }
    }

    /**
     * init Method initialises the XmlRpcClient. It reads the connection string from the OpenTMSProperties property file rpc.server.connectstring.
     * The port is defined in the property rpc.server.port. Default connection string is "http://localhost:4050".
     * The final connection string is built from "rpc.server.connectstring" and "rpc.server.port"
     * @return an XmlRpcClient
     */
    public static XmlRpcClient init()
    {
        try
        {
            String serverPort = "";
            String connectString = OpenTMSProperties.getInstance().getOpenTMSProperty("rpc.server.connectstring");
            if (connectString == null)
            {
                serverPort = OpenTMSProperties.getInstance().getOpenTMSProperty("rpc.server.port");
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
            System.out.println("XmlRpcClient: " + connectString);
            client = new XmlRpcClient(serverPort);
            return client;
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * main executes an OpenTMS message.<br>
     * Here is an example call:<br>
     * call java -Xmx1024m -cp .;lib/OpenTMS.jar;lib/arayaserver.jar;lib/external.jar -Djava.library.path=lib\Win32 de.folt.rpc.client.OpenTMSClient "message=TestMessage" "testString=My personal OpenTMS Test Message"<p>
     * TestMessage represents a OpenTMS rpc message (de.folt.rpc.messages.TestMessage is executed)
     * 
     * @param args a list of strings; structured as above.<br>
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            @SuppressWarnings("unused")
            Vector result = testMessage();
            System.exit(0);
        }

        int iArgsLen = args.length;

        // the key argument is always the message argument!
        // if it does not appear quit

        // argument structure
        // key=value / example document=mydocument.doc

        Vector vector = new Vector();
        Hashtable message = new Hashtable();

        boolean bContainsMessage = false;
        for (int i = 0; i < iArgsLen; i++)
        {
            String arg = args[i];
            String parameter[] = arg.split("=");
            if (parameter.length == 2)
            {
                message.put(parameter[0], parameter[1]);
                if (parameter[0].equals("message"))
                    bContainsMessage = true;
            }
            else
            {
                System.out.println("Error: Invalid Argument: " + arg);
            }
        }

        vector.add(message);
        if (bContainsMessage)
            execute(vector);
        else
        {
            System.out.println("Error: No message parameter found ");
            System.exit(1);
        }

        System.exit(0);
    }

    /**
     * testMessage just sends a test message to the server.
     * 
     * @return is a vector with element 0: success de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS or de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE i  case of a failure.
     */
    @SuppressWarnings("unchecked")
    public static Vector testMessage()
    {
        Vector vector = new Vector();
        Hashtable message = new Hashtable();
        // this is forwarded to de.folt.rpc.messages.TestMessage which gets the value of "testString" from the hashtable
        // and prints it to System.out
        message.put("testString", "OpenTMS Test Message");
        // this will call the method de.folt.rpc.messages.TestMessage
        // actually the run method of TestMessage which takes a hash table as its parameters
        message.put("message", "TestMessage");

        vector.add(message);
        Vector result = execute(vector);

        return result;
    }
}