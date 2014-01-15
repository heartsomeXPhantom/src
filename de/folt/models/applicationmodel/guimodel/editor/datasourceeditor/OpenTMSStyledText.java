/*
 * Created on 28.05.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

/**
 * This sub class of StyledText adds some functions to the normal behavior, e.g. a right mouse click popup for copy, cut und paste.
 * @author klemens
 */
public class OpenTMSStyledText extends StyledText
{

    protected static de.folt.util.Messages message;

    protected boolean changed;

    protected long gotFocus = -1l;

    protected long lostFocus = -1l;

    protected boolean bChanged = false;

    /**
     * @return the bChanged
     */
    public boolean isBChanged()
    {
        return bChanged;
    }

    /**
     * @param changed the bChanged to set
     */
    public void setBChanged(boolean changed)
    {
        bChanged = changed;
    }

    protected Observable observable = new Observable();

    protected Vector<Observer> observers = new Vector<Observer>();

    protected String userLanguage = "en";

    /**
     * @param parent
     * @param style
     */
    public OpenTMSStyledText(Composite parent, int style)
    {
        super(parent, style);
        message = de.folt.util.Messages.getInstance("de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", userLanguage);

        super.addMouseListener(new MouseAdapter()
        {
            public void mouseDown(MouseEvent e)
            {
                switch (e.button)
                {
                    case 1:
                    { // left

                        break;
                    }
                    case 3:
                    { // right
                        Widget w = e.widget;
                        handleRightMouseClick(w, e);
                        break;
                    }
                }
            }
        });

        super.addExtendedModifyListener(new ExtendedModifyListener()
        {
            public void modifyText(ExtendedModifyEvent event)
            {
                bChanged = true;
            }
        });

        FocusListener listener = new FocusListener()
        {
            public void focusGained(FocusEvent event)
            {
                gotFocus = System.currentTimeMillis();
            }

            public void focusLost(FocusEvent event)
            {
                lostFocus = System.currentTimeMillis();
            }
        };

        this.addFocusListener(listener);

    }

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
     * @return the gotFocus
     */
    public long getGotFocus()
    {
        return gotFocus;
    }

    /**
     * @return the lostFocus
     */
    public long getLostFocus()
    {
        return lostFocus;
    }

    /**
     * handleRightMouseClick 
     * @param w 
     * @param eMouse 
     * @return 
     */
    protected Menu handleRightMouseClick(Widget w, MouseEvent eMouse)
    {
        Menu popupmenu = new Menu(this.getShell(), SWT.POP_UP);
        this.setMenu(popupmenu);
        MenuItem copyItem = new MenuItem(popupmenu, SWT.PUSH);
        copyItem.setText(message.getString("Copy.Popup"));
        copyItem.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event e)
            {
                copy();
            }
        });

        MenuItem pasteItem = new MenuItem(popupmenu, SWT.PUSH);
        pasteItem.setText(message.getString("Paste.Popup"));
        pasteItem.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event e)
            {
                paste();
            }
        });

        MenuItem cutItem = new MenuItem(popupmenu, SWT.PUSH);
        cutItem.setText(message.getString("Cut.Popup"));
        cutItem.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event e)
            {
                cut();
            }
        });

        return popupmenu;
    }

    /**
     * @return the changed
     */
    public boolean isChanged()
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

    /**
     * setChanged 
     * @param b
     */
    public void setChanged(boolean b)
    {
        changed = b;
    }

    /**
     * @param gotFocus the gotFocus to set
     */
    public void setGotFocus(long gotFocus)
    {
        this.gotFocus = gotFocus;
    }

    /**
     * @param lostFocus the lostFocus to set
     */
    public void setLostFocus(long lostFocus)
    {
        this.lostFocus = lostFocus;
    }
}
