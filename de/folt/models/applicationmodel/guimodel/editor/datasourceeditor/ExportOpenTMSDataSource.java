/*
 * Created on 20.03.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.io.File;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
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

import de.folt.models.applicationmodel.guimodel.support.CancelTask;
import de.folt.models.applicationmodel.guimodel.support.ProgressDialog;
import de.folt.models.applicationmodel.guimodel.support.ProgressDialogSupport;

public class ExportOpenTMSDataSource extends Dialog
{
    private static Shell shell;

    private static Label text;

    private static List table;

    private de.folt.util.Messages message;

    private static String databasedir = "";

    private Display display;

    @SuppressWarnings("unused")
	public ExportOpenTMSDataSource(Shell parent)
    {
        super(parent);
        shell = new Shell(parent, SWT.DIALOG_TRIM);
        shell.setLayout(new GridLayout(1, false));
        message = de.folt.util.Messages.getInstance("de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", "en");
        display = parent.getDisplay();
        shell.setText(message.getString("exportopenTMSDatabase"));

        Label promptdbs = new Label(shell, SWT.NONE);
        GridData layoutDataTmx = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
        layoutDataTmx.horizontalSpan = 1;
        promptdbs.setLayoutData(layoutDataTmx);
        promptdbs.setText(message.getString("OpenTMS_database_list") + "                                                       ");

        final Vector<String[]> tmxDatabases = de.folt.models.datamodel.DataSourceConfigurations.getOpenTMSDatabasesWithType();
        int size = tmxDatabases.size();
        
        table = new List(shell, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.V_SCROLL);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 1;
        table.setLayoutData(data);
        table.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                String database = (String) table.getSelection()[0];
                database = database.replaceAll("(.*) \\(.*$", "$1");
                if (databasedir.equals(""))
                    text.setText(database + ".tmx");
                else
                    text.setText(databasedir + "/" + database + ".tmx");
            }
        });



        text = new Label(shell, SWT.NONE);
        layoutDataTmx = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
        layoutDataTmx.horizontalSpan = 1;
        text.setLayoutData(layoutDataTmx);
        text.setText("");

        Composite bottom = new Composite(shell, SWT.BORDER);
        bottom.setLayout(new GridLayout(2, true));
        bottom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Button select = new Button(bottom, SWT.PUSH);
        select.setText(message.getString("exportopenTMSDatabase"));
        if (size == 0)
        {
            select.setEnabled(false);
        }

        select.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        select.addSelectionListener(new SelectionAdapter()
        {
            @SuppressWarnings("deprecation")
            public void widgetSelected(SelectionEvent e)
            {
                Cursor hglass = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
                Cursor arrow = new Cursor(shell.getDisplay(), SWT.CURSOR_ARROW);
                shell.setCursor(hglass);
                ProgressDialog progressDialog = new ProgressDialog(shell, message.getString("Export_TMX_Document"), message.getString("Convert_Process"), ProgressDialog.SINGLE_BAR);
                progressDialog.open();
                progressDialog.updateProgressMessage("Export_TMX_Document");

                ProgressDialogSupport pdSupport = null;
                pdSupport = new ProgressDialogSupport(progressDialog);
                CancelTask cancel = null;
                Thread cancelthread = null;
                if (pdSupport != null)
                {
                    pdSupport.updateProgressIndication("Export_TMX_Document");
                    cancel = new CancelTask(pdSupport.returnShell(), "Cancel", "Cancel");
                    cancelthread = new Thread(cancel);
                    cancelthread.run();
                }
                try
                {
                    int selected = table.getSelectionIndex();
                    if (selected >= 0)
                    {
                        int iNum = table.getSelectionIndex();
                        String database = table.getItem(iNum);
                        database = database.replaceAll("(.*) \\(.*$", "$1");
                        Hashtable<String, Object> param = new Hashtable<String, Object>();

                        // ok must do something about the type ...
                        // if sql store to database directory

                        File f = new File(database);
                        if (!f.exists())
                            databasedir = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.dir") + "/database/";
                        else
                            databasedir = "";

                        String exportfile = databasedir + database + ".tmx";
                        param.put("dataSourceName", database);
                        param.put("exportFile", exportfile);
                        ExportObserver importObserver = new ExportObserver(pdSupport);
                        param.put("observer", importObserver);
                        Vector<String> result = de.folt.rpc.connect.Interface.runExportOpenTMSDataSource(param);
                        if (pdSupport != null)
                        {
                            if (!cancel.isCancelled())
                                cancel.close();
                            if (cancelthread.isAlive())
                                cancelthread.stop();
                            pdSupport = null;
                        }
                        pdSupport = null;
                        progressDialog.close();
                        if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + ""))
                        {
                            MessageBox messageBox = new MessageBox(shell);
                            String string = message.getString("Failure_Export");
                            messageBox.setText(string);
                            string = message.getString("OpenTMS_database_Export_Failure") + " " + database;
                            messageBox.setMessage(string);
                            messageBox.open();
                        }
                        else
                        {
                            MessageBox messageBox = new MessageBox(shell);
                            String string = message.getString("Success_Export");
                            messageBox.setText(string);
                            string = message.getString("OpenTMS_database_Export_Success") + " " + database + " -> " + exportfile;
                            messageBox.setMessage(string);
                            messageBox.open();
                        }
                    }
                }
                catch (Exception ex)
                {
                    if (pdSupport != null)
                    {
                        if (!cancel.isCancelled())
                            cancel.close();
                        if (cancelthread.isAlive())
                            cancelthread.stop();
                        pdSupport = null;
                    }
                    pdSupport = null;
                    progressDialog.close();
                    ex.printStackTrace();
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
        
        int iMaxShow = 15;
        
        if (tmxDatabases != null)
        {
            size = tmxDatabases.size();
            iMaxShow = Math.min(size, 15);
            if (size > 0)
            {
                for (int i = 0; i < iMaxShow; i++)
                {
                    String name = tmxDatabases.get(i)[0];
                    String type = tmxDatabases.get(i)[1];
                    table.add(name + " (" + type + ")");
                }
            }
        }
        else
        {
            table.setEnabled(false);
        }

        shell.pack();
        
        if (tmxDatabases != null)
        {
            size = tmxDatabases.size();
            if (size > 0)
            {
                for (int i = iMaxShow; i < size; i++)
                {
                    String name = tmxDatabases.get(i)[0];
                    String type = tmxDatabases.get(i)[1];
                    table.add(name + " (" + type + ")");
                }
            }
        }
        
        if (size > 0)
        {
            table.select(0);
            table.setSelection(0);
            int iNum = table.getSelectionIndex();
            String database = table.getItem(iNum);
            database = database.replaceAll("(.*) \\(.*$", "$1");
            text.setText(databasedir + database + ".tmx");
        }
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
            if (this.display.readAndDispatch())
            {
                this.display.sleep();
            }
        }
    }

    public class ExportObserver implements Observer
    {

        /**
         * @param pdSupport
         */
        public ExportObserver(ProgressDialogSupport pdSupport)
        {
            super();
            this.pdSupport = pdSupport;
        }

        private ProgressDialogSupport pdSupport;

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
         */
        @SuppressWarnings("unchecked")
        @Override
        public void update(Observable arg0, Object arg1)
        {
            Vector<Integer> vec = (Vector<Integer>) arg1;
            int iPos = vec.get(1);
            int iSize = vec.get(0);
            pdSupport.updateProgressIndication(iPos, iSize);
        }

    }
}
