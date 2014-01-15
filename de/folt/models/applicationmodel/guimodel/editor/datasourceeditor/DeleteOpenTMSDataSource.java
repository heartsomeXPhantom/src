/*
 * Created on 20.03.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class DeleteOpenTMSDataSource extends Dialog
{

    private static Shell shell;

    private static List table;
    
    private de.folt.util.Messages message;
    
    private Display display;
    
    
    public DeleteOpenTMSDataSource(Shell parent)
    {
        super(parent);
        shell = new Shell(parent, SWT.DIALOG_TRIM);
        shell.setLayout(new GridLayout(1, false));
        
        message = de.folt.util.Messages.getInstance("de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", "en");
        display = parent.getDisplay();
        
        shell.setText(message.getString("deleteopenTMSDatabase"));
        
        Label promptdbs = new Label(shell, SWT.NONE);
        GridData layoutDataTmx = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        layoutDataTmx.horizontalSpan = 1;
        promptdbs.setLayoutData(layoutDataTmx);
        promptdbs.setText(message.getString("OpenTMS_database_list") + "                                                       ");

        table = new List(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 1;
        table.setLayoutData(data);

        Vector<String> tmxDatabases = de.folt.models.datamodel.DataSourceConfigurations.getOpenTMSDatabases();
        int size = 0;
        if (tmxDatabases != null)
        {
            size = tmxDatabases.size();
            if (size > 0)
            {
                for (int i = 0; i < size; i++)
                {
                    table.add(tmxDatabases.get(i));
                }

                if ((size >= 0))
                    table.select(0);
            }
        }
        else
        {
            table.setEnabled(false);
        }
        
        Composite bottom = new Composite(shell, SWT.BORDER);
        bottom.setLayout(new GridLayout(2, true));
        bottom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Button select = new Button(bottom, SWT.PUSH);
        select.setText(message.getString("deleteopenTMSDatabase"));
        if (size == 0)
        {
            select.setEnabled(false);
        }
        
        select.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        select.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                Cursor hglass = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
                Cursor arrow = new Cursor(shell.getDisplay(), SWT.CURSOR_ARROW);
                shell.setCursor(hglass);
                try
                {
                    int selected = table.getSelectionIndex();
                    if (selected >= 0)
                    {
                        String database = (String) (table.getSelection())[0];
                        Hashtable<String, Object> param = new Hashtable<String, Object>();
                        param.put("dataSourceName", database);
                        Vector<String> result = de.folt.rpc.connect.Interface.runDeleteDB(param);
                        shell.setCursor(arrow);
                        if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + ""))
                        {
                            MessageBox messageBox = new MessageBox(shell);
                            String string = message.getString("Failure_Deleting");
                            messageBox.setText(string);
                            string = message.getString("OpenTMS_database_Delete_Failure") + " " + database;
                            messageBox.setMessage(string);
                            messageBox.open();
                        }
                        else
                        {
                            MessageBox messageBox = new MessageBox(shell);
                            String string = message.getString("Success_Deleting");
                            messageBox.setText(string);
                            string = message.getString("OpenTMS_database_Delete_Success") + " " + database;
                            messageBox.setMessage(string);
                            messageBox.open();
                        }
                    }
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
                shell.setCursor(arrow);
                shell.close();
            }
        });

        Button close = new Button(bottom, SWT.PUSH);
        close.setText(message.getString("Cl&ose"));
        close.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        close.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                shell.close();
            }
        });

        shell.pack();
    }
    
    /**
     * show 
     */
    public void show()
    {
        shell.open();
        shell.forceActive();
        while (!shell.isDisposed())
        {
            if (display.readAndDispatch())
            {
                display.sleep();
            }
        }
    }
}
