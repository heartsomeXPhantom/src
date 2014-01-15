package de.folt.rpc.messages;

import java.util.Hashtable;
import java.util.Vector;

import de.folt.rpc.services.RPCMessage;
import de.folt.util.OpenTMSProperties;


/**
 * @author Klemens Waldhör
 * 
 */
public class ExportOpenTMSDataSource implements RPCMessage
{
    /** ExportOpenTMSDataSource exports an OpenTMS Datasource. Parameters are provided through a hash table. The hashtable contains the following keys:<p>
     * dataSourceName - the name of the data source<br>
     * dataSourceType - the type of the data source; any defined database, e.g. MySQl<br>
     * dataSourceServer - the name of the server, e.g. localhost or IP address<br>
     * dataSourcePort - the port of the data source, e.g. 1433<br>
     * dataSourceUser - the user of the data source, e.g. sa<br>
     * dataSourcePassword - the name of the data source, e.g. folt<br>
     * dataModel  - either TMX or TBX depending on the data source<br>
     * exportFile - the file name where the export is written to<p>
     * It returns:<p>
     * vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS +"");<br>
     * vec.add(dataSourceName + " successfully exported!");<p>
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
            String exportFile =  (String) message.get("exportFile");
            
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
            System.out.println("exportFile=         \"" + exportFile + "\"");
            
            String propFile = OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaPropertiesFile");
            message.put("ArayaPropertiesFile", propFile);
            System.out.println("ArayaPropertiesFile=\"" + propFile + "\"");
            Vector retVec = com.araya.OpenTMS.Interface.runExportDB(message); 
            System.out.println("ExportOpenTMSDataSource " + dataSourceName + " finished!");
            vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS +"");
            if (retVec.size() > 1)
                vec.add(retVec.get(1));
            vec.add(dataSourceName + " successfully exported!");
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