/*
 * Created on 05.01.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel;

import java.util.Enumeration;
import java.util.Hashtable;

import de.folt.util.ObservableHashtable;

/**
 * This class implements the methods to access the main DataModelProperties
 * @author klemens
 *
 */
public class DataSourceProperties extends ObservableHashtable<String, Object>
{
    /**
     * 
     */
    private static final long serialVersionUID = -6124133066626992927L;
    
    
    /**
     * copyHashtable copy the contents of a Hashtable to the DataSourceProperties
     * @param message the hashtable to copy
     */
    public void copyHashtable(Hashtable<String, Object> message)
    {
        Enumeration<String> enumhash = message.keys();
        while (enumhash.hasMoreElements())
        {
            String key = enumhash.nextElement();
            Object value = message.get(key);
            this.put(key, value);
        }
    }
    
    /**
     * getDataModelProperty 
     * @param key
     * @return the object associated with the key
     */
    public Object getDataSourceProperty(String key)
    {
        return super.get(key);
    }

    /**
     * setDataModelProperty 
     * @param key
     * @param value
     */
    public void setDataSourceProperty(String key, Object value)
    {
        super.put(key, value);
    }
    
}
