package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import de.folt.util.OpenTMSProperties;

public class About extends Dialog
{
    private Shell shell;

    private Display display;

    private static String text = "";

    private static String out = "opentmsinfo.txt";

    private static Table propertiesTable = null;
    
    private de.folt.util.Messages message;
    
    private String userLanguage = "en";
    
    private DataSourceEditor dataSourceEditor = new DataSourceEditor();

    /**
     * @param parent
     * @param logfilename
     * @param displayImage
     */
    public About(Shell parent, String currentUser)
    {
        super(parent, SWT.NONE);
        
        message = de.folt.util.Messages.getInstance("de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", userLanguage); 

        String version = dataSourceEditor.getDataSourceEditorVersion();

        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
        display = shell.getDisplay();
        shell.setText(message.getString("About") + message.getString("OpenTMSDataSource_Editor") + " " + version); //$NON-NLS-1$
        shell.setLayout(new GridLayout(1, true));

        text = version + "\n";

        propertiesTable = new Table(shell, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        propertiesTable.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
        propertiesTable.setHeaderVisible(true);

        TableColumn segnum = new TableColumn(propertiesTable, SWT.NONE);
        segnum.setText(message.getString("About.1"));
        segnum.setWidth(120);

        TableColumn dateitem = new TableColumn(propertiesTable, SWT.NONE);
        dateitem.setText(message.getString("About.2"));
        dateitem.setWidth(500);

        addInfo("Version Info", version);
        addInfo("e-mail", message.getString("e-mail"));
        addInfo("OpenTMS Website", OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMSHomepage"));
        Date endTime = Calendar.getInstance(Locale.US).getTime();
        addInfo("Date", endTime.toString());
        addInfo("OS", System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch"));
        addInfo("# Processors", Runtime.getRuntime().availableProcessors() + "");
        addInfo("Java", System.getProperty("java.version"));
        addInfo("Vendor", System.getProperty("java.vendor"));
        addInfo("User", System.getProperty("user.name"));
        addInfo("Login User", currentUser);
        addInfo("User dir", System.getProperty("user.dir"));
        addInfo("TM Version", de.folt.rpc.webserver.OpenTMSServer.getVersion());
        String path = OpenTMSProperties.getInstance().returnPropFilePath();
        File f = new File(path);
        if (f.exists())
            try
            {
                path = f.getCanonicalPath();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        addInfo("OpenTMSProperties", path);
        addInfo("SWT Version", "" + SWT.getVersion());
        addInfo("SWT Platform", SWT.getPlatform());


        Runtime r = Runtime.getRuntime();
        addInfo("totalMemory", "" + r.totalMemory());
        addInfo("freeMemory", "" + r.freeMemory());
        addInfo("maxMemory", "" + r.maxMemory());
        // addInfo("BuildTime", "" + com.araya.util.version.buildDate());

        Button b = new Button(shell, SWT.PUSH);
        b.setText(message.getString("saveInfo") + " " + out); //$NON-NLS-1$
        b.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent ev)
            {
                try
                {

                    FileOutputStream output = new FileOutputStream(out);
                    output.write(text.getBytes("utf-8"));
                    output.close();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        Button exit = new Button(shell, SWT.PUSH);
        exit.setText(message.getString("exit")); //$NON-NLS-1$
        exit.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent ev)
            {
                shell.close();
            }
        });

        shell.setDefaultButton(exit);
        shell.pack();
    }

    private static TableItem setPropertiesTableItem(String[] values)
    {
        TableItem item = new TableItem(propertiesTable, SWT.NONE);
        item.setText(values);
        return item;
    }

    private static void addInfo(String st1, String st2)
    {
        String values[] = new String[2];
        values[0] = st1;
        values[1] = st2;
        setPropertiesTableItem(values);
        text = text + st1 + ": " + st2 + "\n";
    }

    public void show()
    {
        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
    }
}