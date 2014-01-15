/*
 * Created on Jul 16, 2003
 *
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class DisplayLog extends Dialog
{

    private Shell shell;

    private Display display;

    private String logfile = null;

    private StyledText text;

    private boolean isOpen = false;

    private de.folt.util.Messages message;

    private String userLanguage = "en";

    /**
     * @param parent the parent of the log window
     * @param logFile the log  file (or any file) to display
     */
    public DisplayLog(Shell parent, String logFile)
    {
        super(parent, SWT.NONE);
        new DisplayLog(parent, logFile, true, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY);
    }

    /**
     * @param parent the parent of the log window
     * @param logFile the log  file (or any file) to display
     * @param bUpdate if true an update button is displayed
     * @param iStyle style for the window
     */
    public DisplayLog(Shell parent, String logFile, boolean bUpdate, int iStyle)
    {

        super(parent, SWT.NONE);
        message = de.folt.util.Messages.getInstance("de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", userLanguage);

        if (isOpen == true)
        {
            display.beep();
            update();
            return;
        }
        logfile = logFile;

        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.MODELESS | SWT.RESIZE); //)/SWT.PRIMARY_MODAL);
        shell.setLayout(new GridLayout(1, false));
        display = shell.getDisplay();

        shell.addListener(SWT.Close, new Listener()
        {
            public void handleEvent(Event event)
            {
                isOpen = false;
                return;
            }
        });

        text = new StyledText(shell, iStyle);
        GridData textData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);
        textData.heightHint = 400;
        textData.widthHint = 600;
        text.setLayoutData(textData);

        Composite composite = new Composite(shell, SWT.BORDER);
        composite.setLayout(new GridLayout(2, true));
        composite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));

        if (bUpdate)
        {
            Button update = new Button(composite, SWT.PUSH | SWT.CENTER);
            update.setText(message.getString("UpdateLog"));
            update.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    update();
                }
            });
        }

        Button close = new Button(composite, SWT.PUSH | SWT.CENTER);
        close.setText(message.getString("_&Close__10"));
        close.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                isOpen = false;
                shell.close();
            }
        });

        try
        {
            FileInputStream file = new FileInputStream(logFile);
            byte[] array = new byte[file.available()];
            file.read(array);
            text.setText(new String(array));
            file.close();
            file = null;
        }
        catch (FileNotFoundException e1)
        {
            MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
            box.setMessage(e1.getLocalizedMessage());
            box.open();
        }
        catch (IOException e)
        {
            MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
            box.setMessage(e.getLocalizedMessage());
            box.open();
        }

        isOpen = true;
        shell.pack();
    }

    /**
     * setTitle set the title of the log window
     * @param title
     */
    public void setTitle(String title)
    {
        if ((shell != null) && (title != null))
            shell.setText(title);
    }

    /**
     * show show the Log Window
     */
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

    /**
     * update updates the Log Window with the current content of the log file
     */
    public void update()
    {
        try
        {
            FileInputStream file = new FileInputStream(logfile);
            byte[] array = new byte[file.available()];
            file.read(array);
            text.setText(new String(array));
            file.close();
            file = null;
        }
        catch (FileNotFoundException e1)
        {
            MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
            box.setMessage(e1.getLocalizedMessage());
            box.open();
        }
        catch (IOException e)
        {
            MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
            box.setMessage(e.getLocalizedMessage());
            box.open();
        }
    }
}