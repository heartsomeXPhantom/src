/*
 * Created on 08.02.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.fuzzy;

/**
 * A simple data structure element with a string as key and any object as value.
 * @author klemens
 *
 */
public class FuzzyStringHashtableElement<T> extends FuzzyDataStructureElement<String, T>
{

    /**
     * 
     */
    private static final long serialVersionUID = -140415537712209886L;

    private String string = "";
    
    private T value = null;
    
    /**
     * @return the string
     */
    public String getString()
    {
        return string;
    }

    /**
     * @return the value
     */
    public T getValue()
    {
        return value;
    }

    /**
     * @param string the string to set
     */
    public void setString(String string)
    {
        this.string = string;
    }

    /**
     * @param value the value to set
     */
    public void setValue(T value)
    {
        this.value = value;
    }
    
    
}
