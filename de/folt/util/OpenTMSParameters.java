/*
 * Created on 13.12.2008
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.util;

import java.util.Hashtable;

/**
 * @author klemens
 *
 * This is intended as an extension to a hashtable with maybe in the future some specific OpenTMS features
 */
public interface OpenTMSParameters
{
    /**
     * clearParameters - sets all parameters to their default values
     * @return the OpenTMSParameters instance
     */
    public OpenTMSParameters clearParameters();
    
    /**
     * copyParameters creates a copy of the this OpenTMSParameters instance
     * @return the OpenTMSParameters instance
     */
    public OpenTMSParameters copyParameters();
    
    /**
     * setParameters - sets the parameters of the instance based on parameters
     * @param parameters - a hashtable containing the parameters
     * @return the OpenTMSParameters instance
     */
    public OpenTMSParameters modifyParameters(@SuppressWarnings("rawtypes") Hashtable parameters);
    
    /**
     * removeParameter - removes a specific parameter
     * @param key - the key of the parameter to remove
     * @return the OpenTMSParameters instance
     */
    public OpenTMSParameters removeParameter(String key);
    
    /**
     * setParameter - sets one parameter
     * @param key - the key of the parameter
     * @param value - the value of the parameter
     * @return the OpenTMSParameters instance
     */
    public OpenTMSParameters setParameter(String key, Object value);
    
    /**
     * setParameters 
     * @param parameters
     * @return the OpenTMSParameters instance
     */
    public OpenTMSParameters setParameters(@SuppressWarnings("rawtypes") Hashtable parameters);
}
