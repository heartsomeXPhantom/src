/*
 * Created on 16.09.2008
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.rpc.webserver;

/**
 * @author   klemens  To change the template for this generated type comment go to  Window - Preferences - Java - Code Generation - Code and Comments
 */
class Param
{
    /**
     */
    private String mapTo = "";

    /**
     */
    private String name = "";

    /**
     */
    private String type = "";

    /**
     */
    private String value = "";


    /**
     * @param name of property to map
     * @param mapTo name of target property
     * @param type the type of the property
     * @param value  the value of the property
     */
    public Param(String name, String mapTo, String type, String value)
    {
        super();
        this.mapTo = mapTo;
        this.name = name;
        this.type = type;
        this.value = value;
    }


    /**
     * getMapTo 
     * @return
     * @uml.property  name="mapTo"
     */
    public String getMapTo()
    {
        return mapTo;
    }


    /**
     * getName 
     * @return
     * @uml.property  name="name"
     */
    public String getName()
    {
        return name;
    }


    /**
     * getType 
     * @return
     * @uml.property  name="type"
     */
    public String getType()
    {
        return type;
    }


    /**
     * getValue 
     * @return
     * @uml.property  name="value"
     */
    public String getValue()
    {
        return value;
    }


    /**
     * setMapTo 
     * @param  mapTo
     * @uml.property  name="mapTo"
     */
    public void setMapTo(String mapTo)
    {
        this.mapTo = mapTo;
    }


    /**
     * setName 
     * @param  name
     * @uml.property  name="name"
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * setType 
     * @param  type
     * @uml.property  name="type"
     */
    public void setType(String type)
    {
        this.type = type;
    }



    /**
     * setValue 
     * @param  value
     * @uml.property  name="value"
     */
    public void setValue(String value)
    {
        this.value = value;
    }

}