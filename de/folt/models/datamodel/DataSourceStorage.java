/*
 * Created on 12.12.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel;

/**
 * Class implements an association between a data source and its usage (how often createInstance is called)
 * @author klemens
 *
 */
public class DataSourceStorage
{
    private int counter = 0;

    private DataSource dataSource;
    
    private boolean removeIfCounterZero = true;
    
    /**
     * @param dataSource - the data source to add
     */
    public DataSourceStorage(DataSource dataSource)
    {
        this.dataSource = dataSource;
        counter = 1;
    }

    /**
     * decrementCounter decrement the counter associated with the data source
     * @return decremented counter value
     */
    public int decrementCounter()
    {
        counter--;
        return counter;
    }

    /**
     * @return the counter the counter value of the data source
     */
    public int getCounter()
    {
        return counter;
    }

    /**
     * @return the dataSource get the data source
     */
    public DataSource getDataSource()
    {
        return dataSource;
    }

    /**
     * incrementCounter increment the counter associated with the data source
     * @return incremented counter value
     */
    public int incrementCounter()
    {
        counter++;
        return counter;
    }

    /**
     * @return the removeIfCounterZero this boolean indicates for the removeInstance if the data source should be removed (that it the data source method remove is called)
     */
    public boolean isRemoveIfCounterZero()
    {
        return removeIfCounterZero;
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    /**
     * @param removeIfCounterZero the removeIfCounterZero to set (should the data source remove method be called when counter decrements to 0)
     */
    public void setRemoveIfCounterZero(boolean removeIfCounterZero)
    {
        this.removeIfCounterZero = removeIfCounterZero;
    }
}
