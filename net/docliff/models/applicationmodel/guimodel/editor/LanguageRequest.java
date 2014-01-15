package net.docliff.models.applicationmodel.guimodel.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import de.folt.util.LanguageHandling;

public class LanguageRequest
{

    private Display _display;

    private Button cancelButton;

    private Combo combo;

    private String language;

    private de.folt.util.Messages message;

    private Button okButton;

    private Shell shell;

    private String userLanguage = "en";

    /**
     * Method LanguageRequest.
     * 
     * @param display
     */
    public LanguageRequest(Display display, boolean bSource)
    {
        _display = display;
        shell = new Shell(display, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        
        message = de.folt.util.Messages.getInstance("net.docliff.models.applicationmodel.guimodel.editor.XliffEditor", userLanguage);
        
        if (bSource)
            shell.setText(message.getString("Source_Language_1"));
        else
            shell.setText(message.getString("Target_Language_1"));
        shell.setLayout(new GridLayout(1, false));



        Composite composite = new Composite(shell, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        Label label = new Label(composite, SWT.NONE);
        label.setText(message.getString("Select_language"));

        combo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
        combo.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                language = LanguageHandling.getShortLanguageCodeFromCombinedTable(combo.getText());
                okButton.setEnabled(true);
                shell.setDefaultButton(okButton);
            }
        });
        combo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        String languages[] = LanguageHandling.getCombinedLanguages();
        combo.setItems(languages);

        Composite buttonHolder = new Composite(shell, SWT.BORDER);
        buttonHolder.setLayout(new GridLayout(2, true));
        buttonHolder.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));

        okButton = new Button(buttonHolder, SWT.PUSH | SWT.OK);
        okButton.setText(message.getString("AcceptLanguage")); //$NON-NLS-1$
        okButton.setEnabled(false);
        okButton.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
        okButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                shell.close();
            }
        });

        cancelButton = new Button(buttonHolder, SWT.PUSH | SWT.CANCEL);
        cancelButton.setText(message.getString("Cancel"));
        cancelButton.setEnabled(true);
        cancelButton.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
        cancelButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                language = "";
                shell.close();
            }
        });

        shell.pack();
    }

    /**
     * Returns the language.
     * 
     * @return String
     */
    public String getLanguage()
    {
        if (language == null)
        {
            return "";
        }
        if (language.equals(""))
        {
            return language;
        }
        return language;
    }

    /**
     * Method show.
     */
    public void show()
    {
        shell.open();
        shell.forceActive();
        while (!shell.isDisposed())
        {
            if (!_display.readAndDispatch())
            {
                _display.sleep();
            }
        }
    }

}