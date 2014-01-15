package de.folt.rpc.messages;

import java.util.Hashtable;
import java.util.Vector;

import de.folt.rpc.services.RPCMessage;
import de.folt.util.OpenTMSProperties;


/**
 * @author Klemens Waldhör
 * 
 */
public class CreateOpenTMSDataSource implements RPCMessage
{
    /** CreateOpenTMSDataSource creates an OpenTMS Datasource. Parameters are provided through a hash table. The hashtable contains the following keys:<p>
     * dataSourceName - the name of the data source<br>
     * dataSourceType - the type of the data source; any defined database, e.g. MySQl<br>
     * dataSourceServer - the name of the server, e.g. localhost or IP address<br>
     * dataSourcePort - the port of the data source, e.g. 1433<br>
     * dataSourceUser - the user of the data source, e.g. sa<br>
     * dataSourcePassword - the name of the data source, e.g. folt<br>
     * dataModel  - either TMX or TBX depending on the data source<p>
     * It returns:<p>
     * vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS +"");<br>
     * vec.add(dataSourceName + " successfully created!");<p>
     * or<p>
     *  vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE +"");<br>
     *  vec.add(ex.getMessage());<br>
     * @see de.folt.rpc.services.RPCMessage#execute(java.util.Hashtable)
     */
    @SuppressWarnings("unchecked")
    public Vector execute(Hashtable message)
    {
        Vector vec = new Vector();
        try
        {
            String dataSourceName = (String) message.get("dataSourceName");         // folttm
            String dataSourceType = (String) message.get("dataSourceType");         // MySQL
            String dataSourceServer = (String) message.get("dataSourceServer");     // localhost
            String dataSourcePort = (String) message.get("dataSourcePort");         // 2341
            String dataSourceUser = (String) message.get("dataSourceUser");         // sa
            @SuppressWarnings("unused")
            String dataSourcePassword = (String) message.get("dataSourcePassword"); // my password
            
            String dataModel =  (String) message.get("dataModel");
            if ((dataModel == null) || dataModel.equalsIgnoreCase(""))
                dataModel = "TMX";
            else if (dataModel.equalsIgnoreCase("TMX"))
                ;
            else if (dataModel.equalsIgnoreCase("TBX"))
                ;
            else
                dataModel = "TMX"; 
            message.put("dataModel", dataModel);
            // ok - call now a function which creates the database


            System.out.println("dataSourceName=     \"" + dataSourceName + "\"");
            System.out.println("dataSourceUser=     \"" + dataSourceUser + "\"");
            // LogPrint.println("dataSourcePassword= \"" + dataSourcePassword + "\"");
            System.out.println("dataSourceType=     \"" + dataSourceType + "\"");
            System.out.println("dataSourceServer=   \"" + dataSourceServer + "\"");
            System.out.println("dataSourcePort=     \"" + dataSourcePort + "\"");
            System.out.println("dataModel=          \"" + dataModel + "\"");
            
            String propFile = OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaPropertiesFile");
            message.put("ArayaPropertiesFile", propFile);
            System.out.println("ArayaPropertiesFile=\"" + propFile + "\"");
            @SuppressWarnings("unused")
            Vector retVec = com.araya.OpenTMS.Interface.runCreateDB(message); 
            System.out.println("CreateOpenTMSDataSource " + dataSourceName + " finished!");
            vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS +"");
            vec.add(dataSourceName + " successfully created!");
            return vec;
        }
        catch (Exception ex)
        {
            vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE +"");
            vec.add(ex.getMessage());
            ex.printStackTrace();
            return vec;
        }
    }
}