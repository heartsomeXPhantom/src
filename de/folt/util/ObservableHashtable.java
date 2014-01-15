/*
 * Created on 14.12.2008
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.util;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * @author klemens
 * 
 */
public class ObservableHashtable<K,V> extends Hashtable<K,V> implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = -472188917524443638L;
    
    boolean changed = false;

    private Observable observable = new Observable();

    private Vector<Observer> observers = new Vector<Observer>();

    /**
     * addObserver
     * 
     * @param o
     */
    public void addObserver(Observer o)
    {
        observers.add(o);
    }
    
    /**
     * clearChanged
     */
    public synchronized void clearChanged()
    {
        this.changed = false;
    }

    /**
     * countObservers
     * 
     * @return
     */
    public int countObservers()
    {
        return observers.size();
    }

    /**
     * deleteObserver
     * 
     * @param o
     */
    public void deleteObserver(Observer o)
    {
        observers.remove(o);
    }

    /**
     * deleteObservers
     */
    public void deleteObservers()
    {
        observers = new Vector<Observer>();
    }

    /**
     * hasChanged
     * 
     * @return
     */
    public synchronized boolean hasChanged()
    {
        return changed;
    }

    /**
     * @return the changed
     */
    public synchronized boolean isChanged()
    {
        return changed;
    }
    
    

    /**
     * notifyObservers
     */
    public void notifyObservers()
    {
        notifyObservers(null);
    }

    /**
     * notifyObservers 
     * @param arg
     */
    public void notifyObservers(Object arg)
    {
        for (int i = 0; i < observers.size(); i++)
        {
            Observer o = observers.get(i);
            o.update(observable, arg);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public synchronized V put(K arg0, V arg1)
    {
        setChanged(true);
        notifyObservers(this);
        clearChanged();
        return super.put(arg0, arg1);
    }

    /* (non-Javadoc)
     * @see java.util.Hashtable#remove(java.lang.Object)
     */
    @Override
    public synchronized V remove(Object key)
    {
        setChanged(true);
        notifyObservers(this);
        clearChanged();
        return super.remove(key);
    }

    /**
     * @param changed
     *            the changed to set
     */
    public synchronized void setChanged(boolean changed)
    {
        this.changed = changed;
    }

}
