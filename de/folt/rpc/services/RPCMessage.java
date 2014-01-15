package de.folt.rpc.services;

import java.util.Hashtable;
import java.util.Vector;

/**
 * @author Klemens Waldhör
 * 
 */
public interface RPCMessage
{
    /**
     * execute is an Interface which must be implemented for rpc services
     * @param message a hash table containing the message specific parameters
     * @return a Vector containing the results of executing the method.
     */
    public Vector<String> execute(Hashtable<String, String> message);
}