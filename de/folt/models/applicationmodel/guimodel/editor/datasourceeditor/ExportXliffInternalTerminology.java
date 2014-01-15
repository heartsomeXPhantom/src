/*
 * Created on Jul 17, 2003
 *  
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.OpenTMSProperties;

public class ExportXliffInternalTerminology
{

    private String configfile = "";

    @SuppressWarnings("unused")
    private String configFile;

    private String curruser = "";

    private de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.EditorConfiguration editorConfiguration = null;

    private de.folt.util.Messages message;

    private String separator;

    private Shell shell;

    private String targetDocument;

    String targetExtensions[] =
        {
            "*.tbx;*.*"}; //$NON-NLS-1$

    private String xliffDocument;

    public ExportXliffInternalTerminology(Shell parent, String xliffdefaultfilename)
    {
        separator = System.getProperty("file.separator"); //$NON-NLS-1$

        message = de.folt.util.Messages.getInstance("de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", "en");

        configfile = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.DataSourceEditor.EditorConfigurationDirectory");

        curruser = System.getProperty("user.name").toLowerCase();
        editorConfiguration = new de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.EditorConfiguration(shell, configfile, curruser);

        String revXliffFile = editorConfiguration.loadValueForKey("termXliffFile");
        String revConvertedFile = editorConfiguration.loadValueForKey("termTbxFile");

        shell = new Shell(parent.getDisplay(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
        shell.setLayout(new GridLayout(1, false));
        shell.setText(message.getString("exportXliffInternalTerminology")); //$NON-NLS-1$

        Composite filesComposite = new Composite(shell, SWT.NONE);
        filesComposite.setLayout(new GridLayout(3, false));
        filesComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        Label sourceNameLabel = new Label(filesComposite, SWT.NONE);
        sourceNameLabel.setText(message.getString("XLIFF_file")); //$NON-NLS-1$

        final StyledText sourceNameText = new StyledText(filesComposite, SWT.BORDER);
        sourceNameText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        sourceNameText.addModifyListener(new ModifyListener()
        {

            public void modifyText(ModifyEvent arg0)
            {
                String xliffDocumentNew = sourceNameText.getText();
                File xl = new File(xliffDocumentNew);
                xliffDocument = sourceNameText.getText();
                if (!xl.exists())
                {
                    return;
                }
            }
        });


        Button browseSource = new Button(filesComposite, SWT.PUSH);
        browseSource.setText(message.getString("Browse")); //$NON-NLS-1$


        Label targetNameLabel = new Label(filesComposite, SWT.NONE);
        targetNameLabel.setText(message.getString("Target_TBX_document")); //$NON-NLS-1$

        final StyledText targetNameText = new StyledText(filesComposite, SWT.BORDER);
        targetNameText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        if ((revConvertedFile != null) && !revConvertedFile.equals(""))
            targetNameText.setText(revConvertedFile);

        Button browseTarget = new Button(filesComposite, SWT.PUSH);
        browseTarget.setText(message.getString("Browse")); //$NON-NLS-1$
        
        browseSource.addSelectionListener(new SelectionAdapter()
        {

            public void widgetSelected(SelectionEvent e)
            {
                FileDialog fd = new FileDialog(shell, SWT.OPEN);
                String extensions[] =
                    {
                            "*.xlf", "*.xml", "*.*"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                fd.setFilterExtensions(extensions);
                if (System.getProperty("file.separator").equals("/")) { //$NON-NLS-1$ //$NON-NLS-2$
                    fd.setFilterPath(System.getProperty("user.home")); //$NON-NLS-1$
                }
                fd.open();
                if (fd.getFileName() == "")
                { //$NON-NLS-1$
                    fd = null;
                    extensions = null;
                    return;
                }
                else
                {
                    sourceNameText.setText(fd.getFilterPath() + separator + fd.getFileName());
                    targetNameText.setText(fd.getFilterPath() + separator + fd.getFileName() + ".tbx");
                    fd = null;
                }
            }
        });
        
        browseTarget.addSelectionListener(new SelectionAdapter()
        {

            public void widgetSelected(SelectionEvent e)
            {
                FileDialog fd = new FileDialog(shell, SWT.SAVE);
                fd.setFilterExtensions(targetExtensions);
                if (System.getProperty("file.separator").equals("/")) { //$NON-NLS-1$ //$NON-NLS-2$
                    fd.setFilterPath(System.getProperty("user.home")); //$NON-NLS-1$
                }
                fd.open();
                if (fd.getFileName() == "") { //$NON-NLS-1$
                    fd = null;
                    return;
                }
                else
                {
                    targetNameText.setText(fd.getFilterPath() + separator + fd.getFileName());
                    fd = null;
                }
            }
        });

        //
        // Bottom of the screen, buttons section
        //

        Composite bottom = new Composite(shell, SWT.BORDER);
        bottom.setLayout(new GridLayout(2, true));
        bottom.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        Button convertButton = new Button(bottom, SWT.PUSH);
        convertButton.setText(message.getString("exportXliffInternalTerminology")); //$NON-NLS-1$
        convertButton.addSelectionListener(new SelectionAdapter()
        {

            public void widgetSelected(SelectionEvent e)
            {
                xliffDocument = sourceNameText.getText();
                targetDocument = targetNameText.getText();

                if (xliffDocument.equals("")) { //$NON-NLS-1$
                    MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
                    box.setMessage(message.getString("Please_select_a_document_for_conversion")); //$NON-NLS-1$
                    box.open();
                    return;
                }
                if (targetDocument.equals("")) { //$NON-NLS-1$
                    MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
                    box.setMessage(message.getString("Please_select_a_name_for_converted_document")); //$NON-NLS-1$
                    box.open();
                    return;
                }
                if (targetDocument.equals(xliffDocument))
                {
                    MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
                    box.setMessage(message.getString("Please_select_a_different_name_for_converted_document")); //$NON-NLS-1$
                    box.open();
                    return;
                }

                try
                {
                    XliffDocument xliff = new XliffDocument();
                    xliff.loadXmlFile(xliffDocument);
                    
                    
                    if (xliff.exportInternalOpenTMSTerminology(targetDocument))
                    {
                        editorConfiguration.saveKeyValuePair("termXliffFile", xliffDocument);
                        editorConfiguration.saveKeyValuePair("termTbxFile", targetDocument);
                        MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
                        mbox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
                        mbox.setMessage(message.getString("TBX_Export_completed")); //$NON-NLS-1$
                        mbox.open();
                    }
                    else
                    {
                        editorConfiguration.saveKeyValuePair("termXliffFile", xliffDocument);
                        editorConfiguration.saveKeyValuePair("termTbxFile", targetDocument);
                        MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
                        mbox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
                        mbox.setMessage(message.getString("Error_TBX_Export_Document")); //$NON-NLS-1$
                        mbox.open();
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    editorConfiguration.saveKeyValuePair("termXliffFile", xliffDocument);
                    editorConfiguration.saveKeyValuePair("termTbxFile", targetDocument);
                    MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
                    mbox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
                    mbox.setMessage(message.getString("Error_TBX_Export_Document")); //$NON-NLS-1$
                    mbox.open();
                }
            }
        });

        Button close = new Button(bottom, SWT.PUSH);
        close.setText(message.getString("Cl&ose")); //$NON-NLS-1$
        close.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        close.addSelectionListener(new SelectionAdapter()
        {

            public void widgetSelected(SelectionEvent e)
            {
                shell.close();
            }
        });

        if ((revXliffFile != null) && !revXliffFile.equals(""))
            sourceNameText.setText(revXliffFile);

        shell.pack();
    }

    public void show()
    {
        shell.open();
        shell.forceActive();
    }

}