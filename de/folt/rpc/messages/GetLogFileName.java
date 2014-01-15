package de.folt.rpc.messages;

import java.util.Hashtable;
import java.util.Vector;

import de.folt.rpc.services.RPCMessage;


/**
 * @author unnop siripakdee
 * 
 */
public class GetLogFileName implements RPCMessage
{
    /** GetLogFileName returns the currently used log file name of the OpenTMS server. No parameters are used:<p>

     * It returns:<p>
     * vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS +"");<br>
     * vec.add(logfile); -- The log file name -- <br>
     * vec.add(de.folt.util.OpenTMSLogger.returnLogLevel() + ""); -- the log level if supported --<p> 
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
            vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS +"");
            String logfile = de.folt.util.OpenTMSLogger.returnLogFileName();
            vec.add(logfile);
            vec.add(de.folt.util.OpenTMSLogger.returnLogLevel() + "");
            System.out.println("GetLogFileName: "+ " = \"" + logfile + "\"");
        }
        catch (Exception ex)
        {
            vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE +"");
            vec.add(ex.getMessage());
            return vec;
        }

        return vec;
    }
}