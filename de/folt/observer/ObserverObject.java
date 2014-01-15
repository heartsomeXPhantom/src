/*
 * Created on 20.01.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.observer;

import java.io.Serializable;
import java.util.Vector;

/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ObserverObject implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 3058668447429405534L;

    private Vector<de.folt.observer.Observer> observerList = null;

    /**
     * addObserver
     * 
     * @param observer
     *            the observer to add to the GeneralLinguisticObject
     */
    public void addObserver(de.folt.observer.Observer observer)
    {
        if (this.observerList == null)
            this.observerList = new Vector<Observer>();
        this.observerList.add(observer);
    }

    /**
     * executeAllObservers executes all observer associated this GeneneralLinguisticObejct
     */
    protected void executeAllObservers()
    {
        for (int i = 0; i < this.observerList.size(); i++)
        {
            Observer observer = this.observerList.get(i);
            if (observer != null)
                executeObserver(observer);
        }
    }

    /**
     * executeAllObservers executes an observer of a specific ObserverType
     * @param observerType the type of Observer to execute
     */
    protected void executeAllObservers(Observer.ObserverType observerType)
    {
        for (int i = 0; i < this.observerList.size(); i++)
        {
            Observer observer = this.observerList.get(i);
            if ((observer != null) && (observerType == observer.getObserverType()))
                executeObserver(observer);
        }
    }

    /**
     * executeAllObservers 
     * @param observerType the ObserverType to execute
     * @param objects a variable number of arguments for the observer
     */
    protected void executeAllObservers(Observer.ObserverType observerType, Object...objects)
    {
        for (int i = 0; i < this.observerList.size(); i++)
        {
            Observer observer = this.observerList.get(i);
            if ((observer != null) && (observerType == observer.getObserverType()))
                executeObserver(observer, objects);
        }
    }

    /**
     * executeObserver runs the execution method of a observer
     * 
     * @param observer
     */
    protected void executeObserver(de.folt.observer.Observer observer)
    {
        observer.execute(this);
    }
    
    /**
     * executeObserver 
     * @param observer
     * @param objects
     */
    protected void executeObserver(de.folt.observer.Observer observer, Object... objects)
    {
        observer.execute(this, objects);
    }

    /**
     * @return the observerList
     */
    public Vector<de.folt.observer.Observer> getObserverList()
    {
        return observerList;
    }

    /**
     * removeAllObservers removes all Observers for the object
     */
    public void removeAllObservers()
    {
        if (this.observerList == null)
            return;
        ;
        this.observerList = null;
    }
    
    /**
     * removeObserver
     * 
     * @param observer
     *            the observer to remove
     */
    public void removeObserver(de.folt.observer.Observer observer)
    {
        if (this.observerList == null)
            return;
        ;
        this.observerList.remove(observer);
    }

}
