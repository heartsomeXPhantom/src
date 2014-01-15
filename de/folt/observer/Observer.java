/*
 * Created on 20.01.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.observer;

/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class Observer
{

    public enum ObserverType {
        ADD, CHANGE, CREATE, LISTEN, REMOVE;
    }

    private ObserverType observerType; 

    /**
     * execute
     * 
     * @param object
     *            a variable number of objects can be supplied
     */
    abstract public void execute(Object... object);

    /**
     * @return the observerType
     */
    public ObserverType getObserverType()
    {
        return observerType;
    }

    /**
     * @param observerType
     *            the observerType to set
     */
    public void setObserverType(ObserverType observerType)
    {
        this.observerType = observerType;
    }

}
