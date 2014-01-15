package de.folt.rpc.messages;

import java.util.Hashtable;
import java.util.Vector;

import de.folt.rpc.services.RPCMessage;
import de.folt.util.OpenTMSProperties;


/**
 * @author Klemens Waldhör
 * 
 */
public class ReturnOpenTMSDataSources implements RPCMessage
{
    /** ReturnOpenTMSDataSources returns a list of OpenTMS data sources. Parameters are provided through a hash table. The hashtable contains the following keys:<p>
     * dataModel  - either TMX or TBX or ALL depending on the data sources to be returned<br>
     * It returns:<p>
     * com.araya.OpenTMS.Interface.runReturnDBs(message);<p>
     * The result is vector of length number of data sources + 1<br>
     * the description of single data sources looks like that:<br>
     * String value = "TBX|" + name + "|" + user + "|" + password + "|" + type + "|" + server + "|" + port; or <br>
     * String value = "TMX|" + name + "|" + user + "|" + password + "|" + type + "|" + server + "|" + port;<br>
     * or<p>
     *  vec.add("1");<br>
     *  vec.add(ex.getMessage());<br>
     * @see de.folt.rpc.services.RPCMessage#execute(java.util.Hashtable)
     */
    @SuppressWarnings("unchecked")
    public Vector execute(Hashtable message)
    {
        Vector vec = new Vector();
        try
        {
            String dataModel =  (String) message.get("dataModel");
            if ((dataModel == null) || dataModel.equalsIgnoreCase(""))
                dataModel = "TMX";
            else if (dataModel.equalsIgnoreCase("TMX"))
                ;
            else if (dataModel.equalsIgnoreCase("TBX"))
                ;
            else if (dataModel.equalsIgnoreCase("ALL"))
                ;
            else
                dataModel = "ALL"; 
            message.put("dataModel", dataModel);
            // ok - call now a function which creates the database

            System.out.println("dataModel=          \"" + dataModel + "\"");
            
            String propFile = OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaPropertiesFile");
            message.put("ArayaPropertiesFile", propFile);
            
            Vector retVec = com.araya.OpenTMS.Interface.runReturnDBs(message);
            vec = retVec;
            if (vec == null)
            {
                vec = new Vector();
                vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE +"");
                vec.add("return Vector null");
            }
            System.out.println("return vector size " + vec.size());
            System.out.println("returnOpenTMSDataSources for " + dataModel + " finished!");
            
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