/*
 * Created on 27.11.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel;

/**
 * This class extends the BasicDataSource (and its implementation of the DataSource interface) and is intended to be sub classed for specific data sources. It adds a method <b>load()</b> for loading reading the MultiLingualObjects into the basic data source. Thus for any subclass it is sufficient to implement load(). In addition to save changes <b>bPersist()</b> should be implemented.
 * @author klemens
 */
public class ExtendedBasicDataSource extends BasicDataSource
{

    /**
     * 
     */
    public ExtendedBasicDataSource()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param propertiesFileName
     */
    public ExtendedBasicDataSource(String propertiesFileName)
    {
        super(propertiesFileName);
        // TODO Auto-generated constructor stub
    }

    /**
     * main 
     * @param args
     */
    public static void main(String[] args)
    {

    }

    /**
     * Creates an extended basic data source based on the supplied dataSourceProperties. The properties file is determined from the key value pair propertiesFile of dataSourceProperties.
     * @param dataSourceProperties the dataSourceProperties to use for the construction
     */
    public ExtendedBasicDataSource(DataSourceProperties dataSourceProperties)
    {
        super(dataSourceProperties);
        load();
    }

    /**
     * load this method is intended to load MultiLingualObjects into the BasicDataSource (see example in TmxDataSource or similar). Get the dataSourceProperties by using getDataSourceProperties() if any of the parameters in there are needed. 
     */
    protected void load()
    {
        
    }

}
