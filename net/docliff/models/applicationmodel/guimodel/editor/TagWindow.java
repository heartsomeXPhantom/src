/*
 * Created on 30.10.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.docliff.models.applicationmodel.guimodel.editor;

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import de.folt.util.Messages;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TagWindow extends Dialog
{

    public static void main(String[] args)
    {

        Display mydisplay = new Display();
        Shell shell = new Shell(mydisplay, SWT.SHELL_TRIM);

        shell.addListener(SWT.Close, new Listener()
        {
            public void handleEvent(Event event)
            {
                event.widget.dispose();
            }
        });

        shell.addListener(SWT.Resize, new Listener()
        {
            public void handleEvent(Event event)
            {
            }
        });

        shell.setText("Test Tag Window");

        shell.setSize(1200, 900);

        TagWindow tagWindow = new TagWindow(
                shell,
                SWT.NONE,
                10,
                "Um <bla>cc</bla> Bedienunng dem <ph id=\"0\">&lt;/text:span&gt;</ph><ph id=\"1\">&lt;text:alphabetical-index-mark text:string-value=\"Bedienungspersonal\"/&gt;</ph><ph id=\"2\">&lt;text:span text:style-name=\"T3\"&gt;</ph>Bedienungspersonal stets zugänglich sein.",
                "test");
        tagWindow.show();

        shell.pack();
        shell.open();
        while (!shell.isDisposed())
        {
            if (!mydisplay.isDisposed())
            {
                if (!mydisplay.readAndDispatch())
                {
                    mydisplay.sleep();
                }
            }
        }

        if (!mydisplay.isDisposed())
        {
            mydisplay.dispose();
        }

    }

    private boolean bIsDisposed = false;

    private Messages message;

    private Observable observable = new Observable();

    private Vector<Observer> observers = new Vector<Observer>();

    private Shell shell;
    
    private List sourceSegmentTags;

    private String[] sourceTags;

    private List targetSegmentTags;

    private String[] targetTags;

    private String userLanguage = "en";

    public TagWindow(Shell parentShell, int style, int iSegmentNumber, String sourceSegment, String targetSegment)
    {
        super(parentShell, style);
        message = de.folt.util.Messages.getInstance("net.docliff.models.applicationmodel.guimodel.editor.XliffEditor", userLanguage);
        shell = new Shell(parentShell.getDisplay(), SWT.DIALOG_TRIM);
        shell.setLayout(new GridLayout(1, true));
        // shell.setSize(300, 200);
        shell.setText(message.getString("TagWindow") + " " + iSegmentNumber);
        int iGridData = GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL;
        GridData gridentry = new GridData(iGridData);
        sourceSegmentTags = new org.eclipse.swt.widgets.List(shell, SWT.SINGLE | SWT.V_SCROLL | SWT.TOP | SWT.BORDER);
        sourceSegmentTags.setLayoutData(gridentry);
        targetSegmentTags = new org.eclipse.swt.widgets.List(shell, SWT.SINGLE | SWT.V_SCROLL | SWT.TOP | SWT.BORDER);
        targetSegmentTags.setLayoutData(gridentry);

        sourceTags = parseTags(sourceSegment);
        targetTags = parseTags(targetSegment);
        if (sourceTags != null)
            sourceSegmentTags.setItems(sourceTags);
        if (targetTags != null)
            targetSegmentTags.setItems(targetTags);
        sourceSegmentTags.pack();
        targetSegmentTags.pack();
        shell.setSize(400, 600);

        shell.addListener(SWT.Dispose, new Listener()
        {
            public void handleEvent(Event event)
            {
                bIsDisposed = true;
            }
        });

        targetSegmentTags.addMouseListener(new MouseListener()
        {
            public void mouseDoubleClick(MouseEvent e)
            {
                // ok, Insert at given position in target...
                String tag = targetSegmentTags.getItem(targetSegmentTags.getSelectionIndex());
                notifyObservers(tag);
            }

            public void mouseDown(MouseEvent e)
            {
            }

            public void mouseUp(MouseEvent e)
            {
            }
        });

        sourceSegmentTags.addMouseListener(new MouseListener()
        {
            public void mouseDoubleClick(MouseEvent e)
            {
                // ok, Insert at given position in target...
                String tag = sourceSegmentTags.getItem(sourceSegmentTags.getSelectionIndex());
                notifyObservers(tag);
            }

            public void mouseDown(MouseEvent e)
            {
            }

            public void mouseUp(MouseEvent e)
            {
            }
        });

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
     * @return the observable
     */
    public Observable getObservable()
    {
        return observable;
    }

    /**
     * @return the observers
     */
    public Vector<Observer> getObservers()
    {
        return observers;
    }

    public void hide()
    {
        shell.setVisible(false);
    }

    /**
     * @return the bIsDisposed
     */
    public boolean isBIsDisposed()
    {
        return bIsDisposed;
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
     * parseTags 
     * @param segment
     * @return
     */
    private String[] parseTags(String segment)
    {
        String pattern = ".*?<(.*?)( ?)(.*?>)(.*?</\\1>).*?";
        Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher m = p.matcher(segment);
        Vector<String> matches = new Vector<String>();
        while (m.matches())
        {
            String tag = "<";
            for (int i = 1; i <= m.groupCount(); i++)
            {
                tag = tag + m.group(i);
            }
            matches.add(tag);
            int iPos = segment.indexOf(tag);
            if (iPos == -1)
                break;
            segment = segment.substring(iPos + tag.length());
            if (segment.equals(""))
                break;
            m = p.matcher(segment);
        }
        if (matches.size() == 0)
            return null;
        String[] stringMatches = new String[matches.size()];
        for (int i = 0; i < matches.size(); i++)
        {
            stringMatches[i] = matches.get(i);
        }

        return stringMatches;
    }

    /**
     * @param bIsDisposed the bIsDisposed to set
     */
    public void setBIsDisposed(boolean bIsDisposed)
    {
        this.bIsDisposed = bIsDisposed;
    }

    /**
     * @param observable the observable to set
     */
    public void setObservable(Observable observable)
    {
        this.observable = observable;
    }

    /**
     * @param observers the observers to set
     */
    public void setObservers(Vector<Observer> observers)
    {
        this.observers = observers;
    }

    public void show()
    {
        // shell.open();
        if ((sourceTags == null) && (targetTags == null))
        {
            hide();
            return;
        }
        shell.setVisible(true);
    }

    /**
     * update 
     * @param iSegmentNumber
     * @param fullSourceText
     * @param fullTargetText
     */
    public void update(int iSegmentNumber, String sourceSegment, String targetSegment)
    {
        sourceSegmentTags.removeAll();
        targetSegmentTags.removeAll();
        String[] sourceTags = parseTags(sourceSegment);
        String[] targetTags = parseTags(targetSegment);
        if ((sourceTags == null) && (targetTags == null))
        {
            hide();
            return;
        }
        shell.setVisible(true);
        if (sourceTags != null)
            sourceSegmentTags.setItems(sourceTags);
        if (targetTags != null)
            targetSegmentTags.setItems(targetTags);
        shell.setText(message.getString("TagWindow") + " " + iSegmentNumber);

    }
}
