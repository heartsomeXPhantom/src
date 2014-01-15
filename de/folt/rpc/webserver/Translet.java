/*
 * Created on 16.09.2008
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.rpc.webserver;

import java.util.Hashtable;
import java.util.Vector;

/**
 * @author   klemens  To change the template for this generated type comment go to  Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Translet
{
    /**
     */
    private String fullTransletMethod = "";
    /**
     */
    private Hashtable<String, Param> paramTable = null;
    /**
     */
    private String transletClass = "";
    /**
     */
    private String transletMethod = "";
    


    /**
     * @param transletClass the name of the translet
     * @param transletMethod the method of the ranslet
     * @param paramTable the parameter table of the translet
     *
    */

    public Translet(String transletClass, String transletMethod, Hashtable<String, Param> paramTable)
    {
        super();
        this.paramTable = paramTable;
        this.transletClass = transletClass;
        this.transletMethod = transletMethod;
        fullTransletMethod = transletClass + "." + transletMethod;
    }



    /**
     * executeTranslet executes a translet with given parameters (nothing done for the moment)
     * @param parameters the execution parameters
     * @return a vector with the reults
     */
    public Vector<String> executeTranslet(Hashtable<String, Object> parameters)
    {
        Vector<String> vec = new Vector<String>();
        
        return vec;
    }



    /**
     * getFullTransletMethod 
     * @return   the name of the fully qualified method - e.g. de.folt.rpc.messages.TestMessage
     * @uml.property  name="fullTransletMethod"
     */
    public String getFullTransletMethod()
    {
        return fullTransletMethod;
    }



    /**
     * getParamTable returns the parameters - Hashtable - of the Translet class
     * @return   the parameters of this Translet
     * @uml.property  name="paramTable"
     */
    public Hashtable<String, Param> getParamTable()
    {
        return paramTable;
    }



    /**
     * getTransletClass 
     * @return   a TransletClass name as a string
     * @uml.property  name="transletClass"
     */
    public String getTransletClass()
    {
        return transletClass;
    }



    /**
     * getTransletMethod 
     * @return   the metod of this TransletClass
     * @uml.property  name="transletMethod"
     */
    public String getTransletMethod()
    {
        return transletMethod;
    }


 
    /**
     * setFullTransletMethod 
     * @param fullTransletMethod   name of the fully qualified method - e.g. de.folt.rpc.messages.TestMessage
     * @uml.property  name="fullTransletMethod"
     */
    public void setFullTransletMethod(String fullTransletMethod)
    {
        this.fullTransletMethod = fullTransletMethod;
    }



    /**
     * setParamTable 
     * @param  paramTable
     * @uml.property  name="paramTable"
     */
    public void setParamTable(Hashtable<String, Param> paramTable)
    {
        this.paramTable = paramTable;
    }


    /**
     * setTransletClass 
     * @param transletClass   name of tne TransletClass
     * @uml.property  name="transletClass"
     */
    public void setTransletClass(String transletClass)
    {
        this.transletClass = transletClass;
    }    
    
    /**
     * setTransletMethod 
     * @param transletMethod   the method name of this transle class
     * @uml.property  name="transletMethod"
     */
    public void setTransletMethod(String transletMethod)
    {
        this.transletMethod = transletMethod;
    }
}
