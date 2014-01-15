package de.folt.rpc.messages;

import java.util.Hashtable;
import java.util.Vector;

import com.araya.eaglememex.util.EMXProperties;
import com.araya.tmx.Interface;

import de.folt.rpc.services.RPCMessage;
import de.folt.util.OpenTMSProperties;


/**
 * @author Klemens Waldhör
 * 
 *         To change this generated comment edit the template variable "typecomment": Window>Preferences>Java>Templates. To enable and disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class SupportMethodOpenTMS implements RPCMessage
{
    /** SupportMethodOpenTMS returns some support information. Parameters are provided through a hash table. The hashtable contains the following keys:<p>
     * method  - the method to be called - either OpenTMSLanguages / OpenTMSShortLanguages / OpenTMSCharacterEncodings<br>
     * It returns:<p>
     * Vector retVec = com.araya.OpenTMS.Interface.supportMethods(message);<p>
     * The result is vector of length number of information + 1 where the 0th element is 0 for success<br>
     * OpenTMSLanguages - the supported OpenTMS languages, e.g. "de"<br>
     * OpenTMSLanguages - the supported OpenTMS long version of the languages, e.g. "de German(Standard)"<br>
     * OpenTMSCharacterEncodings - the supported OpenTMS character encodings<br>
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
            String method = (String) message.get("method");         // folttm
           
            // ok - call now a function which creates the database
            @SuppressWarnings("unused")
            Interface inter = new Interface();

            System.out.println("method=    \"" + method + "\"");
            
            String propFile = OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaPropertiesFile");
            EMXProperties.getInstance(propFile);
            System.out.println("ArayaPropertiesFile=\"" + propFile + "\"");
            Vector retVec = com.araya.OpenTMS.Interface.supportMethods(message);

            vec = retVec;
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