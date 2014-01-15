/*
 * Created on 15.10.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.docliff.models.applicationmodel.guimodel.editor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.araya.eaglememex.util.EMXProperties;

import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.EditorConfiguration;
import de.folt.models.datamodel.phrasetranslate.PhraseTranslateResult;
import de.folt.util.OpenTMSProperties;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class XliffEditorDictionaryViewer extends Dialog
{

    private static de.folt.util.Messages message;

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

        shell.setText("Test Dictionry Viewer Form");

        shell.setSize(1200, 900);

        @SuppressWarnings("unused")
        String configFile = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.XliffEditor.EditorConfigurationDirectory");
        String propfileName = OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaPropertiesFile");
        EMXProperties.getInstance(propfileName);

        XliffEditorDictionaryViewer xliffEditorform = new XliffEditorDictionaryViewer(shell, SWT.RESIZE);
        xliffEditorform.show();

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

    private String configfile;

    private String curruser;

    // private SashForm dictionaryHolder;

    private String dictionaryName;

    private EditorConfiguration editorConfiguration;

    private int editorHeight;

    private int editorWidth;

    private int editorxLocation;

    private int editoryLocation;

    private Observable observable = new Observable();

    private Vector<Observer> observers = new Vector<Observer>();

    private Shell shell;

    private Table table;

    private String userLanguage = "en";

	private boolean bPreffesSize  = false;

    /**
     * @param shell2
     * @param none
     */
    public XliffEditorDictionaryViewer(Shell parentShell, int style)
    {
        super(parentShell, style);
        createDictionaryViewer(parentShell, "XliffEditorDictionaryViewer", "", "", style);
    }

    /**
     * @param targetLanguage 
     * @param sourceLanguage 
     * @param shell
     */
    public XliffEditorDictionaryViewer(Shell parentShell, String title, String sourceLanguage, String targetLanguage, int style)
    {
        super(parentShell, style);
        createDictionaryViewer(parentShell, title, sourceLanguage, targetLanguage, style);
    }

    public void adaptDictionaryViewer(String sourceLanguage, String targetLanguage)
    {
        table.getColumn(0).setText(message.getString("Source_term") + " " + sourceLanguage);
        table.getColumn(1).setText(message.getString("Target_term") + " " + targetLanguage);
    }

    public void adaptDictionaryViewer(String title, String sourceLanguage, String targetLanguage)
    {
        this.shell.setText(message.getString("XliffEditorDictionary") + " " + title);
        table.getColumn(0).setText(message.getString("Source_term") + " " + sourceLanguage);
        table.getColumn(1).setText(message.getString("Target_term") + " " + targetLanguage);
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
     * close 
     */
    public void close()
    {
        shell.dispose();
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

    private void createDictionaryViewer(Shell parentShell, String title, String sourceLanguage, String targetLanguage, int style)
    {
        message = de.folt.util.Messages.getInstance("net.docliff.models.applicationmodel.guimodel.editor.XliffEditor", userLanguage);
        shell = new Shell(parentShell.getDisplay(), SWT.DIALOG_TRIM | style);
        shell.setLayout(new GridLayout(1, true));
        shell.setSize(300, 400);

        configfile = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.XliffEditor.EditorConfigurationDirectory");
        String propfileName = OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaPropertiesFile");
        EMXProperties.getInstance(propfileName);

        curruser = System.getProperty("user.name").toLowerCase();
        editorConfiguration = new de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.EditorConfiguration(shell, configfile, "docliffEditor", curruser);

        shell.addListener(SWT.Close, new Listener()
        {
            public void handleEvent(Event event)
            {
                savePosition();
                event.doit = false;
            }
        });

        editorWidth = 300;
        editorHeight = 400;
        editorxLocation = 80;
        editoryLocation = 80;

        if (title == null)
            title = "";

        shell.setText(title);
        dictionaryName = title.replaceAll(" ", "");
        try
        {
            editorWidth = Math.max(editorWidth, Integer.parseInt(editorConfiguration.loadValueForKey("w" + dictionaryName)));
            editorHeight = Integer.parseInt(editorConfiguration.loadValueForKey("h" + dictionaryName));

            editorxLocation = Integer.parseInt(editorConfiguration.loadValueForKey("xLoc" + dictionaryName));
            editoryLocation = Integer.parseInt(editorConfiguration.loadValueForKey("yLoc" + dictionaryName));
        }
        catch (NumberFormatException e)
        {
            // e.printStackTrace();
        }

        // dictionaryHolder = new SashForm(this.shell, SWT.NONE);
        // dictionaryHolder.setOrientation(SWT.HORIZONTAL);
        GridLayout dictionaryHolderLayout = new GridLayout(1, true);
        dictionaryHolderLayout.marginWidth = 1;
        dictionaryHolderLayout.marginHeight = 1;
        dictionaryHolderLayout.horizontalSpacing = 0;
        // dictionaryHolder.setLayout(dictionaryHolderLayout);
        // dictionaryHolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        table = new Table(shell, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER); // SWT.BORDER | SWT.V_SCROLL);
        GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL | GridData.FILL_VERTICAL | GridData.GRAB_VERTICAL);
        // data.widthHint = 300;
        table.setLayoutData(data);
        table.setHeaderVisible(true);

        TableColumn sourceTerm = new TableColumn(table, SWT.NONE);
        sourceTerm.setText(message.getString("Source_term") + " " + sourceLanguage);
        sourceTerm.setWidth(editorWidth / 2 - 10);
        TableColumn targetTerm = new TableColumn(table, SWT.NONE);
        targetTerm.setText(message.getString("Target_term") + " " + targetLanguage);
        targetTerm.setWidth(editorWidth / 2 - 10);

        table.setSortDirection(SWT.UP);
        table.setSortColumn(sourceTerm);

        table.addMouseListener(new MouseListener()
        {
            public void mouseDoubleClick(MouseEvent e)
            {
                if (table.getSelectionIndex() > -1)
                {
                    TableItem[] item = table.getSelection();
                    notifyObservers(item[0].getText(1));
                }
            }

            public void mouseDown(MouseEvent e)
            {
                // System.out.println("Mouse Down.");
            }

            public void mouseUp(MouseEvent e)
            {

            }

        });

        Button usePhrase = new Button(shell, SWT.PUSH | SWT.CENTER);
        usePhrase.setText(message.getString("usePhraseForTargetTranslation"));
        usePhrase.setToolTipText(message.getString("usePhraseForTargetTranslationExppnanation"));
        GridData datap = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
        usePhrase.setLayoutData(datap);
        usePhrase.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                if (table.getSelectionIndex() > -1)
                {
                    TableItem[] item = table.getSelection();
                    notifyObservers(item[0].getText(1));
                }
            }
        });

        if (bPreffesSize)
        {
            shell.addControlListener(new ControlAdapter()
            {
                public void controlResized(ControlEvent e)
                {
                    Rectangle area = shell.getClientArea();
                    Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                    int width = area.width - 2 * table.getBorderWidth();
                    if (preferredSize.y > area.height + table.getHeaderHeight())
                    {
                        // Subtract the scrollbar width from the total column width
                        // if a vertical scrollbar will be required
                        Point vBarSize = table.getVerticalBar().getSize();
                        width -= vBarSize.x;
                    }
                    Point oldSize = table.getSize();
                    if (oldSize.x > area.width)
                    {
                        // table is getting smaller so make the columns 
                        // smaller first and then resize the table to
                        // match the client area width
                        table.getColumn(0).setWidth(width / 2);
                        table.getColumn(1).setWidth(width - table.getColumn(0).getWidth());
                        table.setSize(area.width, area.height - table.getBorderWidth() * 2);
                        table.getLinesVisible();
                    }
                    else
                    {
                        // table is getting bigger so make the table 
                        // bigger first and then make the columns wider
                        // to match the client area width
                        table.setSize(area.width, area.height - table.getBorderWidth() * 2);
                        table.getColumn(0).setWidth(width / 2);
                        table.getColumn(1).setWidth(width - table.getColumn(0).getWidth());
                    }
                }
            });
        }

        table.clearAll();
        table.removeAll();
        for (int i = 0; i < 20; i++)
        {
            TableItem tabitem = new TableItem(table, SWT.NONE);

            String[] values =
                {
                        "", ""
                };
            tabitem.setText(values);
        }

        // dictionaryHolder.setWeights(new int[]
        //     {
        //        1
        //    });
        // dictionaryHolder.pack();
        shell.pack();
        shell.setLocation(editorxLocation, editoryLocation);
        shell.setSize(editorWidth, editorHeight);
        table.clearAll();
    }

    /**
     * deleteObservers
     */
    public void deleteObservers()
    {
        observers = new Vector<Observer>();
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

    public void savePosition()
    {
        editorConfiguration.saveKeyValuePair("w" + dictionaryName, shell.getSize().x + "");
        editorConfiguration.saveKeyValuePair("h" + dictionaryName, shell.getSize().y + "");

        editorConfiguration.saveKeyValuePair("xLoc" + dictionaryName, shell.getLocation().x + "");
        editorConfiguration.saveKeyValuePair("yLoc" + dictionaryName, shell.getLocation().y + "");
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

    public void setTerms(String dictionary)
    {
        table.clearAll();
        table.removeAll();
        dictionary = dictionary.replaceAll("\\n", "");
        dictionary = dictionary.replaceAll("\\r", "");
        dictionary = dictionary.replaceFirst("^.*?\\|(.*)", "$1"); // ignore the language!!
        String[] dict = dictionary.split("\\|");

        Arrays.sort(dict);
        for (int i = 0; i < dict.length; i++)
        {
            try
            {
                TableItem tabitem = new TableItem(table, SWT.NONE);
                String[] entry = dict[i].split(";");
                if (entry.length > 1)
                {
                    String[] values =
                        {
                                entry[0], entry[1]
                        };
                    tabitem.setText(values);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        // table.pack();
    }

    /**
     * setTerms set the terms or phrases in the dictionary editor
     * @param phrases
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void setTerms(Vector<PhraseTranslateResult> phrases)
    {
        table.clearAll();
        table.removeAll();
        if (phrases == null)
            return;

        Collections.sort(phrases, new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                String c1 = ((PhraseTranslateResult)o1).getSourcePhrase();
                String c2 = ((PhraseTranslateResult)o2).getSourcePhrase();
                return (c1.toLowerCase()).compareTo(c2.toLowerCase());
            }
        });
        for (int i = 0; i < phrases.size(); i++)
        {
            TableItem tabitem = new TableItem(table, SWT.NONE);

            String[] values =
                {
                        phrases.get(i).getSourcePhrase(), phrases.get(i).getTargetPhrase()
                };
            tabitem.setText(values);
        }
        // table.pack();
    }

    /**
     * setTitle set the title for the viewer
     * @param title the title to set
     */
    public void setTitle(String title)
    {
        shell.setText(title);
    }

    public void show()
    {
        shell.open();
    }

}
