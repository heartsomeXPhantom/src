package de.folt.rpc.messages;

import java.util.Hashtable;
import java.util.Vector;

import de.folt.rpc.services.RPCMessage;


/**
 * @author Klemens Waldhör
 * 
 
 */
public class TestMessage implements RPCMessage
{
    /** Generates a test message.
     * testString - the string which is written to System.out<br>
     * returns either<br>
     * vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS +"");<br>
     * or for an error<br>
     * vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE +"");<br>
     * vec.add(ex.getMessage());   <br>
     * @see de.folt.rpc.services.RPCMessage#execute(java.util.Hashtable)
     */
    @SuppressWarnings("unchecked")
    public Vector execute(Hashtable message)
    {
        Vector vec = new Vector();
        try
        {
            String testMessage = (String) message.get("testString");
            System.out.println("Test message sent:  testString "+ " = \"" + testMessage + "\"");
        }
        catch (Exception ex)
        {
            vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE +"");
            vec.add(ex.getMessage());
            return vec;
        }
        vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS +"");
        return vec;
    }
}