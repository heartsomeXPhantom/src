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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.folt.models.applicationmodel.guimodel.support.CancelTask;
import de.folt.models.applicationmodel.guimodel.support.ProgressDialog;
import de.folt.models.applicationmodel.guimodel.support.ProgressDialogSupport;

public class ImportOpenTMSDataSource extends Dialog
{

    private static Shell shell;

    private static List table;

    private StyledText sourceNameText;

    private de.folt.util.Messages message;

    private Display display;

    private Button select;

    public ImportOpenTMSDataSource(Shell parent)
    {
        super(parent);
        shell = new Shell(parent, SWT.DIALOG_TRIM);
        shell.setLayout(new GridLayout(1, false));

        message = de.folt.util.Messages.getInstance("de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", "en");
        display = parent.getDisplay();
        shell.setText(message.getString("importopenTMSDatabase"));

        Group filesComposite = new Group(shell, SWT.NONE);
        filesComposite.setLayout(new GridLayout(3, false));
        filesComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        filesComposite.setText(message.getString("filesComposite1"));

        Label sourceNameLabel = new Label(filesComposite, SWT.NONE);
        sourceNameLabel.setText(message.getString("Import_Document"));

        sourceNameText = new StyledText(filesComposite, SWT.BORDER);
        sourceNameText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        sourceNameText.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {

                if (sourceNameText.getText().equals(""))
                {
                    select.setEnabled(false);
                    return;
                }
                File f = new File(sourceNameText.getText());
                if (!f.exists())
                {
                    select.setEnabled(false);
                    return;
                }
                select.setEnabled(true);
            }
        });

        sourceNameText.addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent e)
            {
                // int iKey = e.keyCode;
                // String key = Character.toString(e.character);
                // here we should check if it is not a function key...

            }

            public void keyReleased(KeyEvent e)
            {

                if (sourceNameText.getText().equals(""))
                {
                    select.setEnabled(false);
                    return;
                }

                File f = new File(sourceNameText.getText());
                if (!f.exists())
                {
                    select.setEnabled(false);
                    return;
                }
                select.setEnabled(true);

                if (table.getSelectionIndex() < 0)
                    select.setEnabled(false);
            }
        });

        Button browseSource = new Button(filesComposite, SWT.PUSH);
        browseSource.setText(message.getString("Browse"));
        browseSource.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                FileDialog fd = new FileDialog(shell, SWT.OPEN);
                String extensions[] =
                    {
                        "*.tmx;*.xlf;*.xliff;*.tbx;*.*"
                    };
                fd.setFilterExtensions(extensions);
                if (System.getProperty("file.separator").equals("/"))
                {
                    fd.setFilterPath(System.getProperty("user.home"));
                }
                fd.open();
                if (fd.getFileName() == "")
                { //$NON-NLS-1$
                    fd = null;
                    extensions = null;
                    select.setEnabled(true);
                    if (sourceNameText.getText().equals(""))
                        select.setEnabled(false);
                    return;
                }
                else
                {
                    String separator = System.getProperty("file.separator");
                    sourceNameText.setText(fd.getFilterPath() + separator + fd.getFileName());
                    fd = null;
                    select.setEnabled(true);
                    if (table.getSelectionIndex() < 0)
                        select.setEnabled(false);
                }
            }
        });

        Label promptdbs = new Label(shell, SWT.NONE);
        GridData layoutDataTmx = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        layoutDataTmx.horizontalSpan = 1;
        promptdbs.setLayoutData(layoutDataTmx);
        promptdbs.setText(message.getString("OpenTMS_database_list") + "                                                       ");

        table = new List(shell, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.V_SCROLL);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 1;
        table.setLayoutData(data);

        final Vector<String[]> tmxDatabases = de.folt.models.datamodel.DataSourceConfigurations.getOpenTMSDatabasesWithType();

        int size = 0;

        Composite bottom = new Composite(shell, SWT.BORDER);
        bottom.setLayout(new GridLayout(2, true));
        bottom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        select = new Button(bottom, SWT.PUSH);
        select.setText(message.getString("importopenTMSDatabase"));
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
                if (sourceNameText.getText().equals(""))
                    return;
                Cursor hglass = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
                Cursor arrow = new Cursor(shell.getDisplay(), SWT.CURSOR_ARROW);
                shell.setCursor(hglass);
                ProgressDialog progressDialog = new ProgressDialog(shell, message.getString("Import_TMX_Document"), message.getString("Convert_Process"), ProgressDialog.SINGLE_BAR);
                progressDialog.open();
                progressDialog.updateProgressMessage("Import_TMX_Document");

                ProgressDialogSupport pdSupport = null;
                pdSupport = new ProgressDialogSupport(progressDialog);
                CancelTask cancel = null;
                Thread cancelthread = null;
                if (pdSupport != null)
                {
                    pdSupport.updateProgressIndication("Import_TMX_Document");
                    cancel = new CancelTask(pdSupport.returnShell(), "Cancel", "Cancel");
                    pdSupport.setCancelTask(cancel);
                    cancelthread = new Thread(cancel);
                    cancelthread.run();

                }
                try
                {
                    int selected = table.getSelectionIndex();
                    if (selected >= 0)
                    {
                        String database = (String) (table.getSelection())[0];
                        database = database.replaceAll("(.*) \\(.*$", "$1");
                        Hashtable<String, Object> param = new Hashtable<String, Object>();
                        param.put("dataSourceName", database);
                        param.put("sourceDocument", sourceNameText.getText());
                        param.put("inputDocumentType", "FILE");
                        ImportObserver importObserver = new ImportObserver(pdSupport);
                        param.put("observer", importObserver);
                        Vector<String> result = de.folt.rpc.connect.Interface.runImportOpenTMSDataSource(param);
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
                            String string = message.getString("Failure_Import");
                            messageBox.setText(string);
                            string = message.getString("OpenTMS_database_Import_Failure") + " " + sourceNameText.getText() + " -> " + database;
                            messageBox.setMessage(string);
                            messageBox.open();
                        }
                        else
                        {
                            MessageBox messageBox = new MessageBox(shell);
                            String string = message.getString("Success_Import");
                            messageBox.setText(string);
                            string = message.getString("OpenTMS_database_Import_Success") + " " + sourceNameText.getText() + " -> " + database;
                            messageBox.setMessage(string);
                            messageBox.open();
                        }
                    }
                }
                catch (Exception e1)
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
            if (display.readAndDispatch())
            {
                display.sleep();
            }
        }
    }

    public class ImportObserver implements Observer
    {

        /**
         * @param pdSupport
         */
        public ImportObserver(ProgressDialogSupport pdSupport)
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
            if (pdSupport.getCancelTask() != null)
            {
                if (pdSupport.getCancelTask().isCancelled())
                {
                    // should stop operation now..
                    vec.add(0);
                }
                else
                {
                    // operation still running...
                    vec.add(1);
                }  
            }
        }

    }
}
